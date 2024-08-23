package me.florixak.minigametemplate.listeners;

import me.florixak.minigametemplate.game.gameItems.ExecutableItem;
import me.florixak.minigametemplate.game.gameItems.KitsShopItem;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyListener implements Listener {

	private final GameManager gameManager;

	public LobbyListener(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void onItemClick(final PlayerInteractEvent event) {

		final Player p = event.getPlayer();
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(p.getUniqueId());
//		if (!gamePlayer.isLobby()) return;
		final ItemStack item = gamePlayer.getInventory().getItemInHand();

		if (item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			return;

		final String itemName = item.getItemMeta().getDisplayName();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final ExecutableItem executableItem = this.gameManager.getGameItemManager().getExecutableItem(itemName);
			if (executableItem == null) return;
			switch (executableItem.getKey()) {
				case "kits-selector":
					((KitsShopItem) executableItem).execute(gamePlayer);
					break;

			}


		}
	}


}
