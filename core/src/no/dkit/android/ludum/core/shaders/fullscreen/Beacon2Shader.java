package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class Beacon2Shader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "#define pi 3.1415926536\n" +
                "#define N 100\n" +
                "void main( void ) {\n" +
                "\n" +
                "\tvec2 position = ( gl_FragCoord.xy / resolution.xy );\n" +
                "\tvec2 center=position*2.-1.;\n" +
                "\tcenter.x*=resolution.x/resolution.y;\n" +
                "\tfloat c=0.;\n" +
                "\tfloat r=0.5;\n" +
                "\tfloat o;\n" +
                "\tfor(int i=0;i<N;i++)\n" +
                "\t{\n" +
                "\t\tvec2 xy;\n" +
                "\t\to=float(i)/float(N)*2.*pi;\n" +
                "\t\txy.x=r*cos(o);\n" +
                "\t\txy.y=r*sin(o);\n" +
                "\t\txy+=center;\n" +
                "\t\tc+=pow(300000.,(1.-length(xy)*7.)*(1.+0.1*fract(float(-i)/float(N)-time*1.)))/30000.0;\n" +
                "\t}\n" +
                "\tgl_FragColor = vec4( c*vec3(0.4,.2,.1),1.0 );\n" +
                "\n" +
                "}";
    }
}
