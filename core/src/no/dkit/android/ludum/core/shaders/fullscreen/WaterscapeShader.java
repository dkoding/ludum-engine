package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class WaterscapeShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "float f(float arg) {\n" +
                "\treturn 0.5+sin(arg*10.0+time*1.5)*0.05+sin(arg*15.0+time)*0.05+sin(arg*40.0-time)*0.01;\n" +
                "}\n" +
                "\n" +
                "void main( void ) {\n" +
                "\tvec2 position = ( gl_FragCoord.xy / resolution.xy ); position.x *= resolution.x/resolution.y; \n" +
                "\tvec3 color = (position.y>f(position.x))?vec3(0.4, 0.5, 0.9)*0.5/distance(position, vec2(1.5, 0.6)):vec3(0.1, 0.1, 0.7)-pow((f(position.x)-position.y), .3);\n" +
                "\tgl_FragColor = vec4(color, 1.0 );\n" +
                "}";
    }
}
