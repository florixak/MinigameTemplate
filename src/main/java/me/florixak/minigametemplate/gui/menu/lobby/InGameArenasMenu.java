package me.florixak.minigametemplate.gui.menu.lobby;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InGameArenasMenu extends PaginatedMenu {

	private final GamePlayer gamePlayer;
	private final List<Arena> arenaList = this.gameManager.getArenaManager().getInGameArenas();

	public InGameArenasMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.gamePlayer = menuUtils.getGamePlayer();
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
		return this.guiManager.getGui(GuiType.IN_GAME_ARENA_SELECTOR.getKey());
	}

	@Override
	public int getItemsCount() {
		return this.arenaList.size();
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
			new AvailableArenasMenu(this.menuUtils).open();
		} else if (clickedItem.equals(this.guiManager.getItem("previous")) || clickedItem.equals(this.guiManager.getItem("next"))) {
			handlePaging(event, this.arenaList);
		} else {
			if (!this.gamePlayer.isInArena()) {
				this.gamePlayer.sendMessage(Messages.CANT_USE_NOW.toString());
				return;
			}
			handleArenaSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder(true);
		ItemStack displayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Arena arena = this.arenaList.get(i);
			displayItem = ItemUtils.createItem(XMaterial.MAP.parseMaterial(), arena.getName(), 1, arena.getLore());
			ItemUtils.addGlow(displayItem);

			this.inventory.setItem(i - getStartIndex(), displayItem);
		}
	}

	@Override
	public void open() {
		super.open();
	}

	private void handleArenaSelection(final InventoryClickEvent event) {
		final Arena arena = this.arenaList.get(event.getSlot());
		close();

		if (!arena.isEnabled()) {
			this.gamePlayer.sendMessage(Messages.ARENA_DISABLED.toString());
			return;
		}

		if (arena.isEnding()) {
			this.gamePlayer.sendMessage(Messages.ARENA_ENDED.toString());
			return;
		}

		if (arena.getPlayers().size() >= arena.getMaxPlayers()) {
			this.gamePlayer.sendMessage(Messages.ARENA_FULL.toString());
			return;
		}

		arena.joinAsSpectator(this.gamePlayer);
	}
}
