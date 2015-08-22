package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class MineBody extends GameBody {
    int damage = 25;

    public MineBody(Body body, float radius, TextureAtlas.AtlasRegion image) {
        super(body, radius, image);
        addLight(LightFactory.getInstance().getLight(position.x, position.y, Config.TILE_SIZE_X, 4, Color.RED));
        lightMod = .1f;
        bodyType = GameBody.BODY_TYPE.METAL;
    }

    @Override
    public void collidedWith(GameBody other) {
        if (other instanceof AgentBody) {
            other.hit(damage);
        }
        EffectFactory.getInstance().addEffect(position, EffectFactory.EFFECT_TYPE.BIGEXPLOSION);
        delete();
    }
}
