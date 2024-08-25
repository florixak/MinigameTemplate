package me.florixak.minigametemplate.gui;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiManager {

	private final Map<String, ItemStack> guiItem = new HashMap<>();
	private final Map<String, Gui> guis = new HashMap<>();
	private final List<Gui> lobbyItemsInHotbar = new ArrayList<>();
	private final List<Gui> inGameItemsInHotbar = new ArrayList<>();

	private final FileConfiguration invConfig;

	public GuiManager(final GameManager gameManager) {
		this.invConfig = gameManager.getConfigManager().getFile(ConfigType.INVENTORIES).getConfig();

		load();
	}

	private void load() {
		loadLobbyGuis();
		loadInGameGuis();
		loadItems();
	}

	private void loadLobbyGuis() {
		loadGui("lobby", this.lobbyItemsInHotbar);
	}

	private void loadInGameGuis() {
		loadGui("in-game", this.inGameItemsInHotbar);
	}

	private void loadItems() {
		final ConfigurationSection section = this.invConfig.getConfigurationSection("inventories.items");
		for (final String key : section.getKeys(false)) {
			final ConfigurationSection itemSection = section.getConfigurationSection(key);
			final String material = itemSection.getString("display-item");
			final String displayName = itemSection.getString("display-name");
			final Material mat = XMaterial.matchXMaterial(material).get().parseMaterial();
			final List<String> lore = itemSection.getStringList("display-lore");
			final ItemStack item = ItemUtils.createItem(mat, displayName, 1, lore);
			this.guiItem.put(key, item);
			Bukkit.getLogger().info("Loaded item: " + key);
		}
	}

	private void giveLobbyItems(final GamePlayer gamePlayer) {
		gamePlayer.clearInventory();
		for (final Gui gui : this.lobbyItemsInHotbar) {
			gamePlayer.getInventory().setItem(gui.getDisplaySlot(), gui.getDisplayItem());
		}
	}

	private void giveInGameItems(final GamePlayer gamePlayer) {
		gamePlayer.clearInventory();
		for (final Gui gui : this.inGameItemsInHotbar) {
			gamePlayer.getInventory().setItem(gui.getDisplaySlot(), gui.getDisplayItem());
		}
	}

	public void giveItems(final GamePlayer gamePlayer) {
		if (gamePlayer.isInArena()) {
			giveInGameItems(gamePlayer);
		} else {
			giveLobbyItems(gamePlayer);
		}
	}

	private void loadGui(final String sectionName, final List<Gui> itemsInHotbar) {
		final ConfigurationSection section = this.invConfig.getConfigurationSection("inventories." + sectionName);
		for (final String key : section.getKeys(false)) {
			final ConfigurationSection invSection = section.getConfigurationSection(key);
			final boolean enabled = invSection.getBoolean("enabled");
			final String title = invSection.getString("title");
			final int slots = invSection.getInt("slots");
			final String displayItem = invSection.getString("display-item", "AIR");
			final String displayName = invSection.getString("display-name", " ");
			final Material material = XMaterial.matchXMaterial(displayItem).get().parseMaterial();
			final List<String> lore = invSection.getStringList("display-lore");
			final ItemStack displayItemStack = ItemUtils.createItem(material, displayName, 1, lore);
			final int displaySlot = invSection.getInt("display-slot", -1);

			final Gui gui = new Gui(enabled, title, slots, displayItemStack, displaySlot);
			this.guis.put(key, gui);
			if (displaySlot != -1 && enabled) {
				itemsInHotbar.add(gui);
			}
			Bukkit.getLogger().info("Loaded GUI: " + key);
		}
	}


	public ItemStack getItem(final String key) {
		return this.guiItem.get(key);
	}

	public Gui getGui(final String key) {
		return this.guis.get(key);
	}

	public void onDisable() {
		this.guis.clear();
		this.guiItem.clear();
		this.lobbyItemsInHotbar.clear();
	}

}
