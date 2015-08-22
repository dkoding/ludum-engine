package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.GameBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EffectFactory {

    private Array<ParticleEmitter> emitters;

    public enum EFFECT_TYPE {BLOOD, DIRBLOOD, SMOKE, EXPLOSION, BIGEXPLOSION, SPARK, ACHIEVE, EYE}

    static EffectFactory instance = null;
    private static World world;

    AssetManager manager;

    ParticleEffectLoader particleEffectLoader;

    Map<EFFECT_TYPE, ParticleEffect> particleEffects = new HashMap<EFFECT_TYPE, ParticleEffect>();
    Map<EFFECT_TYPE, ParticleEmitter> particleEmitterBlueprints = new HashMap<EFFECT_TYPE, ParticleEmitter>();
    Map<EFFECT_TYPE, GameBody.DRAW_LAYER> particleLayers = new HashMap<EFFECT_TYPE, GameBody.DRAW_LAYER>();

    Array<ParticleEmitter> toRemove = new Array<ParticleEmitter>();

    public static void create(World world) {
        if (instance == null)
            instance = new EffectFactory(world);
        else if (EffectFactory.world != world) {
            instance = new EffectFactory(world);
        }
    }

    public static EffectFactory getInstance() {
        if (instance == null)
            throw new RuntimeException("NOT CREATED YET!");

        return instance;
    }

    private EffectFactory(World world) {
        this.world = world;

        manager = new AssetManager();

        particleEffectLoader = new ParticleEffectLoader(new InternalFileHandleResolver());

        particleEffects.put(EFFECT_TYPE.BLOOD, particleEffectLoader.load("effect/blood.txt"));
        particleEffects.put(EFFECT_TYPE.DIRBLOOD, particleEffectLoader.load("effect/dirblood.txt"));
        particleEffects.put(EFFECT_TYPE.SMOKE, particleEffectLoader.load("effect/smoke.txt"));
        particleEffects.put(EFFECT_TYPE.EXPLOSION, particleEffectLoader.load("effect/explosion.txt"));
        particleEffects.put(EFFECT_TYPE.BIGEXPLOSION, particleEffectLoader.load("effect/bigexplosion.txt"));
        particleEffects.put(EFFECT_TYPE.SPARK, particleEffectLoader.load("effect/spark.txt"));
        particleEffects.put(EFFECT_TYPE.ACHIEVE, particleEffectLoader.load("effect/achieve.txt"));
        particleEffects.put(EFFECT_TYPE.EYE, particleEffectLoader.load("effect/eye.txt"));

        particleLayers.put(EFFECT_TYPE.BLOOD, GameBody.DRAW_LAYER.BACK);
        particleLayers.put(EFFECT_TYPE.DIRBLOOD, GameBody.DRAW_LAYER.BACK);
        particleLayers.put(EFFECT_TYPE.SMOKE, GameBody.DRAW_LAYER.BACK);
        particleLayers.put(EFFECT_TYPE.EYE, GameBody.DRAW_LAYER.FRONT);
        particleLayers.put(EFFECT_TYPE.EXPLOSION, GameBody.DRAW_LAYER.FRONT);
        particleLayers.put(EFFECT_TYPE.BIGEXPLOSION, GameBody.DRAW_LAYER.FRONT);
        particleLayers.put(EFFECT_TYPE.SPARK, GameBody.DRAW_LAYER.BACK);
        particleLayers.put(EFFECT_TYPE.ACHIEVE, GameBody.DRAW_LAYER.FRONT);

        Set<EFFECT_TYPE> effectTypes = particleEffects.keySet();

        for (EFFECT_TYPE effect_type : effectTypes) {
            ParticleEmitter particleEmitter = new ParticleEmitter(particleEffects.get(effect_type).getEmitters().get(0));
            particleEmitter.setContinuous(false);

            particleEmitter.getScale().setHigh(particleEmitter.getScale().getHighMin() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR, particleEmitter.getScale().getHighMax() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR);
            particleEmitter.getScale().setLow(particleEmitter.getScale().getLowMin() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR, particleEmitter.getScale().getLowMax() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR);
            particleEmitter.getVelocity().setHigh(particleEmitter.getVelocity().getHighMin() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR, particleEmitter.getVelocity().getHighMax() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR);
            particleEmitter.getVelocity().setLow(particleEmitter.getVelocity().getLowMin() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR, particleEmitter.getVelocity().getLowMax() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR);

/*
            particleEmitter.getScale().setLow(Config.TILE_SIZE_X / 4, Config.TILE_SIZE_X / 4);
            particleEmitter.getScale().setHigh(Config.TILE_SIZE_X / 2, Config.TILE_SIZE_X / 2);
            particleEmitter.getVelocity().setLow(Config.TILE_SIZE_X / 2, Config.TILE_SIZE_X / 2);
            particleEmitter.getVelocity().setHigh(Config.TILE_SIZE_X, Config.TILE_SIZE_X);
*/
            particleEmitterBlueprints.put(effect_type, particleEmitter);
        }
    }

    public ParticleEmitter addEffect(Vector2 position, EFFECT_TYPE type) {
        return addEffect(position, type, null);
    }

    public void addHitEffect(Vector2 position, GameBody.BODY_TYPE type, float angle) {
        final ParticleEmitter particleEmitter = addHitEffect(position, type);
        if(particleEmitter != null) {
            particleEmitter.getAngle().setHighMin(-20 + angle);
            particleEmitter.getAngle().setHighMax(20 + angle);
        }
    }

    public ParticleEmitter addHitEffect(Vector2 position, GameBody.BODY_TYPE type) {
        switch (type) {
            case HUMANOID:
            case ANIMAL:
            case INSECT:
                return addEffect(position, EFFECT_TYPE.DIRBLOOD, null); // RED
            case STONE:
                return addEffect(position, EFFECT_TYPE.DIRBLOOD, null); // GRAY
            case WOOD:
                return addEffect(position, EFFECT_TYPE.DIRBLOOD, null); // BROWN
            case METAL:
                return addEffect(position, EFFECT_TYPE.SPARK, null);
            case LIQUID:
                return addEffect(position, EFFECT_TYPE.DIRBLOOD, null); // BLUE
            case ZOMBIE:
                return addEffect(position, EFFECT_TYPE.DIRBLOOD, null); // GREEN
            case ALIEN:
                return addEffect(position, EFFECT_TYPE.DIRBLOOD, null); // YELLOW
            case SPACESHIP:
                return addEffect(position, EFFECT_TYPE.EXPLOSION, null); // YELLOW
            default:
                return null;
        }
    }

    public ParticleEmitter addDieEffect(Vector2 position, GameBody.BODY_TYPE type) {
        switch (type) {
            case HUMANOID:
            case ANIMAL:
            case ZOMBIE:
            case ALIEN:
            case INSECT:
                return addEffect(position, EFFECT_TYPE.BLOOD, null);
            case LIQUID:
                return addEffect(position, EFFECT_TYPE.EXPLOSION, null);
            default:
                throw new RuntimeException("NO EFFECT FOR TYPE " + type);
        }
    }

    public ParticleEmitter addEffect(Vector2 position, EFFECT_TYPE type, final ParticleEmitterBox2D.Box2DOperations operations) {
        ParticleEmitter emitter;

        if (operations != null) {
            emitter = new ParticleEmitterBox2D(world, particleEmitterBlueprints.get(type)) {
                @Override
                protected ParticleEmitter.Particle newParticle(Sprite sprite) {
                    return new ParticleBox2D(this, sprite) {
                        @Override
                        protected void collidedWith(GameBody gameBody) {
                            operations.collidedWith(gameBody);
                        }
                    };
                }
            };
        } else
            emitter = new ParticleEmitter(particleEmitterBlueprints.get(type));

        emitter.setPosition(position.x, position.y);
        particleEffects.get(type).getEmitters().add(emitter);

        switch (type) {
            case EXPLOSION:
                BodyFactory.getInstance().createLightSource(position, LightFactory.LIGHT_TYPE.EFFECT_LIGHT, .2f, 5);
                break;
            case BIGEXPLOSION:
                BodyFactory.getInstance().createLightSource(position, LightFactory.LIGHT_TYPE.EFFECT_LIGHT, .2f, 8);
                break;
            case SPARK:
                BodyFactory.getInstance().createLightSource(position, LightFactory.LIGHT_TYPE.EFFECT_LIGHT, -.2f, 2);
                break;
            default:
        }

        emitter.start();
        return emitter;
    }

    public void cleanUp() {
        toRemove.clear();

        for (Map.Entry<EFFECT_TYPE, ParticleEffect> entry : particleEffects.entrySet()) {
            emitters = entry.getValue().getEmitters();

            if (Config.DEBUGTEXT)
                System.out.println("Emitters of type " + entry + ":" + emitters.size);

            toRemove.clear();

            for (ParticleEmitter emitter : emitters) {
                if (emitter.isComplete()) {
                    toRemove.add(emitter);
                }
            }

            emitters.removeAll(toRemove, true);
        }
    }

    public void stopEmitter(ParticleEmitter emitter) {
        emitter.allowCompletion();
    }

    public void update(float deltaTime) {
        for (ParticleEffect entry : particleEffects.values()) {
            entry.update(deltaTime);
        }
    }

    public void drawEffects(SpriteBatch spriteBatch, GameBody.DRAW_LAYER layer) {
        for (Map.Entry<EFFECT_TYPE, ParticleEffect> entry : particleEffects.entrySet()) {
            if (particleLayers.get(entry.getKey()) == layer)
                entry.getValue().draw(spriteBatch);
        }
    }

    public void dispose() {
        for (ParticleEffect effect : particleEffects.values()) {
            effect.dispose();
        }

        manager.dispose();

        instance = null;
        System.out.println(this.getClass().getName() + " disposed");
    }

    private class ParticleEffectLoader extends SynchronousAssetLoader<ParticleEffect, ParticleEffectLoader.ParticleEffectParameter> {

        public ParticleEffectLoader(FileHandleResolver resolver) {
            super(resolver);
        }

        @Override
        public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ParticleEffectParameter parameter) {
            return null;
        }

        @Override
        public ParticleEffect load(AssetManager assetManager, String fileName, FileHandle file, ParticleEffectParameter parameter) {
            ParticleEffect effect = new ParticleEffect();
            FileHandle imgDir = file.parent();
            effect.load(file, imgDir);
            return effect;
        }

        public ParticleEffect load(String fileName) {
            ParticleEffect effect = new ParticleEffect();
            FileHandle file = resolve(fileName);
            FileHandle imgDir = file.parent();
            effect.load(file, imgDir);
            return effect;
        }

        public class ParticleEffectParameter extends AssetLoaderParameters<ParticleEffect> {
            public ParticleEffectParameter() {
            }
        }
    }
}
