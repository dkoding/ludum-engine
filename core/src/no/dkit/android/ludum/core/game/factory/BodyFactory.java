package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Flee;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Seek;
import no.dkit.android.ludum.core.game.ai.behaviors.single.WalkerFlee;
import no.dkit.android.ludum.core.game.ai.behaviors.single.WalkerSeek;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.PlayerData;
import no.dkit.android.ludum.core.game.model.body.DiscardBody;
import no.dkit.android.ludum.core.game.model.body.DiscardJoint;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.LightBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.body.agent.AnimatedAgentBody;
import no.dkit.android.ludum.core.game.model.body.agent.AnimatedPlayerBody;
import no.dkit.android.ludum.core.game.model.body.agent.Blob;
import no.dkit.android.ludum.core.game.model.body.agent.BlobPlayerBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerVehicleBody;
import no.dkit.android.ludum.core.game.model.body.item.CannonBody;
import no.dkit.android.ludum.core.game.model.body.item.CrateBody;
import no.dkit.android.ludum.core.game.model.body.item.DangerBody;
import no.dkit.android.ludum.core.game.model.body.item.DoorBody;
import no.dkit.android.ludum.core.game.model.body.item.KeyBody;
import no.dkit.android.ludum.core.game.model.body.item.LootBody;
import no.dkit.android.ludum.core.game.model.body.item.MineBody;
import no.dkit.android.ludum.core.game.model.body.item.RotatingGunBody;
import no.dkit.android.ludum.core.game.model.body.item.TriggerBody;
import no.dkit.android.ludum.core.game.model.body.scenery.BlockBody;
import no.dkit.android.ludum.core.game.model.body.scenery.BorderBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ChasmBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ExitBody;
import no.dkit.android.ludum.core.game.model.body.scenery.FeatureBody;
import no.dkit.android.ludum.core.game.model.body.scenery.FloorBody;
import no.dkit.android.ludum.core.game.model.body.scenery.LampBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ObscuringFeatureBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ObscuringShadedBody;
import no.dkit.android.ludum.core.game.model.body.scenery.PlatformBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ShadedBody;
import no.dkit.android.ludum.core.game.model.body.scenery.SpawnBody;
import no.dkit.android.ludum.core.game.model.body.scenery.UpgradeBody;
import no.dkit.android.ludum.core.game.model.body.weapon.BombBody;
import no.dkit.android.ludum.core.game.model.body.weapon.BulletBody;
import no.dkit.android.ludum.core.game.model.body.weapon.TongueBody;
import no.dkit.android.ludum.core.game.model.body.weapon.EnergyBallBody;
import no.dkit.android.ludum.core.game.model.body.weapon.ParticleBody;
import no.dkit.android.ludum.core.game.model.body.weapon.RocketBody;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.shaders.RenderOperations;

import java.util.Random;

public class BodyFactory {
    public static final short LIGHT_BITS = (short) (Config.CATEGORY_SCENERY);
    public static final short PLAYER_BITS = (short) (Config.CATEGORY_ENEMY | Config.CATEGORY_ENEMY_BULLET | Config.CATEGORY_SCENERY | Config.CATEGORY_ITEM | Config.CATEGORY_TRIGGER | Config.CATEGORY_PARTICLE | Config.CATEGORY_PLATFORM | Config.CATEGORY_LOOT);
    public static final short PLAYER_BULLET_BITS = (short) (Config.CATEGORY_ENEMY | Config.CATEGORY_SCENERY | Config.CATEGORY_ITEM);
    public static final short ENEMY_BITS = (short) (Config.CATEGORY_SCENERY | Config.CATEGORY_ITEM | Config.CATEGORY_BULLET | Config.CATEGORY_PLAYER | Config.CATEGORY_PARTICLE | Config.CATEGORY_PLATFORM | Config.CATEGORY_LOOT);
    public static final short ENEMY_BULLET_BITS = (short) (Config.CATEGORY_SCENERY | Config.CATEGORY_ITEM | Config.CATEGORY_PLAYER);
    public static final short ITEM_BITS = (short) (Config.CATEGORY_SCENERY | Config.CATEGORY_ITEM | Config.CATEGORY_PLAYER | Config.CATEGORY_ENEMY | Config.CATEGORY_ENEMY_BULLET | Config.CATEGORY_BULLET | Config.CATEGORY_PARTICLE | Config.CATEGORY_LOOT);
    public static final short LOOT_BITS = (short) (Config.CATEGORY_SCENERY | Config.CATEGORY_ITEM | Config.CATEGORY_PLAYER | Config.CATEGORY_ENEMY);
    public static final short SCENERY_BITS = (short) (Config.CATEGORY_ITEM | Config.CATEGORY_PLAYER | Config.CATEGORY_ENEMY | Config.CATEGORY_ENEMY_BULLET | Config.CATEGORY_BULLET | Config.CATEGORY_LIGHT | Config.CATEGORY_PARTICLE | Config.CATEGORY_LOOT);
    public static final short PLATFORM_BITS = (short) (Config.CATEGORY_PLAYER | Config.CATEGORY_ENEMY);
    public static final short PARTICLE_BITS = (short) (Config.CATEGORY_ITEM | Config.CATEGORY_PLAYER | Config.CATEGORY_ENEMY | Config.CATEGORY_SCENERY);
    public static final short TRIGGER_BITS = (short) (Config.CATEGORY_PLAYER | Config.CATEGORY_SCENERY);
    public static final short SELF_COLLIDING_ENEMY_BITS = (short) (ENEMY_BITS | Config.CATEGORY_ENEMY);

    private int explosionNumber = 0;
    private int numJoints = 0;
    private TongueBody tongue;
    private TongueBody dongue;

    public enum ENEMY_IMAGE {
        SHIP_1, SHIP_2, WALKER_1, WALKER_2
    }

    public enum ENEMY_ANIM {
        WALKER_ANIM_1, FLYER_ANIM_1
    }

    public enum ENEMY_TYPE {
        SHIP_SINGLE, SHIP_GROUP, WALKER_SINGLE, WALKER_GROUP, FLYER_SINGLE, FLYER_GROUP, BLOB
    }

    public enum FEATURE_TYPE {
        FLOOR_FEATURE_1, OUTDOOR_FEATURE_1, TOPDOWN_OBSCURING_FEATURE_1, SIDESCROLL_OBSCURING_FEATURE_1, PLANET_FEATURE_1, NEBULA_FEATURE_1, PLATFORM_1, MOVING_PLATFORM_1, SLIMEPOOL
    }

    Vector2 rayDir = new Vector2();
    Vector2 dirCpy = new Vector2();

    protected static BodyFactory bodyFactory;
    public static Filter lightFilter = new Filter();

    Array<ParticleDef> particlesToCreate = new Array<ParticleDef>();
    Array<BulletDef> bulletsToCreate = new Array<BulletDef>();
    Array<AgentDef> agentsToCreate = new Array<AgentDef>();
    Array<LootDef> lootToCreate = new Array<LootDef>();
    Array<LightSourceDef> lightSourcesToCreate = new Array<LightSourceDef>();

    static World world;

    Array<Body> worldBodies = new Array<Body>();
    Array<Joint> worldJoints = new Array<Joint>();

/*
    private final Pool<Body> agentPool;
    private final Pool<Body> lootPool;
    private final Pool<Body> bulletPool;
    private final Pool<Body> particlePool;
    private final Pool<Body> lightPool;
    private final Pool<Vector2> vectorPool;
*/

    private FixtureDef triggerFixture = new FixtureDef();
    private FixtureDef crateFixture = new FixtureDef();
    private FixtureDef mineFixture = new FixtureDef();
    private FixtureDef particleFixture = new FixtureDef();
    private FixtureDef tileFixture = new FixtureDef();
    private FixtureDef doorFixture = new FixtureDef();
    private FixtureDef lootFixture = new FixtureDef();
    private FixtureDef dangerFixture = new FixtureDef();
    private FixtureDef featureFixture = new FixtureDef();
    private FixtureDef platformFixture = new FixtureDef();
    private FixtureDef upgradeFixture = new FixtureDef();
    private FixtureDef exitFixture = new FixtureDef();
    private FixtureDef keyFixture = new FixtureDef();
    private FixtureDef warpFixture = new FixtureDef();
    private FixtureDef lampFixture = new FixtureDef();
    private FixtureDef agentFixture = new FixtureDef();
    private FixtureDef bulletFixture = new FixtureDef();
    private FixtureDef turretFixture = new FixtureDef();

    protected FixtureDef playerFixture = new FixtureDef();

    ParticleBody[] particles;

    public static BodyFactory getInstance() {
        if (bodyFactory == null)
            throw new RuntimeException("NOT CREATED YET!");

        return bodyFactory;
    }

