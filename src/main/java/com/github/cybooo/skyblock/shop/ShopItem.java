package com.github.cybooo.skyblock.shop;

import org.bukkit.Material;

public enum ShopItem {

    GRASS_BLOCK(Material.GRASS_BLOCK, 100, 15);

    private final Material material;
    private final int buyPrice;
    private final int sellPrice;

    ShopItem(Material material, int buyPrice, int sellPrice) {
        this.material = material;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public Material getMaterial() {
        return material;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public static ShopItem getByMaterial(Material material) {
        for (ShopItem item : values()) {
            if (item.getMaterial() == material) {
                return item;
            }
        }
        return null;
    }
}
