package no.dkit.android.ludum.core.game.controller;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Expo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LevelFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.view.TweenImage;
import no.dkit.android.ludum.core.game.view.TweenImageAccessor;

public class SplashScreen implements Screen {
    private SpriteBatch spriteBatch;
    boolean ready = false;
    boolean ready2 = false;
    private final TweenImage tweenImage;
    private boolean introDone = false;

    public SplashScreen() {
        spriteBatch = new SpriteBatch();
        ResourceFactory.createForUI();
        SoundFactory.getInstance();
        LevelFactory.getInstance();
        final Texture texture = new Texture(Gdx.files.internal("images/logo.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        final TextureRegion logo = new TextureRegion(texture);
        tweenImage = new TweenImage(logo, (float) Config.getDimensions().SCREEN_WIDTH / 2, (float) Config.getDimensions().SCREEN_HEIGHT / 2,
                1, 1, 90, 1, logo.getRegionHeight() * .5f, logo.getRegionWidth() * .5f);
    }

    public void render(float delta) {
        XXXX.getTweener().update(delta);

        ready = ResourceFactory.getInstance().poll();
        ready2 = SoundFactory.getInstance().poll();

        Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        tweenImage.draw(spriteBatch);
        spriteBatch.end();

        if (ready && ready2 && introDone) {
            if (Config.SANDBOX) {
                XXXX.changeScreen(XXXX.SCREEN.GAME);
            } else {
                XXXX.changeScreen(XXXX.SCREEN.STARTMENU);
            }
            ready = false;
            ready2 = false;
        }
    }

    public void resize(int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public void show() {
        Timeline.createSequence()
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        introDone = true;
                    }
                })
                .push(
                        Tween.to(tweenImage, TweenImageAccessor.SCALE_XY, .5f).target(1.1f, 1.1f).ease(Expo.OUT))
                .push(
                        Timeline.createParallel()
                                .push(Tween.to(tweenImage, TweenImageAccessor.SCALE_XY, .5f).target(0f, 0f).ease(Expo.IN))
                                .push(Tween.to(tweenImage, TweenImageAccessor.OPACITY, .5f).target(0).ease(Expo.IN))
                )
                .start(XXXX.getTweener());
    }

    public void dispose() {
    }

}