/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.action;

import loon.LSystem;
import loon.canvas.LColor;
import loon.utils.StringKeyValue;
import loon.utils.Easing.EasingMode;
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
	private final EaseTimer easeTimer;

	public ColorTo(LColor endColor) {
		this(null, endColor, 1f);
	}

	public ColorTo(LColor endColor, float duration) {
		this(null, endColor, duration);
	}

	public ColorTo(LColor startColor, LColor endColor, float duration) {
		this(startColor, endColor, duration, LSystem.DEFAULT_EASE_DELAY);
	}

	public ColorTo(LColor startColor, LColor endColor, float duration, float delay) {
		this(startColor, endColor, duration, delay, EasingMode.Linear);
	}

	public ColorTo(LColor startColor, LColor endColor, float duration, EasingMode mode) {
		this(startColor, endColor, duration, 0f, mode);
	}

	public ColorTo(LColor startColor, LColor endColor, float duration, float delay, EasingMode mode) {
		this.easeTimer = new EaseTimer(duration, delay, mode);
		this.start = startColor;
		this.end = endColor;
		this.setColors(startColor, endColor);
	}

	@Override
	public void onLoad() {
		if (startRed == -1 || startGreen == -1 || startBlue == -1 || startAlpha == -1) {
			LColor color = original.getColor();
			if (color != null) {
				startRed = color.r;
				startGreen = color.g;
				startBlue = color.b;
				startAlpha = color.a;
			} else {
				start = LColor.white.cpy();
				startRed = 0f;
				startGreen = 0f;
				startBlue = 0f;
				startAlpha = 1f;
			}
			start = color;
		}
		setColors(start, end);
	}

	@Override
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

		original.setColor(getCurrentColor());
	}

	private void setColors(LColor startColor, LColor endColor) {

		if (startColor == null) {
			return;
		}
		if (endColor == null) {
			endColor = LColor.white.cpy();
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
			tmpColor = new LColor(currentRed, currentGreen, currentBlue, currentAlpha);
		} else {
			tmpColor.setColor(currentRed, currentGreen, currentBlue, currentAlpha);
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
		ColorTo color = new ColorTo(new LColor(startRed, startGreen, startBlue, startAlpha),
				new LColor(endRed, endGreen, endBlue, endAlpha), easeTimer.getDuration());
		color.set(this);
		return color;
	}

	@Override
	public ActionEvent reverse() {
		ColorTo color = new ColorTo(new LColor(endRed, endGreen, endBlue, endAlpha),
				new LColor(startRed, startGreen, startBlue, startAlpha), easeTimer.getDuration());
		color.set(this);
		return color;
	}

	public float getStartRed() {
		return startRed;
	}

	public float getStartGreen() {
		return startGreen;
	}

	public float getStartBlue() {
		return startBlue;
	}

	public float getStartAlpha() {
		return startAlpha;
	}

	public float getSlopeRed() {
		return slopeRed;
	}

	public float getSlopeGreen() {
		return slopeGreen;
	}

	public float getSlopeBlue() {
		return slopeBlue;
	}

	public float getSlopeAlpha() {
		return slopeAlpha;
	}

	public float getEndRed() {
		return endRed;
	}

	public float getEndGreen() {
		return endGreen;
	}

	public float getEndBlue() {
		return endBlue;
	}

	public float getEndAlpha() {
		return endAlpha;
	}

	public LColor getStart() {
		return start.cpy();
	}

	public LColor getEnd() {
		return end.cpy();
	}

	@Override
	public String getName() {
		return "color";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startColor", start).comma().kv("endColor", end).comma().kv("EaseTimer", easeTimer);
		return builder.toString();
	}

}
