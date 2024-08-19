package me.florixak.minigametemplate.managers.player;

import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.player.PlayerQuestData;

import java.util.HashMap;
import java.util.Map;

public class PlayerQuestDataManager {

	private final Map<GamePlayer, PlayerQuestData> playerQuestData = new HashMap<>();

	public PlayerQuestDataManager() {

	}

	public void addPlayerData(final GamePlayer gamePlayer) {
		this.playerQuestData.put(gamePlayer, new PlayerQuestData(gamePlayer));
	}

	public void removePlayerData(final GamePlayer gamePlayer) {
		this.playerQuestData.remove(gamePlayer);
	}

	public PlayerQuestData getPlayerData(final GamePlayer gamePlayer) {
		if (!this.hasPlayerData(gamePlayer)) {
			this.addPlayerData(gamePlayer);
		}
		return this.playerQuestData.get(gamePlayer);
	}

	public boolean hasPlayerData(final GamePlayer gamePlayer) {
		return this.playerQuestData.containsKey(gamePlayer);
	}

	public void clearPlayerData(final GamePlayer gamePlayer) {
		this.playerQuestData.remove(gamePlayer);
	}

	public void clearAllPlayerData() {
		this.playerQuestData.clear();
	}

	public void onDisable() {
		this.playerQuestData.clear();
	}
}
