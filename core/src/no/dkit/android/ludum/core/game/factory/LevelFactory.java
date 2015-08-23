package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.model.world.level.RandomLevel;

import java.util.Arrays;
import java.util.List;

public class LevelFactory {
    public static int level = 0;
    static LevelFactory instance;
    static Json json = new Json();
    private Level currentLevel;

    private List<? extends Level> levels;

    public static LevelFactory getInstance() {
        if (instance == null)
            instance = new LevelFactory();

        return instance;
    }

    public LevelFactory() {
        this.levels = Arrays.asList(
                /*new StartLevel()*/
        );

        if (Config.SANDBOX)
            currentLevel = nextLevel(Config.SANDBOX_TYPE);
        else
            currentLevel = nextLevel(Level.LEVEL_TYPE.TOPDOWN);

    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    private Level createLevel(Level.LEVEL_TYPE levelType) {
        if (level - 1 < levels.size())
            currentLevel = levels.get(level - 1);
        else
            currentLevel = createRandomLevel(levelType);

        return currentLevel;
    }

    private Level.LEVEL_TYPE getRandomLevelType() {
        Level.LEVEL_TYPE levelType;
        levelType = Level.LEVEL_TYPE.TOPDOWN;
        return levelType;
    }

    private Level createRandomLevel(Level.LEVEL_TYPE levelType) {
        System.out.println("type = " + levelType);
        System.out.println("XXXX.level = " + level);
        return new RandomLevel(levelType, level);
    }

    private Level readLevel(Level.LEVEL_TYPE type, int level) {
        FileHandle internal = Gdx.files.external("level/" + type + level);
        String content = internal.readString();
        return json.fromJson(Level.class, content);
    }

    private void writeLevel(Level level) {
        String content = json.toJson(level);
        FileHandle internal = Gdx.files.external("level/" + level.getWorldType() + level.getLevel());
        internal.writeString(content, false);
    }

    public Level nextLevel() {
        return nextLevel(getRandomLevelType());
    }

    public Level previousLevel() {
        return previousLevel(getRandomLevelType());
    }

    public Level nextLevel(Level.LEVEL_TYPE levelType) {
        if (level < Config.MAX_LEVEL)
            level++;
        else
            level = 1;

        try {
            currentLevel = createLevel(levelType);
        } catch (Exception e) {
            currentLevel = nextLevel(levelType);
        }

        System.out.println("currentLevel = " + currentLevel);

        return currentLevel;
    }

    public Level previousLevel(Level.LEVEL_TYPE levelType) {
        if (level > 1)
            level--;
        else
            level = Config.MAX_LEVEL;

        try {
            currentLevel = createLevel(levelType);
        } catch (Exception e) {
            currentLevel = previousLevel(levelType);
        }

        System.out.println("currentLevel = " + currentLevel);

        return currentLevel;
    }
}
