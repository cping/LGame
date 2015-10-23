/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import loon.LSystem;
import loon.Support;

/**
 * Interface and values for OpenGL ES 2.0, based on the official JOGL GL2ES2
 * interface.
 */
public abstract class GL20 {

	public static final int GL_ACTIVE_TEXTURE = 0x84E0;
	public static final int GL_DEPTH_BUFFER_BIT = 0x00000100;
	public static final int GL_STENCIL_BUFFER_BIT = 0x00000400;
	public static final int GL_COLOR_BUFFER_BIT = 0x00004000;
	public static final int GL_FALSE = 0;
	public static final int GL_TRUE = 1;
	public static final int GL_POINTS = 0x0000;
	public static final int GL_LINES = 0x0001;
	public static final int GL_LINE_LOOP = 0x0002;
	public static final int GL_LINE_STRIP = 0x0003;
	public static final int GL_TRIANGLES = 0x0004;
	public static final int GL_TRIANGLE_STRIP = 0x0005;
	public static final int GL_TRIANGLE_FAN = 0x0006;
	public static final int GL_ZERO = 0;
	public static final int GL_ONE = 1;
	public static final int GL_SRC_COLOR = 0x0300;
	public static final int GL_ONE_MINUS_SRC_COLOR = 0x0301;
	public static final int GL_SRC_ALPHA = 0x0302;
	public static final int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
	public static final int GL_DST_ALPHA = 0x0304;
	public static final int GL_ONE_MINUS_DST_ALPHA = 0x0305;
	public static final int GL_DST_COLOR = 0x0306;
	public static final int GL_ONE_MINUS_DST_COLOR = 0x0307;
	public static final int GL_SRC_ALPHA_SATURATE = 0x0308;
	public static final int GL_FUNC_ADD = 0x8006;
	public static final int GL_BLEND_EQUATION = 0x8009;
	public static final int GL_BLEND_EQUATION_RGB = 0x8009; /* == BLEND_EQUATION */
	public static final int GL_BLEND_EQUATION_ALPHA = 0x883D;
	public static final int GL_FUNC_SUBTRACT = 0x800A;
	public static final int GL_FUNC_REVERSE_SUBTRACT = 0x800B;
	public static final int GL_BLEND_DST_RGB = 0x80C8;
	public static final int GL_BLEND_SRC_RGB = 0x80C9;
	public static final int GL_BLEND_DST_ALPHA = 0x80CA;
	public static final int GL_BLEND_SRC_ALPHA = 0x80CB;
	public static final int GL_CONSTANT_COLOR = 0x8001;
	public static final int GL_ONE_MINUS_CONSTANT_COLOR = 0x8002;
	public static final int GL_CONSTANT_ALPHA = 0x8003;
	public static final int GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004;
	public static final int GL_BLEND_COLOR = 0x8005;
	public static final int GL_ARRAY_BUFFER = 0x8892;
	public static final int GL_ELEMENT_ARRAY_BUFFER = 0x8893;
	public static final int GL_ARRAY_BUFFER_BINDING = 0x8894;
	public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 0x8895;
	public static final int GL_STREAM_DRAW = 0x88E0;
	public static final int GL_STATIC_DRAW = 0x88E4;
	public static final int GL_DYNAMIC_DRAW = 0x88E8;
	public static final int GL_BUFFER_SIZE = 0x8764;
	public static final int GL_BUFFER_USAGE = 0x8765;
	public static final int GL_CURRENT_VERTEX_ATTRIB = 0x8626;
	public static final int GL_FRONT = 0x0404;
	public static final int GL_BACK = 0x0405;
	public static final int GL_FRONT_AND_BACK = 0x0408;
	public static final int GL_TEXTURE_2D = 0x0DE1;
	public static final int GL_CULL_FACE = 0x0B44;
	public static final int GL_BLEND = 0x0BE2;
	public static final int GL_DITHER = 0x0BD0;
	public static final int GL_STENCIL_TEST = 0x0B90;
	public static final int GL_DEPTH_TEST = 0x0B71;
	public static final int GL_SCISSOR_TEST = 0x0C11;
	public static final int GL_POLYGON_OFFSET_FILL = 0x8037;
	public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 0x809E;
	public static final int GL_SAMPLE_COVERAGE = 0x80A0;
	public static final int GL_NO_ERROR = 0;
	public static final int GL_INVALID_ENUM = 0x0500;
	public static final int GL_INVALID_VALUE = 0x0501;
	public static final int GL_INVALID_OPERATION = 0x0502;
	public static final int GL_OUT_OF_MEMORY = 0x0505;
	public static final int GL_CW = 0x0900;
	public static final int GL_CCW = 0x0901;
	public static final int GL_LINE_WIDTH = 0x0B21;
	public static final int GL_ALIASED_POINT_SIZE_RANGE = 0x846D;
	public static final int GL_ALIASED_LINE_WIDTH_RANGE = 0x846E;
	public static final int GL_CULL_FACE_MODE = 0x0B45;
	public static final int GL_FRONT_FACE = 0x0B46;
	public static final int GL_DEPTH_RANGE = 0x0B70;
	public static final int GL_DEPTH_WRITEMASK = 0x0B72;
	public static final int GL_DEPTH_CLEAR_VALUE = 0x0B73;
	public static final int GL_DEPTH_FUNC = 0x0B74;
	public static final int GL_STENCIL_CLEAR_VALUE = 0x0B91;
	public static final int GL_STENCIL_FUNC = 0x0B92;
	public static final int GL_STENCIL_FAIL = 0x0B94;
	public static final int GL_STENCIL_PASS_DEPTH_FAIL = 0x0B95;
	public static final int GL_STENCIL_PASS_DEPTH_PASS = 0x0B96;
	public static final int GL_STENCIL_REF = 0x0B97;
	public static final int GL_STENCIL_VALUE_MASK = 0x0B93;
	public static final int GL_STENCIL_WRITEMASK = 0x0B98;
	public static final int GL_STENCIL_BACK_FUNC = 0x8800;
	public static final int GL_STENCIL_BACK_FAIL = 0x8801;
	public static final int GL_STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802;
	public static final int GL_STENCIL_BACK_PASS_DEPTH_PASS = 0x8803;
	public static final int GL_STENCIL_BACK_REF = 0x8CA3;
	public static final int GL_STENCIL_BACK_VALUE_MASK = 0x8CA4;
	public static final int GL_STENCIL_BACK_WRITEMASK = 0x8CA5;
	public static final int GL_VIEWPORT = 0x0BA2;
	public static final int GL_SCISSOR_BOX = 0x0C10;
	public static final int GL_COLOR_CLEAR_VALUE = 0x0C22;
	public static final int GL_COLOR_WRITEMASK = 0x0C23;
	public static final int GL_UNPACK_ALIGNMENT = 0x0CF5;
	public static final int GL_PACK_ALIGNMENT = 0x0D05;
	public static final int GL_MAX_TEXTURE_SIZE = 0x0D33;
	public static final int GL_MAX_VIEWPORT_DIMS = 0x0D3A;
	public static final int GL_SUBPIXEL_BITS = 0x0D50;
	public static final int GL_RED_BITS = 0x0D52;
	public static final int GL_GREEN_BITS = 0x0D53;
	public static final int GL_BLUE_BITS = 0x0D54;
	public static final int GL_ALPHA_BITS = 0x0D55;
	public static final int GL_DEPTH_BITS = 0x0D56;
	public static final int GL_STENCIL_BITS = 0x0D57;
	public static final int GL_POLYGON_OFFSET_UNITS = 0x2A00;
	public static final int GL_POLYGON_OFFSET_FACTOR = 0x8038;
	public static final int GL_TEXTURE_BINDING_2D = 0x8069;
	public static final int GL_SAMPLE_BUFFERS = 0x80A8;
	public static final int GL_SAMPLES = 0x80A9;
	public static final int GL_SAMPLE_COVERAGE_VALUE = 0x80AA;
	public static final int GL_SAMPLE_COVERAGE_INVERT = 0x80AB;
	public static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2;
	public static final int GL_COMPRESSED_TEXTURE_FORMATS = 0x86A3;
	public static final int GL_DONT_CARE = 0x1100;
	public static final int GL_FASTEST = 0x1101;
	public static final int GL_NICEST = 0x1102;
	public static final int GL_GENERATE_MIPMAP_HINT = 0x8192;
	public static final int GL_BYTE = 0x1400;
	public static final int GL_UNSIGNED_BYTE = 0x1401;
	public static final int GL_SHORT = 0x1402;
	public static final int GL_UNSIGNED_SHORT = 0x1403;
	public static final int GL_INT = 0x1404;
	public static final int GL_UNSIGNED_INT = 0x1405;
	public static final int GL_FLOAT = 0x1406;
	public static final int GL_FIXED = 0x140C;
	public static final int GL_DEPTH_COMPONENT = 0x1902;
	public static final int GL_ALPHA = 0x1906;
	public static final int GL_RGB = 0x1907;
	public static final int GL_RGBA = 0x1908;
	public static final int GL_BGRA = 0x80E1;
	public static final int GL_LUMINANCE = 0x1909;
	public static final int GL_LUMINANCE_ALPHA = 0x190A;
	public static final int GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033;
	public static final int GL_UNSIGNED_SHORT_5_5_5_1 = 0x8034;
	public static final int GL_UNSIGNED_SHORT_5_6_5 = 0x8363;
	public static final int GL_UNSIGNED_INT_8_8_8_8 = 0x8035;
	public static final int GL_UNSIGNED_INT_8_8_8_8_REV = 0x8367;
	public static final int GL_FRAGMENT_SHADER = 0x8B30;
	public static final int GL_VERTEX_SHADER = 0x8B31;
	public static final int GL_MAX_VERTEX_ATTRIBS = 0x8869;
	public static final int GL_MAX_VERTEX_UNIFORM_VECTORS = 0x8DFB;
	public static final int GL_MAX_VARYING_VECTORS = 0x8DFC;
	public static final int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;
	public static final int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C;
	public static final int GL_MAX_TEXTURE_IMAGE_UNITS = 0x8872;
	public static final int GL_MAX_FRAGMENT_UNIFORM_VECTORS = 0x8DFD;
	public static final int GL_SHADER_TYPE = 0x8B4F;
	public static final int GL_DELETE_STATUS = 0x8B80;
	public static final int GL_LINK_STATUS = 0x8B82;
	public static final int GL_VALIDATE_STATUS = 0x8B83;
	public static final int GL_ATTACHED_SHADERS = 0x8B85;
	public static final int GL_ACTIVE_UNIFORMS = 0x8B86;
	public static final int GL_ACTIVE_UNIFORM_MAX_LENGTH = 0x8B87;
	public static final int GL_ACTIVE_ATTRIBUTES = 0x8B89;
	public static final int GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 0x8B8A;
	public static final int GL_SHADING_LANGUAGE_VERSION = 0x8B8C;
	public static final int GL_CURRENT_PROGRAM = 0x8B8D;
	public static final int GL_NEVER = 0x0200;
	public static final int GL_LESS = 0x0201;
	public static final int GL_EQUAL = 0x0202;
	public static final int GL_LEQUAL = 0x0203;
	public static final int GL_GREATER = 0x0204;
	public static final int GL_NOTEQUAL = 0x0205;
	public static final int GL_GEQUAL = 0x0206;
	public static final int GL_ALWAYS = 0x0207;
	public static final int GL_KEEP = 0x1E00;
	public static final int GL_REPLACE = 0x1E01;
	public static final int GL_INCR = 0x1E02;
	public static final int GL_DECR = 0x1E03;
	public static final int GL_INVERT = 0x150A;
	public static final int GL_INCR_WRAP = 0x8507;
	public static final int GL_DECR_WRAP = 0x8508;
	public static final int GL_VENDOR = 0x1F00;
	public static final int GL_RENDERER = 0x1F01;
	public static final int GL_VERSION = 0x1F02;
	public static final int GL_EXTENSIONS = 0x1F03;
	public static final int GL_NEAREST = 0x2600;
	public static final int GL_LINEAR = 0x2601;
	public static final int GL_NEAREST_MIPMAP_NEAREST = 0x2700;
	public static final int GL_LINEAR_MIPMAP_NEAREST = 0x2701;
	public static final int GL_NEAREST_MIPMAP_LINEAR = 0x2702;
	public static final int GL_LINEAR_MIPMAP_LINEAR = 0x2703;
	public static final int GL_TEXTURE_MAG_FILTER = 0x2800;
	public static final int GL_TEXTURE_MIN_FILTER = 0x2801;
	public static final int GL_TEXTURE_WRAP_S = 0x2802;
	public static final int GL_TEXTURE_WRAP_T = 0x2803;
	public static final int GL_TEXTURE = 0x1702;
	public static final int GL_TEXTURE_CUBE_MAP = 0x8513;
	public static final int GL_TEXTURE_BINDING_CUBE_MAP = 0x8514;
	public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515;
	public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516;
	public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517;
	public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518;
	public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519;
	public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A;
	public static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C;
	public static final int GL_TEXTURE0 = 0x84C0;
	public static final int GL_TEXTURE1 = 0x84C1;
	public static final int GL_TEXTURE2 = 0x84C2;
	public static final int GL_TEXTURE3 = 0x84C3;
	public static final int GL_TEXTURE4 = 0x84C4;
	public static final int GL_TEXTURE5 = 0x84C5;
	public static final int GL_TEXTURE6 = 0x84C6;
	public static final int GL_TEXTURE7 = 0x84C7;
	public static final int GL_TEXTURE8 = 0x84C8;
	public static final int GL_TEXTURE9 = 0x84C9;
	public static final int GL_TEXTURE10 = 0x84CA;
	public static final int GL_TEXTURE11 = 0x84CB;
	public static final int GL_TEXTURE12 = 0x84CC;
	public static final int GL_TEXTURE13 = 0x84CD;
	public static final int GL_TEXTURE14 = 0x84CE;
	public static final int GL_TEXTURE15 = 0x84CF;
	public static final int GL_TEXTURE16 = 0x84D0;
	public static final int GL_TEXTURE17 = 0x84D1;
	public static final int GL_TEXTURE18 = 0x84D2;
	public static final int GL_TEXTURE19 = 0x84D3;
	public static final int GL_TEXTURE20 = 0x84D4;
	public static final int GL_TEXTURE21 = 0x84D5;
	public static final int GL_TEXTURE22 = 0x84D6;
	public static final int GL_TEXTURE23 = 0x84D7;
	public static final int GL_TEXTURE24 = 0x84D8;
	public static final int GL_TEXTURE25 = 0x84D9;
	public static final int GL_TEXTURE26 = 0x84DA;
	public static final int GL_TEXTURE27 = 0x84DB;
	public static final int GL_TEXTURE28 = 0x84DC;
	public static final int GL_TEXTURE29 = 0x84DD;
	public static final int GL_TEXTURE30 = 0x84DE;
	public static final int GL_TEXTURE31 = 0x84DF;
	public static final int GL_REPEAT = 0x2901;
	public static final int GL_CLAMP_TO_EDGE = 0x812F;
	public static final int GL_MIRRORED_REPEAT = 0x8370;
	public static final int GL_FLOAT_VEC2 = 0x8B50;
	public static final int GL_FLOAT_VEC3 = 0x8B51;
	public static final int GL_FLOAT_VEC4 = 0x8B52;
	public static final int GL_INT_VEC2 = 0x8B53;
	public static final int GL_INT_VEC3 = 0x8B54;
	public static final int GL_INT_VEC4 = 0x8B55;
	public static final int GL_BOOL = 0x8B56;
	public static final int GL_BOOL_VEC2 = 0x8B57;
	public static final int GL_BOOL_VEC3 = 0x8B58;
	public static final int GL_BOOL_VEC4 = 0x8B59;
	public static final int GL_FLOAT_MAT2 = 0x8B5A;
	public static final int GL_FLOAT_MAT3 = 0x8B5B;
	public static final int GL_FLOAT_MAT4 = 0x8B5C;
	public static final int GL_SAMPLER_2D = 0x8B5E;
	public static final int GL_SAMPLER_CUBE = 0x8B60;
	public static final int GL_VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622;
	public static final int GL_VERTEX_ATTRIB_ARRAY_SIZE = 0x8623;
	public static final int GL_VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624;
	public static final int GL_VERTEX_ATTRIB_ARRAY_TYPE = 0x8625;
	public static final int GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A;
	public static final int GL_VERTEX_ATTRIB_ARRAY_POINTER = 0x8645;
	public static final int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F;
	public static final int GL_IMPLEMENTATION_COLOR_READ_TYPE = 0x8B9A;
	public static final int GL_IMPLEMENTATION_COLOR_READ_FORMAT = 0x8B9B;
	public static final int GL_COMPILE_STATUS = 0x8B81;
	public static final int GL_INFO_LOG_LENGTH = 0x8B84;
	public static final int GL_SHADER_SOURCE_LENGTH = 0x8B88;
	public static final int GL_SHADER_COMPILER = 0x8DFA;
	public static final int GL_SHADER_BINARY_FORMATS = 0x8DF8;
	public static final int GL_NUM_SHADER_BINARY_FORMATS = 0x8DF9;
	public static final int GL_LOW_FLOAT = 0x8DF0;
	public static final int GL_MEDIUM_FLOAT = 0x8DF1;
	public static final int GL_HIGH_FLOAT = 0x8DF2;
	public static final int GL_LOW_INT = 0x8DF3;
	public static final int GL_MEDIUM_INT = 0x8DF4;
	public static final int GL_HIGH_INT = 0x8DF5;
	public static final int GL_FRAMEBUFFER = 0x8D40;
	public static final int GL_RENDERBUFFER = 0x8D41;
	public static final int GL_RGBA4 = 0x8056;
	public static final int GL_RGB5_A1 = 0x8057;
	public static final int GL_RGB565 = 0x8D62;
	public static final int GL_DEPTH_COMPONENT16 = 0x81A5;
	public static final int GL_STENCIL_INDEX = 0x1901;
	public static final int GL_STENCIL_INDEX8 = 0x8D48;
	public static final int GL_RENDERBUFFER_WIDTH = 0x8D42;
	public static final int GL_RENDERBUFFER_HEIGHT = 0x8D43;
	public static final int GL_RENDERBUFFER_INTERNAL_FORMAT = 0x8D44;
	public static final int GL_RENDERBUFFER_RED_SIZE = 0x8D50;
	public static final int GL_RENDERBUFFER_GREEN_SIZE = 0x8D51;
	public static final int GL_RENDERBUFFER_BLUE_SIZE = 0x8D52;
	public static final int GL_RENDERBUFFER_ALPHA_SIZE = 0x8D53;
	public static final int GL_RENDERBUFFER_DEPTH_SIZE = 0x8D54;
	public static final int GL_RENDERBUFFER_STENCIL_SIZE = 0x8D55;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 0x8CD0;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 0x8CD1;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 0x8CD2;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3;
	public static final int GL_COLOR_ATTACHMENT0 = 0x8CE0;
	public static final int GL_DEPTH_ATTACHMENT = 0x8D00;
	public static final int GL_STENCIL_ATTACHMENT = 0x8D20;
	public static final int GL_NONE = 0;
	public static final int GL_FRAMEBUFFER_COMPLETE = 0x8CD5;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9;
	public static final int GL_FRAMEBUFFER_UNSUPPORTED = 0x8CDD;
	public static final int GL_FRAMEBUFFER_BINDING = 0x8CA6;
	public static final int GL_RENDERBUFFER_BINDING = 0x8CA7;
	public static final int GL_MAX_RENDERBUFFER_SIZE = 0x84E8;
	public static final int GL_INVALID_FRAMEBUFFER_OPERATION = 0x0506;

