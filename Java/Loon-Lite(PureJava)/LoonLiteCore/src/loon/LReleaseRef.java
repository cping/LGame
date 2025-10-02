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

import loon.geom.BooleanValue;

/**
 * LRelease的结果反馈实现
 */
public final class LReleaseRef implements LRelease {

	private LRelease _released;

	private BooleanValue _refCompleted;

	public LReleaseRef(BooleanValue b) {
		this(null, b);
	}

	public LReleaseRef(LRelease r, BooleanValue b) {
		if (r != this) {
			this._released = r;
		}
		this._refCompleted = b == null ? new BooleanValue() : b;
	}

	public boolean isCompleted() {
		return _refCompleted.get();
	}

	@Override
	public void close() {
		if (_released != null) {
			_released.close();
		}
		if (_refCompleted != null) {
			_refCompleted.set(true);
		}
	}

}
