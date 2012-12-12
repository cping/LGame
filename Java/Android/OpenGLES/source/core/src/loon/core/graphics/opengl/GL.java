package loon.core.graphics.opengl;

import javax.microedition.khronos.opengles.GL10;

/**
 * Copyright 2008 - 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email javachenpeng@yahoo.com
 * @version 0.1
 */
public interface GL {

	public static int MODE_NORMAL = 1;

	public static int MODE_ALPHA_MAP = 2;

	public static int MODE_ALPHA_BLEND = 3;

	public static int MODE_COLOR_MULTIPLY = 4;

	public static int MODE_ADD = 5;

	public static int MODE_SCREEN = 6;

	public static int MODE_ALPHA = 7;

	public static int MODE_SPEED = 8;

	public static int MODE_ALPHA_ONE = 9;

	public static int MODE_NONE = 10;

	public static final int GL_QUADS = -1;

	public static final int GL_POLYGON = GL.GL_TRIANGLE_FAN;

	public static final int GL_T2F_V3F = -2;

	public static final int GL_FILL = -3;

	public static final int GL_RGBA8 = -4;

	public static final int GL_RGB5_A1 = -5;

	public static final int GL_RGBA4 = -6;

	public static final int GL_RGBA2 = -7;

	public static final int GL_RGB8 = -8;

	public static final int GL_RGB5 = -9;

	public static final int GL_RGB4 = -10;

	public static final int GL_R3_G3_B2 = -11;

	public static final int GL_OES_VERSION_1_0 = 1;

	public static final int GL_OES_read_format = 1;

	public static final int GL_OES_compressed_paletted_texture = 1;

	public static final int GL_DEPTH_BUFFER_BIT = GL10.GL_DEPTH_BUFFER_BIT;

	public static final int GL_STENCIL_BUFFER_BIT = GL10.GL_STENCIL_BUFFER_BIT;

	public static final int GL_COLOR_BUFFER_BIT = GL10.GL_COLOR_BUFFER_BIT;

	public static final int GL_FALSE = GL10.GL_FALSE;

	public static final int GL_TRUE = GL10.GL_TRUE;

	public static final int GL_POINTS = GL10.GL_POINTS;

	public static final int GL_LINES = GL10.GL_LINES;

	public static final int GL_LINE_LOOP = GL10.GL_LINE_LOOP;

	public static final int GL_LINE_STRIP = GL10.GL_LINE_STRIP;

	public static final int GL_TRIANGLES = GL10.GL_TRIANGLES;

	public static final int GL_TRIANGLE_STRIP = GL10.GL_TRIANGLE_STRIP;

	public static final int GL_TRIANGLE_FAN = GL10.GL_TRIANGLE_FAN;

	public static final int GL_NEVER = GL10.GL_NEVER;

	public static final int GL_LESS = GL10.GL_LESS;

	public static final int GL_EQUAL = GL10.GL_EQUAL;

	public static final int GL_LEQUAL = GL10.GL_LEQUAL;

	public static final int GL_GREATER = GL10.GL_GREATER;

	public static final int GL_NOTEQUAL = GL10.GL_NOTEQUAL;

	public static final int GL_GEQUAL = GL10.GL_GEQUAL;

	public static final int GL_ALWAYS = GL10.GL_ALWAYS;

	public static final int GL_ZERO = GL10.GL_ZERO;

	public static final int GL_ONE = GL10.GL_ONE;

	public static final int GL_SRC_COLOR = GL10.GL_SRC_COLOR;

	public static final int GL_ONE_MINUS_SRC_COLOR = GL10.GL_ONE_MINUS_SRC_COLOR;

	public static final int GL_SRC_ALPHA = GL10.GL_SRC_ALPHA;

	public static final int GL_ONE_MINUS_SRC_ALPHA = GL10.GL_ONE_MINUS_SRC_ALPHA;

	public static final int GL_DST_ALPHA = GL10.GL_DST_ALPHA;

