package me.florixak.minigametemplate.managers.boards;

import lombok.Getter;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
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

	private int titleOrder = 0;

	private final List<String> title;
	@Getter
	private final String footer;
	private final List<String> lobby;
	private final List<String> waiting;
	private final List<String> starting;
	private final List<String> inGame;
	private final List<String> ending;

	private final GameManager gameManager;

	public ScoreboardManager(final GameManager gameManager) {
		this.gameManager = gameManager;

		final FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SCOREBOARD).getConfig();

		this.title = config.getStringList("scoreboard.title");
		this.footer = config.getString("scoreboard.footer");

		this.lobby = config.getStringList("scoreboard.lobby");
		this.waiting = config.getStringList("scoreboard.waiting");
		this.starting = config.getStringList("scoreboard.starting");
		this.inGame = config.getStringList("scoreboard.in-game");
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
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(p.getUniqueId());

		ScoreHelper helper = this.players.get(p.getUniqueId());
		if (helper == null) helper = new ScoreHelper(p);

		if (this.titleOrder >= this.title.size()) this.titleOrder = 0;
		helper.setTitle(this.title.get(this.titleOrder));
		this.titleOrder++;

		if (!gamePlayer.isInArena()) {
			helper.setSlotsFromList(this.lobby);
			return helper;
		}

		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
		switch (arena.getArenaState()) {
			case WAITING:
				helper.setSlotsFromList(this.waiting);
				break;
			case STARTING:
				helper.setSlotsFromList(this.starting);
				break;
			case INGAME:
				helper.setSlotsFromList(this.inGame);
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