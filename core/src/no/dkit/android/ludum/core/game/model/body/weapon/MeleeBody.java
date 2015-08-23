package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class MeleeBody extends WeaponBody {
    Fixture f;
    private long lastCollission;

    public MeleeBody(Body body, float radius, TextureRegion image) {
        super(body, radius, image, 0, 1);
        f = body.getFixtureList().get(0);
    }

    @Override
    public void delete() {

    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        return;
    }

    @Override
    public void collidedWith(GameBody other) {
        super.collidedWith(other);

        if (System.currentTimeMillis() < lastCollission + 200) return;

        lastCollission = System.currentTimeMillis();

        if (other instanceof AgentBody) {
            GameModel.getPlayer().heal(1);
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return body.getAngle();
    }

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
        position.set(body.getPosition().x, body.getPosition().y);
    }

    public void enable() {
        final Filter filterData = f.getFilterData();
        filterData.maskBits = BodyFactory.PLAYER_BULLET_BITS;
        f.setFilterData(filterData);
    }

    public void disable() {
        final Filter filterData = f.getFilterData();
        filterData.maskBits = 0;
        f.setFilterData(filterData);
    }
}
