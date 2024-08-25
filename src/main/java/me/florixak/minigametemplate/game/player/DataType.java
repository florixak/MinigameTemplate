package me.florixak.minigametemplate.game.player;

public enum DataType {

	NAME("name"),

	MONEY("money"),
	TOKENS("tokens"),
	LEVEL("level"),
	EXP("exp"),
	REQUIRED_EXP("required-exp"),

	SOLO_GAMES_PLAYED("solo.games-played"),
	SOLO_WINS("solo.wins"),
	SOLO_LOSSES("solo.losses"),
	SOLO_KILLS("solo.kills"),
	SOLO_DEATHS("solo.deaths"),
	SOLO_ASSISTS("solo.assists"),
	SOLO_KILLSTREAK("solo.killstreak"),

	TEAMS_GAMES_PLAYED("teams.games-played"),
	TEAMS_WINS("teams.wins"),
	TEAMS_LOSSES("teams.losses"),
	TEAMS_KILLS("teams.kills"),
	TEAMS_DEATHS("teams.deaths"),
	TEAMS_ASSISTS("teams.assists"),
	TEAMS_KILLSTREAK("teams.killstreak");


	private final String configPath;

	DataType(final String configPath) {
		this.configPath = configPath;
	}

	public String getConfigPath() {
		return this.configPath;
	}

	public static DataType getByName(final String name) {
		for (final DataType type : values()) {
			if (type.getConfigPath().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.configPath;
	}
}
