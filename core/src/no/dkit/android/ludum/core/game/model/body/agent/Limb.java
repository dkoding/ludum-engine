package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.dkit.android.ludum.core.game.factory.ParticleBox2D;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class Limb extends AgentBody {
    AgentBody head;

    public Limb(AgentBody head) {
        super();
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

    @Override
    protected void attacked() {
        head.attacked();
    }

    @Override
    protected void critical() {
        head.critical();
    }

    @Override
    protected void hurt() {
        head.hurt();
    }

    @Override
    public void offScreen() {
    }
}
