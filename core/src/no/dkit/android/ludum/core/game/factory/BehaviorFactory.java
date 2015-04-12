package no.dkit.android.ludum.core.game.factory;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.ai.behaviors.group.ChangeGroupMindBehavior;
import no.dkit.android.ludum.core.game.ai.behaviors.group.Flocking;
import no.dkit.android.ludum.core.game.ai.behaviors.group.GroupArrive;
import no.dkit.android.ludum.core.game.ai.behaviors.group.GroupOffsetArriveDistanced;
import no.dkit.android.ludum.core.game.ai.behaviors.group.GroupSeek;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Arrive;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Behavior;
import no.dkit.android.ludum.core.game.ai.behaviors.single.BoxConstraint;
import no.dkit.android.ludum.core.game.ai.behaviors.single.ChangeMindBehavior;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Evade;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Flee;
import no.dkit.android.ludum.core.game.ai.behaviors.single.OffsetArriveAngled;
import no.dkit.android.ludum.core.game.ai.behaviors.single.OffsetArriveDistanced;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Pursuit;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Seek;
import no.dkit.android.ludum.core.game.ai.mind.Mind;
import no.dkit.android.ludum.core.game.ai.simulationobjects.Neighborhood;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

public class BehaviorFactory {
    public enum MINDBEHAVIOR {AGGRESSIVE, DEFENSIVE, NEUTRAL}

    public enum BEHAVIOR {ARRIVE, BOX, FLEE, EVADE, PURSUIT, SEEK, WANDER}

    public enum OFFSETBEHAVIOR {OFFSETARRIVEANGLED, OFFSETSEEK, OFFSETARRIVE, OFFSETSEEKANGLED, OFFSETARRIVEDISTANCED}

    public enum GROUPBEHAVIOR {ALIGNMENT, COHESION, FLOCKING, GROUPARRIVE, GROUPSEEK, SEPARATION}

    public enum GROUPOFFSETBEHAVIOR {GROUPOFFSETARRIVEDISTANCED}

    public static void setupDefaultMindBehaviors(Vector2 target, AgentBody agent) {
        agent.getMind().addBehavior(getMindBehavior(BehaviorFactory.MINDBEHAVIOR.AGGRESSIVE, target, true, true));
        agent.getMind().addAttackBehavior(getMindBehavior(BehaviorFactory.MINDBEHAVIOR.NEUTRAL, target, false, true));
        agent.getMind().addDefendBehavior(getMindBehavior(BehaviorFactory.MINDBEHAVIOR.NEUTRAL, target, false, true));
    }

    public static void setupDefaultGroupMindBehaviors(Vector2 target, AgentBody agent, Neighborhood neighborhood) {
        agent.getMind().addBehavior(getGroupMindBehavior(MINDBEHAVIOR.AGGRESSIVE, neighborhood, target, true, true));
        agent.getMind().addAttackBehavior(getGroupMindBehavior(MINDBEHAVIOR.AGGRESSIVE, neighborhood, target, true, false));
    }

    public static void setupRangedBehavior(Vector2 target, AgentBody agent, AgentBody targetAgent, Vector2 position) {
        setupDefaultMindBehaviors(target, agent);

        agent.getMind().addBehavior(getSingleBehavior(BEHAVIOR.BOX, position));
        agent.getMind().addAttackBehavior(getOffsetBehavior(OFFSETBEHAVIOR.OFFSETARRIVEDISTANCED, targetAgent, 1f, 0f));
        agent.getMind().addDefendBehavior(getSingleBehavior(BEHAVIOR.FLEE, target));

        agent.getBody().setLinearVelocity(
                MathUtils.random(agent.minSpeed * 2) - agent.minSpeed,
                MathUtils.random(agent.minSpeed * 2) - agent.minSpeed);
    }

    public static void setupRangedGroupBehavior(Vector2 target, AgentBody agent, AgentBody targetAgent, Vector2 position, Neighborhood neighborhood) {
        setupDefaultMindBehaviors(target, agent);

        agent.getMind().addBehavior(getSingleBehavior(BehaviorFactory.BEHAVIOR.BOX, position));
        agent.getMind().addAttackBehavior(getGroupOffsetBehavior(BehaviorFactory.GROUPOFFSETBEHAVIOR.GROUPOFFSETARRIVEDISTANCED, neighborhood, targetAgent, 1f, 0f));
        agent.getMind().addDefendBehavior(getSingleBehavior(BehaviorFactory.BEHAVIOR.FLEE, target));

        agent.getBody().setLinearVelocity(
                MathUtils.random(agent.minSpeed * 2) - agent.minSpeed,
                MathUtils.random(agent.minSpeed * 2) - agent.minSpeed);
    }

    public static void setupMeleeBehavior(Vector2 target, AgentBody agent, Vector2 position) {
        setupDefaultMindBehaviors(target, agent);

        agent.getMind().addBehavior(getSingleBehavior(BehaviorFactory.BEHAVIOR.BOX, position));
        agent.getMind().addAttackBehavior(getSingleBehavior(BehaviorFactory.BEHAVIOR.SEEK, target));
        agent.getMind().addDefendBehavior(getSingleBehavior(BehaviorFactory.BEHAVIOR.FLEE, target));

        agent.getBody().setLinearVelocity(
                MathUtils.random(agent.minSpeed * 2) - agent.minSpeed,
                MathUtils.random(agent.minSpeed * 2) - agent.minSpeed);
    }

