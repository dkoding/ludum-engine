package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class NeonPulseShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "void main( void ) {\n" +
                "\tvec2 rminus;\n" +
                "\tfloat res_y;\n" +
                "\tif (resolution.x > resolution.y) {\n" +
                "\t\trminus = vec2(resolution.x / resolution.y, 1.0);\n" +
                "\t\tres_y = resolution.y;\n" +
                "\t} else {\n" +
                "\t\trminus = vec2(1.0, resolution.y / resolution.x);\n" +
                "\t\tres_y = resolution.x;\n" +
                "\t}\n" +
                "\n" +
                "\tvec2 position = gl_FragCoord.xy / res_y * 2.0 - rminus;\n" +
                "\t\n" +
                "\tvec3 col = vec3(0, 0, 0);\n" +
                "\t\n" +
                "\tfloat bt = time * 0.03;\n" +
                "\tfloat broken = 1.1 - sin(bt) * sin(bt) * 0.35;\n" +
                "\tfloat intensity = min(1.0, cos(bt) / 1.5 + 1.0);\n" +
                "\t\t\n" +
                "\tfor (int i = 0; i < 3; ++i) {\n" +
                "\t\tfloat mt = mod(time, 999.0) * 0.3 * (float(i) * 0.3);\n" +
                "\t\tfloat t = mod(time * 0.4, 1111.0) * (float(i) + 2.0);\n" +
                "\t\tfloat x2 = cos(t) * 0.7 + sin(mt) * 0.02;\n" +
                "\t\tfloat y2 = sin(t) * 0.7 + cos(mt) * 0.02;\n" +
                "\t\tfloat x = cos(t/0.3) * x2 + sin(t/0.3) * y2;\n" +
                "\t\t//float y = sin(t/0.3) * x2 + cos(t/0.3) * y2;\n" +
                "\t\tx = x2;\n" +
                "\t\tfloat y = y2;\n" +
                "\t\tvec2 d3 = position - vec2(x, y);\n" +
                "\t\t\n" +
                "\t\tfloat tt = t * (float(i) + 5.0) / 20.0 + d3.x * tan(t*1.5) + d3.y * tan(t*2.0);\n" +
                "\t\tvec2 d = vec2(d3.x * cos(tt) + d3.y * sin(tt), d3.x * sin(tt) - d3.y * cos(tt));\n" +
                "\t\t\n" +
                "\t\tfloat rx = broken - (abs(sin(t * 0.5)) / 2.0);\n" +
                "\t\tfloat ry = broken - (abs(cos(t * 0.5)) / 2.0);\n" +
                "\t\td.x *= rx;\n" +
                "\t\td.y *= ry;\n" +
                "\t\t\n" +
                "\t\tfloat d2 = dot(d, d);\n" +
                "\t\tfloat a = abs(d2 - 0.03);\n" +
                "\n" +
                "\t\tfloat u = mod(time*2.0, 1111.0) * (float(i) * 0.1 + 2.0);\n" +
                "\t\tcol.r += 0.005 * sin(u) * sin(u) / (a + 0.00001);\n" +
                "\t\tif (d2 < 0.03)\n" +
                "\t\t\tcol.r +=  0.3 * sin(u) * sin(u);\n" +
                "\n" +
                "\t\tu = mod(time*1.9, 1111.0) * (float(i) * 0.1 + 2.0);\n" +
                "\t\tcol.g += 0.005 * cos(u) * cos(u) / (a + 0.00001);\n" +
                "\t\tif (d2 < 0.03)\n" +
                "\t\t\tcol.g +=  0.3 * sin(u) * sin(u);\n" +
                "\n" +
                "\t\tu = mod(time*1.8, 1111.0) * (float(i) * 0.1 + 2.0);\n" +
                "\t\tcol.b += 0.005 * sin(u+0.787) * sin(u+0.787) / (a + 0.00001);\n" +
                "\t\tif (d2 < 0.03)\n" +
                "\t\t\tcol.b +=  0.3 * sin(u) * sin(u);\n" +
                "\t}\n" +
                "\tgl_FragColor = vec4(col * intensity, 1.0);\n" +
                "}";
    }
}
