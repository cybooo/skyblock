package com.github.cybooo.skyblock.listeners;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.achievements.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final SkyBlockPlugin plugin;

    public PlayerListener(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage("§a§l + §a" + player.getName());

        if (plugin.getConfig().getString("spawn.world") != null) {
            Location location = new Location(Bukkit.getWorld(plugin.getConfig().getString("spawn.world")),
                    plugin.getConfig().getDouble("spawn.x"),
                    plugin.getConfig().getDouble("spawn.y"),
                    plugin.getConfig().getDouble("spawn.z"),
                    (float) plugin.getConfig().getDouble("spawn.yaw"),
                    (float) plugin.getConfig().getDouble("spawn.pitch"));
            player.teleport(location);
        }

        // Pro testovací potřeby.
        if (plugin.getEconomy().getBalance(player) == 0) {
            plugin.getEconomy().depositPlayer(player, 100000);
        }

        plugin.getAchievementManager().registerIntoDatabase(player);
        plugin.getPlayerManager().getPlayerData(player.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerManager().saveDataIntoDatabase(event.getPlayer());
        event.setQuitMessage("§c§l- §c" + event.getPlayer().getName());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getWorld().getName().equals("world")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getIslandManager().isInIsland(player, plugin.getIslandManager().getIsland(player))) {
            event.setCancelled(true);
            return;
        }
        plugin.getAchievementManager().addAchievementProgress(player, Achievement.BREAK_10_BLOCKS, 1);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getIslandManager().isInIsland(player, plugin.getIslandManager().getIsland(player))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK ||
                event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        if (!plugin.getIslandManager().isInIsland(player, plugin.getIslandManager().getIsland(player))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (!plugin.getIslandManager().isInIsland(player, plugin.getIslandManager().getIsland(player))) {
                event.setCancelled(true);
            }
        }
    }

}
