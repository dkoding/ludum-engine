package no.dkit.android.ludum.core.game.ai.behaviors.group;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

import java.util.Iterator;

/**
 * Aligns all agents to the same velocity direction
 */
public class Alignment extends GroupBehavior {
    Vector2 force = new Vector2(0, 0);
    AgentBody neighbor_vehicle;
    Iterator it;

    public Alignment(Neighborhood neighborhood, float nearAreaRadius, float influence) {
        super(nearAreaRadius, neighborhood, influence);
    }

    public Vector2 calculate(AgentBody v) {
        int count = 0;

        it = neighborhood.getNearAgentBodys(v, activeDistance);

        while (it.hasNext()) {
            count++;
            neighbor_vehicle = (AgentBody) it.next();
            if (neighbor_vehicle.getBody() != null)
                force.add(neighbor_vehicle.getBody().getLinearVelocity());
        }

        if (count != 0)
            force.scl(1f / count);

        force.scl(influence);

        return force;
    }
}