package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.single.OffsetArriveAngled;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;

public class Facer extends AgentBody {
    AgentBody head;

    public Facer(AgentBody target, Body body, TextureRegion image, float radius, float offsetX, float offsetY) {
        super(body, radius, image);
        this.head = target;

        mind = new PrioritizingMind();

        mind.addBehavior(new OffsetArriveAngled(target, 3, Config.getDimensions().SCREEN_LONGEST, 1, offsetX, offsetY));
        mind.addAttackBehavior(new OffsetArriveAngled(target, 3, Config.getDimensions().SCREEN_LONGEST, 1, offsetX / 2f, offsetY / 2f));
        mind.addDefendBehavior(new OffsetArriveAngled(target, 3, Config.getDimensions().SCREEN_LONGEST, 1, offsetX * 2f, offsetY * 2f));

        body.setLinearVelocity(
                MathUtils.random(minSpeed * 2) - minSpeed,
                MathUtils.random(minSpeed * 2) - minSpeed);

        initHealth(1);

        bodyType = BODY_TYPE.METAL;

        playerFocused = true;
        flying = true;
    }

    @Override
    public float getAngle() {
        return 90;
    }
}
