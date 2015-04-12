package no.dkit.android.ludum.core.game.transition;

import com.badlogic.gdx.Screen;

public abstract class TransitionEffect {
    protected float start;
    protected float duration;

    // returns a value between 0 and 1 representing the level of completion of the transition.
    protected float getAlpha() {
        return 1f-duration/start;
    }

    protected void update(float delta) {
        duration -= delta;
        if (duration < 0) duration = 0;
    }

    protected abstract void render(Screen current, Screen next);

    protected boolean isFinished() {
        return duration == 0;
    }

    public TransitionEffect(float duration) {
        this.duration = duration;
        this.start = duration;
    }
}