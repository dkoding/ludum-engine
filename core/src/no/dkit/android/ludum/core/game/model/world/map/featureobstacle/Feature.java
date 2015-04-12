package no.dkit.android.ludum.core.game.model.world.map.featureobstacle;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

import java.util.Arrays;
import java.util.List;

public class Feature {
    public static final int FEATURE_ROWS = 4;  // PARTALL!
    public static final int FEATURE_COLS = 4;
    public static final int FEATURE_NUM_ROWS = 8;
    public static final int FEATURE_NUM_COLS = 14;

    static int OBSTACLE = 999;

    public static enum DIRECTION {RIGHT, LEFT, UP, DOWN}

    public static enum FEATURE_TYPE {LRU, LR, LRUD, LRD, START, END, RANDOM}

    public int[][] tiles = new int[FEATURE_NUM_COLS][FEATURE_NUM_ROWS];
    public int[][] items = new int[FEATURE_NUM_COLS][FEATURE_NUM_ROWS];

    List<? extends Obstacle> obstacles = Arrays.asList(
            new Obstacle(),
            new Obstacle.BlockObstacle(),
            new Obstacle.ChaliceObstacle(),
            new Obstacle.CrateMineObstacle(),
            new Obstacle.CrossObstacle()
    );

    public Feature() {
        for (int c = 0; c < FEATURE_NUM_COLS; c++)
            for (int r = 0; r < FEATURE_NUM_ROWS; r++)
                tiles[c][r] = 0;

        for (int c = 0; c < FEATURE_NUM_COLS; c++)
            tiles[c][0] = tiles[c][FEATURE_NUM_ROWS - 1] = AbstractMap.SOLID;

        for (int r = 0; r < FEATURE_NUM_ROWS; r++)
            tiles[0][r] = tiles[FEATURE_NUM_COLS - 1][r] = AbstractMap.SOLID;
    }

    public void substitute() {
        for (int c = 0; c < FEATURE_NUM_COLS; c++)
            for (int r = 0; r < FEATURE_NUM_ROWS; r++) {
                if (tiles[c][r] == OBSTACLE) {
                    Obstacle obstacle = getObstacleFor(c, r);
                    obstacle.substitute();
                    obstacle.applyTiles(tiles);
                    obstacle.applyItems(items);
                }
            }
    }

    private Obstacle getObstacleFor(int c, int r) {
        int random = MathUtils.random(obstacles.size() - 1);
        try {
            return obstacles.get(random).init(c,r);
        } catch (Exception e) {
            throw new RuntimeException("DERP...");
        }
    }

    public static class LeftRight extends Feature {
        public LeftRight() {
            super();

            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;

            substitute();
        }
    }

