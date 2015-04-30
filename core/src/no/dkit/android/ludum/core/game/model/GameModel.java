package no.dkit.android.ludum.core.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Arrive;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.LaserFactory;
import no.dkit.android.ludum.core.game.factory.LevelFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.factory.LootFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.ShaderFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.factory.TextItem;
import no.dkit.android.ludum.core.game.model.body.DiscardBody;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.body.item.DangerBody;
import no.dkit.android.ludum.core.game.model.body.item.KeyBody;
import no.dkit.android.ludum.core.game.model.body.item.LootBody;
import no.dkit.android.ludum.core.game.model.body.scenery.BlockBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ChasmBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ExitBody;
import no.dkit.android.ludum.core.game.model.body.scenery.FeatureBody;
import no.dkit.android.ludum.core.game.model.body.scenery.FloorBody;
import no.dkit.android.ludum.core.game.model.body.scenery.LampBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ObscuringFeatureBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ObscuringShadedBody;
import no.dkit.android.ludum.core.game.model.body.scenery.PlatformBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ShadedBody;
import no.dkit.android.ludum.core.game.model.body.weapon.LaserBody;
import no.dkit.android.ludum.core.game.model.body.weapon.ParticleBody;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.loot.Weapon;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.model.world.level.SandboxLevel;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.game.model.world.map.UniverseMap;
import no.dkit.android.ludum.core.game.quest.GameEvent;
import no.dkit.android.ludum.core.game.view.GameView;
import no.dkit.android.ludum.core.shaders.RenderOperations;
import no.dkit.android.ludum.core.shaders.TerrainShader;

import java.util.Iterator;
import java.util.List;

public class GameModel {
    long lastCleanup = System.currentTimeMillis();
    long updateBodiesInterval = (XXXX.performance == Config.PERFORMANCE.LOW ? 80 : 16); // Todo: More granularity diff bodies
    boolean objectsDirty = true; // Don't get objects all the time
    public boolean panning = false;
    long lastTap = System.currentTimeMillis();

    Camera camera;

    Array<Body> worldBodies = new Array<Body>();
    Array<GameBody> movingBodies = new Array<GameBody>();
    Array<GameBody> nonMovingBodies = new Array<GameBody>();
    Array<FeatureBody> featureBodies = new Array<FeatureBody>();
    Array<GameBody> featureLayer = new Array<GameBody>();
    Array<ShadedBody> shadedLayer = new Array<ShadedBody>();
    Array<ShadedBody> obscuringShadedLayer = new Array<ShadedBody>();
    Array<GameBody> backLayer = new Array<GameBody>();
    Array<GameBody> mediumLayer = new Array<GameBody>();
    Array<GameBody> frontLayer = new Array<GameBody>();
    Array<BlockBody> terrainLayer = new Array<BlockBody>();
    Array<FloorBody> floorLayer = new Array<FloorBody>();
    Array<ObscuringFeatureBody> obscuringFeatureLayer = new Array<ObscuringFeatureBody>();

    private long lastShaderUpdate = System.currentTimeMillis();
    protected long shaderFrameTime;

    public boolean running = false;
    long start = 0;

    GameView gameView;

    final Map map;
    final AbstractMap worldMap;

    static PlayerBody playerBody;

    Body selectedBody = null;

    GameBody[][] tiles;

    final int worldWidth;
    final int worldHeight;
    final float halfWorldWidth;
    final float halfWorldHeight;

    World world;

    static private Integer numEnemies;

    private Vector3 testPoint;
    public Vector3 translateVector;
    protected Vector2 pointPos;
    private Vector3 touchPos;
    private Vector2 aimPos;
    private float zoom;
    private RenderOperations background;
    private RenderOperations foreground;

    protected TerrainShader terrainShader;

    private float leftBorder = -Integer.MAX_VALUE;
    private float rightBorder = Integer.MAX_VALUE;
    private float topBorder = -Integer.MAX_VALUE;
    private float bottomBorder = Integer.MAX_VALUE;
    private Object userData;
    private Object userDataA;
    private Object userDataB;
    private Body bodyA;
    private Body bodyB;
    final Level level;
    private Iterator<Body> bodyIterator;
    private Array<Contact> contactList;

