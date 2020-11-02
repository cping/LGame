package org.loon.framework.android.game.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.geom.Rectangle;
import org.loon.framework.android.game.core.geom.Shape;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.resource.Resources;
import org.loon.framework.android.game.core.store.InvalidRecordIDException;
import org.loon.framework.android.game.core.store.RecordEnumeration;
import org.loon.framework.android.game.core.store.RecordStore;
import org.loon.framework.android.game.utils.collection.ArrayByte;

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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1.2
 */
public class GraphicsUtils {

	final static public Matrix matrix = new Matrix();

	final static public Canvas canvas = new Canvas();

	final static private HashMap<String, LImage> lazyImages = new HashMap<String, LImage>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	final static private HashMap<String, LImage[][]> lazySplitMap = new HashMap<String, LImage[][]>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	final static public BitmapFactory.Options ARGB4444options = new BitmapFactory.Options();

	final static public BitmapFactory.Options ARGB8888options = new BitmapFactory.Options();

	final static public BitmapFactory.Options RGB565options = new BitmapFactory.Options();

	static {
		ARGB8888options.inDither = false;
		ARGB8888options.inJustDecodeBounds = false;
		ARGB8888options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		ARGB4444options.inDither = false;
		ARGB4444options.inJustDecodeBounds = false;
		ARGB4444options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		RGB565options.inDither = false;
		RGB565options.inJustDecodeBounds = false;
		RGB565options.inPreferredConfig = Bitmap.Config.RGB_565;
		try {
			BitmapFactory.Options.class.getField("inPurgeable").set(
					ARGB8888options, true);
			BitmapFactory.Options.class.getField("inPurgeable").set(
					ARGB4444options, true);
			BitmapFactory.Options.class.getField("inPurgeable").set(
					RGB565options, true);

			BitmapFactory.Options.class.getField("inInputShareable").set(
					ARGB8888options, true);
			BitmapFactory.Options.class.getField("inInputShareable").set(
					ARGB4444options, true);
			BitmapFactory.Options.class.getField("inInputShareable").set(
					RGB565options, true);

		} catch (Exception e) {
		}
		try {
			BitmapFactory.Options.class.getField("inScaled").set(
					ARGB8888options, false);
			BitmapFactory.Options.class.getField("inScaled").set(
					ARGB4444options, false);
			BitmapFactory.Options.class.getField("inScaled").set(RGB565options,
					false);
		} catch (Exception e) {
		}
	}

