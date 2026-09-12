package com.lumorixgame.borumt2;

public enum CharacterClass {
    SAVASCI,
    NINJA,
    SURA,
    SAMAN;

    public String getDisplayName() {
        switch (this) {
            case SAVASCI: return "Savaşçı";
            case NINJA: return "Ninja";
            case SURA: return "Sura";
            case SAMAN: return "Şaman";
            default: return name();
        }
    }
}
