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
import loon.LSystem;
import loon.LTexture;
import loon.Support;
import loon.canvas.NinePatchAbstract.Repeat;
import loon.geom.Polygon;
import loon.geom.RectI;
import loon.geom.Shape;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.utils.ArrayByte;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.Scale;
import loon.utils.TArray;

/**
 * 跨平台处理像素用类(不同平台内部渲染实现通常有细节差异，某些时候不如自己写同一方法更能保证效果一致，比如转换代码到C#或C++平台时,
 * 而且一些第三方支持库也可能没有类似于Image的本地图像渲染支持（如使用MonoGame或Unity3D为基础平台），这时就可以直接套用此类的实现)
 */
public class Pixmap extends Limit implements LRelease {

	public final static int SRC_IN = 0;

	public final static int SRC_OUT = 1;

	public final static int SRC_OVER = 2;

	private int _composite = -1;

	private Canvas tmpCanvas = null;

	public Image getImage() {
		if (tmpCanvas == null) {
			if (LSystem.base() != null) {
				Graphics graphics = LSystem.base().graphics();
				Scale scale = graphics.scale();
				tmpCanvas = graphics.createCanvas(scale.invScaledCeil(getWidth()), scale.invScaledCeil(getHeight()));
			}
		}
		tmpCanvas.image.setPixmap(this);
		return tmpCanvas.image;
	}

	public LTexture texture() {
		return toTexture();
	}

	public LTexture toTexture() {
		return getImage().texture();
	}

	public static Pixmap createImageNicePatch(String path, int x, int y, int w, int h) {
		return createImageNicePatch(path, null, x, y, w, h);
	}

