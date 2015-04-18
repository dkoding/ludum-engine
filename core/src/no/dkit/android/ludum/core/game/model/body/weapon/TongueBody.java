package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class TongueBody extends WeaponBody {
    TextureRegion eyeImage;
    TextureRegion pupilImage;
    Body tip;
    Body[] rest;

    public TongueBody(Body tip, Body[] rest, float radius, TextureRegion image, TextureRegion eyeImage, TextureRegion pupilImage, float angle, int damage) {
        super(tip, radius, image, angle, damage);
        final MassData massData = new MassData();
        massData.mass = 0f;

        this.tip = tip;
        this.eyeImage = eyeImage;
        this.pupilImage = pupilImage;

        this.rest = rest;
        ttl = 2000; // MS
        bounce = true;
        this.damage = damage;
        speed = Config.TONGUE_SPEED;

        tip.setMassData(massData);

        switch (this.damage) {
            case 1:
                this.color = Color.RED;
                break;
            case 2:
                this.color = Color.BLUE;
                break;
            case 3:
                this.color = Color.valueOf("4B0082");
                break;
            default:
                this.color = Color.WHITE;
        }

        for (Body body : rest) {
            body.setUserData(new TongueRest(body, body.getFixtureList().get(0).getShape().getRadius()));
            body.setMassData(massData);
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
        spriteBatch.setColor(this.color);

        for (int i = 1; i < rest.length; i++) {
            spriteBatch.draw(image,
                    rest[i].getPosition().x - radius, rest[i].getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    2f/((i*.05f)+1f), 2f/((i*.05f)+1f),
                    0,
                    true);
        }

        spriteBatch.setColor(this.color);

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                2, 2,
                0,
                true);

        spriteBatch.setColor(Color.WHITE);

        if (eyeImage != null)
            spriteBatch.draw(eyeImage,
                    rest[0].getPosition().x - radius, rest[0].getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    2, 2,
                    0,
                    true);

        spriteBatch.setColor(Color.BLUE);

        if (pupilImage != null)
            spriteBatch.draw(pupilImage,
                    rest[0].getPosition().x - radius, rest[0].getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    1, 1,
                    0,
                    true);

        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void delete() {
/*
        body.setUserData(new DiscardBody(body));

        for (Body rest : this.rest) {
            rest.setUserData(new DiscardBody(rest));
*/
/*
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Body limb = rest[x];
                    limb.setUserData(new DiscardBody(limb));

                    if (x == rest.length)
                        body.setUserData(new DiscardBody(body));
                }
            }, .05f * i);
*//*

        }
*/
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    @Override
    public void collidedWith(GameBody other) {
        super.collidedWith(other);
    }

    public void lick(Vector2 target, Vector2 firingDirection) {
        slurp(target);
        tip.applyForceToCenter(firingDirection.nor().scl(speed), true);
        for (Body limb : rest) {
            limb.applyForceToCenter(firingDirection.nor().scl(speed), true);
        }
    }

    public void slurp(Vector2 target) {
        tip.setTransform(target.x, target.y, 0);
        for (Body limb : rest) {
            limb.setTransform(target.x, target.y, 0);
        }
    }

    @Override
    protected void hitNonAgentEffect(GameBody other) {
    }
}
