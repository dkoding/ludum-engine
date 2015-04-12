package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class TextFactory {

    BitmapFont font = new BitmapFont();
    BitmapFontCache fontCache;

    Array<TextItem> texts;
    static TextFactory instance = null;

    int width;
    int height;
    float alpha;
    float alphaMod;

    public static TextFactory getInstance() {
        if (instance == null)
            instance = new TextFactory();

        return instance;
    }

    private TextFactory() {
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();

        alpha = 1;
        alphaMod = 0;

        texts = new Array<TextItem>();
        font = getFont(50);
        font.setScale(1f);
        fontCache = new BitmapFontCache(font);
    }

    public void addTexts(float alphaMod, TextItem... items) {
        this.alpha = 1;
        this.alphaMod = alphaMod;
        texts.clear();
        for (TextItem item : items) {
            texts.add(item);
        }
        updateCache();
    }

    public void addText(TextItem textItem, float alphaMod) {
        alpha = 1;
        this.alphaMod = alphaMod;
        texts.clear();
        texts.add(textItem);
        updateCache();
    }

    public void addText(TextItem textItem) {
        alpha = 1;
        this.alphaMod = 0;
        texts.clear();
        texts.add(textItem);
        updateCache();
    }

    public void addText(String text) {
        alpha = 1;
        this.alphaMod = 0;
        texts.clear();
        texts.add(new TextItem(text));
        updateCache();
    }

    public void removeText(TextItem textItem) {
        texts.removeValue(textItem, true);
        updateCache();
    }

    public void updateCache() {
        fontCache.clear();

        for (TextItem text : texts) {
            BitmapFont.TextBounds bounds = font.getBounds(text.getText());
            fontCache.addText(text.getText(), text.getPosition().x - (bounds.width / 2), text.getPosition().y + (bounds.height / 4));
            fontCache.setColor(text.getColor());
        }
    }

    public void drawText(SpriteBatch spriteBatch) {
        if (alpha <= 0) return;
        spriteBatch.begin();
        fontCache.draw(spriteBatch, alpha);
        spriteBatch.end();

        alpha += alphaMod;
    }

    public void dispose() {
        font.dispose();
        instance = null;
        System.out.println(this.getClass().getName() + " disposed");
    }

    private BitmapFont getFont(int size) {
       return font;
    }
}
