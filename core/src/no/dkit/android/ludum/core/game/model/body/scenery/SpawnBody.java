package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.world.level.Level;

public class SpawnBody extends GameBody {
    public SpawnBody(Body body, float halfTileSizeX, TextureRegion image) {
        super(body, halfTileSizeX, image);
        addLight(LightFactory.getInstance().getLight(position.x, position.y, Config.TILE_SIZE_X * 2, 8, Color.WHITE));
        rotationMod = 1;
        lightMod = .01f;
        alpha = .5f;
        alphaMod = .1f;
    }

    @Override
    public void update() {
        super.update();

        if (GameModel.getNumEnemies() < Config.MAX_ENEMIES && MathUtils.random() < Config.ENEMY_CHANCE) {
            BodyFactory.getInstance().createAgent(Level.getInstance().getRandomEnemyType(),
                    position.cpy().add(
                            MathUtils.random(-Config.TILE_SIZE_X, Config.TILE_SIZE_X),
                            MathUtils.random(-Config.TILE_SIZE_Y, Config.TILE_SIZE_Y)
                    ));
        }
    }
}
