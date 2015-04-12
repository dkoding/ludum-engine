package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class PixelizeShader extends AbstractTextureShader {


    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "#define AMOUNT 4.\n" +
                "void main(void)\n" +
                "{\n" +
                "\tvec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                "\tuv=floor(uv*vec2(resolution.x/AMOUNT,resolution.y/AMOUNT))/vec2(resolution.x/AMOUNT,resolution.y/AMOUNT);\n" +
                "\tvec4 c=texture2D(u_texture,uv);\n" +
                "\tc.x=float(int((c.x*8.0)+0.5))/8.0;\n" +
                "\tc.y=float(int((c.y*8.0)+0.5))/8.0;\n" +
                "\tc.z=float(int((c.z*8.0)+0.5))/8.0;\n" +
                "\tgl_FragColor=vec4(c);\n" +
                "}";
    }
}
