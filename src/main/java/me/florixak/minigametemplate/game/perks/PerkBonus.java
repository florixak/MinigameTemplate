package me.florixak.minigametemplate.game.perks;

import lombok.Getter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.RandomUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;

import java.util.List;

@Getter
public class PerkBonus {

	private final List<Double> coins;
	private final List<Double> exp;

	public PerkBonus(final List<Double> coins, final List<Double> uhcExp) {
		this.coins = coins;
		this.exp = uhcExp;
	}

	public String getFormattedCoins() {
		if (this.coins.isEmpty()) return "0";
		if (this.coins.size() == 1) return String.valueOf(this.coins.get(0));
		return this.coins.get(0) + "-" + this.coins.get(1);
	}

	public String getFormattedUhcExp() {
		if (this.exp.isEmpty()) return "0";
		if (this.exp.size() == 1) return String.valueOf(this.exp.get(0));
		return this.exp.get(0) + "-" + this.exp.get(1);
	}


	public String getFormattedExp() {
		if (this.exp.isEmpty()) return "0";
		if (this.exp.size() == 1) return String.valueOf(this.exp.get(0));
		return this.exp.get(0) + "-" + this.exp.get(1);
	}

	public void giveBonus(final GamePlayer gamePlayer) {
		final double randomCoins = this.exp.get(0) + (this.exp.get(1) - this.exp.get(0)) * RandomUtils.getRandom().nextDouble();
		final double randomExp = this.exp.get(0) + (this.exp.get(1) - this.exp.get(0)) * RandomUtils.getRandom().nextDouble();

		gamePlayer.getData().depositMoney(randomCoins);
		gamePlayer.getData().addExp(randomExp);
		gamePlayer.sendMessage(Messages.PERKS_RECEIVED_BONUS.toString().replace("%coins%", TextUtils.formatToOneDecimal(randomCoins)).replace("%exp%", TextUtils.formatToOneDecimal(randomExp)));
	}

}
