package no.dkit.android.ludum.core.shaders;

import com.badlogic.gdx.graphics.Texture;

public abstract class AbstractDualTextureShader extends AbstractTextureShader {
    Texture texture2;

    public void init(int width, int height, Texture texture, Texture texture2) {
        super.init(width, height, texture);
        this.texture2 = texture2;
    }

    public void init(int width, int height, Texture texture, Texture texture2, boolean transparent) {
        super.init(width, height, texture, transparent);
        this.texture2 = texture2;
    }

    @Override
    protected void performTextureBindings() {
        shader.setUniformi("u_texture1", 1);
        texture2.bind(1);
        texture.bind(0);
    }

    @Override
    protected void cleanupTextures() {
        texture.dispose();
        texture2.dispose();
    }

    public void bind(Texture texture, Texture texture2) {
        this.texture = texture;
        this.texture2 = texture2;
        performTextureBindings();
    }
}
