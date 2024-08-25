package me.florixak.minigametemplate.config;

import me.florixak.minigametemplate.MinigameTemplate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

	private final Map<ConfigType, ConfigHandler> configurations;

	public ConfigManager() {
		this.configurations = new HashMap<>();
	}

	public void loadFiles(final MinigameTemplate plugin) {

		registerFile(ConfigType.SETTINGS, new ConfigHandler(plugin, "config"));
		registerFile(ConfigType.MESSAGES, new ConfigHandler(plugin, "messages"));
		registerFile(ConfigType.SCOREBOARD, new ConfigHandler(plugin, "scoreboard"));
		registerFile(ConfigType.PLAYER_DATA, new ConfigHandler(plugin, "player-data"));
		registerFile(ConfigType.KITS, new ConfigHandler(plugin, "kits"));
		registerFile(ConfigType.PERKS, new ConfigHandler(plugin, "perks"));
		registerFile(ConfigType.QUESTS, new ConfigHandler(plugin, "quests"));
		registerFile(ConfigType.INVENTORIES, new ConfigHandler(plugin, "inventories"));

		this.configurations.values().forEach(ConfigHandler::saveDefaultConfig);

		Messages.setConfiguration(getFile(ConfigType.MESSAGES).getConfig());
	}

	public ConfigHandler getFile(final ConfigType type) {
		return this.configurations.get(type);
	}

	public void reloadFiles() {
		this.configurations.values().forEach(ConfigHandler::reload);
		Messages.setConfiguration(getFile(ConfigType.MESSAGES).getConfig());
	}

	public void registerFile(final ConfigType type, final ConfigHandler config) {
		this.configurations.put(type, config);
	}

	private FileConfiguration getFileConfiguration(final File file) {
		return YamlConfiguration.loadConfiguration(file);
	}

	public void saveFile(final ConfigType type) {
		getFile(type).save();
	}

}
