package loon.action;

import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

/**
 * 让指定对象产生闪烁效果
 */
public class FlashTo extends ActionEvent {

	private EaseTimer easeTimer;

	private float interval = 0;

	public FlashTo() {
		this(1f, 1f / 60f, EasingMode.Linear);
	}

	public FlashTo(float duration) {
		this(duration, 1f / 60f, EasingMode.Linear);
	}

	public FlashTo(float duration, float delay) {
		this(duration, delay, EasingMode.Linear);
	}

	public FlashTo(float duration, EasingMode easing) {
		this(duration, 1f / 60f, easing);
	}

	public FlashTo(float duration, float delay, EasingMode easing) {
		this.easeTimer = new EaseTimer(duration, delay, easing);
		this.interval = delay;
	}

	@Override
	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			original.setVisible(true);
			_isCompleted = true;
			return;
		}
		interval -= easeTimer.getProgress();
		if (this.interval <= 0) {
			this.original.setVisible(!this.original.isVisible());
			this.interval = easeTimer.getDelay();
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
		FlashTo flash = new FlashTo(easeTimer.getDuration(),
				easeTimer.getDelay(), easeTimer.getEasingMode());
		flash.set(this);
		return flash;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "flash";
	}

}
