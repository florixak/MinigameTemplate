package me.florixak.minigametemplate;

import lombok.Getter;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.hooks.LuckPermsHook;
import me.florixak.minigametemplate.hooks.PAPIHook;
import me.florixak.minigametemplate.hooks.ProtocolLibHook;
import me.florixak.minigametemplate.hooks.VaultHook;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.NMSUtils;
import me.florixak.minigametemplate.versions.VersionUtils;
import me.florixak.minigametemplate.versions.VersionUtils_1_20;
import me.florixak.minigametemplate.versions.VersionUtils_1_8;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MinigameTemplate extends JavaPlugin {

	@Getter
	private static MinigameTemplate instance;

	private VaultHook vaultHook;
	private ProtocolLibHook protocolLibHook;
	private LuckPermsHook luckPermsHook;
	private PAPIHook papiHook;

	private GameManager gameManager;

	@Override
	public void onEnable() {
		instance = this;

		getLogger().info(getDescription().getName());
		getLogger().info("Author: " + getDescription().getAuthors().get(0));
		getLogger().info("Version: " + getDescription().getVersion());

		NMSUtils.checkNMSVersion();

		this.gameManager = new GameManager(this);
		if (GameValues.BUNGEECORD.ENABLED) {
			try {
				getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
				getLogger().info("Registered BungeeCord channel.");
			} catch (final Exception e) {
				getLogger().warning("Failed to register BungeeCord channel.");
			}
		}
		registerDependencies();
	}

	@Override
	public void onDisable() {
		this.gameManager.onDisable();
	}

	public void registerDependencies() {
		this.vaultHook = new VaultHook();
		this.protocolLibHook = new ProtocolLibHook();
		this.luckPermsHook = new LuckPermsHook();
		this.papiHook = new PAPIHook(this);
	}

	public static boolean useOldMethods() {
		return NMSUtils.useOldMethods();
	}

	public VersionUtils getVersionUtils() {
		if (useOldMethods()) {
			return new VersionUtils_1_8();
		} else {
			return new VersionUtils_1_20();
		}
	}
}
