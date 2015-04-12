package no.dkit.android.ludum.core.game.model.body;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

public abstract class DirectionalGameBody extends GameBody {
    protected int direction;
    protected float xOffset;
    protected float yOffset;
    protected float startDirection;

    protected DirectionalGameBody(Body body, float radius, int direction) {
        super(body, radius);
        this.direction = direction;

        if (direction == AbstractMap.N) {
            yOffset -= radius;
            startDirection = MathUtils.PI / 2f;
        } else if (direction == AbstractMap.S) {
            yOffset += radius;
            startDirection = -MathUtils.PI / 2f;
        } else if (direction == AbstractMap.E) {
            xOffset += radius;
            startDirection = MathUtils.PI;
        } else if (direction == AbstractMap.W) {
            xOffset -= radius;
            startDirection = 0;
        } else if (direction == AbstractMap.SE) {
            yOffset -= radius;
            startDirection = MathUtils.PI / 2f;
        } else if (direction == AbstractMap.NW) {
            yOffset += radius;
            startDirection = -MathUtils.PI / 2f;
        } else if (direction == AbstractMap.NE) {
            xOffset += radius;
            startDirection = MathUtils.PI;
        } else if (direction == AbstractMap.SW) {
            xOffset -= radius;
            startDirection = 0;
        }

        position.set(position.x+xOffset, position.y+yOffset);
        body.setTransform(position.x, position.y, startDirection);
    }

    protected DirectionalGameBody(Body body, float radius, int direction, float offset, boolean inverseOriginX, boolean inverseOriginY) {
        super(body, radius);
        this.direction = direction;

        if (direction == AbstractMap.N) {
            yOffset -= inverseOriginY ? -offset : offset;
            startDirection = inverseOriginY ? -MathUtils.PI / 2f : MathUtils.PI / 2f;
        } else if (direction == AbstractMap.S) {
            yOffset += inverseOriginY ? -offset : offset;
            startDirection = inverseOriginY ? MathUtils.PI / 2f : -MathUtils.PI / 2f;
        } else if (direction == AbstractMap.W) {
            xOffset += inverseOriginX ? -offset : offset;
            startDirection = inverseOriginX ? 0 : MathUtils.PI;
        } else if (direction == AbstractMap.E) {
            xOffset -= inverseOriginX ? -offset : offset;
            startDirection = inverseOriginX ? MathUtils.PI : 0;
        } else if (direction == AbstractMap.SE) {
            yOffset -= inverseOriginY ? -offset : offset;
            startDirection = inverseOriginY ? -MathUtils.PI / 2f : MathUtils.PI / 2f;
        } else if (direction == AbstractMap.NW) {
            yOffset += inverseOriginY ? -offset : offset;
            startDirection = inverseOriginY ? MathUtils.PI / 2f : -MathUtils.PI / 2f;
        } else if (direction == AbstractMap.SW) {
            xOffset += inverseOriginX ? -offset : offset;
            startDirection = inverseOriginX ? 0 : MathUtils.PI;
        } else if (direction == AbstractMap.NE) {
            xOffset -= inverseOriginX ? -offset : offset;
            startDirection = inverseOriginX ? MathUtils.PI : 0;
        }

        position.set(position.x+xOffset, position.y+yOffset);
        body.setTransform(position.x, position.y, startDirection);
    }
}
