package no.dkit.android.ludum.core.game.factory;

import no.dkit.android.ludum.core.game.model.body.weapon.MeleeWeapon;
import no.dkit.android.ludum.core.game.model.loot.Bomb;
import no.dkit.android.ludum.core.game.model.loot.Gun;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.loot.Orb;
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
        weapons.add(Loot.LOOT_TYPE.GUN);

        loot.addAll(weapons);
        loot.add(Loot.LOOT_TYPE.ORB);
    }

    public Loot.LOOT_TYPE getRandomLootType() {
        return Level.getInstance().getRandomLootType();
    }

    public Loot getLoot(Loot.LOOT_TYPE type) {
        switch (type) {
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
            case GUN:
                return new Gun();
            case MELEE:
                return new MeleeWeapon();
            default:
                throw new RuntimeException("DERP..." + type);
        }
    }

    public void dispose() {
    }
}
