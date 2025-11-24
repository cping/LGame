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
package loon.teavm;

import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.canvas.ImageData;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

import loon.Graphics;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.LColor;
import loon.canvas.Pattern;
import loon.geom.Affine2f;
import loon.teavm.TeaGame.TeaSetting;
import loon.utils.MathUtils;
import loon.utils.Scale;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

public class TeaImage extends ImageImpl {

	public ImageData scaleImage(HTMLImageElement image, float scale) {
		return scaleImage(image, scale, scale);
	}

	public ImageData scaleImage(HTMLImageElement image, float scaleToRatioh, float scaleToRatiow) {
		HTMLCanvasElement canvasTmp = (HTMLCanvasElement) HTMLDocument.current().createElement(setting.canvasName);
		CanvasRenderingContext2D context = (CanvasRenderingContext2D) canvasTmp.getContext(setting.canvasMethod);
		float ch = (image.getHeight() * scaleToRatioh);
		float cw = (image.getWidth() * scaleToRatiow);
		canvasTmp.setHeight((int) ch);
		canvasTmp.setWidth((int) cw);

		float sx = 0;
		float sy = 0;
		float sw = image.getWidth();
		float sh = image.getHeight();

		float dx = 0;
		float dy = 0;
		float dw = image.getWidth();
		float dh = image.getHeight();

		context.scale(scaleToRatioh, scaleToRatiow);
		context.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);

		float w = dw * scaleToRatioh;
		float h = dh * scaleToRatiow;
		ImageData imageData = context.getImageData(0, 0, w, h);

