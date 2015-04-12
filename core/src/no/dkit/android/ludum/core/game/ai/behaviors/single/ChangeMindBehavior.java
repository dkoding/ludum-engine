package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.mind.Mind;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class ChangeMindBehavior extends SingleBehavior {
    private Vector2 target = new Vector2();
    private Vector2 tmp = new Vector2();
    private Mind.MindState newState;
    private boolean whenSpotted;
    private boolean considerAngle;
    private float angleToTarget;
    private float vehAngle;
    private boolean withinAngle = false;

    public ChangeMindBehavior(Vector2 target, float distance, Mind.MindState toState, boolean whenSpotted, boolean considerAngle) {
        super(distance, 1);
        this.target = target;
        this.newState = toState;
        this.whenSpotted = whenSpotted;
        this.considerAngle = considerAngle;
    }

    public Vector2 calculate(AgentBody veh) {
        if (MathUtils.random() < Config.AGENT_SPOT_CHANCE) return NO_VECTOR;

        boolean withinReach = tmp.set(target).sub(veh.position).len() < getActiveDistance();

        if (considerAngle) {
            vehAngle = veh.getAngle();

            tmp.set(target.x - veh.position.x, target.y - veh.position.y);
            angleToTarget = (MathUtils.radiansToDegrees * MathUtils.atan2(tmp.y, tmp.x));

            if(angleToTarget < 0) angleToTarget+=360;
            if(vehAngle < 0) vehAngle+=360;

            if (Math.abs(angleToTarget-vehAngle) < Config.AGENT_SPOT_ARC) withinAngle = true;
        }

        if (whenSpotted && withinReach) {
            if (!considerAngle || (considerAngle && withinAngle)) {
                veh.getMind().setState(newState);
            }
        }

        if (!whenSpotted && !withinReach) // Contact with target lost
            veh.getMind().setState(newState);

        if (Config.DEBUGTEXT)
            System.out.println("MIND: " + veh.getMind().getState());

        return NO_VECTOR;
    }
}
