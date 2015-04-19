package no.dkit.android.ludum.core.game.model.world.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.game.model.world.map.UniverseMap;
import no.dkit.android.ludum.core.shaders.RenderOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Level {
    public static final String NOT_USED = "";
    public AbstractMap map;

    private static Level instance;

    public enum LEVEL_TYPE {UNIVERSE, TOPDOWN, SIDESCROLL}

    public List<BodyFactory.ENEMY_IMAGE> walkers = new ArrayList<BodyFactory.ENEMY_IMAGE>();
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
        if (worldType == LEVEL_TYPE.SIDESCROLL)
            setStartPositionToItem(AbstractMap.ITEM_ENTRANCE_CAVE);
        else if (worldType == LEVEL_TYPE.TOPDOWN)
            setStartPositionToItem(AbstractMap.ITEM_ENTRANCE_SURFACE);
        else if (worldType == LEVEL_TYPE.UNIVERSE)
            setStartPositionToItem(AbstractMap.ITEM_ENTRANCE_UNIVERSE);
    }

    protected void getSpecialFeaturesFor(int level, LEVEL_TYPE levelType, boolean inside, boolean platforms) {
        map.createSpecialFeatures(level, levelType, inside, platforms);
    }

    protected void getStandardFeaturesFor(int level, LEVEL_TYPE levelType, boolean inside, boolean platforms) {
        map.createStandardFeatures(level, levelType, inside, platforms);
    }

    public Loot.LOOT_TYPE getWeaponFor(BodyFactory.ENEMY_IMAGE enemy) {
        switch (enemy) {
            case SHIP_1:
                return Loot.LOOT_TYPE.LASER;
            case SHIP_2:
                return Loot.LOOT_TYPE.ROCKET;
            case WALKER_1:
                return Loot.LOOT_TYPE.LASER;
            case WALKER_2:
                return Loot.LOOT_TYPE.ROCKET;
        }
        throw new RuntimeException("No weapon assigned to enemy " + enemy);
    }

    public Loot.LOOT_TYPE getWeaponFor(BodyFactory.ENEMY_ANIM enemy) {
        switch (enemy) {
            case WALKER_ANIM_1:
                return Loot.LOOT_TYPE.GUN;
            case FLYER_ANIM_1:
                return Loot.LOOT_TYPE.FIREBALL;
        }
        throw new RuntimeException("No weapon assigned to enemy " + enemy);
    }

    protected void getDefaultFeaturesFor(LEVEL_TYPE worldType) {
        indoorFeatureTypes.clear();
        outdoorFeatureTypes.clear();

        switch (worldType) {
            case SIDESCROLL:
                indoorFeatureTypes.addAll(
                        Arrays.asList(BodyFactory.FEATURE_TYPE.SIDESCROLL_OBSCURING_FEATURE_1));
                outdoorFeatureTypes.addAll(
                        Arrays.asList(BodyFactory.FEATURE_TYPE.SIDESCROLL_OBSCURING_FEATURE_1));
                break;
            case TOPDOWN:
                indoorFeatureTypes.addAll(
                        Arrays.asList(
                                BodyFactory.FEATURE_TYPE.FLOOR_FEATURE_1
                        ));
                outdoorFeatureTypes.addAll(
                        Arrays.asList(
                                BodyFactory.FEATURE_TYPE.TOPDOWN_OBSCURING_FEATURE_1,
                                BodyFactory.FEATURE_TYPE.OUTDOOR_FEATURE_1,
                                BodyFactory.FEATURE_TYPE.SLIMEPOOL
                        ));
                break;
            case UNIVERSE:
                indoorFeatureTypes.addAll(
                        Arrays.asList(
                                BodyFactory.FEATURE_TYPE.PLANET_FEATURE_1));
                outdoorFeatureTypes.addAll(
                        Arrays.asList(BodyFactory.FEATURE_TYPE.NEBULA_FEATURE_1
                        ));
                break;
        }
    }

    protected void getDefaultLightsFor(LEVEL_TYPE worldType) {
        lightTypes.clear();

        switch (worldType) {
            case SIDESCROLL:
                lightTypes.add(LightFactory.LIGHT_TYPE.LARGE_PLAYER_LIGHT);
                break;
            case TOPDOWN:
                lightTypes.addAll(Arrays.asList(LightFactory.LIGHT_TYPE.INSIDE_CONE, LightFactory.LIGHT_TYPE.SMALL_PLAYER_LIGHT));
                break;
            case UNIVERSE:
                lightTypes.add(LightFactory.LIGHT_TYPE.LARGE_PLAYER_LIGHT);
                break;
        }
    }

    protected void getDefaultBackgroundFor(LEVEL_TYPE worldType) {
        switch (worldType) {
            case SIDESCROLL:
                background = RenderOperations.BACKGROUND_TYPE.UNIVERSE;
                break;
            case TOPDOWN:
                background = RenderOperations.BACKGROUND_TYPE.GROUND;
                break;
            case UNIVERSE:
                background = RenderOperations.BACKGROUND_TYPE.UNIVERSE;
        }
    }

    protected void getDefaultForegroundFor(LEVEL_TYPE worldType) {
        switch (worldType) {
            case SIDESCROLL:
                foreground = RenderOperations.FOREGROUND_TYPE.FOG;
                break;
            case TOPDOWN:
                foreground = RenderOperations.FOREGROUND_TYPE.FOG;
                break;
            case UNIVERSE:
                foreground = RenderOperations.FOREGROUND_TYPE.FOG;
        }
    }

    protected void getDefaultGravityFor(LEVEL_TYPE worldType) {
        if (worldType.equals(LEVEL_TYPE.SIDESCROLL))
            gravity.set(0, -1);
        else
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

        enemyTypes.addAll(
                Arrays.asList(
                        BodyFactory.ENEMY_TYPE.BLOB,
                        BodyFactory.ENEMY_TYPE.FLYER_SINGLE,
                        BodyFactory.ENEMY_TYPE.WALKER_SINGLE
                )
        );
        walkers.addAll(
                Arrays.asList(
                        BodyFactory.ENEMY_IMAGE.WALKER_1,
                        BodyFactory.ENEMY_IMAGE.WALKER_2
                )
        );
        flyers.addAll(
                Arrays.asList(
                        BodyFactory.ENEMY_ANIM.FLYER_ANIM_1
                )
        );
    }

    protected void getDefaultLootFor(LEVEL_TYPE worldType) {
        weaponTypes.clear();
        lootTypes.clear();

        weaponTypes.addAll(
                Arrays.asList(
                        Loot.LOOT_TYPE.TONGUE
                ));
        lootTypes.addAll(
                Arrays.asList(
                        Loot.LOOT_TYPE.MEDPACK,
                        Loot.LOOT_TYPE.TREASURE,
                        Loot.LOOT_TYPE.ARMOR,
                        Loot.LOOT_TYPE.ORB
                )
        );
        lootTypes.addAll(weaponTypes);
    }

    protected void getDefaultImagesFor(LEVEL_TYPE worldType) {
        wallTexture = "tile" + MathUtils.random(1, 5);
        indoorImage = "indoor";
        chasmImage = "chasm";
        corridorImage = "path";
        doorImage = "door";
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

    public BodyFactory.ENEMY_IMAGE getRandomWalker() {
        return walkers.get(MathUtils.random(walkers.size() - 1));
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

    protected void setStartPositionToItem(int itemType) {
        for (int x = 0; x < map.getSizeX(); x++)
            for (int y = 0; y < map.getSizeY(); y++)
                if (map.item[x][y] == itemType) {
                    startPosition.set(x, y);
                    return;
                }
        startPosition.set(-1, -1);
    }

    protected void setStartPositionTo(float x, float y) {
        startPosition.set(x, y);
    }

    protected void setStartPositionToTile(int tileType) {
        for (int x = 0; x < map.getSizeX(); x++)
            for (int y = 0; y < map.getSizeY(); y++)
                if (map.map2d[x][y] == tileType) {
                    startPosition.set(x, y);
                    return;
                }
        startPosition.set(-1, -1);
    }

    protected boolean sanityChecked() {
        boolean exitFound = false;
        boolean startFeatureFound = false;
        boolean keyFound = false;

        for (int x = 0; x < map.getWidth(); x++)
            for (int y = 0; y < map.getHeight(); y++) {
                if (!exitFound)
                    if (map.item[x][y] == UniverseMap.ITEM_ENTRANCE_CAVE
                            || map.item[x][y] == UniverseMap.ITEM_TRIGGER)
                        exitFound = true;

                if (!keyFound)
                    if (map.item[x][y] == UniverseMap.ITEM_KEY)
                        keyFound = true;
            }

        startFeatureFound = startPosition.x != -1 && startPosition.y != -1;

        return exitFound && startFeatureFound && keyFound;
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
}
