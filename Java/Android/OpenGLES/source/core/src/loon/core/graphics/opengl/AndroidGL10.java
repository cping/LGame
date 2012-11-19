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
class AndroidGL10 implements GL10 {

	final javax.microedition.khronos.opengles.GL10 gl;

	public AndroidGL10(javax.microedition.khronos.opengles.GL10 gl) {
		this.gl = gl;
	}

	public final void glActiveTexture(int texture) {
		gl.glActiveTexture(texture);
	}

	public final void glAlphaFunc(int func, float ref) {
		gl.glAlphaFunc(func, ref);
	}

	public final void glBindTexture(int target, int texture) {
		gl.glBindTexture(target, texture);
	}

	public final void glBlendFunc(int sfactor, int dfactor) {
		gl.glBlendFunc(sfactor, dfactor);
	}

	public final void glClear(int mask) {
		gl.glClear(mask);
	}

	public final void glClearColor(float red, float green, float blue,
			float alpha) {
		gl.glClearColor(red, green, blue, alpha);
	}

	public final void glClearDepthf(float depth) {
		gl.glClearDepthf(depth);
	}

	public final void glClearStencil(int s) {
		gl.glClearStencil(s);
	}

	public final void glClientActiveTexture(int texture) {
		try {
			gl.glClientActiveTexture(texture);
		} catch (Throwable ex) {
		}
	}

	public final void glColor4f(float red, float green, float blue, float alpha) {
		gl.glColor4f(red, green, blue, alpha);
	}

	public final void glColorMask(boolean red, boolean green, boolean blue,
			boolean alpha) {
		gl.glColorMask(red, green, blue, alpha);
	}

	public final void glColorPointer(int size, int type, int stride,
			Buffer pointer) {
		gl.glColorPointer(size, type, stride, pointer);
	}

	public final void glCompressedTexImage2D(int target, int level,
			int internalformat, int width, int height, int border,
			int imageSize, Buffer data) {
		gl.glCompressedTexImage2D(target, level, internalformat, width, height,
				border, imageSize, data);
	}

