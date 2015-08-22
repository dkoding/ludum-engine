package no.dkit.android.ludum.core.game.model.world.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.MapFactory;

import java.util.Random;

public class RandomLevel extends Level {
    public RandomLevel(LEVEL_TYPE worldType, int level) throws RuntimeException {
        super(worldType, level, false, false);

        MathUtils.random = new Random(Config.RANDOM_SEED + level);

        if (worldType == LEVEL_TYPE.TOPDOWN) {
            this.platforms = MathUtils.randomBoolean();
            this.inside = MathUtils.randomBoolean();
        }

        this.map = MapFactory.getInstance().getRandomMap(worldType, level, false, false); // Inside + platforms make no sense

        setupDefaults(worldType);
        addItems(worldType, level);
        getDefaultPlayerStartPosition(worldType);

        this.map.removeAllHints();
    }

    @Override
    public void onStart() {
        BodyFactory.getInstance().createAgent(BodyFactory.ENEMY_TYPE.WALKER_SINGLE, new Vector2(2,2));
        BodyFactory.getInstance().createAgent(BodyFactory.ENEMY_TYPE.WALKER_GROUP, new Vector2(2,2));
    }
}
