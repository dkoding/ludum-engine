package no.dkit.android.ludum.core.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.UpgradeScreenModel;

public class UpgradeScreen implements Screen {
    UpgradeScreenModel model;

    public UpgradeScreen() {
        model = new UpgradeScreenModel();
    }

    public void render(float delta) {
        model.update();
    }

    public void resize(int width, int height) {
        model.getStage().getViewport().update(width, height);
    }

    public void show() {
        if (Config.music)
            SoundFactory.getInstance().playMusic(SoundFactory.MUSIC_TYPE.SHOP);

        Gdx.input.setInputProcessor(model.getStage());
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
            SoundFactory.getInstance().playMusic(SoundFactory.MUSIC_TYPE.SHOP);

        Gdx.input.setInputProcessor(model.getStage());
    }

    @Override
    public void dispose() {
        model.dispose();
    }
}
