package no.dkit.android.ludum.core.game.model.world.level;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.MapFactory;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

import java.util.Random;

public class RandomLevel extends Level {
    int attempts = 0;

    public RandomLevel(LEVEL_TYPE worldType, int level) throws RuntimeException {
        super(worldType, level, false, false);

        MathUtils.random = new Random(Config.RANDOM_SEED + level);

        inside = false;
        platforms = false;

        this.map = MapFactory.getInstance().getRandomMap(worldType, level, inside, platforms); // Inside + platforms make no sense

        setupDefaults(worldType);
        addItems(worldType, level);
        getDefaultPlayerStartPosition(worldType);

        this.map.removeAllHints();

        // Must sanity check random maps
        while (!sanityChecked()) {
            attempts++;
            map.item = new int[map.getSizeX()][map.getSizeY()];
            map.itemDirection = new int[map.getSizeX()][map.getSizeY()];
            addItems(worldType, level);
            getDefaultPlayerStartPosition(worldType);

            if (attempts > 100) throw new RuntimeException("Could not use this map...");
        }

        this.map.removeAllHints();
    }

    @Override
    public void onStart() {
    }
}
