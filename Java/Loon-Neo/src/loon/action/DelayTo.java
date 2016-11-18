package loon.action;

import loon.LSystem;
import loon.utils.timer.LTimer;

public class DelayTo extends ActionEvent {

	private LTimer timer;

	private float delay;

	public DelayTo(float d) {
		this.timer = new LTimer((long) ((this.delay = d) * LSystem.SECOND));
	}

	@Override
	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			_isCompleted = true;
		}
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
		return new DelayTo(delay);
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "delay";
	}
}
