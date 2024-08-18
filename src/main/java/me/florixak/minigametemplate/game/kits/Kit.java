package me.florixak.minigametemplate.game.kits;

import lombok.Getter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class Kit {

	private final String name;
	private final String displayName;
	private final List<ItemStack> items;
	private final ItemStack displayItem;
	private final double cost;

	public Kit(final String name, final String displayName, final ItemStack displayItem, final double cost, final List<ItemStack> items) {
		this.name = name;
		this.displayName = displayName;
		this.displayItem = displayItem;
		this.cost = cost;
		this.items = items;
	}

	public String getDisplayName() {
		return TextUtils.color(this.displayName);
	}

	public boolean isFree() {
		return getCost() == 0;
	}

	public String getFormattedCost() {
		return Messages.KITS_COST.toString().replace("%cost%", String.valueOf(getCost()));
	}

	public void giveKit(final GamePlayer gamePlayer) {
		final Player p = gamePlayer.getPlayer();

		for (final ItemStack item : getItems()) {
			p.getInventory().addItem(item);
		}
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof Kit
				&& ((Kit) o).getName().equals(getName())
				&& ((Kit) o).getDisplayName().equals(getDisplayName())
				&& ((Kit) o).getCost() == getCost()
				&& ((Kit) o).getItems().equals(getItems());
	}
}
