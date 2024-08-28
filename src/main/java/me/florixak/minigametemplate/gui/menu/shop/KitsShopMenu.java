package me.florixak.minigametemplate.gui.menu.shop;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KitsShopMenu extends PaginatedMenu {

	private final GamePlayer gamePlayer;
	private final List<Kit> kitsList;

	public KitsShopMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.gamePlayer = menuUtils.getGamePlayer();
		this.kitsList = this.gameManager.getKitsManager().getKitsList().stream()
				.filter(kit -> !this.gamePlayer.getData().hasBought(kit)).collect(Collectors.toList());
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
		return this.guiManager.getGui(GuiType.KITS_SHOP.getKey());
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
		addMenuBorder(true);
		ItemStack kitDisplayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Kit kit = this.kitsList.get(i);
			final List<String> lore = new ArrayList<>();

			lore.add(kit.getFormattedCost());
			lore.addAll(kit.getLore());

			kitDisplayItem = ItemUtils.createItem(kit.getDisplayItem().getType(), kit.getDisplayName(), 1, lore);

			this.inventory.setItem(i - getStartIndex(), kitDisplayItem);
		}
	}

	@Override
	public void open() {
		if (!GameValues.KITS.ENABLED) {
			this.gamePlayer.sendMessage(Messages.KITS_DISABLED.toString());
			return;
		}
		if (this.kitsList.isEmpty()) {
			this.gamePlayer.sendMessage("You have already bought all the kits!");
			return;
		}
		super.open();
	}

	private void handleKitSelection(final InventoryClickEvent event) {
		final Kit selectedKit = this.kitsList.get(event.getSlot());
		close();

		if (this.guiManager.getGui(GuiType.PURCHASE_CONFIRM.getKey()).isEnabled()) {
			this.menuUtils.setToBuy(selectedKit);
			new ConfirmPurchaseMenu(this.menuUtils).open();
		} else {
			this.gamePlayer.getData().buy(selectedKit);
		}


	}
}
