package no.dkit.android.ludum.core.game.model.world.map;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.world.map.dungeon.Dungeon;

import java.util.Random;

/**
 * Generate map with a large composite structure
 */
public class DungeonMap extends AbstractMap {
    public static void main(String[] args) {
        DungeonMap td = new DungeonMap();
        td.createMap(2, true, true);
    }

    public DungeonMap createMap(int level, boolean inside, boolean platforms) {
        MathUtils.random = new Random(Config.RANDOM_SEED + level);

        initVariables(Config.DUNGEON_SIZE, Config.DUNGEON_SIZE);

        Dungeon dungeon = new Dungeon();
        dungeon.createDungeon(width, height, 10);

        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                map2d[x][y] = dungeon.getCell(x, y);
            }
        }

        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                item[x][y] = dungeon.getItem(x, y);
            }
        }

        clearOccupied(SOLID);
        directDoors();

        if (inside) {
            replaceAll(CLEAR, SOLID, map2d);
            cleanupInvisible(SOLID, CLEAR);
            replaceAll(CLEAR, OCCUPIED, map2d);
        } else {
            box(SOLID);
            trimCorners(SOLID);
        }

        if (platforms) {
            replaceAll(CORRIDOR, PLATFORM, map2d);
            directPlatforms();
        }

        printMap(map2d);

        return this;
    }
}