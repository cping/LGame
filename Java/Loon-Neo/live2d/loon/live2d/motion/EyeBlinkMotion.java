package loon.live2d.motion;

import loon.live2d.*;
import loon.live2d.util.*;

public class EyeBlinkMotion {

	long a;
	long b;
	EYE_STATE eyeState;
	boolean flag;
	String leftEyeName;
	String rightEyeName;
	int g;
	int h;
	int i;
	int j;

	public EyeBlinkMotion() {
		this.eyeState = EYE_STATE.STATE_FIRST;
		this.g = 4000;
		this.h = 100;
		this.i = 50;
		this.j = 150;
		this.flag = true;
		this.leftEyeName = "PARAM_EYE_L_OPEN";
		this.rightEyeName = "PARAM_EYE_R_OPEN";
	}

	public long calcNextBlink() {
		return (long) (UtSystem.getUserTimeMSec() + Math.random()
				* (2 * this.g - 1));
	}

	public void setInterval(final int blinkIntervalMsec) {
		this.g = blinkIntervalMsec;
	}

	public void setEyeMotion(final int closingMotionMsec,
			final int closedMotionMsec, final int openingMotionMsec) {
		this.h = closingMotionMsec;
		this.i = closedMotionMsec;
		this.j = openingMotionMsec;
	}

	public void setParam(final ALive2DModel model) {
		final long userTimeMSec = UtSystem.getUserTimeMSec();
		float n2 = 0.0f;
		switch (eyeState) {
		case STATE_CLOSING: {
			float n = (userTimeMSec - this.b) / this.h;
			if (n >= 1.0f) {
				n = 1.0f;
				this.eyeState = EYE_STATE.STATE_CLOSED;
				this.b = userTimeMSec;
			}
			n2 = 1.0f - n;
			break;
		}
		case STATE_CLOSED: {
			if ((userTimeMSec - this.b) / this.i >= 1.0f) {
				this.eyeState = EYE_STATE.STATE_OPENING;
				this.b = userTimeMSec;
			}
			n2 = 0.0f;
			break;
		}
		case STATE_OPENING: {
			float n3 = (userTimeMSec - this.b) / this.j;
			if (n3 >= 1.0f) {
				n3 = 1.0f;
				this.eyeState = EYE_STATE.STATE_INTERVAL;
				this.a = this.calcNextBlink();
			}
			n2 = n3;
			break;
		}
		case STATE_INTERVAL: {
			if (this.a < userTimeMSec) {
				this.eyeState = EYE_STATE.STATE_CLOSING;
				this.b = userTimeMSec;
			}
			n2 = 1.0f;
			break;
		}
		default: {
			this.eyeState = EYE_STATE.STATE_INTERVAL;
			this.a = this.calcNextBlink();
			n2 = 1.0f;
			break;
		}
		}
		if (!this.flag) {
			n2 = -n2;
		}
		model.setParamFloat(this.leftEyeName, n2);
		model.setParamFloat(this.rightEyeName, n2);
	}

	enum EYE_STATE {
		STATE_FIRST, STATE_INTERVAL, STATE_CLOSING, STATE_CLOSED, STATE_OPENING
	}
}
