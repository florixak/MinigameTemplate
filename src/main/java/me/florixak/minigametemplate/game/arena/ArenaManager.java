package me.florixak.minigametemplate.game.arena;

import lombok.Getter;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaManager {

	private final GameManager gameManager;
	private final FileConfiguration arenaConfig;
	@Getter
	private final List<Arena> arenas = new ArrayList<>();

	public ArenaManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.arenaConfig = gameManager.getConfigManager().getFile(ConfigType.ARENAS).getConfig();

		loadArenas();
	}

	public void loadArenas() {
		final ConfigurationSection section = this.arenaConfig.getConfigurationSection("arenas");
		if (section == null) {
			Bukkit.getLogger().info("No arenas found in the config.");
			return;
		}
		for (final String key : section.getKeys(false)) {
			final int id = Integer.parseInt(key);
			final String name = section.getString(key + ".name");
			final boolean enabled = section.getBoolean(key + ".enabled");
			int maxPlayers = 0;
			final int minPlayers = section.getInt(key + ".min-players");

			Location centerLocation = null;
			if (section.contains(key + ".center-location")) {
				centerLocation = loadArenaCenterLocation(key);
			}

			List<GameTeam> teamsList = new ArrayList<>();
			if (section.contains(key + ".teams")) {
				teamsList = loadArenaTeams(key);
				for (final GameTeam team : teamsList) {
					maxPlayers += team.getSize();
				}
			}

			final Arena arena = new Arena(id, name, enabled, maxPlayers, minPlayers, centerLocation, teamsList);
			this.arenas.add(arena);
			Bukkit.getLogger().info("Loaded arena: " + arena.toString());
			Bukkit.getLogger().info("Arenas: " + this.arenas.toString());
		}
	}

	private Location loadArenaCenterLocation(final String key) {
		final ConfigurationSection section = this.arenaConfig.getConfigurationSection("arenas." + key);
		final ConfigurationSection centerLocationSection = section.getConfigurationSection(key + ".center-location");

		final String world = section.getString("world", "world");
		final double x = centerLocationSection.getDouble("x");
		final double y = centerLocationSection.getDouble("y");
		final double z = centerLocationSection.getDouble("z");
		final float yaw = (float) centerLocationSection.getDouble("yaw");
		final float pitch = (float) centerLocationSection.getDouble("pitch");
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	private List<GameTeam> loadArenaTeams(final String key) {
		final ConfigurationSection section = this.arenaConfig.getConfigurationSection("arenas." + key + ".teams");
		final List<GameTeam> teams = new ArrayList<>();
		if (section == null) {
			Bukkit.getLogger().info("No teams found in the config.");
			return teams;
		}
		for (final String teamsKey : section.getKeys(false)) {
			final int size = section.getInt(teamsKey + ".size");

			final String world = section.getString(teamsKey + ".world", "world");
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

	public void saveArenas() {

	}

	public void createArena() {
	}

	public void deleteArena(final Arena arena) {
		this.arenas.remove(arena);
		this.arenaConfig.set("arenas." + arena.getId(), null);
		this.gameManager.getConfigManager().saveFile(ConfigType.ARENAS);
	}

	public boolean existsArena(final int id) {
		for (final Arena arena : this.arenas) {
			if (arena.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public Arena getArena(final String name) {
		for (final Arena arena : this.arenas) {
			if (arena.getName().equals(name)) {
				return arena;
			}
		}
		return null;
	}

	public Arena getArena(final int id) {
		for (final Arena arena : this.arenas) {
			if (arena.getId() == id) {
				return arena;
			}
		}
		return null;
	}

	public Arena getPlayerArena(final Player player) {
		return getPlayerArena(this.gameManager.getPlayerManager().getGamePlayer(player.getUniqueId()));
	}

	public Arena getPlayerArena(final GamePlayer player) {
		for (final Arena arena : this.arenas) {
			if (arena.isPlayerIn(player)) {
				return arena;
			}
		}
		return null;
	}

	public boolean isPlayerInArena(final GamePlayer player) {
		return getPlayerArena(player) != null;
	}

	public List<Arena> getActiveArenas() {
		return this.arenas.stream().filter(Arena::isEnabled).collect(Collectors.toList());
	}

	public List<Arena> getNotInGameArenas() {
		final List<Arena> notInGameArenas = new ArrayList<>(getWaitingArenas());
		notInGameArenas.addAll(getStartingArenas());
		return notInGameArenas;
	}

	private List<Arena> getWaitingArenas() {
		return getActiveArenas().stream().filter(Arena::isWaiting).collect(Collectors.toList());
	}

	private List<Arena> getStartingArenas() {
		return getActiveArenas().stream().filter(Arena::isStarting).collect(Collectors.toList());
	}

	public List<Arena> getInGameArenas() {
		return getActiveArenas().stream().filter(arena -> !arena.isWaiting()).filter(arena -> !arena.isStarting()).collect(Collectors.toList());
	}

	public List<Arena> getInactiveArenas() {
		return this.arenas.stream().filter(arena -> !arena.isEnabled()).collect(Collectors.toList());
	}

	private List<GamePlayer> findTopKillers(final List<GamePlayer> players) {
		players.sort((gamePlayer1, gamePlayer2) -> Integer.compare(gamePlayer2.getKills(), gamePlayer1.getKills()));
		return players;
	}

	public List<GamePlayer> getTopKillers(final Arena arena) {
		return findTopKillers(arena.getPlayers());
	}
}
