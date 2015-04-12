package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class RewardBody extends GameBody {
    long created;
    long timeToLive = Config.ITEM_TTL;

    public RewardBody(Body body, float halfTileSizeX, TextureRegion image) {
        super(body, halfTileSizeX, image);
        addLight(LightFactory.getInstance().getLight(position.x, position.y, halfTileSizeX, 6, Color.WHITE));
        light.setStaticLight(false);
        lightMod = .2f;
        rotationMod = 3;
        created = System.currentTimeMillis();
        getBody().setLinearVelocity(MathUtils.random(Config.MAX_SPEED * 2) - Config.MAX_SPEED, MathUtils.random(Config.MAX_SPEED * 2) - Config.MAX_SPEED);
        getBody().setAngularVelocity(MathUtils.random(Config.ROTATE_SPEED * 2) - Config.ROTATE_SPEED);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        if (alpha != 0)
            spriteBatch.setColor(1, 1, 1, alpha);

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                body.getAngle() * MathUtils.radiansToDegrees,
                true);

        spriteBatch.setColor(1, 1, 1, 1);
    }

    @Override
    public void setActive(boolean active) {
        if (!this.active && active)
            body.setAngularVelocity(MathUtils.random(Config.ROTATE_SPEED * 2) - Config.ROTATE_SPEED);
        super.setActive(active);
    }

    @Override
    public void update() {
        super.update();

        if (System.currentTimeMillis() > created + (timeToLive / 2))
            alphaMod = -.2f;
        if (System.currentTimeMillis() > created + timeToLive)
            delete();
    }
}
