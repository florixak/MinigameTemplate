package me.florixak.minigametemplate.game.perks;

import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.potion.PotionEffect;

@Getter
public class PerkEffect {

	private final String displayName;
	private final PotionEffect effectType;

	public PerkEffect(final PotionEffect effectType) {
		this.effectType = effectType;
		this.displayName = getEffectName() + " " + getFormattedAmplifier() + " (" + getDuration() + "s)";
	}

	public String getEffectName() {
		return TextUtils.toNormalCamelText(XPotion.matchXPotion(this.effectType.getType()).name());
	}

	public int getDuration() {
		return this.effectType.getDuration();
	}

	public int getAmplifier() {
		return this.effectType.getAmplifier();
	}

	public int getFormattedAmplifier() {
		return this.effectType.getAmplifier() + 1;
	}

	public void giveEffect(final GamePlayer gamePlayer) {
		gamePlayer.addEffect(XPotion.matchXPotion(this.effectType.getType()), this.effectType.getDuration(), getFormattedAmplifier());
		gamePlayer.sendMessage(Messages.PERKS_RECEIVED_EFFECT.toString()
				.replace("%effect%", getDisplayName())
				.replace("%effect-duration%", String.valueOf(getDuration()))
				.replace("%effect-level%", String.valueOf(getFormattedAmplifier())));
	}
}
