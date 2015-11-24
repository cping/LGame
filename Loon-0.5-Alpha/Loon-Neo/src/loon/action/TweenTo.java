package loon.action;

public class TweenTo<T> extends ActionEvent {

	private ActionTweenBase<T> _base;

	public TweenTo(ActionTweenBase<T> b) {
		this._base = b;
	}

	public ActionTweenBase<T> get() {
		return _base;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void onLoad() {

	}

	public void update(long elapsedTime) {
		if (isComplete) {
			return;
		}
		_base.update(elapsedTime);
		if (_base.isFinished()) {
			isComplete = _base.actionEventOver();
		}
	}

	@Override
	public ActionEvent cpy() {
		return new TweenTo<T>(_base);
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "tween";
	}
}
