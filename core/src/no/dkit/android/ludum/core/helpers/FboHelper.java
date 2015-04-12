package no.dkit.android.ludum.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FboHelper {
    private FrameBuffer fbo;
    private int width;
    private int height;
    private boolean alpha;

    public FboHelper(int width, int height, boolean alpha) {
        this.width = width;
        this.height = height;
        this.alpha = alpha;
        createRenderBuffer();
    }

    private void createRenderBuffer() {
        if (alpha)
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        else
            fbo = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);

        fbo.getColorBufferTexture();
    }

    public void startFbo() {
        fbo.begin();
        Gdx.gl20.glClearColor(1, 1, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public void endFbo() {
        fbo.end();
    }

    public Texture getTexture() {
        return fbo.getColorBufferTexture();
    }
}
