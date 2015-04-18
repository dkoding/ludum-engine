package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.dkit.android.ludum.core.game.factory.ParticleBox2D;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class TongueRest extends WeaponBody {
    WeaponBody head;

    public TongueRest(WeaponBody head) {
        super(head.getBody(), head.getRadius());
        this.head = head;
    }

    @Override
    public void setActive(boolean active) {
    }

    @Override
    public void delete() {
    }

    @Override
    public void update() {

    }

    @Override
    public void setDisabled(boolean disabled) {
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
    }

    @Override
    public void collidedWith(ParticleBox2D other) {
    }

    @Override
    public void collidedWith(GameBody other) {
    }
}
