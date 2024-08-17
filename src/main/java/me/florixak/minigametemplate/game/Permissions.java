package me.florixak.minigametemplate.game;

public enum Permissions {

	ANVIL("anvil"),
	FORCE_START("forcestart"),
	FORCE_SKIP("forceskip"),
	SETUP("setup"),
	WORKBENCH("workbench"),
	KITS_FREE("free-kits"),
	PERKS_FREE("free-perks"),

	VIP("vip"),
	RESERVED_SLOT("reserved-slot");

	private final String PREFIX = "minigametemplate.";
	private final String permission;

	Permissions(final String permission) {
		this.permission = this.PREFIX + permission;
	}

	public String getPerm() {
		return this.permission;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
