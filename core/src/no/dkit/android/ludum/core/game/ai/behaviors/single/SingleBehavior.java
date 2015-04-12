package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public abstract class SingleBehavior implements Behavior {
    public static final Vector2 NO_VECTOR = new Vector2(0, 0);

    private float influence;
    private float activeDistance;

    public SingleBehavior(float activeDistance, float influence) {
        this.influence = influence;
        this.activeDistance = activeDistance;
    }

    public float getInfluence() {
        return influence;
    }

    public Vector2 calculate(AgentBody veh) {
        return Vector2.Zero;
    }

    final public void setInfluence(float influence) {
        this.influence = influence;
    }

    final public void setActiveDistance(float adistance) {
        activeDistance = adistance;
    }

    public float getActiveDistance() {
        return activeDistance;
    }

    public int compareTo(Behavior o) {
        return 0;
    }
}