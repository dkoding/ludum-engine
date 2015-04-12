package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class WaterRippleRefractShader extends AbstractTextureShader {


    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "\n" +
                "#ifdef GL_ES\n" +
                "precision highp float;\n" +
                "#endif\n" +
                "\n" +
                "const float PI = 3.1415926535897932;\n" +
                "\n" +
                "//speed\n" +
                "const float speed = 0.2;\n" +
                "const float speed_x = 0.3;\n" +
                "const float speed_y = 0.3;\n" +
                "\n" +
                "// refraction\n" +
                "const float emboss = 0.50;\n" +
                "const float intensity = 2.4;\n" +
                "const int steps = 8;\n" +
                "const float frequency = 6.0;\n" +
                "const int angle = 7; // better when a prime\n" +
                "\n" +
                "// reflection\n" +
                "const float delta = 60.;\n" +
                "const float intence = 700.;\n" +
                "\n" +
                "const float reflectionCutOff = 0.012;\n" +
                "const float reflectionIntence = 200000.;\n" +
                "\n" +
                "  float col(vec2 coord)\n" +
                "  {\n" +
                "    float delta_theta = 2.0 * PI / float(angle);\n" +
                "    float col = 0.0;\n" +
                "    float theta = 0.0;\n" +
                "    for (int i = 0; i < steps; i++)\n" +
                "    {\n" +
                "      vec2 adjc = coord;\n" +
                "      theta = delta_theta*float(i);\n" +
                "      adjc.x += cos(theta)*time*speed + time * speed_x;\n" +
                "      adjc.y -= sin(theta)*time*speed - time * speed_y;\n" +
                "      col = col + cos( (adjc.x*cos(theta) - adjc.y*sin(theta))*frequency)*intensity;\n" +
                "    }\n" +
                "\n" +
                "    return cos(col);\n" +
                "  }\n" +
                "\n" +
                "//---------- main\n" +
                "\n" +
                "void main(void)\n" +
                "{\n" +
                "vec2 p = (gl_FragCoord.xy) / resolution.xy, c1 = p, c2 = p;\n" +
                "float cc1 = col(c1);\n" +
                "\n" +
                "c2.x += resolution.x/delta;\n" +
                "float dx = emboss*(cc1-col(c2))/delta;\n" +
                "\n" +
                "c2.x = p.x;\n" +
                "c2.y += resolution.y/delta;\n" +
                "float dy = emboss*(cc1-col(c2))/delta;\n" +
                "\n" +
                "c1.x += dx*2.;\n" +
                "c1.y = -(c1.y+dy*2.);\n" +
                "\n" +
                "float alpha = 1.+dot(dx,dy)*intence;\n" +
                "\t\n" +
                "float ddx = dx - reflectionCutOff;\n" +
                "float ddy = dy - reflectionCutOff;\n" +
                "if (ddx > 0. && ddy > 0.)\n" +
                "\talpha = pow(alpha, ddx*ddy*reflectionIntence);\n" +
                "\t\n" +
                "vec4 col = texture2D(u_texture,c1)*(alpha);\n" +
                "gl_FragColor = col;\n" +
                "}\n";
    }
}
