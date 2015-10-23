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
package loon.canvas;

import loon.BaseIO;
import loon.Graphics;
import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.font.TextureSource;
import loon.opengl.Painter;
import loon.utils.Scale;
import loon.utils.reply.Function;
import loon.utils.reply.GoFuture;

public abstract class Image extends TextureSource implements Canvas.Drawable,
		LRelease {

	public static Canvas createCanvas(int w, int h) {
		return LSystem.base().graphics().createCanvas(w, h);
	}

	public static Image createImage(int w, int h) {
		return LSystem.base().graphics().createCanvas(w, h).image;
	}

	public static Image createImage(final String path) {
		return BaseIO.loadImage(path);
	}

	public static Image getResize(final Image image, int w, int h) {
		Canvas canvas = LSystem.base().graphics().createCanvas(w, h);
		canvas.draw(image, 0, 0, w, h, 0, 0, image.width(), image.height());
		return canvas.image;
	}

	public static Image drawClipImage(final Image image, int objectWidth,
			int objectHeight, int x1, int y1, int x2, int y2) {
		Canvas canvas = LSystem.base().graphics()
				.createCanvas(objectWidth, objectHeight);
		canvas.draw(image, 0, 0, objectWidth, objectHeight, x1, y1, x2, y2);
		return canvas.image;
	}

	public static Image drawClipImage(final Image image, int objectWidth,
			int objectHeight, int x, int y) {
		Canvas canvas = LSystem.base().graphics()
				.createCanvas(objectWidth, objectHeight);
		canvas.draw(image, 0, 0, objectWidth, objectHeight, x, y, x
				+ objectWidth, objectHeight + y);
		return canvas.image;
	}

	Canvas canvas;

	public final GoFuture<Image> state;

	public Canvas getCanvas() {
		return canvas;
	}

	public abstract boolean isClosed();

	public boolean isLoaded() {
		return state.isCompleteNow();
	}

	public abstract Scale scale();

	public float width() {
		return scale().invScaled(pixelWidth());
	}

	public float height() {
		return scale().invScaled(pixelHeight());
	}

	public abstract int pixelWidth();

	public abstract int pixelHeight();

	public abstract Pattern createPattern(boolean repeatX, boolean repeatY);

	public Image setFormat(LTexture.Format config) {
		texconf = config;
		return this;
	}

	public LTexture texture() {
		if (texture == null || texture.disposed()) {
			texture = createTexture(texconf);
		}
		return texture;
	}

	public LTexture updateTexture() {
		if (texture == null || texture.disposed()) {
			texture = createTexture(texconf);
		} else
			texture.update(this);
		return texture;
	}

	public GoFuture<LTexture> textureAsync() {
		return state.map(new Function<Image, LTexture>() {
			public LTexture apply(Image image) {
				return texture();
			}
		});
	}

	public LTexture createTexture(LTexture.Format config) {
		if (!isLoaded()) {
			throw new IllegalStateException(
					"Cannot create texture from unready image: " + this);
		}
		int texWidth = config.toTexWidth(pixelWidth());
		int texHeight = config.toTexHeight(pixelHeight());
		if (texWidth <= 0 || texHeight <= 0) {
			throw new IllegalArgumentException("Invalid texture size: "
					+ texWidth + "x" + texHeight + " from: " + this);
		}
		LTexture tex = new LTexture(gfx, gfx.createTexture(config), config,
				texWidth, texHeight, scale(), width(), height());
		tex.update(this);
		return tex;
	}

	public static abstract class Region extends TextureSource implements
			Canvas.Drawable {
	}

	public Region region(final float rx, final float ry, final float rwidth,
			final float rheight) {

		return new Region() {
			private LTexture tile;

			@Override
			public boolean isLoaded() {
				return Image.this.isLoaded();
			}

			@Override
			public LTexture draw() {
				if (tile == null) {
					tile = Image.this.texture().copy(rx, ry, rwidth, rheight);
				}
				return tile;
			}

			@Override
			public GoFuture<Painter> tileAsync() {
				return Image.this.state.map(new Function<Image, Painter>() {
					public Painter apply(Image image) {
						return draw();
					}
				});
			}

			@Override
			public float width() {
				return rwidth;
			}

			@Override
			public float height() {
				return rheight;
			}

			@Override
			public void draw(Object ctx, float x, float y, float width,
					float height) {
				Image.this.draw(ctx, x, y, width, height, rx, ry, rwidth,
						rheight);
			}

			@Override
			public void draw(Object ctx, float dx, float dy, float dw,
					float dh, float sx, float sy, float sw, float sh) {
				Image.this.draw(ctx, dx, dy, dw, dh, rx + sx, ry + sy, sw, sh);
			}
		};
	}

	public static interface BitmapTransformer {
	}

	public abstract Image transform(BitmapTransformer xform);

	@Override
	public Painter draw() {
		return texture();
	}

	@Override
	public GoFuture<Painter> tileAsync() {
		return state.map(new Function<Image, Painter>() {
			public Painter apply(Image image) {
				return texture();
			}
		});
	}

	protected final Graphics gfx;
	protected LTexture.Format texconf = LTexture.Format.LINEAR;
	protected LTexture texture;

	protected Image(Graphics gfx, GoFuture<Image> state) {
		this.gfx = gfx;
		this.state = state;
	}

	protected Image(Graphics gfx) {
		this.gfx = gfx;
		this.state = GoFuture.success(this);
	}

	public abstract void upload(Graphics gfx, LTexture tex);

	public abstract void getLight(Image buffer, int v);

	public abstract int getLight(int color, int v);

	public abstract int[] getPixels();

	public abstract int[] getPixels(int pixels[]);

	public abstract int[] getPixels(int x, int y, int w, int h);

	public abstract int[] getPixels(int offset, int stride, int x, int y,
			int width, int height);

	public abstract int[] getPixels(int pixels[], int offset, int stride,
			int x, int y, int width, int height);

	public abstract void setPixels(int[] pixels, int width, int height);

	public abstract void setPixels(int[] pixels, int offset, int stride, int x,
			int y, int width, int height);

	public abstract int[] setPixels(int[] pixels, int x, int y, int w, int h);

	public abstract void setPixel(LColor c, int x, int y);

	public abstract void setPixel(int rgb, int x, int y);

	public abstract int getPixel(int x, int y);

	public abstract int getRGB(int x, int y);

	public abstract void setRGB(int rgb, int x, int y);

	public abstract void getRGB(int startX, int startY, int width, int height,
			int[] rgbArray, int offset, int scanSize);

	public abstract void setRGB(int startX, int startY, int width, int height,
			int[] rgbArray, int offset, int scanSize);

	public abstract boolean hasAlpha();

	public abstract String getSource();

	public abstract Image getSubImage(int x, int y, int width, int height);

	public int getWidth() {
		return (int) width();
	}

	public int getHeight() {
		return (int) height();
	}

}
