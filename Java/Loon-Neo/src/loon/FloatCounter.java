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

import loon.geom.IV;
import loon.geom.SetIV;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

public class FloatCounter implements SetIV<Float>, IV<Float> {

	private final float _min;

	private final float _max;

	private float _def_value;

	private float _value;

	public FloatCounter() {
		this(0);
	}

	public FloatCounter(float v) {
		this(v, -1, -1);
	}

	public FloatCounter(float v, float min, float max) {
		this(1, v, min, max);
	}

	public FloatCounter(float defv, float v, float min, float max) {
		this._def_value = defv;
		if (min != -1 || max != -1) {
			this._value = MathUtils.clamp(v, min, max);
		} else {
			this._value = v;
		}
		this._min = min;
		this._max = max;
	}

	public FloatCounter setDefaultUpdateValue(float v) {
		this._def_value = v;
		return this;
	}

	public float getDefaultUpdateValue() {
		return this._def_value;
	}

	public float limit(float min, float max) {
		return MathUtils.limit(_value, min, max);
	}

	public float next(float v) {
		return increment(v);
	}

	public float next() {
		return increment(_def_value);
	}

	public float plus(float v) {
		return increment(v);
	}

	public float plus() {
		return increment(_def_value);
	}

	public float add(float v) {
		return increment(v);
	}

	public float add() {
		return increment(_def_value);
	}

	public float sub(float v) {
		return reduction(v);
	}

	public float sub() {
		return reduction(_def_value);
	}

	public float back(float v) {
		return reduction(v);
	}

	public float back() {
		return reduction(_def_value);
	}

	public float increment(float val) {
		if (!(this._min == -1 && this._max == -1)) {
			if (this._max != -1 || this._value < this._max) {
				if (this._max != -1 && this._value + val > this._max) {
					this._value = this._max;
				} else {
					this._value += val;
				}
			}
		} else {
			this._value += val;
		}
		return this._value;
	}

	public float reduction(float val) {
		if (!(this._min == -1 && this._max == -1)) {
			if (this._min != -1 || this._value > this._min) {
				if (this._min != -1 && this._value - val < this._min) {
					this._value = this._min;
				} else {
					this._value -= val;
				}
			}
		} else {
			this._value -= val;
		}
		return this._value;
	}

	public float currentPercent() {
		return ((this._value) / (this._max)) * 100f;
	}

	@Override
	public void set(Float v) {
		if (v == null) {
			return;
		}
		setValue(v.floatValue());
	}

	public FloatCounter setValue(float val) {
		if (!(this._min == -1 && this._max == -1)) {
			if (this._max != -1 && val > this._max) {
				this._value = this._max;
			} else if (this._min != -1 && val < this._min) {
				this._value = this._min;
			} else {
				this._value = val;
			}
		} else {
			this._value = val;
		}
		return this;
	}

	public float N() {
		return this._value;
	}

	@Override
	public Float get() {
		return Float.valueOf(this._value);
	}

	public float getValue() {
		return this._value;
	}

	public float increment() {
		return increment(_def_value);
	}

	public float incId(float i) {
		return increment(i) - i;
	}

	public float incId() {
		return incId(_def_value);
	}

	public float reduction() {
		return reduction(_def_value);
	}

	public FloatCounter clear() {
		this._value = 0;
		return this;
	}

	@Override
	public String toString() {
		StringKeyValue v = new StringKeyValue("FloatCounter");
		v.kv("value", _value).comma().kv("min", _min).comma().kv("max", _max).comma().kv("defaultUpdate", _def_value);
		return v.toString();
	}
}
