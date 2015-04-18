package no.dkit.android.ludum.core.game.model.world.map;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.LevelFactory;

import java.util.Random;

public class CaveMap extends AbstractMap {
    public static final double DEATH_LIMIT = 3;
    public static final double BIRTH_LIMIT = 4;

    public static void main(String[] args) {
        CaveMap td = new CaveMap();
        td.createMap(1, true, true);
    }

    public CaveMap createMap(int level, boolean inside, boolean platforms) {
        int baseLevel = level;
        MathUtils.random = new Random(Config.RANDOM_SEED + baseLevel);

        initVariables(Config.CAVE_SIZE, Config.CAVE_SIZE);

        float fillProb = .4f;
        init(map2d, fillProb);
        box(SOLID);

        int numSteps = MathUtils.random(3, 4);

        for (int i = 0; i < numSteps; i++) {
            doSimulationStep();
        }

        int largest = fillSmallCaves();

        if (largest < Config.CAVE_SIZE * 5) {
            printMap(map2d);
            baseLevel++;
            return createMap(baseLevel, inside, platforms);
        }

        makeBorders(BORDER);
        padCorners(BORDER, OCCUPIED);
        replaceAll(OCCUPIED, BORDER, map2d);
        trimCorners(BORDER);
        cleanupCorners(BORDER);
        replaceAll(SOLID, OCCUPIED, map2d);
        replaceAll(BORDER, SOLID, map2d);

        clearOccupied(CLEAR);

        if (inside) {
            if (platforms)
                replaceAll(CLEAR, PLATFORM, map2d);
            else
                replaceAll(CLEAR, ROOM, map2d);
        }

        printMap(map2d);

        // TODO: Fix bug with water outside the cave
        for (int x = 1; x < sizeX - 1; x++) {
            if (map2d[x][1] == CLEAR) {
                item[x][1] = ITEM_LIQUID_SS;
                mapDirection[x-1][1] = NO_DIRECTION;
                mapDirection[x+1][1] = NO_DIRECTION;
            }
        }

        return this;
    }

    public void init(int[][] map, float fillProb) {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (MathUtils.random() < fillProb) {
                    map[x][y] = SOLID;
                }
            }
        }
    }

    public void doSimulationStep() {
        int[][] newMap = new int[sizeX][sizeY];

        //Loop over each row and column of the map
        for (int x = 0; x < map2d.length; x++) {
            for (int y = 0; y < map2d[0].length; y++) {
                int nbs = countAliveNeighbours(map2d, x, y);
                //The new value is based on our simulation rules
                //First, if a cell is alive but has too few neighbours, kill it.
                if (map2d[x][y] == SOLID) {
                    if (nbs < DEATH_LIMIT) {
                        newMap[x][y] = CLEAR;
                    } else {
                        newMap[x][y] = SOLID;
                    }
                } //Otherwise, if the cell is dead now, check if it has the right number of neighbours to be 'born'
                else {
                    if (nbs > BIRTH_LIMIT) {
                        newMap[x][y] = SOLID;
                    } else {
                        newMap[x][y] = CLEAR;
                    }
                }
            }
        }
        map2d = newMap;
    }

    public int countAliveNeighbours(int[][] map, int x, int y) {
        int count = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int neighbour_x = x + i;
                int neighbour_y = y + j;
                //If we're looking at the middle point
                if (i == 0 && j == 0) {
                    //Do nothing, we don't want to add ourselves in!
                }
                //In case the index we're looking at it off the edge of the map
                else if (neighbour_x < 0 || neighbour_y < 0 || neighbour_x >= sizeX || neighbour_y >= sizeY) {
                    count = count + 1;
                }
                //Otherwise, a normal check of the neighbour
                else if (map[neighbour_x][neighbour_y] == SOLID) {
                    count = count + 1;
                }
            }
        }

        return count;
    }
}
