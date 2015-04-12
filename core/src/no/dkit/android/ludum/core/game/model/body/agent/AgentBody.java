package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.mind.Mind;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.ParticleBox2D;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.PoolableGameBody;
import no.dkit.android.ludum.core.game.model.body.scenery.BlockBody;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.loot.Weapon;

public class AgentBody extends PoolableGameBody {
    boolean falling = false;
    Mind mind;
    int weaponIndex = 0;
    Array<Weapon> weapons = new Array<Weapon>();
    Weapon weapon;
    Vector2 angleVector = new Vector2();
    Vector2 tmpVector = new Vector2();
    protected float emitterArc = 5;
    protected float emission = 200;

    int numPlatforms = 0;
    boolean trackingPlatforms = false;

    TextureRegion weaponImage;

    ParticleEmitter trailEmitter;
    EffectFactory.EFFECT_TYPE trailType;

    public float maxSpeed = Config.MAX_SPEED;
    public float minSpeed = Config.MIN_SPEED;

    float speed;
    float angle;
    private boolean isBoss = false;

    private long unconsciousEnd = 0;
    protected boolean unconscious = false;
    private long angleUpdatesStart = 0;
    private long playerFocusedEnd = 0;
    protected boolean updateAngles = true;
    protected boolean playerFocused = false;
    public boolean flying = false;
    private Neighborhood neighborhood;

    public AgentBody(Body body, float radius, TextureRegion image) {
        super(body, radius);
        this.image = image;
    }

    public AgentBody(Body body, float radius) {
        super(body, radius);
    }

    public AgentBody() {
        super();
    }

    public void setMind(Mind mind) {
        this.mind = mind;
    }

    public Mind getMind() {
        return mind;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        angleVector.set(MathUtils.cos(angle), MathUtils.sin(angle));
        body.setTransform(body.getPosition().x, body.getPosition().y, angle * MathUtils.degreesToRadians);
    }

    public static float getShortestAngle(float angle1, float angle2) {
        float difference = angle2 - angle1;
        float lower = -180.0f;
        float upper = +180.0f;

        if (upper <= lower)
            throw new ArithmeticException("Rotary bounds are of negative or zero size");

        float distance = upper - lower;
        float times = (float) Math.floor((difference - lower) / distance);

        return difference - (times * distance);
    }

    public float getSpeed() {
        return speed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    @Override
    public void update() {
        if (!active) return;

        super.update();

        if (unconscious) {
            if (System.currentTimeMillis() > unconsciousEnd) {
                setUnconscious(false, 0);
            }
        }

        if (!updateAngles) {
            if (System.currentTimeMillis() > angleUpdatesStart) {
                setAngleUpdates(true, 0);
            }
        }

        if (playerFocused) {
            if (System.currentTimeMillis() > playerFocusedEnd) {
                setPlayerFocused(false, 0);
            }
        }

        if (weapon != null) {
            weapon.update(this);
            if (!(this instanceof PlayerBody) && mind.getState().equals(Mind.MindState.ATTACK))
                if (MathUtils.random() < Config.BULLET_RATE_HIGH) fireBullet();
        }

        useMind();

        if (falling && scale <= 0)
            onDeath();

        if (trailEmitter != null)
            updateEmission();
    }

    private void useMind() {
        if (!unconscious && !falling) {
            getBody().applyForceToCenter(getMind().calculate(this), true);

            Vector2 linearVelocity = getBody().getLinearVelocity();
            speed = linearVelocity.len();

            if (speed > maxSpeed)
                body.setLinearVelocity(linearVelocity.nor().scl(maxSpeed));

            if (!updateAngles) {
                return;
            }

            if (!(this instanceof PlayerBody)) {
                if (speed < Config.EPSILON)
                    randomSpeed();
                else if (speed < minSpeed)
                    accelerate(linearVelocity);

                if (playerFocused || flying) {
                    if (getMind().getState() == Mind.MindState.DEFEND) {
                        tmpVector.set(position).sub(GameModel.getPlayer().position);
                        angleVector.set(tmpVector.x, tmpVector.y);
                    } else {
                        tmpVector.set(GameModel.getPlayer().position).sub(position);
                        angleVector.set(tmpVector.x, tmpVector.y);
                    }
                } else {
                    angleVector.set(body.getLinearVelocity().x, body.getLinearVelocity().y); // Head in direction of movement
                }
            } else {
                angleVector.set(body.getLinearVelocity().x, body.getLinearVelocity().y); // Head in direction of movement
            }
        }

        determineAngle();
    }

    protected void determineAngle() {
        if (unconscious) {
            angle = body.getAngle() * MathUtils.radiansToDegrees;
        } else if (speed >= Config.EPSILON) {
            angle = MathUtils.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x);
            body.setTransform(body.getPosition().x, body.getPosition().y, angle);
            angle *= MathUtils.radiansToDegrees;
        }
    }

