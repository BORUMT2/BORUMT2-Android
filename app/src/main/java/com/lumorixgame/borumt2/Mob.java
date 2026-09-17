package com.lumorixgame.borumt2;

public class Mob {

    public enum Type {
        NORMAL,
        BOSS,
        METIN
    }

    public final String name;
    public final Type type;

    public final int level;
    public final int maxHp;
    public int hp;

    public final int attackPower;
    public final int defense;

    public final int expReward;
    public final long yangReward;

    public final float x;
    public final float y;
    public final float z;

    public Mob(
            String name,
            Type type,
            int level,
            float x,
            float y,
            float z,
            int maxHp,
            int attackPower,
            int defense,
            int expReward,
            long yangReward) {

        this.name = name;
        this.type = type;
        this.level = level;

        this.x = x;
        this.y = y;
        this.z = z;

        this.maxHp = Math.max(1, maxHp);
        this.hp = this.maxHp;

        this.attackPower = Math.max(0, attackPower);
        this.defense = Math.max(0, defense);

        this.expReward = Math.max(0, expReward);
        this.yangReward = Math.max(0L, yangReward);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public boolean isBoss() {
        return type == Type.BOSS;
    }

    public boolean isMetin() {
        return type == Type.METIN;
    }

    public float distanceTo(float px, float pz) {
        float dx = x - px;
        float dz = z - pz;
        return (float) Math.sqrt(dx * dx + dz * dz);
    }

    public int receiveDamage(int damage) {
        if (!isAlive()) {
            return 0;
        }

        int actualDamage = Math.max(1, damage - defense);
        hp = Math.max(0, hp - actualDamage);

        return actualDamage;
    }
}
