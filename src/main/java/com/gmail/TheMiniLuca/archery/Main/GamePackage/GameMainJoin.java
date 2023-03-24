package com.gmail.TheMiniLuca.archery.Main.GamePackage;

import com.gmail.TheMiniLuca.archery.Main.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class GameMainJoin implements Listener {

    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.user.Insert(player);
        player.teleport(new Location(Bukkit.getWorld("world"), -163, 9, 234));
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.SATURATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 9), true);
        for (Player l : Bukkit.getOnlinePlayers()) {
            if (l != player) {
                player.showPlayer(Main.getPlugin(Main.class), l);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) this.cancel();
                createBored(player);
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 2L, 20 * 3L);
    }

    @SuppressWarnings("deprecation")
    public void createBored(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("HubScoreboard-1", "dummy", "   §f§lARCHERY   ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = objective.getScore("§f");
        score.setScore(7);
        Score score1 = objective.getScore(Main.format("&e[Archery]"));
        score1.setScore(6);
        Score score2 = objective.getScore(Main.format("&f&c"));
        score2.setScore(5);
        Score score3 = objective.getScore(Main.format("&fExp : &3" + plugin.user.getState(player.getUniqueId(), "EXP")));
        score3.setScore(4);
        Score score4 = objective.getScore(Main.format("&7&8"));
        score4.setScore(3);
        Score score5 = objective.getScore(Main.format("&fCoin: &6" + plugin.user.getState(player.getUniqueId(), "COIN")));
        score5.setScore(2);
        Score score6 = objective.getScore(Main.format("&e"));
        score6.setScore(1);
        Score score7 = objective.getScore(Main.format("&eArchery Server!"));
        score7.setScore(0);

        player.setScoreboard(scoreboard);
    }
}