    public static void setupMeleeGroupBehavior(Vector2 target, AgentBody agent, Vector2 position, Neighborhood neighborhood) {
        setupDefaultMindBehaviors(target, agent);

        agent.getMind().addBehavior(getSingleBehavior(BehaviorFactory.BEHAVIOR.BOX, position));
        agent.getMind().addAttackBehavior(getGroupBehavior(BehaviorFactory.GROUPBEHAVIOR.GROUPSEEK, neighborhood, target));
        agent.getMind().addDefendBehavior(getSingleBehavior(BehaviorFactory.BEHAVIOR.FLEE, target));

        agent.getBody().setLinearVelocity(
                MathUtils.random(agent.minSpeed * 2) - agent.minSpeed,
                MathUtils.random(agent.minSpeed * 2) - agent.minSpeed);
    }

    public static Behavior getSingleBehavior(BEHAVIOR behavior, Vector2 target) {
        switch (behavior) {
            case ARRIVE:
                return new Arrive(target, Config.AGENT_ARRIVE_STEPS, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE);
            case BOX:
                return new BoxConstraint(target, Config.AGENT_INFLUENCE_FORCE*10f, Config.getDimensions().AGENT_BOX_WIDTH, Config.getDimensions().AGENT_BOX_HEIGHT);
            case FLEE:
                return new Flee(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE / 2f);
            case EVADE:
                return new Evade(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE);
            case PURSUIT:
                return new Pursuit(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE);
            case SEEK:
                return new Seek(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE);
            case WANDER:
        }
        throw new RuntimeException("Unknown behavior " + behavior);
    }

    public static Behavior getOffsetBehavior(OFFSETBEHAVIOR behavior, AgentBody target, float offsetX, float offsetY) {
        switch (behavior) {
            case OFFSETARRIVE:
            case OFFSETARRIVEDISTANCED:
                return new OffsetArriveDistanced(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE, Config.AGENT_ARRIVE_STEPS, offsetX);
            case OFFSETARRIVEANGLED:
                return new OffsetArriveAngled(target, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE, Config.AGENT_ARRIVE_STEPS, offsetX, offsetY);
            case OFFSETSEEK:
            case OFFSETSEEKANGLED:
        }
        throw new RuntimeException("Unknown behavior " + behavior);
    }

    public static Behavior getMindBehavior(MINDBEHAVIOR behavior, Vector2 target, boolean spotted, boolean considerAngle) {
        switch (behavior) {
            case AGGRESSIVE:
                return new ChangeMindBehavior(target, Config.AGENT_SPOT_DISTANCE, Mind.MindState.ATTACK, spotted, considerAngle);
            case DEFENSIVE:
                return new ChangeMindBehavior(target, Config.AGENT_SPOT_DISTANCE, Mind.MindState.DEFEND, spotted, considerAngle);
            case NEUTRAL:
                return new ChangeMindBehavior(target, Config.AGENT_SPOT_DISTANCE, Mind.MindState.NEUTRAL, spotted, considerAngle);
        }
        throw new RuntimeException("Unknown behavior " + behavior);
    }

    public static Behavior getGroupMindBehavior(MINDBEHAVIOR behavior, Neighborhood neighborhood, Vector2 target, boolean spotted, boolean considerAngle) {
        switch (behavior) {
            case AGGRESSIVE:
                return new ChangeGroupMindBehavior(neighborhood, target, Config.AGENT_SPOT_DISTANCE, Mind.MindState.ATTACK, spotted, considerAngle);
            case DEFENSIVE:
                return new ChangeGroupMindBehavior(neighborhood, target, Config.AGENT_SPOT_DISTANCE, Mind.MindState.DEFEND, spotted, considerAngle);
            case NEUTRAL:
                return new ChangeGroupMindBehavior(neighborhood, target, Config.AGENT_SPOT_DISTANCE, Mind.MindState.NEUTRAL, spotted, considerAngle);
        }
        throw new RuntimeException("Unknown behavior " + behavior);
    }

    public static Behavior getGroupBehavior(GROUPBEHAVIOR behavior, Neighborhood neighborhood, Vector2 target) {
        if (neighborhood.getCount() <= 0) throw new RuntimeException("Neighborhood is empty!");

        switch (behavior) {
            case ALIGNMENT:
            case COHESION:
            case FLOCKING:
                return new Flocking(neighborhood, Config.getDimensions().AGENT_INFLUENCE_AREA, Config.AGENT_INFLUENCE_FORCE);
            case GROUPARRIVE:
                return new GroupArrive(neighborhood, target, Config.getDimensions().AGENT_INFLUENCE_AREA * 2f, Config.AGENT_ARRIVE_STEPS, Config.AGENT_INFLUENCE_FORCE);
            case GROUPSEEK:
                return new GroupSeek(neighborhood, target, Config.getDimensions().AGENT_INFLUENCE_AREA * 2f, Config.AGENT_INFLUENCE_FORCE);
            case SEPARATION:
        }
        throw new RuntimeException("Unknown behavior " + behavior);
    }

    public static Behavior getGroupOffsetBehavior(GROUPOFFSETBEHAVIOR behavior, Neighborhood neighborhood, AgentBody target, float offsetX, float offsetY) {
        if (neighborhood.getCount() <= 0) throw new RuntimeException("Neighborhood is empty!");

        switch (behavior) {
            case GROUPOFFSETARRIVEDISTANCED:
                return new GroupOffsetArriveDistanced(neighborhood, target, Config.getDimensions().AGENT_INFLUENCE_AREA * 2f, Config.AGENT_INFLUENCE_FORCE, Config.AGENT_ARRIVE_STEPS, offsetX);
        }
        throw new RuntimeException("Unknown behavior " + behavior);
    }
}
