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
package loon;

import java.util.Arrays;

import loon.LTextureBatch.Cache;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.event.Updateable;
import loon.geom.Affine2f;
import loon.opengl.BaseBatch;
import loon.opengl.GL20;
import loon.opengl.GLPaint;
import loon.opengl.Painter;
import loon.opengl.TrilateralBatch;
import loon.opengl.TrilateralBatch.Source;
import loon.utils.CollectionUtils;
import loon.utils.GLUtils;
import loon.utils.IntMap;
import loon.utils.NumberUtils;
import loon.utils.Scale;
import loon.utils.StringUtils;
import loon.utils.TimeUtils;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.UnitPort;
import loon.utils.timer.LTimerContext;
import static loon.opengl.GL20.*;

public class LTexture extends Painter implements LRelease {

	public static LTexture createTexture(int w, int h, Format config) {
		return LTextures.createTexture(w, h, config);
	}

	public static LTexture createTexture(int w, int h) {
		return LTextures.createTexture(w, h, Format.DEFAULT);
	}

	public static LTexture createTexture(final String path) {
		return LTextures.loadTexture(path);
	}

	private boolean _disabledTexture = false;

	private boolean _drawing = false;

	private boolean _copySize = false;

	private boolean _scaleSize = false;

	private int[] _cachePixels;

	private String source;

	private Image _image;

	private int imageWidth = 1, imageHeight = 1;

	public float xOff = 0.0f;

	public float yOff = 0.0f;

	public float widthRatio = 1.0f;

	public float heightRatio = 1.0f;

	private LColor[] colors;

	private LTextureBatch batch;

	private boolean _isBatch;

	protected String tmpLazy = "tex" + TimeUtils.millis();

	protected int refCount;

	public final static class Format {

		public static Format NEAREST = new Format(true, false, false, GL_NEAREST, GL_NEAREST, false);

		public static Format LINEAR = new Format(true, false, false, GL_LINEAR, GL_LINEAR, false);

		public static Format UNMANAGED = new Format(false, false, false, GL_NEAREST, GL_LINEAR, false);

		public static Format DEFAULT = LINEAR;

		public final boolean managed;

		public final boolean repeatX, repeatY;

		public final int minFilter, magFilter;

		public final boolean mipmaps;

		public Format(boolean managed, boolean repeatX, boolean repeatY, int minFilter, int magFilter,
				boolean mipmaps) {
			this.managed = managed;
			this.repeatX = repeatX;
			this.repeatY = repeatY;
			this.minFilter = minFilter;
			this.magFilter = magFilter;
			this.mipmaps = mipmaps;
		}

		public Format repeat(boolean repeatX, boolean repeatY) {
			return new Format(managed, repeatX, repeatY, minFilter, magFilter, mipmaps);
		}

		public int toTexWidth(int sourceWidth) {
			return (repeatX || mipmaps) ? GLUtils.nextPOT(sourceWidth) : sourceWidth;
		}

		public int toTexHeight(int sourceHeight) {
			return (repeatY || mipmaps) ? GLUtils.nextPOT(sourceHeight) : sourceHeight;
		}

		@Override
		public String toString() {
			String repstr = (repeatX ? "x" : "") + (repeatY ? "y" : "");
			return "[managed=" + managed + ", repeat=" + repstr + ", filter=" + minFilter + "/" + magFilter
					+ ", mipmaps=" + mipmaps + "]";
		}
	}

	private int id;

	private Format config;

	public int getID() {
		return id;
	}

	public Format getFormat() {
		return config;
	}

	private int pixelWidth;

	private int pixelHeight;

	private Scale scale;

	private float displayWidth;

	private float displayHeight;

	private Graphics gfx;

	// _closed是删除标记，disposed是已经真的被删掉
	boolean _closed, _disposed;

	IntMap<LTexture> childs;

	LTexture parent;

	public boolean isChild() {
		return parent != null;
	}

	public LTexture getParent() {
		return parent;
	}

	public int pixelWidth() {
		return pixelWidth;
	}

	public int pixelHeight() {
		return pixelHeight;
	}

	static int _countTexture = 0;

	LTexture() {
		this._isLoaded = false;
	}

