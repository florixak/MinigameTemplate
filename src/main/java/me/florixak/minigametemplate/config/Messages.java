package me.florixak.minigametemplate.config;

import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public enum Messages {

	PREFIX("prefix"),

	UHC_ADMIN_HELP("help.admin"),
	UHC_VIP_HELP("help.vip"),
	UHC_PLAYER_HELP("help.player"),

	INVALID_CMD("invalid-command"),
	NO_PERM("no-perm"),
	OFFLINE_PLAYER("offline-player"),
	ONLY_PLAYER("only-for-player"),
	NO_MONEY("no-money"),
	MONEY("money"),
	CURRENCY("currency"),
	CANCELLED_PURCHASE("purchase-cancel"),
	CANT_USE_NOW("cant-use-now"),

	LOBBY_JOIN("lobby.join"),
	LOBBY_QUIT("lobby.quit"),
	LOBBY_ARENA_STARTING("lobby.arena-starting"),

	KILLSTREAK_NEW("player.new-killstreak"),
	CANT_PLACE("cant-place"),
	CANT_BREAK("cant-break"),

	TITLE_WIN("title.victory.title"),
	SUBTITLE_WIN("title.victory.subtitle"),
	TITLE_LOSE("title.lose.title"),
	SUBTITLE_LOSE("title.lose.subtitle"),

//	BORDER_SHRINK("arena.border-shrink"),

	ARENA_JOIN("arena.join"),
	ARENA_LEAVE("arena.leave"),
	ARENA_PLAYERS_TO_START("arena.players-to-start"),
	ARENA_NOT_ENOUGH_PLAYERS("arena.not-enough-players"),
	ARENA_FORCE_STARTED("arena.force-started"),
	ARENA_PHASE_FORCE_SKIPPED("arena.phase-force-skipped"),
	ARENA_STARTING("arena.starting"),
	ARENA_ALREADY_STARTING("arena.already-starting"),
	ARENA_STARTING_CANCELED("arena.starting-canceled"),
	ARENA_STARTED("arena.started"),
	ARENA_DISABLED("arena.disabled"),
	ARENA_ENDED("arena.ended"),
	ARENA_SOLO("arena.solo"),
	ARENA_TEAMS("arena.teams"),
	ARENA_KILL("arena.kill"),
	ARENA_DEATH("arena.death"),
	ARENA_RESTARTING("arena.restarting"),
	ARENA_FULL("arena.full"),
	ARENA_KICK_DUE_RESERVED_SLOT("arena.kick-due-reserved-slot"),
	ARENA_PLAYER_KILLED("arena.player-killed"),
	ARENA_PLAYED_DIED("arena.player-died"),
	ARENA_WAITING_HOTBAR("arena.waiting-hotbar"),
	ARENA_LORE("arena.lore.info"),
	ARENA_LORE_JOIN("arena.lore.join"),
	ARENA_LORE_IN_GAME("arena.lore.in-game"),
	ARENA_LORE_FULL("arena.lore.full"),
	ARENA_LORE_RESTARTING("arena.lore.restarting"),
	ARENA_LORE_DISABLED("arena.lore.disabled"),
	ARENA_LORE_SOLO("arena.lore.solo"),

	TEAM_JOIN("teams.join"),
	TEAM_LEAVE("teams.leave"),
	TEAM_FULL("teams.full"),
	TEAM_ALREADY_IN("teams.already-in-team"),
	TEAM_NOT_IN("teams.not-in-team"),
	TEAM_DEFEATED("teams.defeated"),
	TEAM_NONE("teams.scoreboard.none"),
	TEAM_NO_FRIENDLY_FIRE("teams.no-friendly-fire"),
	TEAM_NO_FREE("teams.no-free-team"),

	KITS_SELECTED("kits.selected"),
	KITS_DISABLED("kits.disabled"),
	KITS_SCOREBOARD_SELECTED_NONE("kits.scoreboard.selected-none"),
	KITS_SCOREBOARD_DISABLED("kits.scoreboard.disabled"),
	KITS_SELECTION_CLICK_TO_SELECT("kits.selection.click-to-select"),
	KITS_SELECTION_SELECTED("kits.selection.selected"),
	KITS_SHOP_COST("kits.shop.cost"),
	KITS_SHOP_CLICK_TO_BUY("kits.shop.click-to-buy"),
	KITS_SHOP_MONEY_DEDUCT("kits.shop.money-deduct"),
	KITS_SHOP_MONEY_DEDUCT_AFTER_START("kits.shop.money-deduct-after-start"),
	KITS_RECEIVED("kits.received"),

	PERKS_SELECTED("perks.selected"),
	PERKS_DISABLED("perks.disabled"),
	PERKS_SCOREBOARD_SELECTED_NONE("perks.scoreboard.selected-none"),
	PERKS_SCOREBOARD_DISABLED("perks.scoreboard.disabled"),
	PERKS_SELECTION_CLICK_TO_SELECT("perks.selection.click-to-select"),
	PERKS_SELECTION_SELECTED("perks.selection.selected"),
	PERKS_SHOP_COST("perks.shop.cost"),
	PERKS_SHOP_CLICK_TO_BUY("perks.shop.click-to-buy"),
	PERKS_SHOP_MONEY_DEDUCT("perks.shop.money-deduct"),
	PERKS_SHOP_MONEY_DEDUCT_AFTER_START("perks.shop.money-deduct-after-start"),
	PERKS_RECEIVED_EFFECT("perks.received.effect"),
	PERKS_RECEIVED_ITEM("perks.received.item"),
	PERKS_RECEIVED_BONUS("perks.received.bonus"),

	LEVEL_UP("player.uhc-level.level-up"),

	SHOT_HP("player.shot-hp"),
	GAME_RESULTS("arena-results"),

	QUEST_COMPLETED("quests.completed"),
	QUEST_REWARD("quests.reward"),

	REWARDS_WIN("rewards.win"),
	REWARDS_LOSE("rewards.lose"),
	REWARDS_ACTIVITY("rewards.activity"),
	REWARDS_KILL("rewards.kill"),
	REWARDS_ASSIST("rewards.assist"),
	REWARDS_LEVEL_UP("rewards.level-up"),

	SETUP_SET_HOLOGRAM("setup.hologram.set"),
	SETUP_DEL_HOLOGRAM("setup.hologram.removed"),
	SETUP_NOT_FOUND_HOLOGRAM("setup.hologram.not-found"),
	SETUP_SET_WAIT_LOBBY("setup.waiting-lobby.set"),
	SETUP_DEL_WAIT_LOBBY("setup.waiting-lobby.removed"),
	SETUP_SET_END_LOBBY("setup.ending-lobby.set"),
	SETUP_DEL_END_LOBBY("setup.ending-lobby.removed"),
	SETUP_SET_DEATHMATCH("setup.deathmatch.set"),
	SETUP_RESET_DEATHMATCH("setup.deathmatch.reset");

	private static FileConfiguration config;
	private final String path;

	Messages(final String path) {
		this.path = path;
	}

	public static void setConfiguration(final FileConfiguration c) {
		config = c;
	}

	@Override
	public String toString() {
		final String message = config.getString("Messages." + this.path);
		if (message == null || message.isEmpty()) {
			return TextUtils.color("&cMessage not found! &7(" + this.path + ")");
		}

		final String prefix = config.getString("Messages." + PREFIX.getPath());
		final String currency = config.getString("Messages." + CURRENCY.getPath());
		return TextUtils.color(message
				.replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : "")
				.replace("%currency%", currency != null && !currency.isEmpty() ? currency : "")
		);
	}

	public List<String> toList() {
		final List<String> messages = new ArrayList<>();

		final String prefix = config.getString("Messages." + PREFIX.getPath());
		for (final String message : config.getStringList("Messages." + this.path)) {
			if (message != null && !message.isEmpty()) {
				messages.add(TextUtils.color(message
						.replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : "")));
			}
		}
		return messages;
	}

	public String getPath() {
		return this.path;
	}
}