    public GameModel(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.halfWorldWidth = worldWidth / 2f;
        this.halfWorldHeight = worldHeight / 2f;

        if (Config.SANDBOX)
            level = new SandboxLevel();
        else
            level = LevelFactory.getInstance().getCurrentLevel();

        initVariables();

        if (world != null)
            world.dispose();

        world = new World(Level.getGravity(), true);
        world.setContactListener(new GameContactListener());

        BodyFactory.create(world);

        EffectFactory.create(world);
        LaserFactory.create(world);
        LightFactory.create(world);
        ShaderFactory.create(level.getWorldType(), level.level);

        createTiles(level);

        map = new Map(level.getMap());
        worldMap = level.getMap();

        TextFactory.getInstance().addText(new TextItem("Level " + LevelFactory.level));

        background = ResourceFactory.getInstance().getBackground(level,
                level.getBackgroundType(), Config.getDimensions().WORLD_WIDTH, Config.getDimensions().WORLD_HEIGHT,
                level.getMap().getWidth(), level.getMap().getHeight());

        foreground = ResourceFactory.getInstance().getForeground(level,
                level.getForegroundType(), Config.getDimensions().WORLD_WIDTH, Config.getDimensions().WORLD_HEIGHT,
                level.getMap().getWidth(), level.getMap().getHeight());

        createPlayer(level);
        setupPlayerLights(level);

        resetAim();
        setupTerrainShader();

        level.onStart();

        running = true;
    }

    private void setupTerrainShader() {
        if (LevelFactory.getInstance().getCurrentLevel().getWorldType() == Level.LEVEL_TYPE.UNIVERSE) {
            terrainShader = null;
            return;
        }

        if (terrainShader == null)
            terrainShader = new TerrainShader(
                    ResourceFactory.getInstance().getShaderComponentTexture("dirmask"),
                    ResourceFactory.getInstance().getShaderComponentTexture("anglemask")
            );

        if (XXXX.performance == Config.PERFORMANCE.HIGH) {
            shaderFrameTime = 25;
        } else if (XXXX.performance == Config.PERFORMANCE.MEDIUM) {
            shaderFrameTime = 50;
        } else {
            shaderFrameTime = 75;
        }
    }

    private void setupPlayerLights(Level level) {
        List<LightFactory.LIGHT_TYPE> lightTypes = level.getLightTypes();

        for (LightFactory.LIGHT_TYPE lightType : lightTypes) {
            LightFactory.getInstance().getPlayerLight(lightType).attachToBody(playerBody.getBody());
        }
    }

    private void createPlayer(Level level) {
        switch (level.getWorldType()) {
            case UNIVERSE:
                playerBody = BodyFactory.getInstance().createShipPlayer(level.getStartPosition());
                break;
            case SIDESCROLL:
                playerBody = BodyFactory.getInstance().createSidescrollPlayer(level.getStartPosition());
                break;
            case TOPDOWN:
                if (level.isInside() || level.isPlatforms())
                    playerBody = BodyFactory.getInstance().createTopDownPlayer(level.getStartPosition());
                else
                    playerBody = BodyFactory.getInstance().createTopDownPlayerVehicle(level.getStartPosition());
        }

        final Weapon weapon = LootFactory.getInstance().getWeapon(Loot.LOOT_TYPE.TONGUE);
        weapon.onPickup(playerBody);

        playerBody.getBody().setLinearVelocity(0, 0);
        playerBody.getMind().clear();
        playerBody.getMind().addBehavior(new Arrive(playerBody.position.x, playerBody.position.y, 5, Config.getDimensions().SCREEN_LONGEST, 30));
    }

    private void resetAim() {
        touchPos.set(playerBody.position.x, playerBody.position.y, 0);
        aimPos.set(playerBody.position.x, playerBody.position.y);
        pointPos.set(playerBody.position.x, playerBody.position.y);
    }

    private void initVariables() {
        zoom = 1f;
        numEnemies = 0;
        testPoint = new Vector3();
        translateVector = new Vector3();

        pointPos = new Vector2();
        touchPos = new Vector3();
        aimPos = new Vector2();
    }

