package me.florixak.minigametemplate.utils;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

	/**
	 * Creates an ItemStack with the specified material, name, amount, and lore.
	 *
	 * @param material The material of the item.
	 * @param name     The display name of the item.
	 * @param amount   The amount of the item.
	 * @param lore     The lore of the item.
	 * @return The created ItemStack.
	 */
	public static ItemStack createItem(final Material material, final String name, int amount, final List<String> lore) {
		final ItemStack item = new ItemStack(material);
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return item;

		if (name != null) meta.setDisplayName(TextUtils.color(name));
		if (amount == 0) amount = 1;
		if (lore != null && !lore.isEmpty()) {
			lore.replaceAll(TextUtils::color);
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		item.setAmount(amount);
		return item;
	}

	/**
	 * Checks if the given ItemStack has item meta.
	 *
	 * @param item The ItemStack to check.
	 * @return True if the item has meta, false otherwise.
	 */
	public static boolean hasItemMeta(final ItemStack item) {
		return item.hasItemMeta();
	}

	/**
	 * Sets the color of a leather armor item.
	 *
	 * @param item       The leather armor ItemStack.
	 * @param armorColor The color to set.
	 */
	public static void setArmorItemMeta(final ItemStack item, final Color armorColor) {
		final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		if (armorColor != null) meta.setColor(armorColor);
		item.setItemMeta(meta);
	}

	/**
	 * Adds an unsafe enchantment to an ItemStack.
	 *
	 * @param item         The ItemStack to enchant.
	 * @param enchantment  The enchantment to add.
	 * @param enchantLevel The level of the enchantment.
	 */
	public static void addEnchant(final ItemStack item, final Enchantment enchantment, final int enchantLevel) {
		item.addUnsafeEnchantment(enchantment, enchantLevel);
	}

	/**
	 * Adds an enchantment to an enchanted book ItemStack.
	 *
	 * @param item         The enchanted book ItemStack.
	 * @param enchantment  The enchantment to add.
	 * @param enchantLevel The level of the enchantment.
	 */
	public static void addBookEnchantment(final ItemStack item, final Enchantment enchantment, final int enchantLevel) {
		final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.addStoredEnchant(enchantment, enchantLevel, true);
		item.setItemMeta(meta);
	}

	/**
	 * Adds a glow effect to an ItemStack.
	 *
	 * @param item The ItemStack to add the glow effect to.
	 */
	public static void addGlow(final ItemStack item) {
		final ItemMeta meta = item.getItemMeta();
		meta.addEnchant(XEnchantment.UNBREAKING.getEnchant(), 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
	}

	/**
	 * Removes attributes from an ItemStack.
	 *
	 * @param item The ItemStack to remove attributes from.
	 */
	public static void removeAttributes(final ItemStack item) {
		final ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
	}

	/**
	 * Gets the enchantments of an ItemStack.
	 *
	 * @param item The ItemStack to get enchantments from.
	 * @return A list of enchantments on the item.
	 */
	public static List<Enchantment> getEnchantments(final ItemStack item) {
		final List<Enchantment> enchantments = new ArrayList<>();
		for (final Enchantment enchant : item.getEnchantments().keySet()) {
			enchantments.add(enchant);
		}
		return enchantments;
	}

	/**
	 * Adds lore to an ItemStack.
	 *
	 * @param item  The ItemStack to add lore to.
	 * @param lines Lines of lore.
	 */
	public static void addLore(final ItemStack item, final String... lines) {
		final List<String> lore = new ArrayList<>();
		final ItemMeta meta = item.getItemMeta();
		for (final String line : lines) {
			lore.add(TextUtils.color(line));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	/**
	 * Creates a potion ItemStack.
	 *
	 * @param splash     Determines if the potion is a splash potion.
	 * @param effectType The type of potion effect.
	 * @param customName The name of the potion.
	 * @param duration   The duration of the effect in seconds.
	 * @param amplifier  The amplifier of the effect (starting from 1).
	 * @return The created potion ItemStack.
	 */
	public static ItemStack createPotionItem(final PotionEffectType effectType, final String customName, final int amount, final int duration, final int amplifier, final boolean splash) {
		final Material potionMaterial = splash ? XMaterial.SPLASH_POTION.parseMaterial() : XMaterial.POTION.parseMaterial();
		final ItemStack potion = new ItemStack(potionMaterial, amount);
		final PotionMeta meta = (PotionMeta) potion.getItemMeta();

		if (meta != null) {
			if (customName != null && !customName.isEmpty()) {
				meta.setDisplayName(TextUtils.color("&f" + customName));
				final ArrayList<String> lore = new ArrayList<>();
				lore.add(TextUtils.color("&7" + TextUtils.toNormalCamelText(XPotion.matchXPotion(effectType).name()) + " " + (amplifier) + " (" + (duration) + "s)"));
				meta.setLore(lore);
			} else
				meta.setDisplayName(TextUtils.color("&f" + TextUtils.toNormalCamelText(XPotion.matchXPotion(effectType).name()) + " " + (amplifier) + " (" + (duration) + "s)"));
			meta.addCustomEffect(XPotion.matchXPotion(effectType).buildPotionEffect(duration * 20, amplifier), true);
			potion.setItemMeta(meta);
		}

		return potion;
	}

	/**
	 * Checks if an ItemStack is a potion.
	 *
	 * @param item The ItemStack to check.
	 * @return True if the item is a potion, false otherwise.
	 */
	public static boolean isPotion(final ItemStack item) {
		if (item == null) {
			return false;
		}
		final Material type = XMaterial.matchXMaterial(item).parseMaterial();
		return type == XMaterial.POTION.parseMaterial() || type == XMaterial.SPLASH_POTION.parseMaterial() || type == XMaterial.LINGERING_POTION.parseMaterial();
	}

	/**
	 * Gets the color associated with a potion effect type for Minecraft 1.8.8.
	 *
	 * @param effectType The potion effect type.
	 * @return The color associated with the potion effect type.
	 */
	public static Color getPotionColor(final PotionEffectType effectType) {
		if (effectType == null) return Color.WHITE;

		switch (effectType.getName().toUpperCase()) {
			case "SPEED":
				return Color.fromRGB(8171462);
			case "SLOWNESS":
				return Color.fromRGB(5926017);
			case "HASTE":
				return Color.fromRGB(14270531);
			case "MINING_FATIGUE":
				return Color.fromRGB(4866583);
			case "STRENGTH":
				return Color.fromRGB(9643043);
			case "INSTANT_HEALTH":
				return Color.fromRGB(16262179);
			case "INSTANT_DAMAGE":
				return Color.fromRGB(4393481);
			case "JUMP_BOOST":
				return Color.fromRGB(2293580);
			case "NAUSEA":
				return Color.fromRGB(5578058);
			case "REGENERATION":
				return Color.fromRGB(13458603);
			case "RESISTANCE":
				return Color.fromRGB(10044730);
			case "FIRE_RESISTANCE":
				return Color.fromRGB(14981690);
			case "WATER_BREATHING":
				return Color.fromRGB(3035801);
			case "INVISIBILITY":
				return Color.fromRGB(8356754);
			case "BLINDNESS":
				return Color.fromRGB(2039587);
			case "NIGHT_VISION":
				return Color.fromRGB(2039713);
			case "HUNGER":
				return Color.fromRGB(5797459);
			case "WEAKNESS":
				return Color.fromRGB(4738376);
			case "POISON":
				return Color.fromRGB(5149489);
			case "WITHER":
				return Color.fromRGB(3484199);
			case "HEALTH_BOOST":
				return Color.fromRGB(16284963);
			case "ABSORPTION":
				return Color.fromRGB(2445989);
			case "SATURATION":
				return Color.fromRGB(16262179);
			default:
				return Color.WHITE;
		}
	}

	/**
	 * Gets the attack damage of an ItemStack.
	 *
	 * @param item The ItemStack to get attack damage from.
	 * @return The attack damage of the item.
	 */
	public static double getAttackDamage(final ItemStack item) {
		switch (XMaterial.matchXMaterial(item)) {
			default:
				return 0.25;

			case WOODEN_SHOVEL:
			case GOLDEN_SHOVEL:
			case WOODEN_HOE:
			case GOLDEN_HOE:
			case STONE_HOE:
			case IRON_HOE:
			case DIAMOND_HOE:
			case NETHERITE_HOE:
				return 1;

			case WOODEN_PICKAXE:
			case GOLDEN_PICKAXE:
			case STONE_SHOVEL:
				return 2;

			case WOODEN_AXE:
			case GOLDEN_AXE:
			case STONE_PICKAXE:
			case IRON_SHOVEL:
				return 3;

			case WOODEN_SWORD:
			case GOLDEN_SWORD:
			case STONE_AXE:
			case IRON_PICKAXE:
			case DIAMOND_SHOVEL:
				return 4;

			case STONE_SWORD:
			case IRON_AXE:
			case DIAMOND_PICKAXE:
			case NETHERITE_SHOVEL:
				return 5;

			case IRON_SWORD:
			case DIAMOND_AXE:
			case NETHERITE_PICKAXE:
				return 6;

			case DIAMOND_SWORD:
			case NETHERITE_AXE:
				return 7;

			case NETHERITE_SWORD:
				return 8;
		}
	}

	/**
	 * Checks if an ItemStack is a wool.
	 *
	 * @param item The ItemStack to check.
	 * @return True if the item is a wool, false otherwise.
	 */
	public static boolean isWool(final ItemStack item) {
		return XMaterial.matchXMaterial(item).parseMaterial().name().contains("WOOL");
	}

	/**
	 * Gets a wool ItemStack with the specified durability.
	 *
	 * @param durability The durability of the wool.
	 * @return The wool ItemStack.
	 */
	public static ItemStack getColorWool(final short durability) {
		if (MinigameTemplate.useOldMethods()) {
			final ItemStack wool = new ItemStack(XMaterial.WHITE_WOOL.parseMaterial(), 1);
			wool.setDurability(durability);
			return new ItemStack(XMaterial.WHITE_WOOL.parseMaterial(), 1);
		}
		switch (durability) {
			case 0:
				return XMaterial.WHITE_WOOL.parseItem();
			case 1:
				return XMaterial.ORANGE_WOOL.parseItem();
			case 2:
				return XMaterial.MAGENTA_WOOL.parseItem();
			case 3:
				return XMaterial.LIGHT_BLUE_WOOL.parseItem();
			case 4:
				return XMaterial.YELLOW_WOOL.parseItem();
			case 5:
				return XMaterial.LIME_WOOL.parseItem();
			case 6:
				return XMaterial.PINK_WOOL.parseItem();
			case 7:
				return XMaterial.GRAY_WOOL.parseItem();
			case 8:
				return XMaterial.LIGHT_GRAY_WOOL.parseItem();
			case 9:
				return XMaterial.CYAN_WOOL.parseItem();
			case 10:
				return XMaterial.PURPLE_WOOL.parseItem();
			case 11:
				return XMaterial.BLUE_WOOL.parseItem();
			case 12:
				return XMaterial.BROWN_WOOL.parseItem();
			case 13:
				return XMaterial.GREEN_WOOL.parseItem();
			case 14:
				return XMaterial.RED_WOOL.parseItem();
			case 15:
				return XMaterial.BLACK_WOOL.parseItem();
		}
		return XMaterial.WHITE_WOOL.parseItem();
	}

	/**
	 * Gets the durability of a wool ItemStack.
	 *
	 * @param material The wool material.
	 * @return The durability of the wool.
	 */
	public static short getWoolDurability(final Material material) {
		switch (XMaterial.matchXMaterial(material)) {
			case WHITE_WOOL:
				return 0;
			case ORANGE_WOOL:
				return 1;
			case MAGENTA_WOOL:
				return 2;
			case LIGHT_BLUE_WOOL:
				return 3;
			case YELLOW_WOOL:
				return 4;
			case LIME_WOOL:
				return 5;
			case PINK_WOOL:
				return 6;
			case GRAY_WOOL:
				return 7;
			case LIGHT_GRAY_WOOL:
				return 8;
			case CYAN_WOOL:
				return 9;
			case PURPLE_WOOL:
				return 10;
			case BLUE_WOOL:
				return 11;
			case BROWN_WOOL:
				return 12;
			case GREEN_WOOL:
				return 13;
			case RED_WOOL:
				return 14;
			case BLACK_WOOL:
				return 15;
		}
		return 0;
	}

	/**
	 * Gets a wool ItemStack by color name.
	 *
	 * @param color The name of the color.
	 * @return The wool ItemStack.
	 */
	public static ItemStack getWoolByColor(final String color) {
		return getColorWool(getWoolDurabilityColorByString(color));
	}

	public static ItemStack getWoolByColorFromString(final String string) {
		return getWoolByColor(getContainsColorFromString(string));
	}

	public static String getContainsColorFromString(String string) {
		if (string.contains("WHITE")) {
			return "WHITE";
		} else if (string.contains("ORANGE")) {
			return "ORANGE";
		} else if (string.contains("MAGENTA")) {
			return "MAGENTA";
		} else if (string.contains("LIGHT_BLUE")) {
			return "LIGHT_BLUE";
		} else if (string.contains("YELLOW")) {
			return "YELLOW";
		} else if (string.contains("LIME")) {
			return "LIME";
		} else if (string.contains("PINK")) {
			return "PINK";
		} else if (string.contains("GRAY")) {
			return "GRAY";
		} else if (string.contains("LIGHT_GRAY")) {
			return "LIGHT_GRAY";
		} else if (string.contains("CYAN")) {
			return "CYAN";
		} else if (string.contains("PURPLE")) {
			return "PURPLE";
		} else if (string.contains("BLUE")) {
			return "BLUE";
		} else if (string.contains("BROWN")) {
			return "BROWN";
		} else if (string.contains("GREEN")) {
			return "GREEN";
		} else if (string.contains("RED")) {
			return "RED";
		} else if (string.contains("BLACK")) {
			return "BLACK";
		}
		return "WHITE";
	}

	/**
	 * Gets the durability of a wool ItemStack by color name.
	 *
	 * @param colorName The name of the color.
	 * @return The durability of the wool.
	 */
	public static short getWoolDurabilityColorByString(final String colorName) {
		switch (colorName.toUpperCase()) {
			case "WHITE":
				return 0;
			case "ORANGE":
				return 1;
			case "MAGENTA":
				return 2;
			case "LIGHT_BLUE":
				return 3;
			case "YELLOW":
				return 4;
			case "LIME":
				return 5;
			case "PINK":
				return 6;
			case "GRAY":
				return 7;
			case "LIGHT_GRAY":
				return 8;
			case "CYAN":
				return 9;
			case "PURPLE":
				return 10;
			case "BLUE":
				return 11;
			case "BROWN":
				return 12;
			case "GREEN":
				return 13;
			case "RED":
				return 14;
			case "BLACK":
				return 15;
		}
		return 0;
	}
}