	public static Pixmap createImageNicePatch(String path, Repeat repeat, int x, int y, int w, int h) {
		final PixmapNinePatch np = new PixmapNinePatch(Image.createImage(path).getPixmap(), repeat);
		Pixmap c = Pixmap.createImage(w, h);
		np.drawNinePatch(c, x, y, w, h);
		int[] pixels = c.getData();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == LColor.TRANSPARENT) {
				pixels[i] = 0;
			}
		}
		c.setData(pixels);
		return c;
	}

	public static Pixmap createImage(int w, int h) {
		return new Pixmap(w, h, true);
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

	private RectI temp_rect = new RectI();

	private int _baseColor = LColor.DEF_COLOR;

	private int _background = LColor.black.getRGB();

	private int _transparent = 0;

	private boolean _isClosed;

	private int[] _drawPixels;

	private int _translateX, _translateY, _width, _height, size;

	private LColor xorColor;

	private boolean xorMode;

	private int xorRGB;

	private float _baseAlpha = 1f;

	private boolean _hasAlpha;

	private RectI defClip;

	private RectI clip;

	public Pixmap(int w, int h, boolean hasAlpha) {
		this.set(new int[w * h], w, h, hasAlpha);
	}

	public Pixmap(int[] pixelsData, int w, int h, boolean hasAlpha) {
		this.set(pixelsData, w, h, hasAlpha);
	}

	public void set(Pixmap pix) {
		this.set(CollectionUtils.copyOf(pix.getData()), pix.getWidth(), pix.getHeight(), pix.hasAlpha());
	}

	private void set(int[] pixelsData, int w, int h, boolean hasAlpha) {
		this._width = w;
		this._height = h;
		this._drawPixels = pixelsData;
		this._hasAlpha = hasAlpha;
		this.size = _drawPixels.length;
		if (hasAlpha) {
			this._transparent = 0;
		} else {
			this._transparent = LColor.TRANSPARENT;
		}
		_transparent = 0;
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
		for (int i = 0; i < this.size; i++) {
			drawPoint(_drawPixels, i, c);
		}
		return this;
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
		for (int i = 0; i < size; i++) {
			_drawPixels[i] = 0;
		}
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
		RectI bounds = RectI.getIntersection(setBoundingBox(temp_rect, xps, yps, len), clip, temp_rect);
		for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
			for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
				if (contains(xps, yps, len, bounds, x, y)) {
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
		drawLine(tempX, tempY, tempHeight, tempY);
		drawLine(tempX, tempY + 1, tempX, tempHeight);
		drawLine(tempHeight, tempHeight, tempX + 1, tempHeight);
		drawLine(tempHeight, tempHeight - 1, tempHeight, tempY + 1);
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
			throw new IllegalArgumentException("radius > 0");
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
		float x_diff = 0F;
		float y_diff = 0F;

		int dx = dstX;
		int dy = dstY;
		int sx = srcX;
		int sy = srcY;
		int i = 0;
		int j = 0;

		for (; i < dstHeight; i++) {
			sy = (int) (i * y_ratio) + srcY;
			dy = i + dstY;
			y_diff = (y_ratio * i + srcY) - sy;
			if (sy < 0 || dy < 0) {
				continue;
			}
			if (sy >= img._height || dy >= this._height) {
				break;
			}

			for (j = 0; j < dstWidth; j++) {
				sx = (int) (j * x_ratio) + srcX;
				dx = j + dstX;
				x_diff = (x_ratio * j + srcX) - sx;
				if (sx < 0 || dx < 0) {
					continue;
				}
				if (sx >= img._width || dx >= this._width) {
					break;
				}

				int src_ptr = sx + sy * spitch;
				int dst_ptr = dx + dy * dpitch;
				int src_color = currentPixels[src_ptr];
				int src_pixel = src_color;

				if (src_pixel != _transparent) {
					float ta = (1 - x_diff) * (1 - y_diff);
					float tb = (x_diff) * (1 - y_diff);
					float tc = (1 - x_diff) * (y_diff);
					float td = (x_diff) * (y_diff);

					int a = (int) (((src_pixel & 0xff000000) >> 24) * ta + ((src_pixel & 0xff000000) >> 24) * tb
							+ ((src_pixel & 0xff000000) >> 24) * tc + ((src_pixel & 0xff000000) >> 24) * td) & 0xff;
					int b = (int) (((src_pixel & 0xff0000) >> 16) * ta + ((src_pixel & 0xff0000) >> 16) * tb
							+ ((src_pixel & 0xff0000) >> 16) * tc + ((src_pixel & 0xff0000) >> 16) * td) & 0xff;
					int g = (int) (((src_pixel & 0xff00) >> 8) * ta + ((src_pixel & 0xff00) >> 8) * tb
							+ ((src_pixel & 0xff00) >> 8) * tc + ((src_pixel & 0xff00) >> 8) * td) & 0xff;
					int r = (int) ((src_pixel & 0xff) * ta + (src_pixel & 0xff) * tb + (src_pixel & 0xff) * tc
							+ (src_pixel & 0xff) * td) & 0xff;

					int dst_color = _drawPixels[dst_ptr];
					drawPoint(_drawPixels, dst_ptr, blend(r, g, b, a, dst_color), src_color);
				} else {
					drawPoint(_drawPixels, dst_ptr, _transparent);
				}
			}
		}

		return this;
	}

	private int blend(int src_r, int src_g, int src_b, int src_a, int value) {
		int dst_r = (value & 0x00FF0000) >> 16;
		int dst_g = (value & 0x0000FF00) >> 8;
		int dst_b = (value & 0x000000FF);
		int dst_a = (value & 0xFF000000) >> 24;
		if (src_a == _transparent && dst_a == _transparent) {
			return _transparent;
		}
		if (dst_a == 0) {
			return ((src_a << 24) | (src_b << 16) | (src_g << 8) | src_r);
		}
		dst_r = (int) (dst_r + src_a * (src_r - dst_r) / 255);
		dst_g = (int) (dst_g + src_a * (src_g - dst_g) / 255);
		dst_b = (int) (dst_b + src_a * (src_b - dst_b) / 255);
		dst_a = (int) (((1.0f - (1.0f - src_a / 255.0f) * (1.0f - dst_a / 255.0f)) * 255));
		return (int) ((dst_a << 24) | (dst_b << 16) | (dst_g << 8) | dst_r);
	}

	public Pixmap fillRect(int x, int y, int width, int height) {
		if (_isClosed) {
			return this;
		}
		int maxX = MathUtils.min(x + width - 1 + _translateX, clip.x + clip.width - 1);
		int maxY = MathUtils.min(y + height - 1 + _translateY, clip.y + clip.height - 1);
		for (int row = MathUtils.max(y + _translateY, clip.y), rowOffset = row
				* width; row <= maxY; row++, rowOffset += width) {
			for (int col = MathUtils.max(x + _translateX, clip.x); col <= maxX; col++) {
				drawPoint(_drawPixels, col + rowOffset);
			}
		}
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
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawPoint(xLeft, yTop);
				drawPoint(xRight, yTop);
				drawPoint(xLeft, yBottom);
				drawPoint(xRight, yBottom);
			}
		});
		return this;
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
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawLineImpl(xLeft, xRight, yTop);
				if (yTop != yBottom)
					drawLineImpl(xLeft, xRight, yBottom);
			}
		});
		return this;
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
		final int nPoints = getBoundingShape(xPoints, yPoints, startAngle, MathUtils.abs(arcAngle), centerX, centerY,
				x + _translateX - 1, y + _translateY - 1, width + 2, height + 2);
		final RectI bounds = RectI.getIntersection(setBoundingBox(temp_rect, xPoints, yPoints, nPoints), clip,
				temp_rect);
		this.drawCircle(x, y, width, height, false, new CircleUpdate() {
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
	 * 填充一个弧线
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param start
	 * @param arcAngle
	 */
	public Pixmap fillArc(int x, int y, int width, int height, int start, int arcAngle) {
		if (_isClosed) {
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
			fillOval(x, y, width, height);
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
		final int nPoints = getBoundingShape(xPoints, yPoints, startAngle, MathUtils.abs(arcAngle), centerX, centerY,
				x + _translateX - 1, y + _translateY - 1, width + 2, height + 2);
		final RectI bounds = setBoundingBox(temp_rect, xPoints, yPoints, nPoints);
		this.drawCircle(x, y, width, height, true, new CircleUpdate() {
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft, xRight, yTop);
				if (yTop != yBottom) {
					drawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft, xRight, yBottom);
				}
			}
		});
		return this;
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
		RectI bounds = RectI.getIntersection(setBoundingBox(temp_rect, xPointsCopy, yPointsCopy, nPoints), clip,
				temp_rect);
		for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
			for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
				if (contains(xPointsCopy, yPointsCopy, nPoints, bounds, x, y)) {
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
			if (_drawPixels != null)
				for (int x = MathUtils.max(x1, clip.x); x <= maxX; x++)
					drawPoint(_drawPixels, x + y);
		}
	}

	private void drawVerticalLine(int x, int y1, int y2) {
		if (x >= clip.x && x < clip.x + clip.width) {
			int maxY = MathUtils.min(y2, clip.y + clip.height - 1) * _width;
			if (_drawPixels != null)
				for (int y = MathUtils.max(y1, clip.y) * _width; y <= maxY; y += _width)
					drawPoint(_drawPixels, x + y);
		}
	}

	private void drawPoint(int x, int y) {
		if (!inside(x, y)) {
			drawPoint(_drawPixels, x + y * _width);
		}
	}

	private void drawPoint(int x, int y, int c) {
		if (!inside(x, y)) {
			if (_baseAlpha == 1f) {
				int pixelIndex = x + y * _width;
				_drawPixels[pixelIndex] = xorMode ? 0xFF000000 | ((_drawPixels[pixelIndex] ^ c) ^ xorRGB) : c;
			} else {
				int ialpha = (int) (0xFF * MathUtils.clamp(_baseAlpha, 0, 1));
				c = (ialpha << 24) | (c & 0xFFFFFF);
				int pixelIndex = x + y * _width;
				_drawPixels[pixelIndex] = xorMode ? 0xFF000000 | ((_drawPixels[pixelIndex] ^ c) ^ xorRGB) : c;
			}
		}
	}

	private void drawPoint(int[] pixels, int pixelIndex) {
		int pixel = pixels[pixelIndex];
		if (_composite == -1) {
			pixels[pixelIndex] = xorMode ? 0xFF000000 | ((pixel ^ _baseColor) ^ xorRGB) : _baseColor;
			return;
		} else {
			switch (_composite) {
			default:
			case SRC_IN:
				if (pixel != _transparent) {
					pixels[pixelIndex] = xorMode ? 0xFF000000 | ((pixel ^ _baseColor) ^ xorRGB) : _baseColor;
				}
				break;
			case SRC_OUT:
				if (pixel == _transparent) {
					pixels[pixelIndex] = xorMode ? 0xFF000000 | ((pixel ^ _baseColor) ^ xorRGB) : _baseColor;
				}
				break;
			case SRC_OVER:
				pixels[pixelIndex] = xorMode ? 0xFF000000 | ((pixel ^ _baseColor) ^ xorRGB) : _baseColor;
				break;
			}

		}
	}

	private void drawPoint(int[] pixels, int pixelIndex, int dst) {
		drawPoint(pixels, pixelIndex, dst, 0);
	}

	private void drawPoint(int[] pixels, int pixelIndex, int dst, int src) {
		int pixel = pixels[pixelIndex];
		if (_composite == -1) {
			if (_baseAlpha == 1f) {
				int newColor = xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
				pixels[pixelIndex] = (newColor == 0) ? src : newColor;
			} else {
				int ialpha = (int) (0xFF * MathUtils.clamp(_baseAlpha, 0, 1));
				dst = (ialpha << 24) | (dst & 0xFFFFFF);
				int newColor = xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
				pixels[pixelIndex] = (newColor == 0) ? src : newColor;
			}
			return;
		} else {
			switch (_composite) {
			default:
			case SRC_IN:
				if (pixel != _transparent) {
					if (_baseAlpha == 1f) {
						int newColor = xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
						pixels[pixelIndex] = (newColor == 0) ? src : newColor;
					} else {
						int ialpha = (int) (0xFF * MathUtils.clamp(_baseAlpha, 0, 1));
						dst = (ialpha << 24) | (dst & 0xFFFFFF);
						int newColor = xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
						pixels[pixelIndex] = (newColor == 0) ? src : newColor;
					}
				}
				break;
			case SRC_OUT:
				if (pixel == _transparent) {
					if (_baseAlpha == 1f) {
						int newColor = xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
						pixels[pixelIndex] = (newColor == 0) ? src : newColor;
					} else {
						int ialpha = (int) (0xFF * MathUtils.clamp(_baseAlpha, 0, 1));
						dst = (ialpha << 24) | (dst & 0xFFFFFF);
						int newColor = xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
						pixels[pixelIndex] = (newColor == 0) ? src : newColor;
					}
				}
				break;
			case SRC_OVER:
				if (_baseAlpha == 1f) {
					int newColor = xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
					pixels[pixelIndex] = (newColor == 0) ? src : newColor;
				} else {
					int ialpha = (int) (0xFF * MathUtils.clamp(_baseAlpha, 0, 1));
					dst = (ialpha << 24) | (dst & 0xFFFFFF);
					int newColor = xorMode ? 0xFF000000 | ((pixel ^ dst) ^ xorRGB) : dst;
					pixels[pixelIndex] = (newColor == 0) ? src : newColor;
				}
				break;
			}
		}
	}

	public int getComposite() {
		return this._composite;
	}

	public Pixmap setComposite(int c) {
		this._composite = c;
		return this;
	}

	private void drawArcPoint(int xPoints[], int yPoints[], int nPoints, RectI bounds, int x, int y) {
		if (contains(xPoints, yPoints, nPoints, bounds, x, y)) {
			drawPoint(x, y);
		}
	}

	private void drawArcImpl(int xPoints[], int yPoints[], int nPoints, RectI bounds, int xLeft, int xRight, int y) {
		if (y >= clip.y && y < clip.y + clip.height) {
			for (int x = MathUtils.max(xLeft, clip.x); x <= xRight; x++) {
				if (contains(xPoints, yPoints, nPoints, bounds, x, y)) {
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
		long squareAB = MathUtils.round((long) width * width * height * height, 16L);

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
		return LSystem.base().support().newIntBuffer(_drawPixels);
	}

	public boolean isClosed() {
		return _isClosed;
	}

	public int getSize() {
		return size;
	}

	public int[] getData() {
		return _drawPixels;
	}

	public void setData(int[] pixels) {
		if (_isClosed) {
			return;
		}
		this._drawPixels = pixels;
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

	public ByteBuffer convertPixmapToByteBuffer() {
		return convertPixmapToByteBuffer(false);
	}
	
	public ByteBuffer convertPixmapToByteBuffer(boolean filterTran) {
		Support support = LSystem.base().support();
		ByteBuffer buffer = support.newByteBuffer(_width * _height * 4);
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int pixel = this._drawPixels[y * _width + x];
				if (filterTran && pixel == LColor.TRANSPARENT) {
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
		Support support = LSystem.base().support();
		ByteBuffer buffer = support.newByteBuffer(_width * _height * 3);
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
	public String toString() {
		StringBuilder sbr = new StringBuilder();
		for (int y = 0; y < _height; y++) {
			sbr.append('{');
			for (int x = 0; x < _width; x++) {
				int p = getData(x, y);
				sbr.append(p);
				if (x < _width - 1) {
					sbr.append(',');
				}
			}
			sbr.append('}');
			sbr.append(",\n");
		}
		return sbr.toString();
	}

	@Override
	public void close() {
		_isClosed = true;
		_drawPixels = null;
	}

}
