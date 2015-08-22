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
import com.badlogic.gdx.math.Matrix4;
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
import no.dkit.android.ludum.core.game.model.body.scenery.BlockBody;
import no.dkit.android.ludum.core.game.model.body.scenery.FloorBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ObscuringFeatureBody;
import no.dkit.android.ludum.core.game.model.body.scenery.ShadedBody;
import no.dkit.android.ludum.core.game.model.body.weapon.LaserBody;
import no.dkit.android.ludum.core.game.model.world.level.Level;
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

    Decals decals;

    Camera camera;

    float time = 1f;
    float angle = 0f;
    private Matrix4 newMatrix;
    private Matrix4 prevMatrix;
    private float factorX;
    private float factorY;

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

        MathUtils.random = new Random(Config.RANDOM_SEED + LevelFactory.level);

        if (Config.DEBUG)
            worldRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);

        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);

        shapeRenderer = new ShapeRenderer(20); // Max 10 vertixes
        shapeRenderer.setProjectionMatrix(camera.combined);

        lasers = gameModel.getLasers();

        crosshairImage = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "crosshair");
        targetImage = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "target");

        decals = new Decals(Level.getInstance().getMap().getWidth(), Level.getInstance().getMap().getHeight());
        gameModel.setCamera(camera);

        factorX = (float) Config.getDimensions().WORLD_WIDTH / (float) Level.getInstance().getMap().getWidth();
        factorY = (float) Config.getDimensions().WORLD_HEIGHT / (float) Level.getInstance().getMap().getHeight();
    }

    public void update() {
        updateCamera();

        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        drawImages();
        drawShapes();

        if (Config.DEBUGTEXT)
            startMeasure();
        LightFactory.getInstance().render(camera.combined,
                gameModel.getWorldWidth() / 2, gameModel.getWorldHeight() / 2, gameModel.getTileMap().getSizeX(), gameModel.getTileMap().getSizeX());
        if (Config.DEBUGTEXT)
            endMeasure("Lights");

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
            System.out.println("FPS:" + Gdx.graphics.getFramesPerSecond());
            fpsCounter = 0;
        }

        gameModel.getBackground().render(spriteBatch,
                camera.position.x - Config.getDimensions().WORLD_WIDTH / 2f,
                camera.position.y - Config.getDimensions().WORLD_HEIGHT / 2f,
                Config.getDimensions().WORLD_WIDTH, Config.getDimensions().WORLD_HEIGHT);

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
    }

    // TODO: Move these to model to save on iterations
    private void drawSpriteLayers() {
        spriteBatch.disableBlending();
        drawFloorLayer(gameModel.getFloorLayer());

        spriteBatch.enableBlending();

        if (Config.DEBUGTEXT)
            startMeasure();
        drawShadedLayer(gameModel.getShadedLayer());
        if (Config.DEBUGTEXT)
            endMeasure("Shaded layers");

        spriteBatch.enableBlending();

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

        spriteBatch.end();

        decals.record();
        spriteBatch.begin();
        spriteBatch.enableBlending();
        scaleToDecals();
        EffectFactory.getInstance().drawEffects(spriteBatch, GameBody.DRAW_LAYER.BACK);
        scaleBack();
        spriteBatch.end();
        decals.end();

        if (Config.DEBUGTEXT)
            endMeasure("Draw Effects on BACK layer");

        spriteBatch.begin();
        spriteBatch.enableBlending();

        decals.render(spriteBatch, camera.position.x, camera.position.y, gameModel.translateVector.x, gameModel.translateVector.y);

        spriteBatch.end();

        spriteBatch.begin();

        drawLayer(gameModel.getMediumLayer());
        drawLayer(gameModel.getFrontLayer());

        spriteBatch.end();

        spriteBatch.begin();
    }

    private void scaleBack() {
        spriteBatch.setTransformMatrix(prevMatrix);
    }

    private void scaleToDecals() {
        prevMatrix = spriteBatch.getTransformMatrix().cpy();
        newMatrix = spriteBatch.getTransformMatrix().cpy();

        newMatrix.setToTranslationAndScaling(camera.position.x - Config.getDimensions().WORLD_WIDTH / 2f, camera.position.y - Config.getDimensions().WORLD_HEIGHT / 2f, 0,
                factorX, factorY, 0);

        spriteBatch.setTransformMatrix(newMatrix);
    }

    private void drawLayer(Array<GameBody> layer) {
        if (layer.size == 0) return;

        for (GameBody gameBody : layer) {
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

        for (GameBody body : layer) {
            body.draw(spriteBatch);
        }
    }

    private void drawFloorLayer(Array<FloorBody> layer) {
        if (layer.size == 0) return;

        for (FloorBody body : layer) {
            body.draw(spriteBatch);
        }
    }

    private void drawCrosshairs() {
        if (gameModel.panning) {
            spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            spriteBatch.setColor(255, 255, 255, 128);

            spriteBatch.draw(crosshairImage,
                    gameModel.getPointPos().x - Config.TILE_SIZE_X / 2, gameModel.getPointPos().y - Config.TILE_SIZE_Y / 2,
                    Config.TILE_SIZE_X / 2, Config.TILE_SIZE_Y / 2,
                    Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                    1, 1,
                    -globalRotation,
                    true);
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

        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.setColor(Color.WHITE);
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
