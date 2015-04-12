package no.dkit.android.ludum.core.game.model.world.map;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.world.map.featureobstacle.Feature;
import no.dkit.android.ludum.core.game.model.world.map.featureobstacle.FeatureMap;

import java.util.Random;

public class ObstacleFeatureMap extends AbstractMap {
    public static void main(String[] args) {
        ObstacleFeatureMap sd = new ObstacleFeatureMap();
        sd.createMap(1, true, true);
    }

    public ObstacleFeatureMap createMap(int level, boolean inside, boolean platforms) {
        MathUtils.random = new Random(Config.RANDOM_SEED + level);

        initVariables(Feature.FEATURE_COLS * Feature.FEATURE_NUM_COLS, Feature.FEATURE_ROWS * Feature.FEATURE_NUM_ROWS);

        clearMap();

        int startCol = (int) (MathUtils.random() * Feature.FEATURE_COLS);
        int startRow = (int) (MathUtils.random() * Feature.FEATURE_ROWS);

        start = new int[]{startCol, startRow};
        makePath(path, startCol, startRow, Feature.FEATURE_COLS - 1, Feature.FEATURE_ROWS - 1);
        path[start[0]][start[1]] = START;

        makeFeaturesFromPath();

        new FeatureMap(feature, map2d, item);

        box(SOLID);

        clearOccupied(CLEAR);

        if(!inside)
            trimCorners(SOLID);

        if (inside)
            replaceAll(CLEAR, ROOM, map2d);

        if(platforms) {
            replaceAll(CORRIDOR, PLATFORM, map2d);
            directPlatforms();
        }

        printMap(map2d);

        return this;
    }

    @Override
    protected void occupySurroundingSpaces(int x, int y) {
    }
}
