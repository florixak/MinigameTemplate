package me.florixak.minigametemplate.managers;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.kits.Kit;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class KitsManager {

	private final FileConfiguration config, kitsConfig;
	@Getter
	private final List<Kit> kitsList = new ArrayList<>();

	public KitsManager(final GameManager gameManager) {
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		this.kitsConfig = gameManager.getConfigManager().getFile(ConfigType.KITS).getConfig();

		load();
	}

	public void load() {
		loadKits();
	}

	private void loadKits() {
		if (!GameValues.KITS.ENABLED) return;

		final ConfigurationSection kitsSection = this.kitsConfig.getConfigurationSection("kits");
		for (final String kitName : kitsSection.getKeys(false)) {

			List<ItemStack> itemsList = new ArrayList<>();
			ItemStack displayItem = XMaterial.BARRIER.parseItem();
			double cost = 0;

			final ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitName);
			for (final String param : kitSection.getKeys(false)) {

				if (param.equalsIgnoreCase("display-item")) {
					displayItem = XMaterial.matchXMaterial(kitSection.getString(param, "BARRIER").toUpperCase()).get().parseItem();

				} else if (param.equalsIgnoreCase("cost")) {
					cost = kitSection.getDouble(param, 0);

				} else if (param.equalsIgnoreCase("items")) {
					itemsList = loadItems(kitSection);
				}
			}
			final Kit kit = new Kit(kitName, kitName, displayItem, cost, itemsList);
			addKit(kit);
		}
	}

	private List<ItemStack> loadItems(final ConfigurationSection section) {
		final List<ItemStack> itemsList = new ArrayList<>();
		try {
			final ConfigurationSection itemsSection = section.getConfigurationSection("items");
			if (itemsSection != null && !itemsSection.getKeys(false).isEmpty()) {
				for (final String item : itemsSection.getKeys(false)) {
					final ConfigurationSection itemSection = itemsSection.getConfigurationSection(item);
					final ItemStack i = XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() != null ? XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() : XMaterial.STONE.parseItem();
					final int amount = itemSection.getInt("amount", 1);
					final ItemStack newI = ItemUtils.createItem(i.getType(), null, amount, null);
					newI.setDurability((short) itemSection.getInt("durability", newI.getDurability()));
					final ConfigurationSection enchantsSection = itemSection.getConfigurationSection("enchantments");
					if (enchantsSection != null) {
						for (final String enchant : enchantsSection.getKeys(false)) {
							final String enchantmentName = enchant.toUpperCase();
							final Enchantment e = XEnchantment.matchXEnchantment(enchantmentName).get().getEnchant();
							final int level = enchantsSection.getInt(enchantmentName);
							ItemUtils.addEnchant(newI, e, level);
						}
					}
					itemsList.add(newI);
				}
			}
		} catch (final Exception e) {
			Bukkit.getLogger().info("There is a problem with loading kit items!");
		}

		try {
			final ConfigurationSection potionsSection = section.getConfigurationSection("potions");
			if (potionsSection != null && !potionsSection.getKeys(false).isEmpty()) {
				for (final String potion : potionsSection.getKeys(false)) {
					final ConfigurationSection potionSection = potionsSection.getConfigurationSection(potion);
					final int amplifier = potionSection.getInt("amplifier", 1);
					final int amount = potionSection.getInt("amount", 1);
					final int duration = potionSection.getInt("duration", 44);
					final boolean splash = potionSection.getBoolean("splash", false);
					final PotionEffectType effectType = XPotion.matchXPotion(potion).get().getPotionEffectType();
					final ItemStack potionItem = ItemUtils.createPotionItem(effectType, "", amount, duration, amplifier, splash);

					itemsList.add(potionItem);
				}
			}
		} catch (final Exception e) {
			Bukkit.getLogger().info("There is a problem with loading kit potions!");
		}
		return itemsList;
	}

	private void addKit(final Kit kit) {
		this.kitsList.add(kit);
	}

	public Kit getKit(final String name) {
		for (final Kit kit : this.kitsList) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}
		return null;
	}

	public void onDisable() {
		this.kitsList.clear();
	}

	public boolean exists(final String kitName) {
		for (final Kit kit : this.kitsList) {
			if (kit.getName().equalsIgnoreCase(kitName)) return true;
		}
		return false;
	}
}