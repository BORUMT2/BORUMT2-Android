package com.lumorixgame.borumt2.item;

public class Inventory {
    public static final int SLOT_COUNT = 45;
    private final Item[] slots = new Item[SLOT_COUNT];

    public boolean isValidSlot(int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }

    public Item getItem(int slot) {
        return isValidSlot(slot) ? slots[slot] : null;
    }

    public boolean isEmpty(int slot) {
        return isValidSlot(slot) && slots[slot] == null;
    }

    public boolean addItem(Item item) {
        if (item == null) return false;
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (slots[i] == null) {
                slots[i] = item;
                return true;
            }
        }
        return false;
    }

    public boolean removeItem(int slot) {
        if (!isValidSlot(slot) || slots[slot] == null) return false;
        slots[slot] = null;
        return true;
    }

    public int getFreeSlotCount() {
        int count = 0;
        for (Item item : slots) {
            if (item == null) count++;
        }
        return count;
    }

    public boolean isFull() {
        return getFreeSlotCount() == 0;
    }
}
