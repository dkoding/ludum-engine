package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import no.dkit.android.ludum.core.game.Config;

public class Decals {
    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    float width;
    float height;
    float factor;

    public Decals(float width, float height, float factor) {
        this.factor = factor;
        this.width = width / factor;
        this.height = height / factor;
        createRenderBuffer(width, height);
    }

    private void createRenderBuffer(float width, float height) {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888,
                (int)(width * Config.getDimensions().SCREEN_PIXELS_PER_TILE),
                (int)(height * Config.getDimensions().SCREEN_PIXELS_PER_TILE),
                false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);
    }

    public void record() {
        fbo.begin();
        Gdx.gl20.glClearColor(1, 1, 1, .5f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void end() {
        fbo.end();
    }

    public void render(SpriteBatch batch) {
        batch.draw(fboRegion, 0, 0, 0, 0,
                width * factor,
                height * factor,
                1, 1, 0);
    }
}
