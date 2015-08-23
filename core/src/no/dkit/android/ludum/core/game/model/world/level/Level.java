package no.dkit.android.ludum.core.game.model.world.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.shaders.RenderOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Level {
    public static final String NOT_USED = "";
    public AbstractMap map;

    private static Level instance;

    public boolean wonLevel() { // Default win conditions
        return defaultVictoryCondition();
    }

    public boolean lostLevel() {
        return defaultLoseCondition();
    }

    public enum LEVEL_TYPE {TOPDOWN}

    public List<BodyFactory.ENEMY_ANIM> walkers = new ArrayList<BodyFactory.ENEMY_ANIM>();
    public List<BodyFactory.ENEMY_ANIM> flyers = new ArrayList<BodyFactory.ENEMY_ANIM>();
    public List<BodyFactory.ENEMY_IMAGE> ships = new ArrayList<BodyFactory.ENEMY_IMAGE>();
    public List<BodyFactory.ENEMY_TYPE> enemyTypes = new ArrayList<BodyFactory.ENEMY_TYPE>();
    public List<Loot.LOOT_TYPE> lootTypes = new ArrayList<Loot.LOOT_TYPE>();
    public List<Loot.LOOT_TYPE> weaponTypes = new ArrayList<Loot.LOOT_TYPE>();
    public List<BodyFactory.FEATURE_TYPE> indoorFeatureTypes = new ArrayList<BodyFactory.FEATURE_TYPE>();
    public List<BodyFactory.FEATURE_TYPE> outdoorFeatureTypes = new ArrayList<BodyFactory.FEATURE_TYPE>();
    public RenderOperations.BACKGROUND_TYPE background;
    public RenderOperations.FOREGROUND_TYPE foreground;
    public String wallTexture;
    public String indoorImage;
    public String chasmImage;
    public String doorImage;
    public String corridorImage;

    boolean platforms;
    boolean inside;

    public List<LightFactory.LIGHT_TYPE> lightTypes = new ArrayList<LightFactory.LIGHT_TYPE>();

    public LEVEL_TYPE worldType;
    public int level;
    public static Vector2 gravity = new Vector2(Vector2.Zero);
    public Vector2 startPosition = new Vector2();

    public static Level getInstance() {
        if (instance == null)
            throw new RuntimeException("NOT CREATED YET!");
        return instance;
    }

    protected Level(LEVEL_TYPE worldType, int level, boolean inside, boolean platforms) {
        this.level = level;
        this.worldType = worldType;
        this.platforms = platforms;
        this.inside = inside;
        instance = this;
    }

    protected void setupDefaults(LEVEL_TYPE worldType) {
        getDefaultLootFor(worldType);
        getDefaultAgentsFor(worldType);
        getDefaultGravityFor(worldType);
        getDefaultBackgroundFor(worldType);
        getDefaultForegroundFor(worldType);
        getDefaultLightsFor(worldType);
        getDefaultImagesFor(worldType);
        getDefaultFeaturesFor(worldType);
    }

    protected void addItems(LEVEL_TYPE worldType, int level) {
        getStandardFeaturesFor(level, worldType, inside, platforms);
        getSpecialFeaturesFor(level, worldType, inside, platforms);
    }

    protected void getDefaultPlayerStartPosition(LEVEL_TYPE worldType) {
        if (worldType == LEVEL_TYPE.TOPDOWN) {
            if (!setStartPositionToItem(AbstractMap.START_HINT)) {
                final Vector2 emptyArea = findEmptySpace(AbstractMap.CLEAR);
                setStartPositionTo(emptyArea.x, emptyArea.y);
            }
        }
    }

    protected Vector2 findEmptySpace(int clear) {
        int currentAttempt = 0;
        int maxAttempts = 1000; // Safeguard

        while (currentAttempt < maxAttempts) {
            currentAttempt++;

            int x = MathUtils.random(1, map.getWidth() - 2);
            int y = MathUtils.random(1, map.getHeight() - 2);

            if (currentAttempt < maxAttempts / 2) {
                if (map.map2d[x][y] == clear && map.map2d[x - 1][y] == clear && map.map2d[x + 1][y] == clear && map.map2d[x][y - 1] == clear && map.map2d[x][y + 1] == clear
                        && map.map2d[x - 1][y - 1] == clear && map.map2d[x + 1][y + 1] == clear && map.map2d[x + 1][y - 1] == clear && map.map2d[x - 1][y + 1] == clear) {
                    return new Vector2(x, y);
                }
            } else {
                if (map.map2d[x][y] == clear) {
                    return new Vector2(x, y);
                }
            }
        }

        return new Vector2(1, 1);
    }

    protected void getSpecialFeaturesFor(int level, LEVEL_TYPE levelType, boolean inside, boolean platforms) {
        map.createSpecialFeatures(level, levelType, inside, platforms);
    }

    protected void getStandardFeaturesFor(int level, LEVEL_TYPE levelType, boolean inside, boolean platforms) {
        map.createStandardFeatures(level, levelType);
    }

    public Loot.LOOT_TYPE getWeaponFor(BodyFactory.ENEMY_ANIM enemy) {
        switch (enemy) {
            case FEMALESOLDIER:
            case MALESOLDIER:
                return Loot.LOOT_TYPE.GUN;
        }
        return null;
    }

    protected void getDefaultFeaturesFor(LEVEL_TYPE worldType) {
        indoorFeatureTypes.clear();
        outdoorFeatureTypes.clear();

/*
        indoorFeatureTypes.addAll(
                Arrays.asList(
                        BodyFactory.FEATURE_TYPE.FLOOR_FEATURE_1
                ));
*/
        outdoorFeatureTypes.addAll(
                Arrays.asList(
                        BodyFactory.FEATURE_TYPE.TOPDOWN_OBSCURING_FEATURE_1
                ));
    }

    protected void getDefaultLightsFor(LEVEL_TYPE worldType) {
        lightTypes.clear();

        switch (worldType) {
            case TOPDOWN:
                lightTypes.addAll(Arrays.asList(LightFactory.LIGHT_TYPE.ALWAYS_CONE, LightFactory.LIGHT_TYPE.LARGE_PLAYER_LIGHT));
                break;
        }
    }

    protected void getDefaultBackgroundFor(LEVEL_TYPE worldType) {
        switch (worldType) {
            case TOPDOWN:
                background = RenderOperations.BACKGROUND_TYPE.GROUND;
                break;
        }
    }

    protected void getDefaultForegroundFor(LEVEL_TYPE worldType) {
        switch (worldType) {
            case TOPDOWN:
                foreground = RenderOperations.FOREGROUND_TYPE.FOG;
                break;
        }
    }

    protected void getDefaultGravityFor(LEVEL_TYPE worldType) {
        gravity.set(0, 0);
    }

    public static Vector2 getGravity() {
        return gravity;
    }

    public static void setGravity(Vector2 gravity) {
        Level.gravity = gravity;
    }

    protected void getDefaultAgentsFor(LEVEL_TYPE worldType) {
        enemyTypes.clear();

        switch (worldType) {
            case TOPDOWN:
                enemyTypes.addAll(
                        Arrays.asList(
                                BodyFactory.ENEMY_TYPE.WALKER_SINGLE,
                                BodyFactory.ENEMY_TYPE.WALKER_GROUP
                        )
                );
                walkers.addAll(
                        Arrays.asList(
                                BodyFactory.ENEMY_ANIM.FEMALE,
                                BodyFactory.ENEMY_ANIM.MALE,
                                BodyFactory.ENEMY_ANIM.FEMALESOLDIER,
                                BodyFactory.ENEMY_ANIM.MALESOLDIER
                        )
                );
                break;
        }
    }

    protected void getDefaultLootFor(LEVEL_TYPE worldType) {
        weaponTypes.clear();
        lootTypes.clear();

        switch (worldType) {
            case TOPDOWN: // First weapon is default weapon
                weaponTypes.addAll(
                        Arrays.asList(
                                Loot.LOOT_TYPE.GUN
                        ));
                break;
        }

        lootTypes.addAll(
                Arrays.asList(
                        Loot.LOOT_TYPE.ORB
                )
        );
        lootTypes.addAll(weaponTypes);
    }

    protected void getDefaultImagesFor(LEVEL_TYPE worldType) {
        switch (worldType) {
            case TOPDOWN:
                wallTexture = "wall";
                indoorImage = "indoor";
                chasmImage = "chasm";
                corridorImage = "path";
                doorImage = "door";
                break;
        }
    }

    public String getWallTexture() {
        return wallTexture;
    }

    public String getIndoorImage() {
        return indoorImage;
    }

    public String getDoorImage() {
        return doorImage;
    }

    public String getCorridorImage() {
        return corridorImage;
    }

    public String getChasmImage() {
        return chasmImage;
    }

    public AbstractMap getMap() {
        return map;
    }

    public BodyFactory.ENEMY_IMAGE getRandomShip() {
        return ships.get(MathUtils.random(ships.size() - 1));
    }

    public BodyFactory.ENEMY_ANIM getRandomWalker() {
        int start = level;
        if (start > walkers.size() - 2) start = walkers.size() - 2;

        return walkers.get(MathUtils.random(start, walkers.size() - 1));
    }

    public BodyFactory.ENEMY_ANIM getRandomFlyer() {
        return flyers.get(MathUtils.random(flyers.size() - 1));
    }

    public BodyFactory.ENEMY_TYPE getRandomEnemyType() {
        return enemyTypes.get(MathUtils.random(enemyTypes.size() - 1));
    }

    public BodyFactory.FEATURE_TYPE getRandomIndoorFeatureType() {
        return indoorFeatureTypes.get(MathUtils.random(indoorFeatureTypes.size() - 1));
    }

    public BodyFactory.FEATURE_TYPE getRandomPlanetFeatureType() {
        return indoorFeatureTypes.get(MathUtils.random(indoorFeatureTypes.size() - 1));
    }

    public BodyFactory.FEATURE_TYPE getRandomOutdoorFeatureType() {
        return outdoorFeatureTypes.get(MathUtils.random(outdoorFeatureTypes.size() - 1));
    }

    public Loot.LOOT_TYPE getRandomLootType() {
        return lootTypes.get(MathUtils.random(lootTypes.size() - 1));
    }

    public int getLevel() {
        return level;
    }

    public LEVEL_TYPE getWorldType() {
        return worldType;
    }

    public RenderOperations.BACKGROUND_TYPE getBackgroundType() {
        return background;
    }

    public RenderOperations.FOREGROUND_TYPE getForegroundType() {
        return foreground;
    }

    public List<LightFactory.LIGHT_TYPE> getLightTypes() {
        return lightTypes;
    }

    public abstract void onStart();

    public List<BodyFactory.ENEMY_TYPE> getEnemyTypes() {
        return enemyTypes;
    }

    public void setEnemyTypes(List<BodyFactory.ENEMY_TYPE> enemyTypes) {
        this.enemyTypes = enemyTypes;
    }

    public List<Loot.LOOT_TYPE> getLootTypes() {
        return lootTypes;
    }

    public List<Loot.LOOT_TYPE> getWeaponTypes() {
        return weaponTypes;
    }

    public void setLootTypes(List<Loot.LOOT_TYPE> lootTypes) {
        this.lootTypes = lootTypes;
    }

    public List<BodyFactory.FEATURE_TYPE> getIndoorFeatureTypes() {
        return indoorFeatureTypes;
    }

    public void setIndoorFeatureTypes(List<BodyFactory.FEATURE_TYPE> indoorFeatureTypes) {
        this.indoorFeatureTypes = indoorFeatureTypes;
    }

    public List<BodyFactory.FEATURE_TYPE> getOutdoorFeatureTypes() {
        return outdoorFeatureTypes;
    }

    public void setOutdoorFeatureTypes(List<BodyFactory.FEATURE_TYPE> outdoorFeatureTypes) {
        this.outdoorFeatureTypes = outdoorFeatureTypes;
    }

    public void setBackground(RenderOperations.BACKGROUND_TYPE background) {
        this.background = background;
    }

    public void setWallTexture(String wallTexture) {
        this.wallTexture = wallTexture;
    }

    public void setIndoorImage(String indoorImage) {
        this.indoorImage = indoorImage;
    }

    public void setDoorImage(String doorImage) {
        this.doorImage = doorImage;
    }

    public void setCorridorImage(String corridorImage) {
        this.corridorImage = corridorImage;
    }

    public void setLightTypes(List<LightFactory.LIGHT_TYPE> lightTypes) {
        this.lightTypes = lightTypes;
    }

    public void setWorldType(LEVEL_TYPE worldType) {
        this.worldType = worldType;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Vector2 getStartPosition() {
        return startPosition;
    }

    protected boolean setStartPositionToItem(int itemType) {
        for (int x = 0; x < map.getSizeX(); x++)
            for (int y = 0; y < map.getSizeY(); y++)
                if (map.item[x][y] == itemType) {
                    startPosition.set(x, y);
                    return true;
                }
        return false;
    }

    protected void setStartPositionTo(float x, float y) {
        startPosition.set(x, y);
    }

    public boolean isPlatforms() {
        return platforms;
    }

    public boolean isInside() {
        return inside;
    }

    public String getName() {
        return map.getClass().getName();
    }

    private boolean defaultLoseCondition() {
        return GameModel.getPlayer().isDead();
    }

    private boolean defaultVictoryCondition() {
        return GameModel.getInstance().getTotalNumEnemies() <= 0;
    }
}
