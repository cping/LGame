package loon.core.graphics.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * 
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
public interface GL11 extends GL10 {

	public static final int GL_OES_VERSION_1_0 = 1;

	public static final int GL_MAX_ELEMENTS_VERTICES = javax.microedition.khronos.opengles.GL11.GL_MAX_ELEMENTS_VERTICES;

	public static final int GL_MAX_ELEMENTS_INDICES = javax.microedition.khronos.opengles.GL11.GL_MAX_ELEMENTS_INDICES;

	public static final int GL_POLYGON_SMOOTH_HINT = javax.microedition.khronos.opengles.GL11.GL_POLYGON_SMOOTH_HINT;

	public static final int GL_VERSION_ES_CM_1_0 = 1;

	public static final int GL_VERSION_ES_CL_1_0 = 1;

	public static final int GL_VERSION_ES_CM_1_1 = 1;

	public static final int GL_VERSION_ES_CL_1_1 = 1;

	public static final int GL_CLIP_PLANE0 = javax.microedition.khronos.opengles.GL11.GL_CLIP_PLANE0;

	public static final int GL_CLIP_PLANE1 = javax.microedition.khronos.opengles.GL11.GL_CLIP_PLANE1;

	public static final int GL_CLIP_PLANE2 = javax.microedition.khronos.opengles.GL11.GL_CLIP_PLANE2;

	public static final int GL_CLIP_PLANE3 = javax.microedition.khronos.opengles.GL11.GL_CLIP_PLANE3;

	public static final int GL_CLIP_PLANE4 = javax.microedition.khronos.opengles.GL11.GL_CLIP_PLANE4;

	public static final int GL_CLIP_PLANE5 = javax.microedition.khronos.opengles.GL11.GL_CLIP_PLANE5;

	public static final int GL_CURRENT_COLOR = javax.microedition.khronos.opengles.GL11.GL_CURRENT_COLOR;

	public static final int GL_CURRENT_NORMAL = javax.microedition.khronos.opengles.GL11.GL_CURRENT_NORMAL;

	public static final int GL_CURRENT_TEXTURE_COORDS = javax.microedition.khronos.opengles.GL11.GL_CURRENT_TEXTURE_COORDS;

	public static final int GL_POINT_SIZE = javax.microedition.khronos.opengles.GL11.GL_POINT_SIZE;

	public static final int GL_POINT_SIZE_MIN = javax.microedition.khronos.opengles.GL11.GL_POINT_SIZE_MIN;

	public static final int GL_POINT_SIZE_MAX = javax.microedition.khronos.opengles.GL11.GL_POINT_SIZE_MAX;

	public static final int GL_POINT_FADE_THRESHOLD_SIZE = javax.microedition.khronos.opengles.GL11.GL_POINT_FADE_THRESHOLD_SIZE;

	public static final int GL_POINT_DISTANCE_ATTENUATION = javax.microedition.khronos.opengles.GL11.GL_POINT_DISTANCE_ATTENUATION;

	public static final int GL_LINE_WIDTH = javax.microedition.khronos.opengles.GL11.GL_LINE_WIDTH;

	public static final int GL_CULL_FACE_MODE = javax.microedition.khronos.opengles.GL11.GL_CULL_FACE_MODE;

	public static final int GL_FRONT_FACE = javax.microedition.khronos.opengles.GL11.GL_FRONT_FACE;

	public static final int GL_SHADE_MODEL = javax.microedition.khronos.opengles.GL11.GL_SHADE_MODEL;

	public static final int GL_DEPTH_RANGE = javax.microedition.khronos.opengles.GL11.GL_DEPTH_RANGE;

	public static final int GL_DEPTH_WRITEMASK = javax.microedition.khronos.opengles.GL11.GL_DEPTH_WRITEMASK;

	public static final int GL_DEPTH_CLEAR_VALUE = javax.microedition.khronos.opengles.GL11.GL_DEPTH_CLEAR_VALUE;

	public static final int GL_DEPTH_FUNC = javax.microedition.khronos.opengles.GL11.GL_DEPTH_FUNC;

	public static final int GL_STENCIL_CLEAR_VALUE = javax.microedition.khronos.opengles.GL11.GL_STENCIL_CLEAR_VALUE;

	public static final int GL_STENCIL_FUNC = javax.microedition.khronos.opengles.GL11.GL_STENCIL_FUNC;

