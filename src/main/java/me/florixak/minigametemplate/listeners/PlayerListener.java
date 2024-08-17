package me.florixak.minigametemplate.listeners;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameState;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.Permissions;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.player.PlayerState;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

	private final GameManager gameManager;

	public PlayerListener(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void handleLogin(final PlayerLoginEvent event) {

		final boolean isPlaying = this.gameManager.isPlaying();
		final boolean isFull = this.gameManager.isGameFull();
		final boolean isEnding = this.gameManager.isEnding();

		if (isEnding) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Messages.GAME_ENDED.toString());
		} else if (!isPlaying && isFull) {
			if (!event.getPlayer().hasPermission(Permissions.RESERVED_SLOT.getPerm()) && !event.getPlayer().hasPermission(Permissions.VIP.getPerm())) {
				event.disallow(PlayerLoginEvent.Result.KICK_FULL, Messages.GAME_FULL.toString());
				return;
			}
			final GamePlayer randomGamePlayer = this.gameManager.getPlayerManager().getGamePlayerWithoutPerm(Permissions.RESERVED_SLOT.getPerm());
			if (randomGamePlayer == null) {
				event.disallow(PlayerLoginEvent.Result.KICK_FULL, Messages.GAME_FULL.toString());
				return;
			}
			randomGamePlayer.kick(Messages.KICK_DUE_RESERVED_SLOT.toString());
			event.allow();

		} else if (isPlaying && isFull) {
			event.disallow(PlayerLoginEvent.Result.KICK_FULL, Messages.GAME_FULL.toString());
		}
	}

	@EventHandler
	public void handleJoin(final PlayerJoinEvent event) {
		event.setJoinMessage(null);
		final Player p = event.getPlayer();

		final GamePlayer gamePlayer;
		if (this.gameManager.getPlayerManager().doesPlayerExist(p)) {
			gamePlayer = this.gameManager.getPlayerManager().getUHCPlayer(p);
		} else {
			gamePlayer = this.gameManager.getPlayerManager().newUHCPlayer(p);
		}

		this.gameManager.getScoreboardManager().setScoreboard(p);
//        gameManager.getTaskManager().getPlayingTimeTask().playerJoined(uhcPlayer);

		final boolean isPlaying = this.gameManager.isPlaying();

		if (isPlaying) {
			gamePlayer.setSpectator();
			return;
		}

		this.gameManager.getPlayerManager().setPlayerWaitsAtLobby(gamePlayer);

		Utils.broadcast(PAPI.setPlaceholders(p, Messages.JOIN.toString()));
		Utils.broadcast(PAPI.setPlaceholders(p, Messages.PLAYERS_TO_START.toString()));

//		if (!gameManager.isStarting() && gameManager.getPlayerManager().getOnlinePlayers().size() >= GameValues.GAME.PLAYERS_TO_START) {
//			gameManager.setGameState(GameState.STARTING);
//		}
	}

	@EventHandler
	public void handleQuit(final PlayerQuitEvent event) {

		event.setQuitMessage(null);
		final Player p = event.getPlayer();

		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
		this.gameManager.getScoreboardManager().removeScoreboard(gamePlayer.getPlayer());

		if (this.gameManager.getGameState().equals(GameState.LOBBY) || this.gameManager.isStarting() || this.gameManager.isEnding()) {
			Utils.broadcast(PlaceholderUtil.setPlaceholders(Messages.QUIT.toString().replace("%online%", String.valueOf(this.gameManager.getPlayerManager().getOnlinePlayers().size() - 1)), p));
			gamePlayer.leaveTeam();
			this.gameManager.getPlayerManager().getPlayersList().remove(gamePlayer);
//			if (gameManager.isStarting() && !gameManager.getTaskManager().getGameCheckTask().canStart()) {
//				gameManager.getTaskManager().cancelStartingTask();
//				Utils.broadcast(Messages.GAME_STARTING_CANCELED.toString());
//				gameManager.setGameState(GameState.LOBBY);
//			}
		} else if (this.gameManager.isPlaying() && !this.gameManager.isEnding()) {
			gamePlayer.setState(PlayerState.DEAD);
			if (this.gameManager.getDamageTrackerManager().isInTracker(gamePlayer)) {
				final GamePlayer attacker = this.gameManager.getDamageTrackerManager().getAttacker(gamePlayer);
				final GamePlayer assistant = this.gameManager.getDamageTrackerManager().getAssistant(gamePlayer);
				this.gameManager.getDamageTrackerManager().onDead(gamePlayer);
				attacker.kill(gamePlayer);
				if (assistant != null) {
					assistant.assist(gamePlayer);
				}
				Utils.broadcast(Messages.KILL.toString()
						.replace("%player%", gamePlayer.getName())
						.replace("%killer%", attacker.getName()));
			}
		}
	}

	@EventHandler
	public void handleDeath(final PlayerDeathEvent event) {
		event.setDeathMessage(null);

		GamePlayer uhcKiller = null;
		if (event.getEntity().getKiller() instanceof Player) {
			uhcKiller = this.gameManager.getPlayerManager().getUHCPlayer(event.getEntity().getKiller().getUniqueId());
		}
		final GamePlayer uhcVictim = this.gameManager.getPlayerManager().getUHCPlayer(event.getEntity().getPlayer().getUniqueId());

//		Bukkit.getServer().getPluginManager().callEvent(new GameKillEvent(uhcKiller, uhcVictim));
	}

	@EventHandler
	public void handleItemDrop(final PlayerDropItemEvent event) {
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());

		if (!this.gameManager.isPlaying() || gamePlayer.isDead() || this.gameManager.isEnding()) {
			event.setCancelled(true);
		}
	}

	@Deprecated
	@EventHandler
	public void handleItemPickUp(final PlayerPickupItemEvent event) {
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());

		if (!this.gameManager.isPlaying() || gamePlayer.isDead() || this.gameManager.isEnding()) {
			event.setCancelled(true);
			return;
		}

		if (gamePlayer.getQuestData().hasQuestWithTypeOf("PICKUP")) {
			gamePlayer.getQuestData().addProgressToTypes("PICKUP", event.getItem().getItemStack().getType());
		}
	}

	@EventHandler
	public void handlePortalTeleport(final PlayerTeleportEvent event) {
		if (!GameValues.GAME.NETHER_ENABLED && event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
			event.setCancelled(true);
		}
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleBucketEmpty(final PlayerBucketEmptyEvent event) {
		if (!this.gameManager.isPlaying() || this.gameManager.getGameState().equals(GameState.ENDING)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleBucketFill(final PlayerBucketFillEvent event) {
		if (!this.gameManager.isPlaying() || this.gameManager.getGameState().equals(GameState.ENDING)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEnchantPrepare(final PrepareItemEnchantEvent event) {
		if (UHCRevamp.useOldMethods) {
			// Automatically add lapis lazuli to the enchantment table for 1.8.8
			try {
				event.getInventory().setItem(1, new ItemStack(XMaterial.LAPIS_LAZULI.parseMaterial(), 3)); // 3 lapis lazuli
			} catch (final Exception e) {
			}
		} else {
			// For newer versions, allow enchanting without lapis lazuli
			event.getInventory().setItem(1, new ItemStack(XMaterial.LAPIS_LAZULI.parseMaterial(), 3)); // 3 lapis lazuli
		}
	}
}