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

public class AlphaTo extends ActionEvent {

	private float _startAlpha, _endAlpha;

	public AlphaTo(float end) {
		this(end, 1f);
	}

	public AlphaTo(float end, EasingMode mode) {
		this(-1f, end, 1f, mode);
	}

	public AlphaTo(float end, float duration) {
		this(-1f, end, duration, EasingMode.Linear);
	}

	public AlphaTo(float end, float duration, EasingMode mode) {
		this(-1f, end, duration, mode);
	}

	public AlphaTo(float start, float end, float duration, EasingMode mode) {
		_easeTimer = new EaseTimer(duration, mode);
		_startAlpha = start;
		_endAlpha = end;
	}

	@Override
	public void update(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		if (original != null) {
			original.setAlpha(_startAlpha + (_endAlpha - _startAlpha) * _easeTimer.getProgress());
		}
	}

	@Override
	public void onLoad() {
		if (original != null && _startAlpha == -1f) {
			_startAlpha = original.getAlpha();
		}
	}

	@Override
	public ActionEvent cpy() {
		AlphaTo size = new AlphaTo(_startAlpha, _endAlpha, _easeTimer.getDuration(), _easeTimer.getEasingMode());
		size.set(this);
		return size;
	}

	@Override
	public ActionEvent reverse() {
		AlphaTo size = new AlphaTo(_endAlpha, _startAlpha, _easeTimer.getDuration(), _easeTimer.getEasingMode());
		size.set(this);
		return size;
	}

	@Override
	public String getName() {
		return "alpha";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startAlpha", _startAlpha).comma().kv("endAlpha", _endAlpha);
		return builder.toString();
	}

}