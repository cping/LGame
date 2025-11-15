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

import loon.canvas.LColor;
import loon.geom.Matrix4;
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.opengl.BlendMethod;
import loon.opengl.ExpandVertices;
import loon.opengl.GL20;
import loon.opengl.Submit;
import loon.opengl.ShaderProgram;
import loon.opengl.ShaderSource;
import loon.utils.GLUtils;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.TimeUtils;

/**
 * 这是一个针对单独纹理的批量渲染类,默认绑定在特定Texture上运行（普通纹理texture.geTexturetBatch即可获得）,<br>
 * 也就是当用户操作的纹理有仅有一个时,方便针对此纹理的特定缓存以及渲染操作.
 */
public final class LTextureBatch implements LRelease {

	private final static String _batch_name = "texbatch";

	public static class Cache implements LRelease {

		public float x = 0;

		public float y = 0;

		float[] vertices;

		int vertexIdx;

		int count;

		public Cache(LTextureBatch batch) {
			count = batch._count;
			vertexIdx = batch._vertexIdx;
			vertices = batch._expandVertices.cpy(vertexIdx);
		}

		public boolean isClosed() {
			return vertices == null;
		}

		@Override
		public void close() {
			if (vertices != null) {
				vertices = null;
			}
		}

	}

	private static boolean _runningCache = false;

	private final ShaderSource _source;

	private final ExpandVertices _expandVertices;

	protected boolean _isCacheLocked;

	protected float _invTexWidth = 0, _invTexHeight = 0;

	protected boolean _drawing = false;

	protected boolean _isLoaded;

	protected LTexture _texture;

	protected int _count = 0;

	public int maxVertsInBatch = 0;

	private String _name = _batch_name;

	private IntMap<LTextureBatch.Cache> _caches;

	private float _baseColor = LColor.white.toFloatBits();

	private final Matrix4 _combinedMatrix = new Matrix4();

	private boolean _isClosed;

	private Cache _lastCache;

	private LColor[] _colors;

	private float _xOff, _yOff, _widthRatio, _heightRatio;

	private float _drawWidth, _drawHeight;

	private float _textureSrcX, _textureSrcY;

	private float _srcWidth, _srcHeight;

	private float _renderWidth, _renderHeight;

	private Submit _mesh;

	private BlendState _lastBlendState = BlendState.NonPremultiplied;

	private int _drawCallCount;

	private LTexture _lastTexture = null;

	private ShaderProgram _shader = null;
	private ShaderProgram _customShader = null;
	private ShaderProgram _globalShader = null;

	private LColor _tempColor = new LColor(1, 1, 1, 1);

	private Matrix4 _batchMatrix;

	private int _vertexIdx;

	private int _texWidth, _texHeight;

	private float _tx, _ty;

	public LTextureBatch(LTexture tex) {
		this(tex, 256, LSystem.getShaderSource(), null);
	}

	public LTextureBatch(LTexture tex, final ShaderSource src) {
		this(tex, src, 256);
	}

	public LTextureBatch(LTexture tex, final ShaderSource src, int size) {
		this(tex, size, src, null);
	}

	public LTextureBatch(LTexture tex, final int size, final ShaderSource src, final ShaderProgram defaultShader) {
		if (size > 5460) {
			throw new LSysException("Can't have more than 5460 sprites per batch: " + size);
		}
		this.setTexture(tex);
		this._source = src;
		this._shader = defaultShader;
		this._expandVertices = ExpandVertices.getVerticeCache(size);
		this._mesh = Submit.create();
	}

	/**
	 * 使用独立的矩阵渲染纹理(这个函数是专门为Live2d增加的，因为官方API本身的矩阵限制，没法和loon已有的view做混合运算（
	 * 否则会产生奇怪的效果(因为是2D框架，不需要处理长宽高，所以我默认只用了一个2d矩阵，和live2d的矩阵相乘后会混乱的……)）)
	 * 
	 * @param val
	 */
	public LTextureBatch setBatchMatrix(float[] val) {
		if (_batchMatrix == null) {
			_batchMatrix = new Matrix4(val);
		} else {
			_batchMatrix.set(val);
		}
		return this;
	}

	public LTextureBatch setBatchMatrix(Matrix4 m) {
		if (_batchMatrix == null) {
			_batchMatrix = new Matrix4(m);
		} else {
			_batchMatrix.set(m);
		}
		return this;
	}

	public LTextureBatch setTexture(LTexture tex2d) {
		this._texture = tex2d;
		this._texWidth = _texture.getWidth();
		this._texHeight = _texture.getHeight();
		if (_texture.isCopy()) {
			_invTexWidth = (1f / _texture.width());
			_invTexHeight = (1f / _texture.height());
		} else {
			_invTexWidth = (1f / _texture.width()) * _texture.widthRatio();
			_invTexHeight = (1f / _texture.height()) * _texture.heightRatio();
		}
		return this;
	}

