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
import loon.utils.MathUtils;

public class LongValue implements LRelease{

	private long value;

	public LongValue() {
		this(0);
	}

	public LongValue(long v) {
		this.set(v);
	}

	public LongValue set(long v) {
		this.value = v;
		return this;
	}

	public long scaled(long length) {
		return value * length;
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
		return value;
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
