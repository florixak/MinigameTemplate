package me.florixak.minigametemplate.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Menu;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

	private final GameManager gameManager;

	public InventoryClickListener(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void handleInventoryClick(final InventoryClickEvent event) {
		if (event.getClickedInventory() == null || isNull(event.getCurrentItem()) || event.getClickedInventory() instanceof AnvilInventory)
			return;

		final Player player = (Player) event.getWhoClicked();
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(player.getUniqueId());

		event.setCancelled(true);

		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			if (arena.isPlaying()) {
				event.setCancelled(false);
			}
		}
		final InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Menu) {
			event.setCancelled(true);
			if (event.getCurrentItem() == null) {
				return;
			}
			final Menu menu = (Menu) holder;
			menu.handleMenuClicks(event);
		}
	}

	private boolean isNull(final ItemStack item) {
		return item == null || item.getType().equals(XMaterial.AIR.parseMaterial());
	}
}