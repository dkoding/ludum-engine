package no.dkit.android.ludum.core.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public abstract class AbstractShader implements RenderOperations {
    protected enum PRECISION {LOW, MEDIUM, HIGH}

    protected PRECISION precision = PRECISION.LOW;

    protected boolean wrapped = false;
    boolean enabled = true;
    private Mesh mesh;
    float time = 0;

    protected ShaderProgram shader;
    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    private TextureRegion maskRegion;
    private float width;
    private float height;
    private boolean alpha = false;
    private boolean ready = false;

    public void init (int width, int height) {
        init(width, height, false);
    }

    public void init(int width, int height, boolean alpha) {
        this.alpha = alpha;
        this.width = width;
        this.height = height;
        init();
    }

    public void init() {
        mesh = createFullViewRectangle();
        String vertexShader = getVertexShader();
        String fragmentShader = getFragmentShader();

        vertexShader = setupPrecisionFor(vertexShader, PRECISION.HIGH);
        fragmentShader = setupPrecisionFor(fragmentShader, precision);

        ShaderProgram.pedantic = false;

        shader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shader.isCompiled()) {
            Gdx.app.log(this.getClass().getName(), "ERROR COMPILING SHADER! " + shader.getLog());
            enabled = false;
        }

        if (enabled) {
            shader.begin();
            shader.setUniformf("resolution", this.width, this.height);
            shader.end();
        }

        //Gdx.graphics.setVSync(true);

        createRenderBuffer();

        ready=true;
    }

    private String setupPrecisionFor(String shaderSource, PRECISION precision) {
        return "#ifdef GL_ES \n" +
                "#define LOW lowp\n" +
                "#define MED mediump\n" +
                "#define HIGH highp\n" +
                "precision " + precision.toString().toLowerCase() + "p float;\n" +
                "#else\n" +
                "#define MED\n" +
                "#define LOW\n" +
                "#define HIGH\n" +
                "#endif\n".
                        concat(shaderSource);
    }

    public void update() {
        if(!ready)
            throw new RuntimeException("Trying to use a shader before init!");

        if (!enabled) return;

        fbo.begin();
        renderShaderonMesh();
        fbo.end();
    }

    @Override
    public void update(float deltaX, float deltaY) {
        update();
    }

    @Override
    public void update(float deltaX, float deltaY, float translateX, float translateY) {
        update();
    }

    @Override
    public void update(float centerX, float centerY, float deltaX, float deltaY, float translateX, float translateY) {
        update();
    }

    public void renderFullScreen(SpriteBatch spriteBatch) {
        render(spriteBatch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(SpriteBatch spriteBatch, float x, float y, float width, float height) {
        render(spriteBatch, x, y, width, height, 1, 0);
    }

    public void render(SpriteBatch spriteBatch, float x, float y, float width, float height, float scale, float rotation) {
        if(!ready)
                throw new RuntimeException("Trying to use a shader before init!");

        if (maskRegion != null) {
            spriteBatch.enableBlending();
            Gdx.gl20.glColorMask(false, false, false, true);
            spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
            spriteBatch.begin();

            spriteBatch.draw(maskRegion,
                    x, y,
                    width / 2, height / 2,
                    width, height,
                    scale, scale,
                    rotation, true);

            //spriteBatch.draw(maskRegion, x, y, width, height);
            spriteBatch.end();

            Gdx.gl20.glColorMask(true, true, true, true);
            spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
            spriteBatch.begin();

            spriteBatch.draw(fboRegion,
                    x, y,
                    width / 2, height / 2,
                    width, height,
                    scale, scale,
                    rotation, true);

            spriteBatch.end();
            spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            if (!alpha)
                spriteBatch.disableBlending();

            spriteBatch.begin();
            spriteBatch.draw(fboRegion,
                    x, y,
                    width / 2, height / 2,
                    width, height,
                    scale, scale,
                    rotation, true);
            spriteBatch.end();

            if (!alpha)
                spriteBatch.enableBlending();
        }
    }

    public void render(SpriteBatch spriteBatch, float x, float y) {
        render(spriteBatch, x, y, width, height);
    }

    private void createRenderBuffer() {
        if (fbo == null) {
            if (alpha)
                fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int) width, (int) height, false);
            else
                fbo = new FrameBuffer(Pixmap.Format.RGB888, (int) width, (int) height, false);

            fboRegion = new TextureRegion(fbo.getColorBufferTexture());
            fboRegion.flip(false, true);
        }
    }

    private void renderShaderonMesh() {
        renderShaderonMesh(Gdx.graphics.getDeltaTime());
    }

    private void renderShaderonMesh(float time) {
        if(!ready)
                throw new RuntimeException("Trying to use a shader before init!");

        this.time += time;
        shader.begin();
        shader.setUniformf("time", this.time);
        performTextureBindings();

        if (wrapped) {
            Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT);
            Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT);
        } else {
            Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
            Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
        }

        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
    }

    protected void performTextureBindings() {

    }

    protected void cleanupTextures() {

    }

    // Usually not necessary to override this
    public String getVertexShader() {
        return "attribute vec3 a_position;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "\t gl_Position = vec4(a_position, 1.0 );\n" +
                "}";

    }

    public abstract String getFragmentShader();

    private Mesh createFullViewRectangle() {
        float x1 = -1.0f, y1 = -1.0f;
        float x2 = 1.0f, y2 = 1.0f;
        float z0 = 0f;
        float u1 = 0.0f, v1 = 0.0f;
        float u2 = 1.0f, v2 = 1.0f;

        Mesh mesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3,
                ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(
                VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE
                + "0"));

        mesh.setVertices(new float[]{
                x1, y1, z0, u1, v1,
                x2, y1, z0, u2, v1,
                x2, y2, z0, u2, v2,
                x1, y2, z0, u1, v2});

        mesh.setIndices(new short[]{0, 1, 2, 0, 2, 3});

        return mesh;
    }

    public void dispose() {
        fbo.dispose();
        shader.dispose();
        mesh.dispose();
        cleanupTextures();
    }

    public void setMaskRegion(TextureRegion maskRegion) {
        this.maskRegion = maskRegion;
    }
}
