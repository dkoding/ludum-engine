package no.dkit.android.ludum.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.Config;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(900, 600);
    }

    @Override
    public ApplicationListener getApplicationListener() {
        Config.initialize(900, 600);
        return new XXXX(Config.PERFORMANCE.LOW);
    }
}