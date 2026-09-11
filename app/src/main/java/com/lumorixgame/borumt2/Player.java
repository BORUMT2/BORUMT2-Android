package com.lumorixgame.borumt2;

import com.lumorixgame.borumt2.item.Item;

public class Player {
    public CharacterData data;

    public Player() {
        data = new CharacterData("BÖRÜ", CharacterClass.WARRIOR, Gender.MALE);
        data.createStartingItems();
    }

    public String getDisplayName() {
        return data.name;
    }

    public int getLevel() {
        return data.level;
    }

    public long getYang() {
        return data.yang;
    }

    public CharacterClass getCharacterClass() {
        return data.characterClass;
    }

    public Gender getGender() {
        return data.gender;
    }

    public Item[] getStartingItems() {
        return data.startingItems;
    }

    public float x = 0f;
    public float y = 0f;
    public float z = 0f;
    public float rotation = 0f;
    public float moveSpeed = 1.0f;
    public float targetX = 0f;
    public float targetZ = 0f;

    public void move(float dx, float dz) {
        targetX += dx * moveSpeed;
        targetZ += dz * moveSpeed;
        if (Math.abs(dx) + Math.abs(dz) > 0.001f) {
            rotation = (float)Math.atan2(dx, dz);
        }
    }

    public void update(float deltaTime) {
        float dx = targetX - x;
        float dz = targetZ - z;
        float distance = (float)Math.sqrt(dx * dx + dz * dz);
        if (distance < 0.001f) {
            x = targetX;
            z = targetZ;
            return;
        }
        float step = moveSpeed * 4.0f * deltaTime;
        if (step >= distance) {
            x = targetX;
            z = targetZ;
        } else {
            x += dx / distance * step;
            z += dz / distance * step;
        }
    }
}
