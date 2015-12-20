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

public class LNBezierBy extends LNAction {

	protected LNBezierDef _config;

	protected Vector2f _startPosition;
	
	LNBezierBy(){
		
	}

	public static LNBezierBy Action(float t, LNBezierDef c) {
		LNBezierBy bezier = new LNBezierBy();
		bezier._config = c;
		return bezier;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		_startPosition = node.getPosition();
	}

	@Override
	public void update(float t) {
		float xa = 0;
		float xb = _config.controlPoint_1.x;
		float xc = _config.controlPoint_2.x;
		float xd = _config.endPosition.x;

		float ya = 0;
		float yb = _config.controlPoint_1.y;
		float yc = _config.controlPoint_2.y;
		float yd = _config.endPosition.y;

		float x = LNBezierDef.bezierAt(xa, xb, xc, xd, t);
		float y = LNBezierDef.bezierAt(ya, yb, yc, yd, t);
		_target.setPosition(_startPosition.x + x, _startPosition.y + y);
	}

	@Override
	public LNAction copy() {
		return Action(_duration, _config);
	}

	public LNAction reverse() {
		LNBezierDef r = new LNBezierDef();
		r.endPosition = _config.endPosition.negate();
		r.controlPoint_1 = _config.controlPoint_2.add(_config.endPosition
				.negate());
		r.controlPoint_2 = _config.controlPoint_1.add(_config.endPosition
				.negate());
		return Action(_duration, r);
	}
}
