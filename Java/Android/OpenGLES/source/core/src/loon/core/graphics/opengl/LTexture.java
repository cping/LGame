package loon.core.graphics.opengl;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.HashMap;

import loon.action.collision.CollisionMask;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.geom.Polygon;
import loon.core.geom.Shape;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.opengl.LTextureBatch.GLCache;
import loon.jni.NativeSupport;
import loon.utils.CollectionUtils;

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
public class LTexture implements LRelease {

	public LTextureRegion getTextureRegion(int x, int y, int width, int height) {
		return new LTextureRegion(this, x, y, width, height);
	}

	public LTextureRegion[][] split(int tileWidth, int tileHeight) {
		return new LTextureRegion(this).split(tileWidth, tileHeight);
	}

	public Object Tag;

	int refCount = 0;

	public static final int TOP_LEFT = 0;

	public static final int TOP_RIGHT = 1;

	public static final int BOTTOM_RIGHT = 2;

	public static final int BOTTOM_LEFT = 3;

	public static boolean ALL_LINEAR = false;

	public static boolean ALL_NEAREST = false;

	public static void AUTO_LINEAR() {
		if (!LSystem.isEmulator() && !GLEx.isPixelFlinger()
				&& (LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1)) {
			LTexture.ALL_LINEAR = true;
		}
	}

	public static void AUTO_NEAREST() {
		if (!LSystem.isEmulator()
				&& (LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1)) {
			LTexture.ALL_NEAREST = true;
		}
	}

	public static enum Format {
		DEFAULT, NEAREST, LINEAR, SPEED, STATIC, FONT, BILINEAR, REPEATING, REPEATING_BILINEAR, REPEATING_BILINEAR_PREMULTIPLYALPHA;
	}

	private final int[] GENERATED_TEXTUREID = new int[1];

	private LColor[] colors;

	private int subX, subY, subWidth, subHeight;

	private LTextureBatch batch;

	private boolean isBatch;

	int _hashCode = 1;

	LTextureData imageData;

	LTexture parent;

	HashMap<Integer, LTexture> childs;

	boolean replace, reload;

	boolean isLoaded, isClose, hasAlpha;

	boolean isVisible = true;

	boolean isChild;

	int width, texWidth;

	int height, texHeight;

	int textureID, bufferID;

	public float xOff = 0.0f;

	public float yOff = 0.0f;

	public float widthRatio = 1.0f;

	public float heightRatio = 1.0f;

	final int[] crops = { 0, 0, 0, 0 };

	float dataCords[];

	FloatBuffer data;

	int dataSize;

	int vertexSize;

	int texSize;

	Format format;

	String lazyName;

	boolean isStatic;

	private LTexture() {
		format = Format.DEFAULT;
		imageData = null;
		checkReplace();
	}

	public LTexture(LTexture texture) {
		if (texture == null) {
			throw new RuntimeException("texture is Null !");
		}
		this.imageData = texture.imageData;
		this.parent = texture.parent;
		this.format = texture.format;
		if (texture.colors != null) {
			this.colors = (LColor[]) CollectionUtils.copyOf(texture.colors);
		}
		if (texture.dataCords != null) {
			this.dataCords = CollectionUtils.copyOf(texture.dataCords);
		}
		if (texture.data != null) {
			this.data = NativeSupport.clone(texture.data);
		}
		this.hasAlpha = texture.hasAlpha;
		this.textureID = texture.textureID;
		this.bufferID = texture.bufferID;
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
		this.replace = texture.replace;
		this.isLoaded = texture.isLoaded;
		this.isClose = texture.isClose;
		this.isStatic = texture.isStatic;
		this.isVisible = texture.isVisible;
		System.arraycopy(texture.crops, 0, crops, 0, crops.length);
	}

	public LTexture(String res) {
		this(res, Format.DEFAULT);
	}

	public LTexture(String res, Format format,
			android.graphics.Bitmap.Config config) {
		this(GLLoader.getTextureData(res, config), format);
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
		this.init(d, format);
	}

	public LTexture(String res, boolean multipyAlpha) {
		this(res, Format.DEFAULT, multipyAlpha);
	}

	public LTexture(String res, Format format, boolean multipyAlpha) {
		this(res, null, format, multipyAlpha);
	}

