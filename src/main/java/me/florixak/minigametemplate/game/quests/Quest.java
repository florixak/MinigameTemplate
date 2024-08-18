package me.florixak.minigametemplate.game.quests;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.florixak.minigametemplate.game.player.GamePlayer;
import org.bukkit.Material;

import java.util.List;

@Getter
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

	public Material parseDisplayItem() {
		return XMaterial.matchXMaterial(this.displayItem).get().parseMaterial();
	}

	public void giveReward(final GamePlayer gamePlayer) {
		this.questReward.giveReward(gamePlayer);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Quest && ((Quest) obj).getId().equals(this.id);
	}
}
