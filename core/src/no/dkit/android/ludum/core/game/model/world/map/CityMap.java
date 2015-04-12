package no.dkit.android.ludum.core.game.model.world.map;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.world.map.dungeon.Cell;
import no.dkit.android.ludum.core.game.model.world.map.dungeon.City;

import java.util.Random;

/**
 * Generate a map with several structures separated by roads/corridors.
 */
public class CityMap extends AbstractMap {
    public static void main(String[] args) {
        CityMap td = new CityMap();
        td.createMap(3, true, true);
    }

    public CityMap createMap(int level, boolean inside, boolean platforms) {
        MathUtils.random = new Random(Config.RANDOM_SEED + level);

        initVariables(Config.CITY_SIZE, Config.CITY_SIZE);

        float corridorChance = MathUtils.random();
        int type;

        if (corridorChance < .33f)
            type = City.CORRIDOR_BENT;
        else if (corridorChance < .5f)
            type = City.CORRIDOR_LABYRINTH;
        else
            type = City.CORRIDOR_STRAIGHT;

        City dungeon = new City(width - 1, height - 1, type);
        Cell[][] cells = dungeon.getCells();
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                map2d[x][y] = cells[x][y].getType();
            }
        }

        clearOccupied(SOLID);
        directDoors();

        if (inside) {
            replaceAll(BORDER, CLEAR, map2d);
            replaceAll(SOLID, OCCUPIED, map2d);
            replaceAll(CLEAR, SOLID, map2d);
            cleanupInvisible(SOLID, CLEAR);
            replaceAll(OCCUPIED, SOLID, map2d);
            replaceAll(CLEAR, OCCUPIED, map2d);
        } else {
            box(SOLID);
        }

        if (platforms) {
            replaceAll(CORRIDOR, PLATFORM, map2d);
            directPlatforms();
        }

        printMap(map2d);

        return this;
    }
}