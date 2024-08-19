package me.florixak.minigametemplate.game.arena;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.RandomUtils;
import me.florixak.minigametemplate.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Arena {

	private final GameManager gameManager = GameManager.getInstance();

	private final int id;
	private final String name;
	private final boolean enabled;
	private final int maxPlayers;
	private final int minPlayers;
	private final List<Location> spawnLocations;
	private final Location centerLocation;

	private final List<GamePlayer> players = new ArrayList<>();
	private final List<GameTeam> teams = new ArrayList<>();

	private ArenaState arenaState = ArenaState.WAITING;

	public Arena(final int id, final String name, final int maxPlayers, final boolean enabled, final int minPlayers, final List<Location> spawnLocations, final Location centerLocation) {
		this.id = id;
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		this.enabled = enabled;
		this.spawnLocations = spawnLocations;
		this.centerLocation = centerLocation;
	}

	public void loadTeams() {
		final List<GameTeam> availableTeams = this.gameManager.getTeamsManager().getTeamsList();
		for (int i = 0; i < this.spawnLocations.size(); i++) {
			final GameTeam team = availableTeams.get(i);
			team.setSpawnLocation(this.spawnLocations.get(i));
			this.teams.add(team);
		}
	}

	public void join(final GamePlayer player) {
		this.players.add(player);
	}

	public void leave(final GamePlayer player) {
		this.players.remove(player);
	}

	public void leaveAll() {
		this.players.clear();
	}

	public boolean containsPlayer(final Player player) {
		for (final GamePlayer gamePlayer : this.players) {
			if (gamePlayer.getPlayer().equals(player)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsPlayer(final GamePlayer player) {
		return this.players.contains(player);
	}

	public boolean isFull() {
		return this.players.size() >= this.maxPlayers;
	}

	public boolean canStart() {
		return this.players.size() >= this.minPlayers;
	}

	public void setArenaState(final ArenaState arenaState) {
		if (this.arenaState == arenaState) return;

		this.arenaState = arenaState;

		switch (arenaState) {
			case WAITING:
				break;
			case STARTING:
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.GAME_STARTING.toString()));
				break;
			case INGAME:
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.GAME_STARTED.toString()));
				break;
			case ENDING:
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.GAME_ENDED.toString()));
				break;
			case RESTARTING:
				// kick all players, reset arena, etc.
				break;
		}
	}

	public boolean isWaiting() {
		return this.arenaState == ArenaState.WAITING;
	}

	public boolean isStarting() {
		return this.arenaState == ArenaState.STARTING;
	}

	public boolean isPlaying() {
		return this.arenaState == ArenaState.INGAME;
	}

	public boolean isEnding() {
		return this.arenaState == ArenaState.ENDING;
	}

	public Set<GamePlayer> getAlivePlayers() {
		return this.players.stream()
				.filter(GamePlayer::isAlive)
				.filter(GamePlayer::isOnline)
				.collect(Collectors.toSet());
	}

	public Set<GamePlayer> getOnlinePlayers() {
		return this.players.stream()
				.filter(GamePlayer::isOnline)
				.collect(Collectors.toSet());
	}

	public GamePlayer getRandomOnlinePlayer() {
		return RandomUtils.randomOnlinePlayer(getOnlinePlayers().stream().collect(Collectors.toList()));
	}

	public GamePlayer getGamePlayerWithoutPerm(final String perm) {
		final List<GamePlayer> onlineListWithoutPerm = getPlayers().stream().filter(gamePlayer -> !gamePlayer.hasPermission(perm)).collect(Collectors.toList());
		return RandomUtils.randomOnlinePlayer(onlineListWithoutPerm);
	}

	public List<GamePlayer> getDeadPlayers() {
		return this.players.stream().filter(GamePlayer::isDead).filter(GamePlayer::isOnline).collect(Collectors.toList());
	}

	public List<GamePlayer> getSpectatorPlayers() {
		return this.players.stream().filter(GamePlayer::isSpectator).filter(GamePlayer::isOnline).collect(Collectors.toList());
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

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Arena && ((Arena) obj).getId() == this.id;
	}

	@Override
	public String toString() {
		return "Arena(id=" + this.id + ", name=" + this.name + ", enabled=" + this.enabled + ", maxPlayers=" + this.maxPlayers + ", minPlayers=" + this.minPlayers + ")";
	}

	@Override
	public int hashCode() {
		return this.id;
	}
}
