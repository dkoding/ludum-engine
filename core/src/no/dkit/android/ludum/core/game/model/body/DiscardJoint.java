package no.dkit.android.ludum.core.game.model.body;

import com.badlogic.gdx.physics.box2d.Joint;

public class DiscardJoint extends GameBody {
    Joint joint;

    public DiscardJoint(Joint joint) {
        super();
        this.joint = joint;
    }
}