	public static final int GL_ONE_MINUS_DST_ALPHA = GL10.GL_ONE_MINUS_DST_ALPHA;

	public static final int GL_DST_COLOR = GL10.GL_DST_COLOR;

	public static final int GL_ONE_MINUS_DST_COLOR = GL10.GL_ONE_MINUS_DST_COLOR;

	public static final int GL_SRC_ALPHA_SATURATE = GL10.GL_SRC_ALPHA_SATURATE;

	public static final int GL_FRONT = GL10.GL_FRONT;

	public static final int GL_BACK = GL10.GL_BACK;

	public static final int GL_FRONT_AND_BACK = GL10.GL_FRONT_AND_BACK;

	public static final int GL_FOG = GL10.GL_FOG;

	public static final int GL_LIGHTING = GL10.GL_LIGHTING;

	public static final int GL_TEXTURE_2D = GL10.GL_TEXTURE_2D;

	public static final int GL_CULL_FACE = GL10.GL_CULL_FACE;

	public static final int GL_ALPHA_TEST = GL10.GL_ALPHA_TEST;

	public static final int GL_BLEND = GL10.GL_BLEND;

	public static final int GL_COLOR_LOGIC_OP = GL10.GL_COLOR_LOGIC_OP;

	public static final int GL_DITHER = GL10.GL_DITHER;

	public static final int GL_STENCIL_TEST = GL10.GL_STENCIL_TEST;

	public static final int GL_DEPTH_TEST = GL10.GL_DEPTH_TEST;

	public static final int GL_POINT_SMOOTH = GL10.GL_POINT_SMOOTH;

	public static final int GL_LINE_SMOOTH = GL10.GL_LINE_SMOOTH;

	public static final int GL_SCISSOR_TEST = GL10.GL_SCISSOR_TEST;

	public static final int GL_COLOR_MATERIAL = GL10.GL_COLOR_MATERIAL;

	public static final int GL_NORMALIZE = GL10.GL_NORMALIZE;

	public static final int GL_RESCALE_NORMAL = GL10.GL_RESCALE_NORMAL;

	public static final int GL_POLYGON_OFFSET_FILL = GL10.GL_POLYGON_OFFSET_FILL;

	public static final int GL_VERTEX_ARRAY = GL10.GL_VERTEX_ARRAY;

	public static final int GL_NORMAL_ARRAY = GL10.GL_NORMAL_ARRAY;

	public static final int GL_COLOR_ARRAY = GL10.GL_COLOR_ARRAY;

	public static final int GL_TEXTURE_COORD_ARRAY = GL10.GL_TEXTURE_COORD_ARRAY;

	public static final int GL_MULTISAMPLE = GL10.GL_MULTISAMPLE;

	public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = GL10.GL_SAMPLE_ALPHA_TO_COVERAGE;

	public static final int GL_SAMPLE_ALPHA_TO_ONE = GL10.GL_SAMPLE_ALPHA_TO_ONE;

	public static final int GL_SAMPLE_COVERAGE = GL10.GL_SAMPLE_COVERAGE;

	public static final int GL_NO_ERROR = GL10.GL_NO_ERROR;

	public static final int GL_INVALID_ENUM = GL10.GL_INVALID_ENUM;

	public static final int GL_INVALID_VALUE = GL10.GL_INVALID_VALUE;

	public static final int GL_INVALID_OPERATION = GL10.GL_INVALID_OPERATION;

	public static final int GL_STACK_OVERFLOW = GL10.GL_STACK_OVERFLOW;

	public static final int GL_STACK_UNDERFLOW = GL10.GL_STACK_UNDERFLOW;

	public static final int GL_OUT_OF_MEMORY = GL10.GL_OUT_OF_MEMORY;

	public static final int GL_EXP = GL10.GL_EXP;

	public static final int GL_EXP2 = GL10.GL_EXP2;

	public static final int GL_FOG_DENSITY = GL10.GL_FOG_DENSITY;

	public static final int GL_FOG_START = GL10.GL_FOG_START;

