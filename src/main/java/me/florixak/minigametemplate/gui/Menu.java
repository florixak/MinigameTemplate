package me.florixak.minigametemplate.gui;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Menu implements InventoryHolder {

	protected MenuUtils menuUtils;
	protected Inventory inventory;
	protected ItemStack FILLER = ItemUtils.createItem(XMaterial.AIR.parseMaterial(), " ", 1, null);

	public Menu(final MenuUtils menuUtils) {
		this.menuUtils = menuUtils;
	}

	public abstract String getMenuName();

	public abstract int getSlots();

	public abstract void handleMenuClicks(InventoryClickEvent e);

	public abstract void setMenuItems();

	public void open() {
		// The owner of the inventory created is the Menu itself,
		// so we are able to reverse engineer the Menu object from the
		// inventoryHolder in the MenuListener class when handling clicks
		this.inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

		this.setMenuItems();

		this.menuUtils.getOwner().openInventory(this.inventory);
	}

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}

	public void close() {
		this.menuUtils.getGamePlayer().getPlayer().closeInventory();
	}

	public void setFiller() {
		for (int i = 0; i < getSlots(); i++) {
			if (this.inventory.getItem(i) == null) {
				this.inventory.setItem(i, this.FILLER);
			}
		}
	}

}

