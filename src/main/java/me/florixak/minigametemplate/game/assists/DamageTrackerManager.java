package me.florixak.minigametemplate.game.assists;

import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.player.GamePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class DamageTrackerManager {

	private static final int ASSIST_COOLDOWN = 180;
	private final Map<GamePlayer, Integer> trackerCooldown = new HashMap<>();
	private final Map<GamePlayer, GamePlayer> trackerMap = new HashMap<>();

	private final Map<GamePlayer, Integer> assistCooldown = new HashMap<>();
	private final Map<GamePlayer, GamePlayer> assistMap = new HashMap<>();

	public DamageTrackerManager() {
	}

	public void addTrackerOn(final GamePlayer victim, final GamePlayer attacker) {
		this.trackerMap.put(victim, attacker);
		this.trackerCooldown.put(victim, ASSIST_COOLDOWN);
//		attacker.sendMessage("Damaged " + victim.getName() + ", tracker is on you.");
//		victim.sendMessage("You have been damaged by " + attacker.getName() + ".");

		runCooldown(victim, attacker, this.trackerCooldown, this.trackerMap);
	}

	public void addAssist(final GamePlayer victim, final GamePlayer assistant) {
		this.assistMap.put(victim, assistant);
		this.assistCooldown.put(victim, ASSIST_COOLDOWN);

		runCooldown(victim, assistant, this.assistCooldown, this.assistMap);
	}

	public void removeFromTracker(final GamePlayer victim) {
		this.trackerMap.remove(victim);
		this.trackerCooldown.remove(victim);
	}

	public void removeFromAssist(final GamePlayer victim) {
		this.assistMap.remove(victim);
		this.assistCooldown.remove(victim);
	}

	public boolean isInTracker(final GamePlayer victim) {
		return this.trackerMap.containsKey(victim);
	}

	public boolean isInAssist(final GamePlayer victim) {
		return this.assistMap.containsKey(victim);
	}

	public GamePlayer getAttacker(final GamePlayer victim) {
		return this.trackerMap.get(victim);
	}

	public GamePlayer getAssistant(final GamePlayer victim) {
		return this.assistMap.get(victim);
	}

	public void onDead(final GamePlayer victim) {
		removeFromTracker(victim);
		removeFromAssist(victim);
	}

	public void onDisable() {
		this.trackerCooldown.clear();
		this.trackerMap.clear();
		this.assistCooldown.clear();
		this.assistMap.clear();
	}

	public void runCooldown(final GamePlayer victim, final GamePlayer attacker, final Map<GamePlayer, Integer> cooldown, final Map<GamePlayer, GamePlayer> map) {
		new BukkitRunnable() {
			@Override
			public void run() {
				synchronized (cooldown) {
					if (DamageTrackerManager.this.assistCooldown.containsKey(victim) && DamageTrackerManager.this.trackerMap.containsKey(victim)) {
						DamageTrackerManager.this.assistCooldown.put(victim, DamageTrackerManager.this.assistCooldown.get(victim) - 1);
						if (DamageTrackerManager.this.assistCooldown.get(victim) <= 0 || !attacker.isOnline()) {
							removeFromTracker(victim);
							cancel();
						}
					} else {
						cancel();
					}
				}
			}
		}.runTaskTimer(MinigameTemplate.getInstance(), 0, 20);
	}
}
