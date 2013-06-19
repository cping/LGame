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

public class LNBlink extends LNAction {

	protected int _times;
	
	LNBlink(){
		
	}

	public static LNBlink Action(float duration, int times) {
		LNBlink blink = new LNBlink();
		blink._duration = duration;
		blink._times = times;
		return blink;
	}

	@Override
	public void update(float time) {
		float slice = 1.0f / _times;
		float m = time % slice;
		super._target._visible = m > slice / 2 ? true : false;
	}

	@Override
	public LNAction copy() {
		return Action(_duration, _times);
	}
}
