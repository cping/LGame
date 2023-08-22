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

public class RangeI {

	private int max;

	private int min;

	private boolean enabled = false;

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

	public int getMax() {
		return max;
	}

	public RangeI setMax(int max) {
		this.max = max;
		return this;
	}

	public int getMin() {
		return min;
	}

	public RangeI setMin(int min) {
		this.min = min;
		return this;
	}

}
