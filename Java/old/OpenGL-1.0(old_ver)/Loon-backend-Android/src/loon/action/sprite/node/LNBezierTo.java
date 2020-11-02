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

public class LNBezierTo extends LNBezierBy {

	protected LNBezierDef _originalconfig;
	
	LNBezierTo(){
		
	}

	public static LNBezierTo Action(float t, LNBezierDef c) {
		LNBezierTo bezier = new LNBezierTo();
		bezier._config = c;
		bezier._startPosition = new Vector2f();
		bezier._originalconfig = new LNBezierDef();
		bezier._originalconfig.controlPoint_1 = new Vector2f(
				c.controlPoint_1.x, c.controlPoint_1.y);
		bezier._originalconfig.controlPoint_2 = new Vector2f(
				c.controlPoint_2.x, c.controlPoint_2.y);
		bezier._originalconfig.endPosition = new Vector2f(c.endPosition.x,
				c.endPosition.y);
		return bezier;
	}

	@Override
	public void setTarget(LNNode node) {
		super.setTarget(node);
		_config.controlPoint_1 = _originalconfig.controlPoint_1
				.sub(_startPosition);
		_config.controlPoint_2 = _originalconfig.controlPoint_2
				.sub(_startPosition);
		_config.endPosition = _originalconfig.endPosition.sub(_startPosition);
	}

	@Override
	public LNBezierTo reverse() {
		LNBezierDef r = new LNBezierDef();
		r.endPosition = _config.endPosition.negate();
		r.controlPoint_1 = _config.controlPoint_2.add(_config.endPosition
				.negate());
		r.controlPoint_2 = _config.controlPoint_1.add(_config.endPosition
				.negate());
		return Action(_duration, r);
	}
}
