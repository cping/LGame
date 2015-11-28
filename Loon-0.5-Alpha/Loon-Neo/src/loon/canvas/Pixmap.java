package loon.canvas;

import java.nio.IntBuffer;

import loon.LRelease;
import loon.LSystem;
import loon.geom.Polygon;
import loon.geom.RectI;
import loon.geom.Shape;
import loon.geom.Triangle2f;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

/**
 * 跨平台处理像素用类(不同平台内部渲染实现通常有细节差异，某些时候不如自己写同一方法更能保证效果一致)
 */
public class Pixmap extends Limit implements LRelease {

	public Image getImage() {
		Canvas canvas = LSystem.base().graphics()
				.createCanvas(getWidth(), getHeight());
		canvas.image.setPixmap(this);
		return canvas.image;
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
		if (image.width == w && image.height == h) {
			return image;
		}
		Pixmap result = new Pixmap(w, h, image.hasAlpha);
		result.drawPixmap(image, 0, 0, w, h, 0, 0, image.getWidth(),
				image.getHeight());
		return result;
	}

	public static Pixmap drawClipImage(Pixmap image, int objectWidth,
			int objectHeight, int x1, int y1, int x2, int y2) {
		Pixmap buffer = new Pixmap(objectWidth, objectHeight, true);
		buffer.drawPixmap(image, 0, 0, objectWidth, objectHeight, x1, y1, x2
				- x1, y2 - y1);
		return buffer;
	}

	public static Pixmap drawClipImage(Pixmap image, int objectWidth,
			int objectHeight, int x, int y) {
		Pixmap buffer = new Pixmap(objectWidth, objectHeight, true);
		buffer.drawPixmap(image, 0, 0, objectWidth, objectHeight, x, y,
				objectWidth, objectHeight);
		return buffer;
	}

	public static Pixmap drawCropImage(Pixmap image, int x, int y,
			int objectWidth, int objectHeight) {
		Pixmap buffer = new Pixmap(objectWidth, objectHeight, true);
		buffer.drawPixmap(image, 0, 0, objectWidth, objectHeight, x, y,
				objectWidth, objectHeight);
		return buffer;
	}

	private RectI temp_rect = new RectI();

	private int baseColor = LColor.DEF_COLOR;

	private int background = LColor.black.getRGB();

	private int transparent = 0;

	private boolean isClose;

	private int[] drawPixels;

	private int x, y, width, height, size;

	private LColor xorColor;

	private boolean xorMode;

	private int xorRGB;

	private int translateX, translateY;

	private boolean hasAlpha;

	private RectI defClip;

	private RectI clip;

	public Pixmap(int w, int h, boolean hasAlpha) {
		this.set(new int[w * h], w, h, hasAlpha);
	}

	public Pixmap(int[] pixelsData, int w, int h, boolean hasAlpha) {
		this.set(pixelsData, w, h, hasAlpha);
	}

	private void set(int[] pixelsData, int w, int h, boolean hasAlpha) {
		this.width = w;
		this.height = h;
		this.drawPixels = pixelsData;
		this.hasAlpha = hasAlpha;
		this.size = drawPixels.length;
		if (hasAlpha) {
			this.transparent = 0;
		} else {
			this.transparent = LColor.TRANSPARENT;
		}
		transparent = 0;
		this.defClip = new RectI(0, 0, width, height);
		this.clip = new RectI(0, 0, width, height);
	}

	/**
	 * 清空屏幕为指定颜色
	 * 
	 * @param c
	 */
	public void clearDraw(int c) {
		if (isClose) {
			return;
		}
		for (int i = 0; i < this.size; i++) {
			drawPoint(drawPixels, i, c);
		}
	}

	public void setBackground(int color) {
		this.background = color;
	}

	/**
	 * 清空屏幕
	 * 
	 */
	public void clearDraw() {
		clearDraw(background);
	}

	/**
	 * 
	 *
	 */
	public void clear() {
		for (int i = 0; i < size; i++) {
			drawPixels[i] = 0;
		}
	}

	/**
	 * 清空屏幕
	 * 
	 */
	public void fill() {
		clearDraw(baseColor);
	}

	/**
	 * 向指定坐标插入像素
	 * 
	 * @param x
	 * @param y
	 */
	public void putPixel(int x, int y) {
		putPixel(x, y, baseColor);
	}

