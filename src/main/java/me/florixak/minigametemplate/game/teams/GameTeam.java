package me.florixak.minigametemplate.game.teams;

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

public class GameTeam {

	private final ItemStack displayItem;
	private final int durability;
	private final String name;
	private final int maxSize;

	private final String color;

	private final List<GamePlayer> members;

	public GameTeam(final ItemStack displayItem, final int durability, final String name, final String color, final int maxSize) {
		this.displayItem = displayItem;
		this.durability = durability;
		this.name = name;
		this.maxSize = maxSize;
		this.color = color;
		this.members = new ArrayList<>();
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return getColor() + this.name;
	}

	public ItemStack getDisplayItem() {
		return this.displayItem;
	}

	public int getDisplayItemDurability() {
		return this.durability;
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public String getColor() {
		return this.color;
	}

	public GamePlayer getLeader() {
		return getAliveMembers().get(0);
	}

	public boolean isFull() {
		return getMembers().size() >= this.maxSize;
	}

	public boolean isMember(final GamePlayer gamePlayer) {
		return getMembers().contains(gamePlayer);
	}

	public List<GamePlayer> getMembers() {
		return this.members;
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
		return getAliveMembers() != null && getAliveMembers().size() != 0;
	}

	public int getKills() {
		return getMembers().stream().mapToInt(GamePlayer::getKills).sum();
	}

	public void teleport(final Location loc) {
		if (loc == null) return;
		for (final GamePlayer gamePlayer : getAliveMembers()) {
			if (!gamePlayer.isOnline()) return;
			gamePlayer.getPlayer().teleport(loc);
		}
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

}
