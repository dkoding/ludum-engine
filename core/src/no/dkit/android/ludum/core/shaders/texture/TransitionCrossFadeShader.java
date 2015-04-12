package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractDualTextureShader;

public class TransitionCrossFadeShader extends AbstractDualTextureShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform sampler2D u_texture1;\n" +
                "const float TIME_TRAN = 2.5;\t// Transition time\n" +
                "const float TIME_INTR = 4.0;\t// Intermission between in/out\n" +
                "const float TIME_PADN = 2.0;\t// Padding time at the end of out.\n" +
                "// ===================================================================\n" +
                "#define TRAN0 texture2D(u_texture, uv).xyz\n" +
                "#define TRAN1 texture2D(u_texture1, uv).xyz\n" +
                "void main(void)\n" +
                "{\n" +
                "\tconst float TIME_TOTAL = (2.0 * TIME_TRAN) + TIME_INTR + TIME_PADN;\n" +
                "\tconst float TIME_OUT   = TIME_TRAN + TIME_INTR;\n" +
                "\t\n" +
                "\tvec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                "\t\n" +
                "\tfloat t = mod(time, TIME_TOTAL);\n" +
                "\tfloat r = (t < TIME_OUT ? t : TIME_TOTAL - TIME_PADN - t);\n" +
                "\t\n" +
                "\tvec3 clf = mix(TRAN0, TRAN1, smoothstep(0.0, TIME_TRAN, r));\n" +
                "\t\n" +
                "\tgl_FragColor = vec4(clf,1.0);\n" +
                "}";
    }
}
