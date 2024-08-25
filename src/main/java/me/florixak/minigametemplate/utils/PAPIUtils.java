package me.florixak.minigametemplate.utils;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.arena.Arena;
import org.bukkit.entity.Player;

public class PAPIUtils {

	public static String setPlaceholders(final String message) {

		return message;
	}

	public static String setPlayerPlaceholders(final Player player, final String message) {
		if (player == null) return message;

		return message;
	}

	public static String setArenaPlaceholders(final Player player, final Arena arena, String message) {
		if (arena == null) return message;

		if (message.contains("%arena_name%")) {
			message = message.replace("%arena_name%", arena.getName());
		}

		if (message.contains("%arena_id%")) {
			message = message.replace("%arena_name%", arena.getId());
		}

		if (message.contains("%arena_online%")) {
			message = message.replace("%arena_players%", String.valueOf(arena.getPlayers().size()));
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
			message = message.replace("%arena_alive%", String.valueOf(arena.getAliveTeams().size()));
		}

		if (message.contains("%arena_state%")) {
			message = message.replace("%arena_state%", arena.getArenaState().name());
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

		if (MinigameTemplate.getInstance().getPapiHook().hasPlaceholderAPI()) {
			return PAPI.setPlaceholders(player, message);
		}

		return message;
	}
}
