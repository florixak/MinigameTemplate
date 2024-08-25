package me.florixak.minigametemplate.gui;

import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public abstract class PaginatedMenu extends Menu {

	protected int currentPage = 0;

	protected String title = "Inventory";
	protected int slots = 54;

	protected int index = 0;

	public PaginatedMenu(final MenuUtils menuUtils) {
		super(menuUtils);
	}

	@Override
	public String getMenuName() {
		return this.title;
	}

	public String format(final String title) {
		if (getMaxPages() == 1) {
			return TextUtils.color(title);
		} else {
			return TextUtils.color(title + " - " + (this.currentPage + 1) + " / " + getMaxPages());
		}
	}

	public int getMaxPages() {
		final int maxPages = (int) Math.ceil((double) getItemsCount() / getMaxItemsPerPage());
		return maxPages == 0 ? 1 : maxPages;
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

	public void addMenuBorder(final boolean backItem) {
		if (this.currentPage > 0)
			this.inventory.setItem(getSlots() - 6, this.gameManager.getGuiManager().getItem("previous"));

		this.inventory.setItem(getSlots() - 5, this.gameManager.getGuiManager().getItem("close"));

		if (this.currentPage < getMaxPages() - 1)
			this.inventory.setItem(getSlots() - 4, this.gameManager.getGuiManager().getItem("next"));

		if (backItem)
			this.inventory.setItem(getSlots() - 9, this.gameManager.getGuiManager().getItem("back"));
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
