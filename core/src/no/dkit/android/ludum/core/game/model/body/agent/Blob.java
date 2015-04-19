package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;
import no.dkit.android.ludum.core.game.model.body.DiscardBody;

public class Blob extends AgentBody {
    private final Body[] limbs;
    float mass;
    long lastJump = System.currentTimeMillis();

    public Blob(final Body head, Body[] limbs, TextureRegion image, float radius) {
        super(head, radius, image);
        this.limbs = limbs;
        mind = new PrioritizingMind();

        for (Body limb : limbs) {
            mass += limb.getMass();
            limb.setUserData(new Limb(this));
        }

        mass += head.getMass();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.setColor(Color.GREEN);
        spriteBatch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                2, 2,
                0,
                true);

        for (Body limb : this.limbs) {
            spriteBatch.draw(image,
                    limb.getPosition().x - radius, limb.getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    1, 1,
                    0,
                    true);
        }
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        for (Body limb : limbs) {
            limb.setActive(active);
        }
    }

    @Override
    public void delete() {
        super.delete();
        for (Body limb : limbs) {
            limb.setUserData(new DiscardBody(body));
        }
    }

    @Override
    public void update() {
        super.update();

        if (!active) return;

        if (MathUtils.random() < .01f && System.currentTimeMillis() > lastJump + 3500) {
            getBody().applyForceToCenter(0, mass * Config.getDimensions().WORLD_HEIGHT * 20f /* Twice gravity */, true);
            lastJump = System.currentTimeMillis();
        }
    }
}
