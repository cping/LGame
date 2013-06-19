package loon.core.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.device.LTrans;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.resource.Resources;
import loon.utils.StringUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * 
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
public class LImage implements LRelease {

	/**
	 * 0.3.3版新增类，用以处理TGA格式图像（仅支持24&32位图）
	 */
	public static class LFormatTGA {

		private static final int TGA_HEADER_SIZE = 18;

		private static final int TGA_HEADER_INVALID = 0;

		private static final int TGA_HEADER_UNCOMPRESSED = 1;

		private static final int TGA_HEADER_COMPRESSED = 2;

		public static class State implements LRelease {

			public int type;

			public int pixelDepth;

			public int width;

			public int height;

			public int[] pixels;

			@Override
			public void dispose() {
				if (pixels != null) {
					pixels = null;
				}
			}

		}

		public static State inJustDecode(String res) throws IOException {
			return inJustDecode(Resources.openResource(res));
		}

		public static State inJustDecode(InputStream in) throws IOException {
			return loadHeader(in, new State());
		}

		private static State loadHeader(InputStream in, State info)
				throws IOException {

			in.read();
			in.read();

			info.type = (byte) in.read();

			in.read();
			in.read();
			in.read();
			in.read();
			in.read();
			in.read();
			in.read();
			in.read();
			in.read();

			info.width = (in.read() & 0xff) | ((in.read() & 0xff) << 8);
			info.height = (in.read() & 0xff) | ((in.read() & 0xff) << 8);

			info.pixelDepth = in.read() & 0xff;

			return info;
		}

		private static final short getUnsignedByte(byte[] bytes, int byteIndex) {
			return (short) (bytes[byteIndex] & 0xFF);
		}

		private static final int getUnsignedShort(byte[] bytes, int byteIndex) {
			return (getUnsignedByte(bytes, byteIndex + 1) << 8)
					+ getUnsignedByte(bytes, byteIndex + 0);
		}

		private static void readBuffer(InputStream in, byte[] buffer)
				throws IOException {
			int bytesRead = 0;
			int bytesToRead = buffer.length;
			for (; bytesToRead > 0;) {
				int read = in.read(buffer, bytesRead, bytesToRead);
				bytesRead += read;
				bytesToRead -= read;
			}
		}

		private static final void skipBytes(InputStream in, long toSkip)
				throws IOException {
			for (; toSkip > 0L;) {
				long skipped = in.skip(toSkip);
				if (skipped > 0) {
					toSkip -= skipped;
				} else if (skipped < 0) {
					toSkip = 0;
				}
			}
		}

		private static final int compareFormatHeader(InputStream in,
				byte[] header) throws IOException {

			readBuffer(in, header);
			boolean hasPalette = false;
			int result = TGA_HEADER_INVALID;

			int imgIDSize = getUnsignedByte(header, 0);

			if ((header[1] != (byte) 0) && (header[1] != (byte) 1)) {
				return TGA_HEADER_INVALID;
			}

			switch (getUnsignedByte(header, 2)) {
			case 0:
				result = TGA_HEADER_UNCOMPRESSED;
				break;
			case 1:
				hasPalette = true;
				result = TGA_HEADER_UNCOMPRESSED;
				throw new RuntimeException(
						"Indexed State is not yet supported !");
			case 2:
				result = TGA_HEADER_UNCOMPRESSED;
				break;
			case 3:
				result = TGA_HEADER_UNCOMPRESSED;
				break;
			case 9:
				hasPalette = true;
				result = TGA_HEADER_COMPRESSED;
				throw new RuntimeException(
						"Indexed State is not yet supported !");
			case 10:
				result = TGA_HEADER_COMPRESSED;
				break;
			case 11:
				result = TGA_HEADER_COMPRESSED;
				break;
			default:
				return TGA_HEADER_INVALID;
			}
			if (!hasPalette) {
				if (getUnsignedShort(header, 3) != 0) {
					return TGA_HEADER_INVALID;
				}
			}
			if (!hasPalette) {
				if (getUnsignedShort(header, 5) != 0) {
					return TGA_HEADER_INVALID;
				}
			}

			short paletteEntrySize = getUnsignedByte(header, 7);
			if (!hasPalette) {
				if (paletteEntrySize != 0) {
					return TGA_HEADER_INVALID;
				}
			} else {
				if ((paletteEntrySize != 15) && (paletteEntrySize != 16)
						&& (paletteEntrySize != 24) && (paletteEntrySize != 32)) {
					return TGA_HEADER_INVALID;
				}
			}

			if (getUnsignedShort(header, 8) != 0) {
				return TGA_HEADER_INVALID;
			}

			if (getUnsignedShort(header, 10) != 0) {
				return TGA_HEADER_INVALID;
			}

			switch (getUnsignedByte(header, 16)) {
			case 1:
			case 8:
			case 15:
			case 16:
				throw new RuntimeException(
						"this State with non RGB or RGBA pixels are not yet supported.");
			case 24:
			case 32:
				break;
			default:
				return TGA_HEADER_INVALID;
			}

			if (imgIDSize != 0) {
				skipBytes(in, imgIDSize);
			}

			return result;
		}

