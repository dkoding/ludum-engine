package no.dkit.android.ludum.core.game.ai.mind;

import com.badlogic.gdx.math.Vector2;
import no.dkit.android.ludum.core.game.ai.behaviors.single.Behavior;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;

import java.util.Iterator;
import java.util.PriorityQueue;

public abstract class Mind {
    public enum MindState {ATTACK, DEFEND, NEUTRAL}

    MindState state = MindState.NEUTRAL;

    Vector2 force = new Vector2();

    protected PriorityQueue<Behavior> neutralBehaviors = null;
    protected PriorityQueue<Behavior> attackBehaviors = null;
    protected PriorityQueue<Behavior> defendBehaviors = null;

    public Mind() {
        neutralBehaviors = new PriorityQueue<Behavior>();
        attackBehaviors = new PriorityQueue<Behavior>();
        defendBehaviors = new PriorityQueue<Behavior>();
    }

    public Mind(PriorityQueue<Behavior> behaviors) {
        this.neutralBehaviors = behaviors;
    }

    public void addBehavior(Behavior behave) {
        neutralBehaviors.add(behave);
    }

    public Iterator<Behavior> getBehaviors() {
        if (state == MindState.ATTACK)
            return attackBehaviors.iterator();
        else if (state == MindState.DEFEND)
            return defendBehaviors.iterator();
        else
            return neutralBehaviors.iterator();
    }

    public void addAttackBehavior(Behavior behave) {
        attackBehaviors.add(behave);
    }

    public void addDefendBehavior(Behavior behave) {
        defendBehaviors.add(behave);
    }

    public abstract Vector2 calculate(AgentBody v);

    public void clear() {
        neutralBehaviors.clear();
        attackBehaviors.clear();
        defendBehaviors.clear();
    }

    public MindState getState() {
        return state;
    }

    public void setState(MindState state) {
        this.state = state;
    }
}
