package no.dkit.android.ludum.core.game.quest;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import no.dkit.android.ludum.core.game.model.loot.Loot;

public class RewardItem {
    TextureRegionDrawable drawable;
    String text;
    Loot.LOOT_TYPE type;

    public RewardItem(TextureRegionDrawable drawable, String text, Loot.LOOT_TYPE type) {
        this.drawable = drawable;
        this.text = text;
        this.type = type;
    }

    public TextureRegionDrawable getImage() {
        return drawable;
    }

    public String getText() {
        return text;
    }

    public Loot.LOOT_TYPE getType() {
        return type;
    }
}
