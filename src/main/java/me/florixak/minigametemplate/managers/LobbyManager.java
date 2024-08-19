package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.config.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LobbyManager {

	private final GameManager gameManager;
	private final FileConfiguration config;
	private final String waitingLobbyName;
	private final String endingLobbyName;

	public LobbyManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		this.waitingLobbyName = this.config.getString("settings.lobby.waiting.world", "world");
		this.endingLobbyName = this.config.getString("settings.lobby.ending.world", "world");
	}

	public void checkLobbies() {
		if (!this.gameManager.getWorldManager().worldExists(this.waitingLobbyName)) {
			this.gameManager.getWorldManager().loadWorldIfExists(this.waitingLobbyName);

			if (getWaitingLobbyLocation() != null) {
				Bukkit.getLogger().info("World " + this.waitingLobbyName + " is loaded.");
			}
		}
		if (this.waitingLobbyName.equals(this.endingLobbyName)) {
			return;
		}
		if (!this.gameManager.getWorldManager().worldExists(this.endingLobbyName)) {
			this.gameManager.getWorldManager().loadWorldIfExists(this.endingLobbyName);

			if (getEndingLobbyLocation() != null) {
				Bukkit.getLogger().info("World " + this.endingLobbyName + " is loaded.");
			}
		}
	}

	public void setWaitingLobbyLocation(final Location location) {
		this.config.set("settings.lobby.waiting.world", location.getWorld().getName());
		this.config.set("settings.lobby.waiting.x", location.getX());
		this.config.set("settings.lobby.waiting.y", location.getY());
		this.config.set("settings.lobby.waiting.z", location.getZ());
		this.config.set("settings.lobby.waiting.yaw", location.getYaw());
		this.config.set("settings.lobby.waiting.pitch", location.getPitch());
		this.gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
	}

	public void removeWaitingLobby() {
		this.config.set("settings.lobby.waiting.world", "world");
		this.config.set("settings.lobby.waiting.x", 0);
		this.config.set("settings.lobby.waiting.y", 10);
		this.config.set("settings.lobby.waiting.z", 0);
		this.config.set("settings.lobby.waiting.yaw", 0);
		this.config.set("settings.lobby.waiting.pitch", 0);
		this.gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
	}

	public Location getWaitingLobbyLocation() {
		return new Location(
				Bukkit.getWorld(this.waitingLobbyName),
				this.config.getDouble("settings.lobby.waiting.x"),
				this.config.getDouble("settings.lobby.waiting.y"),
				this.config.getDouble("settings.lobby.waiting.z"),
				(float) this.config.getDouble("settings.lobby.waiting.yaw"),
				(float) this.config.getDouble("settings.lobby.waiting.pitch")
		);
	}

	public void setEndingLobbyLocation(final Location location) {
		this.config.set("settings.lobby.ending.world", location.getWorld().getName());
		this.config.set("settings.lobby.ending.x", location.getX());
		this.config.set("settings.lobby.ending.y", location.getY());
		this.config.set("settings.lobby.ending.z", location.getZ());
		this.config.set("settings.lobby.ending.yaw", location.getYaw());
		this.config.set("settings.lobby.ending.pitch", location.getPitch());
		this.gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
	}

	public void removeEndingLobby() {
		this.config.set("settings.lobby.ending.world", "UHCEndingLobby");
		this.config.set("settings.lobby.ending.x", 0);
		this.config.set("settings.lobby.ending.y", 10);
		this.config.set("settings.lobby.ending.z", 0);
		this.config.set("settings.lobby.ending.yaw", 0);
		this.config.set("settings.lobby.ending.pitch", 0);
		this.gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
	}

	public Location getEndingLobbyLocation() {
		return new Location(
				Bukkit.getWorld(this.waitingLobbyName),
				this.config.getDouble("settings.lobby.ending.x"),
				this.config.getDouble("settings.lobby.ending.y"),
				this.config.getDouble("settings.lobby.ending.z"),
				(float) this.config.getDouble("settings.lobby.ending.yaw"),
				(float) this.config.getDouble("settings.lobby.ending.pitch")
		);
	}


}