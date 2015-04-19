package no.dkit.android.ludum.core.game.model.loot;

import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.model.GameModel;

public class Tongue extends Weapon {
    public Tongue() {
        super(
                LOOT_TYPE.TONGUE,
                "tongue"
        );

        this.cooldown1 = 100;
        this.cooldown2 = 100;
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
