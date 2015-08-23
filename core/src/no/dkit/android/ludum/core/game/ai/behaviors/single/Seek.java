package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class Seek extends SingleBehavior {
    Vector2 force = new Vector2();
    private Vector2 target = new Vector2();
    float distance;

    public Seek(Vector2 target, float activeDistance, float influence) {
        super(activeDistance, influence);
        this.target = target;
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;
        force.set(target).sub(veh.position);
        distance = force.len();
        if (distance > getActiveDistance()) return NO_VECTOR;
        force.scl(veh.getMaxSpeed() / distance);
        force.sub(veh.getBody().getLinearVelocity());
        force.scl(getInfluence());

        return force;
    }

}