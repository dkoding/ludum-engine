package no.dkit.android.ludum.core.game.model.world.map;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.Config;

import java.util.Random;

public class SandboxMap extends AbstractMap {
    public static void main(String[] args) {
        SandboxMap td = new SandboxMap();
        td.createMap(1, false, false);
    }

    public SandboxMap createMap(int level, boolean inside, boolean platforms) {
        MathUtils.random = new Random(Config.RANDOM_SEED + level);

        initVariables(Config.getDimensions().WORLD_WIDTH * 2, Config.getDimensions().WORLD_HEIGHT * 2);

        clearMap();
        box(SOLID);

        for(int x=1; x<sizeX-1;x++) {
            item[x][1] = ITEM_LIQUID_SS;
        }

        clearOccupied(CLEAR);

        printMap(map2d);

        return this;
    }
}
