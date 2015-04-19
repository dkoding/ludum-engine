package no.dkit.android.ludum.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import no.dkit.android.ludum.core.game.Config;
import no.dkit.android.ludum.core.game.controller.GameScreen;
import no.dkit.android.ludum.core.game.controller.HelpScreen;
import no.dkit.android.ludum.core.game.controller.LoadingScreen;
import no.dkit.android.ludum.core.game.controller.MenuScreen;
import no.dkit.android.ludum.core.game.controller.SplashScreen;
import no.dkit.android.ludum.core.game.controller.UpgradeScreen;
import no.dkit.android.ludum.core.game.factory.BodyFactory;
import no.dkit.android.ludum.core.game.factory.EffectFactory;
import no.dkit.android.ludum.core.game.factory.LightFactory;
import no.dkit.android.ludum.core.game.factory.LootFactory;
import no.dkit.android.ludum.core.game.factory.MapFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.factory.ShaderFactory;
import no.dkit.android.ludum.core.game.factory.SoundFactory;
import no.dkit.android.ludum.core.game.factory.TextFactory;
import no.dkit.android.ludum.core.game.model.PlayerData;
import no.dkit.android.ludum.core.game.model.loot.Weapon;
import no.dkit.android.ludum.core.game.transition.DoneCallback;
import no.dkit.android.ludum.core.game.transition.TransitionScreen;

public class XXXX extends ApplicationAdapter {
    private static boolean isChanging = false;
    private static Game game;
    private static Screen existingGameScreen;
    public static PlayerData playerData;

    public static Config.PERFORMANCE performance;

    public XXXX(Config.PERFORMANCE performance) {
        this.performance = performance;
    }

    public static Game getGame() {
        return game;
    }

    public static void updateGameScreen() {
        if (game.getScreen() instanceof GameScreen) // Safeguard
            ((GameScreen) game.getScreen()).update();
    }

    public static void updateWeaponButton(Weapon weapon) {
        if (game.getScreen() instanceof GameScreen) // Safeguard
            ((GameScreen) game.getScreen()).updateWeaponButton(weapon);
    }

    public static void setScreen(Screen screen) {
        game.setScreen(screen);
        isChanging = false;
    }

    public enum SCREEN {SPLASH, STARTMENU, GAME, WIN_LEVEL, UPGRADE, AFTER_UPGRADE, WIN_GAME, HELP}

    public enum AIM_MODE {DIRECTION, FOCUSED}

    public static AIM_MODE aimMode = AIM_MODE.DIRECTION;

    public void create() {
        if (game == null) {
            game = new Game() {
                @Override
                public void create() {

                }
            };
            game.create();
        }
        game.setScreen(new SplashScreen());
    }

    @Override
    /**
     * The Main Loop
     */
    public void render() {
        if (game != null && game.getScreen() != null)
            game.getScreen().render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() { // TODO - call dispose statically on Factories to avoid error when disposing factory not created yet
        ResourceFactory.getInstance().dispose();
        TextFactory.getInstance().dispose();
        EffectFactory.getInstance().dispose();
        ShaderFactory.getInstance().dispose();
        BodyFactory.getInstance().dispose();
        LightFactory.getInstance().dispose();
        LootFactory.getInstance().dispose();
        MapFactory.getInstance().dispose();
        SoundFactory.getInstance().dispose();
        game.dispose();
    }

    public static void changeScreen(SCREEN to) {
        if (isChanging) return;

        isChanging = true;

        final Screen oldScreen = game.getScreen();
        Screen newScreen = null;

        if (to.equals(SCREEN.STARTMENU)) {
            playerData = new PlayerData();
            newScreen = new MenuScreen();
        } else if (to.equals(SCREEN.HELP)) {
            newScreen = new HelpScreen();
        } else if (to.equals(SCREEN.UPGRADE)) {
            newScreen = new UpgradeScreen();
            existingGameScreen = oldScreen;
        } else if (to.equals(SCREEN.AFTER_UPGRADE)) {
            newScreen = existingGameScreen;
        } else if (to.equals(SCREEN.STARTMENU)) {
            newScreen = new MenuScreen();
        }

        game.setScreen(new TransitionScreen(game, oldScreen, newScreen, new DoneCallback() {
            public void done(Screen oldScreen, Screen newScreen) {
                isChanging = false;
                XXXX.updateGameScreen();
            }
        }));
    }

    public static void changeLevel() {
        if (isChanging) return;

        isChanging = true;

        game.setScreen(new TransitionScreen(game, game.getScreen(), new LoadingScreen(), new DoneCallback() {
            @Override
            public void done(Screen oldScreen, Screen newScreen) {
                isChanging = false;
                XXXX.updateGameScreen();
            }
        }));
    }
}