	public LTexture(Graphics gfx, int id, Format config, int pixWidth, int pixHeight, Scale scale, float dispWidth,
			float dispHeight) {
		this.gfx = gfx;
		this.id = id;
		this.config = config;
		this.pixelWidth = pixWidth;
		this.pixelHeight = pixHeight;
		this.scale = scale;
		this.displayWidth = dispWidth;
		this.displayHeight = dispHeight;
		this._isLoaded = false;
		LTextures.putTexture(this);
		_countTexture++;
	}

	public float toTexWidth() {
		return config.toTexWidth(pixelWidth);
	}

	public float toTexHeight() {
		return config.toTexHeight(pixelHeight);
	}

	public void reference() {
		if (config.managed) {
			refCount++;
		}
	}

	public void release() {
		if (config.managed) {
			if (--refCount == 0) {
				close(true);
			}
		}
	}

	public String src() {
		return getSource();
	}

	public String getSource() {
		return source;
	}

	public Image getImage() {
		if ((_image == null || _image.isClosed()) && !StringUtils.isEmpty(source)) {
			_image = BaseIO.loadImage(source);
		}
		if (_image == null && _cachePixels != null) {
			_image = Image.createImage(imageWidth, imageHeight);
			_image.setPixels(_cachePixels, imageWidth, imageHeight);
		}
		int w = getWidth();
		int h = getHeight();
		if (w != displayWidth || h != displayHeight) {
			if (_image != null) {
				Image tmp = _image.getSubImage((int) (this.xOff * this.displayWidth),
						(int) (this.yOff * this.displayHeight), w, h);
				return tmp;
			}
		}
		return _image;
	}

	public void reload() {
		if (_closed || _disposed) {
			return;
		}
		if (parent != null) {
			parent.reload();
			return;
		}
		this._closed = false;
		this._disposed = false;
		this._drawing = false;
		this._isLoaded = false;
		this._isReload = true;
		this.loadTexture();
	}

	public void loadTexture() {
		if (parent != null) {
			parent.loadTexture();
			return;
		}
		if (!_isLoaded && childs != null) {
			for (LTexture tex : childs.values()) {
				tex._isLoaded = _isLoaded;
				tex._closed = false;
				tex._disposed = false;
				tex._drawing = false;
				tex._isLoaded = false;
				tex._isReload = true;
			}
		}
		if (_image != null && !_isLoaded) {
			update(_image);
		} else if (!_isLoaded) {
			if (!StringUtils.isEmpty(source) && (source.indexOf('<') == -1 && source.indexOf('>') == -1)) {
				_image = BaseIO.loadImage(source);
			} else if (_cachePixels != null) {
				_image = Image.createCanvas(imageWidth, imageHeight).image;
				_image.setPixels(_cachePixels, imageWidth, imageHeight);
			}
			if (_image != null) {
				update(_image);
			}
		}

	}

	private int _memorySize = 0;

	public void update(final Image image) {
		update(image, true);
	}

	public void update(final Image image, final boolean closed) {
		update(image, closed, true);
	}

	public void update(final Image image, final boolean closed, final boolean updated) {
		if (image == null) {
			throw LSystem.runThrow("the image is null, can not conversion it into texture .");
		}
		if (parent != null) {
			parent.update(image, closed);
			return;
		}
		if (_drawing) {
			return;
		}
		this._drawing = true;
		this.source = image.getSource();

		if (image != null) {
			if (config.repeatX || config.repeatY || config.mipmaps) {
				int pixWidth = image.pixelWidth(), pixHeight = image.pixelHeight();
				int potWidth = config.toTexWidth(pixWidth), potHeight = config.toTexWidth(pixHeight);
				if (potWidth != pixWidth || potHeight != pixHeight) {
					Canvas scaled = gfx.createCanvasImpl(Scale.ONE, potWidth, potHeight);
					scaled.draw(image, 0, 0, potWidth, potHeight);
					scaled.image.upload(gfx, LTexture.this);
					scaled.close();
				} else {
					image.upload(gfx, LTexture.this);
				}
			} else {
				image.upload(gfx, LTexture.this);
			}
			imageWidth = image.getWidth();
			imageHeight = image.getHeight();
			if (config.mipmaps) {
				gfx.gl.glGenerateMipmap(GL_TEXTURE_2D);
			}
		}
		if (config.mipmaps) {
			_memorySize = imageWidth * imageHeight * 4 * (1 + 1 / 3);
		} else {
			_memorySize = imageWidth * imageHeight * 4;
		}
		if (closed && !_isReload) {
			if (image != null && (image.getSource() == null || image.getSource().indexOf("<canvas>") != -1)
					&& LSystem.base() != null && LSystem.base().setting.saveTexturePixels) {
				int[] pixels = image.getPixels();
				if (pixels != null) {
					_cachePixels = CollectionUtils.copyOf(pixels);
				}
			}
			if (image != null && image.toClose()) {
				image.destroy();
			}
		}
		_image = image;
		_drawing = false;

		if (updated) {
			_isLoaded = true;
			return;
		}
		if (_isReload) {
			_isReload = false;
			if (childs != null) {
				for (LTexture tex : childs.values()) {
					tex.id = id;
					tex._isLoaded = _isLoaded;
					tex._isReload = _isReload;
				}
			}
		}
	}

