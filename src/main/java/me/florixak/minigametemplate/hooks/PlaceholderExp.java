package me.florixak.minigametemplate.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.TimeUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderExp extends PlaceholderExpansion {

	private final MinigameTemplate plugin;

	public PlaceholderExp(final MinigameTemplate plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getIdentifier() {
		return "minigame";
	}

	@Override
	public String getAuthor() {
		return "FloriXak";
	}

	@Override
	public String getVersion() {
		return this.plugin.getDescription().getVersion();
	}

	@Override
	public boolean persist() {
		return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String onPlaceholderRequest(final Player p, final String params) {
		final String placeholder = params.toLowerCase();

		if (p != null) {
			final GamePlayer gamePlayer = this.plugin.getGameManager().getPlayerManager().getGamePlayer(p.getUniqueId());
			if (placeholder.equals("player")) {
				return p.getName();
			}

			if (placeholder.equals("money")) {
				return String.valueOf(gamePlayer.getPlayerData().getMoney());
			}

			if (placeholder.equals("tokens")) {
				return String.valueOf(gamePlayer.getPlayerData().getTokens());
			}

			if (placeholder.equals("level")) {
				return String.valueOf(gamePlayer.getPlayerData().getLevel());
			}

			if (placeholder.equals("exp")) {
				return String.valueOf(gamePlayer.getPlayerData().getExp());
			}

			if (placeholder.equals("required_exp")) {
				return TextUtils.formatToOneDecimal(gamePlayer.getPlayerData().getRequiredExp());
			}

			if (placeholder.equals("total_games_played")) {
				return String.valueOf(gamePlayer.getPlayerData().getGamesPlayed());
			}

			if (placeholder.equals("total_wins")) {
				return String.valueOf(gamePlayer.getPlayerData().get());
			}

			if (placeholder.equals("total_kills")) {
				return String.valueOf(gamePlayer.getPlayerData().getKills());
			}

			if (placeholder.equals("total_deaths")) {
				return String.valueOf(gamePlayer.getPlayerData().getDeaths());
			}

			if (placeholder.equals("total_assists")) {
				return String.valueOf(gamePlayer.getPlayerData().getAssists());
			}

			if (placeholder.equals("total_losses")) {
				return String.valueOf(gamePlayer.getPlayerData().getLosses());
			}

			if (placeholder.equals("completed_quests")) {
				return String.valueOf(gamePlayer.getPlayerQuestData().getCompletedQuests().size());
			}

			if (this.plugin.getGameManager().getArenaManager().isPlayerInArena(gamePlayer)) {
				final Arena arena = this.plugin.getGameManager().getArenaManager().getPlayerArena(gamePlayer);

				if (placeholder.equals("arena_name")) {
					return TextUtils.color(arena.getName());
				}

				if (placeholder.equals("arena_id")) {
					return TextUtils.color(arena.getId());
				}

				if (placeholder.equals("arena_online")) {
					return String.valueOf(arena.getPlayers().size());
				}

				if (placeholder.equals("arena_min")) {
					return String.valueOf(arena.getMinPlayers());
				}

				if (placeholder.equals("arena_max")) {
					return String.valueOf(arena.getMaxPlayers());
				}

				if (placeholder.equals("arena_alive")) {
					return String.valueOf(arena.getAlivePlayers().size());
				}

				if (placeholder.equals("arena_teams_alive")) {
					return String.valueOf(arena.getAliveTeams().size());
				}

				if (placeholder.equals("arena_state")) {
					if (arena.getArenaState() == null) return Messages.ARENA_LORE_DISABLED.toString();
					return arena.getArenaState().toString();
				}

				if (placeholder.equals("arena_time")) {
					return String.valueOf(arena.getArenaTime());
				}

				if (placeholder.equals("arena_winner")) {
					return TextUtils.color(arena.getWinner());
				}

				if (placeholder.equals("arena_mode")) {
					return arena.getArenaMode();
				}

				if (placeholder.equals("team")) {
					if (gamePlayer.hasTeam()) return TextUtils.color(gamePlayer.getTeam().getDisplayName());
					else return Messages.TEAM_NONE.toString();
				}

				if (placeholder.equals("kit")) {
					if (gamePlayer.hasKit()) return TextUtils.color(gamePlayer.getKit().getDisplayName());
					else return Messages.KITS_SCOREBOARD_SELECTED_NONE.toString();
				}

				if (placeholder.equals("perk")) {
					if (gamePlayer.hasPerk()) return TextUtils.color(gamePlayer.getPerk().getDisplayName());
					else return Messages.PERKS_SCOREBOARD_SELECTED_NONE.toString();
				}
			}

		}


		if (placeholder.equals("date")) {
			return TimeUtils.getCurrentDate();
		}

		if (placeholder.equals("online")) {
			return String.valueOf(this.plugin.getGameManager().getPlayerManager().getPlayers().size());
		}

		if (placeholder.equals("max_online")) {
			return String.valueOf(Bukkit.getServer().getMaxPlayers());
		}

		if (placeholder.equals("scoreboard_footer")) {
			return TextUtils.color(this.plugin.getGameManager().getConfigManager().getFile(ConfigType.SCOREBOARD).getConfig().getString("scoreboard.footer"));
		}


		return null;
	}


}
