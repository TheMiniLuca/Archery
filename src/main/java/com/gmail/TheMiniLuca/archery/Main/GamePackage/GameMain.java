package com.gmail.TheMiniLuca.archery.Main.GamePackage;

import com.gmail.TheMiniLuca.archery.Main.Main;
import com.gmail.TheMiniLuca.archery.Main.MySQL.Stadium;
import com.mojang.datafixers.kinds.IdF;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.swing.plaf.nimbus.State;
import java.lang.management.BufferPoolMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.gmail.TheMiniLuca.archery.Main.Main.PlayerStadium;

public class GameMain implements @NotNull Listener {

    private final Main plugin = Main.getPlugin(Main.class);

    public static HashMap<String, Integer> page = new HashMap<>();

    @EventHandler
    public void onEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();
            if (target.getName().equalsIgnoreCase("Game Select")) {
                page.put(player.getName(), 1);
                GUI(player);
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.user.getStadium(player.getName()) != null) {
            int stadium = plugin.user.getStadium(player.getName());
            PlayerStadium.remove(player.getName());
            if (plugin.data.getValue(stadium, "RED") != null) {
                if (((String) plugin.data.getValue(stadium, "RED")).equalsIgnoreCase(player.getName()))
                    plugin.data.setValue(stadium, null, "RED");
            }
            if (plugin.data.getValue(stadium, "BLUE") != null) {
                if (((String) plugin.data.getValue(stadium, "BLUE")).equalsIgnoreCase(player.getName()))
                    plugin.data.setValue(stadium, null, "BLUE");
            }
        }

    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        Player player = (Player) event.getView().getPlayer();
        int slot = event.getSlot() + 1;
        if (event.getView().getTopInventory().equals(event.getClickedInventory())
                && event.getView().getTitle().contains(Main.format("&fGame Select"))) {
            if (!page.containsKey(player.getName())) {
                event.setCancelled(true);
                page.put(player.getName(), 1);
                setPage(player, 1);
                return;
            }
            int i;
            if (page.get(player.getName()) == 1)
                i = slot;
            else
                i = (((page.get(player.getName()) - 1) * 45) + slot);

            event.setCancelled(true);
            if (slot < 46) {
                if (plugin.data.getStadium(player.getName()) != null) {
                    player.sendMessage(Main.format("&c이미 게임에 참가하였습니다. &7&o( stadium #" + plugin.data.getStadium(player.getName()) + " )"));
                    return;
                }
                if (plugin.data.getValue(i, "STADIUM") == null) {
                    player.sendMessage(Main.format("&c설정되지" +
                            " 않은 경기장입니다. &7&o( stadium #" + (i) + " )"));
                    return;
                } else {
                    Stadium stadium = new Stadium(i);
                    if (plugin.data.getValue(i, "RED") != null && plugin.data.getValue(i, "BLUE") != null) {
                        player.sendMessage(Main.format("&c이미 게임이 시작되었습니다. &7&o( stadium #" + (i) + " )"));
                        return;
                    } else if (plugin.data.getValue(i, "RED") == null) {
                        player.teleport((Location) plugin.data.getValue(i, "LOCATION"));
                        plugin.data.setValue(i, player.getName(), "RED");
                        stadium.sendStadium(Main.format("&c" + player.getName() + "&e님이 게임에 참가했습니다!"));

                    } else if (plugin.data.getValue(i, "BLUE") == null) {
                        player.teleport((Location) plugin.data.getValue(i, "LOCATION"));
                        plugin.data.setValue(i, player.getName(), "BLUE");
                        stadium.sendStadium(Main.format("&9" + player.getName() + " &e님이 게임에 참가했습니다!"));
                    }
                    if (plugin.data.getValue(i, "RED") != null
                            && plugin.data.getValue(i, "BLUE") != null) {
                        stadium.setValue(1, "STATE");
                        stadium.setValue(10, "TIME");
                    }
                    PlayerStadium.put(player.getName(), i);
                }
                return;
            }
            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().equals(Back(page.get(player.getName())))) {
                setPage(player, page.get(player.getName()) - 1);
                return;
            }
            if (event.getCurrentItem().equals(Next(page.get(player.getName())))) {
                setPage(player, page.get(player.getName()) + 1);
            }
        }
    }


    private void setPage(Player player, int set) {
        page.put(player.getName(), set);
        Inventory inv = player.getOpenInventory().getTopInventory();
        for (int l = 0; l <= 9 * 5 - 1; l++) {
            if (page.get(player.getName()) == 1)
                inv.setItem(l, Game(l + 1));
            else
                inv.setItem(l, Game(((page.get(player.getName()) - 1) * 45) + l + 1));
        }
        inv.setItem(50, info(page.get(player.getName())));
        inv.setItem(45, Back(page.get(player.getName())));
        inv.setItem(53, Next(page.get(player.getName())));
    }

    private ItemStack Next(int page) {
        ItemStack is = new ItemStack(Material.ARROW);
        ItemMeta im = is.getItemMeta();
        if (page + 1 == 11) {
            return new ItemStack(Material.AIR);
        }
        im.displayName(Component.text(Main.format("&aPage " + (page + 1))));
        is.setItemMeta(im);
        return is;
    }

    private ItemStack Back(int page) {
        ItemStack is = new ItemStack(Material.ARROW);
        ItemMeta im = is.getItemMeta();
        if (page - 1 == 0) {
            return new ItemStack(Material.AIR);
        }
        im.displayName(Component.text(Main.format("&aPage " + (page - 1))));
        is.setItemMeta(im);
        return is;
    }

    private ItemStack info(int page) {
        ItemStack is = new ItemStack(Material.MAP);
        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text(Main.format("&f게임 정보")));
        ArrayList<String> al = new ArrayList<>();
        //al.add(Main.format(" &6&l> &f서버 개수: &70/" + plugin.data.getNumber()));
        al.add(Main.format(" &6&l> &f현재 페이지 : " + page));
        im.setLore(al);
        is.setItemMeta(im);
        return is;
    }

    private ItemStack Game(int stadium) {
        ItemStack is = null;
        ItemMeta im = null;
        if (plugin.data.getValue(stadium, "STADIUM") == null) {
            is = new ItemStack(Material.RED_STAINED_GLASS);
            im = is.getItemMeta();
            im.displayName(Component.text(Main.format("&cStadium #" + stadium)));
        } else if (plugin.data.getValue(stadium, "RED") != null && plugin.data.getValue(stadium, "BLUE") != null) {
            is = new ItemStack(Material.RED_TERRACOTTA);
            im = is.getItemMeta();
            im.displayName(Component.text(Main.format("&aStadium #" + stadium)));
        } else if (plugin.data.getValue(stadium, "RED") != null || plugin.data.getValue(stadium, "BLUE") != null) {
            is = new ItemStack(Material.BLUE_TERRACOTTA);
            im = is.getItemMeta();
            im.displayName(Component.text(Main.format("&aStadium #" + stadium)));
        } else {
            is = new ItemStack(Material.QUARTZ_BLOCK);
            im = is.getItemMeta();
            im.displayName(Component.text(Main.format("&aStadium #" + stadium)));
        }
        int i;
        if (stadium > 64) {
            i = 1;
            //i = (stadium % 64) + 1;
        } else {
            i = stadium;
        }
        is.setAmount(i);
        ArrayList<String> al = new ArrayList<>();
        al.add(Main.format("&8Archery Stadium #" + stadium));
        if (plugin.data.getValue(stadium, "STADIUM") == null)
            al.add(Main.format(" &6&l> &f상태 : &c설정되지 않은 경기장입니다."));
        else if (plugin.data.getValue(stadium, "STATE") == null || (int) plugin.data.getValue(stadium, "STATE") == 1)
            al.add(Main.format(" &6&l> &f상태 : &a게임 시작 준비중.."));
        else if ((int) plugin.data.getValue(stadium, "STATE") == 2 || (int) plugin.data.getValue(stadium, "STATE") == 4)
            al.add(Main.format(" &6&l> &f상태 : &e게임 중..."));
        else if ((int) plugin.data.getValue(stadium, "STATE") == 3)
            al.add(Main.format(" &6&l> &f상태 : &9게임 종료중..."));
        al.add(Main.format("&f플레이어 &7-"));
        if (plugin.data.getValue(stadium, "RED") == null)
            al.add(Main.format(" &c&l> &fRED : &7&o대기중.."));
        else
            al.add(Main.format(" &c&l> &fRED : " + plugin.data.getValue(stadium, "RED")));
        if (plugin.data.getValue(stadium, "BLUE") == null)
            al.add(Main.format(" &9&l> &fBLUE : &7&o대기중.."));
        else
            al.add(Main.format(" &9&l> &fBLUE : " + plugin.data.getValue(stadium, "BLUE")));
        im.setLore(al);
        is.setItemMeta(im);
        return is;
    }

    private void GUI(Player player) {
        Inventory inv =
                Bukkit.createInventory
                        (null, 9 * 6, Main.format("&fGame Select"));
        for (int i = 0; i <= 9 * 5 - 1; i++) {
            inv.setItem(i, Game(i + 1));
        }
        inv.setItem(50, info(1));
        inv.setItem(45, Back(1));
        inv.setItem(53, Next(1));
        player.openInventory(inv);

    }

    HashMap<String, Integer> MoveDelay = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getZ() != event.getTo().getZ() && event.getFrom().getX() != event.getTo().getX()) {
            if (plugin.data.getStadium(player.getName()) == null) {
                if (plugin.isWithinRegion(event.getFrom()) != null) {
                    for (Player l : Bukkit.getOnlinePlayers()) {
                        if (l != player) {
                            if (plugin.isWithinRegion(l.getLocation()) != null)
                                l.hidePlayer(Main.getPlugin(Main.class), player);

                        }
                    }
                    return;
                } else {
                    for (Player l : Bukkit.getOnlinePlayers()) {
                        l.showPlayer(Main.getPlugin(Main.class), player);
                    }
                }

                return;
            }
            if (MoveDelay.containsKey(player.getUniqueId().toString())) {
                if (MoveDelay.get(player.getUniqueId().toString()) <= 0) {
                    Stadium stadium = new Stadium(plugin.data.getStadium(player.getName()));
                    if (stadium.getObject("STATE") == null) {
                        MoveDelay.put(player.getUniqueId().toString(), 2);
                        return;
                    }
                    if (plugin.isWithinRegion(event.getFrom()) == null) {
                        player.teleport((Location) stadium.getObject("LOCATION"));
                        player.sendMessage(Main.format("&c경기장을 벗어날 수 없습니다!"));
                    } else if (plugin.isWithinRegions(event.getFrom()).size() == 1) {
                        if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                            player.teleport((Location) stadium.getObject("LOCATION"));
                            player.sendMessage(Main.format("&c슈팅 라인을 지켜주세요!"));
                        }
                    }
                    MoveDelay.put(player.getUniqueId().toString(), 2);

                }
                MoveDelay.put(player.getUniqueId().toString(), MoveDelay.get(player.getUniqueId().toString()) - 1);
            } else {
                MoveDelay.put(player.getUniqueId().toString(), 2);
            }
        }
    }


    @EventHandler
    public void onBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (plugin.data.getStadium(player.getName()) != null) {
                Stadium stadium = new Stadium(plugin.data.getStadium(player.getName()));
                if (stadium.getValue("STATE") == 4) {
                    event.setCancelled(true);
                    player.sendMessage(Main.format("&c기다려 주세요! "));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), player::updateInventory);
                    return;
                }
                if (stadium.getValue("STATE") == 2) {
                    if (!((stadium.getObject(String.valueOf(stadium.getObject("TURN")))).equals(player.getName()))) {
                        event.setCancelled(true);
                        player.sendMessage(Main.format("&c아직 당신의 차례가 아닙니다."));
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), player::updateInventory);

                    }
                }
            }
        }
    }

    @EventHandler
    public void onHitProjectile(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;
        if (event.getEntity() instanceof Arrow) {
            Arrow Arrow = (Arrow) event.getEntity();
            if (Arrow.getShooter() instanceof Player) {
                Player player = (Player) Arrow.getShooter();
                /*
                if (plugin.isWithinRegion(Arrow.getLocation()) != null)
                    player.sendMessage(Objects.requireNonNull(plugin.isWithinRegion(Arrow.getLocation())));

                 */
                if (plugin.data.getStadium(player.getName()) != null) {
                    Stadium stadium = new Stadium(plugin.data.getStadium(player.getName()));
                    if (stadium.getValue("STATE") == 2) {
                        @NotNull Material block = event.getHitBlock().getType();
                        if ((stadium.getObject(String.valueOf(stadium.getObject("TURN")))).equals(player.getName())) {
                            stadium.Shoot(block);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void shoot(EntityShootBowEvent event) {
        CraftArrow craftArrow = (CraftArrow) event.getProjectile();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (craftArrow.isDead() || craftArrow.isOnGround()) this.cancel();
                craftArrow.setVelocity(craftArrow.getVelocity().add(new Vector(0, 0.05, 0)));
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0L, 1L);


    }


}
