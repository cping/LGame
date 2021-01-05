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

import loon.LTextureBatch.Cache;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.events.Updateable;
import loon.geom.Affine2f;
import loon.geom.Clip;
import loon.geom.XYZW;
import loon.opengl.BaseBatch;
import loon.opengl.GL20;
import loon.opengl.GLEx;
import loon.opengl.GLPaint;
import loon.opengl.Painter;
import loon.opengl.ShaderSource;
import loon.utils.CollectionUtils;
import loon.utils.GLUtils;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.Scale;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TimeUtils;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.UnitPort;
import loon.utils.timer.LTimerContext;


public class LTexture extends Painter implements LRelease {

	public static LTexture createTexture(int w, int h, Format config) {
		return LSystem.createTexture(w, h, config);
	}

	public static LTexture createTexture(int w, int h) {
		return LSystem.createTexture(w, h, Format.DEFAULT);
	}

	public static LTexture createTexture(final String path) {
		return LSystem.loadTexture(path);
	}

	/**
	 * 是否无视子纹理使用情况,强制注销纹理
	 */
	private boolean _forcedDeleteTexture = false;

	private boolean _disabledTexture = false;

	private boolean _drawing = false;

	private boolean _copySize = false;

	private boolean _scaleSize = false;

	private Updateable _closeSubmit;

	private int[] _cachePixels;

	private String source;

	private Image _image;

	private int imageWidth = 1, imageHeight = 1;

	private Clip _textureClip;

	private LColor[] colors;

	private LTextureBatch batch;

	private boolean _isBatch;

	protected String tmpLazy = "tex" + TimeUtils.millis();

	protected int refCount;

	public final static class Format {

		public static Format NEAREST = new Format(true, false, false, GL20.GL_NEAREST, GL20.GL_NEAREST, false);

		public static Format LINEAR = new Format(true, false, false, GL20.GL_LINEAR, GL20.GL_LINEAR, false);

