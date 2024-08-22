package me.florixak.minigametemplate.listeners;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.managers.player.PlayerManager;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class EntityListener implements Listener {

	private final GameManager gameManager;
	private final PlayerManager playerManager;

	public EntityListener(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.playerManager = gameManager.getPlayerManager();
	}

	@EventHandler
	public void handleDamage(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		final Player player = (Player) event.getEntity();
		final GamePlayer gamePlayer = this.playerManager.getGamePlayer(player.getUniqueId());

		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			if (!arena.isPlaying()) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleEntityHitEntity(final EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) { // if damager not player
			event.setCancelled(true); // disable event
			return;
		}

		final Player damager = (Player) event.getDamager();
		final GamePlayer damagerPlayer = this.playerManager.getGamePlayer(damager.getUniqueId());

		if (!this.gameManager.getArenaManager().isPlayerInArena(damagerPlayer)) { // if damager not in arena
			event.setCancelled(true); // disable event
		} else {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(damagerPlayer);
			if (!arena.isPlaying()) {
				event.setCancelled(true);
				return;
			}

			if (event.getEntity() instanceof Player) {
				final Player entity = (Player) event.getEntity();
				final GamePlayer damagedPlayer = this.playerManager.getGamePlayer(entity.getUniqueId());

				if (damagerPlayer.getTeam() == damagedPlayer.getTeam()) {
					event.setCancelled(true);
					damagerPlayer.sendMessage(Messages.TEAM_NO_FRIENDLY_FIRE.toString());
					return;
				}

				if (this.gameManager.getDamageTrackerManager().isInTracker(damagedPlayer)) { // if in tracker
					if (damagerPlayer.equals(this.gameManager.getDamageTrackerManager().getAttacker(damagedPlayer))) { // if attacker
//					uhcPlayerE.sendMessage("Damager is still the same."); // send message
						return;
					}
//				uhcPlayerE.sendMessage("New Damager! You have been damaged by " + uhcPlayerD.getName() + "."); // send message
					final GamePlayer attackerPlayer = this.gameManager.getDamageTrackerManager().getAttacker(damagedPlayer); // get attackerage
					this.gameManager.getDamageTrackerManager().removeFromTracker(damagedPlayer); // remove from tracker
					this.gameManager.getDamageTrackerManager().addTrackerOn(damagedPlayer, damagerPlayer); // add tracker
					if (this.gameManager.getDamageTrackerManager().isInAssist(damagedPlayer)) { // if in assist
						this.gameManager.getDamageTrackerManager().removeFromAssist(damagedPlayer); // remove from assist
					}
//				uhcPlayerE.sendMessage("Old Damager " + attacker.getName() + " is now assistant!");
					this.gameManager.getDamageTrackerManager().addAssist(damagedPlayer, attackerPlayer); // add assist
//				uhcPlayerE.sendMessage("Test of assistant: " + gameManager.getDamageTrackerManager().getAssistant(uhcPlayerE).getName());
				} else {
//				uhcPlayerE.sendMessage("You have been damaged by " + uhcPlayerD.getName() + "."); // send message
					this.gameManager.getDamageTrackerManager().addTrackerOn(damagedPlayer, damagerPlayer); // add tracker
				}
			}
		}
	}

	private void handleProjectileHit(final Player shooter, final Player enemy) {
		shooter.sendMessage(Messages.SHOT_HP.toString().replace("%player%", enemy.getDisplayName()).replace("%hp%", String.valueOf(enemy.getHealth())));
	}

	@EventHandler
	public void handleMonsterTargeting(final EntityTargetEvent event) {
		if (!(event.getTarget() instanceof Player)) return;

		final Player player = (Player) event.getTarget();
		final GamePlayer gamePlayer = this.playerManager.getGamePlayer(player.getUniqueId());

		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			if (!arena.isPlaying()) {
				event.setCancelled(true);
				return;
			}
		} else if (event.getEntity() instanceof Monster || event.getEntity() instanceof Slime) {
			if (GameValues.ARENA.MONSTERS_ATTACK) return;
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleMonsterSpawning(final CreatureSpawnEvent event) {
//		if (!gameManager.isPlaying()) {
//			event.setCancelled(true);
//			return;
//		}
		if (event.getEntity() instanceof Monster || event.getEntity() instanceof Slime) {
			if (GameValues.ARENA.SPAWN_MONSTERS) return;
			if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void handleExplode(final EntityExplodeEvent event) {
//		if (!gameManager.isPlaying()) {
//			event.setCancelled(true);
//			return;
//		}
		if (GameValues.ARENA.EXPLOSIONS_DISABLED) event.setCancelled(true);
	}
}