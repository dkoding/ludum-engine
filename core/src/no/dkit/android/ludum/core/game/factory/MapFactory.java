package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.game.model.world.map.CaveMap;
import no.dkit.android.ludum.core.game.model.world.map.CityMap;
import no.dkit.android.ludum.core.game.model.world.map.DungeonMap;
import no.dkit.android.ludum.core.game.model.world.map.MazeMap;
import no.dkit.android.ludum.core.game.model.world.map.ObstacleFeatureMap;
import no.dkit.android.ludum.core.game.model.world.map.UniverseMap;

public class MapFactory {
    static MapFactory instance;

    public static MapFactory getInstance() {
        if (instance == null)
            instance = new MapFactory();

        return instance;
    }

    public AbstractMap getRandomMap(Level.LEVEL_TYPE type, int level, boolean inside, boolean platforms) {
        AbstractMap map;
        float random = MathUtils.random();

        switch (type) {
            case UNIVERSE:
                map = new UniverseMap();
                break;
            case TOPDOWN:
                if (random > .80f)
                    map = new ObstacleFeatureMap();
                else if (random > .60f)
                    map = new CaveMap();
                else if (random > .40f)
                    map = new MazeMap();
                else if (random > .20f)
                    map = new CityMap();
                else
                    map = new DungeonMap();
                break;
            case SIDESCROLL:
                if (random > .80f)
                    map = new ObstacleFeatureMap();
                else if (random > .60f)
                    map = new CaveMap();
                else if (random > .40f)
                    map = new MazeMap();
                else if (random > .20f)
                    map = new CityMap();
                else
                    map = new DungeonMap();
                break;
            default:
                throw new RuntimeException("This should not happen...");
        }

        map.createMap(level, inside, platforms);

        System.out.println("CREATED " + map.getClass().getName() + " MAP WITH LEVEL CODE: " + type + ":" + level + (inside ? " (inside)" : ""));

        return map;
    }

    public void dispose() {

    }
}
