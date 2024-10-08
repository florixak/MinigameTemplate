package me.florixak.minigametemplate.game.perks;

import lombok.Getter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.RandomUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class PerkItem {

	private final String displayName;
	private final ItemStack item;
	private final List<Integer> amount;
	private final int chance;

	public PerkItem(final ItemStack item, final List<Integer> amount, final int chance) {
		this.item = item;
		this.amount = amount;
		this.chance = chance;
		this.displayName = TextUtils.toNormalCamelText(item.getType().toString());
	}

	public String getFormattedAmount() {
		if (this.amount.isEmpty()) return "0";
		if (this.amount.size() == 1) return String.valueOf(this.amount.get(0));
		return this.amount.get(0) + "-" + this.amount.get(1);
	}

	public void giveItem(final GamePlayer gamePlayer) {
		final int num = RandomUtils.randomInteger(1, 100);
		if (num > this.chance) return;
		gamePlayer.sendMessage(Messages.PERKS_RECEIVED_ITEM.toString().replace("%item%", this.displayName));
		gamePlayer.getPlayer().getInventory().addItem(this.item);
	}
}
