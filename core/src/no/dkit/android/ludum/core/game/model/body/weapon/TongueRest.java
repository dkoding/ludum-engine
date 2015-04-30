package no.dkit.android.ludum.core.game.model.body.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class TongueRest extends WeaponBody {
    public TongueRest(Body body, float radius) {
        super(body, radius);
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {

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
    protected void attacked() {

    }

    @Override
    protected void critical() {

    }

    @Override
    protected void hurt() {
    }

    @Override
    protected void hitNonAgentEffect(GameBody other) {
    }

    @Override
    protected void hitAgentEffect(AgentBody other) {
    }
}
