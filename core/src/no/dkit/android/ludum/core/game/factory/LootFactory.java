package no.dkit.android.ludum.core.game.factory;

import no.dkit.android.ludum.core.game.model.loot.Bomb;
import no.dkit.android.ludum.core.game.model.loot.Fireball;
import no.dkit.android.ludum.core.game.model.loot.Flamethrower;
import no.dkit.android.ludum.core.game.model.loot.Gun;
import no.dkit.android.ludum.core.game.model.loot.Laser;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.loot.Medpack;
import no.dkit.android.ludum.core.game.model.loot.Orb;
import no.dkit.android.ludum.core.game.model.loot.Rocket;
import no.dkit.android.ludum.core.game.model.loot.Tongue;
import no.dkit.android.ludum.core.game.model.loot.Weapon;
import no.dkit.android.ludum.core.game.model.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public class LootFactory {
    public static Set<Loot.LOOT_TYPE> loot = new HashSet<Loot.LOOT_TYPE>();
    public static Set<Loot.LOOT_TYPE> weapons = new HashSet<Loot.LOOT_TYPE>();

    static LootFactory instance = null;

    public static LootFactory getInstance() {
        if (instance == null)
            instance = new LootFactory();

        return instance;
    }

    private LootFactory() {
        weapons.add(Loot.LOOT_TYPE.BOMB);
        weapons.add(Loot.LOOT_TYPE.FIREBALL);
        weapons.add(Loot.LOOT_TYPE.FLAME_THROWER);
        weapons.add(Loot.LOOT_TYPE.GUN);
        weapons.add(Loot.LOOT_TYPE.LASER);
        weapons.add(Loot.LOOT_TYPE.ROCKET);

        loot.addAll(weapons);
        loot.add(Loot.LOOT_TYPE.MEDPACK);
        loot.add(Loot.LOOT_TYPE.ORB);
    }

    public Loot.LOOT_TYPE getRandomLootType() {
        return Level.getInstance().getRandomLootType();
    }

    public Loot getLoot(Loot.LOOT_TYPE type) {
        switch (type) {
            case MEDPACK:
                return new Medpack();
            case ORB:
                return new Orb();
            default:
                return getWeapon(type);
        }
    }

    public Weapon getWeapon(Loot.LOOT_TYPE type) {
        switch (type) {
            case BOMB:
                return new Bomb();
            case FIREBALL:
                return new Fireball();
            case FLAME_THROWER:
                return new Flamethrower();
            case GUN:
                return new Gun();
            case LASER:
                return new Laser();
            case ROCKET:
                return new Rocket();
            case TONGUE:
                return new Tongue();
            default:
                throw new RuntimeException("DERP..." + type);
        }
    }

    public void dispose() {
    }
}
