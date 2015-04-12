package no.dkit.android.ludum.core.game.model.world.map.featureobstacle;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

public class Obstacle {
    static final int OBSTACLE_SIZE_X = 4; // Partall!
    static final int OBSTACLE_SIZE_Y = 4;

    // To not interfere with map values
    static final int SOLID_RANDOM25 = 100;
    static final int SOLID_RANDOM50 = 101;
    static final int SOLID_RANDOM75 = 102;
    static final int MINE_RANDOM25 = 103;
    static final int MINE_RANDOM50 = 104;
    static final int MINE_RANDOM75 = 105;
    static final int CRATE_RANDOM25 = 106;
    static final int CRATE_RANDOM50 = 107;
    static final int CRATE_RANDOM75 = 108;

    int[][] cells = new int[OBSTACLE_SIZE_X][OBSTACLE_SIZE_Y];

    private int c;
    private int r;

    public Obstacle() {
        cells = new int[][]{
                {SOLID_RANDOM25, SOLID_RANDOM50, SOLID_RANDOM25, SOLID_RANDOM50},
                {SOLID_RANDOM50, SOLID_RANDOM75, SOLID_RANDOM50, SOLID_RANDOM50},
                {SOLID_RANDOM25, SOLID_RANDOM50, SOLID_RANDOM25, SOLID_RANDOM50},
                {SOLID_RANDOM25, SOLID_RANDOM50, SOLID_RANDOM25, SOLID_RANDOM50},
        };
    }

    public Obstacle init(int c, int r) {
        this.c = c;
        this.r = r;
        return this;
    }

    public void applyTiles(int[][] target) {
        int startX = c - (OBSTACLE_SIZE_X / 2);
        int startY = r - (OBSTACLE_SIZE_Y / 2);

        for (int r = 0; r < OBSTACLE_SIZE_Y; r++)
            for (int c = 0; c < OBSTACLE_SIZE_X; c++)
                if (cells[c][r] < AbstractMap.FIRST_ITEM)
                    target[c + startX][r + startY] = cells[c][r];
    }

    public void applyItems(int[][] target) {
        int startX = c - (OBSTACLE_SIZE_X / 2);
        int startY = r - (OBSTACLE_SIZE_Y / 2);

        for (int r = 0; r < OBSTACLE_SIZE_Y; r++)
            for (int c = 0; c < OBSTACLE_SIZE_X; c++)
                if (cells[c][r] >= AbstractMap.FIRST_ITEM && cells[c][r] <= AbstractMap.LAST_ITEM)
                    target[c + startX][r + startY] = cells[c][r];
    }

    public void substitute() {
        for (int r = 0; r < OBSTACLE_SIZE_Y; r++)
            for (int c = 0; c < OBSTACLE_SIZE_X; c++) {
                float random = MathUtils.random();

                switch (cells[c][r]) {
                    case SOLID_RANDOM25:
                        cells[c][r] = random < .25 ? AbstractMap.SOLID : AbstractMap.CLEAR;
                        break;
                    case SOLID_RANDOM50:
                        cells[c][r] = random < .50 ? AbstractMap.SOLID : AbstractMap.CLEAR;
                        break;
                    case SOLID_RANDOM75:
                        cells[c][r] = random < .75 ? AbstractMap.SOLID : AbstractMap.CLEAR;
                        break;
                    case CRATE_RANDOM25:
                        cells[c][r] = random < .25 ? AbstractMap.ITEM_CRATE : AbstractMap.CLEAR;
                        break;
                    case CRATE_RANDOM50:
                        cells[c][r] = random < .50 ? AbstractMap.ITEM_CRATE : AbstractMap.CLEAR;
                        break;
                    case CRATE_RANDOM75:
                        cells[c][r] = random < .75 ? AbstractMap.ITEM_CRATE : AbstractMap.CLEAR;
                        break;
                    case MINE_RANDOM25:
                        cells[c][r] = random < .25 ? AbstractMap.ITEM_MINE : AbstractMap.CLEAR;
                        break;
                    case MINE_RANDOM50:
                        cells[c][r] = random < .50 ? AbstractMap.ITEM_MINE : AbstractMap.CLEAR;
                        break;
                    case MINE_RANDOM75:
                        cells[c][r] = random < .75 ? AbstractMap.ITEM_MINE : AbstractMap.CLEAR;
                        break;
                    default:
                        break;
                }
            }
    }

    public static class CrateMineObstacle extends Obstacle {
        public CrateMineObstacle() {
            cells = new int[][]{
                    {AbstractMap.CLEAR, CRATE_RANDOM50, SOLID_RANDOM50, SOLID_RANDOM50},
                    {AbstractMap.CLEAR, AbstractMap.CLEAR, MINE_RANDOM50, MINE_RANDOM50},
                    {AbstractMap.CLEAR, CRATE_RANDOM50, SOLID_RANDOM50, SOLID_RANDOM50},
                    {AbstractMap.CLEAR, CRATE_RANDOM50, SOLID_RANDOM50, SOLID_RANDOM50},
            };
        }

        public Obstacle init(int c, int r) {
            return super.init(c, r);
        }
    }

    public static class BlockObstacle extends Obstacle {
        public BlockObstacle() {
            cells = new int[][]{
                    {AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.SOLID, AbstractMap.SOLID},
                    {AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.SOLID, AbstractMap.SOLID},
                    {AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.SOLID, AbstractMap.SOLID},
                    {AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.SOLID, AbstractMap.SOLID},
            };
        }

        public Obstacle init(int c, int r) {
            return super.init(c, r);
        }
    }

    public static class CrossObstacle extends Obstacle {
        public CrossObstacle() {
            cells = new int[][]{
                    {AbstractMap.CLEAR, AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.CLEAR},
                    {AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.SOLID, AbstractMap.SOLID},
                    {AbstractMap.CLEAR, AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.CLEAR},
                    {AbstractMap.CLEAR, AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.CLEAR},
            };
        }

        public Obstacle init(int c, int r) {
            return super.init(c, r);
        }
    }

    public static class ChaliceObstacle extends Obstacle {
        public ChaliceObstacle() {
            cells = new int[][]{
                    {AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.SOLID, AbstractMap.SOLID},
                    {AbstractMap.CLEAR, AbstractMap.CLEAR, AbstractMap.CLEAR, AbstractMap.SOLID},
                    {AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.SOLID, AbstractMap.SOLID},
                    {AbstractMap.CLEAR, AbstractMap.SOLID, AbstractMap.SOLID, AbstractMap.SOLID},
            };
        }

        public Obstacle init(int c, int r) {
            return super.init(c, r);
        }
    }
}
