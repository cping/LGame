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
package loon.core.graphics.device;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.ArrayList;

import loon.AndroidGraphicsUtils;
import loon.LConfig;
import loon.LSystem;
import loon.core.LRelease;
import loon.core.LRuntimeHack;
import loon.core.graphics.filetype.TGA;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.jni.NativeSupport;
import loon.utils.StringUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class LImage implements LRelease {

	private final static ArrayList<LImage> images = new ArrayList<LImage>(100);

	private int bitSize(Bitmap b) {
		return b.getRowBytes() * b.getHeight();
	}

	private Bitmap bitmap;

	private String fileName;

	private LGraphics g;

	private int width, height;

	private boolean isClose, isUpdate, isAutoDispose = true;

	private LTexture texture;

	private Format format = Format.DEFAULT;

	public static LImage createImage(InputStream in, boolean transparency) {
		return AndroidGraphicsUtils.loadImage(in, transparency);
	}

	public static LImage createImage(byte[] buffer, int[] filters) {
		return new LImage(AndroidGraphicsUtils.filterBitmap(buffer, filters));
	}

	public static LImage createImage(byte[] buffer, LColor filter) {
		return new LImage(AndroidGraphicsUtils.filterBitmap(buffer, filter));
	}

	public static LImage createImage(byte[] buffer) {
		return AndroidGraphicsUtils.loadImage(buffer, true);
	}

	public static LImage createImage(byte[] buffer, boolean transparency) {
		return AndroidGraphicsUtils.loadImage(buffer, transparency);
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
		return AndroidGraphicsUtils.loadImage(imageData, imageOffset,
				imageLength, transparency);
	}

	public static LImage createImage(byte[] imageData, int imageOffset,
			int imageLength) {
		return AndroidGraphicsUtils.loadImage(imageData, imageOffset,
				imageLength, false);
	}

	public static LImage createImage(String fileName) {
		return AndroidGraphicsUtils.loadImage(fileName);
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

	private final static ArrayList<String> _otherImages = new ArrayList<String>(
			10);

	static {
		_otherImages.add("tga");
	}

	public final static boolean existType(String fileName) {
		if (fileName == null) {
			return false;
		}
		return _otherImages.contains(LSystem.getExtension(fileName
				.toLowerCase()));
	}

	public LImage(String fileName, Config config) {
		this(fileName, config, true);
	}

	public LImage(String fileName, Config config, boolean filter) {
		if (fileName == null) {
			throw new RuntimeException("file name is null !");
		}
		String res;
		if (StringUtils.startsWith(fileName, '/')) {
			res = fileName.substring(1);
		} else {
			res = fileName;
		}
		this.fileName = fileName;
		Bitmap img = null;
		String ext = LSystem.getExtension(fileName.toLowerCase());
		if ("tga".equals(ext)) {
			try {
				TGA.State tga = TGA.load(res);
				if (tga != null) {
					img = Bitmap.createBitmap(tga.pixels, tga.width,
							tga.height, tga.type == 4 ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
					tga.dispose();
					tga = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			img = AndroidGraphicsUtils.loadBitmap(res, config);
		}
		if (img == null) {
			throw new RuntimeException("File " + fileName + " was not found !");
		}
		if (filter) {
			img = getFilterImage(fileName, ext, img);
		}
		setBitmap(img);
		if (this.bitmap != null) {
			LRuntimeHack.get().trackFree(bitSize(this.bitmap));
		}
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	private static Bitmap getFilterImage(final String name, final String ext,
			Bitmap img) {
		LConfig config = LSystem.getConfig();
		if (config.isAutoAllFilter()) {
			return img;
		}
		if (config.isAutoColorFilter() && config.getFilterFiles() != null
				&& config.getColors() != null) {
			if (config.getFilterkeywords() != null) {
				int count = 0;
				final String[] res = config.getFilterkeywords();
				for (int i = 0; i < res.length; i++) {
					if (name.indexOf(res[i]) != -1) {
						count++;
						break;
					}
				}
				if (count == 0) {
					return img;
				}
			}
			final int size = config.getColors().length;
			for (String e : config.getFilterFiles()) {
				if (e.equalsIgnoreCase(ext) && size > 0) {
					if (size == 1) {
						if (img.hasAlpha() && img.getConfig() != Config.RGB_565) {
							int[] srcImages = AndroidGraphicsUtils
									.getPixels(img);
							int[] pixels = NativeSupport.toColorKey(srcImages,
									config.getColors()[0]);
							if (!img.isMutable()) {
								Bitmap tmp = Bitmap.createBitmap(pixels,
										img.getWidth(), img.getHeight(),
										img.getConfig());
								if (img != null) {
									img.recycle();
									img = null;
								}
								img = tmp;
							} else {
								AndroidGraphicsUtils.setPixels(img, pixels,
										img.getWidth(), img.getHeight());
							}
						} else {
							Bitmap tmp = img.copy(Config.ARGB_8888, true);
							int[] srcImages = AndroidGraphicsUtils
									.getPixels(tmp);
							int[] pixels = NativeSupport.toColorKey(srcImages,
									config.getColors()[0]);
							AndroidGraphicsUtils.setPixels(tmp, pixels,
									img.getWidth(), img.getHeight());
							if (img != null) {
								img.recycle();
								img = null;
							}
							img = tmp;
						}
						return img;
					} else {
						if (img.hasAlpha() && img.getConfig() != Config.RGB_565) {
							int[] srcImages = AndroidGraphicsUtils
									.getPixels(img);
							int[] pixels = NativeSupport.toColorKeys(srcImages,
									config.getColors());
							if (!img.isMutable()) {
								Bitmap tmp = Bitmap.createBitmap(pixels,
										img.getWidth(), img.getHeight(),
										img.getConfig());
								if (img != null) {
									img.recycle();
									img = null;
								}
								img = tmp;
							} else {
								AndroidGraphicsUtils.setPixels(img, pixels,
										img.getWidth(), img.getHeight());
							}
						} else {
							Bitmap tmp = img.copy(Config.ARGB_8888, true);
							int[] srcImages = AndroidGraphicsUtils
									.getPixels(tmp);
							int[] pixels = NativeSupport.toColorKeys(srcImages,
									config.getColors());
							AndroidGraphicsUtils.setPixels(tmp, pixels,
									img.getWidth(), img.getHeight());
							if (img != null) {
								img.recycle();
								img = null;
							}
							img = tmp;
						}
						return img;
					}
				}
			}
		}
		return img;
	}

	private static Bitmap getFilterAllImage(Bitmap img) {
		LConfig config = LSystem.getConfig();
		if (!config.isAutoAllFilter()) {
			return img;
		}
		if (config.isAutoColorFilter() && config.getColors() != null) {
			final int size = config.getColors().length;
			if (size > 0) {
				if (size == 1) {
					if (img.hasAlpha() && img.getConfig() != Config.RGB_565) {
						int[] srcImages = AndroidGraphicsUtils.getPixels(img);
						int[] pixels = NativeSupport.toColorKey(srcImages,
								config.getColors()[0]);
						if (!img.isMutable()) {
							Bitmap tmp = Bitmap.createBitmap(pixels,
									img.getWidth(), img.getHeight(),
									img.getConfig());
							if (img != null) {
								img.recycle();
								img = null;
							}
							img = tmp;
						} else {
							AndroidGraphicsUtils.setPixels(img, pixels,
									img.getWidth(), img.getHeight());
						}
					} else {
						Bitmap tmp = img.copy(Config.ARGB_8888, true);
						int[] srcImages = AndroidGraphicsUtils.getPixels(tmp);
						int[] pixels = NativeSupport.toColorKey(srcImages,
								config.getColors()[0]);
						AndroidGraphicsUtils.setPixels(tmp, pixels,
								img.getWidth(), img.getHeight());
						if (img != null) {
							img.recycle();
							img = null;
						}
						img = tmp;
					}
					return img;
				} else {
					if (img.hasAlpha() && img.getConfig() != Config.RGB_565) {
						int[] srcImages = AndroidGraphicsUtils.getPixels(img);
						int[] pixels = NativeSupport.toColorKeys(srcImages,
								config.getColors());
						if (!img.isMutable()) {
							Bitmap tmp = Bitmap.createBitmap(pixels,
									img.getWidth(), img.getHeight(),
									img.getConfig());
							if (img != null) {
								img.recycle();
								img = null;
							}
							img = tmp;
						} else {
							AndroidGraphicsUtils.setPixels(img, pixels,
									img.getWidth(), img.getHeight());
						}
					} else {
						Bitmap tmp = img.copy(Config.ARGB_8888, true);
						int[] srcImages = AndroidGraphicsUtils.getPixels(tmp);
						int[] pixels = NativeSupport.toColorKeys(srcImages,
								config.getColors());
						AndroidGraphicsUtils.setPixels(tmp, pixels,
								img.getWidth(), img.getHeight());
						if (img != null) {
							img.recycle();
							img = null;
						}
						img = tmp;
					}
					return img;
				}
			}
		}
		return img;
	}

	public LImage(int width, int height) {
		this(width, height, true);
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
				AndroidGraphicsUtils.destroy();
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
		if (this.bitmap != null) {
			LRuntimeHack.get().trackFree(bitSize(this.bitmap));
		}
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(int width, int height, Config config) {
		this.width = width;
		this.height = height;
		this.bitmap = Bitmap.createBitmap(width, height, config);
		if (this.bitmap != null) {
			LRuntimeHack.get().trackFree(bitSize(this.bitmap));
		}
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(LImage img) {
		this(img.getBitmap());
	}

	public LImage(Bitmap bitmap) {
		setBitmap(bitmap);
		if (this.bitmap != null) {
			LRuntimeHack.get().trackFree(bitSize(this.bitmap));
		}
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	private void setBitmap(Bitmap img) {
		this.bitmap = getFilterAllImage(img);
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
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
		return AndroidGraphicsUtils.drawClipImage(this, w, h, x, y, config);
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
		return AndroidGraphicsUtils.drawClipImage(this, w, h, x, y,
				bitmap.getConfig());
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

	public LImage light(float v) {
		return getLightImage(this, v);
	}

	public LImage light(int v) {
		return getLightImage(this, v);
	}

	public static LImage getLightImage(LImage img, float v) {
		return getLightImage(img, (int) (v * 255));
	}

	public static LImage getLightImage(LImage img, int v) {
		LImage newImage = new LImage(img.getWidth(), img.getHeight(), true);
		LGraphics g = newImage.getLGraphics();
		g.drawImage(img, 0, 0);
		getLight(newImage, v);
		return newImage;
	}

	public static void getLight(LImage img, int v) {
		int width = img.getWidth();
		int height = img.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				int rgbValue = img.getRGB(x, y);
				if (rgbValue != 0) {
					int color = getLight(rgbValue, v);
					img.setRGB(color, x, y);
				}
			}
		}
	}

	public static int getLight(int color, int v) {
		int red = LColor.getRed(color);
		int green = LColor.getGreen(color);
		int blue = LColor.getBlue(color);
		red += v;
		green += v;
		blue += v;
		blue = blue > 255 ? 255 : blue;
		red = red > 255 ? 255 : red;
		green = green > 255 ? 255 : green;
		red = red < 0 ? 0 : red;
		green = green < 0 ? 0 : green;
		blue = blue < 0 ? 0 : blue;
		return LColor.getRGB(red, green, blue);
	}

	/**
	 * 返回LImage的hash序列
	 * 
	 */
	@Override
	public int hashCode() {
		return AndroidGraphicsUtils.hashBitmap(bitmap);
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
		this.isUpdate = true;
	}

	public LTexture getTexture() {
		return getTexture(true);
	}

	public LTexture getTexture(boolean autoFree) {
		if (texture == null || texture.isClose() || isUpdate) {
			setAutoDispose(autoFree);
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

	public Buffer getByteBuffer() {
		return getByteBuffer(this);
	}

	public static Buffer getByteBuffer(LImage image) {
		int[] pixels = image.getPixels();
		if (image.hasAlpha()) {
			return GLLoader.argbToRGBABuffer(pixels);
		} else {
			return GLLoader.argbToRGBBuffer(pixels);
		}
	}

	@Override
	public void dispose() {
		dispose(true);
	}

	private void dispose(boolean remove) {
		isClose = true;
		if (this.bitmap != null) {
			LRuntimeHack.get().trackAlloc(bitSize(this.bitmap));
			this.bitmap.recycle();
			this.bitmap = null;
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
