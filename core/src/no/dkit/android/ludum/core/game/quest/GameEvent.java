package no.dkit.android.ludum.core.game.quest;

import no.dkit.android.ludum.core.game.model.body.GameBody;

public interface GameEvent {
    enum EVENT_TYPE {KILLED, FOUND, DESTROYED}

    public void fire(GameBody gameBody);
}
