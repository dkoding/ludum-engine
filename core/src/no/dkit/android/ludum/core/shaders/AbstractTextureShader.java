package no.dkit.android.ludum.core.shaders;

import com.badlogic.gdx.graphics.Texture;

public abstract class AbstractTextureShader extends AbstractShader {
    Texture texture;

    public void init(int width, int height, Texture texture) {
        super.init(width, height, false);
        this.texture = texture;
    }

    public void init(int width, int height, Texture texture, boolean transparent) {
        super.init(width, height, transparent);
        this.texture = texture;
    }

    @Override
    protected void performTextureBindings() {
        if (texture != null)
            texture.bind();
    }

    @Override
    protected void cleanupTextures() {
        if (texture != null)
            texture.dispose();
    }

    public void bind(Texture texture) {
        this.texture = texture;
        performTextureBindings();
    }
}