    private void accelerate(Vector2 linearVelocity) {
        getBody().setLinearVelocity(linearVelocity.scl(1.1f));
    }

    public void randomSpeed() {
        getBody().setLinearVelocity(MathUtils.random(maxSpeed * 2) - maxSpeed, MathUtils.random(maxSpeed * 2) - maxSpeed);
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        if (isCritical()) {
            spriteBatch.setColor(Color.RED);
        } else if (isHurt()) {
            spriteBatch.setColor(Color.ORANGE);
        } else {
            spriteBatch.setColor(Color.WHITE);
        }

        if (mind.getState().equals(Mind.MindState.ATTACK)) {
            spriteBatch.setColor(Color.RED);
        } else if (mind.getState().equals(Mind.MindState.DEFEND)) {
            spriteBatch.setColor(Color.GREEN);
        } else if (mind.getState().equals(Mind.MindState.NEUTRAL)) {
            spriteBatch.setColor(Color.WHITE);
        }

        boolean flip = false;

        if (body.isFixedRotation() && body.getLinearVelocity().x < 0)
            flip = true;

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                flip ? radius * -2 : radius * 2, radius * 2,
                scale, scale,
                angle,
                true);

        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void collidedWith(ParticleBox2D other) {
    }

    @Override
    public void collidedWith(GameBody other) {
        if (other instanceof BlockBody) {
            getBody().getLinearVelocity().set(0, 0);
            if (position.x >= other.position.x + (other.getRadius()))
                getBody().getLinearVelocity().add(-minSpeed, 0);
            if (position.x <= other.position.x - (other.getRadius()))
                getBody().getLinearVelocity().add(minSpeed, 0);
            if (position.y >= other.position.y + (other.getRadius()))
                getBody().getLinearVelocity().add(0, -minSpeed);
            if (position.y <= other.position.y - (other.getRadius()))
                getBody().getLinearVelocity().add(0, minSpeed);

            float distance = getBody().getLinearVelocity().len();

            if (distance > 0)
                getBody().getLinearVelocity().scl(maxSpeed / distance);
        }
    }

    @Override
    protected void hurt() {
        getMind().setState(Mind.MindState.DEFEND);
    }

    @Override
    protected void critical() {
        getMind().setState(Mind.MindState.ATTACK);
    }

    public void offScreen() {
        if (isBoss) return;
        delete();
    }

    final public void fireBullet() {
        if (weapon != null)
            weapon.fire(this.position, GameModel.getPlayer().position);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return falling ? DRAW_LAYER.BACK : DRAW_LAYER.FRONT;
    }

    @Override
    protected void dead() {
        if (neighborhood != null)
            neighborhood.removeAgentBody(this);

        onDeath();
    }

    @Override
    public void hit(int damage) {
        if (!isActive()) return;
        if (isDead()) return;
        damage(damage);
        if (isDead()) onDeath();
        else {
            if (isCritical()) critical();
            else if (isHurt()) hurt();
            else attacked();
        }
    }

    @Override
    protected void attacked() {
        getMind().setState(Mind.MindState.ATTACK);
    }

    public void setBoss() {
        this.isBoss = true;
    }

    public void addLoot(Loot loot) {
        loot.onPickup(this);
    }

    public Array<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeaponIndex(int weaponIndex) {
        this.weaponIndex = weaponIndex;
        setWeapon(weapons.get(weaponIndex));
    }

    public void nextWeapon() {
        weaponIndex++;

        if (weaponIndex > weapons.size - 1) weaponIndex = 0;

        setWeapon(weapons.get(weaponIndex));
    }

