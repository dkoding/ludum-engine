package no.dkit.android.ludum.core.game.model.loot;

import com.badlogic.gdx.graphics.Color;
import no.dkit.android.ludum.core.game.factory.LaserFactory;
import no.dkit.android.ludum.core.game.model.body.agent.PlayerBody;

public class Laser extends Weapon {
    public Laser() {
        super(
                LOOT_TYPE.LASER,
                "laser"
        );
    }

    public void fire1() {
        Color color = getColor();
        LaserFactory.createLaserBeam(origin, firingAngle, color, owner instanceof PlayerBody);

        for (int i = 1; i < emitters; i++) {
            LaserFactory.createLaserBeam(origin, firingAngle + sideAngle * i, color, owner instanceof PlayerBody);
            LaserFactory.createLaserBeam(origin, firingAngle - sideAngle * i, color, owner instanceof PlayerBody);
        }
    }

    protected Color getColor() {
        Color color;

        switch (strength) {
            case 1:
                color = Color.RED;
                break;
            case 2:
                color = Color.CYAN;
                break;
            case 3:
                color = Color.MAGENTA;
                break;
            case 4:
                color = Color.GREEN;
                break;
            case 5:
                color = Color.BLUE;
                break;
            case 6:
                color = Color.valueOf("4B0082");
                break;
            case 7:
                color = Color.valueOf("8F00FF");
                break;
            default:
                color = Color.BLACK;
        }
        return color;
    }

    @Override
    public void upgrade() {
        super.upgrade();
        emitters = level;
        strength = level;
        sideAngle = level * 5;
    }
}
