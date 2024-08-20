package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.tasks.BoardsCheckTask;

public class TasksManager {

	private final GameManager gameManager;

	private BoardsCheckTask boardsCheckTask;

	public TasksManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		startTasks();
	}

	public void startTasks() {
		if (this.boardsCheckTask != null) this.boardsCheckTask.cancel();
		this.boardsCheckTask = new BoardsCheckTask(this.gameManager);
		this.boardsCheckTask.runTaskTimer(this.gameManager.getPlugin(), 0, 20);
	}

}
