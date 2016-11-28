package loon.action;

import loon.canvas.LColor;
import loon.utils.timer.EaseTimer;

/**
 * 渐变指定动作对象的色彩
 */
public class ColorTo extends ActionEvent {

	private float startRed = -1;
	private float startGreen = -1;
	private float startBlue = -1;
	private float startAlpha = -1;

	private float slopeRed;
	private float slopeGreen;
	private float slopeBlue;
	private float slopeAlpha;

	private float endRed;
	private float endGreen;
	private float endBlue;
	private float endAlpha;

	private float currentRed;
	private float currentGreen;
	private float currentBlue;
	private float currentAlpha;

	private LColor tmpColor = null;
	private LColor start, end;
	private EaseTimer easeTimer;

	public ColorTo(LColor endColor) {
		this(null, endColor, 1f);
	}

	public ColorTo(LColor endColor, float duration) {
		this(null, endColor, duration);
	}

	public ColorTo(LColor startColor, LColor endColor, float duration) {
		this(startColor, endColor, duration, 1f / 60f);
	}

	public ColorTo(LColor startColor, LColor endColor, float duration,
			float delay) {
		this.easeTimer = new EaseTimer(duration, delay);
		this.start = startColor;
		this.end = endColor;
		this.setColors(startColor, endColor);
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public void onLoad() {

		if (startRed == -1 || startGreen == -1 || startBlue == -1
				|| startAlpha == -1) {
			LColor color = original.getColor();
			if (color != null) {
				startRed = color.r;
				startGreen = color.g;
				startBlue = color.b;
				startAlpha = color.a;
			} else {
				start = LColor.white;
				startRed = 0f;
				startGreen = 0f;
				startBlue = 0f;
				startAlpha = 1f;
			}
			start = color;
		}
		setColors(start, end);
	}

	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			_isCompleted = true;
			return;
		}

		currentRed = slopeRed * easeTimer.getTimeInAfter() + startRed;
		currentGreen = slopeGreen * easeTimer.getTimeInAfter() + startGreen;
		currentBlue = slopeBlue * easeTimer.getTimeInAfter() + startBlue;
		currentAlpha = slopeAlpha * easeTimer.getTimeInAfter() + startAlpha;

		synchronized (original) {
			original.setColor(getCurrentColor());
		}
	}

	private void setColors(LColor startColor, LColor endColor) {

		if (startColor == null) {
			return;
		}
		if (endColor == null) {
			endColor = LColor.white;
		}

		startRed = startColor.r;
		startGreen = startColor.g;
		startBlue = startColor.b;
		startAlpha = startColor.a;

		slopeRed = (endColor.r - startColor.r);
		slopeGreen = (endColor.g - startColor.g);
		slopeBlue = (endColor.b - startColor.b);
		slopeAlpha = (endColor.a - startColor.a);

		currentRed = startColor.r;
		currentGreen = startColor.g;
		currentBlue = startColor.b;
		currentAlpha = startColor.a;

		endRed = endColor.r;
		endGreen = endColor.g;
		endBlue = endColor.b;
		endAlpha = endColor.a;

	}

	public LColor getCurrentColor() {
		if (tmpColor == null) {
			tmpColor = new LColor(currentRed, currentGreen, currentBlue,
					currentAlpha);
		} else {
			tmpColor.setColor(currentRed, currentGreen, currentBlue,
					currentAlpha);
		}
		return tmpColor;
	}

	public float getCurrentRed() {
		return currentRed;
	}

	public float getCurrentGreen() {
		return currentGreen;
	}

	public float getCurrentBlue() {
		return currentBlue;
	}

	public float getCurrentAlpha() {
		return currentAlpha;
	}

	@Override
	public ActionEvent cpy() {
		ColorTo color = new ColorTo(new LColor(startRed, startGreen, startBlue,
				startAlpha), new LColor(endRed, endGreen, endBlue, endAlpha),
				easeTimer.getDuration());
		color.set(this);
		return color;
	}

	@Override
	public ActionEvent reverse() {
		ColorTo color = new ColorTo(new LColor(endRed, endGreen, endBlue,
				endAlpha), new LColor(startRed, startGreen, startBlue,
				startAlpha), easeTimer.getDuration());
		color.set(this);
		return color;
	}

	@Override
	public String getName() {
		return "color";
	}

}
