package me.florixak.minigametemplate.hooks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.florixak.minigametemplate.game.GameValues;
import org.bukkit.Bukkit;

public class ProtocolLibHook {

	private static ProtocolManager protocolManager;

	public ProtocolLibHook() {
		setupProtocolLib();
	}

	private void setupProtocolLib() {
		if (!GameValues.ADDONS.CAN_USE_PROTOCOLLIB) return;
		try {
			if (!hasProtocolLib()) {
				Bukkit.getLogger().info("ProtocolLib plugin not found! Disabling ProtocolLib Support.");
				return;
			}
			protocolManager = ProtocolLibrary.getProtocolManager();
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public ProtocolManager getProtocolManager() {
		return protocolManager;
	}

	public boolean hasProtocolLib() {
		return Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
	}
}
