package me.florixak.minigametemplate.gui.menu.inGame;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.Permissions;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.gui.menu.shop.ConfirmPurchaseMenu;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PerksShopMenu extends PaginatedMenu {

	private final GamePlayer uhcPlayer;
	private final List<Perk> perksList;

	public PerksShopMenu(final MenuUtils menuUtils) {
		super(menuUtils, GameValues.PERKS.PERKS_SHOP_TITLE);
		this.uhcPlayer = menuUtils.getGamePlayer();
		this.perksList = GameManager.getInstance().getPerksManager().getPerks().stream().filter(perk -> !this.uhcPlayer.getPlayerData().hasPerkBought(perk)).collect(Collectors.toList());
	}

	@Override
	public int getSlots() {
		return GameValues.PERKS.PERKS_SHOP_SLOTS;
	}

	@Override
	public int getItemsCount() {
		return this.perksList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final Material clickedItem = event.getCurrentItem().getType();
		if (clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.CLOSE_ITEM).get().parseMaterial())) {
			close();
		} else if (clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.NEXT_ITEM).get().parseMaterial())
				|| clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.PREVIOUS_ITEM).get().parseMaterial())) {
			handlePaging(event, this.perksList);
		} else {
			handlePerkSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder();
		ItemStack perkDisplayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Perk perk = this.perksList.get(i);
			final List<String> lore = new ArrayList<>();

//			if (this.uhcPlayer.hasPerk() && this.uhcPlayer.getPerk().equals(perk)) {
//				lore.add(Messages.PERKS_INV_SELECTED.toString());
//			} else {
			if (!GameValues.PERKS.BOUGHT_FOREVER) {
				lore.add(perk.getFormattedCost());
			} else {
				lore.add(perk.getFormattedCost());
			}
//			}

			lore.addAll(perk.getDescription());

			perkDisplayItem = ItemUtils.createItem(perk.getDisplayItem().getType(), perk.getDisplayName(), 1, lore);

			this.inventory.setItem(i - getStartIndex(), perkDisplayItem);

		}
	}

	@Override
	public void open() {
		if (!GameValues.PERKS.ENABLED) {
			this.uhcPlayer.sendMessage(Messages.PERKS_DISABLED.toString());
			return;
		}
		super.open();
	}

	private void handlePerkSelection(final InventoryClickEvent event) {
		final Perk selectedPerk = this.perksList.get(event.getSlot());
		close();

		if (this.uhcPlayer.getPlayerData().hasPerkBought(selectedPerk) || this.uhcPlayer.hasPermission(Permissions.PERKS_FREE.getPerm()) || selectedPerk.isFree()) {
			this.uhcPlayer.setPerk(selectedPerk);
		} else {
			if (GameValues.INVENTORY.CONFIRM_PURCHASE_ENABLED) {
				this.menuUtils.setSelectedPerkToBuy(selectedPerk);
				new ConfirmPurchaseMenu(this.menuUtils).open();
			} else {
				this.uhcPlayer.getPlayerData().buyPerk(selectedPerk);
			}
		}

	}
}
