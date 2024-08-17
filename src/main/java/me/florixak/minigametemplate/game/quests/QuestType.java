package me.florixak.minigametemplate.game.quests;

import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Material;

public class QuestType {

	private final String type;
	private final int count;
	private String material;

	public QuestType(final String type, final int count) {
		this.type = type;
		this.count = count;
	}

	public QuestType(final String type, final int count, final String material) {
		this.type = type;
		this.count = count;
		this.material = material;
	}

	public String getType() {
		return this.type;
	}

	public int getCount() {
		return this.count;
	}

	public Material parseMaterial() {
		return XMaterial.matchXMaterial(this.material).get().parseMaterial();
	}

	public String getMaterial() {
		return this.material;
	}

	public boolean hasMaterial() {
		return this.material != null;
	}

}
