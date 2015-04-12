package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class FeatureBody extends GameBody {
    TextureRegion maskRegion;
    float scale;
    float rotation;

    float x;
    float y;

    public FeatureBody(float x, float y, float halfTileSizeX, TextureAtlas.AtlasRegion image, TextureRegion maskRegion) {
        super(halfTileSizeX, image);
        this.x = x;
        this.y = y;
        this.scale = 1;
        this.rotation = MathUtils.random(360);
        this.maskRegion = maskRegion;
        position.set(x,y);
    }

    public FeatureBody(float x, float y, float halfTileSizeX, TextureAtlas.AtlasRegion image) {
        super(halfTileSizeX, image);
        this.x = x;
        this.y = y;
        this.scale = 1;
        this.rotation = MathUtils.random(360);
        position.set(x,y);
    }

    public FeatureBody(float x, float y, float halfTileSizeX, TextureAtlas.AtlasRegion image, float scale) {
        super(halfTileSizeX, image);
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = MathUtils.random(360);
        position.set(x,y);
    }

    public FeatureBody(float x, float y, float halfTileSizeX, TextureAtlas.AtlasRegion image, float scale, int rotation) {
        super(halfTileSizeX, image);
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = rotation;
        position.set(x,y);
    }

    @Override
    public Body getBody() {
        throw new RuntimeException("NOT IMPLEMENTED!");
    }

    @Override
    public void delete() {
        throw new RuntimeException("NOT IMPLEMENTED!");
    }

    public void draw(SpriteBatch spriteBatch) {
        if (image == null || !isActive()) return;

        if (maskRegion != null) {
            spriteBatch.end();

            spriteBatch.enableBlending();
            Gdx.gl20.glColorMask(false, false, false, true);
            spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

            spriteBatch.begin();

            spriteBatch.draw(maskRegion,
                    x - radius, y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    scale, scale,
                    rotation, true);

            spriteBatch.end();

            Gdx.gl20.glColorMask(true, true, true, true);
            spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

            spriteBatch.begin();

            spriteBatch.draw(image,
                    x - radius, y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    scale, scale, rotation, true);

            spriteBatch.end();
            spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            spriteBatch.begin();
        } else {
            spriteBatch.draw(image,
                    x - radius, y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    scale, scale, rotation, true);
        }
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (light != null) light.setActive(active);
    }

    @Override
    public float getActiveDistance() {
        return super.getActiveDistance() * scale;
    }

    @Override
    public void setDisabled(boolean disabled) {
        throw new RuntimeException("NOT IMPLEMENTED!");
    }
}
