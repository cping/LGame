package loon.core.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.resource.Resources;
import loon.utils.MathUtils;
import loon.utils.collection.ArrayByte;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

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
 * @email javachenpeng@yahoo.com
 * @version 0.1.2
 */
public class GraphicsUtils {

	final static public Matrix matrix = new Matrix();

	final static public Canvas canvas = new Canvas();

	final static private HashMap<String, LImage> lazyImages = new HashMap<String, LImage>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	final static public BitmapFactory.Options defaultoptions = new BitmapFactory.Options();

	final static public BitmapFactory.Options ARGB8888options = new BitmapFactory.Options();

	final static public BitmapFactory.Options RGB565options = new BitmapFactory.Options();

	static {
		ARGB8888options.inDither = false;
		ARGB8888options.inJustDecodeBounds = false;
		ARGB8888options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		RGB565options.inDither = false;
		RGB565options.inJustDecodeBounds = false;
		RGB565options.inPreferredConfig = Bitmap.Config.RGB_565;
		try {
			BitmapFactory.Options.class.getField("inPurgeable").set(
					ARGB8888options, true);
			BitmapFactory.Options.class.getField("inPurgeable").set(
					RGB565options, true);
			BitmapFactory.Options.class.getField("inPurgeable").set(
					defaultoptions, true);
			BitmapFactory.Options.class.getField("inInputShareable").set(
					ARGB8888options, true);
			BitmapFactory.Options.class.getField("inInputShareable").set(
					RGB565options, true);
			BitmapFactory.Options.class.getField("inInputShareable").set(
					defaultoptions, true);
		} catch (Exception e) {
		}
		try {
			BitmapFactory.Options.class.getField("inScaled").set(
					ARGB8888options, false);
			BitmapFactory.Options.class.getField("inScaled").set(RGB565options,
					false);
			BitmapFactory.Options.class.getField("inScaled").set(
					defaultoptions, false);
		} catch (Exception e) {
		}
	}

	/**
	 * 加载标准位图文件
	 * 
	 * @param in
	 * @param transparency
	 * @return
	 */
	final static public Bitmap loadBitmap(InputStream in, boolean transparency) {
		if (LSystem.IMAGE_SIZE != 0) {
			return loadSizeBitmap(in, LSystem.IMAGE_SIZE, transparency);
		} else {
			return BitmapFactory.decodeStream(in, null,
					transparency ? ARGB8888options : RGB565options);
		}
	}

	/**
	 * 加载标准位图文件
	 * 
	 * @param resName
	 * @param transparency
	 * @return
	 */
	final static public Bitmap loadBitmap(String resName, boolean transparency) {
		if (LSystem.IMAGE_SIZE != 0) {
			return loadSizeBitmap(resName, LSystem.IMAGE_SIZE, transparency);
		} else {
			InputStream in = null;
			try {
				in = Resources.openResource(resName);
				return loadBitmap(Resources.openResource(resName), transparency);
			} catch (IOException e) {
				throw new RuntimeException(resName + " not found!");
			} finally {
				LSystem.close(in);
			}
		}
	}

