package no.dkit.android.ludum.core.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;

public class HelpScreen implements Screen {
    Stage stage;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;

    public HelpScreen() {
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label[] helpText = new Label[]{
                new Label("Navigate using WASD, space and mouse, or drag and tap on mobile device", skin, "transparent"),
                new Label("These space rocks sometimes contain orbs, destroy them with a bomb or a rocket to harvest crystals.", skin, "transparent"),
                new Label("So do these crates.", skin, "transparent"),
                new Label("Collect energy orbs to buy powerups.", skin, "transparent"),
                new Label("Also collect coins to buy powerups.", skin, "transparent"),
                new Label("Collect health.", skin, "transparent"),
                new Label("Collect armor.", skin, "transparent"),
        };

        Image[] helpImage = new Image[]{
                new Image(ResourceFactory.getInstance().getImage(ResourceFactory.UI, "crosshair")),
                new Image(ResourceFactory.getInstance().getItemImage("crystal")),
                new Image(ResourceFactory.getInstance().getItemImage("crate_metal")),
                new Image(ResourceFactory.getInstance().getItemImage("orb")),
                new Image(ResourceFactory.getInstance().getItemImage("treasure")),
                new Image(ResourceFactory.getInstance().getItemImage("medpack")),
                new Image(ResourceFactory.getInstance().getItemImage("armor")),
        };

        Table window = new Table(skin);
        window.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        window.defaults().pad(10);

        for (int i = 0; i < helpText.length; i++) {
            Label label = helpText[i];
            label.setWrap(true);
            label.setAlignment(Align.center);
            window.add(label).size(300, 100);
            window.add(helpImage[i]).size(100, 100);

            if (i % 3 == 0)
                window.row();
        }

        window.row();

        stage = new Stage(new ScreenViewport(camera));
        stage.addActor(window);
    }

    public void render(float delta) {
        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        drawBackground();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (Gdx.input.justTouched())
            XXXX.changeScreen(XXXX.SCREEN.STARTMENU);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void show() {
        //
    }

    public void hide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pause() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dispose() {
        stage.dispose();
    }

    private void drawBackground() {
    }
}