		return imageData;
	}

	private TeaSetting setting;

	private HTMLImageElement img;
	private HTMLCanvasElement canvas;

	public TeaImage(Graphics gfx, Scale scale, HTMLCanvasElement elem, String source) {
		super(gfx, scale, elem.getWidth(), elem.getHeight(), source, elem);
		this.setting = (TeaSetting) gfx.setting();
		this.canvas = elem;
	}

	public TeaImage(Graphics gfx, Scale scale, HTMLImageElement elem, String source) {
		super(gfx, GoPromise.<Image>create(), scale, elem.getWidth(), elem.getHeight(), source);
		img = elem;
		final GoPromise<Image> pstate = ((GoPromise<Image>) state);
		if (Loon.isComplete(img)) {
			pstate.succeed(this);
		} else {
			if (img != null) {
				pixelWidth = img.getWidth();
				pixelHeight = img.getHeight();
			}
			TeaBase doc = TeaBase.get();
			doc.addEventListener("load", new EventListener<Event>() {
				@Override
				public void handleEvent(Event evt) {
					pixelWidth = img.getWidth();
					pixelHeight = img.getHeight();
					pstate.succeed(TeaImage.this);
				}
			});
			doc.addEventListener("error", new EventListener<Event>() {
				@Override
				public void handleEvent(Event evt) {
					pstate.fail(new RuntimeException("Error loading image " + img.getSrc()));
				}
			});
		}
	}

	public TeaImage(Graphics gfx, Throwable error) {
		super(gfx, GoFuture.<Image>failure(error), Scale.ONE, 50, 50, "<error>");
		setBitmap(createErrorBitmap(pixelWidth, pixelHeight));
	}

	public HTMLImageElement imageElement() {
		return img;
	}

	TeaImage preload(int prePixelWidth, int prePixelHeight) {
		pixelWidth = prePixelWidth;
		pixelHeight = prePixelHeight;
		return this;
	}

	@Override
	public Pattern createPattern(boolean repeatX, boolean repeatY) {
		assert isLoaded() : "Cannot createPattern() a non-ready image";
		return new TeaPattern(img, repeatX, repeatY);
	}

	private void createCanvas() {
		if (canvas == null) {
			canvas = (HTMLCanvasElement) img.getOwnerDocument().createElement(setting.canvasName);
			canvas.setHeight(img.getHeight());
			canvas.setWidth(img.getWidth());
			getContext2d().drawImage(img, 0, 0);
		}
	}

	public CanvasRenderingContext2D getContext2d() {
		if (canvas == null) {
			return null;
		}
		return (CanvasRenderingContext2D) canvas.getContext(setting.canvasMethod);
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scanSize) {
		assert isLoaded() : "Cannot getRgb() a non-ready image";
		if (width <= 0 || height <= 0) {
			return;
		}
		createCanvas();
		CanvasRenderingContext2D ctx = getContext2d();
		ImageData imageData = ctx.getImageData(startX, startY, width, height);
		Uint8ClampedArray pixelData = imageData.getData();
		int i = 0;
		int dst = offset;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = pixelData.get(i++);
				int g = pixelData.get(i++);
				int b = pixelData.get(i++);
				int a = pixelData.get(i++);
				rgbArray[dst + x] = a << 24 | r << 16 | g << 8 | b;
			}
			dst += scanSize;
		}
	}

	@Override
	public void setRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0) {
			return;
		}
		createCanvas();
		CanvasRenderingContext2D ctx = getContext2d();
		ImageData imageData = ctx.createImageData(width, height);
		Uint8ClampedArray pixelData = imageData.getData();
		int i = 0;
		int dst = offset;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = rgbArray[dst + x];
				pixelData.set(i++, (argb >> 16) & 255);
				pixelData.set(i++, (argb >> 8) & 255);
				pixelData.set(i++, (argb) & 255);
				pixelData.set(i++, (argb >> 24) & 255);
			}
			dst += scanSize;
		}
		ctx.putImageData(imageData, startX, startY);
	}

	@Override
	public Image transform(BitmapTransformer xform) {
		return new TeaImage(gfx, scale, ((TeaTransformer) xform).transform(img), source);
	}

	@Override
	public void draw(Object ctx, float x, float y, float width, float height) {
		((CanvasRenderingContext2D) ctx).drawImage(img, x, y, width, height);
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh) {
		CanvasRenderingContext2D context = (CanvasRenderingContext2D) ctx;
		float f = scale().factor;
		sx *= f;
		sy *= f;
		sw *= f;
		sh *= f;
		float scaleX = dw / (sw - sx), scaleY = dh / (sh - sy);
		context.save();
		context.rect(MathUtils.ifloor(dx), MathUtils.ifloor(dy), MathUtils.iceil(dw), MathUtils.iceil(dh));
		context.clip();
		Affine2f affine = new Affine2f(scaleX, 0f, 0f, scaleY, dx - sx * scaleX, dy - sy * scaleY);
		context.transform(affine.m00, affine.m01, affine.m10, affine.m11, affine.tx, affine.ty);
		context.drawImage(img, 0, 0);
		context.restore();
	}

	@Override
	public String toString() {
		return "Image[src=" + source + ", scale=" + scale + ", size=" + width() + "x" + height() + ", psize="
				+ pixelWidth + "x" + pixelHeight + ", img=" + img + ", canvas=" + canvas + "]";
	}

	@Override
	protected void setBitmap(Object bitmap) {
		img = (HTMLImageElement) bitmap;
	}

	@Override
	protected Object createErrorBitmap(int pixelWidth, int pixelHeight) {
		HTMLImageElement img = (HTMLImageElement) HTMLDocument.current().createElement(setting.imageName);
		img.setWidth(pixelWidth);
		img.setHeight(pixelHeight);
		return img;
	}

	@Override
	public void upload(Graphics gfx, LTexture tex) {
		if (!Loon.isComplete(img)) {
			Loon.setComplete(img);
		}
		((TeaGraphics) gfx).updateTexture(tex.getID(), img);
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
		int width = getWidth();
		int height = getHeight();
		int[] pixels = new int[width * height];
		getRGB(0, 0, width, height, pixels, 0, width);
		return pixels;
	}

	@Override
	public int[] getPixels(int[] pixels) {
		int width = getWidth();
		int height = getHeight();
		getRGB(0, 0, width, height, pixels, 0, width);
		return pixels;
	}

	@Override
	public int[] getPixels(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		getRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	@Override
	public int[] getPixels(int offset, int stride, int x, int y, int width, int height) {
		int[] pixels = new int[width * height];
		getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	@Override
	public int[] getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	@Override
	public void setPixels(int[] pixels, int width, int height) {
		if (width <= 0 || height <= 0) {
			return;
		}
		createCanvas();
		CanvasRenderingContext2D ctx = getContext2d();
		ImageData imageData = ctx.createImageData(width, height);
		Uint8ClampedArray pixelData = imageData.getData();
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = pixels[x + y * width];
				pixelData.set(i++, (argb >> 16) & 255);
				pixelData.set(i++, (argb >> 8) & 255);
				pixelData.set(i++, (argb) & 255);
				pixelData.set(i++, (argb >> 24) & 255);
			}
		}
		ctx.putImageData(imageData, 0, 0);
	}

	@Override
	public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		setRGB(x, y, width, height, pixels, 0, width * height);
	}

	@Override
	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		setRGB(x, y, w, h, pixels, 0, w * h);
		return pixels;
	}

	@Override
	public void setPixel(LColor c, int x, int y) {
		setPixel(c.getARGB(), x, y);
	}

	@Override
	public void setPixel(int rgb, int x, int y) {
		createCanvas();
		CanvasRenderingContext2D ctx = getContext2d();
		String css = LColor.cssColorString(rgb);
		ctx.setStrokeStyle(css);
		ctx.setFillStyle(css);
		ctx.setGlobalAlpha(LColor.alpha(rgb));
		float dot = MathUtils.max(1f, (float) ctx.getLineWidth());
		ctx.fillRect(x, y, dot, dot);
	}

	@Override
	public int getPixel(int x, int y) {
		assert isLoaded() : "Cannot getRgb() a non-ready image";
		createCanvas();
		CanvasRenderingContext2D ctx = getContext2d();
		ImageData imageData = ctx.getImageData(0, 0, img.getWidth(), img.getHeight());
		Uint8ClampedArray pixelData = imageData.getData();
		int width = img.getWidth();

		byte bytesPerPixel = 4;
		int bytesPerRow = bytesPerPixel * width;

		int rowOffset = y * bytesPerRow;
		int colOffset = x * bytesPerPixel;
		int pixelDataLoc = rowOffset + colOffset;

		int r = pixelData.get(pixelDataLoc + 0);
		int g = pixelData.get(pixelDataLoc + 1);
		int b = pixelData.get(pixelDataLoc + 2);
		int a = pixelData.get(pixelDataLoc + 3);

		return LColor.getARGB(r, g, b, a);

	}

	@Override
	public int getRGB(int x, int y) {
		return getPixel(x, y);
	}

	@Override
	public void setRGB(int rgb, int x, int y) {
		setPixel(rgb, x, y);
	}

	@Override
	public boolean hasAlpha() {
		return true;
	}

	@Override
	public Image getSubImage(int x, int y, int width, int height) {
		return Image.drawClipImage(this, width, height, x, y);
	}

	@Override
	protected void closeImpl() {
		if (img != null) {
			img.clear();
			img = null;
		}
		if (canvas != null) {
			canvas.clear();
			canvas = null;
		}
	}
}
