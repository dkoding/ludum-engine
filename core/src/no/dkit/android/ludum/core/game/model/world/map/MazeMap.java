package no.dkit.android.ludum.core.game.model.world.map;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;

import java.util.Random;

public class MazeMap extends AbstractMap {
    public static void main(String[] args) {
        MazeMap td = new MazeMap();
        td.createMap(1, true, true);
    }

    public MazeMap createMap(int level, boolean inside, boolean platforms) {
        MathUtils.random = new Random(Config.RANDOM_SEED + level);

        initVariables((int) Math.pow(2, Config.MAZE_SIZE) + 1, (int) Math.pow(2, Config.MAZE_SIZE) + 1);

        clearMap();
        generateMaze(map2d, 2, sizeX < sizeY ? sizeX : sizeY,
                MathUtils.random(2, 3), sizeX * MathUtils.random(5, 10), sizeX, sizeY);

        clearOccupied(SOLID);

        directDoors();

        if (inside) {
            replaceAll(CLEAR, ROOM, map2d);
        }

        printMap(map2d);

        return this;
    }
}
