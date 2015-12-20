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

public class LNSpeed extends LNAction {

	LNSpeed(){
		
	}
	
	protected LNAction _other;

	protected float _speed;

	public static LNSpeed Action(LNAction action, float s) {
		LNSpeed speed = new LNSpeed();
		speed._other = action;
		speed._speed = s;
		return speed;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		_other.setTarget(node);
	}

	@Override
	public void step(float dt) {
		super.step(dt);
		_other.step(dt * _speed);
	}

	@Override
	public void update(float t) {
		_other.update(t);
		if (_other._isEnd) {
			super._isEnd = true;
		}
	}

	public float getSpeed() {
		return _speed;
	}

	public void setSpeed(float speed) {
		this._speed = speed;
	}

	@Override
	public LNAction copy() {
		return Action(_other, _speed);
	}

}
