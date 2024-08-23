package me.florixak.minigametemplate.game.gameItems;

import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.menu.inGame.TeamSelectorMenu;
import org.bukkit.inventory.ItemStack;

public class TeamSelectorItem extends ExecutableItem {

	public TeamSelectorItem(final String name, final String key, final ItemStack item) {
		super(name, key, item);
	}

	@Override
	public void execute(final GamePlayer gamePlayer) {
		final MenuUtils menuUtils = gameManager.getMenuManager().getMenuUtils(gamePlayer);
		new TeamSelectorMenu(menuUtils).open();
	}
}
