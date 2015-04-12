package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;

public class EnergyBallBody extends WeaponBody {
    float[] colors;

    public EnergyBallBody(Body body, float radius, float angle, int damage) {
        super(body, radius, null, angle, damage);
        this.damage = damage;
        speed = 1;
        body.setLinearVelocity(body.getLinearVelocity().nor().scl(speed));

        emitter = EffectFactory.getInstance().addEffect(position, EffectFactory.EFFECT_TYPE.FIREBALL);
        emitter.setContinuous(true);

        emitter.getAngle().setHigh(angle - 185, angle - 175);
        emitter.getAngle().setLow(angle - 185, angle - 175);
        emitter.setPosition(body.getPosition().x - radius, body.getPosition().y - radius);

        switch (this.damage) {
            case 1:
                colors = new float[]{1f, 0.12156863f, 0.047058824f};
                break;
            case 2:
                colors = new float[]{0.14509805f, 0.56078434f, 0.77254903f};
                break;
            case 3:
                colors = new float[]{0.12941177f, 0.77254903f, 0.101960786f};
                break;
            case 4:
                colors = new float[]{1f, 0f, 0f};
                break;
            case 5:
                colors = new float[]{0f, 0f, 1f};
                break;
            case 6:
                colors = new float[]{0f, 1f, 0f};
                break;
            default:
                colors = new float[]{1f, 1f, 1f};
        }

        emitter.getTint().setColors(colors);

        addLight(LightFactory.getInstance().getLight(position.x, position.y, Config.TILE_SIZE_X * 2, 6,
                new Color(colors[0], colors[1], colors[2], 1f)));
        light.setStaticLight(false);
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void update() {
        emitter.setPosition(body.getPosition().x, body.getPosition().y);
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
}
