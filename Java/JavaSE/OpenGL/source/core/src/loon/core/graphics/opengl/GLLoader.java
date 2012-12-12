package loon.core.graphics.opengl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.resource.Resources;
import loon.jni.NativeSupport;
import loon.utils.GraphicsUtils;

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

	public static LTextureData geTextureData(final Buffer buffer,
			final boolean hasAlpha, final int width, final int height) {
		LTextureData data = new LTextureData() {
			public LTextureData copy() {
				return null;
			}

		};
		data.width = width;
		data.height = height;
		data.texWidth = width;
		data.texHeight = data.height;
		data.hasAlpha = hasAlpha;
		data.source = buffer;
		return data;
	}

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

	public static LTextureData getTextureData(BufferedImage img) {
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
		if (fileName == null) {
			throw new RuntimeException("Path is null !");
		}
		return loadLazy(fileName);
	}

	private final static LTextureData loadLazy(String fileName) {
		synchronized (lazyLoader) {
			String key = fileName.trim().toLowerCase();
			LTextureData data = lazyLoader.get(key);
			if (data == null || data.source == null) {
				try {
					lazyLoader.put(key, data = new GLLoader(fileName));
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
		this.source = data.source;
		this.pixels = data.pixels;
		this.fileName = data.fileName;
	}

	private GLLoader(LImage image) {
		create(image);
	}

	private GLLoader(BufferedImage image) {
		create(image);
	}

	private GLLoader(String fileName) {
		if (fileName == null) {
			throw new RuntimeException("file name is null !");
		}
		if (fileName.endsWith("png") && multipyAlpha) {
			ByteBuffer result = null;
			PNGData imageData = new PNGData();
			try {
				result = imageData.loadImage(Resources.openResource(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.fileName = fileName;
			this.width = imageData.getWidth();
			this.height = imageData.getHeight();
			this.texHeight = imageData.getTexHeight();
			this.texWidth = imageData.getTexWidth();
			this.hasAlpha = imageData.hasAlpha();
			this.source = result;
		} else {
			this.create(LImage.createImage(fileName));
			this.fileName = fileName;
		}
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
		if (fileName == null) {
			fileName = image.getPath();
		}

		int srcWidth = image.getWidth();
		int srcHeight = image.getHeight();

		this.hasAlpha = image.hasAlpha();

		int texWidth = GLEx.toPowerOfTwo(srcWidth);
		int texHeight = GLEx.toPowerOfTwo(srcHeight);

		this.width = srcWidth;
		this.height = srcHeight;
		this.texHeight = texHeight;
		this.texWidth = texWidth;

		LImage texImage = new LImage(texWidth, texHeight, hasAlpha);

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

		this.source = texImage.getByteBuffer();
		if (fileName == null) {
			this.pixels = texImage.getPixels();
		}
		if (image.isAutoDispose()) {
			image.dispose();
			image = null;
		}
	}

	/**
	 * 将BufferedImage转化为LTextureData
	 * 
	 * @param image
	 */
	private void create(BufferedImage image) {

		int srcWidth = image.getWidth();
		int srcHeight = image.getHeight();

		this.hasAlpha = image.getColorModel().hasAlpha();

		if (GLEx.isPowerOfTwo(srcWidth) && GLEx.isPowerOfTwo(srcHeight)) {
			this.width = srcWidth;
			this.height = srcHeight;
			this.texHeight = srcHeight;
			this.texWidth = srcWidth;
			this.source = NativeSupport.getByteBuffer((byte[]) image
					.getRaster().getDataElements(0, 0, image.getWidth(),
							image.getHeight(), null));
			if (fileName == null) {
				this.pixels = GraphicsUtils.getPixels(image);
			}
			return;
		}

		int texWidth = GLEx.toPowerOfTwo(srcWidth);
		int texHeight = GLEx.toPowerOfTwo(srcHeight);

		this.width = srcWidth;
		this.height = srcHeight;
		this.texHeight = texHeight;
		this.texWidth = texWidth;

		BufferedImage texImage = new BufferedImage(texWidth, texHeight,
				hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR
						: BufferedImage.TYPE_3BYTE_BGR);

		Graphics2D g = texImage.createGraphics();

		g.drawImage(image, 0, 0, null);

		if (height < texHeight - 1) {
			copyArea(texImage, g, 0, 0, width, 1, 0, texHeight - 1);
			copyArea(texImage, g, 0, height - 1, width, 1, 0, 1);
		}
		if (width < texWidth - 1) {
			copyArea(texImage, g, 0, 0, 1, height, texWidth - 1, 0);
			copyArea(texImage, g, width - 1, 0, 1, height, 1, 0);
		}

		source = NativeSupport.getByteBuffer((byte[]) texImage.getRaster()
				.getDataElements(0, 0, texImage.getWidth(),
						texImage.getHeight(), null));
		if (fileName == null) {
			this.pixels = GraphicsUtils.getPixels(texImage);
		}

		if (texImage != null) {
			texImage.flush();
			texImage = null;
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
	 * 复制指定的BufferedImage图像区域
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
	public static void copyArea(BufferedImage image, Graphics2D g, int x,
			int y, int width, int height, int dx, int dy) {
		BufferedImage tmp = image.getSubimage(x, y, width, height);
		g.drawImage(tmp, x + dx, y + dy, null);
		tmp.flush();
		tmp = null;
	}

	/**
	 * 复制当前LTextureData
	 * 
	 */
	public LTextureData copy() {
		return new GLLoader(this, true);
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