	public static final int GL_FOG_END = GL10.GL_FOG_END;

	public static final int GL_FOG_MODE = GL10.GL_FOG_MODE;

	public static final int GL_FOG_COLOR = GL10.GL_FOG_COLOR;

	public static final int GL_CW = GL10.GL_CW;

	public static final int GL_CCW = GL10.GL_CCW;

	public static final int GL_SMOOTH_POINT_SIZE_RANGE = GL10.GL_SMOOTH_POINT_SIZE_RANGE;

	public static final int GL_SMOOTH_LINE_WIDTH_RANGE = GL10.GL_SMOOTH_LINE_WIDTH_RANGE;

	public static final int GL_ALIASED_POINT_SIZE_RANGE = GL10.GL_ALIASED_POINT_SIZE_RANGE;

	public static final int GL_ALIASED_LINE_WIDTH_RANGE = GL10.GL_ALIASED_LINE_WIDTH_RANGE;

	public static final int GL_IMPLEMENTATION_COLOR_READ_TYPE_OES = GL10.GL_IMPLEMENTATION_COLOR_READ_TYPE_OES;

	public static final int GL_IMPLEMENTATION_COLOR_READ_FORMAT_OES = GL10.GL_IMPLEMENTATION_COLOR_READ_FORMAT_OES;

	public static final int GL_MAX_LIGHTS = GL10.GL_MAX_LIGHTS;

	public static final int GL_MAX_TEXTURE_SIZE = GL10.GL_MAX_TEXTURE_SIZE;

	public static final int GL_MAX_MODELVIEW_STACK_DEPTH = GL10.GL_MAX_MODELVIEW_STACK_DEPTH;

	public static final int GL_MAX_PROJECTION_STACK_DEPTH = GL10.GL_MAX_PROJECTION_STACK_DEPTH;

	public static final int GL_MAX_TEXTURE_STACK_DEPTH = GL10.GL_MAX_TEXTURE_STACK_DEPTH;

	public static final int GL_MAX_VIEWPORT_DIMS = GL10.GL_MAX_VIEWPORT_DIMS;

	public static final int GL_MAX_ELEMENTS_VERTICES = GL10.GL_MAX_ELEMENTS_VERTICES;

	public static final int GL_MAX_ELEMENTS_INDICES = GL10.GL_MAX_ELEMENTS_INDICES;

	public static final int GL_MAX_TEXTURE_UNITS = GL10.GL_MAX_TEXTURE_UNITS;

	public static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = GL10.GL_NUM_COMPRESSED_TEXTURE_FORMATS;

	public static final int GL_COMPRESSED_TEXTURE_FORMATS = GL10.GL_COMPRESSED_TEXTURE_FORMATS;

	public static final int GL_SUBPIXEL_BITS = GL10.GL_SUBPIXEL_BITS;

	public static final int GL_RED_BITS = GL10.GL_RED_BITS;

	public static final int GL_GREEN_BITS = GL10.GL_GREEN_BITS;

	public static final int GL_BLUE_BITS = GL10.GL_BLUE_BITS;

	public static final int GL_ALPHA_BITS = GL10.GL_ALPHA_BITS;

	public static final int GL_DEPTH_BITS = GL10.GL_DEPTH_BITS;

	public static final int GL_STENCIL_BITS = GL10.GL_STENCIL_BITS;

	public static final int GL_DONT_CARE = GL10.GL_DONT_CARE;

	public static final int GL_FASTEST = GL10.GL_FASTEST;

	public static final int GL_NICEST = GL10.GL_NICEST;

	public static final int GL_PERSPECTIVE_CORRECTION_HINT = GL10.GL_PERSPECTIVE_CORRECTION_HINT;

	public static final int GL_POINT_SMOOTH_HINT = GL10.GL_POINT_SMOOTH_HINT;

	public static final int GL_LINE_SMOOTH_HINT = GL10.GL_LINE_SMOOTH_HINT;

