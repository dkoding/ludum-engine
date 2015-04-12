package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;

public class AnimatedSingleEnemy extends AnimatedAgentBody {
    public AnimatedSingleEnemy(Body body, Array<TextureAtlas.AtlasRegion> images, float radius) {
        this(body, images, radius, false);
        bodyType = BODY_TYPE.HUMANOID;
    }

    public AnimatedSingleEnemy(Body body, Array<TextureAtlas.AtlasRegion> images, float radius, boolean flying) {
        super(body, radius, images);
        this.flying = flying;
        mind = new PrioritizingMind();
    }
}
