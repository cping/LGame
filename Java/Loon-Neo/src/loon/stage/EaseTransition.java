package loon.stage;

import loon.utils.Easing;

public abstract class EaseTransition<T extends EaseTransition<T>> extends
		AbstractTransition<T> {

	protected Easing _easing = def();

	public T none() {
		return call(Easing.TIME_LINEAR);
	}

	public T easeIn() {
		return call(Easing.TIME_EASE_IN);
	}

	public T easeOut() {
		return call(Easing.TIME_EASE_OUT);
	}

	public T easeInOut() {
		return call(Easing.TIME_EASE_INOUT);
	}

	public T call(Easing e) {
		_easing = e;
		return asT();
	}

	protected Easing def() {
		return Easing.TIME_EASE_INOUT;
	}

}
