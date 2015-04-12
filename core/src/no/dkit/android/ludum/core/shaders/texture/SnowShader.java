package no.dkit.android.ludum.core.shaders.texture;

import no.dkit.android.ludum.core.shaders.AbstractTextureShader;

public class SnowShader extends AbstractTextureShader {


    @Override
    public String getFragmentShader() {
        return "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "uniform sampler2D u_texture;\n" +
                "\n" +
                "//looks best with around 25 rays\n" +
                "#define NUM_RAYS 13.\n" +
                "\n" +
                "#define VOLUMETRIC_STEPS 19\n" +
                "\n" +
                "#define MAX_ITER 35\n" +
                "#define FAR 6.\n" +
                "\n" +
                "#define time time*1.1\n" +
                "\n" +
                "\n" +
                "mat2 mm2(in float a){float c = cos(a), s = sin(a);return mat2(c,-s,s,c);}\n" +
                "float noise( in float x ){return texture2D(u_texture, vec2(x*.01,1.)).x;}\n" +
                "\n" +
                "float hash( float n ){return fract(sin(n)*43758.5453);}\n" +
                "\n" +
                "//iq's ubiquitous 3d noise\n" +
                "float noise(in vec3 p)\n" +
                "{\n" +
                "\tvec3 ip = floor(p);\n" +
                "    vec3 f = fract(p);\n" +
                "\tf = f*f*(3.0-2.0*f);\n" +
                "\t\n" +
                "\tvec2 uv = (ip.xy+vec2(37.0,17.0)*ip.z) + f.xy;\n" +
                "\tvec2 rg = texture2D( u_texture, (uv+ 0.5)/256.0, -100.0 ).yx;\n" +
                "\treturn mix(rg.x, rg.y, f.z);\n" +
                "}\n" +
                "\n" +
                "mat3 m3 = mat3( 0.00,  0.80,  0.60,\n" +
                "              -0.80,  0.36, -0.48,\n" +
                "              -0.60, -0.48,  0.64 );\n" +
                "\n" +
                "\n" +
                "//See: https://www.shadertoy.com/view/XdfXRj\n" +
                "float flow(in vec3 p, in float t)\n" +
                "{\n" +
                "\tfloat z=2.;\n" +
                "\tfloat rz = 0.;\n" +
                "\tvec3 bp = p;\n" +
                "\tfor (float i= 1.;i < 5.;i++ )\n" +
                "\t{\n" +
                "\t\tp += time*.1;\n" +
                "\t\trz+= (sin(noise(p+t*0.8)*6.)*0.5+0.5) /z;\n" +
                "\t\tp = mix(bp,p,0.6);\n" +
                "\t\tz *= 2.;\n" +
                "\t\tp *= 2.01;\n" +
                "        p*= m3;\n" +
                "\t}\n" +
                "\treturn rz;\t\n" +
                "}\n" +
                "\n" +
                "//could be improved\n" +
                "float sins(in float x)\n" +
                "{\n" +
                " \tfloat rz = 0.;\n" +
                "    float z = 2.;\n" +
                "    for (float i= 0.;i < 3.;i++ )\n" +
                "\t{\n" +
                "        rz += abs(fract(x*1.4)-0.5)/z;\n" +
                "        x *= 1.3;\n" +
                "        z *= 1.15;\n" +
                "        x -= time*.65*z;\n" +
                "    }\n" +
                "    return rz;\n" +
                "}\n" +
                "\n" +
                "float segm( vec3 p, vec3 a, vec3 b)\n" +
                "{\n" +
                "    vec3 pa = p - a;\n" +
                "\tvec3 ba = b - a;\n" +
                "\tfloat h = clamp( dot(pa,ba)/dot(ba,ba), 0.0, 1. );\t\n" +
                "\treturn length( pa - ba*h )*.5;\n" +
                "}\n" +
                "\n" +
                "vec3 path(in float i, in float d)\n" +
                "{\n" +
                "    vec3 en = vec3(0.,0.,1.);\n" +
                "    float sns2 = sins(d+i*0.5)*0.22;\n" +
                "    float sns = sins(d+i*.6)*0.21;\n" +
                "    en.xz *= mm2((hash(i*10.569)-.5)*6.2+sns2);\n" +
                "    en.xy *= mm2((hash(i*4.732)-.5)*6.2+sns);\n" +
                "    return en;\n" +
                "}\n" +
                "\n" +
                "vec2 map(vec3 p, float i)\n" +
                "{\n" +
                "\tfloat lp = length(p);\n" +
                "    vec3 bg = vec3(0.);   \n" +
                "    vec3 en = path(i,lp);\n" +
                "    \n" +
                "    float ins = smoothstep(0.11,.46,lp);\n" +
                "    float outs = .15+smoothstep(.0,.15,abs(lp-1.));\n" +
                "    p *= ins*outs;\n" +
                "    float id = ins*outs;\n" +
                "    \n" +
                "    float rz = segm(p, bg, en)-0.011;\n" +
                "    return vec2(rz,id);\n" +
                "}\n" +
                "\n" +
                "float march(in vec3 ro, in vec3 rd, in float startf, in float maxd, in float j)\n" +
                "{\n" +
                "\tfloat precis = 0.001;\n" +
                "    float h=0.5;\n" +
                "    float d = startf;\n" +
                "    for( int i=0; i<MAX_ITER; i++ )\n" +
                "    {\n" +
                "        if( abs(h)<precis||d>maxd ) break;\n" +
                "        d += h*1.2;\n" +
                "\t    float res = map(ro+rd*d, j).x;\n" +
                "        h = res;\n" +
                "    }\n" +
                "\treturn d;\n" +
                "}\n" +
                "\n" +
                "//volumetric marching\n" +
                "vec3 vmarch(in vec3 ro, in vec3 rd, in float j, in vec3 orig)\n" +
                "{   \n" +
                "    vec3 p = ro;\n" +
                "    vec2 r = vec2(0.);\n" +
                "    vec3 sum = vec3(0);\n" +
                "    float w = 0.;\n" +
                "    for( int i=0; i<VOLUMETRIC_STEPS; i++ )\n" +
                "    {\n" +
                "        r = map(p,j);\n" +
                "        p += rd*.03;\n" +
                "        float lp = length(p);\n" +
                "        \n" +
                "        vec3 col = sin(vec3(1.05,2.5,1.52)*3.94+r.y)*.85+0.4;\n" +
                "        col.rgb *= smoothstep(.0,.015,-r.x);\n" +
                "        col *= smoothstep(0.04,.2,abs(lp-1.1));\n" +
                "        col *= smoothstep(0.1,.34,lp);\n" +
                "        sum += abs(col)*5. * (1.2-noise(lp*2.+j*13.+time*5.)*1.1) / (log(distance(p,orig)-2.)+.75);\n" +
                "    }\n" +
                "    return sum;\n" +
                "}\n" +
                "\n" +
                "//returns both collision dists of unit sphere\n" +
                "vec2 iSphere2(in vec3 ro, in vec3 rd)\n" +
                "{\n" +
                "    vec3 oc = ro;\n" +
                "    float b = dot(oc, rd);\n" +
                "    float c = dot(oc,oc) - 1.;\n" +
                "    float h = b*b - c;\n" +
                "    if(h <0.0) return vec2(-1.);\n" +
                "    else return vec2((-b - sqrt(h)), (-b + sqrt(h)));\n" +
                "}\n" +
                "\n" +
                "void main(void)\n" +
                "{\t\n" +
                "\tvec2 p = gl_FragCoord.xy/resolution.xy-0.5;\n" +
                "\tp.x*=resolution.x/resolution.y;\n" +
                "\tvec2 um = 1;\n" +
                "    \n" +
                "\t//camera\n" +
                "\tvec3 ro = vec3(0.,0.,5.);\n" +
                "    vec3 rd = normalize(vec3(p*.7,-1.5));\n" +
                "    mat2 mx = mm2(time*.4+um.x*6.);\n" +
                "    mat2 my = mm2(time*0.3+um.y*6.); \n" +
                "    ro.xz *= mx;rd.xz *= mx;\n" +
                "    ro.xy *= my;rd.xy *= my;\n" +
                "    \n" +
                "    vec3 bro = ro;\n" +
                "    vec3 brd = rd;\n" +
                "\t\n" +
                "    vec3 col = vec3(0.0125,0.,0.025);\n" +
                "    #if 1\n" +
                "    for (float j = 1.;j<NUM_RAYS+1.;j++)\n" +
                "    {\n" +
                "        ro = bro;\n" +
                "        rd = brd;\n" +
                "        mat2 mm = mm2((time*0.1+((j+1.)*5.1))*j*0.25);\n" +
                "        ro.xy *= mm;rd.xy *= mm;\n" +
                "        ro.xz *= mm;rd.xz *= mm;\n" +
                "        float rz = march(ro,rd,2.5,FAR,j);\n" +
                "\t\tif ( rz >= FAR)continue;\n" +
                "    \tvec3 pos = ro+rz*rd;\n" +
                "    \tcol = max(col,vmarch(pos,rd,j, bro));\n" +
                "    }\n" +
                "    #endif\n" +
                "    \n" +
                "    ro = bro;\n" +
                "    rd = brd;\n" +
                "    vec2 sph = iSphere2(ro,rd);\n" +
                "    \n" +
                "    if (sph.x > 0.)\n" +
                "    {\n" +
                "        vec3 pos = ro+rd*sph.x;\n" +
                "        vec3 pos2 = ro+rd*sph.y;\n" +
                "        vec3 rf = reflect( rd, pos );\n" +
                "        vec3 rf2 = reflect( rd, pos2 );\n" +
                "        float nz = (-log(abs(flow(rf*1.2,time)-.01)));\n" +
                "        float nz2 = (-log(abs(flow(rf2*1.2,-time)-.01)));\n" +
                "        col += (0.1*nz*nz* vec3(0.12,0.12,.5) + 0.05*nz2*nz2*vec3(0.55,0.2,.55))*0.8;\n" +
                "    }\n" +
                "    \n" +
                "\tgl_FragColor = vec4(col*1.3, 1.0);\n" +
                "}";
    }
}
