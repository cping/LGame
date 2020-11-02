package org.loon.framework.android.game.core.graphics;

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
 * @email ceponline@yahoo.com.cn
 * @version 0.1.0
 */
public class LColor {

	public final static int transparent = 0xff000000;
	
	public final static LColor white = new LColor(255, 255, 255);

	public final static LColor lightGray = new LColor(192, 192, 192);

	public final static LColor gray = new LColor(128, 128, 128);

	public final static LColor darkGray = new LColor(64, 64, 64);

	public final static LColor black = new LColor(0, 0, 0);

	public final static LColor red = new LColor(255, 0, 0);

	public final static LColor pink = new LColor(255, 175, 175);

	public final static LColor orange = new LColor(255, 200, 0);

	public final static LColor yellow = new LColor(255, 255, 0);

	public final static LColor green = new LColor(0, 255, 0);

	public final static LColor magenta = new LColor(255, 0, 255);

	public final static LColor cyan = new LColor(0, 255, 255);

	public final static LColor blue = new LColor(0, 0, 255);

	private static final double FACTOR = 0.7;

	private static final int ALPHA = 24;

	private static final int RED = 16;

	private static final int GREEN = 8;

	private static final int BLUE = 0;

	private int r, g, b, alpha;

	private int[] rgba;

	/**
	 * 构造LColor
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	public LColor(int r, int g, int b, int alpha) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.alpha = alpha;
		this.rgba = new int[] { r, g, b, alpha };
	}

	/**
	 * 构造LColor
	 * 
	 * @param c
	 */
	public LColor(LColor c) {
		this(c.r, c.g, c.b, c.alpha);
	}

	/**
	 * 构造LColor
	 * 
	 * @param r
	 * @param g
	 * @param b
	 */
	public LColor(int r, int g, int b) {
		this(r, g, b, 0xff);
	}

	/**
	 * 构造LColor
	 * 
	 * @param r
	 * @param g
	 * @param b
	 */
	public LColor(float r, float g, float b) {
		this((int) (r * 255 + 0.5), (int) (g * 255 + 0.5),
				(int) (b * 255 + 0.5));

	}

	/**
	 * 构造LColor
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public LColor(float r, float g, float b, float a) {
		this((int) (r * 255 + 0.5), (int) (g * 255 + 0.5),
				(int) (b * 255 + 0.5), (int) (a * 255 + 0.5));

	}

	/**
	 * 构造LColor，并判定是否允许透明度
	 * 
	 * @param rgba
	 * @param hasalpha
	 */
	public LColor(int rgba, boolean hasalpha) {
		if (hasalpha) {
			r = LColor.getRed(rgba);
			g = LColor.getGreen(rgba);
			b = LColor.getBlue(rgba);
			alpha = LColor.getAlpha(rgba);
		} else {
			r = LColor.getRed(rgba);
			g = LColor.getGreen(rgba);
			b = LColor.getBlue(rgba);
		}
	}

	/**
	 * 构造LColor
	 * 
	 * @param color
	 */
	public LColor(int color) {
		this.r = getRed(color);
		this.g = getGreen(color);
		this.b = getBlue(color);
		this.alpha = getAlpha(color);
	}

	/**
	 * 构造LColor
	 * 
	 * @param colors
	 */
	public LColor(int[] colors) {
		this.r = colors[0];
		this.g = colors[1];
		this.b = colors[2];
		this.alpha = colors[3];
	}

	/**
	 * 返回像素集合
	 * 
	 * @return
	 */
	public int[] getRGBs() {
		return rgba;
	}

	/**
	 * 将color返回为像素
	 * 
	 * @param color
	 * @return
	 */
	public int getPixel(final LColor c) {
		return getPixel(c.getRed(), c.getGreen(), c.getBlue());
	}

	public static int getPixel(int r, int g, int b) {
		return (255 << 24) + (r << 16) + (g << 8) + b;
	}

	public void setAlphaValue(int alpha) {
		this.alpha = alpha;
	}

	public void setAlpha(float alpha) {
		setAlphaValue((int) (255 * alpha));
	}

