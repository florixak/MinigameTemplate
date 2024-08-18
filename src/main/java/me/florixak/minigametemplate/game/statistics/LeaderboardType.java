package me.florixak.minigametemplate.game.statistics;

import com.cryptomorin.xseries.XMaterial;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.Utils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public enum LeaderboardType {

	WINS("wins"),
	KILLS("kills"),
	ASSISTS("assists"),
	DEATHS("deaths"),
	LOSSES("losses"),
	KILLSTREAK("killstreak"),
	UHC_LEVEL("uhc-level"),
	GAMES_PLAYED("games-played");

	@Getter
	private final String type;

	LeaderboardType(final String type) {
		this.type = type;
	}

	public String getDatabaseType() {
		return this.type.replace("-", "_");
	}

	public String getHologramName() {
		return "stats-" + this.type;
	}

	private String getTopStatsDisplayItemString() {
		switch (this) {
			case WINS:
				return GameValues.STATISTICS.TOP_WINS_ITEM;
			case KILLS:
				return GameValues.STATISTICS.TOP_KILLS_ITEM;
			case ASSISTS:
				return GameValues.STATISTICS.TOP_ASSISTS_ITEM;
			case DEATHS:
				return GameValues.STATISTICS.TOP_DEATHS_ITEM;
			case LOSSES:
				return GameValues.STATISTICS.TOP_LOSSES_ITEM;
			case KILLSTREAK:
				return GameValues.STATISTICS.TOP_KILLSTREAK_ITEM;
			case UHC_LEVEL:
				return GameValues.STATISTICS.TOP_LEVEL_ITEM;
			case GAMES_PLAYED:
				return GameValues.STATISTICS.TOP_GAMES_PLAYED_ITEM;
			default:
				return "PAPER";
		}
	}

	public String getTopStatsDisplayName() {
		switch (this) {
			case WINS:
				return GameValues.STATISTICS.TOP_WINS_NAME;
			case KILLS:
				return GameValues.STATISTICS.TOP_KILLS_NAME;
			case ASSISTS:
				return GameValues.STATISTICS.TOP_ASSISTS_NAME;
			case DEATHS:
				return GameValues.STATISTICS.TOP_DEATHS_NAME;
			case LOSSES:
				return GameValues.STATISTICS.TOP_LOSSES_NAME;
			case KILLSTREAK:
				return GameValues.STATISTICS.TOP_KILLSTREAK_NAME;
			case UHC_LEVEL:
				return GameValues.STATISTICS.TOP_LEVEL_NAME;
			case GAMES_PLAYED:
				return GameValues.STATISTICS.TOP_GAMES_PLAYED_NAME;
			default:
				return "TOP STATS";
		}
	}

	public ItemStack getTopStatsDisplayItem() {
		final ItemStack topStatsItem = XMaterial.matchXMaterial(this.getTopStatsDisplayItemString()).get().parseItem();
		final String topStatsName = GameValues.STATISTICS.TOP_STATS_CUST_NAME;

		final List<String> topStatsLore = this.getLeaderboardList();

		return ItemUtils.createItem(topStatsItem.getType(),
				topStatsName.replace("%top-stats-mode%", TextUtils.color(this.getTopStatsDisplayName())),
				1,
				topStatsLore);
	}

	private List<String> getReplacedTopStats(final List<String> list) {
		final List<String> topStatsLore = new ArrayList<>();
		for (String lore : list) {
			for (int j = 1; j <= list.size(); j++) {
				lore = lore
						.replace("%uhcrevamp_top-" + j + "%", "%uhcrevamp_" + this.type + "-top-" + j + "%");
				//.replace("%uhc-top-" + j + "%", "%uhcrevamp_" + this.type + "-top-" + j + "-value%");
			}
			topStatsLore.add(TextUtils.color(lore.replace("%top-stats-mode%", TextUtils.color(this.getTopStatsDisplayName()))));
		}
		return topStatsLore;
	}


	public void createHologram(final Location loc) {
		final String name = "stats-" + this.type;
		final Hologram hologram = Utils.createHologram(name, getReplacedTopStats(GameValues.STATISTICS.TOP_STATS_HOLOGRAM), loc, true);
	}

	public void removeHologram() {
		final String name = "stats-" + this.type;
		final Hologram hologram = DHAPI.getHologram(name);
		if (hologram == null) return;
		hologram.delete();
	}

	public void moveHologram(final Location loc) {
		final String name = "stats-" + this.type;
		final Hologram hologram = DHAPI.getHologram(name);
		if (hologram == null) return;
		DHAPI.moveHologram(hologram, loc);
	}

	private List<String> getLeaderboardList() {
		return PAPI.setPlaceholders(null, getReplacedTopStats(GameValues.STATISTICS.TOP_STATS_LORE));
	}

	public static LeaderboardType getLeaderboardType(final String type) {
		for (final LeaderboardType leaderboardType : LeaderboardType.values()) {
			if (leaderboardType.getType().equalsIgnoreCase(type)) {
				return leaderboardType;
			}
		}
		return null;
	}
}
