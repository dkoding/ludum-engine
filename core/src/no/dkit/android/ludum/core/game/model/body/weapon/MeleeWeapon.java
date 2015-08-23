package no.dkit.android.ludum.core.game.model.body.weapon;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Expo;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.model.GameModel;
import no.dkit.android.ludum.core.game.model.body.GameBody;
import no.dkit.android.ludum.core.game.model.body.agent.MonsterPlayerBody;
import no.dkit.android.ludum.core.game.model.loot.Weapon;
import no.dkit.android.ludum.core.game.view.TweenBodyAccessor;

public class MeleeWeapon extends Weapon {

    private Body[] weaponBodies = new Body[2];
    private final Vector2 lootPos = new Vector2();
    private final Vector2 lootOffset = new Vector2();
    private int start;

    public MeleeWeapon() {
        super(
                LOOT_TYPE.MELEE,
                "melee"
        );

        cooldown1 = 1000;
        cooldown2 = 2000;

        this.weaponImage = ResourceFactory.getInstance().getItemImage("meleeweapon");
    }

    @Override
    public void fire1() {
        int rotation = 120;
        int start = (int)GameModel.getPlayer().getAngle()+ 90;

        Timeline.createSequence()
                .beginSequence()
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type == TweenCallback.COMPLETE) {
                            ((MonsterPlayerBody) owner).disableMelee();
                        } else if (type == TweenCallback.START) {
                            ((MonsterPlayerBody) owner).enableMelee();
                        }
                    }
                })
                .setCallbackTriggers(TweenCallback.COMPLETE | TweenCallback.START)
                .push(Tween.to(weaponBodies[0], TweenBodyAccessor.ROTATION_RAD, 1f).target((start + rotation) * MathUtils.degreesToRadians).ease(Bounce.OUT))
                .push(Tween.to(weaponBodies[0], TweenBodyAccessor.ROTATION_RAD, .7f).target(start * MathUtils.degreesToRadians).ease(Expo.IN))
                .end()
                .start(XXXX.getTweener());

        Timeline.createSequence()
                .beginSequence()
                .push(Tween.to(weaponBodies[1], TweenBodyAccessor.ROTATION_RAD, 1f).target(((start - rotation)) * MathUtils.degreesToRadians).ease(Bounce.OUT))
                .push(Tween.to(weaponBodies[1], TweenBodyAccessor.ROTATION_RAD, .7f).target(start * MathUtils.degreesToRadians).ease(Expo.IN))
                .end()
                .start(XXXX.getTweener());
    }

    @Override
    public void onPickup(GameBody owner) {
        super.onPickup(owner);

        if (owner instanceof MonsterPlayerBody) {
            weaponBodies = BodyFactory.getInstance().attachMeleeWeapons((MonsterPlayerBody) owner, Config.TILE_SIZE_X / 2);
        }
    }
}
