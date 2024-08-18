package me.florixak.minigametemplate.listeners;

import me.florixak.minigametemplate.game.GameState;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.menu.*;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.managers.MenuManager;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MenuItemsInteractListener implements Listener {

	private final GameManager gameManager;

	public MenuItemsInteractListener(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void onItemClick(final PlayerInteractEvent event) {

		final Player p = event.getPlayer();
		final GamePlayer uhcPlayer = this.gameManager.getPlayerManager().getGamePlayer(p.getUniqueId());
		final ItemStack item = p.getInventory().getItemInHand();

		if (this.gameManager.getGameState() == GameState.LOBBY || this.gameManager.getGameState() == GameState.STARTING) {
			if (item == null || item.getItemMeta() == null || item.getType() == Material.AIR || item.getItemMeta().getDisplayName() == null)
				return;

			if (event.getAction() == Action.RIGHT_CLICK_AIR) {

				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
						TextUtils.color(GameValues.INVENTORY.TEAMS_TITLE))) {
					new TeamsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
				}
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
						TextUtils.color(GameValues.INVENTORY.KITS_TITLE))) {
					new KitsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
				}
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
						TextUtils.color(GameValues.INVENTORY.PERKS_TITLE))) {
					new PerksMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
				}
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
						TextUtils.color(GameValues.INVENTORY.STATS_TITLE))) {
					new StatisticsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
				}
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
						TextUtils.color(GameValues.INVENTORY.QUESTS_TITLE))) {
					new QuestsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
				}
			}
		}
	}
}