	public LColor brighter() {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();

		int i = (int) (1.0 / (1.0 - FACTOR));
		if (r == 0 && g == 0 && b == 0) {
			return new LColor(i, i, i);
		}
		if (r > 0 && r < i) {
			r = i;
		}
		if (g > 0 && g < i) {
			g = i;
		}
		if (b > 0 && b < i) {
			b = i;
		}
		return new LColor(Math.min((int) (r / FACTOR), 255), Math.min(
				(int) (g / FACTOR), 255), Math.min((int) (b / FACTOR), 255));
	}

	public LColor darker() {
		return new LColor(Math.max((int) (getRed() * FACTOR), 0), Math.max(
				(int) (getGreen() * FACTOR), 0), Math.max(
				(int) (getBlue() * FACTOR), 0));
	}

	/**
	 * 返回ARGB
	 * 
	 * @return
	 */
	public int getARGB() {
		return getARGB(r, g, b, alpha);
	}

	/**
	 * 返回RGB
	 * 
	 * @return
	 */
	public int getRGB() {
		return getRGB(r, g, b);
	}

	/**
	 * 返回指定像素
	 * 
	 * @param pixels
	 * @param width
	 * @param x
	 * @param y
	 * @return
	 */
	public static int getPixel(int[] pixels, int width, int x, int y) {
		return pixels[width * y + x];
	}

	/**
	 * 判定色彩是否相等
	 * 
	 * @param c
	 * @return
	 */
	public boolean equals(final LColor c) {
		return (c.r == r) && (c.g == g) && (c.b == b) && (c.alpha == alpha);
	}

	/**
	 * 获得24位色
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int getRGB(int r, int g, int b) {
		return getARGB(r, g, b, 0xff);
	}

	/**
	 * 获得RGB颜色
	 * 
	 * @param pixels
	 * @return
	 */
	public static int getRGB(int pixels) {
		int r = (pixels >> 16) & 0xff;
		int g = (pixels >> 8) & 0xff;
		int b = pixels & 0xff;
		return getRGB(r, g, b);
	}

	/**
	 * 获得32位色
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 * @return
	 */
	public static int getARGB(int r, int g, int b, int alpha) {
		return (alpha << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * 获得Aplha
	 * 
	 * @param color
	 * @return
	 */
	public static int getAlpha(int color) {
		return color >>> 24;
	}

	/**
	 * 获得Red
	 * 
	 * @param color
	 * @return
	 */
	public static int getRed(int color) {
		return (color >> 16) & 0xff;
	}

	/**
	 * 获得Green
	 * 
	 * @param color
	 * @return
	 */
	public static int getGreen(int color) {
		return (color >> 8) & 0xff;
	}

	/**
	 * 获得Blud
	 * 
	 * @param color
	 * @return
	 */
	public static int getBlue(int color) {
		return color & 0xff;
	}

	/**
	 * 像素前乘
	 * 
	 * @param argbColor
	 * @return
	 */
	public static int premultiply(int argbColor) {
		int a = argbColor >>> 24;
		if (a == 0) {
			return 0;
		} else if (a == 255) {
			return argbColor;
		} else {
			int r = (argbColor >> 16) & 0xff;
			int g = (argbColor >> 8) & 0xff;
			int b = argbColor & 0xff;
			r = (a * r + 127) / 255;
			g = (a * g + 127) / 255;
			b = (a * b + 127) / 255;
			return (a << 24) | (r << 16) | (g << 8) | b;
		}
	}

	/**
	 * 像素前乘
	 * 
	 * @param rgbColor
	 * @param alpha
	 * @return
	 */
	public static int premultiply(int rgbColor, int alpha) {
		if (alpha <= 0) {
			return 0;
		} else if (alpha >= 255) {
			return 0xff000000 | rgbColor;
		} else {
			int r = (rgbColor >> 16) & 0xff;
			int g = (rgbColor >> 8) & 0xff;
			int b = rgbColor & 0xff;

			r = (alpha * r + 127) / 255;
			g = (alpha * g + 127) / 255;
			b = (alpha * b + 127) / 255;
			return (alpha << 24) | (r << 16) | (g << 8) | b;
		}
	}

	/**
	 * 消除前乘像素
	 * 
	 * @param preARGBColor
	 * @return
	 */
	public static int unpremultiply(int preARGBColor) {
		int a = preARGBColor >>> 24;
		if (a == 0) {
			return 0;
		} else if (a == 255) {
			return preARGBColor;
		} else {
			int r = (preARGBColor >> 16) & 0xff;
			int g = (preARGBColor >> 8) & 0xff;
			int b = preARGBColor & 0xff;

			r = 255 * r / a;
			g = 255 * g / a;
			b = 255 * b / a;
			return (a << 24) | (r << 16) | (g << 8) | b;
		}
	}

	/**
	 * 锐化指定像素集合
	 * 
	 * @param pixels
	 * @param w
	 * @param h
	 * @param f
	 */
	public static void sharpen(int[] pixels, int w, int h, double f) {
		int[] tmp = new int[pixels.length];
		System.arraycopy(pixels, 0, tmp, 0, tmp.length);
		for (int y = 0; y < h; y = y + 2) {
			for (int x = 0; x < w; x = x + 2) {
				for (int i = 0; i < 3; ++i) {
					int color = 0;
					switch (i) {
					case 0:
						color = RED;
						break;
					case 1:
						color = GREEN;
						break;
					case 2:
						color = BLUE;
						break;
					}
					int val = ((int) (getPixel(color, pixels, x - 1, y, w, h)
							* -f + getPixel(color, pixels, x, y - 1, w, h) * -f
							+ getPixel(color, pixels, x, y, w, h) * (1 + 4 * f)
							+ getPixel(color, pixels, x, y + 1, w, h) * -f + getPixel(
							color, pixels, x - 1, y, w, h)
							* -f));
					putPixel(val, color, tmp, x, y, w, h);
				}
			}
		}
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				pixels[w * y + x] = tmp[w * y + x];
			}
		}
		tmp = null;
	}

