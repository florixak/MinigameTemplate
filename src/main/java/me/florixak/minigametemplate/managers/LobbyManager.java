package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.config.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LobbyManager {

	private final GameManager gameManager;
	private final FileConfiguration config;
	private final String lobbyName;

	public LobbyManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		this.lobbyName = this.config.getString("settings.lobby.world", "world");
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
}