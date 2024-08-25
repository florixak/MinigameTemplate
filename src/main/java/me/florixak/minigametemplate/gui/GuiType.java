package me.florixak.minigametemplate.gui;

import lombok.Getter;

@Getter
public enum GuiType {

	ARENA_SELECTOR("arena-selector"),
	IN_GAME_ARENA_SELECTOR("in-game-arena-selector"),
	PROFILE("profile"),
	LEADERBOARDS("leaderboards"),
	SHOP("shop"),
	COSMETICS_SHOP("cosmetics-shop"),
	KITS_SHOP("kits-shop"),
	PERKS_SHOP("perks-shop"),
	TEAMS_SELECTOR("teams-selector"),
	KITS_SELECTOR("kits-selector"),
	PERKS_SELECTOR("perks-selector"),
	COSMETICS_SELECTOR("cosmetics-selector"),
	QUESTS("quests"),
	LEAVE_CONFIRM("leave-confirm"),
	PURCHASE_CONFIRM("purchase-confirm");

	private final String key;

	GuiType(final String key) {
		this.key = key;
	}


}