	public LTexture(String res, android.graphics.Bitmap.Config config,
			Format format, boolean multipyAlpha) {
		LTextureData data = GLLoader.getTextureData(res, config);
		data.setMultipyAlpha(multipyAlpha);
		this.init(data, format);
	}

	private void init(LTextureData d, Format format) {
		this.format = format;
		this.imageData = d;
		this.texWidth = d.texWidth;
		this.texHeight = d.texHeight;
		this.width = d.width;
		this.height = d.height;
		this.checkReplace();
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

	public final void checkReplace() {
		this.replace = Format.BILINEAR == format || Format.BILINEAR == format
				|| Format.REPEATING_BILINEAR == format;
		this.isStatic = format == Format.SPEED || format == Format.STATIC;
	}

	public synchronized final void loadTexture() {
		if (parent != null) {
			parent.loadTexture();
			textureID = parent.textureID;
			isLoaded = parent.isLoaded;
			return;
		}
		if (imageData == null || isLoaded || GLEx.gl == null) {
			return;
		}
		if (imageData.source == null && imageData.fileName != null) {
			imageData = GLLoader.getTextureData(imageData.fileName);
		}
		if (imageData.source == null && imageData.fileName == null) {
			return;
		}
		isLoaded = true;
		loadTextureBuffer();
		setFormat(format);
		LTextures.loadTexture(this);
		LTextureBatch.isBatchCacheDitry = true;
	}

	private synchronized void loadTextureBuffer() {
		if (!reload) {
			this.textureID = createTextureID();
			this.reload = false;
		}
		bind();

		hasAlpha = imageData.hasAlpha;

		setWidth(imageData.width);
		setHeight(imageData.height);
		setTextureWidth(imageData.texWidth);
		setTextureHeight(imageData.texHeight);

		imageData.createTexture();

		if (GLEx.isVbo()) {
			GLEx.updateHardwareBuff(this);
		}

	}

	public void reload() {
		this.isLoaded = false;
		this.reload = true;
		this._hashCode = 1;
		if (childs != null) {
			Updateable u = new Updateable() {
				@Override
				public void action() {
					loadTexture();
					for (int i = 0; i < childs.size(); i++) {
						LTexture child = childs.get(i);
						if (child != null) {
							child.textureID = textureID;
							child.isLoaded = isLoaded;
							child.reload = reload;
							if (GLEx.isVbo()) {
								child.bufferID = GLEx.createBufferID();
								GLEx.bufferDataARR(child.bufferID, data,
										GL11.GL_STATIC_DRAW);
							}
						}
					}
					LTextureBatch.isBatchCacheDitry = true;
				}
			};
			LSystem.load(u);
		}
	}

	private synchronized int createTextureID() {
		if (textureID > 0) {
			GLEx.deleteTexture(this.textureID);
			this.textureID = -1;
			GLEx.deleteBuffer(this.bufferID);
			this.bufferID = -1;
		}
		GLEx.gl10.glGenTextures(1, GENERATED_TEXTUREID, 0);
		return (textureID = GENERATED_TEXTUREID[0]);
	}

	public boolean isReplace() {
		return replace;
	}

	public void setFormat(Format format) {

		int minFilter = GL.GL_NEAREST;
		int maxFilter = GL.GL_NEAREST;
		int wrapS = GL.GL_CLAMP_TO_EDGE;
		int wrapT = GL.GL_CLAMP_TO_EDGE;
		int texEnv = GL.GL_MODULATE;

		if (imageData != null) {
			if (format == Format.DEFAULT && imageData.hasAlpha) {
				format = Format.SPEED;
			} else if (format == Format.DEFAULT && !imageData.hasAlpha) {
				format = Format.STATIC;
				this.format = format;
			}
		}

		switch (format) {
		case DEFAULT:
		case NEAREST:
			break;
		case LINEAR:
			minFilter = GL.GL_LINEAR;
			maxFilter = GL.GL_LINEAR;
			wrapS = GL.GL_CLAMP_TO_EDGE;
			wrapT = GL.GL_CLAMP_TO_EDGE;
			texEnv = GL.GL_MODULATE;
			break;
		case STATIC:
		case SPEED:
			minFilter = GL.GL_NEAREST;
			maxFilter = GL.GL_NEAREST;
			wrapS = GL.GL_REPEAT;
			wrapT = GL.GL_REPEAT;
			texEnv = GL.GL_REPLACE;
			break;
		case BILINEAR:
			minFilter = GL.GL_LINEAR;
			maxFilter = GL.GL_LINEAR;
			wrapS = GL.GL_CLAMP_TO_EDGE;
			wrapT = GL.GL_CLAMP_TO_EDGE;
			texEnv = GL.GL_REPLACE;
			break;
		case REPEATING:
			minFilter = GL.GL_NEAREST;
			maxFilter = GL.GL_NEAREST;
			wrapS = GL.GL_REPEAT;
			wrapT = GL.GL_REPEAT;
			texEnv = GL.GL_REPLACE;
			break;
		case REPEATING_BILINEAR:
			minFilter = GL.GL_LINEAR;
			maxFilter = GL.GL_LINEAR;
			wrapS = GL.GL_REPEAT;
			wrapT = GL.GL_REPEAT;
			texEnv = GL.GL_REPLACE;
			break;
		case REPEATING_BILINEAR_PREMULTIPLYALPHA:
			minFilter = GL.GL_LINEAR;
			maxFilter = GL.GL_LINEAR;
			wrapS = GL.GL_REPEAT;
			wrapT = GL.GL_REPEAT;
			texEnv = GL.GL_MODULATE;
			break;
		default:
			break;
		}

		if (format != Format.FONT) {
			if (ALL_LINEAR && !ALL_NEAREST) {
				minFilter = GL.GL_LINEAR;
				maxFilter = GL.GL_LINEAR;
			} else if (ALL_NEAREST && !ALL_LINEAR) {
				minFilter = GL.GL_NEAREST;
				maxFilter = GL.GL_NEAREST;
			} else if (ALL_NEAREST && ALL_LINEAR) {
				minFilter = GL.GL_NEAREST;
				maxFilter = GL.GL_LINEAR;
			}
		}

		GL10 gl10 = GLEx.gl10;
		if (gl10 == null) {
			return;
		}
		gl10.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				minFilter);
		gl10.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				maxFilter);
		gl10.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrapS);
		gl10.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrapT);
		gl10.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, texEnv);
	}

	public int getTextureID() {
		return textureID;
	}

	public void setVertCords(int width, int height) {
		if (dataCords == null) {

			dataCords = new float[] { 0.0f, 0.0f, width, 0.0f, 0.0f, height,
					width, height, xOff, yOff, widthRatio, yOff, xOff,
					heightRatio, widthRatio, heightRatio };
			data = NativeSupport.getFloatBuffer(dataCords);

			dataSize = data.capacity() * 4;

			vertexSize = 8 * 4;

			texSize = 8 * 4;
		}
		dataCords[0] = 0;
		dataCords[1] = 0;
		dataCords[2] = width;
		dataCords[3] = 0;
		dataCords[4] = 0;
		dataCords[5] = height;
		dataCords[6] = width;
		dataCords[7] = height;

		this.width = width;
		this.height = height;

		NativeSupport.replaceFloats(data, dataCords);

	}

	public void setTexCords(float texXOff, float texYOff, float texWidthRatio,
			float texHeightRatio) {
		if (dataCords == null) {

			dataCords = new float[] { 0.0f, 0.0f, imageData.width, 0.0f, 0.0f,
					imageData.height, imageData.width, imageData.height, xOff,
					yOff, widthRatio, yOff, xOff, heightRatio, widthRatio,
					heightRatio };
			data = NativeSupport.getFloatBuffer(dataCords);

			dataSize = data.capacity() * 4;

			vertexSize = 8 * 4;

			texSize = 8 * 4;
		}
		dataCords[8] = texXOff;
		dataCords[9] = texYOff;
		dataCords[10] = texWidthRatio;
		dataCords[11] = texYOff;
		dataCords[12] = texXOff;
		dataCords[13] = texHeightRatio;
		dataCords[14] = texWidthRatio;
		dataCords[15] = texHeightRatio;

		this.xOff = texXOff;
		this.yOff = texYOff;
		this.widthRatio = texWidthRatio;
		this.heightRatio = texHeightRatio;
		NativeSupport.replaceFloats(data, dataCords);
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
				sub.replace = replace;
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
				if (GLEx.isVbo()) {
					sub.bufferID = GLEx.createBufferID();
					GLEx.bufferDataARR(sub.bufferID, sub.data,
							GL11.GL_STATIC_DRAW);
				}
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

					@Override
					public void action() {

						loadTexture();

						sub.parent = LTexture.this;
						sub.textureID = textureID;
						sub.isLoaded = isLoaded;
						sub.imageData = imageData;
						sub.hasAlpha = hasAlpha;
						sub.replace = replace;
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
						if (GLEx.isVbo()) {
							sub.bufferID = GLEx.createBufferID();
							GLEx.bufferDataARR(sub.bufferID, sub.data,
									GL11.GL_STATIC_DRAW);
						}
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

			if (dataCords == null) {
				setVertCords(this.getWidth(), this.getHeight());
			}

			final LTexture copy = new LTexture();

			if (isLoaded || !LSystem.isThreadDrawing()) {

				copy.parent = LTexture.this;
				copy.imageData = imageData;
				copy.textureID = textureID;
				copy.isLoaded = isLoaded;
				copy.replace = replace;
				copy.isStatic = isStatic;
				copy.reload = reload;
				copy.format = format;
				copy.hasAlpha = hasAlpha;
				copy.setVertCords(width, height);
				copy.texWidth = texWidth;
				copy.texHeight = texHeight;
				copy.setTexCords(xOff, yOff, widthRatio, heightRatio);
				if (flipHorizontal) {
					swap(8, 10, copy.dataCords);
					swap(12, 14, copy.dataCords);
				}
				if (flipVertial) {
					swap(9, 13, copy.dataCords);
					swap(11, 15, copy.dataCords);
				}
				copy.xOff = dataCords[8];
				copy.yOff = dataCords[9];
				copy.widthRatio = dataCords[14];
				copy.heightRatio = dataCords[15];
				NativeSupport.replaceFloats(copy.data, copy.dataCords);
				System.arraycopy(crops, 0, copy.crops, 0, crops.length);
				if (GLEx.isVbo()) {
					copy.bufferID = GLEx.createBufferID();
					GLEx.bufferDataARR(copy.bufferID, copy.data,
							GL11.GL_STATIC_DRAW);
				}
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

					@Override
					public void action() {

						loadTexture();
						copy.parent = LTexture.this;
						copy.imageData = imageData;
						copy.textureID = textureID;
						copy.isLoaded = isLoaded;
						copy.replace = replace;
						copy.isStatic = isStatic;
						copy.reload = reload;
						copy.format = format;
						copy.hasAlpha = hasAlpha;
						copy.setVertCords(width, height);
						copy.texWidth = texWidth;
						copy.texHeight = texHeight;
						copy.setTexCords(xOff, yOff, widthRatio, heightRatio);
						if (flipHorizontal) {
							swap(8, 10, copy.dataCords);
							swap(12, 14, copy.dataCords);
						}
						if (flipVertial) {
							swap(9, 13, copy.dataCords);
							swap(11, 15, copy.dataCords);
						}
						copy.xOff = dataCords[8];
						copy.yOff = dataCords[9];
						copy.widthRatio = dataCords[14];
						copy.heightRatio = dataCords[15];
						NativeSupport.replaceFloats(copy.data, copy.dataCords);
						System.arraycopy(crops, 0, copy.crops, 0, crops.length);
						if (GLEx.isVbo()) {
							copy.bufferID = GLEx.createBufferID();
							GLEx.bufferDataARR(copy.bufferID, copy.data,
									GL11.GL_STATIC_DRAW);
						}
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

	private void crop(LTexture texture, int x, int y, int width, int height) {
		texture.crops[0] = x;
		texture.crops[1] = height + y;
		texture.crops[2] = width;
		texture.crops[3] = -height;
		texture.subX = x;
		texture.subY = y;
		texture.subWidth = width;
		texture.subHeight = height;
	}

	public boolean isChild() {
		return isChild;
	}

	private void swap(int idx1, int idx2, float[] texCords) {
		float tmp = texCords[idx1];
		texCords[idx1] = texCords[idx2];
		texCords[idx2] = tmp;
	}

	public LTexture getParent() {
		return parent;
	}

	public synchronized void bind() {
		GLEx.gl10.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		GLEx.gl10.glBindTexture(GL.GL_TEXTURE_2D, textureID);
	}

	public synchronized void bind(int unit) {
		GLEx.gl10.glActiveTexture(GL.GL_TEXTURE0 + unit);
		GLEx.gl10.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		GLEx.gl10.glBindTexture(GL.GL_TEXTURE_2D, textureID);
	}

	@Override
	public int hashCode() {
		if (_hashCode == 1) {
			int[] buffer = imageData.source;
			if (buffer == null) {
				if (imageData.fileName != null) {
					LImage tmp = LImage.createImage(imageData.fileName);
					if (tmp != null) {
						buffer = tmp.getPixels();
						tmp.dispose();
						tmp = null;
					}
				} else {
					_hashCode = LSystem.unite(_hashCode, width);
					_hashCode = LSystem.unite(_hashCode, height);
					_hashCode = LSystem.unite(_hashCode, texWidth);
					_hashCode = LSystem.unite(_hashCode, texHeight);
					_hashCode = LSystem.unite(_hashCode, textureID);
					_hashCode = LSystem.unite(_hashCode, texSize);
					if (dataCords != null) {
						for (int i = 0; i < dataCords.length; i++) {
							_hashCode = LSystem.unite(_hashCode, dataCords[i]);
						}
					}
					return _hashCode;
				}
			}
			int skip = 3;
			int limit = buffer.length;
			if (limit < 512) {
				skip = 1;
			}
			for (int j = 0; j < limit; j += skip) {
				if (j < limit) {
					_hashCode = LSystem.unite(_hashCode, buffer[j]);
				}
			}
			if (dataCords != null) {
				for (int i = 0; i < dataCords.length; i++) {
					_hashCode = LSystem.unite(_hashCode, dataCords[i]);
				}
			}
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

	public LTextureData getImageData() {
		return imageData;
	}

	public Format getFormat() {
		return format;
	}

	public void closeChildAll() {
		if (childs != null) {
			for (LTexture tex2d : childs.values()) {
				if (tex2d != null && !tex2d.isClose) {
					tex2d.destroy();
					tex2d = null;
				}
			}
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

	@Override
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

		@Override
		public void dispose() {
			if (data != null) {
				data = null;
			}
		}

	}

	private Mask maskCache;

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
			} else if (imageData.source != null) {
				int[] data = imageData.source;
				if (data != null) {
					image = LTextureData
							.createPixelImage(data, imageData.texWidth,
									imageData.texHeight, imageData.width,
									imageData.height, imageData.config);
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
		if (colors == null) {
			colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
		colors[corner].a = a;
	}

	public void setColor(int corner, float r, float g, float b) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
	}

	public LTextureBatch getTextureBatch() {
		makeBatch();
		return batch;
	}

	void makeBatch() {
		if (!isBatch) {
			batch = new LTextureBatch(this);
			isBatch = true;
		}
	}

	void freeBatch() {
		if (isBatch) {
			if (batch != null) {
				batch.dispose();
				batch = null;
				isBatch = false;
			}
		}
	}

	public boolean isBatch() {
		return (isBatch && batch.useBegin);
	}

	public void glBegin() {
		makeBatch();
		batch.glBegin();
	}

	public void glBegin(int type) {
		makeBatch();
		batch.glBegin(type);
	}

	public void glEnd() {
		if (isBatch) {
			batch.glEnd();
		}
	}

	public void setBatchPos(float x, float y) {
		if (isBatch) {
			batch.setLocation(x, y);
		}
	}

	public boolean isBatchLocked() {
		return isBatch && batch.isLocked;
	}

	public void glCacheCommit() {
		if (isBatch) {
			batch.glCacheCommit();
		}
	}

	public void glLock() {
		if (isBatch) {
			batch.lock();
		}
	}

	public void glUnLock() {
		if (isBatch) {
			batch.unLock();
		}
	}

	public void draw(float x, float y) {
		draw(x, y, width, height);
	}

	public void draw(float x, float y, float width, float height) {
		if (isBatch) {
			batch.draw(colors, x, y, width, height);
		} else {
			GLEx.self.drawTexture(this, x, y, width, height);
		}
	}

	public void draw(float x, float y, LColor[] c) {
		if (isBatch) {
			batch.draw(c, x, y, width, height);
		} else {
			GLEx.self.drawTexture(this, x, y, width, height);
		}
	}

	public void draw(float x, float y, LColor c) {
		if (isBatch) {
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height);
			if (update) {
				setImageColor(LColor.white);
			}
		} else {
			GLEx.self.drawTexture(this, x, y, width, height, c);
		}
	}

	public void draw(float x, float y, float width, float height, LColor c) {
		if (isBatch) {
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height);
			if (update) {
				setImageColor(LColor.white);
			}
		} else {
			GLEx.self.drawTexture(this, x, y, width, height, c);
		}
	}

	public void drawFlipX(float x, float y, LColor c) {
		if (isBatch) {
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height, 0, 0, width, height, true,
					false);
			if (update) {
				setImageColor(LColor.white);
			}
		} else {
			GLEx.self.drawFlipTexture(this, x, y, c);
		}
	}

	public void drawFlipY(float x, float y, LColor c) {
		if (isBatch) {
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height, 0, 0, width, height, false,
					true);
			if (update) {
				setImageColor(LColor.white);
			}
		} else {
			GLEx.self.drawMirrorTexture(this, x, y, c);
		}
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2, LColor[] c) {
		if (isBatch) {
			batch.draw(c, x, y, width, height, x1, y1, x2, y2);
		} else {
			GLEx.self.drawTexture(this, x, y, width, height, x1, y1, x2, y2);
		}
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2, LColor c) {
		if (isBatch) {
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height, x1, y1, x2, y2);
			if (update) {
				setImageColor(LColor.white);
			}
		} else {
			GLEx.self.drawTexture(this, x, y, width, height, x1, y1, x2, y2);
		}
	}

	public void draw(float x, float y, float srcX, float srcY, float srcWidth,
			float srcHeight) {
		if (isBatch) {
			batch.draw(colors, x, y, srcWidth - srcX, srcHeight - srcY, srcX,
					srcY, srcWidth, srcHeight);
		} else {
			GLEx.self.drawTexture(this, x, y, srcWidth - srcX,
					srcHeight - srcY, srcX, srcY, srcWidth, srcHeight);
		}
	}

	public void drawEmbedded(float x, float y, float width, float height,
			float x1, float y1, float x2, float y2) {
		draw(x, y, width - x, height - y, x1, y1, x2, y2);
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2) {
		if (isBatch) {
			batch.draw(colors, x, y, width, height, x1, y1, x2, y2);
		} else {
			GLEx.self.drawTexture(this, x, y, width, height, x1, y1, x2, y2);
		}
	}

	public void draw(float x, float y, float rotation) {
		draw(x, y, this.width, this.height, 0, 0, this.width, this.height,
				rotation, LColor.white);
	}

	public void draw(float x, float y, float w, float h, float rotation,
			LColor c) {
		draw(x, y, w, h, 0, 0, this.width, this.height, rotation, c);
	}

	public void drawEmbedded(float x, float y, float width, float height,
			float x1, float y1, float x2, float y2, LColor c) {
		draw(x, y, width - x, height - y, x1, y1, x2, y2, c);
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2, float rotation, LColor c) {
		if (rotation == 0) {
			draw(x, y, width, height, x1, y1, x2, y2, c);
			return;
		}
		if (isBatch) {
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height, x1, y1, x2, y2, rotation);
			if (update) {
				setImageColor(LColor.white);
			}
		} else {
			GLEx.self.drawTexture(this, x, y, width, height, x1, y1, x2, y2, c,
					rotation);
		}
	}

	private boolean checkUpdateColor(LColor c) {
		return c != null && !LColor.white.equals(c);
	}

	public GLCache newBatchCache() {
		if (isBatch) {
			return batch.newGLCache();
		}
		return null;
	}

	public GLCache newBatchCache(boolean flag) {
		if (isBatch) {
			return batch.newGLCache(flag);
		}
		return null;
	}

	public void postLastBatchCache() {
		if (isBatch) {
			batch.postLastCache();
		}
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
