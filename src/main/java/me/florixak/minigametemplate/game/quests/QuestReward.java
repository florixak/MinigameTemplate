package me.florixak.minigametemplate.game.quests;

import me.florixak.minigametemplate.game.player.GamePlayer;

public class QuestReward {

	private final double money;
	private final double uhcExp;

	public QuestReward(final double money, final double uhcExp) {
		this.money = money;
		this.uhcExp = uhcExp;
	}

	public double getMoney() {
		return this.money;
	}

	public double getUhcExp() {
		return this.uhcExp;
	}

	public void giveReward(final GamePlayer gamePlayer) {
		if (this.money > 0) {
			gamePlayer.getData().depositMoney(this.money);
		}
		if (this.uhcExp > 0) {
			gamePlayer.getData().addExp(this.uhcExp);
		}
	}
}
