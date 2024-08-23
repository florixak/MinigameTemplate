package me.florixak.minigametemplate.game.perks;

import lombok.Getter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.gameItems.BuyableItem;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Perk extends BuyableItem {

	private static final GameManager gameManager = GameManager.getInstance();

	private final String name;
	private final ItemStack displayItem;
	private final double cost;
	private final List<String> description;
	private final List<PerkBonus> perkBonus;
	private final List<PerkEffect> perkEffect;
	private final List<PerkItem> perkItem;

	public Perk(final String name, final ItemStack displayItem, final double cost, final List<String> description, final List<PerkBonus> perkBonus, final List<PerkEffect> perkEffect, final List<PerkItem> perkItem) {
		this.name = name;
		this.displayItem = displayItem;
		this.cost = cost;
		this.description = description;
		this.perkBonus = perkBonus;
		this.perkEffect = perkEffect;
		this.perkItem = perkItem;
	}

	public String getDisplayName() {
		return TextUtils.color(this.name);
	}

	public String getFormattedCost() {
		return Messages.PERKS_SHOP_COST.toString().replace("%cost%", String.valueOf(getCost()));
	}

	public boolean isFree() {
		return getCost() == 0;
	}

	public List<String> getDescription() {
		final List<String> formattedDescription = new ArrayList<>();
		for (String desc : this.description) {
			if (hasPerkEffect()) {
				for (final PerkEffect effect : this.perkEffect) {
					desc = desc
							.replace("%effect%", effect.getEffectName())
							.replace("%effect-level%", String.valueOf(effect.getFormattedAmplifier()))
							.replace("%effect-duration%", String.valueOf(effect.getDuration()));
				}
			}
			if (hasPerkItem()) {
				for (final PerkItem item : this.perkItem) {
					desc = desc
							.replace("%item-name%", TextUtils.toNormalCamelText(item.getItem().getType().toString()))
							.replace("%item-amount%", String.valueOf(item.getFormattedAmount()))
							.replace("%item-chance%", item.getChance() + "%");
				}
			}
			if (hasPerkBonus()) {
				for (final PerkBonus bonus : this.perkBonus) {
					desc = desc
							.replace("%bonus-coins%", bonus.getFormattedCoins())
							.replace("%bonus-uhc-exp%", bonus.getFormattedUhcExp())
							.replace("%bonus-exp%", bonus.getFormattedExp())
							.replace("%currency%", Messages.CURRENCY.toString());
				}
			}
			formattedDescription.add(TextUtils.color(desc));
		}
		return formattedDescription;
	}

	public boolean hasPerkBonus() {
		return this.perkBonus != null && !this.perkBonus.isEmpty();
	}

	public boolean hasPerkEffect() {
		return this.perkEffect != null && !this.perkEffect.isEmpty();
	}

	public boolean hasPerkItem() {
		return this.perkEffect != null && !this.perkItem.isEmpty();
	}

	public void givePerk(final GamePlayer gamePlayer) {
		if (hasPerkEffect()) {
			for (final PerkEffect effect : this.perkEffect) {
				effect.giveEffect(gamePlayer);
			}
		}
		if (hasPerkItem()) {
			for (final PerkItem item : this.perkItem) {
				item.giveItem(gamePlayer);
			}
		}
		if (hasPerkBonus()) {
			for (final PerkBonus bonus : this.perkBonus) {
				bonus.giveBonus(gamePlayer);
			}
		}
	}

	public void buy(final GamePlayer gamePlayer) {
		super.buy(gamePlayer);
		gamePlayer.getPlayerData().getBoughtPerksList().add(this);
		gamePlayer.getPlayerData().savePerks();
		gameManager.getSoundManager().playSelectBuySound(gamePlayer.getPlayer());
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof Perk
				&& ((Perk) o).getName().equals(getName())
				&& ((Perk) o).getDisplayItem().equals(getDisplayItem())
				&& ((Perk) o).getCost() == getCost()
				&& ((Perk) o).getDescription().equals(getDescription())
				&& ((Perk) o).getPerkBonus().equals(getPerkBonus())
				&& ((Perk) o).getPerkEffect().equals(getPerkEffect())
				&& ((Perk) o).getPerkItem().equals(getPerkItem());
	}

}