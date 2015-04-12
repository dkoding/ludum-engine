package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class InwardScrollShader extends AbstractTextureShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "void main(void)\n" +
                "{\n" +
                "\tvec2 uv=((gl_FragCoord.xy/resolution.xy)-vec2(0.5,0.5))*2.0;\n" +
                "\tuv.y=abs(uv.y);\n" +
                "\tgl_FragColor=texture2D(u_texture,fract(vec2((uv.x/uv.y),(1.0/uv.y)+(time))))*uv.y;" +
                "}\n";
    }
}
