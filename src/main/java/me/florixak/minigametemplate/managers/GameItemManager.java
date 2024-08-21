package me.florixak.minigametemplate.managers;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GameItemManager {

	private final GameManager gameManager;
	private final FileConfiguration config;

	private final HashMap<Integer, ItemStack> lobbyItemsMap = new HashMap<>();
	private final HashMap<Integer, ItemStack> inGameItemsMap = new HashMap<>();

	public GameItemManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

		loadItems();
	}

	private void loadItems() {
		loadLobbyItems();
		loadInGameItems();
		loadSpectatorItems();
	}

	private void loadLobbyItems() {
		final ConfigurationSection section = this.config.getConfigurationSection("settings.items.lobby");
		if (section == null) return;
		loadItems(section, this.lobbyItemsMap);
	}

	private void loadInGameItems() {
		final ConfigurationSection section = this.config.getConfigurationSection("settings.items.in-game");
		if (section == null) return;
		loadItems(section, this.inGameItemsMap);
	}

	private void loadSpectatorItems() {
		final ConfigurationSection section = this.config.getConfigurationSection("settings.items.spectator");
		if (section == null) return;
		loadItems(section, this.inGameItemsMap);
	}

	private void loadItems(final ConfigurationSection section, final HashMap<Integer, ItemStack> map) {
		if (section == null) return;
		for (final String selector : section.getKeys(false)) {
			final String displayName = section.getString(selector + ".display-name");
			final ItemStack item = XMaterial.matchXMaterial(selector.toUpperCase()).get().parseItem();
			final int slot = section.getInt(selector + ".slot");
			final ItemStack newItem = ItemUtils.createItem(item.getType(), displayName, 1, null);
			if (slot < 0 || slot > 8) continue;
			map.put(slot, newItem);
		}

	}

	public void giveItems(final GamePlayer gamePlayer) {
		gamePlayer.clearInventory();
		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			if (arena.isWaiting() || arena.isStarting()) {
				giveInGameItems(gamePlayer);
			} else {
				giveSpectatorItems(gamePlayer);
			}
		} else {
			giveLobbyItems(gamePlayer);
		}

	}

	private void giveLobbyItems(final GamePlayer gamePlayer) {
		if (this.lobbyItemsMap.isEmpty()) loadLobbyItems();
		if (this.lobbyItemsMap.isEmpty()) return;
		giveItems(gamePlayer, this.lobbyItemsMap);
	}

	private void giveInGameItems(final GamePlayer gamePlayer) {
		if (this.inGameItemsMap.isEmpty()) loadInGameItems();
		if (this.inGameItemsMap.isEmpty()) return;
		giveItems(gamePlayer, this.inGameItemsMap);
	}

	private void giveSpectatorItems(final GamePlayer gamePlayer) {
		if (this.inGameItemsMap.isEmpty()) loadSpectatorItems();
		if (this.inGameItemsMap.isEmpty()) return;
		giveItems(gamePlayer, this.inGameItemsMap);
	}

	private void giveItems(final GamePlayer gamePlayer, final HashMap<Integer, ItemStack> map) {
		if (map.isEmpty()) return;
		for (final int slot : map.keySet()) {
			gamePlayer.getInventory().setItem(slot, map.get(slot));
		}
	}

	public void onDisable() {
		this.lobbyItemsMap.clear();
		this.inGameItemsMap.clear();
	}
}
