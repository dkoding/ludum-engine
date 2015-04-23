package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class LaserBody {
    long startTime;
    Vector2 startPos = new Vector2();
    Vector2 endPos = new Vector2();
    Vector2 tmpVector = new Vector2();
    Color beamColor;
    Color glowColor;
    float rotation;
    float length;

    private int damage = 1;

    public LaserBody(Vector2 startPos, float rotation, float length, Color glowColor, Color beamColor) {
        this.startPos.set(startPos);
        tmpVector.set(length, 0).rotate(rotation + 90);
        this.endPos.set(startPos).add(tmpVector);
        this.glowColor = new Color(glowColor);
        this.beamColor = new Color(beamColor);
        this.rotation = rotation;
        this.length = length;
        this.startTime = System.currentTimeMillis();
    }

    public void draw(SpriteBatch spriteBatch, TextureRegion glow, TextureRegion beam, TextureRegion end, TextureRegion endGlow) {
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        glowColor.lerp(Color.CLEAR, .1f);
        spriteBatch.setColor(glowColor);

        spriteBatch.draw(endGlow,
                endPos.x - Config.TILE_SIZE_X, endPos.y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .2f, .2f,
                0,
                true);

        spriteBatch.draw(endGlow,
                startPos.x - Config.TILE_SIZE_X, startPos.y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, .1f,
                0,
                true);

        spriteBatch.draw(glow,
                startPos.x - Config.TILE_SIZE_X, startPos.y,
                Config.TILE_SIZE_X, 0,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, length,
                rotation,
                true);

        beamColor.lerp(Color.CLEAR, .2f);
        spriteBatch.setColor(beamColor);

        spriteBatch.draw(end,
                endPos.x - Config.TILE_SIZE_X, endPos.y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, .1f,
                0,
                true);

        spriteBatch.draw(end,
                startPos.x - Config.TILE_SIZE_X, startPos.y - Config.TILE_SIZE_X,
                Config.TILE_SIZE_X, Config.TILE_SIZE_Y,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, .1f,
                0,
                true);

        spriteBatch.draw(beam,
                startPos.x - Config.TILE_SIZE_X, startPos.y,
                Config.TILE_SIZE_X, 0,
                Config.TILE_SIZE_X * 2, Config.TILE_SIZE_Y * 2,
                .1f, length,
                rotation,
                true);

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public boolean isActive() {
        return System.currentTimeMillis() - startTime < 500;
    }

    public void collidedWith(GameBody other, Vector2 end) {
        other.hit(damage);
        if (other instanceof AgentBody) {
            EffectFactory.getInstance().addHitEffect(end, other.bodyType, rotation - 90);
        } else {
            EffectFactory.getInstance().addHitEffect(end, other.bodyType, rotation - 270);
        }
    }
}

