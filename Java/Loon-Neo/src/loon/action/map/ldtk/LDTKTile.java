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
package loon.action.map.ldtk;

import loon.LTexture;
import loon.events.ChangeEvent;
import loon.geom.RectBox;
import loon.geom.XY;
import loon.opengl.GLEx.Direction;
import loon.utils.MathUtils;

public class LDTKTile {

	private LTexture _texture;

	private float _pixelX, _pixelY;

	private int _pixelW, _pixelH;

	private int _tileX, _tileY;

	private int _tileId;

	private boolean _flipX;

	private boolean _flipY;

	private boolean _visible;

	private boolean _dirty;

	private RectBox _rectSize;

	private int _typeIdFlag;

	private ChangeEvent<LDTKTile> _changeValue;

	public LDTKTile(ChangeEvent<LDTKTile> layer, int id, int tf, LTexture tex, float x, float y, int w, int h, int tx,
			int ty) {
		this._tileId = id;
		this._typeIdFlag = tf;
		this._texture = tex;
		this._pixelX = x;
		this._pixelY = y;
		this._pixelW = w;
		this._pixelH = h;
		this._tileX = tx;
		this._tileY = ty;
		this._visible = true;
	}

	private void update() {
		if (this._dirty) {
			if (_changeValue != null) {
				_changeValue.onChange(this);
			}
			this._dirty = false;
		}
	}

	public int getId() {
		return this._tileId;
	}

	public int getTypeIdFlag() {
		return this._typeIdFlag;
	}

	public int getTileX() {
		return this._tileX;
	}

	public int getTileY() {
		return this._tileY;
	}

	public boolean contains(float x, float y) {
		return getRectBox().contains(x, y);
	}

	public boolean contains(XY pos) {
		return getRectBox().contains(pos);
	}

	public RectBox getRectBox() {
		if (_rectSize == null) {
			_rectSize = new RectBox(_pixelX, _pixelY, _pixelW, _pixelH);
		} else {
			_rectSize.setBounds(_pixelX, _pixelY, _pixelW, _pixelH);
		}
		return _rectSize;
	}

	public int getPixelWidth() {
		return this._pixelW;
	}

	public int getPixelHeight() {
		return this._pixelH;
	}

	public Direction getDirection() {
		if (!_flipX && !_flipY) {
			return Direction.TRANS_NONE;
		} else if (_flipX && !_flipY) {
			return Direction.TRANS_FLIP;
		} else if (!_flipX && _flipY) {
			return Direction.TRANS_MIRROR;
		} else {
			return Direction.TRANS_MF;
		}
	}

	public boolean isFlipX() {
		return this._flipX;
	}

	public boolean isFlipY() {
		return this._flipY;
	}

	public LDTKTile flip(boolean x, boolean y) {
		this._flipX = x;
		this._flipY = y;
		return this;
	}

	public boolean isVisible() {
		return _visible;
	}

	public LDTKTile setVisible(boolean v) {
		if (v != this._visible) {
			update();
		}
		this._visible = v;
		return this;
	}

	public LTexture getTexture() {
		return _texture;
	}

	public LDTKTile setTexture(LTexture tex) {
		this._texture = tex;
		return this;
	}

	public float getX() {
		return _pixelX;
	}

	public LDTKTile setX(int x) {
		if (x != this._pixelX) {
			update();
		}
		this._pixelX = x;
		this._tileX = MathUtils.iceil(_pixelX / _pixelW);
		return this;
	}

	public float getY() {
		return _pixelY;
	}

	public LDTKTile setY(int y) {
		if (y != this._pixelX) {
			update();
		}
		this._pixelY = y;
		this._tileY = MathUtils.iceil(_pixelY / _pixelH);
		return this;
	}

	public LDTKTile setTypeFlag(int t) {
		if (t != this._typeIdFlag) {
			update();
		}
		_typeIdFlag = t;
		return this;
	}
}
