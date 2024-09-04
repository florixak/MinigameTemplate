package me.florixak.minigametemplate.listeners.events;

import lombok.Getter;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ArenaDeathEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final GamePlayer player;
	private final GamePlayer killer;
	private final Arena arena;

	private boolean cancelled;

	public ArenaDeathEvent(final GamePlayer player, final GamePlayer killer, final Arena arena) {
		this.player = player;
		this.killer = killer;
		this.arena = arena;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(final boolean b) {
		this.cancelled = b;
	}
}
