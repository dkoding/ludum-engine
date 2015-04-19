package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Timer;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LootFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.factory.TextItem;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.PoolableGameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.loot.Weapon;

public class LootBody extends PoolableGameBody {
    Loot.LOOT_TYPE type;
    TextureAtlas.AtlasRegion bgImage;
    boolean collected;
    Color color;

    public LootBody(Body body, float halfTileSizeX, Loot.LOOT_TYPE type, TextureAtlas.AtlasRegion itemImage, TextureAtlas.AtlasRegion bgImage, Color color) {
        super(body, halfTileSizeX, itemImage);
        this.color = color;
        collected = false;
        this.bgImage = bgImage;
        this.type = type;
        lightMod = .2f;
        rotationMod = 3;
        ttl = 0;
        created = System.currentTimeMillis();
        getBody().setLinearVelocity(MathUtils.random(Config.LOOT_SPEED * 2) - Config.LOOT_SPEED, MathUtils.random(Config.LOOT_SPEED * 2) - Config.LOOT_SPEED);
    }

    public LootBody(Body body, float halfTileSizeX, Loot.LOOT_TYPE type, TextureAtlas.AtlasRegion itemImage, Color color) {
        this(body, halfTileSizeX, type, itemImage, null, color);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    @Override
    public void collidedWith(GameBody other) {
        if(collected) return;
        if (other instanceof AgentBody) {
            if (!((AgentBody) other).active) return;
            if (other instanceof PlayerBody) {
                collected = true;
                SoundFactory.getInstance().playSound(SoundFactory.SOUND_TYPE.ARMOR);
                TextFactory.getInstance().addText(new TextItem("Collect LOOT", 0, Config.getDimensions().SCREEN_HEIGHT / 3, Color.WHITE), -.01f);
                final Loot loot = LootFactory.getInstance().getLoot(type);
                ((PlayerBody) other).addLoot(loot);

                if (loot instanceof Weapon)
                    body.setLinearVelocity(Config.TILE_SIZE_X * 5, -Config.TILE_SIZE_Y * 4);
                else
                    body.setLinearVelocity(Config.TILE_SIZE_X * 5, Config.TILE_SIZE_Y * 4);

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        delete();
                    }
                }, 1f);
            }
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive() || image == null) return;

        if(type == Loot.LOOT_TYPE.TREASURE) color = Color.YELLOW;
        else if(type == Loot.LOOT_TYPE.MEDPACK) color = Color.RED;
        else if(type == Loot.LOOT_TYPE.ORB) color = Color.PURPLE;
        else if(type == Loot.LOOT_TYPE.ARMOR) color = Color.BLUE;

        spriteBatch.setColor(Color.YELLOW);

        if (bgImage != null)
            spriteBatch.draw(bgImage,
                    body.getPosition().x - radius, body.getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    1, 1,
                    rotation,
                    true);

        spriteBatch.setColor(color);

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                rotation,
                true);

        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void update() {
        super.update();

        if (ttl > 0) {
            if (System.currentTimeMillis() > created + (ttl / 2))
                alphaMod = -.2f;
            if (System.currentTimeMillis() > created + ttl)
                delete();
        }
    }
}
