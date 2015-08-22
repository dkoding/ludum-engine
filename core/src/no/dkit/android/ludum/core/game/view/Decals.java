package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class Decals {
    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    float width;
    float height;

    public Decals(float width, float height) {
        this.width = width;
        this.height = height;
        createRenderBuffer(width, height);
    }

    private void createRenderBuffer(float width, float height) {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int) width, (int) height, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);
    }

    public void record() {
        fbo.begin();
    }

    public void end() {
        fbo.end();
    }

    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(fboRegion, 0, 0, width, height);
    }
}
