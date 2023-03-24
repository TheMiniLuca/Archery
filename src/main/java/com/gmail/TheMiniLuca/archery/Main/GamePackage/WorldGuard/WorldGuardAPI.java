package com.gmail.TheMiniLuca.archery.Main.GamePackage.WorldGuard;


import com.gmail.TheMiniLuca.archery.Main.Main;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.management.BufferPoolMXBean;


public class WorldGuardAPI {

    private final Main plugin = Main.getPlugin(Main.class);

    private final String name;
    private final Player player;

    public WorldGuardAPI(String name, Player player) {
        this.name = name;
        this.player = player;
    }




    public void createRegion() {
        Location loc = player.getLocation();
        BlockVector3 min = null;
        BlockVector3 max = null;
        if (player.getFacing().name().contains("SOUTH")) {
            min = BlockVector3.at(loc.getBlockX() - 13, 0, loc.getBlockZ() + 210);
            max = BlockVector3.at(loc.getBlockX() + 13, 255, loc.getBlockZ() - 5);
        } else if (player.getFacing().name().contains("NORTH")){
            min = BlockVector3.at(loc.getBlockX() - 13, 0, loc.getBlockZ() - 210);
            max = BlockVector3.at(loc.getBlockX() + 13, 255, loc.getBlockZ() + 5);
        }
        assert min != null;
        ProtectedRegion region = new ProtectedCuboidRegion(name, min, max);
        region.setFlag(Flags.PVP, StateFlag.State.DENY);
        region.setFlag(Flags.BUILD, StateFlag.State.DENY);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(player.getWorld());
        RegionManager regions = container.get(world);
        assert regions != null;
        regions.addRegion(region);
    }

    public void createCross() {
        Location loc = player.getLocation();
        BlockVector3 min = null;
        BlockVector3 max = null;
        if (player.getFacing().name().contains("SOUTH")) {
            min = BlockVector3.at(loc.getBlockX() - 13, 0, loc.getBlockZ() + 1);
            max = BlockVector3.at(loc.getBlockX() + 13, 255, loc.getBlockZ() - 5);
        } else if (player.getFacing().name().contains("NORTH")){
            min = BlockVector3.at(loc.getBlockX() - 13, 0, loc.getBlockZ() - 1);
            max = BlockVector3.at(loc.getBlockX() + 13, 255, loc.getBlockZ() + 5);
        }
        assert min != null;
        ProtectedRegion region = new ProtectedCuboidRegion(name, min, max);
        region.setFlag(Flags.PVP, StateFlag.State.DENY);
        region.setFlag(Flags.BUILD, StateFlag.State.DENY);
        region.setFlag(Flags.USE, StateFlag.State.ALLOW);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(player.getWorld());
        RegionManager regions = container.get(world);
        assert regions != null;
        regions.addRegion(region);
    }

}