    public static void create(World world) {
        if (bodyFactory == null)
            bodyFactory = new BodyFactory(world);
        else if (BodyFactory.world != world) {
            bodyFactory = new BodyFactory(world);
        }
    }

    protected BodyFactory(final World world) {
        BodyFactory.world = world;

        MathUtils.random = new Random(Config.RANDOM_SEED + LevelFactory.level);

        createTriggerAttributes(createCircleShape(Config.TILE_SIZE_X / 2));
        createCrateAttributes(createCenteredBoxShape(Config.TILE_SIZE_X / 2, Config.TILE_SIZE_Y / 2));
        createMineAttributes(createCircleShape(Config.TILE_SIZE_X / 5));
        createParticleAttributes(createCircleShape(Config.TILE_SIZE_X / 40));
        createTileAttributes(createCenteredBoxShape(Config.TILE_SIZE_X, Config.TILE_SIZE_Y));
        createDoorAttributes(createDoorShape());
        createPlatformAttributes(createCenteredBoxShape(Config.TILE_SIZE_X, Config.TILE_SIZE_Y));
        createItemAttributes(createCircleShape(Config.TILE_SIZE_X / 2));
        createKeyAttributes(createCircleShape(Config.TILE_SIZE_X / 2));
        createDangerAttributes(createCircleShape(Config.TILE_SIZE_X / 2));
        createPlanetAttributes(createCircleShape(Config.TILE_SIZE_X / 2));
        createUpgradeAttributes(createCircleShape(Config.TILE_SIZE_X));
        createExitAttributes(createCircleShape(Config.TILE_SIZE_X));
        createWarpAttributes(createCircleShape(Config.TILE_SIZE_X));
        createLampAttributes(createCircleShape(Config.TILE_SIZE_X / 4));
        createTurretAttributes(createTurretShape());
        createAgentAttributes(createCircleShape(Config.TILE_SIZE_X / 3), 3);

        setupCategories();
/*
        agentPool = new Pool<Body>() {
            @Override
            protected Body newObject() {
                Body body = createBody(agentBodyDef);
                body.createFixture(agentFixture);
                body.setActive(false);
                return body;
            }
        };

        bulletPool = new Pool<Body>() {
            @Override
            protected Body newObject() {
                Body body = createBody(bulletDef);
                body.createFixture(bulletFixture);
                body.setActive(false);
                return body;
            }
        };

        particlePool = new Pool<Body>() {
            @Override
            protected Body newObject() {
                Body body = createBody(particleDef);
                body.createFixture(particleFixture);
                body.setActive(false);
                return body;
            }
        };

        lootPool = new Pool<Body>() {
            @Override
            protected Body newObject() {
                Body body = createBody(itemBodyDef);
                body.createFixture(lootFixture);
                body.setActive(false);
                return body;
            }
        };

        lightPool = new Pool<Body>() {
            @Override
            protected Body newObject() {
                Body body = createBody(lightDef);
                body.setActive(false);
                return body;
            }
        };

        vectorPool = new Pool<Vector2>() {
            @Override
            protected Vector2 newObject() {
                return new Vector2(0, 0);
            }
        };
*/

        createParticleBlueprints();
    }

    private void createParticleBlueprints() {
        int numParticles = Config.NUM_PARTICLES * Config.MAX_EXPLOSIONS;
        particles = new ParticleBody[numParticles];

        for (int i = 0; i < numParticles; i++) {
            Body particle = getDefaultParticleBody(new Vector2(0, 0));
            particles[i] = new ParticleBody(particle, particleFixture.shape.getRadius(),
                    ResourceFactory.getInstance().getItemImage("particle"));
            particles[i].setActive(false);
        }
    }

    public FixtureDef getAgentAttributes(Shape shape, float density) {
        createAgentAttributes(shape, density);
        return agentFixture;
    }

    private void createAgentAttributes(Shape shape, float density) {
        agentFixture.shape = shape;
        agentFixture.density = density;
        agentFixture.friction = 0.5f;
        agentFixture.restitution = 0.5f;
        agentFixture.isSensor = false;
    }

    private void setupCategories() {
        lightFilter.categoryBits = Config.CATEGORY_LIGHT;
        lightFilter.maskBits = LIGHT_BITS;

        playerFixture.filter.categoryBits = Config.CATEGORY_PLAYER;
        playerFixture.filter.maskBits = PLAYER_BITS;

        bulletFixture.filter.categoryBits = Config.CATEGORY_BULLET;
        bulletFixture.filter.maskBits = PLAYER_BULLET_BITS;

        particleFixture.filter.categoryBits = Config.CATEGORY_PARTICLE;
        particleFixture.filter.maskBits = PARTICLE_BITS;

        agentFixture.filter.categoryBits = Config.CATEGORY_ENEMY;
        agentFixture.filter.maskBits = ENEMY_BITS;

        turretFixture.filter.categoryBits = Config.CATEGORY_ENEMY;
        turretFixture.filter.maskBits = ENEMY_BITS;

        crateFixture.filter.categoryBits = Config.CATEGORY_ITEM;
        crateFixture.filter.maskBits = ITEM_BITS;

        doorFixture.filter.categoryBits = Config.CATEGORY_SCENERY;
        doorFixture.filter.maskBits = SCENERY_BITS;

        lootFixture.filter.categoryBits = Config.CATEGORY_LOOT;
        lootFixture.filter.maskBits = LOOT_BITS;

        keyFixture.filter.categoryBits = Config.CATEGORY_TRIGGER;
        keyFixture.filter.maskBits = TRIGGER_BITS;

        dangerFixture.filter.categoryBits = Config.CATEGORY_ITEM;
        dangerFixture.filter.maskBits = ITEM_BITS;

        mineFixture.filter.categoryBits = Config.CATEGORY_ITEM;
        mineFixture.filter.maskBits = ITEM_BITS;

        tileFixture.filter.categoryBits = Config.CATEGORY_SCENERY;
        tileFixture.filter.maskBits = SCENERY_BITS;

        platformFixture.filter.categoryBits = Config.CATEGORY_PLATFORM;
        platformFixture.filter.maskBits = PLATFORM_BITS;

        triggerFixture.filter.categoryBits = Config.CATEGORY_TRIGGER;
        triggerFixture.filter.maskBits = TRIGGER_BITS;

        upgradeFixture.filter.categoryBits = Config.CATEGORY_TRIGGER;
        upgradeFixture.filter.maskBits = TRIGGER_BITS;

        exitFixture.filter.categoryBits = Config.CATEGORY_TRIGGER;
        exitFixture.filter.maskBits = TRIGGER_BITS;

        lampFixture.filter.categoryBits = Config.CATEGORY_ITEM;
        lampFixture.filter.maskBits = ITEM_BITS;
    }

    private FixtureDef createTileAttributes(Shape shape) {
        tileFixture.shape = shape;
        tileFixture.density = 1000f;
        tileFixture.friction = 0f;
        tileFixture.restitution = 1f;
        tileFixture.isSensor = false;

        return tileFixture;
    }

    private FixtureDef createPlatformAttributes(Shape shape) {
        platformFixture.shape = shape;
        platformFixture.isSensor = true;
        return platformFixture;
    }

    private FixtureDef createDoorAttributes(Shape shape) {
        doorFixture.shape = shape;
        doorFixture.density = 1000f;
        doorFixture.friction = 0f;
        doorFixture.restitution = 1f;
        doorFixture.isSensor = false;

        return doorFixture;
    }

    private FixtureDef createItemAttributes(Shape shape) {
        lootFixture.shape = shape;
        lootFixture.density = 5f;
        lootFixture.friction = .01f;
        lootFixture.restitution = .5f;
        lootFixture.isSensor = true;

        return lootFixture;
    }

    private FixtureDef createParticleAttributes(Shape shape) {
        particleFixture.shape = shape;
        particleFixture.density = 5000f;
        particleFixture.friction = 0f;
        particleFixture.restitution = .99f;
        particleFixture.isSensor = false;
        particleFixture.filter.groupIndex = -1; // particles should not collide with each other

        return particleFixture;
    }

    private FixtureDef createMineAttributes(Shape shape) {
        mineFixture.shape = shape;
        mineFixture.density = 5000f;
        mineFixture.friction = 0f;
        mineFixture.restitution = .99f;
        mineFixture.isSensor = true;

        return mineFixture;
    }

    private FixtureDef createCrateAttributes(Shape shape) {
        crateFixture.shape = shape;
        crateFixture.density = 50f;
        crateFixture.friction = 1f;
        crateFixture.restitution = 1f;
        crateFixture.isSensor = false;

        return crateFixture;
    }

