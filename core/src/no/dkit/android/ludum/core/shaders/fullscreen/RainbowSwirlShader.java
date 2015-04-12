package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class RainbowSwirlShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform vec2 resolution;\n" +
                "uniform float time;\n" +
                "\n" +
                "const float Pi = 3.14159;\n" +
                "\n" +
                "float sinApprox(float x) {\n" +
                "    x = Pi + (2.0 * Pi) * floor(x / (2.0 * Pi)) - x;\n" +
                "    return (4.0 / Pi) * x - (4.0 / Pi / Pi) * x * abs(x);\n" +
                "}\n" +
                "\n" +
                "float cosApprox(float x) {\n" +
                "    return sinApprox(x + 0.5 * Pi);\n" +
                "}\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "\tvec2 p=(2.0*gl_FragCoord.xy-resolution)/max(resolution.x,resolution.y);\n" +
                "\tfor(int i=1;i<50;i++)\n" +
                "\t{\n" +
                "\t\tvec2 newp=p;\n" +
                "\t\tnewp.x+=0.6/float(i)*sin(float(i)*p.y+time/40.0+0.3*float(i))+1.0;\n" +
                "\t\tnewp.y+=0.6/float(i)*sin(float(i)*p.x+time/40.0+0.3*float(i+10))-1.4;\n" +
                "\t\tp=newp;\n" +
                "\t}\n" +
                "\tvec3 col=vec3(0.5*sin(3.0*p.x)+0.5,0.5*sin(3.0*p.y)+0.5,sin(p.x+p.y));\n" +
                "\tgl_FragColor=vec4(col, 1.0);\n" +
                "}\n";
    }
}
