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
package loon.geom;

import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

/**
 * 矩形对象剪切用类
 */
public class Clip {

	private Clip _parent;

	private int _displayWidth;

	private int _displayHeight;

	private float _offX, _offY;

	private float _widthRatio, _heightRatio;

	private float _factor;

	private int _regionWidth, _regionHeight;

	public Clip(float x, float y, float w, float h) {
		this(x, y, w, h, false);
	}

	public Clip(float x, float y, float w, float h, boolean updateSize) {
		this(null, 1f, (int) x, (int) y, (int) w, (int) h, updateSize);
	}

	public Clip(int x, int y, int w, int h, boolean updateSize) {
		this(null, 1f, x, y, w, h, updateSize);
	}

	public Clip(Clip parent, float x, float y, float w, float h, boolean updateSize) {
		this(parent, 1f, (int) x, (int) y, (int) w, (int) h, updateSize);
	}

	public Clip(Clip parent, int x, int y, int w, int h, boolean updateSize) {
		this(parent, 1f, x, y, w, h, updateSize);
	}

	public Clip(Clip parent, float factor, int x, int y, int w, int h, boolean updateSize) {
		this._parent = parent;
		this._factor = factor;
		if (parent == null) {
			this._displayWidth = w;
			this._displayHeight = h;
			this.setRegion(x, y, w, h);
		} else {
			this.setRegion(parent, x, y, w, h, updateSize);
		}
	}

	public Clip getParent() {
		return this._parent;
	}

	public Clip setRegion(Clip region, boolean updateSize) {
		if (region != null) {
			if (updateSize) {
				this._displayWidth = region.getRegionWidth();
				this._displayHeight = region.getRegionHeight();
			} else {
				this._displayWidth = region._displayWidth;
				this._displayHeight = region._displayHeight;
			}
			setRegion(region._offX, region._offY, region._widthRatio, region._heightRatio);
		}
		return this;
	}

	public Clip setRegion(Clip region, int x, int y, int width, int height, boolean updateSize) {
		if (region != null) {
			if (updateSize) {
				this._displayWidth = region.getRegionWidth();
				this._displayHeight = region.getRegionHeight();
			} else {
				this._displayWidth = region._displayWidth;
				this._displayHeight = region._displayHeight;
			}
			setRegion(region.getRegionX() + x, region.getRegionY() + y, width, height);
		} else {
			setRegion(x, y, width, height);
		}
		return this;
	}

	public Clip setRegion(int x, int y, int width, int height) {
		float invTexWidth = _factor / _displayWidth;
		float invTexHeight = _factor / _displayHeight;
		setRegion(x * invTexWidth, y * invTexHeight, (x + width) * invTexWidth, (y + height) * invTexHeight);
		_regionWidth = MathUtils.abs(width);
		_regionHeight = MathUtils.abs(height);
		return this;
	}

	public Clip setRegion(float u, float v, float u2, float v2) {
		int texWidth = _displayWidth, texHeight = _displayHeight;
		_regionWidth = MathUtils.round(MathUtils.abs(u2 - u) * texWidth);
		_regionHeight = MathUtils.round(MathUtils.abs(v2 - v) * texHeight);
		if (_regionWidth == _factor && _regionHeight == _factor) {
			float adjustX = 0.25f / texWidth;
			u += adjustX;
			u2 -= adjustX;
			float adjustY = 0.25f / texHeight;
			v += adjustY;
			v2 -= adjustY;
		}
		this._offX = u;
		this._offY = v;
		this._widthRatio = u2;
		this._heightRatio = v2;
		return this;
	}

	public float getDisplayWidth() {
		return _displayWidth;
	}

	public float getDisplayHeight() {
		return _displayHeight;
	}

	public float sx() {
		return _offX;
	}

	public float sy() {
		return _offY;
	}

	public float tx() {
		return _widthRatio;
	}

	public float ty() {
		return _heightRatio;
	}

	public float xOff() {
		return _offX;
	}

	public float yOff() {
		return _offY;
	}

	public float widthRatio() {
		return _widthRatio;
	}

	public float heightRatio() {
		return _heightRatio;
	}

	public Clip setU(float offX) {
		this._offX = offX;
		_regionWidth = MathUtils.round(MathUtils.abs(_widthRatio - _offX) * _displayWidth);
		return this;
	}

	public float getV() {
		return _offY;
	}

	public Clip setV(float offY) {
		this._offY = offY;
		_regionHeight = MathUtils.round(MathUtils.abs(_heightRatio - _offY) * _displayHeight);
		return this;
	}

	public float getU2() {
		return _widthRatio;
	}

	public Clip setU2(float ratio) {
		this._widthRatio = ratio;
		_regionWidth = MathUtils.round(MathUtils.abs(_widthRatio - _offX) * _displayWidth);
		return this;
	}

	public float getV2() {
		return _heightRatio;
	}

	public Clip setV2(float ratio) {
		this._heightRatio = ratio;
		_regionHeight = MathUtils.round(MathUtils.abs(_heightRatio - _offY) * _displayHeight);
		return this;
	}

	public int getRegionX() {
		return MathUtils.round(_offX * _displayWidth);
	}

	public Clip setRegionX(int x) {
		setU(x / (float) _displayWidth);
		return this;
	}

	public int getRegionY() {
		return MathUtils.round(_offY * _displayHeight);
	}

	public Clip setRegionY(int y) {
		setV(y / (float) _displayHeight);
		return this;
	}

	public int getRegionWidth() {
		return _regionWidth;
	}

	public Clip setRegionWidth(int width) {
		if (isFlipX()) {
			setU(_widthRatio + width / (float) _displayWidth);
		} else {
			setU2(_offX + width / (float) _displayWidth);
		}
		return this;
	}

	public int getRegionHeight() {
		return _regionHeight;
	}

	public Clip setRegionHeight(int height) {
		if (isFlipY()) {
			setV(_heightRatio + height / (float) _displayHeight);
		} else {
			setV2(_offY + height / (float) _displayHeight);
		}
		return this;
	}

	public Clip flip(boolean x, boolean y) {
		if (x) {
			float temp = _offX;
			_offX = _widthRatio;
			_widthRatio = temp;
		}
		if (y) {
			float temp = _offY;
			_offY = _heightRatio;
			_heightRatio = temp;
		}
		return this;
	}

	public Clip offset(float xAmount, float yAmount) {
		if (xAmount != 0) {
			float width = (_widthRatio - _offX) * _displayWidth;
			_offX = (_offX + xAmount) % 1;
			_widthRatio = _offX + width / _displayWidth;
		}
		if (yAmount != 0) {
			float height = (_heightRatio - _offY) * _displayHeight;
			_offY = (_offY + yAmount) % 1;
			_heightRatio = _offY + height / _displayHeight;
		}
		return this;
	}

	public boolean isFlipX() {
		return _offX > _widthRatio;
	}

	public boolean isFlipY() {
		return _offY > _heightRatio;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Clip");
		builder.kv("x", getRegionX()).comma().kv("y", getRegionY()).comma().kv("width", getRegionWidth()).comma()
				.kv("height", getRegionHeight());
		return builder.toString();
	}
}
