package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class LightSwirlShader extends AbstractShader {


    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "float distFactor = 250.0;\n" +
                "float uStarSize = 9.0;\n" +
                "const int uNumStars = 100;\n" +
                "vec4 domColor = vec4(0.8,0.0,1.0,1.0);\n" +
                "\n" +
                "float rand(vec2 co){\n" +
                "  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "vec4 getColor(vec2 seed)\n" +
                "{\n" +
                "   vec4 color;\n" +
                "   \n" +
                "   color.r = domColor.r * rand(seed*134.23);\n" +
                "   color.g = domColor.g * rand(seed*235.43);\n" +
                "   color.b = domColor.b * rand(seed*-32.89);\n" +
                "   \n" +
                "   return color;\n" +
                "}\n" +
                "\n" +
                "float makeStar(float dist, float speed, float size)\n" +
                "{\n" +
                "   vec2 currPos = resolution.xy * 0.5;\n" +
                "   currPos.x += dist * sin(time*speed);\n" +
                "   currPos.y += dist * cos(time*speed);\n" +
                "   return size / (1.0+length(currPos - gl_FragCoord.xy)); \n" +
                "}\n" +
                "\n" +
                "void main(void)\n" +
                "{\n" +
                "   vec4 final = vec4(0);\n" +
                "   \n" +
                "   vec2 seed = vec2(3.44,9.12);\n" +
                "   \n" +
                "   for(int i = 0; i < uNumStars; i++) {\n" +
                "      vec4 color = getColor(seed);\n" +
                "      float dist = rand(seed) * distFactor;\n" +
                "      float speed = rand(seed*3.99);\n" +
                "      float size = rand(seed*225.22) * uStarSize;\n" +
                "      final += color * makeStar(dist,speed,size);\n" +
                "           \n" +
                "      seed.x += 0.54;\n" +
                "      seed.y += 0.24;\n" +
                "   }\n" +
                "   \n" +
                "   final.a = 1.0;\n" +
                "   gl_FragColor = final;\n" +
                "}";
    }
}