	public static final int GL_POLYGON_SMOOTH_HINT = GL10.GL_POLYGON_SMOOTH_HINT;

	public static final int GL_FOG_HINT = GL10.GL_FOG_HINT;

	public static final int GL_LIGHT_MODEL_AMBIENT = GL10.GL_LIGHT_MODEL_AMBIENT;

	public static final int GL_LIGHT_MODEL_TWO_SIDE = GL10.GL_LIGHT_MODEL_TWO_SIDE;

	public static final int GL_AMBIENT = GL10.GL_AMBIENT;

	public static final int GL_DIFFUSE = GL10.GL_DIFFUSE;

	public static final int GL_SPECULAR = GL10.GL_SPECULAR;

	public static final int GL_POSITION = GL10.GL_POSITION;

	public static final int GL_SPOT_DIRECTION = GL10.GL_SPOT_DIRECTION;

	public static final int GL_SPOT_EXPONENT = GL10.GL_SPOT_EXPONENT;

	public static final int GL_SPOT_CUTOFF = GL10.GL_SPOT_CUTOFF;

	public static final int GL_CONSTANT_ATTENUATION = GL10.GL_CONSTANT_ATTENUATION;

	public static final int GL_LINEAR_ATTENUATION = GL10.GL_LINEAR_ATTENUATION;

	public static final int GL_QUADRATIC_ATTENUATION = GL10.GL_QUADRATIC_ATTENUATION;

	public static final int GL_BYTE = GL10.GL_BYTE;

	public static final int GL_UNSIGNED_BYTE = GL10.GL_UNSIGNED_BYTE;

	public static final int GL_SHORT = GL10.GL_SHORT;

	public static final int GL_UNSIGNED_SHORT = GL10.GL_UNSIGNED_SHORT;

	public static final int GL_FLOAT = GL10.GL_FLOAT;

	public static final int GL_FIXED = GL10.GL_FIXED;

	public static final int GL_CLEAR = GL10.GL_CLEAR;

	public static final int GL_AND = GL10.GL_AND;

	public static final int GL_AND_REVERSE = GL10.GL_AND_REVERSE;

	public static final int GL_COPY = GL10.GL_COPY;

	public static final int GL_AND_INVERTED = GL10.GL_AND_INVERTED;

	public static final int GL_NOOP = GL10.GL_NOOP;

	public static final int GL_XOR = GL10.GL_XOR;

	public static final int GL_OR =  GL10.GL_OR;

	public static final int GL_NOR =  GL10.GL_NOR;

	public static final int GL_EQUIV =  GL10.GL_EQUIV;

	public static final int GL_INVERT =  GL10.GL_INVERT;

	public static final int GL_OR_REVERSE =  GL10.GL_OR_REVERSE;

	public static final int GL_COPY_INVERTED =  GL10.GL_COPY_INVERTED;

	public static final int GL_OR_INVERTED =  GL10.GL_OR_INVERTED;

	public static final int GL_NAND =  GL10.GL_NAND;

	public static final int GL_SET =  GL10.GL_SET;

	public static final int GL_EMISSION =  GL10.GL_EMISSION;

	public static final int GL_SHININESS =  GL10.GL_SHININESS;

	public static final int GL_AMBIENT_AND_DIFFUSE =  GL10.GL_AMBIENT_AND_DIFFUSE;

	public static final int GL_MODELVIEW =  GL10.GL_MODELVIEW;

	public static final int GL_PROJECTION =  GL10.GL_PROJECTION;

	public static final int GL_TEXTURE =  GL10.GL_TEXTURE;

	public static final int GL_ALPHA =  GL10.GL_ALPHA;

	public static final int GL_RGB =  GL10.GL_RGB;

	public static final int GL_RGBA =  GL10.GL_RGBA;

	public static final int GL_LUMINANCE =  GL10.GL_LUMINANCE;

	public static final int GL_LUMINANCE_ALPHA =  GL10.GL_LUMINANCE_ALPHA;

