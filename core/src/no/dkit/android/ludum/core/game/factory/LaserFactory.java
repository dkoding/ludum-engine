package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.DirectionalGameBody;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.body.item.MineBody;
import no.dkit.android.ludum.core.game.model.body.scenery.SpawnBody;
import no.dkit.android.ludum.core.game.model.body.weapon.LaserBody;

import java.util.Iterator;

public class LaserFactory {
    static World world;
    static LaserFactory instance = null;
    static Array<LaserBody> lasers;
    Array<LaserBody> toRemove = new Array<LaserBody>();
    private static Vector2 tmp = new Vector2();
    private static Vector2 end = new Vector2();
    private static Vector2 tmp2 = new Vector2();

    public static void create(World world) {
        if (instance == null)
            instance = new LaserFactory(world);
        else if (LightFactory.world != world) {
            instance = new LaserFactory(world);
        }
    }

    public static LaserFactory getInstance() {
        if (instance == null)
            throw new RuntimeException("Create first!");

        return instance;
    }

    private LaserFactory(World world) {
        lasers = new Array<LaserBody>();

        LaserFactory.world = world;
    }

    public static void createLaserBeam(Vector2 start, float angle, Color color, final boolean playerOwned) {
        tmp.set(start);
        end.set(start).add(new Vector2(0, Config.getDimensions().SCREEN_LONGEST).rotate(angle));
        final FixtureGetter fixtureGetter = new FixtureGetter();

        world.rayCast(new RayCastCallback() {
            Object userData;

            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                userData = fixture.getBody().getUserData();
                if (userData != null && // TODO: Use GameBody and filters...
                        (playerOwned && (userData instanceof AgentBody || userData instanceof DirectionalGameBody || userData instanceof MineBody || userData instanceof SpawnBody))
                        || (!playerOwned && userData instanceof PlayerBody)) {
                    fixtureGetter.setF(fixture, (GameBody) userData);
                    end.set(point.x, point.y);
                    return fraction;
                } else
                    return -1;
            }
        }, start, end);

        tmp2.set(end);
        float length = tmp.sub(tmp2).len();
        LaserBody laserBody = new LaserBody(start, angle, length, color, Color.WHITE);
        lasers.add(laserBody);

        if (fixtureGetter.getF() != null) {
            laserBody.collidedWith(fixtureGetter.getBody(), end);
        }
    }

    public void cleanUp() {
        Iterator<LaserBody> laserIterator = lasers.iterator();

        toRemove.clear();

        while (laserIterator.hasNext()) {
            LaserBody laserBody = laserIterator.next();
            if (!laserBody.isActive()) toRemove.add(laserBody);
        }

        lasers.removeAll(toRemove, true);
    }

    public Array<LaserBody> getLasers() {
        return lasers;
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
}
