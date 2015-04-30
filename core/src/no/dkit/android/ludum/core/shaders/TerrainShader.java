package no.dkit.android.ludum.core.shaders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class TerrainShader {
    Texture dirmask;
    Texture angleMask;
    Texture texture;
    Color color = Color.WHITE;
    private ShaderProgram shader;

    public TerrainShader(Texture dirmask, Texture anglemask) {
        this.dirmask = dirmask;
        this.angleMask = anglemask;
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
                        "uniform vec4 color;\n" +
                        "uniform sampler2D u_texture;\n" +
                        "uniform sampler2D u_mask;\n" +
                        "void main(void) {\n" +
                        "	//sample the colour from the first texture\n" +
                        "	vec4 texColor0 = texture2D(u_texture, vTexCoord);\n" +
                        "	//get the mask; we will only use the alpha channel\n" +
                        "	float mask = texture2D(u_mask, vTexCoord).a;\n" +
                        "	//Calculate color and mask\n" +
                        "	gl_FragColor = texColor0 * mask * color;\n" +
                        "}";

        ShaderProgram.pedantic = false;

        shader = new ShaderProgram(VERT, FRAG);

        if (!shader.isCompiled())
            System.out.println("ERROR COMPILING SHADER! " + shader.getLog());

        shader.begin();
        shader.setUniformi("u_mask", 1);
        shader.setUniformi("u_texture", 0);
        shader.setUniformf("color", color);
        shader.end();
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void setUseAngle(boolean angle) {
        if (angle)
            angleMask.bind(1);
        else
            dirmask.bind(1);
    }

    public void bind(Texture texture) {
        this.texture = texture;
        this.texture.bind(0);
    }

    public void setColor(Color color) {
        this.color = color;
        shader.setUniformf("color", color);
    }

    public Texture getTexture() {
        return texture;
    }
}
