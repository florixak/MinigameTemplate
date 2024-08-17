package me.florixak.minigametemplate.managers;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.utils.TeleportUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamManager {

	private final FileConfiguration teamsConfig;
	private final List<GameTeam> teamsList;

	public TeamManager(final GameManager gameManager) {

		this.teamsConfig = gameManager.getConfigManager().getFile(ConfigType.TEAMS).getConfig();
		this.teamsList = new ArrayList<>();
	}

	public void loadTeams() {
		if (!GameValues.TEAM.TEAM_MODE) return;

		if (this.teamsConfig.contains("teams") && this.teamsConfig.getConfigurationSection("teams").getKeys(false).isEmpty()) {
			MinigameTemplate.getInstance().getLogger().info("Team file is empty!");
			return;
		}

		for (final String teamName : this.teamsConfig.getConfigurationSection("teams").getKeys(false)) {
			final ItemStack display_item = XMaterial.matchXMaterial(this.teamsConfig.getString("teams." + teamName + ".display-item", "BARRIER")
					.toUpperCase()).get().parseItem();
			final int durability = this.teamsConfig.getInt("teams." + teamName + ".durability");
			final String color = this.teamsConfig.getString("teams." + teamName + ".color");
			final GameTeam team = new GameTeam(display_item, durability, teamName, color, GameValues.TEAM.TEAM_SIZE);
			this.teamsList.add(team);
		}
	}

	public GameTeam getTeam(final String name) {
		return this.teamsList.stream().filter(team -> team.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public List<GameTeam> getTeamsList() {
		return this.teamsList;
	}

	public String getTeamsString() {
		return getTeamsList().stream().map(GameTeam::getDisplayName).collect(Collectors.joining(", "));
	}

	public List<GameTeam> getLivingTeams() {
		return this.teamsList.stream().filter(GameTeam::isAlive).collect(Collectors.toList());
	}

	public void addTeam(final GameTeam team) {
		if (exists(team.getName()) || team == null) return;
		this.teamsList.add(team);
	}

    /*public void removeTeam(String teamName) {
        if (!exists(teamName) || teamName == null) return;

        List<String> teams_list = teamsConfig.getStringList("teams");
        teams_list.remove(teamName);

        UHCTeam team = getTeam(teamName);

        this.teamsList.remove(team);
        teamsConfig.set("teams", teams_list);
        gameManager.getConfigManager().getFile(ConfigType.TEAMS).save();
    }*/

	public void joinRandomTeam(final GamePlayer gamePlayer) {
		if (gamePlayer.hasTeam()) return;
		final GameTeam team = findFreeTeam();
		if (team == null) {
			gamePlayer.setSpectator();
			gamePlayer.sendMessage(Messages.TEAM_NO_FREE.toString());
			return;
		}
		team.addMember(gamePlayer);
	}

	public GameTeam getWinnerTeam() {
		for (final GameTeam team : getLivingTeams()) {
			for (final GamePlayer member : team.getMembers()) {
				if (member.isWinner()) {
					return team;
				}
			}
		}
		return null;
	}

	public void teleportInToGame() {
		for (final GameTeam team : getLivingTeams()) {
			final Location location = TeleportUtils.getSafeLocation();
			for (final GamePlayer member : team.getMembers()) {
				member.setSpawnLocation(location);
			}
			team.teleport(location);
		}
	}

	public void teleportAfterMining() {
		for (final GameTeam team : getLivingTeams()) {
			final Player p = team.getLeader().getPlayer();
			final Location location = p.getLocation();

			final double y = location.getWorld().getHighestBlockYAt(location);
			location.setY(y);

			team.teleport(location);
		}
	}

	public List<GameTeam> getFreeTeams() {
		return this.teamsList.stream().filter(team -> !team.isFull()).collect(Collectors.toList());
	}

	private GameTeam findFreeTeam() {
		final List<GameTeam> freeTeams = getFreeTeams();
		final GameTeam emptyTeam = freeTeams.stream().filter(team -> team.getMembers().isEmpty()).findFirst().orElse(null);
		if (emptyTeam == null) return freeTeams.stream().findFirst().orElse(null);
		return emptyTeam;
	}

	public boolean exists(final String teamName) {
		return this.teamsList.contains(getTeam(teamName));
	}

	public void onDisable() {
		this.teamsList.clear();
	}

}