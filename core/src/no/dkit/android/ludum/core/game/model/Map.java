package no.dkit.android.ludum.core.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.game.model.world.map.UniverseMap;

public class Map {
    private Texture texture;
    private Pixmap map;
    private int sizeX;
    private int sizeY;
    private AbstractMap universe;
    private long lastDraw = System.currentTimeMillis();

    public Map(AbstractMap universe) {
        this.universe = universe;
        texture = new Texture(universe.getSizeX(), universe.getSizeX(), Pixmap.Format.RGBA8888);
        sizeX = universe.getSizeX();
        sizeY = universe.getSizeY();
        map = new Pixmap(sizeX, sizeY, Pixmap.Format.RGBA8888);
        init();
    }

    public void init() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Color universeColor = getColorForUniverse(universe.map2d[x][y]);
                map.setColor(universeColor.r, universeColor.g, universeColor.b, universeColor.a);
                map.drawPixel(x, sizeY - 1 - y);

                if (universe.item[x][y] != 0) {
                    Color itemColor = getColorForItem(universe.item[x][y]);
                    map.setColor(itemColor.r, itemColor.g, itemColor.b, universeColor.a);
                    map.drawPixel(x, sizeY - 1 - y);
                }
            }
        }

        texture.draw(map, 0, 0);
    }

    private Color getColorForUniverse(int val) {
        switch (val) {
            case UniverseMap.CLEAR:
                return Color.valueOf("00000080");
            case UniverseMap.BORDER:
                return Color.WHITE;
            case UniverseMap.CHASM:
                return Color.RED;
            case UniverseMap.SOLID:
                return Color.WHITE;
            case UniverseMap.DOOR:
                return Color.DARK_GRAY;
            case UniverseMap.CORRIDOR:
                return Color.LIGHT_GRAY;
            case UniverseMap.ROOM:
                return Color.LIGHT_GRAY;
            default:
                return Color.WHITE;
        }
    }

    // For drawing the first map
    private Color getColorForItem(int val) {
        switch (val) {
            case UniverseMap.ITEM_UPGRADE:
                return Color.GREEN;
            case UniverseMap.ITEM_ENTRANCE_SURFACE:
                return Color.GREEN;
            case UniverseMap.ITEM_ENTRANCE_UNIVERSE:
                return Color.GREEN;
            case UniverseMap.ITEM_ENTRANCE_CAVE:
                return Color.GREEN;
            case UniverseMap.ITEM_SPAWN:
                return Color.RED;
            case UniverseMap.ITEM_TRIGGER:
                return Color.YELLOW;
            case UniverseMap.ITEM_LIQUID_SS:
                return Color.BLUE;
            case UniverseMap.ITEM_LIQUID_TD:
                return Color.BLUE;
            default:
                return Color.WHITE;
        }
    }

    public void spot(GameBody gameBody, Color color) {
        map.setColor(color);
        map.drawPixel(Math.round(gameBody.position.x), sizeY - 1 - Math.round(gameBody.position.y));
    }

    public void dispose() {
        texture.dispose();
        map.dispose();
    }

    public Texture getImage() {
        if(System.currentTimeMillis() - Config.MAP_UPDATE_FREQUENCE_MILLIS > lastDraw) {
            texture.draw(map, 0, 0);
            lastDraw = System.currentTimeMillis();
        }

        return texture;
    }
}
