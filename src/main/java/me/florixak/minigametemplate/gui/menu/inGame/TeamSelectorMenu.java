package me.florixak.minigametemplate.gui.menu.inGame;

import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.gui.Gui;
import me.florixak.minigametemplate.gui.GuiType;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamSelectorMenu extends PaginatedMenu {

	private final GamePlayer gamePlayer;
	private final List<GameTeam> teamsList;

	public TeamSelectorMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.gamePlayer = menuUtils.getGamePlayer();
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(this.gamePlayer);
		this.teamsList = arena.getTeams();
	}

	@Override
	public String getMenuName() {
		return format(getGui().getTitle());
	}

	@Override
	public Gui getGui() {
		return this.guiManager.getGui(GuiType.TEAMS_SELECTOR.getKey());
	}

	@Override
	public int getSlots() {
		return getGui().getSlots();
	}

	@Override
	public int getItemsCount() {
		return this.teamsList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		final ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem.equals(this.guiManager.getItem("filler"))) {
			event.setCancelled(true);
		} else if (clickedItem.equals(this.guiManager.getItem("close"))) {
			close();
		} else if (clickedItem.equals(this.guiManager.getItem("previous")) || clickedItem.equals(this.guiManager.getItem("next"))) {
			handlePaging(event, this.teamsList);
		} else {
			handleTeamSelection(event);
		}
	}

	@Override
	public void setMenuItems() {
		addMenuBorder(false);
		ItemStack item;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final GameTeam team = this.teamsList.get(i);
			final List<String> lore = new ArrayList<>();
			lore.add(TextUtils.color("&7(" + team.getMembers().size() + "/" + team.getSize() + ")"));
			for (final GamePlayer member : team.getMembers()) {
				lore.add(TextUtils.color("&f" + member.getName()));
			}
			item = ItemUtils.createItem(team.getDisplayMaterial(), "&l" + team.getDisplayName(), 1, lore);
			if (MinigameTemplate.useOldMethods()) {
				item.setDurability((short) team.getDisplayItemDurability());
			}

			this.inventory.setItem(i - getStartIndex(), item);
		}
	}

	@Override
	public void open() {
		super.open();
	}

	private void handleTeamSelection(final InventoryClickEvent event) {
		close();
		this.teamsList.get(event.getSlot()).addMember(this.gamePlayer);
	}
}
