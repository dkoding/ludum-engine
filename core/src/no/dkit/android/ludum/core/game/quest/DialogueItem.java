package no.dkit.android.ludum.core.game.quest;

public class DialogueItem {
    int imageIndex;
    String text;

    public DialogueItem(int imageIndex, String text) {
        this.imageIndex = imageIndex;
        this.text = text;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public String getText() {
        return text;
    }
}
