package no.dkit.android.ludum.core.game.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import no.dkit.android.ludum.core.game.factory.LootFactory;
import no.dkit.android.ludum.core.game.model.GameModel;

public class RewardTable extends Table {
    final RewardTable table;

    public RewardTable(Quest quest) {
        table = this;
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        setSkin(skin);
        RewardItem rewardItem = quest.getRewards().get(0);
        Image rewardImage = new Image(rewardItem.getImage());
        TextButton button = new TextButton("X", skin, "medium");
        Label header = new Label("MISSION COMPLETE!", skin, "halftransparent");
        header.setAlignment(Align.top | Align.left);
        Label text = new Label(rewardItem.getText(), skin, "halftransparent");
        text.setWrap(true);
        text.setAlignment(Align.top | Align.left);

        add(button).align(Align.left);
        add(header).align(Align.right);
        row();
        add(rewardImage);
        add(text).size(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 4).align(Align.bottom);
        pack();
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2, Gdx.graphics.getHeight() / 2 - getHeight() / 2);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.remove();
            }
        });

        GameModel.getPlayer().addLoot(LootFactory.getInstance().getLoot(rewardItem.getType()));
    }
}
