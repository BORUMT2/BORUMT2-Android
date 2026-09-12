package com.lumorixgame.borumt2;

public class Account {
    public String username;
    public AccountRole role;

    public Account(String username, AccountRole role) {
        this.username = username;
        this.role = role;
    }

    public boolean isGameMaster() {
        return role != null && role.isGameMaster();
    }
}
