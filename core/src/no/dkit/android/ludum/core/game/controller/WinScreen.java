package no.dkit.android.ludum.core.game.controller;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Sine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.single.SingleBehavior;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.view.TweenImage;
import no.dkit.android.ludum.core.game.view.TweenImageAccessor;
import no.dkit.android.ludum.core.shaders.AbstractShader;
import no.dkit.android.ludum.core.shaders.RenderOperations;
import no.dkit.android.ludum.core.shaders.fullscreen.FireShader;

public class WinScreen implements Screen {
    private SpriteBatch spriteBatch;
    private final TweenImage tweenImage;
    private final TweenImage tweenImage2;
    private final TweenImage tweenImage3;

    RenderOperations background;
    private long lastShaderUpdate = System.currentTimeMillis();
    private long shaderFrameTime = 1;

    public WinScreen() {
        spriteBatch = new SpriteBatch();

        final Texture texture = new Texture(Gdx.files.internal("images/win.png"));
        final Texture texture2 = new Texture(Gdx.files.internal("images/wins.png"));
        final Texture texture3 = new Texture(Gdx.files.internal("images/flames.png"));

        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texture2.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texture3.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        final TextureRegion image = new TextureRegion(texture);
        final TextureRegion image2 = new TextureRegion(texture2);
        final TextureRegion image3 = new TextureRegion(texture3);

        tweenImage = new TweenImage(image, (float) Config.getDimensions().SCREEN_WIDTH / 2, (float) Config.getDimensions().SCREEN_HEIGHT / 2,
                1, 1, 90, 1, image.getRegionHeight(), image.getRegionWidth());
        tweenImage2 = new TweenImage(image2, (float) Config.getDimensions().SCREEN_WIDTH / 2, image2.getRegionHeight()*2 + (float) Config.getDimensions().SCREEN_HEIGHT / 2,
                1, 1, 90, 1, image2.getRegionHeight(), image2.getRegionWidth());
        tweenImage3 = new TweenImage(image3, (float) Config.getDimensions().SCREEN_WIDTH / 2, image2.getRegionHeight()*2 + image3.getRegionHeight()*2 + (float) Config.getDimensions().SCREEN_HEIGHT / 2,
                1, 1, 90, 1, image3.getRegionHeight(), image3.getRegionWidth());

        background = new FireShader();
        ((AbstractShader) background).init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(float delta) {
        XXXX.getTweener().update(delta);

        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        drawBackground();

        spriteBatch.begin();
        tweenImage.draw(spriteBatch);
        tweenImage2.draw(spriteBatch);
        tweenImage3.draw(spriteBatch);
        spriteBatch.end();
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
                .push(Tween.from(tweenImage, TweenImageAccessor.POSITION_Y, 1f).target(-tweenImage.getRegionHeight()/2f).ease(Sine.OUT)).repeatYoyo(10,0f)
                .start(XXXX.getTweener());

        Timeline.createSequence()
                .push(Tween.from(tweenImage2, TweenImageAccessor.POSITION_Y, 2f).target(-tweenImage2.getRegionHeight()).ease(Bounce.OUT))
                .start(XXXX.getTweener());

        Timeline.createSequence()
                .push(Tween.from(tweenImage3, TweenImageAccessor.POSITION_Y, 1f).target(-tweenImage3.getRegionHeight()).ease(Bounce.OUT))
                .start(XXXX.getTweener());

        if (Config.music)
            SoundFactory.getInstance().playMusic(SoundFactory.MUSIC_TYPE.WIN);
    }

    public void dispose() {
    }

    private void drawBackground() {
        if (System.currentTimeMillis() > lastShaderUpdate + shaderFrameTime) {
            background.update();
            lastShaderUpdate = System.currentTimeMillis();
        }

        background.renderFullScreen(spriteBatch);
    }
}