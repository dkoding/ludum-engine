package no.dkit.android.ludum.core.game.quest;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.world.level.Level;

public abstract class Quest {
    protected int dialogueStep = 0;
    protected int IMAGE_JOPLIN = 0;
    protected int IMAGE_BERRY = 1;
    protected Level.LEVEL_TYPE type;

    private Array<DialogueItem> dialogueItems = new Array<DialogueItem>();
    private Array<TextureRegionDrawable> images = new Array<TextureRegionDrawable>();

    protected Array<RewardItem> rewards = new Array<RewardItem>();

    public Quest(Level.LEVEL_TYPE type) {
        this.type = type;
        setupDialogueImages();
        setupRewards();
    }

    protected void setupRewards() {
        rewards.add(new RewardItem(new TextureRegionDrawable(ResourceFactory.getInstance().getItemImage("orb")), "You found some orbs!", Loot.LOOT_TYPE.ORB));
    }

    protected void setupDialogueImages() {
        images.add(new TextureRegionDrawable(ResourceFactory.getInstance().getImage(ResourceFactory.UI, "face")));
        images.add(new TextureRegionDrawable(ResourceFactory.getInstance().getImage(ResourceFactory.UI, "face2")));
    }

    public void addDialogueItem(DialogueItem item) {
        dialogueItems.add(item);
    }

    public void addImage(TextureRegionDrawable image) {
        images.add(image);
    }

    public Drawable getCurrentImage() {
        return images.get(dialogueItems.get(dialogueStep).getImageIndex());
    }

    public Array<RewardItem> getRewards() {
        return rewards;
    }

    public String getCurrentText() {
        return dialogueItems.get(dialogueStep).getText();
    }

    public boolean progressDialog() {
        dialogueStep++;

        if (dialogueStep == dialogueItems.size)
            startQuest();

        return dialogueItems.size > dialogueStep;
    }

    public abstract void startQuest();

    public abstract void completedQuest();
}