package me.florixak.minigametemplate.sql;

import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.player.DataType;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLGetter {

	private final Connection conn;
	private final String table;

	public SQLGetter(final GameManager gameManager) {
		this.conn = gameManager.getMysql().getConnection();

		this.table = GameValues.DATABASE.TABLE;
		createTable();
	}

	public void createTable() {
		final PreparedStatement ps;

		try {
			ps = this.conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + this.table + " "
					+ "(uuid VARCHAR(100) PRIMARY KEY,"
					+ "name VARCHAR(100),"
					+ "money DECIMAL(24,2) DEFAULT " + GameValues.STATISTICS.STARTING_MONEY + ","
					+ "tokens INT(100) DEFAULT " + GameValues.STATISTICS.STARTING_TOKENS + ","
					+ "level INT(100) DEFAULT " + GameValues.STATISTICS.STARTING_LEVEL + ","
					+ "exp DECIMAL(24,2) DEFAULT 0,"
					+ "required_exp DECIMAL(24,2) DEFAULT " + GameValues.STATISTICS.STARTING_REQUIRED_EXP + ","
					+ "solo_wins INT(100) DEFAULT 0,"
					+ "team_wins INT(100) DEFAULT 0,"
					+ "solo_losses INT(100) DEFAULT 0,"
					+ "team_losses INT(100) DEFAULT 0,"
					+ "solo_kills INT(100) DEFAULT 0,"
					+ "team_kills INT(100) DEFAULT 0,"
					+ "solo_killstreak INT(100) DEFAULT 0,"
					+ "team_killstreak INT(100) DEFAULT 0,"
					+ "solo_assists INT(100) DEFAULT 0,"
					+ "team_assists INT(100) DEFAULT 0,"
					+ "solo_deaths INT(100) DEFAULT 0,"
					+ "team_deaths INT(100) DEFAULT 0,"
					+ "kits VARCHAR(100) DEFAULT '',"
					+ "perks VARCHAR(100) DEFAULT '',"
					+ "cosmetics VARCHAR(100) DEFAULT '')"
			);
			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void createPlayer(final Player p) {
		try {
			final UUID uuid = p.getUniqueId();

			if (!exists(uuid)) {
				final PreparedStatement ps2 = this.conn.prepareStatement("INSERT IGNORE INTO " + this.table + " (uuid,name) VALUES (?,?)");
				ps2.setString(1, uuid.toString());
				ps2.setString(2, p.getName());
				ps2.executeUpdate();
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean exists(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT * FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
			return false;

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/* Kits */
	public List<String> getBoughtKits(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT kits FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				final String kits = rs.getString("kits");
				final List<String> kitsList = Arrays.asList(kits.split(", "));
//                Bukkit.getLogger().info(kitsList.toString());
				return kitsList;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void setBoughtKits(final UUID uuid, final String kits) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET kits=? WHERE uuid=?");
			ps.setString(1, kits);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	/* Perks */
	public List<String> getBoughtPerks(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT perks FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				final String perks = rs.getString("perks");
				final List<String> perksList = Arrays.asList(perks.split(", "));
//                Bukkit.getLogger().info(perksList.toString());
				return perksList;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void setBoughtPerks(final UUID uuid, final String perks) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET perks=? WHERE uuid=?");
			ps.setString(1, perks);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	/* Cosmetics */
	public List<String> getBoughtCosmetics(final UUID uuid) {
		return new ArrayList<>();
	}

	/* Statistics */
	public Map<String, Integer> getTopStatistics(final String type) {
		final Map<String, Integer> topStatistics = new HashMap<>();
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT name, " + type + " FROM " + this.table + " ORDER BY " + type + " DESC LIMIT 10");
			final ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				topStatistics.put(rs.getString("name"), rs.getInt(type));
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return topStatistics;
	}

	/* SQL GETTERS AND SETTERS */
	public String getString(final UUID uuid, final String column) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT " + column + " FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(column);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setString(final UUID uuid, final String column, final String value) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET " + column + "=? WHERE uuid=?");
			ps.setString(1, value);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getInt(final UUID uuid, final String column) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT " + column + " FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(column);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setInt(final UUID uuid, final String column, final int value) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET " + column + "=? WHERE uuid=?");
			ps.setInt(1, value);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void addInt(final UUID uuid, final String column, final int value) {
		setInt(uuid, column, getInt(uuid, column) + value);
	}

	public double getDouble(final UUID uuid, final String column) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT " + column + " FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble(column);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setDouble(final UUID uuid, final String column, final double value) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET " + column + "=? WHERE uuid=?");
			ps.setDouble(1, value);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void addDouble(final UUID uuid, final String column, final double value) {
		setDouble(uuid, column, getDouble(uuid, column) + value);
	}

	public List<String> getList(final UUID uuid, final String column) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT " + column + " FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				final String list = rs.getString(column);
				return Arrays.asList(list.split(", "));
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void setStat(final UUID uuid, final DataType dataType, final Object object) {
		if (object instanceof String) {
			setString(uuid, dataType.getDatabasePath(), (String) object);
		} else if (object instanceof Integer) {
			setInt(uuid, dataType.getDatabasePath(), (Integer) object);
		} else if (object instanceof Double) {
			setDouble(uuid, dataType.getDatabasePath(), (Double) object);
		} else if (object instanceof List) {
			final String joinedList = String.join(", ", (List<String>) object);
			setString(uuid, dataType.getDatabasePath(), joinedList);
		}
	}

	public boolean isTableEmpty() {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT COUNT(*) FROM " + this.table);
			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) == 0;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void emptyTable() {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("TRUNCATE " + this.table);
			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void resetPlayer(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("DELETE FROM " + this.table + " WHERE 'uuid'=?");
			ps.setString(1, uuid.toString());
			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}
}