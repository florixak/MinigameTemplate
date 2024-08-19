package me.florixak.minigametemplate.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.TimeUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
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
		return "1.0";
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

			if (placeholder.equals("level")) {
				return String.valueOf(gamePlayer.getPlayerData().getLevel());
			}

			if (placeholder.equals("exp")) {
				return String.valueOf(gamePlayer.getPlayerData().getExp());
			}

			if (placeholder.equals("required-exp")) {
				return TextUtils.formatToOneDecimal(gamePlayer.getPlayerData().getRequiredExp());
			}

			if (placeholder.equals("tokens")) {
				return String.valueOf(0);
			}

			if (placeholder.equals("total-wins")) {
				return String.valueOf(gamePlayer.getPlayerData().getWins());
			}

			if (placeholder.equals("total-kills")) {
				return String.valueOf(gamePlayer.getPlayerData().getKills());
			}

			if (placeholder.equals("total-deaths")) {
				return String.valueOf(gamePlayer.getPlayerData().getDeaths());
			}

			if (placeholder.equals("total-assists")) {
				return String.valueOf(gamePlayer.getPlayerData().getAssists());
			}

			if (placeholder.equals("total-losses")) {
				return String.valueOf(gamePlayer.getPlayerData().getLosses());
			}

			if (placeholder.equals("completed-quests")) {
				return String.valueOf(gamePlayer.getPlayerQuestData().getCompletedQuests().size());
			}
		}

		if (placeholder.equals("date")) {
			return TimeUtils.getCurrentDate();
		}

		return null;
	}
}
