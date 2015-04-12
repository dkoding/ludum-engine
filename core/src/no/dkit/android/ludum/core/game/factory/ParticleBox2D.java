package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class ParticleBox2D extends ParticleEmitter.Particle {
    private final static float EPSILON = 0.02f;

    Object userData;

    private ParticleEmitterBox2D box2DParticleEmitter;

    public ParticleBox2D(ParticleEmitterBox2D box2DParticleEmitter, Sprite sprite) {
        super(sprite);
        this.box2DParticleEmitter = box2DParticleEmitter;
    }

    @Override
    public void translate(float velocityX, float velocityY) {
        //if ((velocityX * velocityX + velocityY * velocityY) < EPSILON) return;

        final float x = (getX() + getWidth() / 2f);
        final float y = (getY() + getHeight() / 2f);

        box2DParticleEmitter.particleCollided = false;
        box2DParticleEmitter.startPoint.set(x, y);
        box2DParticleEmitter.endPoint.set(x + velocityX, y + velocityY);
        if (box2DParticleEmitter.world != null) {
            box2DParticleEmitter.world.rayCast(new RayCastCallback() {
                public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                    userData = fixture.getBody().getUserData();

                    if (userData != null && shouldCollide((GameBody) userData)) {
                        box2DParticleEmitter.particleCollided = true;
                        box2DParticleEmitter.normalAngle = MathUtils.atan2(normal.y, normal.x) * MathUtils.radiansToDegrees;
                        box2DParticleEmitter.fixtureGetter.setF(fixture, (GameBody) userData);
                        return fraction;
                    } else
                        return -1;
                }

            }, box2DParticleEmitter.startPoint, box2DParticleEmitter.endPoint);
        }

        if (box2DParticleEmitter.particleCollided) {
            angle = 2f * box2DParticleEmitter.normalAngle - angle - 180f;
            angleCos = MathUtils.cosDeg(angle);
            angleSin = MathUtils.sinDeg(angle);
            velocityX *= angleCos;
            velocityY *= angleSin;
            velocityDiff = .001f;
            velocity = .001f;
        }

        if (box2DParticleEmitter.fixtureGetter.getF() != null)
            this.collidedWith(box2DParticleEmitter.fixtureGetter.getBody());

        super.translate(velocityX, velocityY);
    }

    protected void collidedWith(GameBody body) {

    }

    protected boolean shouldCollide(GameBody gameBody) {
        final Body body = gameBody.getBody();

        if (body != null) {
            final Fixture fixture = body.getFixtureList().get(0);
            if (fixture != null)
                return (fixture.getFilterData().maskBits & Config.CATEGORY_PARTICLE) != 0
                        && (fixture.getFilterData().maskBits & BodyFactory.PARTICLE_BITS) != 0;
        }
        return false;
    }
}
