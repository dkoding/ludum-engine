package no.dkit.android.ludum.core.game.model;

import no.dkit.android.ludum.core.game.Config;

public class PlayerData {
    int health = Config.PLAYER_START_HEALTH;
    int armor = Config.PLAYER_START_ARMOR;
    int orbs = Config.PLAYER_START_ORBS;
    int credits = Config.PLAYER_START_CREDITS;
    float maxSpeed = Config.PLAYER_MAX_SPEED;
    float minSpeed = Config.PLAYER_MIN_SPEED;
    int score;
    private int keys = 0;
    private int rescues = 0;

    public PlayerData() {
        health = Config.PLAYER_START_HEALTH;
        armor = Config.PLAYER_START_ARMOR;
        orbs = Config.PLAYER_START_ORBS;
        credits = Config.PLAYER_START_CREDITS;
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
        this.keys++;
        this.rescues++;
    }

    public void removeKey() {
        this.keys--;
    }

    public int getKeys() {
        return keys;
    }

    public void addCredits(int gold) {
        this.credits += gold;
    }

    public void removeCredits(int credits) {
        this.credits -= credits;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void addArmor(int armor) {
        this.armor += armor;
    }

    public void removeArmor(int armor) {
        this.armor -= armor;
    }

    public int getRescues() {
        return rescues;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }
}