	public static final int GL_STENCIL_VALUE_MASK = javax.microedition.khronos.opengles.GL11.GL_STENCIL_VALUE_MASK;

	public static final int GL_STENCIL_FAIL = javax.microedition.khronos.opengles.GL11.GL_STENCIL_FAIL;

	public static final int GL_STENCIL_PASS_DEPTH_FAIL = javax.microedition.khronos.opengles.GL11.GL_STENCIL_PASS_DEPTH_FAIL;

	public static final int GL_STENCIL_PASS_DEPTH_PASS = javax.microedition.khronos.opengles.GL11.GL_STENCIL_PASS_DEPTH_PASS;

	public static final int GL_STENCIL_REF = javax.microedition.khronos.opengles.GL11.GL_STENCIL_REF;

	public static final int GL_STENCIL_WRITEMASK = javax.microedition.khronos.opengles.GL11.GL_STENCIL_WRITEMASK;

	public static final int GL_MATRIX_MODE = javax.microedition.khronos.opengles.GL11.GL_MATRIX_MODE;

	public static final int GL_VIEWPORT = javax.microedition.khronos.opengles.GL11.GL_VIEWPORT;

	public static final int GL_MODELVIEW_STACK_DEPTH = javax.microedition.khronos.opengles.GL11.GL_MODELVIEW_STACK_DEPTH;

	public static final int GL_PROJECTION_STACK_DEPTH = javax.microedition.khronos.opengles.GL11.GL_PROJECTION_STACK_DEPTH;

	public static final int GL_TEXTURE_STACK_DEPTH = javax.microedition.khronos.opengles.GL11.GL_TEXTURE_STACK_DEPTH;

	public static final int GL_MODELVIEW_MATRIX = javax.microedition.khronos.opengles.GL11.GL_MODELVIEW_MATRIX;

	public static final int GL_MODELVIEW_MATRIX_FLOAT_AS_INT_BITS_OES = javax.microedition.khronos.opengles.GL11.GL_MODELVIEW_MATRIX_FLOAT_AS_INT_BITS_OES;

	public static final int GL_PROJECTION_MATRIX = javax.microedition.khronos.opengles.GL11.GL_PROJECTION_MATRIX;

	public static final int GL_TEXTURE_MATRIX = javax.microedition.khronos.opengles.GL11.GL_TEXTURE_MATRIX;

	public static final int GL_ALPHA_TEST_FUNC = javax.microedition.khronos.opengles.GL11.GL_ALPHA_TEST_FUNC;

	public static final int GL_ALPHA_TEST_REF = javax.microedition.khronos.opengles.GL11.GL_ALPHA_TEST_REF;

	public static final int GL_BLEND_DST = javax.microedition.khronos.opengles.GL11.GL_BLEND_DST;

	public static final int GL_BLEND_SRC = javax.microedition.khronos.opengles.GL11.GL_BLEND_SRC;

	public static final int GL_LOGIC_OP_MODE = javax.microedition.khronos.opengles.GL11.GL_LOGIC_OP_MODE;

	public static final int GL_SCISSOR_BOX = javax.microedition.khronos.opengles.GL11.GL_SCISSOR_BOX;

	public static final int GL_COLOR_CLEAR_VALUE = javax.microedition.khronos.opengles.GL11.GL_COLOR_CLEAR_VALUE;

	public static final int GL_COLOR_WRITEMASK = javax.microedition.khronos.opengles.GL11.GL_COLOR_WRITEMASK;

	public static final int GL_MAX_CLIP_PLANES = javax.microedition.khronos.opengles.GL11.GL_MAX_CLIP_PLANES;

	public static final int GL_POLYGON_OFFSET_UNITS = javax.microedition.khronos.opengles.GL11.GL_POLYGON_OFFSET_UNITS;

	public static final int GL_POLYGON_OFFSET_FACTOR = javax.microedition.khronos.opengles.GL11.GL_POLYGON_OFFSET_FACTOR;

	public static final int GL_TEXTURE_BINDING_2D = javax.microedition.khronos.opengles.GL11.GL_TEXTURE_BINDING_2D;

	public static final int GL_VERTEX_ARRAY_SIZE = javax.microedition.khronos.opengles.GL11.GL_VERTEX_ARRAY_SIZE;

