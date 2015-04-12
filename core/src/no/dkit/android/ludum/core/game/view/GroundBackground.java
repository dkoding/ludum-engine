package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.graphics.Texture;

public class GroundBackground extends MovingBackground {
    public GroundBackground(Texture backLayer, Texture frontLayer, float viewWidth, float viewHeight, float worldwidth, float worldheight) {
        super(backLayer, frontLayer, viewWidth, viewHeight, worldwidth, worldheight);
    }

    @Override
    public void update(float centerX, float centerY, float deltaX, float deltaY, float translateX, float translateY) {
        background.setU(background.getU() + deltaX);
        background.setV(background.getV() - deltaY);

        background.setU2(background.getU() + viewWidth);
        background.setV2(background.getV() + viewHeight);

        fog.setU(fog.getU() + .005f + (deltaX / 3));
        fog.setV(fog.getV() - .005f - (deltaY / 3));

        fog.setU2(fog.getU() + viewWidth/3);
        fog.setV2(fog.getV() + viewHeight/3);

        background.translate(translateX, translateY);
        fog.translate(translateX, translateY);
    }
}