    public static class LeftRight2 extends Feature {
        public LeftRight2() {
            super();

            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 3][FEATURE_NUM_ROWS / 4] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 3 * 2][FEATURE_NUM_ROWS / 4] = OBSTACLE;
            substitute();
        }
    }

    public static class LeftRightUp extends Feature {
        public LeftRightUp() {
            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);
            createWallOpening(DIRECTION.UP);

            tiles[FEATURE_NUM_COLS / 3][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 3 * 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;

            substitute();
        }
    }

    public static class LeftRightUp2 extends Feature {
        public LeftRightUp2() {
            super();

            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);
            createWallOpening(DIRECTION.UP);

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            substitute();
        }
    }

    public static class LeftRightDown extends Feature {
        public LeftRightDown() {
            super();

            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);
            createWallOpening(DIRECTION.DOWN);

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            substitute();
        }
    }

    public static class LeftRightDown2 extends Feature {
        public LeftRightDown2() {
            super();

            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);
            createWallOpening(DIRECTION.DOWN);

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 4][FEATURE_NUM_ROWS / 4] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 4][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            substitute();
        }
    }

    public static class LeftRightUpDown extends Feature {
        public LeftRightUpDown() {
            super();

            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);
            createWallOpening(DIRECTION.DOWN);
            createWallOpening(DIRECTION.UP);

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            substitute();
        }
    }

    public static class LeftRightUpDown2 extends Feature {
        public LeftRightUpDown2() {
            super();

            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);
            createWallOpening(DIRECTION.DOWN);
            createWallOpening(DIRECTION.UP);

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 3][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 3 * 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            substitute();
        }
    }

    public static class Random extends Feature {
        public Random() {
            if (MathUtils.random() > .5f) tiles[0][FEATURE_NUM_ROWS / 2] = 0;
            if (MathUtils.random() > .5f) tiles[FEATURE_NUM_COLS - 1][FEATURE_NUM_ROWS / 2] = 0;
            if (MathUtils.random() > .5f) tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS - 1] = 0;
            if (MathUtils.random() > .5f) tiles[FEATURE_NUM_COLS / 2][0] = 0;

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;

            substitute();
        }
    }

    public static class Random2 extends Feature {
        public Random2() {
            super();

            if (MathUtils.random() > .5f) createWallOpening(DIRECTION.RIGHT);
            if (MathUtils.random() > .5f) createWallOpening(DIRECTION.LEFT);
            if (MathUtils.random() > .5f) createWallOpening(DIRECTION.DOWN);
            if (MathUtils.random() > .5f) createWallOpening(DIRECTION.UP);

            tiles[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 4][FEATURE_NUM_ROWS / 3] = OBSTACLE;
            tiles[FEATURE_NUM_COLS / 4][FEATURE_NUM_ROWS / 3] = OBSTACLE;

            substitute();
        }
    }

    public static class Start extends Feature {
        public Start() {
            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);
            createWallOpening(DIRECTION.DOWN);
            createWallOpening(DIRECTION.UP);

            items[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = AbstractMap.START_HINT;
        }
    }

    public static class End extends Feature {
        public End() {
            createWallOpening(DIRECTION.RIGHT);
            createWallOpening(DIRECTION.LEFT);
            createWallOpening(DIRECTION.DOWN);
            createWallOpening(DIRECTION.UP);

            items[FEATURE_NUM_COLS / 2][FEATURE_NUM_ROWS / 2] = AbstractMap.END_HINT;
        }
    }

    protected void createWallOpening(DIRECTION direction) {
        int x;
        int y;
        int length;

        if (direction == DIRECTION.UP || direction == DIRECTION.DOWN)
            length = MathUtils.random(1, FEATURE_NUM_COLS / 3);
        else
            length = MathUtils.random(1, FEATURE_NUM_ROWS / 3);

        switch (direction) {
            case RIGHT:
                x = FEATURE_NUM_COLS - 1;
                y = FEATURE_NUM_ROWS / 2;
                break;
            case LEFT:
                x = 0;
                y = FEATURE_NUM_ROWS / 2;
                break;
            case UP:
                x = FEATURE_NUM_COLS / 2;
                y = 0;
                break;
            case DOWN:
            default:
                x = FEATURE_NUM_COLS / 2;
                y = FEATURE_NUM_ROWS - 1;
                break;
        }

        for (int i = 0; i < length; i++) {
            if (direction == DIRECTION.RIGHT || direction == DIRECTION.LEFT) {
                tiles[x][y + i] = AbstractMap.CLEAR;
                tiles[x][y - i] = AbstractMap.CLEAR;
            } else if (direction == DIRECTION.UP || direction == DIRECTION.DOWN) {
                tiles[x + i][y] = AbstractMap.CLEAR;
                tiles[x - i][y] = AbstractMap.CLEAR;
            }
        }
    }

    public static Feature getFeatureForType(FEATURE_TYPE type) {
        switch (type) {
            case LRUD:
                if (MathUtils.random() < .5f)
                    return new LeftRightUpDown();
                else
                    return new LeftRightUpDown2();
            case LRU:
                if (MathUtils.random() < .5f)
                    return new LeftRightUp();
                else
                    return new LeftRightUp2();
            case LRD:
                if (MathUtils.random() < .5f)
                    return new LeftRightDown();
                else
                    return new LeftRightDown2();
            case LR:
                if (MathUtils.random() < .5f)
                    return new LeftRight();
                else
                    return new LeftRight2();
            case START:
                return new Start();
            case END:
                return new End();
            case RANDOM:
            default:
                if (MathUtils.random() < .5f)
                    return new Random();
                else
                    return new Random2();
        }
    }
}
