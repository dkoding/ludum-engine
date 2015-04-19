package no.dkit.android.ludum.core.game.model.body.item;

import box2dLight.Light;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
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
        rotationMod = 0;
        bodyType = BODY_TYPE.METAL;
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        spriteBatch.setColor(color);

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                .75f, .75f,
                +90,
                true);

        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void collidedWith(GameBody other) { // Will always be playerbody because of collission bits
        ((PlayerBody) other).getData().addKey();

        TextFactory.getInstance().addText(new TextItem("BABY RESCUED! FIND THE EXIT!"), 0f);

        int counter = 0;

        SoundFactory.getInstance().playMusic(SoundFactory.MUSIC_TYPE.WIN);
        SoundFactory.getInstance().playSound(SoundFactory.SOUND_TYPE.CASH);

        while (counter < 50) {
            Vector2 position = other.position;
            position.add(MathUtils.random(-Config.getDimensions().WORLD_WIDTH / 2, Config.getDimensions().WORLD_WIDTH / 2),
                    MathUtils.random(-Config.getDimensions().WORLD_HEIGHT / 2, Config.getDimensions().WORLD_HEIGHT / 2));
            EffectFactory.getInstance().addEffect(position, EffectFactory.EFFECT_TYPE.ACHIEVE);
            Light light = LightFactory.getInstance().getLight(position.x, position.y, Config.TILE_SIZE_X * 2, 6, new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1));
            light.setStaticLight(true);
            counter++;
        }

        delete();
    }
}
