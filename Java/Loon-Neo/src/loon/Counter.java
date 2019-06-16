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

import loon.utils.MathUtils;

/**
 * 一个简单的计数器
 */
public class Counter {

	private int _min;

	private int _max;

	private int _value;

	public Counter() {
		this(0);
	}

	public Counter(int v) {
		this(v, -1, -1);
	}

	public Counter(int v, int min, int max) {
		if (min != -1 || max != -1) {
			this._value = MathUtils.clamp(v, min, max);
		} else {
			this._value = v;
		}
		this._min = min;
		this._max = max;
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

	public int getValue() {
		return this._value;
	}

	public int increment() {
		return increment(1);
	}

	public int reduction() {
		return reduction(1);
	}

	public Counter clear() {
		this._value = 0;
		return this;
	}

	@Override
	public String toString() {
		return String.valueOf(_value);
	}

}
