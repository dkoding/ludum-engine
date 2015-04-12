package no.dkit.android.ludum.core.game.ai.mind;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Behavior;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

import java.util.Iterator;


public class BlendingMind extends Mind {
    public Vector2 calculate(AgentBody v) {
        force.set(0, 0);

        Iterator<Behavior> iterator = getBehaviors();

        while (iterator.hasNext()) {
            Behavior behavior = iterator.next();
            force.add(behavior.calculate(v));
        }

        return force;
    }

}