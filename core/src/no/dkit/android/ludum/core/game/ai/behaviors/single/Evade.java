package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class Evade extends SingleBehavior {
    Vector2 force = new Vector2();
    float distance;
    float estimate;
    /**
     * The estimate factor is used to determine the future position of the quarry.
     * A factor of zero makes the pursuit behavior work the same way as the seek
     * behavior.
     */
    protected float m_estimateFactor = 1.0f;
    /**
     * This is the estimated position of the target object
     */
    protected Vector2 m_estimatePos = new Vector2();
    /**
     * The previous position of the target
     */
    protected Vector2 m_prevPos = new Vector2();
    private Vector2 target = new Vector2();

    public Evade(Vector2 target, float activeDistance, float influence) {
        super(activeDistance, influence);
        this.target = target;
        m_prevPos.set(0,0);
    }

    public Evade(float x, float y, int activeDistance, float influence) {
        super(activeDistance, influence);
        this.target.set(x, y);
        m_prevPos.set(0,0);
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;

        force.set(target).sub(veh.position);
        distance = force.len();

        estimate = 0;

        if (distance > 0)
            estimate = distance / getActiveDistance();

        estimate *= m_estimateFactor;

        m_estimatePos.set(
                target.x + (target.x - m_prevPos.x) * estimate,
                target.y + (target.y - m_prevPos.y) * estimate
        );

        m_prevPos.set(target.x, target.y);

        if (distance > getActiveDistance()) return NO_VECTOR;

        force.set(veh.position).sub(m_estimatePos);
        distance = force.len();

        force.scl(veh.getMaxSpeed() / distance);
        force = force.sub(veh.getBody().getLinearVelocity());
        force.scl(getInfluence());

        return force;

    }
}