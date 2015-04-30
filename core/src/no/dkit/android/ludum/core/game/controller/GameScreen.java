package no.dkit.android.ludum.core.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LevelFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.view.GameView;

public class GameScreen implements Screen, InputProcessor {
    public static final String TOGGLE = "toggle";
    public static final String TRANSPARENT = "transparent";
    public static final String WEAPONWINDOW = "WEAPONWINDOW";

    GameModel gameModel;
    GameView gameView;
    GestureDetector processor;
    InputMultiplexer multiplexer;
    Stage stage; // UI

    Label fpsLabel;
    Label fps;

    Image healthImage;
    Image tongueImage;
    Image keysImage;
    Label health;
    Label orb;
    Label keys;

    StringBuilder fpsBuilder = new StringBuilder();
    StringBuilder healthBuilder = new StringBuilder();
    StringBuilder orbBuilder = new StringBuilder();
    StringBuilder keysBuilder = new StringBuilder();

    long lastUpdateTime = System.currentTimeMillis();
    long lastTextUpdate = System.currentTimeMillis();

    int totalX = 0;
    int totalY = 0;

    int[] keysToMonitor = new int[]{    // TODO: Get this from PlayerBody
            Input.Keys.W,
            Input.Keys.S,
            Input.Keys.A,
            Input.Keys.D,
            Input.Keys.SPACE,
            Input.Keys.SHIFT_LEFT
    };

//    private static ImageButton mapButton;
    private static Button travelSidescrollButton;
    private static Button noTravelSidescrollButton;
    private static Button travelUniverseButton;
    private static Button travelTopdownButton;

    protected final Skin skin;

    private OrthographicCamera camera;

    // constructor to keep a reference to the main Game class
    public GameScreen() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        gameModel = new GameModel(Config.getDimensions().WORLD_WIDTH, Config.getDimensions().WORLD_HEIGHT);
        gameView = new GameView(gameModel);

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        MyGestureListener listener = new MyGestureListener();

        processor = new GestureDetector(listener);
        processor.setLongPressSeconds(.5f);

        stage = new Stage(new ScreenViewport(camera));

        skin = new Skin(Gdx.files.internal("uiskin.json"));

/*
        mapButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(gameModel.getMap().getImage()))) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                gameModel.getMap().getImage();
                super.draw(batch, .5f);
            }
        };
*/

        travelSidescrollButton = new Button(new Image(ResourceFactory.getInstance().getImage(ResourceFactory.UI, "travelsidescroll")), skin, TRANSPARENT);
        travelSidescrollButton.setVisible(false);
        noTravelSidescrollButton = new Button(new Image(ResourceFactory.getInstance().getImage(ResourceFactory.UI, "notravelsidescroll")), skin, TRANSPARENT);
        noTravelSidescrollButton.setVisible(false);
        travelUniverseButton = new Button(new Image(ResourceFactory.getInstance().getImage(ResourceFactory.UI, "traveluniverse")), skin, TRANSPARENT);
        travelUniverseButton.setVisible(false);
        travelTopdownButton = new Button(new Image(ResourceFactory.getInstance().getImage(ResourceFactory.UI, "traveltopdown")), skin, TRANSPARENT);
        travelTopdownButton.setVisible(false);

/*
        mapButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                XXXX.changeScreen(XXXX.SCREEN.STARTMENU);
            }
        });
*/
        travelSidescrollButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LevelFactory.getInstance().nextLevel(Level.LEVEL_TYPE.SIDESCROLL);
                XXXX.changeLevel();
            }
        });
        travelTopdownButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LevelFactory.getInstance().nextLevel(Level.LEVEL_TYPE.TOPDOWN);
                XXXX.changeLevel();
            }
        });
        travelUniverseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LevelFactory.getInstance().nextLevel(Level.LEVEL_TYPE.UNIVERSE);
                XXXX.changeLevel();
            }
        });

        fpsLabel = new Label("FPS: ", skin, TRANSPARENT);
        fpsLabel.setAlignment(Align.right);

        healthImage = new Image(ResourceFactory.getInstance().getImage(ResourceFactory.ITEM, "medpack"));
        healthImage.setColor(Color.RED);
        tongueImage = new Image(ResourceFactory.getInstance().getImage(ResourceFactory.ITEM, "tongueweapon"));
        tongueImage.setColor(Color.RED);
        keysImage = new Image(ResourceFactory.getInstance().getImage(ResourceFactory.ITEM, "key"));

        fps = new Label("", skin, TRANSPARENT);
        fps.setAlignment(Align.right);
        health = new Label("", skin, TRANSPARENT);
        health.setAlignment(Align.right);
        orb = new Label("", skin, TRANSPARENT);
        orb.setAlignment(Align.right);
        keys = new Label("", skin, TRANSPARENT);
        keys.setAlignment(Align.right);

        Table mapWindow = new Table(skin);
        mapWindow.defaults();
        mapWindow.pad(5);
