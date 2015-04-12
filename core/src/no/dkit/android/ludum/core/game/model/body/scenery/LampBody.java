package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.model.body.DirectionalGameBody;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

public class LampBody extends DirectionalGameBody {
    public LampBody(Body body, float radius, Color color, int direction) {
        this(body, radius, color, direction, Config.TILE_SIZE_X);
        bodyType = BODY_TYPE.METAL;
    }

    public LampBody(Body body, float radius, Color color, int direction, float offset) {
        super(body, radius, direction, offset, true, false);
        bodyType = BODY_TYPE.METAL;
        if (direction == AbstractMap.NO_DIRECTION) {
            addLight(LightFactory.getInstance().getLight(position.x, position.y, Config.getDimensions().WORLD_WIDTH/4, 6, color));
            image = ResourceFactory.getInstance().getItemImage("lamp");
        } else {
            addLight(LightFactory.getInstance().getConeLight(getBody().getPosition(), Config.getDimensions().WORLD_WIDTH/4, 4, color, startDirection, 90));
            image = ResourceFactory.getInstance().getItemImage("wallamp");
        }

        light.setStaticLight(true);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        spriteBatch.draw(image,
                body.getPosition().x - radius,
                body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                startDirection * MathUtils.radiansToDegrees,
                true);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.BACK;
    }
}
