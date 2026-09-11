package com.lumorixgame.borumt2.item;

public class SpecialItems {
    public static final Item EFSUN_NESNESI = create("Efsun Nesnesi", ItemType.EFSUN_NESNESI, 200);
    public static final Item EFSUN_6_7_8_EKLEME = create("6-7-8 Efsun Ekleme Nesnesi", ItemType.EFSUN_6_7_8_EKLEME, 300);
    public static final Item EFSUN_6_7_8_DEGISTIRME = create("6-7-8 Efsun Degistirme Nesnesi", ItemType.EFSUN_6_7_8_DEGISTIRME, 300);

    private static Item create(String name, ItemType type, long epPrice) {
        Item item = new Item(name, 0, 0, 0);
        item.type = type;
        item.epPrice = epPrice;
        item.marketTradable = true;
        return item;
    }
}
