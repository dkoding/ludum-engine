package no.dkit.android.ludum.core.game.model.world.map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.model.world.map.featureobstacle.Feature;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractMap {
    protected static int maxValue = 18;

    protected static final int caveSizeVariation = 3;
    protected static final int caveMinimumSize = 3;

    public float startX;
    public float startY;

    private float startPosX;
    private float startPosY;

    protected int width;
    protected int height;

    protected static final int START = 10;
    protected static final int END = 99;

    public static final int NO_DIRECTION = 0;

    public static final int E = 1;
    public static final int W = 2;
    public static final int N = 3;
    public static final int S = 4;

    public static final int NE = 5;
    public static final int NW = 6;
    public static final int SE = 7;
    public static final int SW = 8;

    static protected int[][] path = new int[Feature.FEATURE_COLS][Feature.FEATURE_ROWS];
    static protected Feature[][] feature = new Feature[Feature.FEATURE_COLS][Feature.FEATURE_ROWS];

    protected static int[] start = new int[2];
    protected static int[] end = new int[2];

    public static final int END_HINT = -3; // Empty, but unavailable
    public static final int START_HINT = -2; // Empty, but unavailable
    public static final int OCCUPIED = -1; // Empty, but unavailable
    public static final int OUTSIDEBORDERS = -4; // Empty, but unavailable

    public static final int CLEAR = 0; // Empty space, outdoor
    public static final int CHASM = 3;
    public static final int PLATFORM = 4;
    public static final int BORDER = 5; // Unpassable space
    public static final int SOLID = 6; // Unpassable space
    public static final int DOOR = 7;
    public static final int CORRIDOR = 8;
    public static final int ROOM = 9;

    public static final int FIRST_ITEM = 10; // Collectable
    public static final int ITEM_ROCK = 10; // Collectable
    public static final int ITEM_SPAWN = 11; // Monster spawn
    public static final int ITEM_FEATURE = 12;
    /*
        public static final int ITEM_REWARD = 13; // Pickup
    */
    public static final int ITEM_CANNON = 14; // Cannon
    public static final int ITEM_UPGRADE = 15; // Upgrade
    public static final int ITEM_ENTRANCE_SURFACE = 16; // Exit
    public static final int ITEM_KEY = 17; // Key
    public static final int ITEM_ENTRANCE_CAVE = 18; // Exit
    public static final int ITEM_ENTRANCE_UNIVERSE = 19; // Exit
    public static final int ITEM_LAMP = 20;
    public static final int ITEM_MINE = 21;
    public static final int ITEM_LOOT = 22;
    public static final int ITEM_CRATE = 23;
    public static final int ITEM_TRIGGER = 24;
    public static final int ITEM_LIQUID_TD = 26;
    public static final int ITEM_LIQUID_SS = 27;
    public static final int LAST_ITEM = 27;

    protected int sizeX;
    protected int sizeY;

    public int[][] map2d;
    public int[][] mapDirection;
    public int[][] item;
    public int[][] itemDirection;
    protected boolean[][] visited;

    protected void initVariables(int sizeX, int sizeY) {
        startX = 0;
        startY = 0;

        this.sizeX = sizeX;
        this.sizeY = sizeY;

        width = sizeX;
        height = sizeY;

        map2d = new int[sizeX][sizeY];
        mapDirection = new int[sizeX][sizeY];
        item = new int[sizeX][sizeY];
        itemDirection = new int[sizeX][sizeY];
        visited = new boolean[sizeX][sizeY];

        path = new int[Feature.FEATURE_COLS][Feature.FEATURE_ROWS];
        feature = new Feature[Feature.FEATURE_COLS][Feature.FEATURE_ROWS];
    }

    public static int[][] rotateMatrixLeft(int[][] matrix) {
        /* W and H are already swapped */
        int w = matrix.length;
        int h = matrix[0].length;
        int[][] ret = new int[h][w];
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                ret[i][j] = matrix[j][h - i - 1];
            }
        }
        return ret;
    }

    public abstract AbstractMap createMap(int level, boolean inside, boolean platforms);

    protected void clearOccupied(int clear) {
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                if (map2d[x][y] == AbstractMap.OCCUPIED) map2d[x][y] = clear;
    }

    protected void clearItemOccupied(int clear) {
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                if (map2d[x][y] == AbstractMap.OCCUPIED) map2d[x][y] = clear;
    }

    protected void replaceAll(int from, int to, int[][] array) {
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                if (array[x][y] == from) array[x][y] = to;
    }

    protected void upgrade(int upgradeType) {
        int shopX = MathUtils.random(1, sizeX - 2);
        int shopY = MathUtils.random(1, sizeY - 2);

        map2d[shopX][shopY] = OCCUPIED;
        item[shopX][shopY] = upgradeType;
        occupySurroundingSpaces(shopX, shopY);
    }

    protected int keys(int maxKeys) {
        int currentAttempt = 0;
        int maxAttempts = 1000; // Safeguard
        int numKeys = 0;
        while (numKeys < maxKeys && currentAttempt < maxAttempts) {
            currentAttempt++;
            int keyX = MathUtils.random(1, sizeY - 2);
            int keyY = MathUtils.random(1, sizeY - 2);

            if (currentAttempt < maxAttempts / 2) {
                if (map2d[keyX][keyY - 1] > AbstractMap.CLEAR && map2d[keyX][keyY - 1] <= SOLID && map2d[keyX][keyY] == AbstractMap.CLEAR) {
                    map2d[keyX][keyY] = AbstractMap.OCCUPIED;
                    item[keyX][keyY] = AbstractMap.ITEM_KEY;
                    numKeys++;
                }
            } else {
                keyX = MathUtils.random(1, sizeX - 2);
                keyY = MathUtils.random(1, sizeY - 2);

                if (map2d[keyX][keyY - 1] > AbstractMap.CLEAR && map2d[keyX][keyY - 1] <= SOLID && map2d[keyX][keyY] == AbstractMap.CLEAR) {
                    map2d[keyX][keyY] = AbstractMap.OCCUPIED;
                    item[keyX][keyY] = AbstractMap.ITEM_KEY;
                    numKeys++;
                }
            }
        }

        return numKeys;
    }

    protected void occupySurroundingSpaces(int x, int y) {
        map2d[x - 1][y - 1] = AbstractMap.OCCUPIED;
        map2d[x - 1][y] = AbstractMap.OCCUPIED;
        map2d[x - 1][y + 1] = AbstractMap.OCCUPIED;

        map2d[x + 1][y - 1] = AbstractMap.OCCUPIED;
        map2d[x + 1][y] = AbstractMap.OCCUPIED;
        map2d[x + 1][y + 1] = AbstractMap.OCCUPIED;

        map2d[x][y - 1] = AbstractMap.OCCUPIED;
        map2d[x][y + 1] = AbstractMap.OCCUPIED;
    }

    protected int placeItemsWithinEmptySpace(int maxItems, int itemType, int clear) {
        int numItems = 0;
        int currentAttempt = 0;
        int maxAttempts = 1000; // Safeguard

        while (numItems < maxItems && currentAttempt < maxAttempts) {
            currentAttempt++;

            int x = MathUtils.random(2, sizeX - 2);
            int y = MathUtils.random(2, sizeY - 2);

            if (currentAttempt < maxAttempts / 2) {
                if (map2d[x][y] == clear && map2d[x - 1][y] == clear && map2d[x + 1][y] == clear && map2d[x][y - 1] == clear && map2d[x][y + 1] == clear
                        && map2d[x - 1][y - 1] == clear && map2d[x + 1][y + 1] == clear && map2d[x + 1][y - 1] == clear && map2d[x - 1][y + 1] == clear) {
                    map2d[x][y] = OCCUPIED;
                    item[x][y] = itemType;
                    numItems++;
                }
            } else {
                if (map2d[x][y] == clear) {
                    map2d[x][y] = OCCUPIED;
                    item[x][y] = itemType;
                    numItems++;
                }
            }
        }

        return numItems;
    }

    protected int placeItems(int maxItems, int itemType, int clear) {
        int numItems = 0;
        int currentAttempt = 0;
        int maxAttempts = 1000; // Safeguard

        while (numItems < maxItems && currentAttempt < maxAttempts) {
            currentAttempt++;

            int x = MathUtils.random(2, sizeX - 2);
            int y = MathUtils.random(2, sizeY - 2);

            if (map2d[x][y] == clear) {
                map2d[x][y] = OCCUPIED;
                item[x][y] = itemType;
                numItems++;
            }
        }

        return numItems;
    }

    protected int replaceSomeTiles(int maxItems, int fromTile, int toTile) {
        int numItems = 0;
        int currentAttempt = 0;
        int maxAttempts = 1000; // Safeguard

        while (numItems < maxItems && currentAttempt < maxAttempts) {
            currentAttempt++;

            int x = MathUtils.random(2, sizeX - 2);
            int y = MathUtils.random(2, sizeY - 2);

            if (map2d[x][y] == fromTile) {
                map2d[x][y] = toTile;
                numItems++;
            }
        }

        return numItems;
    }

    protected int placeItemsEncasedFacing(int maxItems, int itemType, int dir, int clear, int solid) {
        int numItems = 0;
        int currentAttempt = 0;
        int maxAttempts = 1000; // Safeguard

        if (dir == N) {
            while (numItems < maxItems && currentAttempt < maxAttempts) {
                int x = MathUtils.random(1, sizeX - 2);
                int y = MathUtils.random(1, sizeY - 2);

                currentAttempt++;

                if (map2d[x][y] == solid && map2d[x][y + 1] == clear && map2d[x + 1][y] == solid && map2d[x - 1][y] == solid && map2d[x][y - 1] == solid) {
                    map2d[x][y] = OCCUPIED;
                    item[x][y] = itemType;
                    //direction[x][y] = dir;
                    numItems++;
                }
            }
        } else {
            throw new RuntimeException("NOT IMPLEMENTED YET!");
        }

        return numItems;
    }

    protected int placeItemsOnSurface(int maxItems, int itemType, int dir, int clear, int solid) {
        int numItems = 0;
        int currentAttempt = 0;
        int maxAttempts = 1000; // Safeguard

        while (numItems < maxItems && currentAttempt < maxAttempts) {
            int x = MathUtils.random(1, sizeX - 2);
            int y = MathUtils.random(1, sizeY - 2);

            int xOffset = x;
            int yOffset = y;

            if (dir == N) yOffset--;
            if (dir == S) yOffset++;
            if (dir == W) xOffset--;
            if (dir == E) xOffset++;

            currentAttempt++;

            if (map2d[xOffset][yOffset] == solid && mapDirection[xOffset][yOffset] < NE && map2d[x][y] == clear) {
                map2d[x][y] = OCCUPIED;
                item[x][y] = itemType;
                itemDirection[x][y] = dir;
                numItems++;
            }
        }

        return numItems;
    }

    protected void box(int solid) {
        for (int x = 0; x < sizeX; x++) map2d[x][0] = map2d[x][sizeY - 1] = solid;
        for (int y = 0; y < sizeY; y++) map2d[0][y] = map2d[sizeX - 1][y] = solid;
    }

    protected void mazeBox(int[][] tileMap, int tileType) {
        for (int x = 0; x < sizeX; x++) tileMap[x][0] = tileMap[x][sizeX - 1] = tileType;
        for (int y = 0; y < sizeY; y++) tileMap[0][y] = tileMap[sizeY - 1][y] = tileType;
    }

    protected void combine() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (visited[x][y]) map2d[x][y] = CLEAR;
                if (map2d[x][y] == CLEAR) map2d[x][y] = AbstractMap.CLEAR;
            }
        }
    }

    protected void squareDiamond() {
        float h = maxValue / 2f;

        for (int sideLength = sizeX - 1; sideLength >= 2; sideLength /= 2, h /= 2.0) {
            int halfSide = sideLength / 2;

            for (int x = 0; x < sizeX - 1; x += sideLength) {
                for (int y = 0; y < sizeY - 1; y += sideLength) {
                    double avg = map2d[x][y] +
                            map2d[x + sideLength][y] +
                            map2d[x][y + sideLength] +
                            map2d[x + sideLength][y + sideLength];
                    avg /= 4.0;

                    assignValueToMap(x + halfSide, y + halfSide, (int) (avg + MathUtils.random(2 * h) - h));
                }
            }

            for (int x = 0; x < sizeX - 1; x += halfSide) {
                for (int y = (x + halfSide) % sideLength; y < sizeY - 1; y += sideLength) {
                    double avg =
                            map2d[(x - halfSide + sizeX) % sizeX][y] +
                                    map2d[(x + halfSide) % sizeY][y] +
                                    map2d[x][(y + halfSide) % sizeX] +
                                    map2d[x][(y - halfSide + sizeY) % sizeY];
                    avg /= 4.0;
                    avg = avg + MathUtils.random(2 * h) - h;
                    assignValueToMap(x, y, (int) avg);
                    if (x == 0) assignValueToMap(sizeX - 1, y, (int) avg);
                    if (y == 0) assignValueToMap(x, sizeY - 1, (int) avg);
                }
            }
        }
    }

    protected void assignValueToMap(int x, int y, int avg) {
        if (avg > maxValue / 2) avg = maxValue / 2;
        if (avg < 1) avg = 1;

        map2d[x][y] = avg;
    }

    protected void initMap(int value) {
        map2d[0][0] = value;
        map2d[0][sizeY - 1] = value;
        map2d[sizeX - 1][sizeY - 1] = value;
        map2d[sizeX - 1][0] = value;
    }

    public void printMap() {
        printMap(map2d);
    }

    protected void printMap(int[][] map) {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++)
                printChar(map[x][y], mapDirection[x][y]);
            System.out.println();
        }
    }

    private static void printChar(int i) {
        printChar(i, NO_DIRECTION);
    }

    private static void printChar(int i, int direction) {
/*
        System.out.print(i);
        if(true) return;
*/

        switch (i) {
            case SOLID:
                if (direction == NE || direction == SW)
                    System.out.print("/");
                else if (direction == NW || direction == SE)
                    System.out.print("\\");
                else
                    System.out.print("#");
                break;
            case BORDER:
            case PLATFORM:
                if (direction == NE || direction == SW)
                    System.out.print("/");
                else if (direction == NW || direction == SE)
                    System.out.print("\\");
                else if (direction == N || direction == S)
                    System.out.print("|");
                else if (direction == E || direction == W)
                    System.out.print("-");
                else
                    System.out.print("+");
                break;
            case CLEAR:
                System.out.print(" ");
                break;
            case OCCUPIED:
                System.out.print("@");
                break;
            case OUTSIDEBORDERS:
                System.out.print("O");
                break;
            case CORRIDOR:
                System.out.print("C");
                break;
            case ROOM:
                System.out.print(".");
                break;
            case DOOR:
                System.out.print("D");
                break;
            default:
                System.out.print("?");
        }
    }

    protected void clearMap() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                map2d[x][y] = AbstractMap.CLEAR;
            }
        }
    }

    protected void generateCaves(boolean[][] visited) {
        for (int i = 0; i < sizeX; i++) {
            int x = MathUtils.random(sizeX - 1);
            int y = MathUtils.random(sizeY - 1);
            if (!visited[x][y]) {
                generate(x, y, MathUtils.random(caveSizeVariation) + caveMinimumSize);
                if (startPosX == 0 && startPosY == 0) {
                    startPosX = x;
                    startPosY = y;
                }
            }
        }
    }

    protected void generate(int x, int y, int length) {
        length--;

        if (x < 0 || y < 0 || x > sizeX - 1 || y > sizeY - 1 || length == 0) return;

        visited[x][y] = true;

        float random = MathUtils.random();

        if (random < .25) {
            generate(x + 1, y, length);
            generate(x + 1, y + 1, length);
        } else if (random < .50) {
            generate(x - 1, y, length);
            generate(x - 1, y + 1, length);
        } else if (random < .75) {
            generate(x, y + 1, length);
            generate(x - 1, y + 1, length);
        } else {
            generate(x, y - 1, length);
            generate(x + 1, y - 1, length);
        }
    }

    public void pan(float deltaX, float deltaY) {
        if (startX + deltaX < 0) startX = -deltaX;
        if (startY + deltaY < 0) startY = -deltaY;
        if (startX + deltaX > (sizeX - width)) startX = sizeX - width - deltaX;
        if (startY + deltaY > (sizeY - height)) startY = sizeY - height - deltaY;

        startX += deltaX;
        startY += deltaY;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void generateMaze(int[][] tileMap, int minLength, int maxLength, int granularity, int numWalls, int numRows, int numCols) {
        mazeBox(tileMap, AbstractMap.OCCUPIED);

        for (int count = 0; count < numWalls; count++) {
            attemptRandomWall(tileMap, minLength, maxLength, granularity, numRows, numCols, AbstractMap.OCCUPIED);
        }
    }

    public void attemptRandomWall(int[][] tileMap, int minLength, int maxlength, int granularity, int numRows, int numCols, int wallType) {
        Vector2 point = selectWallStart(granularity, numRows, numCols);

        int col = (int) point.x;
        int row = (int) point.y;

        if (tileMap[row][col] == wallType) return;

        int dir = MathUtils.random(3);
        int len = minLength + MathUtils.random(maxlength - minLength);
        len = len * granularity + 1;
        createWall(tileMap, row, col, dir, len, wallType);
    }

    public Vector2 selectWallStart(int granularity, int numRows, int numCols) {
        int colSpacing = numCols / granularity;
        int rowSpacing = numRows / granularity;
        int startCol = granularity * MathUtils.random(colSpacing);
        int startRow = granularity * MathUtils.random(rowSpacing);
        return new Vector2(startCol, startRow);
    }

    public void createWall(int[][] tileMap, int row, int col, int dir, int len, int wallType) {
        int colStep = 0;
        int rowStep = 0;

        if (dir == 0) {
            colStep = 0;
            rowStep = -1;
        }
        if (dir == 1) {
            colStep = -1;
            rowStep = 0;
        }
        if (dir == 2) {
            colStep = 1;
            rowStep = 0;
        }
        if (dir == 3) {
            colStep = 0;
            rowStep = 1;
        }

        for (int i = 1; i < len; i++) {
            if (tileMap[row][col] == wallType) return;
            tileMap[row][col] = wallType;
            col += colStep;
            row += rowStep;
        }
    }

    protected void makeFeaturesFromPath() {
        for (int r = 0; r < Feature.FEATURE_ROWS; r++)
            for (int c = 0; c < Feature.FEATURE_COLS; c++) {
                feature[c][r] = getFeatureFor(c, r);
            }
    }

    protected static void printPath() {
        for (int r = 0; r < Feature.FEATURE_ROWS; r++) {
            for (int c = 0; c < Feature.FEATURE_COLS; c++)
                printChar(path[c][r]);
            System.out.println();
        }
    }

    protected static void makePath(int[][] path, int col, int row, int maxCol, int maxRow) {
        boolean free = false;
        int counter = 0;
        int direction = 0;

        while (!free) {
            counter++;
            direction = MathUtils.random(1, 4);

            if ((direction == E && col < maxCol && path[col + 1][row] == 0)
                    || (direction == W && col > 0 && path[col - 1][row] == 0)
                    || (direction == S && row < maxRow && path[col][row + 1] == 0)
                    || (direction == N && row > 0 && path[col][row - 1] == 0)) {
                free = true;
                counter = 0;
            }

            if (counter == 100) {
                path[col][row] = END;
                return;
            }
        }

        path[col][row] = direction;

        if (direction == E) col++;
        else if (direction == W) col--;
        else if (direction == N) row--;
        else if (direction == S) row++;

        makePath(path, col, row, maxCol, maxRow);
    }

    public Feature getFeatureFor(int c, int r) {
        int direction = path[c][r];

        boolean exitAbove = false;
        boolean exitBelow = false;

        if (r > 0 && (path[c][r - 1] == S || path[c][r - 1] == END || path[c][r - 1] == START)) exitAbove = true;
        if (r < Feature.FEATURE_ROWS - 1 && (path[c][r + 1] == N || path[c][r + 1] == END || path[c][r + 1] == START))
            exitBelow = true;

        Feature feature;

        switch (direction) {
            case E:
                if (exitAbove && exitBelow) feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.LRUD);
                else if (exitAbove) feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.LRU);
                else if (exitBelow) feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.LRD);
                else feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.LR);
                break;
            case W:
                if (exitAbove && exitBelow) feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.LRUD);
                else if (exitAbove) feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.LRU);
                else if (exitBelow) feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.LRD);
                else feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.LR);
                break;
            case N:
                feature = exitBelow ? Feature.getFeatureForType(Feature.FEATURE_TYPE.LRUD) : Feature.getFeatureForType(Feature.FEATURE_TYPE.LRU);
                break;
            case S:
                feature = exitAbove ? Feature.getFeatureForType(Feature.FEATURE_TYPE.LRUD) : Feature.getFeatureForType(Feature.FEATURE_TYPE.LRD);
                break;
            case START:
                feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.START);
                break;
            case END:
                feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.END);
                break;
            default:
                feature = Feature.getFeatureForType(Feature.FEATURE_TYPE.RANDOM);
        }

        return feature;
    }

    protected int fillSmallCaves() {
        class Cave {
            int startX, startY, size;

            private Cave(int startX, int startY, int size) {
                this.startX = startX;
                this.startY = startY;
                this.size = size;
            }
        }

        setStartPositionToTile(CLEAR);

        int count = floodfill(start[0], start[1], OCCUPIED, CLEAR);
        int largest = count;

        Array<Cave> caves = new Array<Cave>();
        caves.add(new Cave(start[0], start[1], count));

        while (count > 0) {
            setStartPositionToTile(CLEAR);
            count = floodfill(start[0], start[1], OCCUPIED, CLEAR);
            if (count > 0)
                caves.add(new Cave(start[0], start[1], count));
            if (count > largest) largest = count;
        }

        clearOccupied(CLEAR);

        if (caves.size > 1)
            for (Cave cave : caves) {
                if (cave.size < largest)
                    floodfill(cave.startX, cave.startY, SOLID, CLEAR);
            }

        return largest;
    }

    final int getEndFeature() {
        return -1;
    }

    final int getStartFeature() {
        return -1;
    }

    protected void setStartPositionToTile(int feature) {
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                if (map2d[x][y] == feature) {
                    start = new int[]{x, y};
                    break;
                }
    }

    protected int floodfill(int startX, int startY, int replacementColor, int targetColor) {
        int count = 0;

        class Point {
            int x, y;

            Point(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        Point node = new Point(startX, startY);

        int width = sizeX;
        int height = sizeY;

        if (targetColor != replacementColor) {
            Queue<Point> queue = new LinkedList<Point>();
            do {

                int x = node.x;
                int y = node.y;
                while (x > 0 && map2d[x - 1][y] == targetColor) {
                    x--;
                }

                boolean spanUp = false;
                boolean spanDown = false;
                while (x < width && map2d[x][y] == targetColor) {
                    map2d[x][y] = replacementColor;
                    count++;
                    if (!spanUp && y > 0 && map2d[x][y - 1] == targetColor) {
                        queue.add(new Point(x, y - 1));
                        spanUp = true;
                    } else if (spanUp && y > 0 && map2d[x][y - 1] != targetColor) {
                        spanUp = false;
                    }
                    if (!spanDown && y < height - 1 && map2d[x][y + 1] == targetColor) {
                        queue.add(new Point(x, y + 1));
                        spanDown = true;
                    } else if (spanDown && y < (height - 1) && map2d[x][y + 1] != targetColor) {
                        spanDown = false;
                    }
                    x++;
                }

            } while ((node = queue.poll()) != null);
        }

        return count;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected void makeBorders(int borderFeature) {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (x < sizeX - 1 && map2d[x + 1][y] == CLEAR && map2d[x][y] == SOLID) map2d[x][y] = borderFeature;
                if (y < sizeY - 1 && map2d[x][y + 1] == CLEAR && map2d[x][y] == SOLID) map2d[x][y] = borderFeature;
                if (x > 0 && map2d[x - 1][y] == CLEAR && map2d[x][y] == SOLID) map2d[x][y] = borderFeature;
                if (y > 0 && map2d[x][y - 1] == CLEAR && map2d[x][y] == SOLID) map2d[x][y] = borderFeature;
            }
        }
    }

    protected void padCorners(int borderFeature, int toFeature) {
        int north, south, east, west;

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (map2d[x][y] == SOLID) {
                    if (y > 0) north = map2d[x][y - 1];
                    else north = -1;
                    if (y < sizeY - 1) south = map2d[x][y + 1];
                    else south = -1;
                    if (x < sizeX - 1) east = map2d[x + 1][y];
                    else east = -1;
                    if (x > 0) west = map2d[x - 1][y];
                    else west = -1;

                    if (east == borderFeature && south == borderFeature) {
                        map2d[x][y] = toFeature;
                        mapDirection[x][y] = NE;
                    } else if (west == borderFeature && south == borderFeature) {
                        map2d[x][y] = toFeature;
                        mapDirection[x][y] = NW;
                    } else if (east == borderFeature && north == borderFeature) {
                        map2d[x][y] = toFeature;
                        mapDirection[x][y] = SE;
                    } else if (west == borderFeature && north == borderFeature) {
                        map2d[x][y] = toFeature;
                        mapDirection[x][y] = SW;
                    }
                }
            }
        }
    }

    protected void cleanupCorners(int borderFeature) {
        int north, south, east, west;

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (mapDirection[x][y] != NO_DIRECTION) {
                    if (y > 0) north = map2d[x][y - 1];
                    else north = -1;
                    if (y < sizeY - 1) south = map2d[x][y + 1];
                    else south = -1;
                    if (x < sizeX - 1) east = map2d[x + 1][y];
                    else east = -1;
                    if (x > 0) west = map2d[x - 1][y];
                    else west = -1;

                    int numOccupied = 0;

                    if (west == borderFeature) numOccupied++;
                    if (east == borderFeature) numOccupied++;
                    if (north == borderFeature) numOccupied++;
                    if (south == borderFeature) numOccupied++;

                    if (numOccupied > 2) {
                        mapDirection[x][y] = NO_DIRECTION;
                    }
                }
            }
        }
    }

    protected void trimCorners(int border) {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (map2d[x][y] != border) continue;

                int west = map2d[x - 1][y];
                int east = map2d[x + 1][y];
                int north = map2d[x][y - 1];
                int south = map2d[x][y + 1];
                int nw = map2d[x - 1][y - 1];
                int ne = map2d[x + 1][y - 1];
                int sw = map2d[x - 1][y + 1];
                int se = map2d[x - 1][y + 1];

                int numClear = 0;

                if (west == CLEAR) numClear++;
                if (east == CLEAR) numClear++;
                if (north == CLEAR) numClear++;
                if (south == CLEAR) numClear++;
                if (numClear > 2) {
                    // Do nothing...
                } else if (west == CLEAR && nw == CLEAR && north == CLEAR) {
                    mapDirection[x][y] = NE;
                } else if (east == CLEAR && ne == CLEAR && north == CLEAR) {
                    mapDirection[x][y] = NW;
                } else if (west == CLEAR && sw == CLEAR && south == CLEAR) {
                    mapDirection[x][y] = SE;
                } else if (east == CLEAR && se == CLEAR && south == CLEAR) {
                    mapDirection[x][y] = SW;
                }
            }
        }
    }

    protected void replaceIf(int x, int y, int from, int to) {
        if (map2d[x][y] == from) map2d[x][y] = to;
    }

    protected void directDoors() {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (map2d[x][y] == UniverseMap.DOOR)
                    if (map2d[x + 1][y] == UniverseMap.ROOM) mapDirection[x][y] = W;
                    else if (map2d[x - 1][y] == UniverseMap.ROOM) mapDirection[x][y] = E;
                    else if (map2d[x][y + 1] == UniverseMap.ROOM) mapDirection[x][y] = S;
                    else if (map2d[x][y - 1] == UniverseMap.ROOM) mapDirection[x][y] = N;
            }
        }
    }

    protected void directPlatforms() {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (map2d[x][y] == UniverseMap.PLATFORM) {
                    // Doors are most important
                    if (map2d[x + 1][y] == UniverseMap.DOOR) mapDirection[x][y] = E;
                    else if (map2d[x - 1][y] == UniverseMap.DOOR) mapDirection[x][y] = W;
                    else if (map2d[x][y - 1] == UniverseMap.DOOR) mapDirection[x][y] = N;
                    else if (map2d[x][y + 1] == UniverseMap.DOOR) mapDirection[x][y] = S;
                    else if (map2d[x + 1][y] == UniverseMap.PLATFORM || map2d[x - 1][y] == UniverseMap.PLATFORM)
                        mapDirection[x][y] = MathUtils.randomBoolean() ? E : W;
                    else if (map2d[x][y + 1] == UniverseMap.PLATFORM || map2d[x][y - 1] == UniverseMap.PLATFORM)
                        mapDirection[x][y] = MathUtils.randomBoolean() ? S : N;
                }
            }
        }
    }

    protected void cleanupInvisible(int lookFor, int cleanWith) {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int west = map2d[x - 1][y];
                int east = map2d[x + 1][y];
                int north = map2d[x][y - 1];
                int south = map2d[x][y + 1];
                int nw = map2d[x - 1][y - 1];
                int ne = map2d[x + 1][y - 1];
                int sw = map2d[x - 1][y + 1];
                int se = map2d[x + 1][y + 1];

                int numClear = 0;

                if (west == lookFor || west == cleanWith) numClear++;
                if (east == lookFor || east == cleanWith) numClear++;
                if (north == lookFor || north == cleanWith) numClear++;
                if (south == lookFor || south == cleanWith) numClear++;
                if (ne == lookFor || ne == cleanWith) numClear++;
                if (nw == lookFor || nw == cleanWith) numClear++;
                if (se == lookFor || se == cleanWith) numClear++;
                if (sw == lookFor || sw == cleanWith) numClear++;

                if (numClear == 8)
                    map2d[x][y] = cleanWith;
            }
        }
    }

    protected void clearRegionsWith(int feature) {
        setStartPositionToTile(feature);
        int count = floodfill(start[0], start[1], CLEAR, feature);
        while (count != 0) {
            setStartPositionToTile(feature);
            count = floodfill(start[0], start[1], CLEAR, feature);
        }
    }

    public void createStandardFeatures(int level, Level.LEVEL_TYPE levelType, boolean inside, boolean platforms) {
        int surface = SOLID;

        int space;

        if (levelType == Level.LEVEL_TYPE.TOPDOWN && platforms)
            space = PLATFORM;
        else
            space = CLEAR;

        placeAllItems(level, levelType, space, surface, inside, platforms);

        if (levelType == Level.LEVEL_TYPE.TOPDOWN && platforms)
            clearOccupied(PLATFORM);
        else
            clearOccupied(CLEAR);

        space = ROOM;
        surface = SOLID;

        if (platforms)
            replaceSomeTiles(Config.MAX_CHASMS, ROOM, CHASM);

        placeAllItems(level, levelType, space, surface, inside, platforms);

        clearOccupied(ROOM);
        printMap(map2d);
    }

    private void placeAllItems(int level, Level.LEVEL_TYPE levelType, int space, int surface, boolean inside, boolean chasms) {
        if (levelType == Level.LEVEL_TYPE.SIDESCROLL) {
            placeItemsOnSurface(Config.MAX_EXITS, ITEM_ENTRANCE_CAVE, N, space, surface);
            if (!inside)
                placeItemsEncasedFacing(Config.MAX_LIQUID, ITEM_LIQUID_SS, N, space, surface);
            placeItemsOnSurface(Config.MAX_FEATURES, ITEM_FEATURE, N, space, surface); // TODO: THIS SHOULD NOT BE ITEM, THIS SHOULD BE TILE
            placeItemsOnSurface(Config.MAX_CRATES, ITEM_CRATE, N, space, surface);
            placeItemsOnSurface(Config.MAX_KEYS, ITEM_KEY, N, space, surface);
            placeItemsWithinEmptySpace(Config.MAX_LOOT, ITEM_LOOT, space);
            placeLamps(space, surface);
            placeCannons(level, space, surface);
        } else if (levelType == Level.LEVEL_TYPE.TOPDOWN) {
            placeItemsWithinEmptySpace(Config.MAX_EXITS, ITEM_ENTRANCE_UNIVERSE, space);
            placeItemsWithinEmptySpace(Config.MAX_EXITS, ITEM_ENTRANCE_SURFACE, space);
            placeItemsWithinEmptySpace(Config.MAX_EXITS, ITEM_ENTRANCE_CAVE, space);
            if (!inside && (!chasms && space == AbstractMap.ROOM))
                placeItemsWithinEmptySpace(Config.MAX_LIQUID, ITEM_LIQUID_TD, space);
            placeItemsWithinEmptySpace(Config.MAX_LOOT, ITEM_LOOT, space);
            if (!chasms && space == AbstractMap.ROOM)
                placeItemsWithinEmptySpace(Config.MAX_FEATURES, ITEM_FEATURE, space);
            placeLamps(space, surface);
            placeCannons(level, space, surface);
        } else if (levelType == Level.LEVEL_TYPE.UNIVERSE) {
            placeItemsWithinEmptySpace(Config.MAX_EXITS, ITEM_ENTRANCE_UNIVERSE, space);
            placeItemsWithinEmptySpace(Config.MAX_EXITS, ITEM_UPGRADE, space);
            placeItemsWithinEmptySpace(Config.MAX_LOOT, ITEM_LOOT, space);
            placeItemsWithinEmptySpace(Config.MAX_SPACE_FEATURES, ITEM_FEATURE, space);
            placeItemsWithinEmptySpace(Config.MAX_ROCKS, ITEM_ROCK, space); // Create clusters

            map2d[sizeX / 2][sizeY / 2] = OCCUPIED;
            item[sizeX / 2][sizeY / 2] = ITEM_ENTRANCE_SURFACE;
        }

        placeItemsWithinEmptySpace(level > Config.MAX_MINES ? Config.MAX_MINES :
                level < Config.MIN_MINES ? Config.MIN_MINES : level, ITEM_MINE, space); // TODO: Cluster
        placeItemsWithinEmptySpace(level > Config.MAX_SPAWN ? Config.MAX_SPAWN :
                level < Config.MIN_SPAWN ? Config.MIN_SPAWN : level, ITEM_SPAWN, space); // TODO: Add starting enemies
    }

    private void placeCannons(int level, int clear, int surface) {
        placeItemsWithinEmptySpace(Config.MAX_CANNONS, ITEM_CANNON, clear);
        placeItemsOnSurface(level > Config.MAX_CANNONS ? Config.MAX_CANNONS : level, ITEM_CANNON, N, clear, surface);
        placeItemsOnSurface(level > Config.MAX_CANNONS ? Config.MAX_CANNONS : level, ITEM_CANNON, S, clear, surface);
        placeItemsOnSurface(level > Config.MAX_CANNONS ? Config.MAX_CANNONS : level, ITEM_CANNON, E, clear, surface);
        placeItemsOnSurface(level > Config.MAX_CANNONS ? Config.MAX_CANNONS : level, ITEM_CANNON, W, clear, surface);
    }

    private void placeLamps(int space, int surface) {
        placeItemsWithinEmptySpace(Config.MAX_LAMPS, ITEM_LAMP, space);
        placeItemsOnSurface(Config.MAX_LAMPS, ITEM_LAMP, N, space, surface);
        placeItemsOnSurface(Config.MAX_LAMPS, ITEM_LAMP, S, space, surface);
        placeItemsOnSurface(Config.MAX_LAMPS, ITEM_LAMP, E, space, surface);
        placeItemsOnSurface(Config.MAX_LAMPS, ITEM_LAMP, W, space, surface);
    }

    public void createSpecialFeatures(int level, Level.LEVEL_TYPE levelType, boolean inside, boolean chasms) {
    }

    public void removeAllHints() {
        replaceAll(START_HINT, CLEAR, item);
        replaceAll(END_HINT, CLEAR, item);
    }
}
