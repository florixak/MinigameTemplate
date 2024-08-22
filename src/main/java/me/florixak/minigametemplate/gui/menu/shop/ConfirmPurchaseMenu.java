package me.florixak.minigametemplate.gui.menu.shop;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Menu;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Material;
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
		return 35;
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {

		this.uhcPlayer.getPlayer().closeInventory();

		if (event.getSlot() == 11) {
			if (this.kitToBuy != null) {
				this.uhcPlayer.getPlayerData().buyKit(this.menuUtils.getSelectedKitToBuy());
				this.menuUtils.setSelectedKitToBuy(null);
			} else if (this.perkToBuy != null) {
				this.uhcPlayer.getPlayerData().buyPerk(this.menuUtils.getSelectedPerkToBuy());
				this.menuUtils.setSelectedPerkToBuy(null);
			} else {
				this.uhcPlayer.getPlayerData().withdrawMoney(0);
			}
		} else if (event.getSlot() == 15) {
			this.uhcPlayer.sendMessage(Messages.CANCELLED_PURCHASE.toString());
			GameManager.getInstance().getSoundManager().playPurchaseCancelSound(this.uhcPlayer.getPlayer());
		}
	}

	@Override
	public void setMenuItems() {
		this.moneyToWithdraw = this.kitToBuy != null ? this.kitToBuy.getCost() : this.perkToBuy.getCost();

		this.inventory.setItem(11, ItemUtils.createItem(
				XMaterial.matchXMaterial(Material.EMERALD).parseMaterial(),
				TextUtils.color("Confirm purchase of &6%cost% %currency%"
						.replace("%cost%", String.valueOf(this.moneyToWithdraw))
						.replace("%currency%", Messages.CURRENCY.toString())),
				1,
				null)
		);

		this.inventory.setItem(15, ItemUtils.createItem(
				XMaterial.matchXMaterial(Material.REDSTONE).parseMaterial(),
				TextUtils.color("Cancel purchase of &6%cost% %currency%"),
				1,
				null)
		);
	}

	@Override
	public void open() {
		super.open();
	}
}
