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

    private Music music;
    private long lastPlayTime;
    private Sound currentSound;
    private Sound newSound;

    public enum SOUND_TYPE {
        OUCH1, SCREAM, JUMP,
        DOOROPEN, DOORCLOSE, DOORLOCKED,
        GUNEMPTY, WEAPONPICKUP, CASH, ORB, ARMOR,
        THROWER, HIT, IMPACT, EXPLOSION, ROCKET, LASER, BOMB, BULLET, TONGUE
    }

    public enum MUSIC_TYPE {
        MENU, GAME, LOSE, WIN, SHOP
    }

    private static final Map<GameBody.BODY_TYPE, Sound> launchSounds;
    private static final Map<GameBody.BODY_TYPE, Sound> hitSounds;
    private static final Map<GameBody.BODY_TYPE, Sound> dieSounds;
    private static final Map<SOUND_TYPE, String> soundFileNames;
    private static final Map<MUSIC_TYPE, String> musicFileNames;

    AssetManager manager;

    static {
        launchSounds = new HashMap<GameBody.BODY_TYPE, Sound>();
        hitSounds = new HashMap<GameBody.BODY_TYPE, Sound>();
        dieSounds = new HashMap<GameBody.BODY_TYPE, Sound>();

        musicFileNames = new HashMap<MUSIC_TYPE, String>();
        musicFileNames.put(MUSIC_TYPE.LOSE, "dead");
        musicFileNames.put(MUSIC_TYPE.WIN, "win");
        musicFileNames.put(MUSIC_TYPE.MENU, "menu");
        musicFileNames.put(MUSIC_TYPE.GAME, "menu");

        soundFileNames = new HashMap<SOUND_TYPE, String>();

        soundFileNames.put(SOUND_TYPE.TONGUE, "tongue");    //
        soundFileNames.put(SOUND_TYPE.OUCH1, "ouch1");    //
        soundFileNames.put(SOUND_TYPE.GUNEMPTY, "gunempty");
        soundFileNames.put(SOUND_TYPE.WEAPONPICKUP, "weaponpickup");
        soundFileNames.put(SOUND_TYPE.THROWER, "thrower");
        soundFileNames.put(SOUND_TYPE.HIT, "hit");     //
        soundFileNames.put(SOUND_TYPE.IMPACT, "impact");    //
        soundFileNames.put(SOUND_TYPE.JUMP, "jump");    //
        soundFileNames.put(SOUND_TYPE.EXPLOSION, "explosion");  //
        soundFileNames.put(SOUND_TYPE.ROCKET, "rocket");   //
        soundFileNames.put(SOUND_TYPE.LASER, "laser");   //
        soundFileNames.put(SOUND_TYPE.CASH, "cash"); //
        soundFileNames.put(SOUND_TYPE.ORB, "orb");  //
        soundFileNames.put(SOUND_TYPE.ARMOR, "armor");  //
        soundFileNames.put(SOUND_TYPE.BOMB, "bomb");   //
        soundFileNames.put(SOUND_TYPE.BULLET, "bullet");   //
    }

    public static SoundFactory getInstance() {
        if (soundFactory == null)
            soundFactory = new SoundFactory();

        return soundFactory;
    }

    protected SoundFactory() {
        MathUtils.random.setSeed(Config.RANDOM_SEED + LevelFactory.level);

        manager = new AssetManager();
        manager.setErrorListener(this);

        for (String fileName : soundFileNames.values()) {
            manager.load(soundpath + fileName + soundFormat, Sound.class);
        }

        for (String fileName : musicFileNames.values()) {
            manager.load(musicpath + fileName + musicFormat, Music.class);
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
        switch (type) {
            case FLAME_THROWER:
                playSound(SOUND_TYPE.THROWER, pitch);
                break;
            case LASER:
                playSound(SOUND_TYPE.LASER, pitch);
                break;
            case BOMB:
                playSound(SOUND_TYPE.BOMB, pitch);
                break;
            case TONGUE:
                playSound(SOUND_TYPE.TONGUE, pitch);
                break;
            case ROCKET:
            case FIREBALL:
                playSound(SOUND_TYPE.ROCKET, pitch);
                break;
            default:
                playSound(SOUND_TYPE.BULLET, pitch);
        }
    }

    public void playHitSound(GameBody.BODY_TYPE type) {
        playHitSound(type, 1);
    }

    public void playHitSound(GameBody.BODY_TYPE type, float pitch) {
        switch (type) {
            case SPACESHIP:
            case BULLET:
            case EXPLOSIVE:
            case METAL:
            case STONE:
            case WOOD:
                playSound(SOUND_TYPE.EXPLOSION, pitch);
                break;
            default:
                playSound(SOUND_TYPE.IMPACT, pitch);
        }
    }

    public void playDieSound(GameBody.BODY_TYPE type) {
        playDieSound(type, 1);
    }

    public void playDieSound(GameBody.BODY_TYPE type, float pitch) {
        switch (type) {
            case HUMANOID:
                playSound(SOUND_TYPE.OUCH1, pitch);
                break;
            default:
                playSound(SOUND_TYPE.EXPLOSION, pitch);
        }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {

    }

    public void playMusic(MUSIC_TYPE type) {
        if (music != null && music.isPlaying())
            music.stop();

        music = manager.get(musicpath + musicFileNames.get(type) + musicFormat, Music.class);

        music.setPosition(0);

        switch (type) {
            case MENU:
                music.setLooping(true);
                break;
            case GAME:
                music.setLooping(true);
                break;
            case LOSE:
                music.setLooping(true);
                break;
            case WIN:
                music.setLooping(false);
                break;
        }

        music.play();
    }

    /**
     * Plays the sound. If the sound is already playing, it will be played again, concurrently.
     *
     * @param pitch the pitch multiplier, 1 == default, >1 == faster, <1 == slower, the value has to be between 0.5 and 2.0
     * @return the id of the sound instance if successful, or -1 on failure.
     */
    public void playSound(SOUND_TYPE type, float pitch) {
        newSound = manager.get(soundpath + soundFileNames.get(type) + soundFormat, Sound.class);
        if(newSound == currentSound) return;
        if(System.currentTimeMillis() - lastPlayTime <= 3) return;      // Prevent too many sounds playing

        lastPlayTime = System.currentTimeMillis();
        newSound.play(1f, pitch, 0f);
        currentSound = newSound;
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
