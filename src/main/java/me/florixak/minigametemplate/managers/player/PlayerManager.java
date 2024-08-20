package me.florixak.minigametemplate.managers.player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.player.PlayerState;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager {

	private final GameManager gameManager;
	@Getter
	private final List<GamePlayer> players;

	public PlayerManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.players = new ArrayList<>();
	}

	public boolean doesPlayerExist(final Player player) {
		return getGamePlayer(player.getUniqueId()) != null;
	}

	public GamePlayer getGamePlayer(final UUID uuid) {
		for (final GamePlayer gamePlayer : getPlayers()) {
			if (gamePlayer.getUuid().equals(uuid)) {
				return gamePlayer;
			}
		}
		return null;
	}

	public GamePlayer getGamePlayer(final String name) {
		for (final GamePlayer gamePlayer : getPlayers()) {
			if (gamePlayer.getName().equals(name)) {
				return gamePlayer;
			}
		}
		return null;
	}

	public GamePlayer getGamePlayer(final Player player) {
		return getGamePlayer(player.getUniqueId());
	}

	public GamePlayer getOrCreateGamePlayer(final Player player) {
		if (doesPlayerExist(player)) {
			return getGamePlayer(player);
		} else {
			return newGamePlayer(player);
		}
	}

	public synchronized GamePlayer newGamePlayer(final Player player) {
		return newGamePlayer(player.getUniqueId(), player.getName());
	}

	public synchronized GamePlayer newGamePlayer(final UUID uuid, final String name) {
		final GamePlayer newPlayer = new GamePlayer(uuid, name);
		getPlayers().add(newPlayer);
		return newPlayer;
	}

	public void removePlayer(final GamePlayer gamePlayer) {
		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			arena.leave(gamePlayer);
		}
		this.gameManager.getScoreboardManager().removeScoreboard(gamePlayer.getPlayer());
		this.gameManager.getPlayerQuestDataManager().removePlayerData(gamePlayer);
		this.gameManager.getPlayerDataManager().removePlayerData(gamePlayer);
		getPlayers().remove(gamePlayer);
	}

	public List<GamePlayer> getOnlinePlayers() {
		return getPlayers().stream().filter(GamePlayer::isOnline).collect(Collectors.toList());
	}

	public List<GamePlayer> getPlayersInLobby() {
		return getPlayers().stream().filter(gamePlayer -> gamePlayer.getState() == PlayerState.LOBBY).collect(Collectors.toList());
	}

	public List<GamePlayer> getPlayersInArenas() {
		return getPlayers().stream().filter(gamePlayer -> gamePlayer.getState() != PlayerState.LOBBY).collect(Collectors.toList());
	}


	public void setPlayerAtLobby(final GamePlayer gamePlayer) {
		final Player p = gamePlayer.getPlayer();
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setExhaustion(0);
		p.setExp(0);
		p.setLevel(0);
		p.setFireTicks(0);
		p.setGameMode(GameMode.ADVENTURE);

		p.teleport(this.gameManager.getLobbyManager().getLobbyLocation());

		gamePlayer.clearPotions();
		gamePlayer.clearInventory();
		this.gameManager.getLobbyManager().giveLobbyItems(gamePlayer);
	}

	public void setPlayerForGame(final GamePlayer gamePlayer) {
		gamePlayer.setState(PlayerState.ALIVE);

		gamePlayer.setGameMode(GameMode.SURVIVAL);
		gamePlayer.getPlayer().setHealth(gamePlayer.getPlayer().getMaxHealth());
		gamePlayer.getPlayer().setFoodLevel(20);
		gamePlayer.getPlayer().setExhaustion(0);

		gamePlayer.clearPotions();
		gamePlayer.clearInventory();

		if (!gamePlayer.hasTeam()) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			arena.joinRandomTeam(gamePlayer);
		}

		if (gamePlayer.hasKit()) {
			if (!GameValues.KITS.BOUGHT_FOREVER) {
				gamePlayer.getPlayerData().withdrawMoney(gamePlayer.getKit().getCost());
				gamePlayer.sendMessage(PAPI.setPlaceholders(gamePlayer.getPlayer(), Messages.KITS_MONEY_DEDUCT.toString()
						.replace("%previous-money%", String.valueOf((gamePlayer.getPlayerData()).getMoney() + gamePlayer.getKit().getCost()))
						.replace("%current-money%", String.valueOf(gamePlayer.getPlayerData().getMoney())
						))
				);
			}
			gamePlayer.getKit().giveKit(gamePlayer);
		}
	}

	public void sendPlayersToBungeeLobby() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			try {
				final ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(GameValues.BUNGEECORD.LOBBY_SERVER);
				player.sendPluginMessage(MinigameTemplate.getInstance(), "BungeeCord", out.toByteArray());
			} catch (final Exception e) {
				Bukkit.getLogger().info("Failed to send " + player.getName() + " to the lobby server.");
			}
		}
	}

	public void onDisable() {
		for (final GamePlayer gamePlayer : getPlayers()) {
			gamePlayer.reset();
		}
		this.players.clear();
	}
}