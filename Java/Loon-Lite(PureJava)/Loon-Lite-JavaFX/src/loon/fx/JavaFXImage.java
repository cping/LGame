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
package loon.fx;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import loon.Graphics;
import loon.canvas.ImageImpl;
import loon.canvas.LColor;
import loon.utils.MathUtils;
import loon.utils.Scale;

public class JavaFXImage extends ImageImpl {

	protected WritableImage buffer;

	public JavaFXImage(Graphics gfx, Scale scale, WritableImage buffer, String source) {
		super(gfx, scale, (int) buffer.getWidth(), (int) buffer.getHeight(), source, buffer);
	}

	public JavaFXImage(JavaFXGame game, boolean async, int preWidth, int preHeight, String source) {
		super(game, async, Scale.ONE, preWidth, preHeight, source);
	}

	public WritableImage fxImage() {
		return buffer;
	}

	@Override
	public void draw(Object ctx, float x, float y, float w, float h) {
		GraphicsContext gfx = (GraphicsContext) ctx;
		gfx.drawImage(buffer, MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w), MathUtils.ifloor(h));
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh) {
		float f = scale().factor;
		sx *= f;
		sy *= f;
		sw *= f;
		sh *= f;
		GraphicsContext gfx = (GraphicsContext) ctx;
		gfx.drawImage(buffer, MathUtils.ifloor(dx), MathUtils.ifloor(dy), MathUtils.ifloor(dw), MathUtils.ifloor(dh),
				MathUtils.ifloor(sx), MathUtils.ifloor(sy), MathUtils.ifloor(sw), MathUtils.ifloor(sh));
	}

	public void getLight(loon.canvas.Image buffer, int v) {
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
		PixelReader reader = buffer.getPixelReader();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				pixels[i * w + j] = reader.getArgb(i, j);
			}
		}
		return pixels;
	}

	public int getPixel(int x, int y) {
		PixelReader reader = buffer.getPixelReader();
		return reader.getArgb(x, y);
	}

	public int getRGB(int x, int y) {
		PixelReader reader = buffer.getPixelReader();
		return reader.getArgb(x, y);
	}

	public int[] getPixels(int pixels[]) {
		int w = (int) width();
		int h = (int) height();
		PixelReader reader = buffer.getPixelReader();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				pixels[i * w + j] = reader.getArgb(i, j);
			}
		}
		return pixels;
	}

	public int[] getPixels(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		PixelReader reader = buffer.getPixelReader();
		for (int i = x; i < w; i++) {
			for (int j = y; j < h; j++) {
				pixels[i * w + j] = reader.getArgb(i, j);
			}
		}
		return pixels;
	}

	@Override
	public int[] getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	@Override
	public int[] getPixels(int offset, int stride, int x, int y, int width, int height) {
		int pixels[] = new int[width * height];
		getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	public int[] getPixels(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize) {
		int yoff = offset;
		int off;
		if (rgbArray == null) {
			rgbArray = new int[offset + h * scansize];
		}
		PixelReader reader = buffer.getPixelReader();
		for (int y = startY; y < startY + h; y++, yoff += scansize) {
			off = yoff;
			for (int x = startX; x < startX + w; x++) {
				rgbArray[off++] = reader.getArgb(x, y);
			}
		}
		return rgbArray;
	}

	public void setPixels(int[] pixels, int width, int height) {
		PixelWriter out = buffer.getPixelWriter();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				out.setArgb(i, j, pixels[i]);
			}
		}
	}

	public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		setRGB(x, y, width, height, pixels, offset, stride);
	}

	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		PixelWriter out = buffer.getPixelWriter();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				out.setArgb(i, j, pixels[i]);
			}
		}
		return pixels;
	}

	public void setPixel(LColor c, int x, int y) {
		PixelWriter out = buffer.getPixelWriter();
		out.setArgb(x, y, c.getRGB());
	}

	public void setPixel(int rgb, int x, int y) {
		PixelWriter out = buffer.getPixelWriter();
		out.setArgb(x, y, rgb);
	}

	public void setRGB(int rgb, int x, int y) {
		setPixel(x, y, rgb);
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		int yoff = offset;
		int off;
		if (rgbArray == null) {
			rgbArray = new int[offset + height * scanSize];
		}
		PixelReader reader = buffer.getPixelReader();
		for (int y = startY; y < startY + height; y++, yoff += scanSize) {
			off = yoff;
			for (int x = startX; x < startX + width; x++) {
				rgbArray[off++] = reader.getArgb(x, y);
			}
		}
	}

	public void setRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scansize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		PixelWriter out = buffer.getPixelWriter();
		int yoff = offset;
		int off;
		for (int y = startY; y < startY + height; y++, yoff += scansize) {
			off = yoff;
			for (int x = startX; x < startX + width; x++) {
				int pixel = rgbArray[off++];
				out.setArgb(x, y, pixel);
			}
		}
	}

	@Override
	public String toString() {
		return "Image[src=" + source + ", buffer=" + buffer + "]";
	}

	@Override
	protected void setBitmap(Object bitmap) {
		buffer = (WritableImage) bitmap;
	}

	@Override
	protected Object createErrorBitmap(int rawWidth, int rawHeight) {
		Canvas canvas = new Canvas(rawWidth, rawHeight);
		GraphicsContext context = canvas.getGraphicsContext2D();
		SnapshotParameters par = new SnapshotParameters();
		WritableImage image = new WritableImage(rawWidth, rawHeight);
		context.setFill(Color.RED);
		for (int yy = 0; yy <= rawHeight / 15; yy++) {
			for (int xx = 0; xx <= rawWidth / 45; xx++) {
				context.fillText("ERROR", xx * 45, yy * 15);
			}
		}
		canvas.snapshot(par, image);
		return image;
	}

	public boolean hasAlpha() {
		return true;
	}

	@Override
	public loon.canvas.Image getSubImage(int x, int y, int width, int height) {
		return loon.canvas.Image.drawClipImage(this, width, height, x, y);
	}

	@Override
	protected void closeImpl() {
		if (buffer != null) {
			buffer = null;
		}
	}

}
