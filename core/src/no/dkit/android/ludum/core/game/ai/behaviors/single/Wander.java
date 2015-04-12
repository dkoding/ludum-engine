package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class Wander extends SingleBehavior {
    protected Vector2 force = new Vector2();
    protected Vector2 circlePosition;
    protected Vector2 seekDirection;

    public Wander(Vector2 circlePosition, int circleRadius, float influence) {
        super(circleRadius, influence);
        this.circlePosition = circlePosition;
        seekDirection.set(0,0);
    }

    public Wander(float x, float y, int circleRadius, float influence) {
        super(circleRadius, influence);
        this.circlePosition.set(x, y);
        seekDirection.set(0,0);
    }

    public Vector2 calculate(AgentBody v) {
        if (v.getBody() == null) return NO_VECTOR;

        if (MathUtils.random() < .001f) {
            force.set(0, 0);
            force.set(v.getBody().getLinearVelocity());
            force.rotate(MathUtils.random(360) - 180f);
        }

        return force;
    }
}