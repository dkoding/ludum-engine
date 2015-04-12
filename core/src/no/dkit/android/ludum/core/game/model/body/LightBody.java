package no.dkit.android.ludum.core.game.model.body;

import box2dLight.Light;
import com.badlogic.gdx.physics.box2d.Body;

public class LightBody extends PoolableGameBody {
    int numRepetitions = 3;

    public LightBody(Body body, float radius, Light light, float lightMod, int numRepetitions) {
        super(body, radius);
        addLight(light);
        this.lightMod = lightMod;
        this.numRepetitions = numRepetitions;
        bodyType = BODY_TYPE.METAL;
    }

    public void update() {
        if (light != null && lightMod != 0) {
            lightCounter += lightMod;

            if (lightCounter + lightMod >= 1 || lightCounter + lightMod <= 0) {
                lightMod = -lightMod;
                numRepetitions--;
            }

            light.setColor(1, 1, 1, lightCounter);
            light.setDistance(lightCounter * radius * 2);
        }

        if (numRepetitions == 0)
            delete();
    }
}
