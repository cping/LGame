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
import loon.canvas.Pixmap;
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

public final class LTexture extends Painter implements LRelease {

	public static LTexture createTexture(int w, int h, Format config) {
		return LSystem.createTexture(w, h, config);
	}

	public static LTexture createTexture(int w, int h) {
		return LSystem.createTexture(w, h, Format.DEFAULT);
	}

	public static LTexture createTexture(final String path) {
		return LSystem.loadTexture(path);
	}

	private static class TextureClosedUpdate implements Updateable {

		private Graphics _gfx;

		private LTexture _texture;

		private int _textureId;

		public TextureClosedUpdate(int id, Graphics gfx, LTexture tex) {
			this._textureId = id;
			this._gfx = gfx;
			this._texture = tex;
		}

		@Override
		public void action(Object a) {
			synchronized (LTexture.class) {
				if (_gfx.game.delTexture(_textureId)) {
					if (_gfx.game.setting.disposeTexture && !_texture._disposed && _texture._closed) {
						GLUtils.deleteTexture(_gfx.gl, _textureId);
						_texture._disposed = true;
					}
					if (_texture._image != null && !_texture._disabledImage) {
						_texture._image.close();
						_texture._image = null;
					}
					if (_texture._childs != null) {
						_texture._childs.clear();
						_texture._childs = null;
					}
					_texture._cachePixels = null;
					_texture._isLoaded = false;
					_texture._closed = true;
					_texture._memorySize = 0;
					_texture.freeBatch();
					_gfx.game.log()
							.debug("Texture : " + _texture.getSource() + " Closed,Size = " + _texture.getWidth() + ","
									+ _texture.getHeight()
									+ (_texture.Tag != null ? ",Tag = " + _texture.Tag : LSystem.EMPTY));
				}
			}
		}
	}

	private static class PostTextureDelete extends RealtimeProcess {

		private Updateable _closed;

		private Graphics _gfx;

		public PostTextureDelete(Graphics gfx, Updateable closed) {
			super("TextureDeleted", LSystem.SECOND, GameProcessType.Texture);
			this._gfx = gfx;
			this._closed = closed;
		}

		@Override
		public void run(LTimerContext time) {
			_gfx.game.processImpl.addLoad(_closed);
			kill();
		}

	}

	/**
	 * 是否无视子纹理使用情况,强制注销纹理
	 */
	private boolean _forcedDeleteTexture = false;

	private boolean _disabledTexture = false;

	private boolean _disabledImage = false;

	private boolean _drawing = false;

	private boolean _copySize = false;

	private boolean _scaleSize = false;

	private Updateable _closeSubmit;

	private int[] _cachePixels;

	private String _source;

	private Image _image;

	private int _memorySize = 0;

	private int _imageWidth = 1, _imageHeight = 1;

	private Clip _textureClip;

	private LColor[] _colors;

	private LTextureBatch _textureBatch;

	private boolean _isBatch;

	protected String tmpLazy = "tex" + TimeUtils.millis();

