package me.florixak.minigametemplate.game;

public enum GameState {

	LOBBY,
	STARTING,
	INGAME,
	ENDING,
	RESTARTING;

	@Override
	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
}
