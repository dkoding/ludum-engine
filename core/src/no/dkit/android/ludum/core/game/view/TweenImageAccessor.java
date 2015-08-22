package no.dkit.android.ludum.core.game.view;

import aurelienribon.tweenengine.TweenAccessor;

public class TweenImageAccessor implements TweenAccessor<TweenImage> {
    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int SCALE_X = 4;
    public static final int SCALE_Y = 5;
    public static final int SCALE_XY = 6;
    public static final int ROTATION = 7;
    public static final int OPACITY = 8;

    @Override
    public int getValues(TweenImage target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X:
                returnValues[0] = target.getxPos();
                return 1; // Number of values returned
            case POSITION_Y:
                returnValues[0] = target.getyPos();
                return 1; // Number of values returned
            case POSITION_XY:
                returnValues[0] = target.getxPos();
                returnValues[1] = target.getyPos();
                return 2; // Number of values returned
            case SCALE_X:
                returnValues[0] = target.getxScale();
                return 1; // Number of values returned
            case SCALE_Y:
                returnValues[0] = target.getyScale();
                return 1; // Number of values returned
            case SCALE_XY:
                returnValues[0] = target.getxScale();
                returnValues[1] = target.getyScale();
                return 2; // Number of values returned
            case OPACITY:
                returnValues[0] = target.getAlpha();
                return 1; // Number of values returned
            case ROTATION:
                returnValues[0] = target.getRotation();
                return 1; // Number of values returned
            default:
                return -1;
        }
    }

    @Override
    public void setValues(TweenImage target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X:
                target.setxPos(newValues[0]);
                break;
            case POSITION_Y:
                target.setyPos(newValues[0]);
                break;
            case POSITION_XY:
                target.setxPos(newValues[0]);
                target.setyPos(newValues[1]);
                break;
            case SCALE_X:
                target.setxScale(newValues[0]);
                break;
            case SCALE_Y:
                target.setyScale(newValues[0]);
                break;
            case SCALE_XY:
                target.setxScale(newValues[0]);
                target.setyScale(newValues[1]);
                break;
            case OPACITY:
                target.setAlpha(newValues[0]);
                break;
            case ROTATION:
                target.setRotation(newValues[0]);
                break;
            default:
        }
    }
}
