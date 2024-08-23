package me.florixak.minigametemplate.gui.menu.shop;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.Permissions;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KitsShopMenu extends PaginatedMenu {

	private final GameManager gameManager = GameManager.getInstance();
	private final List<Kit> kitsList;
	private final GamePlayer gamePlayer;

	public KitsShopMenu(final MenuUtils menuUtils) {
		super(menuUtils, GameValues.KITS.KIT_SHOP_TITLE);
		this.gamePlayer = menuUtils.getGamePlayer();
		this.kitsList = GameManager.getInstance().getKitsManager().getKitsList().stream()
				.filter(kit -> !this.gamePlayer.getPlayerData().hasBought(kit)).collect(Collectors.toList());
	}

	@Override
	public int getSlots() {
		return GameValues.KITS.KIT_SHOP_SLOTS;
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
			if (this.gameManager.getArenaManager().isPlayerInArena(this.gamePlayer)) return;
			handleKitSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder();
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
		super.open();
	}

	private void handleKitSelection(final InventoryClickEvent event) {
		final Kit selectedKit = this.kitsList.get(event.getSlot());
		close();

		if (this.gamePlayer.getPlayerData().hasBought(selectedKit)
				|| this.gamePlayer.hasPermission(Permissions.KITS_FREE.getPerm())
				|| selectedKit.isFree()) {
			this.gamePlayer.getPlayerData().buy(selectedKit);
		} else {
			if (GameValues.INVENTORY.CONFIRM_PURCHASE_ENABLED) {
				this.menuUtils.setToBuy(selectedKit);
				new ConfirmPurchaseMenu(this.menuUtils).open();
			} else {
				this.gamePlayer.getPlayerData().buy(selectedKit);
			}
		}

	}
}
