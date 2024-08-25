package me.florixak.minigametemplate.tasks;

import lombok.Getter;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.utils.TimeUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask extends BukkitRunnable {

	private final Arena arena;
	@Getter
	private int seconds = GameValues.COUNTDOWNS.STARTING;

	public StartingTask(final Arena arena) {
		this.arena = arena;
	}

	@Override
	public void run() {
		if (this.seconds <= 0) {
			this.cancel();
			return;
		}

		if (this.seconds == 30 || this.seconds == 15 || this.seconds == 10 || this.seconds <= 5) {
			this.arena.hotbarMessage("Starting in " + TimeUtils.getFormattedTime(this.seconds));
		}

		this.seconds--;
	}
}
