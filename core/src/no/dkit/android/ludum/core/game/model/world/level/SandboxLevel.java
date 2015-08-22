package no.dkit.android.ludum.core.game.model.world.level;

import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.LootFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;
import no.dkit.android.ludum.core.game.model.world.map.SandboxMap;

import java.util.List;

public class SandboxLevel extends Level {

    public SandboxLevel() {
        super(Config.SANDBOX_TYPE, 1, false, false);
        this.map = new SandboxMap().createMap(level, inside, platforms);
        setStartPositionTo(map.getWidth() / 2f, map.getHeight() / 2f);

        getDefaultLootFor(worldType);
        getDefaultGravityFor(worldType);
        getDefaultBackgroundFor(worldType);
        getDefaultForegroundFor(worldType);
        getDefaultLightsFor(worldType);
        getDefaultImagesFor(worldType);
        getDefaultFeaturesFor(worldType);

        //map.item[(int)startPosition.x][(int)startPosition.y] = AbstractMap.ITEM_LIQUID_SS;

        enemyTypes.clear();
        enemyTypes.add(BodyFactory.ENEMY_TYPE.WALKER_SINGLE);
    }

    public void onStart() {
        final List<Loot.LOOT_TYPE> weaponTypes = getWeaponTypes();

        for (Loot.LOOT_TYPE weaponType : weaponTypes) {
            givePlayerWeapon(LootFactory.getInstance().getWeapon(weaponType));
        }
/*
        TriggerBody trigger = BodyFactory.getInstance().createTrigger(2, 2);
        trigger.setTrigger(new QuestTrigger(new KillBlobQuest(worldType)));
*/
    }

    private void givePlayerWeapon(Loot loot) {
        GameModel.getPlayer().addLoot(loot);
    }
}
