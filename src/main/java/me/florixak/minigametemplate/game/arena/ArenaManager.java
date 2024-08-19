package me.florixak.minigametemplate.game.arena;

import lombok.Getter;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class ArenaManager {

	private final GameManager gameManager;
	@Getter
	private List<Arena> arenas;

	public ArenaManager(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public void loadArenas() {
		this.arenas.add(new Arena(1, "Arena1", 10, 2, true, null, new Location(Bukkit.getWorld("world"), 0, 0, 0)));
		this.arenas.add(new Arena(2, "Arena2", 10, 2, true, null, new Location(Bukkit.getWorld("world"), 0, 0, 0)));
	}

	public void saveArenas() {

	}

	public void createArena() {

	}

	public void deleteArena() {

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
}
