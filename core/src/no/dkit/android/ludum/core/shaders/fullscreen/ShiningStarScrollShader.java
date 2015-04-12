package no.dkit.android.ludum.core.shaders.fullscreen;

import no.dkit.android.ludum.core.shaders.AbstractShader;

public class ShiningStarScrollShader extends AbstractShader {

    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "#define BLADES 6.0\n" +
                "#define BIAS 0.1\n" +
                "#define SHARPNESS 3.0\n" +
                "\n" +
                "float rand(vec2 co){ return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453); }\n" +
                "\n" +
                "vec3 star(vec2 position) {\n" +
                "\tfloat blade = clamp(pow(sin(atan(position.y,position.x )*BLADES)+BIAS, SHARPNESS), 0.0, 1.0);\n" +
                "\tvec3 color = mix(vec3(-0.34, -0.5, -1.0), vec3(0.0, -0.5, -1.0), (position.y + 1.0) * 0.25);\n" +
                "\tcolor += (vec3(0.95, 0.65, 0.30) * 1.0 / distance(vec2(0.0), position) * 0.075);\n" +
                "\tcolor += vec3(0.95, 0.45, 0.30) * min(1.0, blade *0.7) * (1.0 / distance(vec2(0.0, 0.0), position)*0.075);\n" +
                "\treturn color;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "// Tweaked from http://glsl.heroku.com/e#4982.0\n" +
                "float hash( float n ) { return fract(sin(n)*43758.5453); }\n" +
                "\n" +
                "float noise( in vec2 x )\n" +
                "{\n" +
                "\tvec2 p = floor(x);\n" +
                "\tvec2 f = fract(x);\n" +
                "    \tf = f*f*(3.0-2.0*f);\n" +
                "    \tfloat n = p.x + p.y*57.0;\n" +
                "    \tfloat res = mix(mix(hash(n+0.0), hash(n+1.0),f.x), mix(hash(n+57.0), hash(n+58.0),f.x),f.y);\n" +
                "    \treturn res;\n" +
                "}\n" +
                "\n" +
                "vec3 cloud(vec2 p) {\n" +
                "\tfloat f = 0.0;\n" +
                "    \tf += 0.50000*noise(p*1.0*10.0); \n" +
                "    \tf += 0.25000*noise(p*2.0*10.0); \n" +
                "    \tf += 0.12500*noise(p*4.0*10.0); \n" +
                "    \tf += 0.06250*noise(p*8.0*10.0);\t\n" +
                "\tf *= f;\n" +
                "\treturn vec3(f*.65, f*.45, f)*.6;\n" +
                "}\n" +
                "\n" +
                "const float LAYERS\t= 5.0;\n" +
                "const float SPEED\t= 0.003;\n" +
                "const float SCALE\t= 50.0;\n" +
                "const float DENSITY\t= 2.;\n" +
                "const float BRIGHTNESS\t= 10.0;\n" +
                "       vec2 ORIGIN\t= resolution.xy*.5;\n" +
                "\n" +
                "\n" +
                "void main( void ) {\n" +
                "\t\n" +
                "\tvec2   pos = gl_FragCoord.xy - ORIGIN;\n" +
                "\tfloat dist = length(pos) / resolution.y;\n" +
                "\tvec2 coord = vec2(pow(dist, 0.1), atan(pos.x, pos.y) / (3.1415926*2.0));\n" +
                "\t\n" +
                "\t// Nebulous cloud\n" +
                "\tvec3 color = cloud(pos/resolution);\n" +
                "\t// Background stars\n" +
                "\tfloat a = pow((1.0-dist),20.0);\n" +
                "\tfloat t = time*-.05;\n" +
                "\tfloat r = coord.x - (t*SPEED);\n" +
                "\tfloat c = fract(a+coord.y + 0.0*.543);\n" +
                "\tvec2  p = vec2(r, c*.5)*4000.0;\n" +
                "\tvec2 uv = fract(p)*2.0-1.0;\n" +
                "\tfloat m = clamp((rand(floor(p))-.9)*BRIGHTNESS, 0.0, 1.0);\n" +
                "\tcolor +=  clamp((1.0-length(uv*2.0))*m*dist, 0.0, 1.0);\n" +
                "\t\n" +
                "\tfor ( float i = 1.0;i<8.0;i++) {\n" +
                "\t\ta = pow((1.0-dist),2000.0);\n" +
                "\t\tt = i*10.0 - time*i*i;\n" +
                "\t\tr = coord.x + (t*SPEED);\n" +
                "\t\tc = fract(a+coord.y + i*.543);\n" +
                "\t\tp = vec2(r, c*.99)*SCALE*(LAYERS/(i*i));\n" +
                "\t\tuv = fract(p)*2.0-1.0;\n" +
                "\t\tm = clamp((rand(floor(p))-DENSITY/i)*BRIGHTNESS, 0.0, 1.0);\n" +
                "\t\tcolor +=  clamp(star(uv*0.5)*m*dist, 0.0, 1.0);\n" +
                "\t}\n" +
                "\t\n" +
                "\tgl_FragColor = vec4(color, 1.0);\n" +
                "}";
    }
}