	public static abstract class Buffers {

		public ByteBuffer byteBuffer = createByteBuffer(256);
		public ShortBuffer shortBuffer = createShortBuffer(32);
		public FloatBuffer floatBuffer = createFloatBuffer(32);
		public IntBuffer intBuffer = createIntBuffer(32);
		private final static int limit = 32;

		public void setByteBuffer(byte[] source, int offset, int length) {
			int size = length - offset;
			resizeByteBuffer(size);
			Support support = LSystem.base().support();
			if (support.isNative() && size > limit) {
				support.copy(source, offset, byteBuffer, length);
			} else {
				byteBuffer.put(source, offset, length);
				byteBuffer.rewind();
			}
		}

		public void setShortBuffer(short[] source, int offset, int length) {
			int size = length - offset;
			resizeShortBuffer(size);
			Support support = LSystem.base().support();
			if (support.isNative() && size > limit) {
				support.copy(source, offset, shortBuffer, length);
			} else {
				shortBuffer.put(source, offset, length);
				shortBuffer.rewind();
			}
		}

		public void setIntBuffer(int[] source, int offset, int length) {
			int size = length - offset;
			resizeIntBuffer(size);
			Support support = LSystem.base().support();
			if (support.isNative() && size > limit) {
				support.copy(source, offset, intBuffer, length);
			} else {
				intBuffer.put(source, offset, length);
				intBuffer.rewind();
			}
		}

