
package no.dkit.android.ludum.core.game.model.body.agent;

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
import no.dkit.android.ludum.core.game.model.body.item.CannonBody;
import no.dkit.android.ludum.core.game.model.body.scenery.BlockBody;
import no.dkit.android.ludum.core.game.model.body.scenery.LampBody;
import no.dkit.android.ludum.core.game.quest.GameEvent;
import no.dkit.android.ludum.core.game.quest.GameEventListener;

import java.util.HashMap;
import java.util.Map;

public class PlayerBody extends AgentBody implements GameEventListener {
    private boolean hasCollided = false;
    private Vector2 collidePos;
    private Body collideBody;
    private boolean justJumped;
    private long lastJump;

    public void onLick() {
    }

    public void onSlurp() {
    }

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

    private Vector2 jump = new Vector2();

    public void scheduleJump(float x, float y) {
        jump.set(x, y);
    }

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

        //SoundFactory.getInstance().playHitSound(bodyType);

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
        SoundFactory.getInstance().playMusic(SoundFactory.MUSIC_TYPE.LOSE);

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
                scale, scale,
                angle,
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
            }
        } else if (other instanceof BlockBody || other instanceof CannonBody || other instanceof LampBody) { // Walls
            justJumped = System.currentTimeMillis() - 50 < lastJump;
            if (hasCollided || justJumped) return;
            onLanded();
            hasCollided = true;
            collidePos = position;
            collideBody = other.getBody();
            //BodyFactory.getInstance().weld(other.getBody(), this.getBody());
        }
    }

    protected void onLanded() {

    }

    private void loosenGrip() {
        hasCollided = false;
        collidePos = null;
        collideBody = null;
/*
        final JointEdge next = getBody().getJointList().select(new Predicate<JointEdge>() {
            @Override
            public boolean evaluate(JointEdge arg0) {
                return arg0 != null && arg0.joint != null && arg0.joint instanceof WeldJoint;
            }
        }).iterator().next();
        final WeldJoint joint = (WeldJoint) next.joint;
        joint.setUserData(new DiscardJoint(next.joint));
*/
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
/*
            this.tmp.set(firingDirection).scl(Config.TILE_SIZE_X / 2);
            tmp.add(position);
*/
            weapon.fire1(position, touchPos);
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

    public void jump(float x, float y) {
        if (!hasCollided) return;

        lastJump = System.currentTimeMillis();
        loosenGrip();
        SoundFactory.getInstance().playSound(SoundFactory.SOUND_TYPE.JUMP);
        body.setLinearVelocity(x * Config.JUMP_STRENGTH, y * Config.JUMP_STRENGTH);
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

    private void parseControllerInput() {
        if (jump.len() > 0) {
            jump(jump.x, jump.y);
            jump.set(0, 0);
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
        if (hasCollided) {
            body.setGravityScale(0);
            this.position.set(collidePos.x, collidePos.y);
            body.setTransform(collidePos.x, collidePos.y, 0);
        } else {
            body.setGravityScale(1);
            super.update();
        }

        parseControllerInput();
        determineAngle();
    }

    @Override
    protected void determineAngle() {
        if (hasCollided) {
            angle = (MathUtils.radiansToDegrees * MathUtils.atan2(collideBody.getPosition().x - position.x, collideBody.getPosition().y - position.y)) + 90;
        } else {
            if (speed >= Config.EPSILON) {
                angle = MathUtils.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x);
                angle *= MathUtils.radiansToDegrees;
            }
        }
    }

    public void removeAllKeys() {
        input.clear();
    }

    @Override
    protected void updateEmission() {
        if (controlMode == CONTROL_MODE.NEWTONIAN) {
            trailEmitter.setPosition(position.x, position.y);
            emitterAngle = body.getAngle() - 180;
            emissionSpeed = speedVector.len();

            tmp.set(-Config.TILE_SIZE_X / 2f, 0).rotate(angle);
            tmp2.set(position).add(tmp);

            trailEmitter.setPosition(tmp2.x, tmp2.y);
            trailEmitter.getAngle().setHigh(emitterAngle - emitterArc, emitterAngle + emitterArc);
            trailEmitter.getAngle().setLow(emitterAngle - emitterArc, emitterAngle + emitterArc);
            trailEmitter.getVelocity().setHigh(0, emissionSpeed);
            trailEmitter.getVelocity().setLow(0, emissionSpeed / 4);
            trailEmitter.getEmission().setLow(emission * emissionSpeed, emission * emissionSpeed);
            trailEmitter.getEmission().setHigh(emission * emissionSpeed, emission * emissionSpeed);
        } else {
            super.updateEmission();
        }
    }

    public void setControlMode(CONTROL_MODE controlMode) {
        this.controlMode = controlMode;
    }
}
