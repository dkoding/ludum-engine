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

    float posX;
    float posY;

    public Decals(float width, float height) {
        this.width = width;
        this.height = height;
        createRenderBuffer();
    }

    private void createRenderBuffer() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888,
                (int) (width * Config.getDimensions().SCREEN_PIXELS_PER_TILE),
                (int) (height * Config.getDimensions().SCREEN_PIXELS_PER_TILE), // To capture screen
                false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);
    }

    public void record() {
        fbo.begin();
        //Gdx.gl20.glClearColor(1, 1, 1, .5f);
        //Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void end() {
        fbo.end();
    }

    public void render(SpriteBatch batch, float x, float y, float deltaX, float deltaY) {
        posX-=deltaX;
        posY-=deltaY;

        batch.draw(fboRegion,
                posX + x - Config.getDimensions().WORLD_WIDTH / 2f, posY + y - Config.getDimensions().WORLD_HEIGHT / 2f,
                0, 0,
                Config.getDimensions().WORLD_WIDTH, Config.getDimensions().WORLD_HEIGHT,
                1, 1,
                0);
    }
}
