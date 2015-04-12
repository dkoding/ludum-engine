package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.shaders.AbstractDualTextureShader;
import no.dkit.android.ludum.core.shaders.AbstractShader;
import no.dkit.android.ludum.core.shaders.AbstractTextureShader;
import no.dkit.android.ludum.core.shaders.RenderOperations;
import no.dkit.android.ludum.core.shaders.fullscreen.ArcadeTunnelShader;
import no.dkit.android.ludum.core.shaders.fullscreen.BeaconShader;
import no.dkit.android.ludum.core.shaders.fullscreen.FireShader;
import no.dkit.android.ludum.core.shaders.fullscreen.LightSwirlShader;
import no.dkit.android.ludum.core.shaders.fullscreen.NeonCrossShader;
import no.dkit.android.ludum.core.shaders.fullscreen.NeonPulseShader;
import no.dkit.android.ludum.core.shaders.fullscreen.NeonWaveShader;
import no.dkit.android.ludum.core.shaders.fullscreen.NeonWaveShader2;
import no.dkit.android.ludum.core.shaders.fullscreen.NeonWaveShader3;
import no.dkit.android.ludum.core.shaders.fullscreen.RainbowSwirlShader;
import no.dkit.android.ludum.core.shaders.fullscreen.ShiningStarScrollShader;
import no.dkit.android.ludum.core.shaders.fullscreen.SimpleCloudShader;
import no.dkit.android.ludum.core.shaders.fullscreen.SpiralShader;
import no.dkit.android.ludum.core.shaders.texture.FlagShader;
import no.dkit.android.ludum.core.shaders.texture.InwardScrollShader;
import no.dkit.android.ludum.core.shaders.texture.PixelizeShader;
import no.dkit.android.ludum.core.shaders.texture.TransitionCrossFadeShader;
import no.dkit.android.ludum.core.shaders.texture.TransitionSwipeShader;
import no.dkit.android.ludum.core.shaders.texture.VignetteShader;
import no.dkit.android.ludum.core.shaders.texture.WaterRippleRefractShader;
import no.dkit.android.ludum.core.shaders.texture.WaterSinusWaveShader;
import no.dkit.android.ludum.core.shaders.texture.WaveInWindShader;
import no.dkit.android.ludum.core.shaders.texture.WaveShader;

import java.util.HashMap;
import java.util.Map;

import static no.dkit.android.ludum.core.shaders.RenderOperations.BACKGROUND_TYPE;

public class ShaderFactory {
    static private Level.LEVEL_TYPE worldType;
    static private int level;

    private static ShaderFactory ourInstance;

    static Map<BACKGROUND_TYPE, AbstractShader> shaderMap = new HashMap<BACKGROUND_TYPE, AbstractShader>();

    static {
        // Textured
        shaderMap.put(BACKGROUND_TYPE.FLAG, new FlagShader());
        shaderMap.put(BACKGROUND_TYPE.INWARDSCROLL, new InwardScrollShader());
        shaderMap.put(BACKGROUND_TYPE.PIXELIZE, new PixelizeShader());
        shaderMap.put(BACKGROUND_TYPE.TRANSITIONCROSSFADE, new TransitionCrossFadeShader());
        shaderMap.put(BACKGROUND_TYPE.TRANSITIONSWIPE, new TransitionSwipeShader());
        shaderMap.put(BACKGROUND_TYPE.VIGNETTE, new VignetteShader());
        shaderMap.put(BACKGROUND_TYPE.WATERRIPPLEREFRACT, new WaterRippleRefractShader());
        shaderMap.put(BACKGROUND_TYPE.WATERSINUSWAVE, new WaterSinusWaveShader());
        shaderMap.put(BACKGROUND_TYPE.WAVEINWIND, new WaveInWindShader());
        shaderMap.put(BACKGROUND_TYPE.WAVE, new WaveShader());

        // NonTextured
        shaderMap.put(BACKGROUND_TYPE.ARCADE_TUNNEL, new ArcadeTunnelShader());
        shaderMap.put(BACKGROUND_TYPE.BEACON, new BeaconShader());
        shaderMap.put(BACKGROUND_TYPE.FIRE, new FireShader());
        shaderMap.put(BACKGROUND_TYPE.LIGHTSWIRL, new LightSwirlShader());
        shaderMap.put(BACKGROUND_TYPE.NEONCROSS, new NeonCrossShader());
        shaderMap.put(BACKGROUND_TYPE.NEONPULSE, new NeonPulseShader());
        shaderMap.put(BACKGROUND_TYPE.NEONWAVE, new NeonWaveShader());
        shaderMap.put(BACKGROUND_TYPE.NEONWAVE2, new NeonWaveShader2());
        shaderMap.put(BACKGROUND_TYPE.NEONWAVE3, new NeonWaveShader3());
        shaderMap.put(BACKGROUND_TYPE.RAINBOWSWIRL, new RainbowSwirlShader());
        shaderMap.put(BACKGROUND_TYPE.SHININGSTARSCROLL, new ShiningStarScrollShader());
        shaderMap.put(BACKGROUND_TYPE.SIMPLE_CLOUD, new SimpleCloudShader());
        shaderMap.put(BACKGROUND_TYPE.SPIRAL, new SpiralShader());
    }

