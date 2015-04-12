package no.dkit.android.ludum.core.game.model.loot;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;

public class Treasure extends Loot {

    public Treasure() {
        super(LOOT_TYPE.TREASURE, "treasure");
    }

    @Override
    public void onPickup(GameBody body) {
        if (body instanceof PlayerBody)
            ((PlayerBody) body).addCredits(MathUtils.random(2, 10));
    }
}
