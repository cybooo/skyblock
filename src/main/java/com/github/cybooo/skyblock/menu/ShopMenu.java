package com.github.cybooo.skyblock.menu;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.shop.ShopItem;
import com.github.cybooo.skyblock.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu implements Listener {

    private final SkyBlockPlugin plugin;

    public ShopMenu(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, "Obchod");
        int i = 0;
        for (ShopItem shopItem : ShopItem.values()) {
            ItemStack itemStack = new ItemStack(shopItem.getMaterial());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§6§l" + Utils.formatString(shopItem.getMaterial().toString()));

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§fNákupní cena: §e" + shopItem.getBuyPrice() + " Mincí");
            lore.add("§fProdejní cena: §e" + shopItem.getSellPrice() + " Mincí");
            lore.add("");
            lore.add("§eKlikni levým pro nákup");
            lore.add("§eKlikni pravým pro prodej");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
            i++;
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Obchod")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) {
                return;
            }
            ShopItem shopItem = ShopItem.getByMaterial(event.getCurrentItem().getType());
            if (shopItem == null) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            if (event.getClick() == ClickType.LEFT) {
                if (plugin.getPlayerManager().getPlayerData(player.getName()).getMoney() >= shopItem.getBuyPrice()) {
                    plugin.getPlayerManager().getPlayerData(player.getName()).removeMoney(shopItem.getBuyPrice());
                    player.sendMessage("§aZakoupil jsi si §2§l" + Utils.formatString(shopItem.getMaterial().toString())
                            + "§a za §2§l" + shopItem.getBuyPrice() + "§a Mincí!");
                    player.getInventory().addItem(new ItemStack(shopItem.getMaterial()));
                } else {
                    player.sendMessage("§cTento item si nemůžeš dovolit!");
                }
            } else if (event.getClick() == ClickType.RIGHT) {
                if (player.getInventory().containsAtLeast(new ItemStack(shopItem.getMaterial()), 1)) {
                    ItemStack toRemove = new ItemStack(shopItem.getMaterial());
                    player.getInventory().removeItem(new ItemStack(shopItem.getMaterial()));
                    plugin.getPlayerManager().getPlayerData(player.getName()).addMoney(shopItem.getSellPrice() * toRemove.getAmount());
                    player.sendMessage("§aProdal jsi si §2§l" + toRemove.getAmount() + "x " + Utils.formatString(shopItem.getMaterial().toString())
                            + "§a za §2§l" + shopItem.getSellPrice() + "§a Mincí!");
                } else {
                    player.sendMessage("§cNemáš nic na prodej!");
                }
            }

        }
    }

}
