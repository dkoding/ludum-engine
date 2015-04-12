package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

public class PlatformBody extends GameBody {
    Color color = Color.WHITE;
    private TextureRegion maskRegion;
    float offsetX;
    float offsetY;
    public boolean moving;
    public boolean sliding;
    public boolean rotating;
    int direction;

    float xMod = 0;
    float yMod = 0;
    float maxDistance = Config.TILE_SIZE_X;

    public PlatformBody(Body body, float halfTileSizeX, TextureAtlas.AtlasRegion image, TextureRegion maskRegion, boolean rot, float offsetX, float offsetY, int direction) {
        super(body, halfTileSizeX, image);
        this.maskRegion = maskRegion;

        bodyType = BODY_TYPE.STONE;

        this.rotating = rot;
        this.direction = direction;

        if (rotating) {
            float speed = MathUtils.randomBoolean() ? .01f : .005f;
            rotationMod = MathUtils.randomBoolean() ? speed : -speed;
        } else if (direction != AbstractMap.NO_DIRECTION) {
            sliding = true;
            float speed = .01f;

            if (direction == AbstractMap.N) {
                yMod = -speed;
            } else if (direction == AbstractMap.S) {
                yMod = speed;
            } else if (direction == AbstractMap.W) {
                xMod = -speed;
            } else if (direction == AbstractMap.E) {
                xMod = speed;
            }
            //rotation = MathUtils.random();
        } else {
            rotation = MathUtils.random();
        }

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        position.set(position.x + offsetX, position.y + offsetY);
        body.setTransform(position.x, position.y, rotation);

        this.moving = sliding || rotating;
    }

    public PlatformBody(Body body, float halfTileSizeX, TextureAtlas.AtlasRegion image, boolean rotating, float offsetX, float offsetY) {
        this(body, halfTileSizeX, image, null, rotating, offsetX, offsetY, AbstractMap.NO_DIRECTION);
    }

    public PlatformBody(Body body, float halfTileSizeX, TextureAtlas.AtlasRegion image, float offsetX, float offsetY, int direction) {
        this(body, halfTileSizeX, image, null, false, offsetX, offsetY, direction);
    }

    @Override
    public void update() {
        if (!moving) return;
        super.update();

        if (sliding) {
            maxDistance += xMod;
            maxDistance += yMod;

            if (maxDistance <= 0 || maxDistance >= Config.TILE_SIZE_X * 2) {
                xMod = -xMod;
                yMod = -yMod;
            }

            position.add(xMod, yMod);
        }

        body.setTransform(position.x, position.y, rotation);
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
                    position.x - radius + offsetX, position.y - radius + offsetY,
                    radius - offsetX, radius - offsetY,
                    radius * 2, radius * 2,
                    1, 1,
                    rotation * MathUtils.radiansToDegrees, true);

            spriteBatch.end();

            Gdx.gl20.glColorMask(true, true, true, true);
            spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

            spriteBatch.begin();

            spriteBatch.setColor(color);
            spriteBatch.draw(image,
                    position.x - radius + offsetX, position.y - radius + offsetY,
                    radius - offsetX, radius - offsetY,
                    radius * 2, radius * 2,
                    1, 1,
                    rotation * MathUtils.radiansToDegrees, true);

            spriteBatch.end();
            spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            spriteBatch.begin();
        } else {
            spriteBatch.draw(image,
                    position.x - radius + offsetX, position.y - radius + offsetY,
                    radius - offsetX, radius - offsetY,
                    radius * 2, radius * 2,
                    1, 1,
                    rotation * MathUtils.radiansToDegrees, true);
        }
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.BACK;
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getBody().getUserData() instanceof AgentBody)
            ((AgentBody) contact.getFixtureA().getBody().getUserData()).addPlatform();
        if (contact.getFixtureB().getBody().getUserData() instanceof AgentBody)
            ((AgentBody) contact.getFixtureB().getBody().getUserData()).addPlatform();
    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getBody().getUserData() instanceof AgentBody)
            ((AgentBody) contact.getFixtureA().getBody().getUserData()).removePlatform();
        if (contact.getFixtureB().getBody().getUserData() instanceof AgentBody)
            ((AgentBody) contact.getFixtureB().getBody().getUserData()).removePlatform();
    }
}
