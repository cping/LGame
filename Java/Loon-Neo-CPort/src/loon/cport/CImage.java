/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.cport;

import loon.Graphics;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.LColor;
import loon.canvas.Pattern;
import loon.canvas.Pixmap;
import loon.cport.bridge.STBFont;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.TextureSource;
import loon.utils.MathUtils;
import loon.utils.Scale;

public class CImage extends ImageImpl {

	protected Pixmap buffer;

	public CImage(Graphics gfx, Scale scale, Pixmap buffer, String source) {
		super(gfx, scale, buffer.getWidth(), buffer.getHeight(), source, buffer);
	}

	public CImage(CGame game, boolean async, int preWidth, int preHeight, String source) {
		super(game, async, Scale.ONE, preWidth, preHeight, source);
	}

	public Pixmap bufferedImage() {
		return buffer;
	}

	@Override
	public Pattern createPattern(boolean repeatX, boolean repeatY) {
		return new CPattern(repeatX, repeatY);
	}

	@Override
	public Image transform(BitmapTransformer xform) {
		return new CImage(gfx, scale, buffer, source);
	}

	@Override
	public void draw(Object ctx, float x, float y, float w, float h) {
		Pixmap gfx = (Pixmap) ctx;
		gfx.drawPixmap(buffer, MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w), MathUtils.ifloor(h));
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh) {
		float f = scale().factor;
		sx *= f;
		sy *= f;
		sw *= f;
		sh *= f;
		Pixmap gfx = (Pixmap) ctx;
		gfx.drawPixmap(buffer, MathUtils.ifloor(dx), MathUtils.ifloor(dy), MathUtils.ifloor(dw), MathUtils.ifloor(dh),
				MathUtils.ifloor(sx), MathUtils.ifloor(sy), MathUtils.ifloor(sw), MathUtils.ifloor(sh));
	}

	@Override
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

	@Override
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
		return buffer.getData();
	}

	@Override
	public int[] getPixels(int pixels[]) {
		int w = (int) width();
		int h = (int) height();
		buffer.getRGB(0, 0, w, h, pixels, 0, w);
		return pixels;
	}

	@Override
	public int[] getPixels(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		buffer.getRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	@Override
	public int[] getPixels(int offset, int stride, int x, int y, int width, int height) {
		int pixels[] = new int[width * height];
		buffer.getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	@Override
	public int[] getPixels(int pixels[], int offset, int stride, int x, int y, int width, int height) {
		buffer.getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	@Override
	public void setPixels(int[] pixels, int width, int height) {
		buffer.setRGB(0, 0, width, height, pixels, 0, width);
	}

	@Override
	public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		buffer.setRGB(x, y, width, height, pixels, offset, stride);
	}

	@Override
	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		buffer.setRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	@Override
	public void setPixel(LColor c, int x, int y) {
		buffer.setRGB(x, y, c.getRGB());
	}

	@Override
	public void setPixel(int rgb, int x, int y) {
		buffer.setRGB(x, y, rgb);
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
		buffer.setRGB(x, y, rgb);
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		buffer.getRGB(startX, startY, width, height, rgbArray, offset, scanSize);
	}

	@Override
	public void setRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scansize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		setPixels(rgbArray, offset, scansize, startX, startY, width, height);
	}

	@Override
	public String toString() {
		return "Image[src=" + source + ", buffer=" + buffer + "]";
	}

	@Override

	public void upload(Graphics gfx, LTexture tex) {
		((CGraphics) gfx).upload(buffer, tex);
	}

	@Override
	protected void setBitmap(Object bitmap) {
		buffer = (Pixmap) bitmap;
	}

	@Override
	protected Object createErrorBitmap(int rawWidth, int rawHeight) {
		final Pixmap image = new Pixmap(rawWidth, rawHeight, true);
		image.setColor(LColor.red);
		final int size = LSystem.getFontSize();
		if (STBFont.existsSysFont()) {
			IFont font = LSystem.getSystemGameFont();
			if (font != null && font instanceof LFont) {
				CTextLayout layout = (CTextLayout) ((LFont) font).getTextLayout();
				for (int yy = 0; yy <= rawHeight / 15; yy++) {
					for (int xx = 0; xx <= rawWidth / 45; xx++) {
						layout.drawText(image, "Error", xx * 45, yy * 15);
					}
				}
			}
		} else {
			for (int yy = 0; yy <= rawHeight / 15; yy++) {
				for (int xx = 0; xx <= rawWidth / 45; xx++) {
					image.drawRect(xx * 45, yy * 15, size + 1, size + 1);
				}
			}
		}
		return image;
	}

	@Override
	public boolean hasAlpha() {
		return buffer.hasAlpha();
	}

	@Override
	public Image getSubImage(int x, int y, int width, int height) {
		return new CImage(gfx, scale, buffer.copy(x, y, width, height), TextureSource.RenderCanvas);
	}

	@Override
	protected void closeImpl() {
		if (buffer != null) {
			buffer = null;
		}
	}
}
