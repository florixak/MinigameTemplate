package me.florixak.minigametemplate.tasks;

import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdateTask extends BukkitRunnable {

	private final GameManager gameManager;

	public ScoreboardUpdateTask(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void run() {
		this.gameManager.getPlayerManager().getOnlinePlayers().forEach(gamePlayer -> {
			this.gameManager.getScoreboardManager().updateScoreboard(gamePlayer.getUuid());
		});
	}
}
