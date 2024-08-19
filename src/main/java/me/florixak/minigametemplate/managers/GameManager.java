package me.florixak.minigametemplate.managers;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigManager;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameState;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.assists.DamageTrackerManager;
import me.florixak.minigametemplate.listeners.*;
import me.florixak.minigametemplate.managers.boards.ScoreboardManager;
import me.florixak.minigametemplate.managers.boards.TabManager;
import me.florixak.minigametemplate.managers.player.PlayerDataManager;
import me.florixak.minigametemplate.managers.player.PlayerManager;
import me.florixak.minigametemplate.managers.player.PlayerQuestDataManager;
import me.florixak.minigametemplate.sql.MySQL;
import me.florixak.minigametemplate.sql.SQLGetter;
import me.florixak.minigametemplate.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameManager {

	@Getter
	private static GameManager instance;
	private final MinigameTemplate plugin;

	private final ConfigManager configManager;
	private final PlayerManager playerManager;
	private final PlayerDataManager playerDataManager;
	private final PlayerQuestDataManager playerQuestDataManager;
	private final TeamManager teamsManager;
	private final KitsManager kitsManager;
	private final PerksManager perksManager;
	private final QuestManager questManager;
	private final LeaderboardManager leaderboardManager;
	private final ScoreboardManager scoreboardManager;
	private final TabManager tabManager;
	private final MenuManager menuManager;
	private final TasksManager tasksManager;
	private final BorderManager borderManager;
	private final SoundManager soundManager;
	private final LobbyManager lobbyManager;
	private final WorldManager worldManager;
	private final DamageTrackerManager damageTrackerManager;

	private MySQL mysql;
	private SQLGetter data;

	private GameState gameState = GameState.LOBBY;
	private final FileConfiguration config;

	public GameManager(final MinigameTemplate plugin) {
		this.plugin = plugin;
		instance = this;

		this.configManager = new ConfigManager();
		this.configManager.loadFiles(plugin);
		this.playerManager = new PlayerManager(this);
		this.playerDataManager = new PlayerDataManager();
		this.playerQuestDataManager = new PlayerQuestDataManager();
		this.teamsManager = new TeamManager(this);
		this.kitsManager = new KitsManager(this);
		this.perksManager = new PerksManager(this);
		this.questManager = new QuestManager(this);
		this.leaderboardManager = new LeaderboardManager(this);
		this.scoreboardManager = new ScoreboardManager(this);
		this.tabManager = new TabManager();
		this.menuManager = new MenuManager();
		this.tasksManager = new TasksManager();
		this.borderManager = new BorderManager();
		this.soundManager = new SoundManager();
		this.lobbyManager = new LobbyManager(this);
		this.worldManager = new WorldManager();
		this.damageTrackerManager = new DamageTrackerManager();

		this.config = getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		connectToDatabase();

		registerCommands();
		registerListeners();
	}

	public static GameManager getGameManager() {
		return instance;
	}

	public void setGameState(final GameState gameState) {
		if (this.gameState == gameState) return;

		this.gameState = gameState;

		switch (gameState) {
			case LOBBY:
				break;
			case STARTING:
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.GAME_STARTING.toString()));
				break;
			case INGAME:
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.GAME_STARTED.toString()));
				break;
			case ENDING:
				Utils.broadcast(PAPI.setPlaceholders(null, Messages.GAME_ENDED.toString()));
				break;
			case RESTARTING:
				break;
		}
	}

	public boolean isLobby() {
		return this.gameState == GameState.LOBBY;
	}

	public boolean isStarting() {
		return this.gameState == GameState.STARTING;
	}

	public boolean isPlaying() {
		return this.gameState == GameState.INGAME;
	}

	public boolean isEnding() {
		return this.gameState == GameState.ENDING;
	}

	public void registerCommands() {
		final List<CommandExecutor> commands = new ArrayList<>();

		for (final CommandExecutor command : commands) {
			this.plugin.getCommand("command").setExecutor(command);
		}
	}

	public void registerListeners() {
		final List<Listener> listeners = new ArrayList<>();

		listeners.add(new PlayerListener(this));
		listeners.add(new GameListener());
		listeners.add(new InventoryClickListener(this));
		listeners.add(new MenuItemsInteractListener(this));
		listeners.add(new EntityListener());
		if (!MinigameTemplate.useOldMethods()) listeners.add(new AnvilClickListener());

		for (final Listener listener : listeners) {
			this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
		}
	}

	public boolean isGameFull() {
		return this.playerManager.getPlayers().size() >= this.playerManager.getMaxPlayers();
	}

	private void connectToDatabase() {
		try {
			if (this.config == null || !GameValues.DATABASE.ENABLED) return;

			final String host = GameValues.DATABASE.HOST;
			final String port = GameValues.DATABASE.PORT;
			final String database = GameValues.DATABASE.DATABASE;
			final String username = GameValues.DATABASE.USERNAME;
			final String password = GameValues.DATABASE.PASSWORD;

			this.mysql = new MySQL(host, port, database, username, password);
			if (this.mysql.hasConnection()) {
				this.data = new SQLGetter(this);
			}
		} catch (final Exception e) {
			Bukkit.getLogger().info("Failed to connect to MySQL database!");
		}

	}

	private void disconnectDatabase() {
		if (GameValues.DATABASE.ENABLED && this.mysql != null && this.mysql.hasConnection()) {
			this.mysql.disconnect();
		}
	}

	public boolean isDatabaseConnected() {
		return this.mysql != null && this.mysql.hasConnection();
	}

	public void onDisable() {
//		this.configManager.saveAll();
		this.playerManager.onDisable();
		this.playerDataManager.onDisable();
		this.playerQuestDataManager.onDisable();
		this.teamsManager.onDisable();
		this.kitsManager.onDisable();
		this.perksManager.onDisable();
		this.questManager.onDisable();
		this.leaderboardManager.onDisable();
		this.damageTrackerManager.onDisable();
		disconnectDatabase();
	}
}
