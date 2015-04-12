package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class FireShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "float rand(vec2 n) { \n" +
                "\treturn fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "float noise(vec2 n) {\n" +
                "\tconst vec2 d = vec2(0.0, 1.0);\n" +
                "\tvec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));\n" +
                "\treturn mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);\n" +
                "}\n" +
                "\n" +
                "float fbm(vec2 n) {\n" +
                "\tfloat total = 0.0, amplitude = 1.0;\n" +
                "\tfor (int i = 0; i < 5; i++) {\n" +
                "\t\ttotal += noise(n) * amplitude;\n" +
                "\t\tn += n;\n" +
                "\t\tamplitude *= 0.5;\n" +
                "\t}\n" +
                "\treturn total;\n" +
                "}\n" +
                "\n" +
                "void main() {\n" +
                "\tconst vec3 c1 = vec3(0.1, 0.0, 0.0);\n" +
                "\tconst vec3 c2 = vec3(0.7, 0.0, 0.0);\n" +
                "\tconst vec3 c3 = vec3(0.2, 0.0, 0.0);\n" +
                "\tconst vec3 c4 = vec3(1.0, 0.9, 0.0);\n" +
                "\tconst vec3 c5 = vec3(0.1);\n" +
                "\tconst vec3 c6 = vec3(0.9);\n" +
                "\tvec2 p = gl_FragCoord.xy * 5.0 / resolution.xx;\n" +
                "\tfloat q = fbm(p - time * 0.1);\n" +
                "\tvec2 r = vec2(fbm(p + q + time * 0.7 - p.x - p.y), fbm(p + q - time * 0.4));\n" +
                "\tvec3 c = mix(c1, c2, fbm(p + r)) + mix(c3, c4, r.x) - mix(c5, c6, r.y);\n" +
                "\tgl_FragColor = vec4(c * cos(1.57 * gl_FragCoord.x / resolution.x), 1.0);\n" +
                "}\n";
    }
}
