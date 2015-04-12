package no.dkit.android.ludum.core.game.model.world.map;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;

import java.util.Random;

public class UniverseMap extends AbstractMap {
    public static void main(String[] args) {
        UniverseMap um = new UniverseMap();
        um.createMap(1, false, true);
    }

    public AbstractMap createMap(int level, boolean inside, boolean platforms) {
        MathUtils.random = new Random(Config.RANDOM_SEED + level);

        initVariables((int) Math.pow(2, Config.UNIVERSE_SIZE) + 1, (int) Math.pow(2, Config.UNIVERSE_SIZE) + 1);

        clearMap();
        //initMap(maxValue / 2);
        clearOccupied(CLEAR);
        box(BORDER);

        printMap(map2d);

        return this;
    }
}
