package no.dkit.android.ludum.core.game.ai.behaviors.group;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Flee;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class GroupFlee extends GroupBehavior {
    Vector2 force = new Vector2(0, 0);
    protected Separation m_separation;
    protected Flee m_flee;
    protected Cohesion m_cohesion;

    public GroupFlee(Neighborhood neighborhood, Vector2 target, float nearAreaRadius, float influence) {
        super(nearAreaRadius, neighborhood, influence);
        m_flee = new Flee(target, nearAreaRadius, influence);
        m_cohesion = new Cohesion(neighborhood, activeDistance, influence);
        m_separation = new Separation(neighborhood, activeDistance, influence / 3f);
    }

    public Vector2 calculate(AgentBody v) {
        force.set(0, 0);
        force.add(m_separation.calculate(v));
        force.add(m_cohesion.calculate(v));
        force.add(m_flee.calculate(v));
        return force;
    }
}