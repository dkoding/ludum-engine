package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class SidescrollBackground extends MovingBackground {
    public SidescrollBackground(Texture backLayer, Texture frontLayer, float width, float height, float worldwidth, float worldheight) {
        super(backLayer, frontLayer, width, height, worldwidth, worldheight);
    }

    @Override
    public void render(Batch spriteBatch, float x, float y, float width, float height, float scale, float rotation) {

    }

    @Override
    public void update(float centerX, float centerY, float deltaX, float deltaY, float translateX, float translateY) {
        background.setU(background.getU() + deltaX*.9f);
        background.setV(background.getV() - deltaY*.9f);

        background.setU2(background.getU() + viewWidth);
        background.setV2(background.getV() + viewHeight);

        fog.setU(fog.getU() + (deltaX / 3));
        fog.setV(fog.getV() + .01f - (deltaY / 3));

        fog.setU2(fog.getU() + viewWidth / 3);
        fog.setV2(fog.getV() + viewHeight / 3);

        background.translate(translateX, translateY);
        fog.translate(translateX, translateY);
    }
}
