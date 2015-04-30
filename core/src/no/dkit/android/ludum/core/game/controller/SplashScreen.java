package no.dkit.android.ludum.core.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;

public class SplashScreen implements Screen {
    private SpriteBatch spriteBatch;
    private Texture splash;
    boolean ready = false;
    boolean ready2 = false;

    float xPos;
    float yPos;

    public SplashScreen() {
        spriteBatch = new SpriteBatch();
        splash = new Texture(Gdx.files.internal("images/dkit.png"));
        ResourceFactory.createForUI();
        SoundFactory.getInstance();

        xPos = Config.getDimensions().SCREEN_WIDTH / 2 - splash.getWidth() / 2;
        yPos = Config.getDimensions().SCREEN_HEIGHT / 2 - splash.getHeight() / 2;

        // WTF..........    Plutselig vises ingen ting i WEB versjonen etter 1.5.6... ARGH!
    }

    public void render(float delta) {
        ready = ResourceFactory.getInstance().poll();
        ready2 = SoundFactory.getInstance().poll();

        Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(splash, xPos, yPos);
        spriteBatch.end();

        if (ready && ready2) {
            if (Config.SANDBOX) {
                XXXX.changeLevel();
            } else
                XXXX.changeScreen(XXXX.SCREEN.STARTMENU);
            ready = false;
            ready2 = false;
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