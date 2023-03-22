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
package loon.opengl;

import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.geom.Matrix4;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.LSystem;

public class TrilateralBatch extends BaseBatch {

	private final static String BATCHNAME = "trilbatch";

	private final Matrix4 _viewMatrix;

	private final ExpandVertices _expandVertices;

	private int _blendMode = -1;

	private float _ubufWidth = 0;

	private float _ubufHeight = 0;

	private boolean _uflip = true;

	private boolean _loaded = false, _locked = false;

	private int _maxSpritesInBatch = 0;

	private int _indexCount = 0;

	private ShaderProgram _batchShader;

	private Submit _submit;

	public TrilateralBatch(GL20 gl) {
		this(gl, LSystem.DEF_SOURCE);
	}

	public TrilateralBatch(GL20 gl, ShaderSource src) {
		this(gl, 512, src);
	}

	public TrilateralBatch(GL20 gl, int maxSize, ShaderSource src) {
		super(gl);
		this._expandVertices = new ExpandVertices(maxSize);
		this._shader_source = src;
		this._viewMatrix = new Matrix4();
		this.init();
	}

	@Override
	public void init() {
		this._submit = new Submit();
		this._blendMode = -1;
		this._alpha = 255;
	}

	private int _alpha;

	protected final static float addX(float m00, float m01, float m10, float m11, float x, float y, float tx) {
		return m00 * x + m10 * y + tx;
	}

	protected final static float addY(float m00, float m01, float m10, float m11, float x, float y, float ty) {
		return m01 * x + m11 * y + ty;
	}

	@Override
	public void addQuad(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float x1, float y1,
			float sx1, float sy1, float x2, float y2, float sx2, float sy2, float x3, float y3, float sx3, float sy3,
			float x4, float y4, float sx4, float sy4) {

		if (_locked) {
			return;
		}
		final float nx1 = addX(m00, m01, m10, m11, x1, y1, tx);
		final float ny1 = addY(m00, m01, m10, m11, x1, y1, ty);
		final float nx2 = addX(m00, m01, m10, m11, x2, y2, tx);
		final float ny2 = addY(m00, m01, m10, m11, x2, y2, ty);
		final float nx3 = addX(m00, m01, m10, m11, x3, y3, tx);
		final float ny3 = addY(m00, m01, m10, m11, x3, y3, ty);
		final float nx4 = addX(m00, m01, m10, m11, x4, y4, tx);
		final float ny4 = addY(m00, m01, m10, m11, x4, y4, ty);

		final int r = (tint & 0x00FF0000) >> 16;
		final int g = (tint & 0x0000FF00) >> 8;
		final int b = (tint & 0x000000FF);
		_alpha = (tint & 0xFF000000) >> 24;
		if (_alpha < 0) {
			_alpha += 256;
		}

		final int color = (_alpha << 24) | (b << 16) | (g << 8) | r;

		final float colorFloat = NumberUtils.intBitsToFloat(color & 0xfeffffff);

		int index = this._indexCount;

		_expandVertices.setVertice(index++, nx1);
		_expandVertices.setVertice(index++, ny1);
		_expandVertices.setVertice(index++, colorFloat);
		_expandVertices.setVertice(index++, sx1);
		_expandVertices.setVertice(index++, sy1);

		_expandVertices.setVertice(index++, nx2);
		_expandVertices.setVertice(index++, ny2);
		_expandVertices.setVertice(index++, colorFloat);
		_expandVertices.setVertice(index++, sx2);
		_expandVertices.setVertice(index++, sy2);

		_expandVertices.setVertice(index++, nx4);
		_expandVertices.setVertice(index++, ny4);
		_expandVertices.setVertice(index++, colorFloat);
		_expandVertices.setVertice(index++, sx4);
		_expandVertices.setVertice(index++, sy4);

		_expandVertices.setVertice(index++, nx3);
		_expandVertices.setVertice(index++, ny3);
		_expandVertices.setVertice(index++, colorFloat);
		_expandVertices.setVertice(index++, sx3);
		_expandVertices.setVertice(index++, sy3);

		this._indexCount = index;

		if (lastTexId != curTexId) {
			flush();
		}
	}

	@Override
	public void quad(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float x1, float y1,
			float x2, float y2, float x3, float y3, float x4, float y4, float u, float v, float u2, float v2) {

		final int r = (tint & 0x00FF0000) >> 16;
		final int g = (tint & 0x0000FF00) >> 8;
		final int b = (tint & 0x000000FF);
		_alpha = (tint & 0xFF000000) >> 24;
		if (_alpha < 0) {
			_alpha += 256;
		}

		final int color = (_alpha << 24) | (b << 16) | (g << 8) | r;

		final float colorFloat = NumberUtils.intBitsToFloat(color & 0xfeffffff);

		quad(m00, m01, m10, m11, tx, ty, x1, y1, colorFloat, x2, y2, colorFloat, x3, y3, colorFloat, x4, y4, colorFloat,
				u, v, u2, v2);
	}

