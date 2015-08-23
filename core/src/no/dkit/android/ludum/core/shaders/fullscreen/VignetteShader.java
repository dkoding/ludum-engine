package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class VignetteShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform float alpha;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "void main( void ) {\n" +
                "\tvec3 color;\n" +
                "\t\n" +
                "\n" +
                "\tvec2 pos = gl_FragCoord.xy / resolution;\n" +
                "\t\n" +
                "\tcolor = vec3(.3-cos((pos.x-0.5) * 3.14159) * sin(pos.y * 3.14159), 0.0, 0.0);\n" +
                "\t\n" +
                "\tgl_FragColor = vec4(color, alpha);\n" +
                "\n" +
                "}";
    }

    public void setAlpha(float alpha) {
        shader.begin();
        shader.setUniformf("alpha", alpha);
        shader.end();
    }
}