		private static final void writePixel(int[] pixels, final byte red,
				final byte green, final byte blue, final byte alpha,
				final boolean hasAlpha, final int offset) {
			int pixel;
			if (hasAlpha) {
				pixel = (red & 0xff);
				pixel |= ((green & 0xff) << 8);
				pixel |= ((blue & 0xff) << 16);
				pixel |= ((alpha & 0xff) << 24);
				pixels[offset / 4] = pixel;
			} else {
				pixel = (red & 0xff);
				pixel |= ((green & 0xff) << 8);
				pixel |= ((blue & 0xff) << 16);
				pixels[offset / 4] = pixel;
			}
		}

		private static int[] readBuffer(InputStream in, int width, int height,
				int srcBytesPerPixel, boolean acceptAlpha,
				boolean flipVertically) throws IOException {

			int[] pixels = new int[width * height];
			byte[] buffer = new byte[srcBytesPerPixel];

			final boolean copyAlpha = (srcBytesPerPixel == 4) && acceptAlpha;
			final int dstBytesPerPixel = acceptAlpha ? srcBytesPerPixel : 3;
			final int trgLineSize = width * dstBytesPerPixel;

			int dstByteOffset = 0;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int read = in.read(buffer, 0, srcBytesPerPixel);

					if (read < srcBytesPerPixel) {
						return pixels;
					}
					int actualByteOffset = dstByteOffset;
					if (!flipVertically) {
						actualByteOffset = ((height - y - 1) * trgLineSize)
								+ (x * dstBytesPerPixel);
					}

					if (copyAlpha) {
						writePixel(pixels, buffer[2], buffer[1], buffer[0],
								buffer[3], true, actualByteOffset);
					} else {
						writePixel(pixels, buffer[2], buffer[1], buffer[0],
								(byte) 0, false, actualByteOffset);
					}

					dstByteOffset += dstBytesPerPixel;
				}
			}
			return pixels;
		}

		private static void loadUncompressed(byte[] header, State tga,
				InputStream in, boolean acceptAlpha, boolean flipVertically)
				throws IOException {

			// 图像宽
			int orgWidth = getUnsignedShort(header, 12);

			// 图像高
			int orgHeight = getUnsignedShort(header, 14);

			// 图像位图(24&32)
			int pixelDepth = getUnsignedByte(header, 16);

			tga.width = orgWidth;
			tga.height = orgHeight;
			tga.pixelDepth = pixelDepth;

			boolean isOriginBottom = (header[17] & 0x20) == 0;

			if (!isOriginBottom) {
				flipVertically = !flipVertically;
			}

			// 不支持的格式
			if ((orgWidth <= 0) || (orgHeight <= 0)
					|| ((pixelDepth != 24) && (pixelDepth != 32))) {
				throw new IOException("Invalid texture information !");
			}

			int bytesPerPixel = (pixelDepth / 8);

			// 获取图像数据并转为int[]
			tga.pixels = readBuffer(in, orgWidth, orgHeight, bytesPerPixel,
					acceptAlpha, flipVertically);
			// 图像色彩模式
			tga.type = (acceptAlpha && (bytesPerPixel == 4) ? 4 : 3);
		}

		private static void loadCompressed(byte[] header, State tga,
				InputStream in, boolean acceptAlpha, boolean flipVertically)
				throws IOException {

			int orgWidth = getUnsignedShort(header, 12);
			int orgHeight = getUnsignedShort(header, 14);
			int pixelDepth = getUnsignedByte(header, 16);

			tga.width = orgWidth;
			tga.height = orgHeight;
			tga.pixelDepth = pixelDepth;

			boolean isOriginBottom = (header[17] & 0x20) == 0;

			if (!isOriginBottom) {
				flipVertically = !flipVertically;
			}

			if ((orgWidth <= 0) || (orgHeight <= 0)
					|| ((pixelDepth != 24) && (pixelDepth != 32))) {
				throw new IOException("Invalid texture information !");
			}

			int bytesPerPixel = (pixelDepth / 8);
			int pixelCount = orgHeight * orgWidth;
			int currentPixel = 0;

			byte[] colorBuffer = new byte[bytesPerPixel];

			int width = orgWidth;
			int height = orgHeight;

			final int dstBytesPerPixel = (acceptAlpha && (bytesPerPixel == 4) ? 4
					: 3);
			final int trgLineSize = orgWidth * dstBytesPerPixel;

			int[] pixels = new int[width * height];

			int dstByteOffset = 0;

			do {
				int chunkHeader = 0;
				try {
					chunkHeader = (byte) in.read() & 0xFF;
				} catch (IOException e) {
					throw new IOException(
							"Could not read RLE imageData header !");
				}

				boolean repeatColor;

				if (chunkHeader < 128) {
					chunkHeader++;
					repeatColor = false;
				} else {
					chunkHeader -= 127;
					readBuffer(in, colorBuffer);
					repeatColor = true;
				}

				for (int counter = 0; counter < chunkHeader; counter++) {
					if (!repeatColor) {
						readBuffer(in, colorBuffer);
					}

					int x = currentPixel % orgWidth;
					int y = currentPixel / orgWidth;

					int actualByteOffset = dstByteOffset;
					if (!flipVertically) {
						actualByteOffset = ((height - y - 1) * trgLineSize)
								+ (x * dstBytesPerPixel);
					}

					if (dstBytesPerPixel == 4) {
						writePixel(pixels, colorBuffer[2], colorBuffer[1],
								colorBuffer[0], colorBuffer[3], true,
								actualByteOffset);
					} else {
						writePixel(pixels, colorBuffer[2], colorBuffer[1],
								colorBuffer[0], (byte) 0, false,
								actualByteOffset);
					}

					dstByteOffset += dstBytesPerPixel;

					currentPixel++;

					if (currentPixel > pixelCount) {
						throw new IOException("Too many pixels read !");
					}
				}
			} while (currentPixel < pixelCount);

			tga.pixels = pixels;
			tga.type = dstBytesPerPixel;

		}

		public static State load(String res) throws IOException {
			return load(res, new State());
		}

		public static State load(String res, State tag) throws IOException {
			InputStream in = Resources.openResource(res);
			State tga = load(in, tag, true, false);
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (Exception e) {
				}
			}
			return tga;
		}

		public static State load(InputStream in, State tga,
				boolean acceptAlpha, boolean flipVertically) throws IOException {
			if (in.available() < TGA_HEADER_SIZE) {
				return (null);
			}
			byte[] header = new byte[TGA_HEADER_SIZE];
			final int headerType = compareFormatHeader(in, header);
			if (headerType == TGA_HEADER_INVALID) {
				return (null);
			}
			if (headerType == TGA_HEADER_UNCOMPRESSED) {
				loadUncompressed(header, tga, in, acceptAlpha, flipVertically);
			} else if (headerType == TGA_HEADER_COMPRESSED) {
				loadCompressed(header, tga, in, acceptAlpha, flipVertically);
			} else {
				throw new IOException("State file be type 2 or type 10 !");
			}
			return tga;
		}
	}

	private final static String tgaExtension = ".tga";

	private final static ArrayList<LImage> images = new ArrayList<LImage>(100);

	private Bitmap bitmap;

	private String fileName;

	private LGraphics g;

	private int width, height;

	private boolean isClose, isUpdate, isAutoDispose = true;

	private LTexture texture;

	private Format format = Format.DEFAULT;

	public static LImage createImage(InputStream in, boolean transparency) {
		return GraphicsUtils.loadImage(in, transparency);
	}

	public static LImage createImage(byte[] buffer) {
		return GraphicsUtils.loadImage(buffer, true);
	}

	public static LImage createImage(byte[] buffer, boolean transparency) {
		return GraphicsUtils.loadImage(buffer, transparency);
	}

	public static LImage createImage(int width, int height, boolean transparency) {
		return new LImage(width, height, transparency);
	}

	public static LImage createImage(int width, int height) {
		return new LImage(width, height, false);
	}

	public static LImage createImage(int width, int height, Config config) {
		return new LImage(width, height, config);
	}

	public static LImage createImage(byte[] imageData, int imageOffset,
			int imageLength, boolean transparency) {
		return GraphicsUtils.loadImage(imageData, imageOffset, imageLength,
				transparency);
	}

	public static LImage createImage(byte[] imageData, int imageOffset,
			int imageLength) {
		return GraphicsUtils.loadImage(imageData, imageOffset, imageLength,
				false);
	}

	public static LImage createImage(String fileName) {
		return GraphicsUtils.loadImage(fileName);
	}

	/**
	 * 以指定像素集合生成LImage文件
	 * 
	 * @param rgb
	 * @param width
	 * @param height
	 * @param processAlpha
	 * @return
	 */
	public static final LImage createRGBImage(int[] rgb, int width, int height,
			boolean processAlpha) {
		Bitmap bitmap = null;
		try {
			Bitmap.Config config;
			if (processAlpha) {
				config = Bitmap.Config.ARGB_8888;
			} else {
				config = Bitmap.Config.RGB_565;
			}
			bitmap = Bitmap.createBitmap(rgb, width, height, config);
		} catch (Exception e) {
			LSystem.gc();
			Bitmap.Config config;
			if (processAlpha) {
				config = Bitmap.Config.ARGB_8888;
			} else {
				config = Bitmap.Config.RGB_565;
			}
			bitmap = Bitmap.createBitmap(rgb, width, height, config);
		}
		return new LImage(bitmap);
	}

	/**
	 * 生成旋转为指定角度的图像
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param transform
	 * @return
	 */
	public static LImage createImage(LImage image, int x, int y, int width,
			int height, int transform) {
		int[] buf = new int[width * height];
		image.getPixels(buf, 0, width, x, y, width, height);
		int th;
		int tw;
		if ((transform & 4) != 0) {
			th = width;
			tw = height;
		} else {
			th = height;
			tw = width;
		}
		if (transform != 0) {
			int[] trans = new int[buf.length];
			int sp = 0;
			for (int sy = 0; sy < height; sy++) {
				int tx;
				int ty;
				int td;

				switch (transform) {
				case LTrans.TRANS_ROT90:
					tx = tw - sy - 1;
					ty = 0;
					td = tw;
					break;
				case LTrans.TRANS_ROT180:
					tx = tw - 1;
					ty = th - sy - 1;
					td = -1;
					break;
				case LTrans.TRANS_ROT270:
					tx = sy;
					ty = th - 1;
					td = -tw;
					break;
				case LTrans.TRANS_MIRROR:
					tx = tw - 1;
					ty = sy;
					td = -1;
					break;
				case LTrans.TRANS_MIRROR_ROT90:
					tx = tw - sy - 1;
					ty = th - 1;
					td = -tw;
					break;
				case LTrans.TRANS_MIRROR_ROT180:
					tx = 0;
					ty = th - sy - 1;
					td = 1;
					break;
				case LTrans.TRANS_MIRROR_ROT270:
					tx = sy;
					ty = 0;
					td = tw;
					break;
				default:
					throw new RuntimeException("illegal transformation: "
							+ transform);
				}

				int tp = ty * tw + tx;
				for (int sx = 0; sx < width; sx++) {
					trans[tp] = buf[sp++];
					tp += td;
				}
			}
			buf = trans;
		}

		return createRGBImage(buf, tw, th, true);
	}

	/**
	 * 创建指定数量的LImage
	 * 
	 * @param count
	 * @param w
	 * @param h
	 * @param transparency
	 * @return
	 */
	public static LImage[] createImage(int count, int w, int h,
			boolean transparency) {
		LImage[] image = new LImage[count];
		for (int i = 0; i < image.length; i++) {
			image[i] = new LImage(w, h, transparency);
		}
		return image;
	}

	/**
	 * 创建指定数量的LImage
	 * 
	 * @param count
	 * @param w
	 * @param h
	 * @param config
	 * @return
	 */
	public static LImage[] createImage(int count, int w, int h, Config config) {
		LImage[] image = new LImage[count];
		for (int i = 0; i < image.length; i++) {
			image[i] = new LImage(w, h, config);
		}
		return image;
	}

	public LImage(String fileName) {
		this(fileName, null);
	}

	public LImage(String fileName, Config config) {
		if (fileName == null) {
			throw new RuntimeException("file name is null !");
		}
		String res;
		if (StringUtils.startsWith(fileName,'/')) {
			res = fileName.substring(1);
		} else {
			res = fileName;
		}
		this.fileName = fileName;
		Bitmap bitmap = null;
		if (fileName.toLowerCase().lastIndexOf(tgaExtension) != -1) {
			try {
				LFormatTGA.State tga = LFormatTGA.load(res);
				if (tga != null) {
					bitmap = Bitmap.createBitmap(tga.pixels, tga.width,
							tga.height, tga.type == 4 ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
					tga.dispose();
					tga = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			bitmap = GraphicsUtils.loadBitmap(res, config);
		}
		if (bitmap == null) {
			throw new RuntimeException("File " + fileName + " was not found !");
		}
		setBitmap(bitmap);
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(int width, int height) {
		this(width, height, false);
	}

	/**
	 * 构建一个LImage(true:ARGB8888或false:RGB565)
	 * 
	 * @param width
	 * @param height
	 * @param transparency
	 */
	public LImage(int width, int height, boolean transparency) {
		try {
			LSystem.gc(1000, 1);
			this.width = width;
			this.height = height;
			if (transparency) {
				this.bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
			} else {
				this.bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.RGB_565);
			}
		} catch (Exception e) {
			try {
				GraphicsUtils.destroy();
				LTextures.destroyAll();
				LSystem.gc();
				this.width = width;
				this.height = height;
				if (transparency) {
					this.bitmap = Bitmap.createBitmap(width, height,
							Bitmap.Config.ARGB_8888);
				} else {
					this.bitmap = Bitmap.createBitmap(width, height,
							Bitmap.Config.RGB_565);
				}
			} catch (Exception ex) {
				LSystem.gc();
			}
		}
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(int width, int height, Config config) {
		this.width = width;
		this.height = height;
		this.bitmap = Bitmap.createBitmap(width, height, config);
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(LImage img) {
		this(img.getBitmap());
	}

	public LImage(Bitmap bitmap) {
		setBitmap(bitmap);
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public void setBitmap(Bitmap bitmap) {
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		this.bitmap = bitmap;
	}

	public Config getConfig() {
		Config config = bitmap.getConfig();
		if (config == null) {
			return Config.ARGB_8888;
		}
		return config;
	}

	@Override
	public LImage clone() {
		return new LImage(bitmap);
	}

	public boolean hasAlpha() {
		if (bitmap == null) {
			return false;
		}
		if (bitmap.getConfig() == Config.RGB_565) {
			return false;
		}
		return bitmap.hasAlpha();
	}

	public LGraphics getLGraphics() {
		if (g == null || g.isClose()) {
			g = new LGraphics(bitmap);
			isUpdate = true;
		}
		return g;
	}

	public LGraphics create() {
		return new LGraphics(bitmap);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public int getWidth() {
		return bitmap.getWidth();
	}

	public int getHeight() {
		return bitmap.getHeight();
	}

	public int[] getPixels() {
		int pixels[] = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		return pixels;
	}

	public int[] getPixels(int pixels[]) {
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		return pixels;
	}

	public int[] getPixels(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		bitmap.getPixels(pixels, 0, w, x, y, w, h);
		return pixels;
	}

	public int[] getPixels(int offset, int stride, int x, int y, int w, int h) {
		int pixels[] = new int[w * h];
		bitmap.getPixels(pixels, offset, stride, x, y, w, h);
		return pixels;
	}

	public int[] getPixels(int pixels[], int offset, int stride, int x, int y,
			int width, int height) {
		bitmap.getPixels(pixels, offset, stride, x, y, width, height);
		return pixels;
	}

	public void setPixels(int[] pixels, int w, int h) {
		isUpdate = true;
		bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
	}

	public void setPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		isUpdate = true;
		bitmap.setPixels(pixels, offset, stride, x, y, width, height);
	}

	public void setRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
		setPixels(rgbArray, offset, scansize, startX, startY, w, h);
	}

	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		isUpdate = true;
		bitmap.setPixels(pixels, 0, w, x, y, w, h);
		return pixels;
	}

	public int getPixel(int x, int y) {
		return bitmap.getPixel(x, y);
	}

	public int[] getRGB(int pixels[], int offset, int stride, int x, int y,
			int width, int height) {
		bitmap.getPixels(pixels, offset, stride, x, y, width, height);
		return pixels;
	}

	public void getRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
		getPixels(rgbArray, offset, scansize, startX, startY, w, h);
	}

	public int getRGB(int x, int y) {
		return bitmap.getPixel(x, y);
	}

	public void setPixel(LColor c, int x, int y) {
		bitmap.setPixel(x, y, c.getRGB());
	}

	public void setPixel(int rgb, int x, int y) {
		isUpdate = true;
		bitmap.setPixel(x, y, rgb);
	}

	public void setRGB(int rgb, int x, int y) {
		isUpdate = true;
		bitmap.setPixel(x, y, rgb);
	}

	/**
	 * 截小图
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param transparency
	 * @return
	 */
	public LImage getSubImage(int x, int y, int w, int h, Config config) {
		return GraphicsUtils.drawClipImage(this, w, h, x, y, config);
	}

	/**
	 * 截小图
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public LImage getSubImage(int x, int y, int w, int h) {
		return GraphicsUtils
				.drawClipImage(this, w, h, x, y, bitmap.getConfig());
	}

	/**
	 * 扩充图像为指定大小
	 * 
	 * @param w
	 * @param h
	 * @return
	 */
	public LImage scaledInstance(int w, int h) {
		int width = getWidth();
		int height = getHeight();
		if (width == w && height == h) {
			return this;
		}
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
		return new LImage(resizedBitmap);
	}

	public LColor getColorAt(int x, int y) {
		return new LColor(this.getRGBAt(x, y));
	}

	public int getRGBAt(int x, int y) {
		if (x >= this.getWidth()) {
			throw new IndexOutOfBoundsException("X is out of bounds: " + x
					+ "," + this.getWidth());
		} else if (y >= this.getHeight()) {
			throw new IndexOutOfBoundsException("Y is out of bounds: " + y
					+ "," + this.getHeight());
		} else if (x < 0) {
			throw new IndexOutOfBoundsException("X is out of bounds: " + x);
		} else if (y < 0) {
			throw new IndexOutOfBoundsException("Y is out of bounds: " + y);
		} else {
			return bitmap.getPixel(x, y);
		}
	}

	/**
	 * 返回LImage的hash序列
	 * 
	 */
	@Override
	public int hashCode() {
		return GraphicsUtils.hashBitmap(bitmap);
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
		this.isUpdate = true;
	}

	public LTexture getTexture() {
		if (texture == null || texture.isClose() || isUpdate) {
			setAutoDispose(false);
			LTexture tmp = texture;
			texture = new LTexture(GLLoader.getTextureData(this), format);
			if (tmp != null) {
				tmp.dispose();
				tmp = null;
			}
			isUpdate = false;
		}
		return texture;
	}

	/**
	 * 判定当前LImage是否已被关闭
	 * 
	 * @return
	 */
	public boolean isClose() {
		return isClose || bitmap == null
				|| (bitmap != null ? bitmap.isRecycled() : false);
	}

	public boolean isAutoDispose() {
		return isAutoDispose && !isClose();
	}

	public void setAutoDispose(boolean dispose) {
		this.isAutoDispose = dispose;
	}

	public LPixmapData newPixmap() {
		return new LPixmapData(this);
	}

	public String getPath() {
		return fileName;
	}

	@Override
	public void dispose() {
		dispose(true);
	}

	private void dispose(boolean remove) {
		isClose = true;
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		if (texture != null && isAutoDispose) {
			texture.dispose();
			texture = null;
		}
		if (remove) {
			images.remove(this);
		}
	}

	public static void disposeAll() {
		if (images.size() > 0) {
			for (LImage img : images) {
				if (img != null) {
					img.dispose(false);
					img = null;
				}
			}
			images.clear();
		}
	}
}
