package no.dkit.android.ludum.core.game.model.body.scenery;

import box2dLight.Light;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.controller.GameScreen;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.shaders.AbstractShader;

public class ExitBody extends ShadedBody {

    private Level.LEVEL_TYPE exitType;

    public ExitBody(Body body, float radius, Level.LEVEL_TYPE type, TextureRegion image, float rotationMod, AbstractShader shader, TextureRegion maskRegion) {
        super(body, radius, shader);
        Light light = LightFactory.getInstance().getLight(position.x, position.y, radius, 16, Config.COLOR_5_BLUE_LIGHTEST);
        light.setStaticLight(true);
        addLight(light);
        this.exitType = type;
        this.image = image;
        this.rotationMod = rotationMod;
        this.maskRegion = maskRegion;
        if (rotationMod == 0) rotation = 0;
    }

    public ExitBody(Body body, float radius, Level.LEVEL_TYPE type, TextureRegion image, AbstractShader shader, TextureRegion maskRegion) {
        this(body, radius, type, image, 0, shader, maskRegion);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.enableBlending();

        spriteBatch.draw(image,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                rotation + 90, true);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.MEDIUM;
    }

    @Override
    public void collidedWith(GameBody other) {
    }

    @Override
    public void beginContact(Contact contact) {
        GameScreen.enableTravelMenu(exitType);
    }

    @Override
    public void endContact(Contact contact) {
        GameScreen.disableTravelMenu(exitType);
    }
}
