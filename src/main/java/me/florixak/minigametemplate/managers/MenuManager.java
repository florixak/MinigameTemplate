package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.MenuUtils;

import java.util.HashMap;

public class MenuManager {

	private static final HashMap<GamePlayer, MenuUtils> menuUtilsMap = new HashMap<>();

	public static MenuUtils getMenuUtils(final GamePlayer gamePlayer) {
		if (!menuUtilsMap.containsKey(gamePlayer)) {
			menuUtilsMap.put(gamePlayer, new MenuUtils(gamePlayer));
		}
		return menuUtilsMap.get(gamePlayer);
	}
}
