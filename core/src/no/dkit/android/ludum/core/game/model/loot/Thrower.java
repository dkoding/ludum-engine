package no.dkit.android.ludum.core.game.model.loot;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.ParticleEmitterBox2D;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;

public abstract class Thrower extends Weapon {
    private final EffectFactory.EFFECT_TYPE effect;
    ParticleEmitter emitter;
    ParticleEmitterBox2D.Box2DOperations operations;
    float arc;

    public Thrower(LOOT_TYPE type, String imageName, EffectFactory.EFFECT_TYPE effect) {
        super(type, imageName);
        cooldown1 = 3000;
        cooldown2 = 3000;
        this.effect = effect;
        operations = new ParticleEmitterBox2D.Box2DOperations() {
            @Override
            public void collidedWith(GameBody gameBody) {
                if (owner instanceof PlayerBody && !(gameBody instanceof PlayerBody)) {
                    gameBody.hit(1);
                } else if (!(owner instanceof PlayerBody) && gameBody instanceof PlayerBody) {
                    gameBody.hit(1);
                }
            }
        };
    }

    @Override
    public void fire1() {
        arc = 2;
        fire();
    }

    @Override
    public void fire2() {
        arc = 10;
        fire();
    }

    private void fire() {
        emitter = EffectFactory.getInstance().addEffect(owner.position, effect, operations);
        float angle = owner instanceof PlayerBody ? ((PlayerBody) owner).getFiringAngle() : owner.getBody().getAngle() * MathUtils.radiansToDegrees;
        emitter.getAngle().setHigh(angle - arc, angle + arc);
        emitter.getAngle().setLow(angle - arc, angle + arc);
        emitter.getVelocity().setLow(Config.TILE_SIZE_X);
        emitter.getVelocity().setHigh(Config.TILE_SIZE_X * 4);
        emitter.start();
    }

    public void update(GameBody owner) {
        if (emitter == null || !owner.isActive()) return;
        emitter.setPosition(owner.position.x, owner.position.y);
    }
}
