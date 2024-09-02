package me.florixak.minigametemplate.listeners;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	private final GameManager gameManager;

	public PlayerListener(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void handleJoin(final PlayerJoinEvent event) {
		event.setJoinMessage(null);
		final Player p = event.getPlayer();

		final GamePlayer gamePlayer;
		if (this.gameManager.getPlayerManager().doesPlayerExist(p)) {
			gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(p);
			Bukkit.getLogger().info("Player exists: " + p.getName());
		} else {
			gamePlayer = this.gameManager.getPlayerManager().newGamePlayer(p);
			Bukkit.getLogger().info("New player: " + p.getName());
		}

		this.gameManager.getScoreboardManager().setScoreboard(p);

		this.gameManager.getPlayerManager().setPlayerForLobby(gamePlayer);
		Utils.broadcast(PAPI.setPlaceholders(p, Messages.LOBBY_JOIN.toString()));
	}

	@EventHandler
	public void handleQuit(final PlayerQuitEvent event) {
		event.setQuitMessage(null);
		final Player p = event.getPlayer();

		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(p.getUniqueId());
		this.gameManager.getPlayerManager().removePlayer(gamePlayer);
	}

	@EventHandler
	public void handleDeath(final PlayerDeathEvent event) {
		event.setDeathMessage(null);

		GamePlayer uhcKiller = null;
		if (event.getEntity().getKiller() instanceof Player) {
			uhcKiller = this.gameManager.getPlayerManager().getGamePlayer(event.getEntity().getKiller().getUniqueId());
		}
		final GamePlayer uhcVictim = this.gameManager.getPlayerManager().getGamePlayer(event.getEntity().getPlayer().getUniqueId());

//		Bukkit.getServer().getPluginManager().callEvent(new GameKillEvent(uhcKiller, uhcVictim));
	}

	@EventHandler
	public void handleItemDrop(final PlayerDropItemEvent event) {
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(event.getPlayer().getUniqueId());
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(event.getPlayer());

		if (!checkIfPlayerCan(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void handleItemPickUp(final PlayerPickupItemEvent event) {
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(event.getPlayer().getUniqueId());
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(event.getPlayer());

		if (!checkIfPlayerCan(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}

		if (gamePlayer.getQuestData().hasQuestWithTypeOf("PICKUP")) {
			gamePlayer.getQuestData().addProgressToTypes("PICKUP", event.getItem().getItemStack().getType());
		}
	}

	private boolean checkIfPlayerCan(final Player player) {
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(player.getUniqueId());
		if (gamePlayer.isInArena()) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(player);
			return arena.isPlaying() && !gamePlayer.getArenaData().isSpectator();
		}
		return false;
	}
}