package me.florixak.minigametemplate.gui.menu.inGame;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.Permissions;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.gui.menu.shop.ConfirmPurchaseMenu;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitsMenu extends PaginatedMenu {

	private final GameManager gameManager = GameManager.getInstance();
	private final List<Kit> kitsList = this.gameManager.getKitsManager().getKitsList();
	private final GamePlayer gamePlayer;

	public KitsMenu(final MenuUtils menuUtils) {
		super(menuUtils, GameValues.KITS.KITS_TITLE);
		this.gamePlayer = menuUtils.getGamePlayer();
	}

	@Override
	public int getSlots() {
		return GameValues.KITS.KITS_SLOTS;
	}

	@Override
	public int getItemsCount() {
		return this.kitsList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final Material clickedItem = event.getCurrentItem().getType();
		if (clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.CLOSE_ITEM).get().parseMaterial())) {
			close();
		} else if (clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.NEXT_ITEM).get().parseMaterial())
				|| clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.PREVIOUS_ITEM).get().parseMaterial())) {
			handlePaging(event, this.kitsList);
		} else {
			if (!this.gameManager.getArenaManager().isPlayerInArena(this.gamePlayer)) {
				this.gamePlayer.sendMessage(Messages.CANT_USE_NOW.toString());
				return;
			}
			handleKitSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder();
		ItemStack kitDisplayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Kit kit = this.kitsList.get(i);
			final List<String> lore = kit.getLore();

			if (this.gamePlayer.hasKit() && this.gamePlayer.getKit().equals(kit)) {
				lore.add(" ");
				lore.add(Messages.KITS_INV_SELECTED.toString());
			} else {
				if (!GameValues.KITS.BOUGHT_FOREVER) {
					lore.add(kit.getFormattedCost());
				} else {
					if (this.gamePlayer.getPlayerData().hasKitBought(kit) || this.gamePlayer.hasPermission(Permissions.KITS_FREE.getPerm()) || kit.isFree()) {
						lore.add(Messages.KITS_INV_CLICK_TO_SELECT.toString());
					} else {
						lore.add(kit.getFormattedCost());
					}
				}
			}

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
		super.open();
	}

	private void handleKitSelection(final InventoryClickEvent event) {
		final Kit selectedKit = this.kitsList.get(event.getSlot());
		close();

		if (!GameValues.KITS.BOUGHT_FOREVER) {
			if (!selectedKit.isFree() && this.gamePlayer.getPlayerData().getMoney() < selectedKit.getCost() && !this.gamePlayer.hasPermission(Permissions.KITS_FREE.getPerm())) {
				this.gamePlayer.sendMessage(Messages.NO_MONEY.toString());
				return;
			}
			this.gamePlayer.setKit(selectedKit);
			this.gamePlayer.sendMessage(Messages.KITS_MONEY_DEDUCT_INFO.toString());
		} else {
			if (this.gamePlayer.getPlayerData().hasKitBought(selectedKit)
					|| this.gamePlayer.hasPermission(Permissions.KITS_FREE.getPerm())
					|| selectedKit.isFree()) {
				this.gamePlayer.setKit(selectedKit);
			} else {
				if (GameValues.INVENTORY.CONFIRM_PURCHASE_ENABLED) {
					this.menuUtils.setSelectedKitToBuy(selectedKit);
					new ConfirmPurchaseMenu(this.menuUtils).open();
				} else {
					this.gamePlayer.getPlayerData().buyKit(selectedKit);
				}
			}
		}
	}
}
