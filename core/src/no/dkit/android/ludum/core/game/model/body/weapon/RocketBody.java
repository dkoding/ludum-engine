package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;

public class RocketBody extends WeaponBody {
    float width;
    float height;
    float angle;

    public RocketBody(Body body, float width, float height, TextureRegion image, float angle, int damage) {
        super(body, width / 2, image, angle, damage);
        this.width = width * 2;
        this.height = height * 2;
        this.angle = angle;
        speed = 1;

        body.setLinearVelocity(body.getLinearVelocity().nor().scl(speed / 6f));

        emitter = EffectFactory.getInstance().addEffect(position, EffectFactory.EFFECT_TYPE.SMOKE);
        emitter.setContinuous(true);

        emitter.getAngle().setHigh(angle - 180, angle - 180);
        emitter.getAngle().setLow(angle - 180, angle - 180);
        emitter.getVelocity().setLow(.1f, .2f);
        emitter.getVelocity().setHigh(.2f, .4f);
        emitter.getTint().setColors(new float[]{1f, 1f, 1f, 1f, .8f, 0f});

        emitter.getScale().setHigh(emitter.getScale().getHighMin() * .2f, emitter.getScale().getHighMax() * .4f);
        emitter.getScale().setLow(emitter.getScale().getLowMin() * .2f, emitter.getScale().getLowMax() * .4f);

        emitter.setPosition(body.getPosition().x, body.getPosition().y);

        //addLight(LightFactory.getInstance().getLight(getPosition(), Config.HALF_TILE_SIZE_X * 2, 6, Color.ORANGE));
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void update() {
        super.update();

        if (body.getLinearVelocity().len() > Config.EPSILON) {
            body.setTransform(position.x, position.y, MathUtils.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x));
            angle = body.getAngle() * MathUtils.radiansToDegrees;
        }

        emitter.setPosition(body.getPosition().x, body.getPosition().y);

        if (getBody().getLinearVelocity().len() < Config.MAX_SPEED * 2)
            getBody().setLinearVelocity(getBody().getLinearVelocity().scl(1.03f));
        else if (getBody().getLinearVelocity().len() >= Config.MAX_SPEED * 2)
            emitter.allowCompletion();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getAngle() {
        return angle;
    }

    @Override
    public void dispose() {
        super.dispose();
        EffectFactory.getInstance().stopEmitter(emitter);
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        spriteBatch.setColor(Config.COLOR_3_BLUE_MEDIUM);
        spriteBatch.draw(image,
                body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2,
                getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(),
                1, 1,
                angle,
                true);
        spriteBatch.setColor(Color.WHITE);
    }
}
