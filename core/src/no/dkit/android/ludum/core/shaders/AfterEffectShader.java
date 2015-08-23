package no.dkit.android.ludum.core.shaders;

import com.badlogic.gdx.graphics.Texture;

public class AfterEffectShader extends AbstractTextureShader {
    Texture texture;

    public void bind(Texture texture) {
        this.texture = texture;
        this.texture.bind(0);
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public String getFragmentShader() {
        return "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" + //
                "varying LOWP vec4 vColor;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "void main()\n" +
                "{\n" +
                "    // get screen uvs\n" +
                "    vec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                "    \n" +
                "    // center\n" +
                "    uv -= 0.5;\n" +
                "    vec2 uv2 = uv;\n" +
                "    \n" +
                "    // correct aspect ratio\n" +
                "    float ar = resolution.x / resolution.y;\n" +
                "    uv.x *= ar;\n" +
                "    \n" +
                "    // measure distance from center\n" +
                "    float dd = dot(uv, uv);\n" +
                "    float dd2 = dot(uv2, uv2);\n" +
                "\n" +
                "    // warp\n" +
                "    uv = (uv * dd) * 0.4 + uv * 0.6;\n" +
                "    uv2 = (uv2 * dd2) * 0.4 + uv2 * 0.6;\n" +
                "\n" +
                "    //compute vignette\n" +
                "    float vignette = (1.0 - abs(uv2.x)) * (1.0 - abs(uv2.y)) / (1.0 + dd2);\n" +
                "    vignette *= vignette * 2.0;\n" +
                "    vignette *= max(0.0, 1.0 - 2.75 * max(abs(uv2.x), abs(uv2.y)));\n" +
                "    vignette = pow(vignette, 0.25);\n" +
                "\n" +
                "    // restore\n" +
                "    uv += 0.5;\n" +
                "    uv2 += 0.5;\n" +
                "\n" +
                "    // sample texture\n" +
                "    vec4 color = texture2D(u_texture, uv2);\n" +
                "    // debug checker with aspect ratio correction\n" +
                "    // float a = mod(uv.x * 20.0, 2.0);\n" +
                "    // float b = mod(uv.y * 20.0, 2.0);\n" +
                "    // float c = 0.3;\n" +
                "    // if(int(a) != int(b))\n" +
                "    //    c = 0.7;\n" +
                "\n" +
                "    // apply vertical scanlines \n" +
                "    float v = abs(sin(uv.x * 270.0 + time * 3.0));\n" +
                "    v += abs(sin(uv.x * 380.0 + time * 1.1));\n" +
                "    v *= abs(sin(uv.x * 300.0 + time * 1.8));\n" +
                "    v = mix(v, 0.5, 0.9) - 0.1;\n" +
                "    // overlay\n" +
                "    if(v > 0.5)\n" +
                "        color = 1.0 - (1.0 - 2.0 * (v - 0.5)) * (1.0 - color);\n" +
                "    else\n" +
                "        color = (2.0 * v) * color;\n" +
                "\n" +
                "    // apply vignette\n" +
                "    color *= vignette;\n" +
                "\n" +
                "    gl_FragColor = color;\n" +
                "}";
    }
}
