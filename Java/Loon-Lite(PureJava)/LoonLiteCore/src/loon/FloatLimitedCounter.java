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

public class FloatLimitedCounter extends FloatCounter {

	private final float _limit;

	public FloatLimitedCounter(float limit) {
		this(limit, 0);
	}

	public FloatLimitedCounter(float limit, float v) {
		this(limit, v, -1, -1);
	}

	public FloatLimitedCounter(float limit, float v, float min, float max) {
		super(v, min, max);
		this._limit = limit;
	}

	public float getLimit() {
		return _limit;
	}

	@Override
	public float increment(float val) {
		if (!isLimitReached()) {
			return super.increment(val);
		}
		return getValue();
	}

	@Override
	public float increment() {
		if (!isLimitReached()) {
			return super.increment();
		}
		return getValue();
	}

	@Override
	public float reduction(float val) {
		if (!isLimitReached()) {
			return super.reduction(val);
		}
		return getValue();
	}

	@Override
	public float reduction() {
		if (!isLimitReached()) {
			return super.reduction();
		}
		return getValue();
	}

	public float valuesUntilLimitRemains() {
		return _limit - getValue();
	}

	public boolean isLimitReached() {
		return _limit > 0 ? (getValue() >= _limit) : (getValue() <= _limit);
	}

}
