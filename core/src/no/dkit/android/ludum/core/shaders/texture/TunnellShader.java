package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class TunnellShader extends AbstractTextureShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "void main( void )\n" +
                "{\n" +
                "    vec2 p = (-resolution.xy + 2.0*gl_FragCoord.xy)/resolution.y;\n" +
                "    float a = atan(p.y,p.x);\n" +
                "    float r = pow( pow(p.x*p.x,16.0) + pow(p.y*p.y,16.0), 1.0/32.0 );\n" +
                "    vec2 uv = vec2( 0.5/r + 0.5*time,a/3.1416 );\n" +
                "    vec3 col =  texture2D( u_texture, uv ).xyz * r;\n" +
                "    gl_FragColor = vec4( col, 1.0 );\n" +
                "}\n";
    }
}
