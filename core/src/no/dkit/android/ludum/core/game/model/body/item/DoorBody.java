package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.model.body.DirectionalGameBody;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;
import no.dkit.android.ludum.core.game.model.world.map.UniverseMap;

public class DoorBody extends DirectionalGameBody {
    public boolean open = false;
    public boolean locked = false;

    private final TextureRegion doorImage;

    public DoorBody(Body body, float radius, int direction, TextureRegion floorImage, TextureRegion doorImage) {
        super(body, radius, direction, 0, false, false);
        this.image = floorImage;
        this.doorImage = doorImage;
        bodyType = BODY_TYPE.WOOD;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isActive()) return;

        spriteBatch.draw(image,
                body.getPosition().x - radius,
                body.getPosition().y - radius,
                radius, radius,
                radius * 2, radius * 2,
                1, 1,
                0, true);

        if (open) {
            xOffset = 0;
            yOffset = 0;

            if (direction == UniverseMap.N)
                xOffset = 1.8f * radius;
            if (direction == UniverseMap.S)
                xOffset = -1.8f * radius;
            if (direction == UniverseMap.E)
                yOffset = 1.8f * radius;
            if (direction == UniverseMap.W)
                yOffset = -1.8f * radius;

            spriteBatch.draw(doorImage,
                    body.getPosition().x - radius + xOffset,
                    body.getPosition().y - radius + yOffset,
                    radius, radius,
                    radius * 2, radius * 2,
                    1, 1,
                    MathUtils.radiansToDegrees * body.getAngle(), true);
        } else {
            if (locked)
                spriteBatch.setColor(Color.RED);

            spriteBatch.draw(doorImage,
                    body.getPosition().x - radius,
                    body.getPosition().y - radius,
                    radius, radius,
                    radius * 2, radius * 2,
                    1, 1,
                    MathUtils.radiansToDegrees * body.getAngle(), true);

            spriteBatch.setColor(Color.WHITE);
        }
    }

    @Override
    public void beginContact(Contact contact) {
/*
        if (contact.getFixtureA().getBody().getUserData() instanceof PlayerBody
                || contact.getFixtureB().getBody().getUserData() instanceof PlayerBody) {

            if (locked) {
                TextFactory.getInstance().addText("DOOR IS LOCKED - KEY REQUIRED");
                SoundFactory.getInstance().getDoorLockedSound().play();
                return;
            }

            openDoor();
        }
*/
    }

    public void openDoor() {
        locked = false;
        Filter filter = new Filter();
        filter.categoryBits = Config.CATEGORY_SCENERY;
        filter.maskBits = Config.CATEGORY_PLAYER;
        body.getFixtureList().get(0).setSensor(true);
        body.getFixtureList().get(0).setFilterData(filter);
        SoundFactory.getInstance().playSound(SoundFactory.SOUND_TYPE.DOOROPEN);
        open = true;
    }

    @Override
    public void collidedWith(GameBody other) {
        if (other instanceof PlayerBody) {
            if (locked) {
                if (((PlayerBody) other).getData().getKeys() > 0) {
                    ((PlayerBody) other).getData().removeKey();
                    openDoor();
                } else {
                    TextFactory.getInstance().addText("DOOR IS LOCKED - KEY REQUIRED");
                    SoundFactory.getInstance().playSound(SoundFactory.SOUND_TYPE.DOORLOCKED);
                }
            } else {
                if (!open)
                    openDoor();
            }
        }
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.BACK;
    }
}
