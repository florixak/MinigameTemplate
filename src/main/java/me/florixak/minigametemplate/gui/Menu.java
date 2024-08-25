package me.florixak.minigametemplate.gui;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Menu implements InventoryHolder {

	protected final GameManager gameManager = GameManager.getInstance();
	protected final GuiManager guiManager = this.gameManager.getGuiManager();
	protected MenuUtils menuUtils;
	protected Inventory inventory;
	protected ItemStack FILLER = this.guiManager.getItem("filler");
	protected Gui gui;

	public Menu(final MenuUtils menuUtils) {
		this.menuUtils = menuUtils;
	}

	public abstract String getMenuName();

	public abstract int getSlots();

	public abstract void handleMenuClicks(InventoryClickEvent e);

	public abstract Gui getGui();

	public abstract void setMenuItems();

	public void open() {
		// The owner of the inventory created is the Menu itself,
		// so we are able to reverse engineer the Menu object from the
		// inventoryHolder in the MenuListener class when handling clicks
		this.inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

		this.setMenuItems();
		this.setFiller();

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
			final ItemStack item = this.inventory.getItem(i);
			if (item == null || item.getType().equals(XMaterial.AIR.parseMaterial())) {
				this.inventory.setItem(i, this.FILLER);
			}
		}
	}

}

