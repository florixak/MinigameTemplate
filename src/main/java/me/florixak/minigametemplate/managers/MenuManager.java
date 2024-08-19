package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.MenuUtils;

import java.util.HashMap;

public class MenuManager {

	private final HashMap<GamePlayer, MenuUtils> menuUtilsMap = new HashMap<>();

	public MenuManager() {
	}

	public MenuUtils getMenuUtils(final GamePlayer gamePlayer) {
		if (!this.menuUtilsMap.containsKey(gamePlayer)) {
			this.menuUtilsMap.put(gamePlayer, new MenuUtils(gamePlayer));
		}
		return this.menuUtilsMap.get(gamePlayer);
	}

	public void removeMenuUtils(final GamePlayer gamePlayer) {
		this.menuUtilsMap.remove(gamePlayer);
	}

	public void onDisable() {
		this.menuUtilsMap.clear();
	}
}
