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
import loon.geom.RectBox;
import loon.geom.XY;
import loon.opengl.GLEx.Direction;

public class LDTKTile {

	private LTexture _texture;

	private float _pixelX, _pixelY;

	private int _pixelW, _pixelH;

	private boolean _flipX;

	private boolean _flipY;

	private RectBox _rectSize;

	public LDTKTile(LTexture tex, float x, float y, int w, int h) {
		this._texture = tex;
		this._pixelX = x;
		this._pixelY = y;
		this._pixelW = w;
		this._pixelH = h;
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
		this._pixelX = x;
		return this;
	}

	public float getY() {
		return _pixelY;
	}

	public LDTKTile setY(int y) {
		this._pixelY = y;
		return this;
	}
}
