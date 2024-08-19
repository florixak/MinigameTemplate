package me.florixak.minigametemplate.game.teams;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.NMSUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class GameTeam {

	private final ItemStack displayItem;
	private final int durability;
	private final String name;
	private final int size;

	private final List<GamePlayer> members = new ArrayList<>();
	@Setter
	private Location spawnLocation;

	public GameTeam(final String name, final int size, final Location spawnLocation) {
		this.displayItem = XMaterial.MAP.parseItem();
		this.durability = 1;
		this.name = name;
		this.size = size;
		this.spawnLocation = spawnLocation;
	}

	public String getDisplayName() {
		return TextUtils.color(this.name);
	}

	public int getDisplayItemDurability() {
		return this.durability;
	}

	public GamePlayer getLeader() {
		return getAliveMembers().get(0);
	}

	public boolean isFull() {
		return getMembers().size() >= this.size;
	}

	public boolean isMember(final GamePlayer gamePlayer) {
		return getMembers().contains(gamePlayer);
	}

	public String getMembersToString() {
		return getMembers().stream().map(GamePlayer::getName).collect(Collectors.joining(", "));
	}

	private List<GamePlayer> getMembers(final Predicate<GamePlayer> filter) {
		return getMembers().stream().filter(filter).collect(Collectors.toList());
	}

	public List<GamePlayer> getAliveMembers() {
		return getMembers(GamePlayer::isAlive);
	}

	public boolean isAlive() {
		return getAliveMembers() != null && !getAliveMembers().isEmpty();
	}

	public int getKills() {
		return getMembers().stream().mapToInt(GamePlayer::getKills).sum();
	}

	public void setWinners() {
		getMembers().forEach(GamePlayer::setWinner);
	}

	public boolean isWinner() {
		return getMembers().stream().anyMatch(GamePlayer::isWinner);
	}

	public void addMember(final GamePlayer gamePlayer) {

		if (isMember(gamePlayer)) {
			gamePlayer.sendMessage(Messages.TEAM_ALREADY_IN.toString());
			return;
		}

		if (isFull() && !gamePlayer.getPlayer().hasPermission("hoc.*")) {
			gamePlayer.sendMessage(Messages.TEAM_FULL.toString());
			return;
		}
		if (gamePlayer.hasTeam()) {
			gamePlayer.getTeam().removeMember(gamePlayer);
		}

		gamePlayer.setTeam(this);
		this.members.add(gamePlayer);

		gamePlayer.sendMessage(Messages.TEAM_JOIN.toString().replace("%team%", gamePlayer.getTeam().getDisplayName()));
	}

	public void removeMember(final GamePlayer gamePlayer) {
		this.members.remove(gamePlayer);
		gamePlayer.setTeam(null);
	}

	public void sendHotBarMessage(final String message) {
		if (message.isEmpty() || message == null) return;
		getMembers().stream().filter(GamePlayer::isAlive).forEach(gamePlayer -> NMSUtils.sendHotBarMessageViaNMS(gamePlayer.getPlayer(), message));
	}

	public void sendMessage(final String message) {
		if (message.isEmpty() || message == null) return;
		getMembers().stream().filter(GamePlayer::isOnline).forEach(gamePlayer -> gamePlayer.sendMessage(TextUtils.color(message)));
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GameTeam
				&& ((GameTeam) o).getName().equals(this.getName());
	}

	@Override
	public String toString() {
		return "GameTeam(name=" + this.name + ", maxSize=" + this.size + ", members=" + this.members.size() + ")";
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

}
