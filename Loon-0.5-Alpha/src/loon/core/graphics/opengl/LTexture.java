/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.core.graphics.opengl;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;

import loon.LSystem;
import loon.action.collision.CollisionMask;
import loon.core.LRelease;
import loon.core.event.Updateable;
import loon.core.geom.Polygon;
import loon.core.geom.Shape;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LImage;
import loon.core.graphics.device.LShadow;
import loon.jni.NativeSupport;


public class LTexture implements LRelease {

	public LTexture makeShadow(int size, float alpha, LColor c) {
		return new LShadow(this.getImage(), size, alpha, c).getTexture();
	}

	public LTexture makeShadow() {
		return new LShadow(this.getImage()).getTexture();
	}

	public LTextureRegion getTextureRegion(int x, int y, int width, int height) {
		return new LTextureRegion(this, x, y, width, height);
	}

	public LTextureRegion[][] split(int tileWidth, int tileHeight) {
		return new LTextureRegion(this).split(tileWidth, tileHeight);
	}

	public Object Tag;

	int refCount = 0;

	public static void AUTO_LINEAR() {
		if (LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1) {
			LTexture.ALL_LINEAR = true;
		}
	}

	public static void AUTO_NEAREST() {
		if (LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1) {
			LTexture.ALL_NEAREST = true;
		}
	}

	public static final int TOP_LEFT = 0;

	public static final int TOP_RIGHT = 1;

	public static final int BOTTOM_RIGHT = 2;

	public static final int BOTTOM_LEFT = 3;

	public static boolean ALL_LINEAR = false;

	public static boolean ALL_NEAREST = false;

	public static enum Format {
		DEFAULT, NEAREST, LINEAR, FONT, SPEED, STATIC, BILINEAR, REPEATING, REPEATING_BILINEAR, REPEATING_BILINEAR_PREMULTIPLYALPHA;
	}

	private int subX, subY, subWidth, subHeight;

	private boolean isBatch;

	LTextureData imageData;

	LTexture parent;

	HashMap<Integer, LTexture> childs;

	int _hashCode = 1;

	boolean hasAlpha;

	boolean isLoaded, reload, isClose;

	boolean isVisible = true;

	boolean isChild;

	int width, texWidth;

	int height, texHeight;

	int textureID;

	public float xOff = 0.0f;

	public float yOff = 0.0f;

	public float widthRatio = 1.0f;

	public float heightRatio = 1.0f;

	int dataSize;

	int vertexSize;

	int texSize;

	Format format;

	String lazyName;

	boolean isStatic;

	private LTexture() {
		format = Format.DEFAULT;
		imageData = null;
	}

	LTexture(LTexture texture) {
		if (texture == null) {
			throw new RuntimeException("texture is Null !");
		}
		this.imageData = texture.imageData;
		this.parent = texture.parent;
		this.format = texture.format;
		this.hasAlpha = texture.hasAlpha;
		this.textureID = texture.textureID;
		this.width = texture.width;
		this.height = texture.height;
		this.parent = texture.parent;
		this.childs = texture.childs;
		this.texWidth = texture.texWidth;
		this.texHeight = texture.texHeight;
		this.xOff = texture.xOff;
		this.yOff = texture.yOff;
		this.widthRatio = texture.widthRatio;
		this.heightRatio = texture.heightRatio;
		this.isLoaded = texture.isLoaded;
		this.isClose = texture.isClose;
		this.isStatic = texture.isStatic;
		this.isVisible = texture.isVisible;
	}

	public LTexture(String res) {
		this(res, Format.DEFAULT);
	}

	public LTexture(String res, Format format) {
		this(GLLoader.getTextureData(res), format);
	}

	public LTexture(LImage pix) {
		this(pix, Format.DEFAULT);
	}

	public LTexture(LImage pix, Format format) {
		this(GLLoader.getTextureData(pix), format);
	}

	public LTexture(int width, int height, Format format) {
		this(width, height, true, format);
	}

	public LTexture(int width, int height, boolean hasAlpha, Format format) {
		this(new LImage(width, height, hasAlpha), format);
	}

	public LTexture(int width, int height, boolean hasAlpha) {
		this(new LImage(width, height, hasAlpha), Format.DEFAULT);
	}

	public LTexture(LTextureData data) {
		this(data, Format.DEFAULT);
	}

