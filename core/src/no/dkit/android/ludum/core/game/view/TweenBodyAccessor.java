package no.dkit.android.ludum.core.game.view;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.physics.box2d.Body;

public class TweenBodyAccessor implements TweenAccessor<Body> {
    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int ROTATION_RAD = 4;

    @Override
    public int getValues(Body target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X:
                returnValues[0] = target.getPosition().x;
                return 1; // Number of values returned
            case POSITION_Y:
                returnValues[0] = target.getPosition().y;
                return 1; // Number of values returned
            case POSITION_XY:
                returnValues[0] = target.getPosition().x;
                returnValues[1] = target.getPosition().y;
                return 2; // Number of values returned
            case ROTATION_RAD:
                returnValues[0] = target.getAngle();
                return 1; // Number of values returned
            default:
                return -1;
        }
    }

    @Override
    public void setValues(Body target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X:
                target.setTransform(target.getPosition().x + newValues[0], target.getPosition().y, target.getAngle());
                break;
            case POSITION_Y:
                target.setTransform(target.getPosition().x, target.getPosition().y + newValues[0], target.getAngle());
                break;
            case POSITION_XY:
                target.setTransform(target.getPosition().x + newValues[0], target.getPosition().y + newValues[1], target.getAngle());
                break;
            case ROTATION_RAD:
                target.setTransform(target.getPosition().x, target.getPosition().y, newValues[0]);
                break;
            default:
        }
    }
}
