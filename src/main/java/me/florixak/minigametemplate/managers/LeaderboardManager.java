package me.florixak.minigametemplate.managers;

import com.cryptomorin.xseries.XMaterial;
import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.statistics.Leaderboard;
import me.florixak.minigametemplate.game.statistics.LeaderboardType;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaderboardManager {

	private final GameManager gameManager;

	private final Map<LeaderboardType, List<Leaderboard>> topLists = new HashMap<>();

	public LeaderboardManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		loadTopStatistics();
	}

	private void loadTopStatistics() {
		this.topLists.put(LeaderboardType.WINS, getTotalTopOf(LeaderboardType.WINS));
		this.topLists.put(LeaderboardType.KILLS, getTotalTopOf(LeaderboardType.KILLS));
		this.topLists.put(LeaderboardType.ASSISTS, getTotalTopOf(LeaderboardType.ASSISTS));
		this.topLists.put(LeaderboardType.DEATHS, getTotalTopOf(LeaderboardType.DEATHS));
		this.topLists.put(LeaderboardType.LOSSES, getTotalTopOf(LeaderboardType.LOSSES));
		this.topLists.put(LeaderboardType.KILLSTREAK, getTotalTopOf(LeaderboardType.KILLSTREAK));
		this.topLists.put(LeaderboardType.UHC_LEVEL, getTotalTopOf(LeaderboardType.UHC_LEVEL));
		this.topLists.put(LeaderboardType.GAMES_PLAYED, getTotalTopOf(LeaderboardType.GAMES_PLAYED));
	}

	public void updateTopStatistic(final LeaderboardType type) {
		this.topLists.put(type, getTotalTopOf(type));
	}

	private List<Leaderboard> getTotalTopOf(final LeaderboardType type) {
		final List<Leaderboard> topTotal = new ArrayList<>();

		if (this.gameManager.isDatabaseConnected()) {
			final Map<String, Integer> topStatistics = this.gameManager.getData().getTopStatistics(type.getDatabaseType());
			if (topStatistics != null) {
				topTotal.addAll(topStatistics.entrySet().stream().map(entry -> new Leaderboard(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
			}
		} else {
			final FileConfiguration playerData = this.gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
			if (playerData.getConfigurationSection("player-data") == null) return topTotal;
			for (final String uuid : playerData.getConfigurationSection("player-data").getKeys(false)) {
				final String name = playerData.getString("player-data." + uuid + ".name");
				final int value = playerData.getInt("player-data." + uuid + "." + type.getType());
				topTotal.add(new Leaderboard(name, value));
			}
		}

		topTotal.sort((name1, name2) -> Integer.compare(name2.getValue(), name1.getValue()));
		//type.updateHologram();
		return topTotal;
	}

	public List<Leaderboard> getTopStatistics(final LeaderboardType type) {
		return this.topLists.get(type);
	}

	public ItemStack getPlayerStatsItem(final GamePlayer gamePlayer) {
		final ItemStack playerStatsItem = GameValues.STATISTICS.PLAYER_STATS_DIS_ITEM.equalsIgnoreCase("PLAYER_HEAD")
				? gamePlayer.getPlayerHead(gamePlayer.getName())
				: XMaterial.matchXMaterial(GameValues.STATISTICS.PLAYER_STATS_DIS_ITEM.toUpperCase())
				.get().parseItem();

		final String playerStatsName = GameValues.STATISTICS.PLAYER_STATS_CUST_NAME != null
				? GameValues.STATISTICS.PLAYER_STATS_CUST_NAME
				: gamePlayer.getName();

		final List<String> playerStatsLore = new ArrayList<>();

		for (String text : GameValues.STATISTICS.PLAYER_STATS_LORE) {
			text = PAPI.setPlaceholders(gamePlayer.getPlayer(), text);
			playerStatsLore.add(TextUtils.color(text));
		}
		return ItemUtils.createItem(
				playerStatsItem.getType(),
				PAPI.setPlaceholders(gamePlayer.getPlayer(), playerStatsName),
				1,
				playerStatsLore);
	}

	public void onDisable() {
		this.topLists.clear();
	}
}