	public void bind() {
		GLUtils.bindTexture(LSystem.base().graphics().gl, id);
	}

	public void bind(int unit) {
		LSystem.base().graphics().gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
		GLUtils.bindTexture(LSystem.base().graphics().gl, id);
	}

	public boolean isClose() {
		if (parent != null) {
			return parent.isClose();
		}
		return _disposed || _closed;
	}

	public boolean disposed() {
		if (parent != null) {
			return parent.disposed();
		}
		return _disposed && _closed;
	}

	public UnitPort disposeAct() {
		return new UnitPort() {
			public void onEmit() {
				close();
			}
		};
	}

	@Override
	public LTexture texture() {
		return this;
	}

	@Override
	public float width() {
		float result = displayWidth * widthRatio - displayWidth * xOff;
		return result > 0 ? result : -result;
	}

	@Override
	public float height() {
		float result = displayHeight * heightRatio - displayHeight * yOff;
		return result > 0 ? result : -result;
	}

	@Override
	public float sx() {
		return xOff;
	}

	@Override
	public float sy() {
		return yOff;
	}

	@Override
	public float tx() {
		return widthRatio;
	}

	@Override
	public float ty() {
		return heightRatio;
	}

	@Override
	public void addToBatch(BaseBatch batch, int tint, Affine2f tx, float x, float y, float width, float height) {
		if (isClose()) {
			return;
		}
		batch.addQuad(this, tint, tx, x, y, width, height);
	}

	@Override
	public void addToBatch(BaseBatch batch, int tint, Affine2f tx, float dx, float dy, float dw, float dh, float sx,
			float sy, float sw, float sh) {
		if (isClose()) {
			return;
		}
		batch.addQuad(this, tint, tx, dx, dy, dw, dh, sx, sy, sw, sh);
	}

	public void closeChildAll() {
		if (childs != null) {
			for (LTexture tex2d : childs.values()) {
				if (tex2d != null) {
					tex2d.close();
					tex2d = null;
				}
			}
		}
	}

