package no.dkit.android.ludum.core.game.model.loot;

import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.factory.TextItem;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class Medpack extends Loot {

    public Medpack() {
        super(LOOT_TYPE.MEDPACK, "medpack");
    }

    @Override
    public void onPickup(GameBody body) {
        EffectFactory.getInstance().addEffect(body.position, EffectFactory.EFFECT_TYPE.ACHIEVE);
        TextFactory.getInstance().addText(new TextItem("HEALTH INCREASED!"), 0f);
        body.heal(10);
    }
}
