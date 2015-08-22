package no.dkit.android.ludum.core.game;

import no.dkit.android.ludum.core.game.model.world.level.Level;

public class Config {
    public static final int MAX_LEVEL = 100;
    public static boolean DEBUG = false;
    public static final float DEBUG_SCALE = 1f;
    public static boolean DEBUGTEXT = false;

    public static final boolean SANDBOX = false;
    public static final Level.LEVEL_TYPE SANDBOX_TYPE = Level.LEVEL_TYPE.TOPDOWN;

    public static final float AGENT_SPOT_CHANCE = .9f;
    public static final float AGENT_SPOT_ARC = 45;

    public static final int MAP_UPDATE_FREQUENCE_MILLIS = 200;

    public static final float TILE_SIZE_X = .5f;
    public static final float TILE_SIZE_Y = .5f;

    public static final long OUCH_RATE = 2000;
    public static final long DEGUG_THRESHOLD_MILLIS = 0;
    public static final int MAX_EXPLOSIONS = 3;
    public static final int GROUP_SIZE = 5;

    public static final int UNIVERSE_SIZE = 4;
    public static final int MAZE_SIZE = 4;
    public static final int CAVE_SIZE = 20;
    public static final int DUNGEON_SIZE = 30;
    public static final int CITY_SIZE = 25;

    public static final float FPS = 60f;
    public static final int NUM_PARTICLES = 50;
    public static final float ENEMY_CHANCE = .1f;

    public static final int PLAYER_START_HEALTH = 100;
    public static final int PLAYER_START_ARMOR = 100;
    public static final int PLAYER_START_ORBS = 20;
    public static final int PLAYER_START_KEYS = 3;
    public static final int PLAYER_START_CREDITS = 200;

    public static final int TURRET_TURN_SPEED = 3;

    public static final int MAX_ENEMIES = 1;
    public static final int MAX_CHASMS = 3;
    public static final int MAX_LOOT = 3;
    public static final int MAX_FEATURES = 50;
    public static final int MAX_SPACE_FEATURES = UNIVERSE_SIZE * 4;
    public static final int MIN_SPAWN = 5;
    public static final int MAX_SPAWN = 10;
    public static final int MIN_MINES = 10;
    public static final int MAX_MINES = 20;
    public static final int MAX_ROCKS = UNIVERSE_SIZE * 4;
    public static final int MAX_CANNONS = 10;
    public static final int MAX_LAMPS = 10;
    public static final int MAX_CRATES = 10;

    public static final long ITEM_TTL = 5000;
    public static final float WARP_CHANCE = .2f;
    public static final float ROCK_CHANCE = .2f;
    public static final float CANNON_CHANCE = .2f;
    public static final float BULLET_RATE_LOW = .1f;
    public static final float BULLET_RATE_MEDIUM = .2f;
    public static final float BULLET_RATE_HIGH = .00003f;
    public static final int NUM_STARS = 25;
    public static final long RANDOM_SEED = 666;
    public static final int MAX_KEYS = 10;
    public static final int MAX_EXITS = 1;
    public static final int MAX_LIQUID = 10;

    private static Dimensions dimensions;

    public static final long LASER_COOLDOWN = 900;
    public static final long BULLET_COOLDOWN = 600;
    public static final long BOMB_COOLDOWN = 3000;
    public static final long ROCKET_COOLDOWN = 2700;
    public static final long PLASMA_COOLDOWN = 300;
    public static final long LASSO_COOLDOWN = 500;

    public static final float AGENT_SPOT_DISTANCE = 1.5f;
    public static final float AGENT_INFLUENCE_FORCE = 1f;
    public static final int AGENT_ARRIVE_STEPS = 3;

    public static final int PARTICLE_DENSITY = 50;
    public static final int BULLET_DENSITY = 1;
    public static final int LOOT_DENSITY = 3;

    public static final float SHIP_RADIUS = Config.TILE_SIZE_X / 3f;
    public static final float SHIP_DENSITY = 3f;
    public static final float WALKER_RADIUS = Config.TILE_SIZE_X / 5f;
    public static final float WALKER_DENSITY = 3f;
    public static final float FLYER_RADIUS = Config.TILE_SIZE_X / 3f;
    public static final float FLYER_DENSITY = 3f;
    public static final float ZOMBIE_RADIUS = Config.TILE_SIZE_X / 5f;
    public static final float ZOMBIE_DENSITY = 3f;
    public static final float BLOB_RADIUS = Config.TILE_SIZE_X / 4f;
    public static final float BLOB_DENSITY = 3f;
    public static final float BLOBLIMB_RADIUS = Config.TILE_SIZE_X / 8f;
    public static final float BLOBLIMB_DENSITY = 3f;

