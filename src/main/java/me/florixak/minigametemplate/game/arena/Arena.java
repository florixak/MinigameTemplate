package me.florixak.minigametemplate.game.arena;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import lombok.Setter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.player.PlayerArenaData;
import me.florixak.minigametemplate.game.player.PlayerState;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.tasks.ArenaCheckTask;
import me.florixak.minigametemplate.tasks.EndingTask;
import me.florixak.minigametemplate.tasks.InGameTask;
import me.florixak.minigametemplate.tasks.StartingTask;
import me.florixak.minigametemplate.utils.PAPIUtils;
import me.florixak.minigametemplate.utils.RandomUtils;
import me.florixak.minigametemplate.utils.Utils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Arena {

	protected final GameManager gameManager = GameManager.getInstance();
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
	protected boolean solo = false;

	protected final List<GamePlayer> players = new ArrayList<>();
	protected final Map<GamePlayer, PlayerArenaData> playerArenaData = new HashMap<>();
	protected final List<GamePlayer> spectators = new ArrayList<>();

	protected ArenaCheckTask arenaCheckTask;
	protected StartingTask startingTask;
	protected InGameTask inGameTask;
	protected EndingTask endingTask;

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

		if (checkIfAllTeamSizeOne()) {
			this.solo = true;
		}

		if (enabled) {
			this.arenaCheckTask = new ArenaCheckTask(this);
			this.arenaCheckTask.runTaskTimer(MinigameTemplate.getInstance(), 0L, 20L);
		}

	}


	/* Arena State */
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
				this.inGameTask = new InGameTask(this);
				this.inGameTask.runTaskTimer(MinigameTemplate.getInstance(), 0, 20);
				broadcast(PAPI.setPlaceholders(null, Messages.ARENA_STARTED.toString()));
				break;
			case ENDING:
				setWinner();
				broadcast(PAPIUtils.setArenaPlaceholders(null, this, "%arena_winner% is the WINNER!"));
				broadcast(PAPI.setPlaceholders(null, Messages.ARENA_ENDED.toString()));
				saveAndShowStatistics();
				end();
				break;
			case RESTARTING:
				kickAll();
