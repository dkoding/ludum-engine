package no.dkit.android.ludum.core.game.model.body;

import box2dLight.ConeLight;
import box2dLight.Light;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.LootFactory;
import no.dkit.android.ludum.core.game.factory.ParticleBox2D;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.quest.GameEvent;

public abstract class GameBody {
    protected int health = 1;
    int hurtThreshold = health > 2 ? 2 : 0;
    int criticalThreshold = health > 1 ? 1 : 0;

    public enum BODY_TYPE {
        HUMANOID,
        ALIEN,
        ANIMAL,
        ZOMBIE,
        INSECT,
        STONE,
        WOOD,
        METAL,
        LIQUID,
        SPACESHIP,
        EXPLOSIVE,
        BULLET,
        SHELL,
        ARROW,
        SAW
    }

    public enum DRAW_LAYER {BACK, MEDIUM, FRONT}

    public long created;
    public long ttl = 0;
    protected float alpha = 1f;
    public float alphaMod = 0;
    protected float rotation = 0;
    public float rotationMod = 0;
    public float scale = 1;
    public float scaleMod = 0;
    protected float lightCounter = 0;
    protected float lightDistance = 0;
    public float lightMod = 0;

    public BODY_TYPE bodyType = BODY_TYPE.STONE;

    protected DRAW_LAYER drawLayer = DRAW_LAYER.MEDIUM;
    protected float radius;
    protected Body body;
    protected Light light;
    public boolean active = true;   // Start as inactive otherwise portals will spawn enemies offscreen at start
    protected TextureRegion image;
    public Vector2 position = new Vector2(0, 0);

    protected GameBody(float radius, TextureRegion image) {
        this.radius = radius;
        this.image = image;
        created = System.currentTimeMillis();
    }

    protected GameBody() {
        created = System.currentTimeMillis();
    }

    public GameBody(Body body, float radius) {
        this.body = body;
        body.setUserData(this);
        this.radius = radius;
        created = System.currentTimeMillis();
        this.image = null;
        this.position.set(body.getPosition().x, body.getPosition().y);
    }

    public GameBody(Body body, float radius, TextureRegion image) {
        this.body = body;
        body.setUserData(this);
        this.radius = radius;
        created = System.currentTimeMillis();
        this.image = image;
        this.position.set(body.getPosition().x, body.getPosition().y);
    }

    public Body getBody() {
        return body;
    }

    public final float getRadius() {
        return radius;
    }

    public float getActiveDistance() {
        if (this.light != null)
            return lightDistance;
        else
            return radius;
    }

    public DRAW_LAYER getDrawLayer() {
        return drawLayer;
    }

    public void addLight(Light light) {
        if (this.light != null) this.light.remove();
        this.light = light;
        if (body != null && !(body.getUserData() instanceof DiscardBody))
            light.attachToBody(getBody());
        lightDistance = light.getDistance() - light.getSoftShadowLength();
    }

    public Light getLight() {
        return light;
    }

    public void damage(int damage) {
        health -= damage;
    }

    public void heal(int heal) {
        health += heal;
    }

    protected void attacked() {
    }

    protected void dead() {
    }

    public void onDeath() {
        SoundFactory.getInstance().playDieSound(bodyType);
        EffectFactory.getInstance().addDieEffect(position, bodyType);
        //BodyFactory.getInstance().createLoot(position, LootFactory.getInstance().getRandomLootType());
        GameModel.onEvent(GameEvent.EVENT_TYPE.KILLED, this);
        delete();
    }

    public void hit(int damage) {
        if (isDead()) return;
        this.health -= damage;
        if (isDead()) onDeath();
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean isHurt() {
        return health > criticalThreshold && health <= hurtThreshold;
    }

    public boolean isCritical() {
        return health > 0 && health <= criticalThreshold;
    }

    public void update() {
        if (!active) return;

        this.position.set(body.getPosition().x, body.getPosition().y);

        if (light != null && lightMod != 0) {
            lightCounter += lightMod;

            if (lightCounter + lightMod >= lightDistance || lightCounter + lightMod <= 0) lightMod = -lightMod;

            light.setDistance(lightCounter);
            if (light instanceof ConeLight)
                light.setDirection(rotation);
        }

        if (rotationMod != 0) {
            rotation += rotationMod;
            if (rotation >= 360) rotation -= 360;
            else if (rotation <= 0) rotation += 360;
        }

        if (alphaMod != 0) {
            alpha += alphaMod;
            if (alpha + alphaMod >= 1 || alpha + alphaMod <= 0) alphaMod = -alphaMod;
        }

        if (scaleMod != 0) {
            scale += scaleMod;
            if ((scaleMod > 0 && scale >= 10) || scaleMod < 0 && scale <= 0)
                scaleMod = 0;
        }

        if (ttl > 0 && System.currentTimeMillis() > created + ttl)
            delete();
    }

    public void dispose() {
        if (light != null) {
            light.remove();
            light = null;
        }
    }

    public void collidedWith(GameBody other) {
    }

    public void collidedWith(ParticleBox2D other) {
    }

    protected void hurt() {
    }

    protected void critical() {
    }

    protected void initHealth(int health) {
        this.health = health;
        hurtThreshold = health > 2 ? 2 : 0;
        criticalThreshold = health > 1 ? 1 : 0;
    }

    public void delete() {
        this.active = false;
        body.setLinearVelocity(0, 0);
        dispose();
        body.setUserData(new DiscardBody(body));
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        spriteBatch.draw(image,
                position.x - radius, position.y - radius,
                radius, radius,
                radius * 2, radius * 2,
                scale, scale,
                rotation + 90, true);
    }

    public void setActive(boolean active) {
        this.active = active;
        this.body.setAwake(active); // Do no deactivate bodies as they then won't trigger collissions
        if (light != null) light.setActive(active);
    }

    public boolean isActive() {
        return this.active;
    }

    public void beginContact(Contact contact) {
    }

    public void endContact(Contact contact) {
    }

    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    protected float getMaxImpulse(ContactImpulse impulse) {
        int length = impulse.getCount();
        float maxImpulse = 0;
        for (int i = 0; i < length; i++)
            maxImpulse = Math.max(maxImpulse, impulse.getNormalImpulses()[i]);
        return maxImpulse;
    }

    public TextureRegion getImage() {
        return image;
    }

    public void setDisabled(boolean disabled) {
        body.setActive(!disabled);
    }
}
