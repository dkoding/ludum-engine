package no.dkit.android.ludum.core.game.ai.mind;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Behavior;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

import java.util.Iterator;

public class PrioritizingMind extends Mind {
    public Vector2 calculate(AgentBody v) {
        force.set(0, 0);
        boolean proceed = true;

        Iterator<Behavior> iterator = getBehaviors();

        while (iterator.hasNext() && proceed) {
            Behavior behave = iterator.next();

            Vector2 calculatedForce = behave.calculate(v);

            if (calculatedForce.len() >= Config.EPSILON) {
                proceed = false;
                force.add(calculatedForce);
            }
        }

        if (force.len() >= v.maxSpeed) {
            force.scl(v.maxSpeed / force.len());
        }

        return force;
    }

}