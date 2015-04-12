package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class NeonWaveShader2 extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "#define SPEED 0.5\n" +
                "#define PI 3.141592653589793\n" +
                "#define TAU (PI*2.0)\n" +
                "vec3 getColors( vec2 position ) {\n" +
                "\tvec3 res = vec3(cos(position.x*2.0*time ), sin(position.y), 1. - 0.5 * cos(time));\n" +
                "\treturn res;\n" +
                "}\n" +
                "float factorize(float speed,float phase,float minout) {\n" +
                "\tfloat back =sin(speed*time+phase)/2.+0.5;\n" +
                "\tback*=minout;\n" +
                "\tback+=1.-minout;\n" +
                "\treturn back;\n" +
                "}\n" +
                "vec3 imagesshader(vec2 pos) {\n" +
                "\tvec2 position = 2.0 * pos.xy / resolution.xy - 1.0;\n" +
                "\tposition.x *= resolution.x / resolution.y; // Correct aspect ratio.\n" +
                "\tfloat r = length(position);\n" +
                "\tfloat a = atan(position.y, position.x);\n" +
                "\tfloat factor = sin(time) /2. + 0.5;\n" +
                "\tfloat u = position.x,v = position.y;\t\n" +
                "\tu = position.x/abs(position.y);\n" +
                "\tv = 1./abs(position.y);\t\n" +
                "\tvec2 p = vec2(u, v);\n" +
                "\tp += vec2( SPEED , SPEED * time );\n" +
                "\tvec3 color = vec3(1.0);\n" +
                "\tcolor *= 1./v;\n" +
                "\tcolor *= 2.-r;\n" +
                "\tcolor+=vec3(factorize(3.,4.,0.2)*factorize(0.1,PI,0.4)*.7,factorize(1.25,1.6584,0.2)*factorize(0.1,PI,0.4)*.7,factorize(4.25,3.1548,0.2)*factorize(0.1,0.0,0.4)*0.3);\n" +
                "\tfloat newy=0.3*(factor*0.8+0.2)*(sin(4.0*position.x+time)+cos(2.0*position.x+time))*exp((sin(time*1.8)*0.8+0.2)*0.5*position.x);\n" +
                "\tfloat newy2=0.3*(sin(time*0.4+PI)*0.8+0.2)*(sin(3.954*position.x+0.6*time+1.485)+cos(1.624*position.x+0.3*time+1.65))*exp((sin(time)*0.8+0.2)*0.62*position.x);\n" +
                "\tfloat yspef = pow(abs(newy-position.y),0.2);\n" +
                "\tfloat yspef2 = pow(abs(newy2-position.y),0.2);\n" +
                "\tcolor*=0.5/vec3(yspef,yspef,yspef);\n" +
                "\tcolor*=0.4/vec3(yspef2,yspef2,yspef2);\n" +
                "\treturn color;\n" +
                "}\n" +
                "void main( void ) {\n" +
                "\tvec3 color;\n" +
                "\tcolor = imagesshader(gl_FragCoord.xy);\n" +
                "\tgl_FragColor = vec4( color, 1.0 );\n" +
                "}";
    }
}