	public LTextureBatch setLocation(float tx, float ty) {
		this._tx = tx;
		this._ty = ty;
		return this;
	}

	public float getInvTexWidth() {
		return this._invTexWidth;
	}

	public float getInvTexHeight() {
		return this._invTexHeight;
	}

	public LTexture toTexture() {
		return _texture;
	}

	public LTextureBatch glColor4f() {
		_expandVertices.setVertice(_vertexIdx++, _baseColor);
		return this;
	}

	public LTextureBatch glColor4f(LColor baseColor) {
		_expandVertices.setVertice(_vertexIdx++, baseColor.toFloatBits());
		return this;
	}

	public LTextureBatch glColor4f(float r, float g, float b, float a) {
		_expandVertices.setVertice(_vertexIdx++, LColor.toFloatBits(r, g, b, a));
		return this;
	}

	public LTextureBatch glColor4f(float baseColor) {
		_expandVertices.setVertice(_vertexIdx++, baseColor);
		return this;
	}

	public LTextureBatch glTexCoord2f(float u, float v) {
		_expandVertices.setVertice(_vertexIdx++, u);
		_expandVertices.setVertice(_vertexIdx++, v);
		return this;
	}

	public LTextureBatch glVertex2f(Vector2f v) {
		_expandVertices.setVertice(_vertexIdx++, v.x);
		_expandVertices.setVertice(_vertexIdx++, v.y);
		return this;
	}

	public LTextureBatch glVertex2f(float x, float y) {
		_expandVertices.setVertice(_vertexIdx++, x);
		_expandVertices.setVertice(_vertexIdx++, y);
		return this;
	}

	public BlendState getBlendState() {
		return _lastBlendState;
	}

	public LTextureBatch setBlendState(BlendState state) {
		this._lastBlendState = state;
		return this;
	}

	public final static boolean isRunningCache() {
		return _runningCache;
	}

	protected ShaderProgram createShaderProgram() {
		return GLUtils.createShaderProgram(_source.vertexShader(), _source.fragmentShader());
	}

	protected ShaderProgram getShaderProgram() {
		return _shader;
	}

	public LTextureBatch begin() {
		if (!_isLoaded) {
			if (_shader == null) {
				_shader = createShaderProgram();
			}
			_isLoaded = true;
		}
		if (_drawing) {
			throw new LSysException("TextureBatch.end must be called before begin.");
		}
		LSystem.mainEndDraw();
		if (!_isCacheLocked) {
			_vertexIdx = 0;
			_lastTexture = null;
		}
		LSystem.base().graphics().gl.glDepthMask(false);
		if (_customShader != null) {
			_customShader.begin();
		} else {
			_shader.begin();
		}
		setupMatrices(LSystem.base().graphics().getViewMatrix());
		_drawing = true;
		_runningCache = true;
		_drawCallCount = 0;
		return this;
	}

	public LTextureBatch end() {
		if (!_isLoaded) {
			return this;
		}
		if (!_drawing) {
			throw new LSysException("TextureBatch.begin must be called before end.");
		}
		if (_vertexIdx > 0) {
			if (_tx != 0 || _ty != 0) {
				Matrix4 project = LSystem.base().graphics().getViewMatrix().cpy();
				project.translate(_tx, _ty, 0);
				if (_drawing) {
					setupMatrices(project);
				}
			}
			submit();
		}
		_drawing = false;
		LSystem.base().graphics().gl.glDepthMask(true);
		if (_customShader != null) {
			_customShader.end();
		} else {
			_shader.end();
		}
		LSystem.mainBeginDraw();
		_drawCallCount = 0;
		return this;
	}

	public LTextureBatch draw(float[] vertices, int offset, int length) {
		if (checkTexture(_texture)) {
			return this;
		}
		int remainingVertices = _expandVertices.length() - _vertexIdx;
		if (remainingVertices == 0) {
			submit();
			remainingVertices = _expandVertices.length();
		}
		int vertexCount = MathUtils.min(remainingVertices, length - offset);
		System.arraycopy(vertices, offset, _expandVertices.getVertices(), _vertexIdx, vertexCount);
		offset += vertexCount;
		_vertexIdx += vertexCount;
		while (offset < length) {
			submit();
			vertexCount = MathUtils.min(_expandVertices.length(), length - offset);
			System.arraycopy(vertices, offset, _expandVertices.getVertices(), 0, vertexCount);
			offset += vertexCount;
			_vertexIdx += vertexCount;
		}
		return this;
	}

	public LTextureBatch setColor(LColor tint) {
		_baseColor = tint.toFloatBits();
		return this;
	}

