package com.lumorixgame.borumt2.item;

import com.lumorixgame.borumt2.Player;

public class TradeManagerShop {

    public static boolean buyUpgradeMaterial(Player player, int materialIndex) {
        return buyUpgradeMaterial(player, materialIndex, 6);
    }

    public static boolean buyUpgradeMaterial(Player player, int materialIndex, int quantity) {
        if (player == null) {
            return false;
        }

        Item[] materials = TradeManagerMarket.getUpgradeMaterials();

        if (materialIndex < 0 || materialIndex >= materials.length) {
            return false;
        }

        Item material = materials[materialIndex];

        if (quantity != 6) {
            return false;
        }

        long price = material.yangValue;

        if (player.getYang() < price) {
            return false;
        }

        Item packageItem = new Item(
                material.name,
                material.level,
                material.plus,
                material.yangValue
        );
        packageItem.type = material.type;
        packageItem.marketTradable = material.marketTradable;
        packageItem.quantity = quantity;

        if (!player.getInventory().addItem(packageItem)) {
            return false;
        }

        player.data.yang -= price;

        return true;
    }
}
