package me.florixak.minigametemplate.game.arena;

import lombok.Getter;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

	private final GameManager gameManager;
	private final FileConfiguration arenaConfig;
	@Getter
	private List<Arena> arenas;

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
			final int maxPlayers = section.getInt(key + ".max-players");
			final boolean enabled = section.getBoolean(key + ".enabled");
			final int minPlayers = section.getInt(key + ".min-players");

			final List<Location> spawnLocations = new ArrayList<>();
			for (final String spawnLoc : section.getConfigurationSection(key + ".spawn-locations").getKeys(false)) {
				final String world = this.arenaConfig.getString(key + ".spawn-locations." + spawnLoc + ".world");
				final double x = this.arenaConfig.getDouble(key + ".spawn-locations." + spawnLoc + ".x");
				final double y = this.arenaConfig.getDouble(key + ".spawn-locations." + spawnLoc + ".y");
				final double z = this.arenaConfig.getDouble(key + ".spawn-locations." + spawnLoc + ".z");
				final float yaw = (float) this.arenaConfig.getDouble(key + ".spawn-locations." + spawnLoc + ".yaw");
				final float pitch = (float) this.arenaConfig.getDouble(key + ".spawn-locations." + spawnLoc + ".pitch");

				final Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
				spawnLocations.add(location);
			}

			Location centerLocation = null;
			if (section.contains(key + ".center-location")) {
				final String world = this.arenaConfig.getString(key + ".center-location.world");
				final double x = this.arenaConfig.getDouble(key + ".center-location.x");
				final double y = this.arenaConfig.getDouble(key + ".center-location.y");
				final double z = this.arenaConfig.getDouble(key + ".center-location.z");
				final float yaw = (float) this.arenaConfig.getDouble(key + ".center-location.yaw");
				final float pitch = (float) this.arenaConfig.getDouble(key + ".center-location.pitch");

				centerLocation = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
			}

			final Arena arena = new Arena(id, name, maxPlayers, enabled, minPlayers, spawnLocations, centerLocation);
			this.arenas.add(arena);
			Bukkit.getLogger().info("Loaded arena: " + arena.toString());
		}
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

	public void joinArena(final GamePlayer player, final Arena arena) {
		arena.join(player);
	}

	public void leaveArena(final GamePlayer player, final Arena arena) {
		arena.leave(player);
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
		for (final Arena arena : this.arenas) {
			if (arena.containsPlayer(player)) {
				return arena;
			}
		}
		return null;
	}

	public Arena getPlayerArena(final GamePlayer player) {
		for (final Arena arena : this.arenas) {
			if (arena.containsPlayer(player)) {
				return arena;
			}
		}
		return null;
	}

	public boolean isPlayerInArena(final GamePlayer player) {
		return getPlayerArena(player) != null;
	}

	private List<GamePlayer> findTopKillers(final List<GamePlayer> players) {
		players.sort((gamePlayer1, gamePlayer2) -> Integer.compare(gamePlayer2.getKills(), gamePlayer1.getKills()));
		return players;
	}

	public List<GamePlayer> getTopKillers(final Arena arena) {
		return findTopKillers(arena.getPlayers());
	}
}