    public static synchronized int getNumEnemies() {
        return numEnemies;
    }

    private void createTiles(Level level) {
        MathUtils.random.setSeed(Config.RANDOM_SEED + LevelFactory.level);

        tiles = new GameBody[level.getMap().getSizeX()][level.getMap().getSizeY()];

        final TextureAtlas.AtlasRegion indoorImage = ResourceFactory.getInstance().getWorldTypeImage(level.getIndoorImage());
        final TextureAtlas.AtlasRegion doorImage = ResourceFactory.getInstance().getItemImage(level.getDoorImage());
        final TextureAtlas.AtlasRegion corridorImage = ResourceFactory.getInstance().getWorldTypeImage(level.getCorridorImage());
        final Texture wallTexture = ResourceFactory.getInstance().getTexture(level.getWallTexture());

        final AbstractMap map = level.getMap();

        for (int x = 0; x < level.getMap().getSizeX(); x++) {
            for (int y = 0; y < level.getMap().getSizeY(); y++) {
                if (map.map2d[x][y] != UniverseMap.CLEAR && map.map2d[x][y] != UniverseMap.OUTSIDEBORDERS) {
                    tiles[x][y] = BodyFactory.getInstance().createMapTile(x, y, map.map2d[x][y], map.mapDirection[x][y],
                            indoorImage, doorImage, corridorImage, wallTexture
                    );
                    if (tiles[x][y] instanceof FeatureBody)
                        featureBodies.add((FeatureBody) tiles[x][y]);
                }

                if (map.item[x][y] != UniverseMap.CLEAR) {
                    if (map.item[x][y] == AbstractMap.ITEM_FEATURE) {
                        FeatureBody item;

                        if (map.map2d[x][y] == AbstractMap.ROOM)
                            item = BodyFactory.getInstance().createFeature(x, y, level.getRandomIndoorFeatureType());
                        else
                            item = BodyFactory.getInstance().createFeature(x, y, level.getRandomOutdoorFeatureType());

                        featureBodies.add(item);
                    } else
                        BodyFactory.getInstance().createItem(x, y, map.item[x][y], map.itemDirection[x][y]);
                }
            }
        }
    }

    public void update() {
        if (!running) return;

        if (Config.DEBUGTEXT)
            startMeasure();
        world.step(Config.BOX_STEP, Config.BOX_VELOCITY_ITERATIONS, Config.BOX_POSITION_ITERATIONS);
        if (Config.DEBUGTEXT)
            endMeasure("Stepped world");

        float deltaX = 0;
        float deltaY = 0;

        deltaX = playerBody.position.x - camera.position.x;
        deltaY = playerBody.position.y - camera.position.y;

        autopan(deltaX, deltaY);

        if (System.currentTimeMillis() > lastCleanup + updateBodiesInterval) {
            if (Config.DEBUGTEXT)
                startMeasure();
            cleanUp();
            if (Config.DEBUGTEXT)
                endMeasure("Cleaned up");
            lastCleanup = System.currentTimeMillis();
            objectsDirty = true;
        }

        if (Config.DEBUGTEXT)
            startMeasure();
        updateBodiesMapAndAI();
        if (Config.DEBUGTEXT)
            endMeasure("Updated bodies and AI");

        if (System.currentTimeMillis() > lastShaderUpdate + shaderFrameTime) {
            if (Config.DEBUGTEXT)
                startMeasure();
            ShaderFactory.getInstance().updateShaders();
            if (Config.DEBUGTEXT)
                endMeasure("Updated shaders");
            lastShaderUpdate = System.currentTimeMillis(); // Todo: Move to beginning of method?
        }

        if (Config.DEBUGTEXT)
            startMeasure();
        EffectFactory.getInstance().update(Gdx.graphics.getDeltaTime());
        if (Config.DEBUGTEXT)
            endMeasure("Updated Effects");

        if (Config.DEBUGTEXT)
            startMeasure();
        checkCollissions();
        if (Config.DEBUGTEXT)
            endMeasure("Checked collissions");

        //LightFactory.getInstance().updatePlayerLights(playerBody.getAngle(), worldMap.map2d[(int) (playerBody.position.x + Config.TILE_SIZE_X)][(int) (playerBody.position.y + Config.TILE_SIZE_Y)]);
    }

