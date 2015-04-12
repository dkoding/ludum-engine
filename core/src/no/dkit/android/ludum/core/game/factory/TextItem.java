package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;

public class TextItem {
    private long startTime;
    private String text;
    private Vector2 position = new Vector2();
    private Color color;

    public TextItem(String text) {
        this.text = text;
        this.position = new Vector2(0, Config.getDimensions().SCREEN_HEIGHT / 3);
        this.color = Color.WHITE;
    }

    public TextItem(String text, Vector2 position, Color color) {
        startTime = System.currentTimeMillis();
        this.text = text;
        this.position.set(position.x, position.y);
        this.color = color;
    }

    public TextItem(String text, int x, int y, Color color) {
        startTime = System.currentTimeMillis();
        this.text = text;
        this.position.set(x,y);
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public long getStartTime() {
        return startTime;
    }
}
