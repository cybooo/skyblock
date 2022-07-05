package com.github.cybooo.skyblock.commands;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyCommand implements CommandExecutor {

    private final SkyBlockPlugin plugin;

    public MoneyCommand(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tento prikaz neni dostupny z konzole.");
            return false;
        }
        player.sendMessage("§aStav konta: §2§l" + plugin.getEconomy().getBalance(player) + "§a Mincí");
        return true;
    }
}
