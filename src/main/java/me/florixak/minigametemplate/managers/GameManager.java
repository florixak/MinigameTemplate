package me.florixak.minigametemplate.managers;

import lombok.Getter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.commands.AnvilCommand;
import me.florixak.minigametemplate.commands.JoinCommand;
import me.florixak.minigametemplate.commands.LeaveCommand;
import me.florixak.minigametemplate.commands.MinigameCommand;
import me.florixak.minigametemplate.config.ConfigManager;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.ArenaManager;
import me.florixak.minigametemplate.game.assists.DamageTrackerManager;
import me.florixak.minigametemplate.listeners.*;
import me.florixak.minigametemplate.managers.boards.ScoreboardManager;
import me.florixak.minigametemplate.managers.boards.TablistManager;
import me.florixak.minigametemplate.managers.player.PlayerDataManager;
import me.florixak.minigametemplate.managers.player.PlayerManager;
import me.florixak.minigametemplate.managers.player.PlayerQuestDataManager;
import me.florixak.minigametemplate.sql.MySQL;
import me.florixak.minigametemplate.sql.SQLGetter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
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
	private final WorldManager worldManager;
	private final ArenaManager arenaManager;
	private final GameItemManager gameItemManager;
	private final KitsManager kitsManager;
	private final PerksManager perksManager;
	private final QuestManager questManager;
	private final LeaderboardManager leaderboardManager;
	private final ScoreboardManager scoreboardManager;
	private final TablistManager tablistManager;
	private final MenuManager menuManager;
	private final TasksManager tasksManager;
	private final BorderManager borderManager;
	private final SoundManager soundManager;
	private final LobbyManager lobbyManager;
	private final DamageTrackerManager damageTrackerManager;

	private MySQL mysql;
	private SQLGetter data;

	private final FileConfiguration config;

	public GameManager(final MinigameTemplate plugin) {
		this.plugin = plugin;
		instance = this;

		this.configManager = new ConfigManager();
		this.configManager.loadFiles(plugin);
		this.playerManager = new PlayerManager(this);
		this.playerDataManager = new PlayerDataManager();
		this.playerQuestDataManager = new PlayerQuestDataManager();
		this.worldManager = new WorldManager();
		this.lobbyManager = new LobbyManager(this);
		this.gameItemManager = new GameItemManager(this);
		this.kitsManager = new KitsManager(this);
		this.perksManager = new PerksManager(this);
		this.questManager = new QuestManager(this);
		this.leaderboardManager = new LeaderboardManager(this);
		this.scoreboardManager = new ScoreboardManager(this);
		this.tablistManager = new TablistManager(this);
		this.menuManager = new MenuManager();
		this.tasksManager = new TasksManager(this);
		this.borderManager = new BorderManager();
		this.soundManager = new SoundManager();
		this.damageTrackerManager = new DamageTrackerManager();
		this.arenaManager = new ArenaManager(this);

		this.config = getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		connectToDatabase();

		registerCommands();
		registerListeners();
	}

	private void registerCommands() {
		registerCommand("minigame", new MinigameCommand(this));
		registerCommand("join", new JoinCommand(this));
		registerCommand("leave", new LeaveCommand(this));
		registerCommand("anvil", new AnvilCommand(this));
	}

	private void registerCommand(final String command, final CommandExecutor executor) {
		final PluginCommand pluginCommand = this.plugin.getCommand(command);

		if (pluginCommand == null) {
			this.plugin.getLogger().info("Error in registering command! (" + command + ")");
			return;
		}
		pluginCommand.setExecutor(executor);
	}

	private void registerListeners() {
		final List<Listener> listeners = new ArrayList<>();

		listeners.add(new PlayerListener(this));
		listeners.add(new GameListener(this));
		listeners.add(new InventoryClickListener(this));
		listeners.add(new MenuItemsInteractListener(this));
		listeners.add(new EntityListener(this));
		if (!MinigameTemplate.useOldMethods()) listeners.add(new AnvilClickListener());

		for (final Listener listener : listeners) {
			this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
		}
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
		this.kitsManager.onDisable();
		this.perksManager.onDisable();
		this.questManager.onDisable();
		this.leaderboardManager.onDisable();
		this.damageTrackerManager.onDisable();
		this.arenaManager.onDisable();
		this.gameItemManager.onDisable();
		disconnectDatabase();
	}
}
