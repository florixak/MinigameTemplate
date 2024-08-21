package me.florixak.minigametemplate.game.player;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PlayerData {

	private static final MinigameTemplate plugin = MinigameTemplate.getInstance();
	private static final GameManager gameManager = GameManager.getInstance();
	private static final FileConfiguration playerData = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

	private final GamePlayer gamePlayer;
	private String playerName;
	private double money;
	private int level;
	private double exp;
	private double requiredExp;
	private int wins;
	private int losses;
	private int kills;
	private int killstreak;
	private int assists;
	private int deaths;
	private final List<Kit> boughtKitsList = new ArrayList<>();
	private final List<Perk> boughtPerksList = new ArrayList<>();

	public PlayerData(final GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		initializeData();
	}

	public void initializeData() {
		if (!hasData()) {
			setInitialData();
			return;
		}
		if (gameManager.isDatabaseConnected()) loadDataFromDatabase();
		else loadDataFromConfig();

		loadBoughtKits();
		loadBoughtPerks();

		if (this.level == 0) {
			this.level = GameValues.STATISTICS.FIRST_LEVEL;
		}
		if (this.exp == 0) {
			this.exp = 0;
		}
		if (this.requiredExp == 0) {
			this.requiredExp = GameValues.STATISTICS.FIRST_REQUIRED_EXP;
		}
	}

	private void setInitialData() {

		if (plugin.getVaultHook().hasEconomy()) {
			if (GameValues.STATISTICS.STARTING_MONEY > 0 && !plugin.getVaultHook().hasAccount(this.gamePlayer.getPlayer())) {
				plugin.getVaultHook().deposit(this.gamePlayer.getName(), GameValues.STATISTICS.STARTING_MONEY);
			}
		}

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().createPlayer(this.gamePlayer.getPlayer());
			return;
		}

		final String path = "player-data." + this.gamePlayer.getUuid();

		playerData.set(path + ".name", this.gamePlayer.getName());
		playerData.set(path + ".money", GameValues.STATISTICS.STARTING_MONEY);
		playerData.set(path + ".level", GameValues.STATISTICS.FIRST_LEVEL);
		playerData.set(path + ".exp", 0);
		playerData.set(path + ".required-exp", GameValues.STATISTICS.FIRST_REQUIRED_EXP);
		playerData.set(path + ".games-played", 0);
		playerData.set(path + ".wins", 0);
		playerData.set(path + ".losses", 0);
		playerData.set(path + ".kills", 0);
		playerData.set(path + ".killstreak", 0);
		playerData.set(path + ".assists", 0);
		playerData.set(path + ".deaths", 0);
		playerData.set(path + ".kits", new ArrayList<>());
		playerData.set(path + ".perks", new ArrayList<>());
//        playerData.set(path + ".time-played", 0);

		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void loadDataFromDatabase() {
		this.playerName = gameManager.getData().getName(this.gamePlayer.getUuid());
		this.money = gameManager.getData().getMoney(this.gamePlayer.getUuid());
		this.level = gameManager.getData().getLevel(this.gamePlayer.getUuid());
		this.exp = gameManager.getData().getExp(this.gamePlayer.getUuid());
		this.requiredExp = gameManager.getData().getRequiredExp(this.gamePlayer.getUuid());
		this.wins = gameManager.getData().getWins(this.gamePlayer.getUuid());
		this.losses = gameManager.getData().getLosses(this.gamePlayer.getUuid());
		this.kills = gameManager.getData().getKills(this.gamePlayer.getUuid());
		this.killstreak = gameManager.getData().getKillstreak(this.gamePlayer.getUuid());
		this.assists = gameManager.getData().getAssists(this.gamePlayer.getUuid());
		this.deaths = gameManager.getData().getDeaths(this.gamePlayer.getUuid());
	}

	private void loadDataFromConfig() {
		final String path = "player-data." + this.gamePlayer.getUuid();
		this.playerName = playerData.getString(path + ".name");
		this.money = playerData.getDouble(path + ".money", GameValues.STATISTICS.STARTING_MONEY);
		this.level = playerData.getInt(path + ".level", GameValues.ERROR_INT_VALUE);
		this.exp = playerData.getDouble(path + ".exp", GameValues.ERROR_INT_VALUE);
		this.requiredExp = playerData.getDouble(path + ".required-exp", GameValues.ERROR_INT_VALUE);
		this.wins = playerData.getInt(path + ".wins", GameValues.ERROR_INT_VALUE);
		this.losses = playerData.getInt(path + ".losses", GameValues.ERROR_INT_VALUE);
		this.kills = playerData.getInt(path + ".kills", GameValues.ERROR_INT_VALUE);
		this.killstreak = playerData.getInt(path + ".killstreak", GameValues.ERROR_INT_VALUE);
		this.assists = playerData.getInt(path + ".assists", GameValues.ERROR_INT_VALUE);
		this.deaths = playerData.getInt(path + ".deaths", GameValues.ERROR_INT_VALUE);
	}

	private boolean hasData() {
		if (gameManager.isDatabaseConnected()) {
			return gameManager.getData().exists(this.gamePlayer.getUuid());
		}
		return playerData.getConfigurationSection("player-data." + this.gamePlayer.getUuid()) != null;
	}

	public double getMoney() {
		if (plugin.getVaultHook().hasEconomy()) {
			return plugin.getVaultHook().getBalance(this.gamePlayer.getPlayer());
		}
		return this.money;
	}

	public void depositMoney(final double amount) {
		if (plugin.getVaultHook().hasEconomy()) {
			plugin.getVaultHook().deposit(this.gamePlayer.getName(), amount);
			return;
		}
		this.money += amount;
		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().setMoney(this.gamePlayer.getUuid(), this.money);
			return;
		}
		playerData.set("player-data." + this.gamePlayer.getUuid() + ".money", this.money);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void withdrawMoney(final double amount) {
		if (plugin.getVaultHook().hasEconomy()) {
			plugin.getVaultHook().withdraw(this.gamePlayer.getName(), amount);
			return;
		}
		this.money -= amount;
		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().setMoney(this.gamePlayer.getUuid(), this.money);
			return;
		}
		playerData.set("player-data." + this.gamePlayer.getUuid() + ".money", this.money);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private boolean hasEnoughMoney(final double amount) {
		return getMoney() >= amount;
	}

	public int getGamesPlayed() {
		return (this.wins + this.losses);
	}

	public void setGamesPlayed() {

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().setGamesPlayed(this.gamePlayer.getUuid(), getGamesPlayed());
			return;
		}
		playerData.set("player-data." + this.gamePlayer.getUuid() + ".games-played", getGamesPlayed());
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void addWin() {
		this.wins++;
		final double money = GameValues.REWARDS.COINS_FOR_WIN * GameValues.REWARDS.MULTIPLIER;
		final double uhcExp = GameValues.REWARDS.EXP_FOR_WIN * GameValues.REWARDS.MULTIPLIER;

		this.gamePlayer.addMoneyForGameResult(money);
		this.gamePlayer.addExpForGameResult(uhcExp);

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().addWin(this.gamePlayer.getUuid());
			return;
		}

		playerData.set("player-data." + this.gamePlayer.getUuid() + ".wins", this.wins);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void addLose() {
		this.losses++;
		final double money = GameValues.REWARDS.COINS_FOR_LOSE;
		final double exp = GameValues.REWARDS.EXP_FOR_LOSE;

		this.gamePlayer.addMoneyForGameResult(money);
		this.gamePlayer.addExpForGameResult(exp);

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().addLose(this.gamePlayer.getUuid());
			return;
		}

		playerData.set("player-data." + this.gamePlayer.getUuid() + ".losses", this.losses);
		gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
	}

	private void addKills(final int amount) {
		final double money = GameValues.REWARDS.COINS_FOR_KILL;
		final double exp = GameValues.REWARDS.EXP_FOR_KILL;

		this.gamePlayer.addMoneyForKills(money);
		this.gamePlayer.addExpForKills(exp);

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().addKill(this.gamePlayer.getUuid(), amount);
			return;
		}

		playerData.set("player-data." + this.gamePlayer.getUuid() + ".kills", this.kills + amount);
		gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
	}

	private void setKillstreak(final int amount) {
		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().setKillstreak(this.gamePlayer.getUuid(), amount);
			return;
		}
		playerData.set("player-data." + this.gamePlayer.getUuid() + ".killstreak", amount);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void addAssists(final int amount) {
		final double money = GameValues.REWARDS.COINS_FOR_ASSIST;
		final double exp = GameValues.REWARDS.EXP_FOR_ASSIST;

		this.gamePlayer.addMoneyForAssists(money);
		this.gamePlayer.addExpForAssists(exp);

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().addAssist(this.gamePlayer.getUuid(), amount);
			return;
		}

		playerData.set("player-data." + this.gamePlayer.getUuid() + ".assists", this.assists + amount);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void addDeaths(final int amount) {

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().addDeath(this.gamePlayer.getUuid(), amount);
			return;
		}

		playerData.set("player-data." + this.gamePlayer.getUuid() + ".deaths", this.deaths + amount);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void buyKit(final Kit kit) {
		if (!kit.isFree() && !hasEnoughMoney(kit.getCost())) {
			this.gamePlayer.sendMessage(Messages.NO_MONEY.toString());
			gameManager.getSoundManager().playPurchaseCancelSound(this.gamePlayer.getPlayer());
			return;
		}
		this.boughtKitsList.add(kit);
		withdrawMoney(kit.getCost());
		saveKits();
		final String kitCost = String.valueOf(kit.getCost());
		final String money = String.valueOf(getMoney());
		final String prevMoney = String.valueOf(this.gamePlayer.getPlayerData().getMoney() + kit.getCost());
		this.gamePlayer.sendMessage(Messages.KITS_MONEY_DEDUCT.toString(), "%previous-money%", prevMoney, "%money%", money, "%kit%", kit.getDisplayName(), "%kit-cost%", kitCost);
		this.gamePlayer.setKit(kit);
		gameManager.getSoundManager().playSelectBuySound(this.gamePlayer.getPlayer());
	}

	public boolean hasKitBought(final Kit kit) {
		return this.boughtKitsList.contains(kit);
	}

	private void loadBoughtKits() {
		final List<String> kitsInString;
		if (gameManager.isDatabaseConnected()) {
			kitsInString = gameManager.getData().getBoughtKits(this.gamePlayer.getUuid());
		} else {
			kitsInString = playerData.getStringList("player-data." + this.gamePlayer.getUuid() + ".kits");
		}
		for (final String kitName : kitsInString) {
			final Kit kit = gameManager.getKitsManager().getKit(kitName);
			if (kit != null) this.boughtKitsList.add(kit);
		}
	}

	private void saveKits() {
		final List<String> kitsNameList = this.boughtKitsList.stream().map(Kit::getName).collect(Collectors.toList());

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().setBoughtKits(this.gamePlayer.getUuid(), String.join(", ", kitsNameList));
			Bukkit.getLogger().info("Saved kits: " + kitsNameList);
			return;
		}

		playerData.set("player-data." + this.gamePlayer.getUuid() + ".kits", kitsNameList);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void buyPerk(final Perk perk) {
		if (!perk.isFree() && !hasEnoughMoney(perk.getCost())) {
			this.gamePlayer.sendMessage(Messages.NO_MONEY.toString());
			gameManager.getSoundManager().playPurchaseCancelSound(this.gamePlayer.getPlayer());
			return;
		}
		this.boughtPerksList.add(perk);
		withdrawMoney(perk.getCost());
		savePerks();
		final String perkCost = String.valueOf(perk.getCost());
		final String money = String.valueOf(getMoney());
		final String prevMoney = String.valueOf(this.gamePlayer.getPlayerData().getMoney() + perk.getCost());
		this.gamePlayer.sendMessage(Messages.PERKS_MONEY_DEDUCT.toString().toString(), "%previous-money%", prevMoney, "%money%", money, "%perk%", perk.getDisplayName(), "%perk-cost%", perkCost);
		this.gamePlayer.setPerk(perk);
		gameManager.getSoundManager().playSelectBuySound(this.gamePlayer.getPlayer());
	}

	public boolean hasPerkBought(final Perk perk) {
		return this.boughtPerksList.contains(perk);
	}

	private void loadBoughtPerks() {
		final List<String> boughtPerksList;

		if (gameManager.isDatabaseConnected()) {
			boughtPerksList = gameManager.getData().getBoughtPerks(this.gamePlayer.getUuid());
		} else {
			boughtPerksList = playerData.getStringList("player-data." + this.gamePlayer.getUuid() + ".perks");
		}

		for (final String perkName : boughtPerksList) {
			final Perk perk = gameManager.getPerksManager().getPerk(perkName);
			if (perk != null) this.boughtPerksList.add(perk);
		}
	}

	private void savePerks() {
		final List<String> perksNameList = this.boughtPerksList.stream().map(Perk::getName).collect(Collectors.toList());

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().setBoughtPerks(this.gamePlayer.getUuid(), perksNameList.toString().replace("[", "").replace("]", ""));
			return;
		}

		playerData.set("player-data." + this.gamePlayer.getUuid() + ".perks", perksNameList);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	/*public long getTimePlayed() {
		return timePlayed;
	}

	public void addTimePlayed() {
		playerData.set("player-data." + uhcPlayer.getUuid() + ".time-played", getTimePlayed() + uhcPlayer.getTimePlayed());
		gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
	}*/

	/* UHC Level System */
	public void addExp(final double amount) {
		this.exp += amount;
		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().addExp(this.gamePlayer.getUuid(), amount);
		} else {
			playerData.set("player-data." + this.gamePlayer.getUuid() + ".exp", this.exp);
			gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}
		checkLevelUp();
	}

	private void checkLevelUp() {
		while (this.exp >= this.requiredExp) {
			increaseUHCLevel();
		}
	}

	private void increaseUHCLevel() {
		this.exp -= this.requiredExp;
		final int previousLevel = this.level;
		this.level++;
		final int newLevel = this.level;
		this.requiredExp = setRequiredExp();

		if (gameManager.isDatabaseConnected()) {
			gameManager.getData().setExp(this.gamePlayer.getUuid(), this.exp);
			gameManager.getData().addLevel(this.gamePlayer.getUuid());
			gameManager.getData().setRequiredExp(this.gamePlayer.getUuid(), this.requiredExp);
		} else {
			playerData.set("player-data." + this.gamePlayer.getUuid() + ".exp", this.exp);
			playerData.set("player-data." + this.gamePlayer.getUuid() + ".level", this.level);
			playerData.set("player-data." + this.gamePlayer.getUuid() + ".required-exp", this.requiredExp);
			gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}

		final double reward = GameValues.REWARDS.BASE_REWARD * GameValues.REWARDS.REWARD_COEFFICIENT * this.level;
		depositMoney(reward);
		if (this.gamePlayer.getPlayer() != null) {
			gameManager.getSoundManager().playLevelUpSound(this.gamePlayer.getPlayer());
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.LEVEL_UP.toString()
					.replace("%previous-level%", String.valueOf(previousLevel))
					.replace("%new-level%", String.valueOf(newLevel))));
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.REWARDS_LEVEL_UP.toString().replace("%money%", String.valueOf(reward))));
		}
	}

	private double setRequiredExp() {
		return this.requiredExp * GameValues.STATISTICS.EXP_MULTIPLIER;
	}

	/* Statistics */
	private void addGameResult() {
		if (this.gamePlayer.isWinner()) addWin();
		else addLose();
	}

	public void saveStatistics() {
		addGameResult();
		if (this.gamePlayer.getKills() > 0) addKills(this.gamePlayer.getKills());
		if (this.gamePlayer.getAssists() > 0) addAssists(this.gamePlayer.getAssists());
		if (this.gamePlayer.isDead()) addDeaths(1);

		if (this.gamePlayer.getKills() > this.killstreak) {
			setKillstreak(this.gamePlayer.getKills());
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.KILLSTREAK_NEW.toString()));
		}

		double money = this.gamePlayer.getMoneyForGameResult() + this.gamePlayer.getMoneyForKills() + this.gamePlayer.getMoneyForAssists() + this.gamePlayer.getMoneyForActivity();
		double uhcExp = this.gamePlayer.getExpForGameResult() + this.gamePlayer.getExpForKills() + this.gamePlayer.getExpForAssists() + this.gamePlayer.getExpForActivity();
		money *= GameValues.REWARDS.MULTIPLIER;
		uhcExp *= GameValues.REWARDS.MULTIPLIER;

		setGamesPlayed();
		depositMoney(money);
		addExp(uhcExp);
		gameManager.getPlayerQuestDataManager().getPlayerData(this.gamePlayer).savePlayerQuestData();
	}

	public void showStatistics() {
		final List<String> rewards = this.gamePlayer.isWinner() ? Messages.REWARDS_WIN.toList() : Messages.REWARDS_LOSE.toList();

		for (String message : rewards) {
			message = PAPI.setPlaceholders(this.gamePlayer.getPlayer(), message);
			this.gamePlayer.sendMessage(TextUtils.color(message));
		}
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

}
