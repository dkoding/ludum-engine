package no.dkit.android.ludum.core.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import no.dkit.android.ludum.core.game.Config;

public class UniverseBackground extends MovingBackground {
    TextureRegion planetImage;
    TextureRegion starImage;

    private Array<Vector2> stars = new Array<Vector2>(Config.NUM_STARS);
    private float planetX;
    private float planetY;

    public UniverseBackground(Texture backLayer, Texture frontLayer, TextureRegion planetImage, TextureRegion starImage,
                              float viewWidth, float viewHeight, float worldwidth, float worldheight) {
        super(backLayer, frontLayer, viewWidth, viewHeight, worldwidth, worldheight);

        this.planetImage = planetImage;
        this.starImage = starImage;

        planetX = worldwidth / 2;
        planetY = worldheight / 2;

        createStars(viewWidth, viewHeight);
    }

    private void createStars(float width, float height) {
        for (int i = 0; i < Config.NUM_STARS; i++)
            stars.add(new Vector2(MathUtils.random(width), MathUtils.random(height)));
    }

    @Override
    public void update(float centerX, float centerY, float deltaX, float deltaY, float translateX, float translateY) {
        super.update(centerX, centerY, deltaX, deltaY, translateX, translateY);
        pan(centerX, centerY, deltaX, deltaY);
    }

    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        batch.begin();
        Gdx.gl20.glClearColor(0, 0, .5f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.enableBlending();
        fog.draw(batch);
        background.draw(batch);

        drawStars(batch);
        drawPlanet(batch, x, y);
        batch.end();
    }

    private void drawStars(SpriteBatch spriteBatch) {
        int counter = 0;
        float radius = 0;

        for (Vector2 star : stars) {
            counter++;
            radius = starImage.getRegionWidth() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR;

            if (counter < stars.size * .3f) {
                spriteBatch.setColor(Color.WHITE);

                spriteBatch.draw(starImage,
                        star.x, star.y,
                        radius / 2, radius / 2,
                        radius, radius,
                        .3f, .3f,
                        0, true);
            } else if (counter < stars.size * .6f) {
                spriteBatch.setColor(Color.YELLOW);

                spriteBatch.draw(starImage,
                        star.x, star.y,
                        radius / 2, radius / 2,
                        radius, radius,
                        .2f, .2f,
                        0, true);
            } else {
                spriteBatch.setColor(Color.ORANGE);

                spriteBatch.draw(starImage,
                        star.x, star.y,
                        radius / 2, radius / 2,
                        radius, radius,
                        .1f, .1f,
                        0, true);
            }
        }

        spriteBatch.setColor(Color.WHITE);
    }

    private void drawPlanet(SpriteBatch spriteBatch, float x, float y) {
        float radius = planetImage.getRegionWidth() * Config.getDimensions().WORLD_ON_SCREEN_FACTOR;

        spriteBatch.draw(planetImage,
                planetX - (radius / 2) + ((x - planetX) / worldwidth * 9),
                planetY - (radius / 2) + ((y - planetY) / worldheight * 9),
                radius / 2, radius / 2,
                radius, radius,
                1, 1,
                90, true);

    }

    private void pan(float centerX, float centerY, float deltaX, float deltaY) {
        int counter = 0;
        for (Vector2 star : stars) {
            counter++;

            if (counter % 3 == 0) {
                star.x += deltaX / 2;
                star.y += deltaY / 2;
            } else if (counter % 2 == 0) {
                star.x += deltaX / 3;
                star.y += deltaY / 3;
            } else {
                star.x += deltaX / 4;
                star.y += deltaY / 4;
            }

            if (star.x > centerX + (viewWidth / 2f)) star.x -= viewWidth;
            if (star.x < centerX - (viewWidth / 2f)) star.x += viewWidth;
            if (star.y > centerY + (viewHeight / 2f)) star.y -= viewHeight;
            if (star.y < centerY - (viewHeight / 2f)) star.y += viewHeight;
        }
    }
}