    private void startMeasure() {
        start = System.currentTimeMillis();
    }

    private void endMeasure(String item) {
        if (System.currentTimeMillis() - start > Config.DEGUG_THRESHOLD_MILLIS)
            System.out.println(item + ": " + (System.currentTimeMillis() - start));
    }

    private void updateBodiesMapAndAI() {
        if (objectsDirty) {
            updateBorders();

            map.init();

            nonMovingBodies.clear();
            movingBodies.clear();
            terrainLayer.clear();
            floorLayer.clear();
            featureLayer.clear();
            shadedLayer.clear();
            obscuringShadedLayer.clear();
            obscuringFeatureLayer.clear();

            ShaderFactory.getInstance().clearActive();

            worldBodies = BodyFactory.getInstance().update();

            if (Config.DEBUGTEXT)
                System.out.println("worldBodies.size = " + worldBodies.size);

            bodyIterator = worldBodies.iterator();

            Body currentBody;
            GameBody gameBody;

            int totalNumAgents = 0;

            while (bodyIterator.hasNext()) {
                currentBody = bodyIterator.next();
                if (currentBody == null)
                    continue; // WHY IS THIS NECESSARY!?

                userData = currentBody.getUserData();

                if (userData != null) {
                    gameBody = ((GameBody) userData);

                    if (gameBody instanceof AgentBody)
                        totalNumAgents++;

                    activateDeactivateDisableBody(gameBody);

                    if (gameBody.isActive()) {
                        if (gameBody instanceof BlockBody)
                            terrainLayer.add((BlockBody) gameBody);
                        else if (gameBody instanceof PlatformBody && !((PlatformBody) gameBody).moving)
                            nonMovingBodies.add(gameBody);
                        else if (gameBody instanceof ChasmBody)
                            nonMovingBodies.add(gameBody);
                        else if (gameBody instanceof LampBody)
                            nonMovingBodies.add(gameBody);
                        else if (gameBody instanceof ShadedBody) {
                            if (gameBody instanceof ObscuringShadedBody)
                                obscuringShadedLayer.add((ShadedBody) gameBody);
                            else {
                                shadedLayer.add((ShadedBody) gameBody);
                                if (gameBody instanceof ExitBody)
                                    movingBodies.add(gameBody);
                            }
                            ShaderFactory.getInstance().addActiveShader(((ShadedBody) gameBody).getShader());
                        } else
                            movingBodies.add(gameBody);
                    }
                }
            }

            for (FeatureBody body : featureBodies) {
                boolean active = !isOffScreen(body,
                        body.getActiveDistance() + Config.TILE_SIZE_X, body.getActiveDistance() + Config.TILE_SIZE_Y);
                body.setActive(active);

                if (active) {
                    if (body instanceof ObscuringFeatureBody)
                        obscuringFeatureLayer.add((ObscuringFeatureBody) body);
                    else if (body instanceof FloorBody)
                        floorLayer.add((FloorBody) body);
                    else
                        featureLayer.add(body);
                }
            }

            objectsDirty = false;
            numEnemies = totalNumAgents - 1; // The player is also an agent...
        }

        backLayer.clear();
        mediumLayer.clear();
        frontLayer.clear();

        int numActiveEnemies = 0;
        int numActiveBodies = 0;

        Iterator<GameBody> iterator = movingBodies.iterator();
        GameBody gameBody;

        while (iterator.hasNext()) {
            gameBody = iterator.next();
            numActiveBodies++;
            gameBody.update();

            if (gameBody.getDrawLayer() == GameBody.DRAW_LAYER.BACK)
                backLayer.add(gameBody);
            else if (gameBody.getDrawLayer() == GameBody.DRAW_LAYER.MEDIUM)
                mediumLayer.add(gameBody);
            else if (gameBody.getDrawLayer() == GameBody.DRAW_LAYER.FRONT)
                frontLayer.add(gameBody);

            if (gameBody instanceof PlayerBody) {
                float impulse = playerBody.getImpulse();
                if (impulse >= 100.0f)
                    ((PlayerBody) gameBody).collide((int) impulse);
                map.spot(gameBody, Color.GREEN);
                //fireBullet(); // Make it no-autofire...
            } else if (gameBody instanceof AgentBody) {
                numActiveEnemies++;
                map.spot(gameBody, Color.RED);
            } else if (gameBody instanceof DangerBody) {
                map.spot(gameBody, Color.ORANGE);
            } else if (gameBody instanceof LootBody) {
                map.spot(gameBody, Color.YELLOW);
            } else if (gameBody instanceof KeyBody) {
                map.spot(gameBody, Color.MAGENTA);
            }

            if (Level.getInstance().isPlatforms() && gameBody instanceof AgentBody) {
                if (worldMap.map2d[(int) (gameBody.position.x + Config.TILE_SIZE_X)][(int) (gameBody.position.y + Config.TILE_SIZE_Y)] == AbstractMap.CLEAR
                        || worldMap.map2d[(int) (gameBody.position.x + Config.TILE_SIZE_X)][(int) (gameBody.position.y + Config.TILE_SIZE_Y)] == AbstractMap.PLATFORM)
                    if (((AgentBody) gameBody).getNumPlatforms() == 0) {
                        ((AgentBody) gameBody).fell();
                    }
            }
        }

        if (Config.DEBUGTEXT) {
            System.out.println("numActiveBodies = " + numActiveBodies);
            System.out.println("numActiveEnemies = " + numActiveEnemies);
            System.out.println("numEnemies = " + numEnemies);
        }
    }

