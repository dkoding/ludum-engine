package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.model.PlayerData;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.weapon.MeleeBody;

public class MonsterPlayerBody extends PlayerBody {
    TextureRegion tailImage;
    private Body[] tail;
    float tailRadius;
    Vector2 eyeOffset = new Vector2();
    Vector2 eyePos1 = new Vector2();
    Vector2 eyePos2 = new Vector2();

    long lastCollission;

    boolean tweening = false;

    ParticleEmitter eye1;
    ParticleEmitter eye2;

    private MeleeBody weaponBody;
    private MeleeBody weaponBody2;

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

        if (weapon != null) {
            spriteBatch.draw(weaponBody.getImage(),
                    weaponBody.getPosition().x - radius, weaponBody.getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    2, 2,
                    weaponBody.getAngle() * MathUtils.radiansToDegrees + 90,
                    true);

            spriteBatch.draw(weaponBody2.getImage(),
                    weaponBody2.getPosition().x - radius, weaponBody2.getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    2, -2,
                    weaponBody2.getAngle() * MathUtils.radiansToDegrees + 90,
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
        trailEmitter.setPosition(position.x, position.y);
/*
        trailEmitter.getAngle().setHigh(angle - emitterArc, angle + emitterArc);
        trailEmitter.getAngle().setLow(angle - emitterArc, angle + emitterArc);
        trailEmitter.getVelocity().setHigh(unconscious ? 0 : speed / 2, unconscious ? 0 : speed);
        trailEmitter.getVelocity().setLow(0, unconscious ? 0 : speed / 4);
        trailEmitter.getEmission().setLow(0, 0);
        trailEmitter.getEmission().setHigh(emission, emission);
*/

        eyeOffset.set(Config.TILE_SIZE_X / 8f, 0).rotate(angle + 90);
        eyePos1.set(position).add(eyeOffset);
        eye1.setPosition(eyePos1.x, eyePos1.y);

        eyePos2.set(position).add(eyeOffset.rotate(180));
        eye2.setPosition(eyePos2.x, eyePos2.y);
    }

    public void enableMelee() {
        tweening = true;

        if (Config.DEBUGTEXT)
            System.out.println("ENABLING MELEE!");

        weaponBody.enable();
        weaponBody2.enable();

/*
        if (goingLeft) {
            enable(left);
            disable(right);
        } else {
            disable(left);
            enable(right);
        }
*/
    }

    public void disableMelee() {
        tweening = false;

        if (Config.DEBUGTEXT)
            System.out.println("DISABLING MELEE!");

        weaponBody.disable();
        weaponBody2.disable();
    }

    public void addMeleeWeapons(MeleeBody weapon, MeleeBody weapon2) {
        this.weaponBody = weapon;
        this.weaponBody2 = weapon2;
    }

    public void update() {
        super.update();

        final float bodyAngleRad = (90 + getAngle()) * MathUtils.degreesToRadians;

        if (tweening) {
            this.weaponBody.setTransform(this.position.x, this.position.y, weaponBody.getAngle());
            this.weaponBody2.setTransform(this.position.x, this.position.y, weaponBody2.getAngle());
        } else {
            this.weaponBody.setTransform(this.position.x, this.position.y, bodyAngleRad);
            this.weaponBody2.setTransform(this.position.x, this.position.y, bodyAngleRad);
        }
    }

    @Override
    public void collidedWith(GameBody other) {
        if (System.currentTimeMillis() < lastCollission + 200) return;

        lastCollission = System.currentTimeMillis();

/*
        if (other instanceof AgentBody) {
            EffectFactory.getInstance().addHitEffect(other.position, other.bodyType, weaponBody.getAngle() * MathUtils.radiansToDegrees + 90);
            EffectFactory.getInstance().addHitEffect(other.position, other.bodyType, weaponBody2.getAngle() * MathUtils.radiansToDegrees + 90);
            other.hit(10000);
        }
*/
    }
}
