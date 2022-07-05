package com.github.cybooo.skyblock.commands;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    private final SkyBlockPlugin plugin;

    public SpawnCommand(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tento prikaz neni dostupny z konzole.");
            return false;
        }

        if (plugin.getConfig().getString("spawn.world") == null) {
            player.sendMessage("§cNebylo mozne najit spawn lokaci!");
            return false;
        }
        Location location = new Location(Bukkit.getWorld(plugin.getConfig().getString("spawn.world")),
                plugin.getConfig().getDouble("spawn.x"),
                plugin.getConfig().getDouble("spawn.y"),
                plugin.getConfig().getDouble("spawn.z"),
                (float) plugin.getConfig().getDouble("spawn.yaw"),
                (float) plugin.getConfig().getDouble("spawn.pitch"));
        player.teleport(location);
        return true;
    }
}
