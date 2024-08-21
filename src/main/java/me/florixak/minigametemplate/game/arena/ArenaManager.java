package me.florixak.minigametemplate.game.arena;

import lombok.Getter;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaManager {
	private final GameManager gameManager;
	@Getter
	private final List<Arena> arenas = new ArrayList<>();

	public ArenaManager(final GameManager gameManager) {
		this.gameManager = gameManager;

		loadAllArenas();
	}

	public void saveAllArenas() {
		for (final Arena arena : this.arenas) {
			arena.saveToFile();
		}
	}

	public void loadAllArenas() {
		final File arenasFile = new File(this.gameManager.getPlugin().getDataFolder(), "arenas");
		if (!arenasFile.exists()) {
			arenasFile.mkdirs();
		}

		Bukkit.getLogger().info("Loading arenas... " + arenasFile.listFiles().length);

		for (final File file : arenasFile.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".yml")) {
				final String id = file.getName().replace(".yml", "");
				final Arena arena = Arena.loadFromFile(id);
				if (arena != null) {
					this.arenas.add(arena);
					Bukkit.getLogger().info("Loaded arena: " + arena.toString());
				}
			}
		}
	}

	public void createArena(final String id, final String name, final Location centerLocation, final int minPlayers) {
		final Arena arena = new Arena(id, name, centerLocation, minPlayers);
		this.arenas.add(arena);
		Bukkit.getLogger().info("Created arena: " + arena.toString());
	}

	public boolean enableArena(final Player player, final Arena arena) {
		if (arena.isEnabled()) {
			if (player == null) Bukkit.getLogger().info("Arena is already enabled.");
			else player.sendMessage("Arena is already enabled.");
			return false;
		}
		if (arena.getTeams().isEmpty()) {
			if (player == null) Bukkit.getLogger().info("Arena has no teams.");
			else player.sendMessage("Arena has no teams.");
			return false;
		}
		if (arena.getMaxPlayers() == 0) {
			if (player == null) Bukkit.getLogger().info("Arena has no max players.");
			else player.sendMessage("Arena has no max players.");
			return false;
		}
		arena.setEnabled(true);
		return true;
	}

	public void startArena(final Arena arena) {
		if (!arena.isEnabled()) return;
		if (arena.getPlayers().size() < arena.getMinPlayers()) return;
		arena.start();
	}

	public void disableArena(final Arena arena) {
		if (!arena.isEnabled()) return;
		arena.getPlayers().forEach(gamePlayer -> {
			gamePlayer.sendMessage("Arena has been disabled.");
			arena.leave(gamePlayer);
		});
		arena.setEnabled(false);
	}

	public void deleteArena(final Arena arena) {
		this.arenas.remove(arena);
		arena.delete();
	}

	public boolean existsArena(final String id) {
		for (final Arena arena : this.arenas) {
			if (arena.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public Arena getArena(final String arenaIdOrName) {
		for (final Arena arena : this.arenas) {
			if (arena.getId().equalsIgnoreCase(arenaIdOrName) || arena.getName().equalsIgnoreCase(arenaIdOrName)) {
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

	public void onDisable() {
		saveAllArenas();
		this.arenas.clear();
	}
}
