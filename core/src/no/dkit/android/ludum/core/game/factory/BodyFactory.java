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
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.DiscardBody;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.LightBody;
import no.dkit.android.ludum.core.game.model.body.agent.AnimatedAgentBody;
import no.dkit.android.ludum.core.game.model.body.agent.MonsterPlayerBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.body.item.DoorBody;
import no.dkit.android.ludum.core.game.model.body.item.LootBody;
import no.dkit.android.ludum.core.game.model.body.item.TriggerBody;
import no.dkit.android.ludum.core.game.model.body.scenery.BlockBody;
import no.dkit.android.ludum.core.game.model.body.scenery.BorderBody;
import no.dkit.android.ludum.core.game.model.body.scenery.FeatureBody;
import no.dkit.android.ludum.core.game.model.body.scenery.FloorBody;
import no.dkit.android.ludum.core.game.model.body.scenery.LampBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ObscuringFeatureBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ObscuringShadedBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ShadedBody;
import no.dkit.android.ludum.core.game.model.body.weapon.BulletBody;
import no.dkit.android.ludum.core.game.model.body.weapon.MeleeBody;
import no.dkit.android.ludum.core.game.model.body.weapon.ParticleBody;
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
    public static final short TRIGGER_BITS = Config.CATEGORY_PLAYER;
    public static final short SELF_COLLIDING_ENEMY_BITS = (short) (ENEMY_BITS | Config.CATEGORY_ENEMY);

    private int explosionNumber = 0;

    public enum ENEMY_IMAGE {
        SHIP_1, SHIP_2
    }

    public enum ENEMY_ANIM {
        FEMALE, MALE, FEMALESOLDIER, MALESOLDIER
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

/*
    private final Pool<Body> agentPool;
    private final Pool<Body> lootPool;
    private final Pool<Body> bulletPool;
    private final Pool<Body> particlePool;
    private final Pool<Body> lightPool;
    private final Pool<Vector2> vectorPool;
*/

    private FixtureDef triggerFixture = new FixtureDef();
    private FixtureDef particleFixture = new FixtureDef();
    private FixtureDef tileFixture = new FixtureDef();
    private FixtureDef doorFixture = new FixtureDef();
    private FixtureDef lootFixture = new FixtureDef();
    private FixtureDef featureFixture = new FixtureDef();
    private FixtureDef lampFixture = new FixtureDef();
    private FixtureDef agentFixture = new FixtureDef();
    private FixtureDef bulletFixture = new FixtureDef();
    private FixtureDef linkFixture = new FixtureDef();
    private FixtureDef meleeFixture = new FixtureDef();

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
        createParticleAttributes(createCircleShape(Config.TILE_SIZE_X / 50));
        createTileAttributes(createCenteredBoxShape(Config.TILE_SIZE_X, Config.TILE_SIZE_Y));
        createDoorAttributes(createDoorShape());
        createItemAttributes(createCircleShape(Config.TILE_SIZE_X / 4));
        createPlanetAttributes(createCircleShape(Config.TILE_SIZE_X / 2));
        createLampAttributes(createCircleShape(Config.TILE_SIZE_X / 4));
        createAgentAttributes(createCircleShape(Config.TILE_SIZE_X / 3), 3);
        createLinkAttributes();
        createMeleeAttributes();

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

    private void createLinkAttributes() {
        linkFixture = new FixtureDef();
        linkFixture.isSensor = true;
        linkFixture.restitution = 0;
        linkFixture.friction = 0;
/*
        linkFixture.shape = shape; // This is set on rope creation time
*/
        linkFixture.density = 1;
    }

    private void createMeleeAttributes() {
        meleeFixture = new FixtureDef();
        meleeFixture.isSensor = true;
        meleeFixture.restitution = 1;
        meleeFixture.friction = 1;
        meleeFixture.density = 100;
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
        agentFixture.restitution = 1f;
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

        doorFixture.filter.categoryBits = Config.CATEGORY_SCENERY;
        doorFixture.filter.maskBits = SCENERY_BITS;

        lootFixture.filter.categoryBits = Config.CATEGORY_LOOT;
        lootFixture.filter.maskBits = LOOT_BITS;

        meleeFixture.filter.categoryBits = Config.CATEGORY_BULLET;
        meleeFixture.filter.maskBits = 0;
        meleeFixture.filter.groupIndex = -1; // particles and bullets should not collide with each other

        tileFixture.filter.categoryBits = Config.CATEGORY_SCENERY;
        tileFixture.filter.maskBits = SCENERY_BITS;

        triggerFixture.filter.categoryBits = Config.CATEGORY_TRIGGER;
        triggerFixture.filter.maskBits = TRIGGER_BITS;

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

    private FixtureDef createTriggerAttributes(Shape shape) {
        triggerFixture.shape = shape;
        triggerFixture.density = 5000f;
        triggerFixture.friction = 1f;
        triggerFixture.restitution = 0f;
        triggerFixture.isSensor = true;

        return triggerFixture;
    }

    private FixtureDef createPlanetAttributes(Shape shape) {
        featureFixture.shape = shape;
        featureFixture.density = 5.0f;
        featureFixture.friction = .95f;
        featureFixture.restitution = 0f;
        featureFixture.isSensor = true;

        return featureFixture;
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

    private Shape createCircleShape(float radius) {
        Shape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        return circleShape;
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
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, 1, 1, 1, false);
    }

    private BodyDef createGravityBulletDef(float gravityScale) {
        return createBodyDef(true, BodyDef.BodyType.DynamicBody, 0, gravityScale, 0, 0, false);
    }

    private BodyDef createSlowingBulletDef() {
        return createBodyDef(true, BodyDef.BodyType.DynamicBody, 0, 1, .75f, .75f, false);
    }

    private BodyDef createAgentBodyDef(boolean fixedRotation) {
        return createBodyDef(false, BodyDef.BodyType.DynamicBody, 0, 0, 0, 0, fixedRotation);
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

    private Body getBulletBody(Loot.LOOT_TYPE type, float sourceX, float sourceY, float velocityX, float velocityY, boolean playerOwned) {
        bulletFixture.filter.categoryBits = playerOwned ? Config.CATEGORY_BULLET : Config.CATEGORY_ENEMY_BULLET;
        bulletFixture.filter.maskBits = playerOwned ? PLAYER_BULLET_BITS : ENEMY_BULLET_BITS;

        BodyDef bulletDef;

        switch (type) {
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
        } else if (type == AbstractMap.CORRIDOR) {
            return getCorridorFeature(x, y, corridorImage);
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
        else if (type == AbstractMap.ITEM_TRIGGER)
            return new TriggerBody(body, fixtureDef.shape.getRadius(), ResourceFactory.getInstance().getWorldTypeImage("spawn"));
        else if (type == AbstractMap.ITEM_LAMP)
            return new LampBody(body, fixtureDef.shape.getRadius(), getRandomColor(), direction);
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
        else
            throw new RuntimeException("Should not happen...");
    }

    private LootBody getLoot(Body body, FixtureDef fixtureDef) {
        Loot loot = LootFactory.getInstance().getLoot(LootFactory.getInstance().getRandomLootType());

        if (LootFactory.weapons.contains(loot.getType()))
            return new LootBody(body, fixtureDef.shape.getRadius(), loot.getType(), ResourceFactory.getInstance().getWeaponImage(loot.getImageName()),
                    ResourceFactory.getInstance().getImage(ResourceFactory.UI, "star"));
        else
            return new LootBody(body, fixtureDef.shape.getRadius(), loot.getType(), ResourceFactory.getInstance().getItemImage(loot.getImageName()),
                    ResourceFactory.getInstance().getImage(ResourceFactory.UI, "star"));
    }

    private FixtureDef getItemFixtureDef(int type) {
        FixtureDef fixtureDef;

        if (type == AbstractMap.ITEM_LOOT) {
            fixtureDef = lootFixture;
        } else if (type == AbstractMap.ITEM_TRIGGER) {
            fixtureDef = triggerFixture;
        } else if (type == AbstractMap.ITEM_FEATURE)
            fixtureDef = featureFixture;
        else if (type == AbstractMap.ITEM_LAMP)
            fixtureDef = lampFixture;
        else
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

    private Color getRandomColor() {
        return new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), .75f);
    }

    public void createItems(int num, float x, float y, int type) {
        for (int i = 0; i < num; i++) {
            createItem(x, y, type);
        }
    }

    public PlayerBody createTopDownPlayer(Vector2 playerPosition) {
        Body body = getDefaultPlayerBody(playerPosition, false);
        final MonsterPlayerBody monsterPlayerBody = new MonsterPlayerBody(body, Config.TILE_SIZE_X / 4,
                ResourceFactory.getInstance().getWorldTypeImage("head"),
                ResourceFactory.getInstance().getWorldTypeImage("tail"),
                PlayerBody.CONTROL_MODE.DIRECT);
        final Body[] tail = createTail(monsterPlayerBody, 3, 8);
        monsterPlayerBody.addTail(tail);
        return monsterPlayerBody;
    }

    private Body[] createTail(GameBody head, float length, int numLinks) {
        if (length == 0 || numLinks == 0) throw new RuntimeException("Length and links must be > 0");

        Body[] linkBodies = new Body[numLinks];

        float newX, newY;

        for (int i = 0; i < numLinks; i++) {
            CircleShape tailShape = new CircleShape();
            final float tailRadius = Config.TILE_SIZE_X / 4f / (i + 1);
            tailShape.setRadius(tailRadius);

            BodyDef linkBodyDef = createTailBodyDef(1, true, 2, 2);
            newX = head.position.x + tailRadius * (i + 1);
            newY = head.position.y + tailRadius * (i + 1);
            linkBodyDef.position.set(newX, newY);
            linkFixture.shape = tailShape;
            linkBodies[i] = createBody(linkBodyDef, linkFixture);

            if (i == 0)
                revoluteConnect(head.getBody(), linkBodies[i], -tailRadius / 2, 0, 0, 0);
            else if (i > 0)
                revoluteConnect(linkBodies[i - 1], linkBodies[i], -tailRadius, 0, tailRadius, 0);
        }

        return linkBodies;
    }

    private BodyDef createTailBodyDef(float gravityScale, boolean movable, int linearDamping, int angularDamping) {
        return createBodyDef(false, movable ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody, 0, gravityScale, linearDamping, angularDamping, false);
    }

    public Joint revoluteConnect(Body hinge, Body slave, float x, float y, float x2, float y2) {
        RevoluteJointDef revoluteConnect = new RevoluteJointDef();
        revoluteConnect.bodyA = hinge;
        revoluteConnect.bodyB = slave;
        revoluteConnect.collideConnected = false;
        revoluteConnect.localAnchorA.set(x2, y2);
        revoluteConnect.localAnchorB.set(x, y);
        return world.createJoint(revoluteConnect);
    }

    private Body getDefaultPlayerBody(Vector2 playerPosition, boolean fixedRotation) {
        BodyDef playerDef = createAgentBodyDef(fixedRotation);
        playerDef.position.set(playerPosition.x, playerPosition.y);
        Body body = createBody(playerDef);
        Shape shape = createCircleShape(Config.TILE_SIZE_X / 5);
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
        playerFixture.density = 20f;
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

    public void connectRope(Body master, Body slave, float len) {
        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = master;
        ropeJointDef.bodyB = slave;
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.maxLength = len;
        ropeJointDef.collideConnected = true;
        world.createJoint(ropeJointDef);
    }

    public void createExplosion(Vector2 position, int power) {
        if (particlesToCreate.size < Config.MAX_EXPLOSIONS)
            particlesToCreate.add(new ParticleDef(position, power));
    }

    public GameBody getFloorFeature(float x, float y, TextureAtlas.AtlasRegion indoor) {
        return new FloorBody(x, y, Config.TILE_SIZE_X, indoor);
    }

    public GameBody getCorridorFeature(float x, float y, TextureAtlas.AtlasRegion corridorImage) {
        return new FeatureBody(x, y, Config.TILE_SIZE_X, corridorImage);
    }

    public FeatureBody getFeature(float x, float y, FEATURE_TYPE type) {
        TextureAtlas.AtlasRegion featureImage = ResourceFactory.getInstance().getFeatureImage(type);

        return new ObscuringFeatureBody(x, y, Config.TILE_SIZE_X, featureImage, MathUtils.random(.5f, 2f), MathUtils.random(360), true);
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
        return this.worldBodies;
    }

    private void createBullets() {
        float angle;
        for (BulletDef def : bulletsToCreate) {
            Body body = getBulletBody(def.getType(), def.getSourceX(), def.getSourceY(), def.getVelocityX(), def.getVelocityY(), def.isPlayerOwned());
            angle = MathUtils.radiansToDegrees * MathUtils.atan2(def.getVelocityY(), def.getVelocityX());
            new BulletBody(body, bulletFixture.shape.getRadius(), def.getBulletImage(), angle, def.getDamage());
            break;
        }
        bulletsToCreate.clear();
    }

    public void createLoot() {
        for (LootDef lootDef : lootToCreate) {
            Body body = getDefaultLootBody(lootDef.getPosition());
            Loot loot = LootFactory.getInstance().getLoot(lootDef.getType());
            if (LootFactory.weapons.contains(lootDef.getType()))
                new LootBody(body, Config.TILE_SIZE_X / 4f, lootDef.getType(), ResourceFactory.getInstance().getWeaponImage(loot.getImageName()), ResourceFactory.getInstance().getImage(ResourceFactory.UI, "star"));
            else
                new LootBody(body, Config.TILE_SIZE_X / 10f, lootDef.getType(), ResourceFactory.getInstance().getItemImage(loot.getImageName()));
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
        Vector2 target = GameModel.getPlayer().position;

        for (AgentDef def : agentsToCreate) {
            switch (def.getType()) {
                case WALKER_SINGLE:
                    ENEMY_ANIM randomWalker = Level.getInstance().getRandomWalker();
                    AnimatedAgentBody walkerSingle = new AnimatedAgentBody(getAgentBody(def.getPosition(), true, true, createCircleShape(Config.WALKER_RADIUS), Config.WALKER_DENSITY), Config.WALKER_RADIUS,
                            ResourceFactory.getInstance().getWalkerAnimation(randomWalker));
                    walkerSingle.bodyType = GameBody.BODY_TYPE.HUMANOID;

                    if (randomWalker == ENEMY_ANIM.FEMALESOLDIER || randomWalker == ENEMY_ANIM.MALESOLDIER) {
                        BehaviorFactory.soldierBehavior(target, walkerSingle);
                        final Loot.LOOT_TYPE weaponForWalker = Level.getInstance().getWeaponFor(randomWalker);
                        if (weaponForWalker != null)
                            LootFactory.getInstance().getWeapon(weaponForWalker).onPickup(walkerSingle);
                    } else {
                        BehaviorFactory.civilianBehavior(target, walkerSingle);
                    }
                    walkerSingle.setBoss();
                    break;
                case WALKER_GROUP:
                    ENEMY_ANIM randomGroupWalker = Level.getInstance().getRandomWalker();
                    Neighborhood walkerNeighborhood = new Neighborhood();
                    for (int i = 0; i < Config.GROUP_SIZE; i++) {
                        AnimatedAgentBody groupWalker = new AnimatedAgentBody(getAgentBody(def.getPosition().cpy().add(MathUtils.random() * 2 - 1, MathUtils.random() * 2 - 1),
                                true, true, createCircleShape(Config.WALKER_RADIUS), Config.WALKER_DENSITY), Config.WALKER_RADIUS,
                                ResourceFactory.getInstance().getWalkerAnimation(randomGroupWalker));
                        walkerNeighborhood.addAgentBody(groupWalker);
                        groupWalker.bodyType = GameBody.BODY_TYPE.HUMANOID;

                        if (randomGroupWalker == ENEMY_ANIM.FEMALESOLDIER || randomGroupWalker == ENEMY_ANIM.MALESOLDIER) {
                            BehaviorFactory.soldierGroupBehavior(target, walkerNeighborhood);

                            final Loot.LOOT_TYPE weaponFor = Level.getInstance().getWeaponFor(randomGroupWalker);
                            if (weaponFor != null)
                                LootFactory.getInstance().getWeapon(weaponFor).onPickup(groupWalker);
                        } else {
                            BehaviorFactory.civilianGroupBehavior(target, walkerNeighborhood);
                        }
                        groupWalker.setBoss();
                    }
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
                world.destroyBody(body); // Joints are automatically destroyed
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

    public Body[] attachMeleeWeapons(MonsterPlayerBody owner, float length) {
        if (length == 0) throw new RuntimeException("Length and links must be > 0");

        final BodyDef bodyDef = createBodyDef(true, BodyDef.BodyType.KinematicBody, 0, 0, 0, 0, false);

        final Body weaponBody = createBody(bodyDef);
        final Body weaponBody2 = createBody(bodyDef);

        meleeFixture.shape = createClawShape(length, false);
        meleeFixture.isSensor = true;
        weaponBody.createFixture(meleeFixture);

        meleeFixture.shape = createClawShape(length, true);
        meleeFixture.isSensor = true;
        weaponBody2.createFixture(meleeFixture);

        owner.addMeleeWeapons(
                new MeleeBody(weaponBody, Config.TILE_SIZE_X / 2f, ResourceFactory.getInstance().getWeaponImage("melee")),
                new MeleeBody(weaponBody2, Config.TILE_SIZE_X / 2f, ResourceFactory.getInstance().getWeaponImage("melee")));

        return new Body[]{weaponBody, weaponBody2};
    }

    private Shape createClawShape(float hx, boolean inverted) {
        Shape crossShape = new PolygonShape();
        ((PolygonShape) crossShape).setAsBox(hx / 2, hx / 8, new Vector2(inverted ? hx * .8f : -hx * .8f, hx * .8f), inverted ? MathUtils.degreesToRadians * -30 : MathUtils.degreesToRadians * 30);
        return crossShape;
    }
}
