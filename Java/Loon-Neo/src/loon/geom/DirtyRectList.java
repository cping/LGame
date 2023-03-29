/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
import loon.utils.SortedList;
import loon.utils.StringKeyValue;

/**
 * 脏矩计算用类,产生一个脏矩集合用类,用来展示所有可合并脏矩区域,不连续的将分别保存在集合中
 */
public class DirtyRectList {

	private final int _maxSizeLimit;

	private final DirtyRect _dirtyMergeRect = new DirtyRect();

	private final SortedList<RectBox> _originalList = new SortedList<RectBox>();

	private final SortedList<RectBox> _dirtyList = new SortedList<RectBox>();

	private int _dirtysCount = 0;

	private boolean _dirty = false;

	private boolean _saveInitList = false;

	public DirtyRectList() {
		this(false);
	}

	public DirtyRectList(boolean saveOld) {
		this(2048, saveOld);
	}

	public DirtyRectList(int size, boolean saveOld) {
		this._maxSizeLimit = size;
		this._saveInitList = saveOld;
	}

	public boolean add(RectBox rect) {
		if (rect == null) {
			return false;
		}
		return add(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean add(float x, float y, float w, float h) {
		if (_saveInitList) {
			_originalList.add(new RectBox(x, y, w, h));
		}
		for (int i = 0; i < _dirtysCount; i++) {
			RectBox cur = _dirtyList.get(i);
			if (cur == null) {
				cur = new RectBox();
				_dirtyList.add(cur);
			}
			if (x >= cur.x && y >= cur.y && x + w <= cur.x + cur.width && y + h <= cur.y + cur.height) {
				return false;
			} else if (x <= cur.x && y <= cur.y && x + w >= cur.x + cur.width && y + h >= cur.y + cur.height) {
				cur.width = 0;
				cur.height = 0;
			} else if (x >= cur.x && x < cur.x + cur.width && y >= cur.y && y + h <= cur.y + cur.height) {
				w = MathUtils.floor(x + w - (cur.x + cur.width));
				x = MathUtils.floor(cur.x + cur.width);
				i = -1;
				continue;
			} else if (x + w > cur.x && x + w <= cur.x + cur.width && y >= cur.y && y + h <= cur.y + cur.height) {
				w = MathUtils.floor(cur.x - x);
				i = -1;
				continue;
			} else if (x >= cur.x && x + w <= cur.x + cur.width && y >= cur.y && y < cur.y + cur.height) {
				h = MathUtils.floor(y + h - (cur.y + cur.height));
				y = MathUtils.floor(cur.y + cur.height);
				i = -1;
				continue;
			} else if (x >= cur.x && x + w <= cur.x + cur.width && y + h > cur.y && y + h <= cur.y + cur.height) {
				h = MathUtils.floor(cur.y - y);
				i = -1;
				continue;
			} else if (cur.x >= x && cur.x < x + w && cur.y >= y && cur.y + cur.height <= y + h) {
				cur.width = MathUtils.floor(cur.width - (x + w - cur.x));
				cur.x = x + w;
				i = -1;
				continue;
			} else if (cur.x + cur.width > x && cur.x + cur.width <= x + w && cur.y >= y
					&& cur.y + cur.height <= y + h) {
				cur.width = MathUtils.floor(x - cur.x);
				i = -1;
				continue;
			} else if (cur.x >= x && cur.x + cur.width <= x + w && cur.y >= y && cur.y < y + h) {
				cur.height = MathUtils.floor(cur.height - (y + h - cur.y));
				cur.y = y + h;
				i = -1;
				continue;
			} else if (cur.x >= x && cur.x + cur.width <= x + w && cur.y + cur.height > y
					&& cur.y + cur.height <= y + h) {
				cur.height = MathUtils.floor(y - cur.y);
				i = -1;
				continue;
			}
		}
		for (int i = 0; i < _dirtysCount; i++) {
			final RectBox cur = _dirtyList.get(i);
			if (w > 0 && h > 0 && cur.width > 0 && cur.height > 0
					&& ((MathUtils.max(x + w, cur.x + cur.width) - MathUtils.min(x, cur.x))
							* (MathUtils.max(y + h, cur.y + cur.height) - MathUtils.min(y, cur.y)) < w * h
									+ cur.width * cur.height + _maxSizeLimit)) {
				final float newX = MathUtils.min(cur.x, x);
				final float newY = MathUtils.min(cur.y, y);
				final float newWidth = MathUtils.max(x + w, cur.x + cur.width) - MathUtils.min(cur.x, x);
				final float newHeight = MathUtils.max(y + h, cur.y + cur.height) - MathUtils.min(cur.y, y);
				cur.width = 0;
				cur.height = 0;
				_dirty = true;
				return add(newX, newY, newWidth, newHeight);
			}
		}
		_dirtyList.add(new RectBox(x, y, w, h));
		_dirtysCount++;
		_dirty = true;
		return true;
	}

	public DirtyRect merge() {
		if (_dirtysCount == 0) {
			return _dirtyMergeRect;
		}
		if (_dirty) {
			_dirtyMergeRect.clear();
			for (RectBox r : _dirtyList) {
				if (r != null) {
					_dirtyMergeRect.add(r);
				}
			}
			_dirty = false;
		}
		return _dirtyMergeRect;
	}

	public DirtyRectList remove(RectBox rect) {
		if (_dirtyList.remove(rect)) {
			_dirtysCount--;
		}
		return this;
	}

	public DirtyRectList clear() {
		_dirtyList.clear();
		_originalList.clear();
		_dirtysCount = 0;
		return this;
	}

	public boolean isEmpty() {
		return _dirtysCount == 0;
	}

	public int size() {
		return _dirtysCount;
	}

	public SortedList<RectBox> list() {
		return _dirtyList;
	}

	public SortedList<RectBox> initList() {
		return _originalList;
	}

	@Override
	public String toString() {
		StringKeyValue kv = new StringKeyValue("DirtyRectList");
		kv.newLine();
		int idx = 0;
		for (RectBox rect : _dirtyList) {
			kv.pushBracket()
			.addValue(rect.x)
			.comma()
			.addValue(rect.y)
			.comma()
			.addValue(rect.width)
			.comma()
			.addValue(rect.height)
			.popBracket();
			if (idx < _dirtyList.size - 1) {
				kv.comma();
			}
			kv.newLine();
			idx++;
		}
		return kv.toString();
	}
}
