package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class ChasmBody extends GameBody {
    private TextureRegion maskRegion;
    Sprite behindSprite;
    float offsetX;
    float offsetY;
    float xPos;
    float yPos;

    public ChasmBody(Body body, float xPos, float yPos, float radius, TextureAtlas.AtlasRegion floorImage, Texture behindImage, TextureRegion maskRegion, float offsetX, float offsetY, float rotation) {
        super(body, radius, floorImage);
        this.maskRegion = maskRegion;

        bodyType = BODY_TYPE.STONE;

        this.xPos = xPos;
        this.yPos = yPos;

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rotation = rotation;

        position.set(position.x + this.offsetX, position.y + this.offsetY);
        body.setTransform(position.x, position.y, rotation);

        if (behindImage != null) {
            behindImage.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            behindSprite = new Sprite(behindImage, 0, 0, (int) radius * 2, (int) radius * 2);
            behindSprite.setSize(radius * 2, radius * 2);
            behindSprite.setPosition(position.x - radius + offsetX, position.y - radius + offsetY);
            behindSprite.setRotation(0);
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        if (image == null || !isActive()) return;

        if (behindSprite != null) {
            if (maskRegion != null) {
                spriteBatch.draw(image,
                        xPos - Config.TILE_SIZE_X, yPos - Config.TILE_SIZE_Y,
                        Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                        Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                        1f, 1f,
                        0, true);

                spriteBatch.end();

                spriteBatch.enableBlending();
                Gdx.gl20.glColorMask(false, false, false, true);
                spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

                spriteBatch.begin();

                spriteBatch.draw(maskRegion,
                        position.x - radius + offsetX, position.y - radius + offsetY,
                        radius - offsetX, radius - offsetY,
                        radius * 2, radius * 2,
                        1f, 1f,
                        0, true);

                spriteBatch.end();

                Gdx.gl20.glColorMask(true, true, true, true);
                spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

                spriteBatch.begin();
            }

            float diffX = (position.x + GameModel.getPlayer().position.x) / 20f;
            float diffY = (position.y - GameModel.getPlayer().position.y) / 20f;

            behindSprite.setU(diffX);
            behindSprite.setU2(diffX + radius * 2);
            behindSprite.setV(diffY);
            behindSprite.setV2(diffY + radius * 2);

            behindSprite.draw(spriteBatch);
        } else {
            if (maskRegion != null) {
                spriteBatch.end();

                spriteBatch.enableBlending();
                Gdx.gl20.glColorMask(false, false, false, true);
                spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

                spriteBatch.begin();

                spriteBatch.draw(maskRegion,
                        position.x - radius + offsetX, position.y - radius + offsetY,
                        radius - offsetX, radius - offsetY,
                        radius * 2, radius * 2,
                        1f, 1f,
                        rotation * MathUtils.radiansToDegrees, true);

                spriteBatch.end();

                Gdx.gl20.glColorMask(true, true, true, true);
                spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

                spriteBatch.begin();

                spriteBatch.draw(image,
                        xPos - Config.TILE_SIZE_X, yPos - Config.TILE_SIZE_Y,
                        Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                        Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                        1f, 1f,
                        0, true);
            }
        }

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.end();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.begin();
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.BACK;
    }

    @Override
    public void collidedWith(GameBody other) {
        if (other instanceof AgentBody) {
            if (getBody().getFixtureList().get(0).testPoint(other.position)) {
                ((AgentBody) other).fell(position);
            }
        }
    }

    @Override
    public float getActiveDistance() {
        return Config.TILE_SIZE_X;
    }
}
