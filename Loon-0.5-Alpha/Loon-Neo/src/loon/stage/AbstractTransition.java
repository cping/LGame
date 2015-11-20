package loon.stage;

import loon.event.Updateable;

public abstract class AbstractTransition<T extends AbstractTransition<T>>
		extends StageTransition {

	protected float _duration = defaultDuration();

	protected Updateable _onStart, _onComplete;

	public void setDuration(float dt) {
		this._duration = dt;
	}

	public float getDuration() {
		return this._duration;
	}

	public T duration(float duration) {
		_duration = duration;
		return asT();
	}

	public T onStart(Updateable action) {
		_onStart = action;
		return asT();
	}

	public T onComplete(Updateable action) {
		_onComplete = action;
		return asT();
	}

	@Override
	public void init(Stage o, Stage n) {
		if (_onStart != null) {
			_onStart.action(this);
		}
	}

	@Override
	public void complete(Stage o, Stage n) {
		if (_onComplete != null) {
			_onComplete.action(this);
		}
	}

	@SuppressWarnings({ "unchecked", "cast" })
	protected T asT() {
		return (T) this;
	}

	protected float defaultDuration() {
		return 1000;
	}

}