	public final void glCompressedTexSubImage2D(int target, int level,
			int xoffset, int yoffset, int width, int height, int format,
			int imageSize, Buffer data) {
		gl.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width,
				height, format, imageSize, data);
	}

	public final void glCopyTexImage2D(int target, int level,
			int internalformat, int x, int y, int width, int height, int border) {
		gl.glCopyTexImage2D(target, level, internalformat, x, y, width, height,
				border);
	}

	public final void glCopyTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int x, int y, int width, int height) {
		gl.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width,
				height);
	}

	public final void glCullFace(int mode) {
		gl.glCullFace(mode);
	}

	public final void glDeleteTextures(int n, IntBuffer textures) {
		gl.glDeleteTextures(n, textures);
	}

	public final void glDepthFunc(int func) {
		gl.glDepthFunc(func);
	}

	public final void glDepthMask(boolean flag) {
		gl.glDepthMask(flag);
	}

	public final void glDepthRangef(float zNear, float zFar) {
		gl.glDepthRangef(zNear, zFar);
	}

	public final void glDisable(int cap) {
		gl.glDisable(cap);
	}

	public final void glDisableClientState(int array) {
		gl.glDisableClientState(array);
	}

	public final void glDrawArrays(int mode, int first, int count) {
		gl.glDrawArrays(mode, first, count);
	}

	public final void glDrawElements(int mode, int count, int type,
			Buffer indices) {
		gl.glDrawElements(mode, count, type, indices);
	}

	public final void glEnable(int cap) {
		gl.glEnable(cap);
	}

	public final void glEnableClientState(int array) {
		gl.glEnableClientState(array);
	}

	public final void glFinish() {
		gl.glFinish();
	}

	public final void glFlush() {
		gl.glFlush();
	}

	public final void glFogf(int pname, float param) {
		gl.glFogf(pname, param);
	}

	public final void glFogfv(int pname, FloatBuffer params) {
		gl.glFogfv(pname, params);
	}

	public final void glFrontFace(int mode) {
		gl.glFrontFace(mode);
	}

	public final void glFrustumf(float left, float right, float bottom,
			float top, float zNear, float zFar) {
		gl.glFrustumf(left, right, bottom, top, zNear, zFar);
	}

	public final void glGenTextures(int n, IntBuffer textures) {
		gl.glGenTextures(n, textures);
	}

	public final int glGenTextures() {
		int[] id = new int[1];
		glGenTextures(1, id, 0);
		int tex = id[0];
		return tex;
	}

	public final int glGetError() {
		return gl.glGetError();
	}

	public final void glGetIntegerv(int pname, IntBuffer params) {
		gl.glGetIntegerv(pname, params);
	}

	public final String glGetString(int name) {
		return gl.glGetString(name);
	}

	public final void glHint(int target, int mode) {
		gl.glHint(target, mode);
	}

	public final void glLightModelf(int pname, float param) {
		gl.glLightModelf(pname, param);
	}

	public final void glLightModelfv(int pname, FloatBuffer params) {
		gl.glLightModelfv(pname, params);
	}

	public final void glLightf(int light, int pname, float param) {
		gl.glLightf(light, pname, param);
	}

	public final void glLightfv(int light, int pname, FloatBuffer params) {
		gl.glLightfv(light, pname, params);
	}

	public final void glLineWidth(float width) {
		gl.glLineWidth(width);
	}

	public final void glLoadIdentity() {
		gl.glLoadIdentity();
	}

	public final void glLoadMatrixf(FloatBuffer m) {
		gl.glLoadMatrixf(m);
	}

	public final void glLogicOp(int opcode) {
		gl.glLogicOp(opcode);
	}

	public final void glMaterialf(int face, int pname, float param) {
		gl.glMaterialf(face, pname, param);
	}

	public final void glMaterialfv(int face, int pname, FloatBuffer params) {
		gl.glMaterialfv(face, pname, params);
	}

	public final void glMatrixMode(int mode) {
		gl.glMatrixMode(mode);
	}

	public final void glMultMatrixf(FloatBuffer m) {
		gl.glMultMatrixf(m);
	}

	public final void glMultiTexCoord4f(int target, float s, float t, float r,
			float q) {
		gl.glMultiTexCoord4f(target, s, t, r, q);
	}

	public final void glNormal3f(float nx, float ny, float nz) {
		gl.glNormal3f(nx, ny, nz);
	}

	public final void glNormalPointer(int type, int stride, Buffer pointer) {
		gl.glNormalPointer(type, stride, pointer);
	}

	public final void glOrthof(float left, float right, float bottom,
			float top, float zNear, float zFar) {
		gl.glOrthof(left, right, bottom, top, zNear, zFar);
	}
	
	public final void glOrthox(int left, int right, int bottom,
			int top, int zNear, int zFar) {
		gl.glOrthox(left, right, bottom, top, zNear, zFar);
	}
	
	public final void glPixelStorei(int pname, int param) {
		gl.glPixelStorei(pname, param);
	}

	public final void glPointSize(float size) {
		gl.glPointSize(size);
	}

	public final void glPolygonOffset(float factor, float units) {
		gl.glPolygonOffset(factor, units);
	}

	public final void glPopMatrix() {
		gl.glPopMatrix();
	}

	public final void glPushMatrix() {
		gl.glPushMatrix();
	}

	public final void glReadPixels(int x, int y, int width, int height,
			int format, int type, Buffer pixels) {
		gl.glReadPixels(x, y, width, height, format, type, pixels);
	}

	public final void glRotatef(float angle, float x, float y, float z) {
		gl.glRotatef(angle, x, y, z);
	}

	public final void glSampleCoverage(float value, boolean invert) {
		gl.glSampleCoverage(value, invert);
	}

	public final void glScalef(float x, float y, float z) {
		gl.glScalef(x, y, z);
	}

	public final void glScissor(int x, int y, int width, int height) {
		gl.glScissor(x, y, width, height);
	}

	public final void glShadeModel(int mode) {
		gl.glShadeModel(mode);
	}

	public final void glStencilFunc(int func, int ref, int mask) {
		gl.glStencilFunc(func, ref, mask);
	}

	public final void glStencilMask(int mask) {
		gl.glStencilMask(mask);
	}

	public final void glStencilOp(int fail, int zfail, int zpass) {
		gl.glStencilOp(fail, zfail, zpass);
	}

	public final void glTexCoordPointer(int size, int type, int stride,
			Buffer pointer) {
		gl.glTexCoordPointer(size, type, stride, pointer);
	}

	public final void glTexEnvf(int target, int pname, float param) {
		gl.glTexEnvf(target, pname, param);
	}

	public final void glTexEnvfv(int target, int pname, FloatBuffer params) {
		gl.glTexEnvfv(target, pname, params);
	}

	public final void glTexImage2D(int target, int level, int internalformat,
			int width, int height, int border, int format, int type,
			Buffer pixels) {
		gl.glTexImage2D(target, level, internalformat, width, height, border,
				format, type, pixels);
	}

	public final void glTexParameterf(int target, int pname, float param) {
		gl.glTexParameterf(target, pname, param);
	}

	public final void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			Buffer pixels) {
		gl.glTexSubImage2D(target, level, xoffset, yoffset, width, height,
				format, type, pixels);
	}

	public final void glTranslatef(float x, float y, float z) {
		gl.glTranslatef(x, y, z);
	}

	public final void glVertexPointer(int size, int type, int stride,
			Buffer pointer) {
		gl.glVertexPointer(size, type, stride, pointer);
	}

	public final void glViewport(int x, int y, int width, int height) {
		gl.glViewport(x, y, width, height);
	}

	public final void glDeleteTextures(int n, int[] textures, int offset) {
		gl.glDeleteTextures(n, textures, offset);
	}

	public final void glFogfv(int pname, float[] params, int offset) {
		gl.glFogfv(pname, params, offset);
	}

	public final void glGenTextures(int n, int[] textures, int offset) {
		gl.glGenTextures(n, textures, offset);
	}

	public final void glGetIntegerv(int pname, int[] params, int offset) {
		gl.glGenTextures(pname, params, offset);
	}

	public final void glLightfv(int light, int pname, float[] params, int offset) {
		gl.glLightfv(light, pname, params, offset);
	}

	public final void glLoadMatrixf(float[] m, int offset) {
		gl.glLoadMatrixf(m, offset);
	}

	public final void glMaterialfv(int face, int pname, float[] params,
			int offset) {
		gl.glMaterialfv(face, pname, params, offset);
	}

	public final void glMultMatrixf(float[] m, int offset) {
		gl.glMultMatrixf(m, offset);
	}

	public final void glTexEnvfv(int target, int pname, float[] params,
			int offset) {
		gl.glTexEnvfv(target, pname, params, offset);
	}

	public void glLightModelfv(int pname, float[] params, int offset) {
		gl.glLightModelfv(pname, params, offset);
	}

}
