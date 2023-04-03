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
import loon.utils.MathUtils;

public class IntValue implements LRelease{

	private int value;

	public IntValue() {
		this(0);
	}

	public IntValue(int v) {
		this.set(v);
	}

	public IntValue set(int v) {
		this.value = v;
		return this;
	}

	public int scaled(int length) {
		return value * length;
	}

	public int scaledCeil(int length) {
		return MathUtils.iceil(scaled(length));
	}

	public int scaledFloor(int length) {
		return MathUtils.ifloor(scaled(length));
	}

	public int get() {
		return result();
	}

	public int result() {
		return value;
	}
	
	public IntValue cpy() {
		return new IntValue(value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public void close() {
		this.value = 0;
	}

}
