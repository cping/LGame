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

public class LNScaleBy extends LNAction {
	
	LNScaleBy(){
		
	}
	
	protected float _scaleX;
	
	protected float _scaleY;

	public static LNScaleBy Action(float scale) {
		LNScaleBy action = new LNScaleBy();
		action._scaleX = scale;
		action._scaleY = scale;
		return action;
	}

	public static LNScaleBy Action(float sX, float sY) {
		LNScaleBy action = new LNScaleBy();
		action._scaleX = sX;
		action._scaleY = sY;
		return action;
	}

	@Override
	public void step(float dt) {
		super._target.setScale(this._scaleX, this._scaleY);
		super._isEnd = true;
	}

	@Override
	public LNAction copy() {
		return Action(_scaleX, _scaleY);
	}
}
