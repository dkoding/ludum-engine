package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

public class FloorBody extends FeatureBody {
    public FloorBody(float x, float y, float halfTileSizeX, TextureAtlas.AtlasRegion image) {
        super(x, y, halfTileSizeX, image);
        rotation = MathUtils.random(360);
        bodyType = BODY_TYPE.WOOD;
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.BACK;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        spriteBatch.draw(image,
                x - radius, y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1, 0, true);
    }
}
