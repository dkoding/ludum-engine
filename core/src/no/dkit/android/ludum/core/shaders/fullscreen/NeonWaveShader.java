package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class NeonWaveShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "#define LINES 2.0\n" +
                "#define SUBLINES 3.0\n" +
                "#define BRIGHTNESS 0.04\n" +
                "\n" +
                "const vec3 ORANGE = vec3(0.4, 0.8, 0.4);\n" +
                "const vec3 BLUE = vec3(0.5, 0.9, 20.3);\n" +
                "const vec3 GREEN = vec3(1.0, 10.0, 1.0);\n" +
                "const vec3 RED = vec3(10.0, 0.4, 0.3);\n" +
                "\n" +
                "void main() {\n" +
                "    float x, y, xpos, ypos;\n" +
                "    float t = time*30.0;\n" +
                "    vec3 c = vec3(0.0);\n" +
                "    \n" +
                "    xpos = (gl_FragCoord.x / resolution.x/2.0);\n" +
                "    ypos = (gl_FragCoord.y / (resolution.y/1.5));\n" +
                "    \n" +
                "    x = xpos;\n" +
                "    for (float i = 0.0; i < LINES; i += 1.0) {\n" +
                "        for(float j = 0.0; j < SUBLINES; j += 1.0){\n" +
                "            y = ypos\n" +
                "            + (0.30 * sin(x * 5.000 +( i * 1.5 + j) * 0.2 + t * 0.040)\n" +
                "               + 0.300 * cos(x * 6.350 + (i  + j) * 0.2 + t * 0.050 * j)\n" +
                "               + 0.024 * sin(x * 12.35 + ( i + j * 4.0 ) * 0.8 + t * 0.034 * (8.0 *  j))\n" +
                "               + 0.5);\n" +
                "            \n" +
                "            c += vec3(1.0 - pow(clamp(abs(1.4 - y) * 5.0, 0.0,1.0), 0.25));\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    c *= mix(\n" +
                "             mix(RED, BLUE, ypos)\n" +
                "             , mix(GREEN, RED, xpos)\n" +
                "             ,(sin(t * 0.02) + 1.0) * 0.45\n" +
                "             ) * BRIGHTNESS * clamp(t, 2.0, 2.5);\n" +
                "    \n" +
                "    gl_FragColor = vec4(c, 1.0);\n" +
                "}";
    }
}
