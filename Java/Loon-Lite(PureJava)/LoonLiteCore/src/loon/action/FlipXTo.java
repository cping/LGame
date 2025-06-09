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

import loon.utils.Easing.EasingMode;
import loon.utils.StringKeyValue;

public class FlipXTo extends FlipEffectTo {

	public FlipXTo(boolean flip) {
		this(1f, flip);
	}

	public FlipXTo(float d, boolean flip) {
		this(EasingMode.Linear, d, flip, true);
	}

	public FlipXTo(EasingMode e, float d, boolean flip, boolean eff) {
		super(e, FlipType.FlipX, d, flip, eff);
	}

	@Override
	public ActionEvent cpy() {
		FlipXTo flip = null;
		if (_easeTimer != null) {
			flip = new FlipXTo(_easeTimer.getEasingMode(), _easeTimer.getDuration(), this._flipAllow,
					this._effectRunning);
		} else {
			flip = new FlipXTo(EasingMode.Linear, 1f, this._flipAllow, this._effectRunning);
		}
		flip.set(this);
		return flip;
	}

	@Override
	public ActionEvent reverse() {
		FlipXTo flip = null;
		if (_easeTimer != null) {
			flip = new FlipXTo(_easeTimer.getEasingMode(), _easeTimer.getDuration(), !this._flipAllow,
					this._effectRunning);
		} else {
			flip = new FlipXTo(EasingMode.Linear, 1f, !this._flipAllow, this._effectRunning);
		}
		flip.set(this);
		return flip;
	}

	@Override
	public String getName() {
		return "flipx";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("flipType", _flipType).comma().kv("flipX", _flipAllow).comma().kv("effectRunning", _effectRunning);
		return builder.toString();
	}

}
