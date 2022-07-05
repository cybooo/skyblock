package com.github.cybooo.skyblock.menu;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.island.Island;
import com.github.cybooo.skyblock.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MemberListMenu implements Listener {

    private final SkyBlockPlugin plugin;

    public MemberListMenu(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        Island island = plugin.getIslandManager().getIsland(player);
        if (island == null) {
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "Seznam členů");
        Utils.fillBorders(inventory);

        ItemStack owner = new ItemStack(Material.PAPER);
        ItemMeta ownerMeta = owner.getItemMeta();
        ownerMeta.setDisplayName("§2§l" + island.getOwner());
        owner.setItemMeta(ownerMeta);
        inventory.setItem(10, owner);

        int i = 11;
        for (String member : island.getMembers()) {
            ItemStack memberItem = new ItemStack(Material.PAPER);
            ItemMeta memberMeta = memberItem.getItemMeta();
            memberMeta.setDisplayName("§2§l" + member);
            memberMeta.setLore(Arrays.asList("", "§aKlikni pro vyhození!"));
            memberItem.setItemMeta(memberMeta);
            inventory.setItem(i, memberItem);
            i++;
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Seznam členů")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            if (event.getCurrentItem().getType() == Material.PAPER) {
                Player player = (Player) event.getWhoClicked();
                Island island = plugin.getIslandManager().getIsland(player);
                if (island == null) {
                    return;
                }
                String member = event.getCurrentItem().getItemMeta().getDisplayName().replace("§2§l", "");
                if (member.equals(island.getOwner())) {
                    return;
                }
                if (island.getMembers().remove(member)) {
                    player.sendMessage("§aHráč §2§l" + member + " §abyl vyhozen z ostrova!");
                    Player target = Bukkit.getPlayer(member);
                    if (target != null) {
                        target.sendMessage("§aByl jsi vyhozen z ostrova hráče §2§l" + player.getName() + "§a!");
                        Location location = new Location(Bukkit.getWorld(plugin.getConfig().getString("spawn.world")),
                                plugin.getConfig().getDouble("spawn.x"),
                                plugin.getConfig().getDouble("spawn.y"),
                                plugin.getConfig().getDouble("spawn.z"),
                                (float) plugin.getConfig().getDouble("spawn.yaw"),
                                (float) plugin.getConfig().getDouble("spawn.pitch"));
                        target.teleport(location);
                    }
                    plugin.getIslandManager().removeMemberFromIsland(member, island);
                } else {
                    player.sendMessage("§cHráč §2§l" + member + " §cnebyl nalezen v listu členů!");
                }
                player.closeInventory();
            }
        }
    }

}
