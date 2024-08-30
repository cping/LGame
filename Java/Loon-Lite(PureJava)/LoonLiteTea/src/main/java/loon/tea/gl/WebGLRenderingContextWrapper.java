package loon.tea.gl;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

import loon.tea.dom.HTMLCanvasElementWrapper;
import loon.tea.dom.HTMLImageElementWrapper;
import loon.tea.dom.HTMLVideoElementWrapper;
import loon.tea.dom.ImageDataWrapper;
import loon.tea.dom.WebJSObject;
import loon.tea.dom.typedarray.ArrayBufferViewWrapper;
import loon.tea.dom.typedarray.ArrayBufferWrapper;
import loon.tea.dom.typedarray.Float32ArrayWrapper;
import loon.tea.dom.typedarray.FloatArrayWrapper;
import loon.tea.dom.typedarray.Int32ArrayWrapper;
import loon.tea.dom.typedarray.LongArrayWrapper;
import loon.tea.dom.typedarray.ObjectArrayWrapper;

public interface WebGLRenderingContextWrapper extends JSObject  {
    int getDrawingBufferWidth();

    int getDrawingBufferHeight();

    WebGLContextAttributesWrapper getContextAttributes();

    boolean isContextLost();

    ObjectArrayWrapper<String> getSupportedExtensions();

    WebJSObject getExtension(String name);

    void activeTexture(int texture);

    void attachShader(WebGLProgramWrapper program, WebGLShaderWrapper shader);

    void bindAttribLocation(WebGLProgramWrapper program, int index, String name);

    void bindBuffer(int target, WebGLBufferWrapper buffer);

    void bindFramebuffer(int target, WebGLFramebufferWrapper framebuffer);

    void bindRenderbuffer(int target, WebGLRenderbufferWrapper renderbuffer);

    void bindTexture(int target, WebGLTextureWrapper texture);

    void blendColor(float red, float green, float blue, float alpha);

    void blendEquation(int mode);

    void blendEquationSeparate(int modeRGB, int modeAlpha);

    void blendFunc(int sfactor, int dfactor);

    void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

    void bufferData(int target, int size, int usage);

    void bufferData(int target, ArrayBufferViewWrapper data, int usage);

    void bufferData(int target, ArrayBufferWrapper data, int usage);

    void bufferSubData(int target, int offset, ArrayBufferViewWrapper data);

    void bufferSubData(int target, int offset, ArrayBufferWrapper data);

    int checkFramebufferStatus(int target);

    void clear(int mask);

    void clearColor(float red, float green, float blue, float alpha);

    void clearDepth(float depth);

    void clearStencil(int s);

    void colorMask(boolean red, boolean green, boolean blue, boolean alpha);

    void compileShader(WebGLShaderWrapper shader);

