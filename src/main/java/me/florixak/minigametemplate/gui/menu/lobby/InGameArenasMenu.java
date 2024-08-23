package me.florixak.minigametemplate.gui.menu.lobby;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InGameArenasMenu extends PaginatedMenu {

	private final GameManager gameManager = GameManager.getInstance();
	private final List<Arena> arenaList = this.gameManager.getArenaManager().getInGameArenas();
	private final GamePlayer gamePlayer;

	public InGameArenasMenu(final MenuUtils menuUtils) {
		super(menuUtils, "Kits");
		this.gamePlayer = menuUtils.getGamePlayer();
	}

	@Override
	public int getSlots() {
		return 45;
	}

	@Override
	public int getItemsCount() {
		return this.arenaList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final Material clickedItem = event.getCurrentItem().getType();
		if (clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.CLOSE_ITEM).get().parseMaterial())) {
			close();
		} else if (clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.NEXT_ITEM).get().parseMaterial())
				|| clickedItem.equals(XMaterial.matchXMaterial(GameValues.INVENTORY.PREVIOUS_ITEM).get().parseMaterial())) {
			handlePaging(event, this.arenaList);
		} else {
			if (!this.gameManager.getArenaManager().isPlayerInArena(this.gamePlayer)) {
				this.gamePlayer.sendMessage(Messages.CANT_USE_NOW.toString());
				return;
			}
			handleArenaSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder();
		ItemStack arenaDisplayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Arena arena = this.arenaList.get(i);
			final List<String> lore = new ArrayList<>();

			if (this.gameManager.getArenaManager().isPlayerInArena(this.gamePlayer)
					&& this.gameManager.getArenaManager().getPlayerArena(this.gamePlayer).equals(arena)) {
				lore.add(" ");
				lore.add("You are here!");
			} else {
				lore.add(" ");
				lore.add("Click to spectate!");
			}

			arenaDisplayItem = ItemUtils.createItem(XMaterial.MAP.parseMaterial(), "&c" + arena.getName() + " (In Game)", 1, lore);
			ItemUtils.addGlow(arenaDisplayItem);

			this.inventory.setItem(i - getStartIndex(), arenaDisplayItem);
		}
	}

	@Override
	public void open() {
		super.open();
	}

	private void handleArenaSelection(final InventoryClickEvent event) {
		final Arena arena = this.arenaList.get(event.getSlot());
		close();

	}
}
