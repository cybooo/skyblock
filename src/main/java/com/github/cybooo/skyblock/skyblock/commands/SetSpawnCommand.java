package com.github.cybooo.skyblock.skyblock.commands;

import com.github.cybooo.skyblock.skyblock.SkyBlockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private SkyBlockPlugin plugin;

    public SetSpawnCommand(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tento prikaz neni dostupny z konzole.");
            return false;
        }
        Player player = (Player) sender;
        if (player.isOp()) {
            plugin.getConfig().set("spawn.world", player.getWorld().getName());
            plugin.getConfig().set("spawn.x", player.getLocation().getX());
            plugin.getConfig().set("spawn.y", player.getLocation().getY());
            plugin.getConfig().set("spawn.z", player.getLocation().getZ());
            plugin.getConfig().set("spawn.yaw", player.getLocation().getYaw());
            plugin.getConfig().set("spawn.pitch", player.getLocation().getPitch());
            plugin.saveConfig();
            player.sendMessage("§aSpawn nastaven!");
        } else {
            player.sendMessage("§cNemas opravneni!");
        }
        return true;
    }
}
