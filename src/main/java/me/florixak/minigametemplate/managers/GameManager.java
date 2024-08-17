package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigManager;
import me.florixak.minigametemplate.game.GameState;

public class GameManager {

	private static GameManager instance;
	private final MinigameTemplate plugin;

	private final ConfigManager configManager;
	private final PlayerManager playerManager;
	private final KitsManager kitsManager;
	private final PerksManager perksManager;
	private final TeamManager teamsManager;


	private GameState gameState = GameState.LOBBY;

	public GameManager(final MinigameTemplate plugin) {
		this.plugin = plugin;
		instance = this;

		this.configManager = new ConfigManager();
		this.configManager.loadFiles(plugin);
		this.playerManager = new PlayerManager(this);
		this.teamsManager = new TeamManager(this);
		this.kitsManager = new KitsManager(this);
		this.perksManager = new PerksManager(this);
	}

	public static GameManager getGameManager() {
		return instance;
	}

	public MinigameTemplate getPlugin() {
		return this.plugin;
	}

	public GameState getGameState() {
		return this.gameState;
	}

	public void setGameState(final GameState gameState) {
		if (this.gameState == gameState) return;

		this.gameState = gameState;

		switch (gameState) {
			case LOBBY:
				break;
			case STARTING:
				break;
			case INGAME:
				break;
			case ENDING:
				break;
			case RESTARTING:
				break;
		}
	}

	public void onDisable() {
//		this.configManager.saveAll();
		this.playerManager.onDisable();
		this.teamsManager.onDisable();
		this.kitsManager.onDisable();
		this.perksManager.onDisable();
	}

	public ConfigManager getConfigManager() {
		return this.configManager;
	}

	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public TeamManager getTeamManager() {
		return this.teamsManager;
	}

	public KitsManager getKitsManager() {
		return this.kitsManager;
	}

	public PerksManager getPerksManager() {
		return this.perksManager;
	}
}
