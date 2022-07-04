package com.github.cybooo.skyblock.skyblock.menus;

import com.github.cybooo.skyblock.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.skyblock.island.Island;
import com.github.cybooo.skyblock.skyblock.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ControlPanelMenu implements Listener {

    private final SkyBlockPlugin plugin;

    public ControlPanelMenu(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {

        Island island = plugin.getIslandManager().getIsland(player);
        if (island == null) {
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 4 * 9, "Ovládací panel");
        fillBorders(inventory);

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoItemMeta = info.getItemMeta();
        infoItemMeta.setDisplayName("§2§lTvůj ostrov");
        infoItemMeta.setLore(Arrays.asList(
                "",
                "§7Vlastník: §a" + island.getOwner(),
                "§7Datum vytvoření: §a" + Utils.millisToDate(island.getCreatedMillis()),
                ""
        ));
        info.setItemMeta(infoItemMeta);

        ItemStack teleport = new ItemStack(Material.COMPASS);
        ItemMeta teleportMeta = teleport.getItemMeta();
        teleportMeta.setDisplayName("§6§lTeleport na ostrov");
        teleportMeta.setLore(Arrays.asList("", "§eKlikni pro teleport na tvůj ostrov."));
        teleport.setItemMeta(teleportMeta);

        ItemStack memberList = new ItemStack(Material.PAPER);
        ItemMeta memberListMeta = memberList.getItemMeta();
        memberListMeta.setDisplayName("§6§lSeznam členů");
        memberListMeta.setLore(Arrays.asList("", "§eKlikni pro zobrazení členů ostrova."));
        memberList.setItemMeta(memberListMeta);

        inventory.setItem(4, info);
        inventory.setItem(10, teleport);
        inventory.setItem(11, memberList);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Ovládací panel")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Seznam členů":
                    break;
                case "Teleport na ostrov":
                    player.teleport(plugin.getIslandManager().getIsland(player).getSpawnLocation());
                    break;
            }
        }
    }

    private void fillBorders(Inventory inventory) {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;

        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        int size = inventory.getSize();
        int rows = size / 9;

        if (rows >= 3) {
            for (int i = 0; i <= 8; i++) {
                inventory.setItem(i, item);
            }

            for (int s = 8; s < (inventory.getSize() - 9); s += 9) {
                int lastSlot = s + 1;
                inventory.setItem(s, item);
                inventory.setItem(lastSlot, item);
            }

            for (int lr = (inventory.getSize() - 9); lr < inventory.getSize(); lr++) {
                inventory.setItem(lr, item);
            }
        }
    }

}
