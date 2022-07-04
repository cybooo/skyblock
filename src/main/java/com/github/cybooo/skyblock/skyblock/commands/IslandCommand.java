package com.github.cybooo.skyblock.skyblock.commands;

import com.github.cybooo.skyblock.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.skyblock.island.Island;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommand implements CommandExecutor {

    private final SkyBlockPlugin plugin;

    public IslandCommand(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tento prikaz neni dostupny z konzole.");
            return false;
        }
        Player player = (Player) sender;
        Island island = plugin.getIslandManager().getIsland(player);
        if (island == null) {
            if (!plugin.getIslandManager().hasIsland(player)) {
                player.sendMessage("§aVytvarim tvuj ostrov..");
                plugin.getIslandManager().createIsland(player);
            } else {
                player.sendMessage("§cTvuj ostrov neni na tomto serveru dostupny!");
            }
        } else {
            player.teleport(island.getSpawnLocation());
        }
        return true;
    }

}
