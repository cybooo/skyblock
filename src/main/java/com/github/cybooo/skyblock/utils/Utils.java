package com.github.cybooo.skyblock.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;

public class Utils {

    public static String millisToDate(long millis) {
        return new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date(millis));
    }

    public static void fillBorders(Inventory inventory) {
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

    public static String formatCurrency(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###,###.##");
        return decimalFormat.format(amount);
    }

    public static String formatString(String input) {
        return input.toLowerCase().substring(0, 1).toUpperCase() + input.substring(1).replace("_", "");
    }

}
