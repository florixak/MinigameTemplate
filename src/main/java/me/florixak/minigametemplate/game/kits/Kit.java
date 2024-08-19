package me.florixak.minigametemplate.game.kits;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Kit {

	private final String name;
	private final String displayName;
	private final List<ItemStack> items;
	private final ItemStack displayItem;
	private final double cost;
	private final List<String> lore;

	public Kit(final String name, final String displayName, final ItemStack displayItem, final double cost, final List<ItemStack> items) {
		this.name = name;
		this.displayName = displayName;
		this.displayItem = displayItem;
		this.cost = cost;
		this.items = items;
		this.lore = setLore();
	}

	public String getDisplayName() {
		return TextUtils.color(this.displayName);
	}

	public boolean isFree() {
		return getCost() == 0;
	}

	public String getFormattedCost() {
		return Messages.KITS_COST.toString().replace("%cost%", String.valueOf(getCost()));
	}

	public void giveKit(final GamePlayer gamePlayer) {
		final Player p = gamePlayer.getPlayer();

		for (final ItemStack item : getItems()) {
			p.getInventory().addItem(item);
		}
	}

	private List<String> setLore() {
		final List<String> lore = new ArrayList<>();
		lore.add(" ");
		for (final ItemStack item : this.items) {
			if (!item.getEnchantments().keySet().isEmpty()) {
				final List<Enchantment> enchantmentsList = ItemUtils.getEnchantments(item);
				final StringBuilder enchants = new StringBuilder();
				for (int j = 0; j < enchantmentsList.size(); j++) {
					final String enchantment = XEnchantment.matchXEnchantment(enchantmentsList.get(j)).name();
					enchants.append(TextUtils.toNormalCamelText(enchantment) + " " + item.getEnchantments().get(enchantmentsList.get(j)));
					if (j < enchantmentsList.size() - 1) enchants.append(", ");
				}
				lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString()) + " [" + enchants.toString() + "]"));
			} else if (ItemUtils.isPotion(item)) {
				if (item.hasItemMeta()) {
					final PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
					if (potionMeta != null && potionMeta.hasCustomEffects()) {
						final String effects = potionMeta.getCustomEffects().stream()
								.map(effect -> TextUtils.toNormalCamelText(XPotion.matchXPotion(effect.getType()).name()) + " " + (effect.getAmplifier() + 1) + " (" + (effect.getDuration() / 20) + "s)")
								.collect(Collectors.joining(", "));
						lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString()) + " [" + effects + "]"));
					}
				}
			} else {
				lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString())));
			}
		}
		return lore;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof Kit
				&& ((Kit) o).getName().equals(getName())
				&& ((Kit) o).getDisplayName().equals(getDisplayName())
				&& ((Kit) o).getCost() == getCost()
				&& ((Kit) o).getItems().equals(getItems());
	}
}
