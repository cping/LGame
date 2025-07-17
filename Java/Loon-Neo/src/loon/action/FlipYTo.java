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

import loon.utils.StringKeyValue;
import loon.utils.Easing.EasingMode;

public class FlipYTo extends FlipEffectTo {

	public FlipYTo(boolean flip) {
		this(1f, flip);
	}

	public FlipYTo(float d, boolean flip) {
		this(EasingMode.Linear, d, flip, true);
	}

	public FlipYTo(EasingMode e, float d, boolean flip, boolean eff) {
		super(e, FlipType.FlipY, d, flip, eff);
	}

	public boolean isFlipY() {
		return this._flipAllow;
	}

	@Override
	public ActionEvent cpy() {
		FlipYTo flip = null;
		if (_easeTimer != null) {
			flip = new FlipYTo(_easeTimer.getEasingMode(), _easeTimer.getDuration(), this._flipAllow,
					this._effectRunning);
		} else {
			flip = new FlipYTo(EasingMode.Linear, 1f, this._flipAllow, this._effectRunning);
		}
		flip.set(this);
		return flip;
	}

	@Override
	public ActionEvent reverse() {
		FlipYTo flip = null;
		if (_easeTimer != null) {
			flip = new FlipYTo(_easeTimer.getEasingMode(), _easeTimer.getDuration(), !this._flipAllow,
					this._effectRunning);
		} else {
			flip = new FlipYTo(EasingMode.Linear, 1f, !this._flipAllow, this._effectRunning);
		}
		flip.set(this);
		return flip;
	}

	@Override
	public String getName() {
		return "flipy";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("flipType", _flipType).comma().kv("flipY", _flipAllow).comma().kv("effectRunning", _effectRunning);
		return builder.toString();
	}

}
