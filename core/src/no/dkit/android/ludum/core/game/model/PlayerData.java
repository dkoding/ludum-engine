package no.dkit.android.ludum.core.game.model;

import no.dkit.android.ludum.core.game.Config;

public class PlayerData {
    int health = Config.PLAYER_START_HEALTH;
    int orbs = Config.PLAYER_START_ORBS;
    float maxSpeed = Config.PLAYER_MAX_SPEED;
    float minSpeed = Config.PLAYER_MIN_SPEED;
    int score;
    private int keys = 0;
    private int rescues = 0;

    public PlayerData() {
        health = Config.PLAYER_START_HEALTH;
        orbs = Config.PLAYER_START_ORBS;
        keys = Config.PLAYER_START_KEYS;
        maxSpeed = Config.PLAYER_MAX_SPEED;
        minSpeed = Config.PLAYER_MIN_SPEED;
    }

    public int getHealth() {
        return health;
    }

    public int getOrbs() {
        return orbs;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getMinSpeed() {
        return minSpeed;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setOrbs(int orbs) {
        this.orbs = orbs;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMinSpeed(float minSpeed) {
        this.minSpeed = minSpeed;
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void addOrbs(int orbs) {
        this.orbs += orbs;
    }

    public void removeOrbs(int value) {
        this.orbs -= value;
    }

    public void addKey() {
        this.rescues++;
        this.keys++;
    }

    public void removeKey() {
        this.keys--;
    }

    public int getKeys() {
        return keys;
    }

    public int getRescues() {
        return rescues;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }
}
