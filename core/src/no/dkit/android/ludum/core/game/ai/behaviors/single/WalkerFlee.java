package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class WalkerFlee extends Flee {

    private Vector2 calculate;

    public WalkerFlee(Vector2 target, float activeDistance, float influence) {
        super(target, activeDistance, influence);
    }

    @Override
    public Vector2 calculate(AgentBody veh) {
        calculate = super.calculate(veh);
        calculate.set(calculate.x, 0);
        return calculate;
    }
}