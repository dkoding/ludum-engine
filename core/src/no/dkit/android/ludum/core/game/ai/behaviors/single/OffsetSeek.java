package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class OffsetSeek extends SingleBehavior {
    private Vector2 target = new Vector2();
    private float offsetX = 0;
    private float offsetY = 0;
    Vector2 force = new Vector2();
    float distance;

    public OffsetSeek(Vector2 target, int activeDistance, float influence, float offsetX, float offsetY) {
        super(activeDistance, influence);
        this.target = target;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;
        force.set(target).sub(veh.position).add(offsetX, offsetY);

        distance = force.len();

        if (distance > getActiveDistance()) return NO_VECTOR;

        force.scl(veh.getMaxSpeed() / distance);
        force.sub(veh.getBody().getLinearVelocity());
        force.scl(getInfluence());

        return force;

    }
}
