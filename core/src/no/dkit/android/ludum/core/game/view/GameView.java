package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.LevelFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.body.scenery.BlockBody;
import no.dkit.android.ludum.core.game.model.body.scenery.FloorBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ObscuringFeatureBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ShadedBody;
import no.dkit.android.ludum.core.game.model.body.weapon.LaserBody;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

import java.util.Random;

public class GameView {
    int fpsCounter = 0;
    long start;
    float globalRotation = 0;

    GameModel gameModel;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    Box2DDebugRenderer worldRenderer;

    TextureRegion crosshairImage;
    TextureRegion targetImage;

    Array<LaserBody> lasers;

    Camera camera;

    float time = 1f;
    float angle = 0f;
    private Vector2 tmpVector = new Vector2();

    public GameView(GameModel gameModel) {
        this.gameModel = gameModel;
        this.gameModel.setGameView(this);

        camera = new OrthographicCamera();
        ((OrthographicCamera) camera).setToOrtho(false, gameModel.getWorldWidth(), gameModel.getWorldHeight());
        ((OrthographicCamera) camera).zoom = 1f;

        if (Config.DEBUG) {
            camera.viewportWidth = gameModel.getWorldWidth() * Config.DEBUG_SCALE;
            camera.viewportHeight = gameModel.getWorldHeight() * Config.DEBUG_SCALE;
        }

        camera.update();

        MathUtils.random.setSeed(Config.RANDOM_SEED + LevelFactory.level);

        if (Config.DEBUG)
            worldRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);

        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);

        shapeRenderer = new ShapeRenderer(20); // Max 10 vertixes
        shapeRenderer.setProjectionMatrix(camera.combined);

        lasers = gameModel.getLasers();

        crosshairImage = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "crosshair");
        targetImage = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "target");

        gameModel.setCamera(camera);
    }

    public void update() {
        updateCamera();

        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        drawImages();
        //drawShapes();

        if (Config.DEBUGTEXT)
            startMeasure();
        spriteBatch.begin();
        for (LaserBody laserBody : lasers) {
            laserBody.draw(spriteBatch, ResourceFactory.getInstance().getItemImage("laserglow"),
                    ResourceFactory.getInstance().getItemImage("laserbeam"),
                    ResourceFactory.getInstance().getItemImage("laserend"),
                    ResourceFactory.getInstance().getItemImage("laserendglow")
            );
        }

        spriteBatch.end();
        if (Config.DEBUGTEXT)
            endMeasure("Lasers");

        spriteBatch.begin();
        drawCrosshairs();
        spriteBatch.end();

        if (Config.DEBUGTEXT)
            startMeasure();

        if (Config.DEBUG)
            worldRenderer.render(gameModel.getWorld(), camera.combined);

        if (Config.DEBUGTEXT)
            endMeasure("Debug");

        drawText();

        globalRotation += 1f;
        if (globalRotation > 360) globalRotation = 0;
    }

    private void drawLights() {
        if (Config.DEBUGTEXT)
            startMeasure();
        LightFactory.getInstance().render(camera.combined,
                gameModel.getWorldWidth() / 2, gameModel.getWorldHeight() / 2, gameModel.getTileMap().getSizeX(), gameModel.getTileMap().getSizeX());
        if (Config.DEBUGTEXT)
            endMeasure("Lights");
    }

    private void drawShapes() {
        Array<JointEdge> jointList = gameModel.getPlayerBody().getBody().getJointList();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.translate(-camera.position.x + Config.getDimensions().WORLD_WIDTH / 2f, -camera.position.y + Config.getDimensions().WORLD_HEIGHT / 2f, 0);
        for (JointEdge jointEdge : jointList) {
            shapeRenderer.line(
                    jointEdge.joint.getAnchorA().x,
                    jointEdge.joint.getAnchorA().y,
                    jointEdge.joint.getAnchorB().x,
                    jointEdge.joint.getAnchorB().y);
        }
        shapeRenderer.translate(camera.position.x - Config.getDimensions().WORLD_WIDTH / 2f, camera.position.y - Config.getDimensions().WORLD_HEIGHT / 2f, 0);
        shapeRenderer.end();
    }

    private void drawText() {
        float oldWidth = camera.viewportWidth;
        float oldHeight = camera.viewportHeight;

        camera.viewportWidth = Config.getDimensions().SCREEN_LONGEST;
        camera.viewportHeight = Config.getDimensions().SCREEN_SHORTEST;
        //camera.translate(Config.getDimensions().SCREEN_LONGEST/2,Config.getDimensions().SCREEN_SHORTEST/2, 0f);
        //camera.rotate(90f, 0, 0, 1);

        updateCamera();

        TextFactory.getInstance().drawText(spriteBatch);

        //camera.translate(-Config.getDimensions().SCREEN_LONGEST/2,-Config.getDimensions().SCREEN_SHORTEST/2, 0f);
        //camera.rotate(-90f, 0, 0, 1);
        camera.viewportWidth = oldWidth;
        camera.viewportHeight = oldHeight;

        updateCamera();
    }

    public void updateCamera() {
        ((OrthographicCamera) camera).zoom = gameModel.getZoom();
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    private void drawImages() {
        fpsCounter++;
        if (fpsCounter == 100) {
            if (Config.DEBUGTEXT)
                System.out.println("FPS:" + Gdx.graphics.getFramesPerSecond());
            fpsCounter = 0;
        }

        gameModel.getBackground().render(spriteBatch,
                camera.position.x - Config.getDimensions().WORLD_WIDTH / 2f,
                camera.position.y - Config.getDimensions().WORLD_HEIGHT / 2f,
                Config.getDimensions().WORLD_WIDTH, Config.getDimensions().WORLD_HEIGHT);

        drawLights();

        spriteBatch.begin();
        drawSpriteLayers();
        spriteBatch.end();

        if (Config.DEBUGTEXT)
            startMeasure();
        spriteBatch.begin();
        EffectFactory.getInstance().drawEffects(spriteBatch, GameBody.DRAW_LAYER.FRONT);
        spriteBatch.end();
        if (Config.DEBUGTEXT)
            endMeasure("Draw Effects on FRONT layer");

        spriteBatch.begin();
        drawObscuringLayer(gameModel.getObscuringFeatureLayer());
        drawShadedLayer(gameModel.getObscuringShadedLayer());
        spriteBatch.end();

        gameModel.getForeground().render(spriteBatch,
                camera.position.x - Config.getDimensions().WORLD_WIDTH / 2f,
                camera.position.y - Config.getDimensions().WORLD_HEIGHT / 2f,
                Config.getDimensions().WORLD_WIDTH, Config.getDimensions().WORLD_HEIGHT);
    }

    private void drawPlayer() {
        if (Config.DEBUGTEXT)
            startMeasure();
        gameModel.getPlayerBody().draw(spriteBatch);
        if (Config.DEBUGTEXT)
            endMeasure("Player");
    }

    // TODO: Move these to model to save on iterations
    private void drawSpriteLayers() {
        spriteBatch.enableBlending();

        drawFloorLayer(gameModel.getFloorLayer());

        if (Config.DEBUGTEXT)
            startMeasure();
        drawShadedLayer(gameModel.getShadedLayer());
        if (Config.DEBUGTEXT)
            endMeasure("Shaded layers");

        drawFeatureLayer(gameModel.getFeatureLayer());

        drawLayer(gameModel.getNonMovingBodies());   // Why so many? Sjekk om aktive...

        drawLayer(gameModel.getBackLayer());

        if (Config.DEBUGTEXT)
            startMeasure();
        drawTerrainLayer(gameModel.getTerrainLayer());  // Why so many? Sjekk om aktive...
        if (Config.DEBUGTEXT)
            endMeasure("Terrain layers");

        if (Config.DEBUGTEXT)
            startMeasure();
        EffectFactory.getInstance().drawEffects(spriteBatch, GameBody.DRAW_LAYER.BACK);
        if (Config.DEBUGTEXT)
            endMeasure("Draw Effects on BACK layer");

        drawLayer(gameModel.getMediumLayer());

        drawPlayer();

        drawLayer(gameModel.getFrontLayer());
    }

    private void drawLayer(Array<GameBody> layer) {
        if (layer.size == 0) return;

        for (GameBody gameBody : layer) {
            if (gameBody instanceof PlayerBody) continue;
            gameBody.draw(spriteBatch);
        }
    }

    private void drawObscuringLayer(Array<ObscuringFeatureBody> layer) {
        if (layer.size == 0) return;

        for (ObscuringFeatureBody gameBody : layer) {
            gameBody.draw(spriteBatch);
        }
    }

    private void drawTerrainLayer(Array<BlockBody> layer) {
        if (layer.size == 0 || gameModel.getTerrainShader() == null) return;

        spriteBatch.end();

        spriteBatch.setShader(gameModel.getTerrainShader().getShader());

        gameModel.getTerrainShader().setUseAngle(true);

        spriteBatch.begin();

        for (BlockBody body : layer) {
            gameModel.getTerrainShader().bind(body.getTexture());
            if (body.getDirection() == AbstractMap.NE || body.getDirection() == AbstractMap.NW || body.getDirection() == AbstractMap.SE || body.getDirection() == AbstractMap.SW) {
                body.draw(spriteBatch, gameModel.getTerrainShader().getTexture());
            }
        }

        spriteBatch.end();

        gameModel.getTerrainShader().setUseAngle(false);

        spriteBatch.begin();

        for (BlockBody body : layer) {
            gameModel.getTerrainShader().bind(body.getTexture());
            if (body.getDirection() == AbstractMap.E || body.getDirection() == AbstractMap.W || body.getDirection() == AbstractMap.S || body.getDirection() == AbstractMap.N) {
                body.draw(spriteBatch, gameModel.getTerrainShader().getTexture());
            }
        }

        spriteBatch.end();

        spriteBatch.setShader(null);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);

        spriteBatch.begin();

        for (BlockBody body : layer) {
            if (body.getDirection() == AbstractMap.NO_DIRECTION) {
                body.draw(spriteBatch);
            }
        }

        spriteBatch.end();

        spriteBatch.setShader(null);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);

        spriteBatch.begin();
    }

    private void drawShadedLayer(Array<ShadedBody> layer) {
        if (layer.size == 0) return;

        spriteBatch.end();

        for (ShadedBody body : layer) {
            body.drawShaded(spriteBatch);
        }

        spriteBatch.setShader(null);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);

        spriteBatch.begin();
    }

    private void drawFeatureLayer(Array<GameBody> layer) {
        if (layer.size == 0) return;

        spriteBatch.enableBlending();

        for (GameBody body : layer) {
            body.draw(spriteBatch);
        }
    }

    private void drawFloorLayer(Array<FloorBody> layer) {
        if (layer.size == 0) return;

        spriteBatch.disableBlending();

        for (FloorBody body : layer) {
            body.draw(spriteBatch);
        }

        spriteBatch.enableBlending();
    }

    private void drawCrosshairs() {
        if (gameModel.panning) {
            drawJumpArrow(spriteBatch,
                    ResourceFactory.getInstance().getItemImage("laserglow"),
                    ResourceFactory.getInstance().getItemImage("laserbeam"),
                    ResourceFactory.getInstance().getItemImage("laserend"),
                    ResourceFactory.getInstance().getItemImage("laserendglow")
            );
        }

        long a = System.currentTimeMillis() - gameModel.getLastTap();
        if (a > 255) a = 255;

        spriteBatch.setColor(255, 0, 0, a);
        spriteBatch.draw(targetImage,
                gameModel.getTouchPos().x - Config.TILE_SIZE_X / 2, gameModel.getTouchPos().y - Config.TILE_SIZE_Y / 2,
                Config.TILE_SIZE_X / 2, Config.TILE_SIZE_Y / 2,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                1, 1,
                globalRotation,
                true);

        spriteBatch.setColor(Color.WHITE);
    }

    private void drawJumpArrow(SpriteBatch spriteBatch, TextureRegion glow, TextureRegion beam, TextureRegion end, TextureRegion endGlow) {
        Color glowColor = Color.RED;
        Color beamColor = Color.WHITE;

        Vector2 firingDirection = new Vector2();
        firingDirection.set(gameModel.getPointPos().x - gameModel.getPlayerBody().position.x, gameModel.getPointPos().y - gameModel.getPlayerBody().position.y);
        float firingAngle = (MathUtils.radiansToDegrees * MathUtils.atan2(firingDirection.y, firingDirection.x)) + 90f;

        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        //glowColor.lerp(Color.CLEAR, .1f);
        spriteBatch.setColor(glowColor);

        spriteBatch.draw(endGlow,
                gameModel.getPointPos().x - Config.TILE_SIZE_X, gameModel.getPointPos().y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .2f, .2f,
                0,
                true);

        spriteBatch.draw(endGlow,
                gameModel.getPlayerBody().position.x - Config.TILE_SIZE_X, gameModel.getPlayerBody().position.y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, .1f,
                0,
                true);

        spriteBatch.draw(glow,
                gameModel.getPlayerBody().position.x - Config.TILE_SIZE_X, gameModel.getPlayerBody().position.y,
                Config.TILE_SIZE_X, 0,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, firingDirection.len(),
                firingAngle,
                true);

        //beamColor.lerp(Color.CLEAR, .2f);
        spriteBatch.setColor(beamColor);

        spriteBatch.draw(end,
                gameModel.getPointPos().x - Config.TILE_SIZE_X, gameModel.getPointPos().y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, .1f,
                0,
                true);

        spriteBatch.draw(end,
                gameModel.getPlayerBody().position.x - Config.TILE_SIZE_X, gameModel.getPlayerBody().position.y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, .1f,
                0,
                true);

        spriteBatch.draw(end,
                firingDirection.x - Config.TILE_SIZE_X, firingDirection.y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, .1f,
                0,
                true);

        spriteBatch.draw(beam,
                gameModel.getPlayerBody().position.x - Config.TILE_SIZE_X, gameModel.getPlayerBody().position.y,
                Config.TILE_SIZE_X, 0,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, firingDirection.len(),
                firingAngle,
                true);

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void dispose() {
        spriteBatch.dispose();
        if (Config.DEBUG)
            worldRenderer.dispose();
    }

    private void startMeasure() {
        if (Config.DEBUGTEXT)
            start = System.currentTimeMillis();
    }

    private void endMeasure(String item) {
        if (System.currentTimeMillis() - start > Config.DEGUG_THRESHOLD_MILLIS)
            System.out.println(item + ": " + (System.currentTimeMillis() - start));
    }

    public Camera getCamera() {
        return camera;
    }
}
