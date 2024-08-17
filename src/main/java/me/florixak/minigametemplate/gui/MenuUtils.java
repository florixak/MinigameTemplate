package me.florixak.minigametemplate.gui;

import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.player.GamePlayer;
import org.bukkit.entity.Player;

public class MenuUtils {

	private final GamePlayer gamePlayer;
	private final Player owner;

	private Kit selectedKitToBuy;
	private Perk selectedPerkToBuy;

	public MenuUtils(final GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		this.owner = gamePlayer.getPlayer();
	}

	public Player getOwner() {
		return this.owner;
	}

	public GamePlayer getGamePlayer() {
		return this.gamePlayer;
	}

	public void setSelectedKitToBuy(final Kit selectedKitToBuy) {
		this.selectedKitToBuy = selectedKitToBuy;
	}

	public Kit getSelectedKitToBuy() {
		return this.selectedKitToBuy;
	}

	public Perk getSelectedPerkToBuy() {
		return this.selectedPerkToBuy;
	}

	public void setSelectedPerkToBuy(final Perk selectedPerkToBuy) {
		this.selectedPerkToBuy = selectedPerkToBuy;
	}


}
