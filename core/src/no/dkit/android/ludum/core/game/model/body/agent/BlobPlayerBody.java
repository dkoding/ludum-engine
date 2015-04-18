
package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.PlayerData;
import no.dkit.android.ludum.core.game.quest.GameEventListener;

public class BlobPlayerBody extends PlayerBody implements GameEventListener {
    TextureRegion eye;
    TextureRegion pupil;
    TextureRegion mouth;

    public BlobPlayerBody(Body body, float radius, PlayerData data, TextureRegion image, TextureRegion eye, TextureRegion pupil, TextureRegion mouth, CONTROL_MODE controlMode, BODY_TYPE type) {
        super(body, radius, data, image, controlMode, type);
        this.eye = eye;
        this.pupil = pupil;
        this.mouth = mouth;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        spriteBatch.setColor(Config.COLOR_1_PURPLE);
            spriteBatch.draw(image,
                    position.x - radius, position.y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    1, 1,
                    angle,
                    true);

        spriteBatch.setColor(Color.RED);
        spriteBatch.draw(mouth,
                position.x - radius, position.y - radius,
                radius, radius,
                radius * 2, radius * 2,
                .5f, .5f,
                angle,
                true);

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(eye,
                position.x - radius - .1f, position.y - radius + .1f,
                radius, radius,
                radius * 2, radius * 2,
                .5f, .5f,
                angle,
                true);

        spriteBatch.draw(eye,
                position.x - radius+.1f, position.y - radius+.1f,
                radius, radius,
                radius * 2, radius * 2,
                .5f, .5f,
                angle,
                true);

        spriteBatch.setColor(Config.COLOR_4_BLUE_LIGHT);
        spriteBatch.draw(pupil,
                position.x - radius - .1f, position.y - radius + .1f,
                radius, radius,
                radius * 2, radius * 2,
                .25f, .25f,
                angle,
                true);

        spriteBatch.draw(pupil,
                position.x - radius+.1f, position.y - radius+.1f,
                radius, radius,
                radius * 2, radius * 2,
                .25f, .25f,
                angle,
                true);

    }
}
