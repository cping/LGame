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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import loon.Graphics;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.action.collision.CollisionHelper;
import loon.geom.Polygon;
import loon.geom.RectBox;
import loon.geom.RectI;
import loon.geom.Shape;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.utils.ArrayByte;
import loon.utils.BufferUtils;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.Scale;
import loon.utils.StrBuilder;
import loon.utils.TArray;

/**
 * 跨平台处理像素用类(不同平台内部渲染实现通常有细节差异，某些时候不如自己写同一方法更能保证效果一致，比如转换代码到C#或C++平台时,
 * 而且一些第三方支持库也可能没有类似于Image的本地图像渲染支持（如使用MonoGame或Unity3D为基础平台），这时就可以直接套用此类的实现)
 */
public final class Pixmap extends PixmapComposite implements Canvas.ColorPixel, LRelease {

	private Canvas _paintCanvas = null;

	private Vector2f _pointLocation = new Vector2f();

	private int _composite = -1;

	protected Graphics getGraphics() {
		if (LSystem.base() != null) {
			return LSystem.base().graphics();
		}
		return null;
	}

	public Image getImage() {
		return getImage(true);
	}

	public Image getImage(boolean scaleUpdate) {
		final Scale scale = LSystem.getScale();
		if (scale.factor == 1f) {
			if (_paintCanvas == null) {
				_paintCanvas = getGraphics().createCanvas(getWidth(), getHeight());
			}
			if (_dirty) {
				_paintCanvas.getImage().setPixmap(this);
			}
		} else if (scaleUpdate) {
			final int newWidth = scale.scaledCeil(getWidth());
			final int newHeight = scale.scaledCeil(getHeight());
			if (_paintCanvas == null) {
				_paintCanvas = getGraphics().createCanvas(newWidth, newHeight);
			}
			if (_dirty) {
				_paintCanvas.getImage().setPixmap(Pixmap.getResize(this, newWidth, newHeight));
			}
		} else {
			final int newWidth = scale.invScaledCeil(getWidth());
			final int newHeight = scale.invScaledCeil(getHeight());
			if (_paintCanvas == null) {
				_paintCanvas = getGraphics().createCanvas(newWidth, newHeight);
			}
			if (_dirty) {
				_paintCanvas.getImage().setPixmap(this);
			}
		}
		return _paintCanvas.getImage();
	}

	public LTexture texture() {
		return toTexture();
	}

	public LTexture toTexture() {
		return getImage().texture();
	}

	public static Pixmap createImage(int[] pixels, int w, int h) {
		return createImage(pixels, w, h, true);
	}

	public static Pixmap createImage(int[] pixels, int w, int h, boolean hasAlpha) {
		return new Pixmap(pixels, w, h, hasAlpha);
	}

	public static Pixmap createImage(int w, int h) {
		return createImage(w, h, true);
	}

	public static Pixmap createImage(int w, int h, boolean hasAlpha) {
		return new Pixmap(w, h, hasAlpha);
	}

	public static Pixmap getResize(Pixmap image, int w, int h) {
		if (image == null) {
			return null;
		}
		if (image._width == w && image._height == h) {
			return image;
		}
		Pixmap result = new Pixmap(w, h, image._hasAlpha);
		result.drawPixmap(image, 0, 0, w, h, 0, 0, image.getWidth(), image.getHeight());
		return result;
	}

	public static Pixmap getResize(Pixmap image, int x, int y, int w, int h) {
		if (image == null) {
			return null;
		}
		if (image._width == w && image._height == h) {
			return image;
		}
		Pixmap result = new Pixmap(w, h, image._hasAlpha);
		result.drawPixmap(image, 0, 0, w, h, x, y, image.getWidth() - x, image.getHeight() - y);
		return result;
	}

	public static Pixmap drawClipImage(Pixmap image, int objectWidth, int objectHeight, int x1, int y1, int x2,
			int y2) {
		Pixmap buffer = new Pixmap(objectWidth, objectHeight, true);
		buffer.drawPixmap(image, 0, 0, objectWidth, objectHeight, x1, y1, x2 - x1, y2 - y1);
		return buffer;
	}

	public static Pixmap drawClipImage(Pixmap image, int objectWidth, int objectHeight, int x, int y) {
		Pixmap buffer = new Pixmap(objectWidth, objectHeight, true);
		buffer.drawPixmap(image, 0, 0, objectWidth, objectHeight, x, y, objectWidth, objectHeight);
		return buffer;
	}

	public static Pixmap drawCropImage(Pixmap image, int x, int y, int objectWidth, int objectHeight) {
		Pixmap buffer = new Pixmap(objectWidth, objectHeight, true);
		buffer.drawPixmap(image, 0, 0, objectWidth, objectHeight, x, y, objectWidth, objectHeight);
		return buffer;
	}

	/**
	 * 获得指定像素内指定色彩的矩形范围
	 * 
	 * @param image
	 * @param mask
	 * @param color
	 * @param findColor
	 * @return
	 */
	public static RectBox getColorBoundsRect(Pixmap image, int mask, int color, boolean findColor) {
		int left = image.getWidth() + 1;
		int right = 0;
		int top = image.getHeight() + 1;
		int bottom = 0;
		int pixel;
		boolean hit;
		for (int x = 0; x < image.getWidth(); x++) {
			hit = false;
			for (int y = 0; y < image.getHeight(); y++) {
				pixel = image.getPixel(x, y);
				hit = findColor ? (pixel & mask) == color : (pixel & mask) != color;
				if (hit) {
					if (x < left)
						left = x;
					break;
				}
			}
			if (hit) {
				break;
			}
		}
		int ix;
		for (int x = 0; x < image.getWidth(); x++) {
			ix = (image.getWidth() - 1) - x;
			hit = false;
			for (int y = 0; y < image.getHeight(); y++) {
				pixel = image.getPixel(ix, y);
				hit = findColor ? (pixel & mask) == color : (pixel & mask) != color;
				if (hit) {
					if (ix > right)
						right = ix;
					break;
				}
			}
			if (hit) {
				break;
			}
		}
		for (int y = 0; y < image.getHeight(); y++) {
			hit = false;
			for (int x = 0; x < image.getWidth(); x++) {
				pixel = image.getPixel(x, y);
				hit = findColor ? (pixel & mask) == color : (pixel & mask) != color;

				if (hit) {
					if (y < top)
						top = y;
					break;
				}
			}
			if (hit) {
				break;
			}
		}
		int iy;
		for (int y = 0; y < image.getHeight(); y++) {
			iy = (image.getHeight() - 1) - y;
			hit = false;
			for (int x = 0; x < image.getWidth(); x++) {
				pixel = image.getPixel(x, iy);
				hit = findColor ? (pixel & mask) == color : (pixel & mask) != color;

				if (hit) {
					if (iy > bottom)
						bottom = iy;
					break;
				}
			}

			if (hit) {
				break;
			}
		}
		int w = right - left;
		int h = bottom - top;
		if (w > 0) {
			w++;
		}
		if (h > 0) {
			h++;
		}
		if (w < 0) {
			w = 0;
		}
		if (h < 0) {
			h = 0;
		}
		if (left == right)
			w = 1;
		if (top == bottom)
			h = 1;

		if (left > image.getWidth()) {
			left = 0;
		}
		if (top > image.getHeight()) {
			top = 0;
		}
		return new RectBox(left, top, w, h);
	}

	/**
	 * 模糊化指定像素
	 * 
	 * @param srcPixels
	 * @param dstPixels
	 * @param width
	 * @param height
	 * @param radius
	 */
	public static void blurPass(int[] srcPixels, int[] dstPixels, int width, int height, int radius) {
		final int windowSize = radius * 2 + 1;
		final int radiusPlusOne = radius + 1;

		int sumRed;
		int sumGreen;
		int sumBlue;
		int sumAlpha;

		int srcIndex = 0;
		int dstIndex;
		int pixel;

		int[] sumLookupTable = new int[256 * windowSize];
		for (int i = 0; i < sumLookupTable.length; i++) {
			sumLookupTable[i] = i / windowSize;
		}

		int[] indexLookupTable = new int[radiusPlusOne];
		if (radius < width) {
			for (int i = 0; i < indexLookupTable.length; i++) {
				indexLookupTable[i] = i;
			}
		} else {
			for (int i = 0; i < width; i++) {
				indexLookupTable[i] = i;
			}
			for (int i = width; i < indexLookupTable.length; i++) {
				indexLookupTable[i] = width - 1;
			}
		}

		for (int y = 0; y < height; y++) {
			sumAlpha = sumRed = sumGreen = sumBlue = 0;
			dstIndex = y;

			pixel = srcPixels[srcIndex];
			sumRed += radiusPlusOne * ((pixel >> 24) & 0xFF);
			sumGreen += radiusPlusOne * ((pixel >> 16) & 0xFF);
			sumBlue += radiusPlusOne * ((pixel >> 8) & 0xFF);
			sumAlpha += radiusPlusOne * (pixel & 0xFF);

			for (int i = 1; i <= radius; i++) {
				pixel = srcPixels[srcIndex + indexLookupTable[i]];
				sumRed += (pixel >> 24) & 0xFF;
				sumGreen += (pixel >> 16) & 0xFF;
				sumBlue += (pixel >> 8) & 0xFF;
				sumAlpha += pixel & 0xFF;
			}

			for (int x = 0; x < width; x++) {
				dstPixels[dstIndex] = sumLookupTable[sumRed] << 24 | sumLookupTable[sumGreen] << 16
						| sumLookupTable[sumBlue] << 8 | sumLookupTable[sumAlpha];
				dstIndex += height;

				int nextPixelIndex = x + radiusPlusOne;
				if (nextPixelIndex >= width) {
					nextPixelIndex = width - 1;
				}

				int previousPixelIndex = x - radius;
				if (previousPixelIndex < 0) {
					previousPixelIndex = 0;
				}

				int nextPixel = srcPixels[srcIndex + nextPixelIndex];
				int previousPixel = srcPixels[srcIndex + previousPixelIndex];

				sumRed += (nextPixel >> 24) & 0xFF;
				sumRed -= (previousPixel >> 24) & 0xFF;

				sumGreen += (nextPixel >> 16) & 0xFF;
				sumGreen -= (previousPixel >> 16) & 0xFF;

				sumBlue += (nextPixel >> 8) & 0xFF;
				sumBlue -= (previousPixel >> 8) & 0xFF;

				sumAlpha += nextPixel & 0xFF;
				sumAlpha -= previousPixel & 0xFF;
			}

			srcIndex += width;
		}
	}

	/**
	 * 模糊指定像素，范围为指定半径和迭代次数
	 * 
	 * @param pixels
	 * @param width
	 * @param height
	 * @param radius
	 * @param iterations
	 * @return
	 */
	public static int[] blur(int[] pixels, int width, int height, int radius, int iterations) {
		int[] srcPixels = new int[width * height];
		int[] dstPixels = new int[width * height];
		System.arraycopy(pixels, 0, srcPixels, 0, srcPixels.length);
		for (int i = 0; i < iterations; i++) {
			blurPass(srcPixels, dstPixels, width, height, radius);
			blurPass(dstPixels, srcPixels, height, width, radius);
		}
		return srcPixels;
	}

