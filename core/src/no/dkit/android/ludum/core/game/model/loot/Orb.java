package no.dkit.android.ludum.core.game.model.loot;

import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;

public class Orb extends Loot {

    public Orb() {
        super(LOOT_TYPE.ORB, "orb");
    }

    @Override
    public void onPickup(GameBody body) {
        if (body instanceof PlayerBody)
            ((PlayerBody) body).getData().addOrbs(1);
    }
}
