package me.florixak.minigametemplate.gui.menu.shop;

import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopMenu extends PaginatedMenu {

	public ShopMenu(final MenuUtils menuUtils) {
		super(menuUtils, "Shop");
	}

	@Override
	public int getItemsCount() {
		return 0;
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent e) {

	}

	@Override
	public void setMenuItems() {

	}
}
