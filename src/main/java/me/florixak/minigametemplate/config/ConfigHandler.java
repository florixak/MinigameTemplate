package me.florixak.minigametemplate.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

	private final JavaPlugin plugin;
	private final String name;
	private final File file;
	private FileConfiguration configuration;

	public ConfigHandler(final JavaPlugin plugin, final String name) {
		this.plugin = plugin;
		this.name = name + ".yml";
		this.file = new File(plugin.getDataFolder(), this.name);
		this.configuration = new YamlConfiguration();
	}

	public void saveDefaultConfig() {
		if (!this.file.exists()) {
			this.plugin.saveResource(this.name, false);
		}

		try {
			this.configuration.load(this.file);
		} catch (final InvalidConfigurationException | IOException e) {
			e.printStackTrace();
			this.plugin.getLogger().severe("============= CONFIGURATION ERROR =============");
			this.plugin.getLogger().severe("There was an error loading " + this.name);
			this.plugin.getLogger().severe("Please check for any obvious configuration mistakes");
			this.plugin.getLogger().severe("such as using tabs for spaces or forgetting to end quotes");
			this.plugin.getLogger().severe("before reporting to the developer. The plugin will now disable..");
			this.plugin.getLogger().severe("============= CONFIGURATION ERROR =============");
			this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
		}

	}

	public void save() {
		if (this.configuration == null || this.file == null) return;
		try {
			getConfig().save(this.file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		this.configuration = YamlConfiguration.loadConfiguration(this.file);
	}

	public FileConfiguration getConfig() {
		return this.configuration;
	}
}