    Map<BACKGROUND_TYPE, AbstractShader> allShaders = new HashMap<BACKGROUND_TYPE, AbstractShader>();
    Array<AbstractShader> activeShaders = new Array<AbstractShader>();

    public static ShaderFactory create(Level.LEVEL_TYPE levelType, int level) {
        if (ourInstance == null || levelType != ShaderFactory.worldType || level != ShaderFactory.level) {
            ourInstance = new ShaderFactory(levelType, level);
        }

        return ourInstance;
    }

    public static ShaderFactory getInstance() {
        if (ourInstance == null)
            throw new RuntimeException("NOT CREATED YET!");
        return ourInstance;
    }

    private ShaderFactory(Level.LEVEL_TYPE levelType, int level) {
        dispose();
        ShaderFactory.worldType = levelType;
        ShaderFactory.level = level;

        activeShaders.clear();
        allShaders.clear();
    }

    public void addActiveShader(AbstractShader shader) {
        if (!activeShaders.contains(shader, true))
            activeShaders.add(shader);
    }

    public void clearActive() {
        activeShaders.clear();
    }

    public void updateShaders() {
        if (activeShaders.size == 0) return;

        for (AbstractShader shader : activeShaders) {
            shader.update();
        }
    }

    public void dispose() {
        System.out.println(this.getClass().getName() + " disposing " + allShaders.keySet().size() + " shaders");

        for (BACKGROUND_TYPE type : allShaders.keySet()) {
            allShaders.get(type).dispose();
        }

        activeShaders.clear();
        allShaders.clear();
    }

    public AbstractShader getShader(BACKGROUND_TYPE type, float width, float height) {
        try {
            AbstractShader abstractShader = shaderMap.get(type);
            return addAndReturn(type, abstractShader, width, height);
        } catch (Exception e) {
            throw new RuntimeException("Wrong number of arguments for shader " + type + ":", e);
        }
    }

    public AbstractTextureShader getShader(BACKGROUND_TYPE type, float width, float height, Texture texture) {
        try {
            AbstractTextureShader abstractShader = (AbstractTextureShader) shaderMap.get(type);
            return (AbstractTextureShader) addAndReturn(type, abstractShader, width, height, texture);
        } catch (Exception e) {
            throw new RuntimeException("Wrong number of arguments for shader " + type + ":", e);
        }
    }

    public AbstractDualTextureShader getShader(BACKGROUND_TYPE type, float width, float height, Texture texture1, Texture texture2) {
        try {
            AbstractDualTextureShader abstractShader = (AbstractDualTextureShader) shaderMap.get(type);
            return (AbstractDualTextureShader) addAndReturn(type, abstractShader, width, height, texture1, texture2);
        } catch (Exception e) {
            throw new RuntimeException("Wrong number of arguments for shader " + type + ":", e);
        }
    }

    private AbstractShader addAndReturn(RenderOperations.BACKGROUND_TYPE type, AbstractShader shader,
                                        float width, float height) {
        if (!allShaders.containsKey(type)) {
            shader.init((int) width, (int) height);
            allShaders.put(type, shader);
        }

        return allShaders.get(type);
    }

    private AbstractShader addAndReturn(RenderOperations.BACKGROUND_TYPE type, AbstractTextureShader shader,
                                        float width, float height, Texture texture) {
        if (!allShaders.containsKey(type)) {
            shader.init((int) width, (int) height, texture);
            allShaders.put(type, shader);
        }

        return allShaders.get(type);
    }

    private AbstractShader addAndReturn(RenderOperations.BACKGROUND_TYPE type, AbstractDualTextureShader shader,
                                        float width, float height, Texture texture1, Texture texture2) {
        if (!allShaders.containsKey(type)) {
            shader.init((int) width, (int) height, texture1, texture2);
            allShaders.put(type, shader);
        }

        return allShaders.get(type);
    }
}
