package no.dkit.android.ludum.core.game.ai.behaviors.group;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Arrive;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class GroupArrive extends GroupBehavior {
    Vector2 force = new Vector2(0, 0);
    protected Separation m_separation;
    protected Arrive m_arrive;
    protected Cohesion m_cohesion;

    public GroupArrive(Neighborhood neighborhood, Vector2 target, float nearAreaRadius, int steps, float influence) {
        super(nearAreaRadius, neighborhood, influence);
        m_arrive = new Arrive(target, steps, activeDistance, influence);
        m_cohesion = new Cohesion(neighborhood, activeDistance, influence);
        m_separation = new Separation(neighborhood, activeDistance, influence/3f);
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