package loon.core.graphics;

import java.io.Serializable;
import java.nio.FloatBuffer;

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
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class LColor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2290994222887151982L;

	public static float[] toRGBA(int pixel) {
		int r = (pixel & 0x00FF0000) >> 16;
		int g = (pixel & 0x0000FF00) >> 8;
		int b = (pixel & 0x000000FF);
		int a = (pixel & 0xFF000000) >> 24;
		if (a < 0) {
			a += 256;
		}
		return new float[] { r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f };
	}

	public static int c(int b) {
		if (b < 0) {
			return 256 + b;
		}
		return b;
	}

	public static float[] withAlpha(float[] color, float alpha) {
		float r = color[0];
		float g = color[1];
		float b = color[2];
		float a = alpha;
		return new float[] { r, g, b, a };
	}

	public static byte[] argbToRGBA(int pixel) {
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
	public static byte[] argbToRGBA(int[] pixels) {
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
	public static byte[] argbToRGB(int[] pixels) {
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

	/**
	 * 获得RGB565格式数据
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int rgb565(float r, float g, float b) {
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
	public static int rgba4444(float r, float g, float b, float a) {
		return ((int) (r * 15) << 12) | ((int) (g * 15) << 8)
				| ((int) (b * 15) << 4) | (int) (a * 15);
	}

	/**
	 * 获得RGB888格式数据
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int rgb888(float r, float g, float b) {
		return ((int) (r * 255) << 16) | ((int) (g * 255) << 8)
				| (int) (b * 255);
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
	public static int rgba8888(float r, float g, float b, float a) {
		return ((int) (r * 255) << 24) | ((int) (g * 255) << 16)
				| ((int) (b * 255) << 8) | (int) (a * 255);
	}

	public final static LColor silver = new LColor(0xffc0c0c0);

	public final static LColor lightBlue = new LColor(0xffadd8e6);

	public final static LColor lightCoral = new LColor(0xfff08080);

	public final static LColor lightCyan = new LColor(0xffe0ffff);

	public final static LColor lightGoldenrodYellow = new LColor(0xfffafad2);

	public final static LColor lightGreen = new LColor(0xff90ee90);

	public final static LColor lightPink = new LColor(0xffffb6c1);

	public final static LColor lightSalmon = new LColor(0xffffa07a);

	public final static LColor lightSeaGreen = new LColor(0xff20b2aa);

	public final static LColor lightSkyBlue = new LColor(0xff87cefa);

	public final static LColor lightSlateGray = new LColor(0xff778899);

	public final static LColor lightSteelBlue = new LColor(0xffb0c4de);

	public final static LColor lightYellow = new LColor(0xffffffe0);

	public final static LColor lime = new LColor(0xff00ff00);

	public final static LColor limeGreen = new LColor(0xff32cd32);

	public final static LColor linen = new LColor(0xfffaf0e6);

	public final static LColor maroon = new LColor(0xff800000);

	public final static LColor mediumAquamarine = new LColor(0xff66cdaa);

	public final static LColor mediumBlue = new LColor(0xff0000cd);

	public final static LColor purple = new LColor(0xff800080);

	public final static LColor wheat = new LColor(0xfff5deb3);

	public final static LColor gold = new LColor(0xffffd700);

	public static final LColor white = new LColor(1.0f, 1.0f, 1.0f, 1.0f);

	public static final LColor yellow = new LColor(1.0f, 1.0f, 0.0f, 1.0f);

	public static final LColor red = new LColor(1.0f, 0.0f, 0.0f, 1.0f);

	public static final LColor blue = new LColor(0.0f, 0.0f, 1.0f, 1.0f);

	public static final LColor cornFlowerBlue = new LColor(0.4f, 0.6f, 0.9f,
			1.0f);

	public static final LColor green = new LColor(0.0f, 1.0f, 0.0f, 1.0f);

	public static final LColor black = new LColor(0.0f, 0.0f, 0.0f, 1.0f);

	public static final LColor gray = new LColor(0.5f, 0.5f, 0.5f, 1.0f);

	public static final LColor cyan = new LColor(0.0f, 1.0f, 1.0f, 1.0f);

	public static final LColor darkGray = new LColor(0.3f, 0.3f, 0.3f, 1.0f);

	public static final LColor lightGray = new LColor(0.7f, 0.7f, 0.7f, 1.0f);

	public final static LColor pink = new LColor(1.0f, 0.7f, 0.7f, 1.0f);

	public final static LColor orange = new LColor(1.0f, 0.8f, 0.0f, 1.0f);

	public final static LColor magenta = new LColor(1.0f, 0.0f, 1.0f, 1.0f);

	public float r;

	public float g;

	public float b;

	public float a = 1.0f;

	public LColor() {
		this(LColor.white);
	}

	public LColor(LColor color) {
		this(color.r, color.g, color.b, color.a);
	}

	public LColor(FloatBuffer buffer) {
		setColor(buffer.get(), buffer.get(), buffer.get(), buffer.get());
	}

	public LColor(int r, int g, int b) {
		setColor(r, g, b);
	}

	public LColor(int r, int g, int b, int a) {
		setColor(r, g, b, a);
	}

	public LColor(float r, float g, float b) {
		setColor(r, g, b);
	}

	public LColor(float r, float g, float b, float a) {
		setColor(r, g, b, a);
	}

	public LColor(int value) {
		int r = (value & 0x00FF0000) >> 16;
		int g = (value & 0x0000FF00) >> 8;
		int b = (value & 0x000000FF);
		int a = (value & 0xFF000000) >> 24;

		if (a < 0) {
			a += 256;
		}
		if (a == 0) {
			a = 255;
		}
		setColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}

	public static LColor decode(String nm) {
		return new LColor(Integer.decode(nm).intValue());
	}

	@Override
	public int hashCode() {
		int result = (r != +0.0f ? Float.floatToIntBits(r) : 0);
		result = 31 * result + (g != +0.0f ? Float.floatToIntBits(g) : 0);
		result = 31 * result + (b != +0.0f ? Float.floatToIntBits(b) : 0);
		result = 31 * result + (a != +0.0f ? Float.floatToIntBits(a) : 0);
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
		if (Float.compare(color.a, a) != 0) {
			return false;
		}
		if (Float.compare(color.b, b) != 0) {
			return false;
		}
		if (Float.compare(color.g, g) != 0) {
			return false;
		}
		if (Float.compare(color.r, r) != 0) {
			return false;
		}
		return true;
	}

	public boolean equals(float r1, float g1, float b1, float a1) {
		if (Float.compare(a1, a) != 0) {
			return false;
		}
		if (Float.compare(b1, b) != 0) {
			return false;
		}
		if (Float.compare(g1, g) != 0) {
			return false;
		}
		if (Float.compare(r1, r) != 0) {
			return false;
		}
		return true;
	}

	public LColor darker() {
		return darker(0.5f);
	}

	public LColor darker(float scale) {
		scale = 1 - scale;
		LColor temp = new LColor(r * scale, g * scale, b * scale, a);
		return temp;
	}

	public LColor brighter() {
		return brighter(0.2f);
	}

	public void setColorValue(int r, int g, int b, int a) {
		this.r = r > 1 ? r / 255.0f : r;
		this.g = g > 1 ? g / 255.0f : g;
		this.b = b > 1 ? b / 255.0f : b;
		this.a = a > 1 ? a / 255.0f : a;
	}

	public void setIntColor(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setColor(float r, float g, float b, float a) {
		this.r = r > 1 ? r / 255.0f : r;
		this.g = g > 1 ? g / 255.0f : g;
		this.b = b > 1 ? b / 255.0f : b;
		this.a = a > 1 ? a / 255.0f : a;
	}

	public void setFloatColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setColor(float r, float g, float b) {
		setColor(r, g, b, b > 1 ? 255 : 1.0f);
	}

	public void setColor(int r, int g, int b, int a) {
		this.r = (float) r / 255;
		this.g = (float) g / 255;
		this.b = (float) b / 255;
		this.a = (float) a / 255;
	}

	public void setColor(int r, int g, int b) {
		setColor(r, g, b, 255);
	}

	public void setColor(LColor color) {
		setColor(color.r, color.g, color.b, color.a);
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

	public LColor brighter(float scale) {
		scale += 1;
		LColor temp = new LColor(r * scale, g * scale, b * scale, a);
		return temp;
	}

	public LColor multiply(LColor c) {
		return new LColor(r * c.r, g * c.g, b * c.b, a * c.a);
	}

	public void add(LColor c) {
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		this.a += c.a;
	}

	public void sub(LColor c) {
		this.r -= c.r;
		this.g -= c.g;
		this.b -= c.b;
		this.a -= c.a;
	}

	public void mul(LColor c) {
		this.r *= c.r;
		this.g *= c.g;
		this.b *= c.b;
		this.a *= c.a;
	}

	/**
	 * 直接复制一个Color
	 * 
	 * @param c
	 * @return
	 */
	public LColor copy(LColor c) {
		return new LColor(r, g, b, a);
	}

	/**
	 * 获得像素相加的Color
	 * 
	 * @param c
	 * @return
	 */
	public LColor addCopy(LColor c) {
		LColor copy = new LColor(r, g, b, a);
		copy.r += c.r;
		copy.g += c.g;
		copy.b += c.b;
		copy.a += c.a;
		return copy;
	}

	/**
	 * 获得像素相减的Color
	 * 
	 * @param c
	 * @return
	 */
	public LColor subCopy(LColor c) {
		LColor copy = new LColor(r, g, b, a);
		copy.r -= c.r;
		copy.g -= c.g;
		copy.b -= c.b;
		copy.a -= c.a;
		return copy;
	}

	/**
	 * 获得像素相乘的Color
	 * 
	 * @param c
	 * @return
	 */
	public LColor mulCopy(LColor c) {
		LColor copy = new LColor(r, g, b, a);
		copy.r *= c.r;
		copy.g *= c.g;
		copy.b *= c.b;
		copy.a *= c.a;
		return copy;
	}

	public LColor mul(float s) {
		return new LColor(r * s, g * s, b * s, a * s);
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
	 * 返回RGB
	 * 
	 * @return
	 */
	public int getRGB() {
		return getRGB(getRed(), getGreen(), getBlue());
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

	public static LColor lerp(LColor value1, LColor value2, float amount) {
		return new LColor(lerp(value1.getRed(), value2.getRed(), amount), lerp(
				value1.getGreen(), value2.getGreen(), amount), lerp(
				value1.getBlue(), value2.getBlue(), amount), lerp(
				value1.getAlpha(), value2.getAlpha(), amount));
	}

	static int lerp(int i1, int i2, float amount) {
		return i1 + (int) ((i2 - i1) * amount);
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

	public static float toFloatBits(int r, int g, int b, int a) {
		int color = (a << 24) | (b << 16) | (g << 8) | r;
		float floatColor = Float.intBitsToFloat(color & 0xfeffffff);
		return floatColor;
	}

	public static int toIntBits(int r, int g, int b, int a) {
		return (a << 24) | (b << 16) | (g << 8) | r;
	}

	public float toFloatBits() {
		int color = ((int) (255 * a) << 24) | ((int) (255 * b) << 16)
				| ((int) (255 * g) << 8) | ((int) (255 * r));
		return Float.intBitsToFloat(color & 0xfeffffff);
	}

	public int toIntBits() {
		int color = ((int) (255 * a) << 24) | ((int) (255 * b) << 16)
				| ((int) (255 * g) << 8) | ((int) (255 * r));
		return color;
	}

	public static float toFloatBits(float r, float g, float b, float a) {
		int color = ((int) (255 * a) << 24) | ((int) (255 * b) << 16)
				| ((int) (255 * g) << 8) | ((int) (255 * r));
		return Float.intBitsToFloat(color & 0xfeffffff);
	}

}
