package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class ParticleBody extends WeaponBody {

    public ParticleBody(Body body, float radius, TextureRegion image) {
        super(body, radius, image, 0, 1);
        bounce = true;
        ttl = 500;      // Unusable, creates different results on different hardware...
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.disableBlending();
        spriteBatch.setColor(color);
        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                0, true);
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.enableBlending();
    }

    public void reposition(Vector2 position, Vector2 velocity) {
        created = System.currentTimeMillis();
        body.setActive(true);
        setActive(true);
        body.setTransform(position, 0);
        body.setLinearVelocity(velocity);
    }

    @Override
    public void delete() {
        super.dispose();
        active = false;
        body.setActive(false);
    }

    @Override
    public void collidedWith(GameBody other) {
        super.collidedWith(other);

        if(other instanceof AgentBody)
            ((AgentBody)other).setUnconscious(true, 1500);
    }
}