	public boolean isChildAllClose() {
		if (childs != null) {
			for (LTexture tex2d : childs.values()) {
				if (tex2d != null && !tex2d.isClose()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Texture[id=" + id + ", psize=" + pixelWidth + "x" + pixelHeight + ", dsize=" + displayWidth + "x"
				+ displayHeight + " @ " + scale + ", config=" + config + "]";
	}

	public float getDisplayWidth() {
		return displayWidth;
	}

	public float getDisplayHeight() {
		return displayHeight;
	}

	@Override
	protected void finalize() {
		if (!_disposed && !_closed) {
			gfx.queueForDispose(this);
		}
	}

	public LTexture cpy() {
		return copy();
	}

	public LTexture cpy(final float x, final float y, final float width, final float height) {
		return copy(x, y, width, height);
	}

	public LTexture copy() {
		return copy(0, 0, width(), height());
	}

	public LTexture copy(final float x, final float y, final float width, final float height) {

		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, x);
		hashCode = LSystem.unite(hashCode, y);
		hashCode = LSystem.unite(hashCode, width);
		hashCode = LSystem.unite(hashCode, height);

		if (childs == null) {
			childs = new IntMap<LTexture>(10);
		}

		synchronized (childs) {

			LTexture cache = childs.get(hashCode);

			if (cache != null) {
				return cache;
			}
			final LTexture copy = new LTexture();

			refCount++;
			copy.parent = LTexture.this;
			copy.id = id;
			copy._isLoaded = _isLoaded;
			copy.gfx = gfx;
			copy.config = config;
			copy.source = source;
			copy.scale = scale;
			copy.imageWidth = imageWidth;
			copy.imageHeight = imageHeight;
			copy._image = _image;
			copy._cachePixels = _cachePixels;
			copy._copySize = true;
			copy.pixelWidth = (int) (this.pixelWidth * this.widthRatio);
			copy.pixelHeight = (int) (this.pixelHeight * this.heightRatio);
			copy.displayWidth = this.displayWidth * this.widthRatio;
			copy.displayHeight = this.displayHeight * this.heightRatio;
			copy.xOff = (((float) x / copy.displayWidth) * this.widthRatio) + this.xOff;
			copy.yOff = (((float) y / copy.displayHeight) * this.heightRatio) + this.yOff;
			copy.widthRatio = (((float) width / copy.displayWidth) * widthRatio) + copy.xOff;
			copy.heightRatio = (((float) height / copy.displayHeight) * heightRatio) + copy.yOff;
			copy._disabledTexture = _disabledTexture;

			childs.put(hashCode, copy);
			return copy;
		}
	}

	public boolean isCopy() {
		return _copySize;
	}

	public boolean isScale() {
		return _scaleSize;
	}

	public LTexture scale(final float width, final float height) {

		int hashCode = 1;

		hashCode = LSystem.unite(hashCode, width);
		hashCode = LSystem.unite(hashCode, height);

		if (childs == null) {
			childs = new IntMap<LTexture>(10);
		}

		synchronized (childs) {

			LTexture cache = childs.get(hashCode);

			if (cache != null) {
				return cache;
			}

			final LTexture copy = new LTexture();

			refCount++;
			copy.parent = LTexture.this;
			copy.id = id;
			copy._isLoaded = _isLoaded;
			copy.gfx = gfx;
			copy.config = config;
			copy.source = source;
			copy.scale = scale;
			copy.imageWidth = imageWidth;
			copy.imageHeight = imageHeight;
			copy._image = _image;
			copy._cachePixels = _cachePixels;
			copy._copySize = true;
			copy._scaleSize = true;
			copy.pixelWidth = (int) (this.pixelWidth * this.widthRatio);
			copy.pixelHeight = (int) (this.pixelHeight * this.heightRatio);
			copy.displayWidth = this.displayWidth * this.widthRatio;
			copy.displayHeight = this.displayHeight * this.heightRatio;
			copy.xOff = this.xOff;
			copy.yOff = this.yOff;
			copy.widthRatio = (((float) width / copy.displayWidth) * widthRatio) + copy.xOff;
			copy.heightRatio = (((float) height / copy.displayHeight) * heightRatio) + copy.yOff;
			copy._disabledTexture = _disabledTexture;
			childs.put(hashCode, copy);

			return copy;
		}
	}

	public LTexture setImageColor(float r, float g, float b, float a) {
		setColor(TOP_LEFT, r, g, b, a);
		setColor(TOP_RIGHT, r, g, b, a);
		setColor(BOTTOM_LEFT, r, g, b, a);
		setColor(BOTTOM_RIGHT, r, g, b, a);
		return this;
	}

	public LTexture setImageColor(float r, float g, float b) {
		setColor(TOP_LEFT, r, g, b);
		setColor(TOP_RIGHT, r, g, b);
		setColor(BOTTOM_LEFT, r, g, b);
		setColor(BOTTOM_RIGHT, r, g, b);
		return this;
	}

	public LTexture setImageColor(LColor c) {
		if (c == null) {
			return this;
		}
		setImageColor(c.r, c.g, c.b, c.a);
		return this;
	}

	public LTexture setColor(GLPaint paint) {
		if (colors == null) {
			colors = new LColor[4];
		}
		colors[0] = paint.getTopLeftColor();
		colors[1] = paint.getTopRightColor();
		colors[2] = paint.getBottomLeftColor();
		colors[3] = paint.getBottomRightColor();
		return this;
	}

	public LTexture setColor(int corner, float r, float g, float b, float a) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
		colors[corner].a = a;
		return this;
	}

	public LTexture setColor(int corner, float r, float g, float b) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
		return this;
	}

	public boolean checkExistBatch() {
		return _isBatch && batch != null && !batch.closed();
	}

	public LTextureBatch getTextureBatch() {
		return getTextureBatch(null);
	}

	public LTextureBatch getTextureBatch(String name) {
		return getTextureBatch(name, 256);
	}

	public LTextureBatch getTextureBatch(String name, int size) {
		makeBatch(name, TrilateralBatch.DEF_SOURCE, size);
		return batch;
	}

	public LTextureBatch getTextureBatch(String name, Source source, int size) {
		makeBatch(name, source, size);
		return batch;
	}

	protected void makeBatch(String name, Source source, int size) {
		if (!checkExistBatch()) {
			batch = LTextureBatch.getBatchCache(this);
			if (batch == null || batch.closed()) {
				batch = new LTextureBatch(this, source, size);
				if (!StringUtils.isEmpty(name)) {
					batch.setTextureBatchName(name);
				}
				LTextureBatch.bindBatchCache(batch);
				_isBatch = true;
			}
		}
	}

	protected void freeBatch() {
		if (checkExistBatch()) {
			if (batch != null) {
				batch.close();
				batch = null;
				_isBatch = false;
			}
		}
	}

	public LTexture postCache(Cache cache) {
		if (isBatch()) {
			batch.postCache(cache, colors == null ? null : colors[0], 0, 0);
		}
		return this;
	}

	public boolean isBatch() {
		return (checkExistBatch() && batch.isLoaded);
	}

	public LTexture glBegin() {
		getTextureBatch();
		batch.begin();
		return this;
	}

	public LTexture glEnd() {
		if (checkExistBatch()) {
			batch.end();
		}
		return this;
	}

	public LTexture setBatchPos(float x, float y) {
		if (checkExistBatch()) {
			batch.setLocation(x, y);
		}
		return this;
	}

	public boolean isBatchLocked() {
		return checkExistBatch() && batch.isCacheLocked;
	}

	public boolean existCache() {
		return checkExistBatch() && batch.existCache();
	}

	public LTexture glCacheCommit() {
		if (checkExistBatch()) {
			batch.postLastCache();
		}
		return this;
	}

	public LTexture draw(float x, float y) {
		draw(x, y, width(), height());
		return this;
	}

	public LTexture draw(float x, float y, float width, float height) {
		if (checkExistBatch()) {
			batch.draw(colors, x, y, width, height);
		} else {
			gfx.game.display().GL().draw(this, x, y, width, height, colors == null ? null : colors[0]);
		}
		return this;
	}

	public LTexture draw(float x, float y, LColor[] c) {
		if (checkExistBatch()) {
			batch.draw(c, x, y, width(), height());
		} else {
			gfx.game.display().GL().draw(this, x, y, width(), height(), c == null ? null : c[0]);
		}
		return this;
	}

	public LTexture draw(float x, float y, LColor c) {
		if (checkExistBatch()) {
			LColor old = (colors == null ? LColor.white : colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width(), height());
			if (update) {
				setImageColor(old);
			}
		} else {
			gfx.game.display().GL().draw(this, x, y, width(), height(), c);
		}
		return this;
	}

	public LTexture draw(float x, float y, float width, float height, LColor c) {
		if (checkExistBatch()) {
			LColor old = (colors == null ? LColor.white : colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height);
			if (update) {
				setImageColor(old);
			}
		} else {
			gfx.game.display().GL().draw(this, x, y, width, height, c);
		}
		return this;
	}

	public LTexture drawFlipX(float x, float y, LColor c) {
		if (checkExistBatch()) {
			LColor old = (colors == null ? LColor.white : colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width(), height(), 0, 0, width(), height(), true, false);
			if (update) {
				setImageColor(old);
			}
		} else {
			gfx.game.display().GL().drawFlip(this, x, y, c);
		}
		return this;
	}

	public LTexture drawFlipY(float x, float y, LColor c) {
		if (checkExistBatch()) {
			LColor old = (colors == null ? LColor.white : colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width(), height(), 0, 0, width(), height(), false, true);
			if (update) {
				setImageColor(old);
			}
		} else {
			gfx.game.display().GL().drawMirror(this, x, y, c);
		}
		return this;
	}

	public LTexture draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor[] c) {
		if (checkExistBatch()) {
			batch.draw(c, x, y, width, height, x1, y1, x2, y2);
		} else {
			gfx.game.display().GL().draw(this, x, y, width, height, x1, y1, x2, y2, c == null ? null : c[0]);
		}
		return this;
	}

