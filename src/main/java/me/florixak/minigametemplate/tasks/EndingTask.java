package me.florixak.minigametemplate.tasks;

import lombok.Getter;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.arena.ArenaState;
import org.bukkit.scheduler.BukkitRunnable;


public class EndingTask extends BukkitRunnable {

	private final Arena arena;
	@Getter
	private int time = 10;

	public EndingTask(final Arena arena) {
		this.arena = arena;
	}

	@Override
	public void run() {
		if (this.time == 0) {
			cancel();
			this.arena.setArenaState(ArenaState.RESTARTING);
			return;
		}
		this.time--;
	}
}
