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
import loon.utils.MathUtils;

public class LNScaleTo extends LNAction {

	LNScaleTo(){
		
	}
	
	protected float dt;

	protected float _deltaX, _deltaY;

	protected float _startX, _startY;

	protected float _endX, _endY;

	public static LNScaleTo Action(float duration, Vector2f s) {
		LNScaleTo to = new LNScaleTo();
		to._duration = duration;
		to._endX = s.x;
		to._endY = s.y;
		return to;
	}

	public static LNScaleTo Action(float duration, float s) {
		LNScaleTo to = new LNScaleTo();
		to._duration = duration;
		to._endX = s;
		to._endY = s;
		return to;
	}

	public static LNScaleTo Action(float duration, float sx, float sy) {
		LNScaleTo to = new LNScaleTo();
		to._duration = duration;
		to._endX = sx;
		to._endY = sy;
		return to;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		_startX = super._target.getScaleX();
		_startY = super._target.getScaleY();
		_deltaX = _endX - _startX;
		_deltaY = _endY - _startY;
	}

	@Override
	public void update(float d) {
		dt += MathUtils.max(d, 0.01f);
		super._target.setScale(_startX + (_deltaX * dt), _startY
				+ (_deltaY * dt));
		super._isEnd = (_deltaX > 0 ? (super._target.getScaleX() >= _endX)
				: (super._target.getScaleX() <= _endX))
				&& (_deltaY > 0 ? (super._target.getScaleY() >= _endY)
						: (super._target.getScaleY() <= _endY));

	}

	@Override
	public LNAction copy() {
		return Action(_duration, _endX, _endY);
	}

	public LNScaleTo reverse() {
		return Action(_duration, 1f / _endX, 1f / _endY);
	}
}
