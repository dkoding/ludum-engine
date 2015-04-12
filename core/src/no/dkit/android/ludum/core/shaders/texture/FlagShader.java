package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class FlagShader extends AbstractTextureShader {
    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "float amplitude = 0.0060;\n" +
                "float frequence = 25.00;\n" +
                "float speed = 5.0;\n" +
                "\n" +
                "vec2 wobble(vec2 uv, float amplitude, float frequence, float speed)\n" +
                "{\n" +
                "\tfloat offset = amplitude*sin(uv.y*frequence+time*speed);\n" +
                "\treturn vec2(uv.x-offset,uv.y+offset*2.);\n" +
                "}\n" +
                "\n" +
                "void main(void)\n" +
                "{\n" +
                "\tvec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                "\tuv = wobble(uv,amplitude,frequence,speed);\n" +
                "\tgl_FragColor = texture2D(u_texture,uv);\n" +
                "}";
    }
}
