package me.florixak.minigametemplate.gui.menu;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.gui.PaginatedMenu;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamsMenu extends PaginatedMenu {

	private final UHCPlayer uhcPlayer;
	private final List<UHCTeam> teamsList;

	public TeamsMenu(final MenuUtils menuUtils) {
		super(menuUtils, GameValues.INVENTORY.TEAMS_TITLE);
		this.uhcPlayer = menuUtils.getUHCPlayer();
		this.teamsList = GameManager.getGameManager().getTeamManager().getTeamsList();
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
			if (GameManager.getGameManager().isPlaying()) {
				this.uhcPlayer.sendMessage(Messages.CANT_USE_NOW.toString());
				return;
			}
			handleTeamSelection(event);
		}
	}

	@Override
	public void setMenuItems() {
		addMenuBorder();
		ItemStack item;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final UHCTeam team = this.teamsList.get(i);
			final List<String> lore = new ArrayList<>();
			lore.add(TextUtils.color("&7(" + team.getMembers().size() + "/" + team.getMaxSize() + ")"));
			for (final UHCPlayer member : team.getMembers()) {
				lore.add(TextUtils.color("&f" + member.getName()));
			}
			item = ItemUtils.createItem(team.getDisplayItem().getType(), "&l" + team.getDisplayName(), 1, lore);
			if (UHCRevamp.useOldMethods) {
				item.setDurability((short) team.getDisplayItemDurability());
			}

			inventory.setItem(i - getStartIndex(), item);
		}
	}

	@Override
	public void open() {
		if (!GameValues.TEAM.TEAM_MODE) {
			this.uhcPlayer.sendMessage(Messages.CANT_USE_NOW.toString());
			return;
		}
		super.open();
	}

	private void handleTeamSelection(final InventoryClickEvent event) {
		close();
		this.teamsList.get(event.getSlot()).addMember(this.uhcPlayer);
	}
}
