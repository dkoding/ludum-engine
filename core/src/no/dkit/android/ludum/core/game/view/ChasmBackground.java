package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class ChasmBackground extends MovingBackground {
    public
    ChasmBackground(Texture frontLayer, float viewWidth, float viewHeight, float worldwidth, float worldheight) {
        super(null, frontLayer, viewWidth, viewHeight, worldwidth, worldheight);
    }

    @Override
    public void render(Batch spriteBatch, float x, float y, float width, float height, float scale, float rotation) {

    }

    @Override
    public void update(float centerX, float centerY, float deltaX, float deltaY, float translateX, float translateY) {
        fog.setU(fog.getU() + .004f + (deltaX / 3));
        fog.setV(fog.getV() - .004f - (deltaY / 3));

        fog.setU2(fog.getU() + viewWidth/3);
        fog.setV2(fog.getV() + viewHeight/3);

        fog.translate(translateX, translateY);
    }
}
