package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.shaders.AbstractShader;

public class ShadedBody extends GameBody {
    AbstractShader shader;
    TextureRegion maskRegion;

    public ShadedBody(Body body, float halfTileSizeX, AbstractShader shader, int rotation) {
        super(body, halfTileSizeX);
        this.shader = shader;
        this.rotation = rotation;
    }

    public ShadedBody(Body body, float halfTileSizeX, AbstractShader shader) {
        super(body, halfTileSizeX);
        this.shader = shader;
        rotation = MathUtils.random(360);
    }

    public ShadedBody(Body body, float halfTileSizeX, AbstractShader shader, TextureRegion maskRegion) {
        super(body, halfTileSizeX);
        this.shader = shader;
        this.maskRegion = maskRegion;
        rotation = MathUtils.random(360);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.BACK;
    }

    @Override
    public TextureRegion getImage() {
        return null;
    }

    public void draw(SpriteBatch spriteBatch) {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    public void drawShaded(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        shader.setMaskRegion(maskRegion); // To set to null is OK

        shader.render(spriteBatch,
                body.getPosition().x - getRadius(), body.getPosition().y - getRadius(),
                getRadius() * 2, getRadius() * 2, 1, rotation);
    }

    public AbstractShader getShader() {
        return shader;
    }
}
