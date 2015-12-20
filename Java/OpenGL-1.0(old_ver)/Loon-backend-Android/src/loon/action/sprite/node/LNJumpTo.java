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

public class LNJumpTo extends LNJumpBy {
	
	LNJumpTo(){
		
	}

	public static LNJumpTo Action(float duration, Vector2f delta,
			float height, int jumps) {
		LNJumpTo to = new LNJumpTo();
		to._duration = duration;
		to._delta = delta;
		to._height = height;
		to._jumps = jumps;
		return to;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		super._orgPos = node.getPosition();
		super._delta.set(this._delta.x - this._orgPos.x, this._delta.y
				- this._orgPos.y);
	}
}
