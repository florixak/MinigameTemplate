package me.florixak.minigametemplate.game.statistics;

public class Leaderboard {

	private final String name;
	private final int value;

	public Leaderboard(final String name, final int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public int getValue() {
		return this.value;
	}
}
