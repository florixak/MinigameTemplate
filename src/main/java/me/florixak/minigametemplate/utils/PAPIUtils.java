package me.florixak.minigametemplate.utils;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.entity.Player;

import java.util.List;

public class PAPIUtils {

	private static final GameManager gameManager = GameManager.getInstance();
	private static final String SCOREBOARD_FOOTER = gameManager.getConfigManager().getFile(ConfigType.SCOREBOARD).getConfig().getString("scoreboard.footer", "");

	public static String setPlaceholders(final Player player, final Arena arena, String message) {

		if (message.contains("%date%")) {
			message = message.replace("%date%", TimeUtils.getCurrentDate());
		}

		if (message.contains("%scoreboard_footer%")) {
			message = message.replace("%scoreboard_footer%", SCOREBOARD_FOOTER);
		}

		if (player != null) {
			final GamePlayer gamePlayer = gameManager.getPlayerManager().getGamePlayer(player.getUniqueId());

			if (message.contains("%player%")) {
				message = message.replace("%player%", gamePlayer.getName());
			}

			if (message.contains("%level%")) {
				message = message.replace("%level%", String.valueOf(gamePlayer.getData().getLevel()));
			}

			if (message.contains("%exp%")) {
				message = message.replace("%exp%", String.valueOf(gamePlayer.getData().getExp()));
			}

			if (message.contains("%required_exp%")) {
				message = message.replace("%required_exp%", String.valueOf(gamePlayer.getData().getRequiredExp()));
			}

			if (message.contains("%money%")) {
				message = message.replace("%money%", String.valueOf(gamePlayer.getData().getMoney()));
			}

			if (message.contains("%tokens%")) {
				message = message.replace("%tokens%", String.valueOf(gamePlayer.getData().getTokens()));
			}

			if (message.contains("%health%")) {
				message = message.replace("%health%", String.valueOf(player.getHealth()));
			}

			if (message.contains("%max_health%")) {
				message = message.replace("%max_health%", String.valueOf(player.getMaxHealth()));
			}

			if (message.contains("%food%")) {
				message = message.replace("%food%", String.valueOf(player.getFoodLevel()));
			}
		}

		if (arena != null) {

			if (message.contains("%arena_name%")) {
				message = message.replace("%arena_name%", arena.getName());
			}

			if (message.contains("%arena_id%")) {
				message = message.replace("%arena_name%", arena.getId());
			}

			if (message.contains("%arena_online%")) {
				message = message.replace("%arena_online%", String.valueOf(arena.getPlayers().size()));
			}

			if (message.contains("%arena_min%")) {
				message = message.replace("%arena_min%", String.valueOf(arena.getMinPlayers()));
			}

			if (message.contains("%arena_max%")) {
				message = message.replace("%arena_max%", String.valueOf(arena.getMaxPlayers()));
			}

			if (message.contains("%arena_alive%")) {
				message = message.replace("%arena_alive%", String.valueOf(arena.getAlivePlayers().size()));
			}

			if (message.contains("%arena_teams_alive%")) {
				message = message.replace("%arena_teams_alive%", String.valueOf(arena.getAliveTeams().size()));
			}

			if (message.contains("%arena_state%")) {
				message = message.replace("%arena_state%", arena.getArenaState().toString());
			}

			if (message.contains("%arena_time%")) {
				message = message.replace("%arena_time%", TimeUtils.getFormattedTime(arena.getArenaTime()));
			}

			if (message.contains("%arena_time%")) {
				message = message.replace("%arena_time%", TimeUtils.getFormattedTime(arena.getArenaTime()));
			}

			if (message.contains("%arena_mode%")) {
				message = message.replace("%arena_mode%", arena.getArenaMode());
			}

			if (message.contains("%arena_winner%")) {
				message = message.replace("%arena_winner%", arena.getWinner());
			}

			if (player != null) {
				final GamePlayer gamePlayer = gameManager.getPlayerManager().getGamePlayer(player.getUniqueId());
				if (message.contains("%kit%")) {
					final String kitName = gamePlayer.getArenaData().hasKit() ? gamePlayer.getArenaData().getKit().getName() : Messages.KITS_SCOREBOARD_SELECTED_NONE.toString();
					message = message.replace("%kit%", kitName);
				}
				if (message.contains("%perk%")) {
					final String perkName = gamePlayer.getArenaData().hasPerk() ? gamePlayer.getArenaData().getPerk().getName() : Messages.PERKS_SCOREBOARD_SELECTED_NONE.toString();
					message = message.replace("%perk%", perkName);
				}
				if (message.contains("%team%")) {
					final String teamName = arena.getPlayerArenaData(gamePlayer).hasTeam()
							? arena.getPlayerArenaData(gamePlayer).getTeam().getName()
							: Messages.TEAM_NONE.toString();
					message = message.replace("%team%", teamName);
				}

			}
		}


		if (MinigameTemplate.getInstance().getPapiHook().hasPlaceholderAPI()) {
			return PAPI.setPlaceholders(player, message);
		}

		return message;
	}

	public static List<String> setPlaceholders(final Player player, final Arena arena, final List<String> messages) {
		if (arena == null) return messages;

		for (int i = 0; i < messages.size(); i++) {
			messages.set(i, setPlaceholders(player, arena, messages.get(i)));
		}

		return messages;
	}
}
