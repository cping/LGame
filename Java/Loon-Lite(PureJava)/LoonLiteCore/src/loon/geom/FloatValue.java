/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.geom;

import loon.LRelease;
import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.reply.Nullable;

public class FloatValue implements LRelease {

	private float value = 0f;

	public FloatValue() {
		this(0f);
	}

	public FloatValue(float v) {
		this.set(v);
	}

	public boolean update(float v) {
		set(v);
		return v != 0;
	}

	public FloatValue set(float v) {
		this.value = v;
		return this;
	}

	public float scaled(float length) {
		return value * length;
	}

	public int scaledCeil(float length) {
		return MathUtils.iceil(scaled(length));
	}

	public int scaledFloor(float length) {
		return MathUtils.ifloor(scaled(length));
	}

	public float get() {
		return result();
	}

	public float result() {
		return value;
	}

	public boolean isZero() {
		return MathUtils.equal(value, 0f);
	}

	@Override
	public int hashCode() {
		int result = 59;
		result = LSystem.unite(result, value);
		result = LSystem.unite(result, super.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof FloatValue) {
			if (MathUtils.equal(((FloatValue) o).value, this.value)) {
				return true;
			}
		}
		return false;
	}

	public FloatValue cpy() {
		return new FloatValue(value);
	}

	public Nullable<Float> toNullable() {
		return new Nullable<Float>(value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public void close() {
		this.value = 0f;
	}

}
