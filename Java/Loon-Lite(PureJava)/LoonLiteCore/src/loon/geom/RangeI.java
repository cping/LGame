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

public class RangeI implements XY, SetXY {

	private int max;

	private int min;

	private boolean enabled = true;

	public RangeI(XY pos) {
		this(MathUtils.ifloor(pos.getX()), MathUtils.ifloor(pos.getY()));
	}

	public RangeI(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public int random() {
		return MathUtils.random(min, max);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public RangeI setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public boolean contains(float v) {
		return this.min <= v && this.max >= v;
	}

	public boolean contains(XY v) {
		return this.contains(v.getX()) && this.contains(v.getY());
	}

	public int getMax() {
		return max;
	}

	public RangeI setMax(int max) {
		if (!enabled) {
			return this;
		}
		this.max = max;
		return this;
	}

	public int getMin() {
		return min;
	}

	public RangeI setMin(int min) {
		if (!enabled) {
			return this;
		}
		this.min = min;
		return this;
	}

	@Override
	public void setX(float x) {
		if (!enabled) {
			return;
		}
		this.min = MathUtils.ifloor(x);
	}

	@Override
	public void setY(float y) {
		if (!enabled) {
			return;
		}
		this.max = MathUtils.ifloor(y);
	}

	@Override
	public float getX() {
		return min;
	}

	@Override
	public float getY() {
		return max;
	}

	public int getWidth() {
		return this.max - this.min;
	}

	public int getCenter() {
		return MathUtils.ifloor((this.max + this.min) * 0.5f);
	}

	public float lerp(float ratio) {
		return MathUtils.lerp(this.min, this.max, ratio);
	}

	public RangeI add(int pMin, int pMax) {
		return new RangeI(this.min + pMin, this.max + pMax);
	}

	public RangeI sub(int pMin, int pMax) {
		return new RangeI(this.min - pMin, this.max - pMax);
	}

	public RangeI mul(int pMin, int pMax) {
		return new RangeI(this.min * pMin, this.max * pMax);
	}

	public RangeI div(int pMin, int pMax) {
		return new RangeI(this.min / pMin, this.max / pMax);
	}

	public RangeI cpy() {
		return new RangeI(this.min, this.max);
	}

	public boolean equals(RangeI other) {
		return this.min == other.min && this.max == other.max;
	}

	@Override
	public int hashCode() {
		int result = 38;
		result = LSystem.unite(result, min);
		result = LSystem.unite(result, max);
		result = LSystem.unite(result, enabled);
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
		if (obj instanceof RangeI) {
			return this.equals((RangeI) obj);
		}
		return false;
	}

	@Override
	public String toString() {
		return min + " to " + max;
	}
}
