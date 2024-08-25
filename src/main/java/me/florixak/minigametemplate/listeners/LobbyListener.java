package me.florixak.minigametemplate.listeners;

import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.GuiManager;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.menu.inGame.QuestsMenu;
import me.florixak.minigametemplate.gui.menu.lobby.ArenasMenu;
import me.florixak.minigametemplate.gui.menu.lobby.ProfileMenu;
import me.florixak.minigametemplate.gui.menu.shop.ShopMenu;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyListener implements Listener {

	private final GameManager gameManager;
	private final GuiManager guiManager;

	public LobbyListener(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.guiManager = this.gameManager.getGuiManager();
	}

	@EventHandler
	public void onItemClick(final PlayerInteractEvent event) {

		final Player p = event.getPlayer();
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(p.getUniqueId());
		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) return;
		final ItemStack item = gamePlayer.getInventory().getItemInHand();

		if (item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			return;

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final MenuUtils menuUtils = this.gameManager.getMenuManager().getMenuUtils(gamePlayer);

			if (item.equals(this.guiManager.getGui(GuiType.ARENA_SELECTOR.getKey()).getDisplayItem())) {
				new ArenasMenu(menuUtils).open();
				Bukkit.getLogger().info("Opened Arenas Menu for " + p.getName());
			} else if (item.equals(this.guiManager.getGui(GuiType.SHOP.getKey()).getDisplayItem())) {
				new ShopMenu(menuUtils).open();
				Bukkit.getLogger().info("Opened Kits Menu for " + p.getName());
			} else if (item.equals(this.guiManager.getGui(GuiType.PROFILE.getKey()).getDisplayItem())) {
				new ProfileMenu(menuUtils).open();
				Bukkit.getLogger().info("Opened Perks Menu for " + p.getName());
			} else if (item.equals(this.guiManager.getGui(GuiType.QUESTS.getKey()).getDisplayItem())) {
				new QuestsMenu(menuUtils).open();
				Bukkit.getLogger().info("Opened Quests Menu for " + p.getName());
			} else if (item.equals(this.guiManager.getGui(GuiType.LEADERBOARDS.getKey()).getDisplayItem())) {
				new ProfileMenu(menuUtils).open();
				Bukkit.getLogger().info("Opened Leaderboard Menu for " + p.getName());
			}
		}
	}


}
