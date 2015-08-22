package no.dkit.android.ludum.core.game.model.loot;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;

public abstract class Weapon extends Loot {
    int level = 0;
    int maxLevel = 3;
    int priceFactor = 10;

    protected Integer emitters;
    protected Integer sideAngle;
    protected Integer strength;
    protected Integer speed;
    protected Integer rate;

    boolean randomFire = true;

    GameBody owner;

    protected long lastFire1 = 0;
    protected long lastFire2 = 0;

    public int ammo;
    public long cooldown1;
    public long cooldown2;

    protected boolean playerOwner = false;

    float firingAngle;
    protected Vector2 firingDirection = new Vector2();
    protected Vector2 origin = new Vector2();

    protected Vector2 posCopy = new Vector2();
    protected Vector2 dirCopy = new Vector2();
    protected Vector2 tmp = new Vector2();

    public Weapon(LOOT_TYPE type, String imageName) {
        super(type, imageName);
        this.cooldown1 = 200;
        this.cooldown2 = 1000;
        ammo = MathUtils.random(50, 99);

        emitters = 1;
        sideAngle = 0;
        strength = 1;
        speed = 1;
        rate = 1;
    }

    final public void fire1(Vector2 position, Vector2 target) {
        if (ammo < 2 && playerOwner)
            onGunEmpty();
        else {
            if (isReady1()) {
                prepareFire1(position, target);
                SoundFactory.getInstance().playWeaponLaunchSound(type);
                fire1();
            }
        }
    }

    final public void fire2(Vector2 position, Vector2 target) {
        if (ammo < 2 && playerOwner)
            onGunEmpty();
        else {
            if (isReady2()) {
                prepareFire2(position, target);
                SoundFactory.getInstance().playWeaponLaunchSound(type);
                fire2();
            }
        }
    }

    public void prepareFire1(Vector2 position, Vector2 target) {
        ammo--;
        lastFire1 = System.currentTimeMillis();
        origin.set(position);

        prepareAngles(position, target);

        if (playerOwner)
            XXXX.updateWeaponButton(this);
    }

    public void prepareFire2(Vector2 position, Vector2 target) {
        ammo -= 2;
        lastFire2 = System.currentTimeMillis();
        origin.set(position);

        prepareAngles(position, target);

        if (playerOwner)
            XXXX.updateWeaponButton(this);
    }

    private void prepareAngles(Vector2 position, Vector2 target) {
        firingDirection.set(target.x - position.x, target.y - position.y);
        firingAngle = (MathUtils.radiansToDegrees * MathUtils.atan2(firingDirection.y, firingDirection.x)) - 90f;
    }

    public boolean isReady1() {
        //if (!playerOwner) return true;
        return System.currentTimeMillis() - lastFire1 > cooldown1 && ammo > 0;
    }

    private void onGunEmpty() {
    }

    public boolean isReady2() {
        //if (!playerOwner) return true;
        return System.currentTimeMillis() - lastFire2 > cooldown2 && ammo > 1;
    }

    @Override
    public void onPickup(GameBody owner) {
        if (owner instanceof AgentBody)
            ((AgentBody) owner).addWeapon(this);

        this.owner = owner;
        playerOwner = this.owner instanceof PlayerBody;

        if (playerOwner)
            XXXX.updateGameScreen();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass());
    }

    public void update(GameBody owner) {
    }

    public void fire(Vector2 source, Vector2 target) {
        if (MathUtils.random() > .25f)
            fire1(source, target);
        else
            fire2(source, target);
    }

    // To override
    public boolean specialFire(GameBody source, GameBody target) {
        return false;
    }

    public boolean canUpgrade() {
        return level < maxLevel;
    }

    public void upgrade() {
        if (level < maxLevel)
            level++;
    }

    public int getUpgradePrice() {
        return (level + 1) * priceFactor;
    }

    public int getLevel() {
        return level;
    }

    public void fire1() {
        BodyFactory.getInstance().createBullet(type, origin, firingDirection, ResourceFactory.getInstance().getBulletImage(imageName), strength, playerOwner);

        for (int i = 1; i < emitters; i++) {
            BodyFactory.getInstance().createBullet(type, origin, dirCopy.set(firingDirection).rotate(sideAngle * i), ResourceFactory.getInstance().getBulletImage(imageName), strength, playerOwner);
            BodyFactory.getInstance().createBullet(type, origin, dirCopy.set(firingDirection).rotate(-sideAngle * i), ResourceFactory.getInstance().getBulletImage(imageName), strength, playerOwner);
        }
    }

    public void fire2() {
        fire1();
    }
}