	private RectI temp_rect = new RectI();

	private boolean _dirty;

	private int _baseColor = LColor.DEF_COLOR;

	private int _background = LColor.black.getRGB();

	private int _transparent = 0;

	private boolean _isClosed;

	private int[] _drawPixels;

	private int _translateX, _translateY, _width, _height, _length;

	private LColor srcColor = new LColor();

	private LColor dstColor = new LColor();

	private LColor xorColor;

	private boolean xorMode;

	private int xorRGB;

	private float _baseAlpha = 1f;

	private boolean _hasAlpha;

	private RectI defClip;

	private RectI clip;

	public Pixmap(int w, int h) {
		this(w, h, true);
	}

	public Pixmap(int w, int h, boolean hasAlpha) {
		this(new int[w * h], w, h, hasAlpha);
	}

	public Pixmap(int[] pixelsData, int w, int h) {
		this(pixelsData, w, h, true);
	}

	public Pixmap(int[] pixelsData, int w, int h, boolean hasAlpha) {
		this.set(pixelsData, w, h, hasAlpha);
	}

	public void set(Pixmap pix) {
		if (pix == null) {
			throw new LSysException("Pixmap is null !");
		}
		this.set(CollectionUtils.copyOf(pix.getData()), pix.getWidth(), pix.getHeight(), pix.hasAlpha());
	}

	private void set(int[] pixelsData, int w, int h, boolean hasAlpha) {
		if (w < 0) {
			throw new LSysException("Width may not be negative !");
		}
		if (h < 0) {
			throw new LSysException("Height may not be negative !");
		}
		if (pixelsData == null) {
			pixelsData = new int[w * h];
		}
		this._width = w;
		this._height = h;
		this._drawPixels = pixelsData;
		this._hasAlpha = hasAlpha;
		this._length = _drawPixels.length;
		if (hasAlpha) {
			this._transparent = 0;
		} else {
			this._transparent = LColor.TRANSPARENT;
		}
		this._dirty = true;
		this.defClip = new RectI(0, 0, _width, _height);
		this.clip = new RectI(0, 0, _width, _height);
	}

	/**
	 * 清空屏幕为指定颜色
	 * 
	 * @param c
	 */
	public Pixmap clearDraw(int c) {
		if (_isClosed) {
			return this;
		}
		for (int i = 0; i < this._length; i++) {
			drawPoint(_drawPixels, i, c);
		}
		return this;
	}

	public Pixmap clearDraw(LColor color) {
		return clearDraw(color.getARGB());
	}

	public Pixmap setBackground(int color) {
		this._background = color;
		return this;
	}

	/**
	 * 清空屏幕
	 * 
	 */
	public Pixmap clearDraw() {
		return clearDraw(_background);
	}

	/**
	 * 清空屏幕
	 *
	 */
	public Pixmap clear() {
		for (int i = 0; i < _length; i++) {
			_drawPixels[i] = _transparent;
		}
		_dirty = true;
		return this;
	}

	/**
	 * 清空屏幕
	 * 
	 */
	public Pixmap fill() {
		return clearDraw(_baseColor);
	}

	/**
	 * 过滤指定颜色为目标颜色
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public Pixmap filter(int src, int dst) {
		for (int i = 0; i < _length; i++) {
			if (_drawPixels[i] == src) {
				_drawPixels[i] = dst;
			}
		}
		_dirty = true;
		return this;
	}

	/**
	 * 过滤指定颜色为目标颜色
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public Pixmap filter(LColor src, LColor dst) {
		return filter(src.getARGB(), dst.getARGB());
	}

	/**
	 * 过滤透明色为指定颜色
	 * 
	 * @param dst
	 * @return
	 */
	public Pixmap filter(int dst) {
		return filter(_transparent, dst);
	}

	/**
	 * 过滤透明色为指定颜色
	 * 
	 * @param dst
	 * @return
	 */
	public Pixmap filter(LColor dst) {
		return filter(_transparent, dst.getARGB());
	}

	/**
	 * 灰化Pixmap
	 * 
	 * @param mix
	 * @return
	 */
	public Pixmap greyScale(float mix) {
		mix = MathUtils.min(MathUtils.max(mix, 0f), 1f);
		for (int i = 0; i < _length; i++) {
			int color = _drawPixels[i];
			if (color != _transparent) {
				int[] rgba = LColor.getRGBAs(color);
				int r = rgba[0];
				int g = rgba[1];
				int b = rgba[2];
				float a = rgba[3] / 255f;
				float v = LColor.getLuminanceRGB(r, g, b);
				_drawPixels[i] = LColor.argb(a, v * mix + r * (1 - mix), v * mix + g * (1 - mix),
						v * mix + b * (1 - mix));
			} else {
				_drawPixels[i] = _transparent;
			}
		}
		return this;
	}

	/**
	 * 灰化
	 * 
	 * @return
	 */
	public Pixmap greyScale() {
		return greyScale(1f);
	}

	/**
	 * 过滤指定像素阀值
	 * 
	 * @param threshold
	 * @return
	 */
	public Pixmap threshold(int threshold) {
		threshold = (threshold | 127);
		for (int i = 0; i < _length; i++) {
			int color = _drawPixels[i];
			if (color != _transparent) {
				int[] rgba = LColor.getRGBAs(color);
				int r = rgba[0];
				int g = rgba[1];
				int b = rgba[2];
				float a = rgba[3] / 255f;
				float v = LColor.getLuminanceRGB(r, g, b) > threshold ? 255f : 0f;
				_drawPixels[i] = LColor.argb(a, v, v, v);
			} else {
				_drawPixels[i] = _transparent;
			}
		}
		return this;
	}

	/**
	 * 反色
	 * 
	 * @param mix
	 * @return
	 */
	public Pixmap invert(float mix) {
		mix = MathUtils.min(MathUtils.max(mix, 0f), 1f);
		for (int i = 0; i < _length; i++) {
			int color = _drawPixels[i];
			if (color != _transparent) {
				int[] rgba = LColor.getRGBAs(color);
				int r = rgba[0];
				int g = rgba[1];
				int b = rgba[2];
				float a = rgba[3] / 255f;
				_drawPixels[i] = LColor.argb(a, (255 - r) * mix + r * (1 - mix), (255 - g) * mix + g * (1 - mix),
						(255 - b) * mix + b * (1 - mix));
			} else {
				_drawPixels[i] = _transparent;
			}
		}
		return this;
	}

	/**
	 * 墨化
	 * 
	 * @param mix
	 * @return
	 */
	public Pixmap sepia(float mix) {
		mix = MathUtils.min(MathUtils.max(mix, 0f), 1f);
		for (int i = 0; i < _length; i++) {
			int color = _drawPixels[i];
			if (color != _transparent) {
				int[] rgba = LColor.getRGBAs(color);
				float r = rgba[0] / 255f;
				float g = rgba[1] / 255f;
				float b = rgba[2] / 255f;
				float a = rgba[3] / 255f;
				float nr = (0.393f * r + 0.769f * g + 0.189f * b) * mix + r * (1f - mix);
				float ng = (0.349f * r + 0.686f * g + 0.168f * b) * mix + g * (1f - mix);
				float nb = (0.272f * r + 0.534f * g + 0.131f * b) * mix + b * (1f - mix);
				_drawPixels[i] = LColor.argb(a, nr, ng, nb);
			} else {
				_drawPixels[i] = _transparent;
			}
		}
		return this;
	}

	/**
	 * 让当前pixmap中color乘以指定color
	 * 
	 * @param pixel
	 * @param trans
	 * @return
	 */
	public Pixmap multiply(LColor pixel, int trans) {
		if (pixel == null) {
			return this;
		}
		final LColor newColor = new LColor();
		for (int i = 0; i < _length; i++) {
			int color = _drawPixels[i];
			if (color != _transparent) {
				int[] rgba = LColor.getRGBAs(color);
				int r = rgba[0];
				int g = rgba[1];
				int b = rgba[2];
				int a = rgba[3];
				_drawPixels[i] = newColor.setColor(r, g, b, a).mulSelf(pixel).getARGB();
			} else {
				_drawPixels[i] = trans;
			}
		}
		return this;
	}

	public Pixmap multiply(LColor pixel) {
		return multiply(pixel, _transparent);
	}

	public Pixmap noise() {
		return noise(0.1f);
	}

	/**
	 * 为当前pixmap增加干扰点
	 * 
	 * @param n
	 * @return
	 */
	public Pixmap noise(float n) {
		final float dist = MathUtils.limit(1f - n, 0f, 1f);
		final LColor tmp = new LColor();
		final LColor color = LColor.getRandomRGBAColor(0f, 0.5f);
		int pixel = _transparent;
		for (int i = 0; i < _width; i++) {
			for (int j = 0; j < _height; j++) {
				if (MathUtils.random() > dist) {
					pixel = getPixel(i, j);
					pixel = tmp.setColor(pixel).mul(color).getARGB();
					putPixel(i, j, pixel);
				}
			}
		}
		return this;
	}

	public Pixmap distortionRand() {
		return distortion(1f, MathUtils.random(0f, 7f) + 3f, MathUtils.random(0f, 6f));
	}

	public Pixmap distortion() {
		return distortion(1f, 6f, 3f);
	}

	public Pixmap distortion(float dist, float period, float phase) {
		if (dist <= 0f) {
			dist = 1f;
		}
		for (int i = 0; i < _width; i++) {
			for (int j = 0; j < _height; j++) {
				int nX = distortionUpdate(dist, phase, period, _height, i, j);
				int nY = j;
				if (nX >= 0 && nX < _width && nY >= 0 && nY < _height) {
					putPixel(nX, nY, getPixel(i, j));
				}
			}
		}
		return this;
	}

	private static int distortionUpdate(float rank, float phase, float period, int w, int y, int x) {
		final float newX = MathUtils.PI * rank * x / w + phase;
		final float newY = MathUtils.sin(newX);
		return y + MathUtils.ifloor(newY * period);
	}

	/**
	 * 向指定坐标插入像素
	 * 
	 * @param x
	 * @param y
	 */
	public Pixmap putPixel(int x, int y) {
		return putPixel(x, y, _baseColor);
	}

	/**
	 * 向指定坐标插入像素
	 * 
	 * @param x
	 * @param y
	 */
	public Pixmap putPixel(int x, int y, int c) {
		if (_isClosed) {
			return this;
		}
		if (x < 0 || x >= _width || y < 0 || y >= _height) {
			return this;
		} else {
			drawPoint(x, y, c);
		}
		return this;
	}

	/**
	 * 获得指定区域的RGB色彩
	 * 
	 * @param pixels
	 * @return
	 */
	public int[] getRGB(int[] pixels) {
		getRGB(0, 0, _width, _height, pixels, 0, _width);
		return pixels;
	}

