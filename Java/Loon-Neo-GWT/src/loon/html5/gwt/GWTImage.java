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
package loon.html5.gwt;

import loon.Graphics;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.LColor;
import loon.canvas.Pattern;
import loon.geom.Affine2f;
import loon.jni.EventHandler;
import loon.utils.MathUtils;
import loon.utils.Scale;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;

public class GWTImage extends ImageImpl {

	private static native boolean isComplete(ImageElement img) /*-{
		return img.complete;
	}-*/;

	public static ImageData scaleImage(ImageElement image, float scale) {
		return scaleImage(image, scale, scale);
	}

	public static ImageData scaleImage(ImageElement image, float scaleToRatioh, float scaleToRatiow) {
		Canvas canvasTmp = Canvas.createIfSupported();
		Context2d context = canvasTmp.getContext2d();
		float ch = (image.getHeight() * scaleToRatioh);
		float cw = (image.getWidth() * scaleToRatiow);
		canvasTmp.setCoordinateSpaceHeight((int) ch);
		canvasTmp.setCoordinateSpaceWidth((int) cw);

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

	private ImageElement img;
	CanvasElement canvas;

	public GWTImage(Graphics gfx, Scale scale, CanvasElement elem, String source) {
		super(gfx, scale, elem.getWidth(), elem.getHeight(), source, elem);
		this.canvas = elem;
	}

	public GWTImage(Graphics gfx, Scale scale, ImageElement elem, String source) {
		super(gfx, GoPromise.<Image>create(), scale, elem.getWidth(), elem.getHeight(), source);
		setImageElement(img = elem);
	}

	public GWTImage(Graphics gfx, Throwable error) {
		super(gfx, GoFuture.<Image>failure(error), Scale.ONE, 50, 50, "<error>");
		setBitmap(createErrorBitmap(pixelWidth, pixelHeight));
	}

	protected void setImageElement(ImageElement m) {
		final GoPromise<Image> pstate = ((GoPromise<Image>) state);
		if (isComplete(img)) {
			pstate.succeed(this);
		} else {
			GWTInputMake.addEventListener(img, "load", new EventHandler() {
				@Override
				public void handleEvent(NativeEvent evt) {
					pixelWidth = img.getWidth();
					pixelHeight = img.getHeight();
					pstate.succeed(GWTImage.this);
				}
			}, false);
			GWTInputMake.addEventListener(img, "error", new EventHandler() {
				@Override
				public void handleEvent(NativeEvent evt) {
					pstate.fail(new RuntimeException("Error loading image " + img.getSrc()));
				}
			}, false);
		}
	}

	public ImageElement imageElement() {
		return img;
	}

	GWTImage preload(int prePixelWidth, int prePixelHeight) {
		pixelWidth = prePixelWidth;
		pixelHeight = prePixelHeight;
		return this;
	}

	@Override
	public Pattern createPattern(boolean repeatX, boolean repeatY) {
		assert isLoaded() : "Cannot createPattern() a non-ready image";
		return new GWTPattern(img, repeatX, repeatY);
	}

	private void createCanvas() {
		if (canvas == null) {
			canvas = img.getOwnerDocument().createCanvasElement();
			canvas.setHeight(img.getHeight());
			canvas.setWidth(img.getWidth());
			canvas.getContext2d().drawImage(img, 0, 0);
		}
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scanSize) {
		assert isLoaded() : "Cannot getRgb() a non-ready image";
		if (width <= 0 || height <= 0) {
			return;
		}
		createCanvas();
		Context2d ctx = canvas.getContext2d();
		ImageData imageData = ctx.getImageData(startX, startY, width, height);
		CanvasPixelArray pixelData = imageData.getData();
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
		Context2d ctx = canvas.getContext2d();
		ImageData imageData = ctx.createImageData(width, height);
		CanvasPixelArray pixelData = imageData.getData();
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
		return new GWTImage(gfx, scale, ((GWTTransformer) xform).transform(img), source);
	}

	@Override
	public void draw(Object ctx, float x, float y, float width, float height) {
		((Context2d) ctx).drawImage(img, x, y, width, height);
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh) {
		Context2d context = (Context2d) ctx;
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
		img = (ImageElement) bitmap;
	}

	@Override
	protected Object createErrorBitmap(int pixelWidth, int pixelHeight) {
		ImageElement img = Document.get().createImageElement();
		img.setWidth(pixelWidth);
		img.setHeight(pixelHeight);
		return img;
	}

	@Override
	public void upload(Graphics gfx, LTexture tex) {
		((GWTGraphics) gfx).updateTexture(tex.getID(), img);
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
		Context2d ctx = canvas.getContext2d();
		ImageData imageData = ctx.createImageData(width, height);
		CanvasPixelArray pixelData = imageData.getData();
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
		Context2d ctx = canvas.getContext2d();
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
		Context2d ctx = canvas.getContext2d();
		ImageData imageData = ctx.getImageData(0, 0, img.getWidth(), img.getHeight());
		CanvasPixelArray pixelData = imageData.getData();
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
		img = null;
		canvas = null;
	}
}
