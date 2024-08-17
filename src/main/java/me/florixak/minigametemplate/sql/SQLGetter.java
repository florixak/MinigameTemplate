package me.florixak.minigametemplate.sql;

import me.florixak.minigametemplate.MinigameTemplate;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLGetter {

	private final Connection conn;
	private final String table;

	public SQLGetter(final MinigameTemplate plugin) {
		this.conn = plugin.getSQL().getConnection();

		this.table = "uhcrevamp";
		createTable();
	}

	public void createTable() {
		final PreparedStatement ps;

		try {
			ps = this.conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + this.table + " "
					+ "(uuid VARCHAR(100) PRIMARY KEY,"
					+ "name VARCHAR(100),"
					+ "money DECIMAL(24,2) DEFAULT " + 0 + ","
					+ "level INT(100) DEFAULT " + 0 + ","
					+ "exp DECIMAL(24,2) DEFAULT 0,"
					+ "required_exp DECIMAL(24,2) DEFAULT " + 100 + ","
					+ "games_played INT(100) DEFAULT 0,"
					+ "wins INT(100) DEFAULT 0,"
					+ "losses INT(100) DEFAULT 0,"
					+ "kills INT(100) DEFAULT 0,"
					+ "killstreak INT(100) DEFAULT 0,"
					+ "assists INT(100) DEFAULT 0,"
					+ "deaths INT(100) DEFAULT 0,"
					+ "kits VARCHAR(100) DEFAULT '',"
					+ "perks VARCHAR(100) DEFAULT '')"
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

	public void addLevel(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET uhc_level=? WHERE uuid=?");
			ps.setInt(1, getLevel(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getLevel(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT uhc_level FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("uhc_level");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, getExp(uuid) + exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getExp(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT uhc_exp FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("uhc_exp");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getRequiredExp(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT required_uhc_exp FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("required_uhc_exp");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setRequiredExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET required_uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void setGamesPlayed(final UUID uuid, final int gamesPlayed) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET games_played=? WHERE uuid=?");
			ps.setInt(1, gamesPlayed);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getGamesPlayed(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT games_played FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("games_played");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addWin(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET wins=? WHERE uuid=?");
			ps.setInt(1, getWins(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getWins(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT wins FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("wins");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addLose(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET losses=? WHERE uuid=?");
			ps.setInt(1, getLosses(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getLosses(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT losses FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("losses");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addKill(final UUID uuid, final int kills) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET kills=? WHERE uuid=?");
			ps.setInt(1, getKills(uuid) + kills);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getKills(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT kills FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("kills");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setKillstreak(final UUID uuid, final int killstreak) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET killstreak=? WHERE uuid=?");
			ps.setInt(1, killstreak);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getKillstreak(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT killstreak FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("killstreak");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addAssist(final UUID uuid, final int assists) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET assists=? WHERE uuid=?");
			ps.setInt(1, getAssists(uuid) + assists);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getAssists(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT assists FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("assists");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setAssists(final UUID uuid, final int assists) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET assists=? WHERE uuid=?");
			ps.setInt(1, assists);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void addDeath(final UUID uuid, final int deaths) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET deaths=? WHERE uuid=?");
			ps.setInt(1, getDeaths(uuid) + deaths);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getDeaths(final UUID uuid) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("SELECT deaths FROM " + this.table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("deaths");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setDeaths(final UUID uuid, final int deaths) {
		try {
			final PreparedStatement ps = this.conn.prepareStatement("UPDATE " + this.table + " SET deaths=? WHERE uuid=?");
			ps.setInt(1, deaths);
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