	/**
	 * 以指定大小加载指定的位图文件
	 * 
	 * @param resName
	 * @param width
	 * @param height
	 * @return
	 */
	final static public Bitmap loadScaleBitmap(String resName, int width,
			int height) {
		InputStream in = null;
		try {
			in = Resources.openResource(resName);
			BitmapFactory.Options emptyoptions = new BitmapFactory.Options();
			emptyoptions.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, emptyoptions);
			int scaleWidth = MathUtils.floor((float) emptyoptions.outWidth
					/ width);
			int scaleHeight = MathUtils.floor((float) emptyoptions.outHeight
					/ height);
			emptyoptions.inJustDecodeBounds = false;
			emptyoptions.inSampleSize = Math.min(scaleWidth, scaleHeight);
			return BitmapFactory.decodeStream(in, null, emptyoptions);
		} catch (Exception e) {
			throw new RuntimeException(resName + " not found!");
		} finally {
			LSystem.close(in);
		}
	}

	final static public Bitmap loadBitmap(String resName) {
		return loadBitmap(resName, null);
	}

	/**
	 * 以默认参数加载指定路径图片
	 * 
	 * @param resName
	 * @return
	 */
	final static public Bitmap loadBitmap(String resName, Config config) {
		if (LSystem.IMAGE_SIZE != 0) {
			return loadSizeBitmap(resName, LSystem.IMAGE_SIZE, true);
		} else {
			InputStream in = null;
			try {
				if (config != null) {
					defaultoptions.inPreferredConfig = config;
				}
				in = Resources.openResource(resName);
				Bitmap bitmap = BitmapFactory.decodeStream(in, null,
						defaultoptions);
				if (Config.RGB_565 == config) {
					bitmap = filterBitmapTo565Bitmap(bitmap, bitmap.getWidth(),
							bitmap.getHeight());
				}
				return bitmap;
			} catch (Exception e) {
				throw new RuntimeException(resName + " not found!");
			} finally {
				LSystem.close(in);
			}
		}
	}

	/**
	 * 以指定大小加载指定的LImage文件
	 * 
	 * @param resName
	 * @param width
	 * @param height
	 * @return
	 */
	final static public LImage loadScaleImage(String resName, int width,
			int height) {
		return new LImage(loadScaleBitmap(resName, width, height));
	}

	/**
	 * 加载LImage
	 * 
	 * @param in
	 * @return
	 */
	final static public LImage loadImage(InputStream in, boolean transparency) {
		if (LSystem.IMAGE_SIZE != 0) {
			return loadPoorImage(in, LSystem.IMAGE_SIZE, transparency);
		} else {
			Bitmap bitmap = BitmapFactory.decodeStream(in, null,
					transparency ? ARGB8888options : RGB565options);
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			if ((w < 16 || w > 96) && (h < 16 || h > 96)) {
				return new LImage(bitmap);
			}
			return filterBitmapTo565(bitmap, w, h);
		}
	}

	/**
	 * 返回一个画质较差（经过缩放）的LImage
	 * 
	 * @param resName
	 * @param sampleSize
	 * @param transparency
	 * @return
	 */
	final static public LImage loadPoorImage(String resName, int sampleSize,
			boolean transparency) {
		return new LImage(loadSizeBitmap(resName, sampleSize, transparency));
	}

	/**
	 * 返回一个画质较差（经过缩放）的Bitmap
	 * 
	 * @param resName
	 * @param sampleSize
	 * @return
	 */
	final static public Bitmap loadSizeBitmap(String resName, int sampleSize,
			boolean transparency) {
		InputStream in = null;
		try {
			in = Resources.openResource(resName);
			return loadSizeBitmap(in, sampleSize, transparency);
		} catch (IOException e) {
			throw new RuntimeException(resName + " not found!");
		} finally {
			LSystem.close(in);
		}
	}

	/**
	 * 返回一个画质较差（经过缩放）的LImage
	 * 
	 * @param in
	 * @param sampleSize
	 * @param transparency
	 * @return
	 */
	final static public LImage loadPoorImage(InputStream in, int sampleSize,
			boolean transparency) {
		return new LImage(loadSizeBitmap(in, sampleSize, transparency));
	}

	/**
	 * 返回一个画质较差（经过缩放）的Bitmap
	 * 
	 * @param in
	 * @param sampleSize
	 * @param transparency
	 * @return
	 */
	final static public Bitmap loadSizeBitmap(InputStream in, int sampleSize,
			boolean transparency) {
		ArrayByte byteArray = new ArrayByte();
		try {
			byteArray.write(in);
			byteArray.reset();
			return loadSizeBitmap(byteArray.getData(), sampleSize, transparency);
		} catch (IOException ex) {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
				return BitmapFactory.decodeStream(in, null,
						transparency ? ARGB8888options : RGB565options);
			} catch (IOException e) {
				throw new RuntimeException("Image not found!");
			}
		} finally {
			byteArray = null;
		}

	}

	/**
	 * 返回一个画质较差（经过缩放）的Image
	 * 
	 * @param bytes
	 * @param sampleSize
	 * @param transparency
	 * @return
	 */
	final static public LImage loadPoorImage(byte[] bytes, int sampleSize,
			boolean transparency) {
		return new LImage(loadSizeBitmap(bytes, sampleSize, transparency));
	}

	/**
	 * 返回一个画质较差（经过缩放）的Bitmap
	 * 
	 * @param bytes
	 * @param sampleSize
	 * @param transparency
	 * @return
	 */
	final static public Bitmap loadSizeBitmap(byte[] bytes, int sampleSize,
			boolean transparency) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inSampleSize = sampleSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = transparency ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;

		try {
			BitmapFactory.Options.class.getField("inPurgeable").set(options,
					true);
			BitmapFactory.Options.class.getField("inInputShareable").set(
					options, true);
		} catch (Exception e) {
		}

		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}

	/**
	 * 当非透明图像不等于RGB565模式时，将它过滤为此彩色模式
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	final static public LImage filterBitmapTo565(Bitmap src, int w, int h) {
		return new LImage(filterBitmapTo565Bitmap(src, w, h));
	}

	/**
	 * 当非透明图像不等于RGB565模式时，将它过滤为此彩色模式
	 * 
	 * @param src
	 * @param w
	 * @param h
	 * @return
	 */
	final static public Bitmap filterBitmapTo565Bitmap(Bitmap src, int w, int h) {
		Config config = src.getConfig();
		if (config != Config.RGB_565 && config != Config.ARGB_4444
				&& config != Config.ALPHA_8) {
			boolean isOpaque = true;
			int pixel;
			int size = w * h;
			int[] pixels = new int[size];
			src.getPixels(pixels, 0, w, 0, 0, w, h);
			for (int i = 0; i < size; i++) {
				pixel = LColor.premultiply(pixels[i]);
				if (isOpaque && (pixel >>> 24) < 255) {
					isOpaque = false;
					break;
				}
			}
			pixels = null;
			if (isOpaque) {
				Bitmap newBitmap = src.copy(Config.RGB_565, false);
				src.recycle();
				src = null;
				return newBitmap;
			}
		}
		return src;
	}

	/**
	 * 以ARGB8888格式加载LImage
	 * 
	 * @param in
	 * @param transparency
	 * @return
	 */
	final static public LImage load8888Image(InputStream in) {
		return new LImage(load8888Bitmap(in));
	}

	/**
	 * 以ARGB8888格式加载LImage
	 * 
	 * @param fileName
	 * @return
	 */
	final static public LImage load8888Image(String fileName) {
		return new LImage(load8888Bitmap(fileName));
	}

	/**
	 * 加载LImage
	 * 
	 * @param buffer
	 * @return
	 */
	final static public LImage load8888Image(byte[] buffer) {
		return new LImage(load8888Bitmap(buffer));
	}

	/**
	 * 加载Bitmap
	 * 
	 * @param in
	 * @return
	 */
	final static public Bitmap load8888Bitmap(InputStream in) {
		Bitmap bitmap = BitmapFactory.decodeStream(in, null, ARGB8888options);
		if (bitmap.getConfig() != Config.ARGB_8888) {
			Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, false);
			bitmap.recycle();
			bitmap = null;
			return newBitmap;
		}
		return bitmap;
	}

	/**
	 * 加载Bitmap
	 * 
	 * @param buffer
	 * @return
	 */
	final static public Bitmap load8888Bitmap(byte[] buffer) {
		return BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
				ARGB8888options);
	}

	/**
	 * 加载Bitmap
	 * 
	 * @param fileName
	 * @return
	 */
	final static public Bitmap load8888Bitmap(String fileName) {
		InputStream in = null;
		try {
			in = Resources.openResource(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(in, null,
					ARGB8888options);
			if (bitmap.getConfig() != Config.ARGB_8888) {
				Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, false);
				bitmap.recycle();
				bitmap = null;
				return newBitmap;
			}
			return bitmap;
		} catch (IOException e) {
			throw new RuntimeException(
					("File not found. ( " + fileName + " )").intern());
		} finally {
			LSystem.close(in);
		}
	}

	/**
	 * 加载LImage
	 * 
	 * @param buffer
	 * @return
	 */
	final static public LImage loadImage(byte[] buffer, boolean transparency) {
		return new LImage(loadBitmap(buffer, transparency));
	}

	/**
	 * 加载Bitmap
	 * 
	 * @param buffer
	 * @return
	 */
	final static public Bitmap loadBitmap(byte[] buffer, boolean transparency) {
		if (LSystem.IMAGE_SIZE != 0) {
			return loadSizeBitmap(buffer, LSystem.IMAGE_SIZE, transparency);
		} else {
			return BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
					transparency ? ARGB8888options : RGB565options);
		}
	}

	/**
	 * 加载LImage
	 * 
	 * @param imageData
	 * @param imageOffset
	 * @param imageLength
	 * @return
	 */
	final static public LImage loadImage(byte[] imageData, int imageOffset,
			int imageLength, boolean transparency) {
		return new LImage(loadBitmap(imageData, imageOffset, imageLength,
				transparency));
	}

	/**
	 * 加载Bitmap
	 * 
	 * @param imageData
	 * @param imageOffset
	 * @param imageLength
	 * @return
	 */
	final static public Bitmap loadBitmap(byte[] imageData, int imageOffset,
			int imageLength, boolean transparency) {
		return BitmapFactory.decodeByteArray(imageData, imageOffset,
				imageLength, transparency ? ARGB8888options : RGB565options);
	}

	/**
	 * 加载LImage
	 * 
	 * @param innerFileName
	 * @return
	 */
	final static public LImage loadImage(final String resName,
			boolean transparency) {
		if (resName == null) {
			return null;
		}
		String keyName = resName.toLowerCase();
		LImage image = lazyImages.get(keyName);
		if (image != null && !image.isClose()) {
			return image;
		} else {
			InputStream in = null;
			try {
				in = Resources.openResource(resName);
				image = loadImage(in, transparency);
				lazyImages.put(keyName, image);
			} catch (Exception e) {
				throw new RuntimeException(resName + " not found!");
			} finally {
				LSystem.close(in);
			}
		}
		if (image == null) {
			throw new RuntimeException(
					("File not found. ( " + resName + " )").intern());
		}
		return image;
	}

	final static public LImage loadImage(final String resName) {
		return GraphicsUtils.loadImage(resName, false);
	}

	final static public LImage loadNotCacheImage(final String resName,
			boolean transparency) {
		if (resName == null) {
			return null;
		}
		InputStream in = null;
		try {
			in = Resources.openResource(resName);
			return loadImage(in, transparency);
		} catch (Exception e) {
			throw new RuntimeException(resName + " not found!");
		} finally {
			LSystem.close(in);
		}
	}

	final static public LImage loadNotCacheImage(final String resName) {
		return GraphicsUtils.loadNotCacheImage(resName, false);
	}

	/**
	 * 获得一组序号连续的图片
	 * 
	 * @param fileName
	 * @param range
	 *            (指定图片范围，如("1-2"))
	 * @return
	 */
	public static LImage[] loadSequenceImages(String fileName, String range,
			boolean transparency) {
		try {
			int start_range = -1;
			int end_range = -1;
			int images_count = 1;
			int minusIndex = range.indexOf('-');
			if ((minusIndex > 0) && (minusIndex < (range.length() - 1))) {
				try {
					start_range = Integer.parseInt(range.substring(0,
							minusIndex));
					end_range = Integer.parseInt(range
							.substring(minusIndex + 1));
					if (start_range < end_range) {
						images_count = end_range - start_range + 1;
					}
				} catch (Exception ex) {
				}
			}
			LImage[] images = new LImage[images_count];
			for (int i = 0; i < images_count; i++) {
				String imageName = fileName;
				if (images_count > 1) {
					int dotIndex = fileName.lastIndexOf('.');
					if (dotIndex >= 0) {
						imageName = fileName.substring(0, dotIndex)
								+ (start_range + i)
								+ fileName.substring(dotIndex);
					}
				}
				images[i] = GraphicsUtils.loadImage(imageName, transparency);
			}
			return images;
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * 剪切指定图像
	 * 
	 * @param image
	 * @param objectWidth
	 * @param objectHeight
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static LImage drawClipImage(final LImage image, int objectWidth,
			int objectHeight, int x1, int y1, int x2, int y2, Config config) {
		if (image == null) {
			return null;
		}
		if (objectWidth > image.getWidth()) {
			objectWidth = image.getWidth();
		}
		if (objectHeight > image.getHeight()) {
			objectHeight = image.getHeight();
		}

		Bitmap bitmap = Bitmap.createBitmap(objectWidth, objectHeight, config);
		canvas.setBitmap(bitmap);
		canvas.drawBitmap(image.getBitmap(), new Rect(x1, y1, x2, y2),
				new Rect(0, 0, objectWidth, objectHeight), null);

		if (objectWidth == objectHeight && objectWidth <= 48
				&& objectHeight <= 48) {
			return filterBitmapTo565(bitmap, objectWidth, objectHeight);
		}
		return new LImage(bitmap);
	}

	/**
	 * 剪切指定图像
	 * 
	 * @param image
	 * @param objectWidth
	 * @param objectHeight
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static LImage drawClipImage(final LImage image, int objectWidth,
			int objectHeight, int x1, int y1, int x2, int y2) {
		return drawClipImage(image, objectWidth, objectHeight, x1, y1, x2, y2,
				image.getConfig());
	}

	/**
	 * 剪切指定图像
	 * 
	 * @param image
	 * @param objectWidth
	 * @param objectHeight
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param flag
	 * @return
	 */
	public static LImage drawClipImage(final LImage image, int objectWidth,
			int objectHeight, int x1, int y1, int x2, int y2, boolean flag) {
		return drawClipImage(image, objectWidth, objectHeight, x1, y1, x2, y2,
				flag ? image.getConfig() : Config.RGB_565);
	}

	/**
	 * 剪切指定图像
	 * 
	 * @param image
	 * @param objectWidth
	 * @param objectHeight
	 * @param x
	 * @param y
	 * @param flag
	 * @return
	 */
	public static LImage drawClipImage(final LImage image, int objectWidth,
			int objectHeight, int x, int y, boolean flag) {
		return drawClipImage(image, objectWidth, objectHeight, x, y,
				flag ? image.getConfig() : Config.RGB_565);
	}

	/**
	 * 剪切指定图像
	 * 
	 * @param image
	 * @param objectWidth
	 * @param objectHeight
	 * @param x
	 * @param y
	 * @param config
	 * @return
	 */
	public static LImage drawClipImage(final LImage image, int objectWidth,
			int objectHeight, int x, int y, Config config) {
		if (image.getWidth() == objectWidth
				&& image.getHeight() == objectHeight) {
			return image;
		}
		Bitmap bitmap = Bitmap.createBitmap(objectWidth, objectHeight, config);
		canvas.setBitmap(bitmap);
		canvas.drawBitmap(image.getBitmap(), new Rect(x, y, x + objectWidth,
				objectHeight + y), new Rect(0, 0, objectWidth, objectHeight),
				null);
		if (objectWidth == objectHeight && objectWidth <= 48
				&& objectHeight <= 48) {
			return filterBitmapTo565(bitmap, objectWidth, objectHeight);
		}
		return new LImage(bitmap);
	}

	/**
	 * 剪切指定图像
	 * 
	 * @param image
	 * @param objectWidth
	 * @param objectHeight
	 * @param x
	 * @param y
	 * @return
	 */
	public static LImage drawCropImage(final LImage image, int x, int y,
			int objectWidth, int objectHeight) {
		return GraphicsUtils.drawClipImage(image, objectWidth, objectHeight, x,
				y, image.getConfig());
	}

	/**
	 * 剪切指定图像
	 * 
	 * @param image
	 * @param objectWidth
	 * @param objectHeight
	 * @param x
	 * @param y
	 * @return
	 */
	public static LImage drawClipImage(final LImage image, int objectWidth,
			int objectHeight, int x, int y) {
		return GraphicsUtils.drawClipImage(image, objectWidth, objectHeight, x,
				y, image.getConfig());
	}

	/**
	 * 改变指定Image大小
	 * 
	 * @param image
	 * @param w
	 * @param h
	 * @return
	 */
	public static LImage getResize(LImage image, int w, int h) {
		return new LImage(GraphicsUtils.getResize(image.getBitmap(), w, h));
	}

	/**
	 * 改变指定Bitmap大小
	 * 
	 * @param image
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap getResize(Bitmap image, int w, int h, boolean flag) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (width == w && height == h) {
			return image;
		}
		int newWidth = w;
		int newHeight = h;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		matrix.reset();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
				matrix, flag);
		return resizedBitmap;
	}

	/**
	 * 改变指定Bitmap大小
	 * 
	 * @param image
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap getResize(Bitmap image, int w, int h) {
		return getResize(image, w, h, true);
	}

	/**
	 * 矫正指定的长、宽大小为适当值
	 * 
	 * @param srcWidth
	 * @param srcHeight
	 * @param dstWidth
	 * @param dstHeight
	 * @return
	 */
	final static public RectBox fitLimitSize(int srcWidth, int srcHeight,
			int dstWidth, int dstHeight) {
		int dw = dstWidth;
		int dh = dstHeight;
		if (dw != 0 && dh != 0) {
			double waspect = (double) dw / srcWidth;
			double haspect = (double) dh / srcHeight;
			if (waspect > haspect) {
				dw = (int) (srcWidth * haspect);
			} else {
				dh = (int) (srcHeight * waspect);
			}
		}
		return new RectBox(0, 0, dw, dh);
	}

	/**
	 * 生成Bitmap文件的hash序列
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int hashBitmap(Bitmap bitmap) {
		int hash_result = 0;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		hash_result = (hash_result << 7) ^ h;
		hash_result = (hash_result << 7) ^ w;
		for (int pixel = 0; pixel < 20; ++pixel) {
			int x = (pixel * 50) % w;
			int y = (pixel * 100) % h;
			hash_result = (hash_result << 7) ^ bitmap.getPixel(x, y);
		}
		return hash_result;
	}

	/**
	 * 从一个单独的Bitmap中获得其像素信息
	 * 
	 * @param bit
	 * @return
	 */
	public static int[] getPixels(Bitmap bit) {
		int w = bit.getWidth(), h = bit.getHeight();
		int pixels[] = new int[w * h];
		bit.getPixels(pixels, 0, w, 0, 0, w, h);
		/*if (bit.getConfig() != Config.ARGB_8888) {
			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = pixels[i] & 0x00ff0000 + pixels[i] & 0x0000ff00
						+ pixels[i] & 0x000000ff;
			}
		}*/
		return pixels;
	}

	/**
	 * 清空缓存
	 */
	public static void destroy() {
		if (lazyImages.size() > 0) {
			for (LImage img : lazyImages.values()) {
				if (img != null) {
					img.dispose();
					img = null;
				}
			}
			lazyImages.clear();
			LSystem.gc();
		}
	}
}
