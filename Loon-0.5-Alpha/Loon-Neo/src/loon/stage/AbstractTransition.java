package loon.stage;

public abstract class AbstractTransition<T extends AbstractTransition<T>>
		extends StageTransition {

	protected float _duration = defaultDuration();

	protected Runnable _onStart, _onComplete;

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

	public T onStart(Runnable action) {
		_onStart = action;
		return asT();
	}

	public T onComplete(Runnable action) {
		_onComplete = action;
		return asT();
	}

	@Override
	public void init(Stage o, Stage n) {
		if (_onStart != null) {
			_onStart.run();
		}
	}

	@Override
	public void complete(Stage o, Stage n) {
		if (_onComplete != null) {
			_onComplete.run();
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
