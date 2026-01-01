/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.cport;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import loon.cport.bridge.SDLCall;
import loon.opengl.GLExt;

public class CGL20 extends loon.opengl.GL20 implements GLExt {

	// Not in GLES 2.0
	@Override
	public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth,
			int border, int imageSize, Buffer data) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth,
			int border, int imageSize, int data) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width,
			int height, int depth, int format, int imageSize, Buffer data) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width,
			int height, int depth, int format, int imageSize, int data) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y,
			int width, int height) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glFramebufferTexture3D(int target, int attachment, int textarget, int texture, int level, int zoffset) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public int glGetBoundBuffer(int buffer) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glGetProgramBinary(int program, int bufSize, IntBuffer length, IntBuffer binaryFormat, Buffer binary) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public boolean glIsVBOArrayEnabled() {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public boolean glIsVBOElementEnabled() {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public ByteBuffer glMapBuffer(int target, int access) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glProgramBinary(int program, int binaryFormat, Buffer binary, int length) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glShaderBinary(int n, int[] shaders, int offset, int binaryformat, Buffer binary, int length) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public boolean isExtensionAvailable(String extension) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public boolean isFunctionAvailable(String function) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glTexImage3D(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
			Buffer arg9) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glTexImage3D(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
			int arg9) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height,
			int depth, int format, int type, Buffer pixels) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height,
			int depth, int format, int type, int pixels) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public boolean glUnmapBuffer(int target) {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public String getPlatformGLExtensions() {
		throw new RuntimeException("NYI - not in CPort.");
	}

	@Override
	public int getSwapInterval() {
		throw new RuntimeException("NYI - not in CPort.");
	}

	public CGL20() {
		this(false);
	}

	public CGL20(final boolean checkErrors) {
		super(new Buffers() {
			@Override
			public ByteBuffer createByteBuffer(int size) {
				ByteBuffer buffer = ByteBuffer.allocateDirect(size);
				buffer.order(ByteOrder.nativeOrder());
				return buffer;
			}
		}, checkErrors);
	}

	@Override
	public void glActiveTexture(int texture) {
		SDLCall.glActiveTexture(texture);
	}

	@Override
	public void glAttachShader(int program, int shader) {
		SDLCall.glAttachShader(program, shader);
	}

	@Override
	public void glBindAttribLocation(int program, int index, String name) {
		SDLCall.glBindAttribLocation(program, index, name);
	}

	@Override
	public void glBindBuffer(int target, int buffer) {
		SDLCall.glBindBuffer(target, buffer);
	}

	@Override
	public void glBindFramebuffer(int target, int framebuffer) {
		SDLCall.glBindFramebuffer(target, framebuffer);
	}

	@Override
	public void glBindRenderbuffer(int target, int renderbuffer) {
		SDLCall.glBindRenderbuffer(target, renderbuffer);
	}

	@Override
	public void glBindTexture(int target, int texture) {
		SDLCall.glBindTexture(target, texture);
	}

	@Override
	public void glBlendColor(float red, float green, float blue, float alpha) {
		SDLCall.glBlendColor(red, green, blue, alpha);
	}

	@Override
	public void glBlendEquation(int mode) {
		SDLCall.glBlendEquation(mode);
	}

	@Override
	public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
		SDLCall.glBlendEquationSeparate(modeRGB, modeAlpha);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		SDLCall.glBlendFunc(sfactor, dfactor);
	}

	@Override
	public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
		SDLCall.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	@Override
	public void glBufferData(int target, int size, Buffer data, int usage) {
		SDLCall.glBufferData(target, size, data, usage);
	}

	@Override
	public void glBufferSubData(int target, int offset, int size, Buffer data) {
		SDLCall.glBufferSubData(target, offset, size, data);
	}

	@Override
	public int glCheckFramebufferStatus(int target) {
		return SDLCall.glCheckFramebufferStatus(target);
	}

	@Override
	public void glClear(int mask) {
		SDLCall.glClear(mask);
	}

	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
		SDLCall.glClearColor(red, green, blue, alpha);
	}

	@Override
	public void glClearDepthf(float depth) {
		SDLCall.glClearDepthf(depth);
	}

	@Override
	public void glClearStencil(int s) {
		SDLCall.glClearStencil(s);
	}

	@Override
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		SDLCall.glColorMask(red, green, blue, alpha);
	}

	@Override
	public void glCompileShader(int shader) {
		SDLCall.glCompileShader(shader);
	}

	@Override
	public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
			int imageSize, Buffer data) {
		SDLCall.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
	}

	@Override
	public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height,
			int format, int imageSize, Buffer data) {
		SDLCall.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
	}

	@Override
	public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height,
			int border) {
		SDLCall.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}

	@Override
	public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width,
			int height) {
		SDLCall.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	@Override
	public int glCreateProgram() {
		return SDLCall.glCreateProgram();
	}

	@Override
	public int glCreateShader(int type) {
		return SDLCall.glCreateShader(type);
	}

	@Override
	public void glCullFace(int mode) {
		SDLCall.glCullFace(mode);
	}

	@Override
	public void glDeleteBuffers(int n, IntBuffer buffers) {
		SDLCall.glDeleteBuffers(n, buffers);
	}

	@Override
	public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
		SDLCall.glDeleteFramebuffers(n, framebuffers);
	}

	@Override
	public void glDeleteProgram(int program) {
		SDLCall.glDeleteProgram(program);
	}

	@Override
	public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
		SDLCall.glDeleteRenderbuffers(n, renderbuffers);
	}

	@Override
	public void glDeleteShader(int shader) {
		SDLCall.glDeleteShader(shader);
	}

	@Override
	public void glDeleteTextures(int n, IntBuffer textures) {
		SDLCall.glDeleteTextures(n, textures);
	}

	@Override
	public void glDepthFunc(int func) {
		SDLCall.glDepthFunc(func);
	}

	@Override
	public void glDepthMask(boolean flag) {
		SDLCall.glDepthMask(flag);
	}

	@Override
	public void glDepthRangef(float zNear, float zFar) {
		SDLCall.glDepthRangef(zNear, zFar);
	}

	@Override
	public void glDetachShader(int program, int shader) {
		SDLCall.glDetachShader(program, shader);
	}

	@Override
	public void glDisable(int cap) {
		SDLCall.glDisable(cap);
	}

	@Override
	public void glDisableVertexAttribArray(int index) {
		SDLCall.glDisableVertexAttribArray(index);
	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		SDLCall.glDrawArrays(mode, first, count);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		SDLCall.glDrawElements(mode, count, type, indices);
	}

	@Override
	public void glEnable(int cap) {
		SDLCall.glEnable(cap);
	}

	@Override
	public void glEnableVertexAttribArray(int index) {
		SDLCall.glEnableVertexAttribArray(index);
	}

	@Override
	public void glFinish() {
		SDLCall.glFinish();
	}

	@Override
	public void glFlush() {
		SDLCall.glFlush();
	}

	@Override
	public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
		SDLCall.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
	}

	@Override
	public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		SDLCall.glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}

	@Override
	public void glFrontFace(int mode) {
		SDLCall.glFrontFace(mode);
	}

	@Override
	public void glGenBuffers(int n, IntBuffer buffers) {
		SDLCall.glGenBuffers(n, buffers);
	}

	@Override
	public void glGenFramebuffers(int n, IntBuffer framebuffers) {
		SDLCall.glGenFramebuffers(n, framebuffers);
	}

	@Override
	public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
		SDLCall.glGenRenderbuffers(n, renderbuffers);
	}

	@Override
	public void glGenTextures(int n, IntBuffer textures) {
		SDLCall.glGenTextures(n, textures);
	}

	@Override
	public void glGenerateMipmap(int target) {
		SDLCall.glGenerateMipmap(target);
	}

	@Override
	public int glGetAttribLocation(int program, String name) {
		return SDLCall.glGetAttribLocation(program, name);
	}

	@Override
	public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
		SDLCall.glGetBufferParameteriv(target, pname, params);
	}

	@Override
	public int glGetError() {
		return SDLCall.glGetError();
	}

	@Override
	public void glGetFloatv(int pname, FloatBuffer params) {
		SDLCall.glGetFloatv(pname, params);
	}

	@Override
	public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
		SDLCall.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
	}

	@Override
	public void glGetIntegerv(int pname, IntBuffer params) {
		SDLCall.glGetIntegerv(pname, params);
	}

	@Override
	public String glGetProgramInfoLog(int program) {
		return SDLCall.glGetProgramInfoLog(program);
	}

	@Override
	public void glGetProgramiv(int program, int pname, IntBuffer params) {
		SDLCall.glGetProgramiv(program, pname, params);
	}

	@Override
	public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
		SDLCall.glGetRenderbufferParameteriv(target, pname, params);
	}

	@Override
	public String glGetShaderInfoLog(int shader) {
		return SDLCall.glGetShaderInfoLog(shader);
	}

	@Override
	public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
		glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
	}

	@Override
	public void glGetShaderiv(int shader, int pname, IntBuffer params) {
		SDLCall.glGetShaderiv(shader, pname, params);
	}

	@Override
	public String glGetString(int name) {
		return SDLCall.glGetString(name);
	}

	@Override
	public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
		SDLCall.glGetTexParameterfv(target, pname, params);
	}

	@Override
	public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
		SDLCall.glGetTexParameteriv(target, pname, params);
	}

	@Override
	public int glGetUniformLocation(int program, String name) {
		return SDLCall.glGetUniformLocation(program, name);
	}

	@Override
	public void glGetUniformfv(int program, int location, FloatBuffer params) {
		SDLCall.glGetUniformfv(program, location, params);
	}

	@Override
	public void glGetUniformiv(int program, int location, IntBuffer params) {
		SDLCall.glGetUniformiv(program, location, params);
	}

	@Override
	public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
		SDLCall.glGetVertexAttribfv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
		SDLCall.glGetVertexAttribiv(index, pname, params);
	}

	@Override
	public void glHint(int target, int mode) {
		SDLCall.glHint(target, mode);
	}

	@Override
	public boolean glIsBuffer(int buffer) {
		return SDLCall.glIsBuffer(buffer);
	}

	@Override
	public boolean glIsEnabled(int cap) {
		return SDLCall.glIsEnabled(cap);
	}

	@Override
	public boolean glIsFramebuffer(int framebuffer) {
		return SDLCall.glIsFramebuffer(framebuffer);
	}

	@Override
	public boolean glIsProgram(int program) {
		return SDLCall.glIsProgram(program);
	}

	@Override
	public boolean glIsRenderbuffer(int renderbuffer) {
		return SDLCall.glIsRenderbuffer(renderbuffer);
	}

	@Override
	public boolean glIsShader(int shader) {
		return SDLCall.glIsShader(shader);
	}

	@Override
	public boolean glIsTexture(int texture) {
		return SDLCall.glIsTexture(texture);
	}

	@Override
	public void glLineWidth(float width) {
		SDLCall.glLineWidth(width);
	}

	@Override
	public void glLinkProgram(int program) {
		SDLCall.glLinkProgram(program);
	}

	@Override
	public void glPixelStorei(int pname, int param) {
		SDLCall.glPixelStorei(pname, param);
	}

	@Override
	public void glPolygonOffset(float factor, float units) {
		SDLCall.glPolygonOffset(factor, units);
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
		SDLCall.glReadPixels(x, y, width, height, format, type, pixels);
	}

	@Override
	public void glReleaseShaderCompiler() {
		SDLCall.glReleaseShaderCompiler();
	}

	@Override
	public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
		SDLCall.glRenderbufferStorage(target, internalformat, width, height);
	}

	@Override
	public void glSampleCoverage(float value, boolean invert) {
		SDLCall.glSampleCoverage(value, invert);
	}

	@Override
	public void glScissor(int x, int y, int width, int height) {
		SDLCall.glScissor(x, y, width, height);
	}

	@Override
	public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
		SDLCall.glShaderBinary(n, shaders, binaryformat, binary, length);
	}

	@Override
	public void glStencilFunc(int func, int ref, int mask) {
		SDLCall.glStencilFunc(func, ref, mask);
	}

	@Override
	public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
		SDLCall.glStencilFuncSeparate(face, func, ref, mask);
	}

	@Override
	public void glStencilMask(int mask) {
		SDLCall.glStencilMask(mask);
	}

	@Override
	public void glStencilMaskSeparate(int face, int mask) {
		SDLCall.glStencilMaskSeparate(face, mask);
	}

	@Override
	public void glStencilOp(int fail, int zfail, int zpass) {
		SDLCall.glStencilOp(fail, zfail, zpass);
	}

	@Override
	public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
		SDLCall.glStencilOpSeparate(face, fail, zfail, zpass);
	}

	@Override
	public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format,
			int type, Buffer pixels) {
		SDLCall.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	@Override
	public void glTexParameterf(int target, int pname, float param) {
		SDLCall.glTexParameterf(target, pname, param);
	}

	@Override
	public void glTexParameterfv(int target, int pname, FloatBuffer params) {
		SDLCall.glTexParameterfv(target, pname, params);
	}

	@Override
	public void glTexParameteri(int target, int pname, int param) {
		SDLCall.glTexParameteri(target, pname, param);
	}

	@Override
	public void glTexParameteriv(int target, int pname, IntBuffer params) {
		SDLCall.glTexParameteriv(target, pname, params);
	}

	@Override
	public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
			int type, Buffer pixels) {
		SDLCall.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	@Override
	public void glUniform1f(int location, float x) {
		SDLCall.glUniform1f(location, x);
	}

	@Override
	public void glUniform1fv(int location, int count, FloatBuffer buffer) {
		SDLCall.glUniform1fv(location, count, buffer);
	}

	@Override
	public void glUniform1i(int location, int x) {
		SDLCall.glUniform1i(location, x);
	}

	@Override
	public void glUniform1iv(int location, int count, IntBuffer buffer) {
		SDLCall.glUniform1iv(location, count, buffer);
	}

	@Override
	public void glUniform2f(int location, float x, float y) {
		SDLCall.glUniform2f(location, x, y);
	}

	@Override
	public void glUniform2fv(int location, int count, FloatBuffer buffer) {
		SDLCall.glUniform2fv(location, count, buffer);
	}

	@Override
	public void glUniform2i(int location, int x, int y) {
		SDLCall.glUniform2i(location, x, y);
	}

	@Override
	public void glUniform2iv(int location, int count, IntBuffer buffer) {
		SDLCall.glUniform2iv(location, count, buffer);
	}

	@Override
	public void glUniform3f(int location, float x, float y, float z) {
		SDLCall.glUniform3f(location, x, y, z);
	}

	@Override
	public void glUniform3fv(int location, int count, FloatBuffer buffer) {
		SDLCall.glUniform3fv(location, count, buffer);
	}

	@Override
	public void glUniform3i(int location, int x, int y, int z) {
		SDLCall.glUniform3i(location, x, y, z);
	}

	@Override
	public void glUniform3iv(int location, int count, IntBuffer buffer) {
		SDLCall.glUniform3iv(location, count, buffer);
	}

	@Override
	public void glUniform4f(int location, float x, float y, float z, float w) {
		SDLCall.glUniform4f(location, x, y, z, w);
	}

	@Override
	public void glUniform4fv(int location, int count, FloatBuffer buffer) {
		SDLCall.glUniform4fv(location, count, buffer);
	}

	@Override
	public void glUniform4i(int location, int x, int y, int z, int w) {
		SDLCall.glUniform4i(location, x, y, z, w);
	}

	@Override
	public void glUniform4iv(int location, int count, IntBuffer buffer) {
		SDLCall.glUniform4iv(location, count, buffer);
	}

	@Override
	public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer buffer) {
		SDLCall.glUniformMatrix2fv(location, count, transpose, buffer);
	}

	@Override
	public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer buffer) {
		SDLCall.glUniformMatrix3fv(location, count, transpose, buffer);
	}

	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer buffer) {
		SDLCall.glUniformMatrix4fv(location, count, transpose, buffer);
	}

	@Override
	public void glUseProgram(int program) {
		SDLCall.glUseProgram(program);
	}

	@Override
	public void glValidateProgram(int program) {
		SDLCall.glValidateProgram(program);
	}

	@Override
	public void glVertexAttrib1f(int indx, float x) {
		SDLCall.glVertexAttrib1f(indx, x);
	}

	@Override
	public void glVertexAttrib1fv(int indx, FloatBuffer values) {
		SDLCall.glVertexAttrib1f(indx, values.get());
	}

	@Override
	public void glVertexAttrib2f(int indx, float x, float y) {
		SDLCall.glVertexAttrib2f(indx, x, y);
	}

	@Override
	public void glVertexAttrib2fv(int indx, FloatBuffer values) {
		SDLCall.glVertexAttrib2f(indx, values.get(), values.get());
	}

	@Override
	public void glVertexAttrib3f(int indx, float x, float y, float z) {
		SDLCall.glVertexAttrib3f(indx, x, y, z);
	}

	@Override
	public void glVertexAttrib3fv(int indx, FloatBuffer values) {
		SDLCall.glVertexAttrib3f(indx, values.get(), values.get(), values.get());
	}

	@Override
	public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
		SDLCall.glVertexAttrib4f(indx, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4fv(int indx, FloatBuffer values) {
		SDLCall.glVertexAttrib4f(indx, values.get(), values.get(), values.get(), values.get());
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
		SDLCall.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		SDLCall.glViewport(x, y, width, height);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, int offset) {
		SDLCall.glDrawElementsOffset(mode, count, type, offset);
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset) {
		SDLCall.glVertexAttribPointerOffset(indx, size, type, normalized, stride, offset);
	}

	@Override
	public void glClearDepth(double depth) {
		SDLCall.glClearDepthf((float) depth);
	}

	@Override
	public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
			int data_imageSize, int data) {
		SDLCall.glCompressedTexImage2DOffset(target, level, internalformat, width, height, border, data_imageSize,
				data);
	}

	@Override
	public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height,
			int format, int data_imageSize, int data) {
		SDLCall.glCompressedTexSubImage2DOffset(target, level, xoffset, yoffset, width, height, format, data_imageSize,
				data);
	}

	@Override
	public void glDepthRange(double zNear, double zFar) {
		SDLCall.glDepthRangef((float) zNear, (float) zFar);
	}

	@Override
	public void glGetActiveAttrib(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size,
			int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
		bufs.resizeIntBuffer(2);
		final String nameString = SDLCall.glGetActiveAttrib(program, index, IntBuffer.allocate(bufsize),
				bufs.intBuffer);
		try {
			final byte[] nameBytes = nameString.getBytes("UTF-8");
			final int nameLength = nameBytes.length - nameOffset;
			bufs.setByteBuffer(nameBytes, nameOffset, nameLength);
			bufs.byteBuffer.get(name, nameOffset, nameLength);
			length[lengthOffset] = nameLength;
		} catch (Exception e) {
			e.printStackTrace();
		}
		bufs.intBuffer.get(size, 0, 1);
		bufs.intBuffer.get(type, 0, 1);
	}

	@Override
	public void glGetActiveUniform(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size,
			int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
		bufs.resizeIntBuffer(2);
		// Return name, length
		final String nameString = SDLCall.glGetActiveUniform(program, index, IntBuffer.allocate(256), bufs.intBuffer);
		try {
			final byte[] nameBytes = nameString.getBytes("UTF-8");
			final int nameLength = nameBytes.length - nameOffset;
			bufs.setByteBuffer(nameBytes, nameOffset, nameLength);
			bufs.byteBuffer.get(name, nameOffset, nameLength);
			length[lengthOffset] = nameLength;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Return size, type
		bufs.intBuffer.get(size, 0, 1);
		bufs.intBuffer.get(type, 0, 1);
	}

	@Override
	public void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders) {
		SDLCall.glGetAttachedShaders(program, maxcount, count, shaders);
	}

	@Override
	public boolean glGetBoolean(int pname) {
		return SDLCall.glGetBooleanvResult(pname);
	}

	@Override
	public void glGetBooleanv(int pname, ByteBuffer params) {
		SDLCall.glGetBooleanv(pname, params);
	}

	@Override
	public float glGetFloat(int pname) {
		return SDLCall.glFloatvResult(pname);
	}

	@Override
	public int glGetInteger(int pname) {
		return SDLCall.glIntegervResult(pname);
	}

	@Override
	public void glGetProgramInfoLog(int program, int bufsize, IntBuffer length, ByteBuffer infolog) {
		SDLCall.glGetProgramInfoLogs(program, bufsize, length, infolog);
	}

	@Override
	public void glGetShaderInfoLog(int shader, int bufsize, IntBuffer length, ByteBuffer infolog) {
		SDLCall.glGetShaderInfoLogs(shader, bufsize, length, infolog);
	}

	@Override
	public void glShaderSource(int shader, int count, String[] strings, int[] length, int lengthOffset) {
		if (strings == null || length == null) {
			return;
		}
		if (length.length == 1) {
			for (final String str : strings) {
				SDLCall.glShaderSource(shader, str);
			}
			return;
		}
		int totalLength = 0;
		for (int i = lengthOffset; i < length.length; i++) {
			totalLength += length[i];
		}
		final StringBuilder builder = new StringBuilder(totalLength);
		for (int j = 0; j < count; j++) {
			builder.append(strings[j], 0, length[j]);
		}
		SDLCall.glShaderSource(shader, builder.toString());
	}

	@Override
	public void glShaderSource(int shader, int count, String[] strings, IntBuffer length) {
		glShaderSource(shader, count, strings, length.array(), 0);
	}

	@Override
	public void glShaderSource(int shader, String string) {
		SDLCall.glShaderSource(shader, string);
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format, int type, int pixelsBufferOffset) {
		SDLCall.glReadPixelsOffset(x, y, width, height, format, type, pixelsBufferOffset);
	}

	@Override
	public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format,
			int type, int pixelsBufferOffset) {
		SDLCall.glTexImage2DOffset(target, level, internalformat, width, height, border, format, type,
				pixelsBufferOffset);
	}

	@Override
	public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
			int type, int pixelsBufferOffset) {
		SDLCall.glTexSubImage2DOffset(target, level, xoffset, yoffset, width, height, format, type, pixelsBufferOffset);
	}

	@Override
	public void glGetShaderSource(int shader, int bufsize, int[] length, int lengthOffset, byte[] source,
			int sourceOffset) {
		SDLCall.glGetShaderSource(shader, bufsize, IntBuffer.wrap(length).position(lengthOffset),
				ByteBuffer.wrap(source).position(sourceOffset));
	}

	@Override
	public void glGetShaderSource(int shader, int bufsize, IntBuffer length, ByteBuffer source) {
		SDLCall.glGetShaderSource(shader, bufsize, length, source);
	}

	@Override
	public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, int[] range, int rangeOffset,
			int[] precision, int precisionOffset) {
		SDLCall.glGetShaderPrecisionFormat(shadertype, precisiontype, IntBuffer.wrap(range).position(rangeOffset),
				IntBuffer.wrap(precision).position(precisionOffset));
	}

	@Override
	public String glGetActiveAttrib(int program, int index, IntBuffer size, Buffer type) {
		return SDLCall.glGetActiveAttrib(program, index, size, type);
	}

	@Override
	public String glGetActiveUniform(int program, int index, IntBuffer size, Buffer type) {
		return SDLCall.glGetActiveUniform(program, index, size, type);
	}

	@Override
	public int getGlslVersion() {
		return 120;
	}

	@Override
	public boolean hasGLSL() {
		return true;
	}

}