    private FixtureDef createTriggerAttributes(Shape shape) {
        triggerFixture.shape = shape;
        triggerFixture.density = 5000f;
        triggerFixture.friction = 1f;
        triggerFixture.restitution = 0f;
        triggerFixture.isSensor = true;

        return triggerFixture;
    }

    private FixtureDef createKeyAttributes(Shape shape) {
        keyFixture.shape = shape;
        keyFixture.density = 5f;
        keyFixture.friction = .01f;
        keyFixture.restitution = .5f;
        keyFixture.isSensor = false;

        return keyFixture;
    }

    private FixtureDef createDangerAttributes(Shape shape) {
        dangerFixture.shape = shape;
        dangerFixture.density = 5;
        dangerFixture.friction = .5f;
        dangerFixture.restitution = .5f;
        dangerFixture.isSensor = false;

        return dangerFixture;
    }

    private FixtureDef createPlanetAttributes(Shape shape) {
        featureFixture.shape = shape;
        featureFixture.density = 5.0f;
        featureFixture.friction = .95f;
        featureFixture.restitution = 0f;
        featureFixture.isSensor = true;

        return featureFixture;
    }

    private FixtureDef createUpgradeAttributes(Shape shape) {
        upgradeFixture.shape = shape;
        upgradeFixture.density = 5.0f;
        upgradeFixture.friction = .95f;
        upgradeFixture.restitution = 0f;
        upgradeFixture.isSensor = true;

        return upgradeFixture;
    }

    private FixtureDef createExitAttributes(Shape shape) {
        exitFixture.shape = shape;
        exitFixture.density = 5.0f;
        exitFixture.friction = .95f;
        exitFixture.restitution = 0f;
        exitFixture.isSensor = true;

        return exitFixture;
    }

    private FixtureDef createWarpAttributes(Shape shape) {
        warpFixture.density = 5.0f;
        warpFixture.shape = shape;
        warpFixture.friction = .95f;
        warpFixture.restitution = 0f;
        warpFixture.isSensor = true;

        return warpFixture;
    }

    private FixtureDef createLampAttributes(Shape shape) {
        lampFixture.density = 5.0f;
        lampFixture.shape = shape;
        lampFixture.friction = .95f;
        lampFixture.restitution = 0f;
        lampFixture.isSensor = false;

        return lampFixture;
    }

    private Shape createCenteredBoxShape(float width, float height) {
        Shape boxShape = new PolygonShape();
        ((PolygonShape) boxShape).setAsBox(width, height, new Vector2(0, 0), 0);
        return boxShape;
    }

    private Shape createNonCenteredBoxShape(float width, float height, float offsetX, float offsetY) {
        Shape boxShape = new PolygonShape();
        ((PolygonShape) boxShape).setAsBox(width, height, new Vector2(offsetX, offsetY), 0);
        return boxShape;
    }

    private Shape createDoorShape() {
        Shape doorShape = new PolygonShape();
        ((PolygonShape) doorShape).setAsBox(Config.TILE_SIZE_X / 4, Config.TILE_SIZE_Y, new Vector2(Config.TILE_SIZE_X - Config.TILE_SIZE_X / 4, 0), 0);
        return doorShape;
    }

    private Shape createTileDirShape() {
        Shape tileDirShape = new PolygonShape();
        ((PolygonShape) tileDirShape).set(new float[]{
                -Config.TILE_SIZE_X, -Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X, -Config.TILE_SIZE_Y,
                -Config.TILE_SIZE_X, Config.TILE_SIZE_Y
        });
        return tileDirShape;
    }

    private FixtureDef createBulletAttributes(FixtureDef fixture, Shape circleShape) {
        fixture.shape = circleShape;
        fixture.density = 1f;
        fixture.friction = 0f;
        fixture.restitution = .5f;
        fixture.isSensor = false;

        return fixture;
    }

    private FixtureDef createBombAttributes(FixtureDef fixture, Shape circleShape) {
        fixture.shape = circleShape;
        fixture.density = 10f;
        fixture.friction = 1f;
        fixture.restitution = 0f;

        return fixture;
    }

    private FixtureDef createRocketAttributes(FixtureDef fixture, Shape polygonShape) {
        fixture.shape = polygonShape;
        fixture.density = 2500f;
        fixture.friction = 1f;
        fixture.restitution = 0f;

        return fixture;
    }

    private FixtureDef createTurretAttributes(Shape shape) {
        turretFixture.shape = shape;
        turretFixture.density = 10f;
        turretFixture.friction = 0.5f;
        turretFixture.restitution = .9f;
        turretFixture.isSensor = false;

        return turretFixture;
    }

    private FixtureDef createPlasmaAttributes(FixtureDef fixture, Shape polygonShape) {
        fixture.shape = polygonShape;
        fixture.density = 1f;
        fixture.friction = 1f;
        fixture.restitution = 1f;

        return fixture;
    }

    private FixtureDef createTongueBulletAttributes(FixtureDef fixture, Shape shape) {
        fixture.shape = shape;
        fixture.density = 1f;
        fixture.friction = .2f;
        fixture.restitution = .2f;

        return fixture;
    }

    private FixtureDef createDongueBulletAttributes(FixtureDef fixture, Shape shape) {
        fixture.shape = shape;
        fixture.density = 0f;
        fixture.friction = 0f;
        fixture.restitution = 1f;

        return fixture;
    }

    private FixtureDef createSpearAttributes(FixtureDef fixture, Shape shape) {
        fixture.shape = shape;
        fixture.density = 5000f;
        fixture.friction = 1f;
        fixture.restitution = 1f;

        return fixture;
    }

    private Shape createCircleShape(float radius) {
        Shape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        return circleShape;
    }

    private Shape createBoxShape(float width, float height) {
        Shape boxShape = new PolygonShape();
        ((PolygonShape) boxShape).setAsBox(width, height);
        return boxShape;
    }

    private Shape createTurretShape() {
        Shape turretShape = new CircleShape();
        turretShape.setRadius(Config.TILE_SIZE_X / 2);
        return turretShape;
    }

    private BodyDef createStaticBodyDef() {
        return createBodyDef(false, BodyDef.BodyType.StaticBody, 0, 0, 0, 0, true);
    }

    private BodyDef createKinematicBodyDef() {
        return createBodyDef(false, BodyDef.BodyType.KinematicBody, 0, 1, .9f, 0, true);
    }

    private BodyDef createStaticBodyDef(float angle) {
        return createBodyDef(false, BodyDef.BodyType.StaticBody, angle, 0, 0, 0, true);
    }

    private BodyDef createBulletDef() {
        return createBodyDef(true, BodyDef.BodyType.DynamicBody, 0, 0, 0, 0, true);
    }

