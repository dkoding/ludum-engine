package no.dkit.android.ludum.core.game.model.body.agent;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.game.factory.LootFactory;
import no.dkit.android.ludum.core.game.model.PlayerData;
import no.dkit.android.ludum.core.game.model.loot.Loot;

public class PlayerVehicleBody extends PlayerBody {
    TextureRegion turretImage;

    public PlayerVehicleBody(Body body, float radius, PlayerData data, TextureRegion vehicleImage, TextureRegion turretImage) {
        super(body, radius, data, vehicleImage, CONTROL_MODE.VEHICLE, BODY_TYPE.METAL);
        this.turretImage = turretImage;
        addWeapon(LootFactory.getInstance().getWeapon(Loot.LOOT_TYPE.TONGUE));
    }


    @Override
    public void draw(SpriteBatch spriteBatch) {
        super.draw(spriteBatch);
        spriteBatch.draw(turretImage,
                position.x - radius, position.y - radius,
                radius, radius,
                radius * 2, radius * 2,
                scale, scale,
                firingAngle,
                true);
    }
}
