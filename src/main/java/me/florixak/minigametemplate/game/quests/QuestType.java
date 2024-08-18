package me.florixak.minigametemplate.game.quests;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.Material;

@Getter
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

	public Material getParsedMaterial() {
		return XMaterial.matchXMaterial(this.material).get().parseMaterial();
	}

	public boolean hasMaterial() {
		return this.material != null;
	}

}
