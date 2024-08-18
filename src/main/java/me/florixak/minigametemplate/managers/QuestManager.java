package me.florixak.minigametemplate.managers;

import lombok.Getter;
import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.game.quests.Quest;
import me.florixak.minigametemplate.game.quests.QuestReward;
import me.florixak.minigametemplate.game.quests.QuestType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
	private final FileConfiguration questConfig;
	@Getter
	private final List<Quest> quests = new ArrayList<>();

	public QuestManager(final GameManager gameManager) {
		this.questConfig = gameManager.getConfigManager().getFile(ConfigType.QUESTS).getConfig();
	}

	public void load() {
		loadQuests();
	}

	private void loadQuests() {
		for (final String key : this.questConfig.getConfigurationSection("quests").getKeys(false)) {
			final String name = this.questConfig.getString("quests." + key + ".display-name", "Quest " + key);
			final String displayItem = this.questConfig.getString("quests." + key + ".display-item", "BOOK");
			final List<String> description = this.questConfig.getStringList("quests." + key + ".description");
			final String type = this.questConfig.getString("quests." + key + ".type");
			final int count = this.questConfig.getInt("quests." + key + ".count");
			final double money = this.questConfig.getDouble("quests." + key + ".reward.money");
			final double uhcExp = this.questConfig.getDouble("quests." + key + ".reward.uhc-exp");
			if (this.questConfig.contains("quests." + key + ".material")) {
				final String material = this.questConfig.getString("quests." + key + ".material");
				this.quests.add(new Quest(key, name, displayItem, new QuestType(type, count, material), description, new QuestReward(money, uhcExp)));
				continue;
			}
			this.quests.add(new Quest(key, name, displayItem, new QuestType(type, count), description, new QuestReward(money, uhcExp)));
		}
	}

	public Quest getQuest(final String questId) {
		for (final Quest quest : this.quests) {
			if (quest.getId().equals(questId)) {
				return quest;
			}
		}
		return null;
	}

//	public void resetDailyQuests() {
//		dailyQuests.clear();
//		saveQuests();
//	}

	public void onDisable() {
		this.quests.clear();
	}
}