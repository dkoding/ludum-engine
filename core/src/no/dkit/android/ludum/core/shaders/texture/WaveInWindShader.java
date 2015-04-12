package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class WaveInWindShader extends AbstractTextureShader {



    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "const float speed = 2.0;\n" +
                "const float bendFactor = 0.2;\n" +
                "void main()\n" +
                "{\n" +
                "vec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                "  float height = 1.0 - uv.y;\n" +
                "  float offset = pow(height, 2.5);\n" +
                "  offset *= (sin(time * speed) * bendFactor);\n" +
                "  gl_FragColor = texture2D(u_texture, fract(vec2(uv.x + offset, uv.y)));" +
                "}";
    }
}
