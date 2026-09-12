package com.lumorixgame.borumt2;

public enum AccountRole {
    PLAYER,
    GM;

    public boolean isGameMaster() {
        return this == GM;
    }
}
