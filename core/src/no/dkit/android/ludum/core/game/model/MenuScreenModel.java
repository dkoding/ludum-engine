package no.dkit.android.ludum.core.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LevelFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.shaders.AbstractShader;
import no.dkit.android.ludum.core.shaders.RenderOperations;
import no.dkit.android.ludum.core.shaders.fullscreen.LightSwirlShader;

public class MenuScreenModel {
    public static final String TRANSPARENT = "transparent";
    public static final String TOGGLE = "toggle";
    private Map map;
    private TextureRegion mapImage;

    private final Stage stage;
    private final Label fpsLabel;
    private final Label levelLabel;
    private final Image mapActor;
    private final Skin skin;
    private Level level;

    SpriteBatch spriteBatch;
    RenderOperations background;

    OrthographicCamera camera;

    private long lastShaderUpdate = System.currentTimeMillis();
    private long shaderFrameTime = 1;

    public MenuScreenModel() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextureRegion helpImage = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "help");
        TextureRegion playImage = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "start");
        TextureRegion soundImage = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "sound");
        TextureRegion vibrateImage = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "vibrate");
        TextureRegion logo = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "logo");
        TextureRegion left = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "arrow_left");
        TextureRegion right = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "arrow_right");

        stage = new Stage(new ScreenViewport(camera));

        Button playButton = new Button(new Image(playImage), skin, TRANSPARENT);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                XXXX.changeLevel();
            }
        });

        Button helpButton = new Button(new Image(helpImage), skin, TRANSPARENT);

        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                XXXX.changeScreen(XXXX.SCREEN.HELP);
            }
        });

        Button nextButton = new Button(new Image(right), skin, TRANSPARENT);

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (LevelFactory.level < Config.MAX_LEVEL) {
                    level = LevelFactory.getInstance().nextLevel();
                    updateMapImage();
                    updateBackground();
                }
            }
        });

/*
        Button prevButton = new Button(new Image(left), skin, TRANSPARENT);

        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (LevelFactory.level > 1) {
                    level = LevelFactory.getInstance().previousLevel();
                    updateMapImage();
                    updateBackground();
                }
            }
        });
*/

        final Button toggleSound = new Button(new Image(soundImage), skin, TOGGLE);
        final Button toggleVibrate = new Button(new Image(vibrateImage), skin, TOGGLE);

        NinePatchDrawable up = (NinePatchDrawable) toggleVibrate.getStyle().up;

        NinePatchDrawable checked = new NinePatchDrawable(up) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(Color.GREEN);
                super.draw(batch, x, y, width, height);
            }
        };

        Button.ButtonStyle style = new Button.ButtonStyle();
        style.checked = checked;

        toggleVibrate.setStyle(style);
        toggleSound.setStyle(style);

        Image imageActor = new Image(logo);

        level = LevelFactory.getInstance().getCurrentLevel();

        updateBackground();

        map = new Map(level.getMap());
        mapImage = new TextureRegion(map.getImage());
        mapActor = new Image(mapImage);

        fpsLabel = new Label("FPS: ", skin, TRANSPARENT);
        fpsLabel.setAlignment(Align.center);
        levelLabel = new Label(getLevelLabel(), skin, TRANSPARENT);
        levelLabel.setAlignment(Align.center);

        // window.debug();
        Table window = new Table(skin);
        window.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        window.defaults().pad(10);

        window.add(imageActor).size(logo.getRegionWidth(), logo.getRegionHeight());
        window.row();
/*
        window.add(prevButton).size(200, 200);
*/
        window.add(nextButton).size(50, 50);
        window.row();
        window.add(mapActor).size(200, 200);
        window.row();
        window.add(levelLabel);
        window.row();
        //window.add(toggleVibrate).size(100, 100);
        window.add(playButton);
        //window.add(toggleSound).size(100, 100);
        window.row();
        window.add(fpsLabel);
        //window.pack();

        // stage.addActor(new Button("Behind Window", skin));
        stage.addActor(window);
        //helpButton.setSize(100, 100);
        //helpButton.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 200);
        //stage.addActor(helpButton);
    }

    private String getLevelLabel() {
        return "Level " + LevelFactory.level + ": " + level.getClass().getSimpleName() + ":" + level.getWorldType() + ":" + level.getMap().getClass().getSimpleName();
    }

    private void updateMapImage() {
        map = new Map(level.getMap());
        mapImage = new TextureRegion(map.getImage());
        mapActor.setDrawable(new TextureRegionDrawable(mapImage));
        levelLabel.setText(getLevelLabel());
    }

    private void updateBackground() {
        background = new LightSwirlShader();
        ((AbstractShader)background).init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void update() {
        Gdx.gl20.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        drawBackground();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void drawBackground() {
        if (System.currentTimeMillis() > lastShaderUpdate + shaderFrameTime) {
            background.update();
            lastShaderUpdate = System.currentTimeMillis();
        }

        background.renderFullScreen(spriteBatch);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        map.dispose();
        stage.dispose();
        skin.dispose();
    }
}