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
package loon.utils;

import loon.LRelease;

/**
 * LRelease资源管理器
 */
public class Disposes implements LRelease {

	private final Object _lock = new Object();

	private final SortedList<LRelease> _disposeSelf;

	public Disposes() {
		this._disposeSelf = new SortedList<LRelease>();
	}

	public Disposes put(LRelease... rs) {
		final int size = rs.length;
		synchronized (_lock) {
			for (int i = 0; i < size; i++) {
				LRelease r = rs[i];
				if (r != null) {
					_disposeSelf.add(r);
				}
			}
		}
		return this;
	}

	public Disposes put(LRelease release) {
		if (release == null) {
			return this;
		}
		synchronized (_lock) {
			_disposeSelf.add(release);
		}
		return this;
	}

	public boolean contains(LRelease release) {
		if (release == null) {
			return false;
		}
		synchronized (_lock) {
			return _disposeSelf.contains(release);
		}
	}

	public Disposes remove(LRelease release) {
		if (release == null) {
			return this;
		}
		synchronized (_lock) {
			_disposeSelf.remove(release);
		}
		return this;
	}

	@Override
	public void close() {
		if (_disposeSelf.size == 0) {
			return;
		}
		synchronized (_lock) {
			for (LIterator<LRelease> it = _disposeSelf.listIterator(); it.hasNext();) {
				LRelease release = it.next();
				if (release != null) {
					release.close();
				}
			}
			_disposeSelf.clear();
		}
	}
}
