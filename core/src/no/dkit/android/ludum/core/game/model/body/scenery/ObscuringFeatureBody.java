package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

public class ObscuringFeatureBody extends FeatureBody {
    float offsetX;
    float offsetY;

    public ObscuringFeatureBody(float x, float y, float halfTileSizeX, TextureAtlas.AtlasRegion image) {
        this(x, y, halfTileSizeX, image, 1, 90, false);
        bodyType = BODY_TYPE.WOOD;
    }

    public ObscuringFeatureBody(float x, float y, float halfTileSizeX, TextureAtlas.AtlasRegion image, float scale, int rotation, boolean offset) {
        super(x, y, halfTileSizeX, image, scale, rotation);
        bodyType = BODY_TYPE.WOOD;
        if (offset) {
            offsetX = MathUtils.random(-radius / 2, radius / 2);
            offsetY = MathUtils.random(-radius / 2, radius / 2);
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        spriteBatch.draw(image,
                x - radius + offsetX, y - radius + offsetY,
                radius, radius,
                radius * 2, radius * 2,
                scale, scale, rotation, true);
    }
}