    private void activateDeactivateDisableBody(GameBody gameBody) {
        if (gameBody instanceof ParticleBody) return;
        if (gameBody instanceof PlayerBody) return;

        boolean disabled = isOffScreen(gameBody, gameBody.getActiveDistance() + Config.TILE_SIZE_X * 2, gameBody.getActiveDistance() + Config.TILE_SIZE_Y * 2);

        gameBody.setDisabled(disabled);
        gameBody.setActive(!disabled);

        if (disabled && gameBody instanceof AgentBody) {
            boolean delete = isOffScreen(gameBody, gameBody.getActiveDistance() + Config.TILE_SIZE_X * 4, gameBody.getActiveDistance() + Config.TILE_SIZE_Y * 4);
            if (delete) ((AgentBody) gameBody).offScreen();
        }

        if (disabled) return;

        boolean active = !isOffScreen(gameBody, gameBody.getActiveDistance(), gameBody.getActiveDistance());
        gameBody.setActive(active);
    }

    private boolean isOffScreen(GameBody gameBody, float measureSizeX, float measureSizeY) {
        if (level.getWorldType().equals(Level.LEVEL_TYPE.UNIVERSE) && gameBody instanceof AgentBody) {
            measureSizeX *= 2;
            measureSizeY *= 2;
        }

        testPoint.set(gameBody.position.x, gameBody.position.y, 0);

        return testPoint.x + measureSizeX < leftBorder
                || testPoint.x - measureSizeX > rightBorder
                || testPoint.y + measureSizeY < topBorder
                || testPoint.y - measureSizeY > bottomBorder;
    }

    public void cleanUp() {
        if (Config.DEBUGTEXT)
            startMeasure();
        BodyFactory.getInstance().cleanUp();
        EffectFactory.getInstance().cleanUp();
        LaserFactory.getInstance().cleanUp();
        if (Config.DEBUGTEXT)
            endMeasure("Cleaned up effects and lasers");
    }

    private void checkCollissions() {
        if (!world.isLocked()) {
            if (world.getContactCount() > 0) {
                if (Config.DEBUGTEXT) {
                    System.out.println("Number of contacts: " + world.getContactCount());
                }

                contactList = world.getContactList();

                for (Contact contact : contactList) {
                    if (!contact.isTouching()) continue;

                    if (contact.getFixtureA() == null || contact.getFixtureB() == null) return;

                    bodyA = contact.getFixtureA().getBody();
                    bodyB = contact.getFixtureB().getBody();

                    if (bodyA == bodyB) continue;

                    userDataA = bodyA.getUserData();
                    userDataB = bodyB.getUserData();

                    if (userDataA instanceof DiscardBody || userDataB instanceof DiscardBody)
                        continue;

                    if (userDataA instanceof GameBody
                            && userDataB instanceof GameBody) {
                        ((GameBody) userDataA).collidedWith((GameBody) userDataB);
                    }

                    if (userDataB instanceof GameBody
                            && userDataA instanceof GameBody) {
                        ((GameBody) userDataB).collidedWith((GameBody) userDataA);
                    }
                }
            }
        }
    }

