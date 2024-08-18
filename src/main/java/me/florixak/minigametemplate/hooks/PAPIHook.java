package me.florixak.minigametemplate.hooks;

import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.GameValues;
import org.bukkit.Bukkit;

public class PAPIHook {

	private final MinigameTemplate plugin;

	public PAPIHook(final MinigameTemplate plugin) {
		this.plugin = plugin;
		setupPlaceholderAPI();
	}

	public void setupPlaceholderAPI() {
		if (!GameValues.ADDONS.CAN_USE_PLACEHOLDERAPI) {
			return;
		}
		if (!hasPlaceholderAPI()) {
			Bukkit.getLogger().info("PlaceholderAPI plugin not found! Disabling PAPI support.");
			return;
		}
		new PlaceholderExp(this.plugin).register();
	}

	public boolean hasPlaceholderAPI() {
		return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
	}
}