	/**
	 * 插入指定像素
	 * 
	 * @param val
	 * @param color
	 * @param pixels
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public static void putPixel(int val, int color, int pixels[], int x, int y,
			int w, int h) {
		int nval;

		if (x < 0) {
			x = 0;
		}
		if (x >= w) {
			x = w - 1;
		}
		if (y < 0) {
			y = 0;
		}
		if (y >= h) {
			y = h - 1;
		}
		if (val < 0) {
			val = 0;
		}
		if (val > 255) {
			val = 255;
		}
		switch (color) {
		case ALPHA:
			nval = (pixels[w * y + x] & (~(255 << ALPHA))) | (val << ALPHA);
			break;
		case RED:
			nval = (pixels[w * y + x] & (~(255 << RED))) | (val << RED);
			break;
		case GREEN:
			nval = (pixels[w * y + x] & (~(255 << GREEN))) | (val << GREEN);
			break;
		case BLUE:
			nval = (pixels[w * y + x] & (~(255 << BLUE))) | (val << BLUE);
			break;
		default:
			nval = pixels[w * y + x];
			break;
		}
		pixels[w * y + x] = nval;
	}

	/**
	 * 获得指定像素
	 * 
	 * @param color
	 * @param pixels
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static int getPixel(int color, int pixels[], int x, int y, int w,
			int h) {
		if (x < 0) {
			x = 0;
		}
		if (x >= w) {
			x = w - 1;
		}
		if (y < 0) {
			y = 0;
		}
		if (y >= h) {
			y = h - 1;
		}
		int val = pixels[w * y + x];
		switch (color) {
		case ALPHA:
			val = val >> ALPHA;
			break;
		case RED:
			val = val >> RED;
			break;
		case GREEN:
			val = val >> GREEN;
			break;
		case BLUE:
			val = val >> BLUE;
			break;
		}
		return (val & 255);
	}

	public int getRed() {
		return r;
	}

	public int getGreen() {
		return g;
	}

	public int getBlue() {
		return b;
	}

	public int getAlpha() {
		return alpha;
	}

}
