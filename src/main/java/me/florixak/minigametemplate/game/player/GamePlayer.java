package me.florixak.minigametemplate.game.player;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.NMSUtils;
import me.florixak.minigametemplate.utils.TeleportUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;
import java.util.UUID;

public class GamePlayer {

	private final UUID uuid;
	private final String name;
	private final PlayerData data;
	private final PlayerQuestData questData;

	private PlayerState state;
	private int kills = 0;
	private int assists = 0;
	private Kit kit;
	private Perk perk;
	private GameTeam team;
	private boolean hasWon = false;
	private long timePlayed = 0;
	private Location spawnLoc;

	private double moneyForGameResult = 0, moneyForKills = 0, moneyForAssists = 0, moneyForActivity = 0;
	private double expForGameResult = 0, expForKills = 0, expForAssists = 0, expForActivity = 0;

	public GamePlayer(final UUID uuid, final String name) {
		this.uuid = uuid;
		this.name = name;
		this.data = new PlayerData(this);
		this.questData = new PlayerQuestData(this);
		setState(PlayerState.LOBBY);
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.uuid);
	}

	public String getName() {
		if (Bukkit.getPlayer(this.name) == null) {
			return getData().getName();
		}
		return this.name;
	}

	public boolean isOnline() {
		final Player player = Bukkit.getPlayer(this.uuid);
		return player != null;
	}

	public void setState(final PlayerState state) {
		if (state == this.state) return;
		this.state = state;
	}

	public PlayerState getState() {
		return this.state;
	}

	public PlayerData getData() {
		return this.data;
	}

	public PlayerQuestData getQuestData() {
		return this.questData;
	}

	public void setWinner(final boolean win) {
		if (this.hasWon == win) return;
		this.hasWon = win;

		if (getQuestData().hasQuestWithTypeOf("WIN")) {
			getQuestData().addProgressToTypes("WIN", getPlayer().getInventory().getItemInHand().getType());
		}
	}

	public boolean isWinner() {
		return this.hasWon;
	}

	public boolean isAlive() {
		return getState() == PlayerState.ALIVE;
	}

	public boolean isDead() {
		return getState() == PlayerState.DEAD;
	}

	public boolean isSpectator() {
		return getState() == PlayerState.SPECTATOR || getState() == PlayerState.DEAD;
	}

	public void setTeam(final GameTeam team) {
		this.team = team;
	}

	public GameTeam getTeam() {
		return this.team;
	}

	public boolean hasTeam() {
		return getTeam() != null;
	}

	public int getKills() {
		return this.kills;
	}

	public void addKill() {
		this.kills++;
	}

	public int getAssists() {
		return this.assists;
	}

	public void addAssist() {
		this.assists++;
	}

	public boolean hasKit() {
		return this.kit != null;
	}

	public Kit getKit() {
		return this.kit;
	}

	public void setKit(final Kit kit) {
		if (this.kit != kit) {
			this.kit = kit;
			sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", kit.getDisplayName()));
			GameManager.getGameManager().getSoundManager().playSelectBuySound(getPlayer());
		}
	}

	public boolean hasPerk() {
		return this.perk != null;
	}

	public Perk getPerk() {
		return this.perk;
	}

	public void setPerk(final Perk perk) {
		if (this.perk != perk) {
			this.perk = perk;
			sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", perk.getDisplayName()));
			GameManager.getGameManager().getSoundManager().playSelectBuySound(getPlayer());
		}
	}

	public void setSpawnLocation(final Location spawnLoc) {
		this.spawnLoc = spawnLoc;
	}

	public Location getSpawnLocation() {
		return this.spawnLoc;
	}

	public long getTimePlayed() {
		return this.timePlayed;
	}

	public void addTimePlayed(final long time) {
		this.timePlayed += time;
	}

	public void revive() {
		if (GameValues.TEAM.TEAM_MODE && !hasTeam()) {
			if (GameManager.getGameManager().getTeamManager().getFreeTeams().isEmpty()) {
				sendMessage(Messages.TEAM_NO_FREE.toString());
				return;
			}
			GameManager.getGameManager().getTeamManager().joinRandomTeam(this);
		}

		setState(PlayerState.ALIVE);
		setGameMode(GameMode.SURVIVAL);
		teleport(TeleportUtils.getSafeLocation());

		getPlayer().setHealth(getPlayer().getMaxHealth());
		getPlayer().setFoodLevel(20);
		getPlayer().setExhaustion(0);
		getPlayer().setFireTicks(0);
		clearPotions();
		clearInventory();
		//if (kit != null) getKit().giveKit(this);
	}

	public void kill(final GamePlayer victim) {
		if (victim == null) return;

		addKill();
		getPlayer().giveExp((int) GameValues.REWARDS.EXP_FOR_KILL);
		if (hasPerk()) {
			getPerk().givePerk(this);
		}
		sendMessage(Messages.REWARDS_KILL.toString()
				.replace("%player%", victim.getName())
				.replace("%money%", String.valueOf(GameValues.REWARDS.COINS_FOR_KILL))
				.replace("%uhc-exp%", String.valueOf(GameValues.REWARDS.EXP_FOR_KILL)));
		GameManager.getGameManager().getSoundManager().playKillSound(getPlayer());

		if (getQuestData().hasQuestWithTypeOf("KILL")) {
			getQuestData().addProgressToTypes("KILL", getPlayer().getInventory().getItemInHand().getType());
		}
	}

	public void assist(final GamePlayer victim) {
		if (victim == null) return;

		addAssist();
		sendMessage(Messages.REWARDS_ASSIST.toString()
				.replace("%player%", victim.getName())
				.replace("%money%", String.valueOf(GameValues.REWARDS.COINS_FOR_ASSIST))
				.replace("%uhc-exp%", String.valueOf(GameValues.REWARDS.EXP_FOR_ASSIST)));
		GameManager.getGameManager().getSoundManager().playAssistSound(getPlayer());

		if (getQuestData().hasQuestWithTypeOf("ASSIST")) {
			getQuestData().addProgressToTypes("ASSIST", getPlayer().getInventory().getItemInHand().getType());
		}
	}

	public void die() {
		setState(PlayerState.DEAD);

		getPlayer().spigot().respawn();

		getPlayer().setHealth(getPlayer().getMaxHealth());
		getPlayer().setFoodLevel(20);
		getPlayer().setExhaustion(0);
		getPlayer().setFireTicks(0);
		clearPotions();
		clearInventory();

		setSpectator();
		GameManager.getGameManager().getSoundManager().playDeathSound(getPlayer());
	}

	public void setSpectator() {
		if (this.state != PlayerState.DEAD) {
			setState(PlayerState.SPECTATOR);
		}
		setGameMode(GameMode.SPECTATOR);
		teleport(new Location(Bukkit.getWorld(GameValues.WORLD_NAME), 0, 100, 0));
	}

	public void addMoneyForGameResult(final double money) {
		this.moneyForGameResult += money;
	}

	public void addMoneyForKills(final double money) {
		this.moneyForKills += money;
	}

	public void addMoneyForAssists(final double money) {
		this.moneyForAssists += money;
	}

	public void addMoneyForActivity(final double money) {
		this.moneyForActivity += money;
	}

	public void addExpForGameResult(final double exp) {
		this.expForGameResult += exp;
	}

	public void addExpForKills(final double exp) {
		this.expForKills += exp;
	}

	public void addExpForAssists(final double exp) {
		this.expForAssists += exp;
	}

	public void addExpForActivity(final double exp) {
		this.expForActivity += exp;
	}

	public double getMoneyForGameResult() {
		return this.moneyForGameResult;
	}

	public double getMoneyForKills() {
		return this.moneyForKills;
	}

	public double getMoneyForAssists() {
		return this.moneyForAssists;
	}

	public double getMoneyForActivity() {
		return this.moneyForActivity;
	}

	public double getExpForGameResult() {
		return this.expForGameResult;
	}

	public double getExpForKills() {
		return this.expForKills;
	}

	public double getExpForAssists() {
		return this.expForAssists;
	}

	public double getExpForActivity() {
		return this.expForActivity;
	}

	public boolean hasPermission(final String permission) {
		return getPlayer().hasPermission(permission);
	}

	public void teleport(final Location loc) {
		if (loc == null) return;
		getPlayer().teleport(loc);
	}

	public void clearInventory() {
		getPlayer().getInventory().clear();

		//clear player armor
		final ItemStack[] emptyArmor = new ItemStack[4];
		for (int i = 0; i < emptyArmor.length; i++) {
			emptyArmor[i] = new ItemStack(Material.AIR);
		}
		getPlayer().getInventory().setArmorContents(emptyArmor);
	}

	public void giveExp(final int exp) {
		getPlayer().giveExp(exp);
	}

	public void addEffect(final XPotion potion, final int duration, final int level) {
		getPlayer().addPotionEffect(Objects.requireNonNull(potion.buildPotionEffect(duration * 20, level), "Cannot create potion from null."));
	}

	public void clearPotions() {
//		getPlayer().getActivePotionEffects().clear();
		for (final PotionEffect effect : getPlayer().getActivePotionEffects()) {
			getPlayer().removePotionEffect(effect.getType());
		}
	}

	public void kick(final String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		getPlayer().kickPlayer(TextUtils.color(message));
	}

	public void setGameMode(final GameMode gameMode) {
		getPlayer().setGameMode(gameMode);
	}

	public void sendMessage(final String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		getPlayer().sendMessage(TextUtils.color(message));
	}

	public void sendMessage(final String message, final String... replacements) {
		if (message == null || message.isEmpty() || !isOnline() || replacements.length % 2 != 0) return;

		String messageToSend = TextUtils.color(message);
		for (int i = 0; i < replacements.length; i += 2) {
			messageToSend = messageToSend.replace(replacements[i], replacements[i + 1]);
		}
		sendMessage(messageToSend);
	}

	@SuppressWarnings("deprecation")
	public ItemStack getPlayerHead(final String playerName) {
		final Material type = XMaterial.matchXMaterial(MinigameTemplate.useOldMethods() ? "SKULL_ITEM" : "PLAYER_HEAD").get().parseMaterial();
		final ItemStack item = new ItemStack(type, 1);

		final SkullMeta meta = (SkullMeta) item.getItemMeta();
		if (playerName != null) meta.setDisplayName(TextUtils.color(playerName));
		if (MinigameTemplate.useOldMethods()) {
			item.setDurability((short) 3);
			meta.setOwner(getPlayer().getName());
		} else meta.setOwningPlayer(getPlayer());

		item.setItemMeta(meta);
		return item;
	}

	public void openInventory(final Inventory inventory) {
		getPlayer().openInventory(inventory);
	}

	public void closeInventory() {
		getPlayer().closeInventory();
	}

	public int getPing() {
		try {
			return Integer.parseInt(getPlayer().getClass().getMethod("getPing").invoke(getPlayer()).toString());
		} catch (final Exception e) {
			return 0;
		}
	}

	public void sendHotBarMessage(final String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		NMSUtils.sendHotBarMessageViaNMS(getPlayer(), TextUtils.color(message));
	}

	public void leaveTeam() {
		if (getTeam() == null) return;
		getTeam().removeMember(this);
	}

	public void reset() {
		this.hasWon = false;
		this.kills = 0;
		this.assists = 0;
		this.kit = null;
		this.perk = null;
		if (hasTeam()) getTeam().removeMember(this);
		this.team = null;
		this.spawnLoc = null;
		this.timePlayed = 0;
		this.moneyForGameResult = 0;
		this.moneyForKills = 0;
		this.moneyForAssists = 0;
		this.moneyForActivity = 0;
		this.expForGameResult = 0;
		this.expForKills = 0;
		this.expForAssists = 0;
		this.expForActivity = 0;
		getQuestData().getCompletedQuests().clear();
		getQuestData().getQuestProgress().clear();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GamePlayer && ((GamePlayer) obj).getUUID().equals(this.getUUID());
	}
}