	public static final int GL_UNPACK_ALIGNMENT =  GL10.GL_UNPACK_ALIGNMENT;

	public static final int GL_PACK_ALIGNMENT =  GL10.GL_PACK_ALIGNMENT;

	public static final int GL_UNSIGNED_SHORT_4_4_4_4 =  GL10.GL_UNSIGNED_SHORT_4_4_4_4;

	public static final int GL_UNSIGNED_SHORT_5_5_5_1 =  GL10.GL_UNSIGNED_SHORT_5_5_5_1;

	public static final int GL_UNSIGNED_SHORT_5_6_5 =  GL10.GL_UNSIGNED_SHORT_5_6_5;

	public static final int GL_FLAT =  GL10.GL_FLAT;

	public static final int GL_SMOOTH =  GL10.GL_SMOOTH;

	public static final int GL_KEEP =  GL10.GL_KEEP;

	public static final int GL_REPLACE =  GL10.GL_REPLACE;

	public static final int GL_INCR =  GL10.GL_INCR;

	public static final int GL_DECR =  GL10.GL_DECR;

	public static final int GL_VENDOR =  GL10.GL_VENDOR;

	public static final int GL_RENDERER =  GL10.GL_RENDERER;

	public static final int GL_VERSION =  GL10.GL_VERSION;

	public static final int GL_EXTENSIONS =  GL10.GL_EXTENSIONS;

	public static final int GL_MODULATE =  GL10.GL_MODULATE;

	public static final int GL_DECAL =  GL10.GL_DECAL;

	public static final int GL_ADD =  GL10.GL_ADD;

	public static final int GL_TEXTURE_ENV_MODE =  GL10.GL_TEXTURE_ENV_MODE;

	public static final int GL_TEXTURE_ENV_COLOR =  GL10.GL_TEXTURE_ENV_COLOR;

	public static final int GL_TEXTURE_ENV =  GL10.GL_TEXTURE_ENV;

	public static final int GL_NEAREST =  GL10.GL_NEAREST;

	public static final int GL_LINEAR =  GL10.GL_LINEAR;

	public static final int GL_NEAREST_MIPMAP_NEAREST =  GL10.GL_NEAREST_MIPMAP_NEAREST;

	public static final int GL_LINEAR_MIPMAP_NEAREST =  GL10.GL_LINEAR_MIPMAP_NEAREST;

	public static final int GL_NEAREST_MIPMAP_LINEAR =  GL10.GL_NEAREST_MIPMAP_LINEAR;

	public static final int GL_LINEAR_MIPMAP_LINEAR =  GL10.GL_LINEAR_MIPMAP_LINEAR;

	public static final int GL_TEXTURE_MAG_FILTER =  GL10.GL_TEXTURE_MAG_FILTER;

	public static final int GL_TEXTURE_MIN_FILTER =  GL10.GL_TEXTURE_MIN_FILTER;

	public static final int GL_TEXTURE_WRAP_S =  GL10.GL_TEXTURE_WRAP_S;

	public static final int GL_TEXTURE_WRAP_T =  GL10.GL_TEXTURE_WRAP_T;

	public static final int GL_TEXTURE0 =  GL10.GL_TEXTURE0;

	public static final int GL_TEXTURE1 =  GL10.GL_TEXTURE1;

	public static final int GL_TEXTURE2 =  GL10.GL_TEXTURE2;

	public static final int GL_TEXTURE3 =  GL10.GL_TEXTURE3;

	public static final int GL_TEXTURE4 =  GL10.GL_TEXTURE4;

	public static final int GL_TEXTURE5 =  GL10.GL_TEXTURE5;

	public static final int GL_TEXTURE6 =  GL10.GL_TEXTURE6;

	public static final int GL_TEXTURE7 =  GL10.GL_TEXTURE7;

	public static final int GL_TEXTURE8 =  GL10.GL_TEXTURE8;

	public static final int GL_TEXTURE9 =  GL10.GL_TEXTURE9;

