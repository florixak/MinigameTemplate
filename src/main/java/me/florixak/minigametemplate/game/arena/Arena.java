package me.florixak.minigametemplate.game.arena;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import lombok.Setter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.tasks.ArenaCheckTask;
import me.florixak.minigametemplate.tasks.StartingTask;
import me.florixak.minigametemplate.utils.RandomUtils;
import me.florixak.minigametemplate.utils.Utils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Arena {

	protected static final GameManager gameManager = GameManager.getInstance();
	protected static final File pluginFile = new File(MinigameTemplate.getInstance().getDataFolder(), "arenas");

	protected final String id;
	protected final String name;
	protected boolean enabled;
	protected int maxPlayers;
	protected final int minPlayers;
	protected final Location centerLocation;
	@Setter
	protected Location waitingLocation;
	@Setter
	protected Location endingLocation;
	protected final List<GameTeam> teams;

	protected final List<GamePlayer> players = new ArrayList<>();

	protected final ArenaCheckTask arenaCheckTask = new ArenaCheckTask(this);
	protected final StartingTask startingTask = new StartingTask(this);

	protected ArenaState arenaState = ArenaState.WAITING;

	public Arena(final String id, final String name, final Location centerLocation, final int minPlayers) {
		this.id = id;
		this.name = name;
		this.minPlayers = minPlayers;
		this.enabled = false;
		this.maxPlayers = 0;
		this.centerLocation = centerLocation;
		this.waitingLocation = centerLocation;
		this.endingLocation = centerLocation;
		this.teams = new ArrayList<>();
	}

	public Arena(final String id,
				 final String name,
				 final boolean enabled,
				 final Location centerLocation,
				 final Location waitingLocation,
				 final Location endingLocation,
				 final int minPlayers,
				 final int maxPlayers,
				 final List<GameTeam> teams) {
		this.id = id;
		this.name = name;
		this.enabled = enabled;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.centerLocation = centerLocation;
		this.waitingLocation = waitingLocation;
		this.endingLocation = endingLocation;
		this.teams = teams;

		if (enabled)
			this.arenaCheckTask.runTaskTimer(MinigameTemplate.getInstance(), 0L, 20L);
	}

	public void addTeam(final GameTeam team) {
		this.teams.add(team);
		this.maxPlayers += team.getSize();
	}

	public void removeTeam(final GameTeam team) {
		this.teams.remove(team);
		this.maxPlayers -= team.getSize();
	}

	public GameTeam getTeam(final String name) {
		return this.teams.stream().filter(team -> team.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public void join(final GamePlayer gamePlayer) {
		if (isFull()) {
			gamePlayer.sendMessage(Messages.ARENA_FULL.toString());
			return;
		}
		this.players.add(gamePlayer);
		gameManager.getPlayerManager().setPlayerForWaiting(gamePlayer, this);
	}

	public void joinAsSpectator(final GamePlayer gamePlayer) {
		gamePlayer.setSpectator();
		this.players.add(gamePlayer);
	}

	public void leave(final GamePlayer gamePlayer) {
		this.players.remove(gamePlayer);
		gameManager.getPlayerManager().setPlayerForLobby(gamePlayer);
	}

	public void leaveAll() {
		this.players.forEach(gamePlayer -> gameManager.getPlayerManager().setPlayerForLobby(gamePlayer));
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
		this.startingTask.runTaskTimer(MinigameTemplate.getInstance(), 0, 20);
	}

	public void stopStarting() {
		this.arenaCheckTask.cancel();
	}

	public boolean canEnd() {
		return this.players.size() <= 1 && this.arenaState == ArenaState.INGAME;
	}

	public void end() {
		new BukkitRunnable() {
			@Override
			public void run() {
				setArenaState(ArenaState.RESTARTING);
				Bukkit.getServer().shutdown();
			}
		}.runTaskLater(MinigameTemplate.getInstance(), 20 * 10);
	}

	public void setArenaState(final ArenaState arenaState) {
		if (this.arenaState == arenaState) return;

		this.arenaState = arenaState;

		switch (arenaState) {
			case WAITING:
				break;
			case STARTING:
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.ARENA_STARTING.toString()));
				break;
			case INGAME:
				preparePlayers();
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.ARENA_STARTED.toString()));
				break;
			case ENDING:
				setWinner();
				Utils.broadcast(PAPI.setPlaceholders(null, "%winner% is WINNER!".replace("%winner%", getWinner())));
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.ARENA_ENDED.toString()));
				end();
				break;
			case RESTARTING:
				leaveAll();
				reset();
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

	public void setEnabled(final boolean enabled) {
		if (this.enabled == enabled) return;
		this.enabled = enabled;
		final File file = new File(pluginFile, this.id + ".yml");
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set(this.id + ".enabled", enabled);
		saveToFile();

		if (enabled)
			this.arenaCheckTask.runTaskTimer(MinigameTemplate.getInstance(), 0L, 20L);
		else {
			if (Bukkit.getScheduler().isCurrentlyRunning(this.arenaCheckTask.getTaskId())) this.arenaCheckTask.cancel();
			if (Bukkit.getScheduler().isCurrentlyRunning(this.startingTask.getTaskId())) this.startingTask.cancel();
		}
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

	private void preparePlayers() {
		teleportTeams();
		this.players.forEach(gamePlayer -> {
			gameManager.getPlayerManager().setPlayerForGame(gamePlayer);
		});
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

	public Set<GameTeam> getAliveTeams() {
		return this.teams.stream().filter(GameTeam::isAlive).collect(Collectors.toSet());
	}

	public void teleportTeams() {
		this.teams.forEach(team -> team.getMembers().forEach(gamePlayer -> gamePlayer.teleport(team.getSpawnLocation())));
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

	public GameTeam getWinnerTeam() {
		return this.teams.stream().filter(GameTeam::isWinner).findFirst().orElse(null);
	}

	public GamePlayer getWinnerPlayer() {
		return this.players.stream().filter(GamePlayer::isWinner).filter(GamePlayer::isOnline).findFirst().orElse(null);
	}

	public void setWinner() {
		final GameTeam winner = this.teams.stream().filter(GameTeam::isAlive).findFirst().orElse(null);
		if (winner == null) return;
		winner.setWinner();
	}

	public String getWinner() {
		if (getWinnerPlayer() == null || getWinnerTeam() == null) return "None";
		if (getWinnerPlayer().getTeam().getSize() == 1) return getWinnerPlayer().getName();
		return getWinnerTeam().getDisplayName();
	}

	public void broadcast(final String message) {
		getOnlinePlayers().forEach(player -> player.sendMessage(message));
	}

	public List<String> getLore() {
		final List<String> lore = new ArrayList<>();
		lore.add(" ");
		lore.add(TextUtils.color("&fPlayers: &e" + getPlayers().size() + "/" + getMaxPlayers()));
		lore.add(TextUtils.color("&fState: &e" + getArenaState().toString()));
		lore.add(" ");
		lore.add(TextUtils.color("&fTeams: &e" + getTeams().get(0).getSize() + "x" + getTeams().size()));
		lore.add(" ");
		lore.add(TextUtils.color("&6Click To Join!"));
		return lore;
	}

	public int getGameTime() {
		switch (this.arenaState) {
			case WAITING:
				return 0;
			case STARTING:
				return this.startingTask.getSeconds();
			case INGAME:
				return 0;
			case ENDING:
				return 0;
			case RESTARTING:
				return 0;
		}
		return 0;
	}

	public void delete() {
		final File file = new File(pluginFile, this.id + ".yml");
		file.delete();
	}

	public void saveToFile() {
		final File file = new File(pluginFile, this.id + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		config.set(this.id + ".name", this.name);
		config.set(this.id + ".enabled", this.enabled);

		config.set(this.id + ".min-players", this.minPlayers);
		config.set(this.id + ".max-players", this.maxPlayers);

		config.set(this.id + ".center-location.world", this.centerLocation.getWorld().getName());
		config.set(this.id + ".center-location.x", this.centerLocation.getX());
		config.set(this.id + ".center-location.y", this.centerLocation.getY());
		config.set(this.id + ".center-location.z", this.centerLocation.getZ());
		config.set(this.id + ".center-location.yaw", this.centerLocation.getYaw());
		config.set(this.id + ".center-location.pitch", this.centerLocation.getPitch());

		config.set(this.id + ".waiting-location.world", this.waitingLocation.getWorld().getName());
		config.set(this.id + ".waiting-location.x", this.waitingLocation.getX());
		config.set(this.id + ".waiting-location.y", this.waitingLocation.getY());
		config.set(this.id + ".waiting-location.z", this.waitingLocation.getZ());
		config.set(this.id + ".waiting-location.yaw", this.waitingLocation.getYaw());
		config.set(this.id + ".waiting-location.pitch", this.waitingLocation.getPitch());

		config.set(this.id + ".ending-location.world", this.endingLocation.getWorld().getName());
		config.set(this.id + ".ending-location.x", this.endingLocation.getX());
		config.set(this.id + ".ending-location.y", this.endingLocation.getY());
		config.set(this.id + ".ending-location.z", this.endingLocation.getZ());
		config.set(this.id + ".ending-location.yaw", this.endingLocation.getYaw());
		config.set(this.id + ".ending-location.pitch", this.endingLocation.getPitch());

		final ConfigurationSection teamsSection = config.createSection(this.id + ".teams");
		for (final GameTeam team : this.teams) {
			final String teamKey = team.getName();
			teamsSection.set(teamKey + ".size", team.getSize());
			teamsSection.set(teamKey + ".spawn-location.x", team.getSpawnLocation().getX());
			teamsSection.set(teamKey + ".spawn-location.y", team.getSpawnLocation().getY());
			teamsSection.set(teamKey + ".spawn-location.z", team.getSpawnLocation().getZ());
			teamsSection.set(teamKey + ".spawn-location.yaw", team.getSpawnLocation().getYaw());
			teamsSection.set(teamKey + ".spawn-location.pitch", team.getSpawnLocation().getPitch());
		}

		try {
			config.save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Arena && ((Arena) obj).getId().equals(this.id);
	}

	@Override
	public String toString() {
		return "Arena(id=" + this.id + ", name=" + this.name + ", enabled=" + this.enabled + ", maxPlayers=" + this.maxPlayers + ", minPlayers=" + this.minPlayers + ")";
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	public static Arena loadFromFile(final String id) {
		final File file = new File(pluginFile, id + ".yml");
		if (!file.exists()) return null;

		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		final ConfigurationSection section = config.getConfigurationSection(id);

		if (section == null) {
			Bukkit.getLogger().info("No arena found in the config.");
			return null;
		}

		final String name = section.getString("name");
		final boolean enabled = section.getBoolean("enabled");
		final int maxPlayers = section.getInt("max-players");
		final int minPlayers = section.getInt("min-players");
		final Location centerLocation = getLocationFromConfig(section, "center");
		final Location waitingLocation = getLocationFromConfig(section, "waiting-location");
		final Location endingLocation = getLocationFromConfig(section, "ending-location");

		final List<GameTeam> teams = loadArenaTeams(id);

		return new Arena(id, name, enabled, centerLocation, waitingLocation, endingLocation, minPlayers, maxPlayers, teams);
	}

	private static List<GameTeam> loadArenaTeams(final String id) {
		final File file = new File(pluginFile, id + ".yml");
		if (!file.exists()) return null;

		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		final List<GameTeam> teams = new ArrayList<>();

		final String world = config.getString(id + "center.world", "world");
		final ConfigurationSection section = config.getConfigurationSection(id + ".teams");
		if (section == null) {
			Bukkit.getLogger().info("No teams found in the config.");
			return teams;
		}
		for (final String teamsKey : section.getKeys(false)) {
			final int size = section.getInt(teamsKey + ".size");
			final double x = section.getDouble(teamsKey + ".spawn-location.x");
			final double y = section.getDouble(teamsKey + ".spawn-location.y");
			final double z = section.getDouble(teamsKey + ".spawn-location.z");
			final float yaw = (float) section.getDouble(teamsKey + ".spawn-location.yaw");
			final float pitch = (float) section.getDouble(teamsKey + ".spawn-location.pitch");

			final Location spawnLocation = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
			final GameTeam team = new GameTeam(teamsKey, size, spawnLocation);
			teams.add(team);
			Bukkit.getLogger().info("Loaded team: " + team.toString());
		}
		return teams;
	}

	private static Location getLocationFromConfig(final ConfigurationSection section, final String path) {
		final String world = section.getString(path + ".world", "world");
		final double x = section.getDouble(path + ".x");
		final double y = section.getDouble(path + ".y");
		final double z = section.getDouble(path + ".z");
		final float yaw = (float) section.getDouble(path + ".yaw");
		final float pitch = (float) section.getDouble(path + ".pitch");
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	public void reset() {
		this.players.clear();
		this.arenaState = ArenaState.WAITING;
	}
}
