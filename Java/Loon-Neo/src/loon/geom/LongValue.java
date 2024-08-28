/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import loon.LRelease;
import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.reply.Nullable;

public class LongValue implements LRelease {

	private long _value;

	public LongValue() {
		this(0);
	}

	public LongValue(long v) {
		this.set(v);
	}

	public boolean update(long v) {
		set(v);
		return v != 0;
	}

	public LongValue set(long v) {
		this._value = v;
		return this;
	}

	public long scaled(long length) {
		return _value * length;
	}

	public long scaledCeil(long length) {
		return MathUtils.iceil(scaled(length));
	}

	public long scaledFloor(long length) {
		return MathUtils.ifloor(scaled(length));
	}

	public long get() {
		return result();
	}

	public long result() {
		return _value;
	}

	public boolean isZero() {
		return MathUtils.equal(_value, 0l);
	}

	@Override
	public int hashCode() {
		int result = 59;
		result = LSystem.unite(result, _value);
		result = LSystem.unite(result, super.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof LongValue) {
			if (MathUtils.equal(((LongValue) o)._value, this._value)) {
				return true;
			}
		}
		return false;
	}

	public LongValue cpy() {
		return new LongValue(_value);
	}

	public Nullable<Long> toNullable() {
		return new Nullable<Long>(_value);
	}

	@Override
	public String toString() {
		return String.valueOf(_value);
	}

	@Override
	public void close() {
		this._value = 0;
	}

}
