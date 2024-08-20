package me.florixak.minigametemplate.gui.menu;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.gui.MenuUtils;
import me.florixak.minigametemplate.gui.PaginatedMenu;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.ItemUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamsMenu extends PaginatedMenu {

	private final GameManager gameManager = GameManager.getInstance();
	private final Arena arena;
	private final GamePlayer gamePlayer;
	private final List<GameTeam> teamsList;

	public TeamsMenu(final MenuUtils menuUtils) {
		super(menuUtils, GameValues.INVENTORY.TEAMS_TITLE);
		this.gamePlayer = menuUtils.getGamePlayer();
		this.arena = this.gameManager.getArenaManager().getPlayerArena(this.gamePlayer);
		this.teamsList = this.arena.getTeams();
	}

	@Override
	public int getSlots() {
		return GameValues.INVENTORY.TEAMS_SLOTS;
	}

	@Override
	public int getItemsCount() {
		return this.teamsList.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
			close();
		} else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
			handlePaging(event, this.teamsList);
		} else {
			handleTeamSelection(event);
		}
	}

	@Override
	public void setMenuItems() {
		addMenuBorder();
		ItemStack item;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final GameTeam team = this.teamsList.get(i);
			final List<String> lore = new ArrayList<>();
			lore.add(TextUtils.color("&7(" + team.getMembers().size() + "/" + team.getSize() + ")"));
			for (final GamePlayer member : team.getMembers()) {
				lore.add(TextUtils.color("&f" + member.getName()));
			}
			item = ItemUtils.createItem(team.getDisplayItem().getType(), "&l" + team.getDisplayName(), 1, lore);
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
