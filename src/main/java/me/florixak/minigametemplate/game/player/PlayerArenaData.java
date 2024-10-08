package me.florixak.minigametemplate.game.player;

import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.Data;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.PAPIUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

@Data
public class PlayerArenaData {

	private final GameManager gameManager = GameManager.getInstance();

	private GamePlayer gamePlayer;
	private PlayerData playerData;
	private PlayerQuestData playerQuestData;

	private PlayerState state = PlayerState.LOBBY;

	private UUID uuid;
	private String name;

	private int kills = 0;
	private int assists = 0;
	private Kit kit;
	private Perk perk;
	private GameTeam team;
	private boolean winner = false;
	private long timePlayed = 0;
	private Location spawnLocation;

	private double moneyForGameResult = 0, moneyForKills = 0, moneyForAssists = 0, moneyForActivity = 0;
	private double expForGameResult = 0, expForKills = 0, expForAssists = 0, expForActivity = 0;

	public PlayerArenaData(final GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		this.uuid = gamePlayer.getUuid();
		this.name = gamePlayer.getName();
		this.playerData = gamePlayer.getData();
		this.playerQuestData = gamePlayer.getQuestData();

		Bukkit.getLogger().info("PlayerArenaData created: " + this);
	}

	public void reset() {
		this.winner = false;
		this.kills = 0;
		this.assists = 0;
		this.kit = null;
		this.perk = null;
		if (this.team != null) this.team.removeMember(this.gamePlayer);
		this.team = null;
		this.spawnLocation = null;
		this.timePlayed = 0;
		this.moneyForGameResult = 0;
		this.moneyForKills = 0;
		this.moneyForAssists = 0;
		this.moneyForActivity = 0;
		this.expForGameResult = 0;
		this.expForKills = 0;
		this.expForAssists = 0;
		this.expForActivity = 0;
	}

	public boolean hasTeam() {
		return this.team != null;
	}

	public void addKill() {
		this.kills++;
	}

	public void addAssist() {
		this.assists++;
	}

	public boolean hasKit() {
		return this.kit != null;
	}

	public void setKit(final Kit kit) {
		if (this.kit != kit) {
			this.kit = kit;
			this.gamePlayer.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", kit.getDisplayName()));
			this.gameManager.getSoundManager().playSelectBuySound(this.gamePlayer.getPlayer());
		}
	}

	public boolean hasPerk() {
		return this.perk != null;
	}

	public void setPerk(final Perk perk) {
		if (this.perk != perk) {
			this.perk = perk;
			this.gamePlayer.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", perk.getDisplayName()));
			this.gameManager.getSoundManager().playSelectBuySound(this.gamePlayer.getPlayer());
		}
	}

	public boolean isAlive() {
		return this.state.equals(PlayerState.ALIVE);
	}

	public boolean isDead() {
		return this.state.equals(PlayerState.DEAD);
	}

	public boolean isSpectator() {
		return this.state.equals(PlayerState.SPECTATOR);
	}

	public void setWinner(final boolean winner) {
		this.winner = winner;

		if (this.playerQuestData.hasQuestWithTypeOf("WIN")) {
			this.playerQuestData.addProgressToTypes("WIN", this.gamePlayer.getInventory().getItemInHand().getType());
		}

		if (winner) {
			this.moneyForGameResult = GameValues.REWARDS.COINS_FOR_WIN;
			this.expForGameResult = GameValues.REWARDS.EXP_FOR_WIN;
			this.gamePlayer.sendMessage("%money% and %exp% for winning the game."
					.replace("%money%", String.valueOf(GameValues.REWARDS.COINS_FOR_WIN))
					.replace("%exp%", String.valueOf(GameValues.REWARDS.EXP_FOR_WIN)));
		} else {
			this.moneyForGameResult = GameValues.REWARDS.COINS_FOR_LOSE;
			this.expForGameResult = GameValues.REWARDS.EXP_FOR_LOSE;
			this.gamePlayer.sendMessage("%money% and %exp% for losing the game."
					.replace("%money%", String.valueOf(GameValues.REWARDS.COINS_FOR_LOSE))
					.replace("%exp%", String.valueOf(GameValues.REWARDS.EXP_FOR_LOSE)));
		}
	}

	public void kill(final GamePlayer victim) {
		if (victim == null) return;

		addKill();
		this.gamePlayer.getPlayer().giveExp((int) GameValues.REWARDS.EXP_FOR_KILL);
		if (hasPerk()) {
			getPerk().givePerk(this.gamePlayer);
		}
		this.gamePlayer.sendMessage(Messages.REWARDS_KILL.toString()
				.replace("%player%", victim.getName())
				.replace("%money%", String.valueOf(GameValues.REWARDS.COINS_FOR_KILL))
				.replace("%uhc-exp%", String.valueOf(GameValues.REWARDS.EXP_FOR_KILL)));
		this.gameManager.getSoundManager().playKillSound(this.gamePlayer.getPlayer());

		if (this.playerQuestData.hasQuestWithTypeOf("KILL")) {
			this.playerQuestData.addProgressToTypes("KILL", this.gamePlayer.getInventory().getItemInHand().getType());
		}
	}

