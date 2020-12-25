namespace loon.opengl
{
    /// <summary>
    /// 模拟OpenGL的API（monogame在大部分环境都把OpenGL本地适配为XNA的API，我为了方便移植又反向模拟回来……）
    /// </summary>
    public class GL
    {
     
        public const int GL_QUADS = -1;
        public const int GL_POLYGON = 0x0006;
        public const int GL_POINTS = 0x0000;
        public const int GL_LINES = 0x0001;
        public const int GL_LINE_LOOP = 0x0002;
        public const int GL_LINE_STRIP = 0x0003;
        public const int GL_TRIANGLES = 0x0004;
        public const int GL_TRIANGLE_STRIP = 0x0005;
        public const int GL_TRIANGLE_FAN = 0x0006;

        public const int GL_RENDERER = 0x00;
        public const int GL_ALPHA_TEST = 0xbc0;
        public const int GL_ALWAYS = 0x207;
        public const int GL_BLEND = 0xbe2;
        public const int GL_BYTE = 0x1400;
        public const int GL_CCW = 0x901;
        public const int GL_CLAMP_TO_EDGE = 0x812f;
        public const int GL_COLOR_ARRAY = 0x8076;
        public const int GL_COLOR_BUFFER_BIT = 0x4000;
        public const int GL_COMPRESSED_TEXTURE_FORMATS = 0x86a3;
        public const int GL_CULL_FACE = 0xb44;
        public const int GL_CW = 0x900;
        public const int GL_DEPTH_BUFFER_BIT = 0x100;
        public const int GL_DEPTH_TEST = 0xb71;
        public const int GL_DITHER = 0xbd0;
        public const int GL_EQUAL = 0x202;
        public const int GL_FASTEST = 0x1101;
        public const int GL_FLOAT = 0x1406;
        public const int GL_FOG = 0xb60;
        public const int GL_FOG_COLOR = 0xb66;
        public const int GL_FOG_END = 0xb64;
        public const int GL_FOG_MODE = 0xb65;
        public const int GL_FOG_START = 0xb63;
        public const int GL_GEQUAL = 0x206;
        public const int GL_GREATER = 0x204;
        public const int GL_INCR = 0x1e02;
        public const int GL_KEEP = 0x1e00;
        public const int GL_LEQUAL = 0x203;
        public const int GL_LIGHTING = 0xb50;

        public const int GL_LINEAR = 0x2601;
        public const int GL_LUMINANCE = 0x1909;
        public const int GL_LUMINANCE_ALPHA = 0x190a;
        public const int GL_MAX_TEXTURE_SIZE = 0xd33;
        public const int GL_MODELVIEW = 0x1700;
        public const int GL_MODULATE = 0x2100;

        public const int GL_NEAREST = 0x2600;
        public const int GL_NEAREST_MIPMAP_NEAREST = 0x2700;
        public const int GL_LINEAR_MIPMAP_NEAREST = 0x2701;
        public const int GL_NO_ERROR = 0;
        public const int GL_NORMAL_ARRAY = 0x8075;
        public const int GL_NOTEQUAL = 0x205;
        public const int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86a2;
        public const int GL_ZERO = 0;
        public const int GL_ONE = 1;
        public const int GL_ONE_MINUS_SRC_ALPHA = 0x303;
        public const int GL_DST_COLOR = 0x0306;
        public const int GL_PALETTE8_RGBA4_OES = 0x8b98;
        public const int GL_PALETTE8_RGBA8_OES = 0x8b96;
        public const int GL_PERSPECTIVE_CORRECTION_HINT = 0xc50;
        public const int GL_PROJECTION = 0x1701;
        public const int GL_REPEAT = 0x2901;
        public const int GL_REPLACE = 0x1E01;
        public const int GL_FLAT = 0x1D00;
        public const int GL_RGB = 0x1907;
        public const int GL_RGBA = 0x1908;
        public const int GL_SCISSOR_TEST = 0xc11;
        public const int GL_SHORT = 0x1402;
        public const int GL_SMOOTH = 0x1d01;
   
        public const int GL_SRC_ALPHA = 770;
        public const int GL_STENCIL_BUFFER_BIT = 0x400;
        public const int GL_STENCIL_TEST = 0xb90;
        public const int GL_TEXTURE = 0x1702;
        public const int GL_TEXTURE_2D = 0xde1;
        public const int GL_TEXTURE_COORD_ARRAY = 0x8078;
        public const int GL_TEXTURE_ENV = 0x2300;
        public const int GL_TEXTURE_ENV_MODE = 0x2200;
        public const int GL_TEXTURE_MAG_FILTER = 0x2800;
        public const int GL_TEXTURE_MIN_FILTER = 0x2801;
        public const int GL_TEXTURE_WRAP_S = 0x2802;
        public const int GL_TEXTURE_WRAP_T = 0x2803;
        public const int GL_SRC_COLOR = 0x0300;
        public const int GL_ONE_MINUS_SRC_COLOR = 0x0301;
        public const int GL_DST_ALPHA = 0x0304;
        public const int GL_ONE_MINUS_DST_ALPHA = 0x0305;
        public const int GL_UNSIGNED_BYTE = 0x1401;
        public const int GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033;
        public const int GL_VERTEX_ARRAY = 0x8074;
        public const int GL_LINE_SMOOTH = 0x0B20;

    }
}
