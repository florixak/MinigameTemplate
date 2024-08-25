package me.florixak.minigametemplate.listeners;

import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.GuiManager;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.menu.inGame.*;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.managers.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaListener implements Listener {

	private final GameManager gameManager;
	private final PlayerManager playerManager;
	private final GuiManager guiManager;

	public ArenaListener(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.playerManager = gameManager.getPlayerManager();
		this.guiManager = gameManager.getGuiManager();
	}

	/*@EventHandler
	public void handleGameEnd(final GameEndEvent event) {

		final String winner = event.getWinner();
		final List<String> gameResults = Messages.GAME_RESULTS.toList();
		final List<GamePlayer> topKillers = this.playerManager.getTopKillers();
		final List<String> commands = this.config.getStringList("settings.end-game-commands");

		// Game results and top killers
		if (!gameResults.isEmpty()) {
			for (String message : gameResults) {
				for (int i = 0; i < gameResults.size(); i++) {
					final UHCPlayer topKiller = i < topKillers.size() && topKillers.get(i) != null ? topKillers.get(i) : null;
					final boolean isUHCPlayer = topKiller != null;
					message = message.replace("%winner%", winner)
							.replace("%top-killer-" + (i + 1) + "%", isUHCPlayer ? topKiller.getName() : "None")
							.replace("%top-killer-" + (i + 1) + "-kills%", isUHCPlayer ? String.valueOf(topKiller.getKills()) : "0")
							.replace("%top-killer-" + (i + 1) + "-team%", isUHCPlayer && GameValues.TEAM.TEAM_MODE ? topKiller.getTeam() != null ? topKiller.getTeam().getDisplayName() : "" : "")
							.replace("%top-killer-" + (i + 1) + "-uhc-level%", isUHCPlayer ? String.valueOf(topKiller.getData().getUHCLevel()) : "0");
				}
				message = message.replace("%prefix%", Messages.PREFIX.toString());

				Utils.broadcast(TextUtils.color(message));
			}
		}

		// Statistics
		for (final UHCPlayer player : this.playerManager.getPlayers()) {

			player.getData().saveStatistics();
			if (player.getPlayer() == null) continue;
			player.clearInventory();
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(this.gameManager.getLobbyManager().getEndingLobbyLocation());

			player.getData().showStatistics();
			if (GameValues.TITLE.ENABLED) {
				final int fadeIn = GameValues.TITLE.FADE_IN * 20;
				final int stay = GameValues.TITLE.STAY * 20;
				final int fadeOut = GameValues.TITLE.FADE_OUT * 20;
				if (player.isWinner()) {
					this.gameManager.getSoundManager().playWinSound(player.getPlayer());
					UHCRevamp.getInstance().getVersionUtils().sendTitle(player.getPlayer(), Messages.TITLE_WIN.toString(), Messages.SUBTITLE_WIN.toString(), fadeIn, stay, fadeOut);
				} else {
					this.gameManager.getSoundManager().playGameOverSound(player.getPlayer());
					UHCRevamp.getInstance().getVersionUtils().sendTitle(player.getPlayer(), Messages.TITLE_LOSE.toString(), Messages.SUBTITLE_LOSE.toString(), fadeIn, stay, fadeOut);
				}
			}
		}

		// End game commands
		if (!commands.isEmpty()) {
			for (final String command : commands) {
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
			}
		}
	}*/

	/*@EventHandler
	public void handleGameKill(final GameKillEvent event) {
		final UHCPlayer killer = event.getKiller();
		final UHCPlayer victim = event.getVictim();

		victim.die();

		if (GameValues.TEAM.TEAM_MODE && !victim.getTeam().isAlive()) {
			Utils.broadcast(Messages.TEAM_DEFEATED.toString()
					.replace("%team%", victim.getTeam().getDisplayName()));
		}

		if (killer == null) {
//			Bukkit.getLogger().info("Killer is null");
			Utils.broadcast(PlaceholderUtil.setPlaceholders(Messages.DEATH.toString(), victim.getPlayer()));
			if (this.gameManager.getDamageTrackerManager().isInTracker(victim)) {
				final UHCPlayer attacker = this.gameManager.getDamageTrackerManager().getAttacker(victim);
				this.gameManager.getDamageTrackerManager().onDead(victim);
				attacker.kill(victim);
			}
			return;
		}

		final UHCPlayer attacker = this.gameManager.getDamageTrackerManager().getAttacker(victim);
		final UHCPlayer assistant = this.gameManager.getDamageTrackerManager().getAssistant(victim);

		if (assistant != null) {
			assistant.assist(victim);
		}
		attacker.kill(victim);
		this.gameManager.getDamageTrackerManager().onDead(victim);

		Utils.broadcast(Messages.KILL.toString()
				.replace("%player%", victim.getName())
				.replace("%killer%", killer.getName()));
	}*/

	@EventHandler
	public void onItemClick(final PlayerInteractEvent event) {

		final Player p = event.getPlayer();
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(p.getUniqueId());

		if (!this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) return;
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);

		if (arena.isPlaying()) return;
		final ItemStack item = gamePlayer.getInventory().getItemInHand();

		if (item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			return;

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final MenuUtils menuUtils = this.gameManager.getMenuManager().getMenuUtils(gamePlayer);

			if (item.equals(this.guiManager.getGui(GuiType.TEAMS_SELECTOR.getKey()).getDisplayItem())) {
				new TeamSelectorMenu(menuUtils).open();
			} else if (item.equals(this.guiManager.getGui(GuiType.KITS_SELECTOR.getKey()).getDisplayItem())) {
				new KitsSelectorMenu(menuUtils).open();
			} else if (item.equals(this.guiManager.getGui(GuiType.PERKS_SELECTOR.getKey()).getDisplayItem())) {
				new PerksSelectorMenu(menuUtils).open();
			} else if (item.equals(this.guiManager.getGui(GuiType.COSMETICS_SELECTOR.getKey()).getDisplayItem())) {
//				new CosmeticsSelectorMenu(menuUtils).open();
				Bukkit.getLogger().info("Opened Cosmetics Menu for " + p.getName());
			} else if (item.equals(this.guiManager.getGui(GuiType.QUESTS.getKey()).getDisplayItem())) {
				new QuestsMenu(menuUtils).open();
			} else if (item.equals(this.guiManager.getGui(GuiType.LEAVE_CONFIRM.getKey()).getDisplayItem())) {
				new LeaveConfirmMenu(menuUtils).open();
			}
		}
	}

	@EventHandler
	public void handleBlockBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final GamePlayer gamePlayer = this.playerManager.getGamePlayer(player.getUniqueId());
		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			if (!arena.isPlaying()) {
				event.setCancelled(true);
				return;
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleBlockPlace(final BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		final GamePlayer gamePlayer = this.playerManager.getGamePlayer(player.getUniqueId());
		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			if (!arena.isPlaying()) {
				event.setCancelled(true);
				return;
			}
		} else {
			event.setCancelled(true);

		}
	}

	@EventHandler
	public void handleWeatherChange(final WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void handleHunger(final FoodLevelChangeEvent event) {
		final Player p = (Player) event.getEntity();
		final GamePlayer gamePlayer = this.playerManager.getGamePlayer(p.getUniqueId());
		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			if (!arena.isPlaying()) {
				event.setCancelled(true);
				return;
			} else {
				p.setFoodLevel(20);
				p.setExhaustion(0);
			}
		} else {
			event.setCancelled(true);
		}
	}

	/*@EventHandler
	public void onCraftItem(final CraftItemEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final GamePlayer uhcPlayer = this.gameManager.getPlayerManager().getGamePlayer(player.getUniqueId());
		if (uhcPlayer.getPlayerQuestData().hasQuestWithTypeOf("CRAFT")) {
			uhcPlayer.getPlayerQuestData().addProgressToTypes("CRAFT", event.getRecipe().getResult().getType());
		}
	}*/
}
