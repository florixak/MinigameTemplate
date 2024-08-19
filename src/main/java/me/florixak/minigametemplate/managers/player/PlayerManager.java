package me.florixak.minigametemplate.managers.player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.player.PlayerState;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.RandomUtils;
import me.florixak.minigametemplate.utils.TeleportUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerManager {

	private final GameManager gameManager;
	@Getter
	private final List<GamePlayer> players;
	@Getter
	private int maxPlayersWhenTeams;

	public PlayerManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.players = new ArrayList<>();
	}

	public boolean doesPlayerExist(final Player player) {
		if (getGamePlayer(player.getUniqueId()) != null) return true;
		return false;
	}

	public GamePlayer getGamePlayer(final UUID uuid) {
		for (final GamePlayer gamePlayer : getPlayersList()) {
			if (gamePlayer.getUuid().equals(uuid)) {
				return gamePlayer;
			}
		}
		return null;
	}

	public GamePlayer getGamePlayer(final String name) {
		for (final GamePlayer gamePlayer : getPlayersList()) {
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
		getPlayersList().add(newPlayer);
		return newPlayer;
	}

	public Set<GamePlayer> getAlivePlayers() {
		return this.players.stream()
				.filter(GamePlayer::isAlive)
				.filter(GamePlayer::isOnline)
				.collect(Collectors.toSet());
	}

	public Set<GamePlayer> getAllAlivePlayers() {
		return this.players.stream()
				.filter(GamePlayer::isAlive)
				.collect(Collectors.toSet());
	}

	public Set<GamePlayer> getOnlinePlayers() {
		return this.players.stream()
				.filter(GamePlayer::isOnline)
				.collect(Collectors.toSet());
	}

	public synchronized List<GamePlayer> getPlayersList() {
		return this.players;
	}

	public List<GamePlayer> getDeadPlayers() {
		return this.players.stream().filter(GamePlayer::isDead).filter(GamePlayer::isOnline).collect(Collectors.toList());
	}

	public List<GamePlayer> getSpectatorPlayers() {
		return this.players.stream().filter(GamePlayer::isSpectator).filter(GamePlayer::isOnline).collect(Collectors.toList());
	}

	public GamePlayer getRandomOnlineUHCPlayer() {
		return RandomUtils.randomOnlinePlayer(getOnlinePlayers().stream().collect(Collectors.toList()));
	}

	public GamePlayer getGamePlayerWithoutPerm(final String perm) {
		final List<GamePlayer> onlineListWithoutPerm = getPlayers().stream().filter(gamePlayer -> !gamePlayer.hasPermission(perm)).collect(Collectors.toList());
		return RandomUtils.randomOnlinePlayer(onlineListWithoutPerm);
	}

	public GamePlayer getWinnerPlayer() {
		return this.players.stream().filter(GamePlayer::isWinner).filter(GamePlayer::isOnline).findFirst().orElse(null);
	}

	public void setWinner() {
		final GamePlayer winner = getAlivePlayers().stream()
				.filter(GamePlayer::isOnline)
				.max(Comparator.comparingInt(GamePlayer::getKills))
				.orElse(null);

		if (winner == null) return;

		if (GameValues.TEAM.TEAM_MODE) {
			winner.getTeam().getMembers().forEach(member -> member.setWinner(true));
		} else {
			winner.setWinner(true);
		}
	}

	public String getWinner() {
		if (GameValues.TEAM.TEAM_MODE) {
			final GameTeam winnerTeam = this.gameManager.getTeamsManager().getWinnerTeam();
			return winnerTeam != null ? (winnerTeam.getMembers().size() == 1 ? winnerTeam.getMembers().get(0).getName() : winnerTeam.getName()) : "None";
		}
		return getWinnerPlayer() != null ? getWinnerPlayer().getName() : "None";
	}

	private List<GamePlayer> findTopKillers(final List<GamePlayer> players) {
		players.sort((gamePlayer1, gamePlayer2) -> Integer.compare(gamePlayer2.getKills(), gamePlayer1.getKills()));
		return players;
	}

	public List<GamePlayer> getTopKillers() {
		return findTopKillers(getPlayers());
	}

	public void setMaxPlayers() {
		this.maxPlayersWhenTeams = Math.min(this.gameManager.getTeamsManager().getTeamsList().size() * GameValues.TEAM.TEAM_SIZE, Bukkit.getMaxPlayers());
	}

	public int getMaxPlayers() {
		if (GameValues.TEAM.TEAM_MODE) return this.maxPlayersWhenTeams;
		return Bukkit.getMaxPlayers();
	}

	public void setPlayerWaitsAtLobby(final GamePlayer gamePlayer) {
		final Player p = gamePlayer.getPlayer();
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setExhaustion(0);
		p.setExp(0);
		p.setLevel(0);
		p.setFireTicks(0);
		p.setGameMode(GameMode.ADVENTURE);

		p.teleport(this.gameManager.getLobbyManager().getWaitingLobbyLocation());

		gamePlayer.clearPotions();
		gamePlayer.clearInventory();
		this.gameManager.getKitsManager().giveLobbyKit(gamePlayer);
	}

	public void setPlayerForGame(final GamePlayer gamePlayer) {
		gamePlayer.setState(PlayerState.ALIVE);

		gamePlayer.setGameMode(GameMode.SURVIVAL);
		gamePlayer.getPlayer().setHealth(gamePlayer.getPlayer().getMaxHealth());
		gamePlayer.getPlayer().setFoodLevel(20);
		gamePlayer.getPlayer().setExhaustion(0);

		gamePlayer.clearPotions();
		gamePlayer.clearInventory();

		if (GameValues.TEAM.TEAM_MODE && !gamePlayer.hasTeam()) {
			this.gameManager.getTeamsManager().joinRandomTeam(gamePlayer);
		}

		if (gamePlayer.hasKit()) {
			if (!GameValues.KITS.BOUGHT_FOREVER) {
				gamePlayer.getData().withdrawMoney(gamePlayer.getKit().getCost());
				gamePlayer.sendMessage(PAPI.setPlaceholders(gamePlayer.getPlayer(), Messages.KITS_MONEY_DEDUCT.toString()
						.replace("%previous-money%", String.valueOf((gamePlayer.getData().getMoney() + gamePlayer.getKit().getCost())))
						.replace("%current-money%", String.valueOf(gamePlayer.getData().getMoney())
						))
				);
			}
			gamePlayer.getKit().giveKit(gamePlayer);
		}
	}

	public void teleportInToGame() {
		for (final GamePlayer gamePlayer : getAlivePlayers()) {
			final Location location = TeleportUtils.getSafeLocation();
			gamePlayer.setSpawnLocation(location);
			gamePlayer.teleport(location);
		}
	}

	public void teleportAfterMining() {
		for (final GamePlayer gamePlayer : getAlivePlayers()) {
			final Location location = gamePlayer.getPlayer().getLocation();

			final double y = location.getWorld().getHighestBlockYAt(location);
			location.setY(y);

			gamePlayer.teleport(location);
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
		for (final GamePlayer gamePlayer : getPlayersList()) {
			gamePlayer.reset();
		}
		this.players.clear();
	}
}