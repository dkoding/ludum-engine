package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.game.model.world.map.CaveMap;

public class MapFactory {
    static MapFactory instance;

    public static MapFactory getInstance() {
        if (instance == null)
            instance = new MapFactory();

        return instance;
    }

    public AbstractMap getRandomMap(Level.LEVEL_TYPE type, final int level, boolean inside, boolean platforms) {
        AbstractMap map;
        float random = MathUtils.random();

        map = new CaveMap();
        map.createMap(level, inside, platforms);

        return map;
    }

    public void dispose() {

    }
}
