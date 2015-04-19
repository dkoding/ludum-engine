package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.model.body.DiscardBody;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.weapon.WeaponBody;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

public class CrateBody extends GameBody {
    int health;

    public CrateBody(Body body, float radius) {
        super(body, radius);

        float type = MathUtils.random();

        if (type < .33f) {
            image = ResourceFactory.getInstance().getItemImage("crate");
            bodyType = BODY_TYPE.WOOD;
            this.health = 10;
        } else if (type < .66f) {
            image = ResourceFactory.getInstance().getItemImage("crate");
            bodyType = BODY_TYPE.WOOD;
            this.health = 20;
        } else {
            image = ResourceFactory.getInstance().getItemImage("crate");
            bodyType = BODY_TYPE.METAL;
            this.health = 100;
        }
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.MEDIUM;
    }

    @Override
    public void collidedWith(GameBody other) {
        if (other instanceof WeaponBody || other instanceof DiscardBody) { // TODO: Ugly hack
            health--;

            if (health <= 0) {
                BodyFactory.getInstance().createItems(MathUtils.random(0, 5), position.x, position.y, AbstractMap.ITEM_LOOT);
                delete();
            }
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        spriteBatch.setColor(Color.MAROON);

        spriteBatch.disableBlending();
        spriteBatch.draw(image,
                body.getPosition().x - Config.TILE_SIZE_X / 2, body.getPosition().y - Config.TILE_SIZE_Y / 2,
                Config.TILE_SIZE_X / 2, Config.TILE_SIZE_Y / 2,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                1, 1,
                body.getAngle() * MathUtils.radiansToDegrees,
                true);
        spriteBatch.enableBlending();

        spriteBatch.setColor(Color.WHITE);
    }

    @Override // Because of gravity
    public void setActive(boolean active) {
        super.setActive(true);
    }
}
