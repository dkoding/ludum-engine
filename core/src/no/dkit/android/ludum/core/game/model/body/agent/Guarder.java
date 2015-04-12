package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.single.OffsetArrive;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;

public class Guarder extends AgentBody {
    AgentBody head;

    public Guarder(AgentBody head, Body body, TextureRegion image, float radius, float offsetX, float offsetY) {
        super(body, radius, image);
        this.head = head;

        mind = new PrioritizingMind();

        mind.addBehavior(new OffsetArrive(head.position, 3, Config.getDimensions().SCREEN_LONGEST, 3, offsetX, offsetY));
        mind.addAttackBehavior(new OffsetArrive(head.position, 3, Config.getDimensions().SCREEN_LONGEST, 3, offsetX, offsetY));
        mind.addDefendBehavior(new OffsetArrive(head.position, 3, Config.getDimensions().SCREEN_LONGEST, 3, offsetX, offsetY));

        body.setLinearVelocity(
                MathUtils.random(minSpeed * 2) - minSpeed,
                MathUtils.random(minSpeed * 2) - minSpeed);

        alpha = .75f;

        initHealth(1);

        bodyType = BODY_TYPE.METAL;
    }
}