	public static final int GL_VERTEX_ARRAY_TYPE = javax.microedition.khronos.opengles.GL11.GL_VERTEX_ARRAY_TYPE;

	public static final int GL_VERTEX_ARRAY_STRIDE = javax.microedition.khronos.opengles.GL11.GL_VERTEX_ARRAY_STRIDE;

	public static final int GL_NORMAL_ARRAY_TYPE = javax.microedition.khronos.opengles.GL11.GL_NORMAL_ARRAY_TYPE;

	public static final int GL_NORMAL_ARRAY_STRIDE = javax.microedition.khronos.opengles.GL11.GL_NORMAL_ARRAY_STRIDE;

	public static final int GL_COLOR_ARRAY_SIZE = javax.microedition.khronos.opengles.GL11.GL_COLOR_ARRAY_SIZE;

	public static final int GL_COLOR_ARRAY_TYPE = javax.microedition.khronos.opengles.GL11.GL_COLOR_ARRAY_TYPE;

	public static final int GL_COLOR_ARRAY_STRIDE = javax.microedition.khronos.opengles.GL11.GL_COLOR_ARRAY_STRIDE;

	public static final int GL_TEXTURE_COORD_ARRAY_SIZE = javax.microedition.khronos.opengles.GL11.GL_TEXTURE_COORD_ARRAY_SIZE;

	public static final int GL_TEXTURE_COORD_ARRAY_TYPE = javax.microedition.khronos.opengles.GL11.GL_TEXTURE_COORD_ARRAY_TYPE;

	public static final int GL_TEXTURE_COORD_ARRAY_STRIDE = javax.microedition.khronos.opengles.GL11.GL_TEXTURE_COORD_ARRAY_STRIDE;

	public static final int GL_VERTEX_ARRAY_POINTER = javax.microedition.khronos.opengles.GL11.GL_VERTEX_ARRAY_POINTER;

	public static final int GL_NORMAL_ARRAY_POINTER = javax.microedition.khronos.opengles.GL11.GL_NORMAL_ARRAY_POINTER;

	public static final int GL_COLOR_ARRAY_POINTER = javax.microedition.khronos.opengles.GL11.GL_COLOR_ARRAY_POINTER;

	public static final int GL_TEXTURE_COORD_ARRAY_POINTER = javax.microedition.khronos.opengles.GL11.GL_TEXTURE_COORD_ARRAY_POINTER;

	public static final int GL_SAMPLE_BUFFERS = javax.microedition.khronos.opengles.GL11.GL_SAMPLE_BUFFERS;

	public static final int GL_SAMPLES = javax.microedition.khronos.opengles.GL11.GL_SAMPLES;

	public static final int GL_SAMPLE_COVERAGE_VALUE = javax.microedition.khronos.opengles.GL11.GL_SAMPLE_COVERAGE_VALUE;

	public static final int GL_SAMPLE_COVERAGE_INVERT = javax.microedition.khronos.opengles.GL11.GL_SAMPLE_COVERAGE_INVERT;

	public static final int GL_GENERATE_MIPMAP_HINT = javax.microedition.khronos.opengles.GL11.GL_GENERATE_MIPMAP_HINT;

	public static final int GL_GENERATE_MIPMAP = javax.microedition.khronos.opengles.GL11.GL_GENERATE_MIPMAP;

	public static final int GL_ACTIVE_TEXTURE = javax.microedition.khronos.opengles.GL11.GL_ACTIVE_TEXTURE;

	public static final int GL_CLIENT_ACTIVE_TEXTURE = javax.microedition.khronos.opengles.GL11.GL_CLIENT_ACTIVE_TEXTURE;

	public static final int GL_ARRAY_BUFFER = javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER;

	public static final int GL_ELEMENT_ARRAY_BUFFER = javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER;

	public static final int GL_ARRAY_BUFFER_BINDING = javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER_BINDING;

	public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER_BINDING;

	public static final int GL_VERTEX_ARRAY_BUFFER_BINDING = javax.microedition.khronos.opengles.GL11.GL_VERTEX_ARRAY_BUFFER_BINDING;

	public static final int GL_NORMAL_ARRAY_BUFFER_BINDING = javax.microedition.khronos.opengles.GL11.GL_NORMAL_ARRAY_BUFFER_BINDING;

