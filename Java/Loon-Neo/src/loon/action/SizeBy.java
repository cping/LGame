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

public class SizeBy extends ActionEvent {

	private float _startWidth, _startHeight;

	private float _amountWidth, _amountHeight;

	public SizeBy(float amountWidth, float amountHeight) {
		this(amountWidth, amountHeight, EasingMode.Linear);
	}

	public SizeBy(float amountWidth, float amountHeight, EasingMode mode) {
		this(amountWidth, amountHeight, 1f, mode);
	}

	public SizeBy(float amountWidth, float amountHeight, float duration, EasingMode mode) {
		_easeTimer = new EaseTimer(duration, mode);
		_amountWidth = amountWidth;
		_amountHeight = amountHeight;
	}

	@Override
	public void update(long elapsedTime) {
		if (_isCompleted) {
			return;
		}
		_easeTimer.update(elapsedTime);
		final float percent = _easeTimer.getProgress();
		if (original != null) {
			float width = original.getWidth() + _amountWidth * percent;
			float height = original.getHeight() + _amountHeight * percent;
			original.setSize(width, height);
		}
	}

	@Override
	public void onLoad() {
		if (original != null) {
			this._startWidth = original.getWidth();
			this._startHeight = original.getHeight();
		}
	}

	public float getStartWidth() {
		return _startWidth;
	}

	public SizeBy setStartWidth(float w) {
		this._startWidth = w;
		return this;
	}

	public float getStartHeight() {
		return _startHeight;
	}

	public SizeBy setStartHeight(float h) {
		this._startHeight = h;
		return this;
	}

	public SizeBy setAmount(float width, float height) {
		_amountWidth = width;
		_amountHeight = height;
		return this;
	}

	public float getAmountWidth() {
		return _amountWidth;
	}

	public SizeBy setAmountWidth(float width) {
		_amountWidth = width;
		return this;
	}

	public float getAmountHeight() {
		return _amountHeight;
	}

	public SizeBy setAmountHeight(float height) {
		_amountHeight = height;
		return this;
	}

	@Override
	public ActionEvent cpy() {
		SizeBy size = new SizeBy(_amountWidth, _amountHeight, _easeTimer.getDuration(), _easeTimer.getEasingMode());
		size.set(this);
		return size;
	}

	@Override
	public ActionEvent reverse() {
		SizeBy size = new SizeBy(_startWidth, _startHeight, _easeTimer.getDuration(), _easeTimer.getEasingMode());
		size.set(this);
		return size;
	}

	@Override
	public String getName() {
		return "sizeby";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startWidth", _startWidth).comma().kv("startHeight", _startHeight).comma()
				.kv("amountWidth", _amountWidth).comma().kv("amountHeight", _amountHeight);
		return builder.toString();
	}
}
