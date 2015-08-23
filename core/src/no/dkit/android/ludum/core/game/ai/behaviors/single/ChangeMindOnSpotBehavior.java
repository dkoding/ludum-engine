package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.mind.Mind;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class ChangeMindOnSpotBehavior extends SingleBehavior {
    private Vector2 target = new Vector2();
    private Vector2 tmp = new Vector2();
    private Mind.MindState newState;
    private boolean whenSpotted;
    private boolean whenNoLongeSpotted;
    private boolean considerAngle;
    private float angleToTargetDeg;
    private float vehAngleDeg;
    private boolean withinAngle = false;
    private float shortestAngle;

    public ChangeMindOnSpotBehavior(Vector2 target, float distance, Mind.MindState toState, boolean whenSpotted, boolean considerAngle) {
        super(distance, 1);
        this.target = target;
        this.newState = toState;
        this.whenSpotted = whenSpotted;
        this.whenNoLongeSpotted = !whenSpotted; // For clarity
        this.considerAngle = considerAngle;
    }

    public Vector2 calculate(AgentBody veh) {
        if (MathUtils.random() > Config.AGENT_SPOT_CHANCE) return NO_VECTOR;

        boolean withinReach = tmp.set(target).sub(veh.position).len() < getActiveDistance();

        if (considerAngle) {
            vehAngleDeg = veh.getAngle() * MathUtils.radiansToDegrees;

            tmp.set(target.x - veh.position.x, target.y - veh.position.y);
            angleToTargetDeg = (MathUtils.radiansToDegrees * MathUtils.atan2(tmp.y, tmp.x));

            if (angleToTargetDeg < 0) angleToTargetDeg += 360;
            if (angleToTargetDeg > 360) angleToTargetDeg -= 360;
            if (vehAngleDeg < 0) vehAngleDeg += 360;
            if (vehAngleDeg > 360) vehAngleDeg -= 360;

            shortestAngle = Math.abs(getShortestAngle(angleToTargetDeg, vehAngleDeg));
            //System.out.printf("Look: %1s To target: %2s AngleDiff: %3s\n", vehAngleDeg, angleToTargetDeg, shortestAngle);

            withinAngle = shortestAngle <= Config.AGENT_SPOT_ARC;
        }

        if (whenSpotted && withinReach) {
            if (considerAngle) {
                if (withinAngle)
                    veh.getMind().setState(newState);
            } else
                veh.getMind().setState(newState);
        }

        if (whenNoLongeSpotted) {
            if ((!withinReach || (considerAngle && !withinAngle))) {
                veh.getMind().setState(newState);
            }
        }
/*
        if (Config.DEBUGTEXT)
            System.out.println("MIND: " + veh.getMind().getState());
*/

        return NO_VECTOR;
    }

    public static float getShortestAngle(float angleDeg1, float angleDeg2) {
        float difference = angleDeg2 - angleDeg1;
        float lower = -180.0f;
        float upper = +180.0f;

        if (upper <= lower)
            throw new ArithmeticException("Rotary bounds are of negative or zero size");

        float distance = upper - lower;
        float times = (float) Math.floor((difference - lower) / distance);

        return difference - (times * distance);
    }
}
