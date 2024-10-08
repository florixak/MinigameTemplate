package me.florixak.minigametemplate.gui.menu.lobby;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AvailableArenasMenu extends PaginatedMenu {

	private final GameManager gameManager = GameManager.getInstance();
	private final List<Arena> arenaList = this.gameManager.getArenaManager().getNotInGameArenas();
	private final GamePlayer gamePlayer;

	public AvailableArenasMenu(final MenuUtils menuUtils) {
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
		return this.guiManager.getGui(GuiType.ARENA_SELECTOR.getKey());
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
		} else if (clickedItem.equals(this.guiManager.getItem("previous")) || clickedItem.equals(this.guiManager.getItem("next"))) {
			handlePaging(event, this.arenaList);
		} else if (clickedItem.equals(this.guiManager.getItem("in-game-arenas"))) {
			close();
			new InGameArenasMenu(this.menuUtils).open();
		} else {
			handleArenaSelection(event);
		}

	}

	@Override
	public void setMenuItems() {
		addMenuBorder(false);
		ItemStack displayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Arena arena = this.arenaList.get(i);
			displayItem = ItemUtils.createItem(XMaterial.MAP.parseMaterial(), arena.getName(), 1, arena.getLore());

			this.inventory.setItem(i - getStartIndex(), displayItem);
		}

		this.inventory.setItem(getSlots() - 1, this.guiManager.getItem("in-game-arenas"));
	}

	@Override
	public void open() {
		if (!GameValues.KITS.ENABLED) {
			this.gamePlayer.sendMessage(Messages.KITS_DISABLED.toString());
			return;
		}
		super.open();
	}

	private void handleArenaSelection(final InventoryClickEvent event) {
		final Arena arena = this.arenaList.get(event.getSlot());
		close();

		if (this.gamePlayer.isInArena()) {
			final Arena currentArena = this.gameManager.getArenaManager().getPlayerArena(this.gamePlayer);
			currentArena.leave(this.gamePlayer);
		}
		if (arena.getPlayers().size() >= arena.getMaxPlayers()) {
			this.gamePlayer.sendMessage(Messages.ARENA_FULL.toString());
			return;
		}

		arena.join(this.gamePlayer);
		arena.broadcast(Messages.ARENA_JOIN.toString().replace("%player%", this.gamePlayer.getPlayer().getName()));
	}
}
