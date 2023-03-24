package com.gmail.TheMiniLuca.archery.Main.GamePackage.Shop.Trail;

import com.gmail.TheMiniLuca.archery.Main.Main;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Trail implements Listener {


    @EventHandler
    public void onProjectileShoot(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Entity entity = event.getEntity();
            Player player = (Player) event.getEntity().getShooter();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
                @Override
                public void run() {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Location loc = event.getEntity().getLocation();
                            if ((entity.isOnGround()) || (entity.isDead())
                                    || entity.getType().equals(EntityType.ENDER_PEARL)) {
                                this.cancel();
                            } else {
                                entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), 0, 0.001, 1, 0, 1);
                            }
                        }
                    }.runTaskTimer(Main.getPlugin(Main.class), 0L, 1L);

                }
            }, 2);

        }

    }

}
