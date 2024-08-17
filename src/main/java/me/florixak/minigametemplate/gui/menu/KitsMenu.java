package me.florixak.minigametemplate.gui.menu;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.Permissions;
import me.florixak.uhcrevamp.game.kits.Kit;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.gui.PaginatedMenu;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XEnchantment;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KitsMenu extends PaginatedMenu {

	private final UHCPlayer uhcPlayer;
	private final List<Kit> kitsList;

	public KitsMenu(final MenuUtils menuUtils) {
		super(menuUtils, GameValues.INVENTORY.KITS_TITLE);
		this.uhcPlayer = menuUtils.getUHCPlayer();
		this.kitsList = GameManager.getGameManager().getKitsManager().getKitsList();
	}

	@Override
	public int getSlots() {
		return GameValues.INVENTORY.KITS_SLOTS;
	}

	@Override
	public int getItemsCount() {
		return this.kitsList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
			close();
		} else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
			handlePaging(event, this.kitsList);
		} else {
			if (GameManager.getGameManager().isPlaying()) {
				this.uhcPlayer.sendMessage(Messages.CANT_USE_NOW.toString());
				return;
			}
			handleKitSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder();
		ItemStack kitDisplayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Kit kit = this.kitsList.get(i);
			final List<String> lore = new ArrayList<>();

			if (this.uhcPlayer.hasKit() && this.uhcPlayer.getKit().equals(kit)) {
				lore.add(Messages.KITS_INV_SELECTED.toString());
			} else {
				if (!GameValues.KITS.BOUGHT_FOREVER) {
					lore.add(kit.getFormattedCost());
				} else {
					if (this.uhcPlayer.getData().hasKitBought(kit) || this.uhcPlayer.hasPermission(Permissions.KITS_FREE.getPerm()) || kit.isFree()) {
						lore.add(Messages.KITS_INV_CLICK_TO_SELECT.toString());
					} else {
						lore.add(kit.getFormattedCost());
					}
				}
			}

			for (final ItemStack item : kit.getItems()) {
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
			kitDisplayItem = ItemUtils.createItem(kit.getDisplayItem().getType(), kit.getDisplayName(), 1, lore);

			this.inventory.setItem(i - getStartIndex(), kitDisplayItem);
		}
	}

	@Override
	public void open() {
		if (!GameValues.KITS.ENABLED) {
			this.uhcPlayer.sendMessage(Messages.KITS_DISABLED.toString());
			return;
		}
		super.open();
	}

	private void handleKitSelection(final InventoryClickEvent event) {
		final Kit selectedKit = this.kitsList.get(event.getSlot());
		close();

		if (!GameValues.KITS.BOUGHT_FOREVER) {
			if (!selectedKit.isFree() && this.uhcPlayer.getData().getMoney() < selectedKit.getCost() && !this.uhcPlayer.hasPermission(Permissions.KITS_FREE.getPerm())) {
				this.uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
				return;
			}
			this.uhcPlayer.setKit(selectedKit);
			this.uhcPlayer.sendMessage(Messages.KITS_MONEY_DEDUCT_INFO.toString());
		} else {
			if (this.uhcPlayer.getData().hasKitBought(selectedKit) || this.uhcPlayer.hasPermission(Permissions.KITS_FREE.getPerm()) || selectedKit.isFree()) {
				this.uhcPlayer.setKit(selectedKit);
			} else {
				if (GameValues.INVENTORY.CONFIRM_PURCHASE_ENABLED) {
					this.menuUtils.setSelectedKitToBuy(selectedKit);
					new ConfirmPurchaseMenu(this.menuUtils).open();
				} else {
					this.uhcPlayer.getData().buyKit(selectedKit);
				}
			}
		}
	}
}
