package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class WalkerSeek extends Seek {

    private Vector2 calculate;

    public WalkerSeek(Vector2 target, float activeDistance, float influence) {
        super(target, activeDistance, influence);
    }

    public Vector2 calculate(AgentBody veh) {
        calculate = super.calculate(veh);
        calculate.set(calculate.x, 0);
        return calculate;
    }
}
