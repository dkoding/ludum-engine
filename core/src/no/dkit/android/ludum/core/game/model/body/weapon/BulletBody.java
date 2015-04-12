package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LightFactory;

public class BulletBody extends WeaponBody {
    public BulletBody(Body body, float radius, TextureRegion image, float angle, int damage) {
        super(body, radius, image, angle, damage);
        this.damage = damage;
        speed = 2;
        body.setLinearVelocity(body.getLinearVelocity().nor().scl(speed));

        switch (damage) {
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

        addLight(LightFactory.getInstance().getLight(position.x, position.y, Config.TILE_SIZE_X, 6, color));
        light.setStaticLight(false);
    }
}
