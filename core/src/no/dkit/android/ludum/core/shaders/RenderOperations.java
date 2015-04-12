package no.dkit.android.ludum.core.shaders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface RenderOperations {
    public static enum BACKGROUND_TYPE {
        DARKROCK, GROUND, UNIVERSE, FLAG, INWARDSCROLL, PIXELIZE, TRANSITIONCROSSFADE,
        TRANSITIONSWIPE, VIGNETTE, WATERRIPPLEREFRACT, WATERSINUSWAVE, WAVEINWIND, WAVE,

        ARCADE_TUNNEL, BEACON, /*BEACON2, */    FIRE,
        LIGHTSWIRL, NEONCROSS, NEONPULSE, NEONWAVE, NEONWAVE2, NEONWAVE3,
        RAINBOWSWIRL, SHININGSTARSCROLL, SIMPLE_CLOUD,
        SPIRAL
    }

    public static enum FOREGROUND_TYPE {
        FOG
    }

    public void render(SpriteBatch spriteBatch, float x, float y, float width, float height);

    public void renderFullScreen(SpriteBatch spriteBatch);

    void update();

    void update(float deltaX, float deltaY);

    void update(float deltaX, float deltaY, float translateX, float translateY);

    void update(float centerX, float centerY, float deltaX, float deltaY, float translateX, float translateY);
}