    public void prevWeapon() {
        weaponIndex--;

        if (weaponIndex < 0) weaponIndex = weapons.size - 1;

        setWeapon(weapons.get(weaponIndex));
    }

    // Do NOT call this directly!
    public void addWeapon(Weapon weapon) {
        int i = weapons.indexOf(weapon, false);
        if (i == -1) {
            weapons.add(weapon);
            setWeapon(weapon);
        } else {
            weapons.get(i).ammo += MathUtils.random(2, 10);
        }
    }

    public Weapon getWeapon(Loot.LOOT_TYPE loot_type) {
        for (Weapon weapon : weapons) {
            if (weapon.getType() == loot_type)
                return weapon;
        }
        return null;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    private void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        this.weaponImage = ResourceFactory.getInstance().getWeaponImage(weapon.getImageName());
    }

    public void setUnconscious(boolean unconscious, long timeMillis) {
        this.unconscious = unconscious;
        if (this.unconscious) {
            unconsciousEnd = System.currentTimeMillis() + timeMillis;
        }
    }

    public void setAngleUpdates(boolean angleUpdates, long timeMillis) {
        this.updateAngles = angleUpdates;
        if (!this.updateAngles) {
            angleUpdatesStart = System.currentTimeMillis() + timeMillis;
        }
    }

    public void setPlayerFocused(boolean playerFocused, long timeMillis) {
        this.playerFocused = playerFocused;
        if (this.playerFocused) {
            playerFocusedEnd = System.currentTimeMillis() + timeMillis;
        }
    }

    protected void setupEmitter(EffectFactory.EFFECT_TYPE type, int emitterArc, int emission) {
        setupEmitter(type, emitterArc, emission, new float[]{});
    }

    protected void setupEmitter(EffectFactory.EFFECT_TYPE type, int emitterArc, int emission, float[] colors) {
        this.emission = emission;
        this.emitterArc = emitterArc;
        this.trailType = type;

        if (trailEmitter != null)
            EffectFactory.getInstance().stopEmitter(trailEmitter);

        trailEmitter = EffectFactory.getInstance().addEffect(position, type);
        trailEmitter.setContinuous(true);
        if (colors.length > 0)
            trailEmitter.getTint().setColors(colors);

        trailEmitter.getScale().setLow(Config.TILE_SIZE_X / 8, Config.TILE_SIZE_X / 8);
        trailEmitter.getScale().setHigh(Config.TILE_SIZE_X / 4, Config.TILE_SIZE_X / 4);
        trailEmitter.getVelocity().setLow(Config.TILE_SIZE_X / 8, Config.TILE_SIZE_X / 8);
        trailEmitter.getVelocity().setHigh(Config.TILE_SIZE_X / 4, Config.TILE_SIZE_X / 4);

    }

    protected void updateEmission() {
        trailEmitter.setPosition(position.x, position.y);
        trailEmitter.getAngle().setHigh(angle - emitterArc, angle + emitterArc);
        trailEmitter.getAngle().setLow(angle - emitterArc, angle + emitterArc);
        trailEmitter.getVelocity().setHigh(unconscious ? 0 : speed / 2, unconscious ? 0 : speed);
        trailEmitter.getVelocity().setLow(0, unconscious ? 0 : speed / 4);
        trailEmitter.getEmission().setLow(0, 0);
        trailEmitter.getEmission().setHigh(emission, emission);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (trailEmitter != null)
            EffectFactory.getInstance().stopEmitter(trailEmitter);
    }

    public void addPlatform() {
        trackingPlatforms = true;
        numPlatforms++;
    }

    public void removePlatform() {
        numPlatforms--;
    }

    public int getNumPlatforms() {
        if (trackingPlatforms)
            return numPlatforms;
        else return 1;
    }

    public void fell(Vector2 position) {
        fell();
        body.setTransform(position, angle);
    }

    public void fell() {
        if (falling) return;
        health = 0;
        if (this instanceof PlayerBody)
            SoundFactory.getInstance().playSound(SoundFactory.SOUND_TYPE.SCREAM);
        body.setLinearDamping(5);
        scaleMod = -.02f;
        falling = true;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }
}
