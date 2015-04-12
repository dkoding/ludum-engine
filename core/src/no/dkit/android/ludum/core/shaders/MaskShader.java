package no.dkit.android.ludum.core.shaders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class MaskShader {
    Texture mask;
    Texture texture;

    private ShaderProgram shader;

    public MaskShader(Texture mask) {
        this.mask = mask;
        createShader();
    }

    private void createShader() {
        final String VERT =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                        "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                        "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +

                        "uniform mat4 u_projTrans;\n" +
                        "varying vec4 vColor;\n" +
                        "varying vec2 vTexCoord;\n" +

                        "void main() {\n" +
                        "	vColor = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                        "	vTexCoord = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
                        "	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                        "}";

        final String FRAG =
                //GL ES specific stuff
                "#ifdef GL_ES\n" //
                        + "#define LOWP lowp\n" //
                        + "precision mediump float;\n" //
                        + "#else\n" //
                        + "#define LOWP \n" //
                        + "#endif\n" + //
                        "varying LOWP vec4 vColor;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "uniform sampler2D u_texture;\n" +
                        "uniform sampler2D u_mask;\n" +
                        "void main(void) {\n" +
                        "	//sample the colour from the first texture\n" +
                        "	vec4 texColor0 = texture2D(u_texture, vTexCoord);\n" +
                        "	//get the mask; we will only use the alpha channel\n" +
                        "	float mask = texture2D(u_mask, vTexCoord).a;\n" +
                        "	//Calculate color and mask\n" +
                        "	gl_FragColor = texColor0 * mask;\n" +
                        "}";

        ShaderProgram.pedantic = false;

        shader = new ShaderProgram(VERT, FRAG);

        if (!shader.isCompiled())
            System.out.println("ERROR COMPILING SHADER! " + shader.getLog());

        shader.begin();
        shader.setUniformi("u_mask", 1);
        shader.setUniformi("u_texture", 0);
        shader.end();

        mask.bind(1);
    }

    public ShaderProgram getShader() {
        return shader;
    }
}