	protected int _referenceCount;

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
			builder.kv("managed", managed).comma()
					.kv("repeat", (repeatX ? "x" : LSystem.EMPTY) + (repeatY ? "y" : LSystem.EMPTY)).comma()
					.kv("filter", (minFilter + "/" + magFilter)).comma().kv("mipmaps", mipmaps);
			return builder.toString();
		}
	}

	private int _id = -1;

	private int _lazyHashCode = -1;

	private Format _config;

	public int getID() {
		return _id;
	}

	public Format getFormat() {
		return _config;
	}

	private int _pixelWidth;

	private int _pixelHeight;

	private Scale _scale;

	private Graphics _gfx;

	// _closed是删除标记，disposed是已经真的被删掉
	boolean _closed, _disposed;

	IntMap<LTexture> _childs;

	LTexture _parent;

	public boolean isChild() {
		return _parent != null;
	}

	public LTexture getParent() {
		return _parent;
	}

	public int pixelWidth() {
		return _pixelWidth;
	}

	public int pixelHeight() {
		return _pixelHeight;
	}

	static int _countTexture = 0;

	LTexture() {
		this._isLoaded = false;
	}

	public LTexture(Graphics gfx, int id, Format config, int pixWidth, int pixHeight, Scale scale, float dispWidth,
			float dispHeight) {
		this._gfx = gfx;
		this._id = id;
		this._config = config;
		this._pixelWidth = pixWidth;
		this._pixelHeight = pixHeight;
		this._scale = scale;
		this._textureClip = new Clip(0, 0, dispWidth, dispHeight, false);
		this._isLoaded = false;
		gfx.game.putTexture(this);
		_countTexture++;
	}

	public float toTexWidth() {
		return _config.toTexWidth(_pixelWidth);
	}

	public float toTexHeight() {
		return _config.toTexHeight(_pixelHeight);
	}

	public void reference() {
		if (_config.managed) {
			_referenceCount++;
		}
	}

	public void release() {
		if (_config.managed) {
			if (--_referenceCount == 0) {
				close(true);
			}
		}
	}

	public String src() {
		return getSource();
	}

	public String getSource() {
		return StringUtils.isEmpty(_source) ? LSystem.EMPTY : _source;
	}

	public boolean isDrawCanvas() {
		return _source != null && _source.indexOf(RenderCanvas) != -1;
	}

	public boolean isImageCanvas() {
		return StringUtils.isEmpty(_source) || (_source.indexOf('<') != -1 && _source.indexOf('>') != -1);
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
		GLEx glex = _gfx.game.displayImpl.GL();
		boolean saved = glex.isSaveFrameBuffer() && glex.running();
		if (saved) {
			glex.end();
			glex.disableFrameBuffer();
		} else {
			glex.flush();
		}
		GL20 gl = _gfx.gl;
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

	public int getPixel(int x, int y) {
		if (_closed || _disposed) {
			return -1;
		}
		if (x < 0 || x >= getDisplayWidth() || y < 0 || y >= getDisplayHeight()) {
			return -1;
		} else {
			int[] pixels = null;
			if (_cachePixels != null) {
				pixels = _cachePixels;
			} else {
				pixels = getPixels();
			}
			if (pixels != null) {
				return pixels[MathUtils.ifloor(y * getDisplayWidth() + x)];
			}
			return -1;
		}
	}

	public int[] getPixels() {
		if (_closed || _disposed) {
			return null;
		}
		if (_cachePixels != null) {
			return CollectionUtils.copyOf(_cachePixels);
		}
		if (_image != null) {
			return _image.getPixels();
		}
		Image img = getImage();
		if (img != null) {
			return img.getPixels();
		} else {
			throw new LSysException("Unable to get pixels for this texture !");
		}
	}

	public Image getImage() {
		if (_closed || _disposed) {
			return null;
		}
		if ((_image == null || _image.isClosed()) && !isImageCanvas()) {
			_image = BaseIO.loadImage(_source);
		}
		if (_image == null && _cachePixels != null) {
			_image = Image.createImage(_imageWidth, _imageHeight);
			_image.setPixels(_cachePixels, _imageWidth, _imageHeight);
		} else if (_image == null && _cachePixels == null) {
			Image tmp = _getFramebufferData();
			if (tmp != null) {
				_imageWidth = tmp.getWidth();
				_imageHeight = tmp.getHeight();
				_cachePixels = tmp.getPixels();
				_image = tmp;
			}
		}
		if (_image != null) {
			int w = _image.getWidth();
			int h = _image.getHeight();
			if (0 != _textureClip.getRegionX() || 0 != _textureClip.getRegionY() || _textureClip.getRegionWidth() != w
					|| _textureClip.getRegionHeight() != h) {
				if (_image != null) {
					Image tmp = null;
					if (isScale()) {
						tmp = Image.getResize(_image, _textureClip.getRegionX(), _textureClip.getRegionY(),
								_textureClip.getRegionWidth(), _textureClip.getRegionHeight());
					} else {
						tmp = _image.getSubImage(_textureClip.getRegionX(), _textureClip.getRegionY(),
								_textureClip.getRegionWidth(), _textureClip.getRegionHeight());
					}
					return tmp;
				}
			}
		}
		return _image;
	}

	public void reload() {
		if (_closed || _disposed) {
			return;
		}
		if (_parent != null) {
			_parent.reload();
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
		if (_parent != null) {
			_parent.loadTexture();
			return;
		}
		if (!_isLoaded && _childs != null) {
			for (LTexture tex : _childs.values()) {
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
			if (!isImageCanvas()) {
				_image = BaseIO.loadImage(_source);
			} else if (_cachePixels != null) {
				_image = Image.createCanvas(_imageWidth, _imageHeight).image;
				_image.setPixels(_cachePixels, _imageWidth, _imageHeight);
			}
			if (_image != null) {
				update(_image);
			}
		}

	}

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
		if (_parent != null) {
			_parent.update(image, closed);
			return;
		}
		if (_drawing) {
			return;
		}
		this._drawing = true;
		this._source = image.getSource();
		if (image != null) {
			if (_config.repeatX || _config.repeatY || _config.mipmaps) {
				int pixWidth = image.pixelWidth(), pixHeight = image.pixelHeight();
				int potWidth = _config.toTexWidth(pixWidth), potHeight = _config.toTexWidth(pixHeight);
				if (potWidth != pixWidth || potHeight != pixHeight) {
					Canvas scaled = _gfx.createCanvasImpl(Scale.ONE, potWidth, potHeight);
					scaled.draw(image, 0, 0, potWidth, potHeight);
					scaled.image.upload(_gfx, LTexture.this);
					scaled.close();
				} else {
					image.upload(_gfx, LTexture.this);
				}
			} else {
				image.upload(_gfx, LTexture.this);
			}
			_imageWidth = image.getWidth();
			_imageHeight = image.getHeight();
			if (_config.mipmaps) {
				_gfx.gl.glGenerateMipmap(GL20.GL_TEXTURE_2D);
			}
		}
		if (_config.mipmaps) {
			_memorySize = MathUtils.abs(_imageWidth * _imageHeight * 4 * (1 + 1 / 3));
		} else {
			_memorySize = MathUtils.abs(_imageWidth * _imageHeight * 4);
		}
		if (closed && !_isReload) {
			if (image != null && _gfx.game != null && _gfx.game.setting.saveTexturePixels) {
				int[] pixels = image.getPixels();
				if (pixels != null) {
					_cachePixels = pixels;
				}
			}
			if (image != null && image.toClose()) {
				image.destroy();
			}
		}
		if (closed) {
			_image = null;
		} else {
			_image = image;
		}
		_drawing = false;
		if (updated) {
			_isLoaded = true;
			return;
		}
		if (_isReload) {
			_isReload = false;
			if (_childs != null) {
				for (LTexture tex : _childs.values()) {
					tex._id = _id;
					tex._isLoaded = _isLoaded;
					tex._isReload = _isReload;
				}
			}
		}
	}

	public void bind() {
		GLUtils.bindTexture(_gfx.gl, _id);
	}

	public void unbind() {
		GLUtils.bindTexture(_gfx.gl, 0);
	}

	public void bind(int unit) {
		_gfx.gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
		GLUtils.bindTexture(_gfx.gl, _id);
	}

	public void unbind(int unit) {
		_gfx.gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
		GLUtils.bindTexture(_gfx.gl, 0);
	}

	public void blit(final Pixmap pix) {
		this.blit(pix, 0, 0);
	}

	public void blit(final Pixmap pix, final int x, final int y) {
		if (pix == null) {
			return;
		}
		blit(pix, x, y, pix.getWidth(), pix.getHeight());
	}

	public void blit(final Pixmap pix, final int x, final int y, final int w, final int h) {
		if (pix == null) {
			return;
		}
		final LGame game = _gfx.game;
		if (game == null || game.displayImpl == null) {
			return;
		}
		final GLEx gl = game.displayImpl.GL();
		if (gl == null) {
			return;
		}
		LSystem.load(new Updateable() {

			@Override
			public void action(Object a) {
				final BaseBatch batch = gl.batch();
				batch.setTexture(LTexture.this);
				bind();
				Pixmap newPix = pix;
				if (w != pix.getWidth() || h != pix.getHeight()) {
					newPix = Pixmap.getResize(pix, w, h);
				}
				_gfx.gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, x, y, newPix.getWidth(), newPix.getHeight(),
						GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, newPix.convertPixmapToByteBuffer());
			}
		});

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

	@Override
	public void quad(BaseBatch batch, int tint, Affine2f tx, float x1, float y1, float x2, float y2, float x3, float y3,
			float x4, float y4) {
		if (isClosed()) {
			return;
		}
		batch.quad(this, tint, tx, x1, y1, x2, y2, x3, y3, x4, y4);
	}

	public void closeChildAll() {
		if (_childs != null) {
			for (LTexture tex2d : _childs.values()) {
				if (tex2d != null) {
					tex2d.close();
					tex2d = null;
				}
			}
		}
	}

	public boolean isChildAllClose() {
		if (_childs != null) {
			for (LTexture tex2d : _childs.values()) {
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
		builder.kv("_id", _id).comma().kv("pixelSize", (_pixelWidth + "x" + _pixelHeight)).comma()
				.kv("displaySize",
						(_textureClip.getRegionWidth() + "x" + _textureClip.getRegionHeight() + " @ " + _scale))
				.comma().kv("config", _config);
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

	public LTexture sub() {
		return copy();
	}

	public LTexture sub(XYZW rect) {
		return copy(rect);
	}

	public LTexture sub(final float x, final float y, final float width, final float height) {
		return copy(x, y, width, height);
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

		if (_childs == null) {
			_childs = new IntMap<LTexture>(10);
		}

		synchronized (_childs) {

			LTexture cache = _childs.get(hashCode);

			if (cache != null) {
				return cache;
			}
			final LTexture copy = new LTexture();

			_referenceCount++;
			copy._parent = LTexture.this;
			copy._id = _id;
			copy._lazyHashCode = hashCode;
			copy._isLoaded = _isLoaded;
			copy._gfx = _gfx;
			copy._config = _config;
			copy._source = _source;
			copy._scale = _scale;
			copy._imageWidth = _imageWidth;
			copy._imageHeight = _imageHeight;
			copy._image = _image;
			copy._cachePixels = _cachePixels;
			copy._copySize = true;
			copy._pixelWidth = MathUtils.iceil(_textureClip.getDisplayWidth());
			copy._pixelHeight = MathUtils.iceil(_textureClip.getDisplayHeight());
			if (copy._scaleSize) {
				copy._textureClip = new Clip(this._textureClip, x, y, width, height, true);
			} else {
				copy._textureClip = new Clip(this._textureClip, x, y, width, height, false);
			}
			copy._disabledTexture = _disabledTexture;
			copy._disabledImage = _disabledImage;
			copy._forcedDeleteTexture = _forcedDeleteTexture;

			_childs.put(hashCode, copy);
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
		if (MathUtils.equal(getWidth(), width) && MathUtils.equal(getHeight(), height)) {
			return this;
		}

		int hashCode = 1;

		hashCode = LSystem.unite(hashCode, width);
		hashCode = LSystem.unite(hashCode, height);

		if (_childs == null) {
			_childs = new IntMap<LTexture>(10);
		}

		synchronized (_childs) {

			LTexture cache = _childs.get(hashCode);

			if (cache != null) {
				return cache;
			}

			final LTexture copy = new LTexture();

			_referenceCount++;
			copy._parent = LTexture.this;
			copy._id = _id;
			copy._lazyHashCode = hashCode;
			copy._isLoaded = _isLoaded;
			copy._gfx = _gfx;
			copy._config = _config;
			copy._source = _source;
			copy._scale = _scale;
			copy._imageWidth = _imageWidth;
			copy._imageHeight = _imageHeight;
			copy._image = _image;
			copy._cachePixels = _cachePixels;
			copy._copySize = true;
			copy._scaleSize = true;
			copy._pixelWidth = MathUtils.iceil(_textureClip.getDisplayWidth());
			copy._pixelHeight = MathUtils.iceil(_textureClip.getDisplayHeight());
			if (copy._scaleSize) {
				copy._textureClip = new Clip(this._textureClip, 0, 0, width, height, true);
			} else {
				copy._textureClip = new Clip(this._textureClip, 0, 0, width, height, false);
			}
			copy._disabledTexture = _disabledTexture;
			copy._disabledImage = _disabledImage;
			copy._forcedDeleteTexture = _forcedDeleteTexture;

			_childs.put(hashCode, copy);
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
		if (_colors == null) {
			_colors = new LColor[4];
		}
		_colors[0] = paint.getTopLeftColor();
		_colors[1] = paint.getTopRightColor();
		_colors[2] = paint.getBottomLeftColor();
		_colors[3] = paint.getBottomRightColor();
		return this;
	}

	public LTexture setColor(int corner, float r, float g, float b, float a) {
		if (_colors == null) {
			_colors = new LColor[] { new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		_colors[corner].r = r;
		_colors[corner].g = g;
		_colors[corner].b = b;
		_colors[corner].a = a;
		return this;
	}

	public LTexture setColor(int corner, float r, float g, float b) {
		if (_colors == null) {
			_colors = new LColor[] { new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		_colors[corner].r = r;
		_colors[corner].g = g;
		_colors[corner].b = b;
		return this;
	}

	public boolean checkExistBatch() {
		return _isBatch && _textureBatch != null && !_textureBatch.closed();
	}

	public LTextureBatch getTextureBatch() {
		return getTextureBatch(null);
	}

	public LTextureBatch getTextureBatch(String name) {
		return getTextureBatch(name, 256);
	}

	public LTextureBatch getTextureBatch(String name, int size) {
		makeBatch(name, _gfx.game.displayImpl.getShaderSource(), size);
		return _textureBatch;
	}

	public LTextureBatch getTextureBatch(String name, ShaderSource source, int size) {
		makeBatch(name, source, size);
		return _textureBatch;
	}

	protected void makeBatch(String name, ShaderSource source, int size) {
		if (!checkExistBatch()) {
			_textureBatch = _gfx.game.getBatchCache(this);
			if (_textureBatch == null || _textureBatch.closed()) {
				_textureBatch = new LTextureBatch(this, source, size);
				if (!StringUtils.isEmpty(name)) {
					_textureBatch.setTextureBatchName(name);
				}
				_gfx.game.bindBatchCache(_textureBatch);
				_isBatch = true;
			}
		}
	}

	protected void freeBatch() {
		if (checkExistBatch()) {
			if (_textureBatch != null) {
				_textureBatch.close();
				_textureBatch = null;
				_isBatch = false;
			}
		}
	}

	public LTexture postCache(Cache cache) {
		if (isBatch()) {
			_textureBatch.postCache(cache, _colors == null ? null : _colors[0], 0, 0);
		}
		return this;
	}

	public boolean isBatch() {
		return (checkExistBatch() && _textureBatch.isLoaded);
	}

	public LTexture begin() {
		return glBegin();
	}

	public LTexture glBegin() {
		getTextureBatch();
		_textureBatch.begin();
		return this;
	}

	public LTexture end() {
		return glEnd();
	}

	public LTexture glEnd() {
		if (checkExistBatch()) {
			_textureBatch.end();
		}
		return this;
	}

	public LTexture setBatchPos(float x, float y) {
		if (checkExistBatch()) {
			_textureBatch.setLocation(x, y);
		}
		return this;
	}

	public boolean isBatchLocked() {
		return checkExistBatch() && _textureBatch.isCacheLocked;
	}

	public boolean existCache() {
		return checkExistBatch() && _textureBatch.existCache();
	}

	public LTexture glCacheCommit() {
		if (checkExistBatch()) {
			_textureBatch.postLastCache();
		}
		return this;
	}

	public LTexture draw(float x, float y) {
		draw(x, y, width(), height());
		return this;
	}

	public LTexture draw(float x, float y, float width, float height) {
		if (checkExistBatch()) {
			_textureBatch.draw(_colors, x, y, width, height);
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, width, height, _colors == null ? null : _colors[0]);
		}
		return this;
	}

	public LTexture draw(float x, float y, LColor[] c) {
		if (checkExistBatch()) {
			_textureBatch.draw(c, x, y, width(), height());
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, width(), height(), c == null ? null : c[0]);
		}
		return this;
	}

	public LTexture draw(float x, float y, LColor c) {
		if (checkExistBatch()) {
			LColor old = (_colors == null ? LColor.white : _colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			_textureBatch.draw(_colors, x, y, width(), height());
			if (update) {
				setImageColor(old);
			}
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, width(), height(), c);
		}
		return this;
	}

	public LTexture draw(float x, float y, float width, float height, LColor c) {
		if (checkExistBatch()) {
			LColor old = (_colors == null ? LColor.white : _colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			_textureBatch.draw(_colors, x, y, width, height);
			if (update) {
				setImageColor(old);
			}
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, width, height, c);
		}
		return this;
	}

	public LTexture drawFlipX(float x, float y, LColor c) {
		if (checkExistBatch()) {
			LColor old = (_colors == null ? LColor.white : _colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			_textureBatch.draw(_colors, x, y, width(), height(), 0, 0, width(), height(), true, false);
			if (update) {
				setImageColor(old);
			}
		} else {
			_gfx.game.displayImpl.GL().drawFlip(this, x, y, c);
		}
		return this;
	}

	public LTexture drawFlipY(float x, float y, LColor c) {
		if (checkExistBatch()) {
			LColor old = (_colors == null ? LColor.white : _colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			_textureBatch.draw(_colors, x, y, width(), height(), 0, 0, width(), height(), false, true);
			if (update) {
				setImageColor(old);
			}
		} else {
			_gfx.game.displayImpl.GL().drawMirror(this, x, y, c);
		}
		return this;
	}

	public LTexture draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor[] c) {
		if (checkExistBatch()) {
			_textureBatch.draw(c, x, y, width, height, x1, y1, x2, y2);
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, width, height, x1, y1, x2, y2, c == null ? null : c[0]);
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
			LColor old = (_colors == null ? LColor.white : _colors[0]);

			final boolean update = checkUpdateColor(c);

			if (update) {
				setImageColor(c);
			}
			_textureBatch.draw(_colors, x, y, width, height, x1, y1, x2, y2);
			if (update) {
				setImageColor(old);
			}
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, width, height, x1, y1, x2, y2, c);
		}
		return this;
	}

	public LTexture draw(float x, float y, float srcX, float srcY, float srcWidth, float srcHeight) {
		if (checkExistBatch()) {
			_textureBatch.draw(_colors, x, y, srcWidth - srcX, srcHeight - srcY, srcX, srcY, srcWidth, srcHeight);
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, srcWidth - srcX, srcHeight - srcY, srcX, srcY, srcWidth,
					srcHeight, _colors == null ? null : _colors[0]);
		}
		return this;
	}

	public LTexture drawEmbedded(float x, float y, float width, float height, float x1, float y1, float x2, float y2) {
		return draw(x, y, width - x, height - y, x1, y1, x2, y2);
	}

	public LTexture draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2) {
		if (checkExistBatch()) {
			_textureBatch.draw(_colors, x, y, width, height, x1, y1, x2, y2);
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, width, height, x1, y1, x2, y2,
					_colors == null ? null : _colors[0]);
		}
		return this;
	}

	public LTexture draw(float x, float y, float rotation) {
		return draw(x, y, this.width(), this.height(), 0, 0, this.width(), this.height(), rotation,
				_colors == null ? null : _colors[0]);
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
			LColor old = (_colors == null ? LColor.white : _colors[0]);
			final boolean update = checkUpdateColor(c);
			if (update) {
				setImageColor(c);
			}
			_textureBatch.draw(_colors, x, y, width, height, x1, y1, x2, y2, rotation);
			if (update) {
				setImageColor(old);
			}
		} else {
			_gfx.game.displayImpl.GL().draw(this, x, y, width, height, x1, y1, x2, y2, c, rotation);
		}
		return this;
	}

	protected void bindFilter(int param) {
		if (this._id < -1) {
			final GL20 gl = _gfx.gl;
			int old = GLUtils.getCurrentHardwareTextureID();
			GLUtils.bindTexture(gl, _id);
			gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, param);
			gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, param);
			GLUtils.bindTexture(gl, old);
		}
	}

	public void minmapLinearFilter() {
		if (this._id < -1) {
			final GL20 gl = _gfx.gl;
			int old = GLUtils.getCurrentHardwareTextureID();
			GLUtils.bindTexture(gl, _id);
			gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
			if (_config.mipmaps) {
				_gfx.gl.glGenerateMipmap(GL20.GL_TEXTURE_2D);
			}
			gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR_MIPMAP_LINEAR);
			GLUtils.bindTexture(gl, old);
		}
	}

	public void nearestFilter() {
		bindFilter(GL20.GL_NEAREST);
	}

	public void linearFilter() {
		bindFilter(GL20.GL_LINEAR);
	}

	protected void bindParameter(int param) {
		if (this._id < -1) {
			final GL20 gl = _gfx.gl;
			int old = GLUtils.getCurrentHardwareTextureID();
			GLUtils.bindTexture(gl, _id);
			gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, param);
			gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, param);
			GLUtils.bindTexture(gl, old);
		}
	}

	public void clampToEdge() {
		bindParameter(GL20.GL_CLAMP_TO_EDGE);
	}

	public void repeat() {
		bindParameter(GL20.GL_REPEAT);
	}

	public void mirrorRepeat() {
		bindParameter(GL20.GL_MIRRORED_REPEAT);
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
			return _textureBatch.newCache();
		}
		return null;
	}

	public LTexture postLastBatchCache() {
		if (checkExistBatch()) {
			_textureBatch.postLastCache();
		}
		return this;
	}

	public LTexture disposeLastCache() {
		if (checkExistBatch()) {
			_textureBatch.disposeLastCache();
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
			if (_source != null && !_source.equals(tmp._source)) {
				return false;
			}
			if ((tmp.width() != width()) || (tmp.height() != height())) {
				return false;
			}
			if (this._id == tmp._id && this._textureClip.getRegionX() == tmp._textureClip.getRegionX()
					&& this._textureClip.getRegionY() == tmp._textureClip.getRegionY()
					&& this._textureClip.getRegionWidth() == tmp._textureClip.getRegionWidth()
					&& this._textureClip.getRegionHeight() == tmp._textureClip.getRegionHeight()
					&& this._config == tmp._config && this._parent == tmp._parent && this._pixelWidth == tmp._pixelWidth
					&& this._pixelHeight == tmp._pixelHeight) {
				if (_image != null && tmp._image != null) {
					return CollectionUtils.equals(_image.getPixels(), tmp._image.getPixels());
				}
				return true;
			}
		}
		return false;
	}

	public boolean isCloseSubmitting() {
		return _closeSubmit != null && _gfx.game.processImpl.containsUnLoad(_closeSubmit);
	}

	public LTexture cancalSubmit() {
		if (isCloseSubmitting()) {
			_gfx.game.processImpl.removeUnLoad(_closeSubmit);
		}
		return this;
	}

	protected void freeTexture() {
		if (_disabledTexture || disposed() || isCloseSubmitting()) {
			return;
		}
		final int textureId = _id;
		if (textureId > 0) {
			if (!_gfx.game.containsTexture(textureId)) {
				return;
			}
			if (_parent != null) {
				_parent.close();
				return;
			}
			synchronized (LTextures.class) {
				_gfx.game.removeTexture(this);
				if (_textureBatch != null) {
					_gfx.game.disposeBatchCache(_textureBatch, false);
				}
				_closeSubmit = new TextureClosedUpdate(textureId, _gfx, this);
				if (!LTextureBatch.isRunningCache() && isDrawCanvas()) {
					RealtimeProcessManager.get().addProcess(new PostTextureDelete(_gfx, _closeSubmit));
				} else {
					_gfx.game.processImpl.addLoad(_closeSubmit);
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
		result = LSystem.unite(result, _childs == null ? 0 : _childs.size);
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
	 * 此值为true时,构建当前纹理的Image对象将无法释放
	 * 
	 * @param d
	 * @return
	 */
	public LTexture setDisabledImage(boolean d) {
		_disabledImage = d;
		return this;
	}

	public boolean isDisabledImage() {
		return _disabledImage;
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
		return this._referenceCount;
	}

	public boolean disposed() {
		if (_parent != null) {
			return _parent.disposed();
		}
		return _disposed && _closed;
	}

	public boolean isClosed() {
		if (_parent != null) {
			return _parent.isClosed();
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

	/*
	 * @Override protected void finalize() { if (!_disposed && !_closed) {
	 * gfx.queueForDispose(this); } }
	 */

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
			if (_childs != null) {
				_childs.clear();
			}
		}
		if (isChild()) {
			final IntMap<LTexture> list = _parent._childs;
			if (list != null) {
				list.remove(_lazyHashCode);
			}
			_parent.close();
			return;
		}
		this._referenceCount--;
		if (!isChildAllClose()) {
			return;
		}
		// forcedDelete时强制删除子纹理及父纹理，无论状态
		if (forcedDelete) {
			_referenceCount = 0;
			if (_parent != null) {
				_parent.close(true);
			} else {
				_closed = true;
				_countTexture--;
				freeTexture();
			}
		} else if (_referenceCount <= 0 && _gfx.game.getRefTextureCount(getSource()) <= 0) {
			if (_parent != null && _parent.isChildAllClose()) {
				_parent.close();
			} else {
				_closed = true;
				_countTexture--;
				freeTexture();
			}
		}
	}

}
