package loon.an;

import android.graphics.Bitmap;

import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.LColor;
import loon.opengl.TextureSource;
import loon.utils.Scale;

public class JavaANImage extends ImageImpl {

	protected Bitmap buffer;

	public JavaANImage(Graphics gfx, Bitmap buffer) {
		this(gfx, Scale.ONE, buffer, TextureSource.RenderCanvas);
	}

	public JavaANImage(Graphics gfx, Scale scale, Bitmap buffer, String source) {
		super(gfx, scale, buffer.getWidth(), buffer.getHeight(), source, buffer);
	}

	public JavaANImage(JavaANGame game, int preWidth, int preHeight) {
		super(game, false, Scale.ONE, preWidth, preHeight, TextureSource.RenderCanvas);
	}

	public JavaANImage(JavaANGame game, boolean async, int preWidth, int preHeight, String source) {
		super(game, async, Scale.ONE, preWidth, preHeight, source);
	}

	public Bitmap anImage() {
		return buffer;
	}

	public JavaANPattern createPattern(boolean repeatX, boolean repeatY) {
		return new JavaANPattern(repeatX, repeatY, buffer);
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		buffer.getPixels(rgbArray, offset, scanSize, startX, startY, width, height);
	}

	@Override
	public void setRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		buffer.setPixels(rgbArray, offset, scanSize, startX, startY, width, height);
	}

	@Override
	public void draw(Object ctx, float x, float y, float w, float h) {
		draw(ctx, x, y, w, h, 0, 0, width(), height());
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh) {
		sx *= scale.factor;
		sy *= scale.factor;
		sw *= scale.factor;
		sh *= scale.factor;

		JavaANCanvas canvas = ((JavaANCanvas) ctx);

		canvas.draw(buffer, dx, dy, dw, dh, sx, sy, sw, sh);
	}

	@Override
	public String toString() {
		return "Image[src=" + source + ", bitmap=" + buffer + "]";
	}

	@Override
	protected void setBitmap(Object bitmap) {
		buffer = (Bitmap) bitmap;
	}

	@Override
	protected Object createErrorBitmap(int pixelWidth, int pixelHeight) {
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		android.graphics.Canvas c = new android.graphics.Canvas(bitmap);
		android.graphics.Paint p = new android.graphics.Paint();
		p.setColor(android.graphics.Color.RED);
		for (int yy = 0; yy <= pixelHeight / 15; yy++) {
			for (int xx = 0; xx <= pixelWidth / 45; xx++) {
				c.drawText("ERROR", xx * 45, yy * 15, p);
			}
		}
		return bitmap;
	}

	@Override
	protected Canvas getCanvasImpl() {
		if (canvas == null || canvas.isClosed()) {
			return canvas = new JavaANCanvas(gfx, this);
		}
		return canvas;
	}

	public void getLight(Image buffer, int v) {
		int width = (int) buffer.width();
		int height = (int) buffer.height();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				int rgbValue = buffer.getRGB(x, y);
				if (rgbValue != 0) {
					int color = getLight(rgbValue, v);
					buffer.setRGB(color, x, y);
				}
			}
		}
	}

	public int getLight(int color, int v) {
		int red = LColor.getRed(color);
		int green = LColor.getGreen(color);
		int blue = LColor.getBlue(color);
		red += v;
		green += v;
		blue += v;
		blue = blue > 255 ? 255 : blue;
		red = red > 255 ? 255 : red;
		green = green > 255 ? 255 : green;
		red = red < 0 ? 0 : red;
		green = green < 0 ? 0 : green;
		blue = blue < 0 ? 0 : blue;
		return LColor.getRGB(red, green, blue);
	}

	@Override
	public int[] getPixels() {
		int w = (int) width();
		int h = (int) height();
		int pixels[] = new int[w * h];
		buffer.getPixels(pixels, 0, w, 0, 0, w, h);
		return pixels;
	}

	@Override
	public int[] getPixels(int[] pixels) {
		int w = (int) width();
		int h = (int) height();
		buffer.getPixels(pixels, 0, w, 0, 0, w, h);
		return pixels;
	}

	@Override
	public int[] getPixels(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		buffer.getPixels(pixels, 0, w, x, y, w, h);
		return pixels;
	}

	@Override
	public int[] getPixels(int offset, int stride, int x, int y, int w, int h) {
		int pixels[] = new int[w * h];
		buffer.getPixels(pixels, offset, stride, x, y, w, h);
		return pixels;
	}

	@Override
	public int[] getPixels(int pixels[], int offset, int stride, int x, int y, int width, int height) {
		buffer.getPixels(pixels, offset, stride, x, y, width, height);
		return pixels;
	}

	@Override
	public void setPixels(int[] pixels, int w, int h) {
		buffer.setPixels(pixels, 0, w, 0, 0, w, h);
	}

	@Override
	public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		buffer.setPixels(pixels, offset, stride, x, y, width, height);
	}

	@Override
	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		buffer.setPixels(pixels, 0, w, x, y, w, h);
		return pixels;
	}

	@Override
	public void setPixel(LColor c, int x, int y) {
		buffer.setPixel(x, y, c.getRGB());
	}

	@Override
	public void setPixel(int rgb, int x, int y) {
		buffer.setPixel(x, y, rgb);
	}

	@Override
	public int getPixel(int x, int y) {
		return buffer.getPixel(x, y);
	}

	@Override
	public int getRGB(int x, int y) {
		return buffer.getPixel(x, y);
	}

	@Override
	public void setRGB(int rgb, int x, int y) {
		buffer.setPixel(x, y, rgb);
	}

	@Override
	public Image getSubImage(int x, int y, int w, int h) {
		return JavaANGraphicsUtils.drawClipImage(this, w, h, x, y, buffer.getConfig());
	}

	@Override
	public boolean hasAlpha() {
		if (buffer == null) {
			return false;
		}
		if (buffer.getConfig() == Bitmap.Config.RGB_565) {
			return false;
		}
		return buffer.hasAlpha();
	}

	@Override
	protected void closeImpl() {
		if (this.buffer != null) {
			this.buffer.recycle();
			this.buffer = null;
		}
		if (canvas != null && canvas instanceof JavaANCanvas) {
			((JavaANCanvas) canvas).closeImpl();
		}
	}

}
