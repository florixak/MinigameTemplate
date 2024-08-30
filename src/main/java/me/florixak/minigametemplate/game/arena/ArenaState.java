package me.florixak.minigametemplate.game.arena;

public enum ArenaState {

	WAITING,
	STARTING,
	INGAME,
	ENDING,
	RESTARTING,
	DISABLED;

	@Override
	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
}
