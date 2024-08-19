package me.florixak.minigametemplate.gui;

import lombok.Getter;
import lombok.Setter;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.player.GamePlayer;
import org.bukkit.entity.Player;

@Getter
@Setter
public class MenuUtils {

	private final GamePlayer gamePlayer;
	private final Player owner;

	private Kit selectedKitToBuy;
	private Perk selectedPerkToBuy;

	public MenuUtils(final GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		this.owner = gamePlayer.getPlayer();
	}
}
