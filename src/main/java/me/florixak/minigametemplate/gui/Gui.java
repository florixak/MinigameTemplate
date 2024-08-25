package me.florixak.minigametemplate.gui;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class Gui {

	private final boolean enabled;
	private final String title;
	private final int slots;

	private final ItemStack displayItem;
	private final int displaySlot;

	public Gui(final boolean enabled, final String title, final int slots, final ItemStack displayItem, final int displaySlot) {
		this.enabled = enabled;
		this.title = title;
		this.slots = slots;
		this.displayItem = displayItem;
		this.displaySlot = displaySlot;
	}

	public String getDisplayItemName() {
		return this.displayItem.getItemMeta().getDisplayName();
	}
}
