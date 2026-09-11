package com.lumorixgame.borumt2;

import com.lumorixgame.borumt2.item.Item;

public class CharacterData {
    public String name;
    public CharacterClass characterClass;
    public Gender gender;

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
        Item weapon = new Item("Başlangıç Silahı", 10, 9, 0);
        weapon.setFixedBonuses("Güç +10", "Saldırı Hızı +5%");

        Item armor = new Item("Başlangıç Zırhı", 10, 9, 0);
        armor.setFixedBonuses("Savunma +10", "HP +100");

        startingItems = new Item[]{weapon, armor};
    }
}
