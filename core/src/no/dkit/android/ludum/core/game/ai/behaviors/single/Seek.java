package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class Seek extends SingleBehavior {
    Vector2 des_vel = new Vector2();
    protected Vector2 target = new Vector2();
    float distance;

    public Seek(Vector2 target, float activeDistance, float influence) {
        super(activeDistance, influence);
        this.target = target;
    }

    public Vector2 calculate(AgentBody veh) {
        if (veh.getBody() == null) return NO_VECTOR;
        if (target == null) return Vector2.Zero;
        des_vel.set(target).sub(veh.position);
        distance = des_vel.len();
        if (distance > getActiveDistance()) return NO_VECTOR;
        des_vel.scl(veh.getMaxSpeed() / distance);
        des_vel.sub(veh.getBody().getLinearVelocity());
        des_vel.scl(getInfluence());
        return des_vel;
    }

}