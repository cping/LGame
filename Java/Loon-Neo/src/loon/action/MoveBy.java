package loon.action;

import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class MoveBy extends ActionEvent {

	private int _speed = 1, _count = 0;

	private float _startX = -1, _startY = -1, _endX, _endY, _lastX, _lastY;

	private EaseTimer easeTimer;

	public MoveBy(float endX, float endY, float duration, float delay,
			EasingMode easing) {
		this(-1, -1, endX, endY, 0, duration, delay, easing);
	}

	public MoveBy(float endX, float endY, EasingMode easing) {
		this(-1, -1, endX, endY, 0, 1f, 1f / 60f, easing);
	}

	public MoveBy(float endX, float endY, float duration, EasingMode easing) {
		this(-1, -1, endX, endY, 0, duration, 1f / 60f, easing);
	}

	public MoveBy(float endX, float endY, int speed) {
		this(-1, -1, endX, endY, speed, 1f, 1f / 60f, EasingMode.Linear);
	}

	public MoveBy(float startX, float startY, float endX, float endY,
			int speed, float duration, float delay, EasingMode easing) {
		this._startX = startX;
		this._startY = startY;
		this._endX = endX;
		this._endY = endY;
		this._speed = speed;
		this.easeTimer = new EaseTimer(duration, delay, easing);
		this.setDelay(0);
	}

	@Override
	public void update(long elapsedTime) {
		synchronized (original) {
			if (_speed == 0) {
				easeTimer.update(elapsedTime);
				if (easeTimer.isCompleted()) {
					_isCompleted = true;
					return;
				}
				original.setLocation(
						_startX + (_endX - _startX) * easeTimer.getProgress()
								+ offsetX, _startY + (_endY - _startY)
								* easeTimer.getProgress() + offsetY);
			} else {
				float x = original.getX();
				float y = original.getY();
				if (x < _endX) {
					x += _speed;
				} else if (x > _endX + _speed) {
					x -= _speed;
				}
				if (y < _endY) {
					y += _speed;
				} else if (y > _endY + _speed) {
					y -= _speed;
				}
				if (_count > 0) {
					_isCompleted = true;
					return;
				}
				if (_lastX == x && _lastY == y) {
					_count++;
				}
				_lastX = x;
				_lastY = y;
				original.setLocation(x + offsetX, y + offsetY);
			}
		}
		return;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			if (_startX == -1) {
				_startX = original.getX();
			}
			if (_startY == -1) {
				_startY = original.getY();
			}
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		MoveBy move = new MoveBy(_startX, _startY, _endX, _endY, _speed,
				easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode());
		move.oldX = oldX;
		move.oldY = oldY;
		return move;
	}

	@Override
	public ActionEvent reverse() {
		MoveBy move = new MoveBy(_endX, _endY, _startX, _startY, _speed,
				easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode());
		move.oldX = oldX;
		move.oldY = oldY;
		return move;
	}

	@Override
	public String getName() {
		return "moveby";
	}

}
