package no.dkit.android.ludum.core.game.model.loot;

import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.model.GameModel;

public class Dongue extends Weapon {
    public Dongue() {
        super(
                LOOT_TYPE.TONGUE,
                "tongue"
        );

        this.cooldown1 = 1000;
        this.cooldown2 = 10;
    }

    @Override
    public void fire1() {
        BodyFactory.getInstance().lick(GameModel.getPlayer(), firingDirection);
    }

    @Override
    public void fire2() {
        BodyFactory.getInstance().slurp(GameModel.getPlayer());
    }
}
