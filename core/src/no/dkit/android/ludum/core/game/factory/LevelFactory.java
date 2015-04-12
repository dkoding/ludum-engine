package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
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
            currentLevel = nextLevel(Level.LEVEL_TYPE.UNIVERSE);

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
        float type = MathUtils.random();

        if (type < .33)
            levelType = Level.LEVEL_TYPE.SIDESCROLL;
        else if (type < .66)
            levelType = Level.LEVEL_TYPE.TOPDOWN;
        else
            levelType = Level.LEVEL_TYPE.UNIVERSE;
        return levelType;
    }

    private Level createRandomLevel(Level.LEVEL_TYPE levelType) {
        System.out.println("type = " + levelType);
        System.out.println("XXXX.level = " + level);
        return new RandomLevel(levelType, level);
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
            return currentLevel;

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
            return currentLevel;

        try {
            currentLevel = createLevel(levelType);
        } catch (Exception e) {
            currentLevel = previousLevel(levelType);
        }

        System.out.println("currentLevel = " + currentLevel);

        return currentLevel;
    }
}
