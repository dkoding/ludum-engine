package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

import com.badlogic.gdx.utils.Timer;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.DiscardBody;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class TongueBody extends WeaponBody {
    Body[] rest;

    public TongueBody(Body tip, Body[] rest, float radius, TextureRegion image, float angle, int damage) {
        super(tip, radius, image, angle, damage);

        this.rest = rest;
        ttl = 2000; // MS
        bounce = true;
        this.damage = 1;
        speed = 5;
        tip.setLinearVelocity(tip.getLinearVelocity().nor().scl(speed));

        switch (this.damage) {
            case 1:
                color = Color.PINK;
                break;
            case 2:
                color = Color.valueOf("8F00FF");
                break;
            case 3:
                color = Color.valueOf("4B0082");
                break;
            default:
                color = Color.WHITE;
        }

        for (Body body : rest) {
            body.setUserData(new TongueRest(body, body.getFixtureList().get(0).getShape().getRadius(), image, color));
        }

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
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.setColor(color);
        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                2, 2,
                getAngle(),
                true);
        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void delete() {
        for (int i = 0; i < rest.length; i++) {
            final int x = i;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Body limb = rest[x];
                    limb.setUserData(new DiscardBody(body));

                    if (x == rest.length)
                        body.setUserData(new DiscardBody(body));

                }
            }, .05f * i);
        }
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    @Override
    public void collidedWith(GameBody other) {
        super.collidedWith(other);
    }

    public static class TongueRest extends GameBody {
        private final Color color;
        TextureRegion image;

        public TongueRest(Body body, float radius, TextureRegion image, Color color) {
            super(body, radius);
            this.image = image;
            this.color = color;
        }

        @Override
        public DRAW_LAYER getDrawLayer() {
            return DRAW_LAYER.FRONT;
        }

        @Override
        public void draw(SpriteBatch spriteBatch) {
            spriteBatch.setColor(color);
            spriteBatch.draw(image,
                    body.getPosition().x - radius, body.getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    1, 1,
                    0,
                    true);
            spriteBatch.setColor(Color.WHITE);
        }
    }
}
