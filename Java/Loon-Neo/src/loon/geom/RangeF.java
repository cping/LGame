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

public class RangeF implements XY, SetXY {

	private float max;

	private float min;

	private boolean enabled = false;

	public RangeF(float min, float max) {
		this.min = min;
		this.max = max;
	}

	public float random() {
		return MathUtils.random(min, max);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public RangeF setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public float getMax() {
		return max;
	}

	public RangeF setMax(float max) {
		this.max = max;
		return this;
	}

	public float getMin() {
		return min;
	}

	public RangeF setMin(float min) {
		this.min = min;
		return this;
	}

	@Override
	public void setX(float x) {
		this.min = x;
	}

	@Override
	public void setY(float y) {
		this.max = y;
	}

	@Override
	public float getX() {
		return min;
	}

	@Override
	public float getY() {
		return max;
	}

	public RangeF cpy() {
		return new RangeF(this.min, this.max);
	}
}
