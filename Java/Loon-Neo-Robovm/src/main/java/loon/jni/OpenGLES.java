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
package loon.jni;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.robovm.rt.bro.*;
import org.robovm.rt.bro.annotation.*;
import org.robovm.rt.bro.ptr.*;

@Library("OpenGLES")
public class OpenGLES {
  static {
    Bro.bind(OpenGLES.class);
  }

  @Bridge public static native void glActiveTexture (int texture);
  @Bridge public static native void glAttachShader (int program, int shader);
  @Bridge public static native void glBindAttribLocation (int program, int index, String name);
  @Bridge public static native void glBindBuffer (int target, int buffer);
  @Bridge public static native void glBindFramebuffer (int target, int framebuffer);
  @Bridge public static native void glBindRenderbuffer (int target, int renderbuffer);
  @Bridge public static native void glBindTexture (int target, int texture);
  @Bridge public static native void glBlendColor (float red, float green, float blue, float alpha);
  @Bridge public static native void glBlendEquation (int mode);
  @Bridge public static native void glBlendEquationSeparate (int modeRGB, int modeAlpha);
  @Bridge public static native void glBlendFunc (int sfactor, int dfactor);
  @Bridge public static native void glBlendFuncSeparate (int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);
  @Bridge public static native void glBufferData (int target, int size, Buffer data, int usage);
  @Bridge public static native void glBufferSubData (int target, int offset, int size, Buffer data);
  @Bridge public static native int  glCheckFramebufferStatus (int target);
  @Bridge public static native void glClear (int mask);
  @Bridge public static native void glClearColor (float red, float green, float blue, float alpha);
  @Bridge public static native void glClearDepthf (float depth);
  @Bridge public static native void glClearStencil (int s);
  @Bridge public static native void glColorMask (boolean red, boolean green, boolean blue, boolean alpha);
  @Bridge public static native void glCompileShader (int shader);
  @Bridge public static native void glCompressedTexImage2D (int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data);
  @Bridge public static native void glCompressedTexSubImage2D (int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data);
  @Bridge public static native void glCopyTexImage2D (int target, int level, int internalformat, int x, int y, int width, int height, int border);
  @Bridge public static native void glCopyTexSubImage2D (int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);
  @Bridge public static native int  glCreateProgram ();
  @Bridge public static native int  glCreateShader (int type);
  @Bridge public static native void glCullFace (int mode);
  @Bridge public static native void glDeleteBuffers (int n, IntBuffer buffers);
  @Bridge public static native void glDeleteFramebuffers (int n, IntBuffer framebuffers);
  @Bridge public static native void glDeleteProgram (int program);
  @Bridge public static native void glDeleteRenderbuffers (int n, IntBuffer renderbuffers);
  @Bridge public static native void glDeleteShader (int shader);
  @Bridge public static native void glDeleteTextures (int n, IntBuffer textures);
  @Bridge public static native void glDepthFunc (int func);
  @Bridge public static native void glDepthMask (boolean flag);
  @Bridge public static native void glDepthRangef (float zNear, float zFar);
  @Bridge public static native void glDetachShader (int program, int shader);
  @Bridge public static native void glDisable (int cap);
  @Bridge public static native void glDisableVertexAttribArray (int index);
  @Bridge public static native void glDrawArrays (int mode, int first, int count);
  @Bridge public static native void glDrawElements (int mode, int count, int type, Buffer indices);
  @Bridge public static native void glDrawElements (int mode, int count, int type, int indices);
  @Bridge public static native void glEnable (int cap);
  @Bridge public static native void glEnableVertexAttribArray (int index);
  @Bridge public static native void glFinish ();
  @Bridge public static native void glFlush ();
  @Bridge public static native void glFramebufferRenderbuffer (int target, int attachment, int renderbuffertarget, int renderbuffer);
  @Bridge public static native void glFramebufferTexture2D (int target, int attachment, int textarget, int texture, int level);
  @Bridge public static native void glFrontFace (int mode);
  @Bridge public static native void glGenBuffers (int n, IntBuffer buffers);
  @Bridge public static native void glGenerateMipmap (int target);
  @Bridge public static native void glGenFramebuffers (int n, IntBuffer framebuffers);
  @Bridge public static native void glGenRenderbuffers (int n, IntBuffer renderbuffers);
  @Bridge public static native void glGenTextures (int n, IntBuffer textures);
  @Bridge public static native String glGetActiveAttrib (int program, int index, IntBuffer size, Buffer type);
  @Bridge public static native String glGetActiveUniform (int program, int index, IntBuffer size, Buffer type);
  @Bridge public static native void glGetAttachedShaders (int program, int maxcount, Buffer count, IntBuffer shaders);
  @Bridge public static native int glGetAttribLocation (int program, String name);
  @Bridge public static native void glGetBooleanv (int pname, Buffer params);
  @Bridge public static native void glGetBufferParameteriv (int target, int pname, IntBuffer params);
  @Bridge public static native int glGetError ();
  @Bridge public static native void glGetFloatv (int pname, FloatBuffer params);
  @Bridge public static native void glGetFramebufferAttachmentParameteriv (int target, int attachment, int pname, IntBuffer params);
  @Bridge public static native void glGetIntegerv (int pname, IntBuffer params);
  @Bridge public static native void glGetProgramiv (int program, int pname, IntBuffer params);
  @Bridge public static native void glGetProgramInfoLog (int program, int maxLogLen, IntBuffer length, ByteBuffer logData);
  @Bridge public static native void glGetRenderbufferParameteriv (int target, int pname, IntBuffer params);
  @Bridge public static native void glGetShaderiv (int shader, int pname, IntBuffer params);
  @Bridge public static native void glGetShaderInfoLog (int shader, int maxLogLen, IntBuffer length, ByteBuffer logData);
  @Bridge public static native void glGetShaderPrecisionFormat (int shadertype, int precisiontype, IntBuffer range, IntBuffer precision);
  @Bridge public static native void glGetShaderSource (int shader, int bufsize, Buffer length, String source);
  @Bridge public static native String glGetString (int name);
  @Bridge public static native void glGetTexParameterfv (int target, int pname, FloatBuffer params);
  @Bridge public static native void glGetTexParameteriv (int target, int pname, IntBuffer params);
  @Bridge public static native void glGetUniformfv (int program, int location, FloatBuffer params);
  @Bridge public static native void glGetUniformiv (int program, int location, IntBuffer params);
  @Bridge public static native int glGetUniformLocation (int program, String name);
  @Bridge public static native void glGetVertexAttribfv (int index, int pname, FloatBuffer params);
  @Bridge public static native void glGetVertexAttribiv (int index, int pname, IntBuffer params);
  @Bridge public static native void glGetVertexAttribPointerv (int index, int pname, Buffer pointer);
  @Bridge public static native void glHint (int target, int mode);
  @Bridge public static native boolean glIsBuffer (int buffer);
  @Bridge public static native boolean glIsEnabled (int cap);
  @Bridge public static native boolean glIsFramebuffer (int framebuffer);
  @Bridge public static native boolean glIsProgram (int program);
  @Bridge public static native boolean glIsRenderbuffer (int renderbuffer);
  @Bridge public static native boolean glIsShader (int shader);
  @Bridge public static native boolean glIsTexture (int texture);
  @Bridge public static native void glLineWidth (float width);
  @Bridge public static native void glLinkProgram (int program);
  @Bridge public static native void glPixelStorei (int pname, int param);
  @Bridge public static native void glPolygonOffset (float factor, float units);
  @Bridge public static native void glReadPixels (int x, int y, int width, int height, int format, int type, Buffer pixels);
  @Bridge public static native void glReleaseShaderCompiler ();
  @Bridge public static native void glRenderbufferStorage (int target, int internalformat, int width, int height);
  @Bridge public static native void glSampleCoverage (float value, boolean invert);
  @Bridge public static native void glScissor (int x, int y, int width, int height);
  @Bridge public static native void glShaderBinary (int n, IntBuffer shaders, int binaryformat, Buffer binary, int length);
  @Bridge public static native void glShaderSource (int shader, int count, BytePtr.BytePtrPtr sources, IntPtr lengths);
  @Bridge public static native void glStencilFunc (int func, int ref, int mask);
  @Bridge public static native void glStencilFuncSeparate (int face, int func, int ref, int mask);
  @Bridge public static native void glStencilMask (int mask);
  @Bridge public static native void glStencilMaskSeparate (int face, int mask);
  @Bridge public static native void glStencilOp (int fail, int zfail, int zpass);
  @Bridge public static native void glStencilOpSeparate (int face, int fail, int zfail, int zpass);
  @Bridge public static native void glTexImage2D (int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels);
  @Bridge(symbol="glTexImage2D") public static native void glTexImage2Dp (int target, int level, int internalformat, int width, int height, int border, int format, int type, IntPtr pixels);
  @Bridge public static native void glTexParameterf (int target, int pname, float param);
  @Bridge public static native void glTexParameterfv (int target, int pname, FloatBuffer params);
  @Bridge public static native void glTexParameteri (int target, int pname, int param);
  @Bridge public static native void glTexParameteriv (int target, int pname, IntBuffer params);
  @Bridge public static native void glTexSubImage2D (int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels);
  @Bridge public static native void glUniform1f (int location, float x);
  @Bridge public static native void glUniform1fv (int location, int count, FloatBuffer v);
  @Bridge public static native void glUniform1i (int location, int x);
  @Bridge public static native void glUniform1iv (int location, int count, IntBuffer v);
  @Bridge public static native void glUniform2f (int location, float x, float y);
  @Bridge public static native void glUniform2fv (int location, int count, FloatBuffer v);
  @Bridge public static native void glUniform2i (int location, int x, int y);
  @Bridge public static native void glUniform2iv (int location, int count, IntBuffer v);
  @Bridge public static native void glUniform3f (int location, float x, float y, float z);
  @Bridge public static native void glUniform3fv (int location, int count, FloatBuffer v);
  @Bridge public static native void glUniform3i (int location, int x, int y, int z);
  @Bridge public static native void glUniform3iv (int location, int count, IntBuffer v);
  @Bridge public static native void glUniform4f (int location, float x, float y, float z, float w);
  @Bridge public static native void glUniform4fv (int location, int count, FloatBuffer v);
  @Bridge public static native void glUniform4i (int location, int x, int y, int z, int w);
  @Bridge public static native void glUniform4iv (int location, int count, IntBuffer v);
  @Bridge public static native void glUniformMatrix2fv (int location, int count, boolean transpose, FloatBuffer value);
  @Bridge public static native void glUniformMatrix3fv (int location, int count, boolean transpose, FloatBuffer value);
  @Bridge public static native void glUniformMatrix4fv (int location, int count, boolean transpose, FloatBuffer value);
  @Bridge public static native void glUseProgram (int program);
  @Bridge public static native void glValidateProgram (int program);
  @Bridge public static native void glVertexAttrib1f (int indx, float x);
  @Bridge public static native void glVertexAttrib1fv (int indx, FloatBuffer values);
  @Bridge public static native void glVertexAttrib2f (int indx, float x, float y);
  @Bridge public static native void glVertexAttrib2fv (int indx, FloatBuffer values);
  @Bridge public static native void glVertexAttrib3f (int indx, float x, float y, float z);
  @Bridge public static native void glVertexAttrib3fv (int indx, FloatBuffer values);
  @Bridge public static native void glVertexAttrib4f (int indx, float x, float y, float z, float w);
  @Bridge public static native void glVertexAttrib4fv (int indx, FloatBuffer values);
  @Bridge public static native void glVertexAttribPointer (int indx, int size, int type, boolean normalized, int stride, Buffer ptr);
  @Bridge public static native void glVertexAttribPointer (int indx, int size, int type, boolean normalized, int stride, int ptr);
  @Bridge public static native void glViewport (int x, int y, int width, int height);
}
