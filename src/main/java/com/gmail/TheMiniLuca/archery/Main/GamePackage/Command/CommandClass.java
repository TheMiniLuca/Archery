package com.gmail.TheMiniLuca.archery.Main.GamePackage.Command;

import com.gmail.TheMiniLuca.archery.Main.GamePackage.WorldGuard.WorldGuardAPI;
import com.gmail.TheMiniLuca.archery.Main.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandClass implements CommandExecutor {

    private final Main plugin = Main.getPlugin(Main.class);


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equalsIgnoreCase("stadium") && sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = player.getLocation();
            if (!player.isOp()) {
                player.sendMessage(Main.format("you do not have Permission"));
                return false;
            }
            if (args.length == 0) {
                player.sendMessage(Main.format("&e/&fstadium [<Number>] &7-&o <number> Stadium Location Settings. on sender's Location!"));
                player.sendMessage(Main.format("&e/&fstadium tp [<Number>] &7-&o <number> Stadium Location Teleport."));
                return false;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("tp")) {
                    if (isInteger(args[1])) {
                        int stadium = Integer.parseInt(args[1]);
                        if (plugin.data.exists(stadium)) {
                            player.teleport((Location) plugin.data.getValue(stadium, "LOCATION"));
                            player.sendMessage(Main.format("Teleport! " + stadium + "'s stadium!"));
                        } else {
                            player.sendMessage(Main.format("&cthis Stadium not exists it!"));
                        }
                    } else {
                        player.sendMessage(Main.format("&cThe value is not an int"));
                    }
                }
                if (args[0].equalsIgnoreCase("allDelete")) {
                    if (args[1].equalsIgnoreCase("confirm")) {
                        player.sendMessage(Main.format("&aSuccess!"));
                        plugin.data.emptyTable();
                    }
                }
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("show")) {
                    if (plugin.isWithinRegion(loc) != null) {
                        player.sendMessage(String.valueOf(Objects.requireNonNull(plugin.isWithinRegions(loc))));
                        player.sendMessage(String.valueOf(plugin.isWithinRegions(loc).size()));
                    } else {
                        player.sendMessage(Main.format("null"));
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("allDelete")) {
                    player.sendMessage(Main.format("&cif you really want allDelete Value? please run Command: &8/setStadium allDelete confirm"));
                    return false;
                }

                if (isInteger(args[0])) {
                    int stadium = Integer.parseInt(args[0]);
                    if (plugin.data.exists(stadium)) {
                        player.sendMessage(Main.format("&cthis Stadium exists it!\n&fPrevious Value: \n &6&l > &f" + stadium + " stadium 's Location &7: &f"
                                + plugin.data.getValue(stadium, "LOCATION") + "\n&fValue &7- \n &6&l > &fLocation &7: &f"
                                + plugin.SimpleLocation(loc) + "\n &6&l > &fNumber of stadium &7: &f" + stadium));
                        plugin.data.remove(stadium);
                        plugin.data.Insert(loc, stadium);
                    } else {
                        player.sendMessage(Main.format("&athis Stadium Insert! \n&fValue &7- \n &6&l > &fLocation &7: &f"
                                + plugin.SimpleLocation(loc) + "\n &6&l > &fNumber of stadium &7: &f" + stadium));
                        plugin.data.Insert(loc, stadium);
                    }
                    WorldGuardAPI guard = new WorldGuardAPI(stadium + "-stadium", player);
                    guard.createRegion();
                    WorldGuardAPI cross = new WorldGuardAPI(stadium + "-Cross", player);
                    cross.createCross();
                    return true;
                } else {
                    player.sendMessage(Main.format("&cThe value is not an int"));
                }
            }
        }
        return false;
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