		public static Format UNMANAGED = new Format(false, false, false, GL20.GL_NEAREST, GL20.GL_LINEAR, false);

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
			StringKeyValue builder = new StringKeyValue("Managed");
			builder.kv("managed", managed).comma().kv("repeat", (repeatX ? "x" : "") + (repeatY ? "y" : "")).comma()
					.kv("filter", (minFilter + "/" + magFilter)).comma().kv("mipmaps", mipmaps);
			return builder.toString();
		}
	}

	private int _id = -1;

	private int _lazyHashCode = -1;

	private Format config;

	public int getID() {
		return _id;
	}

	public Format getFormat() {
		return config;
	}

	private int pixelWidth;

	private int pixelHeight;

	private Scale scale;

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

	public LTexture(Graphics gfx, int _id, Format config, int pixWidth, int pixHeight, Scale scale, float dispWidth,
			float dispHeight) {
		this.gfx = gfx;
		this._id = _id;
		this.config = config;
		this.pixelWidth = pixWidth;
		this.pixelHeight = pixHeight;
		this.scale = scale;
		this._textureClip = new Clip(0, 0, dispWidth, dispHeight, false);
		this._isLoaded = false;
		gfx.game.putTexture(this);
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
		return StringUtils.isEmpty(source) ? "" : source;
	}

	/**
	 * 拷贝当前纹理图像到缓冲区并转化为Image
	 * 
	 * @return
	 */
	public LTexture cpyFramebufferData() {
		Image img = _getFramebufferData();
		if (img == null) {
			return null;
		}
		return img.onHaveToClose(true).texture();
	}

	private Image _getFramebufferData() {
		GLEx glex = gfx.game.displayImpl.GL();
		boolean saved = glex.isSaveFrameBuffer() && glex.running();
		if (saved) {
			glex.end();
			glex.disableFrameBuffer();
		} else {
			glex.flush();
		}
		GL20 gl = gfx.gl;
		final int fb = gl.glGenFramebuffer();
		if (fb == 0) {
			throw new LSysException("Failed to gen framebuffer: " + gl.glGetError());
		}
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, fb);
		gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D, getID(), 0);
		boolean canRead = GLUtils.isFrameBufferCompleted(gl);
		if (!canRead) {
			return null;
		}
		Image image = GLUtils.getFrameBuffeImage(gl, 0, 0, getWidth(), getHeight(), false, true);
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);
		gl.glDeleteFramebuffer(fb);
		if (saved) {
			glex.enableFrameBuffer();
			glex.begin();
		}
		return image;
	}

	public int[] getPixels() {
		if (_cachePixels != null) {
			return CollectionUtils.copyOf(_cachePixels);
		}
		if (_image != null) {
			return _image.getPixels();
		}
		return getImage().getPixels();
	}

	public Image getImage() {
		if ((_image == null || _image.isClosed()) && !StringUtils.isEmpty(source)) {
			_image = BaseIO.loadImage(source);
		}
		if (_image == null && _cachePixels != null) {
			_image = Image.createImage(imageWidth, imageHeight);
			_image.setPixels(_cachePixels, imageWidth, imageHeight);
		} else if (_image == null && _cachePixels == null) {
			Image tmp = _getFramebufferData();
			if (tmp != null) {
				imageWidth = tmp.getWidth();
				imageHeight = tmp.getHeight();
				_cachePixels = tmp.getPixels();
				_image = tmp;
			}
		}
		int w = _image.getWidth();
		int h = _image.getHeight();
		if (0 != _textureClip.getRegionX() || 0 != _textureClip.getRegionY() || _textureClip.getRegionWidth() != w
				|| _textureClip.getRegionHeight() != h) {
			if (_image != null) {
				Image tmp = _image.getSubImage(_textureClip.getRegionX(), _textureClip.getRegionY(),
						_textureClip.getRegionWidth(), _textureClip.getRegionHeight());
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
			throw new LSysException("the image is null, can not conversion it into texture .");
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
				gfx.gl.glGenerateMipmap(GL20.GL_TEXTURE_2D);
			}
		}
		if (config.mipmaps) {
			_memorySize = imageWidth * imageHeight * 4 * (1 + 1 / 3);
		} else {
			_memorySize = imageWidth * imageHeight * 4;
		}
		if (closed && !_isReload) {
			if (image != null && (image.getSource() == null || image.getSource().indexOf("<canvas>") != -1)
					&& gfx.game != null && gfx.game.setting.saveTexturePixels) {
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
					tex._id = _id;
					tex._isLoaded = _isLoaded;
					tex._isReload = _isReload;
				}
			}
		}
	}

	public void bind() {
		GLUtils.bindTexture(gfx.gl, _id);
	}

	public void bind(int unit) {
		gfx.gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
		GLUtils.bindTexture(gfx.gl, _id);
	}

	public Clip getClip() {
		return _textureClip;
	}

	@Override
	public LTexture texture() {
		return this;
	}

	@Override
	public float width() {
		return _textureClip.getRegionWidth();
	}

	@Override
	public float height() {
		return _textureClip.getRegionHeight();
	}

	@Override
	public float sx() {
		return _textureClip.sx();
	}

	@Override
	public float sy() {
		return _textureClip.sy();
	}

	@Override
	public float tx() {
		return _textureClip.tx();
	}

	@Override
	public float ty() {
		return _textureClip.ty();
	}

	@Override
	public void addToBatch(BaseBatch batch, int tint, Affine2f tx, float x, float y, float width, float height) {
		if (isClosed()) {
			return;
		}
		batch.addQuad(this, tint, tx, x, y, width, height);
	}

	@Override
	public void addToBatch(BaseBatch batch, int tint, Affine2f tx, float dx, float dy, float dw, float dh, float sx,
			float sy, float sw, float sh) {
		if (isClosed()) {
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
				if (tex2d != null && !tex2d.isClosed()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("LTexture");
		builder.kv("_id", _id).comma().kv("pixelSize", (pixelWidth + "x" + pixelHeight)).comma()
				.kv("displaySize",
						(_textureClip.getRegionWidth() + "x" + _textureClip.getRegionHeight() + " @ " + scale))
				.comma().kv("config", config);
		return builder.toString();
	}

	@Override
	public float getDisplayWidth() {
		return _textureClip.getDisplayWidth();
	}

	@Override
	public float getDisplayHeight() {
		return _textureClip.getDisplayHeight();
	}

	public LTexture cpy() {
		return copy();
	}

	public LTexture cpy(XYZW rect) {
		return copy(rect);
	}

	public LTexture cpy(final float x, final float y, final float width, final float height) {
		return copy(x, y, width, height);
	}

	public LTexture copy() {
		return copy(0, 0, width(), height());
	}

	public LTexture copy(XYZW rect) {
		return copy(rect.getX(), rect.getY(), rect.getZ(), rect.getW());
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
			copy._id = _id;
			copy._lazyHashCode = hashCode;
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
			copy.pixelWidth = (int) _textureClip.getDisplayWidth();
			copy.pixelHeight = (int) _textureClip.getDisplayHeight();
			if (this._scaleSize) {
				copy._textureClip = new Clip(this._textureClip, x, y, width, height, true);
			} else {
				copy._textureClip = new Clip(this._textureClip, x, y, width, height, false);
			}
			copy._disabledTexture = _disabledTexture;
			copy._forcedDeleteTexture = _forcedDeleteTexture;

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
			copy._id = _id;
			copy._lazyHashCode = hashCode;
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
			copy.pixelWidth = (int) _textureClip.getDisplayWidth();
			copy.pixelHeight = (int) _textureClip.getDisplayHeight();
			if (this._scaleSize) {
				copy._textureClip = new Clip(this._textureClip, 0, 0, width, height, true);
			} else {
				copy._textureClip = new Clip(this._textureClip, 0, 0, width, height, false);
			}
			copy._disabledTexture = _disabledTexture;
			copy._forcedDeleteTexture = _forcedDeleteTexture;

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
		makeBatch(name, gfx.game.displayImpl.getShaderSource(), size);
		return batch;
	}

	public LTextureBatch getTextureBatch(String name, ShaderSource source, int size) {
		makeBatch(name, source, size);
		return batch;
	}

	protected void makeBatch(String name, ShaderSource source, int size) {
		if (!checkExistBatch()) {
			batch = gfx.game.getBatchCache(this);
			if (batch == null || batch.closed()) {
				batch = new LTextureBatch(this, source, size);
				if (!StringUtils.isEmpty(name)) {
					batch.setTextureBatchName(name);
				}
				gfx.game.bindBatchCache(batch);
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

	public LTexture begin() {
		return glBegin();
	}

	public LTexture glBegin() {
		getTextureBatch();
		batch.begin();
		return this;
	}

	public LTexture end() {
		return glEnd();
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
			gfx.game.displayImpl.GL().draw(this, x, y, width, height, colors == null ? null : colors[0]);
		}
		return this;
	}

	public LTexture draw(float x, float y, LColor[] c) {
		if (checkExistBatch()) {
			batch.draw(c, x, y, width(), height());
		} else {
			gfx.game.displayImpl.GL().draw(this, x, y, width(), height(), c == null ? null : c[0]);
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
			gfx.game.displayImpl.GL().draw(this, x, y, width(), height(), c);
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
			gfx.game.displayImpl.GL().draw(this, x, y, width, height, c);
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
			gfx.game.displayImpl.GL().drawFlip(this, x, y, c);
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
			gfx.game.displayImpl.GL().drawMirror(this, x, y, c);
		}
		return this;
	}

	public LTexture draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor[] c) {
		if (checkExistBatch()) {
			batch.draw(c, x, y, width, height, x1, y1, x2, y2);
		} else {
			gfx.game.displayImpl.GL().draw(this, x, y, width, height, x1, y1, x2, y2, c == null ? null : c[0]);
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
			gfx.game.displayImpl.GL().draw(this, x, y, width, height, x1, y1, x2, y2, c);
		}
		return this;
	}

	public LTexture draw(float x, float y, float srcX, float srcY, float srcWidth, float srcHeight) {
		if (checkExistBatch()) {
			batch.draw(colors, x, y, srcWidth - srcX, srcHeight - srcY, srcX, srcY, srcWidth, srcHeight);
		} else {
			gfx.game.displayImpl.GL().draw(this, x, y, srcWidth - srcX, srcHeight - srcY, srcX, srcY, srcWidth,
					srcHeight, colors == null ? null : colors[0]);
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
			gfx.game.displayImpl.GL().draw(this, x, y, width, height, x1, y1, x2, y2,
					colors == null ? null : colors[0]);
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
			gfx.game.displayImpl.GL().draw(this, x, y, width, height, x1, y1, x2, y2, c, rotation);
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
			if (this._id == tmp._id && this._textureClip.getRegionX() == tmp._textureClip.getRegionX()
					&& this._textureClip.getRegionY() == tmp._textureClip.getRegionY()
					&& this._textureClip.getRegionWidth() == tmp._textureClip.getRegionWidth()
					&& this._textureClip.getRegionHeight() == tmp._textureClip.getRegionHeight()
					&& this.config == tmp.config && this.parent == tmp.parent && this.pixelWidth == tmp.pixelWidth
					&& this.pixelHeight == tmp.pixelHeight) {
				if (_image != null && tmp._image != null) {
					return CollectionUtils.equals(_image.getPixels(), tmp._image.getPixels());
				}
				return true;
			}
		}
		return false;
	}

	public boolean isCloseSubmitting() {
		return _closeSubmit != null && gfx.game.processImpl.containsUnLoad(_closeSubmit);
	}

	public LTexture cancalSubmit() {
		if (isCloseSubmitting()) {
			gfx.game.processImpl.removeUnLoad(_closeSubmit);
		}
		return this;
	}

	protected void freeTexture() {
		if (disposed()) {
			return;
		}
		if (_disabledTexture) {
			return;
		}
		if (isCloseSubmitting()) {
			return;
		}
		final int textureId = _id;
		if (textureId > 0) {
			if (!gfx.game.containsTexture(textureId)) {
				return;
			}
			if (parent != null) {
				parent.close();
				return;
			}
			synchronized (LTextures.class) {
				gfx.game.removeTexture(this);
				if (batch != null) {
					gfx.game.disposeBatchCache(batch, false);
				}
				_closeSubmit = new Updateable() {

					@Override
					public void action(Object a) {
						synchronized (LTexture.class) {
							if (gfx.game.delTexture(textureId)) {
								if (gfx.game.setting.disposeTexture && !_disposed && _closed) {
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
								gfx.game.log().debug("Texture : " + getSource() + " Closed,Size = " + getWidth() + ","
										+ getHeight() + (Tag != null ? ",Tag = " + Tag : ""));
							}
						}
					}
				};
				if (!LTextureBatch.isRunningCache() && isDrawCanvas()) {
					RealtimeProcess process = new RealtimeProcess() {

						@Override
						public void run(LTimerContext time) {
							gfx.game.processImpl.addLoad(_closeSubmit);
							kill();
						}
					};
					process.setProcessType(GameProcessType.Texture);
					process.setDelay(LSystem.SECOND);
					RealtimeProcessManager.get().addProcess(process);
				} else {
					gfx.game.processImpl.addLoad(_closeSubmit);
				}
			}
		}
	}

	public float xOff() {
		return _textureClip.xOff();
	}

	public float yOff() {
		return _textureClip.yOff();
	}

	public float widthRatio() {
		return _textureClip.widthRatio();
	}

	public float heightRatio() {
		return _textureClip.heightRatio();
	}

	public int getWidth() {
		return MathUtils.ifloor(width());
	}

	public int getHeight() {
		return MathUtils.ifloor(height());
	}

	public boolean isDrawCanvas() {
		return source != null && source.indexOf("<canvas>") != -1;
	}

	@Override
	public int hashCode() {
		int result = getID();
		result = LSystem.unite(result, width() != +0.0f ? NumberUtils.floatToIntBits(width()) : 0);
		result = LSystem.unite(result, height() != +0.0f ? NumberUtils.floatToIntBits(height()) : 0);
		result = LSystem.unite(result, disposed() ? 1 : 0);
		result = LSystem.unite(result, _textureClip.getRegionX());
		result = LSystem.unite(result, _textureClip.getRegionY());
		result = LSystem.unite(result, _textureClip.getRegionWidth());
		result = LSystem.unite(result, _textureClip.getRegionHeight());
		result = LSystem.unite(result, childs == null ? 0 : childs.size);
		return result;
	}

	public float getMinU() {
		return sx();
	}

	public float getMinV() {
		return sy();
	}

	public float getMaxU() {
		return tx();
	}

	public float getMaxV() {
		return ty();
	}

	public int getMemSize() {
		return _memorySize;
	}

	/**
	 * 关闭纹理资源（默认非强制关闭）
	 */
	@Override
	public void close() {
		close(_forcedDeleteTexture);
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

	/**
	 * 是否无视子纹理使用情况强制注销当前纹理
	 * 
	 * @return
	 */
	public boolean isForcedDelete() {
		return _forcedDeleteTexture;
	}

	public LTexture setForcedDelete(boolean forcedDelete) {
		this._forcedDeleteTexture = forcedDelete;
		return this;
	}

	public int getRefCount() {
		return this.refCount;
	}

	public boolean disposed() {
		if (parent != null) {
			return parent.disposed();
		}
		return _disposed && _closed;
	}

	public boolean isClosed() {
		if (parent != null) {
			return parent.isClosed();
		}
		return _disposed || _closed;
	}

	public UnitPort disposeAct() {
		return new UnitPort() {
			@Override
			public void onEmit() {
				close();
			}
		};
	}

	@Override
	protected void finalize() {
		if (!_disposed && !_closed) {
			gfx.queueForDispose(this);
		}
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
		}
		if (isChild()) {
			IntMap<LTexture> list = parent.childs;
			if (list != null) {
				list.remove(_lazyHashCode);
			}
			parent.close();
			return;
		}
		this.refCount--;
		if (!isChildAllClose()) {
			return;
		}
		// forcedDelete时强制删除子纹理及父纹理，无论状态
		if (forcedDelete) {
			refCount = 0;
			if (parent != null) {
				parent.close(true);
			} else {
				_closed = true;
				_countTexture--;
				freeTexture();
			}
		} else if (refCount <= 0 && gfx.game.getRefTextureCount(getSource()) <= 0) {
			if (parent != null && parent.isChildAllClose()) {
				parent.close();
			} else {
				_closed = true;
				_countTexture--;
				freeTexture();
			}
		}
	}

}
