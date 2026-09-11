package com.lumorixgame.borumt2.item;

public class ItemUpgradeSystem {
    public static boolean canUpgrade(int plus) {
        return plus < 9;
    }

    public static boolean isSafeUpgrade(int plus) {
        return plus >= 0 && plus < 5;
    }

    public static int getSuccessRate(int plus) {
        switch (plus) {
            case 0: return 100;
            case 1: return 100;
            case 2: return 100;
            case 3: return 100;
            case 4: return 100;
            case 5: return 80;
            case 6: return 60;
            case 7: return 40;
            case 8: return 20;
            default: return 0;
        }
    }
}
