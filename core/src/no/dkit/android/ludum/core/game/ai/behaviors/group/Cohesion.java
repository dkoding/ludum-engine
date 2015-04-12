package no.dkit.android.ludum.core.game.ai.behaviors.group;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

import java.util.Iterator;

/**
 * All agents in same neighborhood clings together
 */
public class Cohesion extends GroupBehavior {
    Vector2 force = new Vector2(0, 0);
    Iterator it;
    AgentBody neighbor_vehicle;

    public Cohesion(Neighborhood neighborhood, float nearAreaRadius, float influence) {
        super(nearAreaRadius, neighborhood, influence);
    }

    public Vector2 calculate(AgentBody v) {
        float x = 0;
        float y = 0;

        int count = 0;

        it = neighborhood.getNearAgentBodys(v, activeDistance);

        while (it.hasNext()) {
            count++;

            neighbor_vehicle = (AgentBody) it.next();

            x += neighbor_vehicle.position.x;
            y += neighbor_vehicle.position.y;
        }

        if (count == 0) return NO_VECTOR;

        x /= count;
        y /= count;

        force.set(x, y).sub(v.position);
        force.scl(influence);

        return force;
    }

}