package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.group.Flocking;
import no.dkit.android.ludum.core.game.ai.behaviors.group.GroupArrive;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Evade;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.factory.BehaviorFactory;

public class AnimatedFlocker extends AnimatedAgentBody {
    public AnimatedFlocker(Body body, Array<TextureAtlas.AtlasRegion> images, float radius, Vector2 target, Neighborhood neighborhood) {
        super(body, radius, images);
        neighborhood.addAgentBody(this);

        mind = new PrioritizingMind();

        mind.addBehavior(BehaviorFactory.getSingleBehavior(BehaviorFactory.BEHAVIOR.BOX, position.cpy()));
        mind.addBehavior(new Flocking(neighborhood, Config.getDimensions().SCREEN_LONGEST * 2, .5f));
        mind.addAttackBehavior(new GroupArrive(neighborhood, target, Config.getDimensions().SCREEN_SHORTEST * 2, 3, 1f));
        mind.addDefendBehavior(new Evade(target, 2, 1f));

        body.setLinearVelocity(
                MathUtils.random(minSpeed * 2) - minSpeed,
                MathUtils.random(minSpeed * 2) - minSpeed);

        initHealth(3);

        bodyType = BODY_TYPE.ANIMAL;
    }
}
