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

import loon.core.geom.Vector2f;

public class LNMoveBy extends LNMoveTo {

	protected float _lastTime;

	LNMoveBy() {

	}

	public static LNMoveBy Action(float duration, Vector2f pos) {
		LNMoveBy by = new LNMoveBy();
		by._diff = pos;
		by._duration = duration;
		by._lastTime = 0f;
		return by;
	}

	public static LNMoveBy Action(float duration, float dx, float dy) {
		return LNMoveBy.Action(duration, new Vector2f(dx, dy));
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		super._orgPos = node.getPosition();
		super._pos = super._orgPos.add(super._diff);
	}

	@Override
	public void update(float t) {
		if (t == 1f) {
			super._isEnd = true;
			super._target.setPosition(super._pos);
		} else {
			Vector2f position = super._target.getPosition();
			super._target.setPosition(super._diff.mul((t - this._lastTime))
					.add(position));
			this._lastTime = t;
		}
	}

	@Override
	public LNMoveBy reverse() {
		return Action(_duration, _diff.negate());
	}
}
