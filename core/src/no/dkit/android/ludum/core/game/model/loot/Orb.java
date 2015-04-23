package no.dkit.android.ludum.core.game.model.loot;

import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.factory.TextItem;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;

public class Orb extends Loot {

    public Orb() {
        super(LOOT_TYPE.ORB, "orb");
    }

    @Override
    public void onPickup(GameBody body) {
        if (body instanceof PlayerBody) {
            if(XXXX.playerData.getOrbs() < 10) {
                XXXX.playerData.addOrbs(1);
                EffectFactory.getInstance().addEffect(body.position, EffectFactory.EFFECT_TYPE.ACHIEVE);
                TextFactory.getInstance().addText(new TextItem("TONGUE POWER INCREASED!"), 0f);
            } else {
                TextFactory.getInstance().addText(new TextItem("TONGUE POWER AT MAXIMUM!"), 0f);
            }
        }
    }
}
