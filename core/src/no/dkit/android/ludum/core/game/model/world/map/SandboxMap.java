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
        //for (int x = 0; x < sizeX; x++) map2d[x][1]  = SOLID;

        clearOccupied(CLEAR);

        //placeItemsWithinEmptySpace(10, ITEM_LAMP, CLEAR);
/*
        placeItemsWithinEmptySpace(5, ITEM_CANNON, CLEAR);
        placeItemsOnSurface(5,ITEM_CANNON, AbstractMap.N, CLEAR, SOLID);
        placeItemsEncasedFacing(1, ITEM_LIQUID_SS, N, CLEAR, SOLID);
        placeItemsOnSurface(1, ITEM_ENTRANCE_UNIVERSE, N, CLEAR, SOLID);
        placeItemsOnSurface(2, ITEM_CRATE, N, CLEAR, SOLID);
        placeItemsOnSurface(5, ITEM_FEATURE, N, CLEAR, SOLID);
        placeItemsWithinEmptySpace(1, ITEM_CANNON, CLEAR);
*/
        clearOccupied(CLEAR);

        start = new int[]{sizeX / 4, sizeY / 4};

        printMap(map2d);

        return this;
    }
}
