package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class OffsetSeekAngled extends SingleBehavior {
    AgentBody target;
    Vector2 force = new Vector2();
    Vector2 offsetVector = new Vector2();
    private Vector2 tmp = new Vector2();
    float distance;

    public OffsetSeekAngled(AgentBody target, int activeDistance, float influence, float offsetX, float offsetY) {
        super(activeDistance, influence);
        this.target = target;
        offsetVector.set(offsetX, offsetY);
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;

        tmp.set(offsetVector);
        force.set(target.position).sub(veh.position).add(tmp.rotate(target.getAngle()));

        distance = force.len();

        if (distance > getActiveDistance()) return NO_VECTOR;
        if (distance < .1f) return NO_VECTOR;

        force.scl(veh.getMaxSpeed() / distance);
        force.sub(veh.getBody().getLinearVelocity());
        force.scl(getInfluence());

        return force;
    }
}
