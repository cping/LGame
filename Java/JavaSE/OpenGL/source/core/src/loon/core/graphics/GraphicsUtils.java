package loon.core.graphics;

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
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
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

import loon.core.LSystem;
import loon.core.resource.Resources;
import loon.utils.StringUtils;

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
final public class GraphicsUtils {

	// 自定义的RGB配色器
	final static private DirectColorModel COLOR_MODEL_RGB = new DirectColorModel(
			24, 0xFF0000, 0x00FF00, 0x0000FF);

	// 自定义的ARGB配色器
	final static private DirectColorModel COLOR_MODEL_ARGB = new DirectColorModel(
			32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000);

	final static public Toolkit toolKit = Toolkit.getDefaultToolkit();

	final static private Map cacheImages = Collections
			.synchronizedMap(new HashMap(LSystem.DEFAULT_MAX_CACHE_SIZE));

	// 此部分不要求缓存必然有效，故采用WeakHashMap
	final static private Map cacheByteImages = new WeakHashMap(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

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

	public static Image newRGBImage(int[] rgb, int width, int height,
			boolean processAlpha) {
		if (rgb == null) {
			throw new NullPointerException();
		}
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		if (!processAlpha) {
			int l = rgb.length;
			int[] rgbAux = new int[l];
			for (int i = 0; i < l; i++) {
				rgbAux[i] = rgb[i] | 0xff000000;
			}
			rgb = rgbAux;
		}
		img.setRGB(0, 0, width, height, rgb, 0, width);
		return img;

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
		if (flag) {
			return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
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
	 * @param range
	 *            (指定图片范围，如("1-2"))
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

	public static BufferedImage rotateImage(final BufferedImage image,
			final int degrees) {
		AffineTransform at = new AffineTransform();
		at.rotate(
				degrees < 0 ? -Math.toRadians(degrees) : Math
						.toRadians(degrees), image.getWidth() / 2, image
						.getHeight() / 2);
		BufferedImageOp bio = new AffineTransformOp(at,
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage dest = bio.filter(image, null);
		return dest;
	}

	/**
	 * 设定画布呈相方式为低劣
	 * 
	 * @param g
	 */
	public static void setPoorRenderingHints(final Graphics2D g) {
		g.addRenderingHints(hints_poor);
	}

	/**
	 * 设定画布呈相方式为优秀
	 * 
	 * @param g
	 */
	public static void setExcellentRenderingHints(final Graphics2D g) {
		g.addRenderingHints(hints_excellent);
	}

	/**
	 * 设定画布呈相方式为一般
	 * 
	 * @param g
	 */
	public static void setGeneralRenderingHints(final Graphics2D g) {
		g.addRenderingHints(hints_general);
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
		boolean isOpaque = true;
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, true);
		if ((w > 48 && w < 512) || (h > 48 && h < 512)) {
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int pixels[] = (int[]) pg.getPixels();
			int pixel;
			int size = w * h;
			for (int i = 0; i < size; i++) {
				pixel = LColor.premultiply(pixels[i]);
				if (isOpaque && (pixel >>> 24) < 255) {
					isOpaque = false;
					break;
				}
			}
		} else {
			isOpaque = !pg.getColorModel().hasAlpha();
		}
		BufferedImage bufferimage = GraphicsUtils.createImage(w, h, !isOpaque);
		Graphics g = bufferimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		if (cacheImages.containsValue(image)) {
			Object key = null;
			Set entry = cacheImages.entrySet();
			for (Iterator it = entry.iterator(); it.hasNext();) {
				Entry e = (Entry) it.next();
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
	 * 加载内部file转为Image
	 * 
	 * @param innerFileName
	 * @return
	 */
	final static public Image loadImage(final String innerFileName) {
		return GraphicsUtils.loadImage(innerFileName, true);
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
			return javax.imageio.ImageIO.read(in);
		} catch (IOException e) {
			throw new RuntimeException(
					("File not found. ( " + resName + " )").intern());
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
	final static public Image loadImage(final String innerFileName,
			final boolean isInner) {
		if (innerFileName == null) {
			return null;
		}
		String tmp_file = innerFileName, innerName = StringUtils
				.replaceIgnoreCase(innerFileName, "\\", "/");
		String keyName = innerName.toLowerCase();
		Object imageReference = cacheImages.get(keyName);
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
					in = new DataInputStream(new BufferedInputStream(
							Resources.openResource(innerName)));
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
					return loadImage(innerFileName, false);
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
			in = new DataInputStream(new BufferedInputStream(
					Resources.openResource(innerFileName)));
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
	 * 清空image缓存
	 * 
	 */
	final static public void destroyImages() {
		cacheImages.clear();
		System.gc();
	}

}
