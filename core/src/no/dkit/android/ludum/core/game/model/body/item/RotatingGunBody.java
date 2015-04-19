package no.dkit.android.ludum.core.game.model.body.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.loot.Weapon;

public class RotatingGunBody extends GameBody {
    Weapon weapon;
    public Vector2 offset = new Vector2();
    public Vector2 source = new Vector2();
    Vector2 target = new Vector2();
    boolean alternate = false;

    public RotatingGunBody(Body body, float radius, TextureAtlas.AtlasRegion image, Weapon weapon, float rotationMod, boolean alternateFire) {
        super(body, radius, image);
        lightMod = .1f;
        bodyType = BODY_TYPE.METAL;
        this.weapon = weapon;
        weapon.onPickup(this);
        this.rotationMod = MathUtils.randomBoolean() ? rotationMod : -rotationMod;
        this.rotation = MathUtils.random(360);
        this.alternate = alternateFire;
    }

    @Override
    public void update() {
        super.update();
        rotation += rotationMod;

        body.setTransform(position.x, position.y, MathUtils.degRad * rotation);

        if (!weapon.specialFire(this, GameModel.getPlayer()) && MathUtils.random() < .1f) {
            offset.set(Config.TILE_SIZE_X / 2f, 0).rotate(body.getAngle() * MathUtils.radiansToDegrees);
            source.set(position).add(offset);
            target.set(source).add(offset);
            if (alternate)
                weapon.fire2(source, target);
            else
                weapon.fire1(source, target);
        }

        weapon.update(this);
    }
}
