package me.florixak.minigametemplate.tasks;

import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingTask extends BukkitRunnable {

	private static GameManager gameManager;
	private int countdown = 10;

	public EndingTask(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void run() {
		if (this.countdown == 0) {
			Bukkit.getServer().shutdown();
			cancel();
			return;
		}
		this.countdown--;
	}
}
