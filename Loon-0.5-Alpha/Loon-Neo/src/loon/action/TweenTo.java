package loon.action;

public class TweenTo<T> extends ActionEvent {

	private ActionTweenBase<T> _base;

	public TweenTo(ActionTweenBase<T> b) {
		this._base = b;
	}
	
	public ActionTweenBase<T> get(){
		return _base;
	}

	public boolean isComplete() {
		return isComplete || _base.isFinished();
	}

	public void onLoad() {

	}

	public void update(long elapsedTime) {
		_base.update(elapsedTime);
	}
}
