package me.florixak.minigametemplate.gui.menu.inGame;

import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.Menu;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class LeaveConfirmMenu extends Menu {

	private final GamePlayer gamePlayer;

	public LeaveConfirmMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.gamePlayer = menuUtils.getGamePlayer();
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
		return this.guiManager.getGui(GuiType.LEAVE_CONFIRM.getKey());
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem.equals(this.guiManager.getItem("confirm"))) {
			close();
			this.gameManager.getArenaManager().getPlayerArena(this.gamePlayer).leave(this.gamePlayer);
		} else if (clickedItem.equals(this.guiManager.getItem("cancel"))) {
			close();
		}
	}

	@Override
	public void setMenuItems() {
		getInventory().setItem(11, this.guiManager.getItem("confirm"));
		getInventory().setItem(15, this.guiManager.getItem("cancel"));
	}
}
