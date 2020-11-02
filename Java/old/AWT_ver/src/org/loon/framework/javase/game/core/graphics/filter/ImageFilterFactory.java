package org.loon.framework.javase.game.core.graphics.filter;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import org.loon.framework.javase.game.utils.GraphicsUtils;

/**
 * Copyright 2008 - 2010
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
public class ImageFilterFactory implements ImageFilterType {

	private static ImageFilterFactory filterFactry = null;

	public static ImageFilterFactory getInstance() {
		if (filterFactry == null) {
			filterFactry = new ImageFilterFactory();
		}
		return filterFactry;
	}

	/**
	 * 将图片转色为灰色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getGray(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.GrayFilter));
	}

	/**
	 * 将图片转色为灰色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getGray(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.GrayFilter);
	}

	/**
	 * 过滤为偏黄图像
	 * 
	 * @param image
	 * @return
	 */
	public static Image getYellow(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.YellowFilter);
	}

	/**
	 * 过滤为偏黄图像
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getYellow(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.GrayFilter));
	}

	/**
	 * 过滤为暗淡的图像
	 * 
	 * @param image
	 * @return
	 */
	public static Image getRate(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.RateFilter);
	}

	/**
	 * 过滤为暗淡的图像
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getRate(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.RateFilter));
	}

	/**
	 * 过滤为黑白图像
	 * 
	 * @param image
	 * @return
	 */
	public static Image getBlackWhite(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.BlackWhiteFilter);
	}

	/**
	 * 过滤为红色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getBlackWhite(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.BlackWhiteFilter));
	}

	/**
	 * 过滤为红色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getRed(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.RedFilter);
	}

	/**
	 * 过滤为绿色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getRed(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.RedFilter));
	}

	/**
	 * 过滤为绿色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getGreen(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.GreenFilter);
	}

	/**
	 * 过滤为紫色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getGreen(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.GreenFilter));
	}

	/**
	 * 过滤为紫色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getMagenta(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.MagentaFilter);
	}

	/**
	 * 过滤为黑白图像
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getMagenta(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.MagentaFilter));
	}

	/**
	 * 过滤为粉红色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getPink(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.PinkFilter);
	}

	/**
	 * 过滤为粉红色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getPink(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.PinkFilter));
	}

	/**
	 * 过滤为白色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getWhite(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.WhiteFilter);
	}

	/**
	 * 过滤为白色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getWhite(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.WhiteFilter));
	}

	/**
	 * 过滤为纯红色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getAllRed(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.AllRedFilter);
	}

	/**
	 * 过滤为纯红色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getAllRed(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.AllRedFilter));
	}

	/**
	 * 过滤为纯白色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getAllWhite(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.AllWhiteFilter);
	}

	/**
	 * 过滤为纯白色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getAllWhite(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.AllWhiteFilter));
	}

	/**
	 * 过滤为纯黑色
	 * 
	 * @param image
	 * @return
	 */
	public static Image getAllWBlack(final Image img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return factory.doFilter(img, ImageFilterType.AllBlackFilter);
	}

	/**
	 * 过滤为纯黑色
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getAllWBlack(final BufferedImage img) {
		ImageFilterFactory factory = ImageFilterFactory.getInstance();
		return GraphicsUtils.getBufferImage(factory.doFilter(img,
				ImageFilterType.AllBlackFilter));
	}

	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
			case 0:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (t * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 1:
				r = (int) (q * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 2:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (t * 255.0f + 0.5f);
				break;
			case 3:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (q * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 4:
				r = (int) (t * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 5:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (q * 255.0f + 0.5f);
				break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}

	public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
		float hue, saturation, brightness;
		if (hsbvals == null) {
			hsbvals = new float[3];
		}
		int cmax = (r > g) ? r : g;
		if (b > cmax)
			cmax = b;
		int cmin = (r < g) ? r : g;
		if (b < cmin)
			cmin = b;

		brightness = ((float) cmax) / 255.0f;
		if (cmax != 0)
			saturation = ((float) (cmax - cmin)) / ((float) cmax);
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
			float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
			float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0f + redc - bluec;
			else
				hue = 4.0f + greenc - redc;
			hue = hue / 6.0f;
			if (hue < 0)
				hue = hue + 1.0f;
		}
		hsbvals[0] = hue;
		hsbvals[1] = saturation;
		hsbvals[2] = brightness;
		return hsbvals;
	}

	public Image doFilter(Image img, int filter) {
		if (NoneFilter == filter) {
			return img;
		}
		ImageFilter imgfilter = null;
		if (filter == GreenFilter) {
			imgfilter = new GreenFilter();
		} else if (filter == RedBlueSwapFilter) {
			imgfilter = new RedBlueSwapFilter();
		} else if (filter == YellowInvertFilter) {
			imgfilter = new YellowInvertFilter();
		} else if (filter == PsychedeliaFilter) {
			imgfilter = new PsychedeliaFilter();
		} else if (filter == RedFilter) {
			imgfilter = new RedFilter();
		} else if (filter == MagentaFilter) {
			imgfilter = new MagentaFilter();
		} else if (filter == PinkFilter) {
			imgfilter = new PinkFilter();
		} else if (filter == YellowFilter) {
			imgfilter = new YellowFilter();
		} else if (filter == BlueFilter) {
			imgfilter = new BlueFilter();
		} else if (filter == SwapFilter) {
			imgfilter = new SwapFilter();
		} else if (filter == GrayFilter) {
			imgfilter = new GrayFilter(true, 25);
		} else if (filter == BlackWhiteFilter) {
			imgfilter = new BlackWhiteFilter();
		} else if (filter == RateFilter) {
			imgfilter = new RateFilter();
		} else if (filter == WhiteFilter) {
			imgfilter = new WhiteFilter();
		} else if (filter == AllRedFilter) {
			imgfilter = new AllRedFilter();
		} else if (filter == AllWhiteFilter) {
			imgfilter = new AllWhiteFilter();
		} else if (filter == AllBlackFilter) {
			imgfilter = new AllBlackFilter();
		} else {
			return null;
		}
		return doFilter(img, imgfilter);
	}

	public Image doFilter(Image img, ImageFilter imgfilter) {
		ImageProducer imageProducer = new FilteredImageSource(img.getSource(),
				imgfilter);
		return GraphicsUtils.toolKit.createImage(imageProducer);
	}

	final class GrayFilter extends RGBImageFilter {

		private boolean brighter;

		private int percent;

		public GrayFilter(boolean b, int p) {
			brighter = b;
			percent = p;
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			int gray = (int) ((0.299 * ((rgb >> 16) & 0xff) + 0.587
					* ((rgb >> 8) & 0xff) + 0.114 * (rgb & 0xff)) / 3);
			if (brighter) {
				gray = (255 - ((255 - gray) * (100 - percent) / 100));
			} else {
				gray = (gray * (100 - percent) / 100);
			}
			if (gray < 0) {
				gray = 0;
			}
			if (gray > 255) {
				gray = 255;
			}
			return (rgb & 0xff000000) | (gray << 16) | (gray << 8)
					| (gray << 0);
		}
	}

	final class BlackWhiteFilter extends RGBImageFilter {

		public BlackWhiteFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			int red = (rgb & 0x00ff0000) >> 16;
			int green = (rgb & 0x0000ff00) >> 8;
			int blue = (rgb & 0x000000ff);
			int ret = 0xff000000;
			int grey;

			grey = (int) ((double) red * .299 + (double) green * .587 + (double) blue * .114);
			ret |= grey;
			ret |= (grey << 8);
			ret |= (grey << 16);
			return ret;

		}
	}

	final class RateFilter extends RGBImageFilter {

		private final double rRate;

		private final double gRate;

		private final double bRate;

		public RateFilter() {
			this(0.69999999999999996D);
		}

		public RateFilter(double d) {
			this(d, d, d);
		}

		public RateFilter(double d, double d1, double d2) {
			canFilterIndexColorModel = true;
			if (d < 0.0D) {
				d = 0.0D;
			}
			if (d1 < 0.0D) {
				d1 = 0.0D;
			}
			if (d2 < 0.0D) {
				d2 = 0.0D;
			}
			rRate = d;
			gRate = d1;
			bRate = d2;
		}

		public int filterRGB(int i, int j, int k) {
			int l = (k & 0xff000000) >> 24;
			int i1 = (k & 0xff0000) >> 16;
			int j1 = (k & 0xff00) >> 8;
			int k1 = k & 0xff;
			i1 = (int) (rRate * (double) i1);
			if (i1 > 255) {
				i1 = 255;
			}
			j1 = (int) (gRate * (double) j1);
			if (j1 > 255) {
				j1 = 255;
			}
			k1 = (int) (bRate * (double) k1);
			if (k1 > 255) {
				k1 = 255;
			}
			return (l << 24) + (i1 << 16) + (j1 << 8) + k1;
		}

	}

	final class GreenFilter extends RGBImageFilter {
		public GreenFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xff00ff00) | ((rgb & 0xffff00) >> 16) | ((rgb & 0x00) << 16));
		}
	}

	final class RedBlueSwapFilter extends RGBImageFilter {
		public RedBlueSwapFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xff00ff00) | ((rgb & 0xff0000) >> 16) | ((rgb & 0xff) << 16));
		}
	}

	final class YellowInvertFilter extends RGBImageFilter {
		public YellowInvertFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xffff00) | ((rgb & 0x00) >> 16) | ((rgb & 0xffff) << 16));
		}
	}

	final class PsychedeliaFilter extends RGBImageFilter {
		public PsychedeliaFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xff00ff00 << 16) | ((rgb & 0xff0000) >> 16) | ((rgb & 0xff) << 16));
		}
	}

	final class RedFilter extends RGBImageFilter {
		public RedFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0x0000ff00 << 16) | ((rgb & 0x000000) >> 16) | ((rgb & 0xff) << 16));
		}
	}

	final class AllRedFilter extends RGBImageFilter {
		public AllRedFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			int transparency = (rgb >> 24) & 0xFF;
			if (transparency <= 1) {
				return rgb;
			}
			return Color.red.getRGB();
		}
	}

	final class AllBlackFilter extends RGBImageFilter {
		public AllBlackFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			int transparency = (rgb >> 24) & 0xFF;
			if (transparency <= 1) {
				return rgb;
			}
			return Color.black.getRGB();
		}
	}

	final class AllWhiteFilter extends RGBImageFilter {
		public AllWhiteFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			int transparency = (rgb >> 24) & 0xFF;
			if (transparency <= 1) {
				return rgb;
			}
			return Color.white.getRGB();
		}
	}

	final class WhiteFilter extends RGBImageFilter {

		private final float[] hsv = new float[3];

		public WhiteFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(final int x, final int y, final int rgb) {
			int transparency = (rgb >> 24) & 0xFF;

			if (transparency <= 1) {
				return rgb;
			}
			transparency /= 2;
			RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 0) & 0xFF,
					hsv);
			hsv[1] = 0;

			return HSBtoRGB(hsv[0], hsv[1], hsv[2]) + (transparency << 24);
		}
	}

	final class MagentaFilter extends RGBImageFilter {
		public MagentaFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xffffffff << 16) | ((rgb & 0xff00ff) >> 16) | ((rgb & 0x00)));
		}
	}

	final class PinkFilter extends RGBImageFilter {
		public PinkFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xffffffff >> 16) | ((rgb & 0xff00ff) << 16) | ((rgb & 0x00)));
		}
	}

	final class YellowFilter extends RGBImageFilter {
		public YellowFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xff00ff00 >> 16) | ((rgb & 0xffff00) << 16) | ((rgb & 0x00)));
		}
	}

	final class BlueFilter extends RGBImageFilter {
		public BlueFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xff00ff00 << 16) | ((rgb & 0xffff00) >> 16) | ((rgb & 0x00) >> 8)

			);
		}
	}

	final class SwapFilter extends RGBImageFilter {
		public SwapFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			return ((rgb & 0xff00ff00 << 8) | ((rgb & 0xffff00) >> 8) | ((rgb & 0x00) >> 8));
		}
	}

}
