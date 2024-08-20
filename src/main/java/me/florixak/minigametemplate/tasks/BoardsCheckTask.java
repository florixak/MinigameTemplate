package me.florixak.minigametemplate.tasks;

import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class BoardsCheckTask extends BukkitRunnable {

	private final GameManager gameManager;

	public BoardsCheckTask(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void run() {
		this.gameManager.getPlayerManager().getPlayers().forEach(gamePlayer -> {
			this.gameManager.getScoreboardManager().updateScoreboard(gamePlayer.getUuid());
		});
		this.gameManager.getTablistManager().setPlayerList();
	}
}
