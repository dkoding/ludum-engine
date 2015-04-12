package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class ArcadeTunnelShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "void main( void ) {\n" +
                "\tvec2 pos1=gl_FragCoord.xy/resolution.x;\n" +
                "\tpos1=pos1-.5*vec2(.7,.7)*vec2(resolution.x/resolution.y,1);\n" +
                "\tfloat l1=length(pos1);\n" +
                "\tfloat l2=step(0.1,fract(1.0/l1+time/1.0));\n" +
                "\tfloat a=step(0.1,fract(atan(pos1.x,pos1.y)*5.0));\n" +
                "\tif(a!=l2){\n" +
                "\t\tgl_FragColor=vec4(l2,l1,l2,1);\n" +
                "\t}\n" +
                "}";
    }
}
