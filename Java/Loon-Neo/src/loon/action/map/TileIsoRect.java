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
package loon.action.map;

import loon.LRelease;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.font.IFont;
import loon.geom.Polygon;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 工具用类,绘制指定大小角度的斜角网格区域用
 */
public class TileIsoRect implements LRelease {

	public static class TileIsoImage {

		public LTexture image;

		public float x;

		public float y;

		public float width;

		public float height;

		public float rotation;
	}

	public static TileIsoRect createPos(float x, float y, float tileW, float tileH) {
		return createPos(x, y, tileW, tileH, 0.5f);
	}

	public static TileIsoRect createPos(float x, float y, float tileW, float tileH, float angle) {
		final float newX = MathUtils.sin(angle) * tileW;
		final float newY = MathUtils.sin(angle) * tileH;
		if (angle >= 1f) {
			return new TileIsoRect(x + tileW / 2f + newX / 2f, y + tileH / 2f + newY / 2f, tileW, tileH, angle);
		} else {
			return new TileIsoRect(x + tileW * angle, y + tileH * angle, tileW, tileH, angle);
		}
	}

	public static TileIsoRect createCenter(float x, float y, float tileW, float tileH) {
		return createCenter(x, y, tileW, tileH, 0.5f);
	}

	public static TileIsoRect createCenter(float x, float y, float tileW, float tileH, float angle) {
		return new TileIsoRect(x, y, tileW, tileH, angle);
	}

	protected final float[] verticesX = new float[4];
	protected final float[] verticesY = new float[4];

	private LColor _color;

	private float _angle;
	private float _sinAngle;
	private float _rotation;
	private float _diagonalWidth;
	private float _diagonalHeight;
	private float _centerX;
	private float _centerY;

	private boolean _visible;
	private boolean _fill;
	private boolean _dirty;
	private boolean _selected;
	private boolean _drawImageNotColored;

	private TileIsoImage _isoImage;

	private Polygon _isoRect = new Polygon();

	private int _flag;
	private Object _tag;

	public TileIsoRect(float cx, float cy, float size) {
		this(cx, cy, size, size);
	}

	public TileIsoRect(float cx, float cy, float w, float h) {
		this(cx, cy, w, h, 0.5f);
	}

	public TileIsoRect(float cx, float cy, float w, float h, float angle) {
		this(cx, cy, w, h, angle, null);
	}

	public TileIsoRect(float cx, float cy, float w, float h, float angle, LColor c) {
		this._centerX = cx;
		this._centerY = cy;
		this._angle = angle;
		this._sinAngle = MathUtils.sin(angle);
		this._diagonalWidth = w;
		this._diagonalHeight = h;
		this._color = c;
		this._dirty = _visible = true;
	}

	public void createVertices() {
		final float newX = _sinAngle * _diagonalWidth;
		final float newY = _sinAngle * _diagonalHeight;
		verticesX[0] = MathUtils.ifloor(_centerX + newX);
		verticesX[1] = _centerX;
		verticesX[2] = MathUtils.ifloor(_centerX - newX);
		verticesX[3] = _centerX;
		verticesY[0] = _centerY;
		verticesY[1] = _centerY - newY;
		verticesY[2] = _centerY;
		verticesY[3] = _centerY + newY;
		_isoRect.setPolygon(verticesX, verticesY, 4);
		if (_rotation != 0f) {
			_isoRect.setRotation(_rotation);
		}
	}

	public void draw(GLEx g) {
		draw(g, null, null);
	}

	public void draw(GLEx g, String text, LColor fontColor) {
		if (!_visible) {
			return;
		}
		if (_dirty) {
			createVertices();
			_dirty = false;
		}
		final int color = g.color();
		if (_drawImageNotColored && _isoImage != null) {
			g.setColor(LColor.white);
			drawImage(g);
			g.setColor(color);
		} else {
			final boolean saveColor = _color != null;
			if (saveColor) {
				g.setColor(_color);
			}
			drawImage(g);
			if (_fill) {
				g.fill(_isoRect);
			} else {
				g.draw(_isoRect);
			}
			if (text != null) {
				IFont font = g.getFont();
				g.drawString(text, _centerX - font.stringWidth(text) / 2f, _centerY - font.stringHeight(text) / 2f,
						fontColor == null ? LColor.white : fontColor);
			}
			if (saveColor) {
				g.setColor(color);
			}
		}
	}

	private void drawImage(GLEx g) {
		if (_isoImage != null) {
			if (_isoImage.width > 0f && _isoImage.height > 0f) {
				g.draw(_isoImage.image, _centerX + _isoImage.x - _isoImage.width / 2f,
						_centerY + _isoImage.y - _isoImage.height / 2f, _isoImage.width, _isoImage.height,
						(_isoImage.rotation == -1f ? MathUtils.toDegrees(_sinAngle) : _isoImage.rotation) + _rotation);
			} else {
				float newW = _diagonalWidth;
				float newH = _diagonalHeight;
				if (_angle < 1f) {
					newW = (_diagonalWidth * _sinAngle) / 2f + (_diagonalWidth * _angle);
					newH = (_diagonalHeight * _sinAngle) / 2f + (_diagonalHeight * _angle);
				} else {
					newW = (_diagonalWidth * _sinAngle) + _diagonalWidth / 2f;
					newH = (_diagonalHeight * _sinAngle) + _diagonalHeight / 2f;
				}
				g.draw(_isoImage.image, _centerX + _isoImage.x - newW / 2f, _centerY + _isoImage.y - newH / 2f, newW,
						newH, (_isoImage.rotation == -1f ? 45f : _isoImage.rotation) + _rotation);
			}
		}
	}

