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

import java.nio.IntBuffer;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
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

	public JavaFXImage(JavaFXGame game, int preWidth, int preHeight) {
		super(game, false, Scale.ONE, preWidth, preHeight, "<canvas>");
	}

	public JavaFXImage(JavaFXGame game, boolean async, int preWidth, int preHeight, String source) {
		super(game, async, Scale.ONE, preWidth, preHeight, source);
	}

	public WritableImage fxImage() {
		return buffer;
	}

	@Override
	public void draw(Object ctx, float x, float y, float w, float h) {
		JavaFXCanvas gfx = (JavaFXCanvas) ctx;
		gfx.context.drawImage(buffer, MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
				MathUtils.ifloor(h));
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh) {
		float f = scale().factor;
		sx *= f;
		sy *= f;
		sw *= f;
		sh *= f;
		JavaFXCanvas gfx = (JavaFXCanvas) ctx;
		gfx.context.drawImage(buffer, MathUtils.ifloor(sx), MathUtils.ifloor(sy), MathUtils.ifloor(sw - sx),
				MathUtils.ifloor(sh - sy), MathUtils.ifloor(dx), MathUtils.ifloor(dy), MathUtils.ifloor(dw - dx),
				MathUtils.ifloor(dh - dy));
	}

	@Override
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

	public static int[] getRGB(Image image, int x, int y, int width, int height) {
		int[] rgb = new int[width * height];
		PixelReader reader = image.getPixelReader();
		PixelFormat.Type type = reader.getPixelFormat().getType();
		WritablePixelFormat<IntBuffer> format = null;
		if (type == PixelFormat.Type.INT_ARGB_PRE) {
			format = PixelFormat.getIntArgbPreInstance();
		} else {
			format = PixelFormat.getIntArgbInstance();
		}
		reader.getPixels(x, y, width, height, format, rgb, 0, width);
		return rgb;
	}

	@Override
	public int[] getPixels() {
		return getRGB(buffer, 0, 0, (int) width(), (int) height());
	}

	@Override
	public int getPixel(int x, int y) {
		PixelReader reader = buffer.getPixelReader();
		return reader.getArgb(x, y);
	}

	@Override
	public int getRGB(int x, int y) {
		PixelReader reader = buffer.getPixelReader();
		return reader.getArgb(x, y);
	}

	@Override
	public int[] getPixels(int[] pixels) {
		int w = (int) width();
		int h = (int) height();
		if (pixels == null) {
			pixels = new int[w * h];
		}
		PixelReader reader = buffer.getPixelReader();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				pixels[i * w + j] = reader.getArgb(i, j);
			}
		}
		return pixels;
	}

	@Override
	public int[] getPixels(int x, int y, int w, int h) {
		return getRGB(buffer, x, y, w, h);
	}

	@Override
	public int[] getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		if (pixels == null) {
			pixels = new int[offset + height * width];
		}
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
		if (rgbArray == null) {
			rgbArray = new int[offset + h * scansize];
		}
		getRGB(startX, startY, w, h, rgbArray, offset, scansize);
		return rgbArray;
	}

	@Override
	public void setPixels(int[] pixels, int width, int height) {
		setRGB(0, 0, width, height, pixels, 0, width);
	}

	@Override
	public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		setRGB(x, y, width, height, pixels, offset, stride);
	}

	@Override
	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		setRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	@Override
	public void setPixel(LColor c, int x, int y) {
		PixelWriter out = buffer.getPixelWriter();
		out.setArgb(x, y, c.getRGB());
	}

	@Override
	public void setPixel(int rgb, int x, int y) {
		PixelWriter out = buffer.getPixelWriter();
		out.setArgb(x, y, rgb);
	}

	@Override
	public void setRGB(int rgb, int x, int y) {
		setPixel(x, y, rgb);
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		PixelReader reader = buffer.getPixelReader();
		PixelFormat.Type type = reader.getPixelFormat().getType();
		WritablePixelFormat<IntBuffer> format = null;
		if (type == PixelFormat.Type.INT_ARGB_PRE) {
			format = PixelFormat.getIntArgbPreInstance();
		} else {
			format = PixelFormat.getIntArgbInstance();
		}
		reader.getPixels(startX, startY, width, height, format, rgbArray, offset, scanSize);
	}

	@Override
	public void setRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scansize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		PixelWriter writer = buffer.getPixelWriter();
		PixelFormat.Type type = writer.getPixelFormat().getType();
		WritablePixelFormat<IntBuffer> format = null;
		if (type == PixelFormat.Type.INT_ARGB_PRE) {
			format = PixelFormat.getIntArgbPreInstance();
		} else {
			format = PixelFormat.getIntArgbInstance();
		}
		writer.setPixels(startX, startY, width, height, format, rgbArray, offset, scansize);
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

	@Override
	public boolean hasAlpha() {
		if (buffer == null) {
			return false;
		}
		PixelFormat.Type type = buffer.getPixelReader().getPixelFormat().getType();
		if (type == PixelFormat.Type.BYTE_RGB) {
			return false;
		}
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
