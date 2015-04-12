package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class OffsetArriveAngled extends SingleBehavior {
    private AgentBody target;
    protected int num_steps;
    Vector2 force = new Vector2();
    Vector2 offsetVector = new Vector2();
    private Vector2 tmp = new Vector2();
    float distance;
    float speed;

    public OffsetArriveAngled(AgentBody target, float activeDistance, float influence, int steps, float offsetX, float offsetY) {
        super(activeDistance, influence);
        this.target = target;
        this.num_steps = steps;
        offsetVector.set(offsetX, offsetY);
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;

        tmp.set(offsetVector);
        force.set(target.position).sub(veh.position).add(tmp.rotate(target.getAngle()));

        distance = force.len();

        if (distance > getActiveDistance()) return NO_VECTOR;
        if(distance < Config.EPSILON) return NO_VECTOR;

        speed = (distance*num_steps) / num_steps;
        if (speed > veh.getMaxSpeed()) speed = veh.getMaxSpeed();

        force.scl(speed / distance);
        force.sub(veh.getBody().getLinearVelocity());
        force.scl(getInfluence());

        return force;
    }
}
