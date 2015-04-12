package no.dkit.android.ludum.core.game.ai.behaviors.group;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

import java.util.Iterator;

public class Separation extends GroupBehavior {
    Vector2 force = new Vector2(0, 0);
    Vector2 temp = new Vector2(0,0);
    AgentBody neighbor_vehicle;
    Iterator it;

    public Separation(Neighborhood neighborhood, float nearAreaRadius, float influence) {
        super(nearAreaRadius, neighborhood, influence);
    }

    public Vector2 calculate(AgentBody v) {
        force.set(0,0);
        it = neighborhood.getNearAgentBodys(v, activeDistance);

        while (it.hasNext()) {
            neighbor_vehicle = (AgentBody) it.next();
            temp.set(neighbor_vehicle.position).sub(v.position);
            temp.scl(-1 / temp.len());
            force = force.add(temp);
        }

        force.scl(influence);

        return force;
    }
}