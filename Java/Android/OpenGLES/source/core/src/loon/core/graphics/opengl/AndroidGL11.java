package loon.core.graphics.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1
 */
class AndroidGL11 extends AndroidGL10 implements GL11 {
	
	private final javax.microedition.khronos.opengles.GL11 gl;

	public AndroidGL11 (javax.microedition.khronos.opengles.GL10 gl) {
		super(gl);
		this.gl = (javax.microedition.khronos.opengles.GL11)gl;
	}

	 public void glBindBuffer (int target, int buffer) {
		gl.glBindBuffer(target, buffer);
	}

	 public void glBufferData (int target, int size, Buffer data, int usage) {
		gl.glBufferData(target, size, data, usage);
	}

	 public void glBufferSubData (int target, int offset, int size, Buffer data) {
		gl.glBufferSubData(target, offset, size, data);
	}

	 public void glClipPlanef (int plane, FloatBuffer equation) {
		gl.glClipPlanef(plane, equation);
	}

	 public void glColor4ub (byte red, byte green, byte blue, byte alpha) {
		gl.glColor4ub(red, green, blue, alpha);
	}

	 public void glDeleteBuffers (int n, IntBuffer buffers) {
		gl.glDeleteBuffers(n, buffers);
	}

	 public void glGenBuffers (int n, IntBuffer buffers) {
		gl.glGenBuffers(n, buffers);
	}

	 public void glGetBooleanv (int pname, IntBuffer params) {
		gl.glGetBooleanv(pname, params);
	}

	 public void glGetBufferParameteriv (int target, int pname, IntBuffer params) {
		gl.glGetBufferParameteriv(target, pname, params);
	}

	 public void glGetClipPlanef (int pname, FloatBuffer eqn) {
		gl.glGetClipPlanef(pname, eqn);
	}

	 public void glGetFloatv (int pname, FloatBuffer params) {
		gl.glGetFloatv(pname, params);
	}

	 public void glGetLightfv (int light, int pname, FloatBuffer params) {
		gl.glGetLightfv(light, pname, params);
	}

	 public void glGetMaterialfv (int face, int pname, FloatBuffer params) {
		gl.glGetMaterialfv(face, pname, params);
	}

	 public void glGetPointerv (int pname, Buffer[] params) {
		gl.glGetPointerv(pname, params);
	}

	 public void glGetTexEnviv (int env, int pname, IntBuffer params) {
		gl.glGetTexEnviv(env, pname, params);
	}

	 public void glGetTexParameterfv (int target, int pname, FloatBuffer params) {
		gl.glGetTexParameterfv(target, pname, params);
	}

	 public void glGetTexParameteriv (int target, int pname, IntBuffer params) {
		gl.glGetTexParameteriv(target, pname, params);
	}

	 public boolean glIsBuffer (int buffer) {
		return gl.glIsBuffer(buffer);
	}

	 public boolean glIsEnabled (int cap) {
		return gl.glIsEnabled(cap);
	}

	 public boolean glIsTexture (int texture) {
		return gl.glIsTexture(texture);
	}

	 public void glPointParameterf (int pname, float param) {
		gl.glPointParameterf(pname, param);
	}

	 public void glPointParameterfv (int pname, FloatBuffer params) {
		gl.glPointParameterfv(pname, params);
	}

	 public void glPointSizePointerOES (int type, int stride, Buffer pointer) {
		gl.glPointSizePointerOES(type, stride, pointer);
	}

	 public void glTexEnvi (int target, int pname, int param) {
		gl.glTexEnvi(target, pname, param);
	}

	 public void glTexEnviv (int target, int pname, IntBuffer params) {
		gl.glTexEnviv(target, pname, params);
	}

	 public void glTexParameterfv (int target, int pname, FloatBuffer params) {
		gl.glTexParameterfv(target, pname, params);
	}

	 public void glTexParameteri (int target, int pname, int param) {
		gl.glTexParameteri(target, pname, param);
	}

	 public void glTexParameteriv (int target, int pname, IntBuffer params) {
		gl.glTexParameteriv(target, pname, params);
	}

	 public void glClipPlanef (int plane, float[] equation, int offset) {
		gl.glClipPlanef(plane, equation, offset);
	}

	 public void glDeleteBuffers (int n, int[] buffers, int offset) {
		gl.glDeleteBuffers(n, buffers, offset);
	}

	 public void glGenBuffers (int n, int[] buffers, int offset) {
		gl.glGenBuffers(n, buffers, offset);
	}

	 public void glGetBooleanv (int pname, boolean[] params, int offset) {
		gl.glGetBooleanv(pname, params, offset);
	}

	 public void glGetBufferParameteriv (int target, int pname, int[] params, int offset) {
		gl.glGetBufferParameteriv(target, pname, params, offset);
	}

	 public void glGetClipPlanef (int pname, float[] eqn, int offset) {
		gl.glGetClipPlanef(pname, eqn, offset);
	}

	 public void glGetFloatv (int pname, float[] params, int offset) {
		gl.glGetFloatv(pname, params, offset);
	}

	 public void glGetLightfv (int light, int pname, float[] params, int offset) {
		gl.glGetLightfv(light, pname, params, offset);
	}

	 public void glGetMaterialfv (int face, int pname, float[] params, int offset) {
		gl.glGetMaterialfv(face, pname, params, offset);
	}

	 public void glGetTexEnviv (int env, int pname, int[] params, int offset) {
		gl.glGetTexEnviv(env, pname, params, offset);
	}

	 public void glGetTexParameterfv (int target, int pname, float[] params, int offset) {
		gl.glGetTexParameterfv(target, pname, params, offset);
	}

	 public void glGetTexParameteriv (int target, int pname, int[] params, int offset) {
		gl.glGetTexParameteriv(target, pname, params, offset);
	}

	 public void glPointParameterfv (int pname, float[] params, int offset) {
		gl.glPointParameterfv(pname, params, offset);
	}

	 public void glTexEnviv (int target, int pname, int[] params, int offset) {
		gl.glTexEnviv(target, pname, params, offset);
	}

	 public void glTexParameterfv (int target, int pname, float[] params, int offset) {
		gl.glTexParameterfv(target, pname, params, offset);
	}

	 public void glTexParameteriv (int target, int pname, int[] params, int offset) {
		gl.glTexParameteriv(target, pname, params, offset);
	}

	 public void glColorPointer (int size, int type, int stride, int pointer) {
		gl.glColorPointer(size, type, stride, pointer);
	}

	 public void glNormalPointer (int type, int stride, int pointer) {
		gl.glNormalPointer(type, stride, pointer);
	}

	 public void glTexCoordPointer (int size, int type, int stride, int pointer) {
		gl.glTexCoordPointer(size, type, stride, pointer);
	}

	 public void glVertexPointer (int size, int type, int stride, int pointer) {
		gl.glVertexPointer(size, type, stride, pointer);
	}

	 public void glDrawElements (int mode, int count, int type, int indices) {
		gl.glDrawElements(mode, count, type, indices);
	}
}
