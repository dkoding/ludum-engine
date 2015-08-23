package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.group.Flocking;
import no.dkit.android.ludum.core.game.ai.behaviors.group.GroupArrive;
import no.dkit.android.ludum.core.game.ai.behaviors.group.GroupFlee;
import no.dkit.android.ludum.core.game.ai.behaviors.group.Separation;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Arrive;
import no.dkit.android.ludum.core.game.ai.behaviors.single.BoxConstraint;
import no.dkit.android.ludum.core.game.ai.behaviors.single.ChangeMindOnSpotBehavior;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Flee;
import no.dkit.android.ludum.core.game.ai.mind.Mind;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.world.level.Level;

public class BehaviorFactory {

    public static final float FLEE_FACTOR = Config.AGENT_INFLUENCE_FORCE * 2;
    public static final float ARRIVE_FACTOR = Config.AGENT_INFLUENCE_FORCE * 1.5f;
    public static final float FLEE_DISTANCE = Config.getDimensions().AGENT_SPOT_DISTANCE * 1.5f;

    // Switch between defend and neutral
    public static void civilianBehavior(Vector2 target, AgentBody agent) {
        applyBoxConstraints(agent);

        agent.getMind().addBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.DEFEND, true, false));
        agent.getMind().addDefendBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.NEUTRAL, false, false));
        agent.getMind().addAttackBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.DEFEND, true, false));

        agent.getMind().addDefendBehavior(new Flee(target, FLEE_DISTANCE, FLEE_FACTOR));
        agent.getMind().addAttackBehavior(new Arrive(target, 3, Config.getDimensions().WORLD_WIDTH, ARRIVE_FACTOR));
    }

    // Switch between defend and neutral
    public static void soldierBehavior(Vector2 target, AgentBody agent) {
        applyBoxConstraints(agent);

        agent.getMind().addBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.ATTACK, true, false));
        agent.getMind().addDefendBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.NEUTRAL, false, false));
        agent.getMind().addAttackBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.ATTACK, true, false));

        agent.getMind().addDefendBehavior(new Flee(target, FLEE_DISTANCE, FLEE_FACTOR));
        agent.getMind().addAttackBehavior(new Arrive(target, 3, Config.getDimensions().WORLD_WIDTH, ARRIVE_FACTOR));
    }

    // Switch between defend and neutral, but sometimes attack
    public static void civilianGroupBehavior(Vector2 target, Neighborhood neighborhood) {
        final Array<AgentBody> agentBodyList = neighborhood.getAgentBodyList();

        final Separation separation = new Separation(neighborhood, Config.getDimensions().WORLD_WIDTH, Config.AGENT_INFLUENCE_FORCE * 3);

        final GroupFlee fleeing = new GroupFlee(neighborhood, target, Config.AGENT_INFLUENCE_FORCE * 1.5f, FLEE_FACTOR);
        final GroupArrive groupArrive = new GroupArrive(neighborhood, target, Config.AGENT_INFLUENCE_FORCE, 3, ARRIVE_FACTOR);

        for (AgentBody agent : agentBodyList) {
            applyBoxConstraints(agent);

            agent.getMind().addBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.DEFEND, true, false));
            agent.getMind().addDefendBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.NEUTRAL, false, false));
            agent.getMind().addAttackBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.DEFEND, true, false));

            agent.getMind().addDefendBehavior(fleeing);
            agent.getMind().addAttackBehavior(groupArrive);

            agent.getMind().addBehavior(separation);
        }
    }

    public static void soldierGroupBehavior(Vector2 target, Neighborhood neighborhood) {
        final Array<AgentBody> agentBodyList = neighborhood.getAgentBodyList();

        final Flocking flocking = new Flocking(neighborhood, Config.getDimensions().WORLD_WIDTH, Config.AGENT_INFLUENCE_FORCE * 3);

        final GroupFlee fleeing = new GroupFlee(neighborhood, target, Config.AGENT_INFLUENCE_FORCE * 1.5f, FLEE_FACTOR);
        final GroupArrive groupArrive = new GroupArrive(neighborhood, target, Config.AGENT_INFLUENCE_FORCE, 3, ARRIVE_FACTOR);

        for (AgentBody agent : agentBodyList) {
            applyBoxConstraints(agent);

            agent.getMind().addBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.ATTACK, true, false));
            agent.getMind().addDefendBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.NEUTRAL, false, false));
            agent.getMind().addAttackBehavior(new ChangeMindOnSpotBehavior(target, Config.getDimensions().AGENT_SPOT_DISTANCE, Mind.MindState.ATTACK, true, false));

            agent.getMind().addDefendBehavior(fleeing);
            agent.getMind().addAttackBehavior(groupArrive);

            agent.getMind().addBehavior(flocking);
        }
    }

    private static void applyBoxConstraints(AgentBody agent) {
        final BoxConstraint boxConstraint = new BoxConstraint(
                new Vector2(Level.getInstance().getMap().getWidth() / 2f, Level.getInstance().getMap().getHeight() / 2f),
                Config.AGENT_INFLUENCE_FORCE * 10,
                Level.getInstance().getMap().getWidth(), Level.getInstance().getMap().getHeight());

        agent.getMind().addBehavior(boxConstraint);
        agent.getMind().addDefendBehavior(boxConstraint);
        agent.getMind().addAttackBehavior(boxConstraint);
    }
}
