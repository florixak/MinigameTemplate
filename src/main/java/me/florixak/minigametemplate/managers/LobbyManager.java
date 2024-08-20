package me.florixak.minigametemplate.managers;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class LobbyManager {

	private final GameManager gameManager;
	private final FileConfiguration config;
	private final String lobbyName;

	private final HashMap<Integer, ItemStack> lobbyMap = new HashMap<>();

	public LobbyManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		this.lobbyName = this.config.getString("settings.lobby.waiting.world", "world");

		loadLobbyItems();
		checkLobbies();
	}

	public void checkLobbies() {
		if (!this.gameManager.getWorldManager().worldExists(this.lobbyName)) {
			this.gameManager.getWorldManager().loadWorldIfExists(this.lobbyName);

			if (getLobbyLocation() != null) {
				Bukkit.getLogger().info("World " + this.lobbyName + " is loaded.");
			}
		}
	}

	public void setLobbyLocation(final Location location) {
		this.config.set("settings.lobby.world", location.getWorld().getName());
		this.config.set("settings.lobby.x", location.getX());
		this.config.set("settings.lobby.y", location.getY());
		this.config.set("settings.lobby.z", location.getZ());
		this.config.set("settings.lobby.yaw", location.getYaw());
		this.config.set("settings.lobby.pitch", location.getPitch());
		this.gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
	}

	public void removeLobby() {
		this.config.set("settings.lobby.world", "world");
		this.config.set("settings.lobby.x", 0);
		this.config.set("settings.lobby.y", 60);
		this.config.set("settings.lobby.z", 0);
		this.config.set("settings.lobby.yaw", 0);
		this.config.set("settings.lobby.pitch", 0);
		this.gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
	}

	public Location getLobbyLocation() {
		return new Location(
				Bukkit.getWorld(this.lobbyName),
				this.config.getDouble("settings.lobby.x"),
				this.config.getDouble("settings.lobby.y", 64),
				this.config.getDouble("settings.lobby.z"),
				(float) this.config.getDouble("settings.lobby.yaw"),
				(float) this.config.getDouble("settings.lobby.pitch")
		);
	}

	private void loadLobbyItems() {
		final ConfigurationSection section = this.config.getConfigurationSection("settings.inventories");
		if (section == null) return;
		for (final String selector : section.getKeys(false)) {
			if (selector.matches("next|next-item|previous|previous-item|back|back-item|close|close-item")) continue;
			final String displayName = section.getString(selector + ".display-name");
			final String material = section.getString(selector + ".display-item", "BARRIER").toUpperCase();
			final ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
			final int slot = section.getInt(selector + ".slot");
			final ItemStack newItem = ItemUtils.createItem(item.getType(), displayName, 1, null);
			if (slot < 0 || slot > 8) continue;
			this.lobbyMap.put(slot, newItem);
		}
	}

	public void giveLobbyItems(final GamePlayer gamePlayer) {
		if (this.lobbyMap.isEmpty()) return;
		for (final int slot : this.lobbyMap.keySet()) {
			gamePlayer.getInventory().setItem(slot, this.lobbyMap.get(slot));
		}
	}

	public void onDisable() {
		this.lobbyMap.clear();
	}
}