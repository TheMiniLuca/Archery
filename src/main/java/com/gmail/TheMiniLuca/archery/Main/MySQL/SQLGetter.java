package com.gmail.TheMiniLuca.archery.Main.MySQL;

import com.gmail.TheMiniLuca.archery.Main.Main;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.gmail.TheMiniLuca.archery.Main.Main.PlayerStadium;

public class SQLGetter {

    private final Main plugin;
    private final String table;

    public SQLGetter(Main plugin, String table) {
        this.plugin = plugin;
        this.table = table;
    }

    public void createTable() {
        PreparedStatement ps = null;
        try {
            if (table.equalsIgnoreCase("STADIUM")) {
                File newFolder = new File(plugin.getDataFolder() + File.separator + table);
                newFolder.mkdir();
                /*
                ps = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "
                        + table + "(STADIUM INT(100), LOCATION VARCHAR(100), RED VARCHAR(100)," +
                        " BLUE VARCHAR(100), REDSCORE INT(100), BLUESCORE INT(100)" +
                        ", REDTIME INT(100), BLUETIME INT(100), STATE INT(100), TURN VARCHAR(100), TIME INT(100), NUMBER INT(100), PRIMARY KEY (STADIUM))");
                        */
            } else if (table.equalsIgnoreCase("USERDATA")) {
                ps = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "
                        + table + "(NAME VARCHAR(100), UUID VARCHAR(100), LEVEL INT(100), EXP INT(100), COIN INT(100), PRIMARY KEY (NAME))");
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }

    public void Save(FileConfiguration configuration, File file) {
        try {
            configuration.save(file);
        } catch (IOException ignore) {

        }
    }

    public void Insert(Location location, int stadium) {
        File file;
        FileConfiguration configuration;
        file = new File(plugin.getDataFolder() + File.separator + table, stadium + ".yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("stadium.STADIUM", stadium);
        configuration.set("stadium.LOCATION", location);
        Save(configuration, file);
        /*
        try {
            if (!exists(stadium)) {
                PreparedStatement ps = plugin.getConnection().prepareStatement("INSERT INTO " + table + "(STADIUM, LOCATION) VALUES(?,?)");
                ps.setInt(1, stadium);
                ps.setString(2, plugin.SimpleLocation(location));
                ps.executeUpdate();
                ps.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

         */
    }

    public Integer getStadium(String name) {
        return PlayerStadium.getOrDefault(name, null);
        /*
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement("SELECT STADIUM FROM STADIUM WHERE RED=? OR BLUE=?;");
            ps.setString(1, name);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int p = rs.getInt("STADIUM");
                rs.close();
                ps.close();
                return p;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

         */
    }

    public void Insert(Player player) {
        try {
            if (!exists(player.getUniqueId())) {
                PreparedStatement ps = plugin.getConnection().prepareStatement("INSERT INTO " + table + "(NAME, UUID) VALUES(?,?)");
                ps.setString(1, player.getName());
                ps.setString(2, player.getUniqueId().toString());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(int stadium) {
        File file;
        FileConfiguration configuration;
        file = new File(plugin.getDataFolder() + File.separator + table, stadium + ".yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        return configuration.get("stadium.STADIUM") != null;

        /*
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE STADIUM=?");
            ps.setInt(1, stadium);

            ResultSet results = ps.executeQuery();
            if (results.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }

         */
    }

    public boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            ps.setString(1, uuid.toString());

            ResultSet results = ps.executeQuery();
            boolean b = results.next();
            results.close();
            ps.close();
            return b;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setValue(int stadium, Object ob, String value) {
        File file;
        FileConfiguration configuration;
        file = new File(plugin.getDataFolder() + File.separator + table, stadium + ".yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        if (configuration.get("stadium.STADIUM") != null) {
            configuration.set("stadium." + value.toUpperCase(), ob);
            Save(configuration, file);
        }
        /*
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement(
                    "UPDATE " + table + " SET " + value + "=? WHERE STADIUM=?");
            ps.setObject(1, ob);
            ps.setInt(2, stadium);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

         */
    }

    public void setValue(UUID uuid, Object v1, String v2) {
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement(
                    "UPDATE USERDATA SET " + v2 + "=? WHERE UUID=?");
            ps.setObject(1, v1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getValue(int stadium, String value) {
        File file;
        FileConfiguration configuration;
        file = new File(plugin.getDataFolder() + File.separator + table, stadium + ".yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        if (configuration.get("stadium.STADIUM") != null) {
            if (configuration.get("stadium." + value) != null) {
                return configuration.get("stadium." + value);
            } return null;
        } else return null;
        /*
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement(
                    "SELECT " + value + " FROM " + table + " WHERE STADIUM=?");
            ps.setInt(1, stadium);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Object o = rs.getObject(value);
                rs.close();
                return o;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

         */
    }

    public Object getValue(UUID uuid, String value) {
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement(
                    "SELECT " + value + " FROM " + table + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Object o = rs.getObject(value);
                rs.close();
                return o;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getState(UUID uuid, String value) {
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement(
                    "SELECT " + value + " FROM " + table + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int i = rs.getInt(value);
                rs.close();
                return i;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    public ArrayList<String> getSelect() {
        try {
            Statement sta = plugin.getConnection().createStatement();
            ResultSet res = sta.executeQuery(
                    "SELECT * FROM " + table + " ORDER BY STADIUM DESC");
            ArrayList<String> al = new ArrayList<>();
            while (res.next()) {
                String uuid = res.getString("STADIUM");
                al.add(uuid);
            }
            return al;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

     */


    public ArrayList<Integer> getSelect() {
        /*
        try {
            Statement sta = plugin.getConnection().createStatement();
            ResultSet res = sta.executeQuery(
                    "SELECT STADIUM FROM " + table + " WHERE STATE>=1");
            ArrayList<Integer> al = new ArrayList<>();
            while (res.next()) {
                int i = res.getInt("STADIUM");
                al.add(i);
            }
            return al;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

         */
        return null;
    }

    public int getNumber() {
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement(
                    "SELECT COUNT(STADIUM) as COUNT FROM " + table);
            ResultSet rs = ps.executeQuery();
            int points = 0;
            if (rs.next()) {
                points = rs.getInt("COUNT");
                rs.close();
                return points;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getNumber(String s) {
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement(
                    "SELECT " + s + "(STADIUM) as COUNT FROM " + table);
            ResultSet rs = ps.executeQuery();
            int points = 0;
            if (rs.next()) {
                points = rs.getInt("COUNT");
                return points;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void emptyTable() {
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement("TRUNCATE " + table);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(int stadium) {
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement("DELETE FROM " + table + " WHERE STADIUM=?");
            ps.setInt(1, stadium);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
