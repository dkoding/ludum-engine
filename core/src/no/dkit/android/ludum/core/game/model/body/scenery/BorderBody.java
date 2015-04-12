package no.dkit.android.ludum.core.game.model.body.scenery;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class BorderBody extends GameBody {
    public BorderBody(Body body, float halfTileSizeX, TextureRegion image) {
        super(body, halfTileSizeX, image);
        rotationMod = 1;
        alphaMod = .1f;
        scale = .1f;
    }

    @Override
    public void hit(int damage) {

    }
}
