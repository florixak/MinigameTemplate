package me.florixak.minigametemplate.hooks;

import me.florixak.minigametemplate.game.GameValues;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

	private Economy economy;

	public VaultHook() {
		setupEconomy();
	}

	public void setupEconomy() {
		if (!GameValues.ADDONS.CAN_USE_VAULT) return;
		if (!hasVault()) {
			Bukkit.getLogger().info("Vault plugin not found! Disabling Vault support.");
			return;
		}
		final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (rsp != null) this.economy = rsp.getProvider();
	}

	private boolean hasVault() {
		return Bukkit.getPluginManager().getPlugin("Vault") != null;
	}

	public boolean hasEconomy() {
		return this.economy != null;
	}

	public boolean hasAccount(final Player target) {
		if (!hasEconomy())
			return false;
		return this.economy.hasAccount(target);
	}

	public double getBalance(final Player target) {
		if (!hasEconomy())
			return -1.00;

		return this.economy.getBalance(target);
	}

	public String withdraw(final String target, final double amount) {
		if (!hasEconomy())
			return "ERROR";

		return this.economy.withdrawPlayer(target, amount).errorMessage;
	}

	public String deposit(final String target, final double amount) {
		if (!hasEconomy())
			return "ERROR";
		return this.economy.depositPlayer(target, amount).errorMessage;
	}

	public String formatCurrencySymbol(final double amount) {
		if (!hasEconomy())
			return "ERROR";

		return this.economy.format(amount);
	}
}