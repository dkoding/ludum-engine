package no.dkit.android.ludum.core.shaders.texture;

import com.badlogic.gdx.graphics.Texture;
import no.dkit.android.ludum.core.shaders.AbstractDualTextureShader;

public class WaterSinusWaveShader extends AbstractDualTextureShader {
    @Override
    public String getFragmentShader() {
        return "uniform sampler2D u_texture;\n"
                + "uniform sampler2D u_texture1;\n"
                + "uniform vec2 resolution;\n"
                + "uniform float time;\n"
                + "#define PI 3.14159265\n"
                + "void main()     \n"
                + "{               \n"
                + "  float angle = time * (2. * PI);\n"
                + "  vec2 uv = gl_FragCoord.xy / resolution.xy;\n"
                + "  vec2 displacement = texture2D(u_texture1, uv/6.0).xy;\n" //
                + "  float t=uv.y +displacement.y*0.1-0.15+  (sin(uv.x * 30.0+angle) * 0.015); \n" //
                + "  gl_FragColor = texture2D(u_texture, vec2(uv.x,t));\n"
                + "}";
    }

    @Override
    public void init(int width, int height, Texture texture, Texture texture2) {
        super.init(width, height, texture, texture2, true);
        wrapped = false;
    }
}
