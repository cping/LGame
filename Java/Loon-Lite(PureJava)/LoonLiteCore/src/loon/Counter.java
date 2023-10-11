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

import loon.geom.IV;
import loon.geom.SetIV;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringKeyValue;

/**
 * 一个简单的计数器(主要用于用户跨类跨函数传参)，允许设定递增或递减值，也可以限制最大和最小取值范围.
 * 
 * 如果参数需要更多科学计算,则可以使用 @see Calculator 类.
 */
public class Counter implements SetIV<Integer>, IV<Integer> {

	private final int _min;

	private final int _max;

	private int _def_value;

	private int _value;

	public Counter() {
		this(0);
	}

	public Counter(int v) {
		this(v, -1, -1);
	}

	public Counter(int v, int min, int max) {
		this(1, v, min, max);
	}

	public Counter(int defv, int v, int min, int max) {
		this._def_value = defv;
		if (min != -1 || max != -1) {
			this._value = MathUtils.clamp(v, min, max);
		} else {
			this._value = v;
		}
		this._min = min;
		this._max = max;
	}

	public Counter setDefaultUpdateValue(int v) {
		this._def_value = v;
		return this;
	}

	public int getDefaultUpdateValue() {
		return this._def_value;
	}

	public int next(int v) {
		return increment(v);
	}

	public int next() {
		return increment(_def_value);
	}

	public int plus(int v) {
		return increment(v);
	}

	public int plus() {
		return increment(_def_value);
	}

	public int add(int v) {
		return increment(v);
	}

	public int add() {
		return increment(_def_value);
	}

	public int sub(int v) {
		return reduction(v);
	}

	public int sub() {
		return reduction(_def_value);
	}

	public int back(int v) {
		return reduction(v);
	}

	public int back() {
		return reduction(_def_value);
	}

	public int increment(int val) {
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

	public int reduction(int val) {
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
	public void set(Integer v) {
		if (v == null) {
			return;
		}
		setValue(v.intValue());
	}

	public Counter setValue(int val) {
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

	public int N() {
		return this._value;
	}

	@Override
	public Integer get() {
		return Integer.valueOf(this._value);
	}

	public int getValue() {
		return this._value;
	}

	public int increment() {
		return increment(_def_value);
	}

	public int incId(int i) {
		return increment(i) - i;
	}

	public int incId() {
		return incId(_def_value);
	}

	public int reduction() {
		return reduction(_def_value);
	}

	public Counter clear() {
		this._value = 0;
		return this;
	}

	@Override
	public String toString() {
		StringKeyValue v = new StringKeyValue("Counter");
		v.kv("value", _value).comma()
		       .kv("min", _min).comma()
		       .kv("max", _max).comma()
		       .kv("defaultUpdate", _def_value);
		return v.toString();
	}

}
