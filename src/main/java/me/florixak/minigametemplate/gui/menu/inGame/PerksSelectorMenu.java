package me.florixak.minigametemplate.gui.menu.inGame;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.perks.Perk;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.gui.menu.shop.ShopMenu;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PerksSelectorMenu extends PaginatedMenu {

	private final GamePlayer gamePlayer;
	private final List<Perk> perksList;

	public PerksSelectorMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.gamePlayer = menuUtils.getGamePlayer();
		this.perksList = this.gameManager.getPerksManager().getPerks().stream()
				.filter(perk -> this.gamePlayer.getData().hasBought(perk)).collect(Collectors.toList());
	}

	@Override
	public String getMenuName() {
		return format(getGui().getTitle());
	}

	@Override
	public int getSlots() {
		return getGui().getSlots();
	}

	@Override
	public Gui getGui() {
		return this.guiManager.getGui(GuiType.PERKS_SELECTOR.getKey());
	}

	@Override
	public int getItemsCount() {
		return this.perksList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem.equals(this.guiManager.getItem("filler"))) {
			event.setCancelled(true);
		} else if (clickedItem.equals(this.guiManager.getItem("close"))) {
			close();
		} else if (clickedItem.equals(this.guiManager.getItem("back"))) {
			close();
			new ShopMenu(this.menuUtils).open();
		} else if (clickedItem.equals(this.guiManager.getItem("previous")) || clickedItem.equals(this.guiManager.getItem("next"))) {
			handlePaging(event, this.perksList);
		} else {
			handlePerkSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder(false);
		ItemStack displayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Perk perk = this.perksList.get(i);
			final List<String> lore = new ArrayList<>(perk.getLore());

			displayItem = ItemUtils.createItem(perk.getDisplayItem().getType(), perk.getDisplayName(), 1, lore);

			this.inventory.setItem(i - getStartIndex(), displayItem);
		}
	}

	@Override
	public void open() {
		if (!GameValues.PERKS.ENABLED) {
			this.gamePlayer.sendMessage(Messages.PERKS_DISABLED.toString());
			return;
		}
		super.open();
	}

	private void handlePerkSelection(final InventoryClickEvent event) {
		final Perk selectedPerk = this.perksList.get(event.getSlot());
		close();
		final Arena arena = this.gamePlayer.getArena();
		arena.getPlayerArenaData(this.gamePlayer).setPerk(selectedPerk);
	}
}
