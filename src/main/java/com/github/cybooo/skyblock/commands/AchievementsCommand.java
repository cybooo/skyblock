package com.github.cybooo.skyblock.commands;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AchievementsCommand implements CommandExecutor {

    private final SkyBlockPlugin plugin;

    public AchievementsCommand(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tento prikaz neni dostupny z konzole.");
            return false;
        }
        plugin.getMenuManager().getAchievementsMenu().open(player);
        return true;
    }
}