//				disable();
				// kick all players, reset arena, etc.
				break;
		}
	}

	public boolean isWaiting() {
		return this.arenaState.equals(ArenaState.WAITING);
	}

	public boolean isStarting() {
		return this.arenaState.equals(ArenaState.STARTING);
	}

	public boolean isPlaying() {
		return this.arenaState.equals(ArenaState.INGAME);
	}

	public boolean isEnding() {
		return this.arenaState.equals(ArenaState.ENDING);
	}

	public void setEnabled(final boolean enabled) {
		if (this.enabled == enabled) return;
		this.enabled = enabled;
		final File file = new File(pluginFile, this.id + ".yml");
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set(this.id + ".enabled", enabled);
		saveToFile();

		if (enabled) {
			this.arenaCheckTask = new ArenaCheckTask(this);
			this.arenaCheckTask.runTaskTimer(MinigameTemplate.getInstance(), 0L, 20L);
		} else {
			if (this.arenaCheckTask != null) this.arenaCheckTask.cancel();
			if (this.startingTask != null) this.startingTask.cancel();
		}
	}

	public void disable() {
		kickAll();
		setEnabled(false);
		this.teams.clear();
		this.spectators.clear();
		this.playerArenaData.clear();
		this.players.clear();
	}

	public boolean isFull() {
		return this.players.size() >= this.maxPlayers;
	}

	public boolean canStart() {
//		return this.players.size() >= this.minPlayers;
		return this.players.size() == 1;
	}

	public void start() {
		setArenaState(ArenaState.STARTING);
		this.startingTask = new StartingTask(this);
		this.startingTask.runTaskTimer(MinigameTemplate.getInstance(), 0, 20);
	}

	public void stopStarting() {
		this.arenaCheckTask.cancel();
	}

	public boolean canEnd() {
		return this.players.size() <= 0 && this.arenaState == ArenaState.INGAME;
	}

	public void end() {
		setArenaState(ArenaState.ENDING);
		this.endingTask = new EndingTask(this);
		this.endingTask.runTaskTimer(this.gameManager.getPlugin(), 0, 20);
	}


	/* Players */
	public boolean isPlayerIn(final GamePlayer player) {
		return this.players.contains(player);
	}

	public boolean isPlayerIn(final UUID uuid) {
		return this.players.contains(this.gameManager.getPlayerManager().getGamePlayer(uuid));
	}

	public PlayerArenaData getPlayerArenaData(final GamePlayer gamePlayer) {
		return this.playerArenaData.get(gamePlayer);
	}

	public void join(final GamePlayer gamePlayer) {
		if (isFull()) {
			gamePlayer.sendMessage(Messages.ARENA_FULL.toString());
			return;
		}
		if (isPlaying()) {
			joinAsSpectator(gamePlayer);
			return;
		}
		this.players.add(gamePlayer);
//		if (this.solo) {
//			joinRandomTeam(gamePlayer);
//		}
		this.playerArenaData.put(gamePlayer, new PlayerArenaData(gamePlayer));
		setPlayerForWaiting(gamePlayer);
	}

	public void joinAsSpectator(final GamePlayer gamePlayer) {
		setSpectator(gamePlayer);
		this.spectators.add(gamePlayer);
	}

	public void setSpectator(final GamePlayer gamePlayer) {
		this.spectators.add(gamePlayer);

		if (!gamePlayer.getArenaData().isDead())
			gamePlayer.getArenaData().setState(PlayerState.SPECTATOR);

		gamePlayer.setGameMode(GameMode.SPECTATOR);
//		teleport(new Location(Bukkit.getWorld(GameValues.WORLD_NAME), 0, 100, 0));
	}

	public void die(final GamePlayer gamePlayer, final boolean leave) {
		gamePlayer.getArenaData().setState(PlayerState.DEAD);
		if (leave) return;
		final Player player = gamePlayer.getPlayer();
		player.spigot().respawn();
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setExhaustion(0);
		player.setFireTicks(0);
		gamePlayer.clearPotions();
		gamePlayer.clearInventory();

		setSpectator(gamePlayer);
		this.gameManager.getSoundManager().playDeathSound(player);
	}

	public void leave(final GamePlayer gamePlayer) {
		if (isPlaying()) gamePlayer.getArenaData().setState(PlayerState.DEAD);
		if (isWaiting() || isStarting()) {
			this.playerArenaData.remove(gamePlayer);
		}
		this.spectators.remove(gamePlayer);
		this.players.remove(gamePlayer);
		this.gameManager.getPlayerManager().setPlayerForLobby(gamePlayer);
	}

	public void kickAll() {
		final List<GamePlayer> allPlayers = new ArrayList<>(this.players);
		allPlayers.addAll(this.spectators);
		allPlayers.forEach(this::leave);
		this.players.clear();
		this.spectators.clear();
	}

	private void preparePlayers() {
		this.players.forEach(gamePlayer -> {
			setPlayerForGame(gamePlayer);
		});
		teleportTeams();
	}

	public Set<GamePlayer> getOnlinePlayers() {
		return this.players.stream()
				.filter(GamePlayer::isOnline)
				.collect(Collectors.toSet());
	}

	public Set<GamePlayer> getAlivePlayers() {
		return getOnlinePlayers().stream()
				.filter(player -> player.getArenaData().getState().equals(PlayerState.ALIVE))
				.collect(Collectors.toSet());
	}

	public Set<GamePlayer> getDeadPlayers() {
		return this.players.stream().filter(player -> player.getArenaData().getState().equals(PlayerState.DEAD)).collect(Collectors.toSet());
	}

	public Set<GamePlayer> getSpectatorPlayers() {
		return getOnlinePlayers().stream().filter(player -> player.getArenaData().getState().equals(PlayerState.SPECTATOR)).collect(Collectors.toSet());
	}

	public GamePlayer getRandomPlayer() {
		return RandomUtils.randomOnlinePlayer(getOnlinePlayers().stream().collect(Collectors.toList()));
	}

	public GamePlayer getPlayerWithoutPerm(final String perm) {
		final List<GamePlayer> onlineListWithoutPerm = getPlayers().stream().filter(GamePlayer::isOnline).filter(gamePlayer -> !gamePlayer.hasPermission(perm)).collect(Collectors.toList());
		return RandomUtils.randomOnlinePlayer(onlineListWithoutPerm);
	}

	public void setPlayerForWaiting(final GamePlayer gamePlayer) {
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
		gamePlayer.getArenaData().setState(PlayerState.WAITING);
		gamePlayer.setGameMode(GameMode.ADVENTURE);
		gamePlayer.teleport(arena.getWaitingLocation());
		this.gameManager.getGuiManager().giveItems(gamePlayer);
	}

	public void setPlayerForGame(final GamePlayer gamePlayer) {
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
		gamePlayer.getArenaData().setState(PlayerState.ALIVE);

		gamePlayer.setGameMode(GameMode.SURVIVAL);
		gamePlayer.getPlayer().setHealth(gamePlayer.getPlayer().getMaxHealth());
		gamePlayer.getPlayer().setFoodLevel(20);
		gamePlayer.getPlayer().setExhaustion(0);

		gamePlayer.clearPotions();
		gamePlayer.clearInventory();

		if (!arena.getPlayerArenaData(gamePlayer).hasTeam()) {
			arena.joinRandomTeam(gamePlayer);
		}

		if (arena.getPlayerArenaData(gamePlayer).hasKit()) {
			arena.getPlayerArenaData(gamePlayer).getKit().giveKit(gamePlayer);
		}
	}

	/* Teams */
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
			setSpectator(gamePlayer);
			return;
		}
		team.addMember(gamePlayer);
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


	/* Winner */
	public GameTeam getWinnerTeam() {
		return this.teams.stream().filter(GameTeam::isWinner).findFirst().orElse(null);
	}

	public PlayerArenaData getWinnerPlayer() {
		return this.playerArenaData.values().stream().filter(data -> data.isWinner()).findFirst().orElse(null);
	}

	public void setWinner() {
		final GameTeam winner = this.teams.stream().filter(GameTeam::isAlive).findFirst().orElse(null);
		if (winner == null) return;
		winner.setWinner();
	}

	public String getWinner() {
		if (getWinnerPlayer() == null || getWinnerTeam() == null) return "None";
		if (getWinnerPlayer().getTeam().getSize() == 1) return getWinnerPlayer().getGamePlayer().getName();
		return getWinnerTeam().getDisplayName();
	}


	/* Arena Methods */
	public void broadcast(final String message) {
		getOnlinePlayers().forEach(player -> player.sendMessage(message));
	}

	public void sendHotbarMessage(final String message) {
		getOnlinePlayers().forEach(player -> player.sendHotBarMessage(message));
	}

	public void sendTitle(final String title, final String subtitle) {
		getOnlinePlayers().forEach(player -> player.sendTitle(title, subtitle, 20, 40, 20));
	}

	public void sendMessage(final String message) {
		getOnlinePlayers().forEach(player -> player.sendMessage(message));
	}

	public List<String> getLore() {
		final List<String> lore = new ArrayList<>();
		for (String loreText : Messages.ARENA_LORE.toList()) {
			loreText = loreText
					.replace("%arena_name%", TextUtils.color(this.name))
					.replace("%arena_id%", this.id)
					.replace("%arena_online%", String.valueOf(this.players.size()))
					.replace("%arena_min%", String.valueOf(this.minPlayers))
					.replace("%arena_max%", String.valueOf(this.maxPlayers))
					.replace("%arena_alive%", String.valueOf(getAlivePlayers().size()))
					.replace("%arena_teams_alive%", String.valueOf(getAliveTeams().size()))
					.replace("%arena_state%", this.arenaState.toString())
					.replace("%arena_seconds%", String.valueOf(getArenaTime()))
					.replace("%arena_mode%", getArenaMode());
			lore.add(loreText);
		}

		if (!isPlaying())
			lore.add(Messages.ARENA_LORE_JOIN.toString());
		else if (isEnding())
			lore.add(Messages.ARENA_LORE_RESTARTING.toString());
		else
			lore.add(Messages.ARENA_LORE_IN_GAME.toString());
		return PAPI.setPlaceholders(null, lore);
	}

	public String getArenaMode() {
		if (this.solo) return "Solo";
		return getTeams().size() + "x" + getTeams().get(0).getSize();
	}

	public int getArenaTime() {
		switch (this.arenaState) {
			case WAITING:
				return 0;
			case STARTING:
				return this.startingTask.getTime();
			case INGAME:
				return this.inGameTask.getTime();
			case RESTARTING:
				return 0;
		}
		return 0;
	}

	public void delete() {
		final File file = new File(pluginFile, this.id + ".yml");
		file.delete();
	}

	private void saveAndShowStatistics() {
		for (final PlayerArenaData arenaData : this.playerArenaData.values()) {
			if (getArenaMode().equalsIgnoreCase("Solo")) {
				arenaData.saveSoloStatistics();
			} else {
				arenaData.saveTeamsStatistics();
			}
			if (isPlayerIn(arenaData.getGamePlayer())) {
				arenaData.showStatistics();
			} else {
				Bukkit.getLogger().info("Arena data saved for " + arenaData.getUuid() + ", " + arenaData.getName() + ".");
			}
		}
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

	private boolean checkIfAllTeamSizeOne() {
		for (final GameTeam team : this.teams) {
			if (team.getSize() != 1) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Arena && ((Arena) obj).getId().equals(this.id) && ((Arena) obj).getName().equals(this.name);
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

		if (!GameManager.getInstance().getWorldManager().worldExists(centerLocation.getWorld().getName())) {
			GameManager.getInstance().getWorldManager().loadWorldIfFileExists(centerLocation.getWorld().getName());
		}


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
		if (!GameManager.getInstance().getWorldManager().worldExists(world)) {
			Bukkit.getLogger().info("World " + world + " does not exist, loading...");
			GameManager.getInstance().getWorldManager().loadWorldIfFileExists(world);
		} else {
			Bukkit.getLogger().info("World " + world + " exists.");
		}
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

}
