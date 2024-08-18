package me.florixak.minigametemplate.game.player;

import me.florixak.minigametemplate.config.ConfigType;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.quests.Quest;
import me.florixak.minigametemplate.game.quests.QuestType;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class PlayerQuestData {

	private final GameManager gameManager = GameManager.getGameManager();
	private final GamePlayer gamePlayer;
	private final FileConfiguration questConfig;
	private final Set<String> completedQuests = new HashSet<>();
	private final Map<String, Integer> questProgress = new HashMap<>();

	public PlayerQuestData(final GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		this.questConfig = this.gameManager.getConfigManager().getFile(ConfigType.QUESTS).getConfig();

		initializeData();
	}

	private void initializeData() {
		if (!hasData()) return;
		loadData();
	}

	private boolean hasData() {
		return this.questConfig.contains("players." + this.gamePlayer.getUUID().toString());
	}

	private void loadData() {
		if (this.questConfig.getConfigurationSection("players") == null)
			return;

		final String uuid = this.gamePlayer.getUUID().toString();

		if (this.questConfig.getConfigurationSection("players." + uuid) == null)
			return;

		for (final String questId : this.questConfig.getStringList("players." + uuid + ".completed")) {
			addCompletedQuest(questId);
		}
		for (final String questId : this.questConfig.getConfigurationSection("players." + uuid + ".progress").getKeys(false)) {
			if (this.completedQuests.contains(questId)) {
				continue;
			}
			setProgress(questId, this.questConfig.getInt("players." + uuid + ".progress." + questId));
		}

	}

	public void savePlayerQuestData() {
		final String path = "players." + this.gamePlayer.getUUID().toString();
		this.questConfig.set(path + ".completed", new ArrayList<>(this.completedQuests));
		this.questProgress.forEach((key, value) -> this.questConfig.set(path + ".progress." + key,
				getProgress(key) == this.gameManager.getQuestManager().getQuest(key).getQuestType().getCount() ? null : value));
		this.gameManager.getConfigManager().saveFile(ConfigType.QUESTS);
	}

	public Set<String> getCompletedQuests() {
		return this.completedQuests;
	}

	public Map<String, Integer> getQuestProgress() {
		return this.questProgress;
	}

	private void addCompletedQuest(final String questId) {
		this.completedQuests.add(questId);
	}

	private void setProgress(final String questId, final int progress) {
		this.questProgress.put(questId, progress);
	}

	public void addProgressToTypes(final String type, final Material material) {
		for (final Quest quest : this.gameManager.getQuestManager().getQuests()) {
			if (isCompletedQuest(quest.getId())) continue;
			final QuestType questType = quest.getQuestType();
			if (questType.getType().equalsIgnoreCase(type)) {
				if (questType.hasMaterial()
						&& material != null
						&& questType.parseMaterial().equals(material)) {
					addProgress(quest.getId());
				} else if (!questType.hasMaterial()) {
					addProgress(quest.getId());
				}
			}
		}
	}

	private void addProgress(final String questId) {
		if (isCompletedQuest(questId)) {
			return;
		}
		if (!this.questProgress.containsKey(questId)) {
			this.questProgress.put(questId, 0);
			return;
		}
		this.questProgress.put(questId, getProgress(questId) + 1);

		if (getProgress(questId) >= this.gameManager.getQuestManager().getQuest(questId).getQuestType().getCount()) {
			completeQuest(this.gameManager.getQuestManager().getQuest(questId));
		}
	}

	public int getProgress(final String questId) {
		if (!this.questProgress.containsKey(questId)) {
			this.questProgress.put(questId, 0);
		}
		return this.questProgress.getOrDefault(questId, 0);
	}

	private void completeQuest(final Quest quest) {
		addCompletedQuest(quest.getId());
		this.questProgress.remove(quest.getId());
		quest.giveReward(this.gamePlayer);
		this.gamePlayer.sendMessage(Messages.QUEST_COMPLETED.toString().replace("%quest%", quest.getDisplayName()));
		this.gamePlayer.sendMessage(Messages.QUEST_REWARD.toString()
				.replace("%quest%", quest.getDisplayName())
				.replace("%money%", String.valueOf(quest.getReward().getMoney()))
				.replace("%uhc-exp%", String.valueOf(quest.getReward().getUhcExp()))
				.replace("%currency%", Messages.CURRENCY.toString()));
		this.gameManager.getSoundManager().playQuestCompletedSound(this.gamePlayer.getPlayer());
	}

	public boolean hasQuestWithTypeOf(final String questType) {
		for (final Quest quest : this.gameManager.getQuestManager().getQuests()) {
			if (!isCompletedQuest(quest.getId())
					&& quest.getQuestType().getType().equalsIgnoreCase(questType)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCompletedQuest(final String questId) {
		return this.completedQuests.contains(questId);
	}
}