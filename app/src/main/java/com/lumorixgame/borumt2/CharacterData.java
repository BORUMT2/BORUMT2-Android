package com.lumorixgame.borumt2;

import com.lumorixgame.borumt2.item.Item;

public class CharacterData {
    public String name;
    public CharacterClass characterClass;
    public Gender gender;

    public String getClassDisplayName() {
        return characterClass.getDisplayName();
    }

    public String getGenderDisplayName() {
        return gender.getDisplayName();
    }

    public int level = 10;
    public long yang = 1000000L;
    public Item[] startingItems;

    public CharacterData(String name, CharacterClass characterClass, Gender gender) {
        this.name = name;
        this.characterClass = characterClass;
        this.gender = gender;
        this.startingItems = new Item[0];
    }

    public void createStartingItems() {
        String weaponName;
        String bonus1;
        String bonus2;

        switch (characterClass) {
            case SAVASCI:
                weaponName = "Başlangıç Kılıcı";
                bonus1 = "Güç +10";
                bonus2 = "Saldırı Hızı +5%";
                break;
            case NINJA:
                weaponName = "Başlangıç Bıçağı";
                bonus1 = "Çeviklik +10";
                bonus2 = "Saldırı Hızı +5%";
                break;
            case SURA:
                weaponName = "Başlangıç Kılıcı";
                bonus1 = "Zeka +10";
                bonus2 = "Büyülü Saldırı +5%";
                break;
            case SAMAN:
                weaponName = "Başlangıç Yelpazesi";
                bonus1 = "Zeka +10";
                bonus2 = "Büyülü Saldırı +5%";
                break;
            default:
                weaponName = "Başlangıç Silahı";
                bonus1 = "Güç +10";
                bonus2 = "Saldırı Hızı +5%";
        }

        Item weapon = new Item(weaponName, 10, 9, 0);
        weapon.setFixedBonuses(bonus1, bonus2);

        Item armor = new Item("Başlangıç Zırhı", 10, 9, 0);
        armor.setFixedBonuses("Savunma +10", "HP +100");

        startingItems = new Item[]{weapon, armor};
    }
}