    private BodyDef createCrateBodyDef() {
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, 1, 1, 1, false);
    }

    private BodyDef createRockBodyDef() {
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, 1, 0, 0, false);
    }

    private BodyDef createKeyBodyDef() {
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, 0, 1, 1, false);
    }

    private BodyDef createGravityBulletDef(float gravityScale) {
        return createBodyDef(true, BodyDef.BodyType.DynamicBody, 0, gravityScale, 0, 0, false);
    }

    private BodyDef createTongueBulletDef() {
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, .1f, 1f, 1f, false);
    }

    private BodyDef createDongueBulletDef() {
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, 0f, 0f, 0f, false);
    }

    private BodyDef createSlowingBulletDef() {
        return createBodyDef(true, BodyDef.BodyType.DynamicBody, 0, 1, .75f, .75f, false);
    }

    private BodyDef createAgentBodyDef(boolean fixedRotation) {
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, .25f, 0, 0, fixedRotation);
    }

    private BodyDef createAgentBodyDef(boolean fixedRotation, float gravityScale) {
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, gravityScale, 0, 0, fixedRotation);
    }

    private BodyDef createBodyDef(boolean bullet, BodyDef.BodyType bodyType, float angle, float gravityScale, float linearDamping, float angularDamping, boolean fixedRotation) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.bullet = bullet;
        bodyDef.type = bodyType;
        bodyDef.angle = angle;
        bodyDef.gravityScale = gravityScale;
        bodyDef.linearDamping = linearDamping;
        bodyDef.fixedRotation = fixedRotation;
        bodyDef.angularDamping = angularDamping;

        return bodyDef;
    }

    public void createLightSource(Vector2 position, LightFactory.LIGHT_TYPE type, float lightMod, int numRepetitions) {
        lightSourcesToCreate.add(new LightSourceDef(position, type, lightMod, numRepetitions));
    }

    private Body getBulletBody(Loot.LOOT_TYPE type, float sourceX, float sourceY, float velocityX, float velocityY, boolean playerOwned, float radius) {
        bulletFixture.filter.categoryBits = playerOwned ? Config.CATEGORY_BULLET : Config.CATEGORY_ENEMY_BULLET;
        bulletFixture.filter.maskBits = playerOwned ? PLAYER_BULLET_BITS : ENEMY_BULLET_BITS;

        BodyDef bulletDef;

        switch (type) {
            case BOMB:
                createBombAttributes(bulletFixture, createCircleShape(Config.TILE_SIZE_X / 6f));
                bulletDef = createGravityBulletDef(.5f);
                break;
            case ROCKET:
                createRocketAttributes(bulletFixture, createBoxShape(Config.TILE_SIZE_X / 4f, Config.TILE_SIZE_X / 16f));
                bulletDef = createGravityBulletDef(.1f);
                break;
            case TONGUE:
                createTongueBulletAttributes(bulletFixture, createCircleShape(radius));
                bulletDef = createTongueBulletDef();
                break;
            case DONGUE:
                createDongueBulletAttributes(bulletFixture, createCircleShape(radius));
                bulletDef = createDongueBulletDef();
                break;
            default:
                createBulletAttributes(bulletFixture, createCircleShape(Config.TILE_SIZE_X / 10f));
                bulletDef = createBulletDef();
        }

        Body body = createBody(bulletDef, bulletFixture);
        body.setBullet(true);
        body.setTransform(sourceX, sourceY, 0);
        body.setLinearVelocity(velocityX, velocityY);
        return body;
    }

    private Body getDefaultLootBody(Vector2 position) {
        Body body = createBody(createKinematicBodyDef(), getItemFixtureDef(AbstractMap.ITEM_LOOT));
        body.setBullet(false);
        body.setTransform(position, 0);
        return body;
    }

    private Body getDefaultLightBody(Vector2 position) {
        Body body = createBody(createStaticBodyDef());
        body.setBullet(false);
        body.setTransform(position, 0);
        return body;
    }

    private Body getDefaultParticleBody(Vector2 position) {
        Body body = createBody(createBulletDef(), particleFixture);
        body.setBullet(true);
        body.setTransform(position, 0);
        body.setGravityScale(0);
        body.setLinearDamping(5);
        return body;
    }

    private Body getAgentBody(Vector2 position, boolean selfColliding, boolean rotating, Shape shape, float density, float gravityScale) {
        Body body = createBody(createAgentBodyDef(!rotating, gravityScale), getAgentAttributes(shape, density));
        body.setTransform(position, 0);
        body.setFixedRotation(!rotating);

        Filter filter = new Filter();
        filter.categoryBits = Config.CATEGORY_ENEMY;

        if (selfColliding)
            filter.maskBits = SELF_COLLIDING_ENEMY_BITS;
        else
            filter.maskBits = ENEMY_BITS;

        body.getFixtureList().get(0).setFilterData(filter);

        return body;
    }

    private Body getAgentBody(Vector2 position, boolean selfColliding, boolean rotating, Shape shape, float density) {
        return getAgentBody(position, selfColliding, rotating, shape, density, 0);
    }

    public void createBullet(Loot.LOOT_TYPE type, Vector2 source, Vector2 velocity, TextureRegion bulletImage, int damage, boolean playerOwned) {
        bulletsToCreate.add(new BulletDef(type, source.x, source.y, velocity.x, velocity.y, bulletImage, damage, playerOwned));
    }

    public void createAgent(ENEMY_TYPE type, Vector2 position) {
        agentsToCreate.add(new AgentDef(type, position));
    }

    public GameBody createMapTile(int x, int y, int type, int direction, TextureAtlas.AtlasRegion indoorImage,
                                  TextureAtlas.AtlasRegion doorImage, TextureAtlas.AtlasRegion corridorImage, Texture wallTexture) {
        if (type == AbstractMap.ROOM) { // Delegate to getFeature-method
            return getFloorFeature(x, y, indoorImage);
        } else if (type == AbstractMap.CHASM) { // Delegate to getFeature-method
            return getRandomChasmBody(x, y, indoorImage);
        } else if (type == AbstractMap.CORRIDOR) {
            return getCorridorFeature(x, y, corridorImage);
        } else if (type == AbstractMap.PLATFORM) {
            return getPlatformBody(x, y, direction);
        }

        FixtureDef fixtureDef = getTileFixtureDef(type, direction);
        BodyDef tileBodyDef = createStaticBodyDef();
        tileBodyDef.position.x = x;
        tileBodyDef.position.y = y;
        Body body = createBody(tileBodyDef);
        body.createFixture(fixtureDef);
        body.setLinearVelocity(0, 0);

        if (type == AbstractMap.DOOR)
            return new DoorBody(body, Config.TILE_SIZE_X, direction, indoorImage, doorImage);
        else if (type == AbstractMap.BORDER)
            return new BorderBody(body, Config.TILE_SIZE_X, ResourceFactory.getInstance().getWorldTypeImage("border"));
        else
            return new BlockBody(body, x, y, type, fixtureDef.shape.getRadius(), direction, wallTexture, GameBody.BODY_TYPE.STONE);
    }

    public GameBody createItem(float x, float y, int type) {
        return createItem(x, y, type, AbstractMap.NO_DIRECTION);
    }

    public void createLoot(Vector2 position, Loot.LOOT_TYPE type) {
        lootToCreate.add(new LootDef(type, position));
    }

    public TriggerBody createTrigger(float x, float y) {
        return (TriggerBody) createItem(x, y, AbstractMap.ITEM_TRIGGER, AbstractMap.NO_DIRECTION);
    }

    public FeatureBody createFeature(float x, float y, FEATURE_TYPE featureType) {
        return getFeature(x, y, featureType);
    }

    public GameBody createItem(float x, float y, int type, int direction) {
        BodyDef def;

        switch (type) {
            case AbstractMap.ITEM_CRATE:
                def = createCrateBodyDef();
                break;
            case AbstractMap.ITEM_ROCK:
                def = createRockBodyDef();
                break;
            case AbstractMap.ITEM_KEY:
                def = createKeyBodyDef();
                break;
            default:
                def = createStaticBodyDef();
        }

        def.position.x = x;
        def.position.y = y;

        Body body = createBody(def, getItemFixtureDef(type));

        return getItemBody(type, direction, body, getItemFixtureDef(type));
    }

    private GameBody getItemBody(int type, int direction, Body body, FixtureDef fixtureDef) {
        final float shaderRadius = fixtureDef.shape.getRadius() * Config.getDimensions().SCREEN_ON_WORLD_FACTOR;

/*        if (type == AbstractMap.ITEM_REWARD)
            return new RewardBody(body, fixtureDef.shape.getRadius(), ResourceFactory.getInstance().getItemImage("orb"));
        else */
        if (type == AbstractMap.ITEM_LOOT)
            return getLoot(body, fixtureDef);
        else if (type == AbstractMap.ITEM_ROCK)
            return new DangerBody(body, fixtureDef.shape.getRadius(), ResourceFactory.getInstance().getItemImage("crystal"));
        else if (type == AbstractMap.ITEM_KEY)
            return new KeyBody(body, fixtureDef.shape.getRadius(), ResourceFactory.getInstance().getItemImage("key"));
        else if (type == AbstractMap.ITEM_TRIGGER)
            return new TriggerBody(body, fixtureDef.shape.getRadius(), ResourceFactory.getInstance().getWorldTypeImage("spawn"));
        else if (type == AbstractMap.ITEM_UPGRADE)
            return createUpgradeBody(fixtureDef, body);
        else if (type == AbstractMap.ITEM_SPAWN)
            return new SpawnBody(body, fixtureDef.shape.getRadius(), ResourceFactory.getInstance().getWorldTypeImage("spawn"));
        else if (type == AbstractMap.ITEM_LAMP)
            return new LampBody(body, fixtureDef.shape.getRadius(), getRandomLampColor(), direction);
        else if (type == AbstractMap.ITEM_MINE)
            return new MineBody(body, fixtureDef.shape.getRadius(), ResourceFactory.getInstance().getItemImage("mine"));
        else if (type == AbstractMap.ITEM_CRATE)
            return new CrateBody(body, fixtureDef.shape.getRadius());
        else if (type == AbstractMap.ITEM_CANNON)
            if (direction == AbstractMap.NO_DIRECTION)
                return getRandomRotatingGun(body);
            else
                return new CannonBody(body, Config.TILE_SIZE_X, direction);
        else if (type == AbstractMap.ITEM_LIQUID_SS)
            return new ObscuringShadedBody(body, Config.TILE_SIZE_X,
                    ShaderFactory.getInstance().getShader(RenderOperations.BACKGROUND_TYPE.WATERSINUSWAVE, shaderRadius, shaderRadius,
                            ResourceFactory.getInstance().getTransparentTexture("waterss"),
                            ResourceFactory.getInstance().getShaderComponentTexture("waterdisplacement")), -90);
        else if (type == AbstractMap.ITEM_LIQUID_TD)
            return new ShadedBody(body, Config.TILE_SIZE_X, ShaderFactory.getInstance().getShader(RenderOperations.BACKGROUND_TYPE.WAVE, shaderRadius, shaderRadius,
                    ResourceFactory.getInstance().getTransparentTexture("watertd")
            ),
                    ResourceFactory.getInstance().getImage(ResourceFactory.MASK, "watermask"));
        else if (type == AbstractMap.ITEM_ENTRANCE_SURFACE || type == AbstractMap.ITEM_ENTRANCE_UNIVERSE || type == AbstractMap.ITEM_ENTRANCE_CAVE) {
            return createAppropriateExitBody(LevelFactory.getInstance().getCurrentLevel().getWorldType(), type, fixtureDef, body);
        } else
            throw new RuntimeException("Should not happen...");
    }

    private GameBody getRandomRotatingGun(Body body) {
        int random = MathUtils.random.nextInt(4);

        switch (random) {
            case 0:
                return new RotatingGunBody(body, Config.TILE_SIZE_X / 2, ResourceFactory.getInstance().getItemImage("turret"),
                        LootFactory.getInstance().getWeapon(Loot.LOOT_TYPE.GUN), .5f, false);
            case 1:
                return new RotatingGunBody(body, Config.TILE_SIZE_X / 2, ResourceFactory.getInstance().getItemImage("turret"),
                        LootFactory.getInstance().getWeapon(Loot.LOOT_TYPE.LASER), .1f, false);
            case 2:
            default:
                return new RotatingGunBody(body, Config.TILE_SIZE_X / 2, ResourceFactory.getInstance().getItemImage("turret"),
                        LootFactory.getInstance().getWeapon(Loot.LOOT_TYPE.BOMB), 2f, false);
        }
    }

    private GameBody createUpgradeBody(FixtureDef fixtureDef, Body body) {
        return new UpgradeBody(body, fixtureDef.shape.getRadius(), ResourceFactory.getInstance().getWorldTypeImage("upgrade"));
    }

    private LootBody getLoot(Body body, FixtureDef fixtureDef) {
        Loot loot = LootFactory.getInstance().getLoot(LootFactory.getInstance().getRandomLootType());

        if (LootFactory.weapons.contains(loot.getType()))
            return new LootBody(body, fixtureDef.shape.getRadius(), loot.getType(), ResourceFactory.getInstance().getWeaponImage(loot.getImageName()),
                    ResourceFactory.getInstance().getImage(ResourceFactory.UI, "star"), Color.WHITE);
        else
            return new LootBody(body, fixtureDef.shape.getRadius(), loot.getType(), ResourceFactory.getInstance().getItemImage(loot.getImageName()),
                    ResourceFactory.getInstance().getImage(ResourceFactory.UI, "star"), Color.YELLOW);
    }

    private FixtureDef getItemFixtureDef(int type) {
        FixtureDef fixtureDef;

        if (type == AbstractMap.ITEM_LOOT) {
            fixtureDef = lootFixture;
        } else if (type == AbstractMap.ITEM_ROCK) {
            fixtureDef = dangerFixture;
        } else if (type == AbstractMap.ITEM_KEY) {
            fixtureDef = keyFixture;
        } else if (type == AbstractMap.ITEM_TRIGGER) {
            fixtureDef = triggerFixture;
        } else if (type == AbstractMap.ITEM_UPGRADE)
            fixtureDef = upgradeFixture;
        else if (type == AbstractMap.ITEM_FEATURE)
            fixtureDef = featureFixture;
        else if (type == AbstractMap.PLATFORM)
            fixtureDef = platformFixture;
        else if (type == AbstractMap.ITEM_SPAWN)
            fixtureDef = warpFixture;
        else if (type == AbstractMap.ITEM_ENTRANCE_SURFACE || type == AbstractMap.ITEM_ENTRANCE_CAVE || type == AbstractMap.ITEM_ENTRANCE_UNIVERSE)
            fixtureDef = exitFixture;
        else if (type == AbstractMap.ITEM_LIQUID_SS || type == AbstractMap.ITEM_LIQUID_TD)
            fixtureDef = exitFixture; // TODO: GIVE OWN FIXTURE WITH GRAVITY/DRAG LATER
        else if (type == AbstractMap.ITEM_LAMP)
            fixtureDef = lampFixture;
        else if (type == AbstractMap.ITEM_MINE)
            fixtureDef = mineFixture;
        else if (type == AbstractMap.ITEM_CRATE) {
            fixtureDef = crateFixture;
            //fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits | Config.CATEGORY_LIGHT);
        } else if (type == AbstractMap.ITEM_CANNON) {
            fixtureDef = turretFixture;
        } else
            throw new RuntimeException("Have no fixtureDef for type " + type);

        return fixtureDef;
    }

    private FixtureDef getTileFixtureDef(int type, int direction) {
        FixtureDef fixtureDef;

        if (type <= AbstractMap.SOLID) {
            fixtureDef = tileFixture;

            if (direction != AbstractMap.NO_DIRECTION) {
                fixtureDef.shape = createTileDirShape();
            } else {
                fixtureDef.shape = createCenteredBoxShape(Config.TILE_SIZE_X, Config.TILE_SIZE_Y);
            }
        } else if (type == AbstractMap.DOOR)
            fixtureDef = doorFixture;
        else if (type == AbstractMap.ROOM || type == AbstractMap.CORRIDOR) {
            fixtureDef = featureFixture;
            //fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~Config.CATEGORY_LIGHT);
        } else
            throw new RuntimeException("Unknown map tile type " + type);

        return fixtureDef;
    }

    private Color getRandomLampColor() {
        int f = MathUtils.random(5);

        final float alpha = .75f;

        switch (f) {
            case 0:
                return new Color(1f, 0, 0, alpha);
            case 1:
                return new Color(0f, 1f, 0, alpha);
            case 2:
                return new Color(0f, 0, 1f, alpha);
            case 3:
                return new Color(1f, 0, 1f, alpha);
            case 4:
                return new Color(1f, 1f, 0, alpha);
            case 5:
                return new Color(0f, 1f, 1f, alpha);
            default:
                return new Color(1f, 1f, 1f, alpha);
        }
    }

    public void createItems(int num, float x, float y, int type) {
        for (int i = 0; i < num; i++) {
            createItem(x, y, type);
        }
    }

    public PlayerBody createShipPlayer(Vector2 playerPosition) {
        Body body = getDefaultPlayerBody(playerPosition, false);
        return new PlayerBody(body, Config.TILE_SIZE_X / 2, new PlayerData(),
                ResourceFactory.getInstance().getWorldTypeImage("player"),
                PlayerBody.CONTROL_MODE.NEWTONIAN, GameBody.BODY_TYPE.SPACESHIP);
    }

    public PlayerBody createTopDownPlayer(Vector2 playerPosition) {
        Body body = getDefaultPlayerBody(playerPosition, true);
        return new AnimatedPlayerBody(body, Config.TILE_SIZE_X / 2, new PlayerData(), ResourceFactory.getInstance().getWorldTypeAnimation("player"),
                PlayerBody.CONTROL_MODE.DIRECT, GameBody.BODY_TYPE.HUMANOID);
    }

    public PlayerBody createTopDownPlayerVehicle(Vector2 playerPosition) {
        Body body = getVehiclePlayerBody(playerPosition, false);
        return new PlayerVehicleBody(body, Config.TILE_SIZE_X / 2, new PlayerData(),
                ResourceFactory.getInstance().getWorldTypeImage("tank"),
                ResourceFactory.getInstance().getWorldTypeImage("tankgun"));
    }

    public PlayerBody createSidescrollPlayer(Vector2 playerPosition) {
        Body body = getDefaultPlayerBody(playerPosition, true);
        createTongue(body);
        //createDongue(body);

        return new BlobPlayerBody(body, Config.TILE_SIZE_X / 2, new PlayerData(),
                ResourceFactory.getInstance().getWorldTypeImage("player"),
                ResourceFactory.getInstance().getWorldTypeImage("eye"),
                ResourceFactory.getInstance().getWorldTypeImage("pupil"),
                ResourceFactory.getInstance().getWorldTypeImage("mouth"),
                PlayerBody.CONTROL_MODE.DIRECT, GameBody.BODY_TYPE.ALIEN);
    }

    private void createTongue(Body owner) {
         Body tip = getBulletBody(Loot.LOOT_TYPE.TONGUE, owner.getPosition().x, owner.getPosition().y, 0, 0, true, .1f);
         int particles = 10;
         Body[] rest = new Body[particles];

         for (int i = 0; i < particles; i++) {
             rest[i] = getBulletBody(Loot.LOOT_TYPE.TONGUE, owner.getPosition().x, owner.getPosition().y, 0, 0, true, .1f / ((i * .1f) + 1f));

             if (i == 0)
                 BodyFactory.getInstance().connectRope(tip, rest[0], .1f);
             if (i > 0 && i < particles)
                 BodyFactory.getInstance().connectRope(rest[i - 1], rest[i], .1f);
             if (i == particles - 1)
                 BodyFactory.getInstance().connectRope(owner, rest[i], .01f, .02f);
         }

         tongue = new TongueBody(tip, rest, bulletFixture.shape.getRadius(), ResourceFactory.getInstance().getBulletImage("tongue"), null, null, 0, 1);
     }

    private void createDongue(Body owner) {
        Body tip = getBulletBody(Loot.LOOT_TYPE.DONGUE,
                owner.getPosition().x,
                owner.getPosition().y + Config.TILE_SIZE_Y,
                0, 0, true, .05f);

        int particles = 5;
        Body[] rest = new Body[particles];

        for (int i = 0; i < particles; i++) {
            rest[i] = getBulletBody(Loot.LOOT_TYPE.DONGUE,
                    owner.getPosition().x,
                    owner.getPosition().y + Config.TILE_SIZE_Y / ((i * .05f) + 1f),
                    0, 0, true, .05f / ((i * .05f) + 1f));

            if (i == 0)
                BodyFactory.getInstance().rubberConnect(tip, rest[0], 0, -.001f);
            if (i > 0 && i < particles)
                BodyFactory.getInstance().rubberConnect(rest[i - 1], rest[i], 0, -.001f);
            if (i == particles - 1)
                BodyFactory.getInstance().rubberConnect(owner, rest[i], 0, -.001f);
        }

        dongue = new TongueBody(tip, rest, bulletFixture.shape.getRadius(), ResourceFactory.getInstance().getBulletImage("tongue"),
                ResourceFactory.getInstance().getWorldTypeImage("eye"), ResourceFactory.getInstance().getWorldTypeImage("pupil"), 0, 2);
    }

    private Body getDefaultPlayerBody(Vector2 playerPosition, boolean fixedRotation) {
        BodyDef playerDef = createAgentBodyDef(fixedRotation);
        playerDef.position.set(playerPosition.x, playerPosition.y);
        Body body = createBody(playerDef);
        Shape shape = createCircleShape(Config.TILE_SIZE_X / 4);
        body.createFixture(createPlayerAttributes(shape));
        body.setAwake(true);
        return body;
    }

    private Body getVehiclePlayerBody(Vector2 playerPosition, boolean fixedRotation) {
        BodyDef playerDef = createAgentBodyDef(fixedRotation);
        playerDef.position.set(playerPosition.x, playerPosition.y);
        Body body = createBody(playerDef);
        Shape shape = createCenteredBoxShape(Config.TILE_SIZE_X / 2, Config.TILE_SIZE_Y / 4);
        body.createFixture(createPlayerAttributes(shape));
        body.setAwake(true);
        return body;
    }

    protected FixtureDef createPlayerAttributes(Shape shape) {
        playerFixture.shape = shape;
        playerFixture.density = 500f;
        playerFixture.friction = 0f;
        playerFixture.restitution = 0f;
        return playerFixture;
    }

    public void dispose() {
        world.dispose();

        bodyFactory = null;
        System.out.println(this.getClass().getName() + " disposed");
    }

    public void connectDistance(Body master, Body slave) {
        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.bodyA = master;
        distanceJointDef.bodyB = slave;
        distanceJointDef.initialize(master, slave, master.getPosition(), slave.getPosition());
/*
        distanceJointDef.frequencyHz = 1f;
        distanceJointDef.dampingRatio = 1f;
*/
/*
        distanceJointDef.dampingRatio = .5f;
*/
        distanceJointDef.dampingRatio = .8f;
        distanceJointDef.frequencyHz = 2f;
        distanceJointDef.collideConnected = true;
        world.createJoint(distanceJointDef);
    }

    public void connectRope(Body master, Body slave) {
        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = master;
        ropeJointDef.bodyB = slave;
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.maxLength = master.getPosition().cpy().sub(slave.getPosition()).len();
        ropeJointDef.collideConnected = true;
        world.createJoint(ropeJointDef);
    }

    public boolean weld(Body master, Body slave) {
        if (master != null && slave != null) {
            WeldJointDef weldJointDef = new WeldJointDef();
            weldJointDef.initialize(master, slave, master.getPosition());
            world.createJoint(weldJointDef);
            return true;
        }

        return false;
    }

    public void connectRope(Body master, Body slave, float len, float masterAnchorModY) {
        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = slave;
        ropeJointDef.bodyB = master;
        ropeJointDef.localAnchorA.set(0, masterAnchorModY);
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.maxLength = len;
        ropeJointDef.collideConnected = false;
        world.createJoint(ropeJointDef);
    }

    public void connectRope(Body master, Body slave, float len) {
        connectRope(master, slave, len, 0);
    }

    public void rubberConnect(Body master, Body slave, float x, float y) {
        RevoluteJointDef rubberConnect = new RevoluteJointDef();
        rubberConnect.bodyA = slave;
        rubberConnect.bodyB = master;
        rubberConnect.localAnchorA.set(x, y);
        rubberConnect.localAnchorB.set(x, y);
        rubberConnect.collideConnected = false;
        world.createJoint(rubberConnect);
    }

    public void createExplosion(Vector2 position, int power) {
        if (particlesToCreate.size < Config.MAX_EXPLOSIONS)
            particlesToCreate.add(new ParticleDef(position, power));
    }

    public GameBody getFloorFeature(float x, float y, TextureAtlas.AtlasRegion indoor) {
        return new FloorBody(x, y, Config.TILE_SIZE_X, indoor);
    }

    public GameBody getCorridorFeature(float x, float y, TextureAtlas.AtlasRegion corridorImage) {
        return new FeatureBody(x, y, Config.TILE_SIZE_X, corridorImage,
                ResourceFactory.getInstance().getImage(ResourceFactory.MASK, "terrainmask"));
    }

    public GameBody getPlatformBody(float x, float y, int direction) {
        BodyDef tileBodyDef = createStaticBodyDef(0);
        tileBodyDef.position.x = x;
        tileBodyDef.position.y = y;

        boolean round = MathUtils.randomBoolean();
        boolean rotating = MathUtils.random() < .1f;
        boolean sliding = MathUtils.random() < .1f;

        float size = rotating ? Config.TILE_SIZE_X : MathUtils.random(Config.TILE_SIZE_X, Config.TILE_SIZE_X * 1.5f);
        float offsetX = MathUtils.random(size);
        float offsetY = MathUtils.random(size);

        if (rotating) {
            offsetX = MathUtils.random(size / 2);
            offsetY = MathUtils.random(size / 2);
            createPlatformAttributes(createNonCenteredBoxShape(size, size, offsetX, offsetY));
        } else if (sliding) {
            size = Config.TILE_SIZE_X;

            if (direction == AbstractMap.E || direction == AbstractMap.W) {
                offsetX = 0;
                offsetY = MathUtils.random(-Config.TILE_SIZE_Y / 2f, Config.TILE_SIZE_Y / 2f);
            } else {
                offsetX = MathUtils.random(-Config.TILE_SIZE_X / 2f, Config.TILE_SIZE_X / 2f);
                offsetY = 0;
            }

            createPlatformAttributes(createNonCenteredBoxShape(size, size, offsetX, offsetY));
        } else {
            if (round)
                createPlatformAttributes(createCircleShape(size));
            else
                createPlatformAttributes(createCenteredBoxShape(size, size));
        }

        Body body = createBody(tileBodyDef, platformFixture);
        body.setLinearVelocity(0, 0);

        FEATURE_TYPE type = FEATURE_TYPE.PLATFORM_1;

        if (rotating)
            return new PlatformBody(body, size, ResourceFactory.getInstance().getFeatureImage(FEATURE_TYPE.MOVING_PLATFORM_1), true, offsetX, offsetY);
        else if (sliding)
            return new PlatformBody(body, size, ResourceFactory.getInstance().getFeatureImage(FEATURE_TYPE.MOVING_PLATFORM_1), offsetX, offsetY, direction);
        else
            return new PlatformBody(body, size, ResourceFactory.getInstance().getFeatureImage(type),
                    round ? ResourceFactory.getInstance().getImage(ResourceFactory.MASK,
                            "roundplatformmask") : ResourceFactory.getInstance().getImage(ResourceFactory.MASK,
                            "squareplatformmask"), false, 0, 0, AbstractMap.NO_DIRECTION);
    }

    public GameBody getRandomChasmBody(float x, float y, TextureAtlas.AtlasRegion indoor) {
        BodyDef tileBodyDef = createStaticBodyDef(0);
        tileBodyDef.position.x = x;
        tileBodyDef.position.y = y;

        // Defaults
        float size = Config.TILE_SIZE_X;
        float offsetX = 0;
        float offsetY = 0;
        float rotation = 0;
        Texture chasmImage = ResourceFactory.getInstance().getTransparentTexture(Level.getInstance().getChasmImage());
        TextureAtlas.AtlasRegion chasmMask = ResourceFactory.getInstance().getImage(ResourceFactory.MASK, "chasmmask");

        float f = MathUtils.random();

        if (f < .16f) {   // OFFSET LAVA I HULL
            size = Config.TILE_SIZE_X * .5f;
            offsetX = MathUtils.random(-size / 2, size / 2);
            offsetY = MathUtils.random(-size / 2, size / 2);
            rotation = MathUtils.random();
        } else if (f < .32f) { // OFFSETT HULL
            size = Config.TILE_SIZE_X * .5f;
            offsetX = MathUtils.random(-size / 2, size / 2);
            offsetY = MathUtils.random(-size / 2, size / 2);
            rotation = MathUtils.random();
            chasmImage = null;
        } else if (f < .48f) { // LAVA
            chasmMask = null;
        } else if (f < .64f) { // HULL
            chasmImage = null;
            chasmMask = null;
        } else if (f < .80f) { // MASKED HULL
            size = Config.TILE_SIZE_X * .75f;
            rotation = MathUtils.random();
            chasmImage = null;
        } else {  // MASKED LAVA
            size = Config.TILE_SIZE_X * .75f;
        }

        createPlatformAttributes(createNonCenteredBoxShape(size, size, offsetX, offsetY));
        Body body = createBody(tileBodyDef, platformFixture);
        body.setLinearVelocity(0, 0);
        return new ChasmBody(body, x, y, size, indoor, chasmImage, chasmMask, offsetX, offsetY, rotation);
    }

    public FeatureBody getFeature(float x, float y, FEATURE_TYPE type) {
        TextureAtlas.AtlasRegion featureImage = ResourceFactory.getInstance().getFeatureImage(type);

        switch (type) {
            case NEBULA_FEATURE_1:
                return new FeatureBody(x, y, Config.TILE_SIZE_X,
                        featureImage, MathUtils.random(3f, 6f), MathUtils.random(360));
            case FLOOR_FEATURE_1:
                return new FeatureBody(x, y, Config.TILE_SIZE_X, featureImage);
            case SLIMEPOOL:
                return new FeatureBody(x, y, Config.TILE_SIZE_X * 2, featureImage, ResourceFactory.getInstance().getImage(ResourceFactory.MASK, "terrainmask"));
            case TOPDOWN_OBSCURING_FEATURE_1:
                return new ObscuringFeatureBody(x, y, Config.TILE_SIZE_X, featureImage, MathUtils.random(.5f, 2f), MathUtils.random(360), true);
            case SIDESCROLL_OBSCURING_FEATURE_1:
                return new ObscuringFeatureBody(x, y, Config.TILE_SIZE_X, featureImage);
            case OUTDOOR_FEATURE_1:
                return new FeatureBody(x, y, Config.TILE_SIZE_X * 2, featureImage,
                        ResourceFactory.getInstance().getImage(ResourceFactory.MASK, "terrainmask"));
            default:
                throw new RuntimeException("Unknown feature for " + type);
        }
    }


    private ExitBody createAppropriateExitBody(Level.LEVEL_TYPE worldType, int type, FixtureDef fixtureDef, Body body) {
        final float shaderRadius = fixtureDef.shape.getRadius() * Config.getDimensions().SCREEN_ON_WORLD_FACTOR * 2;

        if (type == AbstractMap.ITEM_ENTRANCE_SURFACE) {
            return new ExitBody(body, fixtureDef.shape.getRadius(), Level.LEVEL_TYPE.TOPDOWN,
                    ResourceFactory.getInstance().getWorldTypeImage("exit_to_surface"),
                    ShaderFactory.getInstance().getShader(RenderOperations.BACKGROUND_TYPE.BEACON, shaderRadius, shaderRadius),
                    ResourceFactory.getInstance().getImage(ResourceFactory.MASK, "orb"));
        } else if (type == AbstractMap.ITEM_ENTRANCE_UNIVERSE)
            return new ExitBody(body, fixtureDef.shape.getRadius(), Level.LEVEL_TYPE.UNIVERSE,
                    ResourceFactory.getInstance().getWorldTypeImage("exit_to_space"),
                    ShaderFactory.getInstance().getShader(RenderOperations.BACKGROUND_TYPE.SHININGSTARSCROLL, shaderRadius, shaderRadius),
                    ResourceFactory.getInstance().getImage(ResourceFactory.MASK, "orb"));
        else if (type == AbstractMap.ITEM_ENTRANCE_CAVE)
            return new ExitBody(body, fixtureDef.shape.getRadius(), Level.LEVEL_TYPE.SIDESCROLL,
                    ResourceFactory.getInstance().getWorldTypeImage("exit_to_cave"),
                    ShaderFactory.getInstance().getShader(RenderOperations.BACKGROUND_TYPE.NEONCROSS, shaderRadius, shaderRadius),
                    ResourceFactory.getInstance().getImage(ResourceFactory.MASK, "orb"));
        else
            throw new RuntimeException("What are you trying to do!?");
    }

    public Array<Body> update() {
        if (world.isLocked()) throw new RuntimeException("WORLD IS LOCKED!");

        if (bulletsToCreate.size > 0)
            createBullets();

        if (agentsToCreate.size > 0)
            createAgents();

        if (lootToCreate.size > 0)
            createLoot();

        if (particlesToCreate.size > 0)
            createParticles();

        if (lightSourcesToCreate.size > 0)
            createLights();

        world.getBodies(this.worldBodies);
        world.getJoints(this.worldJoints);
        return this.worldBodies;
    }

    private void createBullets() {
        float angle;
        for (BulletDef def : bulletsToCreate) {
            final Body body = getBulletBody(def.getType(), def.getSourceX(), def.getSourceY(), def.getVelocityX(), def.getVelocityY(), def.isPlayerOwned(), Config.TILE_SIZE_X / 5);
            angle = MathUtils.radiansToDegrees * MathUtils.atan2(def.getVelocityY(), def.getVelocityX());

            switch (def.getType()) {
                case BOMB:
                    new BombBody(body, bulletFixture.shape.getRadius(), def.getBulletImage(), angle, def.getDamage());
                    break;
                case FIREBALL:
                    new EnergyBallBody(body, bulletFixture.shape.getRadius(), angle, def.getDamage());
                    break;
                case GUN:
                    new BulletBody(body, bulletFixture.shape.getRadius(), def.getBulletImage(), angle, def.getDamage());
                    break;
                case ROCKET:
                    new RocketBody(body, Config.TILE_SIZE_X / 4f, Config.TILE_SIZE_X / 16f, def.getBulletImage(), angle, def.getDamage());
                    break;
            }
        }
        bulletsToCreate.clear();
    }

    public void createLoot() {
        for (LootDef lootDef : lootToCreate) {
            Body body = getDefaultLootBody(lootDef.getPosition());
            Loot loot = LootFactory.getInstance().getLoot(lootDef.getType());
            if (LootFactory.weapons.contains(lootDef.getType()))
                new LootBody(body, Config.TILE_SIZE_X / 2f, lootDef.getType(), ResourceFactory.getInstance().getWeaponImage(loot.getImageName()), ResourceFactory.getInstance().getImage(ResourceFactory.UI, "star"), Color.RED);
            else
                new LootBody(body, Config.TILE_SIZE_X / 2f, lootDef.getType(), ResourceFactory.getInstance().getItemImage(loot.getImageName()), Color.YELLOW);
        }

        lootToCreate.clear();
    }

    public void createParticles() {
        float angle;

        float numParticles = Config.NUM_PARTICLES;

        for (ParticleDef def : particlesToCreate) {
            for (int i = 0; i < numParticles; i++) {
                angle = (i / numParticles) * MathUtils.PI2;
                rayDir.set(MathUtils.sin(angle), MathUtils.cos(angle));
                particles[i + (int) (explosionNumber * numParticles)].reposition(def.getPosition(), rayDir.scl(def.getPower()));
            }
            explosionNumber++;
            if (explosionNumber >= Config.MAX_EXPLOSIONS) explosionNumber = 0;
        }

        particlesToCreate.clear();
    }

    public void createLights() {
        for (LightSourceDef def : lightSourcesToCreate) {
            Body body = getDefaultLightBody(def.getPosition());
            new LightBody(body, 1, LightFactory.getInstance().getLightForType(def.getType()), -def.getLightMod(), def.getNumRepetitions());
        }

        lightSourcesToCreate.clear();
    }

    private void createAgents() {
        if(GameModel.getPlayer() == null || GameModel.getPlayer().position == null) return;

        Vector2 target = GameModel.getPlayer().position;

        for (AgentDef def : agentsToCreate) {
            switch (def.getType()) {
                case WALKER_SINGLE:
                    final ENEMY_IMAGE walkerType = MathUtils.random(2) == 1 ? ENEMY_IMAGE.WALKER_1 : ENEMY_IMAGE.WALKER_2;

                    AgentBody walkerSingle = new AgentBody(getAgentBody(def.getPosition(), true, false, createCircleShape(Config.WALKER_RADIUS), Config.WALKER_DENSITY, 1f), Config.TILE_SIZE_X / 2,
                            ResourceFactory.getInstance().getWorldTypeImage(walkerType == ENEMY_IMAGE.WALKER_1 ? "enemy1" : "enemy2"));
                    walkerSingle.bodyType = GameBody.BODY_TYPE.HUMANOID;
                    walkerSingle.setMind(new PrioritizingMind());
                    BehaviorFactory.setupDefaultMindBehaviors(target, walkerSingle);

                    walkerSingle.getMind().addBehavior(new WalkerSeek(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE));
                    walkerSingle.getMind().addAttackBehavior(new WalkerSeek(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE));
                    walkerSingle.getMind().addDefendBehavior(new WalkerFlee(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE));

                    walkerSingle.getBody().setLinearVelocity(
                            MathUtils.random(walkerSingle.minSpeed * 2) - walkerSingle.minSpeed,
                            0f);

                    LootFactory.getInstance().getWeapon(Level.getInstance().getWeaponFor(walkerType)).onPickup(walkerSingle);
                    break;
                case FLYER_SINGLE:
                    ENEMY_ANIM randomFlyer = Level.getInstance().getRandomFlyer();
                    AnimatedAgentBody flyerSingle = new AnimatedAgentBody(getAgentBody(def.getPosition(), true, true, createCircleShape(Config.FLYER_RADIUS), Config.FLYER_DENSITY), Config.TILE_SIZE_X / 2,
                            ResourceFactory.getInstance().getFlyerAnimation(randomFlyer), ResourceFactory.getInstance().getWorldTypeImage("enemy2"));
                    flyerSingle.flying = true;
                    flyerSingle.bodyType = GameBody.BODY_TYPE.HUMANOID;
                    BehaviorFactory.setupRangedBehavior(target, flyerSingle, GameModel.getPlayer(), def.getPosition().cpy());
                    LootFactory.getInstance().getWeapon(Level.getInstance().getWeaponFor(randomFlyer)).onPickup(flyerSingle);
                    break;
                case BLOB:
                    Body head = getAgentBody(def.getPosition(), true, true, createCircleShape(Config.BLOBLIMB_RADIUS), Config.BLOBLIMB_DENSITY, 1);
                    Body[] limbs = new Body[16];

                    for (int i = 0; i < 16; i++) {
                        float angle = (i / (float) 16) * MathUtils.PI2;
                        Vector2 rayDir = new Vector2(MathUtils.sin(angle), MathUtils.cos(angle));
                        limbs[i] = getAgentBody(def.getPosition().cpy().add(rayDir.scl(Config.TILE_SIZE_X / 2f)), true, true,
                                createCircleShape(Config.BLOBLIMB_RADIUS), Config.BLOBLIMB_DENSITY, 1);

                        BodyFactory.getInstance().connectRope(limbs[i], head);
                        if (i > 0)
                            BodyFactory.getInstance().connectRope(limbs[i], limbs[i - 1]);
                    }

                    BodyFactory.getInstance().connectRope(limbs[0], limbs[16 - 1]);

                    Blob blob = new Blob(head, limbs, ResourceFactory.getInstance().getWorldTypeImage("blob"), Config.TILE_SIZE_X / 2f / 2f);
                    BehaviorFactory.setupMeleeBehavior(target, blob, def.getPosition().cpy());
                    break;
                default:
                    throw new RuntimeException("Unknown enemy type " + def.getType());   // Should not happen
            }
        }
        agentsToCreate.clear();
    }

    public void cleanUp() {
        Object userData;
        for (Body body : worldBodies) {
            if (body == null) continue;

            userData = body.getUserData();

            if (userData != null && userData instanceof DiscardBody) {
                if (body.getJointList().size > 0) {
                    world.destroyJoint(body.getJointList().get(0).joint);
                }
                world.destroyBody(body); // Joints are automatically destroyed
            }
        }

        for (Joint joint : worldJoints) {
            if (joint == null) continue;

            userData = joint.getUserData();

            if (userData != null && userData instanceof DiscardJoint) {
                world.destroyJoint(joint);
            }
        }
    }

    private Body createBody(BodyDef bodyDef, FixtureDef fixtureDef) { // For scheduling body creation
        Body body = world.createBody(bodyDef);
        if (fixtureDef != null)
            body.createFixture(fixtureDef);
        return body;
    }

    private Body createBody(BodyDef bodyDef) { // For scheduling body creation
        return createBody(bodyDef, null);
    }

    public static class BulletDef {
        private final Loot.LOOT_TYPE type;
        float sourceX;
        float sourceY;
        float velocityX;
        float velocityY;
        int damage;
        boolean playerOwned;
        TextureRegion bulletImage;

        public BulletDef(Loot.LOOT_TYPE type, float sourceX, float sourceY, float velocityX, float velocityY, TextureRegion bulletImage, int damage, boolean playerOwned) {
            this.sourceX = sourceX;
            this.sourceY = sourceY;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.playerOwned = playerOwned;
            this.bulletImage = bulletImage;
            this.type = type;
            this.damage = damage;
        }

        public float getSourceX() {
            return sourceX;
        }

        public float getSourceY() {
            return sourceY;
        }

        public float getVelocityX() {
            return velocityX;
        }

        public float getVelocityY() {
            return velocityY;
        }

        public boolean isPlayerOwned() {
            return playerOwned;
        }

        public TextureRegion getBulletImage() {
            return bulletImage;
        }

        public Loot.LOOT_TYPE getType() {
            return type;
        }

        public int getDamage() {
            return damage;
        }
    }

    public static class ParticleDef {
        Vector2 position;
        float power;

        public ParticleDef(Vector2 position, float power) {
            this.position = position;
            this.power = power;
        }

        public Vector2 getPosition() {
            return position;
        }

        public float getPower() {
            return power;
        }
    }

    public static class AgentDef {
        ENEMY_TYPE type;
        Vector2 position;

        public AgentDef(ENEMY_TYPE type, Vector2 position) {
            this.type = type;
            this.position = position;
        }

        public ENEMY_TYPE getType() {
            return type;
        }

        public Vector2 getPosition() {
            return position;
        }
    }

    public static class LootDef {
        Loot.LOOT_TYPE type;
        Vector2 position;

        public LootDef(Loot.LOOT_TYPE type, Vector2 position) {
            this.type = type;
            this.position = position;
        }

        public Loot.LOOT_TYPE getType() {
            return type;
        }

        public Vector2 getPosition() {
            return position;
        }
    }

    public static class LightSourceDef {
        float lightMod;
        int numRepetitions;
        LightFactory.LIGHT_TYPE type;
        Vector2 position;

        public LightSourceDef(Vector2 position, LightFactory.LIGHT_TYPE type, float lightMod, int numRepetitions) {
            this.position = position;
            this.type = type;
            this.lightMod = lightMod;
            this.numRepetitions = numRepetitions;
        }

        public Vector2 getPosition() {
            return position;
        }

        public float getLightMod() {
            return lightMod;
        }

        public int getNumRepetitions() {
            return numRepetitions;
        }

        public LightFactory.LIGHT_TYPE getType() {
            return type;
        }
    }

    public void lick(PlayerBody target, Vector2 firingDirection) {
        target.onLick();
        tongue.lick(target.position, firingDirection);
    }

    public void slurp(PlayerBody target) {
        target.onSlurp();
        tongue.slurp(target.position);
        //dongue.slurp(target);
    }

/*
    public Vector2 getVector() {
        return vectorPool.obtain();
    }

    public void freeVector(Vector2 vector) {
        vector.set(0, 0);
        vectorPool.free(vector);
    }
*/
}