	public static final int GL_TEXTURE10 =  GL10.GL_TEXTURE10;

	public static final int GL_TEXTURE11 =  GL10.GL_TEXTURE11;

	public static final int GL_TEXTURE12 =  GL10.GL_TEXTURE12;

	public static final int GL_TEXTURE13 =  GL10.GL_TEXTURE13;

	public static final int GL_TEXTURE14 =  GL10.GL_TEXTURE14;

	public static final int GL_TEXTURE15 =  GL10.GL_TEXTURE15;

	public static final int GL_TEXTURE16 =  GL10.GL_TEXTURE16;

	public static final int GL_TEXTURE17 =  GL10.GL_TEXTURE17;

	public static final int GL_TEXTURE18 =  GL10.GL_TEXTURE18;

	public static final int GL_TEXTURE19 =  GL10.GL_TEXTURE19;

	public static final int GL_TEXTURE20 =  GL10.GL_TEXTURE20;

	public static final int GL_TEXTURE21 =  GL10.GL_TEXTURE21;

	public static final int GL_TEXTURE22 =  GL10.GL_TEXTURE22;

	public static final int GL_TEXTURE23 =  GL10.GL_TEXTURE23;

	public static final int GL_TEXTURE24 =  GL10.GL_TEXTURE24;

	public static final int GL_TEXTURE25 =  GL10.GL_TEXTURE25;

	public static final int GL_TEXTURE26 =  GL10.GL_TEXTURE26;

	public static final int GL_TEXTURE27 =  GL10.GL_TEXTURE27;

	public static final int GL_TEXTURE28 =  GL10.GL_TEXTURE28;

	public static final int GL_TEXTURE29 =  GL10.GL_TEXTURE29;

	public static final int GL_TEXTURE30 =  GL10.GL_TEXTURE30;

	public static final int GL_TEXTURE31 =  GL10.GL_TEXTURE31;

	public static final int GL_REPEAT =  GL10.GL_REPEAT;

	public static final int GL_CLAMP_TO_EDGE =  GL10.GL_CLAMP_TO_EDGE;

	public static final int GL_PALETTE4_RGB8_OES =  GL10.GL_PALETTE4_RGB8_OES;

	public static final int GL_PALETTE4_RGBA8_OES =  GL10.GL_PALETTE4_RGBA8_OES;

	public static final int GL_PALETTE4_R5_G6_B5_OES =  GL10.GL_PALETTE4_R5_G6_B5_OES;

	public static final int GL_PALETTE4_RGBA4_OES =  GL10.GL_PALETTE4_RGBA4_OES;

	public static final int GL_PALETTE4_RGB5_A1_OES =  GL10.GL_PALETTE4_RGB5_A1_OES;

	public static final int GL_PALETTE8_RGB8_OES =  GL10.GL_PALETTE8_RGB8_OES;

	public static final int GL_PALETTE8_RGBA8_OES =  GL10.GL_PALETTE8_RGBA8_OES;

	public static final int GL_PALETTE8_R5_G6_B5_OES =  GL10.GL_PALETTE8_R5_G6_B5_OES;

	public static final int GL_PALETTE8_RGBA4_OES =  GL10.GL_PALETTE8_RGBA4_OES;

	public static final int GL_PALETTE8_RGB5_A1_OES =  GL10.GL_PALETTE8_RGB5_A1_OES;

	public static final int GL_LIGHT0 =  GL10.GL_LIGHT0;

	public static final int GL_LIGHT1 =  GL10.GL_LIGHT1;

	public static final int GL_LIGHT2 =  GL10.GL_LIGHT2;

	public static final int GL_LIGHT3 =  GL10.GL_LIGHT3;

	public static final int GL_LIGHT4 =  GL10.GL_LIGHT4;

	public static final int GL_LIGHT5 =  GL10.GL_LIGHT5;

	public static final int GL_LIGHT6 =  GL10.GL_LIGHT6;

	public static final int GL_LIGHT7 =  GL10.GL_LIGHT7;
}
