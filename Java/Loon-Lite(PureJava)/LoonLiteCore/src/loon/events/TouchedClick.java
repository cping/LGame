/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.events;

import loon.component.LComponent;
import loon.utils.TArray;

/**
 * Touched接口监听实现,利用ClickListener可以让它绑定到任意组件(UI)上
 */
public class TouchedClick implements ClickListener {

	private Touched _downTouch;

	private Touched _upTouch;

	private Touched _dragTouch;

	private Touched _allTouch;

	private boolean _enabled = true, _downClick = false;

	private TArray<ClickListener> clicks;

	public TouchedClick addClickListener(ClickListener c) {
		if ((c == null) || (c == this)) {
			return this;
		}
		if (clicks == null) {
			clicks = new TArray<>(8);
		}
		if (!clicks.contains(c)) {
			clicks.add(c);
		}
		return this;
	}

	@Override
	public void DoClick(LComponent comp) {
		if (!_enabled) {
			return;
		}
		if (_allTouch != null) {
			_allTouch.on(SysTouch.getX(), SysTouch.getY());
		}
		if (clicks != null) {
			for (int i = 0, size = clicks.size; i < size; i++) {
				ClickListener listener = clicks.get(i);
				if (listener != null && listener != this) {
					listener.DoClick(comp);
				}
			}
		}
	}

	@Override
	public void DownClick(LComponent comp, float x, float y) {
		if (!_enabled) {
			return;
		}
		if (_downTouch != null) {
			_downTouch.on(x, y);
		}
		if (clicks != null) {
			for (int i = 0, size = clicks.size; i < size; i++) {
				ClickListener listener = clicks.get(i);
				if (listener != null && listener != this) {
					listener.DownClick(comp, x, y);
				}
			}
		}
		_downClick = true;
	}

	@Override
	public void UpClick(LComponent comp, float x, float y) {
		if (!_enabled) {
			return;
		}
		if (_downClick) {
			if (_upTouch != null) {
				_upTouch.on(x, y);
			}
			if (clicks != null) {
				for (int i = 0, size = clicks.size; i < size; i++) {
					ClickListener listener = clicks.get(i);
					if (listener != null && listener != this) {
						listener.UpClick(comp, x, y);
					}
				}
			}
			_downClick = false;
		}
	}

	@Override
	public void DragClick(LComponent comp, float x, float y) {
		if (!_enabled) {
			return;
		}
		if (_dragTouch != null) {
			_dragTouch.on(x, y);
		}
		if (clicks != null) {
			for (int i = 0, size = clicks.size; i < size; i++) {
				ClickListener listener = clicks.get(i);
				if (listener != null && listener != this) {
					listener.DragClick(comp, x, y);
				}
			}
		}
	}

	public Touched getDownTouch() {
		return _downTouch;
	}

	public void setDownTouch(Touched downTouch) {
		this._downTouch = downTouch;
	}

	public Touched getUpTouch() {
		return _upTouch;
	}

	public void setUpTouch(Touched upTouch) {
		this._upTouch = upTouch;
	}

	public Touched getDragTouch() {
		return _dragTouch;
	}

	public void setDragTouch(Touched dragTouch) {
		this._dragTouch = dragTouch;
	}

	public Touched getAllTouch() {
		return _allTouch;
	}

	public void setAllTouch(Touched allTouch) {
		this._allTouch = allTouch;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public void setEnabled(boolean e) {
		_enabled = e;
	}

	public boolean isClicked() {
		return _downClick;
	}

	public void clear() {
		if (clicks != null) {
			clicks.clear();
		}
		_downClick = false;
	}
}
