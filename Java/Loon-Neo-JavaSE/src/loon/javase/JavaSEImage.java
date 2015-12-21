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
package loon.javase;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import loon.Graphics;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.LColor;
import loon.canvas.Pattern;
import loon.utils.MathUtils;
import loon.utils.Scale;

public class JavaSEImage extends ImageImpl {

	protected BufferedImage buffer;

	public JavaSEImage(Graphics gfx, Scale scale, BufferedImage buffer,
			String source) {
		super(gfx, scale, buffer.getWidth(), buffer.getHeight(), source, buffer);
	}

	public JavaSEImage(JavaSEGame game, boolean async, int preWidth,
			int preHeight, String source) {
		super(game, async, Scale.ONE, preWidth, preHeight, source);
	}

	public BufferedImage bufferedImage() {
		return buffer;
	}

	@Override
	public Pattern createPattern(boolean repeatX, boolean repeatY) {
		assert buffer != null : "Cannot generate a pattern from unready image.";
		Rectangle2D rect = new Rectangle2D.Float(0, 0, width(), height());
		return new JavaSEPattern(repeatX, repeatY, new TexturePaint(buffer,
				rect));
	}

	@Override
	public Image transform(BitmapTransformer xform) {
		return new JavaSEImage(gfx, scale,
				((JavaSETransformer) xform).transform(buffer), source);
	}

	@Override
	public void draw(Object ctx, float x, float y, float w, float h) {
		Graphics2D gfx = (Graphics2D) ctx;
		gfx.drawImage(buffer, MathUtils.ifloor(x), MathUtils.ifloor(y),
				MathUtils.ifloor(w), MathUtils.ifloor(h), null);
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh,
			float sx, float sy, float sw, float sh) {
		float f = scale().factor;
		sx *= f;
		sy *= f;
		sw *= f;
		sh *= f;
		Graphics2D gfx = (Graphics2D) ctx;
		gfx.drawImage(buffer, MathUtils.ifloor(dx), MathUtils.ifloor(dy),
				MathUtils.ifloor(dw), MathUtils.ifloor(dh),
				MathUtils.ifloor(sx), MathUtils.ifloor(sy),
				MathUtils.ifloor(sw), MathUtils.ifloor(sh), null);
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

	public int[] getPixels() {
		int w = (int) width();
		int h = (int) height();
		int pixels[] = new int[w * h];
		buffer.getRGB(0, 0, w, h, pixels, 0, w);
		return pixels;
	}

	public int[] getPixels(int pixels[]) {
		int w = (int) width();
		int h = (int) height();
		buffer.getRGB(0, 0, w, h, pixels, 0, w);
		return pixels;
	}

	public int[] getPixels(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		buffer.getRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	public int[] getPixels(int offset, int stride, int x, int y, int width,
			int height) {
		int pixels[] = new int[width * height];
		buffer.getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	public int[] getPixels(int pixels[], int offset, int stride, int x, int y,
			int width, int height) {
		buffer.getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	public void setPixels(int[] pixels, int width, int height) {
		buffer.setRGB(0, 0, width, height, pixels, 0, width);
	}

	public void setPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		buffer.setRGB(x, y, width, height, pixels, offset, stride);
	}

	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		buffer.setRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	public void setPixel(LColor c, int x, int y) {
		buffer.setRGB(x, y, c.getRGB());
	}

	public void setPixel(int rgb, int x, int y) {
		buffer.setRGB(x, y, rgb);
	}

	public int getPixel(int x, int y) {
		return buffer.getRGB(x, y);
	}

	public int getRGB(int x, int y) {
		return buffer.getRGB(x, y);
	}

	public void setRGB(int rgb, int x, int y) {
		buffer.setRGB(x, y, rgb);
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height,
			int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0){
			return;
		}
		buffer.getRGB(startX, startY, width, height, rgbArray, offset, scanSize);
	}

	public void setRGB(int startX, int startY, int width, int height, int[] rgbArray,
			int offset, int scansize) {
		if (width <= 0 || height <= 0){
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
		((JavaSEGraphics) gfx).upload(buffer, tex);
	}

	@Override
	protected void setBitmap(Object bitmap) {
		buffer = (BufferedImage) bitmap;
	}

	@Override
	protected Object createErrorBitmap(int rawWidth, int rawHeight) {
		BufferedImage bufferimage = new BufferedImage(rawWidth, rawHeight,
				BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g = bufferimage.createGraphics();
		try {
			g.setColor(java.awt.Color.red);
			for (int yy = 0; yy <= rawHeight / 15; yy++) {
				for (int xx = 0; xx <= rawWidth / 45; xx++) {
					g.drawString("ERROR", xx * 45, yy * 15);
				}
			}
		} finally {
			g.dispose();
		}
		return bufferimage;
	}

	public boolean hasAlpha() {
		return buffer.getColorModel().hasAlpha();
	}


	@Override
	public Image getSubImage(int x, int y, int width, int height) {
		return new JavaSEImage(gfx, scale, buffer.getSubimage(x, y, width,
				height), "<canvas>");
	}

	@Override
	protected void closeImpl() {
		if (buffer != null) {
			buffer = null;
		}
	}

}
