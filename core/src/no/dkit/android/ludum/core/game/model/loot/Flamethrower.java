package no.dkit.android.ludum.core.game.model.loot;

import no.dkit.android.ludum.core.game.factory.EffectFactory;

public class Flamethrower extends Thrower {

    public Flamethrower() {
        super(LOOT_TYPE.FLAME_THROWER, "flamethrower", EffectFactory.EFFECT_TYPE.FIRE);
    }

}
