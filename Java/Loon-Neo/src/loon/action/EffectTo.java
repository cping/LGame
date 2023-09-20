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

import loon.action.sprite.effect.BaseEffect;
import loon.utils.StringKeyValue;

public class EffectTo extends ActionEvent {

	private BaseEffect _effect;

	public EffectTo(BaseEffect e) {
		this._effect = e;
	}

	@Override
	public void update(long elapsedTime) {
		_isCompleted = _effect.isCompleted();
	}

	@Override
	public void onLoad() {

	}

	@Override
	public ActionEvent cpy() {
		EffectTo eff = new EffectTo(_effect);
		eff.set(this);
		return eff;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "effect";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("BaseEffect", _effect);
		return builder.toString();
	}
}
