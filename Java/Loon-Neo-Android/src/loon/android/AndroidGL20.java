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
package loon.android;

import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import loon.opengl.GL20;
import android.opengl.GLES20;

public class AndroidGL20 extends GL20 {

	public AndroidGL20() {

		super(new Buffers() {
			public ByteBuffer createByteBuffer(int size) {
				return ByteBuffer.allocateDirect(size).order(
						ByteOrder.nativeOrder());
			}
		}, false);
	}

	@Override
	public String getPlatformGLExtensions() {
		String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
		return extensions;
	}

	@Override
	public int getSwapInterval() {
		return 0;
	}

	@Override
	public void glActiveTexture(int texture) {
		GLES20.glActiveTexture(texture);
	}

	@Override
	public void glAttachShader(int program, int shader) {
		GLES20.glAttachShader(program, shader);
	}

	@Override
	public void glBindAttribLocation(int program, int index, String name) {
		GLES20.glBindAttribLocation(program, index, name);
	}

	@Override
	public void glBindBuffer(int target, int buffer) {
		GLES20.glBindBuffer(target, buffer);
	}

	@Override
	public void glBindFramebuffer(int target, int framebuffer) {
		GLES20.glBindFramebuffer(target, framebuffer);
	}

	@Override
	public void glBindRenderbuffer(int target, int renderbuffer) {
		GLES20.glBindRenderbuffer(target, renderbuffer);
	}

	@Override
	public void glBindTexture(int target, int texture) {
		GLES20.glBindTexture(target, texture);
	}

	@Override
	public void glBlendColor(float red, float green, float blue, float alpha) {
		GLES20.glBlendColor(red, green, blue, alpha);
	}

	@Override
	public void glBlendEquation(int mode) throws RuntimeException {
		GLES20.glBlendEquation(mode);
	}

	@Override
	public void glBlendEquationSeparate(int modeRGB, int modeAlpha)
			throws RuntimeException {
		GLES20.glBlendEquationSeparate(modeRGB, modeAlpha);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		GLES20.glBlendFunc(sfactor, dfactor);
	}

