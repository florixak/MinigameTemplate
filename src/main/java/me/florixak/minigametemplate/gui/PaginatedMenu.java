package me.florixak.minigametemplate.gui;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public abstract class PaginatedMenu extends Menu {

	protected int currentPage = 0;

	protected String title = "Inventory";
	protected int slots = 54;

	protected int index = 0;

	public PaginatedMenu(final MenuUtils menuUtils, final String title) {
		super(menuUtils);
		this.title = title;
	}

	@Override
	public String getMenuName() {
		return formattedMenuName();
	}

	private String formattedMenuName() {
		if (getMaxPages() == 1) {
			return TextUtils.color(this.title);
		} else {
			return TextUtils.color(this.title + " - " + (this.currentPage + 1) + " / " + getMaxPages());
		}
	}

	public int getMaxPages() {
		return (int) Math.ceil((double) getItemsCount() / getMaxItemsPerPage());
	}

	public abstract int getItemsCount();

	@Override
	public int getSlots() {
		return this.slots;
	}

	public int getStartIndex() {
		return this.currentPage * getMaxItemsPerPage();
	}

	public int getEndIndex() {
		return Math.min(getStartIndex() + getMaxItemsPerPage(), getItemsCount());
	}

	public void addMenuBorder() {
		if (this.currentPage > 0)
			this.inventory.setItem(getSlots() - 6,
					ItemUtils.createItem(XMaterial.matchXMaterial(Material.DARK_OAK_BUTTON).parseMaterial(),
							TextUtils.color(GameValues.INVENTORY.PREVIOUS_TITLE), 1, null));

		this.inventory.setItem(getSlots() - 5,
				ItemUtils.createItem(XMaterial.matchXMaterial(Material.BARRIER).parseMaterial(),
						TextUtils.color(GameValues.INVENTORY.CLOSE_TITLE), 1, null));

		if (this.currentPage < getMaxPages() - 1)
			this.inventory.setItem(getSlots() - 4,
					ItemUtils.createItem(XMaterial.matchXMaterial(Material.DARK_OAK_BUTTON).parseMaterial(),
							TextUtils.color(GameValues.INVENTORY.NEXT_TITLE), 1, null));
	}

	private int getMaxItemsPerPage() {
		return getSlots() - 9;
	}

	public void handlePaging(final InventoryClickEvent event, final List<?> recipesList) {
		if (event.getCurrentItem().getItemMeta() == null) return;
		if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(TextUtils.color("Previous"))) {
			handlePrevious();
		} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(TextUtils.color("Next"))) {
			handleNext(recipesList);
		}
	}

	private void handlePrevious() {
		if (this.currentPage == 0) {
			this.menuUtils.getGamePlayer().sendMessage(ChatColor.GRAY + "You are already on the first page.");
		} else {
			this.currentPage -= 1;
			super.open();
		}
	}

	private void handleNext(final List<?> list) {
		if (!((this.index + 1) >= list.size())) {
			this.currentPage += 1;
			super.open();
		} else {
			this.menuUtils.getGamePlayer().sendMessage(ChatColor.GRAY + "You are on the last page.");
		}
	}
}