	public void assist(final GamePlayer victim) {
		if (victim == null) return;

		addAssist();
		this.gamePlayer.sendMessage(Messages.REWARDS_ASSIST.toString()
				.replace("%player%", victim.getName())
				.replace("%money%", String.valueOf(GameValues.REWARDS.COINS_FOR_ASSIST))
				.replace("%exp%", String.valueOf(GameValues.REWARDS.EXP_FOR_ASSIST)));
		this.gameManager.getSoundManager().playAssistSound(this.gamePlayer.getPlayer());

		if (this.playerQuestData.hasQuestWithTypeOf("ASSIST")) {
			this.playerQuestData.addProgressToTypes("ASSIST", this.gamePlayer.getPlayer().getInventory().getItemInHand().getType());
		}
	}

	/* Statistics */
	public void saveSoloStatistics() {
		this.playerData.addGameResult("solo", this.winner);
		if (this.kills > 0)
			this.playerData.setSoloStat(DataType.SOLO_KILLS, this.playerData.getSoloStat(DataType.SOLO_KILLS) + this.kills);
		if (this.assists > 0)
			this.playerData.setSoloStat(DataType.SOLO_ASSISTS, this.playerData.getSoloStat(DataType.SOLO_ASSISTS) + this.assists);
		if (isDead())
			this.playerData.setSoloStat(DataType.SOLO_DEATHS, this.playerData.getSoloStat(DataType.SOLO_DEATHS) + 1);

		if (this.kills > this.playerData.getIntData(DataType.SOLO_KILLSTREAK, 0)) {
			this.playerData.setSoloStat(DataType.SOLO_KILLSTREAK, this.kills);
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.KILLSTREAK_NEW.toString()));
		}

		saveMoneyAndExp();
		this.playerData.setGamesPlayed("solo");
		this.gamePlayer.getQuestData().savePlayerQuestData();
	}

	public void saveTeamsStatistics() {
		this.playerData.addGameResult("teams", this.winner);
		if (this.kills > 0)
			this.playerData.setTeamsStat(DataType.TEAMS_KILLS, this.playerData.getTeamsStat(DataType.TEAMS_KILLS) + this.kills);
		if (this.assists > 0)
			this.playerData.setTeamsStat(DataType.TEAMS_ASSISTS, this.playerData.getTeamsStat(DataType.TEAMS_ASSISTS) + this.assists);
		if (isDead())
			this.playerData.setTeamsStat(DataType.TEAMS_DEATHS, this.playerData.getTeamsStat(DataType.TEAMS_DEATHS) + 1);

		if (this.kills > this.playerData.getIntData(DataType.TEAMS_KILLSTREAK, 0)) {
			this.playerData.setTeamsStat(DataType.TEAMS_KILLSTREAK, this.kills);
			this.gamePlayer.sendMessage(PAPI.setPlaceholders(this.gamePlayer.getPlayer(), Messages.KILLSTREAK_NEW.toString()));
		}

		saveMoneyAndExp();
		this.playerData.setGamesPlayed("teams");
		this.gamePlayer.getQuestData().savePlayerQuestData();
	}

	private void saveMoneyAndExp() {
		double money = this.moneyForGameResult + this.moneyForKills + this.moneyForAssists + this.moneyForActivity;
		double exp = this.expForGameResult + this.expForKills + this.expForAssists + this.expForActivity;
		money *= GameValues.REWARDS.MULTIPLIER;
		exp *= GameValues.REWARDS.MULTIPLIER;

		this.playerData.depositMoney(money);
		this.playerData.addExp(exp);
	}


	public void showStatistics() {
		final List<String> rewards = this.winner ? Messages.REWARDS_WIN.toList() : Messages.REWARDS_LOSE.toList();

		for (String message : rewards) {
			message = PAPIUtils.setPlaceholders(this.gamePlayer.getPlayer(), this.gamePlayer.getArena(), message);
			this.gamePlayer.sendMessage(TextUtils.color(message));
		}
	}

	public void setState(final PlayerState state) {
		if (this.state.equals(state)) return;
		this.state = state;
	}

	@Override
	public String toString() {
		return "PlayerArenaData(" +
				"uuid=" + this.uuid.toString() +
				", name=" + this.name +
				", state=" + this.state +
				", kills=" + this.kills +
				", assists=" + this.assists +
				", kit=" + this.kit +
				", perk=" + this.perk +
				", team=" + this.team +
				", winner=" + this.winner +
				')';
	}

	@Override
	public int hashCode() {
		return this.uuid.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof PlayerArenaData && ((PlayerArenaData) obj).getUuid().equals(this.uuid);
	}


}
