package no.dkit.android.ludum.core.game.quest;

import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.controller.GameScreen;

public class QuestTrigger extends Trigger {
    Quest quest;

    public QuestTrigger(Quest quest) {
        this.quest = quest;
    }

    public Quest getQuest() {
        return quest;
    }

    @Override
    public void fire() {
        if (!active) return;
        active = false;
        ((GameScreen) XXXX.getGame().getScreen()).getStage().addActor(new QuestTable(quest));
    }
}
