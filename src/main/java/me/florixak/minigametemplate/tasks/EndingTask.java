package me.florixak.minigametemplate.tasks;

import lombok.Getter;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.arena.ArenaState;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;


public class EndingTask extends BukkitRunnable {

	private final Arena arena;
	@Getter
	private int countdown = GameValues.COUNTDOWNS.ENDING;

	public EndingTask(final Arena arena) {
		this.arena = arena;
	}

	@Override
	public void run() {
		if (this.countdown == 0) {
			cancel();
			this.arena.setArenaState(ArenaState.RESTARTING);
			Bukkit.getServer().shutdown();
			return;
		}
		this.countdown--;
	}
}
