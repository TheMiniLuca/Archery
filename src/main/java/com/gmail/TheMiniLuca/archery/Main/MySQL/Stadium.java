package com.gmail.TheMiniLuca.archery.Main.MySQL;

import com.gmail.TheMiniLuca.archery.Main.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stadium {

    private final Main plugin = Main.getPlugin(Main.class);
    private final Integer stadium;

    public Stadium(int stadium) {
        this.stadium = stadium;
    }

    public Player getRed() throws NullPointerException {
        Player red = null;
        if (plugin.data.getValue(stadium, "RED") != null)
            red = Bukkit.getPlayer(String.valueOf(plugin.data.getValue(stadium, "RED")));
        else return null;
        if (red == null) return null;
        if (!red.isOnline()) return null;
        return red;
    }

    public Player getBlue() {
        Player blue = null;
        if (plugin.data.getValue(stadium, "BLUE") != null)
            blue = Bukkit.getPlayer(String.valueOf(plugin.data.getValue(stadium, "BLUE")));
        else return null;
        if (blue == null) return null;
        if (!blue.isOnline()) return null;
        return blue;
    }

    public void sendInfo() {
        Player red = null;
        Player blue = null;
        if (plugin.data.getValue(stadium, "RED") != null)
            red = Bukkit.getPlayer(String.valueOf(plugin.data.getValue(stadium, "RED")));
        if (plugin.data.getValue(stadium, "BLUE") != null)
            blue = Bukkit.getPlayer(String.valueOf(plugin.data.getValue(stadium, "BLUE")));
        if (red != null)
            red.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Main.format("§f당신의 팀 : &cRED &f점수 : " + getValue("REDSCORE") + " 남은 시간 : &6" + getValue("REDTIME") + " &f초 상대 점수 : " + getValue("BLUESCORE"))));
        if (blue != null)
            blue.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Main.format("§f당신의 팀 : &9BLUE &f점수 : " + getValue("BLUESCORE") + " 남은 시간 : &6" + getValue("BLUETIME") + " &f초 상대 점수 : " + getValue("REDSCORE"))));
    }

    public void sendStadium(String msg) {
        Player red = null;
        Player blue = null;
        if (plugin.data.getValue(stadium, "RED") != null)
            red = Bukkit.getPlayer(String.valueOf(plugin.data.getValue(stadium, "RED")));
        if (plugin.data.getValue(stadium, "BLUE") != null)
            blue = Bukkit.getPlayer(String.valueOf(plugin.data.getValue(stadium, "BLUE")));
        if (red != null)
            red.sendMessage(msg);
        if (blue != null)
            blue.sendMessage(msg);
    }

    public void Victory(Player player) {
        if (player == null) return;
        setValue(3, "STATE");
        setValue(2, "TIME");
        try {
            getBlue().setGameMode(GameMode.ADVENTURE);
        } catch (NullPointerException ignore) {
        }
        try {
            getRed().setGameMode(GameMode.ADVENTURE);
        } catch (NullPointerException ignore) {
        }
        try {
            if (player.getName().equals(getRed().getName())) {
                Coin(getRed(), 250);
                Exp(getRed(), 25);
            } else {
                Coin(getBlue(), 250);
                Exp(getBlue(), 25);
            }
        } catch (NullPointerException ignore) {
        }
        sendStadium(Main.format("&a&m                                                      "));
        sendStadium(Main.format("                  &f&lARCHERY"));
        sendStadium(Main.format("&6&l            Victory &f- &f" + player.getName()));
        sendStadium(Main.format("&f            &cRED &7- &f" + getValue("REDSCORE") + "  &9BLUE &7- &f" + getValue("BLUESCORE")));
        sendStadium(Main.format("&a&m                                                      "));
    }

    public void draw() {
        setValue(3, "STATE");
        setValue(2, "TIME");
        try {
            getBlue().setGameMode(GameMode.ADVENTURE);
            Coin(getRed(), 100);
            Exp(getRed(), 10);
        } catch (NullPointerException ignore) {
        }
        try {
            getRed().setGameMode(GameMode.ADVENTURE);
            Coin(getBlue(), 100);
            Exp(getBlue(), 10);
        } catch (NullPointerException ignore) {
        }
        sendStadium(Main.format("&a&m                                                      "));
        sendStadium(Main.format("                  &f&lARCHERY"));
        sendStadium(Main.format("&6&l            Victory &f- &e무승부!"));
        sendStadium(Main.format("&f            &cRED &7- &f" + getValue("REDSCORE") + "  &9BLUE &7- &f" + getValue("BLUESCORE")));
        sendStadium(Main.format("&a&m                                                      "));
    }

    public void Exp(Player player, int exp) {
        if (player == null) return;
        int exp1 = plugin.user.getState(player.getUniqueId(), "EXP") + exp;
        plugin.user.setValue(player.getUniqueId(), exp1, "EXP");
        player.sendMessage(Main.format("&f + &3 " + exp + " Archery Experience"));
    }

    public void Coin(Player player, int coin) {
        if (player == null) return;
        int coin1 = plugin.user.getState(player.getUniqueId(), "COIN") + coin;
        plugin.user.setValue(player.getUniqueId(), coin1, "COIN");
        player.sendMessage(Main.format("&f + &6 " + coin + " Archery Coins"));
    }

    public void Shoot(Material m) {
        if (((String) getObject("TURN")).equalsIgnoreCase("RED")) {
            if (m.equals(Material.WHITE_WOOL)) {
                sendStadium(Main.format("&cRED &f" + getRed().getName() + " 님이 &f&n2점&f을 맞췄습니다!"));
                setValue(getValue("REDSCORE") + 2, "REDSCORE");
            } else if (m.equals(Material.BLACK_WOOL)) {
                sendStadium(Main.format("&cRED &f" + getRed().getName() + " 님이 &8&n4점&f을 맞췄습니다!"));
                setValue(getValue("REDSCORE") + 4, "REDSCORE");
            } else if (m.equals(Material.BLUE_WOOL)) {
                sendStadium(Main.format("&cRED &f" + getRed().getName() + " 님이 &9&n6점&f을 맞췄습니다!"));
                setValue(getValue("REDSCORE") + 6, "REDSCORE");
            } else if (m.equals(Material.RED_WOOL)) {
                sendStadium(Main.format("&cRED &f" + getRed().getName() + " 님이 &c&n8점&f을 맞췄습니다!"));
                setValue(getValue("REDSCORE") + 8, "REDSCORE");
            } else if (m.equals(Material.YELLOW_WOOL)) {
                sendStadium(Main.format("&cRED &f" + getRed().getName() + " 님이 &6&n10점&f을 맞췄습니다!"));
                setValue(getValue("REDSCORE") + 10, "REDSCORE");
                Exp(getRed(), 5);
                Coin(getRed(), 50);
            } else {
                sendStadium(Main.format("&cRED &f" + getRed().getName() + " 님이 과녁을 맞추지 못했습니다!"));
            }
            playAllSound(Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
            setValue(4, "STATE");
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                setValue(getValue("NUMBER") + 1, "NUMBER");
                setValue(2, "STATE");
                setValue("BLUE", "TURN");
                sendStadium(Main.format("&9BLUE &fTEAM &e 차례입니다!"));
                playAllSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                try {
                    getBlue().teleport((Location) plugin.data.getValue(stadium, "LOCATION"));
                    getRed().setGameMode(GameMode.SPECTATOR);
                    getBlue().setGameMode(GameMode.ADVENTURE);
                } catch (NullPointerException ignore) {
                    return;
                }


            }, 20 * 2);
            return;
        } else if (((String) getObject("TURN")).equalsIgnoreCase("BLUE")) {
            if (m.equals(Material.WHITE_WOOL)) {
                sendStadium(Main.format("&9BLUE &f" + getBlue().getName() + " 님이 &f&n2점&f을 맞췄습니다!"));
                setValue(getValue("BLUESCORE") + 2, "BLUESCORE");
            } else if (m.equals(Material.BLACK_WOOL)) {
                sendStadium(Main.format("&9BLUE &f" + getBlue().getName() + " 님이 &8&n4점&f을 맞췄습니다!"));
                setValue(getValue("BLUESCORE") + 4, "BLUESCORE");
            } else if (m.equals(Material.BLUE_WOOL)) {
                sendStadium(Main.format("&9BLUE &f" + getBlue().getName() + " 님이 &9&n6점&f을 맞췄습니다!"));
                setValue(getValue("BLUESCORE") + 6, "BLUESCORE");
            } else if (m.equals(Material.RED_WOOL)) {
                sendStadium(Main.format("&9BLUE &f" + getBlue().getName() + " 님이 &c&n8점&f을 맞췄습니다!"));
                setValue(getValue("BLUESCORE") + 8, "BLUESCORE");
            } else if (m.equals(Material.YELLOW_WOOL)) {
                sendStadium(Main.format("&9BLUE &f" + getBlue().getName() + " 님이 &6&n10점&f을 맞췄습니다!"));
                setValue(getValue("BLUESCORE") + 10, "BLUESCORE");
                Exp(getBlue(), 5);
                Coin(getBlue(), 50);
            } else {
                sendStadium(Main.format("&9BLUE &f" + getBlue().getName() + " 님이 과녁을 맞추지 못했습니다!"));
            }
            playAllSound(Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
            setValue(4, "STATE");
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                setValue(2, "STATE");
                setValue("RED", "TURN");
                setValue(getValue("NUMBER") + 1, "NUMBER");
                sendStadium(Main.format("&cRED &fTEAM &e 차례입니다!"));
                playAllSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                try {
                    getRed().teleport((Location) plugin.data.getValue(stadium, "LOCATION"));
                    getBlue().setGameMode(GameMode.SPECTATOR);
                    getRed().setGameMode(GameMode.ADVENTURE);
                } catch (NullPointerException ignore) {

                }

            }, 20 * 2);
            return;
        }
    }

    public void playAllSound(Sound sound, float v1, float v2) {
        Player red = null;
        Player blue = null;
        if (plugin.data.getValue(stadium, "RED") != null)
            red = Bukkit.getPlayer(String.valueOf(plugin.data.getValue(stadium, "RED")));
        if (plugin.data.getValue(stadium, "BLUE") != null)
            blue = Bukkit.getPlayer(String.valueOf(plugin.data.getValue(stadium, "BLUE")));
        if (red != null)
            red.playSound(red.getLocation(), sound, v1, v2);
        if (blue != null)
            blue.playSound(blue.getLocation(), sound, v1, v2);
    }

    public void setValue(Object ob, String value) {
        File file;
        FileConfiguration configuration;
        file = new File(plugin.getDataFolder() + File.separator + "STADIUM", stadium + ".yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        if (configuration.get("stadium.STADIUM") != null) {
            configuration.set("stadium." + value.toUpperCase(), ob);
            plugin.data.Save(configuration, file);
        }
        /*
        try {
            PreparedStatement ps = plugin.getConnection().prepareStatement(
                    "UPDATE STADIUM SET " + value + "=? WHERE STADIUM=?");
            ps.setObject(1, ob);
            ps.setInt(2, stadium);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

         */
    }


    public int getValue(String value) {
        File file;
        FileConfiguration configuration;
        file = new File(plugin.getDataFolder() + File.separator + "STADIUM", stadium + ".yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        if (configuration.get("stadium.STADIUM") != null) {
            if (configuration.get("stadium." + value) != null) {
                return configuration.getInt("stadium." + value);
            }
            return 0;
        } else return 0;
    }

    public Object getObject(String value) {
        File file;
        FileConfiguration configuration;
        file = new File(plugin.getDataFolder() + File.separator + "STADIUM", stadium + ".yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        if (configuration.get("stadium.STADIUM") != null) {
            if (configuration.get("stadium." + value.toUpperCase()) != null) {
                return configuration.get("stadium." + value.toUpperCase());
            }
            return null;
        } else return null;
    }
}
