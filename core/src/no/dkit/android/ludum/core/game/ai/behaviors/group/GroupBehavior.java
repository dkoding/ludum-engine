package no.dkit.android.ludum.core.game.ai.behaviors.group;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Behavior;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;

public abstract class GroupBehavior implements Behavior {
    public static final Vector2 NO_VECTOR = new Vector2(0, 0);

    protected Neighborhood neighborhood;
    protected float activeDistance;
    protected float influence;

    public GroupBehavior(float nearAreaRadius, Neighborhood neighborhood, float influence) {
        activeDistance = nearAreaRadius;
        this.neighborhood = neighborhood;
        this.influence = influence;
    }

    public void setNeighborhood(Neighborhood m_neighborhood) {
        this.neighborhood = m_neighborhood;
    }

    public int compareTo(Behavior o) {
        return 0;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public float getActiveDistance() {
        return activeDistance;
    }

    public float getInfluence() {
        return influence;
    }
}
