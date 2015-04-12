package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.quest.Trigger;

public class TriggerBody extends GameBody {
    Trigger trigger;

    public TriggerBody(Body body, float halfTileSizeX, TextureRegion image) {
        super(body, halfTileSizeX, image);
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public DRAW_LAYER getDrawLayer() {
        return DRAW_LAYER.FRONT;
    }

    @Override
    public void collidedWith(GameBody other) {
        trigger.fire();
    }
}
