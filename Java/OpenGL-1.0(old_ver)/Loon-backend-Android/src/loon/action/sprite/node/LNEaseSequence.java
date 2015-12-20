/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite.node;

import java.util.ArrayList;
import java.util.Arrays;

public class LNEaseSequence extends LNAction {

	LNEaseSequence() {

	}

	protected ArrayList<LNAction> _actionList;

	protected int _index;

	public static LNEaseSequence Action(Easing easing,
			ArrayList<LNAction> actions) {
		LNEaseSequence ease = new LNEaseSequence();
		ease._actionList = actions;
		ease._duration = 0f;
		ease._index = 0;
		ease._easing = easing;
		for (int i = 0; i < actions.size(); i++) {
			actions.get(i)._easing = easing;
			ease._duration += actions.get(i).getDuration();
		}
		return ease;
	}

	public static LNEaseSequence Action(Easing easing, LNAction... actions) {
		int size = actions.length;
		LNEaseSequence ease = new LNEaseSequence();
		ease._actionList = new ArrayList<LNAction>(size);
		ease._actionList.addAll(Arrays.asList(actions));
		ease._duration = 0f;
		ease._easing = easing;
		ease._index = 0;
		for (int i = 0; i < size; i++) {
			actions[i]._easing = easing;
			ease._duration += actions[i].getDuration();
		}
		return ease;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		this._index = 0;
		super._isEnd = false;
		super._target = node;
		if (this._actionList.size() > 0) {
			this._actionList.get(0).setTarget(super._target);
		}
	}

	@Override
	public void step(float dt) {
		if (this._index < this._actionList.size()) {
			do {
				this._actionList.get(this._index).step(dt);
				if (this._actionList.get(this._index).isEnd()) {
					dt = this._actionList.get(this._index).getElapsed()
							- this._actionList.get(this._index).getDuration();
					this._index++;
					if (this._index >= this._actionList.size()) {
						return;
					}
					this._actionList.get(this._index).setTarget(super._target);
				}
				if (this._actionList.get(this._index).getDuration() != 0f) {
					return;
				}
			} while (dt >= 0f);
		} else {
			super._isEnd = true;
		}
	}

	@Override
	public LNAction copy() {
		return Action(_easing, _actionList);
	}
}
