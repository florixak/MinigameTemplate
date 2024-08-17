package me.florixak.minigametemplate.game.perks;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.MathUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;

import java.util.List;

public class PerkBonus {

	private final List<Double> coins;
	private final List<Double> uhcExp;
	private final List<Integer> exp;

	public PerkBonus(final List<Double> coins, final List<Double> uhcExp, final List<Integer> exp) {
		this.coins = coins;
		this.uhcExp = uhcExp;
		this.exp = exp;
	}

	public List<Double> getCoins() {
		return this.coins;
	}

	public String getFormattedCoins() {
		if (getCoins().isEmpty()) return "0";
		if (getCoins().size() == 1) return String.valueOf(getCoins().get(0));
		return getCoins().get(0) + "-" + getCoins().get(1);
	}

	public List<Double> getUhcExp() {
		return this.uhcExp;
	}

	public String getFormattedUhcExp() {
		if (getUhcExp().isEmpty()) return "0";
		if (getUhcExp().size() == 1) return String.valueOf(getUhcExp().get(0));
		return getUhcExp().get(0) + "-" + getUhcExp().get(1);
	}

	public List<Integer> getExp() {
		return this.exp;
	}

	public String getFormattedExp() {
		if (getExp().isEmpty()) return "0";
		if (getExp().size() == 1) return String.valueOf(getExp().get(0));
		return getExp().get(0) + "-" + getExp().get(1);
	}

	public void giveBonus(final GamePlayer gamePlayer) {
		final double randomCoins = getCoins().get(0) + (getCoins().get(1) - getCoins().get(0)) * MathUtils.getRandom().nextDouble();
		final double randomUHCExp = getUhcExp().get(0) + (getUhcExp().get(1) - getUhcExp().get(0)) * MathUtils.getRandom().nextDouble();
		final int randomExp = MathUtils.randomInteger(getExp().get(0), getExp().get(1));

		gamePlayer.getData().depositMoney(randomCoins);
		gamePlayer.getData().addUHCExp(randomUHCExp);
		gamePlayer.giveExp(randomExp);
		gamePlayer.sendMessage(Messages.PERKS_BONUS_RECEIVED.toString().replace("%coins%", TextUtils.formatToOneDecimal(randomCoins)).replace("%uhcExp%", TextUtils.formatToOneDecimal(randomUHCExp)).replace("%exp%", String.valueOf(randomExp)));
	}

}
