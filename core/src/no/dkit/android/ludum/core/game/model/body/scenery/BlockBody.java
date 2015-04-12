package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.DirectionalGameBody;

public class BlockBody extends DirectionalGameBody {
    int x;
    int y;
    int tileIndex;
    Texture texture;

    public BlockBody(Body body, int x, int y, int tileIndex, float halfTileSizeX, int direction, Texture texture, BODY_TYPE bodyType) {
        super(body, halfTileSizeX, direction);

        this.bodyType = bodyType;

        this.texture = texture;
        this.tileIndex = tileIndex;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;
        draw(spriteBatch, texture);
    }

    public void draw(SpriteBatch spriteBatch, Texture texture) {
        if (!isActive()) return;

        spriteBatch.draw(texture,
                body.getPosition().x - Config.TILE_SIZE_X, body.getPosition().y - Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                1f,1f,
                MathUtils.radiansToDegrees * body.getAngle(),
                0, 0, texture.getWidth(), texture.getHeight(),
                false, false);
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public float getActiveDistance() {
        return Config.TILE_SIZE_X;
    }

    @Override
    public TextureRegion getImage() {
        return null;
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public void hit(int damage) {

    }
}
