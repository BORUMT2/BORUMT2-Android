package com.lumorixgame.borumt2.item;

public class UpgradeMaterials {

    public static Item create(String name, boolean plus) {
        Item item = new Item(
                name,
                0,
                0,
                plus ? 4000000L : 2000000L
        );

        item.quantity = 1;
        item.marketTradable = true;

        return item;
    }

    public static Item ORUMCEK_GOZU() {
        return create("Örümcek Gözü", false);
    }

    public static Item ORUMCEK_GOZU_PLUS() {
        return create("Örümcek Gözü+", true);
    }

    public static Item ORUMCEK_AGI() {
        return create("Örümcek Ağı", false);
    }

    public static Item ORUMCEK_AGI_PLUS() {
        return create("Örümcek Ağı+", true);
    }

    public static Item YILAN_KUYRUGU() {
        return create("Yılan Kuyruğu", false);
    }

    public static Item YILAN_KUYRUGU_PLUS() {
        return create("Yılan Kuyruğu+", true);
    }

    public static Item AKREP_KUYRUGU() {
        return create("Akrep Kuyruğu", false);
    }

    public static Item AKREP_KUYRUGU_PLUS() {
        return create("Akrep Kuyruğu+", true);
    }

    public static Item DEGERLI_TAS_PARCASI() {
        return create("Değerli Taş Parçası", false);
    }

    public static Item DEGERLI_TAS_PARCASI_PLUS() {
        return create("Değerli Taş Parçası+", true);
    }

    public static Item BASLANGIC_EL_KITABI() {
        return create("Başlangıç El Kitabı", false);
    }

    public static Item BASLANGIC_EL_KITABI_PLUS() {
        return create("Başlangıç El Kitabı+", true);
    }
}
