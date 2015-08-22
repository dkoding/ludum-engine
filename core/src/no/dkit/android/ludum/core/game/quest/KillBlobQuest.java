package no.dkit.android.ludum.core.game.quest;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.controller.GameScreen;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.AgentBody;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.world.level.Level;

public class KillBlobQuest extends Quest {
    public KillBlobQuest(Level.LEVEL_TYPE type) {
        super(type);

        addImage(new TextureRegionDrawable(ResourceFactory.getInstance().getWeaponImage("flamethrower")));
/*
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: So, you're the new kid on the block?"));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: Something like that."));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: I thought they were going to send a soldier."));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: And I thought they weren't going to send someone from a retirement home."));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: Ha! I'm not that old, don't give me no cane. I'm Berry King."));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: They said you were the best."));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: They just said that to get you on board."));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: Maybe I should go back to slaving over dishes."));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: Now, don't you have no second thoughts now. If I wasn't good, I wouldn't be riskin' my own life. Gave me the best personal ship out there."));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: Good, can you take me to these coordinate?"));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: Looks like it's gonna be a bumpy ride. Ain't gonna be easy, but I'll get you in one piece. So what should we call her?"));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: Her?"));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: You know, the ship. Gotta give her a name. TRN935 ain't gonna cut it."));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: How about...Pearl?"));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: Pearl? Well then, why don't you climb aboard Pearl and I'll get her running up in no time."));
*/
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: Shhhh... did you hear that?"));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: Hear what?"));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: There it was again! A faint swooshing sound..."));
        addDialogueItem(new DialogueItem(IMAGE_BERRY, "Berry King: Uh oh... a SLIME! Quick, kill it before it damages the ship!"));
        addDialogueItem(new DialogueItem(2, "Berry King: Here, take this FLAMETHROWER! That will deal with it!"));
        addDialogueItem(new DialogueItem(IMAGE_JOPLIN, "Joplin Thorogood: I'm on it!"));
    }

    @Override
    public void startQuest() {
    }

    @Override
    public void completedQuest() {
        ((GameScreen) XXXX.getGame().getScreen()).getStage().addActor(new RewardTable(this));
    }

    @Override
    protected void setupRewards() {
        rewards.clear();
        rewards.add(new RewardItem(new TextureRegionDrawable(ResourceFactory.getInstance().getWeaponImage("bomb")), "You found a BOMB THROWER!", Loot.LOOT_TYPE.BOMB));
    }
}
