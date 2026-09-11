package com.lumorixgame.borumt2;

public class NPC {
    public String name;
    public float x;
    public float y;
    public float z;

    public NPC(String name, float x, float y, float z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float distanceTo(float px, float pz) {
        float dx = x - px;
        float dz = z - pz;
        return (float)Math.sqrt(dx * dx + dz * dz);
    }

    public boolean canInteract(float px, float pz) {
        return distanceTo(px, pz) <= 2.5f;
    }
}
