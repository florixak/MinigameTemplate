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

	SOLO_GAMES_PLAYED("solo-games-played"),
	SOLO_WINS("solo-wins"),
	SOLO_KILLS("solo-kills"),
	SOLO_ASSISTS("solo-assists"),
	SOLO_DEATHS("solo-deaths"),
	SOLO_LOSSES("solo-losses"),
	SOLO_KILLSTREAK("solo-killstreak"),

	TEAMS_GAMES_PLAYED("teams-games-played"),
	TEAMS_WINS("teams-wins"),
	TEAMS_KILLS("teams-kills"),
	TEAMS_ASSISTS("teams-assists"),
	TEAMS_DEATHS("teams-deaths"),
	TEAMS_LOSSES("teams-losses"),
	TEAMS_KILLSTREAK("teams-killstreak"),

	UHC_LEVEL("uhc-level"),
	GAMES_PLAYED("games-played");

	@Getter
	private final String type;

	LeaderboardType(final String type) {
		this.type = type;
	}

	public String getDatabaseType() {
		return this.type;
	}

	public String getHologramName() {
		return "stats-" + this.type;
	}

	private String getTopStatsDisplayItemString() {
		return "";
	}

	public String getTopStatsDisplayName() {
		return "";
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
