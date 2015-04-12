package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class SpiralShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "void main(void)\n" +
                "{\n" +
                "\tvec2 vv = gl_FragCoord.xy; \n" +
                "\tvv.x += sin( time * 1.0 ) * .1;\n" +
                "\tvv.y += cos( time * 1.3 ) * .1;\n" +
                "\t\n" +
                "\tvv -= resolution.xy / 2.0;\n" +
                "\tvec4 color = vec4( .0, .0, 1., 1.0 );\n" +
                "\tif ( sin( atan( vv.y, vv.x ) * 2.0 - time * 2.0 + length(vv) / 20.0 ) > 0.0 )\n" +
                "\t\tcolor = vec4( 1., .0, 0., 1.0 );\n" +
                "\tcolor.xyz *= length(vv) *2. / (length(resolution.xy));\n" +
                "\tgl_FragColor = color;\n" +
                "}";
    }
}
