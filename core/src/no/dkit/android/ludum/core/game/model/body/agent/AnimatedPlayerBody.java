package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.model.PlayerData;
import no.dkit.android.ludum.core.game.quest.GameEventListener;

public class AnimatedPlayerBody extends PlayerBody implements GameEventListener {
    private final Array<TextureAtlas.AtlasRegion> images;
    private final Animation walk;
    private float animTimer;
    private TextureRegion currentFrame;

    public AnimatedPlayerBody(Body body, float radius, PlayerData data, Array<TextureAtlas.AtlasRegion> images, CONTROL_MODE controlMode, BODY_TYPE type) {
        super(body, radius, data, images.get(0), controlMode, type);
        this.images = images;
        walk = new Animation(.1f, images);
        animTimer = 0;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        if (isCritical()) {
            spriteBatch.setColor(Color.RED);
        } else if (isHurt()) {
            spriteBatch.setColor(Color.ORANGE);
        }

        Color beforeAlpha = spriteBatch.getColor();

        spriteBatch.setColor(beforeAlpha.r, beforeAlpha.g, beforeAlpha.b, alpha);

        if(flying)
            animTimer += Gdx.graphics.getDeltaTime();
        else
            animTimer += speed/30f;

        currentFrame = walk.getKeyFrame(animTimer, true);

        spriteBatch.draw(currentFrame,
                body.getPosition().x - radius, body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                scale, scale,
                angle - 90);

        spriteBatch.setColor(Color.WHITE);

        if (weapon != null)
            if (weaponImage == null)
                throw new RuntimeException("NO WEAPON FOR WEAPON" + weapon.getImageName());
            else
                spriteBatch.draw(weaponImage,
                        body.getPosition().x - radius * .5f, body.getPosition().y,
                        radius * .5f, 0,
                        radius * 2, radius * 2,
                        scale * .5f, scale * .5f,
                        angle);
    }
}
