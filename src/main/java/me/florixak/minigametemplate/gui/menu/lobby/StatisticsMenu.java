package me.florixak.minigametemplate.gui.menu.lobby;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.statistics.LeaderboardType;
import me.florixak.minigametemplate.gui.Menu;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StatisticsMenu extends Menu {

	private final GamePlayer uhcPlayer;

	public StatisticsMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.uhcPlayer = menuUtils.getGamePlayer();
	}

	@Override
	public String getMenuName() {
		return TextUtils.color(GameValues.INVENTORY.STATS_TITLE);
	}

	@Override
	public int getSlots() {
		return GameValues.INVENTORY.STATS_SLOTS;
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
			close();
		} else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
			handleStatistics(event);
		}

	}

	@Override
	public void setMenuItems() {
		getInventory().setItem(GameValues.STATISTICS.PLAYER_STATS_SLOT, GameManager.getInstance().getLeaderboardManager().getPlayerStatsItem(this.uhcPlayer));

		getInventory().setItem(GameValues.STATISTICS.TOP_WINS_SLOT, LeaderboardType.WINS.getTopStatsDisplayItem());
		getInventory().setItem(GameValues.STATISTICS.TOP_KILLS_SLOT, LeaderboardType.KILLS.getTopStatsDisplayItem());
		getInventory().setItem(GameValues.STATISTICS.TOP_ASSISTS_SLOT, LeaderboardType.ASSISTS.getTopStatsDisplayItem());
		getInventory().setItem(GameValues.STATISTICS.TOP_DEATHS_SLOT, LeaderboardType.DEATHS.getTopStatsDisplayItem());
		getInventory().setItem(GameValues.STATISTICS.TOP_LOSSES_SLOT, LeaderboardType.LOSSES.getTopStatsDisplayItem());
		getInventory().setItem(GameValues.STATISTICS.TOP_KILLSTREAK_SLOT, LeaderboardType.KILLSTREAK.getTopStatsDisplayItem());
		getInventory().setItem(GameValues.STATISTICS.TOP_LEVEL_SLOT, LeaderboardType.UHC_LEVEL.getTopStatsDisplayItem());
		getInventory().setItem(GameValues.STATISTICS.TOP_GAMES_PLAYED_SLOT, LeaderboardType.GAMES_PLAYED.getTopStatsDisplayItem());

		this.inventory.setItem(getSlots() - 5, ItemUtils.createItem(
				XMaterial.matchXMaterial(GameValues.INVENTORY.CLOSE_ITEM).get().parseMaterial(),
				TextUtils.color(GameValues.INVENTORY.CLOSE_TITLE),
				1,
				null));
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
