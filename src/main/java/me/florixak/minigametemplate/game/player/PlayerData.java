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

	private final MinigameTemplate plugin = MinigameTemplate.getInstance();
	private final GameManager gameManager = GameManager.getGameManager();
	private final GamePlayer gamePlayer;
	private final FileConfiguration playerData;

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
		this.playerData = this.gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
		initializeData();
	}

	public void initializeData() {
		if (!hasData()) {
			setInitialData();
			return;
		}
		if (this.gameManager.isDatabaseConnected()) loadDataFromDatabase();
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

		this.playerData.set(path + ".name", this.gamePlayer.getName());
		this.playerData.set(path + ".money", GameValues.STATISTICS.STARTING_MONEY);
		this.playerData.set(path + ".level", GameValues.STATISTICS.FIRST_LEVEL);
		this.playerData.set(path + ".exp", 0);
		this.playerData.set(path + ".required-exp", GameValues.STATISTICS.FIRST_REQUIRED_EXP);
		this.playerData.set(path + ".games-played", 0);
		this.playerData.set(path + ".wins", 0);
		this.playerData.set(path + ".losses", 0);
		this.playerData.set(path + ".kills", 0);
		this.playerData.set(path + ".killstreak", 0);
		this.playerData.set(path + ".assists", 0);
		this.playerData.set(path + ".deaths", 0);
		this.playerData.set(path + ".kits", new ArrayList<>());
		this.playerData.set(path + ".perks", new ArrayList<>());
//        playerData.set(path + ".time-played", 0);

		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void loadDataFromDatabase() {
		this.playerName = this.gameManager.getData().getName(this.gamePlayer.getUuid());
		this.money = this.gameManager.getData().getMoney(this.gamePlayer.getUuid());
		this.level = this.gameManager.getData().getLevel(this.gamePlayer.getUuid());
		this.exp = this.gameManager.getData().getExp(this.gamePlayer.getUuid());
		this.requiredExp = this.gameManager.getData().getRequiredExp(this.gamePlayer.getUuid());
		this.wins = this.gameManager.getData().getWins(this.gamePlayer.getUuid());
		this.losses = this.gameManager.getData().getLosses(this.gamePlayer.getUuid());
		this.kills = this.gameManager.getData().getKills(this.gamePlayer.getUuid());
		this.killstreak = this.gameManager.getData().getKillstreak(this.gamePlayer.getUuid());
		this.assists = this.gameManager.getData().getAssists(this.gamePlayer.getUuid());
		this.deaths = this.gameManager.getData().getDeaths(this.gamePlayer.getUuid());
	}

	private void loadDataFromConfig() {
		final String path = "player-data." + this.gamePlayer.getUuid();
		this.playerName = this.playerData.getString(path + ".name");
		this.money = this.playerData.getDouble(path + ".money", GameValues.STATISTICS.STARTING_MONEY);
		this.level = this.playerData.getInt(path + ".level", GameValues.ERROR_INT_VALUE);
		this.exp = this.playerData.getDouble(path + ".exp", GameValues.ERROR_INT_VALUE);
		this.requiredExp = this.playerData.getDouble(path + ".required-exp", GameValues.ERROR_INT_VALUE);
		this.wins = this.playerData.getInt(path + ".wins", GameValues.ERROR_INT_VALUE);
		this.losses = this.playerData.getInt(path + ".losses", GameValues.ERROR_INT_VALUE);
		this.kills = this.playerData.getInt(path + ".kills", GameValues.ERROR_INT_VALUE);
		this.killstreak = this.playerData.getInt(path + ".killstreak", GameValues.ERROR_INT_VALUE);
		this.assists = this.playerData.getInt(path + ".assists", GameValues.ERROR_INT_VALUE);
		this.deaths = this.playerData.getInt(path + ".deaths", GameValues.ERROR_INT_VALUE);
	}

	private boolean hasData() {
		if (this.gameManager.isDatabaseConnected()) {
			return this.gameManager.getData().exists(this.gamePlayer.getUuid());
		}
		return this.playerData.getConfigurationSection("player-data." + this.gamePlayer.getUuid()) != null;
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
		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setMoney(this.gamePlayer.getUuid(), this.money);
			return;
		}
		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".money", this.money);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void withdrawMoney(final double amount) {
		if (this.plugin.getVaultHook().hasEconomy()) {
			this.plugin.getVaultHook().withdraw(this.gamePlayer.getName(), amount);
			return;
		}
		this.money -= amount;
		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setMoney(this.gamePlayer.getUuid(), this.money);
			return;
		}
		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".money", this.money);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private boolean hasEnoughMoney(final double amount) {
		return getMoney() >= amount;
	}

	public int getGamesPlayed() {
		return (this.wins + this.losses);
	}

	public void setGamesPlayed() {

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setGamesPlayed(this.gamePlayer.getUuid(), getGamesPlayed());
			return;
		}
		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".games-played", getGamesPlayed());
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void addWin() {
		this.wins++;
		final double money = GameValues.REWARDS.COINS_FOR_WIN * GameValues.REWARDS.MULTIPLIER;
		final double uhcExp = GameValues.REWARDS.EXP_FOR_WIN * GameValues.REWARDS.MULTIPLIER;

		this.gamePlayer.addMoneyForGameResult(money);
		this.gamePlayer.addExpForGameResult(uhcExp);

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().addWin(this.gamePlayer.getUuid());
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".wins", this.wins);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void addLose() {
		this.losses++;
		final double money = GameValues.REWARDS.COINS_FOR_LOSE;
		final double exp = GameValues.REWARDS.EXP_FOR_LOSE;

		this.gamePlayer.addMoneyForGameResult(money);
		this.gamePlayer.addExpForGameResult(exp);

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().addLose(this.gamePlayer.getUuid());
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".losses", this.losses);
		this.gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
	}

	private void addKills(final int amount) {
		final double money = GameValues.REWARDS.COINS_FOR_KILL;
		final double exp = GameValues.REWARDS.EXP_FOR_KILL;

		this.gamePlayer.addMoneyForKills(money);
		this.gamePlayer.addExpForKills(exp);

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().addKill(this.gamePlayer.getUuid(), amount);
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".kills", this.kills + amount);
		this.gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
	}

	private void setKillstreak(final int amount) {
		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setKillstreak(this.gamePlayer.getUuid(), amount);
			return;
		}
		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".killstreak", amount);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void addAssists(final int amount) {
		final double money = GameValues.REWARDS.COINS_FOR_ASSIST;
		final double exp = GameValues.REWARDS.EXP_FOR_ASSIST;

		this.gamePlayer.addMoneyForAssists(money);
		this.gamePlayer.addExpForAssists(exp);

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().addAssist(this.gamePlayer.getUuid(), amount);
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".assists", this.assists + amount);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void addDeaths(final int amount) {

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().addDeath(this.gamePlayer.getUuid(), amount);
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".deaths", this.deaths + amount);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void buyKit(final Kit kit) {
		if (!kit.isFree() && !hasEnoughMoney(kit.getCost())) {
			this.gamePlayer.sendMessage(Messages.NO_MONEY.toString());
			this.gameManager.getSoundManager().playPurchaseCancelSound(this.gamePlayer.getPlayer());
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
		this.gameManager.getSoundManager().playSelectBuySound(this.gamePlayer.getPlayer());
	}

	public boolean hasKitBought(final Kit kit) {
		return this.boughtKitsList.contains(kit);
	}

	private void loadBoughtKits() {
		final List<String> kitsInString;
		if (this.gameManager.isDatabaseConnected()) {
			kitsInString = this.gameManager.getData().getBoughtKits(this.gamePlayer.getUuid());
		} else {
			kitsInString = this.playerData.getStringList("player-data." + this.gamePlayer.getUuid() + ".kits");
		}
		for (final String kitName : kitsInString) {
			final Kit kit = this.gameManager.getKitsManager().getKit(kitName);
			if (kit != null) this.boughtKitsList.add(kit);
		}
	}

	private void saveKits() {
		final List<String> kitsNameList = this.boughtKitsList.stream().map(Kit::getName).collect(Collectors.toList());

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setBoughtKits(this.gamePlayer.getUuid(), String.join(", ", kitsNameList));
			Bukkit.getLogger().info("Saved kits: " + kitsNameList);
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".kits", kitsNameList);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void buyPerk(final Perk perk) {
		if (!perk.isFree() && !hasEnoughMoney(perk.getCost())) {
			this.gamePlayer.sendMessage(Messages.NO_MONEY.toString());
			this.gameManager.getSoundManager().playPurchaseCancelSound(this.gamePlayer.getPlayer());
			return;
		}
		this.boughtPerksList.add(perk);
		withdrawMoney(perk.getCost());
		savePerks();
		final String perkCost = String.valueOf(perk.getCost());
		final String money = String.valueOf(getMoney());
		final String prevMoney = String.valueOf(this.gamePlayer.getData().getMoney() + perk.getCost());
		this.gamePlayer.sendMessage(Messages.PERKS_MONEY_DEDUCT.toString().toString(), "%previous-money%", prevMoney, "%money%", money, "%perk%", perk.getDisplayName(), "%perk-cost%", perkCost);
		this.gamePlayer.setPerk(perk);
		this.gameManager.getSoundManager().playSelectBuySound(this.gamePlayer.getPlayer());
	}

	public boolean hasPerkBought(final Perk perk) {
		return this.boughtPerksList.contains(perk);
	}

	private void loadBoughtPerks() {
		final List<String> boughtPerksList;

		if (this.gameManager.isDatabaseConnected()) {
			boughtPerksList = this.gameManager.getData().getBoughtPerks(this.gamePlayer.getUuid());
		} else {
			boughtPerksList = this.playerData.getStringList("player-data." + this.gamePlayer.getUuid() + ".perks");
		}

		for (final String perkName : boughtPerksList) {
			final Perk perk = this.gameManager.getPerksManager().getPerk(perkName);
			if (perk != null) this.boughtPerksList.add(perk);
		}
	}

	private void savePerks() {
		final List<String> perksNameList = this.boughtPerksList.stream().map(Perk::getName).collect(Collectors.toList());

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setBoughtPerks(this.gamePlayer.getUuid(), perksNameList.toString().replace("[", "").replace("]", ""));
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".perks", perksNameList);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
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
		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().addExp(this.gamePlayer.getUuid(), amount);
		} else {
			this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".exp", this.exp);
			this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
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

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setExp(this.gamePlayer.getUuid(), this.exp);
			this.gameManager.getData().addLevel(this.gamePlayer.getUuid());
			this.gameManager.getData().setRequiredExp(this.gamePlayer.getUuid(), this.requiredExp);
		} else {
			this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".exp", this.exp);
			this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".level", this.level);
			this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".required-exp", this.requiredExp);
			this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}

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
		this.gameManager.getPlayerQuestDataManager().getPlayerData(this.gamePlayer).savePlayerQuestData();
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
