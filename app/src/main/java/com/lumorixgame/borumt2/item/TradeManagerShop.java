package com.lumorixgame.borumt2.item;

import com.lumorixgame.borumt2.Player;

public class TradeManagerShop {

    public static boolean buyUpgradeMaterial(Player player, int materialIndex) {
        if (player == null) {
            return false;
        }

        Item[] materials = TradeManagerMarket.getUpgradeMaterials();

        if (materialIndex < 0 || materialIndex >= materials.length) {
            return false;
        }

        Item material = materials[materialIndex];

        long price = material.yangValue;

        if (player.getYang() < price) {
            return false;
        }

        if (player.getInventory().isFull()) {
            return false;
        }

        player.data.yang -= price;
        player.getInventory().addItem(material);

        return true;
    }
}
