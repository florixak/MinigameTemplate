package me.florixak.minigametemplate.gui.menu.lobby;

import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.Menu;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ProfileMenu extends Menu {

	private final GamePlayer uhcPlayer;

	public ProfileMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.uhcPlayer = menuUtils.getGamePlayer();
	}

	@Override
	public String getMenuName() {
		return TextUtils.color(getGui().getTitle());
	}

	@Override
	public Gui getGui() {
		return this.guiManager.getGui(GuiType.PROFILE.getKey());
	}

	@Override
	public int getSlots() {
		return getGui().getSlots();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem.equals(this.guiManager.getItem("filler"))) {
			event.setCancelled(true);
		} else if (clickedItem.equals(this.guiManager.getItem("close"))) {
			close();
		}
	}

	@Override
	public void setMenuItems() {
		getInventory().setItem(GameValues.STATISTICS.PLAYER_STATS_SLOT, GameManager.getInstance().getLeaderboardManager().getPlayerStatsItem(this.uhcPlayer));

		this.inventory.setItem(getSlots() - 5, this.guiManager.getItem("close"));
	}

	private void handleStatistics(final InventoryClickEvent event) {
		event.setCancelled(true);
//		if (ItemUtils.hasItemMeta(event.getCurrentItem())) {
//			if (event.getRawSlot() == GameValues.STATISTICS.TOP_SLOT) {
//				String displayedTop = uhcPlayer.getData().getDisplayedTop();
//				List<String> displayedTops = GameValues.STATISTICS.DISPLAYED_TOPS;
//				for (int i = 0; i < displayedTops.size(); i++) {
//					if (displayedTop.equalsIgnoreCase(displayedTops.get(i))) {
//						if (displayedTops.get(i).equalsIgnoreCase(displayedTops.get(displayedTops.size() - 1)))
//							uhcPlayer.getData().setDisplayedTop(displayedTops.get(0));
//						else uhcPlayer.getData().setDisplayedTop(displayedTops.get(i + 1));
//					}
//				}
//				close();
//				new StatisticsMenu(menuUtils).open();
//			}
//		}
	}
}
