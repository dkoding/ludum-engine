package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class NeonWaveShader3 extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "float funk(float p)\n" +
                "{\n" +
                "float res=fract(p)*2.-1.;\t\n" +
                "res=cos(time*2.-p*10.*3.)*min(res,1.-2.*res);\n" +
                "return res;\n" +
                "}\n" +
                "\n" +
                "void main( void ) \n" +
                "{\n" +
                "\tvec2 p = ( gl_FragCoord.xy / resolution.xy )-vec2(0.1,0.5);\n" +
                "\tfloat c = 0.0;\n" +
                "\tfloat f=funk(p.x*2.+time*0.25);\n" +
                "\tfloat d=(p.y*4.-f)*10.;\n" +
                "\tc=.25/abs(pow(abs(d),.5));\n" +
                "\tc=clamp(c,0.,1.);\n" +
                "\tgl_FragColor = vec4( vec3( c*c+c, c*0.30, 23.*c*p.x ), 1.0 );\n" +
                "}";
    }
}