	/**
	 * 向指定坐标插入像素
	 * 
	 * @param x
	 * @param y
	 */
	public void putPixel(int x, int y, int c) {
		if (isClose) {
			return;
		}
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return;
		} else {
			drawPoint(x, y, c);
		}
	}

	/**
	 * 获得指定区域的RGB色彩
	 * 
	 * @param pixels
	 * @return
	 */
	public int[] getRGB(int[] pixels) {
		getRGB(0, 0, width, height, pixels, 0, width);
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
	public int[] getRGB(int offset, int stride, int x, int y, int width,
			int height) {
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
	public int[] getRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
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
	public void setRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
		int yoff = offset;
		int off;
		for (int y = startY; y < startY + h; y++, yoff += scansize) {
			off = yoff;
			for (int x = startX; x < startX + w; x++) {
				int pixel = rgbArray[off++];
				putPixel(x, y, pixel);
			}
		}
	}

	/**
	 * 设定指定区域的RGB色彩
	 * 
	 * @param pixels
	 * @param width
	 * @param height
	 */
	public void setRGB(int[] pixels, int width, int height) {
		setRGB(0, 0, width, height, pixels, 0, width);
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
	public void setRGB(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		setRGB(x, y, width, height, pixels, offset, stride);
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
	public void setRGB(int x, int y, int[] pixels) {
		putPixel(x, y, (255 << 24) | (pixels[0] << 16) | (pixels[1] << 8)
				| pixels[2]);
	}

	/**
	 * 设定指定区域的RGB色彩
	 * 
	 * @param rgb
	 * @param x
	 * @param y
	 */
	public void setRGB(int rgb, int x, int y) {
		putPixel(x, y, rgb);
	}

	/**
	 * 获得指定位置像素
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPixel(int x, int y) {
		if (isClose) {
			return -1;
		}
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return -1;
		} else {
			return drawPixels[y * width + x];
		}
	}

	/**
	 * 镜像翻转当前LPixmap为新图
	 * 
	 * @return
	 */
	public Pixmap mirror() {
		return mirror(true, false);
	}

	/**
	 * 水平翻转当前LPixmap为新图
	 * 
	 * @return
	 */
	public Pixmap flip() {
		return mirror(false, true);
	}

	/**
	 * 翻转当前LPixmap为新图
	 * 
	 * @param flag
	 * @param flag1
	 * @return
	 */
	public Pixmap mirror(boolean mirror, boolean flip) {
		if (isClose) {
			return null;
		}
		Pixmap pixel = new Pixmap(width, height, hasAlpha);
		int[] pixels = pixel.drawPixels;
		int index = 0;
		int pixelIndex = (mirror ? width - 1 : 0)
				+ (flip ? width * (height - 1) : 0);
		int flag = (mirror ? -1 : 1);
		int offset = (mirror ? width * 2 : 0) + (flip ? -width * 2 : 0);
		pixel.width = width;
		pixel.height = height;
		pixel.transparent = transparent;
		for (int j = 0; j < height;) {
			for (int i = 0; i < width;) {
				pixels[pixelIndex] = drawPixels[index];
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
	 * 将当前LPixmap克隆为新的LPixmap
	 * 
	 * @return
	 */
	public Object cpy() {
		return copy(0, 0, width, height);
	}

	/**
	 * 从当前LPixmap中copy指定范围像素为新的LPixmap
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public Pixmap copy(int x, int y, int w, int h) {
		if (isClose) {
			return null;
		}
		Pixmap pixel = new Pixmap(w, h, hasAlpha);
		pixel.width = w;
		pixel.height = h;
		pixel.drawPixmap(this, 0, 0, w, h, x, y);
		if (x < 0) {
			w -= x;
			x = 0;
		}
		if (y < 0) {
			h -= y;
			y = 0;
		}
		if (x + w > width) {
			w -= (x + w) - width;
		}
		if (y + h > height) {
			h -= (y + h) - height;
		}
		try {
			for (int size = 0; size < h; size++) {
				System.arraycopy(drawPixels, (y + size) * width + x,
						pixel.drawPixels, size * pixel.width, w);
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return pixel;
	}

	/**
	 * 拆分当前LPixmap为指定数量的瓦片
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Pixmap[] split(int row, int col) {
		if (isClose) {
			return null;
		}
		int count = row * col;
		int w = width / row;
		int h = height / col;

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
	public void translate(int x, int y) {
		if (isClose) {
			return;
		}

		translateX = x;
		translateY = y;
		if (defClip != null) {
			defClip.x += translateX;
			defClip.y += translateY;
			clip.x = MathUtils.min(clip.x + translateX, width);
			clip.y = MathUtils.min(clip.y + translateY, height);
			clip.width = MathUtils.min(clip.width + translateX, width
					- translateX);
			clip.height = MathUtils.min(clip.height + translateY, height
					- translateY);
		}
	}

	public float getAlpha() {
		return alpha();
	}

	// 以实际渲染颜色的alpha为优先返回
	public float alpha() {
		return ((baseColor >> 24) & 0xFF) / 255f;
	}

	private float baseAlpha = 1f;

	public Pixmap setAlpha(float alpha) {
		if (alpha < 0.01f) {
			alpha = 0.01f;
			baseAlpha = 0;
		} else if (alpha > 1f) {
			alpha = 1f;
			baseAlpha = 1f;
		} else {
			this.baseAlpha = alpha;
		}
		int ialpha = (int) (0xFF * MathUtils.clamp(alpha, 0, 1));
		this.baseColor = (ialpha << 24) | (baseColor & 0xFFFFFF);
		return this;
	}

	public int color() {
		return baseColor;
	}

	public LColor getColor() {
		return new LColor(baseColor);
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
		return setColor(LColor.getARGB((int) (r > 1 ? r : r * 255),
				(int) (g > 1 ? g : r * 255), (int) (b > 1 ? b : b * 255),
				(int) (a > 1 ? a : a * 255)));
	}

	public Pixmap setColor(int c) {
		if (this.baseAlpha != 1f) {
			this.baseColor = c;
			int ialpha = (int) (0xFF * MathUtils.clamp(this.baseAlpha, 0, 1));
			this.baseColor = (ialpha << 24) | (baseColor & 0xFFFFFF);
		} else {
			this.baseColor = c;
		}
		return this;
	}

	public void setPaintMode() {
		xorColor = null;
		xorMode = false;
		xorRGB = 0;
	}

	public void setXORMode(LColor c) {
		xorColor = c;
		xorMode = xorColor != null;
		xorRGB = xorMode ? xorColor.getRGB() : 0;
	}

	public void setXORMode(int red, int green, int blue) {
		this.xorColor = new LColor(red, green, blue);
		this.xorMode = true;
		this.xorRGB = ((red & 0xFF) << 16) | ((green & 0xFF) << 8)
				| (blue & 0xFF);
	}

	public RectI getClipBounds() {
		if (isClose) {
			return null;
		}
		return defClip != null ? new RectI(defClip.x, defClip.y, defClip.width,
				defClip.height) : new RectI(0, 0, width, height);
	}

	public void clipRect(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		if (defClip != null) {
			defClip = defClip.getIntersection(new RectI(x, y, width, height));
			clip = clip.getIntersection(new RectI(x + translateX, y
					+ translateY, width, height));
		} else {
			defClip = new RectI(x, y, width, height);
			clip = new RectI(x + translateX, y + translateY, width, height);
		}
	}

	public void setClip(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		if (defClip == null) {
			defClip = new RectI(x, y, width, height);
		} else {
			defClip.set(x, y, width, height);
		}
		clip = new RectI(MathUtils.max(x + translateX, 0), MathUtils.max(y
				+ translateY, 0), MathUtils.min(width, width - translateX),
				MathUtils.min(height, height - translateY));
	}

	public RectI getClip() {
		if (isClose) {
			return null;
		}
		return getClipBounds();
	}

	public void setClip(RectI clip) {
		setClip(clip.x, clip.y, clip.width, clip.height);
	}

	public void drawShapeImpl(Shape shape, int x1, int y1) {
		if (shape == null) {
			return;
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
	}

	public void fillShapeImpl(Shape shape, int x1, int y1) {
		if (shape == null) {
			return;
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
		RectI bounds = RectI.getIntersection(
				setBoundingBox(temp_rect, xps, yps, len), clip, temp_rect);
		for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
			for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
				if (contains(xps, yps, len, bounds, x, y)) {
					drawPoint(x, y);
				}
			}
		}
	}

	/**
	 * 绘制五角星
	 */
	public void drawSixStart(LColor color, int x, int y, int r) {
		if (isClose) {
			return;
		}
		setColor(color);
		drawTriangle(color, x, y, r);
		drawRTriangle(color, x, y, r);
	}

	/**
	 * 绘制正三角
	 */
	public void drawTriangle(LColor color, int x, int y, int r) {
		if (isClose) {
			return;
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
	}

	/**
	 * 绘制倒三角
	 */
	public void drawRTriangle(LColor color, int x, int y, int r) {
		if (isClose) {
			return;
		}
		int x1 = x;
		int y1 = y + r;
		int x2 = x - (int) (r * MathUtils.cos(MathUtils.PI / 6.0));
		int y2 = y - (int) (r * MathUtils.sin(MathUtils.PI / 6.0));
		int x3 = x + (int) (r * MathUtils.cos(MathUtils.PI / 6.0));
		int y3 = y - (int) (r * MathUtils.sin(MathUtils.PI / 6.0));
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
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param ts
	 */
	public void fillTriangle(Triangle2f[] ts) {
		fillTriangle(ts, 0, 0);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param ts
	 * @param x
	 * @param y
	 */
	public void fillTriangle(Triangle2f[] ts, int x, int y) {
		if (isClose) {
			return;
		}
		if (ts == null) {
			return;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			fillTriangle(ts[i], x, y);
		}
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param t
	 */
	public void fillTriangle(Triangle2f t) {
		fillTriangle(t, 0, 0);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param t
	 * @param x
	 * @param y
	 */
	public void fillTriangle(Triangle2f t, int x, int y) {
		if (isClose) {
			return;
		}
		if (t == null) {
			return;
		}
		int[] xpos = new int[3];
		int[] ypos = new int[3];
		xpos[0] = x + (int) t.xpoints[0];
		xpos[1] = x + (int) t.xpoints[1];
		xpos[2] = x + (int) t.xpoints[2];
		ypos[0] = y + (int) t.ypoints[0];
		ypos[1] = y + (int) t.ypoints[1];
		ypos[2] = y + (int) t.ypoints[2];
		fillPolygon(xpos, ypos, 3);
	}

	/**
	 * 绘制一组三角
	 * 
	 * @param ts
	 */
	public void drawTriangle(Triangle2f[] ts) {
		drawTriangle(ts, 0, 0);
	}

	/**
	 * 绘制一组三角
	 * 
	 * @param ts
	 * @param x
	 * @param y
	 */
	public void drawTriangle(Triangle2f[] ts, int x, int y) {
		if (isClose) {
			return;
		}
		if (ts == null) {
			return;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			drawTriangle(ts[i], x, y);
		}
	}

	/**
	 * 绘制三角
	 * 
	 * @param t
	 */
	public void drawTriangle(Triangle2f t) {
		drawTriangle(t, 0, 0);
	}

	/**
	 * 绘制三角
	 * 
	 * @param t
	 * @param x
	 * @param y
	 */
	public void drawTriangle(Triangle2f t, int x, int y) {
		if (isClose) {
			return;
		}
		if (t == null) {
			return;
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
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		if (isClose) {
			return;
		}
		x += translateX;
		y += translateY;

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
				if (!inside(x + dx, y + dy) && x >= 0 && x < width && y >= 0
						&& y < height) {
					drawPixels[x + dx + (y + dy) * width] = drawPixels[x + y
							* width];
				}
			}
		}
	}

	/**
	 * 绘制一条直线
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		if (isClose) {
			return;
		}

		x1 += translateX;
		y1 += translateY;
		x2 += translateX;
		y2 += translateY;

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
	}

	/**
	 * 绘制一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 */
	public void drawRect(int x1, int y1, int w1, int h1) {
		if (isClose) {
			return;
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
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		if (isClose) {
			return;
		}
		drawLine(x + arcWidth / 2, y, x + width - arcWidth / 2, y);
		drawLine(x, y + arcHeight / 2, x, y + height - arcHeight / 2);
		drawLine(x + arcWidth / 2, y + height, x + width - arcWidth / 2, y
				+ height);
		drawLine(x + width, y + arcHeight / 2, x + width, y + height
				- arcHeight / 2);
		drawArc(x, y, arcWidth, arcHeight, 90, 90);
		drawArc(x + width - arcWidth, y, arcWidth, arcHeight, 0, 90);
		drawArc(x, y + height + -arcHeight, arcWidth, arcHeight, 180, 90);
		drawArc(x + width - arcWidth, y + height + -arcHeight, arcWidth,
				arcHeight, 270, 90);
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
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		if (isClose) {
			return;
		}
		fillRect(x + arcWidth / 2, y, width - arcWidth + 1, height);
		fillRect(x, y + arcHeight / 2 - 1, arcWidth / 2, height - arcHeight);
		fillRect(x + width - arcWidth / 2, y + arcHeight / 2 - 1, arcWidth / 2,
				height - arcHeight);

		fillArc(x, y, arcWidth - 1, arcHeight - 1, 90, 90);
		fillArc(x + width - arcWidth, y, arcWidth - 1, arcHeight - 1, 0, 90);
		fillArc(x, y + height + -arcHeight, arcWidth - 1, arcHeight - 1, 180,
				90);
		fillArc(x + width - arcWidth, y + height + -arcHeight, arcWidth - 1,
				arcHeight - 1, 270, 90);
	}

	/**
	 * 将一个指定的LPixmap绘制到当前LPixmap
	 * 
	 * @param pixel
	 * @param x
	 * @param y
	 */
	public void drawPixmap(Pixmap pixel, int x, int y) {
		if (pixel == null) {
			return;
		} else {
			drawPixmap(pixel, x, y, pixel.width, pixel.height, 0, 0);
			return;
		}
	}

	/**
	 * 将一个指定的LPixmap绘制到当前LPixmap
	 * 
	 * @param pixel
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param offsetX
	 * @param offsetY
	 */
	public void drawPixmap(Pixmap pixel, int x, int y, int w, int h,
			int offsetX, int offsetY) {
		if (isClose) {
			return;
		}

		x += translateX;
		y += translateY;

		int[] currentPixels = pixel.drawPixels;
		int transparent = pixel.transparent;
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
		if (x + w > width) {
			w = width - x;
		}
		if (y + h > height) {
			h = height - y;
		}
		if (w < 0 || h < 0) {
			return;
		}
		if (transparent < 0) {
			for (int size = 0; size < h; size++) {
				System.arraycopy(currentPixels, (offsetY + size) * pixel.width
						+ offsetX, drawPixels, (y + size) * width + x, w);
			}
		} else {
			int findIndex = y * width + x;
			int drawIndex = offsetY * pixel.width + offsetX;
			int moveFind = width - w;
			int moveDraw = pixel.width - w;
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w;) {
					if (inside(j, i)) {
						continue;
					}
					if (currentPixels[drawIndex] != transparent) {
						drawPoint(drawPixels, findIndex,
								currentPixels[drawIndex]);
					}
					j++;
					findIndex++;
					drawIndex++;
				}
				findIndex += moveFind;
				drawIndex += moveDraw;
			}
		}
	}

	/**
	 * 将一个指定的LPixmap绘制到当前LPixmap，并扩展为指定大小
	 * 
	 * @param pixel
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void drawPixmap(Pixmap pixel, int x, int y, int w, int h) {
		if (pixel == null) {
			return;
		} else {
			drawPixmap(pixel, x, y, w, h, 0, 0, pixel.width, pixel.height);
			return;
		}
	}

	/**
	 * 将一个指定的LPixmap绘制到当前LPixmap，并截取为指定大小
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
	public void drawPixmap(Pixmap img, int dstX, int dstY, int dstWidth,
			int dstHeight, int srcX, int srcY, int srcWidth, int srcHeight) {
		if (isClose || img == null || img.isClose) {
			return;
		}

		dstX += translateX;
		dstY += translateY;
		srcX += translateX;
		srcY += translateY;

		if (dstWidth <= 0 || dstHeight <= 0 || srcWidth <= 0 || srcHeight <= 0) {
			return;
		}
		if (dstWidth == srcWidth && dstHeight == srcHeight) {
			drawPixmap(img, dstX, dstY, dstWidth, dstHeight, srcX, srcY);
			return;
		}

		int[] currentPixels = img.getData();

		int spitch = img.width;
		int dpitch = this.width;

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
			if (sy >= img.height || dy >= this.height) {
				break;
			}

			for (j = 0; j < dstWidth; j++) {
				sx = (int) (j * x_ratio) + srcX;
				dx = j + dstX;
				x_diff = (x_ratio * j + srcX) - sx;
				if (sx < 0 || dx < 0) {
					continue;
				}
				if (sx >= img.width || dx >= this.width) {
					break;
				}

				int src_ptr = sx + sy * spitch;
				int dst_ptr = dx + dy * dpitch;
				int src_color = currentPixels[src_ptr];
				int src_pixel = src_color;

				if (src_pixel != transparent) {
					float ta = (1 - x_diff) * (1 - y_diff);
					float tb = (x_diff) * (1 - y_diff);
					float tc = (1 - x_diff) * (y_diff);
					float td = (x_diff) * (y_diff);

					int a = (int) (((src_pixel & 0xff000000) >> 24) * ta
							+ ((src_pixel & 0xff000000) >> 24) * tb
							+ ((src_pixel & 0xff000000) >> 24) * tc + ((src_pixel & 0xff000000) >> 24)
							* td) & 0xff;
					int b = (int) (((src_pixel & 0xff0000) >> 16) * ta
							+ ((src_pixel & 0xff0000) >> 16) * tb
							+ ((src_pixel & 0xff0000) >> 16) * tc + ((src_pixel & 0xff0000) >> 16)
							* td) & 0xff;
					int g = (int) (((src_pixel & 0xff00) >> 8) * ta
							+ ((src_pixel & 0xff00) >> 8) * tb
							+ ((src_pixel & 0xff00) >> 8) * tc + ((src_pixel & 0xff00) >> 8)
							* td) & 0xff;
					int r = (int) ((src_pixel & 0xff) * ta + (src_pixel & 0xff)
							* tb + (src_pixel & 0xff) * tc + (src_pixel & 0xff)
							* td) & 0xff;

					int dst_color = drawPixels[dst_ptr];
					drawPoint(drawPixels, dst_ptr, blend(r, g, b, a, dst_color));
				} else {
					drawPoint(drawPixels, dst_ptr, transparent);
				}
			}
		}

	}

	private int blend(int src_r, int src_g, int src_b, int src_a, int value) {
		int dst_r = (value & 0x00FF0000) >> 16;
		int dst_g = (value & 0x0000FF00) >> 8;
		int dst_b = (value & 0x000000FF);
		int dst_a = (value & 0xFF000000) >> 24;
		if (src_a == transparent && dst_a == transparent) {
			return transparent;
		}
		if (dst_a == 0) {
			return ((src_a << 24) | (src_b << 16) | (src_g << 8) | src_r);
		}
		dst_r = (int) (dst_r + src_a * (src_r - dst_r) / 255);
		dst_g = (int) (dst_g + src_a * (src_g - dst_g) / 255);
		dst_b = (int) (dst_b + src_a * (src_b - dst_b) / 255);
		dst_a = (int) (((1.0f - (1.0f - src_a / 255.0f)
				* (1.0f - dst_a / 255.0f)) * 255));
		return (int) ((dst_a << 24) | (dst_b << 16) | (dst_g << 8) | dst_r);
	}

	public void fillRect(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		int maxX = MathUtils.min(x + width - 1 + translateX, clip.x
				+ clip.width - 1);
		int maxY = MathUtils.min(y + height - 1 + translateY, clip.y
				+ clip.height - 1);
		for (int row = MathUtils.max(y + translateY, clip.y), rowOffset = row
				* width; row <= maxY; row++, rowOffset += width) {
			for (int col = MathUtils.max(x + translateX, clip.x); col <= maxX; col++) {
				drawPoint(drawPixels, col + rowOffset);
			}
		}

	}

	public void clearRect(int x, int y, int width, int height) {
		fillRect(x, y, width, height);
	}

	/**
	 * 绘制一个椭圆形
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawOval(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		drawCircle(x, y, width, height, false, new CircleUpdate() {
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawPoint(xLeft, yTop);
				drawPoint(xRight, yTop);
				drawPoint(xLeft, yBottom);
				drawPoint(xRight, yBottom);
			}
		});

	}

	/**
	 * 填充一个椭圆形
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void fillOval(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		drawCircle(x, y, width, height, true, new CircleUpdate() {
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawLineImpl(xLeft, xRight, yTop);
				if (yTop != yBottom)
					drawLineImpl(xLeft, xRight, yBottom);
			}
		});

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

	public void drawArc(int x, int y, int width, int height, int start,
			int arcAngle) {
		if (isClose) {
			return;
		}
		if (arcAngle == 0) {
			return;
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
			return;
		} else {
			arcAngle %= 360;
		}
		final int startAngle = arcAngle > 0 ? start
				: (start + arcAngle < 0 ? start + arcAngle + 360 : start
						+ arcAngle);

		final int centerX = x + translateX + width / 2;
		final int centerY = y + translateY + height / 2;
		final int xPoints[] = new int[7];
		final int yPoints[] = new int[7];
		final int nPoints = getBoundingShape(xPoints, yPoints, startAngle,
				MathUtils.abs(arcAngle), centerX, centerY, x + translateX - 1,
				y + translateY - 1, width + 2, height + 2);
		final RectI bounds = RectI.getIntersection(
				setBoundingBox(temp_rect, xPoints, yPoints, nPoints), clip,
				temp_rect);
		this.drawCircle(x, y, width, height, false, new CircleUpdate() {
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawArcPoint(xPoints, yPoints, nPoints, bounds, xLeft, yTop);
				drawArcPoint(xPoints, yPoints, nPoints, bounds, xRight, yTop);
				drawArcPoint(xPoints, yPoints, nPoints, bounds, xLeft, yBottom);
				drawArcPoint(xPoints, yPoints, nPoints, bounds, xRight, yBottom);
			}
		});
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
	public void fillArc(int x, int y, int width, int height, int start,
			int arcAngle) {
		if (isClose) {
			return;
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
			return;
		} else {
			arcAngle %= 360;
		}
		final int startAngle = arcAngle > 0 ? start
				: (start + arcAngle < 0 ? start + arcAngle + 360 : start
						+ arcAngle);
		final int centerX = x + translateX + width / 2;
		final int centerY = y + translateY + height / 2;
		final int xPoints[] = new int[7];
		final int yPoints[] = new int[7];
		final int nPoints = getBoundingShape(xPoints, yPoints, startAngle,
				MathUtils.abs(arcAngle), centerX, centerY, x + translateX - 1,
				y + translateY - 1, width + 2, height + 2);
		final RectI bounds = setBoundingBox(temp_rect, xPoints, yPoints,
				nPoints);
		this.drawCircle(x, y, width, height, true, new CircleUpdate() {
			public void newPoint(int xLeft, int yTop, int xRight, int yBottom) {
				drawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft, xRight,
						yTop);
				if (yTop != yBottom) {
					drawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft,
							xRight, yBottom);
				}
			}
		});
	}

	public void drawPolyline(int xPoints[], int yPoints[], int nPoints) {
		if (isClose) {
			return;
		}
		for (int i = 1; i < nPoints; i++) {
			drawLine(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i]);
		}
	}

	/**
	 * 绘制一个多边形
	 * 
	 * @param p
	 */
	public void drawPolygon(Polygon p) {
		drawShapeImpl(p, 0, 0);
	}

	/**
	 * 绘制一个多边形
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public void drawPolygon(int xPoints[], int yPoints[], int nPoints) {
		drawPolyline(xPoints, yPoints, nPoints);
		drawLine(xPoints[nPoints - 1], yPoints[nPoints - 1], xPoints[0],
				yPoints[0]);
	}

	/**
	 * 绘制并填充一个多边形
	 * 
	 * @param p
	 */
	public void fillPolygon(Polygon p) {
		fillShapeImpl(p, 0, 0);
	}

	/**
	 * 绘制并填充一个多边形
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	protected void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		int[] xPointsCopy;
		if (translateX == 0) {
			xPointsCopy = xPoints;
		} else {
			xPointsCopy = CollectionUtils.copyOf(xPoints);
			for (int i = 0; i < nPoints; i++) {
				xPointsCopy[i] += translateX;
			}
		}
		int[] yPointsCopy;
		if (translateY == 0) {
			yPointsCopy = yPoints;
		} else {
			yPointsCopy = CollectionUtils.copyOf(yPoints);
			for (int i = 0; i < nPoints; i++) {
				yPointsCopy[i] += translateY;
			}
		}
		RectI bounds = RectI.getIntersection(
				setBoundingBox(temp_rect, xPointsCopy, yPointsCopy, nPoints),
				clip, temp_rect);
		for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
			for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
				if (contains(xPointsCopy, yPointsCopy, nPoints, bounds, x, y)) {
					drawPoint(x, y);
				}
			}
		}

	}

	private void drawLineImpl(int x1, int x2, int y) {
		if (isClose) {
			return;
		}
		if (y >= clip.y && y < clip.y + clip.height) {
			y *= width;
			int maxX = MathUtils.min(x2, clip.x + clip.width - 1);
			if (drawPixels != null)
				for (int x = MathUtils.max(x1, clip.x); x <= maxX; x++)
					drawPoint(drawPixels, x + y);
		}
	}

	private void drawVerticalLine(int x, int y1, int y2) {
		if (x >= clip.x && x < clip.x + clip.width) {
			int maxY = MathUtils.min(y2, clip.y + clip.height - 1) * width;
			if (drawPixels != null)
				for (int y = MathUtils.max(y1, clip.y) * width; y <= maxY; y += width)
					drawPoint(drawPixels, x + y);
		}
	}

	private void drawPoint(int x, int y) {
		if (!inside(x, y)) {
			drawPoint(drawPixels, x + y * width);
		}
	}

	private void drawPoint(int x, int y, int c) {
		if (!inside(x, y)) {
			if (baseAlpha == 1f) {
				int pixelIndex = x + y * width;
				drawPixels[pixelIndex] = xorMode ? 0xFF000000 | ((drawPixels[pixelIndex] ^ c) ^ xorRGB)
						: c;
			} else {
				int ialpha = (int) (0xFF * MathUtils.clamp(baseAlpha, 0, 1));
				c = (ialpha << 24) | (c & 0xFFFFFF);
				int pixelIndex = x + y * width;
				drawPixels[pixelIndex] = xorMode ? 0xFF000000 | ((drawPixels[pixelIndex] ^ c) ^ xorRGB)
						: c;
			}
		}
	}

	private void drawPoint(int[] pixels, int pixelIndex) {
		pixels[pixelIndex] = xorMode ? 0xFF000000 | ((pixels[pixelIndex] ^ baseColor) ^ xorRGB)
				: baseColor;
	}

	private void drawPoint(int[] pixels, int pixelIndex, int c) {
		if (baseAlpha == 1f) {
			pixels[pixelIndex] = xorMode ? 0xFF000000 | ((pixels[pixelIndex] ^ c) ^ xorRGB)
					: c;
		} else {
			int ialpha = (int) (0xFF * MathUtils.clamp(baseAlpha, 0, 1));
			c = (ialpha << 24) | (c & 0xFFFFFF);
			pixels[pixelIndex] = xorMode ? 0xFF000000 | ((pixels[pixelIndex] ^ c) ^ xorRGB)
					: c;
		}
	}

	private void drawArcPoint(int xPoints[], int yPoints[], int nPoints,
			RectI bounds, int x, int y) {
		if (contains(xPoints, yPoints, nPoints, bounds, x, y)) {
			drawPoint(x, y);
		}
	}

	private void drawArcImpl(int xPoints[], int yPoints[], int nPoints,
			RectI bounds, int xLeft, int xRight, int y) {
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

	private void drawCircle(int x, int y, int width, int height, boolean fill,
			CircleUpdate listener) {
		int a = width / 2;
		int b = height / 2;
		long squareA = width * width / 4;
		long squareB = height * height / 4;
		long squareAB = MathUtils.round((long) width * width * height * height,
				16L);

		x += translateX;
		y += translateY;
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
			long deltaA = (currentX + 1) * (currentX + 1) * squareB + currentY
					* currentY * squareA - squareAB;
			long deltaB = (currentX + 1) * (currentX + 1) * squareB
					+ (currentY - 1) * (currentY - 1) * squareA - squareAB;
			long deltaC = currentX * currentX * squareB + (currentY - 1)
					* (currentY - 1) * squareA - squareAB;
			if (deltaA <= 0) {
				currentX++;
			} else if (deltaC >= 0) {
				currentY--;
			} else {
				int min = (int) MathUtils.min(
						MathUtils.abs(deltaA),
						MathUtils.min(MathUtils.abs(deltaB),
								MathUtils.abs(deltaC)));
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
				listener.newPoint(centerX - a, lasty1, centerX + a + deltaX,
						lasty2);
			}
		}
	}

	private boolean inside(int x, int y) {
		return (x < clip.x || x >= clip.x + clip.width || y < clip.y || y >= clip.y
				+ clip.height);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 获得当前图像是否不透明
	 * 
	 * @return
	 */
	public boolean hasAlpha() {
		return hasAlpha;
	}

	/**
	 * 获得图像高
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 获得图像宽
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 获得指定区域是否透明
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isTransparent(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return true;
		} else if (!hasAlpha) {
			return false;
		} else {
			int pixel = drawPixels[x + y * width];
			return (pixel >>> 24) == 0;
		}
	}

	public int getTransparent() {
		return transparent;
	}

	public void setTransparent(int transparent) {
		this.transparent = transparent;
	}

	public IntBuffer getPixelsData() {
		return LSystem.base().support().newIntBuffer(drawPixels);
	}

	public boolean isClose() {
		return isClose;
	}

	public int getSize() {
		return size;
	}

	public int[] getData() {
		return drawPixels;
	}

	@Override
	public void close() {
		isClose = true;
		drawPixels = null;
	}

}
