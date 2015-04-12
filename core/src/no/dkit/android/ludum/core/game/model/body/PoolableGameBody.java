package no.dkit.android.ludum.core.game.model.body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class PoolableGameBody extends GameBody {
    public boolean alive = false;

    protected PoolableGameBody() {
        super();
    }

    public PoolableGameBody(Body body, float radius) {
        super(body, radius);
/*
        init();
*/
    }

    public PoolableGameBody(Body body, float radius, TextureRegion image) {
        super(body, radius, image);
/*
        init();
*/
    }


/*
    @Override
    public void delete() {
        dispose();
        alive = false;
        active = false;
        setDisabled(true);
    }

    protected void init() {
        active = true;
        alive = true;
        setDisabled(false);
    }
*/
}
