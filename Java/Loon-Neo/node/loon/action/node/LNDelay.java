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
package loon.action.node;

public class LNDelay extends LNAction {

	LNDelay() {

	}

	public static LNDelay Action(float duration) {
		LNDelay delay = new LNDelay();
		delay._duration = duration;
		return delay;
	}

	@Override
	public void update(float t) {
		if (t == 1f) {
			super._isEnd = true;
		}
	}

	@Override
	public LNAction cpy() {
		return Action(_duration);
	}
}
