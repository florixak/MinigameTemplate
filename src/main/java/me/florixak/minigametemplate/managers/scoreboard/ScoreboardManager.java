package me.florixak.minigametemplate.managers.scoreboard;

import lombok.Getter;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

	private final Map<UUID, ScoreHelper> players = new HashMap<>();

	private final String title;
	@Getter
	private final String footer;
	private final List<String> waiting;
	private final List<String> starting;
	private final List<String> ending;

	private final GameManager gameManager;

	public ScoreboardManager(final GameManager gameManager) {
		this.gameManager = gameManager;

		final FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SCOREBOARD).getConfig();

		this.title = config.getString("scoreboard.title");
		this.footer = config.getString("scoreboard.footer");

		this.waiting = config.getStringList("scoreboard.waiting");
		this.starting = config.getStringList("scoreboard.starting");
		this.ending = config.getStringList("scoreboard.ending");
	}

	public void setScoreboard(final Player p) {
		if (!this.players.containsKey(p.getUniqueId())) {
			this.players.put(p.getUniqueId(), updateScoreboard(p.getUniqueId()));
		}
	}

	public ScoreHelper updateScoreboard(final UUID uuid) {

		final Player p = Bukkit.getPlayer(uuid);
		if (p == null) return null;

		ScoreHelper helper = this.players.get(p.getUniqueId());
		if (helper == null) helper = new ScoreHelper(p);
		helper.setTitle(this.title);
		switch (this.gameManager.getGameState()) {
			case LOBBY:
				helper.setSlotsFromList(this.waiting);
				break;
			case STARTING:
				helper.setSlotsFromList(this.starting);
				break;
			case ENDING:
				helper.setSlotsFromList(this.ending);
				break;
		}
		return helper;
	}

	public void removeScoreboard(final Player p) {
		if (this.players.containsKey(p.getUniqueId())) {
			this.players.remove(p.getUniqueId());
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
}