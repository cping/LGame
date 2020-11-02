package org.loon.framework.javase.game.core.graphics;

import java.awt.Color;

/**
 * 
 * Copyright 2008
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
public class LColor extends Color {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	public final static int transparent = 0xff000000;
	
	public LColor(Color c) {
		super(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
	}
	
	public LColor(int r, int g, int b) {
		super(r, g, b, 255);
	}

	public LColor(int r, int g, int b, int a) {
		super(r, g, b, a);
	}

	public LColor(int rgb) {
		super(rgb);
	}

	public LColor(int rgba, boolean alpha) {
		super(rgba, alpha);
	}

	public LColor(float r, float g, float b) {
		super(r, g, b);
	}

	public LColor(float r, float g, float b, float a) {
		super(r, g, b, a);
	}

	/**
	 * 返回ARGB
	 * 
	 * @return
	 */
	public int getARGB() {
		return getARGB(getRed(), getGreen(), getBlue(), getAlpha());
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
	 * 获得r,g,b
	 * 
	 * @param pixel
	 * @return
	 */
	public static int[] getRGBs(final int pixel) {
		int[] rgbs = new int[3];
		rgbs[0] = (pixel >> 16) & 0xff;
		rgbs[1] = (pixel >> 8) & 0xff;
		rgbs[2] = (pixel) & 0xff;
		return rgbs;
	}

	public Color getAWTColor() {
		return new Color(getRed(), getGreen(), getBlue(), getAlpha());
	}

}
