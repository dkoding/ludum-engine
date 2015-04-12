package no.dkit.android.ludum.core.game.model.body.scenery;

import box2dLight.Light;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import no.dkit.android.ludum.core.game.controller.GameScreen;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class UpgradeBody extends GameBody {
    public UpgradeBody(Body body, float halfTileSizeX, TextureRegion image) {
        super(body, halfTileSizeX, image);
        Light light = LightFactory.getInstance().getLight(position.x, position.y, halfTileSizeX * 2, 16, Color.GRAY);
        light.setStaticLight(true);
        addLight(light);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }
    @Override
    public void collidedWith(GameBody other) {
    }

    @Override
    public void beginContact(Contact contact) {
        GameScreen.enableUpgradeMenu(true);
    }

    @Override
    public void endContact(Contact contact) {
        GameScreen.enableUpgradeMenu(false);
    }
}