    public static final boolean music = false;
    public static final boolean sound = false;

    public static Dimensions getDimensions() {
        return dimensions;
    }

    public static void initialize(int width, int height) {
        dimensions = new Dimensions(width, height);
    }

    public static class Dimensions {
        public int SCREEN_WIDTH;
        public int SCREEN_HEIGHT;

        public int SCREEN_LONGEST;
        public int SCREEN_SHORTEST;
        public float ASPECT_RATIO;
        public int WORLD_WIDTH;
        public int WORLD_HEIGHT;
        public float PAD_RIGHT_LEFT;
        public float PAD_TOP_BOTTOM;
        public float SCREEN_PIXELS_PER_TILE;
        public float SCREEN_ON_WORLD_FACTOR;
        public float WORLD_ON_SCREEN_FACTOR;
        public float WORLD_ON_SCREEN_FACTOR_Y;

        public float AGENT_INFLUENCE_AREA;
        public float AGENT_BOX_INFLUENCE_AREA;

        public int AGENT_BOX_WIDTH;
        public int AGENT_BOX_HEIGHT;

        public Dimensions(int width, int height) {
            SCREEN_WIDTH = width;
            SCREEN_HEIGHT = height;

            SCREEN_LONGEST = SCREEN_HEIGHT > SCREEN_WIDTH ? SCREEN_HEIGHT : SCREEN_WIDTH;
            SCREEN_SHORTEST = SCREEN_HEIGHT > SCREEN_WIDTH ? SCREEN_WIDTH : SCREEN_HEIGHT;
            ASPECT_RATIO = (float) SCREEN_SHORTEST / (float) SCREEN_LONGEST;

            WORLD_WIDTH = 8;   // Number of tiles across screen
            WORLD_HEIGHT = (int) (WORLD_WIDTH * ASPECT_RATIO);

            float fraction = (WORLD_WIDTH * ASPECT_RATIO) - WORLD_HEIGHT;

            if (fraction > 0)
                WORLD_HEIGHT++;

            SCREEN_ON_WORLD_FACTOR = (float) SCREEN_LONGEST / (float) WORLD_WIDTH;
            WORLD_ON_SCREEN_FACTOR = (float) WORLD_WIDTH / (float) SCREEN_LONGEST;
            SCREEN_PIXELS_PER_TILE = SCREEN_ON_WORLD_FACTOR;
            AGENT_BOX_WIDTH = (int) (TILE_SIZE_X * 5); // Square
            AGENT_BOX_HEIGHT = (int) (TILE_SIZE_Y * 5); // Square

            AGENT_INFLUENCE_AREA = WORLD_HEIGHT;
            AGENT_BOX_INFLUENCE_AREA = WORLD_HEIGHT;
        }
    }

    public static final int BOX_POSITION_ITERATIONS = 8;
    public static final int BOX_VELOCITY_ITERATIONS = 3;
    public static final float BOX_STEP = 1 / 25f;

    public static final float PLAYER_MAX_SPEED = 1f;
    public static final float PLAYER_MIN_SPEED = .1f;
    public static final float MAX_SPEED = .5f;
    public static final float LOOT_SPEED = .1f;
    public static final float MIN_SPEED = .01f;
    public static final float ROTATE_SPEED = 5f;

    public static short CATEGORY_PLAYER = 0x2;
    public static short CATEGORY_BULLET = 0x4;
    public static short CATEGORY_SCENERY = 0x8;
    public static short CATEGORY_ITEM = 0x10;
    public static short CATEGORY_ENEMY = 0x20;
    public static short CATEGORY_ENEMY_BULLET = 0x40;
    public static short CATEGORY_TRIGGER = 0x80;
    public static short CATEGORY_LIGHT = 0x100;
    public static short CATEGORY_PARTICLE = 0x200;
    public static short CATEGORY_PLATFORM = 0x400;
    public static short CATEGORY_LOOT = 0x800;

    public static final float EPSILON = .03f;

    public enum PERFORMANCE {LOW, MEDIUM, HIGH}
}
