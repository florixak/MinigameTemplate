package me.florixak.minigametemplate.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.player.PlayerArenaData;
import me.florixak.minigametemplate.gui.GuiManager;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.menu.inGame.*;
import me.florixak.minigametemplate.listeners.events.ArenaDeathEvent;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.managers.player.PlayerManager;
import me.florixak.minigametemplate.utils.PAPIUtils;
import org.bukkit.Bukkit;
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

	@EventHandler
	public void handleArenaDeath(final ArenaDeathEvent event) {
		final GamePlayer player = event.getPlayer();
		final GamePlayer killer = event.getKiller();
		final Arena arena = event.getArena();

		final PlayerArenaData playerArenaData = arena.getPlayerArenaData(player);

		arena.die(player, false);

		if (!playerArenaData.getTeam().isAlive() && !arena.isSolo()) {
			arena.broadcast(Messages.TEAM_DEFEATED.toString()
					.replace("%team%", playerArenaData.getTeam().getDisplayName()));
		}

		if (killer == null) {
			arena.broadcast(PAPIUtils.setPlaceholders(player.getPlayer(), arena, Messages.ARENA_DEATH.toString()));
			if (this.gameManager.getDamageTrackerManager().isInTracker(player)) {
				final GamePlayer attacker = this.gameManager.getDamageTrackerManager().getAttacker(player);
				this.gameManager.getDamageTrackerManager().onDead(player);
				arena.getPlayerArenaData(attacker).kill(player);
			}
		} else {
			arena.broadcast(PAPIUtils.setPlaceholders(player.getPlayer(), arena, Messages.ARENA_DEATH.toString())
					.replace("%killer%", killer.getName()));

			final GamePlayer attacker = this.gameManager.getDamageTrackerManager().getAttacker(player);
			arena.getPlayerArenaData(attacker).kill(player);
		}

		final GamePlayer assistant = this.gameManager.getDamageTrackerManager().getAssistant(player);

		if (assistant != null) {
			arena.getPlayerArenaData(assistant).assist(player);
		}
		this.gameManager.getDamageTrackerManager().onDead(player);
	}

	@EventHandler
	public void onItemClick(final PlayerInteractEvent event) {

		final Player p = event.getPlayer();
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(p.getUniqueId());

		if (!gamePlayer.isInArena()) return;
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);

		if (arena.isPlaying()) return;
		final ItemStack item = gamePlayer.getInventory().getItemInHand();

		if (item.getType().equals(XMaterial.AIR.parseMaterial()) || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			return;

		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
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
		if (gamePlayer.isInArena()) {
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
		if (gamePlayer.isInArena()) {
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
		if (gamePlayer.isInArena()) {
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
