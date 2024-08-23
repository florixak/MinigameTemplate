package me.florixak.minigametemplate.game.gameItems;

import lombok.Getter;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.inventory.ItemStack;

@Getter
public class ExecutableItem {

	protected final static GameManager gameManager = GameManager.getInstance();
	protected final String name;
	protected final String key;
	protected final ItemStack item;

	public ExecutableItem(final String name, final String key, final ItemStack item) {
		this.name = name;
		this.key = key;
		this.item = item;
	}

	public void execute(final GamePlayer gamePlayer) {
	}
}
