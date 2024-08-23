package me.florixak.minigametemplate.tasks;

import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class TablistUpdateTask extends BukkitRunnable {

	private final GameManager gameManager;

	public TablistUpdateTask(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void run() {
		this.gameManager.getPlayerManager().getOnlinePlayers().forEach(gamePlayer -> {
			this.gameManager.getTablistManager().setPlayerList(gamePlayer);
		});
	}


}
