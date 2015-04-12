package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class WaveShader extends AbstractTextureShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "float offsetX = 0.0;\n" +
                "float fixedBasePosY = 0.0;\n" +
                "float speed = 3.0;\n" +
                "float verticleDensity = 6.0;\n" +
                "float swayIntensity = 0.2;\n" +
                "void main(void) {\n" +
                "vec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                "offsetX = sin(uv.y * verticleDensity + time * speed) * swayIntensity;\n" +
                "uv.x += offsetX * (uv.y - fixedBasePosY);\n" +
                "gl_FragColor = texture2D(u_texture, uv);\n" +
                "}";
    }
}
