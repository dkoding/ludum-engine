package no.dkit.android.ludum.core.game.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class QuestTable extends Table {
    private final Quest quest;
    private final Label text;
    private final Image faceImage;

    public QuestTable(Quest quest) {
        this.quest = quest;
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        setSkin(skin);
        faceImage = new Image(quest.getCurrentImage());

        text = new Label(quest.getCurrentText(), skin, "halftransparent");
        text.setWrap(true);
        text.setAlignment(Align.top | Align.left);
        text.setFontScale(1);

        defaults().align(Align.right);
        add(faceImage);
        add(text).size(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 4).align(Align.bottom);
        pack();
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2, Gdx.graphics.getHeight() / 2 - getHeight() / 2);

        text.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateStory();
            }
        });
    }

    private void updateStory() {
        if (!quest.progressDialog()) {
            this.remove();
            return;
        }
        text.setText(quest.getCurrentText());
        faceImage.setDrawable(quest.getCurrentImage());
    }
}
