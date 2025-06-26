/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.canvas;

import java.io.Serializable;

import loon.LSystem;
import loon.geom.Vector3f;
import loon.geom.Vector4f;
import loon.utils.CharUtils;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;

/**
 * 色彩处理用类,Loon中所有组件色彩设置此类皆可通用
 */
public class LColor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2290994222887151982L;

	// 默认色彩
	public static final int DEF_COLOR = 0xFFFFFFFF;

	// 默认黑色透明区域
	public static final int TRANSPARENT = 0;

	public final static boolean isColorValue(String c) {
		if (StringUtils.isNullOrEmpty(c)) {
			return false;
		}
		c = c.trim().toLowerCase();
		if (c.startsWith("#") || c.startsWith("0x") || c.startsWith("rgb") || c.startsWith("argb")
				|| c.startsWith("transparent")) {
			return true;
		} else if (MathUtils.isNan(c)) {
			return true;
		} else if (StringUtils.isHex(c)) {
			return true;
		} else if (StringUtils.isAlphabet(c)) {
			return true;
		}
		return false;
	}

	public final static LColorLinear linear(int[] colors) {
		return new LColorLinear(colors);
	}

	public final static LColorLinear linear(LColor[] colors) {
		return new LColorLinear(colors);
	}

	/**
	 * 计算一种颜色到另一种颜色之间的平滑过渡
	 * 
	 * @param startColor
	 * @param endColor
	 * @param ratio
	 * @return
	 */
	public static int interpolate(int startColor, int endColor, float ratio) {
		int startA = (startColor >> 24) & 0xFF;
		int startR = (startColor >> 16) & 0xFF;
		int startG = (startColor >> 8) & 0xFF;
		int startB = (startColor) & 0xFF;
		int endA = (endColor >> 24) & 0xFF;
		int endR = (endColor >> 16) & 0xFF;
		int endG = (endColor >> 8) & 0xFF;
		int endB = (endColor) & 0xFF;
		int newA = (int) (startA + (endA - startA) * ratio);
		int newR = (int) (startR + (endR - startR) * ratio);
		int newG = (int) (startG + (endG - startG) * ratio);
		int newB = (int) (startB + (endB - startB) * ratio);
		return LColor.getARGB(newR, newG, newB, newA);
	}

	/**
	 * 计算一种颜色到另一种颜色之间的平滑过渡
	 * 
	 * @param c1
	 * @param c2
	 * @param ratio
	 * @return
	 */
	public static LColor interpolate(LColor c1, LColor c2, float ratio) {
		if (c1 != null && c2 == null) {
			return c1;
		}
		if (c1 == null && c2 != null) {
			return c2;
		}
		if (c1 == null || c2 == null) {
			return null;
		}
		int r = (int) ((c2.getRed() - c1.getRed()) * ratio + c1.getRed());
		int g = (int) ((c2.getGreen() - c1.getGreen()) * ratio + c1.getGreen());
		int b = (int) ((c2.getBlue() - c1.getBlue()) * ratio + c1.getBlue());
		int a = (int) ((c2.getAlpha() - c1.getAlpha()) * ratio + c1.getAlpha());
		return new LColor(r, g, b, a);
	}

	/**
	 * 获得24位色
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final int getRGB(int r, int g, int b) {
		return rgb(r, g, b);
	}

	public static final int getBGR(int r, int g, int b) {
		return bgr(r, g, b);
	}

	/**
	 * 获得RGB颜色
	 * 
	 * @param pixels
	 * @return
	 */
	public static final int getRGB(int pixels) {
		int r = (pixels >> 16) & 0xFF;
		int g = (pixels >> 8) & 0xFF;
		int b = pixels & 0xFF;
		return rgb(r, g, b);
	}

	public static final int getBGR(int pixels) {
		int r = (pixels >> 16) & 0xFF;
		int g = (pixels >> 8) & 0xFF;
		int b = pixels & 0xFF;
		return bgr(r, g, b);
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
	public static final int getARGB(int r, int g, int b, int alpha) {
		return argb(alpha, r, g, b);
	}

	public static final int getABGR(int r, int g, int b, int alpha) {
		return abgr(alpha, r, g, b);
	}

	/**
	 * 以指定颜色指定百分比获得渐变色彩
	 * 
	 * @param startColor
	 * @param endColor
	 * @return
	 */
	public static final int getGradient(int startColor, int endColor) {
		return getGradient(startColor, endColor, 1f);
	}

	/**
	 * 以指定颜色指定百分比获得渐变色彩
	 * 
	 * @param startColor
	 * @param endColor
	 * @param percentage
	 * @return
	 */
	public static final int getGradient(int startColor, int endColor, float percentage) {
		if (percentage > 1f) {
			percentage = 1f;
		}
		int alphaStart = alpha(startColor);
		int redStart = red(startColor);
		int blueStart = blue(startColor);
		int greenStart = green(startColor);
		int alphaEnd = alpha(endColor);
		int redEnd = red(endColor);
		int blueEnd = blue(endColor);
		int greenEnd = green(endColor);
		int alphaDiff = alphaEnd - alphaStart;
		int redDiff = redEnd - redStart;
		int blueDiff = blueEnd - blueStart;
		int greenDiff = greenEnd - greenStart;
		int alphaCurrent = (int) (alphaStart + percentage * alphaDiff);
		int redCurrent = (int) (redStart + percentage * redDiff);
		int blueCurrent = (int) (blueStart + percentage * blueDiff);
		int greenCurrent = (int) (greenStart + percentage * greenDiff);
		return argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
	}

	/**
	 * 返回一组随机的RGB色彩
	 * 
	 * @param startColor
	 * @param endColor
	 * @return
	 */
	public static LColor getRandomRGBColor(float startColor, float endColor) {
		return new LColor(MathUtils.random(startColor, endColor), MathUtils.random(startColor, endColor),
				MathUtils.random(startColor, endColor));
	}

	/**
	 * 返回一组随机的RGBA色彩
	 * 
	 * @param startColor
	 * @param endColor
	 * @return
	 */
	public static LColor getRandomRGBAColor(float startColor, float endColor) {
		return new LColor(MathUtils.random(startColor, endColor), MathUtils.random(startColor, endColor),
				MathUtils.random(startColor, endColor), MathUtils.random(startColor, endColor));
	}

	/**
	 * 返回一组随机的RGB色彩
	 * 
	 * @return
	 */
	public static LColor getRandomRGBColor() {
		return getRandomRGBColor(0f, 1f);
	}

	/**
	 * 返回一组随机的RGBA色彩
	 * 
	 * @return
	 */
	public static LColor getRandomRGBAColor() {
		return getRandomRGBAColor(0f, 1f);
	}

	/**
	 * 返回一个符合CIE标准的色彩Luminance(亮度)值
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static float getLuminanceRGB(float r, float g, float b) {
		return 0.2126f * r + 0.7152f * g + 0.0722f * b;
	}

	/**
	 * 返回一个符合CIE标准的色彩Luminance(亮度)值
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static float getLuminanceRGB(int r, int g, int b) {
		return 0.2126f * ((float) r / 255f) + 0.7152f * ((float) g / 255f) + 0.0722f * ((float) b / 255f);
	}

	/**
	 * 获得指定float形式RGB值的HSL值
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static LColor getRGBtoHSL(float r, float g, float b) {
		float max = MathUtils.max(r, g, b);
		float min = MathUtils.min(r, g, b);
		float h = 0, s = 0, l = (max + min) / 2f;
		if (max == min) {
			h = s = 0;
		} else {
			float d = max - min;
			s = l > 0.5f ? d / (2f - max - min) : d / (max + min);
			final int ir = (int) r;
			final int ig = (int) g;
			final int ib = (int) b;
			if (max == ir) {
				h = (g - b) / d + (g < b ? 6 : 0);
			} else if (max == ig) {
				h = (b - r) / d + 2;
			} else if (max == ib) {
				h = (r - g) / d + 4;
			}
		}
		h /= 6;
		return new LColor(h, s, l);
	}

	protected static float hue2rgb(float p, float q, float t) {
		if (t < 0) {
			t += 1f;
		}
		if (t > 1f) {
			t -= 1f;
		}
		if (t < 1f / 6f) {
			return p + (q - p) * 6f * t;
		}
		if (t < 1f / 2f) {
			return q;
		}
		if (t < 2f / 3f) {
			return p + (q - p) * (2f / 3f - t) * 6f;
		}
		return p;
	}

	/**
	 * 转化HSL值为RGB值
	 * 
	 * @param h
	 * @param s
	 * @param l
	 * @return
	 */
	public static LColor getHSLtoRGB(float h, float s, float l) {
		float r, g, b;
		if (s == 0) {
			r = g = b = l;
		} else {
			float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
			float p = 2 * l - q;
			r = hue2rgb(p, q, h + 1 / 3);
			g = hue2rgb(p, q, h);
			b = hue2rgb(p, q, h - 1 / 3);
		}
		return new LColor(r, g, b);
	}

	/**
	 * 注入一个与指定名称绑定的Color(可以使用findName函数再次获得)
	 * 
	 * @param colorName
	 * @param color
	 * @return
	 */
	public final static boolean putName(String colorName, LColor color) {
		return LColorList.get().putColor(colorName, color);
	}

	/**
	 * 返回一个指定英文名称的Color(按照html标准)
	 * 
	 * @param colorName
	 * @return
	 */
	public final static LColor findName(String colorName) {
		return LColorList.get().find(colorName);
	}

	/**
	 * 返回当前像素对应的英文名称
	 * 
	 * @param pixel
	 * @return
	 */
	public final static String getColorName(int pixel) {
		return LColorList.get().find(pixel);
	}

	/**
	 * 返回当前色彩对应的英文名称
	 * 
	 * @param color
	 * @return
	 */
	public final static String getColorName(LColor color) {
		return LColorList.get().find(color);
	}

	public static final LColor lerp(LColor value1, LColor value2, float amount) {
		return new LColor(lerp(value1.getRed(), value2.getRed(), amount),
				lerp(value1.getGreen(), value2.getGreen(), amount), lerp(value1.getBlue(), value2.getBlue(), amount),
				lerp(value1.getAlpha(), value2.getAlpha(), amount));
	}

	private static final int lerp(int color1, int color2, float amount) {
		return color1 + (int) ((color2 - color1) * amount);
	}

	/**
	 * 获得Aplha
	 * 
	 * @param color
	 * @return
	 */
	public static final int getAlpha(int color) {
		return color >>> 24;
	}

	/**
	 * 获得Red
	 * 
	 * @param color
	 * @return
	 */
	public static final int getRed(int color) {
		return (color >> 16) & 0xFF;
	}

	/**
	 * 获得Green
	 * 
	 * @param color
	 * @return
	 */
	public static final int getGreen(int color) {
		return (color >> 8) & 0xFF;
	}

	/**
	 * 获得Blud
	 * 
	 * @param color
	 * @return
	 */
	public static final int getBlue(int color) {
		return color & 0xFF;
	}

	/**
	 * 获得像素预乘
	 * 
	 * @param argbColor
	 * @return
	 */
	public static final int premultiply(int argbColor) {
		int a = argbColor >>> 24;
		if (a == 0) {
			return 0;
		} else if (a == 255) {
			return argbColor;
		} else {
			int r = (argbColor >> 16) & 0xFF;
			int g = (argbColor >> 8) & 0xFF;
			int b = argbColor & 0xFF;
			r = (a * r + 127) / 255;
			g = (a * g + 127) / 255;
			b = (a * b + 127) / 255;
			return (a << 24) | (r << 16) | (g << 8) | b;
		}
	}

	public static final int[] getRGBs(final int pixel) {
		int[] rgbs = new int[3];
		rgbs[0] = (pixel >> 16) & 0xFF;
		rgbs[1] = (pixel >> 8) & 0xFF;
		rgbs[2] = (pixel) & 0xFF;
		return rgbs;
	}

	public static final int[] getRGBAs(final int pixel) {
		int[] rgbas = new int[4];
		rgbas[0] = (pixel >> 16) & 0xFF;
		rgbas[1] = (pixel >> 8) & 0xFF;
		rgbas[2] = (pixel) & 0xFF;
		rgbas[3] = pixel >>> 24;
		return rgbas;
	}

	public static final float toFloatBits(float r, float g, float b, float a) {
		int color = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
		return NumberUtils.intBitsToFloat(color & 0xfeffffff);
	}

	public static final float[] rgbaToFloats(int rgba) {
		float[] rgbas = new float[4];
		rgbas[0] = ((rgba >> 24) & 0xff) / 255f;
		rgbas[1] = ((rgba >> 16) & 0xff) / 255f;
		rgbas[2] = ((rgba >> 8) & 0xff) / 255f;
		rgbas[3] = ((rgba) & 0xff) / 255f;
		return rgbas;
	}

	public static final LColor toDecreaseBrightness(LColor c, float brightness) {
		brightness = brightness > 1f ? 1f : brightness;
		brightness = brightness < 0f ? 0f : brightness;

		float newRed = c.r - (c.r * brightness);
		float newGreen = c.g - (c.g * brightness);
		float newBlue = c.b - (c.b * brightness);

		newRed = newRed < 0f ? 0f : newRed;
		newGreen = newGreen < 0f ? 0f : newGreen;
		newBlue = newBlue < 0f ? 0f : newBlue;

		return new LColor(newRed, newGreen, newBlue, c.a);
	}

	public static final LColor hsvToColor(float h, float s, float v) {
		if (h == 0 && s == 0) {
			return new LColor(v, v, v);
		}
		float c = s * v;
		float x = c * (1f - MathUtils.abs(h % 2f - 1f));
		float m = v - c;

		if (h < 1) {
			return new LColor(c + m, x + m, m);
		} else if (h < 2) {
			return new LColor(x + m, c + m, m);
		} else if (h < 3) {
			return new LColor(m, c + m, x + m);
		} else if (h < 4) {
			return new LColor(m, x + m, c + m);
		} else if (h < 5) {
			return new LColor(x + m, m, c + m);
		} else {
			return new LColor(c + m, m, x + m);
		}
	}

	public static final String cssColorString(int color) {
		float a = ((color >> 24) & 0xFF) / 255f;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;
		return "rgba(" + r + "," + g + "," + b + "," + a + ")";
	}

	public static final LColor hexToColor(String c) {
		try {
			if (c.startsWith("#")) {
				return hexToColor(c.substring(1));
			} else if (c.startsWith("0x")) {
				return hexToColor(c.substring(2));
			} else {
				return new LColor((int) CharUtils.fromHexToLong(c));
			}
		} catch (Throwable e) {
			return new LColor();
		}
	}

	public static final LColor web(final String stringColor) {
		return web(stringColor, 1f);
	}

	public static final LColor web(final String hexColor, final float alpha) {
		if (alpha == 1f) {
			return new LColor(hexColor);
		}
		return new LColor(hexColor).mulSelfAlpha(alpha);
	}

	public static final LColor stringToColor(String c) {
		return hexToColor(c);
	}

	public static final int[] convertToABGR(int pixelHeight, int pixelWidth, int[] srcPixels) {
		return convertToABGR(pixelHeight, pixelWidth, srcPixels, srcPixels);
	}

	public static final int[] toRgbInt(LColor[] color) {
		int[] ints = new int[color.length];
		for (int i = 0; i < color.length; i++) {
			ints[i] = color[i].getRGB();
		}
		return ints;
	}

	public static final LColor[] toRgbaColor(int[] color) {
		LColor[] colors = new LColor[color.length];
		for (int i = 0; i < color.length; i++) {
			colors[i] = new LColor(color[i]);
		}
		return colors;
	}

	public static final int[] convertToABGR(int pixelHeight, int pixelWidth, int[] srcPixels, int[] dstPixels) {
		int pixelCount = pixelWidth * pixelHeight;
		for (int i = 0; i < pixelCount; ++i) {
			int pixel = srcPixels[i];
			int r = (pixel & 0x00FF0000) >> 16;
			int g = (pixel & 0x0000FF00) >> 8;
			int b = (pixel & 0x000000FF);
			int a = (pixel & 0xFF000000) >> 24;
			dstPixels[i] = abgr(r, g, b, a);
		}
		return dstPixels;
	}

	public static final float[] convertHSBtoRGB(float hue, float saturation, float brightness) {
		float normalizedHue = ((hue % 360f) + 360f) % 360f;
		hue = normalizedHue / 360f;
		float r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = brightness;
		} else {
			float h = (hue - MathUtils.floor(hue)) * 6f;
			float f = h - MathUtils.floor(h);
			float p = brightness * (1f - saturation);
			float q = brightness * (1f - saturation * f);
			float t = brightness * (1f - (saturation * (1f - f)));
			switch ((int) h) {
			case 0:
				r = brightness;
				g = t;
				b = p;
				break;
			case 1:
				r = q;
				g = brightness;
				b = p;
				break;
			case 2:
				r = p;
				g = brightness;
				b = t;
				break;
			case 3:
				r = p;
				g = q;
				b = brightness;
				break;
			case 4:
				r = t;
				g = p;
				b = brightness;
				break;
			case 5:
				r = brightness;
				g = p;
				b = q;
				break;
			}
		}
		float[] result = new float[3];
		result[0] = r;
		result[1] = g;
		result[2] = b;
		return result;
	}

	public static final boolean isSimilarRGB(float srcR, float srcG, float srcB, float dstR, float dstG, float dstB,
			float similarOffset) {
		final float newR = (srcR - dstR);
		final float newG = (srcG - dstG);
		final float newB = (srcB - dstB);
		final float v = newR * newR + newG * newG + newB * newB;
		return (MathUtils.sqrt(v) < similarOffset);
	}

	public static final boolean isSimilarRGB(int srcR, int srcG, int srcB, int dstR, int dstG, int dstB,
			int similarOffset) {
		final int newR = (srcR - dstR);
		final int newG = (srcG - dstG);
		final int newB = (srcB - dstB);
		final int v = newR * newR + newG * newG + newB * newB;
		return (MathUtils.sqrtInt(v) < similarOffset);
	}

	public static final boolean isSimilarRGB(int srcColor, int dstColor, int similarOffset) {
		if (srcColor == dstColor) {
			return true;
		}
		final int[] srcColors = getRGBs(srcColor);
		final int[] dstColors = getRGBs(dstColor);
		return isSimilarRGB(srcColors[0], srcColors[1], srcColors[2], dstColors[0], dstColors[1], dstColors[2],
				similarOffset);
	}

	public static final boolean isSimilarRGB(LColor srcColor, LColor dstColor, float similarOffset) {
		if (srcColor == null || dstColor == null) {
			return false;
		}
		if (srcColor == dstColor || srcColor.equals(dstColor)) {
			return true;
		}
		return isSimilarRGB(srcColor.r, srcColor.g, srcColor.b, dstColor.r, dstColor.g, dstColor.b, similarOffset);
	}

	public static final boolean isSimilarRGB(String srcColor, String dstColor, float similarOffset) {
		if (srcColor == null || dstColor == null) {
			return false;
		}
		if (srcColor == dstColor || srcColor.equals(dstColor)) {
			return true;
		}
		return isSimilarRGB(new LColor(srcColor), new LColor(dstColor), similarOffset);
	}

	/**
	 * 转换颜色为ARGB格式的Color
	 * 
	 * @param a
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public final static LColor fromARGB(float a, float r, float g, float b) {
		return new LColor(r, g, b, a);
	}

	/**
	 * 转换颜色为RGBA格式的Color
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @return
	 */
	public final static LColor fromRGBA(float r, float g, float b, float a) {
		return new LColor(r, g, b, a);
	}

	/**
	 * 转换颜色为RGB格式的Color
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public final static LColor fromRGB(float r, float g, float b) {
		return new LColor(r, g, b);
	}

	/**
	 * 转换颜色为RGB格式的Color
	 * 
	 * @param value
	 * @return
	 */
	public static final LColor fromRGB(int value) {
		return new LColor(((value >> 16) & 0xFF), ((value >> 8) & 0xFF), (value & 0xFF), 255);
	}

	/**
	 * 转换颜色为RGBA格式的Color
	 * 
	 * @param value
	 * @return
	 */
	public static final LColor fromRGBA(int value) {
		return new LColor(((value >> 16) & 0xFF), ((value >> 8) & 0xFF), (value & 0xFF), ((value >> 24) & 0xFF));
	}

	/**
	 * 解码指定颜色信息为argb颜色
	 * 
	 * @param colorString
	 * @return
	 */
	public static final int parseColor(String colorString) {
		return decode(colorString).getARGB();
	}

	/**
	 * 解码字符串为color对象
	 * 
	 * @param colorString
	 * @return
	 */
	public static final LColor decode(String colorString) {
		return new LColor(colorString);
	}

	/**
	 * 解码字符串为color对象
	 * 
	 * @param colorString
	 * @return
	 */
	public static final LColor valueOf(String colorString) {
		return new LColor(colorString);
	}

	/**
	 * 转化像素为浮点的r,g,b,a数组
	 * 
	 * @param pixel
	 * @return
	 */
	public static final float[] toRGBA(int pixel) {
		int r = (pixel & 0x00FF0000) >> 16;
		int g = (pixel & 0x0000FF00) >> 8;
		int b = (pixel & 0x000000FF);
		int a = (pixel & 0xFF000000) >> 24;
		if (a < 0) {
			a += 256;
		}
		return new float[] { r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f };
	}

	/**
	 * 混合两种不同的色彩
	 * 
	 * @param curColor
	 * @param dstColor
	 * @return
	 */
	public static final int combine(int curColor, int dstColor) {
		int newA = ((((curColor >> 24) & 0xFF) * (((dstColor >> 24) & 0xFF) + 1)) & 0xFF00) << 16;
		if ((dstColor & 0xFFFFFF) == 0xFFFFFF) {
			return newA | (curColor & 0xFFFFFF);
		}
		int newR = ((((curColor >> 16) & 0xFF) * (((dstColor >> 16) & 0xFF) + 1)) & 0xFF00) << 8;
		int newG = (((curColor >> 8) & 0xFF) * (((dstColor >> 8) & 0xFF) + 1)) & 0xFF00;
		int newB = (((curColor & 0xFF) * ((dstColor & 0xFF) + 1)) >> 8) & 0xFF;
		return newA | newR | newG | newB;
	}

	/**
	 * 混合两种不同的色彩
	 * 
	 * @param curColor
	 * @param dstColor
	 * @return
	 */
	public static final int combine(LColor curColor, LColor dstColor) {
		if (curColor == null && dstColor == null) {
			return TRANSPARENT;
		}
		if (curColor != null && dstColor == null) {
			return curColor.getARGB();
		}
		if (curColor == null && dstColor != null) {
			return dstColor.getARGB();
		}
		return combine(curColor.getARGB(), dstColor.getARGB());
	}

	/**
	 * 获得RGB565格式数据
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final int rgb565(float r, float g, float b) {
		return ((int) (r * 31) << 11) | ((int) (g * 63) << 5) | (int) (b * 31);
	}

	/**
	 * 获得RGBA4444格式数据
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @return
	 */
	public static final int rgba4444(float r, float g, float b, float a) {
		return ((int) (r * 15) << 12) | ((int) (g * 15) << 8) | ((int) (b * 15) << 4) | (int) (a * 15);
	}

	/**
	 * 获得RGB888格式数据
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final int rgb888(float r, float g, float b) {
		return ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
	}

	/**
	 * 获得RGBA8888格式数据
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @return
	 */
	public static final int rgba8888(float r, float g, float b, float a) {
		return ((int) (r * 255) << 24) | ((int) (g * 255) << 16) | ((int) (b * 255) << 8) | (int) (a * 255);
	}

	/**
	 * 获得ARGB格式数据
	 * 
	 * @param a
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final int argb(float a, float r, float g, float b) {
		int alpha = (int) (a * 255);
		int red = (int) (r * 255);
		int green = (int) (g * 255);
		int blue = (int) (b * 255);
		return argb(alpha, red, green, blue);
	}

	public static final int abgr(float a, float r, float g, float b) {
		int alpha = (int) (a * 255);
		int red = (int) (r * 255);
		int green = (int) (g * 255);
		int blue = (int) (b * 255);
		return abgr(alpha, red, green, blue);
	}

	public static final int bgra(float a, float r, float g, float b) {
		int alpha = (int) (a * 255);
		int red = (int) (r * 255);
		int green = (int) (g * 255);
		int blue = (int) (b * 255);
		return bgra(alpha, red, green, blue);
	}

	public static final int rgba(float a, float r, float g, float b) {
		int alpha = (int) (a * 255);
		int red = (int) (r * 255);
		int green = (int) (g * 255);
		int blue = (int) (b * 255);
		return rgba(alpha, red, green, blue);
	}

	/**
	 * 获得ARGB格式数据
	 * 
	 * @param a
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final int argb(int a, int r, int g, int b) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * 获得ABGR格式数据
	 * 
	 * @param a
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final int abgr(int a, int r, int g, int b) {
		return (a << 24) | (b << 16) | (g << 8) | r;
	}

	/**
	 * 获得BGRA格式数据
	 * 
	 * @param a
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final int bgra(int a, int r, int g, int b) {
		return (b << 24) | (g << 16) | (r << 8) | a;
	}

	/**
	 * 获得RGBA格式数据
	 * 
	 * @param a
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final int rgba(int a, int r, int g, int b) {
		return (r << 24) | (g << 16) | (b << 8) | a;
	}

	public static final int rgb(int r, int g, int b) {
		return argb(0xFF, r, g, b);
	}

	public static final int bgr(int r, int g, int b) {
		return argb(0xFF, r, g, b);
	}

	public static final int alpha(int color, float a) {
		if (a < 0f) {
			a = 0f;
		} else if (a > 1f) {
			a = 1f;
		}
		int ialpha = (int) (0xFF * MathUtils.clamp(a, 0, 1f));
		return (ialpha << 24) | (color & 0xFFFFFF);
	}

	public static final int alpha(int color) {
		return (color >> 24) & 0xFF;
	}

	public static final int red(int color) {
		return (color >> 16) & 0xFF;
	}

	public static final int green(int color) {
		return (color >> 8) & 0xFF;
	}

	public static final int blue(int color) {
		return color & 0xFF;
	}

	public static final int withAlpha(int color, int alpha) {
		return (color & 0x00ffffff) | (alpha << 24);
	}

	private static final int convertInt(String v) {
		if (!MathUtils.isNan(v)) {
			return 0;
		}
		if (v.indexOf(LSystem.DOT) == -1) {
			return Integer.parseInt(v);
		}
		return (int) Double.parseDouble(v);
	}

	public static final float encode(float upper, float lower) {
		int upquant = (int) (upper * 255), lowquant = (int) (lower * 255);
		return (float) (upquant * 256 + lowquant);
	}

	public static final float decodeUpper(float encoded) {
		float lower = encoded % 256;
		return (encoded - lower) / 255;
	}

	public static final float decodeLower(float encoded) {
		return (encoded % 256) / 255;
	}

	public static final float[] withAlpha(float[] color, float alpha) {
		float r = color[0];
		float g = color[1];
		float b = color[2];
		float a = alpha;
		return new float[] { r, g, b, a };
	}

	public static final LColor hsb(float hue, float saturation, float brightness, float alpha) {
		float[] rgb = convertHSBtoRGB(hue, saturation, brightness);
		return new LColor(rgb[0], rgb[1], rgb[2], alpha);
	}

	private final static float added(float c, final float i) {
		c += i;
		if (c > 1f) {
			c = 1f;
		} else if (c < 0f) {
			c = 0f;
		}
		return c;
	}

	public static final byte[] argbToRGBA(int pixel) {
		byte[] bytes = new byte[4];
		int r = (pixel >> 16) & 0xFF;
		int g = (pixel >> 8) & 0xFF;
		int b = (pixel >> 0) & 0xFF;
		int a = (pixel >> 24) & 0xFF;
		bytes[0] = (byte) r;
		bytes[1] = (byte) g;
		bytes[2] = (byte) b;
		bytes[3] = (byte) a;
		return bytes;
	}

	/**
	 * 将图片用ARGB格式转化为贴图用RGBA格式
	 * 
	 * @param pixels
	 * @return
	 */
	public static final byte[] argbToRGBA(int[] pixels) {
		int size = pixels.length;
		byte[] bytes = new byte[size * 4];
		int p, r, g, b, a;
		int j = 0;
		for (int i = 0; i < size; i++) {
			p = pixels[i];
			a = (p >> 24) & 0xFF;
			r = (p >> 16) & 0xFF;
			g = (p >> 8) & 0xFF;
			b = (p >> 0) & 0xFF;
			bytes[j + 0] = (byte) r;
			bytes[j + 1] = (byte) g;
			bytes[j + 2] = (byte) b;
			bytes[j + 3] = (byte) a;
			j += 4;
		}
		return bytes;
	}

	/**
	 * 将图片用ARGB格式转化为贴图用RGB格式
	 * 
	 * @param pixels
	 * @return
	 */
	public static final byte[] argbToRGB(int[] pixels) {
		int size = pixels.length;
		byte[] bytes = new byte[size * 3];
		int p, r, g, b;
		int j = 0;
		for (int i = 0; i < size; i++) {
			p = pixels[i];
			r = (p >> 16) & 0xFF;
			g = (p >> 8) & 0xFF;
			b = (p >> 0) & 0xFF;
			bytes[j + 0] = (byte) r;
			bytes[j + 1] = (byte) g;
			bytes[j + 2] = (byte) b;
			j += 3;
		}
		return bytes;
	}

	public static final float linearToGamma(float v) {
		if (v <= 0.0) {
			return 0f;
		} else if (v <= 0.0031308f) {
			return 12.92f * v;
		} else if (v <= 1f) {
			return 1.055f * MathUtils.pow(v, 0.41666f) - 0.055f;
		} else {
			return MathUtils.pow(v, 0.41666f);
		}
	}

	public static final float gammaToLinear(float v) {
		if (v <= 0.04045f)
			return v / 12.92f;
		else if (v < 1f) {
			return MathUtils.pow((v + 0.055f) / 1.055f, 2.4f);
		} else {
			return MathUtils.pow(v, 2.4f);
		}
	}

	public static final int getColorARGBInt(LColor color) {
		if (color == null) {
			return LColor.white.getARGB();
		}
		return color.getARGB();
	}

	public static final int getColorABGRInt(LColor color) {
		if (color == null) {
			return LColor.white.getABGR();
		}
		return color.getABGR();
	}

	public static final int getColorRGBInt(LColor color) {
		if (color == null) {
			return LColor.white.getRGB();
		}
		return color.getRGB();
	}

	public static final int getColorBGRInt(LColor color) {
		if (color == null) {
			return LColor.white.getBGR();
		}
		return color.getBGR();
	}

	public static final LColor toBlackWhite(LColor color) {
		if (color == null) {
			return gray.cpy();
		}
		return toBlackWhite(color, new LColor());
	}

	public static final LColor toBlackWhite(LColor color, LColor targetColor) {
		if (color == null) {
			return gray.cpy();
		}
		if (targetColor == null) {
			targetColor = new LColor();
		}
		if ((color.r * 0.299f + color.g * 0.587f + color.b * 0.114f) >= 0.667f) {
			targetColor.r = 0f;
			targetColor.g = 0f;
			targetColor.b = 0f;
		} else {
			targetColor.r = 1f;
			targetColor.g = 1f;
			targetColor.b = 1f;
		}
		targetColor.a = color.a;
		return targetColor;
	}

	public static final LColor silver = new LColor(0xffc0c0c0, true);

	public static final LColor lightBlue = new LColor(0xffadd8e6, true);

	public static final LColor lightCoral = new LColor(0xfff08080, true);

	public static final LColor lightCyan = new LColor(0xffe0ffff, true);

	public static final LColor lightGoldenrodYellow = new LColor(0xfffafad2, true);

	public static final LColor lightGreen = new LColor(0xff90ee90, true);

	public static final LColor lightPink = new LColor(0xffffb6c1, true);

	public static final LColor lightSalmon = new LColor(0xffffa07a, true);

	public static final LColor lightSeaGreen = new LColor(0xff20b2aa, true);

	public static final LColor lightSkyBlue = new LColor(0xff87cefa, true);

	public static final LColor lightSlateGray = new LColor(0xff778899, true);

	public static final LColor lightSteelBlue = new LColor(0xffb0c4de, true);

	public static final LColor lightYellow = new LColor(0xffffffe0, true);

	public static final LColor lime = new LColor(0xff00ff00, true);

	public static final LColor limeGreen = new LColor(0xff32cd32, true);

	public static final LColor linen = new LColor(0xfffaf0e6, true);

	public static final LColor maroon = new LColor(0xff800000, true);

	public static final LColor mediumAquamarine = new LColor(0xff66cdaa, true);

	public static final LColor mediumBlue = new LColor(0xff0000cd, true);

	public static final LColor purple = new LColor(0xff800080, true);

	public static final LColor wheat = new LColor(0xfff5deb3, true);

	public static final LColor gold = new LColor(0xffffd700, true);

	public static final LColor white = new LColor(1.0f, 1.0f, 1.0f, 1.0f, true);

	public static final LColor transparent = white;

	public static final LColor yellow = new LColor(1.0f, 1.0f, 0.0f, 1.0f, true);

	public static final LColor red = new LColor(1.0f, 0.0f, 0.0f, 1.0f, true);

	public static final LColor blue = new LColor(0.0f, 0.0f, 1.0f, 1.0f, true);

	public static final LColor cornFlowerBlue = new LColor(0.4f, 0.6f, 0.9f, 1.0f, true);

	public static final LColor green = new LColor(0.0f, 1.0f, 0.0f, 1.0f, true);

	public static final LColor black = new LColor(0.0f, 0.0f, 0.0f, 1.0f, true);

	public static final LColor gray = new LColor(0.5f, 0.5f, 0.5f, 1.0f, true);

	public static final LColor cyan = new LColor(0.0f, 1.0f, 1.0f, 1.0f, true);

	public static final LColor darkGray = new LColor(0.3f, 0.3f, 0.3f, 1.0f, true);

	public static final LColor lightGray = new LColor(0.7f, 0.7f, 0.7f, 1.0f, true);

	public static final LColor pink = new LColor(1.0f, 0.7f, 0.7f, 1.0f, true);

	public static final LColor orange = new LColor(1.0f, 0.8f, 0.0f, 1.0f, true);

	public static final LColor magenta = new LColor(1.0f, 0.0f, 1.0f, 1.0f, true);

	public float r = 0.0f;

	public float g = 0.0f;

	public float b = 0.0f;

	public float a = 1.0f;

	private float _olda = -1f;

	private float _oldr = -1f;

	private float _oldg = -1f;

	private float _oldb = -1f;

	private float _colorFloat = -1f;

	private int _argbInt = -1;

	private int _abgrInt = -1;

	private int _rgbInt = -1;

	private int _bgrInt = -1;

	private int _colorInt = -1;

	private boolean _locked = false;

	public static final LColor newWhite() {
		return white.cpy();
	}

	public static final LColor newBlack() {
		return black.cpy();
	}

	public static final LColor newRed() {
		return red.cpy();
	}

	public static final LColor newGreen() {
		return green.cpy();
	}

	public static final LColor newBlue() {
		return blue.cpy();
	}

	public static final LColor newYellow() {
		return yellow.cpy();
	}

	public LColor(String c) {
		this(c, false);
	}

	/**
	 * 转换字符串为color
	 * 
	 * @param c
	 */
	public LColor(String c, boolean locked) {
		if (StringUtils.isEmpty(c)) {
			setColor(1.0f, 1.0f, 1.0f, 1.0f);
			this._locked = locked;
			return;
		}
		this.setColor(c);
		this.initLockData(locked);
	}

	public LColor() {
		this(1.0f, 1.0f, 1.0f, 1.0f, false);
	}

	public LColor(boolean locked) {
		this(1.0f, 1.0f, 1.0f, 1.0f, locked);
	}

	public LColor(LColor color) {
		this(color, false);
	}

	public LColor(LColor color, boolean locked) {
		if (color == null) {
			this.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			this._locked = locked;
			return;
		}
		this.setColor(color.r, color.g, color.b, color.a);
		this.initLockData(locked);
	}

	public LColor(int r, int g, int b) {
		this(r, g, b, false);
	}

	public LColor(int r, int g, int b, boolean locked) {
		this.setColor(r, g, b);
		this.initLockData(locked);
	}

	public LColor(int r, int g, int b, int a) {
		this(r, g, b, a, false);
	}

	public LColor(int r, int g, int b, int a, boolean locked) {
		this.setColor(r, g, b, a);
		this.initLockData(locked);
	}

	public LColor(float r, float g, float b) {
		this(r, g, b, false);

	}

	public LColor(float r, float g, float b, boolean locked) {
		this.setColor(r, g, b);
		this.initLockData(locked);
	}

	public LColor(float r, float g, float b, float a) {
		this(r, g, b, a, false);

	}

	public LColor(float r, float g, float b, float a, boolean locked) {
		this.setColor(r, g, b, a);
		this.initLockData(locked);
	}

	public LColor(int pixel) {
		this(pixel, false);
	}

	public LColor(int pixel, boolean locked) {
		int r = (pixel & 0x00FF0000) >> 16;
		int g = (pixel & 0x0000FF00) >> 8;
		int b = (pixel & 0x000000FF);
		int a = (pixel & 0xFF000000) >> 24;
		if (a < 0) {
			a += 256;
		}
		if (a == 0) {
			a = 255;
		}
		this.setColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
		this.initLockData(locked);
	}

	public boolean isColorLocked() {
		return _locked;
	}

	public LColor reset() {
		return setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private void initLockData(boolean locked) {
		this._locked = locked;
		if (_locked) {
			final int a = getAlpha();
			final int r = getRed();
			final int g = getGreen();
			final int b = getBlue();
			this._argbInt = argb(a, r, g, b);
			this._abgrInt = abgr(a, r, g, b);
			this._rgbInt = rgb(r, g, b);
			this._bgrInt = bgr(r, g, b);
			this._colorFloat = getFloatBits();
			this._colorInt = getIntBits();
		}
	}

	public LColor darker() {
		return darker(0.5f);
	}

	public LColor darker(float scale) {
		scale = 1f - MathUtils.clamp(scale, 0f, 1f);
		LColor temp = new LColor(r * scale, g * scale, b * scale, a);
		return temp;
	}

	public LColor lighter() {
		return lighter(0.5f);
	}

	public LColor lighter(float scale) {
		scale = MathUtils.clamp(scale, 0f, 1f);
		float newRed = MathUtils.clamp(this.r + (1f - this.r) * scale, 0f, 1f);
		float newGreen = MathUtils.clamp(this.g + (1f - this.g) * scale, 0f, 1f);
		float newBlue = MathUtils.clamp(this.b + (1f - b) * scale, 0f, 1f);
		return new LColor(newRed, newGreen, newBlue, a);
	}

	public LColor brighter() {
		return brighter(0.2f);
	}

	public LColor brighter(float scale) {
		scale = MathUtils.clamp(scale, 0f, 1f) + 1f;
		LColor temp = new LColor(r * scale, g * scale, b * scale, a);
		return temp;
	}

	public LColor blackWhiteColor() {
		return brightness() > 0.5f ? LColor.black : LColor.white;
	}

	public float brightness() {
		return (MathUtils.sqrt(0.299f * MathUtils.pow(this.r, 2f) + 0.587f * MathUtils.pow(this.g, 2f)
				+ 0.114f * MathUtils.pow(this.b, 2f)));
	}

	public LColor setColorValue(int r, int g, int b, int a) {
		if (_locked) {
			return this;
		}
		this.r = r > 1 ? (float) r / 255f : r;
		this.g = g > 1 ? (float) g / 255f : g;
		this.b = b > 1 ? (float) b / 255f : b;
		this.a = a > 1 ? (float) a / 255f : a;
		return this;
	}

	public LColor setIntColor(int r, int g, int b, int a) {
		if (_locked) {
			return this;
		}
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	public LColor setColor(float r, float g, float b, float a) {
		if (_locked) {
			return this;
		}
		this.r = r > 1f ? r / 255f : r;
		this.g = g > 1f ? g / 255f : g;
		this.b = b > 1f ? b / 255f : b;
		this.a = a > 1f ? a / 255f : a;
		return this;
	}

	public LColor setFloatColor(float r, float g, float b) {
		return setFloatColor(r, g, b, 1f);
	}

	public LColor setFloatColor(float r, float g, float b, float a) {
		if (_locked) {
			return this;
		}
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	public LColor setColor(float r, float g, float b) {
		return setColor(r, g, b, b > 1 ? 255 : 1.0f);
	}

	public LColor setColor(int r, int g, int b, int a) {
		if (_locked) {
			return this;
		}
		this.r = (float) r / 255;
		this.g = (float) g / 255;
		this.b = (float) b / 255;
		this.a = (float) a / 255;
		return this;
	}

	public LColor setColor(int r, int g, int b) {
		return setColor(r, g, b, 255);
	}

	public LColor setColor(LColor color) {
		if (color == null || color == this) {
			return this;
		}
		return setColor(color.r, color.g, color.b, color.a);
	}

	public LColor setColor(LColor color, float alpha) {
		if (color == null) {
			if (this.a == alpha) {
				return this;
			} else {
				return setColor(this.r, this.g, this.b, alpha);
			}
		} else if (color == this && this.a == alpha) {
			return this;
		}
		return setColor(color.r, color.g, color.b, alpha);
	}

	public LColor setColor(int pixel) {
		return setColorARGB(pixel);
	}

	public LColor setColorARGB(int pixel) {
		if (_locked) {
			return this;
		}
		int r = (pixel & 0x00FF0000) >> 16;
		int g = (pixel & 0x0000FF00) >> 8;
		int b = (pixel & 0x000000FF);
		int a = (pixel & 0xFF000000) >> 24;
		if (a < 0) {
			a += 256;
		}
		return setColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}

	public LColor setColorRGB(int pixel) {
		if (_locked) {
			return this;
		}
		int r = (pixel & 0x00FF0000) >> 16;
		int g = (pixel & 0x0000FF00) >> 8;
		int b = (pixel & 0x000000FF);
		return setColor(r / 255.0f, g / 255.0f, b / 255.0f, 1f);
	}

	public LColor setCMYK(float cyan, float magenta, float yellow, float black, float alpha) {
		float red = (1f - cyan) * (1f - black);
		float green = (1f - magenta) * (1f - black);
		float blue = (1f - yellow) * (1f - black);
		return setColor(red, green, blue, alpha);
	}

	public LColor setCMYK(float cyan, float magenta, float yellow, float black) {
		return setCMYK(cyan, magenta, yellow, black, this.a);
	}

	public LColor setHSB(float hue, float saturation, float brightness) {
		return setHSB(hue, saturation, brightness, this.a);
	}

	public LColor setHSB(float hue, float saturation, float brightness, float alpha) {
		float chroma = brightness * saturation;
		float match = brightness - chroma;
		return setHueChromaMatch(hue, chroma, match, alpha);
	}

	public LColor setHSL(float hue, float saturation, float lightness) {
		return setHSL(hue, saturation, lightness, this.a);
	}

	public LColor setHSL(float hue, float saturation, float lightness, float alpha) {
		float chroma = (1f - MathUtils.abs(2f * lightness - 1f)) * saturation;
		float match = lightness - chroma / 2f;
		return setHueChromaMatch(hue, chroma, match, alpha);
	}

	public LColor setHueChromaMatch(float hue, float chroma, float match, float alpha) {
		hue %= 360f;
		float hueD = hue / 60f;
		float mid = chroma * (1 - MathUtils.abs(hueD % 2 - 1)) + match;
		chroma += match;
		int t = (int) hueD;
		switch (t) {
		case 0:
			setColor(chroma, mid, match, alpha);
		case 1:
			setColor(mid, chroma, match, alpha);
		case 2:
			setColor(match, chroma, mid, alpha);
		case 3:
			setColor(match, mid, chroma, alpha);
		case 4:
			setColor(mid, match, chroma, alpha);
		case 5:
			setColor(chroma, match, mid, alpha);
		}
		return this;
	}

	public float red() {
		return r;
	}

	public float green() {
		return g;
	}

	public float blue() {
		return b;
	}

	public float alpha() {
		return a;
	}

	public LColor getBlackWhite() {
		return toBlackWhite(this);
	}

	public int getRed() {
		return (int) (r * 255);
	}

	public int getGreen() {
		return (int) (g * 255);
	}

	public int getBlue() {
		return (int) (b * 255);
	}

	public int getAlpha() {
		return (int) (a * 255);
	}

	public LColor getHalfRGBA() {
		return new LColor(this).divSelf(2f);
	}

	public LColor getHalfRGB() {
		return new LColor(this.r, this.g, this.b).divSelf(2f);
	}

	public LColor setAll(float c) {
		return setColor(c, c, c, c);
	}

	public LColor setAll(int c) {
		return setColor(c, c, c, c);
	}

	public LColor setAlpha(float alpha) {
		if (_locked) {
			return this;
		}
		this.a = alpha;
		return this;
	}

	public boolean addRed(final float red) {
		if (_locked) {
			return false;
		}
		final float n = added(this.r, red);
		if (n == this.r) {
			return false;
		}
		this.r = n;
		return true;
	}

	public boolean addGreen(final float green) {
		if (_locked) {
			return false;
		}
		final float n = added(this.g, green);
		if (n == this.g) {
			return false;
		}
		this.g = n;
		return true;
	}

	public boolean addBlue(final float blue) {
		if (_locked) {
			return false;
		}
		final float n = added(this.b, blue);
		if (n == this.b) {
			return false;
		}
		this.b = n;
		return true;
	}

	public boolean addAlpha(final float alpha) {
		if (_locked) {
			return false;
		}
		final float n = added(this.a, alpha);
		if (n == this.a) {
			return false;
		}
		this.a = n;
		return true;
	}

	/**
	 * 让当前色彩做加法运算(将产生数值赋予自身)
	 * 
	 * @param v
	 * @return
	 */
	public LColor addSelf(float v) {
		if (_locked) {
			return this;
		}
		this.r += v;
		this.g += v;
		this.b += v;
		this.a += v;
		return this;
	}

	/**
	 * 让当前色彩做加法运算(将产生数值赋予自身)
	 * 
	 * @param c
	 * @return
	 */
	public LColor addSelf(LColor c) {
		if (_locked) {
			return this;
		}
		if (c == null) {
			return this;
		}
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		this.a += c.a;
		return this;
	}

	/**
	 * 让当前色彩做减法运算(将产生数值赋予自身)
	 * 
	 * @param v
	 * @return
	 */
	public LColor subSelf(float v) {
		if (_locked) {
			return this;
		}
		this.r -= v;
		this.g -= v;
		this.b -= v;
		this.a -= v;
		return this;
	}

	/**
	 * 让当前色彩做减法运算(将产生数值赋予自身)
	 * 
	 * @param c
	 * @return
	 */
	public LColor subSelf(LColor c) {
		if (_locked) {
			return this;
		}
		if (c == null) {
			return this;
		}
		this.r -= c.r;
		this.g -= c.g;
		this.b -= c.b;
		this.a -= c.a;
		return this;
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值赋予自身)
	 * 
	 * @param v
	 * @return
	 */
	public LColor mulSelf(float v) {
		if (_locked) {
			return this;
		}
		this.r *= v;
		this.g *= v;
		this.b *= v;
		this.a *= v;
		return this;
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值赋予自身)
	 * 
	 * @param c
	 * @return
	 */
	public LColor mulSelf(LColor c) {
		if (_locked) {
			return this;
		}
		if (c == null) {
			return this;
		}
		this.r *= c.r;
		this.g *= c.g;
		this.b *= c.b;
		this.a *= c.a;
		return this;
	}

	public LColor mulSelfAlpha(float a) {
		if (_locked) {
			return this;
		}
		this.a *= a;
		return this;
	}

	public LColor mulSelfAlpha(LColor c) {
		if (c == null) {
			return this;
		}
		return mulSelfAlpha(c.a);
	}

	/**
	 * 让当前色彩做除法运算(将产生数值赋予自身)
	 * 
	 * @param v
	 * @return
	 */
	public LColor divSelf(float v) {
		if (_locked) {
			return this;
		}
		this.r /= v;
		this.g /= v;
		this.b /= v;
		this.a /= v;
		return this;
	}

	/**
	 * 让当前色彩做除法运算(将产生数值赋予自身)
	 * 
	 * @param c
	 * @return
	 */
	public LColor divSelf(LColor c) {
		if (_locked) {
			return this;
		}
		if (c == null) {
			return this;
		}
		this.r /= c.r;
		this.g /= c.g;
		this.b /= c.b;
		this.a /= c.a;
		return this;
	}

	public LColor divSelfAlpha(float a) {
		if (_locked) {
			return this;
		}
		if (a <= 0) {
			a = 0.01f;
		}
		this.a /= a;
		return this;
	}

	public LColor divSelfAlpha(LColor c) {
		return divSelfAlpha(c.a);
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor mul(float v) {
		return multiply(v);
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor mul(int v) {
		return multiply(v);
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @return
	 */
	public LColor mul(LColor c) {
		return multiply(c);
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor multiply(float v) {
		return multiply(v, new LColor());
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @param o
	 * @return
	 */
	public LColor multiply(float v, LColor o) {
		if (o == null) {
			o = new LColor(r * v, g * v, b * v, a * v);
		} else {
			o.setColor(r * v, g * v, b * v, a * v);
		}
		return o;
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor multiply(int v) {
		return multiply(v, new LColor());
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @param o
	 * @return
	 */
	public LColor multiply(int v, LColor o) {
		if (o == null) {
			o = new LColor(v);
		} else {
			o.setColor(v);
		}
		return o;
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @return
	 */
	public LColor multiply(LColor c) {
		return multiply(c, new LColor());
	}

	/**
	 * 让当前色彩做乘法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @param o
	 * @return
	 */
	public LColor multiply(LColor c, LColor o) {
		if (c == null) {
			return cpy();
		}
		if (o == null) {
			o = new LColor(r * c.r, g * c.g, b * c.b, a * c.a);
		} else {
			o.setColor(r * c.r, g * c.g, b * c.b, a * c.a);
		}
		return o;
	}

	/**
	 * 获得rgb中最大数值
	 * 
	 * @return
	 */
	public float maxRGB() {
		return MathUtils.max(r, MathUtils.max(g, b));
	}

	/**
	 * 获得rgb中最小数值
	 * 
	 * @return
	 */
	public float minRGB() {
		return MathUtils.min(r, MathUtils.min(g, b));
	}

	/**
	 * 让当前色彩做除法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor div(float v) {
		return divide(v);
	}

	/**
	 * 让当前色彩做除法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor div(int v) {
		return divide(v);
	}

	/**
	 * 让当前色彩做除法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @return
	 */
	public LColor div(LColor c) {
		return divide(c);
	}

	/**
	 * 让当前色彩做除法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor divide(float v) {
		return new LColor(r / v, g / v, b / v, a / v);
	}

	/**
	 * 让当前色彩做除法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor divide(int v) {
		return new LColor(v);
	}

	/**
	 * 让当前色彩做除法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @return
	 */
	public LColor divide(LColor c) {
		if (c == null) {
			return cpy();
		}
		return new LColor(r / c.r, g / c.g, b / c.b, a / c.a);
	}

	/**
	 * 让当前色彩做加法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor add(float v) {
		return addition(v);
	}

	/**
	 * 让当前色彩做加法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor add(int v) {
		return addition(v);
	}

	/**
	 * 让当前色彩做加法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @return
	 */
	public LColor add(LColor c) {
		return addition(c);
	}

	/**
	 * 让当前色彩做加法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor addition(float v) {
		return new LColor(r + v, g + v, b + v, a + v);
	}

	/**
	 * 让当前色彩做加法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor addition(int v) {
		return new LColor(v);
	}

	/**
	 * 让当前色彩做加法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @return
	 */
	public LColor addition(LColor c) {
		if (c == null) {
			return cpy();
		}
		return new LColor(r + c.r, g + c.g, b + c.b, a + c.a);
	}

	/**
	 * 让当前色彩做减法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor sub(float v) {
		return subtraction(v);
	}

	/**
	 * 让当前色彩做减法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor sub(int v) {
		return subtraction(v);
	}

	/**
	 * 让当前色彩做减法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @return
	 */
	public LColor sub(LColor c) {
		return subtraction(c);
	}

	/**
	 * 让当前色彩做减法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor subtraction(float v) {
		return new LColor(r - v, g - v, b - v, a - v);
	}

	/**
	 * 让当前色彩做减法运算(将产生数值构建为新的Color)
	 * 
	 * @param v
	 * @return
	 */
	public LColor subtraction(int v) {
		return new LColor(v);
	}

	/**
	 * 让当前色彩做减法运算(将产生数值构建为新的Color)
	 * 
	 * @param c
	 * @return
	 */
	public LColor subtraction(LColor c) {
		if (c == null) {
			return cpy();
		}
		return new LColor(r - c.r, g - c.g, b - c.b, a - c.a);
	}

	/**
	 * 获得指定色彩比较当前色彩的浅色版本
	 * 
	 * @param c
	 * @return
	 */
	public LColor screen(LColor c) {
		if (c == null) {
			return cpy();
		}
		LColor c1 = c.invert();
		LColor c2 = c.invert();
		return c1.multiply(c2).invert();
	}

	/**
	 * 获得指定色彩对比当前色彩的平均值
	 * 
	 * @param c
	 * @return
	 */
	public LColor average(LColor c) {
		if (c == null) {
			return cpy();
		}
		final float newR = (c.r + this.r) / 2f;
		final float newG = (c.g + this.g) / 2f;
		final float newB = (c.b + this.b) / 2f;
		final float newA = (c.a + this.a) / 2f;
		return new LColor(newR, newG, newB, newA);
	}

	/**
	 * 反转当前色彩的颜色
	 * 
	 * @return
	 */
	public LColor invert() {
		return new LColor(1f - this.r, 1f - this.g, 1f - this.b, 1f - this.a);
	}

	/**
	 * 直接复制一个Color
	 * 
	 * @return
	 */
	public LColor cpy() {
		return new LColor(r, g, b, a);
	}

	/**
	 * 获得像素相加的Color
	 * 
	 * @param c
	 * @return
	 */
	public LColor addCopy(LColor c) {
		return addition(c);
	}

	/**
	 * 获得像素相减的Color
	 * 
	 * @param c
	 * @return
	 */
	public LColor subCopy(LColor c) {
		return subtraction(c);
	}

	/**
	 * 获得像素相除的Color
	 * 
	 * @param c
	 * @return
	 */
	public LColor divCopy(LColor c) {
		return divide(c);
	}

	/**
	 * 获得像素相乘的Color
	 * 
	 * @param c
	 * @return
	 */
	public LColor mulCopy(LColor c) {
		return multiply(c);
	}

	public LColor lerp(LColor target, float alpha) {
		return lerp(this, target, alpha);
	}

	public LColor interpolate(int endColor) {
		return interpolate(endColor, 1f);
	}

	public LColor interpolate(int endColor, float r) {
		return new LColor(interpolate(getARGB(), endColor, r));
	}

	public LColor interpolate(LColor endColor) {
		return interpolate(endColor, 1f);
	}

	public LColor interpolate(LColor endColor, float r) {
		return interpolate(endColor == null ? LColor.white.getARGB() : endColor.getARGB(), r);
	}

	public float toFloatBits() {
		if (_locked) {
			return _colorFloat;
		}
		if (a == 1f && r == 1f && g == 1f && b == 1f) {
			return -1.7014117E38f;
		}
		if (a == 0f && r == 0f && g == 0f && b == 0f) {
			return 0f;
		}
		if (_colorFloat == -1f || NumberUtils.compare(this._olda, a) != 0 || NumberUtils.compare(this._oldr, r) != 0
				|| (NumberUtils.compare(this._oldg, g) != 0 || NumberUtils.compare(this._oldb, b) != 0)) {
			this._olda = this.a;
			this._oldr = this.r;
			this._oldg = this.g;
			this._oldb = this.b;
			return _colorFloat = getFloatBits();
		}
		return _colorFloat;
	}

	private float getFloatBits() {
		return NumberUtils.intBitsToFloat(getIntBits() & 0xfeffffff);
	}

	public int toIntBits() {
		if (_locked) {
			return _colorInt;
		}
		if (a == 1f && r == 1f && g == 1f && b == 1f) {
			return -1;
		}
		if (a == 0f && r == 0f && g == 0f && b == 0f) {
			return 0;
		}
		if (_colorInt == -1 || NumberUtils.compare(this._olda, a) != 0 || NumberUtils.compare(this._oldr, r) != 0
				|| (NumberUtils.compare(this._oldg, g) != 0 || NumberUtils.compare(this._oldb, b) != 0)) {
			this._olda = this.a;
			this._oldr = this.r;
			this._oldg = this.g;
			this._oldb = this.b;
			return _colorInt = getIntBits();
		}
		return _colorInt;
	}

	private int getIntBits() {
		return ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
	}

	/**
	 * 返回ARGB
	 * 
	 * @return
	 */
	public int getARGB() {
		if (_locked) {
			return _argbInt;
		}
		if (a == 1f && r == 1f && g == 1f && b == 1f) {
			return -1;
		}
		if (a == 0f && r == 0f && g == 0f && b == 0f) {
			return 0;
		}
		if (_argbInt == -1 || NumberUtils.compare(this._olda, a) != 0 || NumberUtils.compare(this._oldr, r) != 0
				|| (NumberUtils.compare(this._oldg, g) != 0 || NumberUtils.compare(this._oldb, b) != 0)) {
			this._olda = this.a;
			this._oldr = this.r;
			this._oldg = this.g;
			this._oldb = this.b;
			return _argbInt = argb(getAlpha(), getRed(), getGreen(), getBlue());
		}
		return _argbInt;
	}

	/**
	 * 返回ABGR
	 * 
	 * @return
	 */
	public int getABGR() {
		if (_locked) {
			return _abgrInt;
		}
		if (a == 1f && r == 1f && g == 1f && b == 1f) {
			return -1;
		}
		if (a == 0f && r == 0f && g == 0f && b == 0f) {
			return 0;
		}
		if (_abgrInt == -1 || NumberUtils.compare(this._olda, a) != 0 || NumberUtils.compare(this._oldr, r) != 0
				|| (NumberUtils.compare(this._oldg, g) != 0 || NumberUtils.compare(this._oldb, b) != 0)) {
			this._olda = this.a;
			this._oldr = this.r;
			this._oldg = this.g;
			this._oldb = this.b;
			return _abgrInt = abgr(getAlpha(), getRed(), getGreen(), getBlue());
		}
		return _abgrInt;
	}

	/**
	 * 返回RGB
	 * 
	 * @return
	 */
	public int getRGB() {
		if (_locked) {
			return _rgbInt;
		}
		if (r == 1f && g == 1f && b == 1f) {
			return -1;
		}
		if (r == 0f && g == 0f && b == 0f) {
			return 0;
		}
		if (_rgbInt == -1 || NumberUtils.compare(this._oldr, r) != 0
				|| (NumberUtils.compare(this._oldg, g) != 0 || NumberUtils.compare(this._oldb, b) != 0)) {
			this._oldr = this.r;
			this._oldg = this.g;
			this._oldb = this.b;
			return _rgbInt = argb(getAlpha(), getRed(), getGreen(), getBlue());
		}
		return _rgbInt;
	}

	public int getBGR() {
		if (_locked) {
			return _bgrInt;
		}
		if (r == 1f && g == 1f && b == 1f) {
			return -1;
		}
		if (r == 0f && g == 0f && b == 0f) {
			return 0;
		}
		if (_bgrInt == -1 || NumberUtils.compare(this._oldr, r) != 0
				|| (NumberUtils.compare(this._oldg, g) != 0 || NumberUtils.compare(this._oldb, b) != 0)) {
			this._oldr = this.r;
			this._oldg = this.g;
			this._oldb = this.b;
			return _bgrInt = bgr(getRed(), getGreen(), getBlue());
		}
		return _bgrInt;
	}

	/**
	 * 返回ARGB
	 * 
	 * @param alpha
	 * @return
	 */
	public int getARGB(float alpha) {
		return argb((int) (a * alpha * 255), getRed(), getGreen(), getBlue());
	}

	public int getABGR(float alpha) {
		return abgr((int) (a * alpha * 255), getRed(), getGreen(), getBlue());
	}

	public float getMaxColor() {
		return MathUtils.max(this.r, MathUtils.max(this.g, this.b));
	}

	public float getMinColor() {
		return MathUtils.min(this.r, MathUtils.min(this.g, this.b));
	}

	public float[] toRgbFloatArray() {
		return new float[] { r, g, b };
	}

	public float[] toRgbaFloatArray() {
		return new float[] { r, g, b, a };
	}

	public int[] toRgbaIntArray() {
		return new int[] { getRed(), getGreen(), getBlue(), getAlpha() };
	}

	public int[] toRgbIntArray() {
		return new int[] { getRed(), getGreen(), getBlue() };
	}

	public byte[] toRgbaByteArray() {
		return new byte[] { (byte) getRed(), (byte) getGreen(), (byte) getBlue(), (byte) getAlpha() };
	}

	public byte[] toRgbByteArray() {
		return new byte[] { (byte) getRed(), (byte) getGreen(), (byte) getBlue() };
	}

	public String toCSS() {
		return "rgba(" + MathUtils.floor(r * 255) + "," + MathUtils.floor(g * 255) + "," + MathUtils.floor(b * 255)
				+ "," + MathUtils.floor(a * 255) + ")";
	}

	public Vector3f getVector3() {
		return new Vector3f(r, g, b);
	}

	public Vector4f getVector4() {
		return new Vector4f(r, g, b, a);
	}

	public Alpha getAlphaObject() {
		return new Alpha(a);
	}

	/**
	 * 按照特定百分比改变当前色彩，并返回一个新的LColor对象
	 * 
	 * @param percent 最大值为1f，最小值为0f
	 * @return
	 */
	public LColor percent(float percent) {
		if (percent < -1) {
			return new LColor(0, 0, 0, getAlpha());
		}
		if (percent > 1) {
			return new LColor(255, 255, 255, getAlpha());
		}
		if (percent < 0) {
			percent = 1 + percent;
			int r = MathUtils.max(0, MathUtils.min(255, (int) ((getRed() * percent) + 0.5)));
			int g = MathUtils.max(0, MathUtils.min(255, (int) ((getGreen() * percent) + 0.5)));
			int b = MathUtils.max(0, MathUtils.min(255, (int) ((getBlue() * percent) + 0.5)));
			return new LColor(r, g, b, getAlpha());
		} else if (percent > 0) {
			int r = MathUtils.max(0, MathUtils.min(255, (int) (((255 - getRed()) * percent) + getRed() + 0.5)));
			int g = MathUtils.max(0, MathUtils.min(255, (int) (((255 - getGreen()) * percent) + getGreen() + 0.5)));
			int b = MathUtils.max(0, MathUtils.min(255, (int) (((255 - getBlue()) * percent) + getBlue() + 0.5)));
			return new LColor(r, g, b, getAlpha());
		}
		return new LColor(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * 返回一个符合CIE标准的色彩Luminance(亮度)值
	 * 
	 * @return
	 */
	public float getLuminanceRGB() {
		return getLuminanceRGB(r, g, b);
	}

	/**
	 * 返回一个符合CIE标准的色彩Luminance(亮度)值
	 * 
	 * @return
	 */
	public LColor getLuminanceRGBColor() {
		return new LColor(0.2126f * r, 0.7152f * g, 0.0722f * b);
	}

	/**
	 * 获得当前色彩的HSL值
	 * 
	 * @return
	 */
	public LColor getRGBtoHSL() {
		return getRGBtoHSL(r, g, b);
	}

	/**
	 * 转化当前Color的HSL值为RGB值
	 * 
	 * @return
	 */
	public LColor getHSLtoRGB() {
		return getHSLtoRGB(r, g, b);
	}

	/**
	 * 判断当前Color与指定Color是否近似
	 * 
	 * @param c
	 * @param offset
	 * @return
	 */
	public boolean isSimilarRGB(LColor dstColor, float offset) {
		if (dstColor == null) {
			return false;
		}
		return isSimilarRGB(this.r, this.g, this.b, dstColor.r, dstColor.g, dstColor.b, offset);
	}

	/**
	 * 判断当前Color与指定Color是否近似
	 * 
	 * @param dstColor
	 * @param offset
	 * @return
	 */
	public boolean isSimilarRGB(int dstColor, int offset) {
		final int[] colors = getRGBs(dstColor);
		return isSimilarRGB(getRed(), getGreen(), getBlue(), colors[0], colors[1], colors[2], offset);
	}

	/**
	 * 判断当前Color与指定Color是否近似
	 * 
	 * @param dstColor
	 * @param offset
	 * @return
	 */
	public boolean isSimilarRGB(int dstColor, float offset) {
		return isSimilarRGB(this, new LColor(dstColor), offset);
	}

	/**
	 * 判断当前Color与指定Color是否近似
	 * 
	 * @param dstColor
	 * @param offset
	 * @return
	 */
	public boolean isSimilarRGB(String dstColor, float offset) {
		if (dstColor == null) {
			return false;
		}
		return isSimilarRGB(this, new LColor(dstColor), offset);
	}

	/**
	 * 从数组拷贝色彩数值
	 * 
	 * @param arrays
	 * @param offset
	 * @return
	 */
	public float[] fromArray(float[] arrays, int offset) {
		this.r = arrays[offset + 0];
		this.g = arrays[offset + 1];
		this.b = arrays[offset + 2];
		this.a = arrays[offset + 3];
		return arrays;
	}

	/**
	 * 从色彩拷贝数值到数组
	 * 
	 * @param arrays
	 * @param offset
	 * @return
	 */
	public LColor toArray(float[] arrays, int offset) {
		arrays[offset + 0] = this.r;
		arrays[offset + 1] = this.g;
		arrays[offset + 2] = this.b;
		arrays[offset + 3] = this.a;
		return this;
	}

	/**
	 * 从色彩拷贝数值到数组
	 * 
	 * @return
	 */
	public float[] toArray() {
		return new float[] { this.r, this.g, this.b, this.a };
	}

	/**
	 * Gamma空间转换到线性空间
	 * 
	 * @param o
	 * @return
	 */
	public LColor toLinear(LColor o) {
		o.r = gammaToLinear(this.r);
		o.g = gammaToLinear(this.g);
		o.b = gammaToLinear(this.b);
		o.a = this.a;
		return o;
	}

	/**
	 * 线性空间转换到Gamma空间
	 * 
	 * @param o
	 * @return
	 */
	public LColor toGamma(LColor o) {
		o.r = linearToGamma(this.r);
		o.g = linearToGamma(this.g);
		o.b = linearToGamma(this.b);
		o.a = this.a;
		return o;
	}

	/**
	 * 返回指定像素的字符串格式
	 * 
	 * @param color
	 * @return
	 */
	public String toString(int color) {
		String v = CharUtils.toHex(color);
		for (; v.length() < 8;) {
			v = "0" + v;
		}
		return v;
	}

	public float getBrightness() {
		int red = getRed();
		int green = getGreen();
		int blue = getBlue();
		int min = MathUtils.min(MathUtils.min(red, green), blue);
		int max = MathUtils.max(MathUtils.max(red, green), blue);
		return (max + min) / 254f;
	}

	public float getHue() {
		if (r == g && g == b) {
			return 0f;
		}
		int red = getRed();
		int green = getGreen();
		int blue = getBlue();
		int min = MathUtils.min(MathUtils.min(red, green), blue);
		int max = MathUtils.max(MathUtils.max(red, green), blue);

		float delta = max - min;
		float hue;

		if (red == max) {
			hue = (green - blue) / delta;
		} else if (green == max) {
			hue = (blue - red) / delta + 2f;
		} else {
			hue = (red - green) / delta + 4f;
		}
		hue *= 60f;
		if (hue < 0f) {
			hue += 360f;
		}
		return hue;
	}

	public float getSaturation() {
		if (r == g && g == b) {
			return 0f;
		}
		int red = getRed();
		int green = getGreen();
		int blue = getBlue();
		int min = MathUtils.min(MathUtils.min(red, green), blue);
		int max = MathUtils.max(MathUtils.max(red, green), blue);
		int div = max + min;
		if (div > 127) {
			div = 254 - max - min;
		}
		return (max - min) / (float) div;
	}

	public LColor getUnit() {
		float l = this.len();
		return new LColor(r / l, g / l, b / l, a / l);
	}

	public float len2() {
		return r * r + g * g + b * b + a * a;
	}

	public float len() {
		return MathUtils.sqrt(len2());
	}

	public String toRGBAString() {
		return new StrBuilder().append(r).append(LSystem.COMMA).append(g).append(LSystem.COMMA).append(b)
				.append(LSystem.COMMA).append(a).toString();
	}

	public LColor setColor(String c) {
		if (c == null) {
			return this;
		}
		c = c.trim().toLowerCase();
		// 识别字符串格式颜色
		if (c.startsWith("#") || c.startsWith("0x")) {
			setColor(hexToColor(c));
		} else if (c.startsWith("rgb")) {
			int start = c.indexOf('(');
			int end = c.lastIndexOf(')');
			if (start != -1 && end != -1 && end > start) {
				String result = c.substring(start + 1, end).trim();
				String[] list = StringUtils.split(result, LSystem.COMMA);
				if (list.length == 3) {
					setColor(convertInt(list[0].trim()), convertInt(list[1].trim()), convertInt(list[2].trim()));
				} else if (list.length == 4) {
					setColor(convertInt(list[0].trim()), convertInt(list[1].trim()), convertInt(list[2].trim()),
							convertInt(list[3].trim()));
				}
			}
		} else if (c.startsWith("argb")) {
			int start = c.indexOf('(');
			int end = c.lastIndexOf(')');
			if (start != -1 && end != -1 && end > start) {
				String result = c.substring(start + 1, end).trim();
				String[] list = StringUtils.split(result, LSystem.COMMA);
				if (list.length == 3) {
					setColor(convertInt(list[1].trim()), convertInt(list[2].trim()), convertInt(list[0].trim()));
				} else if (list.length == 4) {
					setColor(convertInt(list[1].trim()), convertInt(list[2].trim()), convertInt(list[3].trim()),
							convertInt(list[0].trim()));
				}
			}
		} else if (c.startsWith("transparent")) {
			setColor(TRANSPARENT);
		} else if (MathUtils.isNan(c)) {
			setColor(convertInt(c));
		} else if (StringUtils.isHex(c)) {
			setColor(hexToColor(c));
		} else {
			LColor color = LColorList.get().find(c);
			if (color != null) {
				setColor(color);
			} else {
				setColor(hexToColor(c));
			}
		}
		return this;
	}

	/**
	 * 以指定像素格式返回当前色彩的字符串格式
	 * 
	 * @param format
	 * @return
	 */
	public String toString(String format) {
		if (StringUtils.isEmpty(format)) {
			return toString();
		}
		String newFormat = format.trim().toLowerCase();
		if ("rgb".equals(newFormat)) {
			return toString(getRGB());
		} else if ("argb".equals(newFormat) || "rgba".equals(newFormat)) {
			return toString(getARGB());
		} else if ("bgr".equals(newFormat)) {
			return toString(getBGR());
		} else if ("abgr".equals(newFormat) || "bgra".equals(newFormat)) {
			return toString(getABGR());
		} else if ("hsl".equals(newFormat)) {
			return toString(getRGBtoHSL().getARGB());
		} else if ("alpha".equals(newFormat)) {
			return toString(getAlpha());
		}
		return toString();
	}

	/**
	 * 返回当前Color的字符串格式
	 */
	@Override
	public String toString() {
		return toString(getARGB());
	}

	@Override
	public int hashCode() {
		int result = (r != +0.0f ? NumberUtils.floatToIntBits(r) : 0);
		result = 31 * result + (g != +0.0f ? NumberUtils.floatToIntBits(g) : 0);
		result = 31 * result + (b != +0.0f ? NumberUtils.floatToIntBits(b) : 0);
		result = 31 * result + (a != +0.0f ? NumberUtils.floatToIntBits(a) : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LColor color = (LColor) o;
		if (NumberUtils.compare(color.a, a) != 0) {
			return false;
		}
		if (NumberUtils.compare(color.b, b) != 0) {
			return false;
		}
		if (NumberUtils.compare(color.g, g) != 0) {
			return false;
		}
		if (NumberUtils.compare(color.r, r) != 0) {
			return false;
		}
		return true;
	}

	public boolean equals(float r1, float g1, float b1) {
		return equals(r1, g1, b1, this.a);
	}

	public boolean equals(float r1, float g1, float b1, float a1) {
		if (NumberUtils.compare(a1, this.a) != 0) {
			return false;
		}
		if (NumberUtils.compare(b1, this.b) != 0) {
			return false;
		}
		if (NumberUtils.compare(g1, this.g) != 0) {
			return false;
		}
		if (NumberUtils.compare(r1, this.r) != 0) {
			return false;
		}
		return true;
	}
}
