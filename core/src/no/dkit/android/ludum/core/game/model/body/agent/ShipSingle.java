package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;
import no.dkit.android.ludum.core.game.factory.EffectFactory;

public class ShipSingle extends AgentBody {

    public ShipSingle(Body body, TextureRegion image, float radius) {
        this(body, image, radius, true);
    }

    public ShipSingle(Body body, TextureRegion image, float radius, boolean flying) {
        super(body, radius, image);
        this.flying = flying;
        mind = new PrioritizingMind();
        setupEmitter(EffectFactory.EFFECT_TYPE.SMOKE, 5, 100);
    }
}
