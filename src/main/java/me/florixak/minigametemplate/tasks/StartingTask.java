package me.florixak.minigametemplate.tasks;

import lombok.Getter;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.arena.ArenaState;
import me.florixak.minigametemplate.utils.TimeUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask extends BukkitRunnable {

	private final Arena arena;
	@Getter
	private int time = 10;

	public StartingTask(final Arena arena) {
		this.arena = arena;
	}

	@Override
	public void run() {
		if (!this.arena.canStart()) {
			cancel();
			this.arena.setArenaState(ArenaState.WAITING);
			return;
		}

		if (this.time <= 0) {
			this.cancel();
			this.arena.setArenaState(ArenaState.INGAME);
			return;
		}

		if (this.time == 30 || this.time == 15 || this.time == 10 || this.time <= 5) {
			this.arena.broadcast("Starting in " + TimeUtils.getFormattedTime(this.time));
		}

		this.time--;
	}
}
