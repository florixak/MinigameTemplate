package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.tasks.ScoreboardUpdateTask;
import me.florixak.minigametemplate.tasks.TablistUpdateTask;

public class TasksManager {

	private final GameManager gameManager;

	private ScoreboardUpdateTask scoreboardUpdateTask;
	private TablistUpdateTask tablistUpdateTask;

	public TasksManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		startTasks();
	}

	public void startTasks() {
		if (this.scoreboardUpdateTask != null) this.scoreboardUpdateTask.cancel();
		this.scoreboardUpdateTask = new ScoreboardUpdateTask(this.gameManager);
		this.scoreboardUpdateTask.runTaskTimer(this.gameManager.getPlugin(), 0, 20);

		if (this.tablistUpdateTask != null) this.tablistUpdateTask.cancel();
		this.tablistUpdateTask = new TablistUpdateTask(this.gameManager);
		this.tablistUpdateTask.runTaskTimer(this.gameManager.getPlugin(), 0, 20);
	}

}
