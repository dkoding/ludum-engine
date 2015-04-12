package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class OffsetArriveDistanced extends SingleBehavior {
    private AgentBody target;
    protected int num_steps;
    Vector2 force = new Vector2();
    Vector2 offsetVector = new Vector2();
    float offset;
    float dist;
    float speed;

    public OffsetArriveDistanced(AgentBody target, float activeDistance, float influence, int steps, float offset) {
        super(activeDistance, influence);
        this.target = target;
        this.num_steps = steps;
        this.offset = offset;
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;

        offsetVector.set(veh.position).sub(target.position);
        offsetVector.nor().scl(this.offset);
        force.set(target.position).sub(veh.position).add(offsetVector);

        dist = force.len();

        if (dist > getActiveDistance()) return NO_VECTOR;
        if (dist < Config.EPSILON) return NO_VECTOR;

        speed = (dist * num_steps) / num_steps;
        if (speed > veh.getMaxSpeed()) speed = veh.getMaxSpeed();

        force.scl(speed / dist);
        force.sub(veh.getBody().getLinearVelocity());
        force.scl(getInfluence());

        return force;
    }
}
