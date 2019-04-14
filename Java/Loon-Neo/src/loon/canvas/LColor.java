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

import loon.geom.Vector3f;
import loon.geom.Vector4f;
import loon.utils.ListMap;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringUtils;

public class LColor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2290994222887151982L;

	// 默认色彩
	public static final int DEF_COLOR = 0xFFFFFFFF;

	// 默认黑色透明区域
	public static final int TRANSPARENT = 0xFF000000;

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

	public static final int argb(int a, int r, int g, int b) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public static final int rgb(int r, int g, int b) {
		return argb(0xff, r, g, b);
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

	private static ListMap<String, LColor> colorMap;

	public static final LColor silver = new LColor(0xffc0c0c0);

	public static final LColor lightBlue = new LColor(0xffadd8e6);

	public static final LColor lightCoral = new LColor(0xfff08080);

	public static final LColor lightCyan = new LColor(0xffe0ffff);

	public static final LColor lightGoldenrodYellow = new LColor(0xfffafad2);

	public static final LColor lightGreen = new LColor(0xff90ee90);

	public static final LColor lightPink = new LColor(0xffffb6c1);

	public static final LColor lightSalmon = new LColor(0xffffa07a);

	public static final LColor lightSeaGreen = new LColor(0xff20b2aa);

	public static final LColor lightSkyBlue = new LColor(0xff87cefa);

	public static final LColor lightSlateGray = new LColor(0xff778899);

	public static final LColor lightSteelBlue = new LColor(0xffb0c4de);

	public static final LColor lightYellow = new LColor(0xffffffe0);

	public static final LColor lime = new LColor(0xff00ff00);

	public static final LColor limeGreen = new LColor(0xff32cd32);

	public static final LColor linen = new LColor(0xfffaf0e6);

	public static final LColor maroon = new LColor(0xff800000);

	public static final LColor mediumAquamarine = new LColor(0xff66cdaa);

	public static final LColor mediumBlue = new LColor(0xff0000cd);

	public static final LColor purple = new LColor(0xff800080);

	public static final LColor wheat = new LColor(0xfff5deb3);

	public static final LColor gold = new LColor(0xffffd700);

	public static final LColor white = new LColor(1.0f, 1.0f, 1.0f, 1.0f);

	public static final LColor transparent = white;

	public static final LColor yellow = new LColor(1.0f, 1.0f, 0.0f, 1.0f);

	public static final LColor red = new LColor(1.0f, 0.0f, 0.0f, 1.0f);

	public static final LColor blue = new LColor(0.0f, 0.0f, 1.0f, 1.0f);

	public static final LColor cornFlowerBlue = new LColor(0.4f, 0.6f, 0.9f, 1.0f);

	public static final LColor green = new LColor(0.0f, 1.0f, 0.0f, 1.0f);

	public static final LColor black = new LColor(0.0f, 0.0f, 0.0f, 1.0f);

	public static final LColor gray = new LColor(0.5f, 0.5f, 0.5f, 1.0f);

	public static final LColor cyan = new LColor(0.0f, 1.0f, 1.0f, 1.0f);

	public static final LColor darkGray = new LColor(0.3f, 0.3f, 0.3f, 1.0f);

	public static final LColor lightGray = new LColor(0.7f, 0.7f, 0.7f, 1.0f);

	public static final LColor pink = new LColor(1.0f, 0.7f, 0.7f, 1.0f);

	public static final LColor orange = new LColor(1.0f, 0.8f, 0.0f, 1.0f);

	public static final LColor magenta = new LColor(1.0f, 0.0f, 1.0f, 1.0f);

	public float r = 0.0f;

	public float g = 0.0f;

	public float b = 0.0f;

	public float a = 1.0f;

	public static final LColor newWhite() {
		return new LColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static final LColor newBlack() {
		return new LColor(0.0f, 0.0f, 0.0f, 1.0f);
	}

	public static final LColor newRed() {
		return new LColor(1.0f, 0.0f, 0.0f, 1.0f);
	}

	/**
	 * 转换字符串为color
	 * 
	 * @param c
	 */
	public LColor(String c) {
		if (c == null) {
			setColor(LColor.white);
			return;
		}
		// 识别字符串格式颜色
		if (c.startsWith("#")) {
			setColor(hexToColor(c));
		} else if (c.startsWith("rgb")) {
			int start = c.indexOf('(');
			int end = c.lastIndexOf(')');
			if (start != -1 && end != -1 && end > start) {
				String result = c.substring(start + 1, end).trim();
				String[] list = StringUtils.split(result, ',');
				if (list.length == 3) {
					setColor(Integer.parseInt(list[0]), Integer.parseInt(list[1]), Integer.parseInt(list[2]));
				} else if (list.length == 4) {
					setColor(Integer.parseInt(list[0]), Integer.parseInt(list[1]), Integer.parseInt(list[2]),
							Integer.parseInt(list[3]));
				}
			}
		} else if (MathUtils.isNan(c)) {
			setColor((int) Double.parseDouble(c));
		} else {
			synchronized (this) {
				if (colorMap == null) {
					colorMap = new ListMap<String, LColor>();
					colorMap.put("limegreen", limeGreen);
					colorMap.put("linen", linen);
					colorMap.put("maroon", maroon);
					colorMap.put("mediumAquamarine", mediumAquamarine);
					colorMap.put("mediumblue", mediumBlue);
					colorMap.put("wheat", wheat);
					colorMap.put("gold", gold);
					colorMap.put("white", white);
					colorMap.put("yellow", yellow);
					colorMap.put("red", red);
					colorMap.put("blue", blue);
					colorMap.put("cornflowerblue", cornFlowerBlue);
					colorMap.put("black", black);
					colorMap.put("gray", gray);
					colorMap.put("cyan", cyan);
					colorMap.put("darkgray", darkGray);
					colorMap.put("lightgray", lightGray);
					colorMap.put("pink", pink);
					colorMap.put("orange", orange);
					colorMap.put("magenta", magenta);
					colorMap.put("silver", silver);
					colorMap.put("lightblue", lightBlue);
					colorMap.put("lightcoral", lightCoral);
					colorMap.put("lightcyan", lightCyan);
					colorMap.put("lightgoldenrodyellow", lightGoldenrodYellow);
					colorMap.put("lightgreen", lightGreen);
					colorMap.put("lightpink", lightPink);
					colorMap.put("lightseagreen", lightSeaGreen);
					colorMap.put("lightskyblue", lightSkyBlue);
					colorMap.put("lightslategray", lightSlateGray);
					colorMap.put("lightsteelblue", lightSteelBlue);
					colorMap.put("lightyellow", lightYellow);
					colorMap.put("transparent", transparent);
				}
				LColor color = colorMap.get(c.trim().toLowerCase());
				if (color != null) {
					setColor(color);
				} else {
					setColor(hexToColor(c));
				}
			}
		}
	}

	public LColor() {
		this(LColor.white);
	}

	public LColor(LColor color) {
		if (color == null) {
			setColor(LColor.white);
			return;
		}
		setColor(color.r, color.g, color.b, color.a);
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

	public LColor reset() {
		return setColor(1.0f, 1.0f, 1.0f, 1.0f);
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

	public boolean equals(float r1, float g1, float b1, float a1) {
		if (NumberUtils.compare(a1, a) != 0) {
			return false;
		}
		if (NumberUtils.compare(b1, b) != 0) {
			return false;
		}
		if (NumberUtils.compare(g1, g) != 0) {
			return false;
		}
		if (NumberUtils.compare(r1, r) != 0) {
			return false;
		}
		return true;
	}

	public LColor darker() {
		return darker(0.5f);
	}

	public LColor darker(float scale) {
		scale = 1f - scale;
		LColor temp = new LColor(r * scale, g * scale, b * scale, a);
		return temp;
	}

	public LColor brighter() {
		return brighter(0.2f);
	}

	public LColor setColorValue(int r, int g, int b, int a) {
		this.r = r > 1 ? (float) r / 255f : r;
		this.g = g > 1 ? (float) g / 255f : g;
		this.b = b > 1 ? (float) b / 255f : b;
		this.a = a > 1 ? (float) a / 255f : a;
		return this;
	}

	public LColor setIntColor(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	public LColor setColor(float r, float g, float b, float a) {
		this.r = r > 1f ? r / 255f : r;
		this.g = g > 1f ? g / 255f : g;
		this.b = b > 1f ? b / 255f : b;
		this.a = a > 1f ? a / 255f : a;
		return this;
	}

	public LColor setFloatColor(float r, float g, float b, float a) {
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
		return setColor(color.r, color.g, color.b, color.a);
	}

	public LColor setColor(int pixel) {
		int r = (pixel & 0x00FF0000) >> 16;
		int g = (pixel & 0x0000FF00) >> 8;
		int b = (pixel & 0x000000FF);
		int a = (pixel & 0xFF000000) >> 24;
		if (a < 0) {
			a += 256;
		}
		setColor(r, g, b, a);
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

	public LColor setAlpha(float alpha) {
		this.a = alpha;
		return this;
	}

	public LColor brighter(float scale) {
		scale += 1;
		LColor temp = new LColor(r * scale, g * scale, b * scale, a);
		return temp;
	}

	public LColor multiply(LColor c) {
		return new LColor(r * c.r, g * c.g, b * c.b, a * c.a);
	}

	public LColor add(LColor c) {
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		this.a += c.a;
		return this;
	}

	public LColor sub(LColor c) {
		this.r -= c.r;
		this.g -= c.g;
		this.b -= c.b;
		this.a -= c.a;
		return this;
	}

	public LColor mul(LColor c) {
		this.r *= c.r;
		this.g *= c.g;
		this.b *= c.b;
		this.a *= c.a;
		return this;
	}

	public LColor mulAlpha(float a) {
		this.a *= a;
		return this;
	}

	public LColor mulAlpha(LColor c) {
		return mulAlpha(c.a);
	}

	public LColor div(LColor c) {
		this.r /= c.r;
		this.g /= c.g;
		this.b /= c.b;
		this.a /= c.a;
		return this;
	}

	public LColor divAlpha(float a) {
		if (a <= 0) {
			a = 0.01f;
		}
		this.a /= a;
		return this;
	}

	public LColor divAlpha(LColor c) {
		return divAlpha(c.a);
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

	public static final LColor lerp(LColor value1, LColor value2, float amount) {
		return new LColor(lerp(value1.getRed(), value2.getRed(), amount),
				lerp(value1.getGreen(), value2.getGreen(), amount), lerp(value1.getBlue(), value2.getBlue(), amount),
				lerp(value1.getAlpha(), value2.getAlpha(), amount));
	}

	private static final int lerp(int color1, int color2, float amount) {
		return color1 + (int) ((color2 - color1) * amount);
	}

	public LColor lerp(LColor target, float alpha) {
		return lerp(this, target, alpha);
	}

	/**
	 * 返回ARGB
	 * 
	 * @return
	 */
	public int getARGB() {
		return argb(getAlpha(), getRed(), getGreen(), getBlue());
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

	/**
	 * 返回RGB
	 * 
	 * @return
	 */
	public int getRGB() {
		return rgb(getRed(), getGreen(), getBlue());
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

	/**
	 * 获得RGB颜色
	 * 
	 * @param pixels
	 * @return
	 */
	public static final int getRGB(int pixels) {
		int r = (pixels >> 16) & 0xff;
		int g = (pixels >> 8) & 0xff;
		int b = pixels & 0xff;
		return rgb(r, g, b);
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
		return (color >> 16) & 0xff;
	}

	/**
	 * 获得Green
	 * 
	 * @param color
	 * @return
	 */
	public static final int getGreen(int color) {
		return (color >> 8) & 0xff;
	}

	/**
	 * 获得Blud
	 * 
	 * @param color
	 * @return
	 */
	public static final int getBlue(int color) {
		return color & 0xff;
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
			int r = (argbColor >> 16) & 0xff;
			int g = (argbColor >> 8) & 0xff;
			int b = argbColor & 0xff;
			r = (a * r + 127) / 255;
			g = (a * g + 127) / 255;
			b = (a * b + 127) / 255;
			return (a << 24) | (r << 16) | (g << 8) | b;
		}
	}

	public static final int[] getRGBs(final int pixel) {
		int[] rgbs = new int[3];
		rgbs[0] = (pixel >> 16) & 0xff;
		rgbs[1] = (pixel >> 8) & 0xff;
		rgbs[2] = (pixel) & 0xff;
		return rgbs;
	}

	public static final int[] getRGBAs(final int pixel) {
		int[] rgbas = new int[4];
		rgbas[0] = (pixel >> 16) & 0xff;
		rgbas[1] = (pixel >> 8) & 0xff;
		rgbas[2] = (pixel) & 0xff;
		rgbas[3] = pixel >>> 24;
		return rgbas;
	}

	public byte[] toRgbaByteArray() {
		return new byte[] { (byte) getRed(), (byte) getGreen(), (byte) getBlue(), (byte) getAlpha() };
	}

	public byte[] toRgbByteArray() {
		return new byte[] { (byte) getRed(), (byte) getGreen(), (byte) getBlue() };
	}

	public int toIntBits() {
		int color = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
		return color;
	}

	public static final float toFloatBits(float r, float g, float b, float a) {
		int color = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
		return NumberUtils.intBitsToFloat(color & 0xfeffffff);
	}

	public float toFloatBits() {
		int color = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
		return NumberUtils.intBitsToFloat(color & 0xfeffffff);
	}

	public static final LColor hsvToColor(float h, float s, float v) {
		if (h == 0 && s == 0) {
			return new LColor(v, v, v);
		}
		float c = s * v;
		float x = c * (1 - MathUtils.abs(h % 2 - 1));
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
		double a = ((color >> 24) & 0xff) / 255.0;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = (color >> 0) & 0xff;
		return "rgba(" + r + "," + g + "," + b + "," + a + ")";
	}

	public static final LColor hexToColor(String c) {
		try {
			if (c.startsWith("#")) {
				return hexToColor(c.substring(1));
			} else {
				return new LColor(Integer.parseInt(c.substring(0, 2), 16), Integer.parseInt(c.substring(2, 4), 16),
						Integer.parseInt(c.substring(4, 6), 16));
			}
		} catch (Exception e) {
			return new LColor();
		}
	}

	public static final LColor stringToColor(String c) {
		return hexToColor(c);
	}

	public String toCSS() {
		return "rgba(" + (int) (r * 255) + "," + (int) (g * 255) + "," + (int) (b * 255) + "," + (int) (a * 255) + ")";
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
	 * @param percent
	 *            最大值为1f，最小值为0f
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

	@Override
	public String toString() {
		String value = Integer.toHexString(
				((int) (255 * r) << 24) | ((int) (255 * g) << 16) | ((int) (255 * b) << 8) | ((int) (255 * a)));
		for (; value.length() < 8;) {
			value = "0" + value;
		}
		return value;
	}

}