		public void setFloatBuffer(float[] source, int offset, int length) {
			int size = length - offset;
			resizeFloatBuffer(size);
			Support support = LSystem.base().support();
			if (support.isNative() && size > limit) {
				support.copy(source, offset, floatBuffer, length);
			} else {
				floatBuffer.put(source, offset, length);
				floatBuffer.rewind();
			}
		}

		public void setShortBuffer(short n) {
			shortBuffer.position(0);
			shortBuffer.put(n);
			shortBuffer.rewind();
		}

		public void setIntBuffer(int n) {
			intBuffer.position(0);
			intBuffer.put(n);
			intBuffer.rewind();
		}

		public void setFloatBuffer(float n) {
			floatBuffer.position(0);
			floatBuffer.put(n);
			floatBuffer.rewind();
		}

		public void resizeByteBuffer(int length) {
			int cap = byteBuffer.capacity();
			if (cap < length) {
				byteBuffer = createByteBuffer(newCap(cap, length));
			} else {
				byteBuffer.position(0);
			}
			byteBuffer.limit(length);
		}

		public void resizeShortBuffer(int length) {
			int cap = shortBuffer.capacity();
			if (cap < length) {
				shortBuffer = createShortBuffer(newCap(cap, length));
			} else {
				shortBuffer.position(0);
			}
			shortBuffer.limit(length);
		}

