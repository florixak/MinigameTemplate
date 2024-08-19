package me.florixak.minigametemplate.game.arena;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.tasks.ArenaCheckTask;
import me.florixak.minigametemplate.tasks.StartingTask;
import me.florixak.minigametemplate.utils.RandomUtils;
import me.florixak.minigametemplate.utils.Utils;
import org.bukkit.Location;

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
	private final List<GameTeam> teams;
	private final Location centerLocation;

	private final List<GamePlayer> players = new ArrayList<>();

	private final ArenaCheckTask arenaCheckTask = new ArenaCheckTask(this);
	private final StartingTask startingTask = new StartingTask(this);

	private ArenaState arenaState = ArenaState.WAITING;

	public Arena(final int id, final String name, final boolean enabled, final int maxPlayers, final int minPlayers, final Location centerLocation, final List<GameTeam> teams) {
		this.id = id;
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		this.enabled = enabled;
		this.centerLocation = centerLocation;
		this.teams = teams;

		this.arenaCheckTask.runTaskTimer(this.gameManager.getPlugin(), 0L, 20L);
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

	public boolean isFull() {
		return this.players.size() >= this.maxPlayers;
	}

	public boolean canStart() {
		return this.players.size() >= this.minPlayers;
	}

	public void start() {
		setArenaState(ArenaState.STARTING);
		this.startingTask.runTaskTimer(this.gameManager.getPlugin(), 0, 20);
	}

	public void stopStarting() {
		this.arenaCheckTask.cancel();
	}

	public boolean canEnd() {
		return this.players.size() <= 1 && this.arenaState == ArenaState.INGAME;
	}

	public void end() {
		setArenaState(ArenaState.ENDING);
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
				setWinner();
				Utils.broadcast(PAPI.setPlaceholders(null, "%winner% is WINNER!".replace("%winner%", getWinner())));
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.GAME_ENDED.toString()));
				break;
			case RESTARTING:
				leaveAll();
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

	public boolean isPlayerIn(final GamePlayer player) {
		return this.players.contains(player);
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

	public GamePlayer getRandomPlayer() {
		return RandomUtils.randomOnlinePlayer(getOnlinePlayers().stream().collect(Collectors.toList()));
	}

	public GamePlayer getPlayerWithoutPerm(final String perm) {
		final List<GamePlayer> onlineListWithoutPerm = getPlayers().stream().filter(GamePlayer::isOnline).filter(gamePlayer -> !gamePlayer.hasPermission(perm)).collect(Collectors.toList());
		return RandomUtils.randomOnlinePlayer(onlineListWithoutPerm);
	}

	public List<GamePlayer> getDeadPlayers() {
		return this.players.stream().filter(GamePlayer::isDead).filter(GamePlayer::isOnline).collect(Collectors.toList());
	}

	public List<GamePlayer> getSpectatorPlayers() {
		return this.players.stream().filter(GamePlayer::isSpectator).filter(GamePlayer::isOnline).collect(Collectors.toList());
	}

	public void joinRandomTeam(final GamePlayer gamePlayer) {
		GameTeam team = this.teams.stream().filter(gameTeam -> gameTeam.getMembers().isEmpty()).findFirst().orElse(null);
		if (team == null) team = this.teams.stream().filter(gameTeam -> !gameTeam.isFull()).findFirst().orElse(null);
		if (team == null) {
			gamePlayer.setSpectator();
			return;
		}
		team.addMember(gamePlayer);
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
			final GameTeam winnerTeam = winner.getTeam();
			winnerTeam.setWinners();
		} else {
			winner.setWinner();
		}
	}

	public String getWinner() {
		if (GameValues.TEAM.TEAM_MODE) {
			final GameTeam winnerTeam = this.teams.stream().filter(GameTeam::isWinner).findFirst().orElse(null);
			return winnerTeam != null ? (winnerTeam.getMembers().size() == 1 ? winnerTeam.getMembers().get(0).getName() : winnerTeam.getName()) : "None";
		}
		return getWinnerPlayer() != null ? getWinnerPlayer().getName() : "None";
	}

	public void broadcast(final String message) {
		getOnlinePlayers().forEach(player -> player.sendMessage(message));
	}

	public List<String> getLore() {
		final List<String> lore = new ArrayList<>();
		lore.add(" ");
		lore.add("&fPlayers: &e" + getPlayers().size() + "/" + getMaxPlayers());
		lore.add("&fState: &e" + getArenaState().toString());
		lore.add(" ");
		lore.add("&fTeams: &e" + getTeams().get(0).getSize() + "x" + getTeams().size());
		lore.add(" ");
		lore.add("&6Click To Join!");
		return lore;
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
