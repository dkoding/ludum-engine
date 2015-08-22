package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.mind.Mind;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.factory.TextItem;
import no.dkit.android.ludum.core.game.model.PlayerData;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.quest.GameEvent;
import no.dkit.android.ludum.core.game.quest.GameEventListener;

import java.util.HashMap;
import java.util.Map;

public class PlayerBody extends AgentBody implements GameEventListener {
    public enum CONTROL_MODE {NEWTONIAN, DIRECT, VEHICLE}

    CONTROL_MODE controlMode = CONTROL_MODE.NEWTONIAN;

    float emitterAngle;
    float emissionSpeed;

    Vector2 speedVector = new Vector2();
    public Vector2 firingDirection = new Vector2(0, 1);
    public float firingAngle;
    private Vector2 tmp = new Vector2(0, 0);
    private Vector2 tmp2 = new Vector2(0, 0);
    private Vector2 touchPos = new Vector2(0, 0);
    PlayerData data;
    protected boolean fixedRotation;
    private long effectTimer = System.currentTimeMillis();

    private Float impulse = 0f;
    private Map<GameEvent.EVENT_TYPE, Array<GameEvent>> listeners;

    private Map<Integer, Integer> input = new HashMap<Integer, Integer>();

    public PlayerBody(Body body, float radius, PlayerData data, TextureRegion image, CONTROL_MODE controlMode, BODY_TYPE type) {
        super(body, radius, image);
        this.bodyType = type;
        this.controlMode = controlMode;
        alive = true;
        this.fixedRotation = body.isFixedRotation();

        health = data.getHealth();
        maxSpeed = data.getMaxSpeed();
        minSpeed = data.getMinSpeed();

        this.data = data;
        this.listeners = new HashMap<GameEvent.EVENT_TYPE, Array<GameEvent>>();

        mind = new PrioritizingMind();
        mind.setState(Mind.MindState.NEUTRAL);

        setupEmitter(EffectFactory.EFFECT_TYPE.SMOKE, 5, 200);
    }

    public PlayerBody(Body body, float radius, PlayerData data) {
        super(body, radius);

        alive = true;
        health = data.getHealth();
        maxSpeed = data.getMaxSpeed();
        minSpeed = data.getMinSpeed();

        this.data = data;
        this.listeners = new HashMap<GameEvent.EVENT_TYPE, Array<GameEvent>>();

        setupEmitter(EffectFactory.EFFECT_TYPE.SMOKE, 5, 200);
    }

    @Override
    public void hit(int damage) {
        if (isDead()) return;

        if (data.getArmor() > 0) {
            data.removeArmor(damage);
            if (data.getArmor() < 0) data.setArmor(0);
        } else {
            health -= damage;
            data.setHealth(health);
        }

        SoundFactory.getInstance().playHitSound(bodyType);
        final ParticleEmitter particleEmitter = EffectFactory.getInstance().addHitEffect(position, bodyType);
        particleEmitter.getAngle().setHigh(90, 90);
        particleEmitter.getAngle().setLow(90, 90);

        if (isDead())
            onDeath();
    }

    @Override
    public void onDeath() {
        SoundFactory.getInstance().playDieSound(bodyType);
        EffectFactory.getInstance().addDieEffect(position, bodyType);

        TextFactory.getInstance().addTexts(0,
                new TextItem("YOU DIED!!!", 0, Config.getDimensions().SCREEN_HEIGHT / 3, Color.RED),
                new TextItem("Tap to exit...", 0, Config.getDimensions().SCREEN_HEIGHT / 4, Color.WHITE)
        );

        unconscious = true;
        delete();
    }

    public void heal(int heal) {
        super.heal(heal);
        data.setHealth(health);
    }

    public int getHealth() {
        return data.getHealth();
    }

    public int getScore() {
        return data.getScore();
    }

    public void addScore(int score) {
        data.addScore(score);
    }

    public int getOrbs() {
        return data.getOrbs();
    }

    public void addArmor(int armor) {
        data.addArmor(armor);
    }

    public int getArmor() {
        return data.getArmor();
    }

    public int getCredits() {
        return data.getCredits();
    }

    public int getKeys() {
        return data.getKeys();
    }

