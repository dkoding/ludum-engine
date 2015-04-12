package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class BeaconShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "#define pi 3.1415926536\n" +
                "#define N 12\n" +
                "void main( void ) {\n" +
                "\tvec2 position = ( gl_FragCoord.xy / resolution.xy );\n" +
                "\tvec2 center=position*2.-1.;\n" +
                "\tcenter.x*=resolution.x/resolution.y;\n" +
                "\tfloat c=0.;\n" +
                "\tfloat r=0.3;\n" +
                "\tfloat o;\n" +
                "\tfor(int i=0;i<N;i++)\n" +
                "\t{\n" +
                "\t\tvec2 xy;\n" +
                "\t\to=float(i)/float(N)*2.*pi;\n" +
                "\t\txy.x=r*cos(o);\n" +
                "\t\txy.y=r*sin(o);\n" +
                "\t\txy+=center;\n" +
                "\t\tc+=pow(300000.,(1.-length(xy)*1.9)*(0.99+0.3*fract(float(-i)/float(N)-time*0.5)))/300000.0;\n" +
                "\t}\n" +
                "\tgl_FragColor = vec4( sqrt(c*vec3(.6,.08,.03)),1.0 );\n" +
                "}";
    }
}
