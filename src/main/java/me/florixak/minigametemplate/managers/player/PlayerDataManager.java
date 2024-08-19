package me.florixak.minigametemplate.managers.player;

import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.player.PlayerData;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataManager {

	private final Map<GamePlayer, PlayerData> playerData = new HashMap<>();

	public PlayerDataManager() {
	}

	private void addPlayerData(final GamePlayer gamePlayer) {
		this.playerData.put(gamePlayer, new PlayerData(gamePlayer));
	}

	public void removePlayerData(final GamePlayer gamePlayer) {
		this.playerData.remove(gamePlayer);
	}

	public PlayerData getPlayerData(final GamePlayer gamePlayer) {
		if (!this.hasPlayerData(gamePlayer)) {
			this.addPlayerData(gamePlayer);
		}
		return this.playerData.get(gamePlayer);
	}

	public boolean hasPlayerData(final GamePlayer gamePlayer) {
		return this.playerData.containsKey(gamePlayer);
	}

	public void clearPlayerData(final GamePlayer gamePlayer) {
		this.playerData.remove(gamePlayer);
	}

	public void clearAllPlayerData() {
		this.playerData.clear();
	}

	public void onDisable() {
		this.playerData.clear();
	}


}
