package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class Arrive extends SingleBehavior {
    Vector2 force = new Vector2();
    protected int m_steps;
    private Vector2 target = new Vector2();
    private Vector2 tmp = new Vector2();
    private float distance;

    public Arrive(Vector2 target, int steps, float activeDistance, float influence) {
        super(activeDistance, influence);
        this.target = target;
        m_steps = steps;
    }

    public Arrive(float x, float y, int steps, float influence, int activeDistance) {
        super(activeDistance, influence);
        this.target.set(x, y);
        m_steps = steps;
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;

        tmp.set(target);
        force.set(tmp.sub(veh.position));

        distance = force.len();

        if (distance > getActiveDistance()) return NO_VECTOR;

        float speed = (distance * m_steps) / m_steps;

        if (speed > veh.getMaxSpeed()) speed = veh.getMaxSpeed();

        force.scl(speed / distance);
        force.sub(veh.getBody().getLinearVelocity());
        force.scl(getInfluence());

        return force;
    }
}