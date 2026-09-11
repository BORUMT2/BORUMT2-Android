package com.lumorixgame.borumt2.item;

public class BonusSystem {
    public static final int MAX_BONUS_COUNT = 8;
    public static final long ADD_BONUS_EP = 400;
    public static final long CHANGE_BONUS_EP = 600;

    public static boolean addBonus6(Item item, String bonus) { return canAddBonus6(item) && bonus != null && !bonus.isEmpty() && setBonus6(item, bonus); }

    private static boolean setBonus6(Item item, String bonus) { item.bonus6 = bonus; return true; }

    public static boolean canAddBonus6(Item item) {
        return item != null && !item.hasBonus6();
    }

    public static boolean canAddBonus7(Item item) {
        return item != null && item.hasBonus6() && !item.hasBonus7();
    }

    public static boolean canAddBonus8(Item item) {
        return item != null && item.hasBonus7() && !item.hasBonus8();
    }

    public static boolean canChangeBonus6(Item item) {
        return item != null && item.hasBonus6();
    }

    public static boolean canChangeBonus7(Item item) {
        return item != null && item.hasBonus7();
    }

    public static boolean canChangeBonus8(Item item) {
        return item != null && item.hasBonus8();
    }
}
