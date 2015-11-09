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
package loon.robovm;

import loon.Graphics;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.LColor;
import loon.canvas.Pattern;
import loon.jni.OpenGLES;
import loon.opengl.GL20;
import loon.utils.Scale;

import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBitmapInfo;
import org.robovm.apple.coregraphics.CGColorSpace;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGImageAlphaInfo;
import org.robovm.apple.coregraphics.CGInterpolationQuality;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.rt.bro.ptr.IntPtr;

/*
 * 在IOS环境中有个小问题，那就是UIImage提供的API操作像素不太方便，大量地址调用导致效率低下，看情况我可能需要单独构建一个纯像素的图像操作类
 *  (这个问题是共通的，就算不使用robovm，而使用j2obj或mono转换代码也一样)
 */
public class RoboVMImage extends ImageImpl {

	private CGImage image;

	public RoboVMImage(Graphics gfx, Scale scale, CGImage img, String source) {
		super(gfx, scale, (int) img.getWidth(), (int) img.getHeight(), source,
				img);
	}

	public RoboVMImage(RoboVMGame game, boolean async, int preWidth,
			int preHeight, String source) {
		super(game, async, Scale.ONE, preWidth, preHeight, source);
	}

	public CGImage cgImage() {
		return image;
	}

	public UIImage toUIImage() {
		return new UIImage(cgImage());
	}

	@Override
	public Pattern createPattern(boolean repeatX, boolean repeatY) {
		if (image == null) {
			throw new IllegalStateException(
					"Can't create pattern from un-ready image.");
		}
		return new RoboVMPattern(UIColor.fromPatternImage(toUIImage())
				.getCGColor(), repeatX, repeatY);
	}

	@Override
	public void getRGB(int startX, int startY, int width, int height,
			int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0){
			return;
		}
		int bytesPerRow = 4 * width;
		CGBitmapContext context = CGBitmapContext.create(width, height, 8,
				bytesPerRow, CGColorSpace.createDeviceRGB(), new CGBitmapInfo(
						CGImageAlphaInfo.PremultipliedFirst.value()));
		context.setInterpolationQuality(CGInterpolationQuality.None);
		draw(context, 0, 0, width, height, startX, startY, width, height);

		int[] pixelData = new int[height * width * 4];
		context.getData().get(pixelData);

