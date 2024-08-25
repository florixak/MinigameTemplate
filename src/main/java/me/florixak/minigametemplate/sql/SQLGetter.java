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
					+ "required-exp DECIMAL(24,2) DEFAULT " + GameValues.STATISTICS.STARTING_REQUIRED_EXP + ","
					+ "games-played INT(100) DEFAULT 0,"
					+ "solo-wins INT(100) DEFAULT 0,"
					+ "team-wins INT(100) DEFAULT 0,"
					+ "solo-losses INT(100) DEFAULT 0,"
					+ "team-losses INT(100) DEFAULT 0,"
					+ "solo-kills INT(100) DEFAULT 0,"
					+ "team-kills INT(100) DEFAULT 0,"
					+ "solo-killstreak INT(100) DEFAULT 0,"
					+ "team-killstreak INT(100) DEFAULT 0,"
					+ "solo-assists INT(100) DEFAULT 0,"
					+ "team-assists INT(100) DEFAULT 0,"
					+ "solo-deaths INT(100) DEFAULT 0,"
					+ "team-deaths INT(100) DEFAULT 0,"
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

	public String getName(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT name FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setMoney(final UUID uuid, final double money) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET money=? WHERE uuid=?");
			ps.setDouble(1, money);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getMoney(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT money FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("money");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0.00;
	}

	public void setTokens(final UUID uuid, final int tokens) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET tokens=? WHERE uuid=?");
			ps.setInt(1, tokens);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getTokens(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT tokens FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("tokens");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addLevel(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET level=? WHERE uuid=?");
			ps.setInt(1, getLevel(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getLevel(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT level FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("level");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET exp=? WHERE uuid=?");
			ps.setDouble(1, getExp(uuid) + exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getExp(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT exp FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("exp");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET exp=? WHERE uuid=?");
			ps.setDouble(1, exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getRequiredExp(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT required_exp FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("required_exp");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setRequiredExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET required_exp=? WHERE uuid=?");
			ps.setDouble(1, exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}


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

	public void addBoughtKit(final UUID uuid, final String kit) {
		final List<String> kits = getBoughtKits(uuid);
		if (!kits.contains(kit)) {
			kits.add(kit);
			setBoughtKits(uuid, String.join(",", kits));
		}
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

	public void addBoughtPerk(final UUID uuid, final String perk) {
		final List<String> perks = getBoughtPerks(uuid);
		if (!perks.contains(perk)) {
			perks.add(perk);
			setBoughtPerks(uuid, String.join(",", perks));
		}
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

	public void setStringData(DataType type, UUID uuid, String data) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET " + type.getConfigPath() + "=? WHERE uuid=?");
			ps.setString(1, data);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public String getStringData(DataType type, UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT " + type.getConfigPath() + " FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(type.getConfigPath());
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void setIntData(DataType type, UUID uuid, int data) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET " + type.getConfigPath() + "=? WHERE uuid=?");
			ps.setInt(1, data);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getIntData(DataType type, UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT " + type.getConfigPath() + " FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(type.getConfigPath());
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setDoubleData(DataType type, UUID uuid, double data) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET " + type.getConfigPath() + "=? WHERE uuid=?");
			ps.setDouble(1, data);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getDoubleData(DataType type, UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT " + type.getConfigPath() + " FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble(type.getConfigPath());
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
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