package loon.utils.timer;

import loon.utils.Easing;
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

	public void update(int delta) {
		this._timer += delta;
		if (this._timer >= this._duration) {
			this._timer = this._duration;
			this._finished = true;
		}
		switch (this._mode) {
		case InQuad:
			this._progress = Easing.inQuad(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case OutQuad:
			this._progress = Easing.outQuad(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InOutQuad:
			this._progress = Easing.inOutQuad(this._timer, this._duration,
					1.0f, 0.0f);
			break;
		case InCubic:
			this._progress = Easing.inCubic(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case OutCubic:
			this._progress = Easing.outCubic(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InQuart:
			this._progress = Easing.inQuart(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case OutQuart:
			this._progress = Easing.outQuart(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InOutQuart:
			this._progress = Easing.inOutQuart(this._timer, this._duration,
					1.0f, 0.0f);
			break;
		case InQuint:
			this._progress = Easing.inQuint(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case OutQuint:
			this._progress = Easing.outQuint(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InOutQuint:
			this._progress = Easing.inOutQuint(this._timer, this._duration,
					1.0f, 0.0f);
			break;
		case InSine:
			this._progress = Easing.inSine(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case OutSine:
			this._progress = Easing.outSine(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InOutSine:
			this._progress = Easing.inOutSine(this._timer, this._duration,
					1.0f, 0.0f);
			break;
		case InExp:
			this._progress = Easing.inExp(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case OutExp:
			this._progress = Easing.outExp(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InOutExp:
			this._progress = Easing.inOutExp(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InCirc:
			this._progress = Easing.inCirc(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case OutCirc:
			this._progress = Easing.outCirc(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InOutCirc:
			this._progress = Easing.inOutCirc(this._timer, this._duration,
					1.0f, 0.0f);
			break;
		case InBack:
			this._progress = Easing.inBack(this._timer, this._duration, 1.0f,
					0.0f, 0.1f);
			break;
		case OutBack:
			this._progress = Easing.outBack(this._timer, this._duration, 1.0f,
					0.0f, 0.1f);
			break;
		case InOutBack:
			this._progress = Easing.inOutBack(this._timer, this._duration,
					1.0f, 0.0f, 0.1f);
			break;
		case OutBounce:
			this._progress = Easing.outBounce(this._timer, this._duration,
					1.0f, 0.0f);
			break;
		case InBounce:
			this._progress = Easing.inBounce(this._timer, this._duration, 1.0f,
					0.0f);
			break;
		case InOutBounce:
			this._progress = Easing.inOutBounce(this._timer, this._duration,
					1.0f, 0.0f);
			break;
		case Linear:
		default:
			this._progress = Easing.linear(this._timer, this._duration, 1.0f,
					0.0f);
			break;

		}
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
