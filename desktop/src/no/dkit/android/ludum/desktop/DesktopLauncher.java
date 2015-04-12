package no.dkit.android.ludum.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Ludum-Engine";

        Config.initialize(1280, 720);

        cfg.width = Config.getDimensions().SCREEN_LONGEST;
        cfg.height = Config.getDimensions().SCREEN_SHORTEST;
        new LwjglApplication(new XXXX(Config.PERFORMANCE.HIGH), cfg);
    }
}
