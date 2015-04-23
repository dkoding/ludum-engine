package no.dkit.android.ludum.core.game.transition;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import no.dkit.android.ludum.core.game.controller.GameScreen;
import no.dkit.android.ludum.core.game.controller.MenuScreen;

import java.util.ArrayList;

public class TransitionScreen implements Screen {
    Game game;
    Screen current;
    Screen next;
    DoneCallback done;

    int currentTransitionEffect;
    ArrayList<TransitionEffect> transitionEffects;

    public TransitionScreen(Game game, Screen current, Screen next, DoneCallback done) {
        this.done = done;
        this.game = game;
        this.current = current;
        this.next = next;
        this.currentTransitionEffect = 0;
        transitionEffects = new ArrayList<TransitionEffect>();
        transitionEffects.add(new FadeOutEffect(500, Color.BLACK));
    }

    public void render(float delta) {
        if (currentTransitionEffect == transitionEffects.size()) {
            game.setScreen(next);

            if (!(current instanceof GameScreen))
                current.dispose();
            if (current instanceof GameScreen && next instanceof MenuScreen)
                current.dispose();
            done.done(current, next);
            return;
        }

        transitionEffects.get(currentTransitionEffect).render(current, next);

        if (transitionEffects.get(currentTransitionEffect).isFinished()) {
            currentTransitionEffect++;
            if (currentTransitionEffect < transitionEffects.size())
                transitionEffects.get(currentTransitionEffect).init();
        }
    }

    public void resize(int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void show() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void hide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pause() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}