	public static final int GL_COLOR_ARRAY_BUFFER_BINDING = javax.microedition.khronos.opengles.GL11.GL_COLOR_ARRAY_BUFFER_BINDING;

	public static final int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = javax.microedition.khronos.opengles.GL11.GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING;

	public static final int GL_STATIC_DRAW = javax.microedition.khronos.opengles.GL11.GL_STATIC_DRAW;

	public static final int GL_DYNAMIC_DRAW = javax.microedition.khronos.opengles.GL11.GL_DYNAMIC_DRAW;

	public static final int GL_BUFFER_SIZE = javax.microedition.khronos.opengles.GL11.GL_BUFFER_SIZE;

	public static final int GL_BUFFER_USAGE = javax.microedition.khronos.opengles.GL11.GL_BUFFER_USAGE;

	public static final int GL_SUBTRACT = javax.microedition.khronos.opengles.GL11.GL_SUBTRACT;

	public static final int GL_COMBINE = javax.microedition.khronos.opengles.GL11.GL_COMBINE;

	public static final int GL_COMBINE_RGB = javax.microedition.khronos.opengles.GL11.GL_COMBINE_RGB;

	public static final int GL_COMBINE_ALPHA = javax.microedition.khronos.opengles.GL11.GL_COMBINE_ALPHA;

	public static final int GL_RGB_SCALE = javax.microedition.khronos.opengles.GL11.GL_RGB_SCALE;

	public static final int GL_ADD_SIGNED = javax.microedition.khronos.opengles.GL11.GL_ADD_SIGNED;

	public static final int GL_INTERPOLATE = javax.microedition.khronos.opengles.GL11.GL_INTERPOLATE;

	public static final int GL_CONSTANT = javax.microedition.khronos.opengles.GL11.GL_CONSTANT;

	public static final int GL_PRIMARY_COLOR = javax.microedition.khronos.opengles.GL11.GL_PRIMARY_COLOR;

	public static final int GL_PREVIOUS = javax.microedition.khronos.opengles.GL11.GL_PREVIOUS;

	public static final int GL_OPERAND0_RGB = javax.microedition.khronos.opengles.GL11.GL_OPERAND0_RGB;

	public static final int GL_OPERAND1_RGB = javax.microedition.khronos.opengles.GL11.GL_OPERAND1_RGB;

	public static final int GL_OPERAND2_RGB = javax.microedition.khronos.opengles.GL11.GL_OPERAND2_RGB;

	public static final int GL_OPERAND0_ALPHA = javax.microedition.khronos.opengles.GL11.GL_OPERAND0_ALPHA;

	public static final int GL_OPERAND1_ALPHA = javax.microedition.khronos.opengles.GL11.GL_OPERAND1_ALPHA;

	public static final int GL_OPERAND2_ALPHA = javax.microedition.khronos.opengles.GL11.GL_OPERAND2_ALPHA;

	public static final int GL_ALPHA_SCALE = javax.microedition.khronos.opengles.GL11.GL_ALPHA_SCALE;

	public static final int GL_SRC0_RGB = javax.microedition.khronos.opengles.GL11.GL_SRC0_RGB;

	public static final int GL_SRC1_RGB = javax.microedition.khronos.opengles.GL11.GL_SRC1_RGB;

	public static final int GL_SRC2_RGB = javax.microedition.khronos.opengles.GL11.GL_SRC2_RGB;

	public static final int GL_SRC0_ALPHA = javax.microedition.khronos.opengles.GL11.GL_SRC0_ALPHA;

	public static final int GL_SRC1_ALPHA = javax.microedition.khronos.opengles.GL11.GL_SRC1_ALPHA;

	public static final int GL_SRC2_ALPHA = javax.microedition.khronos.opengles.GL11.GL_SRC2_ALPHA;

	public static final int GL_DOT3_RGB = javax.microedition.khronos.opengles.GL11.GL_DOT3_RGB;

	public static final int GL_DOT3_RGBA = javax.microedition.khronos.opengles.GL11.GL_DOT3_RGBA;

	public static final int GL_POINT_SIZE_ARRAY_OES = javax.microedition.khronos.opengles.GL11.GL_POINT_SIZE_ARRAY_OES;

	public static final int GL_POINT_SIZE_ARRAY_TYPE_OES = javax.microedition.khronos.opengles.GL11.GL_POINT_SIZE_ARRAY_TYPE_OES;

