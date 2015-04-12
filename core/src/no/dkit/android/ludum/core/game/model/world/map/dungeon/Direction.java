package no.dkit.android.ludum.core.game.model.world.map.dungeon;

import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class Direction {
    private static final int[] NORTH = {-1, 0};
    private static final int[] EAST = {0, 1};
    private static final int[] SOUTH = {1, 0};
    private static final int[] WEST = {0, -1};

    public static Map<String, int[]> DIRECTIONS = new HashMap<String, int[]>(4);
    public static Map<String, String> OPPOSITE = new HashMap<String, String>(4);


    public static void initDirections() {
        DIRECTIONS.put("north", NORTH);
        DIRECTIONS.put("east", EAST);
        DIRECTIONS.put("south", SOUTH);
        DIRECTIONS.put("west", WEST);

        OPPOSITE.put("north", "south");
        OPPOSITE.put("east", "west");
        OPPOSITE.put("south", "north");
        OPPOSITE.put("west", "east");
    }

    public static String[] shuffleKeys() {
        String[] dirKeys = new String[4];
        dirKeys = DIRECTIONS.keySet().toArray(dirKeys);
        shuffleArray(dirKeys);
        return dirKeys;
    }

    static void shuffleArray(String[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = MathUtils.random.nextInt(i + 1);
            // Simple swap
            String a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    public static String[] getKeys() {
        String[] dirKeys = new String[4];
        dirKeys = DIRECTIONS.keySet().toArray(dirKeys);

        return dirKeys;
    }

}