	public static LImage createImage(int width, int height, LColor c) {
		LImage image = new LImage(width, height, false);
		LGraphics g = image.getLGraphics();
		g.setColor(c);
		g.fillRect(0, 0, width, height);
		g.dispose();
		return image;
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
					transparency ? ARGB4444options : RGB565options);
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
			try {
				return loadBitmap(Resources.openResource(resName), transparency);
			} catch (IOException e) {
				throw new RuntimeException(resName + " not found!");
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
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(Resources.openResource(resName), null,
					opts);
			int scaleWidth = (int) Math.floor((double) opts.outWidth / width);
			int scaleHeight = (int) Math
					.floor((double) opts.outHeight / height);

			opts.inJustDecodeBounds = false;
			opts.inSampleSize = Math.min(scaleWidth, scaleHeight);

			return BitmapFactory.decodeStream(Resources.openResource(resName),
					null, opts);
		} catch (Exception e) {
			throw new RuntimeException(resName + " not found!");
		}
	}

	/**
	 * 以默认参数加载指定路径图片
	 * 
	 * @param resName
	 * @return
	 */
	final static public Bitmap loadBitmap(String resName) {
		if (LSystem.IMAGE_SIZE != 0) {
			return loadSizeBitmap(resName, LSystem.IMAGE_SIZE, true);
		} else {
			try {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				return BitmapFactory.decodeStream(Resources
						.openResource(resName), null, opts);
			} catch (Exception e) {
				throw new RuntimeException(resName + " not found!");
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
					transparency ? ARGB4444options : RGB565options);
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			if ((w < 16 || w > 128) && (h < 16 || h > 128)) {
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
		try {
			return loadSizeBitmap(Resources.openResource(resName), sampleSize,
					transparency);
		} catch (IOException e) {
			throw new RuntimeException(resName + " not found!");
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
						transparency ? ARGB4444options : RGB565options);
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
		options.inPreferredConfig = transparency ? Bitmap.Config.ARGB_4444
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
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(Resources
					.openResource(fileName), null, ARGB8888options);
			if (bitmap.getConfig() != Config.ARGB_8888) {
				Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, false);
				bitmap.recycle();
				bitmap = null;
				return newBitmap;
			}
			return bitmap;
		} catch (IOException e) {
			throw new RuntimeException(("File not found. ( " + fileName + " )")
					.intern());
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
					transparency ? ARGB4444options : RGB565options);
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
				imageLength, transparency ? ARGB4444options : RGB565options);
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
		LImage image = (LImage) lazyImages.get(keyName);
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
				try {
					if (in != null) {
						in.close();
						in = null;
					}
				} catch (IOException e) {
					LSystem.gc();
				}
			}
		}
		if (image == null) {
			throw new RuntimeException(("File not found. ( " + resName + " )")
					.intern());
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
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
			}
		}

	}

	final static public LImage loadNotCacheImage(final String resName) {
		return GraphicsUtils.loadNotCacheImage(resName, false);
	}

	/**
	 * 加载网络中图像
	 * 
	 * @param string
	 * @return
	 */
	public static LImage loadWebImage(String string, boolean transparency) {
		LImage img = null;
		try {
			java.net.URL url = new java.net.URL(string);
			java.net.HttpURLConnection http = (java.net.HttpURLConnection) url
					.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			InputStream is = http.getInputStream();
			img = GraphicsUtils.loadImage(is, transparency);
			if (img.getWidth() == 0 || img.getHeight() == 0) {
				img = null;
			}
			is.close();
		} catch (Exception e) {
			throw new RuntimeException(("File not found. ( " + string + " )")
					.intern());
		}
		return img;
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
	 * 水平翻转分组图像顺序
	 * 
	 * @param pixels
	 * @return
	 */
	public static LImage[][] getFlipHorizintalImage2D(LImage[][] pixels) {
		int w = pixels.length;
		int h = pixels[0].length;
		LImage pixel[][] = new LImage[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				pixel[i][j] = pixels[j][i];
			}
		}
		return pixel;
	}

	/**
	 * 水平翻转当前图像
	 * 
	 * @return
	 */
	public static LImage rotateImage(final LImage image) {
		return rotate(image, 180);
	}

	/**
	 * 旋转图像为指定角度
	 * 
	 * @param degree
	 * @return
	 */
	public static LImage rotateImage(final LImage image, final int angdeg,
			final boolean d) {
		int w = image.getWidth();
		int h = image.getHeight();
		LImage img = LImage.createImage(w, h, image.getConfig());
		LGraphics g = img.getLGraphics();
		g.setAntiAlias(true);
		g.rotate(d ? (float) -Math.toRadians(angdeg) : (float) Math
				.toRadians(angdeg), w / 2, h / 2);
		g.drawImage(image, 0, 0);
		g.setAntiAlias(false);
		g.dispose();
		return img;
	}

	/**
	 * 创建一个指定形状和填充色的LImage
	 * 
	 * @param shape
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static LImage createShapeImage(Shape shape, LColor c1, LColor c2) {
		Rectangle rect = shape.getBounds();
		LImage image = LImage.createImage(rect.width, rect.height, false);
		LGraphics g = image.getLGraphics();
		g.setColor(c1);
		g.fill(shape);
		g.setColor(c2);
		g.draw(shape);
		g.dispose();
		return image;
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
	 * 分解整图为图片数组
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static LImage[][] getSplit2Images(String fileName, int row, int col,
			boolean isFiltrate, boolean transparency) {
		String keyName = (fileName + row + col + isFiltrate).intern()
				.toLowerCase().trim();
		if (lazySplitMap.size() > LSystem.DEFAULT_MAX_CACHE_SIZE / 3) {
			lazySplitMap.clear();
			System.gc();
		}
		LImage[][] objs = lazySplitMap.get(keyName);
		if (objs == null) {
			LImage image = GraphicsUtils.loadNotCacheImage(fileName,
					transparency);
			objs = getSplit2Images(image, row, col, isFiltrate);
			lazySplitMap.put(keyName, objs);
		}
		return (LImage[][]) objs;
	}

	/**
	 * 分割指定图像为image[][]
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static LImage[][] getSplit2Images(String fileName, int row, int col,
			boolean transparency) {
		return getSplit2Images(fileName, row, col, false, transparency);
	}

	/**
	 * 分割指定图像为image[]
	 * 
	 * @param image
	 * @param row
	 * @param col
	 * @return
	 */

	public static LImage[][] getSplit2Images(LImage image, int row, int col,
			boolean isFiltrate) {
		int wlength = image.getWidth() / row;
		int hlength = image.getHeight() / col;
		LImage[][] abufferedimage = new LImage[wlength][hlength];
		Rect srcR = new Rect();
		Rect dstR = new Rect();
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				Bitmap bitmap = Bitmap
						.createBitmap(row, col, image.getConfig());
				srcR
						.set((x * row), (y * col), row + (x * row), col
								+ (y * col));
				dstR.set(0, 0, row, col);
				canvas.setBitmap(bitmap);
				canvas.drawBitmap(image.getBitmap(), srcR, dstR, null);

				if (row == col && row <= 48 && col <= 48) {
					abufferedimage[x][y] = filterBitmapTo565(bitmap, row, col);
				} else {
					abufferedimage[x][y] = new LImage(bitmap);
				}

				if (isFiltrate) {
					LImage tmp = abufferedimage[x][y];
					int pixels[] = tmp.getPixels();
					for (int i = 0; i < pixels.length; i++) {
						LColor c = new LColor(pixels[i]);
						if ((c.getBlue() == 247 && c.getGreen() == 0 && c
								.getBlue() == 255)
								|| (c.getBlue() == 255 && c.getGreen() == 0 && c
										.getBlue() == 255)
								|| (c.getBlue() == 0 && c.getGreen() == 0 && c
										.getBlue() == 0)) {
							pixels[i] = 0xffffff;
						}
					}
					tmp.setPixels(pixels, tmp.getWidth(), tmp.getHeight());
				}
			}
		}
		return abufferedimage;
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
	 * 返回一个Matrix
	 * 
	 * @return
	 */
	public static Matrix getMatrix() {
		matrix.reset();
		return matrix;
	}

	/**
	 * 拆分指定图像
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static LImage[] getSplitImages(String fileName, int row, int col,
			boolean transparency) {
		return getSplitImages(GraphicsUtils.loadImage(fileName, transparency),
				row, col);
	}

	/**
	 * 拆分指定图像
	 * 
	 * @param image
	 * @param row
	 * @param col
	 * @return
	 */
	public static LImage[] getSplitImages(LImage image, int row, int col) {
		int frame = 0;
		int wlength = image.getWidth() / row;
		int hlength = image.getHeight() / col;
		int total = wlength * hlength;
		Rect srcR = new Rect();
		Rect dstR = new Rect();
		LImage[] images = new LImage[total];
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				Bitmap bitmap = Bitmap
						.createBitmap(row, col, image.getConfig());
				srcR
						.set((x * row), (y * col), row + (x * row), col
								+ (y * col));
				dstR.set(0, 0, row, col);
				canvas.setBitmap(bitmap);
				canvas.drawBitmap(image.getBitmap(), srcR, dstR, null);
				if (row == col && row <= 48 && col <= 48) {
					images[frame] = filterBitmapTo565(bitmap, row, col);
				} else {
					images[frame] = new LImage(bitmap);
				}
				frame++;
			}
		}
		return images;
	}

	/**
	 * copy指定图像到目标图形中
	 * 
	 * @param target
	 * @param source
	 * @return
	 */
	public static LImage copy(LImage target, LImage source) {
		LGraphics g = target.getLGraphics();
		g.drawImage(source, 0, 0);
		g.dispose();
		return target;
	}

	/**
	 * 旋转指定图像为指定角度
	 * 
	 * @param bmp
	 * @param degrees
	 * @return
	 */
	public static Bitmap rotate(Bitmap bit, float degrees) {
		if (bit == null) {
			return bit;
		}
		if (degrees % 360 != 0) {
			int width = bit.getWidth();
			int height = bit.getHeight();
			int nx = width / 2;
			int ny = height / 2;

			matrix.reset();
			matrix.preTranslate(-nx, -ny);
			matrix.postRotate(degrees);
			matrix.postTranslate(nx, ny);

			Bitmap dst = Bitmap.createBitmap(bit, 0, 0, width, height, matrix,
					false);
			return dst;

		} else {
			return bit;
		}
	}

	/**
	 * 旋转指定图像为指定角度
	 * 
	 * @param img
	 * @param degrees
	 * @return
	 */
	public static LImage rotate(LImage img, float degrees) {
		return new LImage(rotate(img.getBitmap(), degrees));
	}

	/**
	 * 矫正指定位图大小
	 * 
	 * @param baseImage
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap fitBitmap(Bitmap baseImage, int width, int height) {
		RectBox rect = calculateFitBitmap(baseImage, width, height);
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(baseImage, rect.width,
				rect.height, true);
		return resizedBitmap;
	}

	/**
	 * 矫正指定LImage图像大小
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static LImage fitImage(LImage image, int width, int height) {
		Bitmap bitmap = image.getBitmap();
		RectBox rect = calculateFitBitmap(bitmap, width, height);
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, rect.width,
				rect.height, true);
		return new LImage(resizedBitmap);
	}

	/**
	 * 矫正指定图像为选定范围内的合适大小
	 * 
	 * @param baseImage
	 * @param width
	 * @param height
	 * @param receiver
	 *            (返回值)
	 * @return
	 */
	final static public RectBox calculateFitBitmap(Bitmap baseImage, int width,
			int height) {
		if (baseImage == null) {
			throw new RuntimeException("Image is null");
		}
		return fitLimitSize(baseImage.getWidth(), baseImage.getHeight(), width,
				height);
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
	 * 加载本地存储环境中图片资源
	 * 
	 * @param recordStore
	 * @param resourceName
	 * @return
	 */
	static public LImage loadAsPNG(String recordStore, String resourceName) {
		RecordStore imagesRS = null;
		LImage img = null;
		try {
			imagesRS = RecordStore.openRecordStore(recordStore, true);
			RecordEnumeration re = imagesRS.enumerateRecords(null, null, true);
			int numRecs = re.numRecords();

			for (int i = 1; i < numRecs; i++) {
				int recId = re.nextRecordId();
				byte[] rec = imagesRS.getRecord(recId);
				ByteArrayInputStream bin = new ByteArrayInputStream(rec);
				DataInputStream din = new DataInputStream(bin);
				String name = din.readUTF();

				if (name.equals(resourceName) == false) {
					continue;
				}
				int width = din.readInt();
				int height = din.readInt();
				din.readLong();
				int length = din.readInt();

				int[] rawImg = new int[width * height];

				for (i = 0; i < length; i++) {
					rawImg[i] = din.readInt();
				}
				img = LImage.createRGBImage(rawImg, width, height, false);
				din.close();
				bin.close();
			}
		} catch (InvalidRecordIDException ignore) {

		} catch (Exception e) {

		} finally {
			try {

				if (imagesRS != null)
					imagesRS.closeRecordStore();
			} catch (Exception ignore) {

			}
		}
		return img;
	}

	/**
	 * 将图像保存到本地存储环境中
	 * 
	 * @param recordStore
	 * @param resourceName
	 * @param image
	 * @return
	 */
	public static int saveAsPNG(String recordStore, String resourceName,
			LImage image) {
		RecordStore imagesRS = null;

		if (resourceName == null) {
			return -1;
		}

		try {
			int[] buffer = image.getPixels();
			imagesRS = RecordStore.openRecordStore(recordStore, true);

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);

			dout.writeUTF(resourceName);
			dout.writeInt(image.getWidth());
			dout.writeInt(image.getHeight());
			dout.writeLong(System.currentTimeMillis());
			dout.writeInt(buffer.length);

			for (int i = 0; i < buffer.length; i++) {
				dout.writeInt(buffer[i]);
			}
			dout.flush();
			dout.close();
			byte[] data = bout.toByteArray();
			return imagesRS.addRecord(data, 0, data.length);
		} catch (Exception e) {
			throw new RuntimeException("Save the image [" + resourceName
					+ "] to RecordStore [" + recordStore + "] failed!");
		} finally {
			try {
				if (imagesRS != null)
					imagesRS.closeRecordStore();
			} catch (Exception ignore) {
			}
		}

	}

	/**
	 * 将指定图像保存为PNG格式
	 * 
	 * @param bitmap
	 * @param output
	 * @return
	 * @throws FileNotFoundException
	 */
	public static boolean saveAsPNG(Bitmap bitmap, String fileName)
			throws FileNotFoundException {
		return bitmap.compress(Bitmap.CompressFormat.PNG, 1,
				new FileOutputStream(fileName));
	}

	/**
	 * 将指定图像保存为PNG格式
	 * 
	 * @param image
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static boolean saveAsPNG(LImage image, String fileName)
			throws FileNotFoundException {
		return image.getBitmap().compress(Bitmap.CompressFormat.PNG, 1,
				new FileOutputStream(fileName));
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
		return pixels;
	}

	/**
	 * 清空缓存
	 */
	public static void destroy() {
		for (LImage img : lazyImages.values()) {
			if (img != null) {
				img.dispose();
				img = null;
			}
		}
		lazyImages.clear();
		for (LImage[][] img : lazySplitMap.values()) {
			if (img != null) {
				for (int i = 0; i < img.length; i++) {
					for (int j = 0; j > img[i].length; j++) {
						if (img[i][j] != null) {
							img[i][j].dispose();
							img[i][j] = null;
						}
					}
				}
			}
		}
		lazySplitMap.clear();
	}

}
