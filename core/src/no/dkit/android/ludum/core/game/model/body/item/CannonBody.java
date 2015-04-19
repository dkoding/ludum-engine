package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.DirectionalGameBody;
import no.dkit.android.ludum.core.game.model.loot.Fireball;
import no.dkit.android.ludum.core.game.model.loot.Gun;
import no.dkit.android.ludum.core.game.model.loot.Weapon;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

public class CannonBody extends DirectionalGameBody {
    enum CANNON_TYPE {SINGLE, DOUBLE}

    CANNON_TYPE type;

    Weapon weapon;
    Vector2 origin = new Vector2();
    Vector2 posCpy = new Vector2();
    Vector2 diffToPlayer = new Vector2();

    public CannonBody(Body body, float radius, int direction) {
        this(body, radius, direction, radius, true);
        bodyType = BODY_TYPE.METAL;
    }

    public CannonBody(Body body, float radius, int direction, float offset, boolean searchLight) {
        super(body, radius, direction, offset, true, false);
        bodyType = BODY_TYPE.METAL;

        float random = MathUtils.random();

        if (random < .5f) {
            image = ResourceFactory.getInstance().getItemImage("cannon1");
            type = CANNON_TYPE.SINGLE;
            weapon = new Gun();
            weapon.onPickup(this);
        } else {
            image = ResourceFactory.getInstance().getItemImage("cannon2");
            type = CANNON_TYPE.DOUBLE;
            weapon = new Fireball();
            weapon.onPickup(this);
        }

        if (searchLight) {
            addLight(LightFactory.getInstance().getConeLight(getBody().getPosition(), Config.getDimensions().WORLD_WIDTH, 3, Color.RED, startDirection, 2));
            light.setSoftnessLength(Config.TILE_SIZE_X);
            light.setActive(false);
            light.setStaticLight(false);
            light.setSoft(false);
            light.setXray(true);
        }
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        spriteBatch.setColor(Color.WHITE);

        spriteBatch.draw(image,
                body.getPosition().x - radius,
                body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                MathUtils.radiansToDegrees * body.getAngle(), true);
    }

    @Override
    public void update() {
        if (!isActive()) return;

        posCpy.set(GameModel.getPlayer().position);
        diffToPlayer.set(posCpy.sub(position));

        float wantedAngle = MathUtils.atan2(diffToPlayer.y, diffToPlayer.x);
    /*
                      ^
            3,14 <--- |----> 0
    */
        if (direction == AbstractMap.N
                && (wantedAngle > MathUtils.PI || wantedAngle < 0)) return;
        else if (direction == AbstractMap.S && wantedAngle > 0) return;
        else if (direction == AbstractMap.W
                && (!(wantedAngle < MathUtils.PI / 2f && wantedAngle > -MathUtils.PI / 2f))) return;
        else if (direction == AbstractMap.E
                && (!(wantedAngle > MathUtils.PI / 2f || wantedAngle < -MathUtils.PI / 2f))) return;

        body.setTransform(position.x, position.y, wantedAngle);

        if (diffToPlayer.len() < Config.getDimensions().WORLD_WIDTH / 2f)
            if (MathUtils.random() < (type == CANNON_TYPE.SINGLE ? Config.BULLET_RATE_LOW : Config.BULLET_RATE_MEDIUM)) {
                float angle = body.getAngle() * MathUtils.radiansToDegrees;

                origin.set(position).add(diffToPlayer.scl(.1f));
                posCpy.set(GameModel.getPlayer().position);

                if (type == CANNON_TYPE.SINGLE) {
                    weapon.fire1(origin, posCpy.rotate(MathUtils.random(-1, 1)));
                    createGunSmoke(angle);
                }
                else if (type == CANNON_TYPE.DOUBLE) {
                    weapon.fire1(origin, posCpy.rotate(MathUtils.random(-1, 1)));
                    weapon.fire2(origin, posCpy.rotate(MathUtils.random(-1, 1)));
                }
            }
    }

    private void createGunSmoke(float angle) {
        ParticleEmitter emitter = EffectFactory.getInstance().addEffect(origin, EffectFactory.EFFECT_TYPE.GUNSMOKE);

        emitter.getAngle().setHigh(angle + -20, angle + 20);
        emitter.getAngle().setLow(angle + -20, angle + 20);
        emitter.getVelocity().setLow(1, 1);
        emitter.getVelocity().setHigh(1, 1);

        emitter.getScale().setHigh(emitter.getScale().getHighMin(), emitter.getScale().getHighMax());
        emitter.getScale().setLow(emitter.getScale().getLowMin(), emitter.getScale().getLowMax());
    }
}
