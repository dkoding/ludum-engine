package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.body.item.CannonBody;
import no.dkit.android.ludum.core.game.model.body.item.DangerBody;
import no.dkit.android.ludum.core.game.model.world.map.UniverseMap;

public class BombBody extends WeaponBody {
    public BombBody(Body body, float radius, TextureRegion image, float angle, int damage) {
        super(body, radius, image, angle, damage);
        this.damage = damage;
        speed = 1;
        body.setLinearVelocity(body.getLinearVelocity().nor().scl(speed));
        //addLight(LightFactory.getInstance().getLight(getPosition(), Config.HALF_TILE_SIZE_X, 6, Color.MAGENTA));
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                0, true);
        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void collidedWith(GameBody other) {
        if (!isActive()) return;

        if (other instanceof DangerBody || other instanceof CannonBody) {
            SoundFactory.getInstance().playDieSound(other.bodyType);
            EffectFactory.getInstance().addDieEffect(other.position, bodyType);
            BodyFactory.getInstance().createItems(MathUtils.random(2), other.position.x, other.position.y, UniverseMap.ITEM_LOOT);
            other.delete();
        }

        super.collidedWith(other);
    }

    @Override
    protected void hitAgentEffect(AgentBody other) {
        super.hitAgentEffect(other);
        BodyFactory.getInstance().createExplosion(position, 10);
    }

    @Override
    protected void hitNonAgentEffect(GameBody other) {
        super.hitNonAgentEffect(other);
        BodyFactory.getInstance().createExplosion(position, 10);
    }
}
