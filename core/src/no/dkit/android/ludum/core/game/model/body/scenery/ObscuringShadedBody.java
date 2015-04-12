package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.shaders.AbstractShader;

public class ObscuringShadedBody extends ShadedBody {
    public ObscuringShadedBody(Body body, float halfTileSizeX, AbstractShader shader, int rotation) {
        super(body, halfTileSizeX, shader, rotation);
    }

    public ObscuringShadedBody(Body body, float halfTileSizeX, AbstractShader shader) {
        super(body, halfTileSizeX, shader);
    }

    public ObscuringShadedBody(Body body, float halfTileSizeX, AbstractShader shader, TextureRegion maskRegion) {
        super(body, halfTileSizeX, shader, maskRegion);
    }
}
