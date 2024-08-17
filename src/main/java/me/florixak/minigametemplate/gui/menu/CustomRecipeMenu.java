package me.florixak.minigametemplate.gui.menu;

import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.gui.Menu;
import me.florixak.uhcrevamp.gui.MenuManager;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CustomRecipeMenu extends Menu {

	private final CustomRecipe recipe;

	public CustomRecipeMenu(final MenuUtils menuUtils) {
		super(menuUtils);
		this.recipe = menuUtils.getSelectedRecipe();
	}

	@Override
	public String getMenuName() {
		return TextUtils.color(this.recipe.getResult().getItemMeta().getDisplayName());
	}

	@Override
	public int getSlots() {
		return 54;
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
			close();
		} else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
			close();
			new CustomRecipesMenu(MenuManager.getMenuUtils(menuUtils.getUHCPlayer())).open();
		}
	}

	@Override
	public void setMenuItems() {
		for (int row = 0; row < 3; row++) {
			final int slot = 11 + row + (row * 8);
			for (int col = 0; col < 3; col++) {
				ItemStack item = this.recipe.getShapeMatrix()[row][col];
				if (item == null) item = new ItemStack(XMaterial.AIR.parseMaterial());
				inventory.setItem(slot + col, item);
			}
		}

		inventory.setItem(24, this.recipe.getResult());
		inventory.setItem(getSlots() - 6, ItemUtils.createItem(
				XMaterial.matchXMaterial(GameValues.INVENTORY.BACK_ITEM).get().parseMaterial(),
				TextUtils.color(GameValues.INVENTORY.BACK_TITLE),
				1,
				null));
		inventory.setItem(getSlots() - 5, ItemUtils.createItem(
				XMaterial.matchXMaterial(GameValues.INVENTORY.CLOSE_ITEM).get().parseMaterial(),
				TextUtils.color(GameValues.INVENTORY.CLOSE_TITLE),
				1,
				null));
	}
}
