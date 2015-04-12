package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class ParticleEmitterBox2D extends com.badlogic.gdx.graphics.g2d.ParticleEmitter {
    final World world;
    final Vector2 startPoint = new Vector2();
    final Vector2 endPoint = new Vector2();

    boolean particleCollided;
    float normalAngle;

    final FixtureGetter fixtureGetter = new FixtureGetter();

    public ParticleEmitterBox2D(World world, ParticleEmitter emitter) {
        super(emitter);
        this.world = world;
    }

    @Override
    protected Particle newParticle(Sprite sprite) {
        return new ParticleBox2D(this, sprite);
    }

    static class FixtureGetter {
        Fixture f;
        private GameBody body;

        public Fixture getF() {
            return f;
        }

        public void setF(Fixture f, GameBody body) {
            this.f = f;
            this.body = body;
        }

        public GameBody getBody() {
            return body;
        }
    }

    public interface Box2DOperations {
        void collidedWith(GameBody gameBody);
    }
}