	public static final int GL_POINT_SIZE_ARRAY_STRIDE_OES = javax.microedition.khronos.opengles.GL11.GL_POINT_SIZE_ARRAY_STRIDE_OES;

	public static final int GL_POINT_SIZE_ARRAY_POINTER_OES = javax.microedition.khronos.opengles.GL11.GL_POINT_SIZE_ARRAY_POINTER_OES;

	public static final int GL_POINT_SIZE_ARRAY_BUFFER_BINDING_OES = javax.microedition.khronos.opengles.GL11.GL_POINT_SIZE_ARRAY_BUFFER_BINDING_OES;

	public static final int GL_POINT_SPRITE_OES = javax.microedition.khronos.opengles.GL11.GL_POINT_SPRITE_OES;

	public static final int GL_COORD_REPLACE_OES = javax.microedition.khronos.opengles.GL11.GL_COORD_REPLACE_OES;

	public static final int GL_OES_point_size_array = 1;

	public static final int GL_OES_point_sprite = 1;

	public void glClipPlanef(int plane, float[] equation, int offset);

	public void glClipPlanef(int plane, FloatBuffer equation);

	public void glGetClipPlanef(int pname, float[] eqn, int offset);

	public void glGetClipPlanef(int pname, FloatBuffer eqn);

	public void glGetFloatv(int pname, float[] params, int offset);

	public void glGetFloatv(int pname, FloatBuffer params);

	public void glGetLightfv(int light, int pname, float[] params, int offset);

	public void glGetLightfv(int light, int pname, FloatBuffer params);

	public void glGetMaterialfv(int face, int pname, float[] params, int offset);

	public void glGetMaterialfv(int face, int pname, FloatBuffer params);

	public void glGetTexParameterfv(int target, int pname, float[] params,
			int offset);

	public void glGetTexParameterfv(int target, int pname, FloatBuffer params);

	public void glPointParameterf(int pname, float param);

	public void glPointParameterfv(int pname, float[] params, int offset);

	public void glPointParameterfv(int pname, FloatBuffer params);

	public void glTexParameterfv(int target, int pname, float[] params,
			int offset);

	public void glTexParameterfv(int target, int pname, FloatBuffer params);

	public void glBindBuffer(int target, int buffer);

	public void glBufferData(int target, int size, Buffer data, int usage);

	public void glBufferSubData(int target, int offset, int size, Buffer data);

	public void glColor4ub(byte red, byte green, byte blue, byte alpha);

	public void glDeleteBuffers(int n, int[] buffers, int offset);

	public void glDeleteBuffers(int n, IntBuffer buffers);

	public void glGetBooleanv(int pname, boolean[] params, int offset);

	public void glGetBooleanv(int pname, IntBuffer params);

	public void glGetBufferParameteriv(int target, int pname, int[] params,
			int offset);

	public void glGetBufferParameteriv(int target, int pname, IntBuffer params);

	public void glGenBuffers(int n, int[] buffers, int offset);

	public void glGenBuffers(int n, IntBuffer buffers);

	public void glGetPointerv(int pname, Buffer[] params);

	public void glGetTexEnviv(int env, int pname, int[] params, int offset);

	public void glGetTexEnviv(int env, int pname, IntBuffer params);

	public void glGetTexParameteriv(int target, int pname, int[] params,
			int offset);

	public void glGetTexParameteriv(int target, int pname, IntBuffer params);

	public boolean glIsBuffer(int buffer);

	public boolean glIsEnabled(int cap);

	public boolean glIsTexture(int texture);

	public void glTexEnvi(int target, int pname, int param);

	public void glTexEnviv(int target, int pname, int[] params, int offset);

	public void glTexEnviv(int target, int pname, IntBuffer params);

	public void glTexParameteri(int target, int pname, int param);

	public void glTexParameteriv(int target, int pname, int[] params, int offset);

	public void glTexParameteriv(int target, int pname, IntBuffer params);

	public void glPointSizePointerOES(int type, int stride, Buffer pointer);

	public void glVertexPointer(int size, int type, int stride, int pointer);

	public void glColorPointer(int size, int type, int stride, int pointer);

	public void glNormalPointer(int type, int stride, int pointer);

	public void glTexCoordPointer(int size, int type, int stride, int pointer);

	public void glDrawElements(int mode, int count, int type, int indices);
}
