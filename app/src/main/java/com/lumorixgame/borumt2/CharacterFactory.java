package com.lumorixgame.borumt2;

public class CharacterFactory {
    public static Player create(String name, CharacterClass characterClass, Gender gender) {
        return new Player(name, characterClass, gender);
    }
}
