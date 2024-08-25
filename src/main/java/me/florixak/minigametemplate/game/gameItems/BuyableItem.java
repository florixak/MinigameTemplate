package me.florixak.minigametemplate.game.gameItems;

public abstract class BuyableItem {

	public abstract String getName();

	public abstract double getCost();

	public abstract boolean isFree();

	public abstract String getFormattedCost();


}
