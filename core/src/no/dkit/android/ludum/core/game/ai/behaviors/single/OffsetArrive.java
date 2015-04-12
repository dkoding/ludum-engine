package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class OffsetArrive extends SingleBehavior {
    private Vector2 target = new Vector2();
    private float offsetX = 0;
    private float offsetY = 0;
    float speed;
    float distance;
    protected int num_steps;

    Vector2 force = new Vector2();

    public OffsetArrive(Vector2 target, int activeDistance, float influence, int steps, float offsetX, float offsetY) {
        super(activeDistance, influence);
        this.target = target;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.num_steps = steps;
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;

        force.set(target).sub(veh.position).add(offsetX, offsetY);

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
