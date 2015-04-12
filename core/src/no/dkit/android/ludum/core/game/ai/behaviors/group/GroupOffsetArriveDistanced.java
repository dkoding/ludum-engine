package no.dkit.android.ludum.core.game.ai.behaviors.group;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.behaviors.single.OffsetArriveDistanced;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class GroupOffsetArriveDistanced extends GroupBehavior {
    protected Separation m_separation;
    protected OffsetArriveDistanced m_arrive;
    protected Cohesion m_cohesion;

    Vector2 force = new Vector2(0, 0);

    public GroupOffsetArriveDistanced(Neighborhood neighborhood, AgentBody target, float activeDistance, float influence, int steps, float distance) {
        super(activeDistance, neighborhood, influence);
        m_arrive = new OffsetArriveDistanced(target, activeDistance, influence, steps, distance);
        m_cohesion = new Cohesion(neighborhood, this.activeDistance, influence);
        m_separation = new Separation(neighborhood, this.activeDistance, influence/3f);
    }

    public Vector2 calculate(AgentBody v) {
        force.set(0, 0);
        force.add(m_arrive.calculate(v));
        force.add(m_cohesion.calculate(v));
        force.add(m_separation.calculate(v));
        force.scl(influence);
        return force;
    }
}