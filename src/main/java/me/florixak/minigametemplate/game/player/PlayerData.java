package me.florixak.minigametemplate.game.player;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {

	private final MinigameTemplate plugin = MinigameTemplate.getInstance();
	private final GameManager gameManager = GameManager.getInstance();
	private final FileConfiguration playerData = this.gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

	private final GamePlayer gamePlayer;
	@Getter
	private double money = GameValues.STATISTICS.STARTING_MONEY;
	@Getter
	private int tokens = GameValues.STATISTICS.STARTING_TOKENS;
	@Getter
	private double exp = 0;
	@Getter
	private int level = GameValues.STATISTICS.STARTING_LEVEL;
	@Getter
	private double requiredExp = GameValues.STATISTICS.STARTING_REQUIRED_EXP;

	private final Map<DataType, Integer> soloStats = new HashMap<>();
	private final Map<DataType, Integer> teamsStats = new HashMap<>();

	public PlayerData(final GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		initializeData();
	}

	private void initializeData() {
		if (!hasData()) {
			setInitialData();
			return;
		}
		loadData();
	}

	private void setInitialData() {
		if (this.plugin.getVaultHook().hasEconomy()) {
			if (GameValues.STATISTICS.STARTING_MONEY > 0 && !this.plugin.getVaultHook().hasAccount(this.gamePlayer.getPlayer())) {
				this.plugin.getVaultHook().deposit(this.gamePlayer.getName(), GameValues.STATISTICS.STARTING_MONEY);
			}
		}

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().createPlayer(this.gamePlayer.getPlayer());
			return;
		}

		final String path = "player-data." + this.gamePlayer.getUuid();
		this.playerData.set(path + ".money", GameValues.STATISTICS.STARTING_MONEY);
		this.playerData.set(path + ".tokens", GameValues.STATISTICS.STARTING_TOKENS);
		this.playerData.set(path + ".level", GameValues.STATISTICS.STARTING_LEVEL);
		this.playerData.set(path + ".exp", 0);
		this.playerData.set(path + ".required-exp", GameValues.STATISTICS.STARTING_REQUIRED_EXP);

		for (final DataType type : DataType.values()) {
			this.playerData.set(path + ".solo." + type.toString(), 0);
			this.playerData.set(path + ".teams." + type.toString(), 0);
		}
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void loadData() {

		for (final DataType type : DataType.values()) {
			this.soloStats.put(type, getIntData(type, 0, "solo"));
			this.teamsStats.put(type, getIntData(type, 0, "teams"));
		}
	}

	private int getIntData(final DataType type, final int defaultValue, final String category) {
		if (this.gameManager.isDatabaseConnected()) {
			return this.gameManager.getData().getIntData(type, this.gamePlayer.getUuid());
		}
		return this.playerData.getInt("player-data." + this.gamePlayer.getUuid() + "." + category + "." + type.toString(), defaultValue);
	}

	private boolean hasData() {
		if (this.gameManager.isDatabaseConnected()) {
			return this.gameManager.getData().exists(this.gamePlayer.getUuid());
		}
		return this.playerData.getConfigurationSection("player-data." + this.gamePlayer.getUuid()) != null;
	}

	public int getSoloStat(final DataType type) {
		return this.soloStats.getOrDefault(type, 0);
	}

	public int getTeamsStat(final DataType type) {
		return this.teamsStats.getOrDefault(type, 0);
	}

	private void setSoloStat(final DataType type, final int value) {
		this.soloStats.put(type, value);
		saveStat(type, value, "solo");
	}

	private void setTeamsStat(final DataType type, final int value) {
		this.teamsStats.put(type, value);
		saveStat(type, value, "teams");
	}

	private void saveStat(final DataType type, final Object value) {
		if (this.gameManager.isDatabaseConnected()) {
//			this.gameManager.getData().setStat(this.gamePlayer.getUuid(), type, value);
		} else {
			this.playerData.set("player-data." + this.gamePlayer.getUuid() + "." + type.toString(), value);
			this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}
	}

	private void saveStat(final DataType type, final int value, final String category) {
		if (this.gameManager.isDatabaseConnected()) {
//			this.gameManager.getData().setStat(this.gamePlayer.getUuid(), type, value);
		} else {
			this.playerData.set("player-data." + this.gamePlayer.getUuid() + "." + category + "." + type.toString(), value);
			this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}
	}

	public void saveSoloStatistics() {
		addGameResult("solo");
		if (this.gamePlayer.getKills() > 0)
			setSoloStat(DataType.SOLO_KILLS, getSoloStat(DataType.SOLO_KILLS) + this.gamePlayer.getKills());
		if (this.gamePlayer.getAssists() > 0)
			setSoloStat(DataType.SOLO_ASSISTS, getSoloStat(DataType.SOLO_ASSISTS) + this.gamePlayer.getAssists());
		if (this.gamePlayer.isDead())
			setSoloStat(DataType.SOLO_DEATHS, getSoloStat(DataType.SOLO_DEATHS) + 1);

		if (this.gamePlayer.getKills() > this.getIntData(DataType.SOLO_KILLSTREAK, 0, "solo")) {
			setSoloStat(DataType.SOLO_KILLSTREAK, this.gamePlayer.getKills());
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.KILLSTREAK_NEW.toString()));
		}

		double money = this.gamePlayer.getMoneyForGameResult() + this.gamePlayer.getMoneyForKills() + this.gamePlayer.getMoneyForAssists() + this.gamePlayer.getMoneyForActivity();
		double uhcExp = this.gamePlayer.getExpForGameResult() + this.gamePlayer.getExpForKills() + this.gamePlayer.getExpForAssists() + this.gamePlayer.getExpForActivity();
		money *= GameValues.REWARDS.MULTIPLIER;
		uhcExp *= GameValues.REWARDS.MULTIPLIER;

		setGamesPlayed("solo");
		depositMoney(money);
		addExp(uhcExp);
		this.gameManager.getPlayerQuestDataManager().getPlayerData(this.gamePlayer).savePlayerQuestData();
	}

	public void saveTeamsStatistics() {
		addGameResult("teams");
		if (this.gamePlayer.getKills() > 0)
			setTeamsStat(DataType.TEAMS_KILLS, getTeamsStat(DataType.TEAMS_KILLS) + this.gamePlayer.getKills());
		if (this.gamePlayer.getAssists() > 0)
			setTeamsStat(DataType.TEAMS_ASSISTS, getTeamsStat(DataType.TEAMS_ASSISTS) + this.gamePlayer.getAssists());
		if (this.gamePlayer.isDead())
			setTeamsStat(DataType.TEAMS_DEATHS, getTeamsStat(DataType.TEAMS_DEATHS) + 1);

		if (this.gamePlayer.getKills() > this.getIntData(DataType.TEAMS_KILLSTREAK, 0, "teams")) {
			setTeamsStat(DataType.TEAMS_KILLSTREAK, this.gamePlayer.getKills());
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.KILLSTREAK_NEW.toString()));
		}

		double money = this.gamePlayer.getMoneyForGameResult() + this.gamePlayer.getMoneyForKills() + this.gamePlayer.getMoneyForAssists() + this.gamePlayer.getMoneyForActivity();
		double uhcExp = this.gamePlayer.getExpForGameResult() + this.gamePlayer.getExpForKills() + this.gamePlayer.getExpForAssists() + this.gamePlayer.getExpForActivity();
		money *= GameValues.REWARDS.MULTIPLIER;
		uhcExp *= GameValues.REWARDS.MULTIPLIER;

		setGamesPlayed("teams");
		depositMoney(money);
		addExp(uhcExp);
		this.gameManager.getPlayerQuestDataManager().getPlayerData(this.gamePlayer).savePlayerQuestData();
	}

	private void addGameResult(final String category) {
		if (category.equalsIgnoreCase("solo")) {
			if (this.gamePlayer.isWinner())
				setSoloStat(DataType.SOLO_WINS, getSoloStat(DataType.SOLO_WINS) + 1);
			else
				setSoloStat(DataType.SOLO_LOSSES, getSoloStat(DataType.SOLO_LOSSES) + 1);
		} else {
			if (this.gamePlayer.isWinner())
				setTeamsStat(DataType.TEAMS_WINS, getTeamsStat(DataType.TEAMS_WINS) + 1);
			else
				setTeamsStat(DataType.TEAMS_LOSSES, getTeamsStat(DataType.TEAMS_LOSSES) + 1);
		}
	}

	private void setGamesPlayed(final String category) {
		if (category.equalsIgnoreCase("solo"))
			setSoloStat(DataType.SOLO_GAMES_PLAYED, getSoloStat(DataType.SOLO_WINS) + getSoloStat(DataType.SOLO_LOSSES));
		else
			setTeamsStat(DataType.TEAMS_GAMES_PLAYED, getTeamsStat(DataType.TEAMS_WINS) + getTeamsStat(DataType.TEAMS_LOSSES));
	}

	public double getMoney() {
		if (this.plugin.getVaultHook().hasEconomy()) {
			return this.plugin.getVaultHook().getBalance(this.gamePlayer.getPlayer());
		}
		return this.money;
	}

	public void depositMoney(final double amount) {
		if (this.plugin.getVaultHook().hasEconomy()) {
			this.plugin.getVaultHook().deposit(this.gamePlayer.getName(), amount);
			return;
		}
		this.money += amount;
		saveStat(DataType.MONEY, (int) this.money);
	}

	public void withdrawMoney(final double amount) {
		if (this.plugin.getVaultHook().hasEconomy()) {
			this.plugin.getVaultHook().withdraw(this.gamePlayer.getName(), amount);
			return;
		}
		this.money -= amount;
		saveStat(DataType.MONEY, this.money);
	}

	public boolean hasEnoughMoney(final double amount) {
		return getMoney() >= amount;
	}

	public void depositTokens(final int amount) {
		this.tokens += amount;
		saveStat(DataType.TOKENS, this.tokens);
	}

	public void withdrawTokens(final int amount) {
		this.tokens -= amount;
		saveStat(DataType.TOKENS, this.tokens);
	}

	public void addActivityRewards() {
		final double money = GameValues.ACTIVITY_REWARDS.MONEY;
		final double uhcExp = GameValues.ACTIVITY_REWARDS.EXP;

		this.gamePlayer.addMoneyForActivity(money);
		this.gamePlayer.addExpForActivity(uhcExp);

		depositMoney(money);
		addExp(uhcExp);

		this.gamePlayer.sendMessage(Messages.REWARDS_ACTIVITY.toString().replace("%money%", String.valueOf(money)).replace("%uhc-exp%", String.valueOf(uhcExp)));
	}

	public void addExp(final double amount) {
		this.exp += amount;
		saveStat(DataType.EXP, this.exp);
		checkLevelUp();
	}

	private void increaseUHCLevel() {
		this.exp -= this.requiredExp;
		final int previousLevel = this.level;
		this.level++;
		final int newLevel = this.level;
		this.requiredExp = setRequiredExp();

		saveStat(DataType.LEVEL, this.level);
		saveStat(DataType.REQUIRED_EXP, this.requiredExp);
		saveStat(DataType.EXP, (int) this.exp);
		saveStat(DataType.REQUIRED_EXP, this.requiredExp);

		final double reward = GameValues.REWARDS.BASE_REWARD * GameValues.REWARDS.REWARD_COEFFICIENT * this.level;
		depositMoney(reward);
		if (this.gamePlayer.getPlayer() != null) {
			this.gameManager.getSoundManager().playLevelUpSound(this.gamePlayer.getPlayer());
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.LEVEL_UP.toString()
					.replace("%previous-level%", String.valueOf(previousLevel))
					.replace("%new-level%", String.valueOf(newLevel))));
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.REWARDS_LEVEL_UP.toString().replace("%money%", String.valueOf(reward))));
		}
	}

	private double setRequiredExp() {
		return this.requiredExp * GameValues.STATISTICS.EXP_MULTIPLIER;
	}

	private void checkLevelUp() {
		while (this.exp >= this.requiredExp) {
			increaseUHCLevel();
		}
	}

	public void showStatistics() {
		final List<String> rewards = this.gamePlayer.isWinner() ? Messages.REWARDS_WIN.toList() : Messages.REWARDS_LOSE.toList();

		for (String message : rewards) {
			message = PAPI.setPlaceholders(this.gamePlayer.getPlayer(), message);
			this.gamePlayer.sendMessage(TextUtils.color(message));
		}
	}
}