		public void resizeIntBuffer(int length) {
			int cap = intBuffer.capacity();
			if (cap < length) {
				intBuffer = createIntBuffer(newCap(cap, length));
			} else {
				intBuffer.position(0);
			}
			intBuffer.limit(length);
		}

		public void resizeFloatBuffer(int length) {
			int cap = floatBuffer.capacity();
			if (cap < length) {
				floatBuffer = createFloatBuffer(newCap(cap, length));
			} else {
				floatBuffer.position(0);
			}
			floatBuffer.limit(length);
		}

		public abstract ByteBuffer createByteBuffer(int size);

		public ShortBuffer createShortBuffer(int size) {
			return createByteBuffer(size * 2).asShortBuffer();
		}

		public IntBuffer createIntBuffer(int size) {
			return createByteBuffer(size * 4).asIntBuffer();
		}

		public FloatBuffer createFloatBuffer(int size) {
			return createByteBuffer(size * 4).asFloatBuffer();
		}

		private int newCap(int cap, int length) {
			int newLength = cap << 1;
			while (newLength < length) {
				newLength <<= 1;
			}
			return newLength;
		}
	}

	public final Buffers bufs;
	public final boolean checkErrors;

	protected GL20(Buffers buffers, boolean checkErrors) {
		this.bufs = buffers;
		this.checkErrors = checkErrors;
	}

	public boolean checkError(String op) {
		int reported = 0;
		if (checkErrors) {
			int error;
			while ((error = glGetError()) != GL_NO_ERROR) {
				reported += 1;
				System.err.println(op + ": glError " + error);
			}
		}
		return reported > 0;
	}

	public void glDeleteBuffer(int id) {
		bufs.setIntBuffer(id);
		glDeleteBuffers(1, bufs.intBuffer);
	}

	public void glDeleteBuffers(int n, int[] buffers, int offset) {
		bufs.setIntBuffer(buffers, offset, n);
		glDeleteBuffers(n, bufs.intBuffer);
	}

	public void glDeleteFramebuffer(int id) {
		bufs.setIntBuffer(id);
		glDeleteFramebuffers(1, bufs.intBuffer);
	}

