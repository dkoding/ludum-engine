package no.dkit.android.ludum.core.game.model.loot;

import no.dkit.android.ludum.core.game.model.body.GameBody;

public class Medpack extends Loot {

    public Medpack() {
        super(LOOT_TYPE.MEDPACK, "medpack");
    }

    @Override
    public void onPickup(GameBody body) {
        body.heal(10);
    }
}
