package com.lumorixgame.borumt2;

public enum Gender {
    MALE,
    FEMALE;

    public String getDisplayName() {
        switch (this) {
            case MALE: return "Erkek";
            case FEMALE: return "Kadın";
            default: return name();
        }
    }
}