	public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
		bufs.setIntBuffer(framebuffers, offset, n);
		glDeleteFramebuffers(n, bufs.intBuffer);
	}

	public void glDeleteRenderbuffer(int id) {
		bufs.setIntBuffer(id);
		glDeleteRenderbuffers(1, bufs.intBuffer);
	}

	public void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
		bufs.setIntBuffer(renderbuffers, offset, n);
		glDeleteRenderbuffers(n, bufs.intBuffer);
	}

	public void glDeleteTexture(int id) {
		bufs.setIntBuffer(id);
		glDeleteTextures(1, bufs.intBuffer);
	}

	public void glDeleteTextures(int n, int[] textures, int offset) {
		bufs.setIntBuffer(textures, offset, n);
		glDeleteTextures(n, bufs.intBuffer);
	}

	public int glGenBuffer() {
		bufs.resizeIntBuffer(1);
		glGenBuffers(1, bufs.intBuffer);
		return bufs.intBuffer.get(0);
	}

	public void glGenBuffers(int n, int[] buffers, int offset) {
		bufs.resizeIntBuffer(n);
		glGenBuffers(n, bufs.intBuffer);
		bufs.intBuffer.get(buffers, offset, n);
	}

	public int glGenFramebuffer() {
		bufs.resizeIntBuffer(1);
		glGenFramebuffers(1, bufs.intBuffer);
		return bufs.intBuffer.get(0);
	}

	public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
		bufs.resizeIntBuffer(n);
		glGenFramebuffers(n, bufs.intBuffer);
		bufs.intBuffer.get(framebuffers, offset, n);
	}

	public int glGenRenderbuffer() {
		bufs.resizeIntBuffer(1);
		glGenRenderbuffers(1, bufs.intBuffer);
		return bufs.intBuffer.get(0);
	}

	public void glGenRenderbuffers(int n, int[] renderbuffers, int offset) {
		bufs.resizeIntBuffer(n);
		glGenRenderbuffers(n, bufs.intBuffer);
		bufs.intBuffer.get(renderbuffers, offset, n);
	}

	public int glGenTexture() {
		bufs.resizeIntBuffer(1);
		glGenTextures(1, bufs.intBuffer);
		return bufs.intBuffer.get(0);
	}

	public void glGenTextures(int n, int[] textures, int offset) {
		bufs.resizeIntBuffer(n);
		glGenTextures(n, bufs.intBuffer);
		bufs.intBuffer.get(textures, offset, n);
	}

	public void glGetAttachedShaders(int program, int maxcount, int[] count,
			int countOffset, int[] shaders, int shadersOffset) {
		int countLength = count.length - countOffset;
		bufs.resizeIntBuffer(countLength);
		int shadersLength = shaders.length - shadersOffset;
		IntBuffer intBuffer2 = bufs.createIntBuffer(shadersLength);
		glGetAttachedShaders(program, maxcount, bufs.intBuffer, intBuffer2);
		bufs.intBuffer.get(count, countOffset, countLength);
		intBuffer2.get(shaders, shadersOffset, shadersLength);
	}

	public void glGetBooleanv(int pname, byte[] params, int offset) {
		final int length = params.length - offset;
		bufs.resizeByteBuffer(length);
		glGetBooleanv(pname, bufs.byteBuffer);
		bufs.byteBuffer.get(params, offset, length);
	}

	public void glGetBufferParameteriv(int target, int pname, int[] params,
			int offset) {
		int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetBufferParameteriv(target, pname, bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glGetFloatv(int pname, float[] params, int offset) {
		final int length = params.length - offset;
		bufs.resizeFloatBuffer(length);
		glGetFloatv(pname, bufs.floatBuffer);
		bufs.floatBuffer.get(params, offset, length);
	}

	public void glGetFramebufferAttachmentParameteriv(int target,
			int attachment, int pname, int[] params, int offset) {
		final int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetFramebufferAttachmentParameteriv(target, attachment, pname,
				bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glGetIntegerv(int pname, int[] params, int offset) {
		final int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetIntegerv(pname, bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glGetProgramBinary(int program, int bufsize, int[] length,
			int lengthOffset, int[] binaryformat, int binaryformatOffset,
			Buffer binary) {
		final int lengthLength = bufsize - lengthOffset;
		bufs.resizeIntBuffer(lengthLength);

		final int binaryformatLength = bufsize - binaryformatOffset;
		final IntBuffer intBuffer2 = bufs.createIntBuffer(binaryformatLength);
		glGetProgramBinary(program, bufsize, bufs.intBuffer, intBuffer2, binary);
		bufs.intBuffer.get(length, lengthOffset, lengthLength);
		intBuffer2.get(binaryformat, binaryformatOffset, binaryformatLength);
	}

	public void glGetProgramInfoLog(int program, int bufsize, int[] length,
			int lengthOffset, byte[] infolog, int infologOffset) {
		final int intLength = length.length - lengthOffset;
		bufs.resizeIntBuffer(intLength);

		final int byteLength = bufsize - infologOffset;
		bufs.resizeByteBuffer(byteLength);

		glGetProgramInfoLog(program, bufsize, bufs.intBuffer, bufs.byteBuffer);

		bufs.intBuffer.get(length, lengthOffset, intLength);
		bufs.byteBuffer.get(infolog, byteLength, infologOffset);
	}

	public void glGetProgramiv(int program, int pname, int[] params, int offset) {
		final int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetProgramiv(program, pname, bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glGetRenderbufferParameteriv(int target, int pname,
			int[] params, int offset) {
		final int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetRenderbufferParameteriv(target, pname, bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glGetShaderInfoLog(int shader, int bufsize, int[] length,
			int lengthOffset, byte[] infolog, int infologOffset) {
		final int intLength = length.length - lengthOffset;
		bufs.resizeIntBuffer(intLength);
		final int byteLength = bufsize - infologOffset;
		bufs.resizeByteBuffer(byteLength);
		glGetShaderInfoLog(shader, bufsize, bufs.intBuffer, bufs.byteBuffer);
		bufs.intBuffer.get(length, lengthOffset, intLength);
		bufs.byteBuffer.get(infolog, byteLength, infologOffset);
	}

	public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
		final int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetShaderiv(shader, pname, bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glGetTexParameterfv(int target, int pname, float[] params,
			int offset) {
		final int length = params.length - offset;
		bufs.resizeFloatBuffer(length);
		glGetTexParameterfv(target, pname, bufs.floatBuffer);
		bufs.floatBuffer.get(params, offset, length);
	}

	public void glGetTexParameteriv(int target, int pname, int[] params,
			int offset) {
		final int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetTexParameteriv(target, pname, bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glGetUniformfv(int program, int location, float[] params,
			int offset) {
		final int length = params.length - offset;
		bufs.resizeFloatBuffer(length);
		glGetUniformfv(program, location, bufs.floatBuffer);
		bufs.floatBuffer.get(params, offset, length);
	}

	public void glGetUniformiv(int program, int location, int[] params,
			int offset) {
		final int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetUniformiv(program, location, bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glGetVertexAttribfv(int index, int pname, float[] params,
			int offset) {
		final int length = params.length - offset;
		bufs.resizeFloatBuffer(length);
		glGetVertexAttribfv(index, pname, bufs.floatBuffer);
		bufs.floatBuffer.get(params, offset, length);
	}

	public void glGetVertexAttribiv(int index, int pname, int[] params,
			int offset) {
		final int length = params.length - offset;
		bufs.resizeIntBuffer(length);
		glGetVertexAttribiv(index, pname, bufs.intBuffer);
		bufs.intBuffer.get(params, offset, length);
	}

	public void glTexParameterfv(int target, int pname, float[] params,
			int offset) {
		final int length = params.length - offset;
		bufs.setFloatBuffer(params, offset, length);
		glTexParameterfv(target, pname, bufs.floatBuffer);
	}

	public void glTexParameteriv(int target, int pname, int[] params, int offset) {
		final int length = params.length - offset;
		bufs.setIntBuffer(params, offset, length);
		glTexParameteriv(target, pname, bufs.intBuffer);
	}

	public void glUniform1fv(int location, int count, float[] v, int offset) {
		bufs.setFloatBuffer(v, offset, count);
		glUniform1fv(location, count, bufs.floatBuffer);
	}

	public void glUniform1iv(int location, int count, int[] v, int offset) {
		bufs.setIntBuffer(v, offset, count);
		glUniform1iv(location, count, bufs.intBuffer);
	}

	public void glUniform2fv(int location, int count, float[] v, int offset) {
		bufs.setFloatBuffer(v, 2 * offset, 2 * count);
		glUniform2fv(location, count, bufs.floatBuffer);
	}

	public void glUniform2iv(int location, int count, int[] v, int offset) {
		bufs.setIntBuffer(v, 2 * offset, 2 * count);
		glUniform2iv(location, count, bufs.intBuffer);
	}

	public void glUniform3fv(int location, int count, float[] v, int offset) {
		bufs.setFloatBuffer(v, 3 * offset, 3 * count);
		glUniform3fv(location, count, bufs.floatBuffer);
	}

	public void glUniform3iv(int location, int count, int[] v, int offset) {
		bufs.setIntBuffer(v, 3 * offset, 3 * count);
		glUniform3iv(location, count, bufs.intBuffer);
	}

	public void glUniform4fv(int location, int count, float[] v, int offset) {
		bufs.setFloatBuffer(v, 4 * offset, 4 * count);
		glUniform4fv(location, count, bufs.floatBuffer);
	}

	public void glUniform4iv(int location, int count, int[] v, int offset) {
		bufs.setIntBuffer(v, 4 * offset, 4 * count);
		glUniform4iv(location, count, bufs.intBuffer);
	}

	public void glUniformMatrix2fv(int location, int count, boolean transpose,
			float[] value, int offset) {
		bufs.setFloatBuffer(value, 2 * 2 * offset, 2 * 2 * count);
		glUniformMatrix2fv(location, count, transpose, bufs.floatBuffer);
	}

	public void glUniformMatrix3fv(int location, int count, boolean transpose,
			float[] value, int offset) {
		bufs.setFloatBuffer(value, 2 * 2 * offset, 3 * 3 * count);
		glUniformMatrix3fv(location, count, transpose, bufs.floatBuffer);
	}

	public void glUniformMatrix4fv(int location, int count, boolean transpose,
			float[] value, int offset) {
		bufs.setFloatBuffer(value, 4 * 4 * offset, 4 * 4 * count);
		glUniformMatrix4fv(location, count, transpose, bufs.floatBuffer);
	}

	public void glVertexAttrib1fv(int indx, float[] values, int offset) {
		glVertexAttrib1f(indx, values[indx + offset]);
	}

	public void glVertexAttrib2fv(int indx, float[] values, int offset) {
		glVertexAttrib2f(indx, values[indx + offset], values[indx + 1 + offset]);
	}

	public void glVertexAttrib3fv(int indx, float[] values, int offset) {
		glVertexAttrib3f(indx, values[indx + offset],
				values[indx + 1 + offset], values[indx + 2 + offset]);
	}

	public void glVertexAttrib4fv(int indx, float[] values, int offset) {
		glVertexAttrib4f(indx, values[indx + offset],
				values[indx + 1 + offset], values[indx + 2 + offset],
				values[indx + 3 + offset]);
	}

	public abstract String getPlatformGLExtensions();

	public abstract boolean hasGLSL();

	public abstract boolean isExtensionAvailable(String extension);

	public abstract boolean isFunctionAvailable(String function);
	
	public abstract int getSwapInterval();

	public abstract void glActiveTexture(int texture);

	public abstract void glAttachShader(int program, int shader);

	public abstract void glBindAttribLocation(int program, int index,
			String name);

	public abstract void glBindBuffer(int target, int buffer);

	public abstract void glBindFramebuffer(int target, int framebuffer);

	public abstract void glBindRenderbuffer(int target, int renderbuffer);

	public abstract void glBindTexture(int target, int texture);

	public abstract void glBlendColor(float red, float green, float blue,
			float alpha);

	public abstract void glBlendEquation(int mode);

	public abstract void glBlendEquationSeparate(int modeRGB, int modeAlpha);

	public abstract void glBlendFunc(int sfactor, int dfactor);

	public abstract void glBlendFuncSeparate(int srcRGB, int dstRGB,
			int srcAlpha, int dstAlpha);

	public abstract void glBufferData(int target, int size, Buffer data,
			int usage);

	public abstract void glBufferSubData(int target, int offset, int size,
			Buffer data);

	public abstract int glCheckFramebufferStatus(int target);

	public abstract void glClear(int mask);

	public abstract void glClearColor(float red, float green, float blue,
			float alpha);

	public abstract void glClearDepth(double depth);

	public abstract void glClearDepthf(float depth);

	public abstract void glClearStencil(int s);

	public abstract void glColorMask(boolean red, boolean green, boolean blue,
			boolean alpha);

	public abstract void glCompileShader(int shader);

	public abstract void glCompressedTexImage2D(int target, int level,
			int internalformat, int width, int height, int border,
			int imageSize, Buffer data);

	public abstract void glCompressedTexImage2D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7);

	public abstract void glCompressedTexImage3D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, Buffer arg8);

	public abstract void glCompressedTexImage3D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, int arg8);

	public abstract void glCompressedTexSubImage2D(int target, int level,
			int xoffset, int yoffset, int width, int height, int format,
			int imageSize, Buffer data);

	public abstract void glCompressedTexSubImage2D(int arg0, int arg1,
			int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8);

	public abstract void glCompressedTexSubImage3D(int arg0, int arg1,
			int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
			int arg8, int arg9, Buffer arg10);

	public abstract void glCompressedTexSubImage3D(int arg0, int arg1,
			int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
			int arg8, int arg9, int arg10);

	public abstract void glCopyTexImage2D(int target, int level,
			int internalformat, int x, int y, int width, int height, int border);

	public abstract void glCopyTexSubImage2D(int target, int level,
			int xoffset, int yoffset, int x, int y, int width, int height);

	public abstract void glCopyTexSubImage3D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, int arg8);

	public abstract int glCreateProgram();

	public abstract int glCreateShader(int type);

	public abstract void glCullFace(int mode);

	public abstract void glDeleteBuffers(int n, IntBuffer buffers);

	public abstract void glDeleteFramebuffers(int n, IntBuffer framebuffers);

	public abstract void glDeleteProgram(int program);

	public abstract void glDeleteRenderbuffers(int n, IntBuffer renderbuffers);

	public abstract void glDeleteShader(int shader);

	public abstract void glDeleteTextures(int n, IntBuffer textures);

	public abstract void glDepthFunc(int func);

	public abstract void glDepthMask(boolean flag);

	public abstract void glDepthRange(double zNear, double zFar);

	public abstract void glDepthRangef(float zNear, float zFar);

	public abstract void glDetachShader(int program, int shader);

	public abstract void glDisable(int cap);

	public abstract void glDisableVertexAttribArray(int index);

	public abstract void glDrawArrays(int mode, int first, int count);

	public abstract void glDrawElements(int mode, int count, int type,
			Buffer indices);

	public abstract void glDrawElements(int mode, int count, int type,
			int offset);

	public abstract void glEnable(int cap);

	public abstract void glEnableVertexAttribArray(int index);

	public abstract void glFinish();

	public abstract void glFlush();

	public abstract void glFramebufferRenderbuffer(int target, int attachment,
			int renderbuffertarget, int renderbuffer);

	public abstract void glFramebufferTexture2D(int target, int attachment,
			int textarget, int texture, int level);

	public abstract void glFramebufferTexture3D(int target, int attachment,
			int textarget, int texture, int level, int zoffset);

	public abstract void glFrontFace(int mode);

	public abstract void glGenBuffers(int n, IntBuffer buffers);

	public abstract void glGenerateMipmap(int target);

	public abstract void glGenFramebuffers(int n, IntBuffer framebuffers);

	public abstract void glGenRenderbuffers(int n, IntBuffer renderbuffers);

	public abstract void glGenTextures(int n, IntBuffer textures);

	public abstract void glGetActiveAttrib(int program, int index, int bufsize,
			int[] length, int lengthOffset, int[] size, int sizeOffset,
			int[] type, int typeOffset, byte[] name, int nameOffset);

	public abstract void glGetActiveAttrib(int program, int index, int bufsize,
			IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name);

	public abstract void glGetActiveUniform(int program, int index,
			int bufsize, int[] length, int lengthOffset, int[] size,
			int sizeOffset, int[] type, int typeOffset, byte[] name,
			int nameOffset);

	public abstract void glGetActiveUniform(int program, int index,
			int bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
			ByteBuffer name);

	public abstract void glGetAttachedShaders(int program, int maxcount,
			IntBuffer count, IntBuffer shaders);

	public abstract int glGetAttribLocation(int program, String name);

	public abstract boolean glGetBoolean(int pname);

	public abstract void glGetBooleanv(int pname, ByteBuffer params);

	public abstract int glGetBoundBuffer(int arg0);

	public abstract void glGetBufferParameteriv(int target, int pname,
			IntBuffer params);

	public abstract int glGetError();

	public abstract float glGetFloat(int pname);

	public abstract void glGetFloatv(int pname, FloatBuffer params);

	public abstract void glGetFramebufferAttachmentParameteriv(int target,
			int attachment, int pname, IntBuffer params);

	public abstract int glGetInteger(int pname);

	public abstract void glGetIntegerv(int pname, IntBuffer params);

	public abstract void glGetProgramBinary(int arg0, int arg1, IntBuffer arg2,
			IntBuffer arg3, Buffer arg4);

	public abstract void glGetProgramInfoLog(int program, int bufsize,
			IntBuffer length, ByteBuffer infolog);

	public abstract String glGetProgramInfoLog(int program);

	public abstract void glGetProgramiv(int program, int pname, IntBuffer params);

	public abstract void glGetRenderbufferParameteriv(int target, int pname,
			IntBuffer params);

	public abstract void glGetShaderInfoLog(int shader, int bufsize,
			IntBuffer length, ByteBuffer infolog);

	public abstract String glGetShaderInfoLog(int shader);

	public abstract void glGetShaderiv(int shader, int pname, IntBuffer params);

	public abstract void glGetShaderPrecisionFormat(int shadertype,
			int precisiontype, int[] range, int rangeOffset, int[] precision,
			int precisionOffset);

	public abstract void glGetShaderPrecisionFormat(int shadertype,
			int precisiontype, IntBuffer range, IntBuffer precision);

	public abstract void glGetShaderSource(int shader, int bufsize,
			int[] length, int lengthOffset, byte[] source, int sourceOffset);

	public abstract void glGetShaderSource(int shader, int bufsize,
			IntBuffer length, ByteBuffer source);

	public abstract String glGetString(int name);

	public abstract void glGetTexParameterfv(int target, int pname,
			FloatBuffer params);

	public abstract void glGetTexParameteriv(int target, int pname,
			IntBuffer params);

	public abstract void glGetUniformfv(int program, int location,
			FloatBuffer params);

	public abstract void glGetUniformiv(int program, int location,
			IntBuffer params);

	public abstract int glGetUniformLocation(int program, String name);

	public abstract void glGetVertexAttribfv(int index, int pname,
			FloatBuffer params);

	public abstract void glGetVertexAttribiv(int index, int pname,
			IntBuffer params);

	public abstract void glHint(int target, int mode);

	public abstract boolean glIsBuffer(int buffer);

	public abstract boolean glIsEnabled(int cap);

	public abstract boolean glIsFramebuffer(int framebuffer);

	public abstract boolean glIsProgram(int program);

	public abstract boolean glIsRenderbuffer(int renderbuffer);

	public abstract boolean glIsShader(int shader);

	public abstract boolean glIsTexture(int texture);

	public abstract boolean glIsVBOArrayEnabled();

	public abstract boolean glIsVBOElementEnabled();

	public abstract void glLineWidth(float width);

	public abstract void glLinkProgram(int program);

	public abstract ByteBuffer glMapBuffer(int arg0, int arg1);

	public abstract void glPixelStorei(int pname, int param);

	public abstract void glPolygonOffset(float factor, float units);

	public abstract void glProgramBinary(int arg0, int arg1, Buffer arg2,
			int arg3);

	public abstract void glReadPixels(int x, int y, int width, int height,
			int format, int type, Buffer pixels);

	public abstract void glReadPixels(int x, int y, int width, int height,
			int format, int type, int pixelsBufferOffset);

	public abstract void glReleaseShaderCompiler();

	public abstract void glRenderbufferStorage(int target, int internalformat,
			int width, int height);

	public abstract void glSampleCoverage(float value, boolean invert);

	public abstract void glScissor(int x, int y, int width, int height);

	public abstract void glShaderBinary(int n, int[] shaders, int offset,
			int binaryformat, Buffer binary, int length);

	public abstract void glShaderBinary(int n, IntBuffer shaders,
			int binaryformat, Buffer binary, int length);

	public abstract void glShaderSource(int shader, int count,
			String[] strings, int[] length, int lengthOffset);

	public abstract void glShaderSource(int shader, int count,
			String[] strings, IntBuffer length);

	public abstract void glShaderSource(int shader, String string);

	public abstract void glStencilFunc(int func, int ref, int mask);

	public abstract void glStencilFuncSeparate(int face, int func, int ref,
			int mask);

	public abstract void glStencilMask(int mask);

	public abstract void glStencilMaskSeparate(int face, int mask);

	public abstract void glStencilOp(int fail, int zfail, int zpass);

	public abstract void glStencilOpSeparate(int face, int fail, int zfail,
			int zpass);

	public abstract void glTexImage2D(int target, int level,
			int internalformat, int width, int height, int border, int format,
			int type, Buffer pixels);

	public abstract void glTexImage2D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8);

	public abstract void glTexImage3D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8, Buffer arg9);

	public abstract void glTexImage3D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8, int arg9);

	public abstract void glTexParameterf(int target, int pname, float param);

	public abstract void glTexParameterfv(int target, int pname,
			FloatBuffer params);

	public abstract void glTexParameteri(int target, int pname, int param);

	public abstract void glTexParameteriv(int target, int pname,
			IntBuffer params);

	public abstract void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			Buffer pixels);

	public abstract void glTexSubImage2D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, int arg8);

	public abstract void glTexSubImage3D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
			int arg9, Buffer arg10);

	public abstract void glTexSubImage3D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
			int arg9, int arg10);

	public abstract void glUniform1f(int location, float x);

	public abstract void glUniform1fv(int location, int count, FloatBuffer v);

	public abstract void glUniform1i(int location, int x);

	public abstract void glUniform1iv(int location, int count, IntBuffer v);

	public abstract void glUniform2f(int location, float x, float y);

	public abstract void glUniform2fv(int location, int count, FloatBuffer v);

	public abstract void glUniform2i(int location, int x, int y);

	public abstract void glUniform2iv(int location, int count, IntBuffer v);

	public abstract void glUniform3f(int location, float x, float y, float z);

	public abstract void glUniform3fv(int location, int count, FloatBuffer v);

	public abstract void glUniform3i(int location, int x, int y, int z);

	public abstract void glUniform3iv(int location, int count, IntBuffer v);

	public abstract void glUniform4f(int location, float x, float y, float z,
			float w);

	public abstract void glUniform4fv(int location, int count, FloatBuffer v);

	public abstract void glUniform4i(int location, int x, int y, int z, int w);

	public abstract void glUniform4iv(int location, int count, IntBuffer v);

	public abstract void glUniformMatrix2fv(int location, int count,
			boolean transpose, FloatBuffer value);

	public abstract void glUniformMatrix3fv(int location, int count,
			boolean transpose, FloatBuffer value);

	public abstract void glUniformMatrix4fv(int location, int count,
			boolean transpose, FloatBuffer value);

	public abstract boolean glUnmapBuffer(int arg0);

	public abstract void glUseProgram(int program);

	public abstract void glValidateProgram(int program);

	public abstract void glVertexAttrib1f(int indx, float x);

	public abstract void glVertexAttrib1fv(int indx, FloatBuffer values);

	public abstract void glVertexAttrib2f(int indx, float x, float y);

	public abstract void glVertexAttrib2fv(int indx, FloatBuffer values);

	public abstract void glVertexAttrib3f(int indx, float x, float y, float z);

	public abstract void glVertexAttrib3fv(int indx, FloatBuffer values);

	public abstract void glVertexAttrib4f(int indx, float x, float y, float z,
			float w);

	public abstract void glVertexAttrib4fv(int indx, FloatBuffer values);

	public abstract void glVertexAttribPointer(int indx, int size, int type,
			boolean normalized, int stride, Buffer ptr);

	public abstract void glVertexAttribPointer(int indx, int size, int type,
			boolean normalized, int stride, int ptr);

	public abstract void glViewport(int x, int y, int width, int height);


}
