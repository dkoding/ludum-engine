
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
    float faceMod = 1f;

    float currentFace = faceMod;
    float currentX = blobSizeXMod;
    float currentY = blobSizeYMod;

    public BlobPlayerBody(Body body, float radius, PlayerData data, TextureRegion image, TextureRegion eye, TextureRegion pupil, TextureRegion mouth, CONTROL_MODE controlMode, BODY_TYPE type) {
        super(body, radius, data, image, controlMode, type);
        this.eye = eye;
        this.pupil = pupil;
        this.mouth = mouth;
        rotationMod = .1f;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        if(currentX < blobSizeXMod) currentX+=.01f;
        if(currentY < blobSizeYMod) currentY+=.01f;
        if(currentFace < faceMod) currentFace +=.01f;

        if(currentX > blobSizeXMod) currentX-=.01f;
        if(currentY > blobSizeYMod) currentY-=.01f;
        if(currentFace > faceMod) currentFace -=.01f;

        spriteBatch.setColor(Config.COLOR_5_BLUE_LIGHTEST);
        spriteBatch.draw(image,
                position.x - radius, position.y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1 * currentX, 1 * currentY,
                0,
                true);

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(mouth,
                position.x - radius, position.y - radius - .1f,
                radius, radius,
                radius * 2, radius * 2,
                .25f * currentFace, .25f * currentFace,
                90,
                true);

        if(health > 20)
            spriteBatch.setColor(Color.WHITE);
        else
            spriteBatch.setColor(Color.RED);

        eyeBase = .1f;

        spriteBatch.draw(eye,
                position.x - radius - eyeBase, position.y - radius + eyeBase,
                radius, radius,
                radius * 2, radius * 2,
                .3f * currentFace, .3f * currentFace,
                -90,
                true);

        spriteBatch.draw(eye,
                position.x - radius + eyeBase, position.y - radius + eyeBase,
                radius, radius,
                radius * 2, radius * 2,
                .3f * currentFace, .3f * currentFace,
                -90,
                true);

        spriteBatch.setColor(Config.COLOR_5_BLUE_LIGHTEST);
        spriteBatch.draw(pupil,
                position.x - radius - eyeBase, position.y - radius + eyeBase,
                radius, radius,
                radius * 2, radius * 2,
                .2f * currentFace, .2f * currentFace,
                0,
                true);

        spriteBatch.draw(pupil,
                position.x - radius + eyeBase, position.y - radius + eyeBase,
                radius, radius,
                radius * 2, radius * 2,
                .2f * currentFace, .2f * currentFace,
                0,
                true);

        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        stateTimer = System.currentTimeMillis();
        state = STATE.HURTING;
        faceMod = 1.2f;
    }

    @Override
    public void onLick() {
        stateTimer = System.currentTimeMillis();
        state = STATE.LICKING;
        faceMod = 1.2f;
    }

    @Override
    public void onSlurp() {
        stateTimer = System.currentTimeMillis();
        state = STATE.LICKING;
        faceMod = 1.2f;
    }

    @Override
    public void onLanded() {
        stateTimer = System.currentTimeMillis();
        state = STATE.LANDING;
        blobSizeXMod = 1.1f;
        blobSizeYMod = 0.9f;
    }

    @Override
    public void update() {
        super.update();

        if (state != STATE.NORMAL && System.currentTimeMillis() - 250 > stateTimer) {
            state = STATE.NORMAL;
            blobSizeXMod = 1f;
            blobSizeYMod = 1f;
            faceMod = 1;
            currentFace = faceMod;
            currentX = blobSizeXMod;
            currentY = blobSizeYMod;
        }
    }

    @Override
    public void scheduleJump(float x, float y) {
        super.scheduleJump(x,y);
        stateTimer = System.currentTimeMillis();
        state = STATE.STARTJUMP;
        blobSizeXMod = 1.1f;
        blobSizeYMod = 0.9f;
    }

    @Override
    public void jump(float x, float y) {
        super.jump(x, y);
        stateTimer = System.currentTimeMillis();
        state = STATE.JUMPING;
        blobSizeXMod = 0.9f;
        blobSizeYMod = 1.1f;
    }
}