    private void deleteBlock(Level level, BlockBody tile, boolean preserveMaze) {
        if (!preserveMaze)
            level.getMap().map2d[tile.getX()][tile.getY()] = UniverseMap.CLEAR;
        tiles[tile.getX()][tile.getY()] = null;
        tile.delete();
    }

    private void afterGameOver() {
        XXXX.changeScreen(XXXX.SCREEN.STARTMENU);
    }

    private void afterLevelWin() {
        XXXX.changeScreen(XXXX.SCREEN.STARTMENU);
    }

    public void longpress(float x, float y) {
        if (playerBody.isDead()) {
            if(System.currentTimeMillis() - 5000 > playerBody.timeOfDeath)
                afterGameOver();
            else
                return;
        }

        touchPos.set(x, y, 0);
        camera.unproject(touchPos);
        aimPos.set(touchPos.x, touchPos.y);
        playerBody.fireBullet2(aimPos);
    }

    public void touch(float x, float y, int button) {
        lastTap = System.currentTimeMillis();

        if (playerBody.isDead()) {
            if(System.currentTimeMillis() - 3000 > playerBody.timeOfDeath)
                afterGameOver();
            else
                return;
        }

        touchPos.set(x, y, 0);
        camera.unproject(touchPos);
        aimPos.set(touchPos.x, touchPos.y);

        if (button == 0)
            playerBody.fireBullet1(aimPos);
        else if (button == 1)
            playerBody.fireBullet2(aimPos);

/*
        final Body body = selectBody();
        if(body != null) {
            final Object userData = body.getUserData();
            if (userData instanceof BlockBody)
                System.out.println("body = " + ((BlockBody) this.userData).toString());
        }
*/

/*
        if (Level.worldType == Level.LEVEL_TYPE.TOPDOWN)
            playerBody.setAngle(playerBody.getFiringAngle());
*/
    }

