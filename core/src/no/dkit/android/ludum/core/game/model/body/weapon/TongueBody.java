package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.DiscardBody;

public class TongueBody extends WeaponBody {
    Body[] rest;
    float width;
    float height;
    float angle;

    public TongueBody(Body tip, Body[] rest, float width, float height, TextureRegion image, float angle, int damage) {
        super(tip, width / 2, image, angle, damage);
        this.rest = rest;

        for (Body limb : rest) {
            limb.setUserData(new TongueRest(this));
        }

        this.width = width * 2;
        this.height = height * 2;
        this.angle = angle;

        ttl = 3000; // MS
        bounce = true;
        this.damage = 1;
        speed = 1;
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
        spriteBatch.draw(image,
                body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2,
                getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(),
                1, 1,
                angle,
                true);

          for (Body limb : this.rest) {
              spriteBatch.draw(image,
                      limb.getPosition().x - getWidth() / 2, limb.getPosition().y - getHeight() / 2,
                      getWidth() / 2, getHeight() / 2,
                      getWidth(), getHeight(),
                      1, 1,
                      limb.getAngle(),
                      true);
          }
          //spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      }

      @Override
      public void setActive(boolean active) {
          super.setActive(active);
          for (Body limb : rest) {
              limb.setActive(active);
          }
      }

      @Override
      public void delete() {
          super.delete();
          for (Body limb : rest) {
              limb.setUserData(new DiscardBody(body));
          }
      }
}