    public void addCredits(int credits) {
        this.data.addCredits(credits);
    }

    public void removeCredits(int credits) {
        data.removeCredits(credits);
    }

    public void addOrbs(int orbs) {
        data.addOrbs(orbs);
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        boolean flip = false;

        if (fixedRotation && body.getLinearVelocity().x < 0)
            flip = true;

        spriteBatch.draw(image,
                position.x - radius, position.y - radius,
                radius, radius,
                radius * 2, radius * 2,
                scale, flip ? -scale : scale,
                fixedRotation ? 90 : angle,
                true);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        if (contact.getFixtureA().getBody().getUserData() == null || contact.getFixtureB().getBody().getUserData() == null)
            return;

        addImpulse(getMaxImpulse(impulse));
    }

    @Override
    public void collidedWith(GameBody other) {
        if (other instanceof AgentBody) { // They hurt me
            hit(1);

            if (System.currentTimeMillis() - 1000 > effectTimer) {
                effectTimer = System.currentTimeMillis();
                SoundFactory.getInstance().playHitSound(bodyType);
                EffectFactory.getInstance().addHitEffect(position, bodyType);
                TextFactory.getInstance().addText(new TextItem("Avoid ENEMIES", 0, Config.getDimensions().SCREEN_HEIGHT / 3, Color.WHITE), -.01f); // TODO: Change this to avoid object creation on repeat events
            }
        }
    }

    public void removeOrbs(int value) {
        data.removeOrbs(value);
    }

    public void collide(int damage) {
        hit(damage);
        TextFactory.getInstance().addText("Avoid smashing into walls");
    }

    public void fireBullet1(Vector2 touchPos) {
        prepareShot(touchPos);
        if (weapon != null) {
            this.tmp.set(firingDirection).scl(Config.TILE_SIZE_X / 2);
            tmp.add(position);
            weapon.fire1(tmp, touchPos);
        }
    }

    public void fireBullet1() {
        if (weapon != null) {
/*
            tmpVector.set(position).rotate(angle);
            touchPos.set(position).add(tmpVector);
*/
            weapon.fire1(position, touchPos);
        }
    }

    public void fireBullet2(Vector2 touchPos) {
        prepareShot(touchPos);
        if (weapon != null) {
            this.tmp.set(firingDirection).scl(Config.TILE_SIZE_X / 2);
            tmp.add(position);
            weapon.fire2(tmp, touchPos);
        }
    }

    public void fireBullet2() {
        if (weapon != null) {
/*
            tmpVector.set(position).rotate(angle);
            touchPos.set(position).add(tmpVector);
*/
            weapon.fire2(position, touchPos);
        }
    }

    private void prepareShot(Vector2 touchPos) {
        this.touchPos.set(touchPos.x, touchPos.y);
        updateFiringAngle();
    }

    private void updateFiringAngle() {
        firingDirection.set(this.touchPos.x - position.x, this.touchPos.y - position.y);
        firingAngle = MathUtils.radiansToDegrees * MathUtils.atan2(firingDirection.y, firingDirection.x);
    }

    public float getFiringAngle() {
        return firingAngle;
    }

    public void addImpulse(float impulse) {
        this.impulse = impulse;
    }

    public float getImpulse() {
        try {
            return impulse;
        } finally {
            impulse = 0f;
        }
    }

    public void setAim(float v, float v1) {
        touchPos.set(v, v1);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        contact.setRestitution(0);
    }

    public void register(GameEvent.EVENT_TYPE eventType, GameEvent event) {
        if (!listeners.containsKey(eventType))
            listeners.put(eventType, new Array<GameEvent>());
        listeners.get(eventType).add(event);
    }

    public void onEvent(GameEvent.EVENT_TYPE eventType, GameBody gameBody) {
        Array<GameEvent> events = listeners.get(eventType);
        if (events != null)
            for (GameEvent event : events) {
                event.fire(gameBody);
            }
    }

    public PlayerData getData() {
        return data;
    }

    public void addKeyPress(int i) {
        mind.clear();
        input.put(i, 1);
    }

    public void removeKeyPress(int i) {
        input.put(i, 0);
    }

