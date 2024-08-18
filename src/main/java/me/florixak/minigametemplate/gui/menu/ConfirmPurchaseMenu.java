package me.florixak.minigametemplate.gui.menu;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Menu;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ConfirmPurchaseMenu extends Menu {

	private final GamePlayer uhcPlayer;
	private final Kit kitToBuy;
	private final Perk perkToBuy;
	private double moneyToWithdraw = 0;

	public ConfirmPurchaseMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.uhcPlayer = menuUtils.getGamePlayer();
		this.kitToBuy = menuUtils.getSelectedKitToBuy();
		this.perkToBuy = menuUtils.getSelectedPerkToBuy();
	}

	@Override
	public String getMenuName() {
		return TextUtils.color("&6Are you sure?");
	}

	@Override
	public int getSlots() {
		return GameValues.INVENTORY.CONFIRM_PURCHASE_SLOTS;
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {

		this.uhcPlayer.getPlayer().closeInventory();

		if (event.getSlot() == 11) {
			if (this.kitToBuy != null) {
				this.uhcPlayer.getData().buyKit(this.menuUtils.getSelectedKitToBuy());
				this.menuUtils.setSelectedKitToBuy(null);
			} else if (this.perkToBuy != null) {
				this.uhcPlayer.getData().buyPerk(this.menuUtils.getSelectedPerkToBuy());
				this.menuUtils.setSelectedPerkToBuy(null);
			} else {
				this.uhcPlayer.getData().withdrawMoney(0);
			}
		} else if (event.getSlot() == 15) {
			this.uhcPlayer.sendMessage(Messages.CANCELLED_PURCHASE.toString());
			GameManager.getGameManager().getSoundManager().playPurchaseCancelSound(this.uhcPlayer.getPlayer());
		}
	}

	@Override
	public void setMenuItems() {
		this.moneyToWithdraw = this.kitToBuy != null ? this.kitToBuy.getCost() : this.perkToBuy.getCost();

		this.inventory.setItem(11, ItemUtils.createItem(
				XMaterial.matchXMaterial(GameValues.INVENTORY.CONFIRM_PURCHASE_ITEM).get().parseMaterial(),
				TextUtils.color(GameValues.INVENTORY.CONFIRM_PURCHASE_NAME
						.replace("%cost%", String.valueOf(this.moneyToWithdraw))
						.replace("%currency%", Messages.CURRENCY.toString())),
				1,
				null)
		);
		this.inventory.setItem(15, ItemUtils.createItem(
				XMaterial.matchXMaterial(GameValues.INVENTORY.CANCEL_PURCHASE_ITEM).get().parseMaterial(),
				TextUtils.color(GameValues.INVENTORY.CANCEL_PURCHASE_NAME),
				1,
				null)
		);
	}

	@Override
	public void open() {
		super.open();
	}
}
