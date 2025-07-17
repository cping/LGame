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

import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.timer.EaseTimer;

public class SizeTo extends ActionEvent {

	private float _startWidth, _startHeight;

	private float _endWidth, _endHeight;

	public SizeTo(float endWidth, float endHeight) {
		this(endWidth, endHeight, EasingMode.Linear);
	}

	public SizeTo(float endWidth, float endHeight, EasingMode mode) {
		this(endWidth, endHeight, 1f, mode);
	}

	public SizeTo(float endWidth, float endHeight, float duration, EasingMode mode) {
		_easeTimer = new EaseTimer(duration, mode);
		_endWidth = endWidth;
		_endHeight = endHeight;
	}

	@Override
	public void update(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		final float percent = _easeTimer.getProgress();
		final float sizeW = _endWidth - _startWidth;
		final float sizeH = _endHeight - _startHeight;
		final float width = _startWidth + sizeW * percent;
		final float height = _startHeight + sizeH * percent;
		if (original != null) {
			original.setSize(width, height);
			if (_easeTimer.isCompleted() && MathUtils.equal(width, _endWidth) && MathUtils.equal(height, _endHeight)) {
				original.setSize(_endWidth, _endHeight);
				_isCompleted = true;
			}

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

	public SizeTo setStartWidth(float w) {
		this._startWidth = w;
		return this;
	}

	public float getStartHeight() {
		return _startHeight;
	}

	public SizeTo setStartHeight(float h) {
		this._startHeight = h;
		return this;
	}

	public float getEndWidth() {
		return _endWidth;
	}

	public SizeTo setEndWidth(float w) {
		this._endWidth = w;
		return this;
	}

	public float getEndHeight() {
		return _endHeight;
	}

	public SizeTo setEndHeight(float h) {
		this._endHeight = h;
		return this;
	}

	@Override
	public ActionEvent cpy() {
		SizeTo size = new SizeTo(_endWidth, _endHeight, _easeTimer.getDuration(), _easeTimer.getEasingMode());
		size.set(this);
		return size;
	}

	@Override
	public ActionEvent reverse() {
		SizeTo size = new SizeTo(_startWidth, _startHeight, _easeTimer.getDuration(), _easeTimer.getEasingMode());
		size.set(this);
		return size;
	}

	@Override
	public String getName() {
		return "size";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startWidth", _startWidth).comma().kv("startHeight", _startHeight).comma().kv("endWidth", _endWidth)
				.comma().kv("endHeight", _endHeight);
		return builder.toString();
	}
}
