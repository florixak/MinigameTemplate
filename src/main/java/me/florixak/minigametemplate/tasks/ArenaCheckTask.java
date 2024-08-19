package me.florixak.minigametemplate.tasks;

import me.florixak.minigametemplate.game.arena.Arena;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaCheckTask extends BukkitRunnable {

	private final Arena arena;

	public ArenaCheckTask(final Arena arena) {
		this.arena = arena;
	}

	@Override
	public void run() {
		if (this.arena.canStart()) {
			this.arena.start();
		} else {
			this.arena.stopStarting();
		}

		if (this.arena.canEnd()) {
			this.arena.end();
		}
	}
}
