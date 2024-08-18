package me.florixak.minigametemplate.game.statistics;

import lombok.Getter;

@Getter
public class Leaderboard {

	private final String name;
	private final int value;

	public Leaderboard(final String name, final int value) {
		this.name = name;
		this.value = value;
	}
}
