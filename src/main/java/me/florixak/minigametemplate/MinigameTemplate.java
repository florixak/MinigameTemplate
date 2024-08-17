package me.florixak.minigametemplate;

import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MinigameTemplate extends JavaPlugin {

	private static MinigameTemplate instance;

	public static String nmsVer;
	public static boolean useOldMethods;

	public static MinigameTemplate getInstance() {
		return instance;
	}

	private GameManager gameManager;

	@Override
	public void onEnable() {
		instance = this;

		this.gameManager = new GameManager(this);
	}

	@Override
	public void onDisable() {

	}

	public GameManager getGameManager() {
		return this.gameManager;
	}

	public void registerCommands() {
		final List<CommandExecutor> commands = new ArrayList<>();

		for (final CommandExecutor command : commands) {

		}
	}

	public void registerListeners() {
		final List<Listener> listeners = new ArrayList<>();

		for (final Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	public void registerDependencies() {

	}
}
