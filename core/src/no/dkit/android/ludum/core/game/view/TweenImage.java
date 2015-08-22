package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TweenImage extends TextureRegion {
    final private TextureRegion tweenTextureRegion;
    private float width;
    private float height;
    private float xPos;
    private float yPos;
    private float xScale;
    private float yScale;
    private float rotation;
    private float alpha;

    private Color color = new Color(Color.WHITE);
    private float radiusX;
    private float radiusY;

    public TweenImage(TextureRegion tweenTextureRegion, float xPos, float yPos, float xScale, float yScale, float rotation, float alpha, float width, float height) {
        this.tweenTextureRegion = tweenTextureRegion;
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xScale = xScale;
        this.yScale = yScale;
        this.rotation = rotation;
        this.alpha = alpha;
        this.radiusX = this.width / 2;
        this.radiusY = this.height / 2;
    }

    public TweenImage(TextureRegion tweenTextureRegion, float xPos, float yPos, float xScale, float yScale, float rotation, float alpha) {
        this.tweenTextureRegion = tweenTextureRegion;
        this.width = tweenTextureRegion.getRegionWidth();
        this.height = tweenTextureRegion.getRegionHeight();
        this.xPos = xPos;
        this.yPos = yPos;
        this.xScale = xScale;
        this.yScale = yScale;
        this.rotation = rotation;
        this.alpha = alpha;
        this.radiusX = this.width / 2;
        this.radiusY = this.height / 2;
    }

    public void draw(Batch spriteBatch) {
        this.draw(spriteBatch, 0);
    }

    public void draw(Batch spriteBatch, float angleMod) {
        color.set(color.r, color.g, color.b, alpha);
        spriteBatch.setColor(color);
        spriteBatch.enableBlending();
        spriteBatch.draw(tweenTextureRegion,
                xPos - radiusX, yPos - radiusY,
                radiusX, radiusY,
                width, height,
                xScale, yScale,
                rotation + angleMod,
                true);
        spriteBatch.setColor(Color.WHITE);
    }

    public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getxPos() {
        return xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getxScale() {
        return xScale;
    }

    public void setxScale(float xScale) {
        this.xScale = xScale;
    }

    public float getyScale() {
        return yScale;
    }

    public void setyScale(float yScale) {
        this.yScale = yScale;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void flip(boolean flip) {
        tweenTextureRegion.flip(flip, false);
    }

    public boolean isFlipped() {
        return tweenTextureRegion.isFlipX();
    }

    public void updateSize(float width, float height) {
        this.width = width;
        this.height = height;
        this.radiusX = this.width / 2;
        this.radiusY = this.height / 2;
    }
}
