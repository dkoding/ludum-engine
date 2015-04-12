package no.dkit.android.ludum.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(1280, 720);
    }

    @Override
    public ApplicationListener getApplicationListener() {
        Config.initialize(1280, 720);
        return new XXXX(Config.PERFORMANCE.LOW);
    }
}