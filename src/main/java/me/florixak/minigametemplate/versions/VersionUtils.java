package me.florixak.minigametemplate.versions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Set;

public interface VersionUtils {

	Set<Material> getWoodPlankValues();

	net.minecraft.server.v1_8_R3.ItemStack giveLapis(Player player, int amount);

	ShapedRecipe createRecipe(ItemStack item, String key);

	void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut);

	void openAnvil(Player player);

}
