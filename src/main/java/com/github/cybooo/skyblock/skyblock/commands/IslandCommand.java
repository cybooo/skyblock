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
                player.sendTitle("§6§lSkyBlock", "§7Vytvářím tvůj ostrov..", 5, 40, 5);
                plugin.getIslandManager().createIsland(player);
            } else {
                player.sendMessage("§cTvůj ostrov není na tomto serveru dostupný!");
            }
        } else {
            player.sendMessage("§aTeleportuji na tvůj ostrov..");
            player.teleport(island.getSpawnLocation());
        }
        return true;
    }

}
