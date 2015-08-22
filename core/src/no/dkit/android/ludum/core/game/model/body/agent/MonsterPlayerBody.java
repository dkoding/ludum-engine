package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.model.PlayerData;

public class MonsterPlayerBody extends PlayerBody {
    TextureRegion tailImage;
    private Body[] tail;
    float tailRadius;
    Vector2 eyeOffset = new Vector2();
    Vector2 eyePos1 = new Vector2();
    Vector2 eyePos2 = new Vector2();

    ParticleEmitter eye1;
    ParticleEmitter eye2;

    public MonsterPlayerBody(Body body, float radius, TextureRegion image, TextureRegion tailImage, CONTROL_MODE controlMode) {
        super(body, radius, new PlayerData(), image, controlMode, BODY_TYPE.ALIEN);
        this.tailImage = tailImage;

        eye1 = EffectFactory.getInstance().addEffect(position, EffectFactory.EFFECT_TYPE.EYE);
        eye1.setContinuous(true);
        eye2 = EffectFactory.getInstance().addEffect(position, EffectFactory.EFFECT_TYPE.EYE);
        eye2.setContinuous(true);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        Body t;

        for (int i = 0; i < tail.length; i++) {
            t = tail[i];

            tailRadius = Config.TILE_SIZE_X / 4f / (i + 1);

            spriteBatch.draw(tailImage,
                    t.getPosition().x - tailRadius, t.getPosition().y - tailRadius,
                    tailRadius, tailRadius,
                    tailRadius * 2, tailRadius * 2,
                    1, 1,
                    t.getAngle() * MathUtils.radiansToDegrees,
                    true);
        }

        spriteBatch.draw(image,
                position.x - radius, position.y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                getAngle(),
                true);
    }

    public void addTail(Body[] tail) {
        this.tail = tail;
    }

    @Override
    public void delete() {
        // Don't delete this body
    }

    @Override
    public void setActive(boolean active) {
        // Nope
    }

    @Override
    protected void updateEmission() {
        super.updateEmission();

        eyeOffset.set(Config.TILE_SIZE_X/8f, 0).rotate(angle+90);
        eyePos1.set(position).add(eyeOffset);
        eye1.setPosition(eyePos1.x, eyePos1.y);

        eyePos2.set(position).add(eyeOffset.rotate(180));
        eye2.setPosition(eyePos2.x, eyePos2.y);
    }
}
