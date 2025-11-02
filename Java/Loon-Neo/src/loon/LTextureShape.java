/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import java.nio.Buffer;
import java.nio.ShortBuffer;

import loon.canvas.LColor;
import loon.geom.Matrix4;
import loon.geom.RectF;
import loon.opengl.BlendMethod;
import loon.opengl.BlendState;
import loon.opengl.GL20;
import loon.opengl.Mesh;
import loon.opengl.ShaderProgram;
import loon.opengl.ShaderSource;
import loon.opengl.VertexAttribute;
import loon.opengl.Mesh.VertexDataType;
import loon.opengl.VertexAttributes.Usage;
import loon.opengl.VertexStream.FillOrigin;
import loon.opengl.VertexStream.FillStyle;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.opengl.VertexStream;

/**
 * 纹理显示用类,将指定纹理图片转化为指定不规则形状并显示
 */
public class LTextureShape implements LRelease {

	private final static short[] GRID_TILE_INDICE = new short[] { -1, 0, -1, 2, 4, 3, -1, 1, -1 };

	private final static short[] TRIANGLES_9_GRID = new short[] { 4, 0, 1, 1, 5, 4, 5, 1, 2, 2, 6, 5, 6, 2, 3, 3, 7, 6,
			8, 4, 5, 5, 9, 8, 9, 5, 6, 6, 10, 9, 10, 6, 7, 7, 11, 10, 12, 8, 9, 9, 13, 12, 13, 9, 10, 10, 14, 13, 14,
			10, 11, 11, 15, 14 };

	private final float[] _gridTexX = new float[4];

	private final float[] _gridTexY = new float[4];

	private final float[] _gridX = new float[4];

	private final float[] _gridY = new float[4];

	private final RectF _viewOffset = new RectF();

	private final Matrix4 _combinedMatrix = new Matrix4();

	private final VertexStream _vertexStream;

	private final Mesh _mesh;

	private ShaderProgram _shader = null;

	private ShaderProgram _customShader = null;

	private ShaderSource _source;

	private Matrix4 _batchMatrix;

	private BlendState _lastBlendState = BlendState.NonPremultiplied;

	private boolean _isLoaded, _drawing, _dirty;

	private int _drawCallCount;

	private int _maxVertsInBatch, _indexCount;

	private int _primitiveType = GL20.GL_TRIANGLES;

	public LTextureShape() {
		this(2048, LSystem.getShaderSource(), null);
	}

