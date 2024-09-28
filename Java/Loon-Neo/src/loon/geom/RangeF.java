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

import loon.LSystem;
import loon.utils.MathUtils;

public class RangeF implements XY, SetXY {

	private float _max;

	private float _min;

	private boolean _enabled = true;

	public RangeF(XY pos) {
		this(pos.getX(), pos.getY());
	}

	public RangeF(float min, float max) {
		this._min = min;
		this._max = max;
	}

	public float random() {
		return MathUtils.random(_min, _max);
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public RangeF setEnabled(boolean enabled) {
		this._enabled = enabled;
		return this;
	}

	public boolean contains(float v) {
		return this._min <= v && this._max >= v;
	}

	public boolean contains(XY v) {
		return this.contains(v.getX()) && this.contains(v.getY());
	}

	public float getMax() {
		return _max;
	}

	public RangeF setMax(float max) {
		if (!_enabled) {
			return this;
		}
		this._max = max;
		return this;
	}

	public float getMin() {
		return _min;
	}

	public RangeF setMin(float min) {
		if (!_enabled) {
			return this;
		}
		this._min = min;
		return this;
	}

	@Override
	public void setX(float x) {
		if (!_enabled) {
			return;
		}
		this._min = x;
	}

	@Override
	public void setY(float y) {
		if (!_enabled) {
			return;
		}
		this._max = y;
	}

	@Override
	public float getX() {
		return _min;
	}

	@Override
	public float getY() {
		return _max;
	}

	public float getWidth() {
		return this._max - this._min;
	}

	public float getCenter() {
		return (this._max + this._min) * 0.5f;
	}

	public float lerp(float ratio) {
		return MathUtils.lerp(this._min, this._max, ratio);
	}

	public RangeF add(float pMin, float pMax) {
		return new RangeF(this._min + pMin, this._max + pMax);
	}

	public RangeF sub(float pMin, float pMax) {
		return new RangeF(this._min - pMin, this._max - pMax);
	}

	public RangeF mul(float pMin, float pMax) {
		return new RangeF(this._min * pMin, this._max * pMax);
	}

	public RangeF div(float pMin, float pMax) {
		return new RangeF(this._min / pMin, this._max / pMax);
	}

	public RangeF cpy() {
		return new RangeF(this._min, this._max);
	}

	public boolean equals(RangeF other) {
		return this._min == other._min && this._max == other._max;
	}

	@Override
	public int hashCode() {
		int result = 38;
		result = LSystem.unite(result, _min);
		result = LSystem.unite(result, _max);
		result = LSystem.unite(result, _enabled);
		result = LSystem.unite(result, super.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof RangeF) {
			return this.equals((RangeF) obj);
		}
		return false;
	}

	@Override
	public String toString() {
		return _min + " to " + _max;
	}
}
