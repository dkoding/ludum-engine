package no.dkit.android.ludum.core.game.model.world.level;

import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.LevelFactory;
import no.dkit.android.ludum.core.game.model.body.item.TriggerBody;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.game.model.world.map.ObstacleFeatureMap;
import no.dkit.android.ludum.core.game.quest.KillBlobQuest;
import no.dkit.android.ludum.core.game.quest.QuestTrigger;
import no.dkit.android.ludum.core.shaders.RenderOperations;

public class StartLevel extends Level {
    public StartLevel() {
        super(LEVEL_TYPE.TOPDOWN, LevelFactory.level, true, false);
        this.map = new ObstacleFeatureMap().createMap(level, inside, platforms);
        setStartPositionToItem(AbstractMap.START_HINT);

        setupDefaults(worldType);
        addItems(worldType, level);

        background = RenderOperations.BACKGROUND_TYPE.GROUND;

        this.map.removeAllHints();
    }

    @Override
    public void onStart() {
        TriggerBody trigger = BodyFactory.getInstance().createTrigger(2, 2);
        trigger.setTrigger(new QuestTrigger(new KillBlobQuest(worldType)));
    }
}
