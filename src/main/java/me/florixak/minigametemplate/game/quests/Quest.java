package me.florixak.minigametemplate.game.quests;

import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Material;

import java.util.List;

public class Quest {

	private final String id;
	private final QuestType questType;
	private final String displayName;
	private final String displayItem;
	private final List<String> description;
	private final QuestReward questReward;

	public Quest(final String id, final String displayName, final String displayItem, final QuestType questType, final List<String> description, final QuestReward questReward) {
		this.id = id;
		this.questType = questType;
		this.displayName = displayName;
		this.displayItem = displayItem;
		this.description = description;
		this.questReward = questReward;
	}

	public String getId() {
		return this.id;
	}

	public QuestType getQuestType() {
		return this.questType;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getDisplayItem() {
		return this.displayItem;
	}

	public Material parseDisplayItem() {
		return XMaterial.matchXMaterial(this.displayItem).get().parseMaterial();
	}

	public List<String> getDescription() {
		return this.description;
	}

	public QuestReward getReward() {
		return this.questReward;
	}

	public void giveReward(final UHCPlayer uhcPlayer) {
		this.questReward.giveReward(uhcPlayer);
	}


	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Quest && ((Quest) obj).getId().equals(this.id);
	}
}
