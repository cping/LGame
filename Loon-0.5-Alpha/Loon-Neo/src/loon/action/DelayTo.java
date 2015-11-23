package loon.action;

import loon.LSystem;
import loon.utils.timer.LTimer;

public class DelayTo extends ActionEvent {

	private LTimer timer = new LTimer();

	public DelayTo(float delay) {
		timer.setDelay((long) (delay * LSystem.SECOND));
	}

	@Override
	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			isComplete = true;
		}
	}

	@Override
	public void onLoad() {

	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public ActionEvent cpy() {
		return new DelayTo(timer.getDelay());
	}

}
