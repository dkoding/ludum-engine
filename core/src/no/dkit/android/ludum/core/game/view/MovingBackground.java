package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.dkit.android.ludum.core.shaders.RenderOperations;

public class MovingBackground implements RenderOperations {
    Sprite background;
    Sprite fog;

    float viewWidth;
    float viewHeight;
    float worldwidth;
    float worldheight;

    public MovingBackground(Texture backLayer, Texture frontLayer, float viewWidth, float viewHeight, float worldwidth, float worldheight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.worldwidth = worldwidth;
        this.worldheight = worldheight;

        if (backLayer != null) {
            backLayer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            background = new Sprite(backLayer, 0, 0, (int) viewWidth, (int) viewHeight);
            background.setSize(viewWidth, viewHeight);
        }

        if (frontLayer != null) {
            frontLayer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            fog = new Sprite(frontLayer, 0, 0, (int) viewWidth, (int) viewHeight);
            fog.setSize(viewWidth, viewHeight);
        }
    }

    public void update() {
        update(0, 0, 0, 0);
    }

    public void update(float deltaX, float deltaY) {
        update(deltaX, deltaY, 0, 0);
    }

    @Override
    public void update(float centerX, float centerY, float deltaX, float deltaY, float translateX, float translateY) {
        if (background != null) {
            background.setU(background.getU() + (deltaX / 4));
            background.setV(background.getV() - (deltaY / 4));
            background.setU2(background.getU() + viewWidth / 3);
            background.setV2(background.getV() + viewHeight / 3);
            background.translate(translateX, translateY);
        }

        if (fog != null) {
            fog.setU(fog.getU() + (deltaX / 5));
            fog.setV(fog.getV() - (deltaY / 5));

            fog.setU2(fog.getU() + viewWidth / 3);
            fog.setV2(fog.getV() + viewHeight / 3);

            fog.translate(translateX, translateY);
        }
    }

    public void update(float deltaX, float deltaY, float translateX, float translateY) {
        update(viewWidth / 2, viewHeight / 2, deltaX, deltaY, translateX, translateY);
    }

    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        batch.begin();
        batch.disableBlending();
        if (background != null)
            background.draw(batch);
        batch.enableBlending();
        if (fog != null)
            fog.draw(batch);
        batch.end();
    }

    @Override
    public void render(Batch spriteBatch, float x, float y, float width, float height, float scale, float rotation) {

    }

    @Override
    public void renderFullScreen(SpriteBatch spriteBatch) {
        render(spriteBatch, 0, 0, viewWidth, viewHeight);
    }
}
