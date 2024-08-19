package me.florixak.minigametemplate.game.quests;

import lombok.Getter;
import me.florixak.minigametemplate.game.player.GamePlayer;

@Getter
public class QuestReward {

	private final double money;
	private final double exp;

	public QuestReward(final double money, final double exp) {
		this.money = money;
		this.exp = exp;
	}

	public void giveReward(final GamePlayer gamePlayer) {
		if (this.money > 0) {
			gamePlayer.getPlayerData().depositMoney(this.money);
		}
		if (this.exp > 0) {
			gamePlayer.getPlayerData().addExp(this.exp);
		}
	}
}
