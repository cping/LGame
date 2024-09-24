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

import loon.Json;

public class LDTKLayer {

	protected String _id;

	protected int _tileSize;

	protected int _width;

	protected int _height;

	protected int _widthInPixels;

	protected int _heightInPixels;

	protected int _gridSize;

	protected int _levelId;

	protected int _randomSeed;

	protected float _opacity;

	protected float _pixelOffsetX;

	protected float _pixelOffsetY;

	protected boolean _visible;

	protected boolean _dirty;

	public LDTKLayer(Json.Object v) {
		this(v.getString("__identifier"), v.getInt("__cWid"), v.getInt("__cHei"), v.getInt("__gridSize"),
				v.getNumber("__opacity"), v.getInt("levelId"), v.getInt("pxOffsetX"), v.getInt("pxOffsetY"),
				v.getInt("seed"), v.getBoolean("visible"));
	}

	public LDTKLayer(String id, int width, int height, int gridSize, float opacity, int levelId, int pixelOffsetX,
			int pixelOffsetY, int randomSeed, boolean v) {
		this._id = id;
		this._width = width;
		this._height = height;
		this._gridSize = gridSize;
		this._opacity = opacity;
		this._levelId = levelId;
		this._pixelOffsetX = pixelOffsetX;
		this._pixelOffsetY = pixelOffsetY;
		this._randomSeed = randomSeed;
		this._tileSize = _width * _height;
		this._dirty = true;
	}

	public String getId() {
		return _id;
	}

	public LDTKLayer setId(String id) {
		if (!this._id.equals(id)) {
			this._dirty = true;
		}
		this._id = id;
		return this;
	}

	public int getTileSize() {
		return _tileSize;
	}

	public float getWidth() {
		return _width;
	}

	public LDTKLayer setWidth(int width) {
		if (this._width != width) {
			this._dirty = true;
		}
		this._width = width;
		return this;
	}

	public float getHeight() {
		return _height;
	}

	public LDTKLayer setHeight(int height) {
		if (this._height != height) {
			this._dirty = true;
		}
		this._height = height;
		return this;
	}

	public int getViewWidth() {
		return _widthInPixels;
	}

	public int getViewHeight() {
		return _heightInPixels;
	}

	public int getGridSize() {
		return _gridSize;
	}

	public LDTKLayer setGridSize(int gridSize) {
		if (this._gridSize != gridSize) {
			this._dirty = true;
		}
		this._gridSize = gridSize;
		return this;
	}

	public float getOpacity() {
		return _opacity;
	}

	public LDTKLayer setOpacity(float opacity) {
		if (this._opacity != opacity) {
			this._dirty = true;
		}
		this._opacity = opacity;
		return this;
	}

	public int getLevelId() {
		return _levelId;
	}

	public LDTKLayer setLevelId(int levelId) {
		if (this._levelId != levelId) {
			this._dirty = true;
		}
		this._levelId = levelId;
		return this;
	}

	public float getPixelOffsetX() {
		return _pixelOffsetX;
	}

	public LDTKLayer setPixelOffsetX(float pixelOffsetX) {
		if (this._pixelOffsetX != pixelOffsetX) {
			this._dirty = true;
		}
		this._pixelOffsetX = pixelOffsetX;
		return this;
	}

	public float getPixelOffsetY() {
		return _pixelOffsetY;
	}

	public LDTKLayer setPixelOffsetY(float pixelOffsetY) {
		if (this._pixelOffsetY != pixelOffsetY) {
			this._dirty = true;
		}
		this._pixelOffsetY = pixelOffsetY;
		return this;
	}

	public int getRandomSeed() {
		return _randomSeed;
	}

	public LDTKLayer setRandomSeed(int randomSeed) {
		if (this._randomSeed != randomSeed) {
			this._dirty = true;
		}
		this._randomSeed = randomSeed;
		return this;
	}

	public boolean isVisible() {
		return this._visible;
	}
}
