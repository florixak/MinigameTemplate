package me.florixak.minigametemplate.game.arena;

import lombok.Getter;
import me.florixak.minigametemplate.game.player.GamePlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Arena {

	private final int id;
	private final String name;
	private final boolean enabled;
	private final int maxPlayers;
	private final int minPlayers;
	private final List<Location> spawnLocations;
	private final Location centerLocation;

	private final List<GamePlayer> players = new ArrayList<>();

	public Arena(final int id, final String name, final int maxPlayers, final int minPlayers, final boolean enabled, final List<Location> spawnLocations, final Location centerLocation) {
		this.id = id;
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		this.enabled = enabled;
		this.spawnLocations = spawnLocations;
		this.centerLocation = centerLocation;
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

	public boolean containsPlayer(final GamePlayer player) {
		return this.players.contains(player);
	}

	public boolean isFull() {
		return this.players.size() >= this.maxPlayers;
	}

	public boolean canStart() {
		return this.players.size() >= this.minPlayers;
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