	public LTextureBatch setColor(float r, float g, float b, float a) {
		int intBits = (int) (255 * a) << 24 | (int) (255 * b) << 16 | (int) (255 * g) << 8 | (int) (255 * r);
		_baseColor = NumberUtils.intToFloatColor(intBits);
		return this;
	}

	public LTextureBatch setColor(float baseColor) {
		this._baseColor = baseColor;
		return this;
	}

	public LColor getColor() {
		int intBits = NumberUtils.floatToIntColor(_baseColor);
		LColor baseColor = _tempColor;
		baseColor.r = (intBits & 0xff) / 255f;
		baseColor.g = ((intBits >>> 8) & 0xff) / 255f;
		baseColor.b = ((intBits >>> 16) & 0xff) / 255f;
		baseColor.a = ((intBits >>> 24) & 0xff) / 255f;
		return baseColor;
	}

	public float getFloatColor() {
		return _baseColor;
	}

	private void checkDrawing() {
		if (!_drawing) {
			throw new LSysException("Not implemented begin !");
		}
	}

	public boolean checkTexture(final LTexture texture) {
		if (!_isLoaded || _isCacheLocked) {
			return false;
		}
		if (_isClosed) {
			return false;
		}
		if (texture == null) {
			return false;
		}
		checkDrawing();
		if (!texture.isLoaded()) {
			texture.loadTexture();
		}
		LTexture tex2d = LTexture.firstFather(texture);
		if (tex2d != null) {
			if (tex2d != _lastTexture) {
				submit();
				_lastTexture = tex2d;
			} else if (_vertexIdx == _expandVertices.length()) {
				submit();
			}
			_invTexWidth = (1f / _texWidth) * texture.widthRatio();
			_invTexHeight = (1f / _texHeight) * texture.heightRatio();
		} else if (texture != _lastTexture) {
			submit();
			_lastTexture = texture;
			_invTexWidth = (1f / _texWidth) * texture.widthRatio();
			_invTexHeight = (1f / _texHeight) * texture.heightRatio();
		} else if (_vertexIdx == _expandVertices.length()) {
			submit();
		}

		return true;
	}

	public LTextureBatch submit() {
		return submit(_lastBlendState);
	}

