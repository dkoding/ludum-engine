package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.factory.TextItem;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;

public class KeyBody extends GameBody {
    long lastSound = System.currentTimeMillis();

    public KeyBody(Body body, float halfTileSizeX, TextureRegion image) {
        super(body, halfTileSizeX, image);
        addLight(LightFactory.getInstance().getLight(position.x, position.y, halfTileSizeX, 6, Color.WHITE));
        light.setStaticLight(false);
        lightMod = .2f;
        rotationMod = 3;
        bodyType = BODY_TYPE.METAL;
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        if (alpha != 0) {
            spriteBatch.setColor(1, 1, 1, alpha);
        }

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                rotation,
                true);
    }

    @Override
    public void collidedWith(GameBody other) { // Will always be playerbody because of collission bits
        ((PlayerBody) other).getData().addKey();
        TextFactory.getInstance().addText(new TextItem("Collect KEYS", 0, Config.getDimensions().SCREEN_HEIGHT / 3, Color.WHITE), -.01f);
        delete();
    }
}
