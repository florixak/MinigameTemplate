package me.florixak.minigametemplate.managers;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GameItemManager {

	private final GameManager gameManager;
	private final FileConfiguration config;
	private final Map<Integer, ItemStack> lobbyItems = new HashMap<>();
	private final Map<Integer, ItemStack> inGameItems = new HashMap<>();

	public GameItemManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		loadItems();
	}

	private void loadItems() {
		loadLobbyItems();
		loadInGameItems();
	}

	private void loadLobbyItems() {
		final ConfigurationSection section = this.config.getConfigurationSection("settings.game-items.lobby");
		if (section == null) return;

		for (final String key : section.getKeys(false)) {
			final ConfigurationSection itemSection = section.getConfigurationSection(key);
			if (itemSection == null || !itemSection.getBoolean("enabled")) continue;

			final String displayName = itemSection.getString("display-name");
			final String materialName = itemSection.getString("display-item");
			final int slot = itemSection.getInt("slot");

			final ItemStack item = XMaterial.matchXMaterial(materialName).get().parseItem();
			final ItemStack newItem = ItemUtils.createItem(item.getType(), displayName, 1, null);
			this.lobbyItems.put(slot, newItem);
		}
		Bukkit.getLogger().info("Loaded " + this.lobbyItems.size() + " lobby items.");
	}

	private void loadInGameItems() {
		final ConfigurationSection section = this.config.getConfigurationSection("settings.game-items.in-game");
		if (section == null) return;

		for (final String key : section.getKeys(false)) {
			final ConfigurationSection itemSection = section.getConfigurationSection(key);
			if (itemSection == null || !itemSection.getBoolean("enabled")) continue;

			final String displayName = itemSection.getString("display-name");
			final String materialName = itemSection.getString("display-item");
			final int slot = itemSection.getInt("slot");

			final ItemStack item = XMaterial.matchXMaterial(materialName).get().parseItem();
			final ItemStack newItem = ItemUtils.createItem(item.getType(), displayName, 1, null);
			this.inGameItems.put(slot, newItem);
		}
		Bukkit.getLogger().info("Loaded " + this.lobbyItems.size() + " lobby items.");
	}

	public void giveItems(final GamePlayer gamePlayer) {
		if (this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
			giveInGameItems(gamePlayer);
		} else {
			giveLobbyItems(gamePlayer);
		}
	}


	private void giveLobbyItems(final GamePlayer gamePlayer) {
		gamePlayer.clearInventory();
		for (final Map.Entry<Integer, ItemStack> entry : this.lobbyItems.entrySet()) {
			gamePlayer.getInventory().setItem(entry.getKey(), entry.getValue());
		}
	}

	private void giveInGameItems(final GamePlayer gamePlayer) {
		gamePlayer.clearInventory();
		for (final Map.Entry<Integer, ItemStack> entry : this.inGameItems.entrySet()) {
			gamePlayer.getInventory().setItem(entry.getKey(), entry.getValue());
		}
	}

	public void onDisable() {
		this.lobbyItems.clear();
		this.inGameItems.clear();
	}
}