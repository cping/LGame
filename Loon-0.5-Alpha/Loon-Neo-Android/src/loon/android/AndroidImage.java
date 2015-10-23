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
package loon.android;

import loon.Graphics;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.LColor;
import loon.canvas.Pattern;
import loon.opengl.GL20;
import loon.utils.Scale;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;

public class AndroidImage extends ImageImpl {

	private boolean closed;

	protected Bitmap bitmap;

	public AndroidImage(Graphics gfx, Scale scale, Bitmap bitmap, String source) {
		super(gfx, scale, bitmap.getWidth(), bitmap.getHeight(), source, bitmap);
	}

	public AndroidImage(AndroidGame game, boolean async, int preWidth,
			int preHeight, String source) {
		super(game, async, Scale.ONE, preWidth, preHeight, source);
		if (this.bitmap != null) {
			AndroidRuntime.get().trackFree(bitSize(this.bitmap));
		}
	}

	public Bitmap bitmap() {
		return bitmap;
	}

	@Override
	public Pattern createPattern(boolean repeatX, boolean repeatY) {
		return new AndroidPattern(repeatX, repeatY, bitmap);
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height,
			int[] rgbArray, int offset, int scanSize) {
		bitmap.getPixels(rgbArray, offset, scanSize, startX, startY, width,
				height);
	}

	@Override
	public void setRGB(int startX, int startY, int width, int height,
			int[] rgbArray, int offset, int scanSize) {
		bitmap.setPixels(rgbArray, offset, scanSize, startX, startY, width,
				height);
	}

	@Override
	public Image transform(BitmapTransformer xform) {
		Bitmap nbitmap = ((AndroidTransformer) xform).transform(bitmap);
		return new AndroidImage(gfx, scale, nbitmap, source);
	}

	@Override
	public void draw(Object ctx, float x, float y, float w, float h) {
		draw(ctx, x, y, w, h, 0, 0, width(), height());
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh,
			float sx, float sy, float sw, float sh) {
		sx *= scale.factor;
		sy *= scale.factor;
		sw *= scale.factor;
		sh *= scale.factor;
		((AndroidCanvas) ctx).draw(bitmap, dx, dy, dw, dh, sx, sy, sw, sh);
	}

	@Override
	public String toString() {
		return "Image[src=" + source + ", bitmap=" + bitmap + "]";
	}

	@Override
	public void upload(Graphics gfx, LTexture tex) {
		gfx.gl.glBindTexture(GL20.GL_TEXTURE_2D, tex.getID());
		GLUtils.texImage2D(GL20.GL_TEXTURE_2D, 0, bitmap, 0);
		gfx.gl.checkError("updateTexture end");
	}

	@Override
	protected void setBitmap(Object bitmap) {
		this.bitmap = (Bitmap) bitmap;
		if (this.bitmap != null) {
			AndroidRuntime.get().trackFree(bitSize(this.bitmap));
		}
	}

	@Override
	protected Object createErrorBitmap(int pixelWidth, int pixelHeight) {
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight,
				Bitmap.Config.ARGB_4444);
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

	private int bitSize(Bitmap b) {
		return b.getRowBytes() * b.getHeight();
	}

	@Override
	public void close() {
		if (this.bitmap != null) {
			AndroidRuntime.get().trackAlloc(bitSize(this.bitmap));
			this.bitmap.recycle();
			this.bitmap = null;
		}
		this.closed = true;
	}

	@Override
	public boolean isClosed() {
		return closed;
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
		bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		return pixels;
	}

	@Override
	public int[] getPixels(int[] pixels) {
		int w = (int) width();
		int h = (int) height();
		bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		return pixels;
	}

	@Override
	public int[] getPixels(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		bitmap.getPixels(pixels, 0, w, x, y, w, h);
		return pixels;
	}

	@Override
	public int[] getPixels(int offset, int stride, int x, int y, int w, int h) {
		int pixels[] = new int[w * h];
		bitmap.getPixels(pixels, offset, stride, x, y, w, h);
		return pixels;
	}
	
	@Override
	public int[] getPixels(int pixels[], int offset, int stride, int x, int y,
			int width, int height) {
		bitmap.getPixels(pixels, offset, stride, x, y, width, height);
		return pixels;
	}
	
	@Override
	public void setPixels(int[] pixels, int w, int h) {
		bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
	}

	@Override
	public void setPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		bitmap.setPixels(pixels, offset, stride, x, y, width, height);
	}

	@Override
	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		bitmap.setPixels(pixels, 0, w, x, y, w, h);
		return pixels;
	}

	@Override
	public void setPixel(LColor c, int x, int y) {
		bitmap.setPixel(x, y, c.getRGB());
	}


	@Override
	public void setPixel(int rgb, int x, int y) {
		bitmap.setPixel(x, y, rgb);
	}

	@Override
	public int getPixel(int x, int y) {
		return bitmap.getPixel(x, y);
	}

	@Override
	public int getRGB(int x, int y) {
		return bitmap.getPixel(x, y);
	}

	@Override
	public void setRGB(int rgb, int x, int y) {
		bitmap.setPixel(x, y, rgb);
	}

	@Override
	public Image getSubImage(int x, int y, int w, int h) {
		return AndroidGraphicsUtils.drawClipImage(this, w, h, x, y,
				bitmap.getConfig());
	}
	
	@Override
	public boolean hasAlpha() {
		if (bitmap == null) {
			return false;
		}
		if (bitmap.getConfig() == Config.RGB_565) {
			return false;
		}
		return bitmap.hasAlpha();
	}

}
