package no.dkit.android.ludum.core.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.loot.Weapon;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.view.GameView;

public class GameScreen implements Screen, InputProcessor {
    public static final String TOGGLE = "toggle";
    public static final String TRANSPARENT = "transparent";
    public static final String WEAPONWINDOW = "WEAPONWINDOW";
    private static Level.LEVEL_TYPE travelType;

    GameModel gameModel;
    GameView gameView;
    GestureDetector processor;
    InputMultiplexer multiplexer;
    Stage stage; // UI

    Label fpsLabel;
    Label fps;

    StringBuilder fpsBuilder = new StringBuilder();

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

    private static ImageButton mapButton;
    private static Button upgradeButton;
    private static Button travelSidescrollButton;
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

        mapButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(gameModel.getMap().getImage()))) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                gameModel.getMap().getImage();
                super.draw(batch, .5f);
            }
        };

        mapButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                XXXX.changeScreen(XXXX.SCREEN.STARTMENU);
            }
        });

        updateWeaponWindow();

        fpsLabel = new Label("FPS: ", skin, TRANSPARENT);
        fpsLabel.setAlignment(Align.right);

        fps = new Label("", skin, TRANSPARENT);
        fps.setAlignment(Align.right);

        Table mapWindow = new Table(skin);
        mapWindow.defaults();
        mapWindow.pad(5);
        mapWindow.add(mapButton).size(100, 100);
        mapButton.getImageCell().fill().expand();
        mapWindow.pack();
        mapWindow.setPosition(0, Config.getDimensions().SCREEN_HEIGHT - mapWindow.getHeight());

        stage.addActor(mapWindow);

        multiplexer = new InputMultiplexer(stage, processor, this);
        //Gdx.input = new RemoteInput();
    }

    public void updateWeaponButton(Weapon weapon) {
        Table table = (Table) stage.getRoot().findActor(WEAPONWINDOW);
        TextButton actor = (TextButton) table.findActor("" + weapon.getId());
        actor.setText("" + weapon.ammo);
        actor.invalidate();
    }

    public void updateWeaponWindow() {
        Table weaponWindow = new Table(skin);
        weaponWindow.setName(WEAPONWINDOW);
        weaponWindow.setSize(Config.getDimensions().SCREEN_WIDTH, Config.getDimensions().SCREEN_HEIGHT / 20);

        ButtonGroup weaponGroup = new ButtonGroup();

        TextButton[] weaponButtons = new TextButton[0];

        Weapon[] playerWeapons = new Weapon[0];

        Array<Weapon> weapons = gameModel.getPlayerWeapons();
        if (weapons.size > 0) {
            playerWeapons = new Weapon[weapons.size];

            for (int i=0; i < weapons.size; i++) {
                playerWeapons[i] = weapons.get(i);
            }
            weaponButtons = new TextButton[playerWeapons.length];
        }

        for (int i = 0; i < playerWeapons.length; i++) {
            final int index = i;
            final int ammo = weapons.get(i).ammo;
            final TextureAtlas.AtlasRegion image = ResourceFactory.getInstance().getImage(ResourceFactory.UI, playerWeapons[i].getImageName());

            weaponButtons[i] = new TextButton("" + ammo, skin, TRANSPARENT) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    if (isChecked()) {
                        batch.setColor(Color.WHITE);
                    } else {
                        batch.setColor(Color.DARK_GRAY);
                    }

                    batch.draw(image, getX(), getY(), getWidth(), getHeight());

                    super.draw(batch, .9f);

                    batch.setColor(Color.WHITE);
                }
            };

            weaponButtons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gameModel.setWeaponIndex(index);
                }
            });

            TextButton.TextButtonStyle style = weaponButtons[i].getStyle();
            weaponButtons[i].setStyle(style);
            weaponButtons[i].setName("" + playerWeapons[i].getId());
            weaponGroup.add(weaponButtons[i]);

            weaponWindow.add(weaponButtons[i]).size(Config.getDimensions().SCREEN_WIDTH / 20);
        }

        weaponWindow.defaults().align(Align.right);
        weaponWindow.defaults().pad(10);

        weaponWindow.pack();
        weaponWindow.setPosition(Gdx.graphics.getWidth() - weaponWindow.getWidth(), 0);

        Actor oldWindow = stage.getRoot().findActor(WEAPONWINDOW);

        if (oldWindow != null)
            stage.getRoot().removeActor(oldWindow);

        stage.addActor(weaponWindow);
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
            fps.setText(fpsBuilder);
            lastTextUpdate = System.currentTimeMillis();
        }

        //stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public static void enableUpgradeMenu(boolean enable) {
        upgradeButton.setVisible(enable);
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
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE) {
            XXXX.changeScreen(XXXX.SCREEN.UPGRADE);
        } else if (keycode == Input.Keys.ESCAPE) {
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

    public void update() {
        updateWeaponWindow();
    }

    private class MyGestureListener implements GestureDetector.GestureListener {
        public boolean touchDown(float x, float y, int pointer, int button) {
            return false;
        }

        public boolean tap(float x, float y, int count, int button) {
            gameModel.touch(x, y);
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
            return false;
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
