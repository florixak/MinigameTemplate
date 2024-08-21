package me.florixak.minigametemplate.listeners.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArenaEndEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();


	@Override
	public boolean isCancelled() {
		return true;
	}

	@Override
	public void setCancelled(final boolean cancel) {

	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return null;
	}

	public static HandlerList getHandlerList() {
		return null;
	}
}
