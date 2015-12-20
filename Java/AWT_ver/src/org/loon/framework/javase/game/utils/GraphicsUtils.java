package org.loon.framework.javase.game.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.LColor;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.component.awt.AWTDataBufferHelper;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.core.resource.Resources;

/**
 * 
 * Copyright 2008 - 2009
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
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
final public class GraphicsUtils {

	// 自定义的RGB配色器
	final static private DirectColorModel COLOR_MODEL_RGB = new DirectColorModel(
			24, 0xFF0000, 0x00FF00, 0x0000FF);

	// 自定义的ARGB配色器
	final static private DirectColorModel COLOR_MODEL_ARGB = new DirectColorModel(
			32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000);

	final static public Toolkit toolKit = Toolkit.getDefaultToolkit();

	final static private HashMap<String, Image> cacheImages = new HashMap<String, Image>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	// 此部分不要求缓存必然有效，故采用WeakHashMap
	final static private Map<String, Image> cacheByteImages = new WeakHashMap<String, Image>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	private static Map<String, Object> lazySplitMap = Collections.synchronizedMap(new HashMap<String, Object>(
			LSystem.DEFAULT_MAX_CACHE_SIZE));

	// 优秀但缓慢的图像加载方式
	final static RenderingHints hints_excellent;

	// 一般的图像加载
	final static RenderingHints hints_general;

	// 低劣但高效的图像加载方式
	final static RenderingHints hints_poor;

	static {
		// 设定图像显示状态为一般
		hints_general = new RenderingHints(null);
		// 设定图像显示状态为优秀
		hints_excellent = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		hints_excellent.put(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
		hints_excellent.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints_excellent.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints_excellent.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		hints_excellent.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hints_excellent.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints_excellent.put(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		hints_excellent.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		// 设定图像显示状态为低劣
		hints_poor = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		hints_poor.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		hints_poor.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		hints_poor.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);
		hints_poor.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		hints_poor.put(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_DISABLE);
		hints_poor.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		hints_poor.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		hints_poor.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		hints_poor.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);
		hints_poor.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		hints_poor.put(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_NORMALIZE);
		hints_poor.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

	}

	private GraphicsUtils() {
	}

	public static Image createImage(int width, int height, Color c) {
		return createLImage(width, height, c).getBufferedImage();
	}

	public static LImage createLImage(int width, int height, Color c) {
		LImage image = new LImage(width, height, false);
		LGraphics g = image.getLGraphics();
		g.setColor(c);
		g.fillRect(0, 0, width, height);
		g.dispose();
		return image;
	}

	/**
	 * 生成对应指定像素的RGB模式BufferedImage
	 * 
	 * @param pixels
	 * @param w
	 * @param h
	 * @param pixelsSize
	 * @return
	 */
	public static BufferedImage newAwtRGBImage(int[] pixels, int w, int h,
			int pixelSize) {
		SampleModel sample = new SinglePixelPackedSampleModel(
				DataBuffer.TYPE_INT, w, h, new int[] { 0xFF0000, 0x00FF00,
						0x0000FF });
		DataBufferInt dataBuffer = new DataBufferInt(pixels, pixelSize);
		WritableRaster raster = Raster.createWritableRaster(sample, dataBuffer,
				new Point(0, 0));
		return new BufferedImage(COLOR_MODEL_RGB, raster, true, null);
	}

	/**
	 * 生成对应指定像素的ARGB模式BufferedImage
	 * 
	 * @param pixels
	 * @param w
	 * @param h
	 * @param pixelSize
	 * @return
	 */
	public static BufferedImage newAwtARGBImage(int[] pixels, int w, int h,
			int pixelSize) {
		DataBuffer dataBuffer = new DataBufferInt(pixels, pixelSize);
		SampleModel sample = new SinglePixelPackedSampleModel(
				DataBuffer.TYPE_INT, w, h, new int[] { 0x00ff0000, 0x0000ff00,
						0x000000ff, 0xff000000 });
		WritableRaster raster = Raster.createWritableRaster(sample, dataBuffer,
				new Point(0, 0));
		return new BufferedImage(COLOR_MODEL_ARGB, raster, true, null);
	}

	public static BufferedImage copy(BufferedImage image, int type) {
		if (image == null) {
			throw new RuntimeException("Image is null !");
		}
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage img = new BufferedImage(width, height, type);
		Graphics g = img.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		if (image != null) {
			image.flush();
			image = null;
		}
		return img;

	}

	/**
	 * 创建指定数量的BufferedImage
	 * 
	 * @param count
	 * @param w
	 * @param h
	 * @param transparency
	 * @return
	 */
	public static BufferedImage[] createImage(int count, int w, int h,
			int transparency) {
		BufferedImage[] image = new BufferedImage[count];
		for (int i = 0; i < image.length; i++) {
			image[i] = GraphicsUtils.createImage(w, h, transparency);
		}
		return image;
	}

	/**
	 * 以指定位置、指定大小填充指定颜色
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void rectFill(Graphics g, int x, int y, int width,
			int height, Color color) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

	/**
	 * 以指定位置、指定大小绘制指定矩形
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void rectDraw(Graphics g, int x, int y, int width,
			int height, Color color) {
		g.setColor(color);
		g.drawRect(x, y, width, height);
	}

	/**
	 * 以指定位置、指定大小绘制指定椭圆形
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void rectOval(Graphics g, int x, int y, int width,
			int height, Color color) {
		g.setColor(color);
		g.drawOval(x, y, width, height);
		g.fillOval(x, y, width, height);
	}

	/**
	 * 返回一个实例化的字体
	 * 
	 * @return
	 */
	public static Font getFont() {
		return getFont(LSystem.FONT, LSystem.FONT_TYPE);
	}

	/**
	 * 返回一个实例化的字体
	 * 
	 * @param size
	 * @return
	 */
	public static Font getFont(int size) {
		return getFont(LSystem.FONT, size);
	}

	/**
	 * 返回一个实例化的字体
	 * 
	 * @param fontName
	 * @param size
	 * @return
	 */
	public static Font getFont(String fontName, int size) {
		return getFont(fontName, 0, size);
	}

	/**
	 * 返回一个实例化的字体
	 * 
	 * @param fontName
	 * @param style
	 * @param size
	 * @return
	 */
	public static Font getFont(String fontName, int style, int size) {
		return new Font(fontName, style, size);
	}

	/**
	 * 以突出样式绘制指定文字信息
	 * 
	 * @param graphics
	 * @param message
	 * @param i
	 * @param j
	 * @param color
	 * @param color1
	 */
	public static void drawStyleString(final Graphics graphics,
			final String message, final int x, final int y, final Color color,
			final Color color1) {
		graphics.setColor(color);
		graphics.drawString(message, x + 1, y);
		graphics.drawString(message, x - 1, y);
		graphics.drawString(message, x, y + 1);
		graphics.drawString(message, x, y - 1);
		graphics.setColor(color1);
		graphics.drawString(message, x, y);

	}

	/**
	 * 绘制六芒星
	 * 
	 * @param g
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public static void drawSixStart(Graphics g, Color color, int x, int y, int r) {
		g.setColor(color);
		drawTriangle(g, color, x, y, r);
		drawRTriangle(g, color, x, y, r);
	}

	/**
	 * 绘制正三角
	 * 
	 * @param g
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public static void drawTriangle(Graphics g, Color color, int x, int y, int r) {
		int x1 = x;
		int y1 = y - r;
		int x2 = x - (int) (r * Math.cos(Math.PI / 6));
		int y2 = y + (int) (r * Math.sin(Math.PI / 6));
		int x3 = x + (int) (r * Math.cos(Math.PI / 6));
		int y3 = y + (int) (r * Math.sin(Math.PI / 6));
		int[] xpos = new int[3];
		xpos[0] = x1;
		xpos[1] = x2;
		xpos[2] = x3;
		int[] ypos = new int[3];
		ypos[0] = y1;
		ypos[1] = y2;
		ypos[2] = y3;
		g.setColor(color);
		g.fillPolygon(xpos, ypos, 3);
	}

	/**
	 * 绘制倒三角
	 * 
	 * @param g
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public static void drawRTriangle(Graphics g, Color color, int x, int y,
			int r) {
		int x1 = x;
		int y1 = y + r;
		int x2 = x - (int) (r * Math.cos(Math.PI / 6.0));
		int y2 = y - (int) (r * Math.sin(Math.PI / 6.0));
		int x3 = x + (int) (r * Math.cos(Math.PI / 6.0));
		int y3 = y - (int) (r * Math.sin(Math.PI / 6.0));
		int[] xpos = new int[3];
		xpos[0] = x1;
		xpos[1] = x2;
		xpos[2] = x3;
		int[] ypos = new int[3];
		ypos[0] = y1;
		ypos[1] = y2;
		ypos[2] = y3;
		g.setColor(color);
		g.fillPolygon(xpos, ypos, 3);
	}

	/**
	 * copy指定图像到目标图形中
	 * 
	 * @param target
	 * @param source
	 * @return
	 */
	public static BufferedImage copy(BufferedImage target, Image source) {
		Graphics2D g = target.createGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return target;
	}

	public static BufferedImage copy(BufferedImage target, BufferedImage source) {
		Graphics2D g = target.createGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return target;
	}

	public static Image copy(Image target, BufferedImage source) {
		Graphics2D g = GraphicsUtils.getBufferImage(target).createGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return target;
	}

	public static Image copy(Image target, Image source) {
		Graphics g = GraphicsUtils.getBufferImage(target).getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return target;
	}

	/**
	 * 获得一个Image对像的ColorModel
	 * 
	 * @param image
	 * @return
	 */
	public static ColorModel getColorModel(Image image) {
		try {
			PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
			pg.grabPixels();
			return pg.getColorModel();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成一个BufferImage
	 * 
	 * @param w
	 * @param h
	 * @param flag
	 * @return
	 */
	final static public BufferedImage createImage(int w, int h, boolean flag) {
		LSystem.gc(50, 1);
		if (flag) {
			if (LSystem.isOverrunJdk15()) {
				return new BufferedImage(w, h,
						BufferedImage.TYPE_4BYTE_ABGR_PRE);
			} else {
				return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
			}
		} else {
			return new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		}
	}

	final static public BufferedImage createIntdexedImage(int w, int h) {
		return new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
	}

	/**
	 * 创建一个指定颜色的图形按钮
	 * 
	 * @param color
	 * @param flag
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage createButtonImage(Color color, boolean flag,
			int w, int h) {
		BufferedImage bufferedimage = new BufferedImage(w, h,
				BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D graphics2d = bufferedimage.createGraphics();
		Color color1 = (color = flag ? color.brighter() : color).brighter();
		GradientPaint gradientpaint = new GradientPaint(0, 0, color, w / 2 + 2,
				h / 2 + 2, color1);
		graphics2d.setPaint(gradientpaint);
		graphics2d.fillRect(2, 2, w - 4, h - 4);
		graphics2d.setColor(Color.BLACK);
		graphics2d.drawLine(1, h - 3, 1, 1);
		graphics2d.drawLine(1, 1, w - 3, 1);
		graphics2d.setColor(Color.BLACK);
		graphics2d.drawLine(0, h - 1, w - 1, h - 1);
		graphics2d.drawLine(w - 1, h - 1, w - 1, 0);
		graphics2d.setColor(Color.BLACK);
		graphics2d.drawRect(0, 0, w - 2, h - 2);
		graphics2d.dispose();
		graphics2d = null;
		return bufferedimage;
	}

	/**
	 * 创建一幅按钮用背景图
	 * 
	 * @param w
	 * @param h
	 * @param color1
	 * @param color2
	 * @return
	 */
	public static BufferedImage createButtonBackground(int w, int h,
			Color color1, Color color2) {
		BufferedImage image = GraphicsUtils.createImage(w, h, false);
		Graphics2D g = image.createGraphics();
		GradientPaint gradientpaint = new GradientPaint(0, 0, color1, w / 2, h,
				color2);
		g.setPaint(gradientpaint);
		g.fillRect(2, 2, w - 4, h - 4);
		g.setColor(Color.BLACK);
		g.drawLine(1, h - 3, 1, 1);
		g.drawLine(1, 1, w - 3, 1);
		g.setColor(Color.BLACK);
		g.drawLine(0, h - 1, w - 1, h - 1);
		g.drawLine(w - 1, h - 1, w - 1, 0);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, w - 2, h - 2);
		g.dispose();
		g = null;
		return image;
	}

	/**
	 * 创建一个指定透明度的BufferedImage
	 * 
	 * @param width
	 * @param height
	 * @param transparency
	 * @return
	 */
	public static BufferedImage createImage(int width, int height, int type) {
		return new BufferedImage(width, height, type);
	}

	/**
	 * 将指定像素集合(int[]格式)转为BufferedImage
	 * 
	 * @param data
	 * @return
	 */
	final static public BufferedImage getImage(int[] data) {
		if (data == null || data.length < 3 || data[0] < 1 || data[1] < 1) {
			return null;
		}
		int width = data[0];
		int height = data[1];
		if (data.length < 2 + width * height) {
			return null;
		}
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_BGR);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				image.setRGB(j, i, data[2 + j + i * width]);
			}
		}
		return image;
	}

	/**
	 * 变更指定BufferedImage大小
	 * 
	 * @param image
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage getResize(BufferedImage image, int w, int h) {
		int width = image.getWidth(), height = image.getHeight();
		if (width == w && height == h) {
			return image;
		}
		BufferedImage img;
		AffineTransform tx = new AffineTransform();
		tx.scale((double) w / width, (double) h / height);
		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		img = op.filter(image, null);
		return img;
	}

	/**
	 * 获得一组序号连续的图片
	 * 
	 * @param fileName
	 * @param range(指定图片范围，如("1-2"))
	 * @return
	 */
	public static Image[] loadSequenceImages(String fileName, String range) {
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
					ex.printStackTrace();
				}
			}
			Image[] images = new Image[images_count];
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
				images[i] = GraphicsUtils.loadImage(imageName);
			}
			return images;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 匹配图片到设定大小的图片之上
	 * 
	 * @param image
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage matchBufferedImage(BufferedImage image, int w,
			int h) {
		BufferedImage result = null;
		Graphics2D graphics2d;
		(graphics2d = (result = GraphicsUtils.createImage(w, h, true))
				.createGraphics()).setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.drawImage(image, 0, 0, null);
		graphics2d.dispose();
		graphics2d = null;
		return result;
	}

	/**
	 * 变更指定Image大小
	 * 
	 * @param image
	 * @param w
	 * @param h
	 * @return
	 */
	public static Image getResize(Image image, int w, int h) {
		if (image == null) {
			return null;
		}
		if (image.getWidth(null) == w && image.getHeight(null) == h) {
			return image;
		}
		BufferedImage result = null;
		Graphics2D graphics2d;
		(graphics2d = (result = GraphicsUtils.createImage(w, h, true))
				.createGraphics()).setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.drawImage(image, 0, 0, w, h, 0, 0, image.getWidth(null),
				image.getHeight(null), null);
		graphics2d.dispose();
		graphics2d = null;
		return result;
	}

	// 文字清晰过滤开
	final static private RenderingHints VALUE_TEXT_ANTIALIAS_ON = new RenderingHints(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	// 文字清晰过滤关
	final static private RenderingHints VALUE_TEXT_ANTIALIAS_OFF = new RenderingHints(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

	// 清晰过滤开
	final static private RenderingHints VALUE_ANTIALIAS_ON = new RenderingHints(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	// 清晰过滤关
	final static private RenderingHints VALUE_ANTIALIAS_OFF = new RenderingHints(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

	/**
	 * 设定图像解析度
	 * 
	 * @param g
	 * @param smooth
	 * @param antialiasing
	 */
	public static void setRenderingHints(Graphics g, boolean smooth,
			boolean antialiasing) {
		if (smooth) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		} else {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		if (antialiasing) {
			((Graphics2D) g).setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} else {
			((Graphics2D) g).setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	/**
	 * 设定文字是否抗锯齿
	 * 
	 * @param g
	 * @param flag
	 */
	public static void setAntialias(Graphics g, boolean flag) {
		if (flag) {
			((Graphics2D) g).setRenderingHints(VALUE_TEXT_ANTIALIAS_ON);
		} else {
			((Graphics2D) g).setRenderingHints(VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	/**
	 * 设定全局抗锯齿
	 * 
	 * @param g
	 * @param flag
	 */
	public static void setAntialiasAll(Graphics g, boolean flag) {
		if (flag) {
			((Graphics2D) g).setRenderingHints(VALUE_ANTIALIAS_ON);
		} else {
			((Graphics2D) g).setRenderingHints(VALUE_ANTIALIAS_OFF);
		}
	}

	/**
	 * 分解指定图像为BufferedImage[]
	 * 
	 * @param image
	 * @param col
	 * @param row
	 * @return
	 */
	public static LImage[] getSplitLImages(LImage image, int row, int col) {
		int width = image.getWidth(), height = image.getHeight();
		if (row == width && col == height) {
			return new LImage[] { image };
		}
		int frame = 0;
		int wlength = image.getWidth() / row;
		int hlength = image.getHeight() / col;
		int total = wlength * hlength;
		boolean transparency = image.hasAlpha();
		LImage[] images = LImage.createImage(total, row, col, transparency);
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				LGraphics g = images[frame].getLGraphics();
				g.drawImage(image, 0, 0, row, col, (x * row), (y * col), row
						+ (x * row), col + (y * col));
				g.dispose();
				g = null;
				frame++;
			}
		}
		return images;
	}

	/**
	 * 分解整图为图片数组
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static Image[] getSplitImages(String fileName, int row, int col) {
		return getSplitImages(fileName, row, col, true);
	}

	/**
	 * 分解整图为图片数组
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static Image[] getSplitImages(String fileName, int row, int col,
			boolean isFiltrate) {
		Image image = GraphicsUtils.loadImage(fileName);
		return getSplitImages(image, row, col, isFiltrate);
	}

	/**
	 * 分割指定图像为image[]
	 * 
	 * @param image
	 * @param row
	 * @param col
	 * @return
	 */
	public static Image[] getSplitImages(Image image, int row, int col,
			boolean isFiltrate) {
		int index = 0;
		int wlength = image.getWidth(null) / row;
		int hlength = image.getHeight(null) / col;
		int l = wlength * hlength;
		Image[] abufferedimage = new Image[l];
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				abufferedimage[index] = GraphicsUtils.createImage(row, col,
						true);
				Graphics g = abufferedimage[index].getGraphics();
				g.drawImage(image, 0, 0, row, col, (x * row), (y * col), row
						+ (x * row), col + (y * col), null);
				g.dispose();
				g = null;
				PixelGrabber pgr = new PixelGrabber(abufferedimage[index], 0,
						0, -1, -1, true);
				try {
					pgr.grabPixels();
				} catch (InterruptedException ex) {
				}
				int pixels[] = (int[]) pgr.getPixels();
				if (isFiltrate) {
					for (int i = 0; i < pixels.length; i++) {
						int[] rgbs = LColor.getRGBs(pixels[i]);
						if ((rgbs[0] == 247 && rgbs[1] == 0 && rgbs[2] == 255)
								|| (rgbs[0] == 255 && rgbs[1] == 255 && rgbs[2] == 255)) {
							pixels[i] = 0;
						}
					}
				}
				ImageProducer ip = new MemoryImageSource(pgr.getWidth(), pgr
						.getHeight(), pixels, 0, pgr.getWidth());
				abufferedimage[index] = toolKit.createImage(ip);
				index++;
			}
		}
		return abufferedimage;
	}

	/**
	 * 分解整图为图片数组
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static LImage[] getSplitLImages(String fileName, int row, int col) {
		return getSplitLImages(fileName, row, col, true);
	}

	/**
	 * 分解整图为图片数组
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static LImage[] getSplitLImages(String fileName, int row, int col,
			boolean isFiltrate) {
		LImage image = LImage.createImage(fileName);
		return getSplitLImages(image, row, col, isFiltrate);
	}

	/**
	 * 分割指定图像为image[]
	 * 
	 * @param image
	 * @param row
	 * @param col
	 * @return
	 */
	public static LImage[] getSplitLImages(LImage image, int row, int col,
			boolean isFiltrate) {
		int index = 0;
		int wlength = image.getWidth() / row;
		int hlength = image.getHeight() / col;
		int l = wlength * hlength;
		LImage[] abufferedimage = new LImage[l];
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				abufferedimage[index] = LImage.createImage(row, col, true);
				LGraphics g = abufferedimage[index].getLGraphics();
				g.drawImage(image, 0, 0, row, col, (x * row), (y * col), row
						+ (x * row), col + (y * col));
				g.dispose();
				g = null;
				PixelGrabber pgr = new PixelGrabber(abufferedimage[index]
						.getBufferedImage(), 0, 0, -1, -1, true);
				try {
					pgr.grabPixels();
				} catch (InterruptedException ex) {
				}
				int pixels[] = (int[]) pgr.getPixels();
				if (isFiltrate) {
					for (int i = 0; i < pixels.length; i++) {
						int[] rgbs = LColor.getRGBs(pixels[i]);
						if ((rgbs[0] == 247 && rgbs[1] == 0 && rgbs[2] == 255)
								|| (rgbs[0] == 255 && rgbs[1] == 255 && rgbs[2] == 255)) {
							pixels[i] = 0;
						}
					}
				}
				ImageProducer ip = new MemoryImageSource(pgr.getWidth(), pgr
						.getHeight(), pixels, 0, pgr.getWidth());
				abufferedimage[index] = new LImage(toolKit.createImage(ip));
				index++;
			}
		}
		return abufferedimage;
	}

	/**
	 * 分解整图为图片数组
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static Image[][] getSplit2Images(String fileName, int row, int col,
			boolean isFiltrate) {
		String keyName = (fileName + row + col + isFiltrate).intern()
				.toLowerCase().trim();
		if (lazySplitMap.size() > LSystem.DEFAULT_MAX_CACHE_SIZE / 3) {
			lazySplitMap.clear();
			System.gc();
		}
		Object objs = lazySplitMap.get(keyName);
		if (objs == null) {
			Image image = GraphicsUtils.loadNotCacheImage(fileName);
			objs = getSplit2Images(image, row, col, isFiltrate);
			lazySplitMap.put(keyName, objs);
		}
		return (Image[][]) objs;
	}

	/**
	 * 分割指定图像为image[][]
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @return
	 */
	public static Image[][] getSplit2Images(String fileName, int row, int col) {
		return getSplit2Images(fileName, row, col, false);
	}

	/**
	 * 分割指定图像为image[]
	 * 
	 * @param image
	 * @param row
	 * @param col
	 * @return
	 */
	public static Image[][] getSplit2Images(Image image, int row, int col,
			boolean isFiltrate) {
		int wlength = image.getWidth(null) / row;
		int hlength = image.getHeight(null) / col;
		Image[][] abufferedimage = new Image[wlength][hlength];
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				abufferedimage[x][y] = GraphicsUtils
						.createImage(row, col, true);
				Graphics g = abufferedimage[x][y].getGraphics();
				g.drawImage(image, 0, 0, row, col, (x * row), (y * col), row
						+ (x * row), col + (y * col), null);
				g.dispose();
				g = null;
				PixelGrabber pgr = new PixelGrabber(abufferedimage[x][y], 0, 0,
						-1, -1, true);
				try {
					pgr.grabPixels();
				} catch (InterruptedException ex) {
					ex.getStackTrace();
				}
				int pixels[] = (int[]) pgr.getPixels();
				if (isFiltrate) {
					for (int i = 0; i < pixels.length; i++) {
						int[] rgbs = LColor.getRGBs(pixels[i]);
						if ((rgbs[0] == 247 && rgbs[1] == 0 && rgbs[2] == 255)
								|| (rgbs[0] == 255 && rgbs[1] == 0 && rgbs[2] == 255)
								|| (rgbs[0] == 0 && rgbs[1] == 0 && rgbs[2] == 0)) {
							pixels[i] = 0;
						}
					}
				}
				ImageProducer ip = new MemoryImageSource(pgr.getWidth(), pgr
						.getHeight(), pixels, 0, pgr.getWidth());
				abufferedimage[x][y] = toolKit.createImage(ip);
			}
		}
		return abufferedimage;
	}

	/**
	 * 水平翻转分组图像顺序
	 * 
	 * @param pixels
	 * @return
	 */
	public static Image[][] getFlipHorizintalImage2D(Image[][] pixels) {
		int w = pixels.length;
		int h = pixels[0].length;
		Image pixel[][] = new Image[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				pixel[i][j] = pixels[j][i];
			}
		}
		return pixel;
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
	public static BufferedImage drawClipImage(final Image image,
			int objectWidth, int objectHeight, int x1, int y1, int x2, int y2) {
		BufferedImage buffer = GraphicsUtils.createImage(objectWidth,
				objectHeight, true);
		Graphics g = buffer.getGraphics();
		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.drawImage(image, 0, 0, objectWidth, objectHeight, x1, y1,
				x2, y2, null);
		graphics2D.dispose();
		graphics2D = null;
		return buffer;
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
	public static BufferedImage drawClipImage(final Image image,
			int objectWidth, int objectHeight, int x, int y) {
		BufferedImage buffer = GraphicsUtils.createImage(objectWidth,
				objectHeight, true);
		Graphics2D graphics2D = buffer.createGraphics();
		graphics2D.drawImage(image, 0, 0, objectWidth, objectHeight, x, y, x
				+ objectWidth, objectHeight + y, null);
		graphics2D.dispose();
		graphics2D = null;
		return buffer;
	}

	/**
	 * 剪切指定图像
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param objectWidth
	 * @param objectHeight
	 * @return
	 */
	public static BufferedImage drawCropImage(final Image image, int x, int y,
			int objectWidth, int objectHeight) {

		BufferedImage buffer = GraphicsUtils.createImage(objectWidth,
				objectHeight, true);
		Graphics2D graphics2D = buffer.createGraphics();
		graphics2D.drawImage(image, 0, 0, objectWidth, objectHeight, x, y, x
				+ objectWidth, objectHeight + y, null);
		graphics2D.dispose();
		graphics2D = null;
		return buffer;
	}

	/**
	 * 水平翻转当前图像
	 * 
	 * @return
	 */
	public static BufferedImage rotateImage(final BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, image.getColorModel()
				.getTransparency())).createGraphics()).drawImage(image, 0, 0,
				w, h, w, 0, 0, h, null);
		graphics2d.dispose();
		return img;
	}

	/**
	 * 水平翻转当前图像
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage rotateImage(final Image image) {
		return GraphicsUtils.rotateImage(GraphicsUtils.getBufferImage(image));
	}

	private static Point rotate(Point p, double angle) {
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		return new Point((int) (p.x * c - p.y * s), (int) (p.x * s + p.y * c));
	}

	public static BufferedImage rotateImage(final BufferedImage image,
			final int degrees) {
		AffineTransform at = new AffineTransform();
		at.rotate(degrees < 0 ? -Math.toRadians(degrees) : Math
				.toRadians(degrees), image.getWidth() / 2,
				image.getHeight() / 2);
		BufferedImageOp bio = new AffineTransformOp(at,
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage dest = bio.filter(image, null);
		return dest;
	}

	/**
	 * 旋转图像为指定角度
	 * 
	 * @param degree
	 * @return
	 */
	public static BufferedImage rotateImageRect(final BufferedImage image,
			final int degrees) {
		double phi = Math.toRadians(degrees);
		int width = image.getWidth(null);
		int height = image.getHeight(null);

		Point a = new Point(0, 0);
		Point b = new Point(width, 0);
		Point c = new Point(0, height);
		Point d = new Point(width, height);

		Point newA = rotate(a, phi);
		Point newB = rotate(b, phi);
		Point newC = rotate(c, phi);
		Point newD = rotate(d, phi);

		int w = Math.max(Math.max(newA.x, newB.x), Math.max(newC.x, newD.x))
				- Math.min(Math.min(newA.x, newB.x), Math.min(newC.x, newD.x));
		int h = Math.max(Math.max(newA.y, newB.y), Math.max(newC.y, newD.y))
				- Math.min(Math.min(newA.y, newB.y), Math.min(newC.y, newD.y));

		Rectangle rect = new Rectangle(0, 0, w, h);
		BufferedImage img = new BufferedImage(rect.width, rect.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setTransform(AffineTransform.getRotateInstance(phi, rect.width / 2,
				rect.height / 2));
		g.drawImage(image, (rect.width - width) / 2,
				(rect.height - height) / 2, null);
		g.dispose();

		return img;

	}

	/**
	 * 设定画布呈相方式为低劣
	 * 
	 * @param g
	 */
	public static void setPoorRenderingHints(final Graphics2D g) {
		g.setRenderingHints(hints_poor);
	}

	/**
	 * 设定画布呈相方式为优秀
	 * 
	 * @param g
	 */
	public static void setExcellentRenderingHints(final Graphics2D g) {
		g.setRenderingHints(hints_excellent);
	}

	/**
	 * 设定画布呈相方式为一般
	 * 
	 * @param g
	 */
	public static void setGeneralRenderingHints(final Graphics2D g) {
		g.setRenderingHints(hints_general);
	}

	/**
	 * 绘制指定大小的3D矩形边框
	 * 
	 * @param g
	 * @param rect
	 * @param back
	 * @param down
	 */
	public static void draw3DRect(Graphics g, Rectangle rect, Color back,
			boolean down) {
		int x1 = rect.x;
		int y1 = rect.y;
		int x2 = rect.x + rect.width - 1;
		int y2 = rect.y + rect.height - 1;
		if (!down) {
			g.setColor(back);
			g.drawLine(x1, y1, x1, y2);
			g.drawLine(x1, y1, x2, y2);
			g.setColor(back.brighter());
			g.drawLine(x1 + 1, y1 + 1, x1 + 1, y2 - 1);
			g.drawLine(x1 + 1, y1 + 1, x2 - 1, y1 + 1);
			g.setColor(Color.black);
			g.drawLine(x1, y2, x2, y2);
			g.drawLine(x2, y1, x2, y2);
			g.setColor(back.darker());
			g.drawLine(x1 + 1, y2 - 1, x2 - 1, y2 - 1);
			g.drawLine(x2 - 1, y1 + 2, x2 - 1, y2 - 1);
		} else {
			g.setColor(Color.black);
			g.drawLine(x1, y1, x1, y2);
			g.drawLine(x1, y1, x2, y1);
			g.setColor(back.darker());
			g.drawLine(x1 + 1, y1 + 1, x1 + 1, y2 - 1);
			g.drawLine(x1 + 1, y1 + 1, x2 - 1, y1 + 1);
			g.setColor(back.brighter());
			g.drawLine(x1, y2, x2, y2);
			g.drawLine(x2, y1, x2, y2);
			g.setColor(back);
			g.drawLine(x1 + 1, y2 - 1, x2 - 1, y2 - 1);
			g.drawLine(x2 - 1, y1 + 2, x2 - 1, y2 - 1);
		}
	}

	/**
	 * 绘制指定文字
	 * 
	 * @param s
	 * @param graphics2D
	 * @param i
	 * @param j
	 * @param k
	 */
	public static void drawString(String message, Graphics2D graphics2D, int x,
			int y, int z) {
		Font font = graphics2D.getFont();
		int size = graphics2D.getFontMetrics(font).stringWidth(message);
		GraphicsUtils.setAlpha(graphics2D, 0.9f);
		graphics2D.drawString(message, x + (z - size) / 2, y);
		GraphicsUtils.setAlpha(graphics2D, 1.0f);
	}

	/**
	 * 绘制指定文字
	 * 
	 * @param message
	 * @param graphics
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void drawString(String message, Graphics graphics, int x,
			int y, int z) {
		GraphicsUtils.drawString(message, (Graphics2D) graphics, x, y, z);
	}

	/**
	 * 在graphics上描绘文字
	 * 
	 * @param message
	 * @param fontName
	 * @param g
	 * @param x1
	 * @param y1
	 * @param style
	 * @param size
	 */
	public static void drawString(String message, String fontName,
			final Graphics g, int x1, int y1, int style, int size) {
		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.setFont(new Font(fontName, style, size));
		GraphicsUtils.setAlpha(g, 0.9f);
		graphics2D.drawString(message, x1, y1);
		GraphicsUtils.setAlpha(g, 1.0f);
	}

	/**
	 * 创建一个指定形状和填充色的BufferedImage
	 * 
	 * @param shape
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static BufferedImage createShapeImage(Shape shape, Color c1, Color c2) {
		Rectangle rect = shape.getBounds();
		BufferedImage image = GraphicsUtils.createImage(rect.width,
				rect.height, true);
		Graphics2D g = image.createGraphics();
		g.setColor(c1);
		g.fill(shape);
		g.setColor(c2);
		g.draw(shape);
		return image;
	}

	/**
	 * 将Image转为BufferImage
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getBufferImage(Image image) {
		if (image == null) {
			return null;
		}
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, true);
		BufferedImage bufferimage = GraphicsUtils.createImage(w, h, pg
				.getColorModel().hasAlpha());
		Graphics g = bufferimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		if (cacheImages.containsValue(image)) {
			Object key = null;
			Set<?> entry = cacheImages.entrySet();
			for (Iterator<?> it = entry.iterator(); it.hasNext();) {
				Entry<?, ?> e = (Entry<?, ?>) it.next();
				if (e.getValue() == image) {
					key = e.getKey();
				}
			}
			if (key != null) {
				Image img = (Image) cacheImages.remove(key);
				if (img != null) {
					img.flush();
					img = null;
				}
				cacheImages.put((String) key, bufferimage);
			}
		}
		if (image != null) {
			image.flush();
			image = null;
		}
		pg = null;
		return bufferimage;
	}

	/**
	 * 加载外部文件Image
	 * 
	 * @param fileName
	 * @return
	 */
	final static public Image loadFileImage(final String fileName) {
		return GraphicsUtils.loadImage(fileName, false);
	}

	/**
	 * 加载内部file转为Image
	 * 
	 * @param innerFileName
	 * @return
	 */
	final static public Image loadImage(final String innerFileName) {
		return GraphicsUtils.loadImage(innerFileName, true);
	}

	/**
	 * 将指定的Image文件地址集合载入图像数组
	 * 
	 * @param src
	 * @return
	 * @throws IOException
	 */
	public static Image[] loadImage(String[] srcFile) throws IOException {
		int len = srcFile.length;
		Image imgs[] = new Image[len];
		for (int i = 0; i < len; ++i) {
			imgs[i] = GraphicsUtils.loadImage(srcFile[i]);
		}
		return imgs;
	}

	/**
	 * 加载内部file转为BufferedImage
	 * 
	 * @param innerFileName
	 * @return
	 */
	final static public BufferedImage loadBufferedImage(final String resName) {
		try {
			InputStream in = Resources.openResource(resName);
			if (in == null) {
				throw new RuntimeException(
						("File not found. ( " + resName + " )").intern());
			}
			return ImageIO.read(in);
		} catch (IOException e) {
			throw new RuntimeException(("File not found. ( " + resName + " )")
					.intern());
		}
	}

	/**
	 * 加载byte[]为Image
	 * 
	 * @param bytes
	 * @return
	 */
	final static public Image loadImage(final byte[] bytes) {
		Image result = null;
		try {
			result = toolKit.createImage(bytes);
			waitImage(result);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	final static public Image loadImage(final String name, final byte[] bytes) {
		if (cacheByteImages.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			cacheByteImages.clear();
			System.gc();
		}
		Image result = null;
		result = (Image) cacheByteImages.get(name);
		if (result == null) {
			try {
				result = toolKit.createImage(bytes);
				cacheByteImages.put(name, result);
				waitImage(result);
			} catch (Exception e) {
				result = null;
			}
		}
		return result;
	}

	/**
	 * 加载内部file转为Image
	 * 
	 * @param inputstream
	 * @return
	 */
	final static public Image loadImage(final String resName,
			final boolean isInner) {
		if (resName == null) {
			return null;
		}
		String tmp_file = resName, innerName = StringUtils.replaceIgnoreCase(
				resName, "\\", "/");
		String keyName = innerName.toLowerCase();
		Image imageReference = cacheImages.get(keyName);
		if (imageReference == null) {
			int read;
			boolean flag;
			byte[] bytes = null;
			File file_tmp = null;
			Image img_tmp = null;
			InputStream in = null;
			ByteArrayOutputStream os = null;
			try {
				os = new ByteArrayOutputStream(8192);
				if (isInner) {
					in = new DataInputStream(new BufferedInputStream(Resources
							.openResource(innerName)));
					flag = true;
				} else {
					file_tmp = new File(tmp_file);
					flag = file_tmp.exists();
					if (flag) {
						in = new DataInputStream(new BufferedInputStream(
								new FileInputStream(file_tmp)));
					}
				}
				if (flag) {
					bytes = new byte[8192];
					while ((read = in.read(bytes)) >= 0) {
						os.write(bytes, 0, read);
					}
					bytes = os.toByteArray();
					img_tmp = toolKit.createImage(bytes);
				}
				cacheImages.put(keyName, imageReference = img_tmp);
				waitImage(img_tmp);
			} catch (Exception e) {
				if (!isInner) {
					imageReference = null;
				} else {
					return loadImage(resName, false);
				}
			} finally {
				try {
					if (os != null) {
						os.flush();
						os = null;
					}
					if (in != null) {
						in.close();
						in = null;
					}
					img_tmp = null;
					bytes = null;
					tmp_file = null;
					file_tmp = null;
				} catch (IOException e) {
				}
			}
		}
		if (imageReference == null) {
			throw new RuntimeException(
					("File not found. ( " + innerName + " )").intern());
		}
		return (Image) imageReference;
	}

	final static public Image loadNotCacheImage(final String innerFileName) {
		if (innerFileName == null) {
			return null;
		}

		int read;

		byte[] bytes = null;

		Image img_tmp = null;
		InputStream in = null;
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream(8192);

			in = new DataInputStream(new BufferedInputStream(Resources
					.openResource(innerFileName)));

			bytes = new byte[8192];
			while ((read = in.read(bytes)) >= 0) {
				os.write(bytes, 0, read);
			}
			bytes = os.toByteArray();
			img_tmp = toolKit.createImage(bytes);

			waitImage(img_tmp);
		} catch (Exception e) {
			throw new RuntimeException(
					("File not found. ( " + innerFileName + " )").intern());
		} finally {
			try {
				if (os != null) {
					os.flush();
					os = null;
				}
				if (in != null) {
					in.close();
					in = null;
				}
				bytes = null;

			} catch (IOException e) {
			}
		}

		return img_tmp;
	}

	private static Panel context = new Panel();

	/**
	 * 同步图像文件
	 * 
	 * @param image
	 */
	public static void waitImage(Image image) {
		if (image == null) {
			return;
		}
		if (image instanceof BufferedImage) {
			return;
		}
		MediaTracker mediaTracker = null;
		try {
			if (context != null) {
				mediaTracker = new MediaTracker(context);
				mediaTracker.addImage(image, 0);
				if ((mediaTracker.statusID(0, true) & MediaTracker.ERRORED) != 0) {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			if (mediaTracker != null) {
				mediaTracker.removeImage(image, 0);
				mediaTracker = null;
			}
		}
		waitImage(100, image);
	}

	/**
	 * 延迟加载image,以使其同步。
	 * 
	 * @param delay
	 * @param image
	 */
	private static void waitImage(int delay, Image image) {
		try {
			for (int i = 0; i < delay; i++) {
				if (toolKit.prepareImage(image, -1, -1, null)) {
					return;
				}
				Thread.sleep(delay);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 加载双图对比过滤图像
	 * 
	 * @param innerName
	 * @return
	 */
	final static public BufferedImage loadDoubleFilterImage(
			final String innerName) {
		Image result = GraphicsUtils.loadImage(innerName);
		return GraphicsUtils.loadDoubleFilterImage(result, Math.round(result
				.getWidth(null) / 2), Math.round(result.getHeight(null)));
	}

	/**
	 * 加载双图对比过滤图像
	 * 
	 * @param innerName
	 * @param width
	 * @param height
	 * @return
	 */
	final static public BufferedImage loadFilterGameImage(
			final String innerName, final int width, final int height) {
		return GraphicsUtils.loadDoubleFilterImage(GraphicsUtils
				.loadImage(innerName), width, height);
	}

	/**
	 * 加载双图对比过滤图像
	 * 
	 * @param img
	 * @param width
	 * @param height
	 * @return
	 */
	final static public BufferedImage loadDoubleFilterImage(final Image img,
			final int width, final int height) {
		BufferedImage img1 = GraphicsUtils.drawClipImage(img, width, height, 0,
				0);
		BufferedImage img2 = GraphicsUtils.drawClipImage(img, width, height,
				width, 0);
		WritableRaster writableRaster1 = img1.getRaster();
		DataBuffer dataBuffer1 = writableRaster1.getDataBuffer();
		int[] basePixels1 = AWTDataBufferHelper.getDataInt(dataBuffer1);
		WritableRaster writableRaster2 = img2.getRaster();
		DataBuffer dataBuffer2 = writableRaster2.getDataBuffer();
		int[] basePixels2 = AWTDataBufferHelper.getDataInt(dataBuffer2);
		int length = basePixels2.length;
		for (int i = 0; i < length; i++) {
			if (basePixels2[i] >= LColor.getRGB(200, 200, 200)) {
				basePixels2[i] = 0xffffff;
			} else {
				basePixels2[i] = basePixels1[i];
			}
		}
		img1.flush();
		img1 = null;
		return img2;
	}

	final static public Image getImageCache(final String name) {
		return (Image) cacheImages.get(name);
	}

	final static public Image getImageByteCache(final String name) {
		return (Image) cacheByteImages.get(name);
	}

	/**
	 * 延迟指定毫秒
	 * 
	 * @param ms
	 */
	final static public void wait(final int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {
		}
	}

	/**
	 * 透明度设定
	 * 
	 * @param g
	 * @param d
	 */
	final static public void setAlpha(Graphics g, double d) {
		AlphaComposite alphacomposite = AlphaComposite
				.getInstance(3, (float) d);
		((Graphics2D) g).setComposite(alphacomposite);
	}

	/**
	 * 透明度设定
	 * 
	 * @param g2d
	 * @param d
	 */
	final static public void setAlpha(Graphics2D g2d, double d) {
		AlphaComposite alphacomposite = AlphaComposite
				.getInstance(3, (float) d);
		g2d.setComposite(alphacomposite);
	}

	/**
	 * 返回当前画布透明度
	 * 
	 * @param g2d
	 * @return
	 */
	final static public float getAlpha(Graphics2D g2d) {
		return ((AlphaComposite) g2d.getComposite()).getAlpha();
	}

	/**
	 * 分解小图
	 * 
	 * @param image
	 * @param objectWidth
	 * @param objectHeight
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 * @throws Exception
	 */
	final static private Image getClipImage(final Image image, int objectWidth,
			int objectHeight, int x1, int y1, int x2, int y2) throws Exception {
		BufferedImage buffer = createImage(objectWidth, objectHeight, true);
		Graphics g = buffer.getGraphics();
		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.drawImage(image, 0, 0, objectWidth, objectHeight, x1, y1,
				x2, y2, null);
		graphics2D.dispose();
		graphics2D = null;
		return buffer;
	}

	/**
	 * 按横行宽度分解图像
	 * 
	 * @param img
	 * @param width
	 * @return
	 */
	final static public Image[] getImageRows(Image img, int width) {
		int iWidth = img.getWidth(null);
		int iHeight = img.getHeight(null);
		int size = iWidth / width;
		Image[] imgs = new Image[size];
		for (int i = 1; i <= size; i++) {
			try {
				imgs[i - 1] = transparencyBlackColor(getClipImage(img, width,
						iHeight, width * (i - 1), 0, width * i, iHeight));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return imgs;
	}

	final static public Image loadImageTransparency(final String fileName,
			final Color c) {
		Image img = GraphicsUtils.loadImage(fileName);
		img = transparencyBlackColor(img, c);
		return img;
	}

	/**
	 * 将黑色颜色部分透明化
	 * 
	 * @param img
	 * @return
	 */
	final static public Image transparencyBlackColor(final Image img) {
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, true);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int pixels[] = (int[]) pg.getPixels();
		int length = pixels.length;
		for (int i = 0; i < length; i++) {
			if (pixels[i] <= -11500000) {
				pixels[i] = 0xffffff;
			}
		}
		return toolKit.createImage(new MemoryImageSource(width, height, pixels,
				0, width));
	}

	final static public Image transparencyBlackColor(final Image img,
			final Color c) {
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, true);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int pixels[] = (int[]) pg.getPixels();
		int length = pixels.length;
		for (int i = 0; i < length; i++) {
			int pixel = pixels[i];
			int[] rgbs = LColor.getRGBs(pixel);
			if (rgbs[0] >= 252 && rgbs[1] >= 252 && rgbs[1] >= 252) {
				pixels[i] = 0xffffff;
			}
		}
		return toolKit.createImage(new MemoryImageSource(width, height, pixels,
				0, width));
	}

	/**
	 * 将指定颜色透明化
	 * 
	 * @param img
	 * @param color
	 */
	final static public void transparencyColor(BufferedImage img, int color) {
		WritableRaster writableRaster = img.getRaster();
		DataBuffer dataBuffer = writableRaster.getDataBuffer();
		int[] basePixels = AWTDataBufferHelper.getDataInt(dataBuffer);
		int length = basePixels.length;
		for (int i = 0; i < length; i++) {
			if (basePixels[i] == color) {
				basePixels[i] = 0xffffff;
			}
		}
	}

	/**
	 * 返回当前剪切板中图像
	 * 
	 * @return
	 */
	public Image getClipboardImage() {
		Transferable transferable = Toolkit.getDefaultToolkit()
				.getSystemClipboard().getContents(null);
		if (transferable != null
				&& transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			// 转换数据为Image并返回
			try {
				return (Image) transferable
						.getTransferData(DataFlavor.imageFlavor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 生成BufferedImage的hash序列
	 * 
	 * @param BufferedImage
	 * @return
	 */
	public static int hashImage(BufferedImage img) {
		int hash_result = 0;
		hash_result = (hash_result << 7) ^ img.getHeight(null);
		hash_result = (hash_result << 7) ^ img.getWidth(null);
		for (int pixel = 0; pixel < 20; ++pixel) {
			int x = (pixel * 50) % img.getWidth(null);
			int y = (pixel * 100) % img.getHeight(null);
			hash_result = (hash_result << 7) ^ img.getRGB(x, y);
		}
		return hash_result;
	}

	/**
	 * 生成Image的hash序列
	 * 
	 * @param img
	 * @return
	 */
	public static int hashImage(Image img) {
		int hash_result = 0;
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		hash_result = (hash_result << 7) ^ height;
		hash_result = (hash_result << 7) ^ width;
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, true);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int pixels[] = (int[]) pg.getPixels();
		for (int pixel = 0; pixel < 20; ++pixel) {
			int x = (pixel * 50) % width;
			int y = (pixel * 100) % height;
			hash_result = (hash_result << 7) ^ pixels[x + width * y];
		}
		return hash_result;
	}

	public static int[] getPixels(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		int pixels[] = new int[width * height];
		img.getRGB(0, 0, width, height, pixels, 0, width);
		return pixels;
	}

	/**
	 * 保存图像为指定路径指定格式
	 * 
	 * @param image
	 * @param fileName
	 * @param format
	 */
	public static void saveImage(BufferedImage image, File file, String format) {
		try {
			FileUtils.makedirs(file);
			javax.imageio.ImageIO.write(image, format, file);
		} catch (IOException e) {
		}
	}

	/**
	 * 保存图像为指定路径指定格式
	 * 
	 * @param image
	 * @param fileName
	 * @param format
	 */
	public static void saveImage(BufferedImage image, String fileName,
			String format) {
		saveImage(image, new File(fileName), format);
	}

	/**
	 * 保存图像为指定路径
	 * 
	 * @param image
	 * @param fileName
	 */
	public static void saveImage(BufferedImage image, String fileName) {
		saveImage(image, new File(fileName), "png");
	}

	/**
	 * 清空image缓存
	 * 
	 */
	final static public void destroyImages() {
		lazySplitMap.clear();
		cacheImages.clear();
		System.gc();
	}

}