	public LTextureBatch submit(BlendState state) {
		if (_vertexIdx == 0) {
			return this;
		}
		if (!_isCacheLocked) {
			int vertCount = _vertexIdx / _expandVertices.vertexSize();
			if (vertCount > maxVertsInBatch) {
				maxVertsInBatch = vertCount;
			}
			this._count = vertCount * 6;
		}
		GL20 gl = LSystem.base().graphics().gl;
		GLUtils.bindTexture(gl, _texture.getID());
		int old = GLUtils.getBlendMode();
		try {
			switch (_lastBlendState) {
			case Additive:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_ALPHA_ONE);
				break;
			case AlphaBlend:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_NORMAL);
				break;
			case Opaque:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_NONE);
				break;
			case NonPremultiplied:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_SPEED);
				break;
			case Null:
				break;
			}
			_mesh.post(_name, _expandVertices.getSize(), _customShader != null ? _customShader : _shader,
					_expandVertices.getVertices(), _vertexIdx, _count);
		} catch (Throwable e) {
			LSystem.error("TextureBatch submit() exception", e);
		} finally {
			if (_expandVertices.expand(this._vertexIdx)) {
				_mesh.reset(_name, _expandVertices.length());
			}
			GLUtils.setBlendMode(gl, old);
			_drawCallCount++;
			GraphicsDrawCall.add(_drawCallCount);
		}
		return this;
	}

	public LTextureBatch setTextureBatchName(String n) {
		this._name = n;
		return this;
	}

	public String getTextureBatchName() {
		return this._name;
	}

	private LTextureBatch setupMatrices(Matrix4 view) {
		if (_batchMatrix != null) {
			_combinedMatrix.set(_batchMatrix);
		} else {
			_combinedMatrix.set(view);
		}
		if (_customShader != null) {
			_customShader.setUniformMatrix("u_projTrans", _combinedMatrix);
			_customShader.setUniformi("u_texture", 0);
			_source.setupShader(_customShader);
		} else {
			_shader.setUniformMatrix("u_projTrans", _combinedMatrix);
			_shader.setUniformi("u_texture", 0);
			_source.setupShader(_shader);
		}
		return this;
	}

	protected LTextureBatch setShader(Matrix4 view, ShaderProgram shader) {
		if (_drawing) {
			submit();
			if (_customShader != null) {
				_customShader.end();
			} else {
				this._shader.end();
			}
		}
		_customShader = shader;
		if (_drawing) {
			if (_customShader != null) {
				_customShader.begin();
			} else {
				this._shader.begin();
			}
			setupMatrices(view);
		}
		return this;
	}

	public void clearDrawCallCount() {
		this._drawCallCount = 0;
	}

	public int getDrawCallCount() {
		return _drawCallCount;
	}

	public boolean isDrawing() {
		return _drawing;
	}

	public LTextureBatch lock() {
		this._isCacheLocked = true;
		return this;
	}

	public LTextureBatch unLock() {
		this._isCacheLocked = false;
		return this;
	}

	private LTextureBatch commit(Matrix4 view, Cache cache, LColor baseColor, BlendState state) {
		if (!_isLoaded) {
			return this;
		}
		if (_drawing) {
			end();
		}
		LSystem.mainEndDraw();
		if (baseColor == null) {
			if (_shader == null) {
				_shader = createShaderProgram();
			}
			_globalShader = _shader;
		} else if (_globalShader == null) {
			_globalShader = GLUtils.createShaderProgram(LSystem.getGLExVertexShader(),
					LSystem.getColorFragmentShader());
		}
		_globalShader.begin();
		float oldColor = getFloatColor();
		if (baseColor != null) {
			_globalShader.setUniformf("v_color", baseColor.r, baseColor.g, baseColor.b, baseColor.a);
		}
		if (_batchMatrix != null) {
			_combinedMatrix.set(_batchMatrix);
		} else {
			_combinedMatrix.set(view);
		}
		if (_globalShader != null) {
			_globalShader.setUniformMatrix("u_projTrans", _combinedMatrix);
			_globalShader.setUniformi("u_texture", 0);
		}
		if (cache.vertexIdx > 0) {
			GL20 gl = LSystem.base().graphics().gl;
			GLUtils.bindTexture(gl, _texture.getID());
			int old = GLUtils.getBlendMode();
			switch (_lastBlendState) {
			case Additive:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_ALPHA_ONE);
				break;
			case AlphaBlend:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_NORMAL);
				break;
			case Opaque:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_NONE);
				break;
			case NonPremultiplied:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_SPEED);
				break;
			case Null:
				break;
			}
			_mesh.post(_name, _expandVertices.getSize(), _globalShader, cache.vertices, cache.vertexIdx, cache.count);
			GLUtils.setBlendMode(gl, old);
		} else if (baseColor != null) {
			_globalShader.setUniformf("v_color", oldColor);
		}
		_globalShader.end();
		LSystem.mainBeginDraw();
		_runningCache = true;
		GraphicsDrawCall.add(1);
		return this;
	}

	public LTextureBatch setIndices(short[] indices) {
		_mesh.getMesh(_name, _expandVertices.getSize()).setIndices(indices);
		return this;
	}

	public LTextureBatch resetIndices() {
		_mesh.resetIndices(_name, _expandVertices.getSize());
		return this;
	}

	public LTextureBatch setGLType(int type) {
		_mesh.setGLType(type);
		return this;
	}

	public boolean postLastCache() {
		if (_lastCache != null) {
			commit(LSystem.base().graphics().getViewMatrix(), _lastCache, null, _lastBlendState);
			return true;
		}
		return false;
	}

	public Cache getLastCache() {
		return _lastCache;
	}

	public boolean existCache() {
		return _lastCache != null && _lastCache.count > 0;
	}

	public Cache newCache() {
		if (_isLoaded) {
			return (_lastCache = new Cache(this));
		} else {
			return null;
		}
	}

	public boolean disposeLastCache() {
		if (_lastCache != null) {
			_lastCache.close();
			_lastCache = null;
			return true;
		}
		return false;
	}

	public LTextureBatch draw(float x, float y) {
		return draw(_colors, x, y, _texture.width(), _texture.height(), 0, 0, _texture.width(), _texture.height());
	}

	public LTextureBatch draw(float x, float y, float width, float height) {
		return draw(_colors, x, y, width, height, 0, 0, _texture.width(), _texture.height());
	}

	public LTextureBatch draw(float x, float y, float width, float height, float srcX, float srcY, float srcWidth,
			float srcHeight) {
		return draw(_colors, x, y, width, height, srcX, srcY, srcWidth, srcHeight);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height) {
		return draw(colors, x, y, width, height, 0, 0, _texture.width(), _texture.height());
	}

	/**
	 * 以指定的色彩，顶点绘制出指定区域内的纹理到指定位置
	 * 
	 * @param colors
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 */
	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight) {

		if (!checkTexture(_texture)) {
			return this;
		}

		_xOff = srcX * _invTexWidth + _texture.xOff();
		_yOff = srcY * _invTexHeight + _texture.yOff();
		_widthRatio = srcWidth * _invTexWidth;
		_heightRatio = srcHeight * _invTexHeight;

		final float fx2 = x + width;
		final float fy2 = y + height;

		if (colors == null) {
			glVertex2f(x, y);
			glColor4f();
			glTexCoord2f(_xOff, _yOff);

			glVertex2f(x, fy2);
			glColor4f();
			glTexCoord2f(_xOff, _heightRatio);

			glVertex2f(fx2, fy2);
			glColor4f();
			glTexCoord2f(_widthRatio, _heightRatio);

			glVertex2f(fx2, y);
			glColor4f();
			glTexCoord2f(_widthRatio, _yOff);

		} else {
			glVertex2f(x, y);
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(_xOff, _yOff);

			glVertex2f(x, fy2);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(_xOff, _heightRatio);

			glVertex2f(fx2, fy2);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(_widthRatio, _heightRatio);

			glVertex2f(fx2, y);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(_widthRatio, _yOff);

		}
		return this;
	}

	public LTextureBatch drawQuad(float drawX, float drawY, float c1, float drawX2, float drawY2, float c2, float srcX,
			float srcY, float c3, float srcX2, float srcY2, float c4) {

		if (!checkTexture(_texture)) {
			return this;
		}

		_drawWidth = drawX2 - drawX;
		_drawHeight = drawY2 - drawY;
		_textureSrcX = ((srcX / _texWidth) * _texture.widthRatio()) + _texture.xOff();
		_textureSrcY = ((srcY / _texHeight) * _texture.heightRatio()) + _texture.yOff();
		_srcWidth = srcX2 - srcX;
		_srcHeight = srcY2 - srcY;
		_renderWidth = ((_srcWidth / _texWidth) * _texture.widthRatio());
		_renderHeight = ((_srcHeight / _texHeight) * _texture.heightRatio());

		glVertex2f(drawX, drawY);
		glColor4f(c1);
		glTexCoord2f(_textureSrcX, _textureSrcY);

		glVertex2f(drawX, drawY + _drawHeight);
		glColor4f(c2);
		glTexCoord2f(_textureSrcX, _textureSrcY + _renderHeight);

		glVertex2f(drawX + _drawWidth, drawY + _drawHeight);
		glColor4f(c3);
		glTexCoord2f(_textureSrcX + _renderWidth, _textureSrcY + _renderHeight);

		glVertex2f(drawX + _drawWidth, drawY);
		glColor4f(c4);
		glTexCoord2f(_textureSrcX + _renderWidth, _textureSrcY);
		return this;
	}

	public LTextureBatch drawQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		return drawQuad(x1, y1, _baseColor, x2, y2, _baseColor, x3, y3, _baseColor, x4, y4, _baseColor);
	}

	public LTextureBatch draw(float x, float y, LColor[] c) {
		draw(c, x, y, _texture.width(), _texture.height());
		return this;
	}

	public LTextureBatch draw(float x, float y, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(_colors, x, y, _texture.width(), _texture.height());
		if (update) {
			setImageColor(LColor.white);
		}
		return this;
	}

	public LTextureBatch draw(float x, float y, float width, float height, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(_colors, x, y, width, height);
		if (update) {
			setImageColor(LColor.white);
		}
		return this;
	}

	public LTextureBatch draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor[] c) {
		draw(c, x, y, width, height, x1, y1, x2, y2);
		return this;
	}

	public LTextureBatch draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(_colors, x, y, width, height, x1, y1, x2, y2);
		if (update) {
			setImageColor(LColor.white);
		}
		return this;
	}

	public LTextureBatch draw(float x, float y, float w, float h, float rotation, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(_colors, x, y, w, h, rotation);
		if (update) {
			setImageColor(LColor.white);
		}
		return this;
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float rotation) {
		return draw(colors, x, y, _texture.width() / 2, _texture.height() / 2, _texture.width(), _texture.height(), 1f,
				1f, rotation, 0, 0, _texture.width(), _texture.height(), false, false);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float rotation) {
		return draw(colors, x, y, _texture.width() / 2, _texture.height() / 2, width, height, 1f, 1f, rotation, 0, 0,
				_texture.width(), _texture.height(), false, false);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float srcX, float srcY, float srcWidth,
			float srcHeight, float rotation) {
		return draw(colors, x, y, _texture.width() / 2, _texture.height() / 2, _texture.width(), _texture.height(), 1f,
				1f, rotation, srcX, srcY, srcWidth, srcHeight, false, false);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, float rotation) {
		return draw(colors, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, srcX, srcY, srcWidth,
				srcHeight, false, false);
	}

	public LTextureBatch draw(float x, float y, float width, float height, float srcX, float srcY, float srcWidth,
			float srcHeight, float rotation, boolean flipX, boolean flipY) {
		return draw(_colors, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, float rotation, boolean flipX, boolean flipY) {
		return draw(colors, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY);
	}

	public LTextureBatch draw(float x, float y, float width, float height, float scaleX, float scaleY, float rotation,
			float srcX, float srcY, float srcWidth, float srcHeight, boolean flipX, boolean flipY) {
		return draw(_colors, x, y, width / 2, height / 2, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY);
	}

	public LTextureBatch draw(float x, float y, float originX, float originY, float width, float height, float scaleX,
			float scaleY, float rotation, float srcX, float srcY, float srcWidth, float srcHeight, boolean flipX,
			boolean flipY) {
		return draw(_colors, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float originX, float originY, float width,
			float height, float scaleX, float scaleY, float rotation, float srcX, float srcY, float srcWidth,
			float srcHeight, boolean flipX, boolean flipY) {

		if (!checkTexture(_texture)) {
			return this;
		}
		final float worldOriginX = x + originX;
		final float worldOriginY = y + originY;
		float fx = -originX;
		float fy = -originY;
		float fx2 = width - originX;
		float fy2 = height - originY;

		if (scaleX != 1 || scaleY != 1) {
			fx *= scaleX;
			fy *= scaleY;
			fx2 *= scaleX;
			fy2 *= scaleY;
		}

		final float p1x = fx;
		final float p1y = fy;
		final float p2x = fx;
		final float p2y = fy2;
		final float p3x = fx2;
		final float p3y = fy2;
		final float p4x = fx2;
		final float p4y = fy;

		float x1;
		float y1;
		float x2;
		float y2;
		float x3;
		float y3;
		float x4;
		float y4;

		if (rotation != 0) {
			final float cos = MathUtils.cosDeg(rotation);
			final float sin = MathUtils.sinDeg(rotation);

			x1 = cos * p1x - sin * p1y;
			y1 = sin * p1x + cos * p1y;

			x2 = cos * p2x - sin * p2y;
			y2 = sin * p2x + cos * p2y;

			x3 = cos * p3x - sin * p3y;
			y3 = sin * p3x + cos * p3y;

			x4 = x1 + (x3 - x2);
			y4 = y3 - (y2 - y1);
		} else {
			x1 = p1x;
			y1 = p1y;

			x2 = p2x;
			y2 = p2y;

			x3 = p3x;
			y3 = p3y;

			x4 = p4x;
			y4 = p4y;
		}

		x1 += worldOriginX;
		y1 += worldOriginY;
		x2 += worldOriginX;
		y2 += worldOriginY;
		x3 += worldOriginX;
		y3 += worldOriginY;
		x4 += worldOriginX;
		y4 += worldOriginY;

		_xOff = srcX * _invTexWidth + _texture.xOff();
		_yOff = srcY * _invTexHeight + _texture.yOff();
		_widthRatio = srcWidth * _invTexWidth;
		_heightRatio = srcHeight * _invTexHeight;

		if (flipX) {
			float tmp = _xOff;
			_xOff = _widthRatio;
			_widthRatio = tmp;
		}

		if (flipY) {
			float tmp = _yOff;
			_yOff = _heightRatio;
			_heightRatio = tmp;
		}

		if (colors == null) {
			glVertex2f(x1, y1);
			glColor4f();
			glTexCoord2f(_xOff, _yOff);

			glVertex2f(x2, y2);
			glColor4f();
			glTexCoord2f(_xOff, _heightRatio);

			glVertex2f(x3, y3);
			glColor4f();
			glTexCoord2f(_widthRatio, _heightRatio);

			glVertex2f(x4, y4);
			glColor4f();
			glTexCoord2f(_widthRatio, _yOff);

		} else {
			glVertex2f(x1, y1);
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(_xOff, _yOff);

			glVertex2f(x2, y2);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(_xOff, _heightRatio);

			glVertex2f(x3, y3);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(_widthRatio, _heightRatio);

			glVertex2f(x4, y4);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(_widthRatio, _yOff);
		}
		return this;
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, boolean flipX, boolean flipY) {

		if (!checkTexture(_texture)) {
			return this;
		}
		_xOff = srcX * _invTexWidth + _texture.xOff();
		_yOff = srcY * _invTexHeight + _texture.yOff();
		_widthRatio = srcWidth * _invTexWidth;
		_heightRatio = srcHeight * _invTexHeight;

		final float fx2 = x + width;
		final float fy2 = y + height;

		if (flipX) {
			float tmp = _xOff;
			_xOff = _widthRatio;
			_widthRatio = tmp;
		}

		if (flipY) {
			float tmp = _yOff;
			_yOff = _heightRatio;
			_heightRatio = tmp;
		}

		if (colors == null) {
			glVertex2f(x, y);
			glColor4f();
			glTexCoord2f(_xOff, _yOff);

			glVertex2f(x, fy2);
			glColor4f();
			glTexCoord2f(_xOff, _heightRatio);

			glVertex2f(fx2, fy2);
			glColor4f();
			glTexCoord2f(_widthRatio, _heightRatio);

			glVertex2f(fx2, y);
			glColor4f();
			glTexCoord2f(_widthRatio, _yOff);
		} else {
			glVertex2f(x, y);
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(_xOff, _yOff);

			glVertex2f(x, fy2);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(_xOff, _heightRatio);

			glVertex2f(fx2, fy2);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(_widthRatio, _heightRatio);

			glVertex2f(fx2, y);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(_widthRatio, _yOff);
		}
		return this;
	}

	public LTextureBatch setImageColor(float r, float g, float b, float a) {
		setColor(LTexture.TOP_LEFT, r, g, b, a);
		setColor(LTexture.TOP_RIGHT, r, g, b, a);
		setColor(LTexture.BOTTOM_LEFT, r, g, b, a);
		setColor(LTexture.BOTTOM_RIGHT, r, g, b, a);
		return this;
	}

	public LTextureBatch setImageColor(float r, float g, float b) {
		setColor(LTexture.TOP_LEFT, r, g, b);
		setColor(LTexture.TOP_RIGHT, r, g, b);
		setColor(LTexture.BOTTOM_LEFT, r, g, b);
		setColor(LTexture.BOTTOM_RIGHT, r, g, b);
		return this;
	}

	public LTextureBatch setImageColor(LColor c) {
		if (c == null) {
			return this;
		}
		setImageColor(c.r, c.g, c.b, c.a);
		return this;
	}

	public LTextureBatch draw(short[] indexArray, float[] vertexArray, float[] uvArray, float x, float y, float sx,
			float sy, LColor baseColor) {
		int length = vertexArray.length;
		if (indexArray.length < 1024) {
			short[] indices = new short[1024];
			for (int i = 0; i < indexArray.length; i++) {
				indices[i] = indexArray[i];
			}
			for (int i = 0; i < indexArray.length; i++) {
				indices[i + indexArray.length] = indexArray[i];
			}
			setIndices(indices);
		} else if (indexArray.length < 2048) {
			short[] indices = new short[2048];
			for (int i = 0; i < indexArray.length; i++) {
				indices[i] = indexArray[i];
			}
			for (int i = 0; i < indexArray.length; i++) {
				indices[i + indexArray.length] = indexArray[i];
			}
			setIndices(indices);
		} else if (indexArray.length < 4096) {
			short[] indices = new short[4096];
			for (int i = 0; i < indexArray.length; i++) {
				indices[i] = indexArray[i];
			}
			for (int i = 0; i < indexArray.length; i++) {
				indices[i + indexArray.length] = indexArray[i];
			}
			setIndices(indices);
		}
		for (int q = 0; q < 4; q++) {
			for (int idx = 0; idx < length; idx += 2) {
				glVertex2f(vertexArray[idx] * sx + x, vertexArray[idx + 1] * sy + y);
				glColor4f(baseColor.r, baseColor.g, baseColor.b, baseColor.a);
				glTexCoord2f(uvArray[idx], uvArray[idx + 1]);
			}
		}
		return this;
	}

	public LTextureBatch setColor(int corner, float r, float g, float b, float a) {
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

	public LTextureBatch setColor(int corner, float r, float g, float b) {
		if (_colors == null) {
			_colors = new LColor[] { new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		_colors[corner].r = r;
		_colors[corner].g = g;
		_colors[corner].b = b;
		return this;
	}

	private boolean checkUpdateColor(LColor c) {
		return c != null && !LColor.white.equals(c);
	}

	public LTextureBatch commit(float x, float y, float sx, float sy, float ax, float ay, float rotaion) {
		if (_isClosed) {
			return this;
		}
		Matrix4 project = LSystem.base().graphics().getViewMatrix();
		boolean update = (x != 0 || y != 0 || rotaion != 0 || sx != 1f || sy != 1f);
		if (update) {
			project = project.cpy();
		}
		if (x != 0 || y != 0) {
			project.translate(x, y, 0);
		}
		if (sx != 1f || sy != 1f) {
			project.scale(sx, sy, 0);
		}
		if (rotaion != 0) {
			if (ax != 0 || ay != 0) {
				project.translate(ax, ay, 0.0f);
				project.rotate(0f, 0f, 1f, rotaion);
				project.translate(-ax, -ay, 0.0f);
			} else {
				project.translate(_texture.width() / 2, _texture.height() / 2, 0.0f);
				project.rotate(0f, 0f, 0f, rotaion);
				project.translate(-_texture.width() / 2, -_texture.height() / 2, 0.0f);
			}
		}
		if (_drawing) {
			setupMatrices(project);
		}
		end();
		_runningCache = true;
		return this;
	}

	public LTextureBatch postCache(Cache cache, LColor baseColor, float x, float y) {
		if (_isClosed) {
			return this;
		}
		x += cache.x;
		y += cache.y;
		Matrix4 project = LSystem.base().graphics().getViewMatrix();
		if (x != 0 || y != 0) {
			project = project.cpy();
			project.translate(x, y, 0);
		}
		commit(project, cache, baseColor, _lastBlendState);
		return this;
	}

	public LTextureBatch postCache(Cache cache, LColor baseColor, float x, float y, float sx, float sy, float ax,
			float ay, float rotaion) {
		if (_isClosed) {
			return this;
		}
		x += cache.x;
		y += cache.y;
		Matrix4 project = LSystem.base().graphics().getViewMatrix();
		boolean update = (x != 0 || y != 0 || rotaion != 0 || sx != 1f || sy != 1f);
		if (update) {
			project = project.cpy();
		}
		if (x != 0 || y != 0) {
			project.translate(x, y, 0);
		}
		if (sx != 1f || sy != 1f) {
			project.scale(sx, sy, 0);
		}
		if (rotaion != 0) {
			if (ax != 0 || ay != 0) {
				project.translate(ax, ay, 0.0f);
				project.rotate(0f, 0f, 1f, rotaion);
				project.translate(-ax, -ay, 0.0f);
			} else {
				project.translate(_texture.width() / 2, _texture.height() / 2, 0.0f);
				project.rotate(0f, 0f, 0f, rotaion);
				project.translate(-_texture.width() / 2, -_texture.height() / 2, 0.0f);
			}
		}
		commit(project, cache, baseColor, _lastBlendState);
		return this;
	}

	public LTextureBatch postCache(Cache cache, LColor baseColor, float rotaion) {
		if (_isClosed) {
			return this;
		}
		float x = cache.x;
		float y = cache.y;
		Matrix4 project = LSystem.base().graphics().getViewMatrix();
		if (rotaion != 0) {
			project = project.cpy();
			project.translate((_texture.width() / 2) + x, (y + _texture.height() / 2) + y, 0.0f);
			project.rotate(0f, 0f, 1f, rotaion);
			project.translate((-_texture.width() / 2) + y, (-_texture.height() / 2) + y, 0.0f);
		}
		commit(project, cache, baseColor, _lastBlendState);
		return this;
	}

	public LTextureBatch postCache(LColor baseColor, float rotaion) {
		if (_lastCache != null) {
			postCache(_lastCache, baseColor, rotaion);
		}
		return this;
	}

	public int saveCache() {
		return saveCache((int) (TimeUtils.millis() + (_caches == null ? 1 : _caches.size)));
	}

	public int saveCache(int hashCodeValue) {
		if (_caches == null) {
			_caches = new IntMap<LTextureBatch.Cache>();
		}
		LTextureBatch.Cache cache = newCache();
		if (cache != null) {
			_caches.put(hashCodeValue, cache);
		}
		return hashCodeValue;
	}

	public LTextureBatch.Cache restoreCachePost(int hashCodeValue) {
		return restoreCachePost(hashCodeValue, _colors == null ? LColor.white : _colors[0], 0f, 0f);
	}

	public LTextureBatch.Cache restoreCachePost(int hashCodeValue, LColor baseColor, float x, float y) {
		LTextureBatch.Cache cache = restoreCache(hashCodeValue);
		if (cache != null) {
			postCache(cache, baseColor, x, y);
		}
		return cache;
	}

	public LTextureBatch.Cache restoreCache(int hashCodeValue) {
		if (_caches != null) {
			return _caches.get(hashCodeValue);
		}
		return null;
	}

	public int getTextureID() {
		if (_texture != null) {
			return _texture.getID();
		}
		return -1;
	}

	public int getTextureHashCode() {
		if (_texture != null) {
			return _texture.hashCode();
		}
		return -1;
	}

	public boolean closed() {
		return _isClosed;
	}

	public boolean isClosed() {
		return closed();
	}

	@Override
	public void close() {
		_isClosed = true;
		_isLoaded = false;
		_isCacheLocked = false;
		if (_shader != null) {
			_shader.close();
		}
		if (_globalShader != null) {
			_globalShader.close();
		}
		if (_customShader != null) {
			_customShader.close();
		}
		if (_caches != null) {
			for (LTextureBatch.Cache cache : _caches) {
				if (cache != null) {
					cache.close();
				}
			}
			_caches.clear();
		}
		if (_lastCache != null) {
			_lastCache.close();
		}
		if (!_batch_name.equals(_name)) {
			_mesh.dispose(_name, _expandVertices.getSize());
		}
		LSystem.disposeBatchCache(this, false);
		_runningCache = false;
	}

	public LTextureBatch destroy() {
		if (_texture != null) {
			_texture.close(true);
		}
		return this;
	}
}
