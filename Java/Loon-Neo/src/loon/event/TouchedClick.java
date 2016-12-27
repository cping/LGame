package loon.event;

import loon.component.LComponent;
import loon.utils.TArray;

public class TouchedClick implements ClickListener {

	private Touched _downTouch;

	private Touched _upTouch;

	private Touched _dragTouch;

	private Touched _allTouch;

	private boolean _enabled = true, _downClick = false;

	private TArray<ClickListener> clicks;

	public TouchedClick addClickListener(ClickListener c) {
		if (clicks == null) {
			clicks = new TArray<ClickListener>(8);
		}
		clicks.add(c);
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
				clicks.get(i).DoClick(comp);
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
				clicks.get(i).DownClick(comp, x, y);
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
					clicks.get(i).UpClick(comp, x, y);
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
				clicks.get(i).DragClick(comp, x, y);
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

	public void clear() {
		if (clicks != null) {
			clicks.clear();
		}
	}
}
