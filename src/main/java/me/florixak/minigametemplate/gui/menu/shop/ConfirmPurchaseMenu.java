package me.florixak.minigametemplate.gui.menu.shop;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.gameItems.BuyableItem;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.Menu;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ConfirmPurchaseMenu extends Menu {

	private final GamePlayer uhcPlayer;
	private final BuyableItem toBuy;

	public ConfirmPurchaseMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.uhcPlayer = menuUtils.getGamePlayer();
		this.toBuy = menuUtils.getToBuy();
	}

	@Override
	public String getMenuName() {
		return TextUtils.color(getGui().getTitle());
	}

	@Override
	public int getSlots() {
		return getGui().getSlots();
	}

	@Override
	public Gui getGui() {
		return this.guiManager.getGui(GuiType.PURCHASE_CONFIRM.getKey());
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final ItemStack clickedItem = event.getCurrentItem();

		if (clickedItem.equals(this.guiManager.getItem("filler"))) {
			event.setCancelled(true);
		} else if (event.getSlot() == 11) {
			close();
			if (this.toBuy == null) return;
			this.uhcPlayer.getPlayerData().buy(this.toBuy);
			this.menuUtils.setToBuy(null);
		} else if (event.getSlot() == 15) {
			close();
			this.uhcPlayer.sendMessage(Messages.CANCELLED_PURCHASE.toString());
			GameManager.getInstance().getSoundManager().playPurchaseCancelSound(this.uhcPlayer.getPlayer());
		}
	}

	@Override
	public void setMenuItems() {
		if (this.toBuy == null) return;

		this.inventory.setItem(11, this.guiManager.getItem("confirm"));

		this.inventory.setItem(15, this.guiManager.getItem("cancel"));
	}

	@Override
	public void open() {
		super.open();
	}
}
