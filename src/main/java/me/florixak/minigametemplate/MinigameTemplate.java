package me.florixak.minigametemplate;

import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.hooks.LuckPermsHook;
import me.florixak.minigametemplate.hooks.PAPIHook;
import me.florixak.minigametemplate.hooks.ProtocolLibHook;
import me.florixak.minigametemplate.hooks.VaultHook;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.versions.VersionUtils;
import me.florixak.minigametemplate.versions.VersionUtils_1_20;
import me.florixak.minigametemplate.versions.VersionUtils_1_8;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinigameTemplate extends JavaPlugin {

	private static MinigameTemplate instance;

	private static String nmsVer;
	private static boolean oldMethods;

	private VaultHook vaultHook;
	private ProtocolLibHook protocolLibHook;
	private LuckPermsHook luckPermsHook;
	private PAPIHook papiHook;

	public static MinigameTemplate getInstance() {
		return instance;
	}

	private GameManager gameManager;

	@Override
	public void onEnable() {
		instance = this;

		getLogger().info(getDescription().getName());
		getLogger().info("Author: " + getDescription().getAuthors().get(0));
		getLogger().info("Version: " + getDescription().getVersion());

		checkNMSVersion();

		if (GameValues.BUNGEECORD.ENABLED) {
			try {
				getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
				getLogger().info("Registered BungeeCord channel.");
			} catch (final Exception e) {
				getLogger().warning("Failed to register BungeeCord channel.");
			}
		}

		this.gameManager = new GameManager(this);
		registerDependencies();
	}

	@Override
	public void onDisable() {
		this.gameManager.onDisable();
	}

	public GameManager getGameManager() {
		return this.gameManager;
	}

	public void registerDependencies() {
		this.vaultHook = new VaultHook();
		this.protocolLibHook = new ProtocolLibHook();
		this.luckPermsHook = new LuckPermsHook();
		this.papiHook = new PAPIHook(this);
	}

	public static boolean useOldMethods() {
		return oldMethods;
	}

	public static String getNMSVersion() {
		return nmsVer;
	}

	public void checkNMSVersion() {
		nmsVer = Bukkit.getServer().getClass().getPackage().getName();
		nmsVer = nmsVer.substring(nmsVer.lastIndexOf(".") + 1);

		if (nmsVer.contains("v1_8") || nmsVer.startsWith("v1_7_")) {
			oldMethods = true;
		}
	}

	public VersionUtils getVersionUtils() {
		if (useOldMethods()) {
			return new VersionUtils_1_8();
		} else {
			return new VersionUtils_1_20();
		}
	}

	public ProtocolLibHook getProtocolLibHook() {
		return this.protocolLibHook;
	}

	public VaultHook getVaultHook() {
		return this.vaultHook;
	}

	public LuckPermsHook getLuckPermsHook() {
		return this.luckPermsHook;
	}

	public PAPIHook getPapiHook() {
		return this.papiHook;
	}
}
