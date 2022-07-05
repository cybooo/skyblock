package com.github.cybooo.skyblock.menu;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.achievements.Achievement;
import com.github.cybooo.skyblock.achievements.AchievementData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AchievementsMenu implements Listener {

    private final SkyBlockPlugin plugin;

    public AchievementsMenu(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "Achievementy");
        int i = 0;
        AchievementData achievementData = plugin.getAchievementManager().getData(player);
        for (Achievement achievement : Achievement.values()) {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§6§l" + achievement.getName());

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7§o" + achievement.getDescription());
            lore.add("");
            if (achievementData.getPendingAchievements().containsKey(achievement)) {
                lore.add("§fPostup: §e" + achievementData.getPendingAchievements().get(achievement) + "/" + achievement.getGoal());
            } else {
                lore.add("§fPostup: §eDokončen!");
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
            i++;
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Achievementy")) {
            event.setCancelled(true);
        }
    }

}