		int i = 0;
		int dst = offset;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = pixelData[i++];
				int g = pixelData[i++];
				int b = pixelData[i++];
				int a = pixelData[i++];
				rgbArray[dst + x] = a << 24 | r << 16 | g << 8 | b;
			}
			dst += scanSize;
		}
		context.dispose();
	}

	@Override
	public void setRGB(int startX, int startY, int width, int height,
			int[] rgbArray, int offset, int scanSize) {
		if (width <= 0 || height <= 0){
			return;
		}
		int bytesPerRow = 4 * width;
		CGBitmapContext context = CGBitmapContext.create(width, height, 8,
				bytesPerRow, CGColorSpace.createDeviceRGB(), new CGBitmapInfo(
						CGImageAlphaInfo.PremultipliedFirst.value()));
		context.setInterpolationQuality(CGInterpolationQuality.None);
		draw(context, 0, 0, width, height, startX, startY, width, height);
		int[] pixelData = new int[height * width * 4];
		context.getData().get(pixelData);
		int i = 0;
		int dst = offset;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = rgbArray[dst + x];
				pixelData[i++] = (argb >> 16) & 255;
				pixelData[i++] = (argb >> 8) & 255;
				pixelData[i++] = (argb) & 255;
				pixelData[i++] = (argb >> 24) & 255;
			}
			dst += scanSize;
		}
		context.getData().set(pixelData);
		context.dispose();
	}

	@Override
	public Image transform(BitmapTransformer xform) {
		UIImage ximage = new UIImage(
				((RoboVMTransformer) xform).transform(cgImage()));
		return new RoboVMImage(gfx, scale, ximage.getCGImage(), source);
	}

	@Override
	public void draw(Object ctx, float x, float y, float width, float height) {
		CGBitmapContext bctx = (CGBitmapContext) ctx;
		y += height;
		bctx.saveGState();
		bctx.translateCTM(x, y);
		bctx.scaleCTM(1, -1);
		bctx.drawImage(new CGRect(0, 0, width, height), cgImage());
		bctx.restoreGState();
	}

	@Override
	public void draw(Object ctx, float dx, float dy, float dw, float dh,
			float sx, float sy, float sw, float sh) {
		sx *= scale.factor;
		sy *= scale.factor;
		sw *= scale.factor;
		sh *= scale.factor;

		CGImage image = cgImage();
		CGBitmapContext bctx = (CGBitmapContext) ctx;
		float iw = image.getWidth(), ih = image.getHeight();
		float scaleX = dw / (sw - sx), scaleY = dh / (sh - sy);

		bctx.saveGState();
		bctx.translateCTM(dx, dy + dh);
		bctx.scaleCTM(1, -1);
		bctx.clipToRect(new CGRect(0, 0, dw, dh));
		bctx.translateCTM(-sx * scaleX, -(ih - (sy + sh)) * scaleY);
		bctx.drawImage(new CGRect(0, 0, iw * scaleX, ih * scaleY), image);
		bctx.restoreGState();
	}

	@Override
	public String toString() {
		return "Image[src=" + source + ", cgimg=" + image + "]";
	}

	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
		this.isClose = true;
	}

	protected RoboVMImage(Graphics gfx, Scale scale, int pixelWidth,
			int pixelHeight, String source) {
		super(gfx, scale, pixelWidth, pixelHeight, null, source);
	}

	@Override
	public void upload(Graphics gfx, LTexture tex) {
		int width = pixelWidth, height = pixelHeight;
		if (width == 0 || height == 0) {
			((RoboVMGraphics) gfx).game.log().info(
					"Ignoring texture update for empty image (" + width + "x"
							+ height + ").");
			return;
		}

		CGBitmapContext bctx = RoboVMGraphics.createCGBitmap(width, height);
		CGRect rect = new CGRect(0, 0, width, height);
		bctx.clearRect(rect);
		bctx.drawImage(rect, image);
		upload(gfx, tex.getID(), width, height, bctx.getData());
		bctx.dispose();
	}

	protected void upload(Graphics gfx, int tex, int width, int height,
			IntPtr data) {
		gfx.gl.glBindTexture(GL20.GL_TEXTURE_2D, tex);
		gfx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
		OpenGLES.glTexImage2Dp(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, width,
				height, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, data);
	}

	@Override
	protected void setBitmap(Object bitmap) {
		image = (CGImage) bitmap;
	}

	@Override
	protected Object createErrorBitmap(int pixelWidth, int pixelHeight) {
		return RoboVMGraphics.createCGBitmap(pixelWidth, pixelHeight).toImage();
	}

	private boolean isClose;

	@Override
	public void close() {
		this.dispose();
	}

	@Override
	public boolean isClosed() {
		return isClose;
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
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		int[] rgbArray = new int[width * height];
		return getPixels(rgbArray);
	}

	@Override
	public int[] getPixels(int[] pixels) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		int length = width * height;
		int[] rgbArray = new int[length];
		getRGB(0, 0, width, height, rgbArray, length, width);
		return rgbArray;
	}

	@Override
	public int[] getPixels(int x, int y, int w, int h) {
		return getPixels(0, w * h, x, y, w, h);
	}

	@Override
	public int[] getPixels(int offset, int stride, int x, int y, int width,
			int height) {
		int[] rgbArray = new int[width * height];
		getRGB(x, y, width, height, rgbArray, offset, width);
		return rgbArray;
	}

	@Override
	public int[] getPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	@Override
	public void setPixels(int[] pixels, int width, int height) {
		setPixels(pixels, 0, width, 0, 0, width, height);
	}

	@Override
	public void setPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		setRGB(x, y, width, height, pixels, offset, stride);
	}

	@Override
	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		setRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	@Override
	public void setPixel(LColor c, int x, int y) {
		setPixel(c.getARGB(), x, y);
	}

	@Override
	public void setPixel(int rgb, int x, int y) {
		setRGB(rgb, x, y);
	}

	@Override
	public int getPixel(int x, int y) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();

		byte bytesPerPixel = 4;
		int bytesPerRow = bytesPerPixel * width;
		CGBitmapContext context = CGBitmapContext.create(width, height, 8,
				bytesPerRow, CGColorSpace.createDeviceRGB(), new CGBitmapInfo(
						CGImageAlphaInfo.PremultipliedFirst.value()));
		context.setInterpolationQuality(CGInterpolationQuality.None);
		draw(context, 0, 0, width, height);
		int[] pixelData = new int[height * width * 4];
		context.getData().get(pixelData);

		int rowOffset = y * bytesPerRow;
		int colOffset = x * bytesPerPixel;
		int pixelDataLoc = rowOffset + colOffset;

		int r = pixelData[pixelDataLoc + 0];
		int g = pixelData[pixelDataLoc + 1];
		int b = pixelData[pixelDataLoc + 2];
		int a = pixelData[pixelDataLoc + 3];
		context.dispose();
		return LColor.getARGB(r, g, b, a);
	}

	@Override
	public int getRGB(int x, int y) {
		return getPixel(x, y);
	}

	@Override
	public void setRGB(int rgb, int x, int y) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();

		byte bytesPerPixel = 4;
		int bytesPerRow = bytesPerPixel * width;
		CGBitmapContext context = CGBitmapContext.create(width, height, 8,
				bytesPerRow, CGColorSpace.createDeviceRGB(), new CGBitmapInfo(
						CGImageAlphaInfo.PremultipliedFirst.value()));
		context.setInterpolationQuality(CGInterpolationQuality.None);
		draw(context, 0, 0, width, height);
		int[] pixelData = new int[height * width * 4];
		context.getData().get(pixelData);

		int rowOffset = y * bytesPerRow;
		int colOffset = x * bytesPerPixel;
		int pixelDataLoc = rowOffset + colOffset;

		pixelData[pixelDataLoc + 0] = (rgb >> 16) & 255;
		pixelData[pixelDataLoc + 1] = (rgb >> 8) & 255;
		pixelData[pixelDataLoc + 2] = (rgb) & 255;
		pixelData[pixelDataLoc + 3] = (rgb >> 24) & 255;

		context.getData().set(pixelData);
		context.dispose();
	}

	@Override
	public boolean hasAlpha() {
		return image.getAlphaInfo().equals(CGImageAlphaInfo.Last)
				|| image.getAlphaInfo().equals(CGImageAlphaInfo.First);
	}

	@Override
	public Image getSubImage(int x, int y, int width, int height) {
		return new RoboVMImage(LSystem.base().graphics(), LSystem.base()
				.graphics().scale(), CGImage.createWithImageInRect(image,
				new CGRect(x, y, width, height)), "<canvas>");
	}

	@Override
	protected void finalize() {
		dispose();
	}

}
