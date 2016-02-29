package loon.utils.timer;

import loon.utils.Easing.EasingMode;

public class EaseTimer {
	
	private final int _duration;
	private int _timer = 0;
	private EasingMode _mode;
	private boolean _finished = false;
	private float _progress = 0.0f;

	public EaseTimer(int duration) {
		this(duration, EasingMode.Linear);
	}

	public EaseTimer(int duration, EasingMode mode) {
		this._duration = duration;
		this._mode = mode;
	}

	public EaseTimer(EaseTimer timer) {
		this._duration = timer._duration;
		this._timer = timer._timer;
		this._mode = timer._mode;
		this._finished = timer._finished;
		this._progress = timer._progress;
	}

	public void reset() {
		this._timer = 0;
		this._progress = 0.0f;
		this._finished = false;
	}

	public EasingMode getEasingMode() {
		return this._mode;
	}

	public int getDuration() {
		return this._duration;
	}

	public int getTimer() {
		return this._timer;
	}

	public boolean isFinished() {
		return this._finished;
	}

	public float getProgress() {
		return this._progress;
	}

}