	public LTexture drawEmbedded(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor c) {
		return draw(x, y, width - x, height - y, x1, y1, x2, y2, c);
	}

	public LTexture draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor c) {
		if (checkExistBatch()) {
			LColor old = (colors == null ? LColor.white : colors[0]);

			final boolean update = checkUpdateColor(c);

			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height, x1, y1, x2, y2);
			if (update) {
				setImageColor(old);
			}
		} else {
			gfx.game.display().GL().draw(this, x, y, width, height, x1, y1, x2, y2, c);
		}
		return this;
	}

	public LTexture draw(float x, float y, float srcX, float srcY, float srcWidth, float srcHeight) {
		if (checkExistBatch()) {
			batch.draw(colors, x, y, srcWidth - srcX, srcHeight - srcY, srcX, srcY, srcWidth, srcHeight);
		} else {
			gfx.game.display().GL().draw(this, x, y, srcWidth - srcX, srcHeight - srcY, srcX, srcY, srcWidth, srcHeight,
					colors == null ? null : colors[0]);
		}
		return this;
	}

	public LTexture drawEmbedded(float x, float y, float width, float height, float x1, float y1, float x2, float y2) {
		return draw(x, y, width - x, height - y, x1, y1, x2, y2);
	}

	public LTexture draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2) {
		if (checkExistBatch()) {
			batch.draw(colors, x, y, width, height, x1, y1, x2, y2);
		} else {
			gfx.game.display().GL().draw(this, x, y, width, height, x1, y1, x2, y2, colors == null ? null : colors[0]);
		}
		return this;
	}

	public LTexture draw(float x, float y, float rotation) {
		return draw(x, y, this.width(), this.height(), 0, 0, this.width(), this.height(), rotation,
				colors == null ? null : colors[0]);
	}

	public LTexture draw(float x, float y, float w, float h, float rotation, LColor c) {
		return draw(x, y, w, h, 0, 0, this.width(), this.height(), rotation, c);
	}

	public LTexture draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			float rotation, LColor c) {
		if (rotation == 0) {
			draw(x, y, width, height, x1, y1, x2, y2, c);
			return this;
		}
		if (checkExistBatch()) {
			LColor old = (colors == null ? LColor.white : colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			batch.draw(colors, x, y, width, height, x1, y1, x2, y2, rotation);
			if (update) {
				setImageColor(old);
			}
		} else {
			gfx.game.display().GL().draw(this, x, y, width, height, x1, y1, x2, y2, c, rotation);
		}
		return this;
	}

	private boolean checkUpdateColor(LColor c) {
		if (c == null) {
			setColor(TOP_LEFT, 1f, 1f, 1f, 1f);
			setColor(TOP_RIGHT, 1f, 1f, 1f, 1f);
			setColor(BOTTOM_LEFT, 1f, 1f, 1f, 1f);
			setColor(BOTTOM_RIGHT, 1f, 1f, 1f, 1f);
		}
		return c != null && !LColor.white.equals(c);
	}

	public Cache saveBatchCache() {
		return newBatchCache();
	}

	public Cache newBatchCache() {
		if (checkExistBatch()) {
			return batch.newCache();
		}
		return null;
	}

	public LTexture postLastBatchCache() {
		if (checkExistBatch()) {
			batch.postLastCache();
		}
		return this;
	}

	public LTexture disposeLastCache() {
		if (checkExistBatch()) {
			batch.disposeLastCache();
		}
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof LTexture) {
			LTexture tmp = (LTexture) o;
			if (this == tmp) {
				return true;
			}
			if (source != null && !source.equals(tmp.source)) {
				return false;
			}
			if ((tmp.width() != width()) || (tmp.height() != height())) {
				return false;
			}
			if (this.id == tmp.id && this.xOff == tmp.xOff && this.yOff == tmp.yOff && this.widthRatio == tmp.widthRatio
					&& this.heightRatio == tmp.heightRatio && this.config == tmp.config && this.parent == tmp.parent
					&& this.displayWidth == tmp.displayWidth && this.displayHeight == tmp.displayHeight
					&& this.pixelWidth == tmp.pixelWidth && this.pixelHeight == tmp.pixelHeight) {
				if (_image != null && tmp._image != null) {
					return Arrays.equals(_image.getPixels(), tmp._image.getPixels());
				}
				return true;
			}
		}
		return false;
	}

	void free() {
		if (disposed()) {
			return;
		}
		if (_disabledTexture) {
			return;
		}
		if (!_isLoaded) {
			return;
		}
		final int textureId = id;
		if (!LTextures.contains(textureId)) {
			return;
		}
		if (parent != null) {
			parent.close();
			return;
		}
		synchronized (LTextures.class) {
			LTextures.removeTexture(this);
			if (batch != null) {
				LTextureBatch.disposeBatchCache(batch, false);
			}
			final Updateable update = new Updateable() {

				@Override
				public void action(Object a) {
					synchronized (LTexture.class) {
						if (LTextures.delTexture(textureId)) {
							if (LSystem._base.setting.disposeTexture && !_disposed && _closed) {
								GLUtils.deleteTexture(gfx.gl, textureId);
								_disposed = true;
							}
							if (_image != null) {
								_image.close();
								_image = null;
							}
							if (childs != null) {
								childs.clear();
								childs = null;
							}
							_cachePixels = null;
							_isLoaded = false;
							_closed = true;
							_memorySize = 0;
							freeBatch();
							LSystem.debug(
									"Texture : " + getSource() + " Closed,Size = " + getWidth() + "," + getHeight());
						}
					}
				}
			};
			if (!LTextureBatch.isRunningCache() && source.indexOf("<canvas>") == -1) {
				RealtimeProcess process = new RealtimeProcess() {

					@Override
					public void run(LTimerContext time) {
						LSystem.load(update);
						kill();
					}
				};
				process.setDelay(LSystem.SECOND);
				RealtimeProcessManager.get().addProcess(process);
			} else {
				LSystem.load(update);
			}
		}
	}

	public int getWidth() {
		return (int) width();
	}

	public int getHeight() {
		return (int) height();
	}

	@Override
	public int hashCode() {
		int result = getID();
		result = LSystem.unite(result, width() != +0.0f ? NumberUtils.floatToIntBits(width()) : 0);
		result = LSystem.unite(result, height() != +0.0f ? NumberUtils.floatToIntBits(height()) : 0);
		result = LSystem.unite(result, disposed() ? 1 : 0);
		result = LSystem.unite(result, xOff);
		result = LSystem.unite(result, yOff);
		result = LSystem.unite(result, widthRatio);
		result = LSystem.unite(result, heightRatio);
		result = LSystem.unite(result, childs == null ? 0 : childs.size);
		return result;
	}

	public float getMinU() {
		return xOff;
	}

	public float getMinV() {
		return yOff;
	}

	public float getMaxU() {
		return widthRatio;
	}

	public float getMaxV() {
		return heightRatio;
	}

	public int getMemSize() {
		return _memorySize;
	}

	/**
	 * 关闭纹理资源（默认非强制关闭）
	 */
	@Override
	public void close() {
		close(false);
	}

	/**
	 * 此值为true时,当前纹理以及其子纹理将无法释放
	 * 
	 * @param d
	 * @return
	 */
	public LTexture setDisabledTexture(boolean d) {
		_disabledTexture = d;
		return this;
	}

	public boolean isDisabledTexture() {
		return _disabledTexture;
	}

	public int getRefCount() {
		return this.refCount;
	}

	/**
	 * 布尔值为真时，将强制关闭当前纹理，无论状态
	 * 
	 * @param forcedDelete
	 */
	public void close(boolean forcedDelete) {
		if (_disabledTexture) {
			return;
		}
		if (disposed()) {
			return;
		}
		if (forcedDelete) {
			if (childs != null) {
				childs.clear();
			}
		} else if (!isChildAllClose()) {
			return;
		}
		this.refCount--;
		// forcedDelete时强制删除子纹理及父纹理，无论状态
		if (forcedDelete) {
			refCount = 0;
			if (parent != null) {
				parent.close(true);
			} else {
				_closed = true;
				_countTexture--;
				free();
			}
		} else if (refCount <= 0 && LTextures.getRefCount(getSource()) <= 0) {
			if (parent != null && parent.isChildAllClose()) {
				parent.close();
			} else {
				_closed = true;
				_countTexture--;
				free();
			}
		}
	}
}