	public TileIsoRect setImage(String path) {
		return setImage(path, 0f, 0f);
	}

	public TileIsoRect setImage(String path, float x, float y) {
		return setImage(LTextures.loadTexture(path), 0f, 0f);
	}

	public TileIsoRect setImage(String path, float rotation) {
		return setImage(path, 0f, 0f, rotation);
	}

	public TileIsoRect setImage(String path, float x, float y, float rotation) {
		return setImage(path, 0f, 0f, -1f, -1f, rotation);
	}

	public TileIsoRect setImage(String path, float x, float y, float w, float h, float rotation) {
		return setImage(LTextures.loadTexture(path), 0f, 0f, w, h, rotation);
	}

	public TileIsoRect setImage(LTexture tex) {
		return setImage(tex, 0f, 0f);
	}

	public TileIsoRect setImage(LTexture tex, float x, float y) {
		return setImage(tex, x, y, -1f, -1f);
	}

	public TileIsoRect setImage(LTexture tex, float x, float y, float w, float h) {
		return setImage(tex, x, y, w, h, -1f);
	}

	public TileIsoRect setImage(LTexture tex, float x, float y, float w, float h, float r) {
		this._isoImage = new TileIsoImage();
		this._isoImage.image = tex;
		this._isoImage.x = x;
		this._isoImage.y = y;
		this._isoImage.width = w;
		this._isoImage.height = h;
		this._isoImage.rotation = r;
		return this;
	}

	public TileIsoRect setRatation(float r) {
		if (MathUtils.equal(this._rotation, r)) {
			return this;
		}
		this._rotation = r;
		this._dirty = true;
		return this;
	}

	public float getRotation() {
		return _rotation;
	}

	public LColor getColor() {
		return _color;
	}

	public TileIsoRect setColor(LColor c) {
		this._color = c;
		return this;
	}

	public boolean isVisible() {
		return _visible;
	}

	public TileIsoRect setVisible(boolean v) {
		this._visible = v;
		return this;
	}

	public boolean isFill() {
		return _fill;
	}

	public TileIsoRect setFill(boolean f) {
		this._fill = f;
		return this;
	}

	public boolean isDirty() {
		return this._dirty;
	}

	public float getAngle() {
		return _angle;
	}

	public TileIsoRect setAngle(float f) {
		if (MathUtils.equal(this._angle, f)) {
			return this;
		}
		this._angle = f;
		this._dirty = true;
		return this;
	}

	public TileIsoRect setSize(float w, float h) {
		setWidth(w);
		setHeight(h);
		return this;
	}

	public float getX() {
		createVertices();
		return _isoRect.getX();
	}

	public float getY() {
		createVertices();
		return _isoRect.getY();
	}

	public float getWidth() {
		createVertices();
		return _isoRect.getWidth();
	}

	public float getHeight() {
		createVertices();
		return _isoRect.getHeight();
	}

	public Polygon getRect() {
		createVertices();
		return _isoRect;
	}

	public TileIsoRect setWidth(float w) {
		if (MathUtils.equal(this._diagonalWidth, w)) {
			return this;
		}
		this._diagonalWidth = w;
		this._dirty = true;
		return this;
	}

	public TileIsoRect setHeight(float h) {
		if (MathUtils.equal(this._diagonalHeight, h)) {
			return this;
		}
		this._diagonalHeight = h;
		this._dirty = true;
		return this;
	}

	public float getCenterX() {
		return _centerX;
	}

	public TileIsoRect setCenterX(float x) {
		if (MathUtils.equal(this._centerX, x)) {
			return this;
		}
		this._centerX = x;
		this._dirty = true;
		return this;
	}

	public float getCenterY() {
		return _centerY;
	}

	public TileIsoRect setCenterY(float y) {
		if (MathUtils.equal(this._centerX, y)) {
			return this;
		}
		this._centerY = y;
		this._dirty = true;
		return this;
	}

	public Object getTag() {
		return _tag;
	}

	public TileIsoRect setTag(Object t) {
		this._tag = t;
		return this;
	}

	public boolean isSelected() {
		return _selected;
	}

	public TileIsoRect setSelected(boolean s) {
		this._selected = s;
		return this;
	}

	public boolean isDrawImageNotColored() {
		return _drawImageNotColored;
	}

	public TileIsoRect setDrawImageNotColored(boolean d) {
		this._drawImageNotColored = d;
		return this;
	}

	public int getFlag() {
		return _flag;
	}

	public TileIsoRect setFlag(int f) {
		this._flag = f;
		return this;
	}

	@Override
	public void close() {
		if (_isoImage != null && _isoImage.image != null) {
			_isoImage.image.close();
			_isoImage.image = null;
		}
	}

}
