package no.dkit.android.ludum.core.game.transition;

import com.badlogic.gdx.Screen;

public abstract class TransitionEffect {
    protected double start;
    protected double end;
    protected double duration;

    // returns a value between 0 and 1 representing the level of completion of the transition.
    protected float getDelta() {
        return (float)(System.currentTimeMillis()-start)/(float)duration;
    }

    protected abstract void render(Screen current, Screen next);

    protected boolean isFinished() {
        return System.currentTimeMillis() > end;
    }

    public TransitionEffect(long duration) {
        this.duration = duration;
        init();
    }

    public void init() {
        this.start = System.currentTimeMillis();
        this.end = this.start + duration;
    }
}