package me.florixak.minigametemplate.listeners;

import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
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

		if (this.gameManager.getArenaManager().isPlayerInArena(uhcPlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(uhcPlayer);

			if (arena.isWaiting() || arena.isStarting()) {
				if (item == null || item.getItemMeta() == null || item.getType() == Material.AIR || item.getItemMeta().getDisplayName() == null)
					return;

				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (item.equals(this.gameManager.getGameItemManager().getLobbyItem(p.getInventory().getHeldItemSlot()))) {
						arena.leave(uhcPlayer);
					}

				}
			}
		}
	}
}
