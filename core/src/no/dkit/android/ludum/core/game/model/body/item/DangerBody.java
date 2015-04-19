package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.quest.GameEvent;

public class DangerBody extends GameBody {
    int size;

    public DangerBody(Body body, float halfTileSizeX, TextureRegion image) {
        super(body, halfTileSizeX, image);
        size = 3;
        addLight(LightFactory.getInstance().getLight(position.x, position.y, halfTileSizeX, 6, Color.WHITE));
        light.setStaticLight(false);
        lightMod = .2f;
        rotationMod = -1;
        getBody().setLinearVelocity(MathUtils.random(Config.MIN_SPEED * 2) - Config.MIN_SPEED, MathUtils.random(Config.MIN_SPEED * 2) - Config.MIN_SPEED);
        getBody().setAngularVelocity(MathUtils.random(Config.ROTATE_SPEED) - Config.ROTATE_SPEED / 2f);

        bodyType = BODY_TYPE.STONE;
    }

    public DangerBody(Body body, float halfTileSizeX, TextureRegion image, int size) {
        this(body, halfTileSizeX, image);
        this.size = size;
    }

    @Override
    public void collidedWith(GameBody other) {
        if (other instanceof PlayerBody)
            other.hit(3);
    }

    @Override
    public GameBody.DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.MEDIUM;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        if (alpha != 0) {
            spriteBatch.setColor(1, 1, 1, alpha);
        } else
            spriteBatch.setColor(Color.WHITE);

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                body.getAngle() * MathUtils.radiansToDegrees,
                true);
    }

    @Override
    public void setActive(boolean active) {
        if (!this.active && active)
            body.setAngularVelocity(MathUtils.random(Config.ROTATE_SPEED * 2) - Config.ROTATE_SPEED);
        super.setActive(active);
    }

    @Override
    public void onDeath() {
        SoundFactory.getInstance().playDieSound(bodyType);
        EffectFactory.getInstance().addDieEffect(position, bodyType);
        BodyFactory.getInstance().createLoot(position, Loot.LOOT_TYPE.ORB);
        BodyFactory.getInstance().createLoot(position, Loot.LOOT_TYPE.TREASURE);
        BodyFactory.getInstance().createLoot(position, Loot.LOOT_TYPE.MEDPACK);
        BodyFactory.getInstance().createLoot(position, Loot.LOOT_TYPE.ARMOR);
        GameModel.onEvent(GameEvent.EVENT_TYPE.KILLED, this);
        delete();
    }
}
