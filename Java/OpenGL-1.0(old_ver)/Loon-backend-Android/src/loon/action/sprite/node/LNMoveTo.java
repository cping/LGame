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

public class LNMoveTo extends LNAction {

	protected Vector2f _diff;

	protected Vector2f _orgPos;

	protected Vector2f _pos;

	LNMoveTo() {

	}

	public static LNMoveTo Action(float duration, Vector2f pos) {
		LNMoveTo to = new LNMoveTo();
		to._pos = pos;
		to._duration = duration;
		return to;
	}

	public static LNMoveTo Action(float duration, float dx, float dy) {
		return LNMoveTo.Action(duration, new Vector2f(dx, dy));
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		this._orgPos = node.getPosition();
		this._diff = this._pos.sub(this._orgPos);
	}

	@Override
	public void update(float t) {
		if (t == 1f) {
			super._isEnd = true;
			super._target.setPosition(this._pos);
		} else {
			super._target.setPosition(this._diff.mul(t).add(this._orgPos));
		}
	}

	@Override
	public LNAction copy() {
		return Action(_duration, _pos);
	}

	public LNAction reverse() {
		return Action(_duration, -_pos.x, -_pos.y);
	}
}