    @Override
    public void update() {
        super.update();
        parseControllerInput();
    }

    @Override
    protected void determineAngle() {
        if (controlMode == CONTROL_MODE.NEWTONIAN)
            angle = getBody().getAngle();
        else
            super.determineAngle();
    }

    private void parseControllerInput() {
        if(unconscious) return;

        if (controlMode == CONTROL_MODE.DIRECT)
            parseDirectControllerInput();
        else if (controlMode == CONTROL_MODE.VEHICLE)
            parseVehicleControllerInput();
        else if (controlMode == CONTROL_MODE.NEWTONIAN)
            parseNewtonianControllerInput();
    }

    private void parseDirectControllerInput() {
        Integer inputKey;

        speedVector.set(0, 0);

        for (Integer integer : input.keySet()) {
            inputKey = input.get(integer);

            if (inputKey != 0) {
                if (integer == Input.Keys.W)
                    speedVector.set(speedVector.x, inputKey);
                if (integer == Input.Keys.S)
                    speedVector.set(speedVector.x, -inputKey);
                if (integer == Input.Keys.A)
                    speedVector.set(-inputKey, speedVector.y);
                if (integer == Input.Keys.D)
                    speedVector.set(inputKey, speedVector.y);
                if (integer == Input.Keys.SPACE && inputKey > 0)
                    fireBullet1();
                if (integer == Input.Keys.SHIFT_LEFT && inputKey > 0)
                    fireBullet2();
            }
        }

        getBody().setLinearDamping(2f);
        getBody().applyForceToCenter(speedVector, true);
    }

    private void parseNewtonianControllerInput() {
        Integer inputKey;

        speedVector.set(.5f, 0).rotate(angle);

        for (Integer integer : input.keySet()) {
            inputKey = input.get(integer);

            if (inputKey != 0) {
                if (integer == Input.Keys.W) {
                    getBody().applyForceToCenter(speedVector, false);
                }
                if (integer == Input.Keys.S)
                    getBody().applyForceToCenter(speedVector.scl(.1f).rotate(180), false);
                if (integer == Input.Keys.A)
                    getBody().applyAngularImpulse(500, true);
                if (integer == Input.Keys.D)
                    getBody().applyAngularImpulse(-500, true);
                if (integer == Input.Keys.SPACE && inputKey > 0)
                    fireBullet1();
                if (integer == Input.Keys.SHIFT_LEFT && inputKey > 0)
                    fireBullet2();
            }
        }

        body.setAngularDamping(3f);
        body.setLinearDamping(.2f);
    }

    private void parseVehicleControllerInput() {
        Integer inputKey;

        speedVector.set(0, 0);

        for (Integer integer : input.keySet()) {
            inputKey = input.get(integer);

            if (inputKey != 0) {
                if (integer == Input.Keys.W)
                    if (speed < Config.PLAYER_MIN_SPEED)
                        getBody().setLinearVelocity(new Vector2(Config.PLAYER_MIN_SPEED, 0).rotate(angle));
                    else if (speed < Config.PLAYER_MAX_SPEED) {
                        getBody().setLinearVelocity(getBody().getLinearVelocity().scl(1.1f));
                    }
                if (integer == Input.Keys.S)
                    if (speed < Config.PLAYER_MIN_SPEED) {
                        getBody().setLinearVelocity(new Vector2(0, 0));
                    } else {
                        getBody().setLinearVelocity(getBody().getLinearVelocity().scl(0.9f));
                    }
                if (integer == Input.Keys.A)
                    getBody().setLinearVelocity(getBody().getLinearVelocity().rotate(1f));
                if (integer == Input.Keys.D)
                    getBody().setLinearVelocity(getBody().getLinearVelocity().rotate(-1f));
                if (integer == Input.Keys.SPACE && inputKey > 0)
                    fireBullet1();
                if (integer == Input.Keys.SHIFT_LEFT && inputKey > 0)
                    fireBullet2();
            }
        }

        body.setAngularDamping(2f);
        body.setLinearDamping(.01f);

    }

    public void removeAllKeys() {
        input.clear();
    }

    public void setControlMode(CONTROL_MODE controlMode) {
        this.controlMode = controlMode;
    }
}
