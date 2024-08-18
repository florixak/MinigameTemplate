package me.florixak.minigametemplate.tasks;

import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCheckTask extends BukkitRunnable {

	private static GameManager gameManager;

	public GameCheckTask(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void run() {

	}
}
