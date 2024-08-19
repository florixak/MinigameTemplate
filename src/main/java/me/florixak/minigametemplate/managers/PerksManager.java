package me.florixak.minigametemplate.managers;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.perks.PerkBonus;
import me.florixak.minigametemplate.game.perks.PerkEffect;
import me.florixak.minigametemplate.game.perks.PerkItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class PerksManager {

	private final FileConfiguration perksConfig;
	@Getter
	private final List<Perk> perks = new ArrayList<>();

	public PerksManager(final GameManager gameManager) {
		this.perksConfig = gameManager.getConfigManager().getFile(ConfigType.PERKS).getConfig();

		loadPerks();
	}

	public void loadPerks() {
		if (!GameValues.PERKS.ENABLED) return;

		for (final String perkName : this.perksConfig.getConfigurationSection("perks").getKeys(false)) {
			ItemStack displayItem = XMaterial.BARRIER.parseItem();
			double cost = 0;
			List<String> description = new ArrayList<>();
			final List<PerkEffect> effects = new ArrayList<>();
			final List<PerkItem> items = new ArrayList<>();
			final List<PerkBonus> bonuses = new ArrayList<>();

			final ConfigurationSection perkSection = this.perksConfig.getConfigurationSection("perks." + perkName);

			for (final String param : perkSection.getKeys(false)) {
				if (param.equalsIgnoreCase("display-item")) {
					displayItem = XMaterial.matchXMaterial(perkSection.getString(param)).get().parseItem();
				} else if (param.equalsIgnoreCase("cost")) {
					cost = perkSection.getDouble(param);
				} else if (param.equalsIgnoreCase("description")) {
					description = perkSection.getStringList(param);
				} else if (param.equalsIgnoreCase("EFFECT")) {
					final ConfigurationSection effectSection = perkSection.getConfigurationSection("EFFECT");
					for (final String effectName : effectSection.getKeys(false)) {
						final int effectDuration = effectSection.getInt(effectName + ".duration");
						final int effectAmplifier = effectSection.getInt(effectName + ".level");
						final PotionEffect effectType = XPotion.matchXPotion(effectName).get().buildPotionEffect(effectDuration, effectAmplifier);
						final PerkEffect perkEffect = new PerkEffect(effectType);
						effects.add(perkEffect);
					}
				} else if (param.equalsIgnoreCase("ITEM")) {
					final ConfigurationSection itemSection = perkSection.getConfigurationSection("ITEM");
					for (final String itemName : itemSection.getKeys(false)) {
						final ItemStack item = XMaterial.matchXMaterial(itemName).get().parseItem();
						final int chance = itemSection.getInt(itemName + ".chance");
						final List<Integer> amount = itemSection.getIntegerList(itemName + ".amount");
						final PerkItem perkItem = new PerkItem(item, amount, chance);
						items.add(perkItem);
					}
				} else if (param.equalsIgnoreCase("BONUS")) {
					final ConfigurationSection bonusSection = perkSection.getConfigurationSection("BONUS");
					final List<Double> coinsBonus = bonusSection.getDoubleList("coins");
					final List<Double> expBonus = bonusSection.getDoubleList("exp");
					final PerkBonus perkBonus = new PerkBonus(coinsBonus, expBonus);
					bonuses.add(perkBonus);
				}
			}
			final Perk perk = new Perk(perkName, displayItem, cost, description, bonuses, effects, items);
			addPerk(perk);
//            Bukkit.getLogger().info(perks.size() + ". Loaded perk: " + perkName + " with cost: " + cost + " and " + effects.size() + " effects, " + items.size() + " items and " + bonuses.size() + " bonuses.");
		}
	}

	public void addPerk(final Perk perk) {
		this.perks.add(perk);
	}

	public Perk getPerk(final String name) {
		for (final Perk perk : this.perks) {
			if (perk.getName().equalsIgnoreCase(name)) {
				return perk;
			}
		}
		return null;
	}

	public Perk getPerk(final int index) {
		return getPerks().get(index);
	}

	public void onDisable() {
		this.perks.clear();
	}
}