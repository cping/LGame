/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import loon.utils.StringKeyValue;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class ScaleBy extends ActionEvent {

	private float _startScaleX, _startScaleY;

	private float _amountScaleX, _amountScaleY;

	public ScaleBy(float amountScaleX, float amountScaleY) {
		this(amountScaleX, amountScaleY, EasingMode.Linear);
	}

	public ScaleBy(float amountScaleX, float amountScaleY, EasingMode mode) {
		this(amountScaleX, amountScaleY, 1f, mode);
	}

	public ScaleBy(float amountScaleX, float amountScaleY, float duration, EasingMode mode) {
		_easeTimer = new EaseTimer(duration, mode);
		_amountScaleX = amountScaleX;
		_amountScaleY = amountScaleY;
	}

	@Override
	public void update(long elapsedTime) {
		if (_isCompleted) {
			return;
		}
		_easeTimer.update(elapsedTime);
		final float percent = _easeTimer.getProgress();
		if (original != null) {
			float width = original.getScaleX() + _amountScaleX * percent;
			float height = original.getScaleY() + _amountScaleY * percent;
			original.setScale(width, height);
		}
	}

	@Override
	public void onLoad() {
		if (original != null) {
			this._startScaleX = original.getWidth();
			this._startScaleY = original.getHeight();
		}
	}

	public float getStartScaleX() {
		return _startScaleX;
	}

	public ScaleBy setStartScaleX(float sx) {
		this._startScaleX = sx;
		return this;
	}

	public float getStartScaleY() {
		return _startScaleY;
	}

	public ScaleBy setStartScaleY(float sy) {
		this._startScaleY = sy;
		return this;
	}

	public ScaleBy setAmount(float sx, float sy) {
		_amountScaleX = sx;
		_amountScaleY = sy;
		return this;
	}

	public float getAmountScaleX() {
		return _amountScaleX;
	}

	public ScaleBy setAmountScaleX(float sx) {
		_amountScaleX = sx;
		return this;
	}

	public float getAmountScaleY() {
		return _amountScaleY;
	}

	public ScaleBy setAmountScaleY(float sy) {
		_amountScaleY = sy;
		return this;
	}

	@Override
	public ActionEvent cpy() {
		ScaleBy size = new ScaleBy(_amountScaleX, _amountScaleY, _easeTimer.getDuration(), _easeTimer.getEasingMode());
		size.set(this);
		return size;
	}

	@Override
	public ActionEvent reverse() {
		ScaleBy size = new ScaleBy(_startScaleY, _startScaleY, _easeTimer.getDuration(), _easeTimer.getEasingMode());
		size.set(this);
		return size;
	}

	@Override
	public String getName() {
		return "scaleby";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startScaleX", _startScaleX).comma().kv("startScaleY", _startScaleY).comma()
				.kv("amountScaleX", _amountScaleX).comma().kv("amountScaleY", _amountScaleY);
		return builder.toString();
	}
}