	/**
	 * 获得指定区域的RGB色彩
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public int[] getRGB(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		getRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	/**
	 * 获得指定区域的RGB色彩
	 * 
	 * @param offset
	 * @param stride
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public int[] getRGB(int offset, int stride, int x, int y, int width, int height) {
		int pixels[] = new int[width * height];
		getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	/**
	 * 获得指定区域的RGB色彩
	 * 
	 * @param x
	 * @param y
	 * @param arrays
	 * @return
	 */
	public int[] getRGB(int x, int y, int[] pixels) {
		int pixel = getPixel(x, y);
		if (pixels == null) {
			pixels = new int[3];
		}
		pixels[0] = (pixel >> 16) & 0xff;
		pixels[1] = (pixel >> 8) & 0xff;
		pixels[2] = (pixel) & 0xff;
		return pixels;
	}

	/**
	 * 获得指定区域的RGB色彩
	 * 
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @param rgbArray
	 * @param offset
	 * @param scansize
	 * @return
	 */
	public int[] getRGB(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize) {
		int yoff = offset;
		int off;
		if (rgbArray == null) {
			rgbArray = new int[offset + h * scansize];
		}
		for (int y = startY; y < startY + h; y++, yoff += scansize) {
			off = yoff;
			for (int x = startX; x < startX + w; x++) {
				rgbArray[off++] = getPixel(x, y);
			}
		}
		return rgbArray;
	}

	/**
	 * 设定指定区域的RGB色彩
	 * 
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @param rgbArray
	 * @param offset
	 * @param scansize
	 */
	public Pixmap setRGB(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize) {
		int yoff = offset;
		int off;
		for (int y = startY; y < startY + h; y++, yoff += scansize) {
			off = yoff;
			for (int x = startX; x < startX + w; x++) {
				int pixel = rgbArray[off++];
				putPixel(x, y, pixel);
			}
		}
		return this;
	}

	/**
	 * 设定指定区域的RGB色彩
	 * 
	 * @param pixels
	 * @param width
	 * @param height
	 */
	public Pixmap setRGB(int[] pixels, int width, int height) {
		return setRGB(0, 0, width, height, pixels, 0, width);
	}

	/**
	 * 设定指定区域的RGB色彩
	 * 
	 * @param pixels
	 * @param offset
	 * @param stride
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Pixmap setRGB(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		return setRGB(x, y, width, height, pixels, offset, stride);
	}

	/**
	 * 设定指定区域的RGB色彩
	 * 
	 * @param pixels
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public int[] setRGB(int[] pixels, int x, int y, int w, int h) {
		setRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	/**
	 * 设定指定区域的RGB色彩
	 * 
	 * @param x
	 * @param y
	 * @param pixels
	 */
	public Pixmap setRGB(int x, int y, int[] pixels) {
		return putPixel(x, y, (255 << 24) | (pixels[0] << 16) | (pixels[1] << 8) | pixels[2]);
	}

	/**
	 * 设定指定区域的RGB色彩
	 * 
	 * @param rgb
	 * @param x
	 * @param y
	 */
	public Pixmap setRGB(int rgb, int x, int y) {
		return putPixel(x, y, rgb);
	}

