package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigManager;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.GameState;
import me.florixak.minigametemplate.listeners.*;
import me.florixak.minigametemplate.managers.scoreboard.ScoreboardManager;
import me.florixak.minigametemplate.managers.scoreboard.TabManager;
import me.florixak.minigametemplate.sql.MySQL;
import me.florixak.minigametemplate.sql.SQLGetter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

	private static GameManager instance;
	private final MinigameTemplate plugin;

	private final ConfigManager configManager;
	private final PlayerManager playerManager;
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

		this.config = getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

		registerCommands();
		registerListeners();
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

	public MySQL getSQL() {
		return this.mysql;
	}

	public SQLGetter getDatabase() {
		return this.data;
	}

	private void connectToDatabase() {
		try {
			final String path = "settings.mysql";
			if (this.config == null || !this.config.getBoolean(path + ".enabled", false)) return;

			final String host = this.config.getString(path + ".host", "localhost");
			final String port = this.config.getString(path + ".port", "3306");
			final String database = this.config.getString(path + ".database", "minigametemplate");
			final String username = this.config.getString(path + ".username", "root");
			final String password = this.config.getString(path + ".password", "");

			this.mysql = new MySQL(host, port, database, username, password);
			if (this.mysql.hasConnection()) {
				this.data = new SQLGetter(this);
			}
		} catch (final Exception e) {
			Bukkit.getLogger().info("Failed to connect to MySQL database!");
		}

	}

	private void disconnectDatabase() {
		if (this.config.getBoolean("settings.mysql.enabled", false) && this.mysql != null && this.mysql.hasConnection()) {
			this.mysql.disconnect();
		}
	}

	public boolean isDatabaseConnected() {
		return this.mysql != null && this.mysql.hasConnection();
	}

	public void onDisable() {
//		this.configManager.saveAll();
		this.playerManager.onDisable();
		this.teamsManager.onDisable();
		this.kitsManager.onDisable();
		this.perksManager.onDisable();
		this.questManager.onDisable();
		this.leaderboardManager.onDisable();
		disconnectDatabase();
	}

	public ConfigManager getConfigManager() {
		return this.configManager;
	}

	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public LeaderboardManager getLeaderboardManager() {
		return this.leaderboardManager;
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

	public QuestManager getQuestManager() {
		return this.questManager;
	}

	public ScoreboardManager getScoreboardManager() {
		return this.scoreboardManager;
	}

	public TabManager getTabManager() {
		return this.tabManager;
	}

	public MenuManager getMenuManager() {
		return this.menuManager;
	}

	public TasksManager getTasksManager() {
		return this.tasksManager;
	}

	public BorderManager getBorderManager() {
		return this.borderManager;
	}

	public SoundManager getSoundManager() {
		return this.soundManager;
	}

	public LobbyManager getLobbyManager() {
		return this.lobbyManager;
	}

	public WorldManager getWorldManager() {
		return this.worldManager;
	}
}
