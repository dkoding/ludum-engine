package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractDualTextureShader;

public class TransitionSwipeShader extends AbstractDualTextureShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform sampler2D u_texture1;\n" +
                "float remap(float value, float inputMin, float inputMax, float outputMin, float outputMax)\n" +
                "{\n" +
                "    return (value - inputMin) * ((outputMax - outputMin) / (inputMax - inputMin)) + outputMin;\n" +
                "}\n" +
                "void main(void)\n" +
                "{\n" +
                "\tvec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                "\tfloat transion = remap(sin(time * 1.0), -1.0, 1.0, -0.5, 1.2);\n" +
                "\tfloat waveTransion = transion + sin(uv.y * 20.0) * 0.1;\n" +
                "\tvec4 a = texture2D(u_texture, uv);\n" +
                "\tvec4 b = texture2D(u_texture1, uv);\n" +
                "\tfloat blend = 0.2;\n" +
                "\tfloat s = remap(clamp(uv.x - (waveTransion - blend * 0.5), 0.0, blend), 0.0, blend, 0.0, 1.0);\n" +
                "\tgl_FragColor = mix(a, b, smoothstep(0.0, 1.0, s));\n" +
                "}";
    }
}
