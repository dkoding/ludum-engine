package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.view.ChasmBackground;
import no.dkit.android.ludum.core.game.view.GroundBackground;
import no.dkit.android.ludum.core.game.view.SidescrollBackground;
import no.dkit.android.ludum.core.game.view.UniverseBackground;
import no.dkit.android.ludum.core.shaders.RenderOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ResourceFactory implements AssetErrorListener {
    public static final String MASK = "mask";
    public static final String UI = "ui";
    public static final String ITEM = "item";

    static ResourceFactory resourceFactory;

    String baseDir = "images/";

    AssetManager manager;

    static Level.LEVEL_TYPE level_type;

    public static void create(Level.LEVEL_TYPE level_type) {
        if (resourceFactory == null || level_type != ResourceFactory.level_type) {
            if(resourceFactory != null)
                resourceFactory.dispose();
            resourceFactory = new ResourceFactory(level_type);
        }
    }

    public static void createForUI() {
        resourceFactory = new ResourceFactory();
    }

    public static ResourceFactory getInstance() {
        if (resourceFactory == null)
            throw new RuntimeException("NOT CREATED YET!");

        return resourceFactory;
    }

    protected ResourceFactory() {
        ResourceFactory.level_type = null;
        MathUtils.random.setSeed(Config.RANDOM_SEED + LevelFactory.level);

        manager = new AssetManager();
        manager.setErrorListener(this);

        List<String> atlases = new ArrayList<String>();
        atlases.addAll(Arrays.asList(UI));

        for (String fileName : atlases) {
            manager.load(baseDir + fileName + ".pack.atlas", TextureAtlas.class);
        }
    }

    protected ResourceFactory(Level.LEVEL_TYPE level_type) {
        ResourceFactory.level_type = level_type;
        MathUtils.random.setSeed(Config.RANDOM_SEED + LevelFactory.level);

        manager = new AssetManager();
        manager.setErrorListener(this);

        List<String> atlases = new ArrayList<String>();
        atlases.addAll(Arrays.asList(MASK, UI, ITEM));

        if (level_type == Level.LEVEL_TYPE.SIDESCROLL)
            atlases.add(Level.LEVEL_TYPE.SIDESCROLL.toString().toLowerCase());
        else if (level_type == Level.LEVEL_TYPE.TOPDOWN)
            atlases.add(Level.LEVEL_TYPE.TOPDOWN.toString().toLowerCase());
        else if (level_type == Level.LEVEL_TYPE.UNIVERSE)
            atlases.add(Level.LEVEL_TYPE.UNIVERSE.toString().toLowerCase());

        for (String fileName : atlases) {
            manager.load(baseDir + fileName + ".pack.atlas", TextureAtlas.class);
        }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error("AssetManagerTest", "Couldn't load asset '" + asset.fileName + "'", throwable);
        throw new RuntimeException("Couldn't load asset '" + asset.fileName + "'", throwable);
    }

    // Return image based on level world type
    public TextureAtlas.AtlasRegion getWeaponImage(String imageName) {
        return getImage(ITEM, imageName + "weapon");
    }

    // Return image based on level world type
    public TextureAtlas.AtlasRegion getBulletImage(String imageName) {
        return getImage(ITEM, imageName + "bullet");
    }

    // Return image based on level world type
    public TextureAtlas.AtlasRegion getItemImage(String imageName) {
        return getImage(ITEM, imageName);
    }

    // Return image based on level world type
    public TextureAtlas.AtlasRegion getWorldTypeImage(String imageName) {
        TextureAtlas.AtlasRegion region = manager.get(baseDir + LevelFactory.getInstance().getCurrentLevel().getWorldType().toString().toLowerCase() + ".pack.atlas",
                TextureAtlas.class).findRegion(imageName);

        if (region == null && !"".equals(imageName))
            throw new RuntimeException("Could not load image " + imageName + " for world " + LevelFactory.getInstance().getCurrentLevel().getWorldType().toString());

        return region;
    }

    public TextureAtlas.AtlasRegion getShipImage(BodyFactory.ENEMY_IMAGE type) {
        switch (type) {
            case SHIP_1:
                return getWorldTypeImage("ship1");
            case SHIP_2:
                return getWorldTypeImage("ship2");
        }
        throw new RuntimeException("Unknown ship image type " + type);
    }

    public Array<TextureAtlas.AtlasRegion> getWalkerAnimation(BodyFactory.ENEMY_ANIM type) {
        switch (type) {
            case WALKER_ANIM_1:
                return getWorldTypeAnimation("walker");
        }
        throw new RuntimeException("Unknown walker image type " + type);
    }

    public Array<TextureAtlas.AtlasRegion> getFlyerAnimation(BodyFactory.ENEMY_ANIM type) {
        switch (type) {
            case FLYER_ANIM_1:
                return getWorldTypeAnimation("propeller");
        }
        throw new RuntimeException("Unknown flyer image type " + type);
    }

    // Return image based on level world type
    public TextureAtlas.AtlasRegion getFeatureImage(BodyFactory.FEATURE_TYPE type) {
        switch (type) {
            case FLOOR_FEATURE_1:
                return getWorldTypeImage("floor_feature1");
            case OUTDOOR_FEATURE_1:
                return getWorldTypeImage("outdoor_feature1");
            case PLATFORM_1:
                return getWorldTypeImage("platform1");
            case MOVING_PLATFORM_1:
                return getWorldTypeImage("moving_platform1");
            case SLIMEPOOL:
                return getWorldTypeImage("slimepool");
            case TOPDOWN_OBSCURING_FEATURE_1:
                return getWorldTypeImage("obscuring_feature1");
            case SIDESCROLL_OBSCURING_FEATURE_1:
                return getWorldTypeImage("obscuring_feature1");
            case PLANET_FEATURE_1:
                return getWorldTypeImage("planet_feature1");
            case NEBULA_FEATURE_1:
                return getWorldTypeImage("nebula_feature1");
            default:
                throw new RuntimeException("Unknown feature type " + type);
        }
    }

    // Return animation based on level world type
    public Array<TextureAtlas.AtlasRegion> getWorldTypeAnimation(String imageName) {
        Array<TextureAtlas.AtlasRegion> regions = manager.get(baseDir + LevelFactory.getInstance().getCurrentLevel().getWorldType().toString().toLowerCase() + ".pack.atlas",
                TextureAtlas.class).findRegions(imageName);

        if (regions == null || regions.size == 0)
            throw new RuntimeException("Could not load image " + imageName + " for world " + LevelFactory.getInstance().getCurrentLevel().getWorldType().toString());

        return regions;
    }

    public TextureAtlas.AtlasRegion getImage(String type, String imageName) {
        TextureAtlas.AtlasRegion region = manager.get(baseDir + type + ".pack.atlas", TextureAtlas.class).findRegion(imageName);

        if (region == null && !"".equals(imageName))
            throw new RuntimeException("Could not load image " + imageName + " for type " + type);

        return region;
    }

    public Texture getTexture(final String name) {
        if (!"".equals(name))
            return new Texture(Gdx.files.internal("images/texture/" + name + ".png"));
        else
            return null;
    }

    public Texture getTransparentTexture(final String name) {
        return new Texture(Gdx.files.internal("images/texture/" + name + ".png"));
    }

    public Texture getShaderComponentTexture(final String name) {
        return new Texture(Gdx.files.internal("images/texture/component/" + name + ".png"));
    }

    public void dispose() {
        manager.dispose();
    }

    public boolean poll() {
        return manager.update();
    }

    public RenderOperations getBackground(Level level, RenderOperations.BACKGROUND_TYPE type, int worldWidth, int worldHeight, int mapWidth, int mapHeight) {
        switch (type) {  // For NON-shader backgrounds
            case DARKROCK:
                return new SidescrollBackground(ResourceFactory.getInstance().getTexture("darkrock"),
                        ResourceFactory.getInstance().getTransparentTexture("fog3"),
                        worldWidth, worldHeight, mapWidth, mapHeight);
            case GROUND:
                if (level.isPlatforms()) // Todo: Water background as well would be cool... :-)
                    return new ChasmBackground(ResourceFactory.getInstance().getTransparentTexture("fog1"),
                            worldWidth, worldHeight, mapWidth, mapHeight);
                else
                    return new GroundBackground(ResourceFactory.getInstance().getTexture("grass"),
                            ResourceFactory.getInstance().getTransparentTexture("fog1"),
                            worldWidth, worldHeight, mapWidth, mapHeight);
            case UNIVERSE:
                return new UniverseBackground(ResourceFactory.getInstance().getTransparentTexture("fog1"),
                        ResourceFactory.getInstance().getTransparentTexture("fog3"),
                        ResourceFactory.getInstance().getWorldTypeImage("bgfeature"),
                        ResourceFactory.getInstance().getWorldTypeImage("star"),
                        worldWidth, worldHeight, mapWidth, mapHeight);
            default:
                return ShaderFactory.getInstance().getShader(type, worldWidth, worldHeight);
        }
    }

    public RenderOperations getForeground(Level level, RenderOperations.FOREGROUND_TYPE type, int worldWidth, int worldHeight, int mapWidth, int mapHeight) {
        switch (type) {
            case FOG:
            default:
                return new ChasmBackground(ResourceFactory.getInstance().getTransparentTexture("fog3"),
                        worldWidth, worldHeight, mapWidth, mapHeight);
        }
    }

    public float getPercentComplete() {
        return manager.getProgress();
    }
}
