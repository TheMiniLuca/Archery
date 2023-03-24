package com.gmail.TheMiniLuca.archery.Main;

import com.gmail.TheMiniLuca.archery.Main.GamePackage.Command.CommandClass;
import com.gmail.TheMiniLuca.archery.Main.GamePackage.GameMain;
import com.gmail.TheMiniLuca.archery.Main.GamePackage.GameMainJoin;
import com.gmail.TheMiniLuca.archery.Main.GamePackage.Shop.Trail.Trail;
import com.gmail.TheMiniLuca.archery.Main.MySQL.SQLGetter;
import com.gmail.TheMiniLuca.archery.Main.MySQL.Stadium;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public final class Main extends JavaPlugin implements @NotNull Listener {

    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    final String username = "root"; // Enter in your db username
    final String password = "xY^RG0j7"; // Enter your password for the db
    final String url = "jdbc:mysql://localhost:3306/archery?useSSL=false"; // Enter URL with db name

    static Connection connection; //This is the variable we will use to connect to database

    public Connection getConnection() {
        return connection;
    }

    public SQLGetter data = new SQLGetter(this, "STADIUM");
    public SQLGetter user = new SQLGetter(this, "USERDATA");

    public Integer MaxTurn = 24;

    public String encodeItem(Location loc) {
        YamlConfiguration config = new YamlConfiguration();

        config.set("location", loc);
        return config.saveToString();
    }

    public Location decodeItem(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(string);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return (Location) config.get("location");
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(plugin instanceof WorldEditPlugin)) {
            return null;
        }
        return (WorldEditPlugin) plugin;
    }

    public static HashMap<String, Integer> PlayerStadium = new HashMap<>();

    @Override
    public void onEnable() {
        reloadConfig();
        saveDefaultConfig();
        getLogger().info(format("&aArchery Plugins Enable"));
        this.getServer().getPluginManager().registerEvents(new GameMain(), this);
        this.getServer().getPluginManager().registerEvents(new Trail(), this);
        this.getServer().getPluginManager().registerEvents(new GameMainJoin(), this);
        try { // try catch to get any SQL errors (for example connections errors)
            connection = DriverManager.getConnection(url, username, password);
            user.createTable();
            getLogger().info(format("&adatabase is connect"));
            // with the method getConnection() from DriverManager, we're trying to set
            // the connection's url, username, password to the variables we made earlier and
            // trying to get a connection at the same time. JDBC allows us to do this.
        } catch (SQLException e) { // catching errors
            getServer().getPluginManager().disablePlugin(this);
            e.printStackTrace(); // prints out SQLException errors to the console (if any)
        }
        File newFolder = new File(getDataFolder() + File.separator + "STADIUM");
        newFolder.mkdir();
        Objects.requireNonNull(getCommand("stadium")).setExecutor(new CommandClass());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 450; i++) {
                    File file;
                    FileConfiguration configuration;
                    file = new File(getDataFolder() + File.separator + "STADIUM", i + ".yml");
                    configuration = YamlConfiguration.loadConfiguration(file);
                    if (configuration.get("stadium.STADIUM") != null) {
                        if (configuration.get("stadium.STATE") != null) {
                            Stadium stadium = new Stadium(i);
                            int state = stadium.getValue("STATE");
                            int time = stadium.getValue("TIME");
                            if (state > 1 && state != 3) {
                                if (stadium.getBlue() == null) {
                                    stadium.Victory(stadium.getRed());
                                    continue;
                                }

                                if (stadium.getRed() == null) {
                                    stadium.Victory(stadium.getBlue());
                                    continue;
                                }
                            }
                            if (state == 4) {
                                stadium.sendInfo();
                            }
                            if (state == 3) {
                                if (time > 0) {
                                    stadium.setValue(stadium.getValue("TIME") - 1, "TIME");
                                } else if ((int) data.getValue(i, "TIME") == 0) {
                                    try {
                                        stadium.getRed().getInventory().clear();
                                        PlayerStadium.remove(stadium.getRed().getName());
                                        stadium.getRed().teleport(new Location(Bukkit.getWorld("world"), -163, 9, 234));
                                    } catch (NullPointerException ignore) {

                                    }
                                    try {
                                        stadium.getBlue().getInventory().clear();
                                        PlayerStadium.remove(stadium.getBlue().getName());
                                        stadium.getBlue().teleport(new Location(Bukkit.getWorld("world"), -163, 9, 234));
                                    } catch (NullPointerException ignore) {

                                    }
                                    stadium.setValue(null, "RED");
                                    stadium.setValue(null, "BLUE");
                                    stadium.setValue(null, "REDSCORE");
                                    stadium.setValue(null, "REDTIME");
                                    stadium.setValue(null, "BLUESCORE");
                                    stadium.setValue(null, "BLUETIME");
                                    stadium.setValue(null, "STATE");
                                    stadium.setValue(null, "TURN");
                                    stadium.setValue(null, "TIME");
                                    stadium.setValue(null, "NUMBER");
                                    continue;
                                }
                            }
                            if (state == 2) {
                                stadium.sendInfo();
                                if (stadium.getValue("NUMBER") == MaxTurn) {
                                    stadium.sendStadium(Main.format("&c화살을 모두 사용해 게임이 종료되었습니다!"));
                                    if (stadium.getValue("REDSCORE") != stadium.getValue("BLUESCORE")) {
                                        if (stadium.getValue("REDSCORE") > stadium.getValue("BLUESCORE")) {
                                            stadium.Victory(stadium.getRed());
                                        } else {
                                            stadium.Victory(stadium.getBlue());
                                        }
                                    } else
                                        stadium.draw();
                                }
                                if (((String) data.getValue(i, "TURN")).equalsIgnoreCase("RED")) {
                                    if (stadium.getValue("REDTIME") > 0) {
                                        stadium.setValue(stadium.getValue("REDTIME") - 1, "REDTIME");
                                    } else {
                                        stadium.setValue(0, "REDTIME");
                                        stadium.sendStadium(Main.format("&c시간 초과!"));
                                        stadium.Victory(stadium.getBlue());
                                    }
                                }
                                if (((String) data.getValue(i, "TURN")).equalsIgnoreCase("BLUE")) {
                                    if (stadium.getValue("BLUETIME") > 0) {
                                        stadium.setValue(stadium.getValue("BLUETIME") - 1, "BLUETIME");
                                    } else {
                                        stadium.setValue(0, "BLUETIME");
                                        stadium.sendStadium(Main.format("&c시간 초과!"));
                                        stadium.Victory(stadium.getRed());
                                    }
                                }
                                if (time > 0) {
                                    if (time == 10) {
                                        stadium.sendStadium("§eGame End in §6" + stadium.getValue("TIME") + " §eSeconds");
                                        stadium.playAllSound(Sound.BLOCK_LEVER_CLICK, 2.0F, 1.0F);
                                    }
                                    if (time <= 5) {
                                        stadium.sendStadium("§eGame End in §c" + stadium.getValue("TIME") + " §eSeconds");
                                        stadium.playAllSound(Sound.BLOCK_LEVER_CLICK, 2.0F, 1.0F);
                                    }
                                    stadium.setValue(stadium.getValue("TIME") - 1, "TIME");
                                } else if ((int) data.getValue(i, "TIME") == 0) {
                                    stadium.setValue(3, "STATE");
                                    stadium.setValue(5, "TIME");
                                    if (stadium.getValue("REDSCORE") != stadium.getValue("BLUESCORE")) {
                                        if (stadium.getValue("REDSCORE") > stadium.getValue("BLUESCORE")) {
                                            stadium.Victory(stadium.getRed());
                                        } else {
                                            stadium.Victory(stadium.getBlue());
                                        }
                                    } else stadium.draw();
                                }
                            }
                            if (state == 1) {
                                if (time > 0) {
                                    if (time == 10) {
                                        stadium.sendStadium("§eStart in §6" + stadium.getValue("TIME") + " §eSeconds");
                                        stadium.playAllSound(Sound.BLOCK_LEVER_CLICK, 1.0F, 1.0F);
                                    }
                                    if (time <= 5) {
                                        stadium.sendStadium("§eStart in §c" + stadium.getValue("TIME") + " §eSeconds");
                                        stadium.playAllSound(Sound.BLOCK_LEVER_CLICK, 1.0F, 1.0F);
                                    }
                                    stadium.setValue(stadium.getValue("TIME") - 1, "TIME");
                                } else if ((int) data.getValue(i, "TIME") == 0) {
                                    stadium.setValue(2, "STATE");
                                    stadium.setValue(182, "TIME");
                                    stadium.setValue(90, "REDTIME");
                                    stadium.setValue(90, "BLUETIME");
                                    stadium.setValue("RED", "TURN");
                                    stadium.sendStadium(Main.format("&cRED &fTEAM &e 차례입니다!"));
                                    stadium.playAllSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                                    stadium.getRed().teleport((Location) data.getValue(i, "LOCATION"));
                                    try {
                                        stadium.getRed().getInventory().clear();
                                    } catch (NullPointerException ignore) {
                                    }
                                    try {
                                        stadium.getBlue().getInventory().clear();
                                    } catch (NullPointerException ignore) {
                                    }
                                    Player red = stadium.getRed();
                                    Player blue = stadium.getBlue();
                                    if (red != null) {
                                        GiveBow(stadium.getRed());
                                        setRed(red);
                                    }
                                    if (blue != null) {
                                        GiveBow(stadium.getBlue());
                                        setBlue(blue);
                                    }
                                    stadium.getBlue().setGameMode(GameMode.SPECTATOR);
                                }
                            }

                        }
                    }
                }
            }
        }, 0, 20);
    }

    public void setRed(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta Helmet = (LeatherArmorMeta) helmet.getItemMeta();
        Helmet.setColor(Color.fromRGB(200, 0, 0));
        helmet.setItemMeta(Helmet);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta Chest = (LeatherArmorMeta) chest.getItemMeta();
        Chest.setColor(Color.fromRGB(200, 0, 0));
        chest.setItemMeta(Chest);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta Leggings = (LeatherArmorMeta) leggings.getItemMeta();
        Leggings.setColor(Color.fromRGB(200, 0, 0));
        leggings.setItemMeta(Leggings);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta im = (LeatherArmorMeta) boots.getItemMeta();
        im.setColor(Color.fromRGB(200, 0, 0));
        boots.setItemMeta(im);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chest);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    public void setBlue(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta Helmet = (LeatherArmorMeta) helmet.getItemMeta();
        Helmet.setColor(Color.fromRGB(0, 0, 200));
        helmet.setItemMeta(Helmet);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta Chest = (LeatherArmorMeta) chest.getItemMeta();
        Chest.setColor(Color.fromRGB(0, 0, 200));
        chest.setItemMeta(Chest);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta Leggings = (LeatherArmorMeta) leggings.getItemMeta();
        Leggings.setColor(Color.fromRGB(0, 0, 200));
        leggings.setItemMeta(Leggings);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta im = (LeatherArmorMeta) boots.getItemMeta();
        im.setColor(Color.fromRGB(0, 0, 200));
        boots.setItemMeta(im);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chest);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }


    public void GiveBow(Player player) {
        ItemStack is = new ItemStack(Material.BOW);
        ItemMeta im = is.getItemMeta();
        im.setUnbreakable(true);
        is.setItemMeta(im);
        player.getInventory().setItem(0, is);
        player.getInventory().setItem(1, new ItemStack(Material.ARROW, 12));
    }

    public String isWithinRegion(Location loc) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        for (ProtectedRegion region : set) {
            return region.getId();
        }
        return null;
    }

    public ArrayList<String> isWithinRegions(Location loc) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        ArrayList<String> al = new ArrayList<>();
        for (ProtectedRegion region : set) {
            al.add(region.getId());
        }
        return al;
    }

    public String SimpleLocation(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        String world = loc.getWorld().getName();
        return x + ":" + y + ":" + z + ":" + world;
    }

    public Location SimpleLocation(String loc) {
        String[] s = loc.split(":");
        double x = Double.parseDouble(s[0]);
        double y = Double.parseDouble(s[1]);
        double z = Double.parseDouble(s[2]);
        String world = s[3];
        return new Location(Bukkit.getWorld(world), x, y, z);
    }


    @Override
    public void onDisable() {
        getLogger().info(format("&cArchery Plugins Disable"));

    }
}
