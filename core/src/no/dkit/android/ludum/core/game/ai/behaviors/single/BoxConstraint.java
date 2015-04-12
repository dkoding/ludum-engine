package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class BoxConstraint extends SingleBehavior {
    private Vector2 target = new Vector2();
    private Vector2 source = new Vector2();
    private Vector2 force = new Vector2();
    protected int width;
    protected int height;

    public BoxConstraint(Vector2 target, float influence, int width, int height) {
        super(Config.getDimensions().WORLD_WIDTH, influence);
        this.target = target;
        this.width = width;
        this.height = height;
    }

    public Vector2 calculate(AgentBody veh) {
        source.set(veh.position);
        force.set(0,0);

        if (source.x >= target.x + (width / 2f))
            force.add(-veh.minSpeed, 0);
        if (source.x <= target.x - (width / 2f))
            force.add(veh.minSpeed, 0);
        if (source.y >= target.y + (height / 2f))
            force.add(0, -veh.minSpeed);
        if (source.y <= target.y - (height / 2))
            force.add(0, veh.minSpeed);

        force.scl(getInfluence());

        return force;
    }
}