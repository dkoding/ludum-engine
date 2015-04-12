package no.dkit.android.ludum.core.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LevelFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.factory.TextItem;

public class LoadingScreen implements Screen {
    private SpriteBatch spriteBatch;
    private Texture progress;
    boolean ready = false;

    public LoadingScreen() {
        spriteBatch = new SpriteBatch();
        TextFactory.getInstance().addText(new TextItem("LOADING, please wait...", new Vector2(Config.getDimensions().SCREEN_WIDTH / 2, Config.getDimensions().SCREEN_HEIGHT / 3), Color.WHITE));
        progress = new Texture(Gdx.files.internal("images/loading.png"));
        if(Config.SANDBOX)
            ResourceFactory.create(Config.SANDBOX_TYPE);
        else
            ResourceFactory.create(LevelFactory.getInstance().getCurrentLevel().getWorldType());
    }

    public void render(float delta) {
        ready = ResourceFactory.getInstance().poll();
        float percentComplete = ResourceFactory.getInstance().getPercentComplete();

        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.setColor(Color.RED);
        spriteBatch.draw(progress,
                Config.getDimensions().SCREEN_WIDTH / 2 - progress.getWidth() * 100 / 2,
                Config.getDimensions().SCREEN_HEIGHT / 2 - progress.getHeight() / 2,
                progress.getWidth() * 100, progress.getHeight() / 3);
        spriteBatch.setColor(Color.GREEN);
        spriteBatch.draw(progress,
                Config.getDimensions().SCREEN_WIDTH / 2 - progress.getWidth() * 100 / 2,
                Config.getDimensions().SCREEN_HEIGHT / 2 - progress.getHeight() / 2,
                progress.getWidth() * 100 * percentComplete, progress.getHeight() / 3);
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.end();

        TextFactory.getInstance().drawText(spriteBatch);

        if (ready) {
            XXXX.setScreen(new GameScreen());
            ready = false;
        }
    }

    public void resize(int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
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
    }
}