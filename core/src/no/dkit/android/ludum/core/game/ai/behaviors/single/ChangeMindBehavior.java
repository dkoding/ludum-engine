package no.dkit.android.ludum.core.game.ai.behaviors.single;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.mind.Mind;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class ChangeMindBehavior extends SingleBehavior {
    private Mind.MindState newState;
    float chance;

    public ChangeMindBehavior(Mind.MindState toState, float change) {
        super(1, 1);
        this.newState = toState;
        this.chance = change;
    }

    public Vector2 calculate(AgentBody veh) {
        if (MathUtils.random() < chance)
            veh.getMind().setState(newState);

        return NO_VECTOR;
    }
}
