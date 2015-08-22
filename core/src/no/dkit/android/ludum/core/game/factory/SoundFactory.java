package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.loot.Loot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SoundFactory implements AssetErrorListener {
    static final String musicFormat = ".mp3";
    static final String soundFormat = ".mp3";

    static final String soundpath = "sound/";
    static final String musicpath = "music/";

    static SoundFactory soundFactory;

    private Music music;

    public enum SOUND_TYPE {
        DOOROPEN
    }

    public enum MUSIC_TYPE {
        MENU, GAME
    }

    private static final Map<SOUND_TYPE, String> soundFileNames;
    private static final Map<MUSIC_TYPE, String> musicFileNames;

    AssetManager manager;

    static {
        soundFileNames = new HashMap<SOUND_TYPE, String>();
        soundFileNames.put(SOUND_TYPE.DOOROPEN, "dooropen");

        musicFileNames = new HashMap<MUSIC_TYPE, String>();
        musicFileNames.put(MUSIC_TYPE.MENU, "menu");
        musicFileNames.put(MUSIC_TYPE.GAME, "game");
    }

    public static SoundFactory getInstance() {
        if (soundFactory == null)
            soundFactory = new SoundFactory();

        return soundFactory;
    }

    protected SoundFactory() {
        MathUtils.random = new Random(Config.RANDOM_SEED + LevelFactory.level);

        manager = new AssetManager();
        manager.setErrorListener(this);

        for (String fileName : soundFileNames.values()) {
            manager.load(soundpath + fileName + soundFormat, Sound.class);
        }
    }

    public void dispose() {
        manager.dispose();
        music.dispose();
        soundFactory = null;
    }

    public void playWeaponLaunchSound(Loot.LOOT_TYPE type) {
        playWeaponLaunchSound(type, 1);
    }

    public void playWeaponLaunchSound(Loot.LOOT_TYPE type, float pitch) {

    }

    public void playHitSound(GameBody.BODY_TYPE type) {
        playHitSound(type, 1);
    }

    public void playHitSound(GameBody.BODY_TYPE type, float pitch) {
    }

    public void playDieSound(GameBody.BODY_TYPE type) {
        playDieSound(type, 1);
    }

    public void playDieSound(GameBody.BODY_TYPE type, float pitch) {
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {

    }

    public void playMusic(MUSIC_TYPE type) {
        if (!Config.music) return;

        if (music != null && music.isPlaying())
            music.stop();

        manager.load(musicpath + musicFileNames.get(type) + musicFormat, Music.class);
        manager.finishLoading();
        music = manager.get(musicpath + musicFileNames.get(type) + musicFormat, Music.class);

        music.setPosition(0);
        music.setLooping(true);

        music.play();
    }

    /**
     * Plays the sound. If the sound is already playing, it will be played again, concurrently.
     *
     * @param pitch the pitch multiplier, 1 == default, >1 == faster, <1 == slower, the value has to be between 0.5 and 2.0
     * @return the id of the sound instance if successful, or -1 on failure.
     */
    public void playSound(SOUND_TYPE type, float pitch) {
        manager.get(soundpath + soundFileNames.get(type) + soundFormat, Sound.class).play(1f, pitch, 0f);
    }

    public void playSound(SOUND_TYPE type) {
        manager.get(soundpath + soundFileNames.get(type) + soundFormat, Sound.class).play();
    }

    public void stopMusic() {
        if (music != null && music.isPlaying()) music.stop();
    }

    public boolean poll() {
        return manager.update();
    }
}
