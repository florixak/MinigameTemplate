package me.florixak.minigametemplate.gui.menu.shop;

import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.Menu;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopMenu extends Menu {

	public ShopMenu(final MenuUtils menuUtils) {
		super(menuUtils);
	}

	@Override
	public String getMenuName() {
		return TextUtils.color(getGui().getTitle());
	}

	@Override
	public int getSlots() {
		return getGui().getSlots();
	}

	@Override
	public Gui getGui() {
		return this.guiManager.getGui(GuiType.SHOP.getKey());
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem.equals(this.guiManager.getItem("filler"))) {
			event.setCancelled(true);
		} else if (clickedItem.equals(this.guiManager.getItem("close"))) {
			close();
		} else if (clickedItem.equals(this.guiManager.getGui(GuiType.COSMETICS_SHOP.getKey()).getDisplayItem())) {
			close();
			new CosmeticsShopMenu(this.menuUtils).open();
		} else if (clickedItem.equals(this.guiManager.getGui(GuiType.KITS_SHOP.getKey()).getDisplayItem())) {
			close();
			new KitsShopMenu(this.menuUtils).open();
		} else if (clickedItem.equals(this.guiManager.getGui(GuiType.PERKS_SHOP.getKey()).getDisplayItem())) {
			close();
			new PerksShopMenu(this.menuUtils).open();
		}
	}

	@Override
	public void setMenuItems() {
		getInventory().setItem(11, this.guiManager.getGui(GuiType.COSMETICS_SHOP.getKey()).getDisplayItem());
		getInventory().setItem(13, this.guiManager.getGui(GuiType.KITS_SHOP.getKey()).getDisplayItem());
		getInventory().setItem(15, this.guiManager.getGui(GuiType.PERKS_SHOP.getKey()).getDisplayItem());
		getInventory().setItem(getSlots() - 5, this.guiManager.getItem("close"));
	}
}
