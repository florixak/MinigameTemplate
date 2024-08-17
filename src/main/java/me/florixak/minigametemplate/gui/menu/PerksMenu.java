package me.florixak.minigametemplate.gui.menu;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.Permissions;
import me.florixak.uhcrevamp.game.perks.Perk;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.gui.PaginatedMenu;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerksMenu extends PaginatedMenu {

	private final UHCPlayer uhcPlayer;
	private final List<Perk> perksList;

	public PerksMenu(final MenuUtils menuUtils) {
		super(menuUtils, GameValues.INVENTORY.PERKS_TITLE);
		this.uhcPlayer = menuUtils.getUHCPlayer();
		this.perksList = GameManager.getGameManager().getPerksManager().getPerks();
	}

	@Override
	public int getSlots() {
		return GameValues.INVENTORY.PERKS_SLOTS;
	}

	@Override
	public int getItemsCount() {
		return this.perksList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
			close();
		} else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
			handlePaging(event, this.perksList);
		} else {
			if (GameManager.getGameManager().isPlaying()) {
				this.uhcPlayer.sendMessage(Messages.CANT_USE_NOW.toString());
				return;
			}
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

			if (this.uhcPlayer.hasPerk() && this.uhcPlayer.getPerk().equals(perk)) {
				lore.add(Messages.PERKS_INV_SELECTED.toString());
			} else {
				if (!GameValues.PERKS.BOUGHT_FOREVER) {
					lore.add(perk.getFormattedCost());
				} else {
					if (this.uhcPlayer.getData().hasPerkBought(perk) || this.uhcPlayer.hasPermission(Permissions.PERKS_FREE.getPerm()) || perk.isFree()) {
						lore.add(Messages.PERKS_INV_CLICK_TO_SELECT.toString());
					} else {
						lore.add(perk.getFormattedCost());
					}
				}
			}

			lore.addAll(perk.getDescription());

			perkDisplayItem = ItemUtils.createItem(perk.getDisplayItem().getType(), perk.getDisplayName(), 1, lore);

			this.inventory.setItem(i - getStartIndex(), perkDisplayItem);

		}
	}

	@Override
	public void open() {
		if (!GameValues.KITS.ENABLED) {
			this.uhcPlayer.sendMessage(Messages.KITS_DISABLED.toString());
			return;
		}
		super.open();
	}

	private void handlePerkSelection(final InventoryClickEvent event) {
		final Perk selectedPerk = this.perksList.get(event.getSlot());
		close();

		if (!GameValues.PERKS.BOUGHT_FOREVER) {
			if (!selectedPerk.isFree() && this.uhcPlayer.getData().getMoney() < selectedPerk.getCost() && !this.uhcPlayer.hasPermission(Permissions.PERKS_FREE.getPerm())) {
				this.uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
				return;
			}
			this.uhcPlayer.setPerk(selectedPerk);
			this.uhcPlayer.sendMessage(Messages.PERKS_MONEY_DEDUCT_INFO.toString());
		} else {
			if (this.uhcPlayer.getData().hasPerkBought(selectedPerk) || this.uhcPlayer.hasPermission(Permissions.PERKS_FREE.getPerm()) || selectedPerk.isFree()) {
				this.uhcPlayer.setPerk(selectedPerk);
			} else {
				if (GameValues.INVENTORY.CONFIRM_PURCHASE_ENABLED) {
					this.menuUtils.setSelectedPerkToBuy(selectedPerk);
					new ConfirmPurchaseMenu(this.menuUtils).open();
				} else {
					this.uhcPlayer.getData().buyPerk(selectedPerk);
				}
			}
		}
	}
}
