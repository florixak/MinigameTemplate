package me.florixak.minigametemplate.gui.menu.inGame;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.gui.menu.shop.ShopMenu;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KitsSelectorMenu extends PaginatedMenu {

	private final GamePlayer gamePlayer;
	private final List<Kit> kitsList;

	public KitsSelectorMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.gamePlayer = menuUtils.getGamePlayer();
		this.kitsList = this.gameManager.getKitsManager().getKitsList().stream()
				.filter(kit -> this.gamePlayer.getData().hasBought(kit)).collect(Collectors.toList());
	}

	@Override
	public String getMenuName() {
		return format(getGui().getTitle());
	}

	@Override
	public int getSlots() {
		return getGui().getSlots();
	}

	@Override
	public Gui getGui() {
		return this.guiManager.getGui(GuiType.KITS_SELECTOR.getKey());
	}

	@Override
	public int getItemsCount() {
		return this.kitsList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem.equals(this.guiManager.getItem("filler"))) {
			event.setCancelled(true);
		} else if (clickedItem.equals(this.guiManager.getItem("close"))) {
			close();
		} else if (clickedItem.equals(this.guiManager.getItem("back"))) {
			close();
			new ShopMenu(this.menuUtils).open();
		} else if (clickedItem.equals(this.guiManager.getItem("previous")) || clickedItem.equals(this.guiManager.getItem("next"))) {
			handlePaging(event, this.kitsList);
		} else {
			handleKitSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder(false);
		ItemStack displayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Kit kit = this.kitsList.get(i);
			final List<String> lore = new ArrayList<>(kit.getLore());

			displayItem = ItemUtils.createItem(kit.getDisplayItem().getType(), kit.getDisplayName(), 1, lore);

			this.inventory.setItem(i - getStartIndex(), displayItem);
		}
	}

	@Override
	public void open() {
		if (!GameValues.KITS.ENABLED) {
			this.gamePlayer.sendMessage(Messages.KITS_DISABLED.toString());
			return;
		}
		super.open();
	}

	private void handleKitSelection(final InventoryClickEvent event) {
		final Kit selectedKit = this.kitsList.get(event.getSlot());
		close();
		this.gamePlayer.getArenaData().setKit(selectedKit);
	}
}
