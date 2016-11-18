package loon.action;

public class MoveBy extends ActionEvent {

	private int _speed = 1, _count = 0;

	private float _endX, _endY, _lastX, _lastY;

	public MoveBy(float endX, float endY, int speed) {
		_endX = endX;
		_endY = endY;
		_speed = speed;
	}

	@Override
	public void update(long elapsedTime) {
		synchronized (original) {
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
		return;
	}

	@Override
	public void onLoad() {

	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		MoveBy move = new MoveBy(_endX, _endY, _speed);
		move.oldX = oldX;
		move.oldY = oldY;
		return move;
	}

	@Override
	public ActionEvent reverse() {
		MoveBy move = new MoveBy(oldX, oldY, _speed);
		move.oldX = oldX;
		move.oldY = oldY;
		return move;
	}

	@Override
	public String getName() {
		return "moveby";
	}

}
