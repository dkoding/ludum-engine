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

        background.translate(translateX, translateY);
    }
}
