
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
    private float eyeBase;

    enum STATE {STARTJUMP, JUMPING, LICKING, LANDING, NORMAL, HURTING}

    STATE state = STATE.NORMAL;
    long stateTimer;

    float blobSizeXMod = 1f;
    float blobSizeYMod = 1f;
    float eyeSizeMod = 1f;

    public BlobPlayerBody(Body body, float radius, PlayerData data, TextureRegion image, TextureRegion eye, TextureRegion pupil, TextureRegion mouth, CONTROL_MODE controlMode, BODY_TYPE type) {
        super(body, radius, data, image, controlMode, type);
        this.eye = eye;
        this.pupil = pupil;
        this.mouth = mouth;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        blobSizeXMod = 1f;
        blobSizeYMod = 1f;

        eyeSizeMod = 1f;

        if(state == STATE.JUMPING) {
            blobSizeXMod = 0.9f;
            blobSizeYMod = 1.1f;
        }

        if(state == STATE.HURTING) {
            eyeSizeMod = 1.2f;
        }

        if(state == STATE.LANDING || state == STATE.STARTJUMP) {
            blobSizeXMod = 1.1f;
            blobSizeYMod = 0.9f;
        }

        spriteBatch.setColor(Config.COLOR_5_BLUE_LIGHTEST);
        spriteBatch.draw(image,
                position.x - radius, position.y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1 * blobSizeXMod, 1 * blobSizeYMod,
                0,
                true);

        spriteBatch.setColor(Color.RED);
        spriteBatch.draw(mouth,
                position.x - radius, position.y - radius - .1f,
                radius, radius,
                radius * 2, radius * 2,
                .25f * eyeSizeMod, .25f * eyeSizeMod,
                0,
                true);

        spriteBatch.setColor(Color.WHITE);

        eyeBase = .15f;

        spriteBatch.draw(eye,
                position.x - radius - eyeBase, position.y - radius + eyeBase,
                radius, radius,
                radius * 2, radius * 2,
                .5f * eyeSizeMod, .5f * eyeSizeMod,
                0,
                true);

        spriteBatch.draw(eye,
                position.x - radius + eyeBase, position.y - radius + eyeBase,
                radius, radius,
                radius * 2, radius * 2,
                .5f * eyeSizeMod, .5f * eyeSizeMod,
                0,
                true);

        spriteBatch.setColor(Config.COLOR_5_BLUE_LIGHTEST);
        spriteBatch.draw(pupil,
                position.x - radius - eyeBase, position.y - radius + eyeBase,
                radius, radius,
                radius * 2, radius * 2,
                .25f * eyeSizeMod, .25f * eyeSizeMod,
                0,
                true);

        spriteBatch.draw(pupil,
                position.x - radius + eyeBase, position.y - radius + eyeBase,
                radius, radius,
                radius * 2, radius * 2,
                .25f * eyeSizeMod, .25f * eyeSizeMod,
                0,
                true);

        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        stateTimer = System.currentTimeMillis();
        state = STATE.HURTING;
    }

    @Override
    public void onLick() {
        stateTimer = System.currentTimeMillis();
        state = STATE.LICKING;
    }

    @Override
    public void onSlurp() {
        stateTimer = System.currentTimeMillis();
        state = STATE.LICKING;
    }

    @Override
    public void onLanded() {
        stateTimer = System.currentTimeMillis();
        state = STATE.LANDING;
    }

    @Override
    public void update() {
        super.update();

        if (state != STATE.NORMAL && System.currentTimeMillis() - 250 > stateTimer) {
            state = STATE.NORMAL;
        }
    }

    @Override
    public void scheduleJump(float x, float y) {
        super.scheduleJump(x,y);
        stateTimer = System.currentTimeMillis();
        state = STATE.STARTJUMP;
    }

    @Override
    public void jump(float x, float y) {
        super.jump(x, y);
        stateTimer = System.currentTimeMillis();
        state = STATE.JUMPING;
    }
}