	public LTexture(LTextureData d, Format format) {
		this.format = format;
		this.hasAlpha = d.hasAlpha;
		this.imageData = d;
		this.texWidth = d.texWidth;
		this.texHeight = d.texHeight;
		this.width = d.width;
		this.height = d.height;
		this.widthRatio = (float) width / (texWidth < 1 ? width : texWidth);
		this.heightRatio = (float) height
				/ (texHeight < 1 ? height : texHeight);
	}

	public final String getFileName() {
		if (imageData != null) {
			return imageData.fileName;
		}
		return null;
	}

	public synchronized final void loadTexture() {
		if (parent != null) {
			parent.loadTexture();
			textureID = parent.textureID;
			isLoaded = parent.isLoaded;
			return;
		}
		if (imageData == null || isLoaded) {
			return;
		}
		if (imageData.source == null && imageData.fileName != null) {
			imageData = GLLoader.getTextureData(imageData.fileName);
		}
		if (imageData.source == null) {
			return;
		}
		isLoaded = true;
		setFormat(format);
		loadTextureBuffer();
		LTextures.loadTexture(this);
	}

	public void unsafeSetFilter(TextureFilter minFilter, TextureFilter magFilter) {
		unsafeSetFilter(minFilter, magFilter, false);
	}

	public void unsafeSetFilter(TextureFilter minFilter,
			TextureFilter magFilter, boolean force) {
		if (minFilter != null && (force || this.minFilter != minFilter)) {
			GLEx.gl.glTexParameterf(GL.GL_TEXTURE_2D,
					GL20.GL_TEXTURE_MIN_FILTER, minFilter.getGLEnum());
			this.minFilter = minFilter;
		}
		if (magFilter != null && (force || this.magFilter != magFilter)) {
			GLEx.gl.glTexParameterf(GL.GL_TEXTURE_2D,
					GL20.GL_TEXTURE_MAG_FILTER, magFilter.getGLEnum());
			this.magFilter = magFilter;
		}
	}

	protected TextureFilter minFilter = TextureFilter.Nearest;
	protected TextureFilter magFilter = TextureFilter.Nearest;
	protected TextureWrap uWrap = TextureWrap.ClampToEdge;
	protected TextureWrap vWrap = TextureWrap.ClampToEdge;

	public void setFilter(TextureFilter minFilter, TextureFilter magFilter) {
		this.minFilter = minFilter;
		this.magFilter = magFilter;
		bind();
		GLEx.gl.glTexParameterf(GL.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER,
				minFilter.getGLEnum());
		GLEx.gl.glTexParameterf(GL.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER,
				magFilter.getGLEnum());
	}

	public void setWrap(TextureWrap u, TextureWrap v) {
		this.uWrap = u;
		this.vWrap = v;
		bind();
		GLEx.gl.glTexParameterf(GL.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S,
				u.getGLEnum());
		GLEx.gl.glTexParameterf(GL.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T,
				v.getGLEnum());
	}