	public LTextureShape(final int size, final ShaderSource src, final ShaderProgram defaultShader) {
		if (size > 5460) {
			throw new LSysException("Can't have more than 5460 sprites per batch: " + size);
		}
		this._source = src;
		this._shader = defaultShader;
		this._vertexStream = new VertexStream(size, size / 2);
		this._mesh = new Mesh(VertexDataType.VertexArray, false, size * 4, size * 6,
				new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
		this._dirty = true;
	}

	public void setViewOffset(float x, float y) {
		_viewOffset.set(x, y);
	}

	public RectF getViewOffset() {
		return _viewOffset;
	}

	public VertexStream getVertexStream() {
		return _vertexStream;
	}

	public LTextureShape setContextRect(float x, float y, float w, float h) {
		_vertexStream.setContextRect(x, y, w, h);
		_dirty = true;
		return this;
	}

	public LTextureShape setTexture(LTexture tex) {
		_vertexStream.setTexture(tex);
		_vertexStream.setContextRect(0, 0, tex.getWidth(), tex.getHeight());
		_dirty = true;
		return this;
	}

	public LTextureShape setColor(LColor c) {
		_vertexStream.setColor(c);
		_dirty = true;
		return this;
	}

	public void addSliceTexture(RectF gridRect, short tileGridIndice) {
		addSliceTexture(_vertexStream.getContentRect(), _vertexStream.getUVRect(), gridRect, tileGridIndice);
	}

	public void addSliceTexture(RectF contentRect, RectF uvRect, RectF gridRect, short tileGridIndice) {
		final LTexture tex = _vertexStream.getTexture();
		final float sourceW = tex.getWidth();
		final float sourceH = tex.getHeight();

		final float sx = uvRect.width / sourceW;
		final float sy = uvRect.height / sourceH;
		final float xMax = gridRect.getRight();
		final float yMax = gridRect.getBottom();

		_gridTexX[0] = uvRect.x;
		_gridTexX[1] = uvRect.x + gridRect.x * sx;
		_gridTexX[2] = uvRect.x + xMax * sx;
		_gridTexX[3] = uvRect.getRight();

		_gridTexY[0] = uvRect.y;
		_gridTexY[1] = uvRect.y + gridRect.y * sy;
		_gridTexY[2] = uvRect.y + yMax * sy;
		_gridTexY[3] = uvRect.getBottom();

		_gridX[0] = contentRect.x;
		if (contentRect.width >= (sourceW - gridRect.width)) {
			_gridX[1] = _gridX[0] + gridRect.x;
			_gridX[2] = contentRect.getRight() - (sourceW - xMax);
			_gridX[3] = contentRect.getRight();
		} else {
			float tmp = gridRect.x / (sourceW - xMax);
			float adjustedTmp = _gridX[0] + contentRect.width * tmp / (1 + tmp);
			_gridX[1] = adjustedTmp;
			_gridX[2] = adjustedTmp;
			_gridX[3] = contentRect.getRight();
		}

		_gridY[0] = contentRect.y;
		if (contentRect.height >= (sourceH - gridRect.height)) {
			_gridY[1] = _gridY[0] + gridRect.y;
			_gridY[2] = contentRect.getBottom() - (sourceH - yMax);
			_gridY[3] = contentRect.getBottom();
		} else {
			float tmp = gridRect.y / (sourceH - yMax);
			float adjustedTmp = _gridY[0] + contentRect.height * tmp / (1 + tmp);
			_gridY[1] = adjustedTmp;
			_gridY[2] = adjustedTmp;
			_gridY[3] = contentRect.getBottom();
		}

		if (tileGridIndice == 0) {
			for (int cy = 0; cy < 4; cy++) {
				for (int cx = 0; cx < 4; cx++) {
					_vertexStream.addVert(_gridX[cx], _gridY[cy], null, _gridTexX[cx], _gridTexY[cy]);
				}
			}
			_vertexStream.addTriangles(TRIANGLES_9_GRID);
		} else {
			final RectF drawRectTmp = new RectF();
			final RectF uvRectTmp = new RectF();
			int qi = _vertexStream.getVertCount();
			for (int pi = 0; pi < 9; pi++) {
				int col = pi % 3;
				int row = MathUtils.floor(pi / 3);
				short part = GRID_TILE_INDICE[pi];
				RectF.minMaxRect(_gridX[col], _gridY[row], _gridX[col + 1], _gridY[row + 1], drawRectTmp);
				RectF.minMaxRect(_gridTexX[col], _gridTexY[row], _gridTexX[col + 1], _gridTexY[row + 1], uvRectTmp);

				if (part != -1 && (tileGridIndice & (1 << part)) != 0) {
					if (qi != _vertexStream.getVertCount()) {
						_vertexStream.triangulateQuad(qi);
					}
					VertexStream.createTile(_vertexStream, drawRectTmp, uvRectTmp,
							(part == 0 || part == 1 || part == 4) ? gridRect.width : drawRectTmp.width,
							(part == 2 || part == 3 || part == 4) ? gridRect.height : drawRectTmp.height, true, true);

					qi = _vertexStream.getVertCount();
				} else {
					_vertexStream.addQuad(drawRectTmp, null, uvRectTmp);
				}
			}
			if (qi != _vertexStream.getVertCount()) {
				_vertexStream.triangulateQuad(qi);
			}
		}
		_dirty = true;
	}

	public void addTile(float x, float y, float w, float h, boolean repeatX, boolean repeatY) {
		_vertexStream.setContextRect(x, y, w, h);
		VertexStream.addTile(_vertexStream, repeatX, repeatY);
		_dirty = true;
	}

	public void addCircle(float x, float y, float w, float h) {
		_vertexStream.setContextRect(x, y, w, h);
		VertexStream.addCircle(_vertexStream);
		_dirty = true;
	}

	public void addCircleProgress(float x, float y, float w, float h, float angle) {
		addCircleProgress(x, y, w, h, FillStyle.R360, angle);
	}

	public void addCircleProgress(float x, float y, float w, float h, FillStyle style, float angle) {
		addCircleProgress(x, y, w, h, style, FillOrigin.Top, angle, true);
	}

	public void addCircleProgress(float x, float y, float w, float h, FillStyle style, FillOrigin origin, float angle,
			boolean clockwise) {
		final float rand = angle / 360;
		_vertexStream.setContextRect(x, y, w, h);
		VertexStream.addProgress(_vertexStream, style, origin, rand, clockwise);
		_dirty = true;
	}

	public void addPolygon(float x, float y, float w, float h, LColor fillColor, LColor lineColor, LColor centerColor,
			float rotation, float lineWidth, int sides) {
		_vertexStream.setContextRect(x, y, w, h);
		VertexStream.addPolygon(_vertexStream, fillColor, lineColor, centerColor, rotation, lineWidth, sides);
		_dirty = true;
	}

	public void addRoundedRect(float x, float y, float w, float h, float rsize) {
		_vertexStream.setContextRect(x, y, w, h);
		VertexStream.addRoundedRect(_vertexStream, rsize, rsize, rsize, rsize);
		_dirty = true;
	}

	public void addVert(float x, float y) {
		addVert(x, y, null);
	}

	public void addVert(float x, float y, LColor color) {
		addVert(x, y, color, -1f, -1f);
	}

	public void addVert(float x, float y, float u, float v) {
		_vertexStream.addVert(x, y, u, v);
		_dirty = true;
	}

	public void addVert(float x, float y, LColor color, float u, float v) {
		_vertexStream.addVert(x, y, color, u, v);
		_dirty = true;
	}

	public void addQuad(RectF rect, LColor color, RectF uvRect) {
		_vertexStream.addQuad(rect, color, uvRect);
		_dirty = true;
	}

	public void addTriangle(short idx0, short idx1, short idx2) {
		_vertexStream.addTriangle(idx0, idx1, idx2);
		_dirty = true;
	}

	private LTextureShape setupMatrices(Matrix4 view) {
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

	public BlendState getBlendState() {
		return _lastBlendState;
	}

	public LTextureShape setBlendState(BlendState state) {
		this._lastBlendState = state;
		return this;
	}

	protected ShaderProgram createShaderProgram() {
		return GLUtils.createShaderProgram(_source.vertexShader(), _source.fragmentShader());
	}

	protected ShaderProgram getShaderProgram() {
		return _shader;
	}

	public int getPrimitiveType() {
		return _primitiveType;
	}

	public LTextureShape setPrimitiveType(int t) {
		_primitiveType = t;
		return this;
	}

	public void setDirty(boolean d) {
		_dirty = d;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public void clearVertexStream() {
		_vertexStream.clear();
		_dirty = true;
	}

	public LTextureShape begin() {
		if (!_isLoaded) {
			if (_shader == null) {
				_shader = createShaderProgram();
			}
			_isLoaded = true;
		}
		if (_drawing) {
			throw new LSysException("TextureShape.end must be called before begin.");
		}
		LSystem.mainEndDraw();
		final Graphics graphics = LSystem.base().graphics();
		graphics.gl.glDepthMask(false);
		if (_customShader != null) {
			_customShader.begin();
		} else {
			_shader.begin();
		}
		setupMatrices(graphics.getViewMatrix());
		_drawing = true;
		_drawCallCount = 0;
		return this;
	}

	public LTextureShape end() {
		if (!_isLoaded) {
			return this;
		}
		if (!_drawing) {
			throw new LSysException("TextureShape.begin must be called before end.");
		}
		final Graphics graphics = LSystem.base().graphics();
		if (_vertexStream.getVertSize() > 0) {
			if (_viewOffset.x != 0 || _viewOffset.y != 0) {
				Matrix4 project = graphics.getViewMatrix().cpy();
				project.translate(_viewOffset.x, _viewOffset.y, 0);
				if (_drawing) {
					setupMatrices(project);
				}
			}
			submit();
		}
		_drawing = false;
		graphics.gl.glDepthMask(true);
		if (_customShader != null) {
			_customShader.end();
		} else {
			_shader.end();
		}
		LSystem.mainBeginDraw();
		_drawCallCount = 0;
		return this;
	}

	public LTextureShape submit() {
		return submit(_lastBlendState);
	}

	public LTextureShape submit(BlendState state) {
		if (_vertexStream.getVertSize() == 0) {
			return this;
		}
		final int vertCount = _vertexStream.getVertCount();
		if (vertCount > _maxVertsInBatch) {
			_maxVertsInBatch = vertCount;
		}
		this._indexCount = vertCount * 6;
		GL20 gl = LSystem.base().graphics().gl;
		GLUtils.bindTexture(gl, _vertexStream.getTexture().getID());
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
			post(_indexCount, _customShader != null ? _customShader : _shader);
		} catch (Throwable e) {
			LSystem.error("TextureBatch submit() exception", e);
		} finally {
			GLUtils.setBlendMode(gl, old);
			_drawCallCount++;
			GraphicsDrawCall.add(_drawCallCount);
		}
		return this;
	}

	public void bindVertexs() {
		if (_dirty) {
			_mesh.setIndices(_vertexStream.getIndicesData(), 0, _vertexStream.getIndiSize());
			_mesh.setVertices(_vertexStream.getVerticesData(), 0, _vertexStream.getVertSize());
			_dirty = false;
		}
	}

	protected void post(int count, ShaderProgram shader) {
		final boolean main_draw_running = LSystem.mainDrawRunning();
		if (!main_draw_running) {
			shader.glUseProgramBind();
		}
		bindVertexs();
		final ShortBuffer buffer = _mesh.getIndicesBuffer(false);
		final int oldPosition = buffer.position();
		final int oldLimit = buffer.limit();
		final Buffer result = ((Buffer) buffer);
		result.position(0);
		result.limit(count);
		_mesh.render(shader, _primitiveType, 0, count);
		result.position(oldPosition);
		result.limit(oldLimit);
		if (!main_draw_running) {
			shader.glUseProgramUnBind();
		}
	}

	@Override
	public void close() {
		_vertexStream.close();
		_mesh.close();
		if (_shader != null) {
			_shader.close();
		}
		if (_customShader != null) {
			_customShader.close();
		}
	}

}