	@Override
	public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha,
			int dstAlpha) throws RuntimeException {
		GLES20.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	@Override
	public void glBufferData(int target, int size, Buffer data, int usage) {
		GLES20.glBufferData(target, size, data, usage);

	}

	@Override
	public void glBufferSubData(int target, int offset, int size, Buffer data) {
		GLES20.glBufferSubData(target, offset, size, data);

	}

	@Override
	public int glCheckFramebufferStatus(int target) {
		return GLES20.glCheckFramebufferStatus(target);
	}

	@Override
	public void glClear(int mask) {
		GLES20.glClear(mask);

	}

	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
		GLES20.glClearColor(red, green, blue, alpha);
	}

	@Override
	public void glClearDepth(double depth) {
		GLES20.glClearDepthf((float) depth);

	}

	@Override
	public void glClearDepthf(float depth) {
		GLES20.glClearDepthf(depth);

	}

	@Override
	public void glClearStencil(int s) {
		GLES20.glClearStencil(s);
	}

	@Override
	public void glColorMask(boolean red, boolean green, boolean blue,
			boolean alpha) {
		GLES20.glColorMask(red, green, blue, alpha);
	}

	@Override
	public void glCompileShader(int shader) {
		GLES20.glCompileShader(shader);
	}

	@Override
	public void glCompressedTexImage2D(int target, int level,
			int internalformat, int width, int height, int border,
			int imageSize, Buffer data) {
		GLES20.glCompressedTexImage2D(target, level, internalformat, width,
				height, border, imageSize, data);
	}

	@Override
	public void glCompressedTexImage2D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7) throws RuntimeException {
		throw new RuntimeException(
				"glCompressedTexImage2D(int, int, int, int, int, int, int, int)"
						+ "not supported.");
	}

	@Override
	public void glCompressedTexImage3D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, Buffer arg8) {
		throw new RuntimeException("glCompressedTexImage3D not supported.");
	}

	@Override
	public void glCompressedTexImage3D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8)
			throws RuntimeException {
		throw new RuntimeException("glCompressedTexImage3D not supported.");
	}

	@Override
	public void glCompressedTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int imageSize,
			Buffer data) {
		GLES20.glCompressedTexSubImage2D(target, level, xoffset, yoffset,
				width, height, format, imageSize, data);
	}

	@Override
	public void glCompressedTexSubImage2D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, int arg8)
			throws RuntimeException {
		throw new RuntimeException(
				"glCompressedSubTexImage2D(int, int, int, int, int, int, int, int, int)"
						+ "not supported.");
	}

	@Override
	public void glCompressedTexSubImage3D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
			int arg9, Buffer arg10) throws RuntimeException {
		throw new RuntimeException("glCompressedTexSubImage3D not supported.");
	}

	@Override
	public void glCompressedTexSubImage3D(int arg0, int arg1, int arg2,
			int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
			int arg9, int arg10) throws RuntimeException {
		throw new RuntimeException("glCompressedTexSubImage3D not supported.");
	}

	@Override
	public void glCopyTexImage2D(int target, int level, int internalformat,
			int x, int y, int width, int height, int border) {
		GLES20.glCopyTexImage2D(target, level, internalformat, x, y, width,
				height, border);
	}

	@Override
	public void glCopyTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int x, int y, int width, int height) {
		GLES20.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y,
				width, height);
	}

	@Override
	public void glCopyTexSubImage3D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8)
			throws RuntimeException {
		throw new RuntimeException("glCopyTexSubImage3D not supported.");
	}

	@Override
	public int glCreateProgram() {
		return GLES20.glCreateProgram();
	}

	@Override
	public int glCreateShader(int type) {
		return GLES20.glCreateShader(type);
	}

	@Override
	public void glCullFace(int mode) {
		GLES20.glCullFace(mode);
	}

	@Override
	public void glDeleteBuffers(int n, int[] buffers, int offset) {
		GLES20.glDeleteBuffers(n, buffers, offset);
	}

	@Override
	public void glDeleteBuffers(int n, IntBuffer buffers) {
		GLES20.glDeleteBuffers(n, buffers);
	}

	@Override
	public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
		GLES20.glDeleteFramebuffers(n, framebuffers, offset);
	}

	@Override
	public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
		GLES20.glDeleteFramebuffers(n, framebuffers);
	}

	@Override
	public void glDeleteProgram(int program) {
		GLES20.glDeleteProgram(program);
	}

	@Override
	public void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
		GLES20.glDeleteRenderbuffers(n, renderbuffers, offset);
	}

	@Override
	public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
		GLES20.glDeleteRenderbuffers(n, renderbuffers);
	}

	@Override
	public void glDeleteShader(int shader) {
		GLES20.glDeleteShader(shader);
	}

	@Override
	public void glDeleteTextures(int n, int[] textures, int offset) {
		GLES20.glDeleteTextures(n, textures, offset);
	}

	@Override
	public void glDeleteTextures(int n, IntBuffer textures) {
		GLES20.glDeleteTextures(n, textures);
	}

	@Override
	public void glDepthFunc(int func) {
		GLES20.glDepthFunc(func);
	}

	@Override
	public void glDepthMask(boolean flag) {
		GLES20.glDepthMask(flag);
	}

	@Override
	public void glDepthRange(double zNear, double zFar) {
		GLES20.glDepthRangef((float) zNear, (float) zFar);

	}

	@Override
	public void glDepthRangef(float zNear, float zFar) {
		GLES20.glDepthRangef(zNear, zFar);
	}

	@Override
	public void glDetachShader(int program, int shader) {
		GLES20.glDetachShader(program, shader);
	}

	@Override
	public void glDisable(int cap) {
		GLES20.glDisable(cap);
	}

	@Override
	public void glDisableVertexAttribArray(int index) {
		GLES20.glDisableVertexAttribArray(index);
	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		GLES20.glDrawArrays(mode, first, count);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		GLES20.glDrawElements(mode, count, type, indices);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, int offset) {
		GLES20.glDrawElements(mode, count, type, offset);
	}

	@Override
	public void glEnable(int cap) {
		GLES20.glEnable(cap);
	}

	@Override
	public void glEnableVertexAttribArray(int index) {
		GLES20.glEnableVertexAttribArray(index);
	}

	@Override
	public void glFinish() {
		GLES20.glFinish();
	}

	@Override
	public void glFlush() {
		GLES20.glFlush();
	}

	@Override
	public void glFramebufferRenderbuffer(int target, int attachment,
			int renderbuffertarget, int renderbuffer) {
		GLES20.glFramebufferRenderbuffer(target, attachment,
				renderbuffertarget, renderbuffer);
	}

	@Override
	public void glFramebufferTexture2D(int target, int attachment,
			int textarget, int texture, int level) {
		GLES20.glFramebufferTexture2D(target, attachment, textarget, texture,
				level);
	}

	@Override
	public void glFramebufferTexture3D(int target, int attachment,
			int textarget, int texture, int level, int zoffset) {
		GLES20.glFramebufferTexture2D(target, attachment, textarget, texture,
				level);
	}

	@Override
	public void glFrontFace(int mode) {
		GLES20.glFrontFace(mode);
	}

	@Override
	public void glGenBuffers(int n, int[] buffers, int offset) {
		GLES20.glGenBuffers(n, buffers, offset);
	}

	@Override
	public void glGenBuffers(int n, IntBuffer buffers) {
		GLES20.glGenBuffers(n, buffers);
	}

	@Override
	public void glGenerateMipmap(int target) {
		GLES20.glGenerateMipmap(target);
	}

	@Override
	public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
		GLES20.glGenFramebuffers(n, framebuffers, offset);
	}

	@Override
	public void glGenFramebuffers(int n, IntBuffer framebuffers) {
		GLES20.glGenFramebuffers(n, framebuffers);
	}

	@Override
	public void glGenRenderbuffers(int n, int[] renderbuffers, int offset) {
		GLES20.glGenRenderbuffers(n, renderbuffers, offset);
	}

	@Override
	public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
		GLES20.glGenRenderbuffers(n, renderbuffers);
	}

	@Override
	public void glGenTextures(int n, int[] textures, int offset) {
		GLES20.glGenTextures(n, textures, offset);
	}

	@Override
	public void glGenTextures(int n, IntBuffer textures) {
		GLES20.glGenTextures(n, textures);
	}

	@Override
	public void glGetActiveAttrib(int program, int index, int bufsize,
			int[] length, int lengthOffset, int[] size, int sizeOffset,
			int[] type, int typeOffset, byte[] name, int nameOffset) {
		GLES20.glGetActiveAttrib(program, index, bufsize, length, lengthOffset,
				size, sizeOffset, type, typeOffset, name, nameOffset);
	}

	@Override
	public void glGetActiveAttrib(int program, int index, int bufsize,
			IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
		int namePos = name.position();
		GLES20.glGetActiveAttrib(program, index, bufsize, length, size, type,
				name.get());
		name.position(namePos);
	}

	@Override
	public void glGetActiveUniform(int program, int index, int bufsize,
			int[] length, int lengthOffset, int[] size, int sizeOffset,
			int[] type, int typeOffset, byte[] name, int nameOffset) {
		GLES20.glGetActiveUniform(program, index, bufsize, length,
				lengthOffset, size, sizeOffset, type, typeOffset, name,
				nameOffset);
	}

	@Override
	public void glGetActiveUniform(int program, int index, int bufsize,
			IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
		int namePos = name.position();
		GLES20.glGetActiveUniform(program, index, bufsize, length, size, type,
				name.get());
		name.position(namePos);
	}

	@Override
	public void glGetAttachedShaders(int program, int maxcount, int[] count,
			int countOffset, int[] shaders, int shadersOffset) {
		GLES20.glGetAttachedShaders(program, maxcount, count, countOffset,
				shaders, shadersOffset);
	}

	@Override
	public void glGetAttachedShaders(int program, int maxcount,
			IntBuffer count, IntBuffer shaders) {
		GLES20.glGetAttachedShaders(program, maxcount, count, shaders);
	}

	@Override
	public int glGetAttribLocation(int program, String name) {
		return GLES20.glGetAttribLocation(program, name);
	}

	@Override
	public boolean glGetBoolean(int pname) {
		byte[] out = new byte[1];
		glGetBooleanv(pname, out, 0);
		return out[0] != GL_FALSE;
	}

	@Override
	public void glGetBooleanv(int pname, byte[] params, int offset) {
		ByteBuffer buffer = ByteBuffer.wrap(params, offset, params.length
				- offset);
		GLES20.glGetBooleanv(pname, buffer.asIntBuffer());
	}

	@Override
	public void glGetBooleanv(int pname, ByteBuffer params) {
		GLES20.glGetBooleanv(pname, params.asIntBuffer());
	}

	@Override
	public int glGetBoundBuffer(int arg0) throws RuntimeException {
		throw new RuntimeException("glGetBoundBuffer not supported in GLES 2.0");
	}

	@Override
	public void glGetBufferParameteriv(int target, int pname, int[] params,
			int offset) {
		GLES20.glGetBufferParameteriv(target, pname, params, offset);
	}

	@Override
	public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
		GLES20.glGetBufferParameteriv(target, pname, params);
	}

	@Override
	public int glGetError() {
		return GLES20.glGetError();
	}

	@Override
	public float glGetFloat(int pname) {
		float[] out = new float[1];
		GLES20.glGetFloatv(pname, out, 0);
		return out[0];
	}

	@Override
	public void glGetFloatv(int pname, float[] params, int offset) {
		GLES20.glGetFloatv(pname, params, offset);
	}

	@Override
	public void glGetFloatv(int pname, FloatBuffer params) {
		GLES20.glGetFloatv(pname, params);
	}

	@Override
	public void glGetFramebufferAttachmentParameteriv(int target,
			int attachment, int pname, int[] params, int offset) {
		GLES20.glGetFramebufferAttachmentParameteriv(target, attachment, pname,
				params, offset);
	}

	@Override
	public void glGetFramebufferAttachmentParameteriv(int target,
			int attachment, int pname, IntBuffer params) {
		GLES20.glGetFramebufferAttachmentParameteriv(target, attachment, pname,
				params);
	}

	@Override
	public int glGetInteger(int pname) {
		int[] out = new int[1];
		GLES20.glGetIntegerv(pname, out, 0);
		return out[0];
	}

	@Override
	public void glGetIntegerv(int pname, int[] params, int offset) {
		GLES20.glGetIntegerv(pname, params, offset);
	}

	@Override
	public void glGetIntegerv(int pname, IntBuffer params) {
		GLES20.glGetIntegerv(pname, params);
	}

	@Override
	public void glGetProgramBinary(int arg0, int arg1, int[] arg2, int arg3,
			int[] arg4, int arg5, Buffer arg6) throws RuntimeException {
		throw new RuntimeException("glGetProgramBinary not supported.");
	}

	@Override
	public void glGetProgramBinary(int arg0, int arg1, IntBuffer arg2,
			IntBuffer arg3, Buffer arg4) throws RuntimeException {
		throw new RuntimeException("glGetProgramBinary not supported.");
	}

	@Override
	public void glGetProgramInfoLog(int program, int bufsize, int[] length,
			int lengthOffset, byte[] infolog, int infologOffset) {
		String log = GLES20.glGetProgramInfoLog(program);
		byte[] byteArray = log.getBytes();
		for (int i = 0; i < bufsize && i < byteArray.length; i++) {
			infolog[i + infologOffset] = byteArray[i];
		}
		length[lengthOffset] = log.length();
	}

	@Override
	public void glGetProgramInfoLog(int program, int bufsize, IntBuffer length,
			ByteBuffer infolog) {
		glGetProgramInfoLog(program, bufsize, length.array(),
				infolog.position(), infolog.array(), infolog.position());
	}

	@Override
	public String glGetProgramInfoLog(int program) {
		return GLES20.glGetProgramInfoLog(program);
	}

	@Override
	public void glGetProgramiv(int program, int pname, int[] params, int offset) {
		GLES20.glGetProgramiv(program, pname, params, offset);
	}

	@Override
	public void glGetProgramiv(int program, int pname, IntBuffer params) {
		GLES20.glGetProgramiv(program, pname, params);
	}

	@Override
	public void glGetRenderbufferParameteriv(int target, int pname,
			int[] params, int offset) {
		GLES20.glGetRenderbufferParameteriv(target, pname, params, offset);
	}

	@Override
	public void glGetRenderbufferParameteriv(int target, int pname,
			IntBuffer params) {
		GLES20.glGetRenderbufferParameteriv(target, pname, params);
	}

	@Override
	public void glGetShaderInfoLog(int shader, int bufsize, int[] length,
			int lengthOffset, byte[] infolog, int infologOffset) {
		String log = GLES20.glGetShaderInfoLog(shader);
		byte[] byteArray = log.getBytes();
		for (int i = 0; i < bufsize && i < byteArray.length; i++) {
			infolog[i + infologOffset] = byteArray[i];
		}
		length[lengthOffset] = log.length();
	}

	@Override
	public void glGetShaderInfoLog(int shader, int bufsize, IntBuffer length,
			ByteBuffer infolog) {
		glGetShaderInfoLog(shader, bufsize, length.array(), length.position(),
				infolog.array(), infolog.position());
	}

	@Override
	public String glGetShaderInfoLog(int shader) {
		return GLES20.glGetShaderInfoLog(shader);
	}

	@Override
	public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
		GLES20.glGetShaderiv(shader, pname, params, offset);
	}

	@Override
	public void glGetShaderiv(int shader, int pname, IntBuffer params) {
		GLES20.glGetShaderiv(shader, pname, params);
	}

	@Override
	public void glGetShaderPrecisionFormat(int shadertype, int precisiontype,
			int[] range, int rangeOffset, int[] precision, int precisionOffset) {
		GLES20.glGetShaderPrecisionFormat(shadertype, precisiontype, range,
				rangeOffset, precision, precisionOffset);
	}

	@Override
	public void glGetShaderPrecisionFormat(int shadertype, int precisiontype,
			IntBuffer range, IntBuffer precision) {
		GLES20.glGetShaderPrecisionFormat(shadertype, precisiontype, range,
				precision);
	}

	@Override
	public void glGetShaderSource(int shader, int bufsize, int[] length,
			int lengthOffset, byte[] source, int sourceOffset) {
		GLES20.glGetShaderSource(shader, bufsize, length, lengthOffset, source,
				sourceOffset);
	}

	@Override
	public void glGetShaderSource(int shader, int bufsize, IntBuffer length,
			ByteBuffer source) {
		GLES20.glGetShaderSource(shader, bufsize, length.array(), 0,
				source.array(), 0);
	}

	@Override
	public String glGetString(int name) {
		return GLES20.glGetString(name);
	}

	@Override
	public void glGetTexParameterfv(int target, int pname, float[] params,
			int offset) {
		GLES20.glGetTexParameterfv(target, pname, params, offset);
	}

	@Override
	public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
		GLES20.glGetTexParameterfv(target, pname, params);
	}

	@Override
	public void glGetTexParameteriv(int target, int pname, int[] params,
			int offset) {
		GLES20.glGetTexParameteriv(target, pname, params, offset);
	}

	@Override
	public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
		GLES20.glGetTexParameteriv(target, pname, params);
	}

	@Override
	public void glGetUniformfv(int program, int location, float[] params,
			int offset) {
		GLES20.glGetUniformfv(program, location, params, offset);
	}

	@Override
	public void glGetUniformfv(int program, int location, FloatBuffer params) {
		GLES20.glGetUniformfv(program, location, params);
	}

	@Override
	public void glGetUniformiv(int program, int location, int[] params,
			int offset) {
		GLES20.glGetUniformiv(program, location, params, offset);
	}

	@Override
	public void glGetUniformiv(int program, int location, IntBuffer params) {
		GLES20.glGetUniformiv(program, location, params);
	}

	@Override
	public int glGetUniformLocation(int program, String name) {
		return GLES20.glGetUniformLocation(program, name);
	}

	@Override
	public void glGetVertexAttribfv(int index, int pname, float[] params,
			int offset) {
		GLES20.glGetVertexAttribfv(index, pname, params, offset);
	}

	@Override
	public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
		GLES20.glGetVertexAttribfv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribiv(int index, int pname, int[] params,
			int offset) {
		GLES20.glGetVertexAttribiv(index, pname, params, offset);
	}

	@Override
	public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
		GLES20.glGetVertexAttribiv(index, pname, params);
	}

	@Override
	public void glHint(int target, int mode) {
		GLES20.glHint(target, mode);
	}

	@Override
	public boolean glIsBuffer(int buffer) {
		return GLES20.glIsBuffer(buffer);
	}

	@Override
	public boolean glIsEnabled(int cap) {
		return GLES20.glIsEnabled(cap);
	}

	@Override
	public boolean glIsFramebuffer(int framebuffer) {
		return GLES20.glIsFramebuffer(framebuffer);
	}

	@Override
	public boolean glIsProgram(int program) {
		return GLES20.glIsProgram(program);
	}

	@Override
	public boolean glIsRenderbuffer(int renderbuffer) {
		return GLES20.glIsRenderbuffer(renderbuffer);
	}

	@Override
	public boolean glIsShader(int shader) {
		return GLES20.glIsShader(shader);
	}

	@Override
	public boolean glIsTexture(int texture) {
		return GLES20.glIsTexture(texture);
	}

	@Override
	public boolean glIsVBOArrayEnabled() {
		// TODO(jonagill) Test!
		return isExtensionAvailable("vertex_buffer_object");
	}

	@Override
	public boolean glIsVBOElementEnabled() {
		return glIsVBOArrayEnabled();
	}

	@Override
	public void glLineWidth(float width) {
		GLES20.glLineWidth(width);
	}

	@Override
	public void glLinkProgram(int program) {
		GLES20.glLinkProgram(program);
	}

	@Override
	public ByteBuffer glMapBuffer(int arg0, int arg1) throws RuntimeException {
		throw new RuntimeException("glMapBuffer() not supported.");
	}

	@Override
	public void glPixelStorei(int pname, int param) {
		GLES20.glPixelStorei(pname, param);
	}

	@Override
	public void glPolygonOffset(float factor, float units) {
		GLES20.glPolygonOffset(factor, units);
	}

	@Override
	public void glProgramBinary(int arg0, int arg1, Buffer arg2, int arg3)
			throws RuntimeException {
		throw new RuntimeException("glProgramBinary() not supported.");
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format,
			int type, Buffer pixels) {
		GLES20.glReadPixels(x, y, width, height, format, type, pixels);
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format,
			int type, int pixelsBufferOffset) throws RuntimeException {
		throw new RuntimeException(
				"Not supported.  Please call glReadPixels(int, int, int, int, int, int, Buffer) instead");
	}

	@Override
	public void glReleaseShaderCompiler() {
		GLES20.glReleaseShaderCompiler();
	}

	@Override
	public void glRenderbufferStorage(int target, int internalformat,
			int width, int height) {
		GLES20.glRenderbufferStorage(target, internalformat, width, height);
	}

	@Override
	public void glSampleCoverage(float value, boolean invert) {
		GLES20.glSampleCoverage(value, invert);
	}

	@Override
	public void glScissor(int x, int y, int width, int height) {
		GLES20.glScissor(x, y, width, height);
	}

	@Override
	public void glShaderBinary(int n, int[] shaders, int offset,
			int binaryformat, Buffer binary, int length) {
		GLES20.glShaderBinary(n, shaders, offset, binaryformat, binary, length);
	}

	@Override
	public void glShaderBinary(int n, IntBuffer shaders, int binaryformat,
			Buffer binary, int length) {
		GLES20.glShaderBinary(n, shaders, binaryformat, binary, length);
	}

	@Override
	public void glShaderSource(int shader, int count, String[] strings,
			int[] length, int lengthOffset) {
		int totalLength = 0;
		for (int i = lengthOffset; i < length.length; i++) {
			totalLength += length[i];
		}
		StringBuilder builder = new StringBuilder(totalLength);

		for (int j = 0; j < count; j++) {
			builder.append(strings[j], 0, length[j]);
		}

		GLES20.glShaderSource(shader, builder.toString());
		// TODO(jonagill): Test me!
	}

	@Override
	public void glShaderSource(int shader, int count, String[] strings,
			IntBuffer length) {
		glShaderSource(shader, count, strings, length.array(), 0);
	}

	@Override
	public void glShaderSource(int shader, String string) {
		GLES20.glShaderSource(shader, string);
	}

	@Override
	public void glStencilFunc(int func, int ref, int mask) {
		GLES20.glStencilFunc(func, ref, mask);

	}

	@Override
	public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
		GLES20.glStencilFuncSeparate(face, func, ref, mask);

	}

	@Override
	public void glStencilMask(int mask) {
		GLES20.glStencilMask(mask);
	}

	@Override
	public void glStencilMaskSeparate(int face, int mask) {
		GLES20.glStencilMaskSeparate(face, mask);
	}

	@Override
	public void glStencilOp(int fail, int zfail, int zpass) {
		GLES20.glStencilOp(fail, zfail, zpass);
	}

	@Override
	public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
		GLES20.glStencilOpSeparate(face, fail, zfail, zpass);
	}

	@Override
	public void glTexImage2D(int target, int level, int internalformat,
			int width, int height, int border, int format, int type,
			Buffer pixels) {
		GLES20.glTexImage2D(target, level, internalformat, width, height,
				border, format, type, pixels);
	}

	@Override
	public void glTexImage2D(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5, int arg6, int arg7, int arg8) {

	}

	@Override
	public void glTexImage3D(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5, int arg6, int arg7, int arg8, Buffer arg9)
			throws RuntimeException {
		throw new RuntimeException("glTexImage3D not supported!");
	}

	@Override
	public void glTexImage3D(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5, int arg6, int arg7, int arg8, int arg9)
			throws RuntimeException {
		throw new RuntimeException("glTexImage3D not supported!");
	}

	@Override
	public void glTexParameterf(int target, int pname, float param) {
		GLES20.glTexParameterf(target, pname, param);
	}

	@Override
	public void glTexParameterfv(int target, int pname, float[] params,
			int offset) {
		GLES20.glTexParameterfv(target, pname, params, offset);
	}

	@Override
	public void glTexParameterfv(int target, int pname, FloatBuffer params) {
		GLES20.glTexParameterfv(target, pname, params);
	}

	@Override
	public void glTexParameteri(int target, int pname, int param) {
		GLES20.glTexParameteri(target, pname, param);
	}

	@Override
	public void glTexParameteriv(int target, int pname, int[] params, int offset) {
		GLES20.glTexParameteriv(target, pname, params, offset);
	}

	@Override
	public void glTexParameteriv(int target, int pname, IntBuffer params) {
		GLES20.glTexParameteriv(target, pname, params);
	}

	@Override
	public void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			Buffer pixels) {
		GLES20.glTexSubImage2D(target, level, xoffset, yoffset, width, height,
				format, type, pixels);
	}

	@Override
	public void glTexSubImage2D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8)
			throws RuntimeException {
		throw new RuntimeException(
				"glTexSubImage2D(int, int, int, int, int, int,"
						+ " int, int, int) not supported.");
	}

	@Override
	public void glTexSubImage3D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8, int arg9,
			Buffer arg10) throws RuntimeException {
		throw new RuntimeException("glTexSubImage3D not supported!");

	}

	@Override
	public void glTexSubImage3D(int arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8, int arg9,
			int arg10) throws RuntimeException {
		throw new RuntimeException("glTexSubImage3D not supported!");

	}

	@Override
	public void glUniform1f(int location, float x) {
		GLES20.glUniform1f(location, x);
	}

	@Override
	public void glUniform1fv(int location, int count, float[] v, int offset) {
		GLES20.glUniform1fv(location, count, v, offset);
	}

	@Override
	public void glUniform1fv(int location, int count, FloatBuffer v) {
		GLES20.glUniform1fv(location, count, v);
	}

	@Override
	public void glUniform1i(int location, int x) {
		GLES20.glUniform1i(location, x);
	}

	@Override
	public void glUniform1iv(int location, int count, int[] v, int offset) {
		GLES20.glUniform1iv(location, count, v, offset);
	}

	@Override
	public void glUniform1iv(int location, int count, IntBuffer v) {
		GLES20.glUniform1iv(location, count, v);
	}

	@Override
	public void glUniform2f(int location, float x, float y) {
		GLES20.glUniform2f(location, x, y);
	}

	@Override
	public void glUniform2fv(int location, int count, float[] v, int offset) {
		GLES20.glUniform2fv(location, count, v, offset);
	}

	@Override
	public void glUniform2fv(int location, int count, FloatBuffer v) {
		GLES20.glUniform2fv(location, count, v);
	}

	@Override
	public void glUniform2i(int location, int x, int y) {
		GLES20.glUniform2i(location, x, y);
	}

	@Override
	public void glUniform2iv(int location, int count, int[] v, int offset) {
		GLES20.glUniform2iv(location, count, v, offset);
	}

	@Override
	public void glUniform2iv(int location, int count, IntBuffer v) {
		GLES20.glUniform2iv(location, count, v);
	}

	@Override
	public void glUniform3f(int location, float x, float y, float z) {
		GLES20.glUniform3f(location, x, y, z);
	}

	@Override
	public void glUniform3fv(int location, int count, float[] v, int offset) {
		GLES20.glUniform3fv(location, count, v, offset);
	}

	@Override
	public void glUniform3fv(int location, int count, FloatBuffer v) {
		GLES20.glUniform3fv(location, count, v);
	}

	@Override
	public void glUniform3i(int location, int x, int y, int z) {
		GLES20.glUniform3i(location, x, y, z);
	}

	@Override
	public void glUniform3iv(int location, int count, int[] v, int offset) {
		GLES20.glUniform3iv(location, count, v, offset);
	}

	@Override
	public void glUniform3iv(int location, int count, IntBuffer v) {
		GLES20.glUniform3iv(location, count, v);
	}

	@Override
	public void glUniform4f(int location, float x, float y, float z, float w) {
		GLES20.glUniform4f(location, x, y, z, w);
	}

	@Override
	public void glUniform4fv(int location, int count, float[] v, int offset) {
		GLES20.glUniform4fv(location, count, v, offset);
	}

	@Override
	public void glUniform4fv(int location, int count, FloatBuffer v) {
		GLES20.glUniform4fv(location, count, v);
	}

	@Override
	public void glUniform4i(int location, int x, int y, int z, int w) {
		GLES20.glUniform4i(location, x, y, z, w);
	}

	@Override
	public void glUniform4iv(int location, int count, int[] v, int offset) {
		GLES20.glUniform4iv(location, count, v, offset);
	}

	@Override
	public void glUniform4iv(int location, int count, IntBuffer v) {
		GLES20.glUniform4iv(location, count, v);
	}

	@Override
	public void glUniformMatrix2fv(int location, int count, boolean transpose,
			float[] value, int offset) {
		GLES20.glUniformMatrix2fv(location, count, transpose, value, offset);
	}

	@Override
	public void glUniformMatrix2fv(int location, int count, boolean transpose,
			FloatBuffer value) {
		GLES20.glUniformMatrix2fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3fv(int location, int count, boolean transpose,
			float[] value, int offset) {
		GLES20.glUniformMatrix3fv(location, count, transpose, value, offset);
	}

	@Override
	public void glUniformMatrix3fv(int location, int count, boolean transpose,
			FloatBuffer value) {
		GLES20.glUniformMatrix3fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose,
			float[] value, int offset) {
		GLES20.glUniformMatrix4fv(location, count, transpose, value, offset);
	}

	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose,
			FloatBuffer value) {
		GLES20.glUniformMatrix4fv(location, count, transpose, value);
	}

	@Override
	public boolean glUnmapBuffer(int arg0) throws RuntimeException {
		throw new RuntimeException("glUnmapBuffer() not supported.");
	}

	@Override
	public void glUseProgram(int program) {
		GLES20.glUseProgram(program);
	}

	@Override
	public void glValidateProgram(int program) {
		GLES20.glValidateProgram(program);
	}

	@Override
	public void glVertexAttrib1f(int indx, float x) {
		GLES20.glVertexAttrib1f(indx, x);
	}

	@Override
	public void glVertexAttrib1fv(int indx, float[] values, int offset) {
		GLES20.glVertexAttrib1fv(indx, values, offset);

	}

	@Override
	public void glVertexAttrib1fv(int indx, FloatBuffer values) {
		GLES20.glVertexAttrib1fv(indx, values);
	}

	@Override
	public void glVertexAttrib2f(int indx, float x, float y) {
		GLES20.glVertexAttrib2f(indx, x, y);
	}

	@Override
	public void glVertexAttrib2fv(int indx, float[] values, int offset) {
		GLES20.glVertexAttrib2fv(indx, values, offset);
	}

	@Override
	public void glVertexAttrib2fv(int indx, FloatBuffer values) {
		GLES20.glVertexAttrib2fv(indx, values);
	}

	@Override
	public void glVertexAttrib3f(int indx, float x, float y, float z) {
		GLES20.glVertexAttrib3f(indx, x, y, z);
	}

	@Override
	public void glVertexAttrib3fv(int indx, float[] values, int offset) {
		GLES20.glVertexAttrib3fv(indx, values, offset);
	}

	@Override
	public void glVertexAttrib3fv(int indx, FloatBuffer values) {
		GLES20.glVertexAttrib3fv(indx, values);
	}

	@Override
	public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
		GLES20.glVertexAttrib4f(indx, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4fv(int indx, float[] values, int offset) {
		GLES20.glVertexAttrib4fv(indx, values, offset);
	}

	@Override
	public void glVertexAttrib4fv(int indx, FloatBuffer values) {
		GLES20.glVertexAttrib4fv(indx, values);
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type,
			boolean normalized, int stride, Buffer ptr) {
		GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type,
			boolean normalized, int stride, int ptr) {
		GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		GLES20.glViewport(x, y, width, height);
	}

	@Override
	public boolean hasGLSL() {
		return true;
	}

	@Override
	public boolean isExtensionAvailable(String extension) {
		String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
		return extensions.contains(extension);
	}

	@Override
	public boolean isFunctionAvailable(String function) {
		Method[] functions = this.getClass().getMethods();
		for (int i = 0; i < functions.length; i++) {
			if (function == functions[i].getName())
				return true;
		}
		return false;
	}

}
