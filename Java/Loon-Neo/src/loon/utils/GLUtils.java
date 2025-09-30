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
package loon.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import loon.BaseIO;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.Support;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.geom.RectBox;
import loon.opengl.BlendMethod;
import loon.opengl.GL20;
import loon.opengl.GlobalSource;
import loon.opengl.ShaderProgram;

public class GLUtils {

	private GLUtils() {
	}

	private static final IntBuffer currentTempIntBuffer = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8)
			.order(ByteOrder.nativeOrder()).asIntBuffer();

	private static final ByteBuffer currentTempByteBuffer = BufferUtils.newByteBuffer(32);

	private static int currentBlendMode = -1;

	private static int currentHardwareBufferID = -1;

	private static int currentHardwareTextureID = -1;

	private static int currentSourceBlendMode = -1;

	private static int currentDestinationBlendMode = -1;

	private static boolean enableDither = false;

	private static boolean enableDepthTest = false;

	private static boolean enablecissorTest = false;

	private static boolean enableBlend = false;

	private static boolean enableCulling = false;

	private static boolean enableTextures = false;

	public static void reset(final GL20 gl) {
		GLUtils.reload();
	}

	public static void reload() {
		GLUtils.currentHardwareBufferID = -1;
		GLUtils.currentHardwareTextureID = -1;
		GLUtils.currentSourceBlendMode = -1;
		GLUtils.currentDestinationBlendMode = -1;
		GLUtils.currentBlendMode = -1;
		GLUtils.enableDither = false;
		GLUtils.enableDepthTest = false;
		GLUtils.enablecissorTest = false;
		GLUtils.enableBlend = false;
		GLUtils.enableCulling = false;
		GLUtils.enableTextures = false;
	}

	public static RectBox getGLViewport(GL20 gl) {
		IntBuffer intBuffer = currentTempIntBuffer;
		gl.glGetIntegerv(GL20.GL_VIEWPORT, intBuffer);
		return new RectBox(intBuffer.get(0), intBuffer.get(1), intBuffer.get(2), intBuffer.get(3));
	}

	public static int nextPOT(int value) {
		int bit = 0x8000, highest = -1, count = 0;
		for (int ii = 15; ii >= 0; ii--, bit >>= 1) {
			if ((value & bit) == 0)
				continue;
			count++;
			if (highest == -1)
				highest = ii;
		}
		return (count > 1) ? (1 << (highest + 1)) : value;
	}

	public static int powerOfTwo(int value) {
		if (value == 0) {
			return 1;
		}
		if ((value & value - 1) == 0) {
			return value;
		}
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return value + 1;
	}

	public static boolean isPowerOfTwo(int value) {
		return (value > 0 && (value & (value - 1)) == 0);
	}

	public static boolean isPowerOfTwo(int width, int height) {
		return (width > 0 && (width & (width - 1)) == 0 && height > 0 && (height & (height - 1)) == 0);
	}

	public static boolean isGLEnabled(GL20 gl, int keyName) {
		boolean result;
		switch (keyName) {
		case GL20.GL_BLEND:
			gl.glGetBooleanv(GL20.GL_BLEND, currentTempByteBuffer);
			result = (currentTempByteBuffer.get() == 1);
			currentTempByteBuffer.clear();
			break;
		default:
			result = false;
		}
		return result;
	}

	public static final int getBlendMode() {
		return currentBlendMode;
	}

	public static final void setBlendMode(GL20 gl, int mode) {
		if (currentBlendMode == mode) {
			return;
		}
		currentBlendMode = mode;
		if (gl == null) {
			return;
		}
		if (currentBlendMode == BlendMethod.MODE_NORMAL) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, false);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_SPEED) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_ALPHA_MAP) {
			GLUtils.disableBlend(gl);
			gl.glColorMask(false, false, false, true);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_ALPHA_BLEND) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, false);
			gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_COLOR_MULTIPLY) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_SRC_COLOR);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_ADD) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_SCREEN) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_COLOR);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_ALPHA_ONE) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_ALPHA) {
			GLUtils.enableBlend(gl);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_MASK) {
			GLUtils.enableBlend(gl);
			gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_LIGHT) {
			GLUtils.enableBlend(gl);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_ALPHA_ADD) {
			GLUtils.enableBlend(gl);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_MULTIPLY) {
			GLUtils.enableBlend(gl);
			gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == BlendMethod.MODE_NONE) {
			GLUtils.disableBlend(gl);
			gl.glColorMask(true, true, true, false);
			return;
		}
		return;
	}

	public void setClearColor(final GL20 gl, float r, float g, float b, float a, boolean clearDepth,
			boolean applyAntialiasing) {
		gl.glClearColor(r, g, b, a);
		int mask = GL20.GL_COLOR_BUFFER_BIT;
		if (clearDepth) {
			mask = mask | GL20.GL_DEPTH_BUFFER_BIT;
		}
		if (applyAntialiasing) {
			mask = mask | GL20.GL_COVERAGE_BUFFER_BIT_NV;
		}
		gl.glClear(mask);
	}

	public static void setClearColor(final GL20 gl, float r, float g, float b, float a) {
		gl.glClearColor(r, g, b, a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	public static void setClearColor(final GL20 gl, LColor c) {
		GLUtils.setClearColor(gl, c.r, c.g, c.b, c.a);
	}

	public static void enablecissorTest(final GL20 gl) {
		try {
			if (!GLUtils.enablecissorTest) {
				GLUtils.enablecissorTest = true;
				gl.glEnable(GL20.GL_SCISSOR_TEST);
			}
		} catch (Throwable e) {
		}
	}

	public static void disablecissorTest(final GL20 gl) {
		try {
			if (GLUtils.enablecissorTest) {
				GLUtils.enablecissorTest = false;
				gl.glDisable(GL20.GL_SCISSOR_TEST);
			}
		} catch (Throwable e) {
		}
	}

	public static void enableBlend(final GL20 gl) {
		try {
			if (!GLUtils.enableBlend) {
				gl.glEnable(GL20.GL_BLEND);
				GLUtils.enableBlend = true;
			}
		} catch (Throwable e) {
		}
	}

	public static void disableBlend(final GL20 gl) {
		try {
			if (GLUtils.enableBlend) {
				gl.glDisable(GL20.GL_BLEND);
				GLUtils.enableBlend = false;
			}
		} catch (Throwable e) {
		}
	}

	public static void disableCulling(final GL20 gl) {
		try {
			if (GLUtils.enableCulling) {
				GLUtils.enableCulling = false;
				gl.glDisable(GL20.GL_CULL_FACE);
			}
		} catch (Throwable e) {
		}
	}

	public static void enableTextures(final GL20 gl) {
		try {
			if (!GLUtils.enableTextures) {
				GLUtils.enableTextures = true;
				gl.glEnable(GL20.GL_TEXTURE_2D);
			}
		} catch (Throwable e) {
		}
	}

	public static void disableTextures(final GL20 gl) {
		try {
			if (GLUtils.enableTextures) {
				GLUtils.enableTextures = false;
				gl.glDisable(GL20.GL_TEXTURE_2D);
			}
		} catch (Throwable e) {
		}
	}

	public static void enableDither(final GL20 gl) {
		try {
			if (!GLUtils.enableDither) {
				GLUtils.enableDither = true;
				gl.glEnable(GL20.GL_DITHER);
			}
		} catch (Throwable e) {
		}
	}

	public static void disableDither(final GL20 gl) {
		try {
			if (GLUtils.enableDither) {
				GLUtils.enableDither = false;
				gl.glDisable(GL20.GL_DITHER);
			}
		} catch (Throwable e) {
		}
	}

	public static void enableDepthTest(final GL20 gl) {
		try {
			if (!GLUtils.enableDepthTest) {
				GLUtils.enableDepthTest = true;
				gl.glEnable(GL20.GL_DEPTH_TEST);
				gl.glDepthMask(true);
			}
		} catch (Throwable e) {
		}
	}

	public static void disableDepthTest(final GL20 gl) {
		try {
			if (GLUtils.enableDepthTest) {
				GLUtils.enableDepthTest = false;
				gl.glDisable(GL20.GL_DEPTH_TEST);
				gl.glDepthMask(false);
			}
		} catch (Throwable e) {
		}
	}

	public static void bindBuffer(final GL20 gl, final int hardwareBufferID) {
		try {
			if (GLUtils.currentHardwareBufferID != hardwareBufferID) {
				GLUtils.currentHardwareBufferID = hardwareBufferID;
				gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, hardwareBufferID);
			}
		} catch (Throwable e) {
		}
	}

	public static int getCurrentHardwareTextureID() {
		return currentHardwareTextureID;
	}

	public static void bindTexture(final GL20 gl, final int hardwareTextureID) {
		try {
			if (GLUtils.currentHardwareTextureID != hardwareTextureID) {
				gl.glBindTexture(GL20.GL_TEXTURE_2D, hardwareTextureID);
				GLUtils.currentHardwareTextureID = hardwareTextureID;
			}
		} catch (Throwable e) {
		}
	}

	public static void deleteTexture(GL20 gl, int id) {
		gl.glDeleteTexture(id);
		currentHardwareTextureID = -1;
	}

	public static void bindTexture(GL20 gl, LTexture tex2d) {
		if (!tex2d.isLoaded()) {
			tex2d.loadTexture();
		}
		bindTexture(gl, tex2d.getID());
	}

	public static void blendFunction(final GL20 gl, final int pSourceBlendMode, final int pDestinationBlendMode) {
		try {
			if (GLUtils.currentSourceBlendMode != pSourceBlendMode
					|| GLUtils.currentDestinationBlendMode != pDestinationBlendMode) {
				GLUtils.currentSourceBlendMode = pSourceBlendMode;
				GLUtils.currentDestinationBlendMode = pDestinationBlendMode;
				gl.glBlendFunc(pSourceBlendMode, pDestinationBlendMode);
			}
		} catch (Throwable e) {
		}
	}

	public static byte[] getFrameBufferRGBAPixels() {
		final int w = LSystem.viewSize.getWidth();
		final int h = LSystem.viewSize.getHeight();
		return getFrameBufferPixels(LSystem.base().graphics().gl, 0, 0, w, h, true, true);
	}

	public static byte[] getFrameBufferRGBPixels() {
		final int w = LSystem.viewSize.getWidth();
		final int h = LSystem.viewSize.getHeight();
		return getFrameBufferPixels(LSystem.base().graphics().gl, 0, 0, w, h, true, false);
	}

	public static byte[] getFrameBufferPixels(final GL20 gl, boolean flipY, boolean alpha) {
		final int w = LSystem.viewSize.getWidth();
		final int h = LSystem.viewSize.getHeight();
		return getFrameBufferPixels(gl, 0, 0, w, h, flipY, alpha);
	}

	public static Pixmap getFrameBufferRGBAPixmap() {
		return getFrameBufferPixmap(LSystem.base().graphics().gl, 0, 0,
				(int) (LSystem.viewSize.width * LSystem.getScaleWidth()),
				(int) (LSystem.viewSize.height * LSystem.getScaleHeight()), true, true);
	}

	public static Pixmap getFrameBufferRGBPixmap() {
		return getFrameBufferPixmap(LSystem.base().graphics().gl, 0, 0,
				(int) (LSystem.viewSize.width * LSystem.getScaleWidth()),
				(int) (LSystem.viewSize.height * LSystem.getScaleHeight()), true, false);
	}

	public static Pixmap getFrameBufferPixmap(final GL20 gl, int x, int y, int w, int h, boolean flipY, boolean alpha) {
		Support support = LSystem.base().support();
		final Pixmap pixmap = new Pixmap(w, h, alpha);
		byte[] buffer = getFrameBufferPixels(gl, x, y, w, h, flipY, alpha);
		if (alpha) {
			pixmap.convertByteBufferToPixmap(support.getByteBuffer(buffer));
		} else {
			pixmap.convertByteBufferRGBToPixmap(support.getByteBuffer(buffer));
		}
		return pixmap;
	}

	public static byte[] getFrameBufferRGBAPixels(final GL20 gl, int x, int y, int w, int h, boolean flipY) {
		return getFrameBufferPixels(gl, x, y, w, h, flipY, true);
	}

	public static byte[] getFrameBufferRGBPixels(final GL20 gl, int x, int y, int w, int h, boolean flipY) {
		return getFrameBufferPixels(gl, x, y, w, h, flipY, false);
	}

	public static byte[] getFrameBufferPixels(final GL20 gl, int x, int y, int w, int h, boolean flipY, boolean alpha) {
		Support support = LSystem.base().support();
		final int bits = alpha ? 4 : 3;
		final ByteBuffer pixels = support.newByteBuffer(w * h * bits);
		gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
		gl.glReadPixels(x, y, w, h, alpha ? GL20.GL_RGBA : GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, pixels);
		final int numBytes = w * h * bits;
		byte[] buffer = new byte[numBytes];
		if (flipY) {
			final int numBytesPerLine = w * bits;
			for (int i = 0; i < h; i++) {
				pixels.position((h - i - 1) * numBytesPerLine);
				pixels.get(buffer, i * numBytesPerLine, numBytesPerLine);
			}
		} else {
			pixels.clear();
			pixels.get(buffer);
		}
		return buffer;
	}

	public static Image getFrameBufferRGBImage(int x, int y, int w, int h) {
		return getFrameBuffeImage(LSystem.base().graphics().gl, x, y, w, h, true, false);
	}

	public static Image getFrameBufferRGBAImage(int x, int y, int w, int h) {
		return getFrameBuffeImage(LSystem.base().graphics().gl, x, y, w, h, true, true);
	}

	public static Image getFrameBuffeImage(final GL20 gl, int x, int y, int width, int height, boolean flipY,
			boolean alpha) {
		Support support = LSystem.base().support();
		final int bits = alpha ? 4 : 3;
		final ByteBuffer pixels = support.newByteBuffer(width * height * bits);
		gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
		gl.glReadPixels(x, y, width, height, alpha ? GL20.GL_RGBA : GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, pixels);
		int idx = 0;
		final int[] buffer = new int[width * height];
		if (flipY) {
			final int offset = -width * 2;
			int rev = width * (height - 1);
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					if (alpha) {
						int r = pixels.get(idx++) & 0xFF;
						int g = pixels.get(idx++) & 0xFF;
						int b = pixels.get(idx++) & 0xFF;
						int a = pixels.get(idx++) & 0xFF;
						buffer[rev] = LColor.argb(a, r, g, b);
					} else {
						int r = pixels.get(idx++) & 0xFF;
						int g = pixels.get(idx++) & 0xFF;
						int b = pixels.get(idx++) & 0xFF;
						buffer[rev] = LColor.rgb(r, g, b);
					}
					rev++;
				}
				rev += offset;
			}
		} else {
			int dst = 0;
			for (int y1 = 0; y1 < height; y1++) {
				for (int x1 = 0; x1 < width; x1++) {
					if (alpha) {
						int r = pixels.get(idx++) & 0xFF;
						int g = pixels.get(idx++) & 0xFF;
						int b = pixels.get(idx++) & 0xFF;
						int a = pixels.get(idx++) & 0xFF;
						buffer[dst + x1] = LColor.argb(a, r, g, b);
					} else {
						int r = pixels.get(idx++) & 0xFF;
						int g = pixels.get(idx++) & 0xFF;
						int b = pixels.get(idx++) & 0xFF;
						buffer[dst + x1] = LColor.rgb(r, g, b);
					}
				}
				dst += width;
			}
		}
		Canvas canvas = Image.createCanvas(width, height);
		canvas.image.setPixels(buffer, width, height);
		return canvas.image;
	}

	public static Image getScreenshot() {
		return getScreenshot(0, 0, (int) (LSystem.viewSize.width * LSystem.getScaleWidth()),
				(int) (LSystem.viewSize.height * LSystem.getScaleHeight()), true);
	}

	public static Image getScreenshot(int x, int y, int w, int h) {
		return getScreenshot(x, y, w, h, true);
	}

	public static Image getScreenshot(int x, int y, int w, int h, boolean flipY, boolean alpha) {
		return getFrameBuffeImage(LSystem.base().graphics().gl, x, y, w, h, flipY, alpha);
	}

	public static Image getScreenshot(int x, int y, int w, int h, boolean flipY) {
		return getScreenshot(x, y, w, h, flipY, LSystem.isDesktop() ? false : true);
	}

	public static boolean isFrameBufferCompleted() {
		return isFrameBufferCompleted(LSystem.base().graphics().gl);
	}

	public static boolean isFrameBufferCompleted(GL20 gl) {
		return gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER) == GL20.GL_FRAMEBUFFER_COMPLETE;
	}

	public static ShaderProgram compileShader(String vertexPath, String fragmentPath) {
		return compileShader(vertexPath, fragmentPath, LSystem.EMPTY, LSystem.EMPTY);
	}

	public static ShaderProgram compileShader(String vertexPath, String fragmentPath, String vertDefines,
			String pixelDefines) {
		if (fragmentPath == null) {
			throw new LSysException("Vertex shader cannot be null .");
		}
		if (vertexPath == null) {
			throw new LSysException("Fragment shader cannot be null .");
		}
		if (vertDefines == null || pixelDefines == null) {
			throw new LSysException("Defines cannot be null .");
		}
		final StrBuilder sbr = new StrBuilder();
		sbr.append("Compiling \"").append(vertexPath).append('/').append(fragmentPath).append('\"');
		if (vertDefines.length() > 0 || pixelDefines.length() > 0) {
			sbr.append(" w/ (").append(vertDefines.replace("\n", ", ")).append(")(")
					.append(pixelDefines.replace("\n", ", ")).append(")");
		}
		sbr.append("......");
		final String prependVert = vertDefines;
		final String prependFrag = pixelDefines;
		final String srcVert = BaseIO.loadText(vertexPath);
		final String srcFrag = BaseIO.loadText(fragmentPath);
		final ShaderProgram shader = new ShaderProgram(prependVert + "\n" + srcVert, prependFrag + "\n" + srcFrag);
		if (!shader.isCompiled()) {
			throw new LSysException(
					"Shader compile error : " + vertexPath + "/" + fragmentPath + "\n" + shader.getLog());
		}
		return shader;
	}

	public static GlobalSource loadShaderSource(String vertexPath, String fragmentPath) {
		return loadShaderSource(vertexPath, fragmentPath, LSystem.EMPTY, LSystem.EMPTY);
	}

	public static GlobalSource loadShaderSource(String vertexPath, String fragmentPath, String vertDefines,
			String pixelDefines) {
		if (fragmentPath == null) {
			throw new LSysException("Vertex shader cannot be null .");
		}
		if (vertexPath == null) {
			throw new LSysException("Fragment shader cannot be null .");
		}
		if (vertDefines == null || pixelDefines == null) {
			throw new LSysException("Defines cannot be null .");
		}
		final StrBuilder sbr = new StrBuilder();
		sbr.append("Compiling \"").append(vertexPath).append('/').append(fragmentPath).append('\"');
		if (vertDefines.length() > 0 || pixelDefines.length() > 0) {
			sbr.append(" w/ (").append(vertDefines.replace("\n", ", ")).append(")(")
					.append(pixelDefines.replace("\n", ", ")).append(")");
		}
		sbr.append("......");
		final String prependVert = vertDefines;
		final String prependFrag = pixelDefines;
		final String srcVert = BaseIO.loadText(vertexPath);
		final String srcFrag = BaseIO.loadText(fragmentPath);
		return new GlobalSource(prependVert + "\n" + srcVert, prependFrag + "\n" + srcFrag);
	}

	public static ShaderProgram createShaderProgram(String ver, String fragment) {
		final ShaderProgram shader = new ShaderProgram(ver, fragment);
		if (shader.isCompiled() == false) {
			throw new LSysException("Shader compile error : " + shader.getLog());
		}
		return shader;
	}
}
