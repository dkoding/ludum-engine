package no.dkit.android.ludum.core.game.transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FadeOutEffect extends TransitionEffect {

    Color color = new Color();
    ShapeRenderer spriteBatch;

    public FadeOutEffect(float duration, Color color) {
        super(duration);
        this.color = color;

        spriteBatch = new ShapeRenderer();
    }

    @Override
    public void render(Screen current, Screen next) {
        current.render(Gdx.graphics.getDeltaTime());

        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatch.begin(ShapeRenderer.ShapeType.Filled);
        spriteBatch.setColor(0, 0, 0, getAlpha());
        spriteBatch.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        Gdx.gl20.glDisable(GL20.GL_BLEND);
    }
}