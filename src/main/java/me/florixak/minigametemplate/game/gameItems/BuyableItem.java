package me.florixak.minigametemplate.game.gameItems;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;

public abstract class BuyableItem {

	public abstract String getName();

	public abstract double getCost();

	public abstract boolean isFree();

	public abstract String getFormattedCost();

	public void buy(final GamePlayer gamePlayer) {
		if (!isFree() && !gamePlayer.getPlayerData().hasEnoughMoney(getCost())) {
			gamePlayer.sendMessage(Messages.NO_MONEY.toString());
			GameManager.getInstance().getSoundManager().playPurchaseCancelSound(gamePlayer.getPlayer());
			return;
		}
		gamePlayer.getPlayerData().withdrawMoney(getCost());
		final String perkCost = String.valueOf(getCost());
		final String money = String.valueOf(gamePlayer.getPlayerData().getMoney());
		final String prevMoney = String.valueOf(gamePlayer.getPlayerData().getMoney() + getCost());
		gamePlayer.sendMessage(Messages.KITS_SHOP_MONEY_DEDUCT.toString().toString(), "%previous-money%", prevMoney, "%money%", money, "%item%", getName(), "%perk-cost%", perkCost);

	}


}
