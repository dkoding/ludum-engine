package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class NeonCrossShader extends AbstractShader {


    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 mouse;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "float makePoint(float x,float y,float fx,float fy,float sx,float sy,float t){\n" +
                "   float xx=x+sin(t*fx)*sx;\n" +
                "   float yy=y+cos(t*fy)*sy;\n" +
                "   return 1.0/sqrt(xx*xx*yy*yy)/(100.);\n" +
                "}\n" +
                "\n" +
                "void main( void ) {\n" +
                "\n" +
                "   vec2 p=(gl_FragCoord.xy/resolution.x)*2.0-vec2(1.0,resolution.y/resolution.x);\n" +
                "\n" +
                "   p=p*2.0;\n" +
                "   \n" +
                "   float x=p.x;\n" +
                "   float y=p.y;\n" +
                "\n" +
                "   float a=makePoint(x,y,2.0,2.5,0.9,0.3,time);\n" +
                "   float b=makePoint(x,y,1.0,1.5,0.6,0.6,time);\n" +
                "   float c=makePoint(x,y,3.0,3.5,0.3,0.9,time);\n" +
                "\t\n" +
                "   float u=dot(vec3(a,b,c),vec3(a,b,c));\n" +
                "\t\n" +
                "   vec3 d=vec3(a,b,c)*u;\n" +
                "   \n" +
                "   gl_FragColor = vec4(d,0.5);\n" +
                "}";
    }
}
