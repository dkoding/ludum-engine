package no.dkit.android.ludum.core.game.model.loot;

import com.badlogic.gdx.math.MathUtils;

public class Rocket extends Weapon {
    public Rocket() {
        super(
                LOOT_TYPE.ROCKET,
                "rocket"
        );

        emitters = MathUtils.random(1,3);
        sideAngle = MathUtils.random(3,5);
    }
}
