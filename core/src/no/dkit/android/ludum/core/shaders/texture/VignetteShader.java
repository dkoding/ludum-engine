package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class VignetteShader extends AbstractTextureShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "void main(void)\n" +
                "{\n" +
                "\tfloat OuterVig = 1.0; // Position for the Outer vignette\n" +
                "\tfloat InnerVig = 0.05; // Position for the inner Vignette Ring\n" +
                "\tvec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                "\tvec4 color = texture2D(u_texture, uv);\n" +
                "\tvec2 center = vec2(0.5,.5); // Center of Screen\n" +
                "\tfloat dist  = distance(center,uv )*1.414213; // Distance  between center and the current Uv. Multiplyed by 1.414213 to fit in the range of 0.0 to 1.0 \n" +
                "\tfloat vig = clamp((OuterVig-dist) / (OuterVig-InnerVig),0.0,1.0); // Generate the Vignette with Clamp which go from outer Viggnet ring to inner vignette ring with smooth steps\n" +
                "\tcolor *= vig; // Multiply the Vignette with the texture color\n" +
                "\tgl_FragColor = color;\n" +
                "}";
    }
}
