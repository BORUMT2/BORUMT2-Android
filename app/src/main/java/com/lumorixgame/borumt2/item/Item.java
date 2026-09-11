package com.lumorixgame.borumt2.item;

public class Item {
    public String name;
    public int level;
    public int plus;
    public long yangValue;
    public ItemType type;
    public long epPrice;
    public boolean marketTradable;
    public String bonus1;
    public String bonus2;
    public String bonus6;
    public String bonus7;
    public String bonus8;

    public Item(String name, int level, int plus, long yangValue) {
        this.name = name;
        this.level = level;
        this.plus = plus;
        this.yangValue = yangValue;
        this.type = ItemType.NORMAL_ITEM;
        this.epPrice = 0;
        this.marketTradable = false;
        this.bonus1 = "";
        this.bonus2 = "";
        this.bonus6 = "";
        this.bonus7 = "";
        this.bonus8 = "";
    }

    public void setFixedBonuses(String bonus1, String bonus2) {
        this.bonus1 = bonus1;
        this.bonus2 = bonus2;
    }

    public boolean hasBonus6() { return !bonus6.isEmpty(); }
    public boolean hasBonus7() { return !bonus7.isEmpty(); }
    public boolean hasBonus8() { return !bonus8.isEmpty(); }
}