    public void dispose() {
        map.dispose();
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public World getWorld() {
        return world;
    }

    public PlayerBody getPlayerBody() {
        return playerBody;
    }

    public void panStop(float x, float y) {
        playerBody.scheduleJump(playerBody.position.x - pointPos.x, playerBody.position.y - pointPos.y);
        pointPos.set(playerBody.position.x, playerBody.position.y);
        panning = false;
    }

    public void panPointPos(int x, int y, int deltaX, int deltaY) {      // Delta is in pixels
        if (!panning) {
            pointPos.set(playerBody.position.x, playerBody.position.y);
        }

        panning = true;

        playerBody.removeAllKeys();
        playerBody.getMind().clear();
/*
        playerBody.getMind().addBehavior(new Arrive(pointPos, 5, Config.getDimensions().SCREEN_LONGEST, 30f));
*/
        //playerBody.setControlMode(PlayerBody.CONTROL_MODE.DIRECT); // For when on mobile without keys

        pointPos.add(deltaX * Config.getDimensions().WORLD_ON_SCREEN_FACTOR, -deltaY * Config.getDimensions().WORLD_ON_SCREEN_FACTOR);
/*
        if (XXXX.aimMode.equals(XXXX.AIM_MODE.DIRECTION)) {
            touchPos.set(pointPos.x, pointPos.y, 0);
            playerBody.setAim(touchPos.x, touchPos.y);
        }
*/
    }

    public void autopan(float deltaX, float deltaY) {      // Delta is in pixels
        translateVector.set(deltaX, deltaY, 0);
        camera.translate(translateVector.x, translateVector.y, 0);
        gameView.updateCamera();
        worldMap.pan(translateVector.x, translateVector.y);
        background.update(camera.position.x, camera.position.y, translateVector.x, translateVector.y, translateVector.x, translateVector.y);
        foreground.update(camera.position.x, camera.position.y, translateVector.x, translateVector.y, translateVector.x, translateVector.y);
    }

    private void updateBorders() {
        if (Config.DEBUG) {
            leftBorder = camera.position.x - halfWorldWidth / Config.DEBUG_SCALE;
            rightBorder = camera.position.x + halfWorldWidth / Config.DEBUG_SCALE;
            topBorder = camera.position.y - halfWorldHeight / Config.DEBUG_SCALE;
            bottomBorder = camera.position.y + halfWorldHeight / Config.DEBUG_SCALE;
        } else {
            leftBorder = camera.position.x - halfWorldWidth;
            rightBorder = camera.position.x + halfWorldWidth;
            topBorder = camera.position.y - halfWorldHeight;
            bottomBorder = camera.position.y + halfWorldHeight;
        }
    }

    private Body selectBody() {
        selectedBody = null;

        getWorld().QueryAABB(
                new QueryCallback() {
                    public boolean reportFixture(Fixture fixture) {
                        Body body = fixture.getBody();

                        if (fixture.testPoint(new Vector2(touchPos.x, touchPos.y)) && selectedBody == null) {
                            selectedBody = body;
                            return true;
                        }

                        return false;
                    }
                }, touchPos.x - .001f, touchPos.y - .001f, touchPos.x + .001f, touchPos.y + .001f);

        return selectedBody;
    }

    public void zoom(float distance) {
        if (distance > 0) zoom -= .01f;
        else zoom += .01f;

        if (zoom < .25f) zoom = .25f;
        if (zoom > 1f) zoom = 1f;

        gameView.updateCamera();
    }

    public float getZoom() {
        return zoom;
    }

    public Vector3 getTouchPos() {
        return touchPos;
    }

    public Vector2 getPointPos() {
        return pointPos;
    }

    public int getHealth() {
        return playerBody.getHealth();
    }

    public int getScore() {
        return playerBody.getScore();
    }

    public int getOrbs() {
        return playerBody.getOrbs();
    }

    public int getKeys() {
        return playerBody.getKeys();
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public Map getMap() {
        return map;
    }

    public RenderOperations getBackground() {
        return background;
    }

    public RenderOperations getForeground() {
        return foreground;
    }

    public static PlayerBody getPlayer() {
        return playerBody;
    }

    public AbstractMap getTileMap() {
        return worldMap;
    }

    public Array<LaserBody> getLasers() {
        return LaserFactory.getInstance().getLasers();
    }

    public Array<GameBody> getFeatureLayer() {
        return featureLayer;
    }

    public Array<GameBody> getMediumLayer() {
        return mediumLayer;
    }

    public Array<BlockBody> getTerrainLayer() {
        return terrainLayer;
    }

    public Array<FloorBody> getFloorLayer() {
        return floorLayer;
    }

    public Array<ShadedBody> getShadedLayer() {
        return shadedLayer;
    }

    public Array<ShadedBody> getObscuringShadedLayer() {
        return obscuringShadedLayer;
    }

    public Array<GameBody> getFrontLayer() {
        return frontLayer;
    }

    public Array<GameBody> getBackLayer() {
        return backLayer;
    }

    public Array<ObscuringFeatureBody> getObscuringFeatureLayer() {
        return obscuringFeatureLayer;
    }

    public TerrainShader getTerrainShader() {
        return terrainShader;
    }

    public Array<Weapon> getPlayerWeapons() {
        return playerBody.getWeapons();
    }

    public Array<GameBody> getNonMovingBodies() {
        return nonMovingBodies;
    }

    public void setWeaponIndex(int weaponIndex) {
        playerBody.setWeaponIndex(weaponIndex);
    }

    public void nextWeapon() {
        playerBody.nextWeapon();
    }

    public void prevWeapon() {
        playerBody.prevWeapon();
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public static void onEvent(GameEvent.EVENT_TYPE killed, GameBody agentBody) {
        playerBody.onEvent(killed, agentBody);
    }

    public long getLastTap() {
        return lastTap;
    }

    public void addKeyPress(int i) {
        panning = false;
        getPlayer().addKeyPress(i);
    }

    public void removeKeyPress(int i) {
        getPlayer().removeKeyPress(i);
    }

}