/*
        mapWindow.add(mapButton).size(100, 100);
        mapButton.getImageCell().fill().expand();
*/
        mapWindow.pack();
        mapWindow.setPosition(0, Config.getDimensions().SCREEN_HEIGHT - mapWindow.getHeight());

        stage.addActor(mapWindow);

        Table scoreWindow = new Table(skin);
        scoreWindow.defaults().align(Align.right);
        scoreWindow.defaults().pad(5).padTop(0).padBottom(0);
        scoreWindow.add(healthImage).size(Config.getDimensions().SCREEN_HEIGHT / 20);
        scoreWindow.add(health).width(Config.getDimensions().SCREEN_WIDTH / 10);
        scoreWindow.row();
        scoreWindow.add(tongueImage).size(Config.getDimensions().SCREEN_HEIGHT / 20);
        scoreWindow.add(orb).width(Config.getDimensions().SCREEN_WIDTH / 10);
        scoreWindow.row();
        scoreWindow.add(keysImage).size(Config.getDimensions().SCREEN_HEIGHT / 20);
        scoreWindow.add(keys).width(Config.getDimensions().SCREEN_WIDTH / 10);
        scoreWindow.row();
        if(Config.DEBUG) {
            scoreWindow.add(fpsLabel);
            scoreWindow.add(fps).width(Config.getDimensions().SCREEN_WIDTH / 10);
        }
        scoreWindow.pack();
        scoreWindow.setPosition(Config.getDimensions().SCREEN_WIDTH - scoreWindow.getWidth(), Config.getDimensions().SCREEN_HEIGHT - scoreWindow.getHeight());

        stage.addActor(scoreWindow);

        Table uiWindow = new Table(skin);
        uiWindow.defaults().align(Align.center);

        Stack stack = new Stack();
        uiWindow.add(stack).size(Config.getDimensions().SCREEN_WIDTH / 20);
        stack.add(noTravelSidescrollButton);
        stack.add(travelSidescrollButton);
        stack.add(travelUniverseButton);
        stack.add(travelTopdownButton);
        uiWindow.pack();
        uiWindow.setPosition(Config.getDimensions().SCREEN_WIDTH / 2 - travelSidescrollButton.getWidth() / 2, Config.getDimensions().SCREEN_HEIGHT *.60f + travelSidescrollButton.getHeight() / 2);
        stage.addActor(uiWindow);

        multiplexer = new InputMultiplexer(stage, processor, this);
        //Gdx.input = new RemoteInput();
    }

    public void render(float delta) {
        if (System.currentTimeMillis() - lastUpdateTime > (1000f / Config.FPS)) {
            lastUpdateTime = System.currentTimeMillis();
            processInput();
        }

        gameModel.update();
        gameView.update();

        if (System.currentTimeMillis() > lastTextUpdate + 1000) {
            fpsBuilder.setLength(0);
            fpsBuilder.append(Gdx.graphics.getFramesPerSecond());

            healthBuilder.setLength(0);
            healthBuilder.append(gameModel.getHealth());

            orbBuilder.setLength(0);
            orbBuilder.append(gameModel.getOrbs());

            keysBuilder.setLength(0);
            keysBuilder.append(gameModel.getKeys());

            fps.setText(fpsBuilder);
            health.setText(healthBuilder);
            orb.setText(orbBuilder);
            keys.setText(keysBuilder);

            lastTextUpdate = System.currentTimeMillis();
        }

        //stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public static void enableTravelMenu(Level.LEVEL_TYPE type) {
        noTravelSidescrollButton.setVisible(false);
        travelSidescrollButton.setVisible(false);
        travelTopdownButton.setVisible(false);
        travelUniverseButton.setVisible(false);

        switch (type) {
            case SIDESCROLL:
                if (GameModel.getPlayer().getData().getKeys() > 0) {
                    travelSidescrollButton.setVisible(true);
                } else {
                    noTravelSidescrollButton.setVisible(true);
                }

                break;
        }
    }

    public static void disableTravelMenu(Level.LEVEL_TYPE type) {
        switch (type) {
            case SIDESCROLL:
                travelSidescrollButton.setVisible(false);
                noTravelSidescrollButton.setVisible(false);
                break;
        }
    }

    public void show() {
        if (Config.music)
            SoundFactory.getInstance().playMusic(SoundFactory.MUSIC_TYPE.GAME);

        Gdx.input.setInputProcessor(multiplexer);
    }

    public void hide() {
        if (Config.music)
            SoundFactory.getInstance().stopMusic();
    }

    public void pause() {
        if (Config.music)
            SoundFactory.getInstance().stopMusic();
    }

    public void resume() {
        if (Config.music)
            SoundFactory.getInstance().playMusic(SoundFactory.MUSIC_TYPE.GAME);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void dispose() {
        gameModel.dispose();
        gameView.dispose();
        System.out.println("GameScreen disposed");
    }

    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }

        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }

    private class MyGestureListener implements GestureDetector.GestureListener {
        public boolean touchDown(float x, float y, int pointer, int button) {
            return false;
        }

        public boolean tap(float x, float y, int count, int button) {
            gameModel.touch(x, y, button);
            return true;
        }

        public boolean longPress(float x, float y) {
            gameModel.longpress(x, y);
            return true;
        }

        public boolean fling(float velocityX, float velocityY, int button) {
            return false;
        }

        public boolean pan(float x, float y, float deltaX, float deltaY) {
            float dstX = gameModel.getPointPos().x - gameView.getCamera().position.x;
            float dstY = gameModel.getPointPos().y - gameView.getCamera().position.y;

            if (Math.abs(dstX) > (gameModel.getWorldWidth()) / 2f) {
                if (dstX > 0 && deltaX > 0) deltaX = 0;
                if (dstX < 0 && deltaX < 0) deltaX = 0;
            }

            if (Math.abs(dstY) > (gameModel.getWorldHeight()) / 2f) {
                if (dstY < 0 && deltaY > 0) deltaY = 0;
                if (dstY > 0 && deltaY < 0) deltaY = 0;
            }

            totalX += deltaX;
            totalY += deltaY;

            gameModel.panPointPos((int) x, (int) y, (int) deltaX, (int) deltaY);
            return true;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            gameModel.panStop(x, y);
            return true;
        }

        public boolean zoom(float initialDistance, float distance) {
            gameModel.zoom((distance - initialDistance) / 10000f);
            return true;
        }

        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }
    }

    private void processInput() {
        for (int i : keysToMonitor) {
            if (Gdx.input.isKeyPressed(i))
                gameModel.addKeyPress(i);
            else
                gameModel.removeKeyPress(i);
        }
    }

    public Stage getStage() {
        return stage;
    }
}
