package me.florixak.minigametemplate.game;

import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class GameValues {

	private static final GameManager gameManager = GameManager.getInstance();
	private static final FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

	public static final int ERROR_INT_VALUE = -1;
	public static final double ERROR_DOUBLE_VALUE = -1.0;
	public static final String ERROR_ITEM = "BARRIER";

	public static final ArenaConfig ARENA = new ArenaConfig();
	public static final Countdowns COUNTDOWNS = new Countdowns();

	public static final TeamsConfig TEAMS = new TeamsConfig();
	public static final KitsConfig KITS = new KitsConfig();
	public static final PerksConfig PERKS = new PerksConfig();
	public static final QuestConfig QUESTS = new QuestConfig();

	public static final StatisticsConfig STATISTICS = new StatisticsConfig();
	public static final RewardConfig REWARDS = new RewardConfig();
	public static final ActivityRewardConfig ACTIVITY_REWARDS = new ActivityRewardConfig();

	public static final TitleConfig TITLE = new TitleConfig();
	public static final ChatConfig CHAT = new ChatConfig();
	public static final Sounds SOUNDS = new Sounds();
	public static final TablistConfig TABLIST = new TablistConfig();

	public static final Database DATABASE = new Database();
	public static final BungeeCordConfig BUNGEECORD = new BungeeCordConfig();
	public static final AddonConfig ADDONS = new AddonConfig();


	private GameValues() {
	}

	public static class ArenaConfig {
		public final boolean EXPLOSIONS_DISABLED = getConfigBoolean("settings.game.no-explosions", true);
		public final boolean PROJECTILE_HIT_HP_ENABLED = getConfigBoolean("settings.game.projectile-hit-hp", false);
		public final boolean SPAWN_MONSTERS = getConfigBoolean("settings.game.spawn-monsters", false);
		public final boolean MONSTERS_ATTACK = getConfigBoolean("settings.game.monsters-attack", false);
		public final int PLAYERS_TO_START = getConfigInt("settings.game.players-to-start", 2);
		public final int STARTING_MESSAGE_AT = getConfigInt("settings.game.starting-message-at", 10);
	}

	public static class KitsConfig {
		public final boolean ENABLED = getConfigBoolean("settings.kits.enabled", true);
	}

	public static class PerksConfig {
		public final boolean ENABLED = getConfigBoolean("settings.perks.enabled", true);
	}

	public static class TeamsConfig {
		public final boolean FRIENDLY_FIRE = getConfigBoolean("settings.teams.friendly-fire", true);
	}

	public static class Countdowns {
		public final int STARTING = getConfigInt("settings.game.countdowns.starting", 20);
		public final int IN_GAME = getConfigInt("settings.game.countdowns.in-game", 20);
		public final int ENDING = getConfigInt("settings.game.countdowns.ending", 20);
	}

	public static class Database {
		public final boolean ENABLED = getConfigBoolean("settings.mysql.enabled", false);
		public final String HOST = getConfigString("settings.mysql.host", "localhost");
		public final String PORT = getConfigString("settings.mysql.port", "3306");
		public final String DATABASE = getConfigString("settings.mysql.database", "database");
		public final String TABLE = getConfigString("settings.mysql.table", "minigame");
		public final String USERNAME = getConfigString("settings.mysql.username", "root");
		public final String PASSWORD = getConfigString("settings.mysql.password", "password");
	}

	public static class ChatConfig {
		public final String SOLO_FORMAT = getConfigString("settings.chat.solo-format", "");
		public final String TEAM_FORMAT = getConfigString("settings.chat.teams-format", "");
		public final String DEAD_FORMAT = getConfigString("settings.chat.dead-format", "");
		public final String LOBBY_FORMAT = getConfigString("settings.chat.lobby-format", "");
		public final String GLOBAL_FORMAT = getConfigString("settings.chat.global-format", "");
		public final List<String> BLOCKED_COMMANDS = getConfigStringList("settings.chat.blocked-commands");
	}

	public static class BungeeCordConfig {
		public final boolean ENABLED = getConfigBoolean("settings.bungee-mode.enabled", false);
		public final String LOBBY_SERVER = getConfigString("settings.bungeecord.lobby-server", "lobby");
	}

	public static class StatisticsConfig {
		public final int STARTING_LEVEL = getConfigInt("settings.statistics.level.starting-level", 0);
		public final double STARTING_REQUIRED_EXP = getConfigDouble("settings.statistics.level.starting-required-exp", 100.0);
		public final double EXP_MULTIPLIER = getConfigDouble("settings.statistics.level.exp-multiplier", 3.75);
		public final String PLAYER_STATS_DIS_ITEM = getConfigString("settings.statistics.player-stats.display-item", "PLAYER_HEAD");
		public final String PLAYER_STATS_CUST_NAME = getConfigString("settings.statistics.player-stats.custom-name", "YOUR STATS");
		public final List<String> PLAYER_STATS_LORE = getConfigStringList("settings.statistics.player-stats.lore");
		public final String TOP_STATS_CUST_NAME = getConfigString("settings.statistics.top-stats.custom-name", "TOP STATS");
		public final List<String> TOP_STATS_LORE = getConfigStringList("settings.statistics.top-stats.lore");
		public final List<String> TOP_STATS_HOLOGRAM = getConfigStringList("settings.statistics.top-stats.hologram");
		public final double STARTING_MONEY = getConfigDouble("settings.statistics.starting-money", 0);
		public final int STARTING_TOKENS = getConfigInt("settings.statistics.starting-tokens", 0);

		public final int PLAYER_STATS_SLOT = getConfigInt("settings.statistics.player-stats.slot", 10);

		public final int TOP_WINS_SLOT = getConfigInt("settings.statistics.top-stats.types.WINS.slot", 12);
		public final int TOP_KILLS_SLOT = getConfigInt("settings.statistics.top-stats.types.KILLS.slot", 13);
		public final int TOP_ASSISTS_SLOT = getConfigInt("settings.statistics.top-stats.types.ASSISTS.slot", 14);
		public final int TOP_DEATHS_SLOT = getConfigInt("settings.statistics.top-stats.types.DEATHS.slot", 15);
		public final int TOP_LOSSES_SLOT = getConfigInt("settings.statistics.top-stats.types.LOSSES.slot", 16);
		public final int TOP_KILLSTREAK_SLOT = getConfigInt("settings.statistics.top-stats.types.KILLSTREAK.slot", 19);
		public final int TOP_LEVEL_SLOT = getConfigInt("settings.statistics.top-stats.types.LEVEL.slot", 20);
		public final int TOP_GAMES_PLAYED_SLOT = getConfigInt("settings.statistics.top-stats.types.GAMES-PLAYED.slot", 21);

		public final String TOP_WINS_NAME = getConfigString("settings.statistics.top-stats.types.WINS.display-name", "Wins");
		public final String TOP_KILLS_NAME = getConfigString("settings.statistics.top-stats.types.KILLS.display-name", "Kills");
		public final String TOP_ASSISTS_NAME = getConfigString("settings.statistics.top-stats.types.ASSISTS.display-name", "Assists");
		public final String TOP_DEATHS_NAME = getConfigString("settings.statistics.top-stats.types.DEATHS.display-name", "Deaths");
		public final String TOP_LOSSES_NAME = getConfigString("settings.statistics.top-stats.types.LOSSES.display-name", "Losses");
		public final String TOP_KILLSTREAK_NAME = getConfigString("settings.statistics.top-stats.types.KILLSTREAK.display-name", "Killstreak");
		public final String TOP_LEVEL_NAME = getConfigString("settings.statistics.top-stats.types.LEVEL.display-name", "UHC Level");
		public final String TOP_GAMES_PLAYED_NAME = getConfigString("settings.statistics.top-stats.types.GAMES-PLAYED.display-name", "Games Played");

		public final String TOP_WINS_ITEM = getConfigString("settings.statistics.top-stats.types.WINS.display-item", "PAPER");
		public final String TOP_KILLS_ITEM = getConfigString("settings.statistics.top-stats.types.KILLS.display-item", "PAPER");
		public final String TOP_ASSISTS_ITEM = getConfigString("settings.statistics.top-stats.types.ASSISTS.display-item", "PAPER");
		public final String TOP_DEATHS_ITEM = getConfigString("settings.statistics.top-stats.types.DEATHS.display-item", "PAPER");
		public final String TOP_LOSSES_ITEM = getConfigString("settings.statistics.top-stats.types.LOSSES.display-item", "PAPER");
		public final String TOP_KILLSTREAK_ITEM = getConfigString("settings.statistics.top-stats.types.KILLSTREAK.display-item", "PAPER");
		public final String TOP_LEVEL_ITEM = getConfigString("settings.statistics.top-stats.types.LEVEL.display-item", "PAPER");
		public final String TOP_GAMES_PLAYED_ITEM = getConfigString("settings.statistics.top-stats.types.GAMES-PLAYED.display-item", "PAPER");
	}

	public static class RewardConfig {
		public final double MULTIPLIER = getConfigDouble("settings.rewards.multiplier", 1);
		public final double BASE_REWARD = getConfigDouble("settings.rewards.base-reward", 100);
		public final double REWARD_COEFFICIENT = getConfigDouble("settings.rewards.reward-coefficient", 1);
		public final double COINS_FOR_WIN = getConfigDouble("settings.rewards.win.coins", 0);
		public final double EXP_FOR_WIN = getConfigInt("settings.rewards.win.uhc-exp", 0);
		public final double COINS_FOR_LOSE = getConfigDouble("settings.rewards.lose.coins", 0);
		public final double EXP_FOR_LOSE = getConfigInt("settings.rewards.lose.uhc-exp", 0);
		public final double COINS_FOR_KILL = getConfigDouble("settings.rewards.kill.coins", 0);
		public final double EXP_FOR_KILL = getConfigDouble("settings.rewards.kill.uhc-exp", 0);
		public final double COINS_FOR_ASSIST = getConfigDouble("settings.rewards.assist.coins", 0);
		public final double EXP_FOR_ASSIST = getConfigDouble("settings.rewards.assist.uhc-exp", 0);
	}

	public static class TablistConfig {
		public final boolean LOBBY_ENABLED = getConfigBoolean("settings.tablist.lobby.enabled", true);
		public final String LOBBY_HEADER = getConfigString("settings.tablist.lobby.header", "Tablist Header");
		public final String LOBBY_FOOTER = getConfigString("settings.tablist.lobby.footer", "Tablist Footer");
		public final String LOBBY_PLAYER_LIST = getConfigString("settings.tablist.lobby.player-list", "&f%minigame_player%");

		public final boolean INGAME_ENABLED = getConfigBoolean("settings.tablist.ingame.enabled", true);
		public final String INGAME_HEADER = getConfigString("settings.tablist.ingame.header", "Tablist Header");
		public final String INGAME_FOOTER = getConfigString("settings.tablist.ingame.footer", "Tablist Footer");
		public final String INGAME_PLAYER_LIST = getConfigString("settings.tablist.ingame.player-list", "&f%minigame_player%");
	}

	public static class ActivityRewardConfig {
		public final boolean ENABLED = getConfigBoolean("settings.rewards.activity.enabled", true);
		public final int INTERVAL = getConfigInt("settings.rewards.activity.period", 300);
		public final double MONEY = getConfigDouble("settings.rewards.activity.money", 10);
		public final double EXP = getConfigDouble("settings.rewards.activity.uhc-exp", 20);
	}

	public static class AddonConfig {
		public final boolean CAN_USE_VAULT = getConfigBoolean("settings.addons.use-Vault", true);
		public final boolean CAN_USE_LUCKPERMS = getConfigBoolean("settings.addons.use-LuckPerms", true);
		public final boolean CAN_USE_PLACEHOLDERAPI = getConfigBoolean("settings.addons.use-PlaceholderAPI", true);
		public final boolean CAN_USE_PROTOCOLLIB = getConfigBoolean("settings.addons.use-ProtocolLib", true);
		public final boolean CAN_USE_DECENTHOLOGRAMS = getConfigBoolean("settings.addons.use-DecentHolograms", true);
	}

	public static class QuestConfig {
		public final boolean ENABLED = getConfigBoolean("settings.quests.enabled", true);
	}

	public static class TitleConfig {
		public final boolean ENABLED = getConfigBoolean("settings.title.enabled", true);
		public final int FADE_IN = getConfigInt("settings.title.fade-in", 20);
		public final int STAY = getConfigInt("settings.title.stay", 60);
		public final int FADE_OUT = getConfigInt("settings.title.fade-out", 20);
	}

	public static class Sounds {
		public final String STARTED_SOUND = getConfigString("settings.sounds.started.sound", "NONE");
		public final float STARTED_VOLUME = (float) getConfigDouble("settings.sounds.started.volume", 0.5);
		public final float STARTED_PITCH = (float) getConfigDouble("settings.sounds.started.pitch", 1.0);

		public final String STARTING_SOUND = getConfigString("settings.sounds.starting.sound", "NONE");
		public final float STARTING_VOLUME = (float) getConfigDouble("settings.sounds.starting.volume", 0.5);
		public final float STARTING_PITCH = (float) getConfigDouble("settings.sounds.starting.pitch", 1.0);

		public final String WIN_SOUND = getConfigString("settings.sounds.win.sound", "NONE");
		public final float WIN_VOLUME = (float) getConfigDouble("settings.sounds.win.volume", 0.5);
		public final float WIN_PITCH = (float) getConfigDouble("settings.sounds.win.pitch", 1.0);

		public final String GAME_OVER_SOUND = getConfigString("settings.sounds.game-over.sound", "NONE");
		public final float GAME_OVER_VOLUME = (float) getConfigDouble("settings.sounds.game-over.volume", 0.5);
		public final float GAME_OVER_PITCH = (float) getConfigDouble("settings.sounds.game-over.pitch", 1.0);

		public final String LEVEL_UP_SOUND = getConfigString("settings.sounds.level-up.sound", "NONE");
		public final float LEVEL_UP_VOLUME = (float) getConfigDouble("settings.sounds.level-up.volume", 0.5);
		public final float LEVEL_UP_PITCH = (float) getConfigDouble("settings.sounds.level-up.pitch", 1.0);

		public final String KILL_SOUND = getConfigString("settings.sounds.kill.sound", "NONE");
		public final float KILL_VOLUME = (float) getConfigDouble("settings.sounds.kill.volume", 0.5);
		public final float KILL_PITCH = (float) getConfigDouble("settings.sounds.kill.pitch", 1.0);

		public final String DEATH_SOUND = getConfigString("settings.sounds.death.sound", "NONE");
		public final float DEATH_VOLUME = (float) getConfigDouble("settings.sounds.death.volume", 0.5);
		public final float DEATH_PITCH = (float) getConfigDouble("settings.sounds.death.pitch", 1.0);

		public final String ASSIST_SOUND = getConfigString("settings.sounds.assist.sound", "NONE");
		public final float ASSIST_VOLUME = (float) getConfigDouble("settings.sounds.assist.volume", 0.5);
		public final float ASSIST_PITCH = (float) getConfigDouble("settings.sounds.assist.pitch", 1.0);

		public final String SELECT_SOUND = getConfigString("settings.sounds.select-buy.sound", "NONE");
		public final float SELECT_VOLUME = (float) getConfigDouble("settings.select-buy.select.volume", 0.5);
		public final float SELECT_PITCH = (float) getConfigDouble("settings.select-buy.select.pitch", 1.0);

		public final String PURCHASE_CANCEL_SOUND = getConfigString("settings.sounds.purchase-cancel.sound", "NONE");
		public final float PURCHASE_CANCEL_VOLUME = (float) getConfigDouble("settings.sounds.purchase-cancel.volume", 0.5);
		public final float PURCHASE_CANCEL_PITCH = (float) getConfigDouble("settings.sounds.purchase-cancel.pitch", 1.0);

		public final String QUEST_COMPLETE_SOUND = getConfigString("settings.sounds.quest-complete.sound", "NONE");
		public final float QUEST_COMPLETE_VOLUME = (float) getConfigDouble("settings.sounds.quest-complete.volume", 0.5);
		public final float QUEST_COMPLETE_PITCH = (float) getConfigDouble("settings.sounds.quest-complete.pitch", 1.0);
	}

	private static boolean getConfigBoolean(final String path, final boolean def) {
		return config.getBoolean(path, def);
	}

	private static String getConfigString(final String path, final String def) {
		return config.getString(path, def);
	}

	private static int getConfigInt(final String path, final int def) {
		return config.getInt(path, def);
	}

	private static double getConfigDouble(final String path, final double def) {
		return config.getDouble(path, def);
	}

	private static List<String> getConfigStringList(final String path) {
		return config.getStringList(path);
	}
}