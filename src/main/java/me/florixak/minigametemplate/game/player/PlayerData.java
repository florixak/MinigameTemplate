package me.florixak.minigametemplate.game.player;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Getter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.cosmetics.Cosmetic;
import me.florixak.minigametemplate.game.gameItems.BuyableItem;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerData {

	private final MinigameTemplate plugin = MinigameTemplate.getInstance();
	private final GameManager gameManager = GameManager.getInstance();
	private final FileConfiguration playerData = this.gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

	private final GamePlayer gamePlayer;
	@Getter
	private String name;
	private double money = GameValues.STATISTICS.STARTING_MONEY;
	@Getter
	private int tokens = GameValues.STATISTICS.STARTING_TOKENS;
	@Getter
	private double exp;
	@Getter
	private int level = GameValues.STATISTICS.STARTING_LEVEL;
	@Getter
	private double requiredExp = GameValues.STATISTICS.STARTING_REQUIRED_EXP;

	private final Map<DataType, Integer> soloStats = new HashMap<>();
	private final Map<DataType, Integer> teamsStats = new HashMap<>();
	private final List<Kit> boughtKitsList = new ArrayList<>();
	private final List<Perk> boughtPerksList = new ArrayList<>();
	private final List<Cosmetic> boughtCosmeticsList = new ArrayList<>();

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
		this.playerData.set(path + ".name", this.gamePlayer.getName());
		this.playerData.set(path + ".money", GameValues.STATISTICS.STARTING_MONEY);
		this.playerData.set(path + ".tokens", GameValues.STATISTICS.STARTING_TOKENS);
		this.playerData.set(path + ".level", GameValues.STATISTICS.STARTING_LEVEL);
		this.playerData.set(path + ".exp", 0);
		this.playerData.set(path + ".required-exp", GameValues.STATISTICS.STARTING_REQUIRED_EXP);

		for (final DataType type : DataType.values()) {
			if (!type.toString().contains("solo") && !type.toString().contains("teams")) continue;
			this.playerData.set(path + "." + type.toString(), 0);
		}
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void loadData() {
		this.name = getStringData(DataType.NAME, this.gamePlayer.getName());
		this.money = getDoubleData(DataType.MONEY, GameValues.STATISTICS.STARTING_MONEY);
		this.tokens = getIntData(DataType.TOKENS, GameValues.STATISTICS.STARTING_TOKENS);
		this.level = getIntData(DataType.LEVEL, GameValues.STATISTICS.STARTING_LEVEL);
		this.exp = getDoubleData(DataType.EXP, 0);
		this.requiredExp = getDoubleData(DataType.REQUIRED_EXP, GameValues.STATISTICS.STARTING_REQUIRED_EXP);

		for (final DataType type : DataType.values()) {
			this.soloStats.put(type, getIntData(type, 0));
			this.teamsStats.put(type, getIntData(type, 0));
		}

		loadBoughtKits();
		loadBoughtPerks();
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

	public void setSoloStat(final DataType type, final int value) {
		this.soloStats.put(type, value);
		saveStat(type, value);
	}

	public void setTeamsStat(final DataType type, final int value) {
		this.teamsStats.put(type, value);
		saveStat(type, value);
	}

	public void saveStat(final DataType type, final Object value) {
		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setStat(this.gamePlayer.getUuid(), type, value);
		} else {
			final String path = "player-data." + this.gamePlayer.getUuid() + "." + type.toString();
			if (value instanceof Integer) {
				this.playerData.set(path, (Integer) value);
			} else if (value instanceof Double) {
				this.playerData.set(path, (Double) value);
			} else if (value instanceof String) {
				this.playerData.set(path, (String) value);
			} else if (value instanceof List) {
				this.playerData.set(path, (List<String>) value);
			} else {
				this.playerData.set(path, value);
			}
			this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}
	}

	public void addGameResult(final String category, final boolean winner) {
		if (category.equalsIgnoreCase("solo")) {
			if (winner)
				setSoloStat(DataType.SOLO_WINS, getSoloStat(DataType.SOLO_WINS) + 1);
			else
				setSoloStat(DataType.SOLO_LOSSES, getSoloStat(DataType.SOLO_LOSSES) + 1);
		} else {
			if (winner)
				setTeamsStat(DataType.TEAMS_WINS, getTeamsStat(DataType.TEAMS_WINS) + 1);
			else
				setTeamsStat(DataType.TEAMS_LOSSES, getTeamsStat(DataType.TEAMS_LOSSES) + 1);
		}
	}

	public void setGamesPlayed(final String category) {
		if (category.equalsIgnoreCase("solo"))
			setSoloStat(DataType.SOLO_GAMES_PLAYED, getSoloStat(DataType.SOLO_WINS) + getSoloStat(DataType.SOLO_LOSSES));
		else
			setTeamsStat(DataType.TEAMS_GAMES_PLAYED, getTeamsStat(DataType.TEAMS_WINS) + getTeamsStat(DataType.TEAMS_LOSSES));
	}

	/* Money & Tokens */
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

	/* Leveling System */
	public void addExp(final double amount) {
		this.exp += amount;
		saveStat(DataType.EXP, this.exp);
		checkLevelUp();
	}

	public void increaseLevel() {
		this.exp -= this.requiredExp;
		final int previousLevel = this.level;
		this.level++;
		final int newLevel = this.level;
		this.requiredExp = setRequiredExp();

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".level", this.level);
		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".required-exp", this.requiredExp);
		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".exp", (int) this.exp);
		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".required-exp", this.requiredExp);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);

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

	public double setRequiredExp() {
		return this.requiredExp * GameValues.STATISTICS.EXP_MULTIPLIER;
	}

	public void checkLevelUp() {
		while (this.exp >= this.requiredExp) {
			increaseLevel();
		}
	}

	/* Kits & Perks */
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

	public void saveKits() {
		final List<String> kitsNameList = this.boughtKitsList.stream().map(Kit::getName).collect(Collectors.toList());

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setBoughtKits(this.gamePlayer.getUuid(), String.join(", ", kitsNameList));
			Bukkit.getLogger().info("Saved kits: " + kitsNameList);
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".kits", kitsNameList);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
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

	public void savePerks() {
		final List<String> perksNameList = this.boughtPerksList.stream().map(Perk::getName).collect(Collectors.toList());

		if (this.gameManager.isDatabaseConnected()) {
			this.gameManager.getData().setBoughtPerks(this.gamePlayer.getUuid(), perksNameList.toString().replace("[", "").replace("]", ""));
			return;
		}

		this.playerData.set("player-data." + this.gamePlayer.getUuid() + ".perks", perksNameList);
		this.gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void buy(final BuyableItem item) {
		if (item == null) return;
		if (hasBought(item)) return;
		if (!hasEnoughMoney(item.getCost())) {
			this.gamePlayer.sendMessage(Messages.NO_MONEY.toString());
			GameManager.getInstance().getSoundManager().playPurchaseCancelSound(this.gamePlayer.getPlayer());
			return;
		}
		if (item instanceof Kit) {
			final Kit kit = (Kit) item;
			this.gamePlayer.sendMessage(Messages.KITS_SHOP_MONEY_DEDUCT.toString().replace("%kit%", kit.getDisplayName()));
			this.boughtKitsList.add(kit);
			saveKits();
			withdrawAndPlaySound(kit.getCost());
		} else if (item instanceof Perk) {
			final Perk perk = (Perk) item;
			this.gamePlayer.sendMessage(Messages.PERKS_SHOP_MONEY_DEDUCT.toString().replace("%perk%", perk.getDisplayName()));
			this.boughtPerksList.add(perk);
			savePerks();
			withdrawAndPlaySound(perk.getCost());
		}
	}

	private void withdrawAndPlaySound(final double amount) {
		withdrawMoney(amount);
		GameManager.getInstance().getSoundManager().playSelectBuySound(this.gamePlayer.getPlayer());
	}

	public boolean hasBought(final BuyableItem item) {
		if (item instanceof Kit) {
			return this.boughtKitsList.contains(item);
		} else if (item instanceof Perk) {
			return this.boughtPerksList.contains(item);
		}
		return false;
	}

	/* Getters & Setters */
	public int getIntData(final DataType type, final int defaultValue) {
		if (this.gameManager.isDatabaseConnected()) {
			return this.gameManager.getData().getInt(this.gamePlayer.getUuid(), type.getDatabasePath());
		}
		return this.playerData.getInt("player-data." + this.gamePlayer.getUuid() + "." + type.toString(), defaultValue);
	}

	private String getStringData(final DataType type, final String defaultValue) {
		if (this.gameManager.isDatabaseConnected()) {
			return this.gameManager.getData().getString(this.gamePlayer.getUuid(), type.getDatabasePath());
		}
		return this.playerData.getString("player-data." + this.gamePlayer.getUuid() + "." + type.toString(), defaultValue);
	}

	private double getDoubleData(final DataType type, final double defaultValue) {
		if (this.gameManager.isDatabaseConnected()) {
			return this.gameManager.getData().getDouble(this.gamePlayer.getUuid(), type.getDatabasePath());
		}
		return this.playerData.getDouble("player-data." + this.gamePlayer.getUuid() + "." + type.toString(), defaultValue);
	}

	@Override
	public boolean equals(final Object o) {
		return this == o || o instanceof PlayerData && this.gamePlayer.getUuid().equals(((PlayerData) o).gamePlayer.getUuid());
	}

	@Override
	public int hashCode() {
		return this.gamePlayer.getUuid().hashCode();
	}

	@Override
	public String toString() {
		return "PlayerData{" +
				"name='" + this.name + '\'' +
				", money=" + this.money +
				", tokens=" + this.tokens +
				", exp=" + this.exp +
				", level=" + this.level +
				", requiredExp=" + this.requiredExp +
				", soloStats=" + this.soloStats +
				", teamsStats=" + this.teamsStats +
				", boughtKitsList=" + this.boughtKitsList +
				", boughtPerksList=" + this.boughtPerksList +
				", boughtCosmeticsList=" + this.boughtCosmeticsList +
				'}';
	}
}