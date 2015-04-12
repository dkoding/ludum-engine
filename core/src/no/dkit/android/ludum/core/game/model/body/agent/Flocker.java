package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.group.Flocking;
import no.dkit.android.ludum.core.game.ai.behaviors.group.GroupArrive;
import no.dkit.android.ludum.core.game.ai.behaviors.single.BoxConstraint;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Evade;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;

public class Flocker extends AgentBody {
    public Flocker(Body body, TextureAtlas.AtlasRegion image, float radius, Vector2 target, Neighborhood neighborhood) {
        super(body, radius, image);
        neighborhood.addAgentBody(this);

        mind = new PrioritizingMind();

        mind.addBehavior(new BoxConstraint(position.cpy(), 3, Config.getDimensions().AGENT_BOX_WIDTH, Config.getDimensions().AGENT_BOX_HEIGHT));
        mind.addBehavior(new Flocking(neighborhood, Config.getDimensions().SCREEN_LONGEST * 2, 1f));
        mind.addAttackBehavior(new GroupArrive(neighborhood, target, Config.getDimensions().SCREEN_SHORTEST * 2, 3, 1f));
        mind.addDefendBehavior(new Evade(target, 2, 1f));

        body.setLinearVelocity(
                MathUtils.random(maxSpeed * 4) - maxSpeed * 2,
                MathUtils.random(maxSpeed * 4) - maxSpeed * 2);

        initHealth(3);

        bodyType = BODY_TYPE.METAL;
    }
}