	/**
	 * 获得指定位置像素
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPixel(int x, int y) {
		if (_isClosed) {
			return -1;
		}
		if (x < 0 || x >= _width || y < 0 || y >= _height) {
			return -1;
		} else {
			return _drawPixels[y * _width + x];
		}
	}

	/**
	 * 让像素沿着X轴方向旋转
	 */
	public Pixmap mirrorX() {
		if (_isClosed) {
			return this;
		}
		int h = this._height;
		int w = MathUtils.floor(this._width / (float) 2);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				this.drawPoint(this._width - x - 1, y, this.getData(x, y));
			}
		}
		return this;
	}

	/**
	 * 让像素沿着Y轴方向旋转
	 */
	public Pixmap mirrorY() {
		if (_isClosed) {
			return this;
		}
		int h = MathUtils.floor(this._height / (float) 2);
		int w = this._width;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				this.drawPoint(x, this._height - y - 1, this.getData(x, y));
			}
		}
		return this;
	}

	/**
	 * 镜像翻转当前Pixmap为新图
	 * 
	 * @return
	 */
	public Pixmap mirror() {
		return mirror(true, false);
	}

	/**
	 * 水平翻转当前Pixmap为新图
	 * 
	 * @return
	 */
	public Pixmap flip() {
		return mirror(false, true);
	}

	/**
	 * 翻转当前Pixmap为新图
	 * 
	 * @param mirror
	 * @param flip
	 * @return
	 */
	public Pixmap mirror(boolean mirror, boolean flip) {
		if (_isClosed) {
			return null;
		}
		Pixmap pixel = new Pixmap(_width, _height, _hasAlpha);
		int[] pixels = pixel._drawPixels;
		int index = 0;
		int pixelIndex = (mirror ? _width - 1 : 0) + (flip ? _width * (_height - 1) : 0);
		int flag = (mirror ? -1 : 1);
		int offset = (mirror ? _width * 2 : 0) + (flip ? -_width * 2 : 0);
		pixel._width = _width;
		pixel._height = _height;
		pixel._transparent = _transparent;
		for (int j = 0; j < _height;) {
			for (int i = 0; i < _width;) {
				pixels[pixelIndex] = _drawPixels[index];
				i++;
				index++;
				pixelIndex += flag;
			}
			j++;
			pixelIndex += offset;
		}
		return pixel;
	}

	/**
	 * 将当前Pixmap克隆为新的Pixmap
	 * 
	 * @return
	 */
	public Pixmap cpy() {
		return copy(0, 0, _width, _height);
	}

	/**
	 * 从当前Pixmap中copy指定范围像素为新的Pixmap
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public Pixmap copy(int x, int y, int w, int h) {
		if (_isClosed) {
			return null;
		}
		Pixmap pixel = new Pixmap(w, h, _hasAlpha);
		pixel._width = w;
		pixel._height = h;
		pixel.drawPixmap(this, 0, 0, w, h, x, y);
		if (x < 0) {
			w -= x;
			x = 0;
		}
		if (y < 0) {
			h -= y;
			y = 0;
		}
		if (x + w > _width) {
			w -= (x + w) - _width;
		}
		if (y + h > _height) {
			h -= (y + h) - _height;
		}
		try {
			for (int size = 0; size < h; size++) {
				System.arraycopy(_drawPixels, (y + size) * _width + x, pixel._drawPixels, size * pixel._width, w);
			}
			_dirty = true;
		} catch (IndexOutOfBoundsException e) {
		}
		return pixel;
	}

	/**
	 * 拆分当前Pixmap为指定数量的瓦片
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Pixmap[] split(int row, int col) {
		if (_isClosed) {
			return null;
		}
		int count = row * col;
		int w = _width / row;
		int h = _height / col;

		Pixmap[] pixels = new Pixmap[count];
		for (int i = 0; i < count; i++) {
			int x = (i % row) * w;
			int y = (i / row) * h;
			pixels[i] = copy(x, y, w, h);
		}

		return pixels;
	}

	/**
	 * 修正图像显示位置
	 * 
	 * @param x
	 * @param y
	 */
	public Pixmap translate(int x, int y) {
		if (_isClosed) {
			return this;
		}
		_translateX = x;
		_translateY = y;
		if (defClip != null) {
			defClip.x += _translateX;
			defClip.y += _translateY;
			clip.x = MathUtils.min(clip.x + _translateX, _width);
			clip.y = MathUtils.min(clip.y + _translateY, _height);
			clip.width = MathUtils.min(clip.width + _translateX, _width - _translateX);
			clip.height = MathUtils.min(clip.height + _translateY, _height - _translateY);
		}
		return this;
	}

	public float getAlpha() {
		return alpha();
	}

	// 以实际渲染颜色的alpha为优先返回
	public float alpha() {
		return ((_baseColor >> 24) & 0xFF) / 255f;
	}

	public Pixmap setAlpha(float alpha) {
		if (alpha < 0.01f) {
			alpha = 0.01f;
			_baseAlpha = 0;
		} else if (alpha > 1f) {
			alpha = 1f;
			_baseAlpha = 1f;
		} else {
			this._baseAlpha = alpha;
		}
		int ialpha = (int) (0xFF * MathUtils.clamp(alpha, 0, 1));
		this._baseColor = (ialpha << 24) | (_baseColor & 0xFFFFFF);
		return this;
	}

	public int color() {
		return _baseColor;
	}

	public LColor getColor() {
		return new LColor(_baseColor);
	}

	public Pixmap resetColor() {
		setColor(LColor.DEF_COLOR);
		return this;
	}

	public Pixmap setColor(LColor color) {
		int argb = color.getARGB();
		setColor(argb);
		return this;
	}

	public Pixmap setColor(int r, int g, int b) {
		return setColor(LColor.getRGB(r, g, b));
	}

	public Pixmap setColor(int r, int g, int b, int a) {
		return setColor(LColor.getARGB(r, g, b, a));
	}

	public Pixmap setColor(float r, float g, float b, float a) {
		return setColor(LColor.getARGB((int) (r > 1 ? r : r * 255), (int) (g > 1 ? g : r * 255),
				(int) (b > 1 ? b : b * 255), (int) (a > 1 ? a : a * 255)));
	}

	public Pixmap setColor(int c) {
		if (this._baseAlpha != 1f) {
			this._baseColor = c;
			int ialpha = (int) (0xFF * MathUtils.clamp(this._baseAlpha, 0, 1));
			this._baseColor = (ialpha << 24) | (_baseColor & 0xFFFFFF);
		} else {
			this._baseColor = c;
		}
		return this;
	}

	public Pixmap setPaintMode() {
		xorColor = null;
		xorMode = false;
		xorRGB = 0;
		return this;
	}

	public Pixmap setXORMode(LColor c) {
		xorColor = c;
		xorMode = xorColor != null;
		xorRGB = xorMode ? xorColor.getRGB() : 0;
		return this;
	}

	public Pixmap setXORMode(int red, int green, int blue) {
		this.xorColor = new LColor(red, green, blue);
		this.xorMode = true;
		this.xorRGB = ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
		return this;
	}

	public RectI getClipBounds() {
		if (_isClosed) {
			return null;
		}
		return defClip != null ? new RectI(defClip.x, defClip.y, defClip.width, defClip.height)
				: new RectI(0, 0, _width, _height);
	}

	public Pixmap clipRect(int x, int y, int width, int height) {
		if (_isClosed) {
			return this;
		}
		if (defClip != null) {
			defClip = defClip.getIntersection(new RectI(x, y, width, height));
			clip = clip.getIntersection(new RectI(x + _translateX, y + _translateY, width, height));
		} else {
			defClip = new RectI(x, y, width, height);
			clip = new RectI(x + _translateX, y + _translateY, width, height);
		}
		return this;
	}

	public Pixmap setClip(int x, int y, int width, int height) {
		if (_isClosed) {
			return this;
		}
		if (defClip == null) {
			defClip = new RectI(x, y, width, height);
		} else {
			defClip.set(x, y, width, height);
		}
		clip = new RectI(MathUtils.max(x + _translateX, 0), MathUtils.max(y + _translateY, 0),
				MathUtils.min(width, width - _translateX), MathUtils.min(height, height - _translateY));
		return this;
	}

	public RectI getClip() {
		if (_isClosed) {
			return null;
		}
		return getClipBounds();
	}

	public Pixmap setClip(RectI clip) {
		return setClip(clip.x, clip.y, clip.width, clip.height);
	}

	public Pixmap drawShapeImpl(Shape shape, int x1, int y1) {
		if (shape == null) {
			return this;
		}
		final float[] points = shape.getPoints();
		int size = points.length;
		int len = size / 2;
		final int[] xps = new int[len];
		final int[] yps = new int[len];
		for (int i = 0, j = 0; i < size; i += 2, j++) {
			xps[j] = (int) (points[i] + x1);
			yps[j] = (int) (points[i + 1] + y1);
		}
		drawPolyline(xps, yps, len);
		drawLine(xps[len - 1], yps[len - 1], xps[0], yps[0]);
		return this;
	}

	public Pixmap fillShapeImpl(Pixmap pixmap, Shape shape, int x1, int y1) {
		if (shape == null) {
			return this;
		}
		final float[] points = shape.getPoints();
		int size = points.length;
		int len = size / 2;
		final int[] xps = new int[len];
		final int[] yps = new int[len];
		for (int i = 0, j = 0; i < size; i += 2, j++) {
			xps[j] = (int) (points[i] + x1);
			yps[j] = (int) (points[i + 1] + y1);
		}
		RectI bounds = CollisionHelper.getIntersection(CollisionHelper.setBoundingBox(temp_rect, xps, yps, len), clip,
				temp_rect);
		for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
			for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
				if (CollisionHelper.contains(xps, yps, len, bounds, x, y)) {
					drawPoint(x, y, pixmap.getData(x, y));
				}
			}
		}
		return this;
	}

	public Pixmap fillShapeImpl(Shape shape, int x1, int y1) {
		if (shape == null) {
			return this;
		}
		final float[] points = shape.getPoints();
		int size = points.length;
		int len = size / 2;
		final int[] xps = new int[len];
		final int[] yps = new int[len];
		for (int i = 0, j = 0; i < size; i += 2, j++) {
			xps[j] = (int) (points[i] + x1);
			yps[j] = (int) (points[i + 1] + y1);
		}
		RectI bounds = CollisionHelper.getIntersection(CollisionHelper.setBoundingBox(temp_rect, xps, yps, len), clip,
				temp_rect);
		for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
			for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
				if (CollisionHelper.contains(xps, yps, len, bounds, x, y)) {
					drawPoint(x, y);
				}
			}
		}
		return this;
	}

	/**
	 * 绘制五角星
	 */
	public Pixmap drawSixStart(LColor color, int x, int y, int r) {
		if (_isClosed) {
			return this;
		}
		setColor(color);
		drawTriangle(color, x, y, r);
		drawRTriangle(color, x, y, r);
		return this;
	}

	/**
	 * 绘制正三角
	 */
	public Pixmap drawTriangle(LColor color, int x, int y, int r) {
		if (_isClosed) {
			return this;
		}
		int x1 = x;
		int y1 = y - r;
		int x2 = x - (int) (r * MathUtils.cos(MathUtils.PI / 6));
		int y2 = y + (int) (r * MathUtils.sin(MathUtils.PI / 6));
		int x3 = x + (int) (r * MathUtils.cos(MathUtils.PI / 6));
		int y3 = y + (int) (r * MathUtils.sin(MathUtils.PI / 6));
		int[] xpos = new int[3];
		xpos[0] = x1;
		xpos[1] = x2;
		xpos[2] = x3;
		int[] ypos = new int[3];
		ypos[0] = y1;
		ypos[1] = y2;
		ypos[2] = y3;
		setColor(color);
		fillPolygon(xpos, ypos, 3);
		return this;
	}

	/**
	 * 绘制倒三角
	 */
	public Pixmap drawRTriangle(LColor color, int x, int y, int r) {
		if (_isClosed) {
			return this;
		}
		int x1 = x;
		int y1 = y + r;
		int x2 = x - (int) (r * MathUtils.cos(MathUtils.PI / 6f));
		int y2 = y - (int) (r * MathUtils.sin(MathUtils.PI / 6f));
		int x3 = x + (int) (r * MathUtils.cos(MathUtils.PI / 6f));
		int y3 = y - (int) (r * MathUtils.sin(MathUtils.PI / 6f));
		int[] xpos = new int[3];
		xpos[0] = x1;
		xpos[1] = x2;
		xpos[2] = x3;
		int[] ypos = new int[3];
		ypos[0] = y1;
		ypos[1] = y2;
		ypos[2] = y3;
		setColor(color);
		fillPolygon(xpos, ypos, 3);
		return this;
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param ts
	 */
	public Pixmap fillTriangle(Triangle2f[] ts) {
		return fillTriangle(ts, 0, 0);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param ts
	 * @param x
	 * @param y
	 */
	public Pixmap fillTriangle(Triangle2f[] ts, int x, int y) {
		if (_isClosed) {
			return this;
		}
		if (ts == null) {
			return this;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			fillTriangle(ts[i], x, y);
		}
		return this;
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param t
	 */
	public Pixmap fillTriangle(Triangle2f t) {
		return fillTriangle(t, 0, 0);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param t
	 * @param x
	 * @param y
	 */
	public Pixmap fillTriangle(Triangle2f t, int x, int y) {
		if (_isClosed) {
			return this;
		}
		if (t == null) {
			return this;
		}
		return fillTriangle(t.xpoints[0], t.ypoints[0], t.xpoints[1], t.ypoints[1], t.xpoints[2], t.ypoints[2]);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @return
	 */
	public Pixmap fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		if (_isClosed) {
			return this;
		}
		int[] xpos = new int[3];
		int[] ypos = new int[3];
		xpos[0] = (int) x1;
		xpos[1] = (int) x2;
		xpos[2] = (int) x3;
		ypos[0] = (int) y1;
		ypos[1] = (int) y2;
		ypos[2] = (int) y3;
		fillPolygon(xpos, ypos, 3);
		return this;
	}

	/**
	 * 绘制一组三角
	 * 
	 * @param ts
	 */
	public Pixmap drawTriangle(Triangle2f[] ts) {
		return drawTriangle(ts, 0, 0);
	}

	/**
	 * 绘制一组三角
	 * 
	 * @param ts
	 * @param x
	 * @param y
	 */
	public Pixmap drawTriangle(Triangle2f[] ts, int x, int y) {
		if (_isClosed) {
			return this;
		}
		if (ts == null) {
			return this;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			drawTriangle(ts[i], x, y);
		}
		return this;
	}

	/**
	 * 绘制三角
	 * 
	 * @param t
	 */
	public Pixmap drawTriangle(Triangle2f t) {
		return drawTriangle(t, 0, 0);
	}

	/**
	 * 绘制三角
	 * 
	 * @param t
	 * @param x
	 * @param y
	 */
	public Pixmap drawTriangle(Triangle2f t, int x, int y) {
		if (_isClosed) {
			return this;
		}
		if (t == null) {
			return this;
		}
		int[] xpos = new int[3];
		int[] ypos = new int[3];
		xpos[0] = x + (int) t.xpoints[0];
		xpos[1] = x + (int) t.xpoints[1];
		xpos[2] = x + (int) t.xpoints[2];
		ypos[0] = y + (int) t.ypoints[0];
		ypos[1] = y + (int) t.ypoints[1];
		ypos[2] = y + (int) t.ypoints[2];
		drawPolygon(xpos, ypos, 3);
		return this;
	}

	/**
	 * 从指定位置开始截取指定大小像素到指定位置
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param dx
	 * @param dy
	 */
	public Pixmap copyArea(int x, int y, int width, int height, int dx, int dy) {
		if (_isClosed) {
			return this;
		}
		x += _translateX;
		y += _translateY;

		int xStart = x;
		int xEnd = x + width - 1;
		int xStep = 1;
		if (dx < 0) {
			xStart = x + width - 1;
			xEnd = x;
			xStep = -1;
		}
		int yStart = y;
		int yEnd = y + height - 1;
		int yStep = 1;
		if (dy < 0) {
			yStart = y + height - 1;
			yEnd = y;
			yStep = -1;
		}
		for (x = xStart; x <= xEnd; x += xStep) {
			for (y = yStart; y <= yEnd; y += yStep) {
				if (!inside(x + dx, y + dy) && x >= 0 && x < width && y >= 0 && y < height) {
					_drawPixels[x + dx + (y + dy) * width] = _drawPixels[x + y * width];
				}
			}
		}
		_dirty = true;
		return this;
	}

	/**
	 * 绘制一条直线
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public Pixmap drawLine(int x1, int y1, int x2, int y2) {
		if (_isClosed) {
			return this;
		}

		x1 += _translateX;
		y1 += _translateY;
		x2 += _translateX;
		y2 += _translateY;

		int dx = x2 - x1;
		int dy = y2 - y1;

		if (dx == 0) {
			if (y1 < y2) {
				drawVerticalLine(x1, y1, y2);
			} else {
				drawVerticalLine(x1, y2, y1);
			}
		} else if (dy == 0) {
			if (x1 < x2) {
				drawLineImpl(x1, x2, y1);
			} else {
				drawLineImpl(x2, x1, y1);
			}
		} else {
			boolean swapXY = false;
			int dxNeg = 1;
			int dyNeg = 1;
			boolean negativeSlope = false;
			if (MathUtils.abs(dy) > MathUtils.abs(dx)) {
				int temp = x1;
				x1 = y1;
				y1 = temp;
				temp = x2;
				x2 = y2;
				y2 = temp;
				dx = x2 - x1;
				dy = y2 - y1;
				swapXY = true;
			}

			if (x1 > x2) {
				int temp = x1;
				x1 = x2;
				x2 = temp;
				temp = y1;
				y1 = y2;
				y2 = temp;
				dx = x2 - x1;
				dy = y2 - y1;
			}

			if (dy * dx < 0) {
				if (dy < 0) {
					dyNeg = -1;
					dxNeg = 1;
				} else {
					dyNeg = 1;
					dxNeg = -1;
				}
				negativeSlope = true;
			}

			int d = 2 * (dy * dyNeg) - (dx * dxNeg);
			int incrH = 2 * dy * dyNeg;
			int incrHV = 2 * ((dy * dyNeg) - (dx * dxNeg));
			int x = x1;
			int y = y1;
			int tempX = x;
			int tempY = y;

			if (swapXY) {
				int temp = x;
				x = y;
				y = temp;
			}

			drawPoint(x, y);
			x = tempX;
			y = tempY;

			while (x < x2) {
				if (d <= 0) {
					x++;
					d += incrH;
				} else {
					d += incrHV;
					x++;
					if (!negativeSlope) {
						y++;
					} else {
						y--;
					}
				}

				tempX = x;
				tempY = y;
				if (swapXY) {
					int temp = x;
					x = y;
					y = temp;
				}
				drawPoint(x, y);
				x = tempX;
				y = tempY;
			}
		}
		return this;
	}

	/**
	 * 虚线绘制
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param divisions
	 * @param width
	 * @return
	 */
	public Pixmap drawDashLine(int x1, int y1, int x2, int y2, int divisions) {
		int dx = x2 - x1, dy = y2 - y1;
		for (int i = 0; i < divisions; i++) {
			if (i % 2 == 0) {
				drawLine(x1 + (int) ((float) i / divisions) * dx, y1 + (int) ((float) i / divisions) * dy,
						x1 + (int) ((i + 1f) / divisions) * dx, y1 + (int) ((i + 1f) / divisions) * dy);
			}
		}
		return this;
	}

	public Pixmap drawAngleLine(int x, int y, float angle, float length) {
		_pointLocation.set(1f).setLength(length).setAngle(angle);
		return drawLine(x, y, x + _pointLocation.x(), y + _pointLocation.y());
	}

	public Pixmap drawDashCircle(int x, int y, float radius) {
		float scaleFactor = 0.6f;
		int sides = 10 + MathUtils.floor(radius * scaleFactor);
		if (sides % 2 != 0) {
			sides++;
		}

		_pointLocation.set(0f);

		for (int i = 0; i < sides; i++) {
			if (i % 2 == 0) {
				continue;
			}
			_pointLocation.set(radius, 0).setAngle(360f / sides * i + 90);
			int x1 = _pointLocation.x();
			int y1 = _pointLocation.y();

			_pointLocation.set(radius, 0).setAngle(360f / sides * (i + 1) + 90);

			drawLine(x1 + x, y1 + y, _pointLocation.x() + x, _pointLocation.y() + y);
		}
		return this;
	}

	public Pixmap drawSpikes(int x, int y, float radius, float length, int spikes, float rot) {
		_pointLocation.set(0f, 1f);
		float step = 360f / spikes;

		for (int i = 0; i < spikes; i++) {
			_pointLocation.setAngle(i * step + rot);
			_pointLocation.setLength(radius);
			int x1 = _pointLocation.x(), y1 = _pointLocation.y();
			_pointLocation.setLength(radius + length);

			drawLine(x + x1, y + y1, x + _pointLocation.x(), y + _pointLocation.y());
		}
		return this;
	}

	/**
	 * 绘制弧线
	 * 
	 * @param x1
	 * @param y1
	 * @param cx1
	 * @param cy1
	 * @param cx2
	 * @param cy2
	 * @param x2
	 * @param y2
	 * @param segments
	 * @param width
	 * @return
	 */
	public Pixmap drawCurve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2,
			int segments, float width) {

		final float subdivstep = 1f / segments;
		final float subdiv_stepa = subdivstep * subdivstep;
		final float subdiv_stepb = subdivstep * subdivstep * subdivstep;

		final float pre1 = 3 * subdivstep;
		final float pre2 = 3 * subdiv_stepa;
		final float pre4 = 6 * subdiv_stepa;
		final float pre5 = 6 * subdiv_stepb;

		final float tmp1x = x1 - cx1 * 2 + cx2;
		final float tmp1y = y1 - cy1 * 2 + cy2;

		final float tmp2x = (cx1 - cx2) * 3 - x1 + x2;
		final float tmp2y = (cy1 - cy2) * 3 - y1 + y2;

		float fx = x1;
		float fy = y1;

		float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_stepb;
		float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_stepb;

		float ddfx = tmp1x * pre4 + tmp2x * pre5;
		float ddfy = tmp1y * pre4 + tmp2y * pre5;

		float dddfx = tmp2x * pre5;
		float dddfy = tmp2y * pre5;

		for (; segments-- > 0;) {
			float fxold = fx, fyold = fy;
			fx += dfx;
			fy += dfy;
			dfx += ddfx;
			dfy += ddfy;
			ddfx += dddfx;
			ddfy += dddfy;
			drawLine((int) fxold, (int) fyold, (int) fx, (int) fy);
		}

		drawLine((int) fx, (int) fy, (int) x2, (int) y2);
		return this;
	}

	/**
	 * 绘制一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 */
	public Pixmap drawRect(int x1, int y1, int w1, int h1) {
		if (_isClosed) {
			return this;
		}
		int tempX = x1;
		int tempY = y1;
		int tempWidth = x1 + w1;
		int tempHeight = y1 + h1;
		if (tempX > tempWidth) {
			x1 = tempX;
			tempX = tempWidth;
			tempWidth = x1;
		}
		if (tempY > tempHeight) {
			y1 = tempY;
			tempY = tempHeight;
			tempHeight = y1;
		}
		drawLine(tempX, tempY, tempWidth, tempY);
		drawLine(tempX, tempY + 1, tempX, tempHeight);
		drawLine(tempWidth, tempHeight, tempX + 1, tempHeight);
		drawLine(tempWidth, tempHeight - 1, tempWidth, tempY + 1);
		return this;
	}

	/**
	 * 绘制一个围绕指定区域旋转的矩形选框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param arcWidth
	 * @param arcHeight
	 */
	public Pixmap drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		if (_isClosed) {
			return this;
		}
		drawLine(x + arcWidth / 2, y, x + width - arcWidth / 2, y);
		drawLine(x, y + arcHeight / 2, x, y + height - arcHeight / 2);
		drawLine(x + arcWidth / 2, y + height, x + width - arcWidth / 2, y + height);
		drawLine(x + width, y + arcHeight / 2, x + width, y + height - arcHeight / 2);
		drawArc(x, y, arcWidth, arcHeight, 90, 90);
		drawArc(x + width - arcWidth, y, arcWidth, arcHeight, 0, 90);
		drawArc(x, y + height + -arcHeight, arcWidth, arcHeight, 180, 90);
		drawArc(x + width - arcWidth, y + height + -arcHeight, arcWidth, arcHeight, 270, 90);
		return this;
	}

	public Pixmap fillRoundRect(int x, int y, int width, int height, int radius) {
		if (radius < 0) {
			throw new LSysException("radius > 0");
		}
		if (radius == 0) {
			fillRect(x, y, width, height);
			return this;
		}
		int mr = MathUtils.min(width, height) / 2;
		if (radius > mr) {
			radius = mr;
		}
		int d = radius * 2;
		int w = width - d;
		int h = height - d;
		if (w > 0 && h > 0) {
			fillRect(x + radius, y, w, radius);
			fillRect(x, y + radius, radius, h);
			fillRect(x + width - radius, y + radius, radius, h);
			fillRect(x + radius, y + height - radius, w, radius);
			fillRect(x + radius, y + radius, w, h);
		}
		fillArc(x + width - d, y + height - d, d, d, 0, 90);
		fillArc(x, y + height - d, d, d, 90, 180);
		fillArc(x + width - d, y, d, d, 270, 360);
		fillArc(x, y, d, d, 180, 270);
		return this;
	}

	/**
	 * 填充一个围绕指定区域旋转的矩形选框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param arcWidth
	 * @param arcHeight
	 */
	public Pixmap fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		if (_isClosed) {
			return this;
		}
		int w = width - arcWidth;
		int h = height - arcHeight;
		if (w > 0 && h > 0) {
			fillRect(x + arcWidth / 2, y, w, height);
			fillRect(x, y + arcHeight / 2 - 1, arcWidth / 2, h);
			fillRect(x + width - arcWidth / 2, y + arcHeight / 2 - 1, arcWidth / 2, h);
		}
		fillArc(x + 1, y, arcWidth - 1, arcHeight - 1, 90, 90);
		fillArc(x + width - arcWidth - 1, y, arcWidth - 1, arcHeight - 1, 0, 90);
		fillArc(x + 1, y + height + -arcHeight, arcWidth - 1, arcHeight - 1, 180, 90);
		fillArc(x + width - arcWidth - 1, y + height + -arcHeight, arcWidth - 1, arcHeight - 1, 270, 90);
		return this;
	}

	/**
	 * 将一个指定的Pixmap绘制到当前Pixmap
	 * 
	 * @param pixel
	 * @param x
	 * @param y
	 */
	public Pixmap drawPixmap(Pixmap pixel, int x, int y) {
		if (pixel == null) {
			return this;
		} else {
			drawPixmap(pixel, x, y, pixel._width, pixel._height, 0, 0);
			return this;
		}
	}

	/**
	 * 将一个指定的Pixmap绘制到当前Pixmap
	 * 
	 * @param pixel
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param offsetX
	 * @param offsetY
	 */
	public Pixmap drawPixmap(Pixmap pixel, int x, int y, int w, int h, int offsetX, int offsetY) {
		if (_isClosed) {
			return this;
		}

		x += _translateX;
		y += _translateY;

		int[] currentPixels = pixel._drawPixels;
		int transparent = pixel._transparent;
		if (x < 0) {
			w += x;
			offsetX -= x;
			x = 0;
		}
		if (y < 0) {
			h += y;
			offsetY -= y;
			y = 0;
		}
		if (x + w > _width) {
			w = _width - x;
		}
		if (y + h > _height) {
			h = _height - y;
		}
		if (w < 0 || h < 0) {
			return this;
		}
		if (transparent < 0) {
			for (int size = 0; size < h; size++) {
				System.arraycopy(currentPixels, (offsetY + size) * pixel._width + offsetX, _drawPixels,
						(y + size) * _width + x, w);
			}
			_dirty = true;
		} else {
			int findIndex = y * _width + x;
			int drawIndex = offsetY * pixel._width + offsetX;
			int moveFind = _width - w;
			int moveDraw = pixel._width - w;
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w;) {
					if (inside(j, i)) {
						continue;
					}
					if (currentPixels[drawIndex] != transparent) {
						drawPoint(_drawPixels, findIndex, currentPixels[drawIndex]);
					}
					j++;
					findIndex++;
					drawIndex++;
				}
				findIndex += moveFind;
				drawIndex += moveDraw;
			}
		}
		return this;
	}

	/**
	 * 将一个指定的Pixmap绘制到当前Pixmap，并扩展为指定大小
	 * 
	 * @param pixel
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public Pixmap drawPixmap(Pixmap pixel, int x, int y, int w, int h) {
		if (pixel == null) {
			return this;
		} else {
			drawPixmap(pixel, x, y, w, h, 0, 0, pixel._width, pixel._height);
			return this;
		}
	}

	/**
	 * 将一个指定的Pixmap绘制到当前Pixmap，并截取为指定大小
	 * 
	 * @param img
	 * @param dstX
	 * @param dstY
	 * @param dstWidth
	 * @param dstHeight
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 */
	public Pixmap drawPixmap(Pixmap img, int dstX, int dstY, int dstWidth, int dstHeight, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		if (_isClosed || img == null || img._isClosed) {
			return this;
		}

		dstX += _translateX;
		dstY += _translateY;
		srcX += _translateX;
		srcY += _translateY;

		if (dstWidth <= 0 || dstHeight <= 0 || srcWidth <= 0 || srcHeight <= 0) {
			return this;
		}
		if (dstWidth == srcWidth && dstHeight == srcHeight) {
			drawPixmap(img, dstX, dstY, dstWidth, dstHeight, srcX, srcY);
			return this;
		}

		int[] currentPixels = img.getData();

		int spitch = img._width;
		int dpitch = this._width;

		float x_ratio = ((float) srcWidth - 1) / dstWidth;
		float y_ratio = ((float) srcHeight - 1) / dstHeight;

		int dx = dstX;
		int dy = dstY;
		int sx = srcX;
		int sy = srcY;
		int i = 0;
		int j = 0;

		for (; i < dstHeight; i++) {
			sy = (int) (i * y_ratio) + srcY;
			dy = i + dstY;

			if (sy < 0 || dy < 0) {
				continue;
			}
			if (sy >= img._height || dy >= this._height) {
				break;
			}

			for (j = 0; j < dstWidth; j++) {
				sx = (int) (j * x_ratio) + srcX;
				dx = j + dstX;
				if (sx < 0 || dx < 0) {
					continue;
				}
				if (sx >= img._width || dx >= this._width) {
					break;
				}

				int src_ptr = sx + sy * spitch;
				int dst_ptr = dx + dy * dpitch;
				int src_pixel = currentPixels[src_ptr];

				drawPoint(_drawPixels, dst_ptr, src_pixel, _transparent);

			}
		}

		return this;
	}

	public Pixmap fillRect(int x, int y, int width, int height) {
		return fillRect(x, y, width, height, _baseColor);
	}

	public Pixmap fillRect(int x, int y, int width, int height, int color) {
		if (_isClosed) {
			return this;
		}
		if (x == 0 && y == 0 && width == _width && height == _height && _composite == -1) {
			clearDraw(color);
			return this;
		}
		int maxX = MathUtils.min(x + width - 1 + _translateX, clip.x + clip.width - 1);
		int maxY = MathUtils.min(y + height - 1 + _translateY, clip.y + clip.height - 1);
		for (int i = MathUtils.max(y + _translateY, clip.y); i <= maxY; i++) {
			for (int j = MathUtils.max(x + _translateX, clip.x); j <= maxX; j++) {
				drawPoint(j, i, color);
			}
		}
		return this;
	}

	public Pixmap fillRectTilted(int xpos, int ypos, int width, int height, int color, int xtilt, int ytilt) {
		if (_isClosed) {
			return this;
		}
		int maxX = MathUtils.min(xpos + width - 1 + _translateX, clip.x + clip.width - 1);
		int maxY = MathUtils.min(ypos + height - 1 + _translateY, clip.y + clip.height - 1);
		for (int y = MathUtils.max(ypos + _translateY, clip.y); y <= maxY; y++) {
			for (int x = MathUtils.max(xpos + _translateX, clip.x); x <= maxX; x++) {
				int ypix = (x - xpos) * ytilt;
				int xpix = (y - ypos) * xtilt;
				if ((x + xpix >= 0) && (x + xpix < this._width) && (y + ypix >= 0) && (y + ypix < this._height)) {
					drawPoint(x + xpix, y + ypix, color);
				}
			}
		}
		return this;
	}

	public Pixmap drawColumn(int xpos, int ypos, int w, int h, int p, int color1, int color2, int color3) {
		fillRect(xpos, ypos, w, h, color1);
		fillRectTilted(xpos - p, ypos - p, p, h, color2, 0, 1);
		fillRectTilted(xpos - p, ypos - p, w, p, color3, 1, 0);
		return this;
	}

	public Pixmap clearRect(int x, int y, int width, int height) {
		return fillRect(x, y, width, height);
	}

	/**
	 * 绘制一个椭圆形
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Pixmap drawOval(int x, int y, int width, int height) {
		if (_isClosed) {
			return this;
		}
		drawCircle(x, y, width, height, false, new CircleUpdate() {
			@Override
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawPoint(xLeft, yTop);
				drawPoint(xRight, yTop);
				drawPoint(xLeft, yBottom);
				drawPoint(xRight, yBottom);
			}
		});
		return this;
	}

	public Pixmap drawOval(int x, int y, int width, int height, int c) {
		int tmp = color();
		setColor(c);
		drawOval(x, y, width, height);
		setColor(tmp);
		return this;
	}

	public Pixmap drawOval(int x, int y, int width, int height, LColor color) {
		return drawOval(x, y, width, height, color == null ? LColor.DEF_COLOR : color.getARGB());
	}

	/**
	 * 填充一个椭圆形
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Pixmap fillOval(int x, int y, int width, int height) {
		if (_isClosed) {
			return this;
		}
		drawCircle(x, y, width, height, true, new CircleUpdate() {
			@Override
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawLineImpl(xLeft, xRight, yTop);
				if (yTop != yBottom) {
					drawLineImpl(xLeft, xRight, yBottom);
				}
			}
		});
		return this;
	}

	public Pixmap fillOval(int x, int y, int width, int height, int c) {
		int tmp = color();
		setColor(c);
		fillOval(x, y, width, height);
		setColor(tmp);
		return this;
	}

	public Pixmap fillOval(int x, int y, int width, int height, LColor color) {
		return fillOval(x, y, width, height, color == null ? LColor.DEF_COLOR : color.getARGB());
	}

	/**
	 * 绘制一个弧线
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param start
	 * @param arcAngle
	 */

	public Pixmap drawArc(int x, int y, int width, int height, int start, int arcAngle) {
		if (_isClosed) {
			return this;
		}
		if (arcAngle == 0) {
			return this;
		}
		if (arcAngle < 0) {
			start = 360 - arcAngle;
			arcAngle = 360 + arcAngle;
		}
		start %= 360;
		if (start < 0) {
			start += 360;
		}
		if (arcAngle % 360 == 0) {
			drawOval(x, y, width, height);
			return this;
		} else {
			arcAngle %= 360;
		}
		final int startAngle = arcAngle > 0 ? start
				: (start + arcAngle < 0 ? start + arcAngle + 360 : start + arcAngle);

		final int centerX = x + _translateX + width / 2;
		final int centerY = y + _translateY + height / 2;
		final int xPoints[] = new int[7];
		final int yPoints[] = new int[7];
		final int nPoints = CollisionHelper.getBoundingShape(xPoints, yPoints, startAngle, MathUtils.abs(arcAngle),
				centerX, centerY, x + _translateX - 1, y + _translateY - 1, width + 2, height + 2);
		final RectI bounds = CollisionHelper
				.getIntersection(CollisionHelper.setBoundingBox(temp_rect, xPoints, yPoints, nPoints), clip, temp_rect);
		this.drawCircle(x, y, width, height, false, new CircleUpdate() {
			@Override
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawArcPoint(xPoints, yPoints, nPoints, bounds, xLeft, yTop);
				drawArcPoint(xPoints, yPoints, nPoints, bounds, xRight, yTop);
				drawArcPoint(xPoints, yPoints, nPoints, bounds, xLeft, yBottom);
				drawArcPoint(xPoints, yPoints, nPoints, bounds, xRight, yBottom);
			}
		});
		return this;
	}

	/**
	 * 填充一个圆弧
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param start
	 * @param arcAngle
	 */
	public Pixmap fillArc(int x, int y, int width, int height, int start, int arcAngle) {
		return fillArc(x, y, width, height, start, arcAngle, false);
	}

	/**
	 * 填充一个圆弧
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param start
	 * @param arcAngle
	 * @param reverse
	 * @return
	 */
	public Pixmap fillArc(int x, int y, int width, int height, int start, int arcAngle, boolean reverse) {
		if (_isClosed) {
			return this;
		}
		if (reverse) {
			if (arcAngle < 0) {
				start = 360 - arcAngle;
				arcAngle = 360 + arcAngle;
			}
			start %= 360;
			if (start < 0) {
				start += 360;
			}
			if (arcAngle % 360 == 0) {
				fillOval(x, y, width, height);
				return this;
			} else {
				arcAngle %= 360;
			}
		} else {
			arcAngle = -arcAngle;
		}
		final int startAngle = arcAngle > 0 ? start
				: (start + arcAngle < 0 ? start + arcAngle + 360 : start + arcAngle);
		final int centerX = x + _translateX + width / 2;
		final int centerY = y + _translateY + height / 2;
		final int xPoints[] = new int[7];
		final int yPoints[] = new int[7];
		final int nPoints = CollisionHelper.getBoundingShape(xPoints, yPoints, startAngle, MathUtils.abs(arcAngle),
				centerX, centerY, x + _translateX - 1, y + _translateY - 1, width + 2, height + 2);
		final RectI bounds = CollisionHelper.setBoundingBox(temp_rect, xPoints, yPoints, nPoints);
		this.drawCircle(x, y, width, height, true, new CircleUpdate() {
			@Override
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft, xRight, yTop);
				if (yTop != yBottom) {
					drawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft, xRight, yBottom);
				}
			}
		});
		return this;
	}

	public Pixmap fillCircle(int x, int y, int radius) {
		return fillOval(x, y, radius, radius);
	}

	public Pixmap drawPolyline(int xPoints[], int yPoints[], int nPoints) {
		if (_isClosed) {
			return this;
		}
		for (int i = 1; i < nPoints; i++) {
			drawLine(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i]);
		}
		return this;
	}

	/**
	 * 绘制一个多边形
	 * 
	 * @param p
	 */
	public Pixmap drawPolygon(Polygon p) {
		return drawShapeImpl(p, 0, 0);
	}

	/**
	 * 绘制一个多边形
	 * 
	 * @param path
	 * @return
	 */
	public Pixmap draw(Path2D path) {
		return drawPolygon(path.getPointiX(), path.getPointiY(), path.size() / 2);
	}

	/**
	 * 绘制一个多边形
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public Pixmap drawPolygon(int xPoints[], int yPoints[], int nPoints) {
		drawPolyline(xPoints, yPoints, nPoints);
		drawLine(xPoints[nPoints - 1], yPoints[nPoints - 1], xPoints[0], yPoints[0]);
		return this;
	}

	/**
	 * 绘制并填充一个多边形
	 * 
	 * @param p
	 */
	public Pixmap fillPolygon(Polygon p) {
		return fillShapeImpl(p, 0, 0);
	}

	/**
	 * 绘制并填充一个多边形
	 * 
	 * @param pixmap
	 * @param poly
	 * @return
	 */
	public Pixmap fillPolygon(Pixmap pixmap, Polygon poly) {
		return fillShapeImpl(pixmap, poly, 0, 0);
	}

	/**
	 * 绘制并填充一个多边形
	 * 
	 * @param pixmap
	 * @param poly
	 * @param x
	 * @param y
	 * @return
	 */
	public Pixmap fillPolygon(Pixmap pixmap, Polygon poly, int x, int y) {
		return fillShapeImpl(pixmap, poly, x, y);
	}

	/**
	 * 绘制并填充一个多边形
	 * 
	 * @param path
	 * @return
	 */
	public Pixmap fill(Path2D path) {
		return fillPolygon(path.getPointiX(), path.getPointiY(), path.size() / 2);
	}

	/**
	 * 绘制并填充一个多边形
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public Pixmap fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		int[] xPointsCopy;
		if (_translateX == 0) {
			xPointsCopy = xPoints;
		} else {
			xPointsCopy = CollectionUtils.copyOf(xPoints);
			for (int i = 0; i < nPoints; i++) {
				xPointsCopy[i] += _translateX;
			}
		}
		int[] yPointsCopy;
		if (_translateY == 0) {
			yPointsCopy = yPoints;
		} else {
			yPointsCopy = CollectionUtils.copyOf(yPoints);
			for (int i = 0; i < nPoints; i++) {
				yPointsCopy[i] += _translateY;
			}
		}
		RectI bounds = CollisionHelper.getIntersection(
				CollisionHelper.setBoundingBox(temp_rect, xPointsCopy, yPointsCopy, nPoints), clip, temp_rect);
		for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
			for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
				if (CollisionHelper.contains(xPointsCopy, yPointsCopy, nPoints, bounds, x, y)) {
					drawPoint(x, y);
				}
			}
		}
		return this;

	}

	private void drawLineImpl(int x1, int x2, int y) {
		if (_isClosed) {
			return;
		}
		if (y >= clip.y && y < clip.y + clip.height) {
			y *= _width;
			int maxX = MathUtils.min(x2, clip.x + clip.width - 1);
			for (int x = MathUtils.max(x1, clip.x); x <= maxX; x++) {
				drawPoint(_drawPixels, x + y);
			}
		}
	}

	private void drawVerticalLine(int x, int y1, int y2) {
		if (x >= clip.x && x < clip.x + clip.width) {
			int maxY = MathUtils.min(y2, clip.y + clip.height - 1) * _width;
			for (int y = MathUtils.max(y1, clip.y) * _width; y <= maxY; y += _width)
				drawPoint(_drawPixels, x + y);
		}
	}

	public Pixmap drawPoint(int x, int y, LColor c) {
		if (c == null) {
			return this;
		}
		return drawPoint(x, y, c.getARGB());
	}

	public Pixmap drawPoint(int x, int y) {
		return drawPoint(x, y, _baseColor);
	}

	public Pixmap drawPoint(int x, int y, int c) {
		if (!inside(x, y)) {
			drawPoint(_drawPixels, x + y * _width, c, _transparent);
		}
		return this;
	}

	private void drawPoint(int[] pixels, int pixelIndex) {
		drawPoint(pixels, pixelIndex, _baseColor, _transparent);
	}

	private void drawPoint(int[] pixels, int pixelIndex, int dst) {
		drawPoint(pixels, pixelIndex, dst, _transparent);
	}

	private final int getXorNewColor(int pixel, int dst) {
		return xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
	}

	private void drawPoint(int[] pixels, int pixelIndex, int dst, int src) {
		int newColor = _transparent;
		int pixel = pixels[pixelIndex];
		if (src == _transparent) {
			src = pixel;
		}
		if (_composite == -1) {
			if (_baseAlpha != 1f) {
				dst = SET_DEFAULT(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
			}
		} else {
			switch (_composite) {
			default:
			case SRC_IN:
				dst = SET_SRC_IN(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case SRC_OUT:
				dst = SET_SRC_OUT(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case CLEAR:
				dst = SET_CLEAR(_transparent);
				break;
			case DST:
				dst = SET_DST(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case SRC_OVER:
				dst = SET_SRC_OVER(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case SRC_ATOP:
				dst = SET_SRC_ATOP(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case ADD:
				dst = SET_ADD(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case RED:
				dst = SET_RED(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case GREEN:
				dst = SET_GREEN(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case BLUE:
				dst = SET_BLUE(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case COLOR_BURN:
				dst = SET_COLOR_BURN(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case SOFT_LIGHT:
				dst = SET_SOFT_LIGHT(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case DIFFERENCE:
				dst = SET_DIFFERENCE(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case EXCLUSION:
				dst = SET_EXCLUSION(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case LIGHTEN:
				dst = SET_LIGHTEN(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case MULTIPLY:
				dst = SET_MULTIPLY(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case OVERLAY:
				dst = SET_OVERLAY(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case DODGE:
				dst = SET_DODGE(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			case SCREEN:
				dst = SET_SCREEN(srcColor.setColor(src), dstColor.setColor(dst), _transparent, _baseAlpha);
				break;
			}
		}
		newColor = getXorNewColor(pixel, dst);
		if (_baseColor == LColor.DEF_COLOR) {
			pixels[pixelIndex] = (newColor == _transparent) ? src : newColor;
		} else {
			if (newColor == _transparent) {
				pixels[pixelIndex] = src;
			} else {
				dstColor.setColor(newColor);
				srcColor.setColor(_baseColor);
				pixels[pixelIndex] = dstColor.multiply(srcColor).getARGB();
			}
		}
		_dirty = true;
	}

	public Pixmap blendTo(int colorTo, int alpha) {
		for (int i = 0; i < this._length; i++) {
			int colorFrom = this._drawPixels[i];
			this._drawPixels[i] = blendToColor(colorFrom, colorTo, alpha);
		}
		_dirty = true;
		return this;
	}

	public int getComposite() {
		return this._composite;
	}

	public Pixmap setComposite(int c) {
		this._composite = c;
		return this;
	}

	private void drawArcPoint(int xPoints[], int yPoints[], int nPoints, RectI bounds, int x, int y) {
		if (CollisionHelper.contains(xPoints, yPoints, nPoints, bounds, x, y)) {
			drawPoint(x, y);
		}
	}

	private void drawArcImpl(int xPoints[], int yPoints[], int nPoints, RectI bounds, int xLeft, int xRight, int y) {
		if (y >= clip.y && y < clip.y + clip.height) {
			for (int x = MathUtils.max(xLeft, clip.x); x <= xRight; x++) {
				if (CollisionHelper.contains(xPoints, yPoints, nPoints, bounds, x, y)) {
					drawPoint(x, y);
				}
			}
		}
	}

	private interface CircleUpdate {
		public void newPoint(int xLeft, int yTop, int xRight, int yBottom);
	}

	private void drawCircle(int x, int y, int width, int height, boolean fill, CircleUpdate listener) {
		int a = width / 2;
		int b = height / 2;
		long squareA = width * width / 4;
		long squareB = height * height / 4;
		long squareAB = MathUtils.round(width * width * height * height, 16L);

		x += _translateX;
		y += _translateY;
		int centerX = x + a;
		int centerY = y + b;

		int deltaX = (width % 2 == 0) ? 0 : 1;
		int deltaY = (height % 2 == 0) ? 0 : 1;

		int currentY = b;
		int currentX = 0;

		int lastx1 = centerX - currentX;
		int lastx2 = centerX + currentX + deltaX;
		int lasty1 = centerY - currentY;
		int lasty2 = centerY + currentY + deltaY;
		while (currentX <= a && currentY >= 0) {
			long deltaA = (currentX + 1) * (currentX + 1) * squareB + currentY * currentY * squareA - squareAB;
			long deltaB = (currentX + 1) * (currentX + 1) * squareB + (currentY - 1) * (currentY - 1) * squareA
					- squareAB;
			long deltaC = currentX * currentX * squareB + (currentY - 1) * (currentY - 1) * squareA - squareAB;
			if (deltaA <= 0) {
				currentX++;
			} else if (deltaC >= 0) {
				currentY--;
			} else {
				int min = (int) MathUtils.min(MathUtils.abs(deltaA),
						MathUtils.min(MathUtils.abs(deltaB), MathUtils.abs(deltaC)));
				if (min == MathUtils.abs(deltaA)) {
					currentX++;
				} else if (min == MathUtils.abs(deltaC)) {
					currentY--;
				} else {
					currentX++;
					currentY--;
				}
			}

			int x1 = centerX - currentX;
			int x2 = centerX + currentX + deltaX;
			int y1 = centerY - currentY;
			int y2 = centerY + currentY + deltaY;
			if (!fill || lasty1 != y1) {
				listener.newPoint(lastx1, lasty1, lastx2, lasty2);
				lasty1 = y1;
				lasty2 = y2;
			}
			lastx1 = x1;
			lastx2 = x2;
		}
		if (lasty1 < lasty2) {
			for (; lasty1 <= lasty2; lasty1++, lasty2--) {
				listener.newPoint(centerX - a, lasty1, centerX + a + deltaX, lasty2);
			}
		}
	}

	public Pixmap blendHorizontal(Pixmap pix) {
		return blendHorizontal(pix, false);
	}

	/**
	 * 混合当前pixmap和指定pixmap生成新的pixmap
	 * 
	 * @param pix
	 * @param horizontal
	 * @return
	 */
	public Pixmap blendHorizontal(Pixmap pix, boolean horizontal) {
		Pixmap leftImage = null;
		Pixmap rightImage = null;
		if (horizontal) {
			leftImage = pix;
			rightImage = this;
		} else {
			leftImage = this;
			rightImage = pix;
		}
		int width = (leftImage.getWidth() + rightImage.getWidth());
		int height = MathUtils.max(leftImage.getHeight(), rightImage.getHeight());
		Pixmap image = new Pixmap(width, height, true);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = 0;
				if (x < leftImage.getWidth()) {
					if (y < leftImage.getHeight()) {
						pixel = leftImage.getPixel(x, y);
					} else {
						pixel = _transparent;
					}
				} else {
					if (y < rightImage.getHeight()) {
						pixel = rightImage.getPixel(x - leftImage.getWidth(), y);
					} else {
						pixel = _transparent;
					}
				}
				image.drawPoint(x, y, pixel);
			}
		}
		return image;
	}

	public Pixmap blendVertical(Pixmap pix) {
		return blendVertical(pix, false);
	}

	public Pixmap blendVertical(Pixmap pix, boolean vertical) {
		Pixmap topImage = null;
		Pixmap bottomImage = null;
		if (vertical) {
			topImage = this;
			bottomImage = pix;
		} else {
			topImage = pix;
			bottomImage = this;
		}
		int width = MathUtils.max(topImage.getWidth(), bottomImage.getWidth());
		int height = (topImage.getHeight() + bottomImage.getHeight());
		Pixmap image = new Pixmap(width, height, true);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = 0;
				if (y < topImage.getHeight()) {
					if (x < topImage.getWidth()) {
						pixel = topImage.getPixel(x, y);
					} else {
						pixel = _transparent;
					}
				} else {
					if (x < bottomImage.getWidth()) {
						pixel = bottomImage.getPixel(x, y - topImage.getHeight());
					} else {
						pixel = _transparent;
					}
				}
				image.drawPoint(x, y, pixel);
			}
		}
		return image;
	}

	private boolean inside(int x, int y) {
		return (x < clip.x || x >= clip.x + clip.width || y < clip.y || y >= clip.y + clip.height);
	}

	public int getX() {
		return _translateX;
	}

	public void setX(int x) {
		this._translateX = x;
	}

	public int getY() {
		return _translateY;
	}

	public void setY(int y) {
		this._translateY = y;
	}

	/**
	 * 获得当前图像是否不透明
	 * 
	 * @return
	 */
	public boolean hasAlpha() {
		return _hasAlpha;
	}

	/**
	 * 获得图像高
	 * 
	 * @return
	 */
	public int getWidth() {
		return _width;
	}

	/**
	 * 获得图像宽
	 * 
	 * @return
	 */
	public int getHeight() {
		return _height;
	}

	/**
	 * 获得指定区域是否透明
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isTransparent(int x, int y) {
		if (x < 0 || y < 0 || x >= _width || y >= _height) {
			return true;
		} else if (!_hasAlpha) {
			return false;
		} else {
			int pixel = _drawPixels[x + y * _width];
			return (pixel >>> 24) == 0;
		}
	}

	public int getTransparent() {
		return _transparent;
	}

	public void setTransparent(int transparent) {
		this._transparent = transparent;
	}

	public IntBuffer getPixelsData() {
		return BufferUtils.newIntBuffer(_drawPixels);
	}

	public boolean isClosed() {
		return _isClosed;
	}

	public int getSize() {
		return _length;
	}

	public RectBox getOpaqueBounds() {
		final RectBox rect = new RectBox(this._width, this._height);
		for (int i = 0; i < this._length; i++) {
			int pixel = _drawPixels[i];
			if (LColor.getAlpha(pixel) == 0) {
				continue;
			}
			int cX = i % this._width;
			int cY = i / this._width;
			rect.x = MathUtils.min(rect.x, cX);
			rect.y = MathUtils.min(rect.y, cY);
			rect.width = MathUtils.max(rect.width, cX);
			rect.height = MathUtils.max(rect.height, cY);
		}
		rect.width = MathUtils.ifloor(1 + MathUtils.max(0, rect.width - rect.x));
		rect.height = MathUtils.ifloor(1 + MathUtils.max(0, rect.height - rect.y));
		return rect;
	}

	public int[] getData() {
		return _drawPixels;
	}

	public int[] getABGRData() {
		return LColor.convertToABGR(this._width, this._height, this._drawPixels, new int[_length]);
	}

	public void setData(int[] pixels) {
		if (_isClosed) {
			return;
		}
		this._drawPixels = pixels;
		this._dirty = true;
	}

	public int getData(int x, int y) {
		if (_isClosed) {
			return -1;
		}
		if (x < 0 || x >= this._width || y < 0 || y >= this._height) {
			return -1;
		} else {
			return this._drawPixels[y * _width + x];
		}
	}

	public TArray<Vector2f> getPoints(final Vector2f size, final int interval, final float scale) {
		final int[] pixels = _drawPixels;
		final TArray<Vector2f> points = new TArray<Vector2f>();
		for (int y = 0; y < getHeight(); y += interval) {
			for (int x = 0; x < getWidth(); x += interval) {
				int tx = MathUtils.clamp(x + MathUtils.nextInt(-interval / 2, interval / 2), 0, getWidth() - 1);
				int ty = MathUtils.clamp(y + MathUtils.nextInt(-interval / 2, interval / 2), 0, getHeight() - 1);
				int color = pixels[getWidth() * ty + tx];
				if (LColor.getRed(color) == 255) {
					points.add((new Vector2f(tx, ty).sub(size)).mul(scale));
				}
			}
		}
		return points;
	}

	public Pixmap setLinear(LColor start, LColor end) {
		return setLinear(0f, 0f, _width, _height, start, end);
	}

	public Pixmap setLinear(float x, float y, float w, float h, LColor start, LColor end) {
		return setGradient(LGradation.createLinear(x, y, w, h, start, end));
	}

	public Pixmap setRadial(LColor start, LColor end) {
		return setRadial(_width / 2, _height / 2, MathUtils.min(_width, _height) / 2, start, end);
	}

	public Pixmap setRadial(float centerX, float centerY, float radius, LColor start, LColor end) {
		return setGradient(LGradation.createRadial(centerX, centerY, radius, start, end));
	}

	public Pixmap setGradient(PixmapGradient g) {
		return setGradient(g, 0);
	}

	public Pixmap setGradient(PixmapGradient g, int adjust) {
		if (g != null) {
			g.fill(_drawPixels, 0, adjust, 0, 0, _width, _height);
			this._dirty = true;
		}
		return this;
	}

	public String getBase64() {
		return getRGBAsToArrayByte().toString();
	}

	public ArrayByte getRGBAsToArrayByte() {
		return new ArrayByte(getRGBABytes());
	}

	public byte[] getABGRBytes() {
		return getRGBABytes(true);
	}

	public byte[] getRGBABytes() {
		return getRGBABytes(false);
	}

	public byte[] getRGBABytes(boolean flag) {
		int idx = 0;
		final int bits = 4;
		final int[] pixesl = _drawPixels;
		byte[] buffer = new byte[getWidth() * getHeight() * bits];
		for (int i = 0, size = buffer.length; i < size; i += bits) {
			int pixel = pixesl[idx++];
			if (flag) {
				buffer[i + 3] = (byte) (LColor.getAlpha(pixel));
				buffer[i + 2] = (byte) (LColor.getRed(pixel));
				buffer[i + 1] = (byte) (LColor.getGreen(pixel));
				buffer[i] = (byte) (LColor.getBlue(pixel));
			} else {
				buffer[i] = (byte) (LColor.getRed(pixel));
				buffer[i + 1] = (byte) (LColor.getGreen(pixel));
				buffer[i + 2] = (byte) (LColor.getBlue(pixel));
				buffer[i + 3] = (byte) (LColor.getAlpha(pixel));
			}
		}
		return buffer;
	}

	public byte[] getBGRBytes() {
		return getRGBBytes(true);
	}

	public byte[] getRGBBytes() {
		return getRGBBytes(false);
	}

	public byte[] getRGBBytes(boolean flag) {
		int idx = 0;
		final int bits = 3;
		final int[] pixesl = _drawPixels;
		byte[] buffer = new byte[getWidth() * getHeight() * bits];
		for (int i = 0, size = buffer.length; i < size; i += bits) {
			int pixel = pixesl[idx++];
			if (flag) {
				buffer[i + 2] = (byte) (LColor.getRed(pixel));
				buffer[i + 1] = (byte) (LColor.getGreen(pixel));
				buffer[i] = (byte) (LColor.getBlue(pixel));
			} else {
				buffer[i] = (byte) (LColor.getRed(pixel));
				buffer[i + 1] = (byte) (LColor.getGreen(pixel));
				buffer[i + 2] = (byte) (LColor.getBlue(pixel));
			}
		}
		return buffer;
	}

	public Pixmap blur(int radius, int iterations, boolean disposePixmap) {
		return blur(0, 0, getWidth(), getHeight(), 0, 0, getWidth(), getHeight(), radius, iterations);
	}

	public Pixmap blur(int dstx, int dsty, int dstwidth, int dstheight, int srcx, int srcy, int srcwidth, int srcheight,
			int radius, int iterations) {
		Pixmap selfPixel = this;
		boolean srcEq = srcx == 0 && srcy == 0 && srcwidth == getWidth() && srcheight == getHeight();
		boolean dstEq = dstx == 0 && dsty == 0 && dstwidth == getWidth() && dstheight == getHeight();
		if (!srcEq || !dstEq) {
			Pixmap tmp = new Pixmap(dstwidth, dstheight, true);
			tmp.drawPixmap(this, dstx, dsty, dstwidth, dstheight, srcx, srcy, srcwidth, srcheight);
			selfPixel = tmp;
		}
		int[] pixels = blur(selfPixel.getData(), dstwidth, dstheight, radius, iterations);
		return new Pixmap(pixels, dstwidth, dstheight);
	}

	public ByteBuffer convertPixmapToByteBuffer() {
		return convertPixmapToByteBuffer(false);
	}

	public ByteBuffer convertPixmapToByteBuffer(boolean filterTran) {
		ByteBuffer buffer = BufferUtils.newByteBuffer(_width * _height * 4);
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int pixel = this._drawPixels[y * _width + x];
				if (filterTran && pixel == _transparent) {
					pixel = 0;
				}
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();
		return buffer;
	}

	public ByteBuffer convertPixmapToRGBByteBuffer() {
		ByteBuffer buffer = BufferUtils.newByteBuffer(_width * _height * 3);
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int pixel = this._drawPixels[y * _width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
			}
		}
		buffer.flip();
		return buffer;
	}

	public void convertByteBufferRGBToPixmap(ByteBuffer buffer) {
		int idx = 0;
		int dst = 0;
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				int r = buffer.get(idx++) & 0xFF;
				int g = buffer.get(idx++) & 0xFF;
				int b = buffer.get(idx++) & 0xFF;
				this._drawPixels[dst + x] = LColor.rgb(r, g, b);
			}
			dst += _width;
		}
	}

	public void convertByteBufferToPixmap(ByteBuffer buffer) {
		int idx = 0;
		int dst = 0;
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				int r = buffer.get(idx++) & 0xFF;
				int g = buffer.get(idx++) & 0xFF;
				int b = buffer.get(idx++) & 0xFF;
				int a = buffer.get(idx++) & 0xFF;
				this._drawPixels[dst + x] = LColor.argb(a, r, g, b);
			}
			dst += _width;
		}
	}

	@Override
	public int hashCode() {
		int result = 65;
		result = LSystem.unite(result, _width);
		result = LSystem.unite(result, _height);
		result = LSystem.unite(result, _hasAlpha);
		result = LSystem.unite(result, _transparent);
		result = LSystem.unites(result, _drawPixels);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Pixmap) {
			return equals((Pixmap) obj);
		}
		return false;
	}

	public boolean equals(final Pixmap dst) {
		return equals(this, dst);
	}

	public static boolean equals(final Pixmap src, final Pixmap dst) {
		if (src == dst) {
			return true;
		}
		if (src == null) {
			return false;
		}
		if (dst == null) {
			return false;
		}
		if (src.getWidth() != dst.getWidth() || src.getHeight() != dst.getHeight()
				|| src.hasAlpha() != dst.hasAlpha()) {
			return false;
		}
		for (int x = 0; x < dst.getWidth(); x++) {
			for (int y = 0; y < dst.getHeight(); y++) {
				if (src.getPixel(x, y) != dst.getPixel(x, y)) {
					return false;
				}
			}
		}
		return true;
	}

	public int size() {
		return _width * _height;
	}

	public boolean isEmpty() {
		return _width == 0 && _height == 0;
	}

	public boolean isColorEmpty() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				final int pixel = getPixel(x, y);
				if (pixel >> 24 != 0x00 || pixel != _transparent) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int getPixelWidth() {
		return getWidth();
	}

	@Override
	public int getPixelHeight() {
		return getHeight();
	}

	@Override
	public int get(float x, float y) {
		return getPixel(MathUtils.ifloor(x), MathUtils.ifloor(y));
	}

	@Override
	public void set(float x, float y, int pixel) {
		putPixel(MathUtils.ifloor(x), MathUtils.ifloor(y), pixel);
	}

	@Override
	public int[] pixels() {
		return getData();
	}

	@Override
	public void bindPixels(int[] pixels) {
		setData(pixels);
	}

	public PixmapLimit findLimit() {
		return PixmapLimit.find(this);
	}

	@Override
	public String toString() {
		StrBuilder sbr = new StrBuilder();
		for (int y = 0; y < _height; y++) {
			sbr.append(LSystem.BRACKET_START);
			for (int x = 0; x < _width; x++) {
				int p = getData(x, y);
				sbr.append(p);
				if (x < _width - 1) {
					sbr.append(LSystem.COMMA);
				}
			}
			sbr.append(LSystem.BRACKET_END);
			sbr.append(LSystem.COMMA).append(LSystem.LF);
		}
		return sbr.toString();
	}

	@Override
	public void close() {
		_isClosed = true;
		_drawPixels = null;
		if (_paintCanvas != null) {
			_paintCanvas.close();
			_paintCanvas = null;
		}
	}

}
