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
    static final String soundFormat = ".wav";

    static final String soundpath = "sound/";
    static final String musicpath = "music/";

    static SoundFactory soundFactory;

    private long lastPlayTime;
    private Sound currentSound;
    private Sound newSound;

    private Music music;

    public enum SOUND_TYPE {
        BLOOD, DIE, EXPLOSION, SHOOT, CHOP, DOOR
    }

    public enum MUSIC_TYPE {
        MENU, WIN, GAME
    }

    private static final Map<SOUND_TYPE, String> soundFileNames;
    private static final Map<MUSIC_TYPE, String> musicFileNames;

    AssetManager manager;

    static {
        soundFileNames = new HashMap<SOUND_TYPE, String>();
        soundFileNames.put(SOUND_TYPE.DOOR, "door");
        soundFileNames.put(SOUND_TYPE.BLOOD, "blood");
        soundFileNames.put(SOUND_TYPE.DIE, "die");
        soundFileNames.put(SOUND_TYPE.EXPLOSION, "explosion");
        soundFileNames.put(SOUND_TYPE.SHOOT, "shoot");
        soundFileNames.put(SOUND_TYPE.CHOP, "chop");

        musicFileNames = new HashMap<MUSIC_TYPE, String>();
        musicFileNames.put(MUSIC_TYPE.MENU, "menu");
        musicFileNames.put(MUSIC_TYPE.GAME, "game");
        musicFileNames.put(MUSIC_TYPE.WIN, "win");
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
        if (type == Loot.LOOT_TYPE.MELEE)
            SoundFactory.getInstance().playSound(SOUND_TYPE.CHOP);
        else
            SoundFactory.getInstance().playSound(SOUND_TYPE.SHOOT);
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
        music.setLooping(type != MUSIC_TYPE.WIN);

        music.play();
    }

    public void playSound(SOUND_TYPE type) {
        newSound = manager.get(soundpath + soundFileNames.get(type) + soundFormat, Sound.class);
        if (newSound == currentSound && System.currentTimeMillis() - lastPlayTime <= 100)
            return; // Prevent same sounds playing
        lastPlayTime = System.currentTimeMillis();
        newSound.play(1f, MathUtils.random(.75f, 1.25f), 0f);
        currentSound = newSound;

    }

    public void stopMusic() {
        if (music != null && music.isPlaying()) music.stop();
    }

    public boolean poll() {
        return manager.update();
    }
}
