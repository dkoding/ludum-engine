package no.dkit.android.ludum.core.game.model.loot;

import no.dkit.android.ludum.core.game.model.body.GameBody;

public abstract class Loot {
    public enum LOOT_TYPE {
        BOMB,
        GUN,
        ROCKET,
        FIREBALL,
        FLAME_THROWER,
        LASER,

        MEDPACK,
        ORB,
        TREASURE,
        ARMOR
    }

    LOOT_TYPE type;

    // Using these for easy tie-in with UI
    int id;
    static int idCounter;
    String imageName;

    public abstract void onPickup(GameBody body);

    protected Loot(LOOT_TYPE type, String imageName) {
        id = idCounter++;
        this.type = type;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public String getImageName() {
        return this.imageName;
    }

    public LOOT_TYPE getType() {
        return type;
    }
}
