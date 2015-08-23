package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.ai.mind.PrioritizingMind;

public class AnimatedAgentBody extends AgentBody {
    TextureRegion currentFrame;
    Array<TextureAtlas.AtlasRegion> images = new Array<TextureAtlas.AtlasRegion>();
    float animTimer;

    Animation walk;

    public AnimatedAgentBody(Body body, float radius, Array<TextureAtlas.AtlasRegion> images) {
        super(body, radius);
        this.images = images;
        walk = new Animation(.1f, images);
        animTimer = 0;
        mind = new PrioritizingMind();
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        if (isCritical()) {
            spriteBatch.setColor(Color.RED);
        } else if (isHurt()) {
            spriteBatch.setColor(Color.ORANGE);
        }

        if (weapon != null)
            spriteBatch.setColor(Color.GREEN);

        Color beforeAlpha = spriteBatch.getColor();

        spriteBatch.setColor(beforeAlpha.r, beforeAlpha.g, beforeAlpha.b, alpha);

        if (flying)
            animTimer += Gdx.graphics.getDeltaTime();
        else
            animTimer += speed / 30f;

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
