package com.lumorixgame.borumt2;

public class CharacterData {
    public String name;
    public CharacterClass characterClass;
    public Gender gender;

    public int level = 10;
    public long yang = 1000000L;

    public CharacterData(String name, CharacterClass characterClass, Gender gender) {
        this.name = name;
        this.characterClass = characterClass;
        this.gender = gender;
    }
}