	private synchronized void loadTextureBuffer() {
		if (!reload) {
			this.textureID = createTextureID();
			this.reload = false;
		}
		this.hasAlpha = imageData.hasAlpha;
		int srcPixelFormat = hasAlpha ? GL.GL_RGBA : GL.GL_RGB;
		setWidth(imageData.width);
		setHeight(imageData.height);
		setTextureWidth(imageData.texWidth);
		setTextureHeight(imageData.texHeight);
		bind();
		GLEx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
		GLEx.gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, srcPixelFormat,
				imageData.texWidth, imageData.texHeight, 0, srcPixelFormat,
				GL.GL_UNSIGNED_BYTE, imageData.source);
		setFormat(format);
		if (imageData.fileName != null) {
			NativeSupport.freeMemory(imageData.source);
		}
		bind();
	}

	protected void delete() {
		if (textureID != 0) {
			GLEx.gl.glDeleteTexture(textureID);
			textureID = 0;
		}
	}

	private synchronized int createTextureID() {
		delete();
		return (textureID = createGLHandle());
	}

	protected static int createGLHandle() {
		return GLEx.gl.glGenTexture();
	}

	public void setFormat(Format format) {
		switch (format) {
		case DEFAULT:
		case NEAREST:
			setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			break;
		case FONT:
		case LINEAR:
			setFilter(TextureFilter.Linear, TextureFilter.Linear);
			setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			break;
		case STATIC:
		case SPEED:
			setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			break;
		case BILINEAR:
			setFilter(TextureFilter.Linear, TextureFilter.Linear);
			setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			break;
		case REPEATING:
			setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			break;
		case REPEATING_BILINEAR:
		case REPEATING_BILINEAR_PREMULTIPLYALPHA:
			setFilter(TextureFilter.Linear, TextureFilter.Linear);
			setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			break;
		}
	}

	public enum TextureFilter {
		Nearest(GL20.GL_NEAREST), Linear(GL20.GL_LINEAR), MipMap(
				GL20.GL_LINEAR_MIPMAP_LINEAR), MipMapNearestNearest(
				GL20.GL_NEAREST_MIPMAP_NEAREST), MipMapLinearNearest(
				GL20.GL_LINEAR_MIPMAP_NEAREST), MipMapNearestLinear(
				GL20.GL_NEAREST_MIPMAP_LINEAR), MipMapLinearLinear(
				GL20.GL_LINEAR_MIPMAP_LINEAR);

		final int glEnum;

		TextureFilter(int glEnum) {
			this.glEnum = glEnum;
		}

		public boolean isMipMap() {
			return glEnum != GL20.GL_NEAREST && glEnum != GL20.GL_LINEAR;
		}

		public int getGLEnum() {
			return glEnum;
		}
	}

	public int getTextureObjectHandle() {
		return this.textureID;
	}

	public enum TextureWrap {
		MirroredRepeat(GL20.GL_MIRRORED_REPEAT), ClampToEdge(
				GL20.GL_CLAMP_TO_EDGE), Repeat(GL20.GL_REPEAT);

		final int glEnum;

		TextureWrap(int glEnum) {
			this.glEnum = glEnum;
		}

		public int getGLEnum() {
			return glEnum;
		}
	}

	public int getTextureID() {
		return textureID;
	}

	public void setVertCords(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setTexCords(float texXOff, float texYOff, float texWidthRatio,
			float texHeightRatio) {
		this.xOff = texXOff;
		this.yOff = texYOff;
		this.widthRatio = texWidthRatio;
		this.heightRatio = texHeightRatio;
	}

	public void setWidth(int width) {
		this.width = width;
		setVertCords(width, height);
	}

	public void setHeight(int height) {
		this.height = height;
		setVertCords(width, height);
	}

	public int getWidth() {
		if (width == 0 && imageData != null) {
			return imageData.getWidth();
		}
		return width;
	}

	public int getHeight() {
		if (height == 0 && imageData != null) {
			return imageData.getHeight();
		}
		return height;
	}

	public void setTextureWidth(int textureWidth) {
		setTextureSize(textureWidth, texHeight);
	}

	public void setTextureHeight(int textureHeight) {
		setTextureSize(texWidth, textureHeight);
	}

	public float getTextureWidth() {
		return texWidth;
	}

	public float getTextureHeight() {
		return texHeight;
	}

	public void setTextureSize(int textureWidth, int textureHeight) {
		this.texWidth = textureWidth;
		this.texHeight = textureHeight;
		setTexCordRatio();
	}

	private void setTexCordRatio() {
		widthRatio = (float) width / (texWidth < 1 ? width : texWidth);
		heightRatio = (float) height / (texHeight < 1 ? height : texHeight);
		setTexCords(xOff, yOff, widthRatio, heightRatio);
	}

	public LTexture getSubTexture(final int x, final int y, final int width,
			final int height) {

		int hashCode = 1;

		hashCode = LSystem.unite(hashCode, x);
		hashCode = LSystem.unite(hashCode, y);
		hashCode = LSystem.unite(hashCode, width);
		hashCode = LSystem.unite(hashCode, height);

		if (childs == null) {
			childs = new HashMap<Integer, LTexture>(10);
		}

		synchronized (childs) {
			LTexture cache = childs.get(hashCode);

			if (cache != null) {
				return cache;
			}

			final LTexture sub = new LTexture();

			if (isLoaded || !LSystem.isThreadDrawing()) {
				sub.parent = LTexture.this;
				sub.textureID = textureID;
				sub.isLoaded = isLoaded;
				sub.imageData = imageData;
				sub.hasAlpha = hasAlpha;
				sub.isStatic = isStatic;
				sub.reload = reload;
				sub.format = format;
				sub.width = width;
				sub.height = height;
				sub.texWidth = texWidth;
				sub.texHeight = texHeight;
				sub.setVertCords(width, height);
				sub.xOff = (((float) x / this.width) * widthRatio) + xOff;
				sub.yOff = (((float) y / this.height) * heightRatio) + yOff;
				sub.widthRatio = (((float) width / LTexture.this.width) * widthRatio)
						+ sub.xOff;
				sub.heightRatio = (((float) height / LTexture.this.height) * heightRatio)
						+ sub.yOff;
				sub.setTexCords(sub.xOff, sub.yOff, sub.widthRatio,
						sub.heightRatio);
				crop(sub, x, y, width, height);

			} else {

				sub.width = width;
				sub.height = height;
				sub.texWidth = texWidth;
				sub.texHeight = texHeight;
				sub.imageData = imageData;
				sub.subX = x;
				sub.subY = y;
				sub.subWidth = width;
				sub.subHeight = height;
				sub.isVisible = false;

				Updateable u = new Updateable() {

					public void action(Object a) {

						loadTexture();

						sub.parent = LTexture.this;
						sub.textureID = textureID;
						sub.isLoaded = isLoaded;
						sub.imageData = imageData;
						sub.hasAlpha = hasAlpha;
						sub.isStatic = isStatic;
						sub.reload = reload;
						sub.format = format;
						sub.width = width;
						sub.height = height;
						sub.texWidth = texWidth;
						sub.texHeight = texHeight;
						sub.setVertCords(width, height);
						sub.xOff = (((float) x / LTexture.this.width) * widthRatio)
								+ xOff;
						sub.yOff = (((float) y / LTexture.this.height) * heightRatio)
								+ yOff;
						sub.widthRatio = (((float) width / LTexture.this.width) * widthRatio)
								+ sub.xOff;
						sub.heightRatio = (((float) height / LTexture.this.height) * heightRatio)
								+ sub.yOff;
						sub.setTexCords(sub.xOff, sub.yOff, sub.widthRatio,
								sub.heightRatio);
						crop(sub, x, y, width, height);

						sub.isVisible = true;
					}
				};
				LSystem.load(u);
			}
			isChild = true;
			childs.put(hashCode, sub);

			return sub;
		}
	}

	public void closeChildAll() {
		if (childs != null) {
			for (LTexture tex2d : childs.values()) {
				if (tex2d != null) {
					tex2d.destroy();
					tex2d = null;
				}
			}
			System.gc();
		}
	}

	public boolean isChildAllClose() {
		if (childs != null) {
			for (LTexture tex2d : childs.values()) {
				if (tex2d != null && !tex2d.isClose) {
					return false;
				}
			}
		}
		return true;
	}

	public LTexture scale(float scale) {
		int nW = (int) (width * scale);
		int nH = (int) (height * scale);
		return copy(nW, nH, false, false);
	}

	public LTexture scale(int width, int height) {
		return copy(width, height, false, false);
	}

	public LTexture copy() {
		return copy(width, height, false, false);
	}

	public LTexture flip(boolean flipHorizontal, boolean flipVertial) {
		return copy(width, height, flipHorizontal, flipVertial);
	}

	private LTexture copy(final int width, final int height,
			final boolean flipHorizontal, final boolean flipVertial) {

		int hashCode = 1;

		hashCode = LSystem.unite(hashCode, width);
		hashCode = LSystem.unite(hashCode, height);
		hashCode = LSystem.unite(hashCode, flipHorizontal);
		hashCode = LSystem.unite(hashCode, flipVertial);

		if (childs == null) {
			childs = new HashMap<Integer, LTexture>(10);
		}

		synchronized (childs) {

			LTexture cache = childs.get(hashCode);

			if (cache != null) {
				return cache;
			}

			final LTexture copy = new LTexture();

			if (isLoaded || !LSystem.isThreadDrawing()) {
				copy.parent = LTexture.this;
				copy.imageData = imageData;
				copy.textureID = textureID;
				copy.isLoaded = isLoaded;
				copy.isStatic = isStatic;
				copy.reload = reload;
				copy.format = format;
				copy.hasAlpha = hasAlpha;
				copy.setVertCords(width, height);
				copy.texWidth = texWidth;
				copy.texHeight = texHeight;
				copy.setTexCords(xOff, yOff, widthRatio, heightRatio);

			} else {

				copy.width = width;
				copy.height = height;
				copy.texWidth = texWidth;
				copy.texHeight = texHeight;
				copy.imageData = imageData;
				copy.subX = 0;
				copy.subY = 0;
				copy.subWidth = width;
				copy.subHeight = height;
				copy.isVisible = false;

				Updateable u = new Updateable() {

					public void action(Object a) {

						loadTexture();

						copy.parent = LTexture.this;
						copy.imageData = imageData;
						copy.textureID = textureID;
						copy.isLoaded = isLoaded;
						copy.isStatic = isStatic;
						copy.reload = reload;
						copy.format = format;
						copy.hasAlpha = hasAlpha;
						copy.setVertCords(width, height);
						copy.texWidth = texWidth;
						copy.texHeight = texHeight;
						copy.setTexCords(xOff, yOff, widthRatio, heightRatio);

						copy.isVisible = true;
					}
				};
				LSystem.load(u);
			}
			isChild = true;
			childs.put(hashCode, copy);

			return copy;
		}
	}

	public boolean isChild() {
		return isChild;
	}

	private void crop(LTexture texture, int x, int y, int width, int height) {
		texture.subX = x;
		texture.subY = y;
		texture.subWidth = width;
		texture.subHeight = height;
	}

	public LTexture getParent() {
		return parent;
	}

	public synchronized void bind() {
		GLEx.gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
	}

	public synchronized void bind(int unit) {
		GLEx.gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
		GLEx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureID);
	}
	

	public synchronized void unbind() {
		GLEx.gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}


	public int hashCode() {
		if (_hashCode == 1 && imageData.source != null) {
			ByteBuffer buff = (ByteBuffer) imageData.source;
			buff.rewind();
			for (int j = 0; j < buff.limit(); j++) {
				_hashCode = LSystem.unite(_hashCode, buff.get());
			}
			buff.position(0);
		}
		return _hashCode;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public boolean isClose() {
		return isClose;
	}

	public Format getFormat() {
		return format;
	}

	public void dispose() {
		dispose(true);
	}

	void dispose(final boolean remove) {
		if (!isChildAllClose()) {
			return;
		}
		LTextures.removeTexture(this, remove);
	}

	public boolean isRecycled() {
		return this.isClose;
	}

	private Shape shapeCache;

	private Mask maskCache;

	public static class Mask implements Serializable, LRelease {

		/**
		 * 像素遮挡关系处理器
		 */
		private static final long serialVersionUID = -4316629891519820901L;

		private int height;

		private int width;

		private boolean[][] data;

		public Mask(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public Mask(boolean[][] data, int width, int height) {
			this.data = data;
			this.width = width;
			this.height = height;
		}

		public boolean[][] getData() {
			return data;
		}

		public boolean getPixel(int x, int y) {
			if (x < 0 || x >= width || y < 0 || y >= height) {
				return false;
			}
			return data[y][x];
		}

		public void setData(boolean[][] data) {
			this.data = data;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public void dispose() {
			if (data != null) {
				data = null;
			}
		}

	}

	public Shape getShape() {
		if (shapeCache != null) {
			return shapeCache;
		}
		LImage shapeImage = getImage();
		if (shapeImage != null) {
			Polygon polygon = CollisionMask.makePolygon(shapeImage);
			if (shapeImage != null) {
				shapeImage.dispose();
				shapeImage = null;
			}
			return (shapeCache = polygon);
		}
		throw new RuntimeException("Create texture for shape fail !");
	}

	public Mask getMask() {
		if (maskCache != null) {
			return maskCache;
		}
		LImage maskImage = getImage();
		if (maskImage != null) {
			Mask mask = CollisionMask.createMask(maskImage);
			if (maskImage != null) {
				maskImage.dispose();
				maskImage = null;
			}
			return (maskCache = mask);
		}
		throw new RuntimeException("Create texture for shape fail !");
	}

	public final LImage getImage() {
		LImage image = null;
		if (imageData != null) {
			if (imageData.fileName != null) {
				image = LImage.createImage(imageData.fileName);
			} else if (imageData.pixels != null) {
				int[] data = imageData.pixels;
				if (data != null) {
					image = createPixelImage(data, imageData.texWidth,
							imageData.texHeight, imageData.width,
							imageData.height, imageData.hasAlpha);
				}
			}
		}
		if (subWidth != 0 && subHeight != 0) {
			if (image != null) {
				LImage tmp = image.getSubImage(subX, subY, subWidth, subHeight);
				if (tmp != image) {
					if (image != null) {
						image.dispose();
						image = null;
					}
					return tmp;
				} else {
					return image;
				}
			}
		}
		return image;
	}

	private final static LImage createPixelImage(int[] pixels, int texWidth,
			int texHeight, int width, int height, boolean alpha) {
		LImage image = new LImage(texWidth, texHeight, alpha);
		image.setPixels(pixels, texWidth, texHeight);
		if (texWidth != width || texHeight != height) {
			LImage temp = image.getSubImage(0, 0, width, height);
			if (temp != image) {
				if (image != null) {
					image.dispose();
					image = null;
				}
				image = temp;
			}
		}
		return image;
	}
	
	public LTextureBatch getBatch(){
		return new LTextureBatch(this);
	}

	public void setImageColor(float r, float g, float b, float a) {
		setColor(TOP_LEFT, r, g, b, a);
		setColor(TOP_RIGHT, r, g, b, a);
		setColor(BOTTOM_LEFT, r, g, b, a);
		setColor(BOTTOM_RIGHT, r, g, b, a);
	}

	public void setImageColor(float r, float g, float b) {
		setColor(TOP_LEFT, r, g, b);
		setColor(TOP_RIGHT, r, g, b);
		setColor(BOTTOM_LEFT, r, g, b);
		setColor(BOTTOM_RIGHT, r, g, b);
	}

	public void setImageColor(LColor c) {
		if (c == null) {
			return;
		}
		setImageColor(c.r, c.g, c.b, c.a);
	}

	public void setColor(int corner, float r, float g, float b, float a) {

	}

	public void setColor(int corner, float r, float g, float b) {

	}

	void freeBatch() {

	}

	public boolean isBatch() {
		return false;
	}

	public void glBegin() {

	}

	public void glBegin(int type) {

	}

	public void glEnd() {

	}

	public void setBatchPos(float x, float y) {

	}

	public boolean isBatchLocked() {
		return false;
	}

	public void glCacheCommit() {

	}

	public void glLock() {

	}

	public void glUnLock() {

	}

	public void draw(float x, float y) {
		draw(x, y, width, height);
	}

	public void draw(float x, float y, float width, float height) {

	}

	public void draw(float x, float y, LColor[] c) {

	}

	public void draw(float x, float y, LColor c) {

	}

	public void draw(float x, float y, float width, float height, LColor c) {

	}

	public void drawFlipX(float x, float y, LColor c) {

	}

	public void drawFlipY(float x, float y, LColor c) {

	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2, LColor[] c) {

	}

	public void drawEmbedded(float x, float y, float width, float height,
			float x1, float y1, float x2, float y2, LColor c) {
		draw(x, y, width - x, height - y, x1, y1, x2, y2, c);
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2, LColor c) {

	}

	public void draw(float x, float y, float srcX, float srcY, float srcWidth,
			float srcHeight) {

	}

	public void drawEmbedded(float x, float y, float width, float height,
			float x1, float y1, float x2, float y2) {
		draw(x, y, width - x, height - y, x1, y1, x2, y2);
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2) {

	}

	public void draw(float x, float y, float rotation) {
		draw(x, y, this.width, this.height, 0, 0, this.width, this.height,
				rotation, LColor.white);
	}

	public void draw(float x, float y, float w, float h, float rotation,
			LColor c) {
		draw(x, y, w, h, 0, 0, this.width, this.height, rotation, c);
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2, float rotation, LColor c) {

	}

	public void freeCache() {
		if (shapeCache != null) {
			shapeCache = null;
		}
		if (maskCache != null) {
			maskCache.dispose();
			maskCache = null;
		}
	}

	public void destroy() {
		destroy(true);
	}

	public void destroy(boolean remove) {
		dispose(remove);
		freeCache();
		freeBatch();
	}

}
