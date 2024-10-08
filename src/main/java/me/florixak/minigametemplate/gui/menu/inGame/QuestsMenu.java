package me.florixak.minigametemplate.gui.menu.inGame;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.quests.Quest;
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

public class QuestsMenu extends PaginatedMenu {

	private final GamePlayer gamePlayer;
	private final List<Quest> quests = this.gameManager.getQuestManager().getQuests();

	public QuestsMenu(final MenuUtils menuUtils) {
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
		return this.guiManager.getGui(GuiType.QUESTS.getKey());
	}

	@Override
	public int getItemsCount() {
		return this.quests.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
			close();
		} else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
			handlePaging(event, this.quests);
		}
	}

	@Override
	public void setMenuItems() {
		addMenuBorder(false);
		ItemStack questDisplayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Quest quest = this.quests.get(i);
			final List<String> lore = new ArrayList<>();
			final boolean completed = this.gamePlayer.getQuestData().isCompletedQuest(quest.getId());
			final int count = quest.getQuestType().getCount();

			for (final String description : quest.getDescription()) {
				lore.add(TextUtils.color(description
						.replace("%count%", String.valueOf(count))
						.replace("%progress%", String.valueOf(!completed ? this.gamePlayer.getQuestData().getProgress(quest.getId()) : count))
						.replace("%material%", quest.getQuestType().hasMaterial() ?
								TextUtils.toNormalCamelText(quest.getQuestType().getParsedMaterial().name()) :
								"None")
						.replace("%money%", String.valueOf(quest.getQuestReward().getMoney()))
						.replace("%uhc-exp%", String.valueOf(quest.getQuestReward().getExp()))
						.replace("%currency%", Messages.CURRENCY.toString())
						.replace("%status%", completed ? "&aCompleted" : "&cNot Completed"
						)));
			}

			questDisplayItem = ItemUtils.createItem(quest.parseDisplayItem(), quest.getDisplayName(), 1, lore);

			if (completed) {
				ItemUtils.addGlow(questDisplayItem);
			}

			this.inventory.setItem(i - getStartIndex(), questDisplayItem);

		}
	}
}