	@Override
	public void quad(float m00, float m01, float m10, float m11, float tx, float ty, float x1, float y1, float c1,
			float x2, float y2, float c2, float x3, float y3, float c3, float x4, float y4, float c4, float u, float v,
			float u2, float v2) {

		final float nx1 = addX(m00, m01, m10, m11, x1, y1, tx);
		final float ny1 = addY(m00, m01, m10, m11, x1, y1, ty);
		final float nx2 = addX(m00, m01, m10, m11, x2, y2, tx);
		final float ny2 = addY(m00, m01, m10, m11, x2, y2, ty);
		final float nx3 = addX(m00, m01, m10, m11, x3, y3, tx);
		final float ny3 = addY(m00, m01, m10, m11, x3, y3, ty);
		final float nx4 = addX(m00, m01, m10, m11, x4, y4, tx);
		final float ny4 = addY(m00, m01, m10, m11, x4, y4, ty);

		int index = this._indexCount;

		_expandVertices.setVertice(index++, nx1);
		_expandVertices.setVertice(index++, ny1);
		_expandVertices.setVertice(index++, c1);
		_expandVertices.setVertice(index++, u);
		_expandVertices.setVertice(index++, v);

		_expandVertices.setVertice(index++, nx2);
		_expandVertices.setVertice(index++, ny2);
		_expandVertices.setVertice(index++, c2);
		_expandVertices.setVertice(index++, u);
		_expandVertices.setVertice(index++, v2);

		_expandVertices.setVertice(index++, nx3);
		_expandVertices.setVertice(index++, ny3);
		_expandVertices.setVertice(index++, c3);
		_expandVertices.setVertice(index++, u2);
		_expandVertices.setVertice(index++, v2);

		_expandVertices.setVertice(index++, nx4);
		_expandVertices.setVertice(index++, ny4);
		_expandVertices.setVertice(index++, c4);
		_expandVertices.setVertice(index++, u2);
		_expandVertices.setVertice(index++, v);

		this._indexCount = index;

		if (lastTexId != curTexId) {
			flush();
		}

	}

	@Override
	public void begin(float fbufWidth, float fbufHeight, boolean flip) {
		if (this._ubufWidth != fbufWidth || this._ubufHeight != fbufHeight || this._uflip != flip) {
			this._ubufWidth = fbufWidth;
			this._ubufHeight = fbufHeight;
			this._viewMatrix.setToOrtho2D(0, 0, _ubufWidth, _ubufHeight);
			this._uflip = flip;
			if (!flip) {
				Affine2f a2f = new Affine2f();
				float w = _ubufWidth / 2;
				float h = _ubufHeight / 2;
				a2f.translate(w, h);
				a2f.scale(-1, 1);
				a2f.translate(-w, -h);
				a2f.translate(w, h);
				a2f.rotate(MathUtils.PI);
				a2f.translate(-w, -h);
				this._viewMatrix.mul(a2f);
			}
		}
		final boolean dirty = isShaderDirty();
		if (!_loaded || dirty) {
			if (_batchShader == null || dirty) {
				if (_batchShader != null) {
					_batchShader.close();
					_batchShader = null;
				}
				_batchShader = LSystem.createShader(_shader_source.vertexShader(), _shader_source.fragmentShader());
				setShaderDirty(false);
			}
			_loaded = true;
		}
		_batchShader.begin();
		setupMatrices();
	}

	@Override
	public void flush() {
		super.flush();
		if (_indexCount > 0) {
			submit();
		}
		if (_batchShader != null) {
			_batchShader.end();
		}
	}

	protected int vertexSize() {
		return _expandVertices.vertexSize();
	}

	@Override
	public void end() {
		super.end();
		this._blendMode = -1;
		this._alpha = 255;
	}

	public int getSize() {
		return _expandVertices.getSize();
	}

	public TrilateralBatch setShaderUniformf(String name, LColor color) {
		if (_batchShader != null) {
			_batchShader.setUniformf(name, color);
		}
		return this;
	}

	public TrilateralBatch setShaderUniformf(int name, LColor color) {
		if (_batchShader != null) {
			_batchShader.setUniformf(name, color);
		}
		return this;
	}

	public boolean isLockSubmit() {
		return _locked;
	}

	public TrilateralBatch setLockSubmit(boolean locked) {
		this._locked = locked;
		return this;
	}

	public void submit() {
		if (_indexCount == 0) {
			return;
		}
		try {
			int spritesInBatch = _indexCount / 20;
			if (spritesInBatch > _maxSpritesInBatch) {
				_maxSpritesInBatch = spritesInBatch;
			}
			int count = spritesInBatch * 6;
			bindTexture();
			GL20 gl = LSystem.base().graphics().gl;
			int blend = GLUtils.getBlendMode();
			if (_blendMode == -1) {
				if (_alpha >= 240) {
					GLUtils.setBlendMode(gl, BlendMethod.MODE_NORMAL);
				} else {
					GLUtils.setBlendMode(gl, BlendMethod.MODE_SPEED);
				}
			} else {
				GLUtils.setBlendMode(gl, _blendMode);
			}
			_submit.post(BATCHNAME, _expandVertices.getSize(), _batchShader, _expandVertices.getVertices(), _indexCount,
					count);
			GLUtils.setBlendMode(gl, blend);
		} catch (Throwable ex) {
			LSystem.error("Batch submit() error", ex);
		} finally {
			if (_expandVertices.expand(this._indexCount)) {
				_submit.reset(BATCHNAME, _expandVertices.length());
			}
			if (!_locked) {
				_indexCount = 0;
			}
		}
	}

	private void setupMatrices() {
		if (_batchShader != null) {
			_batchShader.setUniformMatrix("u_projTrans", _viewMatrix);
			_batchShader.setUniformi("u_texture", 0);
			_shader_source.setupShader(_batchShader);
		}
	}

	@Override
	public BaseBatch setBlendMode(int b) {
		this._blendMode = b;
		return this;
	}

	@Override
	public int getBlendMode() {
		return _blendMode;
	}

	@Override
	public void close() {
		super.close();
		if (_batchShader != null) {
			_batchShader.close();
		}
		this._blendMode = -1;
	}

	@Override
	public String toString() {
		return "tris/" + _expandVertices.length();
	}

}
