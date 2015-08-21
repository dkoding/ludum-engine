package no.dkit.android.ludum.core.game.factory;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

import java.util.HashMap;
import java.util.Map;

public class LightFactory {

    private int numRays;

    public enum LIGHT_TYPE {INSIDE_CONE, SMALL_PLAYER_LIGHT, ALWAYS_CONE, LARGE_PLAYER_LIGHT, EFFECT_LIGHT}

    static World world;
    static LightFactory instance = null;
    private RayHandler rayHandler;

    static Map<LIGHT_TYPE, Light> playerLights;

    public static void create(World world) {
        if (instance == null)
            instance = new LightFactory(world);
        else if (LightFactory.world != world) {
            instance = new LightFactory(world);
        }
    }

    public static LightFactory getInstance() {
        if (instance == null)
            throw new RuntimeException("Create first!");

        return instance;
    }

    private LightFactory(World world) {
        Light.setGlobalContactFilter(BodyFactory.lightFilter);
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);
        rayHandler.setCulling(false);
        rayHandler.setShadows(true);
        //rayHandler.setBlur(true);
        LightFactory.world = world;
        playerLights = new HashMap<LIGHT_TYPE, Light>();

        if (XXXX.performance == Config.PERFORMANCE.LOW) {
            numRays = 16;
        } else if (XXXX.performance == Config.PERFORMANCE.MEDIUM) {
            numRays = 32;
        } else {
            numRays = 64;
        }
    }

    public Light getPlayerLight(LIGHT_TYPE type) {
        Light light = getLightForType(type);

        playerLights.put(type, light);

        return light;
    }

    public Light getLightForType(LIGHT_TYPE type) {
        Light light;

        if (type == LIGHT_TYPE.LARGE_PLAYER_LIGHT) {
            Color color = new Color(1f, 1f, 1f, 1f);
            light = getLight(0, 0, Config.getDimensions().WORLD_WIDTH, numRays, color);
            light.setSoftnessLength(Config.getDimensions().WORLD_WIDTH / 2f);
        } else if (type == LIGHT_TYPE.SMALL_PLAYER_LIGHT) {
            Color color = new Color(1f, 1f, 1f, 1f);
            light = getLight(0, 0, Config.getDimensions().WORLD_WIDTH / 2f, numRays, color);
            light.setSoftnessLength(Config.getDimensions().WORLD_WIDTH / 4f);
        } else if (type == LIGHT_TYPE.EFFECT_LIGHT) {
            Color color = new Color(1f, 1f, 1f, 1f);
            light = getLight(0, 0, Config.TILE_SIZE_X, numRays, color);
            light.setSoftnessLength(Config.TILE_SIZE_X);
        } else if (type == LIGHT_TYPE.ALWAYS_CONE) {
            Color color = new Color(1f, 1f, 1f, 1f);
            light = getConeLight(Vector2.Zero, Config.getDimensions().WORLD_WIDTH / 2f, numRays, color, 0, 30);
            light.setSoftnessLength(Config.TILE_SIZE_X * 2);
        } else if (type == LIGHT_TYPE.INSIDE_CONE) {
            Color color = new Color(1f, 1f, 1f, 1f);
            light = getConeLight(Vector2.Zero, Config.getDimensions().WORLD_WIDTH / 2f, numRays, color, 0, 30);
            light.setSoftnessLength(Config.TILE_SIZE_X);
        } else {
            throw new RuntimeException("UNKNOWN LIGHT TYPE " + type);
        }

        light.setActive(true);
        light.setStaticLight(false);
        light.setSoft(true);
        light.setXray(false);

        return light;
    }

    public void updatePlayerLights(float angle, int currentTileType) {
        for (Map.Entry<LIGHT_TYPE, Light> entry : playerLights.entrySet()) {
            if (entry.getKey() == LIGHT_TYPE.ALWAYS_CONE) {
                entry.getValue().setDirection(angle);
            } else if (entry.getKey() == LIGHT_TYPE.INSIDE_CONE) {
                entry.getValue().setDirection(angle);

                if (currentTileType == AbstractMap.ROOM || currentTileType == AbstractMap.DOOR || currentTileType == AbstractMap.CHASM) {
                    entry.getValue().setActive(true);
                } else {
                    entry.getValue().setActive(false);
                }
            } else if (entry.getKey() == LIGHT_TYPE.LARGE_PLAYER_LIGHT) {

            } else if (entry.getKey() == LIGHT_TYPE.SMALL_PLAYER_LIGHT) {
                if (currentTileType == AbstractMap.ROOM || currentTileType == AbstractMap.DOOR || currentTileType == AbstractMap.CHASM) {
                    entry.getValue().setDistance(1);
                } else {
                    entry.getValue().setDistance(Config.getDimensions().WORLD_WIDTH / 2);
                }
            }
        }
    }

    public PointLight getLight(float x, float y, float distance, int numRays, Color color) {
        PointLight pointLight = new PointLight(rayHandler, numRays, color, distance, x, y);
        pointLight.setStaticLight(true);
        pointLight.setXray(true);
        pointLight.setSoftnessLength(0);
        pointLight.setSoft(false);
        return pointLight;
    }

    public ConeLight getConeLight(Vector2 position, float distance, int numRays, Color color, float angle, float width) {
        ConeLight coneLight = new ConeLight(rayHandler, numRays, color, distance, position.x, position.y, angle, width);
        coneLight.setStaticLight(true);
        coneLight.setXray(true);
        coneLight.setSoftnessLength(0);
        coneLight.setSoft(false);
        return coneLight;
    }

    public Light getDirectionalLight(int numRays, Color color, float angle) {
        return new DirectionalLight(rayHandler, numRays, color, angle);
    }

    public void dispose() {
        if (instance != null)
            rayHandler.dispose();
        instance = null;
        System.out.println(this.getClass().getName() + " disposed");
    }

    public void render(Matrix4 combined, int width, int height, int sizeX, int sizeY) {
        rayHandler.setCombinedMatrix(combined, width, height, sizeX, sizeY);
        rayHandler.updateAndRender();
    }

    public void setAmbientLight(Color color) {
        rayHandler.setAmbientLight(color);
    }
}
