package loon.action;

import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class ShakeTo extends ActionEvent {

	private EaseTimer easeTimer;
	private float shakeTimer;
	private float shakeX, oldShakeX;
	private float shakeY, oldShakeY;
	private float startOffsetX;
	private float startOffsetY;

	public ShakeTo(float shakeX, float shakeY) {
		this(shakeX, shakeY, 1f);
	}

	public ShakeTo(float shakeX, float shakeY, float duration) {
		this(shakeX, shakeY, duration, 1f / 60f, EasingMode.Linear);
	}

	public ShakeTo(float shakeX, float shakeY, float duration, float delay) {
		this(shakeX, shakeY, duration, delay, EasingMode.Linear);
	}

	public ShakeTo(float shakeX, float shakeY, float duration, float delay,
			EasingMode easing) {
		this.easeTimer = new EaseTimer(duration, delay, easing);
		this.shakeX = oldShakeX = shakeX;
		this.shakeY = oldShakeY = shakeY;
		this.offsetX = shakeX;
		this.offsetY = shakeY;
		this.shakeTimer = delay;
	}

	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			_isCompleted = true;
			return;
		}
		this.shakeX += easeTimer.getTimeInAfter() * easeTimer.getDelta();
		this.shakeY += easeTimer.getTimeInAfter() * easeTimer.getDelta();

		if (this.offsetX > 0.0f) {
			this.offsetX = (-this.shakeX);
			this.offsetY = (-this.shakeY);
		} else {
			this.offsetX = this.shakeX;
			this.offsetY = this.shakeY;
		}

		synchronized (original) {
			original.setLocation(this.startOffsetX + this.offsetX,
					this.startOffsetY + this.offsetY);
		}
	}

	@Override
	public void onLoad() {
		if (original != null) {
			this.startOffsetX = original.getX();
			this.startOffsetY = original.getY();
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		return new ShakeTo(easeTimer.getDuration(), shakeTimer, oldShakeX,
				oldShakeY);
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "shake";
	}

}
