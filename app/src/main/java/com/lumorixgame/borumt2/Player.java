package com.lumorixgame.borumt2;

public class Player {
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
