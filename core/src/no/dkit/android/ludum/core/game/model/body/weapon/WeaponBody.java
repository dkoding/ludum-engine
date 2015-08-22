package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.PoolableGameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class WeaponBody extends PoolableGameBody {
    static long lastRicochet;
    static long lastHit;

    protected boolean bounce = false;
    protected int damage;
    protected int speed;
    Color color;
    ParticleEmitter emitter;

    float width;
    float height;
    float angle;

    public WeaponBody(Body body, float radius) {
        super(body, radius);
        color = Color.WHITE;
        damage = 1;
        speed = 1;
        bodyType = BODY_TYPE.METAL;
    }

    public WeaponBody(Body body, float radius, TextureRegion image, float angle, int damage) {
        super(body, radius, image);
        color = Color.WHITE;
        this.damage = damage;
        speed = 1;
        bodyType = BODY_TYPE.METAL;
        this.angle = angle;
    }

    protected void hitAgentEffect(AgentBody other) {
        EffectFactory.getInstance().addHitEffect(position, other.bodyType, angle);
        SoundFactory.getInstance().playHitSound(other.bodyType);
    }

    protected void hitNonAgentEffect(GameBody other) {
        EffectFactory.getInstance().addHitEffect(position, other.bodyType, angle);
        SoundFactory.getInstance().playHitSound(other.bodyType);
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active)
            delete();
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        spriteBatch.setColor(color);
        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                rotation,
                true);
        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    @Override
    public void collidedWith(GameBody other) {
        if (!active) return;

        other.hit(damage);

        if (other instanceof AgentBody) {
            hitAgentEffect((AgentBody) other);
        } else {
            hitNonAgentEffect(other);
        }

        if (!bounce) {
            body.setLinearVelocity(0, 0);
            delete();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        contact.setEnabled(bounce);
    }
}
