package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class LightSwirlShader extends AbstractShader {


    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "\n" +
                "float uDistanceFactor = 250.0;\n" +
                "float uStarSize = 9.0;\n" +
                "const int uNumStars = 100;\n" +
                "vec4 uDominantColor = vec4(0.8,0.5,0.4,1.0);\n" +
                "\n" +
                "float rand(vec2 co){\n" +
                "  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "vec4 getColor(vec2 seed)\n" +
                "{\n" +
                "   vec4 color;\n" +
                "   \n" +
                "   color.r = uDominantColor.r * rand(seed*134.23);\n" +
                "   color.g = uDominantColor.g * rand(seed*235.43);\n" +
                "   color.b = uDominantColor.b * rand(seed*-32.89);\n" +
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
                "   vec2 derpseed = vec2(3.44,9.12);\n" +
                "   \n" +
                "   for(int i = 0; i < uNumStars; i++) {\n" +
                "      vec4 color = getColor(derpseed);\n" +
                "      float dist = rand(derpseed) * uDistanceFactor;\n" +
                "      float speed = rand(derpseed*3.99);\n" +
                "      float size = rand(derpseed*225.22) * uStarSize;\n" +
                "      final += color * makeStar(dist,speed,size);\n" +
                "           \n" +
                "      derpseed.x += 0.54;\n" +
                "      derpseed.y += 0.24;\n" +
                "   }\n" +
                "   \n" +
                "   final.a = 1.0;\n" +
                "   gl_FragColor = final;\n" +
                "}";
    }
}
