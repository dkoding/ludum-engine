package no.dkit.android.ludum.core.game.model.loot;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;

public class Armor extends Loot {

    public Armor() {
        super(LOOT_TYPE.TREASURE, "armor");
    }

    @Override
    public void onPickup(GameBody body) {
        if (body instanceof PlayerBody)
            ((PlayerBody) body).addArmor(MathUtils.random(2, 10));
    }
}
