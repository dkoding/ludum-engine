package no.dkit.android.ludum.core.game.ai.simulationobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.ai.behaviors.group.GroupBehavior;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Behavior;
import no.dkit.android.ludum.core.game.ai.mind.Mind;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

import java.util.Iterator;

public class Neighborhood {
    protected float[][] distanceMatrix;

    protected Array<AgentBody> closeBodies = new Array<AgentBody>();
    protected int numElements = 0;

    private Array<AgentBody> neighborhoodBodies;

    /**
     * Constructor
     */
    public Neighborhood() {
        neighborhoodBodies = new Array<AgentBody>();
    }

    public void addAgentBody(AgentBody newAgentBody) {
        neighborhoodBodies.add(newAgentBody);
        newAgentBody.setNeighborhood(this);

        Behavior behave;
        Iterator<Behavior> it = newAgentBody.getMind().getBehaviors();

        while (it.hasNext()) {
            behave = it.next();

            if (behave instanceof GroupBehavior)
                ((GroupBehavior) behave).setNeighborhood(this);
        }

        if (neighborhoodBodies.size != numElements) {
            update();
        }
    }

    public void removeAgentBody(AgentBody agentBody) {
        neighborhoodBodies.removeValue(agentBody, false);
        update();
    }

    private void update() {
        init();

        for (int i = 0; i <= numElements - 1; i++) {
            for (int j = i + 1; j <= numElements - 1; j++) {
                AgentBody tmp_v1, tmp_v2;
                Vector2 tmp_p1, tmp_p2;
                Vector2 tmp_vect;

                tmp_v1 = neighborhoodBodies.get(i);
                tmp_v2 = neighborhoodBodies.get(j);

                tmp_p1 = tmp_v1.getBody().getPosition();
                tmp_p2 = tmp_v2.getBody().getPosition();

                tmp_vect = tmp_p1.sub(tmp_p2);

                float lenSqr = tmp_vect.len2();
                distanceMatrix[i][j] = lenSqr;
                distanceMatrix[j][i] = lenSqr;
            }
        }
    }

    /**
     * Initializes the neighborhood object distance matrix
     */
    public void init() {
        numElements = neighborhoodBodies.size;
        distanceMatrix = new float[numElements][numElements];

        for (int i = 0; i <= numElements - 1; i++) {
            for (int j = 0; j <= numElements - 1; j++) {
                distanceMatrix[i][j] = -1;
            }
        }
    }

    public void removeAll() {
        neighborhoodBodies.clear();
        numElements = 0;
    }

    public void setState(Mind.MindState state) {
        for (int i = 0; i < neighborhoodBodies.size; i++) {
            neighborhoodBodies.get(i).getMind().setState(state);
        }
    }

    public int getCount() {
        return numElements;
    }

    public Iterator getNearAgentBodys(AgentBody v, float distance) {
        closeBodies.clear();
        int vehicleIndex = neighborhoodBodies.indexOf(v, false);
        float distSquared = distance * distance;

        for (int i = 0; i <= numElements - 1; i++) {
            if ((i != vehicleIndex) && (distanceMatrix[vehicleIndex][i] <= distSquared))
                closeBodies.add(neighborhoodBodies.get(i));
        }

        return closeBodies.iterator();
    }

    public Array<AgentBody> getAgentBodyList() {
        return neighborhoodBodies;
    }
}