    void copyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border);

    void copyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);

    WebGLBufferWrapper createBuffer();

    WebGLFramebufferWrapper createFramebuffer();

    WebGLProgramWrapper createProgram();

    WebGLRenderbufferWrapper createRenderbuffer();

    WebGLShaderWrapper createShader(int type);

    WebGLTextureWrapper createTexture();

    void cullFace(int mode);

    void deleteBuffer(WebGLBufferWrapper buffer);

    void deleteFramebuffer(WebGLFramebufferWrapper framebuffer);

    void deleteProgram(WebGLProgramWrapper program);

    void deleteRenderbuffer(WebGLRenderbufferWrapper renderbuffer);

    void deleteShader(WebGLShaderWrapper shader);

    void deleteTexture(WebGLTextureWrapper texture);

    void depthFunc(int func);

    void depthMask(boolean flag);

    void depthRange(float zNear, float zFar);

    void detachShader(WebGLProgramWrapper program, WebGLShaderWrapper shader);

    void disable(int cap);

    void disableVertexAttribArray(int index);

    void drawArrays(int mode, int first, int count);

    void drawElements(int mode, int count, int type, int offset);

    void enable(int cap);

    void enableVertexAttribArray(int index);

    void finish();

    void flush();

    void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget, WebGLRenderbufferWrapper renderbuffer);

    void framebufferTexture2D(int target, int attachment, int textarget, WebGLTextureWrapper texture, int level);

    void frontFace(int mode);

    void generateMipmap(int target);

    WebGLActiveInfoWrapper getActiveAttrib(WebGLProgramWrapper program, int index);

    WebGLActiveInfoWrapper getActiveUniform(WebGLProgramWrapper program, int index);

    ObjectArrayWrapper<WebGLShaderWrapper> getAttachedShaders(WebGLProgramWrapper program);

    int getAttribLocation(WebGLProgramWrapper program, String name);

    WebJSObject getParameter(int pname);

    @JSMethod("getParameter")
    int getParameteri(int pname);

    @JSMethod("getParameter")
    float getParameterf(int pname);

    @JSMethod("getParameter")
    JSObject getParametero(int pname);

    @JSMethod("getParameter")
    ArrayBufferViewWrapper getParameterv(int pname);

    @JSMethod("getParameter")
    public String getParameterString(int pname);

    WebJSObject getBufferParameter(int target, int pname);

    int getError();

    WebJSObject getFramebufferAttachmentParameter(int target, int attachment, int pname);

    @JSMethod("getFramebufferAttachmentParameter")
    int getFramebufferAttachmentParameteri(int target, int attachment, int pname);

    WebJSObject getProgramParameter(WebGLProgramWrapper program, int pname);

    @JSMethod("getProgramParameter")
    int getProgramParameteri(WebGLProgramWrapper program, int pname);

    @JSMethod("getProgramParameter")
    boolean getProgramParameterb(WebGLProgramWrapper program, int pname);

    String getProgramInfoLog(WebGLProgramWrapper program);

    WebJSObject getRenderbufferParameter(int target, int pname);

    WebJSObject getShaderParameter(WebGLShaderWrapper shader, int pname);

    @JSMethod("getShaderParameter")
    boolean getShaderParameterb(WebGLShaderWrapper shader, int pname);

    @JSMethod("getShaderParameter")
    int getShaderParameteri(WebGLShaderWrapper shader, int pname);

    String getShaderInfoLog(WebGLShaderWrapper shader);

    String getShaderSource(WebGLShaderWrapper shader);

    WebJSObject getTexParameter(int target, int pname);

    WebJSObject getUniform(WebGLProgramWrapper program, WebGLUniformLocationWrapper location);

    WebGLUniformLocationWrapper getUniformLocation(WebGLProgramWrapper program, String name);

    WebJSObject getVertexAttrib(int index, int pname);

    int getVertexAttribOffset(int index, int pname);

    void hint(int target, int mode);

    boolean isBuffer(WebGLBufferWrapper buffer);

    boolean isEnabled(int cap);

    boolean isFramebuffer(WebGLFramebufferWrapper framebuffer);

    boolean isProgram(WebGLProgramWrapper program);

    boolean isRenderbuffer(WebGLRenderbufferWrapper renderbuffer);

    boolean isShader(WebGLShaderWrapper shader);

    boolean isTexture(WebGLTextureWrapper texture);

    void lineWidth(float width);

    void linkProgram(WebGLProgramWrapper program);

    void pixelStorei(int pname, int param);

    void polygonOffset(float factor, float units);

    void readPixels(int x, int y, int width, int height, int format, int type, ArrayBufferViewWrapper pixels);

    void renderbufferStorage(int target, int internalformat, int width, int height);

    void sampleCoverage(float value, boolean invert);

    void scissor(int x, int y, int width, int height);

    void shaderSource(WebGLShaderWrapper shader, String source);

    void stencilFunc(int func, int ref, int mask);

    void stencilFuncSeparate(int face, int func, int ref, int mask);

    void stencilMask(int mask);

    void stencilMaskSeparate(int face, int mask);

    void stencilOp(int fail, int zfail, int zpass);

    void stencilOpSeparate(int face, int fail, int zfail, int zpass);

    void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ArrayBufferViewWrapper pixels);

    void texImage2D(int target, int level, int internalformat, int format, int type, ImageDataWrapper pixels);

    void texImage2D(int target, int level, int internalformat, int format, int type, HTMLImageElementWrapper image);

    void texImage2D(int target, int level, int internalformat, int format, int type, HTMLCanvasElementWrapper canvas);

    void texImage2D(int target, int level, int internalformat, int format, int type, HTMLVideoElementWrapper video);

    void texParameterf(int target, int pname, float param);

    void texParameteri(int target, int pname, int param);

    void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ArrayBufferViewWrapper pixels);

    void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, ImageDataWrapper pixels);

    void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLImageElementWrapper image);

    void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLCanvasElementWrapper canvas);

    void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLVideoElementWrapper video);

    void uniform1f(WebGLUniformLocationWrapper location, float x);

    void uniform1fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v);

    void uniform1fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v);

    void uniform1i(WebGLUniformLocationWrapper location, int x);

    void uniform1iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v);

    void uniform1iv(WebGLUniformLocationWrapper location, LongArrayWrapper v);

    void uniform2f(WebGLUniformLocationWrapper location, float x, float y);

    void uniform2fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v);

    void uniform2fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v);

    void uniform2i(WebGLUniformLocationWrapper location, int x, int y);

    void uniform2iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v);

    void uniform2iv(WebGLUniformLocationWrapper location, LongArrayWrapper v);

    void uniform3f(WebGLUniformLocationWrapper location, float x, float y, float z);

    void uniform3fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v);

    void uniform3fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v);

    void uniform3i(WebGLUniformLocationWrapper location, int x, int y, int z);

    void uniform3iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v);

    void uniform3iv(WebGLUniformLocationWrapper location, LongArrayWrapper v);

    void uniform4f(WebGLUniformLocationWrapper location, float x, float y, float z, float w);

    void uniform4fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v);

    void uniform4fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v);

    void uniform4i(WebGLUniformLocationWrapper location, int x, int y, int z, int w);

    void uniform4iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v);

    void uniform4iv(WebGLUniformLocationWrapper location, LongArrayWrapper v);

    void uniformMatrix2fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value);

    void uniformMatrix2fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value);

    void uniformMatrix3fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value);

    void uniformMatrix3fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value);

    void uniformMatrix4fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value);

    void uniformMatrix4fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value);

    void useProgram(WebGLProgramWrapper program);

    void validateProgram(WebGLProgramWrapper program);

    void vertexAttrib1f(int indx, float x);

    void vertexAttrib1fv(int indx, Float32ArrayWrapper values);

    void vertexAttrib1fv(int indx, FloatArrayWrapper values);

    void vertexAttrib2f(int indx, float x, float y);

    void vertexAttrib2fv(int indx, Float32ArrayWrapper values);

    void vertexAttrib2fv(int indx, FloatArrayWrapper values);

    void vertexAttrib3f(int indx, float x, float y, float z);

    void vertexAttrib3fv(int indx, Float32ArrayWrapper values);

    void vertexAttrib3fv(int indx, FloatArrayWrapper values);

    void vertexAttrib4f(int indx, float x, float y, float z, float w);

    void vertexAttrib4fv(int indx, Float32ArrayWrapper values);

    void vertexAttrib4fv(int indx, FloatArrayWrapper values);

    void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset);

    void viewport(int x, int y, int width, int height);

    void uniform1iv(WebGLUniformLocationWrapper location, int[] v);

    void uniform1fv(WebGLUniformLocationWrapper loc, float[] v);

    void uniform2fv(WebGLUniformLocationWrapper loc, float[] v);

    void uniform2iv(WebGLUniformLocationWrapper loc, int[] v);

    void uniform3fv(WebGLUniformLocationWrapper loc, float[] v);

    void uniform3iv(WebGLUniformLocationWrapper loc, int[] v);

    void uniform4fv(WebGLUniformLocationWrapper loc, float[] v);

    void uniform4iv(WebGLUniformLocationWrapper loc, int[] v);

    void uniformMatrix2fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value);

    void uniformMatrix3fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value);

    void uniformMatrix4fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value);
}