package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.tasks.ScoreboardUpdateTask;
import me.florixak.minigametemplate.tasks.TablistUpdateTask;
import org.bukkit.Bukkit;

public class TasksManager {

	private final GameManager gameManager;

	private ScoreboardUpdateTask scoreboardUpdateTask;
	private TablistUpdateTask tablistUpdateTask;

	private long scoreboardUpdateInterval;
	private long tablistUpdateInterval;

	public TasksManager(final GameManager gameManager) {
		this.gameManager = gameManager;

		this.scoreboardUpdateInterval = gameManager.getConfigManager().getFile(ConfigType.SCOREBOARD).getConfig().getLong("scoreboard.update-interval", 20);
		if (this.scoreboardUpdateInterval < 1) {
			this.scoreboardUpdateInterval = 20;
			Bukkit.getLogger().info("Scoreboard update interval is invalid, using default value of 20 ticks.");
		}

		this.tablistUpdateInterval = GameValues.TABLIST.UPDATE_INTERVAL;
		if (this.tablistUpdateInterval < 1) {
			this.tablistUpdateInterval = 20;
			Bukkit.getLogger().info("Tablist update interval is invalid, using default value of 20 ticks.");
		}

		startTasks();
	}

	public void startTasks() {
		if (this.scoreboardUpdateTask != null) this.scoreboardUpdateTask.cancel();
		this.scoreboardUpdateTask = new ScoreboardUpdateTask(this.gameManager);
		this.scoreboardUpdateTask.runTaskTimer(this.gameManager.getPlugin(), 0, this.scoreboardUpdateInterval);
		
		if (this.tablistUpdateTask != null) this.tablistUpdateTask.cancel();
		this.tablistUpdateTask = new TablistUpdateTask(this.gameManager);
		this.tablistUpdateTask.runTaskTimer(this.gameManager.getPlugin(), 0, this.tablistUpdateInterval);
	}

}
