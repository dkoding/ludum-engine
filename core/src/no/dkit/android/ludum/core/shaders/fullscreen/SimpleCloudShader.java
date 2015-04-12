package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class SimpleCloudShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "float hash( vec2 p )\n" +
                "{\n" +
                "\tfloat h = dot(p,vec2(127.1,311.7));\n" +
                "    return -1.0 + 2.0*fract(sin(h)*43758.5453123);\n" +
                "}\n" +
                "\n" +
                "float noise( in vec2 p )\n" +
                "{\n" +
                "    vec2 i = floor( p );\n" +
                "    vec2 f = fract( p );\n" +
                "\t\n" +
                "\tvec2 u = f*f*(3.0-2.0*f);\n" +
                "\n" +
                "    return mix( mix( hash( i + vec2(0.0,0.0) ), \n" +
                "                     hash( i + vec2(1.0,0.0) ), u.x),\n" +
                "                mix( hash( i + vec2(0.0,1.0) ), \n" +
                "                     hash( i + vec2(1.0,1.0) ), u.x), u.y);\n" +
                "}\n" +
                "///////////////////////\n" +
                "\n" +
                "\n" +
                "float space = 0.48;\n" +
                "float krnl = 1.;\n" +
                "\n" +
                "vec4 getTexCoordValue (float x, float y, vec2 uv, float scale)\n" +
                "{\n" +
                "\n" +
                "\tvec2 p = vec2 (x, y);\n" +
                "    //vec2 p = gl_FragCoord.xy / iResolution.xy;\n" +
                "\n" +
                "\t//vec2 uv = p*vec2(iResolution.x/iResolution.y,1.0);\n" +
                "\t\n" +
                "\tfloat f = 0.0;\n" +
                "\n" +
                "\t{\n" +
                "\t\tuv *= scale;\n" +
                "        mat2 m = mat2( 1.6,  1.2, -1.2,  1.6 );\n" +
                "\t\tf  = 0.5000*noise( uv ); uv = m*uv;\n" +
                "\t\tf += 0.2500*noise( uv ); uv = m*uv;\n" +
                "\t\tf += 0.1250*noise( uv ); uv = m*uv;\n" +
                "\t\tf += 0.0625*noise( uv ); uv = m*uv;\n" +
                "\t}\n" +
                "\n" +
                "\tf = 0.5 + 0.5*f;\n" +
                "\t\n" +
                "    f *= smoothstep( 0.0, 0.005, abs(p.x-0.0) );\t\n" +
                "\t\n" +
                "\treturn vec4( f, f, f, 1.0 );\t\n" +
                "}\n" +
                "\n" +
                "float getVolume (float x, float y, float time)\n" +
                "{\t\n" +
                "\tvec2 uv = vec2(x, y) / resolution.xy;\t\n" +
                "\tfloat vol = 0.0;\n" +
                "\tconst int layers = 6;\n" +
                "\tfloat totalColor = 1.0;\n" +
                "\t\n" +
                "\tfor (int i = 0; i < layers; ++i)\n" +
                "\t{\n" +
                "\t\n" +
                "\t\tif (mod(float(i), 2.0) == 0.0)\n" +
                "\t\t{\n" +
                "\t\t\tuv.x += time * (0.01 + float(i) * 0.001);\t\t\t\n" +
                "\t\t} else {\n" +
                "\t\t\tuv.y -= time * (0.01 + float(i) * 0.001);\t\t\n" +
                "\t\t}\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\tvec4 col = getTexCoordValue(x, y, uv, float(i) * 3.0 + 2.0);\n" +
                "\t\t\n" +
                "\t\tfloat amount = totalColor * 0.5;\n" +
                "\t\ttotalColor = totalColor - (amount);\n" +
                "\t\ttotalColor = max (totalColor, 0.0);\n" +
                "\t\t\n" +
                "\t\tvol += (col.r + col.g + col.b) * 0.333 * amount;\t\t\n" +
                "\t}\n" +
                "\treturn vol;\n" +
                "\t\n" +
                "}\n" +
                "\n" +
                "float filter (float vol, float space)\n" +
                "{\n" +
                "\tvol = clamp (vol - space, 0.0, 1.0);\t\n" +
                "\tvol = vol * (1.0 / (1.0 - space));\t\n" +
                "\treturn vol;\n" +
                "}\n" +
                "\n" +
                "void drawBG()\n" +
                "{\n" +
                "\tfloat h = gl_FragCoord.y / resolution.y;\n" +
                "\th = 1.0 - h;\n" +
                "\th *= 0.6;\n" +
                "\tgl_FragColor = vec4(h, h, h + 0.5, 0.0);\n" +
                "\tgl_FragColor.b = min (1.0, gl_FragColor.b);\n" +
                "\t\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "void main(void)\n" +
                "{\n" +
                "\t\n" +
                "\tfloat t = 60.0 + time * 2.445;\n" +
                "\tfloat s = sin(time);\n" +
                "\tfloat vol = 0.0;\n" +
                "\tvol += getVolume (gl_FragCoord.x, gl_FragCoord.y, t);\t\n" +
                "\t\n" +
                "\tvec2 lightPos = resolution.xy * 1.0;\t\n" +
                "\tvec2 diff = gl_FragCoord.xy - lightPos;\t\n" +
                "\tvec2 diffNorm = normalize(diff) * 2.5;\n" +
                "\t\n" +
                "\tconst int steps = 8;\n" +
                "\tfloat shade = 0.0;\n" +
                "\t\n" +
                "\tfor (int i = 0; i < steps; ++i)\n" +
                "\t{\n" +
                "\t\tfloat ifl = float(i);\n" +
                "\t\t\n" +
                "\t\tvec2 pos = gl_FragCoord.xy;\n" +
                "\t\tfloat sample = getVolume (pos.x + diffNorm.x * ifl, pos.y + diffNorm.y * ifl, t);\t\t\n" +
                "\t\tshade += sample / float(steps);\t\t\n" +
                "\t}\n" +
                "\t\n" +
                "\tdrawBG();\n" +
                "\t\n" +
                "\t\n" +
                "\tvol = filter(vol, space);\n" +
                "\tvol = clamp(vol, 0.0, 1.0);\n" +
                "\tshade = filter(shade, space * 1.2);\n" +
                "\tshade = clamp(shade, 0.0, 1.0);\n" +
                "\t\n" +
                "\t\n" +
                "\tgl_FragColor.r += vol;\n" +
                "\tgl_FragColor.g += vol;\n" +
                "\tgl_FragColor.b += vol;\n" +
                "\t\n" +
                "\t\n" +
                "\tshade *= 1.5;\n" +
                "\tshade = 1.0 - shade;\n" +
                "\t\n" +
                "\tgl_FragColor.r *= shade;\n" +
                "\tgl_FragColor.g *= shade;\n" +
                "\tgl_FragColor.b *= shade;\n" +
                "\t\n" +
                "\t\n" +
                "\t//////////////////////////////\n" +
                "\t\n" +
                "\n" +
                "\n" +
                "\t\n" +
                "}";
    }
}
