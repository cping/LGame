package loon.core.graphics.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import loon.core.graphics.GraphicsUtils;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.jni.NativeSupport;
import loon.utils.CollectionUtils;
import loon.utils.FileUtils;

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
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public final class GLLoader extends LTextureData {

	private static final boolean IS_LITTLE_ENDIAN = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

	private final static HashMap<String, LTextureData> lazyLoader = new HashMap<String, LTextureData>(
			10);

	public static LTextureData getTextureData(LImage img) {
		if (img == null) {
			throw new RuntimeException("Source image is null !");
		}
		try {
			return new GLLoader(img);
		} catch (Exception e) {
			throw new RuntimeException("Source image is null !");
		}
	}

	public static LTextureData getTextureData(String fileName) {
		return getTextureData(fileName, null);
	}

	public static LTextureData getTextureData(String fileName,
			android.graphics.Bitmap.Config config) {
		if (fileName == null) {
			throw new RuntimeException("Path is null !");
		}
		return loadLazy(fileName, config);
	}

	private final static LTextureData loadLazy(String fileName,
			android.graphics.Bitmap.Config config) {
		synchronized (lazyLoader) {
			String key = fileName.trim().toLowerCase();
			LTextureData data = lazyLoader.get(key);
			if (data == null || data.source == null) {
				try {
					lazyLoader.put(key, data = new GLLoader(fileName, config));
				} catch (Exception ex) {
					throw new RuntimeException("Path " + fileName
							+ " is null !");
				}
			}
			return data;
		}
	}

	public final static void destory() {
		LTextureBatch.clearBatchCaches();
		synchronized (lazyLoader) {
			for (LTextureData loader : lazyLoader.values()) {
				if (loader != null) {
					loader.dispose();
					loader = null;
				}
			}
			lazyLoader.clear();
		}
	}

	private GLLoader(LTextureData data, boolean newCopy) {
		this.width = data.width;
		this.height = data.height;
		this.texWidth = data.texWidth;
		this.texHeight = data.texHeight;
		this.hasAlpha = data.hasAlpha;
		this.source = newCopy ? CollectionUtils.copyOf(data.source)
				: data.source;
		this.fileName = data.fileName;
	}

	private GLLoader(LImage image) {
		create(image);
	}

	private GLLoader(String fileName, android.graphics.Bitmap.Config config) {
		if (fileName == null) {
			throw new RuntimeException("file name is null !");
		}
		this.create(new LImage(fileName, config));
		this.fileName = fileName;
	}

	/**
	 * 将LImage转化为LTextureData
	 * 
	 * @param image
	 * @return
	 */
	private void create(LImage image) {
		if (image == null) {
			return;
		}
		if (source != null) {
			return;
		}
		if (fileName == null) {
			fileName = image.getPath();
		}

		this.config = image.getConfig();
		int srcWidth = image.getWidth();
		int srcHeight = image.getHeight();
		this.hasAlpha = image.hasAlpha();

		if (fileName != null && !fileName.endsWith("tga")) {

			if (GLEx.isPowerOfTwo(srcWidth) && GLEx.isPowerOfTwo(srcHeight)) {
				this.width = srcWidth;
				this.height = srcHeight;
				this.texHeight = srcHeight;
				this.texWidth = srcWidth;

				if (image.isAutoDispose()) {
					image.dispose();
					image = null;
				}
				return;
			}

			int texWidth = GLEx.toPowerOfTwo(srcWidth);
			int texHeight = GLEx.toPowerOfTwo(srcHeight);

			this.width = srcWidth;
			this.height = srcHeight;
			this.texHeight = texHeight;
			this.texWidth = texWidth;

			if (image != null && image.isAutoDispose()) {
				image.dispose();
				image = null;
			}
		} else {

			if (GLEx.isPowerOfTwo(srcWidth) && GLEx.isPowerOfTwo(srcHeight)) {
				this.width = srcWidth;
				this.height = srcHeight;
				this.texHeight = srcHeight;
				this.texWidth = srcWidth;
				this.source = image.getPixels();
				if (image.isAutoDispose()) {
					image.dispose();
					image = null;
				}
				return;
			}

			int texWidth = GLEx.toPowerOfTwo(srcWidth);
			int texHeight = GLEx.toPowerOfTwo(srcHeight);

			this.width = srcWidth;
			this.height = srcHeight;
			this.texHeight = texHeight;
			this.texWidth = texWidth;

			LImage texImage = new LImage(texWidth, texHeight, image.getConfig());

			LGraphics g = texImage.getLGraphics();

			g.drawImage(image, 0, 0);

			if (this.height < texHeight - 1) {
				copyArea(texImage, g, 0, 0, width, 1, 0, texHeight - 1);
				copyArea(texImage, g, 0, height - 1, width, 1, 0, 1);
			}
			if (this.width < texWidth - 1) {
				copyArea(texImage, g, 0, 0, 1, height, texWidth - 1, 0);
				copyArea(texImage, g, width - 1, 0, 1, height, 1, 0);
			}

			this.source = texImage.getPixels();

			if (texImage != null && texImage.isAutoDispose()) {
				texImage.dispose();
				texImage = null;
			}
			if (image != null && image.isAutoDispose()) {
				image.dispose();
				image = null;
			}
		}

	}

	/**
	 * 复制指定的LImage图像区域
	 * 
	 * @param image
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param dx
	 * @param dy
	 */
	public static void copyArea(LImage image, LGraphics g, int x, int y,
			int width, int height, int dx, int dy) {
		LImage tmp = image.getSubImage(x, y, width, height);
		g.drawImage(tmp, x + dx, y + dy);
		tmp.dispose();
		tmp = null;
	}

	/**
	 * 复制当前LTextureData
	 * 
	 */
	@Override
	public LTextureData copy() {
		return new GLLoader(this, true);
	}

	/**
	 * 创建纹理
	 */
	@Override
	public void createTexture() {
		submitGL(this);
	}

	/**
	 * 提交纹理数据到GL渲染器
	 */
	public final static void submitGL(LTextureData data) {
		if (data.source == null && data.fileName != null) {
			if (data.multipyAlpha) {
				data.multipyAlpha = isPNGExt(data.fileName);
			}
			PixelFormat format = PixelFormat.getPixelFormat(data.config);
			android.graphics.Bitmap temp = GraphicsUtils.loadBitmap(
					data.fileName, data.config);
			LImage texImage = new LImage(data.texWidth, data.texHeight,
					data.config);
			LGraphics g = texImage.getLGraphics();
			g.drawBitmap(temp, 0, 0);
			if (data.height < data.texHeight - 1) {
				copyArea(texImage, g, 0, 0, data.width, 1, 0,
						data.texHeight - 1);
				copyArea(texImage, g, 0, data.height - 1, data.width, 1, 0, 1);
			}
			if (data.width < data.texWidth - 1) {
				copyArea(texImage, g, 0, 0, 1, data.height, data.texWidth - 1,
						0);
				copyArea(texImage, g, data.width - 1, 0, 1, data.height, 1, 0);
			}
			android.opengl.GLUtils.texImage2D(GL.GL_TEXTURE_2D, 0,
					format.getGLFormat(), texImage.getBitmap(),
					format.getGLType(), 0);
			if (texImage != null) {
				texImage.dispose();
				texImage = null;
			}
			if (temp != null) {
				temp.recycle();
				temp = null;
			}
		} else {
			if (data.multipyAlpha) {
				data.multipyAlpha = isPNGExt(data.fileName);
			}
			PixelFormat format = PixelFormat.getPixelFormat(data.config);
			if (data.multipyAlpha
					&& (data.texWidth > 48 && data.texHeight > 48)
					&& (data.config != android.graphics.Bitmap.Config.RGB_565)) {
				final GL10 gl = GLEx.gl10;
				if (gl != null) {
					Buffer pixelBuffer = getBufferPixels(data.source, format);
					gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,
							format.getGLFormat(), data.texWidth,
							data.texHeight, 0, format.getGLFormat(),
							format.getGLType(), pixelBuffer);
					pixelBuffer = null;
				}
			} else {
				android.graphics.Bitmap bind = android.graphics.Bitmap
						.createBitmap(data.texWidth, data.texHeight,
								data.config);
				bind.setPixels(data.source, 0, data.texWidth, 0, 0,
						data.texWidth, data.texHeight);
				android.opengl.GLUtils.texImage2D(GL.GL_TEXTURE_2D, 0,
						format.getGLFormat(), bind, format.getGLType(), 0);
				if (bind != null) {
					bind.recycle();
					bind = null;
				}
			}
		}
		if (data.fileName != null) {
			data.source = null;
		}
	}

	/**
	 * 添加纹理到当前纹理之上
	 * 
	 * @param x
	 * @param y
	 */
	public void addTexture(int x, int y) {
		addGL(this, x, y);
	}

	/**
	 * 添加纹理数据到GL渲染器
	 */
	public final static void addGL(LTextureData data, int x, int y) {
		if (data.multipyAlpha) {
			data.multipyAlpha = isPNGExt(data.fileName);
		}
		PixelFormat format = PixelFormat.getPixelFormat(data.config);
		if (data.multipyAlpha && (data.texWidth > 48 && data.texHeight > 48)
				&& (data.config != android.graphics.Bitmap.Config.RGB_565)) {
			final GL10 gl = GLEx.gl10;
			if (gl != null) {
				Buffer pixelBuffer = getBufferPixels(data.source, format);
				gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, x, y, data.width,
						data.height, format.getGLFormat(), format.getGLType(),
						pixelBuffer);
				pixelBuffer = null;
				if (data.fileName != null) {
					data.source = null;
				}
			}
		} else {
			LImage image = LTextureData.createPixelImage(data.source,
					data.texWidth, data.texHeight, data.width, data.height,
					data.config);
			android.opengl.GLUtils
					.texSubImage2D(GL.GL_TEXTURE_2D, 0, x, y,
							image.getBitmap(), format.getGLFormat(),
							format.getGLType());
			if (image != null) {
				image.dispose();
				image = null;
			}
			if (data.fileName != null) {
				data.source = null;
			}
		}
	}

	private static boolean isPNGExt(String resName) {
		if (resName == null) {
			return false;
		} else {
			String ext = FileUtils.getExtension(resName);
			if ("png".equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 像素修正
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int[] getFixPixels(android.graphics.Bitmap bitmap) {
		final int[] rgba = GraphicsUtils.getPixels(bitmap);
		PixelFormat format = PixelFormat.getPixelFormat(bitmap.getConfig());
		switch (format) {
		case RGBA_8888:
			return convertARGB_8888toRGBA_8888(rgba);
		default:
			break;
		}
		return rgba;
	}

	private static Buffer getBufferPixels(final int[] source, PixelFormat format) {
		final int[] rgba = CollectionUtils.copyOf(source);
		switch (format) {
		case RGB_565:
			return ByteBuffer.wrap(convertARGB_8888toRGB_565(rgba));
		case RGBA_8888:
			return IntBuffer.wrap(convertARGB_8888toRGBA_8888(rgba));
		case RGBA_4444:
			return ByteBuffer.wrap(convertARGB_8888toARGB_4444(rgba));
		case A_8:
			return ByteBuffer.wrap(convertARGB_8888toA_8(rgba));
		default:
			throw new IllegalArgumentException("Unexpected "
					+ PixelFormat.class.getSimpleName() + ": '" + format + "'.");
		}
	}

	private static int[] convertARGB_8888toRGBA_8888(final int[] pixels) {
		if (IS_LITTLE_ENDIAN) {
			for (int i = pixels.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				pixels[i] = pixel & 0xFF00FF00 | (pixel & 0x000000FF) << 16
						| (pixel & 0x00FF0000) >> 16;
			}
		} else {
			for (int i = pixels.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				pixels[i] = (pixel & 0x00FFFFFF) << 8
						| (pixel & 0xFF000000) >> 24;
			}
		}
		return pixels;
	}

	private static byte[] convertARGB_8888toRGB_565(final int[] pixels) {
		final byte[] pixelsRGB_565 = new byte[pixels.length * 2];
		if (IS_LITTLE_ENDIAN) {
			for (int i = pixels.length - 1, j = pixelsRGB_565.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				final int red = ((pixel >> 16) & 0xFF);
				final int green = ((pixel >> 8) & 0xFF);
				final int blue = ((pixel) & 0xFF);
				pixelsRGB_565[j--] = (byte) ((red & 0xF8) | (green >> 5));
				pixelsRGB_565[j--] = (byte) (((green << 3) & 0xE0) | (blue >> 3));
			}
		} else {
			for (int i = pixels.length - 1, j = pixelsRGB_565.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				final int red = ((pixel >> 16) & 0xFF);
				final int green = ((pixel >> 8) & 0xFF);
				final int blue = ((pixel) & 0xFF);
				pixelsRGB_565[j--] = (byte) (((green << 3) & 0xE0) | (blue >> 3));
				pixelsRGB_565[j--] = (byte) ((red & 0xF8) | (green >> 5));
			}
		}
		return pixelsRGB_565;
	}

	private static byte[] convertARGB_8888toARGB_4444(final int[] pixels) {
		final byte[] pixelsARGB_4444 = new byte[pixels.length * 2];
		if (IS_LITTLE_ENDIAN) {
			for (int i = pixels.length - 1, j = pixelsARGB_4444.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				final int alpha = ((pixel >> 28) & 0x0F);
				final int red = ((pixel >> 16) & 0xF0);
				final int green = ((pixel >> 8) & 0xF0);
				final int blue = ((pixel) & 0x0F);
				pixelsARGB_4444[j--] = (byte) (alpha | red);
				pixelsARGB_4444[j--] = (byte) (green | blue);
			}
		} else {
			for (int i = pixels.length - 1, j = pixelsARGB_4444.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				final int alpha = ((pixel >> 28) & 0x0F);
				final int red = ((pixel >> 16) & 0xF0);
				final int green = ((pixel >> 8) & 0xF0);
				final int blue = ((pixel) & 0x0F);
				pixelsARGB_4444[j--] = (byte) (green | blue);
				pixelsARGB_4444[j--] = (byte) (alpha | red);
			}
		}
		return pixelsARGB_4444;
	}

	private static byte[] convertARGB_8888toA_8(final int[] pixels) {
		final byte[] pixelsA_8 = new byte[pixels.length];
		if (IS_LITTLE_ENDIAN) {
			for (int i = pixels.length - 1; i >= 0; i--) {
				pixelsA_8[i] = (byte) (pixels[i] >> 24);
			}
		} else {
			for (int i = pixels.length - 1; i >= 0; i--) {
				pixelsA_8[i] = (byte) (pixels[i] & 0xFF);
			}
		}
		return pixelsA_8;
	}

	public static Buffer argbToRGBABuffer(final int[] pixels) {
		if (IS_LITTLE_ENDIAN) {
			for (int i = pixels.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				final int red = ((pixel >> 16) & 0xFF);
				final int green = ((pixel >> 8) & 0xFF);
				final int blue = ((pixel) & 0xFF);
				final int alpha = (pixel >> 24);
				pixels[i] = alpha << 24 | blue << 16 | green << 8 | red;
			}
		} else {
			for (int i = pixels.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				final int red = ((pixel >> 16) & 0xFF);
				final int green = ((pixel >> 8) & 0xFF);
				final int blue = ((pixel) & 0xFF);
				final int alpha = (pixel >> 24);
				pixels[i] = red << 24 | green << 16 | blue << 8 | alpha;
			}
		}
		return NativeSupport.newIntBuffer(pixels);
	}

	public static Buffer argbToRGBBuffer(final int[] pixels) {
		if (IS_LITTLE_ENDIAN) {
			for (int i = pixels.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				final int red = ((pixel >> 16) & 0xFF);
				final int green = ((pixel >> 8) & 0xFF);
				final int blue = ((pixel) & 0xFF);
				pixels[i] = blue << 16 | green << 8 | red;
			}
		} else {
			for (int i = pixels.length - 1; i >= 0; i--) {
				final int pixel = pixels[i];
				final int red = ((pixel >> 16) & 0xFF);
				final int green = ((pixel >> 8) & 0xFF);
				final int blue = ((pixel) & 0xFF);
				pixels[i] = red << 24 | green << 16 | blue << 8;
			}
		}
		return NativeSupport.newIntBuffer(pixels);
	}

}
