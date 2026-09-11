package com.lumorixgame.borumt2;

public class Player {
    public float x = 0f;
    public float y = 0f;
    public float z = 0f;
    public float rotation = 0f;
    public float moveSpeed = 1.0f;

    public void move(float dx, float dz) {
        x += dx * moveSpeed;
        z += dz * moveSpeed;

        if (Math.abs(dx) + Math.abs(dz) > 0.001f) {
            rotation = (float)Math.atan2(dx, dz);
        }
    }
}
